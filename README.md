# Room304

Game 2D RPG / Visual Novel viết bằng Java 21 và LibGDX, chạy desktop bằng LWJGL3. Xem [báo cáo audit](docs/CODEBASE_AUDIT.md) để biết những giới hạn và khuyến nghị còn lại.

## Chạy, build và kiểm tra

Cần JDK 21 và `JAVA_HOME` trỏ đúng thư mục JDK. Trong PowerShell tại thư mục dự án:

```powershell
.\gradlew.bat lwjgl3:run
.\gradlew.bat build
.\gradlew.bat core:regressionTest
```

Linux/macOS dùng `./gradlew`. Khi wrapper và dependencies đã được cache, thêm `--offline` để tránh truy cập mạng. Có thể chọn cache riêng bằng `-g .gradle-user-home`; thư mục này được Git bỏ qua. Lệnh dùng trong audit:

```powershell
.\gradlew.bat --offline --no-daemon -g .gradle-user-home build
```

`build` compile hai module, chạy `check` và đóng gói desktop. `core:regressionTest` chạy kiểm tra logic Java với assertion được bật, không cần OpenGL hoặc dependency test mới; lỗi làm build thất bại. `test` là task tiêu chuẩn nhưng hiện chưa có suite JUnit. Regression source nằm trong `core/src/regression/java/` và được nối vào `check`. JAR chạy được nằm tại `lwjgl3/build/libs/`. Task `lwjgl3:run` đặt working directory thành `assets/`; chạy từ IDE cũng cần thiết lập như vậy.

Điều khiển: WASD di chuyển, Ctrl chạy nhanh, F tương tác, F/Enter tiếp tục thoại; F/E/Esc hoặc X đóng tủ đồ, click ô để chuyển item. Player di chuyển được trong thoại NPC; tủ đồ và thông báo cửa khóa giữ player đứng yên.

## Kiến trúc và luồng game

| Thành phần | Trách nhiệm |
| --- | --- |
| `lwjgl3` | Launcher, cấu hình cửa sổ và startup theo hệ điều hành; phụ thuộc `core` |
| `Room304Game` | Tạo screen và dispose screen hiện hành khi thoát |
| `GameScreen` | Điều phối update/render, scene, camera và tương tác; sở hữu tài nguyên cấp screen |
| `WorldMap`, `HallwayMap`, `gameplay.BackyardMap` | Art, kích thước, collision, vùng tương tác và spawn |
| `Player`, `Npc`, `Furniture`, `Wardrobe`, `Wall` | Entity, animation, hitbox và hành vi riêng; `NpcInteractionSystem` chọn NPC trong tầm |
| `dialogue/` | Dữ liệu dialogue, JSON loader có validation, controller tiến/đóng thoại |
| `wardrobe/` | Controller giữ slot và chuyển đồ vào ô trống đầu tiên; item bất biến |
| `ui/` | Scene2D views hiển thị controller, sở hữu font, texture, portrait cache và listener |

Mỗi frame: cập nhật animation map phòng → controller/input theo trạng thái UI/scene → camera theo player → world render → dialogue và wardrobe. Trong phòng, dialogue vẫn cập nhật player/NPC target nhưng không kích hoạt tương tác mới trong cùng frame đóng thoại. Ưu tiên tương tác chồng vùng: NPC → wardrobe → exit.

World render dùng SpriteBatch của screen; UI có Stage/batch/viewport riêng. FitViewport giữ canvas 16:9; world camera có zoom riêng. Thứ tự vẽ trong phòng xử lý wardrobe phía trước/sau player và chèn player theo Y của NPC. Hiện có một NPC; thêm nhiều NPC cần kiểm tra/sắp thứ tự Y trước khi dựa vào thuật toán hiện tại.

## Thêm nội dung

- **NPC:** thêm cấu hình `Npc` trong map; sprite sheet ngang có các frame vuông. Hitbox cố định theo `Npc.HITBOX_WIDTH/HEIGHT`; overload nhận width/height được giữ tương thích và không đổi hitbox. Vùng tương tác dựa trên feet hitbox.
- **Dialogue:** thêm JSON tại `assets/dialogue/`, gọi `DialogueLoader.load(...)` khi tạo NPC. Format giữ nguyên: `{ "id": "...", "lines": [{ "speaker": "...", "text": "...", "portrait": "..." }] }`. `id`, `speaker`, `text` không blank; `portrait` có thể null/bỏ qua. Getters controller chỉ được gọi khi `isActive()`.
- **Map/scene:** map giữ art, kích thước, wall, spawn và vùng tương tác; thêm gameplay class mới vào `gameplay/`. Nối update/render/chuyển scene ở screen theo mẫu hiện có. Chỉ tách scene thành class riêng khi logic độc lập tăng đủ lớn.
- **Item:** khai báo trong `WardrobeController`; `WardrobeItem` bất biến. Hotbar dùng index `INVENTORY_SLOTS + i` trong cùng mảng player inventory. Chưa có stacking/equipment/persistence.
- **Story:** lựa chọn dialogue hiện là UI tĩnh. Quest, branching, ending và save chưa được triển khai; cần xác định dữ liệu và yêu cầu story trước khi thêm hệ thống.

## Ownership và quy ước làm việc

Object tạo texture/font/batch/stage chịu trách nhiệm dispose nó. `GameScreen` khởi tạo một lần, `hide()` tháo input processor của chính wardrobe stage, `show()` khôi phục nếu tủ còn mở. Owner dispose screen đúng một lần; không dùng lại screen sau dispose.

Map dispose background, furniture và NPC; không dispose wardrobe lần thứ hai vì đã nằm trong furniture. Entity dispose texture riêng. Stage dispose batch của Stage, không dispose texture/font dùng bởi actor; view phải dispose các tài nguyên đó. Portrait cache thuộc `DialogueView`, được giải phóng cùng view. Không dispose tài nguyên mượn. Getter bounds hiện trả đối tượng mutable; caller dùng để đọc, không tự sửa tọa độ/hitbox của map.

Không tạo tài nguyên đồ họa hoặc listener lặp lại mỗi frame. Một task root `generateAssetList` sinh `assets/assets.txt` khi xử lý resources; không sửa file đó bằng tay. Chỉ cân nhắc AssetManager khi cần chia sẻ asset hoặc loading bất đồng bộ với ownership rõ ràng.

Đọc `AGENTS.md` trước khi làm việc, giữ thay đổi theo một hệ thống, dùng API/abstraction hiện có. Sửa dialogue/inventory cần chạy regression checks. Sửa input/render/lifecycle cần kiểm tra desktop: di chuyển, NPC dialogue, F/Enter đóng thoại, chuyển phòng/sân, tủ đồ/click/hotbar, resize và thoát game. Compile và kiểm tra logic không thay thế visual QA.
