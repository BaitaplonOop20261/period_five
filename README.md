# Room304

Game phiêu lưu pixel 2D / Visual Novel viết bằng Java 21 và libGDX, chạy desktop bằng LWJGL3. Luồng chính đã nối nội dung C0→C6, điều tra và hai kết thúc A/B. Xem [tiến độ](DEVELOPMENT_PROGRESS.md) và [hướng dẫn tích hợp asset](ASSET_INTEGRATION_GUIDE.md).

**Chơi** tạo lượt mới từ mở màn khi chưa có bản lưu; `Room304Game` chuyển `MenuScreen` → `GameScreen`, nơi `StorySession` giữ tiến độ xuyên suốt bốn khu vực: phòng 304, hành lang tầng ba, phòng bảo vệ và lớp học. Người chơi đọc thoại/tài liệu, thu thập manh mối, dùng sổ ghi chú và lựa chọn nhánh truyện. Kết thúc C có code và dữ liệu, nhưng `enableC` trong `game/assets/config/story-world.json` mặc định là `false`.

Lobby đầu game dùng nền trường học lúc hoàng hôn, logo bên trên và menu bên trái: **Chơi / Chơi tiếp → Cài đặt → Hướng dẫn → Thoát**. Chưa có bản chính và backup thì hiện Chơi; có một trong hai thì chỉ hiện Chơi tiếp. Chơi tiếp xác nhận rồi tải bản chính nếu file tồn tại; chỉ khi không có bản chính mới hỏi tải backup. Không có màn chọn giữa hai bản và không tự dùng backup khi bản chính lỗi. Lobby dùng canvas 960×540; màn chơi, thoại và pause giữ viewport riêng. Xem [asset và bố cục lobby](ASSET_SPEC/15_MAIN_MENU_LOBBY_2026-11-06.md).

Menu tạm dừng chỉ có tiếp tục, lưu, hướng dẫn, âm thanh và về menu; không tải hoặc quản lý bản lưu trong lúc chơi. Cài đặt ở màn hình chờ có **Bản lưu** để xem thông tin và xóa riêng bản chính/backup, luôn hỏi xác nhận. Mục **Khôi phục backup** hỏi xác nhận rồi mở lượt chơi từ backup; không có backup thì mục này bị vô hiệu. Muốn bắt đầu lượt mới, xóa cả hai bản trong Cài đặt. Esc quay lại từng cấp; Hủy được chọn mặc định trong các hộp xác nhận.

Lưu thủ công dùng **một slot + một backup** tại `%LOCALAPPDATA%/Room304/saves/room304.json` trên Windows (fallback: `~/.room304/saves/room304.json`). Vị trí không phụ thuộc working directory; backup tên `room304.backup.json`. Chỉ lưu khi khám phá sau cảnh đầu; nút Lưu bị vô hiệu và có giải thích khi đang đọc thoại, ghi chú hoặc chạy cảnh. Bản lưu giữ tiến độ, phòng, tọa độ, hướng nhìn, mã lượt chơi, thời điểm và mục tiêu. Không có autosave hoặc lưu giữa hội thoại.

Định dạng file v2 đọc được v1 và tách `contentVersion` khỏi version định dạng. Khi vị trí mới chưa có slot/backup, game kiểm tra và sao chép save cũ `saves/room304.json` trong working directory sang vị trí mới, giữ nguyên file cũ. Dấu migration được ghi sau khi nhập hoặc xóa trong UI để save cũ không xuất hiện lại khi khởi động. Khi lưu tiếp, bản chính hợp lệ trước đó trở thành backup; bản chính lỗi không ghi đè backup tốt. Mở backup chỉ phục hồi lượt chơi trong RAM; lưu tiếp để thay bản chính. Save/backup được loại khỏi asset list và JAR. Chưa có migration cho thay đổi cốt truyện/bản đồ hoặc tự chuyển nhân vật khi tọa độ cũ bị chặn; game báo lỗi và giữ màn hiện tại.

Art, portrait, TMX và âm thanh tuỳ chọn còn thiếu; runtime dùng hình học/nhân vật đơn giản, hiển thị chữ và bỏ qua clip thiếu để vẫn chơi được. File bắt buộc hoặc asset có sẵn nhưng sai định dạng báo lỗi rõ. Các kết nối trình bày và kiểm tra tự động không thay thế nghiệm thu mỹ thuật/âm thanh với tài nguyên hoàn chỉnh.

Bản ghép 24/10/2026 giữ nền phòng 304 đã xóa bàn giáo viên cũ, 20 bộ bàn ghế và world 640×480. Bàn giáo viên dùng sprite register tách lớp; nhân vật được vẽ trước/sau bàn theo vị trí. Linh có idle và animation đi bốn hướng; Trang, Đức, Bà Năm, Thầy Hùng có idle và Nga dùng một frame.

Thùng thiết bị nằm bên phải bục giảng, sát tường dưới bảng thông báo; vùng va chạm và tương tác đi cùng thùng. Máy cassette đã được vẽ trong nền phòng 304 nên không chồng thêm sprite lên máy. Khi tương tác vẫn dùng các ảnh cận cảnh riêng cho mở khay, có pin và đang phát băng. Vật có `inBackground` dùng hình sẵn trên bản đồ; chỉ các biến thể trạng thái được cấu hình rõ mới vẽ đè (như tủ mở).

Các bản bố trí 320×240 trước đây vẫn được lưu trong lịch sử Git và tài liệu tham khảo. Tọa độ, collision, spawn và vùng tương tác hiện tại lấy từ config/story-world.json; không áp lại tọa độ cũ vào nền hiện tại.

Camera mặc định bám theo Linh trong cả bốn khu vực với khung nhìn 480×360 và dừng tại mép bản đồ. Mỗi scene trong story-world.json bật "followCamera": true; HUD và hội thoại giữ canvas 640×480 riêng, cố định trên màn hình.

## Điều khiển luồng chính

| Thao tác | Phím |
| --- | --- |
| Di chuyển | WASD hoặc phím hướng |
| Tương tác với vật/NPC gần nhất | E hoặc Enter |
| Hiện hết chữ / tiếp tục thoại | Enter hoặc nút Tiếp tục |
| Chọn phương án/menu | ↑/↓, Enter; có thể nhấp chuột |
| Mở/đóng ghi chú khi khám phá/đọc ghi chú | N |
| Tạm dừng / tiếp tục | Esc |

Thoại, ghi chú, chuyển phòng và pause khóa di chuyển. Phím đang giữ khi đổi ngữ cảnh phải được thả rồi nhấn lại; lần nhấn mở tương tác không đồng thời bỏ qua câu đầu. Tiến trình chỉ cập nhật khi bước truyện hoàn tất; có cassette khác với đã nghe cassette.

Tên nhân vật luôn hiện màu trắng trên đầu. Đồ vật chỉ hiện tên vàng khi được chọn và có thể tương tác. Animation `ui/interaction_mark.png` hiện trên đúng một mục tiêu gần nhất trong tầm và hướng tương tác, gồm đồ vật, cửa và NPC; phím E/Enter dùng cùng mục tiêu. Dấu cao 18 world unit, giữ tỉ lệ frame 8:25 và nằm trong khung camera 480×360. Khi hai mục tiêu cách đều nhau, giữ mục tiêu đang chọn để tránh nhấp nháy; ẩn dấu khi đọc thoại, ghi chú, tạm dừng hoặc chuyển phòng.

Khi nhấn **E hoặc Enter** vào đồ vật, UI làm tối cảnh và hiện ảnh cận cảnh phía trên lời thoại. Ảnh đổi theo trạng thái tủ, sổ, thùng, máy cassette và hồ sơ. Dùng **Enter / Tiếp tục** để đọc và hoàn tất tương tác; **Esc** tạm dừng rồi tiếp tục tại ảnh đang xem. Bộ ảnh lấy từ `ASSET_SPEC/download`, được đóng gói trong `game/assets/ui/inspection/`; xem [bảng tích hợp](ASSET_SPEC/10_INTERACTION_CLOSEUPS_2026-10-17.md).

<a id="moi-truong-phat-trien-chung"></a>
## Môi trường phát triển chung

Theo [hướng dẫn nhóm](AGENTS/AGENTS.md): **Oracle JDK 21.0.11**, **Gradle Wrapper 9.8.0**, **libGDX/FreeType 1.14.2**, **LWJGL 3.4.3**. Đặt `JAVA_HOME`, Project SDK và Gradle JVM về cùng JDK đã thống nhất; dùng Wrapper của project.

Project Gradle nằm trong **`game/`**; mở thư mục này trong IntelliJ. Package gốc là `com.room304`. Launcher/IDE cần working directory là **`game/assets/`**; task `lwjgl3:run` đã đặt sẵn. JAR đóng gói nằm ở `game/lwjgl3/build/libs/`.

## Build và kiểm tra

Trong PowerShell tại thư mục gốc repository:

```powershell
.\game\gradlew.bat -p game build --console=plain --stacktrace
.\game\gradlew.bat -p game core:regressionTest
.\game\gradlew.bat -p game lwjgl3:desktopRegressionTest
.\game\gradlew.bat -p game lwjgl3:storyDesktopTest
.\game\gradlew.bat -p game lwjgl3:run
```

Linux/macOS dùng `./game/gradlew -p game`. Khi Wrapper và dependencies đã được cache, có thể thêm `--offline`; cache riêng dùng `-g .gradle-user-home` (Git bỏ qua).

`build` compile hai module, chạy `check` và đóng gói desktop. `core:regressionTest` được nối vào `check`, chạy assertion Java không cần OpenGL: inventory, input, thoại/choice/reference, điều kiện truyện, routing và save. Task `test` tiêu chuẩn chưa có suite JUnit; `NO-SOURCE` không có nghĩa đã chạy test.

Hai task desktop cần OpenGL, chạy cửa sổ ẩn và không tự nằm trong `build`. `desktopRegressionTest` kiểm tra bản demo cũ: tương tác gần nhất, movement/collision, thoại, pause, tủ đồ và vòng đời screen. `storyDesktopTest` kiểm tra luồng chính qua input/UI/render thật: A/B, C với cấu hình test bật riêng, đi tới vật tương tác, ghi chú, pause, save/load, menu/chơi lại và resize. Source kiểm tra nằm trong `game/core/src/regression/java/` và `game/lwjgl3/src/regression/java/`. Chỉ báo một kiểm tra đạt khi lệnh thực tế kết thúc với mã 0.

## Kiến trúc và nội dung

| Thành phần | Trách nhiệm |
| --- | --- |
| `Room304Game`, `MenuScreen`, `GameScreen` | Tài nguyên chung, menu/chuyển screen, input/update/render và pause |
| `story/StorySession`, `StoryDirector`, `GameState` | Routing, hoàn tất bước truyện, tiến độ, manh mối và điều kiện ending |
| `story/StoryContent`, `SaveStore`, `ProgressSnapshot` | Nội dung truyện, lưu/tải một slot và xác thực tiến độ |
| `screen/StoryInput`, `dialogue/` | Phím theo ngữ cảnh, thoại/lựa chọn, typewriter và giao diện |
| `world/SceneCatalog`, `StoryWorldView`, `StoryVisuals` | Bốn scene, spawn/collision, NPC theo mốc truyện và trình bày |
| `Player`, `RoomInteractionSystem` | Tọa độ/vùng chân, movement/animation và chọn tương tác gần nhất |
| `assets/GameAssets`, `audio/StoryAudio` | AssetManager dùng chung, animation, ambience/clip và fallback |

`GameScreen` cập nhật truyện/input trước khi vẽ. Một `StorySession` và `GameState` tồn tại xuyên các lần đổi phòng. `Player` giữ tọa độ và vùng chân; đi chéo được chuẩn hóa, collision chia bước nhỏ và animation dùng độ dời thật. UI không tự cấp manh mối hoặc quyết định ending; điều kiện/tác động nằm trong Java.

Các file cấu hình chính, đường dẫn tính từ repository:

- `game/assets/story/content.json`: nội dung C0→C6 và ending, ID thoại/tài liệu/lựa chọn.
- `game/assets/config/story-world.json`: scene, collision/spawn, NPC, cấu hình nhân vật và `enableC`.
- `game/assets/config/story-visuals.json`, `story-audio.json`: cue trình bày/âm thanh theo dòng truyện.
- `game/assets/config/asset-manifest.json`: ID, đường dẫn, loại asset, bắt buộc/tuỳ chọn và metadata animation.
- `game/assets/story/ui-layout.json`: layout luồng chính; `game/assets/dialogue/` chứa schema và ví dụ thoại dùng lại.

Xem [hướng dẫn Dialogue Presentation](game/assets/dialogue/README.md) cho speaker, biểu cảm, liên kết câu và lựa chọn. Thêm asset theo [hướng dẫn tích hợp](ASSET_INTEGRATION_GUIDE.md); không sửa `game/assets/assets.txt` bằng tay vì task `generateAssetList` sinh lại nó.

## Demo cũ và ví dụ Trang

`DemoGameScreen` nằm trong **source regression** tại `game/lwjgl3/src/regression/java/com/room304/screen/`; không phải màn chơi mà launcher mở. Demo giữ bản đồ 305/306/sân sau, tủ đồ và mẫu thoại Trang để kiểm tra các thành phần dùng lại. Mẫu Trang có lựa chọn hỏi và quay lại menu, cùng bảng tên Player/NPC.

Điều khiển demo: WASD, Ctrl chạy, F tương tác; W/S hoặc ↑/↓ đổi lựa chọn, F/Enter xác nhận/tiếp tục; F/E hoặc X đóng tủ, nhấp ô chuyển item; Esc tạm dừng. Các phím này thuộc demo; luồng chính dùng bảng điều khiển phía trên.

## Ownership và cộng tác

`Room304Game` sở hữu `GameAssets`, chuyển screen sau khi frame/callback kết thúc và dispose screen cũ. Entity, view và `StoryAudio` mượn texture/audio từ AssetManager; không dispose tài nguyên mượn. Screen/view sở hữu và giải phóng batch, Stage, font và tài nguyên UI do mình tạo. `StoryWorldView` sở hữu renderer và cache TMX trong lượt chơi, giải phóng khi dispose screen. `hide()` tháo input processor thuộc screen và xóa lệnh UI đang chờ; `show()` khôi phục theo trạng thái.

Không tạo texture/font/listener mỗi frame. Getter bounds trả đối tượng mutable để đọc; caller không tự sửa tọa độ/hitbox. Khi thêm tài nguyên ngoài, ghi nguồn, giấy phép và credit. Đọc [CODEX.md](CODEX.md) và [AGENTS/AGENTS.md](AGENTS/AGENTS.md) trước khi sửa; giữ UTF-8 và dấu tiếng Việt. Sửa logic cần regression phù hợp; sửa input/render/lifecycle cần kiểm tra desktop và chơi thử. Compile và test logic không chứng minh chất lượng art/audio cuối cùng.
