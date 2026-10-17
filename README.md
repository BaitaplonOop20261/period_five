# Room304

Game phiêu lưu pixel 2D / Visual Novel viết bằng Java 21 và libGDX, chạy desktop bằng LWJGL3. Luồng chính đã nối nội dung C0→C6, điều tra và hai kết thúc A/B. Xem [tiến độ](DEVELOPMENT_PROGRESS.md) và [hướng dẫn tích hợp asset](ASSET_INTEGRATION_GUIDE.md).

**Bắt đầu** tạo lượt mới từ mở màn; `Room304Game` chuyển `MenuScreen` → `GameScreen`, nơi `StorySession` giữ tiến độ xuyên suốt bốn khu vực: phòng 304, hành lang tầng ba, phòng bảo vệ và lớp học. Người chơi đọc thoại/tài liệu, thu thập manh mối, dùng sổ ghi chú và lựa chọn nhánh truyện. Kết thúc C có code và dữ liệu, nhưng `enableC` trong `game/assets/config/story-world.json` mặc định là `false`.

Menu tạm dừng có tiếp tục, lưu/tải, hướng dẫn, âm thanh và về menu. Lưu thủ công dùng **một slot** `saves/room304.json`, tính từ thư mục làm việc; chỉ lưu ở trạng thái khám phá sau cảnh đầu, khi không đọc thoại, ghi chú hoặc chạy bước truyện. Bản lưu giữ tiến độ, phòng, tọa độ và hướng nhìn. Về menu bỏ phần tiến độ chưa lưu; chơi lại tạo lượt mới và có thể xem hoặc bỏ qua mở màn.

Art, portrait, TMX và âm thanh tuỳ chọn còn thiếu; runtime dùng hình học/nhân vật đơn giản, hiển thị chữ và bỏ qua clip thiếu để vẫn chơi được. File bắt buộc hoặc asset có sẵn nhưng sai định dạng báo lỗi rõ. Các kết nối trình bày và kiểm tra tự động không thay thế nghiệm thu mỹ thuật/âm thanh với tài nguyên hoàn chỉnh.

Đợt nhập thử 13/10/2026 đã có idle Linh/Đức/Trang và ảnh tĩnh làm idle bà Năm/thầy Hùng, cùng nền phòng 304 bố trí lại tám bàn thường và lối đi. Nền được vẽ lại theo artstyle nguồn, xuất PNG RGBA 640×480 ở 1× và lọc Nearest. Nhân vật dùng frame 32×32 của ảnh đã nhận; hướng khác/walk/pose chưa có dùng idle xuống. Xem [biên bản asset](ASSET_SPEC/09_IMPORTED_ASSETS_2026-10-13.md) cho file nguồn, tên runtime, metadata, bố cục và kết quả kiểm tra.

Ngày 17/10, phòng 304 và lớp bên cạnh đã giảm kích thước world từ 640×480 xuống 320×240 để bàn ghế cân đối với người. Ảnh nền gốc vẫn 640×480; runtime vẽ vào phòng nhỏ hơn với Nearest, giữ nguyên profile nhân vật. Va chạm, spawn, NPC và tầm E đi cùng tỉ lệ mới; xem [ghi chú tỉ lệ phòng](ASSET_SPEC/11_CLASSROOM_SCALE_2026-10-17.md).

Camera dùng chung khung nhìn 320×240 trên mọi map: phòng 304/lớp nhỏ hiện trọn phòng, map rộng cuộn theo Linh và dừng ở mép bản đồ. Độ lớn nhân vật trên màn hình giữ nguyên khi chuyển phòng; HUD và ảnh tương tác giữ vị trí cố định. Sân sau trong bản demo kiểm thử cũng dùng cùng tỉ lệ khung nhìn. Xem [ghi chú camera](ASSET_SPEC/14_FOLLOW_CAMERA_2026-10-17.md).

## Điều khiển luồng chính

| Thao tác | Phím |
| --- | --- |
| Di chuyển | WASD hoặc phím hướng |
| Tương tác với vật/NPC gần nhất | E |
| Hiện hết chữ / tiếp tục thoại | Enter hoặc nút Tiếp tục |
| Chọn phương án/menu | ↑/↓, Enter; có thể nhấp chuột |
| Mở/đóng ghi chú khi khám phá/đọc ghi chú | N |
| Tạm dừng / tiếp tục | Esc |

Thoại, ghi chú, chuyển phòng và pause khóa di chuyển. Phím đang giữ khi đổi ngữ cảnh phải được thả rồi nhấn lại; lần nhấn mở tương tác không đồng thời bỏ qua câu đầu. Tiến trình chỉ cập nhật khi bước truyện hoàn tất; có cassette khác với đã nghe cassette.

Bản đồ dùng dấu chấm than chuyển động ở mục tiêu E gần nhất thay cho nhãn tên đồ vật/nhân vật. Đồ vật đang chọn có viền vàng sáng nhẹ; NPC chỉ có dấu trên đầu. Dấu giữ tỉ lệ nhỏ so với nhân vật và ẩn khi mở thoại, ghi chú, pause hoặc chuyển phòng. Xem [tích hợp dấu tương tác](ASSET_SPEC/12_INTERACTION_CUES_2026-10-17.md).

Khi nhấn **E** vào đồ vật, UI làm tối cảnh và hiện ảnh cận cảnh phía trên lời thoại. Ảnh đổi theo trạng thái tủ, sổ, thùng, máy cassette và hồ sơ. Dùng **Enter / Tiếp tục** để đọc và hoàn tất tương tác; **Esc** tạm dừng rồi tiếp tục tại ảnh đang xem. Bộ ảnh lấy từ `ASSET_SPEC/download`, được đóng gói trong `game/assets/ui/inspection/`; xem [bảng tích hợp](ASSET_SPEC/10_INTERACTION_CLOSEUPS_2026-10-17.md).

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
