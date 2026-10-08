# Room304 — Codebase audit

Ngày: 2026-10-08. Phạm vi: mã Java của `core`, launcher desktop, cấu hình Gradle và README; không phân tích binary/cache hoặc nội dung asset ngoài nhu cầu xác định đường dẫn. Báo cáo được lập trước khi sửa source. Working tree ban đầu sạch.

## Kiến trúc trước refactor

- `lwjgl3` phụ thuộc `core`; `core` dùng LibGDX và FreeType. Java 21, Gradle wrapper 9.8.0; không có test source hay test framework.
- `Room304Game` sở hữu `GameScreen`. Screen quản lý lifecycle, cập nhật, chuyển scene, input tương tác, camera, render và UI.
- `Player` xử lý input WASD/Ctrl, collision và animation; `Npc` giữ dialogue, hitbox và animation. Map sở hữu texture, furniture, wall và NPC.
- `dialogue`: dữ liệu JSON, loader có validation và controller tuần tự. `wardrobe`: dữ liệu item bất biến và controller chuyển đồ. `ui`: Scene2D views, font và texture riêng.
- Chưa có quest/ending/save system. Không cần thêm các hệ thống đó trong đợt audit này. Không phát hiện vòng phụ thuộc class; package gốc phụ thuộc `gameplay.BackyardMap`, package này lại dùng entity trong package gốc.

## Confirmed issues — ưu tiên và phương án

Các điều kiện kích hoạt được xác nhận qua source; chưa đồng nghĩa mọi nhánh đã được tái hiện trong cửa sổ game. Không phát hiện Critical issue trong phạm vi kiểm tra.

| ID / mức độ | File / bằng chứng trước sửa | Ảnh hưởng thực tế | Phương án đơn giản | Regression / sửa ngay |
| --- | --- | --- | --- | --- |
| A1 High | `build.gradle`: `generateAssetList` xóa/ghi `assets.txt` trong closure cấu hình và được đăng ký ở cả hai subproject; không có action/output | Kể cả cấu hình task không liên quan cũng ghi asset; hai project sở hữu cùng output | Một task root, khai báo input/output, ghi UTF-8 trong `doLast`; hai `processResources` phụ thuộc task này | Thấp; giữ đường dẫn và format. Có |
| A2 Medium | `GameScreen.show()` luôn tạo mới toàn bộ tài nguyên; `hide()` rỗng, `dispose()` chưa tháo input processor của wardrobe stage | Gọi lại `show()` làm mất ownership tài nguyên cũ; stage ẩn/disposed có thể vẫn nhận input | Khởi tạo một lần; tháo processor của chính screen khi hide/dispose, khôi phục nếu mở lại wardrobe | Thấp; không thay scene/state. Có |
| A3 Medium | `DialogueView.updatePortrait()`: nhánh null xóa drawable nhưng giữ `portraitTexturePath`; nhánh cùng path chỉ đặt visible | Chuỗi ảnh A → không ảnh → ảnh A hiển thị rỗng; A → B → A tải lại A và giữ texture cũ đến dispose | Cache drawable theo path trong view, gán lại drawable kể cả cache hit; giữ một owner dispose | Thấp; giữ filter/layout. Có |
| A4 Medium | `WardrobeView.render()`: 27 + 27 + 9 lần `clearListeners()` / `new ClickListener()` mỗi frame mở tủ | 3.780 listener/giây ở 60 FPS; thay listener khi input gesture đang diễn ra; lặp code ba vùng | Đăng ký một lần khi dựng grid; handler dùng controller hiện hành; chỉ format label khi item bất biến thay đổi | Thấp–vừa; cần kiểm tra click/drag qua nhiều frame và hotbar index. Có |
| A5 Low | `GameScreen` tạo/dispose `ShapeRenderer` nhưng không sử dụng | Cấp phát mesh/shader không phục vụ render | Xóa field/import/allocation/dispose; giữ public debug API của entity | Thấp. Có |
| A6 Medium | `GameScreen.render()` chứa update, camera, world render, UI; cập nhật player/NPC trong phòng bị lặp ở nhánh dialogue và nhánh thường | Khó sửa thứ tự update; dễ tái xuất lỗi animation/movement khi mở dialogue | Tách private `updateGame`, `updateRoomPlayer`, `renderWorld`, `renderRoom`; giữ thứ tự và điều kiện hiện tại | Vừa; đặc biệt F đóng dialogue không được mở lại ngay. Có |
| A7 Low | `README.md` vẫn mô tả template vẽ logo | Thành viên mới thiếu hướng dẫn hệ thống, ownership và mở rộng | Viết lại README duy nhất cho kiến trúc, quy ước và lệnh build/test | Thấp. Có |
| A8 Low | `WorldMap.getWalls/getFurniture/getNpcs()` tạo wrapper mỗi lần; `getExitBounds()` tạo Rectangle mới | Allocation nhỏ, có mặt trong vòng render/update | Có thể cache view/bounds sau khi xác định rõ hợp đồng mutable bounds | Tác động API getter; lợi ích nhỏ. Để sau |

## Potential issues — chưa tự sửa

| ID / mức độ | File / điều kiện và bằng chứng | Tác động / cách kiểm chứng | Phương án / regression / sửa ngay |
| --- | --- | --- | --- |
| P1 Medium | `GameScreen` chèn player theo Y khi duyệt NPC theo thứ tự list; hiện chỉ có một NPC | Thêm nhiều NPC không được sắp Y có thể render sai lớp; cần scene có nhiều NPC | Sort danh sách render khi thật sự thêm nhiều NPC; có ảnh hưởng layering. Chưa |
| P2 Medium | `Player.update()` chỉ kiểm tra hitbox tại vị trí đích; delta không bị giới hạn | Delta đủ lớn có thể vượt vật cản mỏng; chưa có tái hiện runtime | Đo/reproduce trước khi chọn substep hoặc delta policy; thay đổi chuyển động nên chưa sửa |
| P3 Medium | Constructor map/player/view tải nhiều tài nguyên; lỗi tải giữa chừng chưa dọn phần đã tạo. Chuyển phòng dispose map cũ trước khi tạo map mới | Asset bị thiếu/hỏng có thể làm khởi tạo thất bại và mất tài nguyên; đường đi thành công có dispose tương ứng | Khi bổ sung error/loading screen, làm initialization có rollback; phạm vi lớn hơn audit an toàn. Chưa |
| P4 Low | `DialogueLoader` yêu cầu speaker không blank; dialogue cửa khóa được tạo trực tiếp với speaker rỗng; getters controller giả định active | Chưa có JSON narrator hoặc caller sai trạng thái; không phải lỗi dữ liệu hiện tại | Quyết định quy ước narrator và caller contract trước khi thay validation/API. Chưa |

## Optional improvements — không triển khai

- Không chuyển hàng loạt package: root còn nhiều gameplay class, nhưng thay tên hiện chưa giải quyết lỗi trực tiếp. Chỉ thêm class mới vào domain phù hợp.
- Không thêm scene interface/factory/ECS/quest manager. Ba scene vẫn đủ nhỏ; chỉ tách thành scene riêng khi có logic độc lập tăng rõ rệt.
- `AssetManager` có ích khi cần chia sẻ asset/loading bất đồng bộ; hiện tài nguyên sở hữu theo object khá rõ. Không thay ownership toàn dự án chỉ để dùng API này.
- Các public helper/debug method và constructor NPC nhận width/height chưa có caller nội bộ. Giữ compatibility; ghi rõ overload dùng hitbox cố định thay vì xóa API.
- Response choices trong dialogue hiện là label tĩnh; chưa có dữ liệu hoặc handler lựa chọn. Giữ UI/gameplay hiện hành; cần yêu cầu story trước khi triển khai nhánh.
- Tọa độ map và theme UI hard-coded theo art hiện tại; không tạo config/skin system trước khi có nhu cầu dùng lại thực tế.

## Kế hoạch và giới hạn verification

1. Baseline compile/test offline trên JDK đang có; không truy cập mạng hoặc cài công cụ.
2. Nhóm build: sửa A1 và compile hai module/test.
3. Nhóm lifecycle/UI: sửa A2–A5, compile/test.
4. Nhóm orchestration/tài liệu: sửa A6–A7, bổ sung regression checks không dependency cho dialogue tiến/đóng và chuyển đồ (đầy kho, index sai, hotbar); build toàn dự án offline.

Giữ nguyên nội dung dialogue, assets thủ công, vị trí/hitbox/tốc độ, ưu tiên tương tác NPC → wardrobe → exit, di chuyển trong NPC dialogue, đóng băng player khi mở wardrobe và dialogue cửa khóa. Không đổi public signature hoặc format JSON. Visual QA cần chạy desktop; test logic/compile không chứng minh texture, font, layering và input chuột hoạt động trực quan.

## Kết quả sau refactor

Đã sửa A1–A7 theo phương án trên. A8, P1–P4 và các optional improvement được giữ nguyên có chủ đích. Không thêm dependency, không chuyển package, không đổi public signature hoặc JSON format. Git diff không có thay đổi asset/nội dung hội thoại. Không commit/push.

### Kiến trúc sau sửa

Vẫn có hai module, các controller/view/map hiện có và ownership theo object. `GameScreen.render()` điều phối `updateGame()` → `renderWorld()` → UI; `updateRoomPlayer()` dùng chung cho phòng khi có/không có dialogue, `renderRoom()` giữ layering hiện tại. Không tạo manager/interface hoặc hệ thống scene mới. Nhóm thay đổi này giảm vùng code phải đọc khi sửa update/render; screen vẫn chịu trách nhiệm chuyển scene, nên chưa phải kiến trúc scene hoàn toàn độc lập.

`show()` tái sử dụng tài nguyên của screen; `hide()`/`dispose()` tháo processor của wardrobe stage khi chính stage đó đang được dùng. `DialogueView` cache một drawable/texture cho mỗi portrait path, phục hồi drawable sau dòng không có ảnh. `WardrobeView` tạo 63 slot listener một lần và cập nhật text khi reference item bất biến thay đổi. Một task root sở hữu asset-list output và chỉ ghi trong task action.

### Build và regression checks

Môi trường thực tế: Temurin JDK **21.0.12.1**, Gradle **9.8.0**, dependency cache có sẵn. `JAVA_HOME` ban đầu trỏ JDK 25 không tồn tại; chỉ đặt lại biến môi trường trong tiến trình build, không sửa cấu hình máy/dự án. Sandbox chặn đọc JAR cache ở lần đầu; compile/build được chạy ngoài sandbox sau bước xin quyền, luôn dùng `--offline`. Không tải dependency hoặc cài công cụ.

| Bước | Lệnh/task | Kết quả |
| --- | --- | --- |
| Baseline | `:core:compileJava :lwjgl3:compileJava test` | Thành công; `test` ở cả hai module là `NO-SOURCE` |
| Nhóm build A1 | Compile hai module, `test`, `:lwjgl3:processResources` | Thành công; một task root `:generateAssetList` chạy |
| Nhóm lifecycle/UI A2–A5 | Compile hai module và `test` | Thành công |
| Nhóm orchestration/docs A6–A7 | `build` | **BUILD SUCCESSFUL**; compile, core checks, desktop JAR/start scripts/TAR/ZIP thành công |
| Kiểm tra riêng A1 | `help`, so sánh SHA-256/timestamp của `assets/assets.txt` trước/sau | Không ghi lại asset list; list không chứa chính nó |
| Rà diff | `git diff --check`, diff code/build và danh sách file | Không có lỗi whitespace; không có asset/dependency/public signature thay đổi |

Lệnh build đầy đủ (PowerShell, với `JAVA_HOME` hợp lệ):

```powershell
.\gradlew.bat --offline --no-daemon -g .gradle-user-home build --console=plain '-Dorg.gradle.logging.level=lifecycle'
```

`core:regressionTest` là JavaExec có bật assertion, được nối vào `check`, không giả lập kết quả task `test`. Bảy scenario đã chạy thành công: chuyển đồ và giữ item cũ; index sai/ô rỗng; đầy cả hai kho không mất/ghi đè item; chuyển qua hotbar; các phím đóng wardrobe; dialogue không input/tiến/đóng/reset; JSON hợp lệ, portrait optional, dữ liệu thiếu/sai/malformed và file không tồn tại. Proxy chỉ cung cấp input/files cho logic thật; lỗi check làm build thất bại. Fixture kho đầy dùng reflection vì public API không có thao tác thêm item tùy ý; không mở rộng production API chỉ phục vụ test. Fixture JSON được ghi trong `core/build/regression-fixtures/`.

### Resource review và phần chưa kiểm chứng

| Owner | Tài nguyên / đường giải phóng |
| --- | --- |
| `Room304Game` | Dispose screen hiện hành khi thoát; `GameScreen.dispose()` gọi `hide()` để bỏ processor trước khi dispose stage |
| `GameScreen` | SpriteBatch, ba map, Player, marker Texture và hai view được dispose; ShapeRenderer thừa đã xóa |
| `WorldMap` / map khác | Background, từng furniture/NPC; wardrobe chỉ dispose qua furniture list |
| `Player` / `Npc` / `Furniture` | Texture do chính object tạo được dispose tại object |
| Hai UI view | Stage/batch riêng, font, các texture được theo dõi; DialogueView dispose font trước font generator; portrait texture chỉ được thêm một lần vào danh sách ownership |

Review source không thấy thiếu/double dispose trên đường khởi tạo thành công hiện tại. Đây là review ownership, không phải đo GPU memory. P3 vẫn áp dụng khi khởi tạo thất bại giữa chừng; screen phải được owner dispose đúng một lần và không tái sử dụng sau dispose.

**Chưa chạy visual QA/runtime trong cửa sổ desktop.** Regression checks không khởi tạo texture, Stage hoặc OpenGL; chưa chứng minh thực tế portrait switching, gesture chuột, hide/show screen, resize, layering hoặc hành vi di chuyển/collision. Thứ tự update/render và các điều kiện gameplay được bảo toàn qua diff, chưa được xác nhận bằng thao tác chơi.

Checklist thủ công tiếp theo: di chuyển/Ctrl trước và trong NPC dialogue; F/Enter đóng mà không mở lại; mở tủ rồi click/giữ chuột qua nhiều frame, chuyển hotbar và đóng bằng X/F/E/Esc; đổi phòng/sân; đổi portrait A → null → A và A → B → A bằng fixture riêng; resize; hide/show screen; thoát game.

### File thay đổi

- `build.gradle`: sửa vòng đời và ownership task sinh asset list.
- `core/build.gradle`: regression source set/task và nối vào `check`.
- `core/src/main/java/vn/room304/game/GameScreen.java`: lifecycle, bỏ resource thừa, tách private update/render và bỏ duplication.
- `core/src/main/java/vn/room304/game/ui/DialogueView.java`: portrait cache/restore.
- `core/src/main/java/vn/room304/game/ui/WardrobeView.java`: listener ổn định, cache hiển thị item.
- `core/src/regression/java/vn/room304/game/RegressionChecks.java`: kiểm tra logic không dependency.
- `README.md`: onboarding, kiến trúc, controls, ownership và quy ước mở rộng.
- `docs/CODEBASE_AUDIT.md`: báo cáo này.

### Giai đoạn tiếp theo

1. Hoàn thành checklist desktop trước khi release, ưu tiên các nhánh UI/lifecycle vừa sửa.
2. Khi thêm nhiều NPC, giải quyết P1 với danh sách render được sắp Y và kiểm tra layering.
3. Reproduce delta lớn trước khi chọn collision substep; chưa đổi movement policy chỉ vì rủi ro lý thuyết.
4. Khi cần recovery/loading, xử lý rollback khi khởi tạo asset lỗi và cân nhắc AssetManager có ownership chung.
5. Chốt yêu cầu narrator/branching/quest/save trước khi thêm schema hoặc abstraction. Dùng test framework chuẩn khi suite và nhu cầu reporting tăng đủ để biện minh dependency test.

## Đợt tiếp theo — systematic game logic refactoring

Phần này ghi kết quả trên working tree mới hơn audit ban đầu: đã có OpeningScreen, pause, hitbox mới, RoomInteractionSystem chọn vật gần nhất và desktop regression checks. Những thay đổi có sẵn đó được giữ lại. Yêu cầu hiện tại xác định rõ dialogue phải khóa movement và animation phản ánh chuyển động thực tế.

### Nguyên nhân xác nhận và cách sửa

| Vấn đề trước đợt này | Nguyên nhân gốc | Thay đổi |
| --- | --- | --- |
| `Player.moving` được gán từ phím trước collision | Đồng nhất ý định di chuyển với chuyển động thật; đâm tường vẫn dùng run animation | Xác định độ dời sau collision/clamp; `isMoving()` suy ra từ độ dời, hướng và cadence dựa trên độ dời |
| Screen gọi `updateIdle(delta)` khi thoại và `updateIdle(0)` ngay sau mở thoại | Ngừng player update theo ngữ cảnh rồi sửa pose ở tầng điều phối; movement đã chạy trên frame mở | Xử lý lệnh trước movement; một luồng player update cho mọi scene, truyền hướng 0 khi input khóa; bỏ `updateIdle()` |
| Player, dialogue, wardrobe và screen tự đọc phím | Binding và quyền xử lý lệnh phân tán; controller phụ thuộc hardware/global input | Thêm một `GameplayInput` snapshot; screen route theo ngữ cảnh; controller nhận `advance/open/close`, player nhận movement intent đã được kiểm tra |
| `WardrobeView.render()` đóng controller khi có yêu cầu X | Business state thay đổi trong bước draw, sau khi screen đã giải quyết input processor | Bind controller một lần; X chỉ phát request, screen consume trong update; gom processor selection vào `restoreInputProcessor()` |
| Movement được cập nhật riêng ở từng nhánh scene/UI | Nhiều đường đi quyết định player có được update hay không | Một lần `updatePlayer()` mỗi gameplay tick, collision vẫn theo map hiện tại; opening/closing UI và teleport tiêu thụ quyền movement của frame |

Chỉ thêm một class `GameplayInput`; movement/animation tách thành private method trong Player. Không tạo interface, movement manager, animation manager, event bus hoặc trạng thái IDLE/WALK riêng bị trùng với độ dời. DialogueController sở hữu tiến trình thoại; WardrobeController sở hữu open/items; Player sở hữu vị trí và độ dời; screen sở hữu scene/pause và phân phối quyền điều khiển. Render không tự tiến/đóng business state.

Các API nội bộ được cập nhật cùng mọi caller: Player.update nhận hướng/sprint và kích thước map rõ ràng; bỏ overload tự đọc bàn phím/default map và `updateIdle`; `DialogueController.update()` đổi thành `advance()`; bỏ `WardrobeController.update()` đọc phím; WardrobeView nhận controller trong constructor và `render()` không nhận model động. Không giữ wrapper đọc hardware vì sẽ duy trì coupling đã xác định.

### Kiểm chứng thực tế của đợt này

- Baseline: core checks và desktop checks hiện có đều thành công trước khi sửa.
- Nhóm input/movement/animation: compile cả hai module, **8 core scenarios** và desktop checks cũ thành công.
- Nhóm UI command routing và bổ sung coverage: **`build :lwjgl3:desktopRegressionTest` thành công**, offline trên JDK 21; JAR/start scripts/TAR/ZIP được tạo. Không thêm dependency/build plugin hoặc tải dữ liệu mạng.
- Desktop checks khởi tạo **LWJGL3/OpenGL thật, asset thật, cửa sổ ẩn**. Kiểm tra: đứng yên và idle clock; đi được và tốc độ cũ; zero delta; sprint bị tường chặn; trượt dọc tường và hướng/cadence thực tế; góc tường chặn cả hai trục; world clamp; collision với furniture/NPC; opening/closing dialogue với phím giữ, WASD/Ctrl, F/Enter, 5 vòng lặp mở/đóng; pause/resume; chuyển phòng và các test selector cũ.
- UI checks dispatch touchDown/touchUp qua Scene2D Stage thật: giữ click qua 3 frame chuyển đúng một item; listener không bị thay; wardrobe mở/đóng bằng X/F/E khóa movement trên frame tương ứng; pause không route F vào wardrobe; draw view hai lần không đóng controller; X giải phóng input processor trong cùng update; hide/show giữ batch và khôi phục stage.
- Core checks xác nhận snapshot reset qua frame, WASD đối nhau triệt tiêu, held F không tự lặp, binding Enter/E/Esc đúng ngữ cảnh; inventory/dialogue/JSON validation vẫn đạt.

Lệnh kiểm chứng:

```powershell
.\gradlew.bat --offline --no-daemon -g .gradle-user-home build :lwjgl3:desktopRegressionTest --console=plain '-Dorg.gradle.logging.level=lifecycle'
```

Các check desktop chứng minh trạng thái và thao tác tự động trên runtime thật; **chưa có visual QA bằng mắt hoặc kiểm thử toàn bộ menu mở đầu/audio/resize**. Không tuyên bố gameplay được bảo toàn hoàn toàn. Hai thay đổi hành vi có chủ đích: run animation phản ánh chuyển động thật khi bị chặn, và frame mở modal không áp dụng movement trước rồi sửa pose. X/keyboard close đều tiêu thụ movement của frame đóng; frame sau nhận lại phím giữ. Tốc độ, hitbox, nearest-target/tie policy, assets và nội dung thoại được giữ.

Không thêm tài nguyên đồ họa hoặc đổi dispose ownership. Runtime checks tạo Player fixture có `finally` dispose; screen chính vẫn do Game owner dispose. Các vấn đề partial initialization khi asset lỗi, large-delta collision và sorting nhiều NPC vẫn để sau vì cần recovery policy/reproduction hoặc scene thực tế; không thêm quest/cutscene/ending/scene framework khi chưa có yêu cầu cụ thể.

### File sửa riêng trong đợt này

`GameScreen.java`, `Player.java`, `dialogue/DialogueController.java`, `wardrobe/WardrobeController.java`, `ui/WardrobeView.java`, class mới `gameplay/GameplayInput.java`, hai bộ `RegressionChecks.java` / `GameplayRegressionChecks.java`, `README.md` và báo cáo này. Không sửa các thay đổi menu/audio/map/asset hoặc build config đã có trong working tree; không commit/push.
