# Room304

Game 2D RPG / Visual Novel viết bằng Java 21 và LibGDX, chạy desktop bằng LWJGL3. Xem [tiến độ](DEVELOPMENT_PROGRESS.md) và [hướng dẫn tích hợp asset](ASSET_INTEGRATION_GUIDE.md) để tiếp tục phát triển.

Luồng khởi động: menu **Bắt đầu** → C0-01 hiện dần tên trường và giờ trong 1 giây → bấm tiếp để thêm thông báo sửa chữa → **Tiếp tục** để chữ mờ đi, rồi lớp đen mờ dần trong 1 giây và hiện cảnh chơi đang có. Trong mở màn, dùng Enter / F / Space hoặc nhấp nút; Esc trở về menu. Chưa triển khai nội dung C0-02 trở đi.

Trong cảnh chơi, **Esc** mở menu **Tạm dừng**. **Tiếp tục chơi** hoặc Esc đóng menu và giữ nguyên trạng thái; **Về menu** kết thúc lượt hiện tại, trở về menu chính. Chọn **Bắt đầu** lần nữa sẽ chạy lại mở màn và tạo lượt chơi mới. Khi tạm dừng, nhân vật, animation, tương tác và tiến độ thoại đều ngừng; nếu đang mở tủ, tủ vẫn giữ nguyên khi tiếp tục.

Chưa có âm thanh. Thư mục `game/assets/audio/ambience/` và `game/assets/audio/sfx/` đã được chuẩn bị; xem [quy ước asset âm thanh](game/assets/audio/README.md) để thêm hai loop nền C0-01 đúng tên mà không cần sửa code.

Project Gradle nằm trong **`game/`**; mở thư mục này trong IntelliJ. Package gốc là `com.room304`; các lớp hiện có được nhóm trong `screen/`, `world/`, `dialogue/` và `audio/`. Tủ đồ nằm trong `world/wardrobe/`. Asset nền nằm trong `game/assets/maps/`, sprite trong `game/assets/sprites/`. Package `story/` hiện chứa nền tảng tiến trình thuần Java; `tilesets/` chưa có asset.

## Chạy, build và kiểm tra

Cần JDK 21 và `JAVA_HOME` trỏ đúng thư mục JDK. Trong PowerShell tại thư mục dự án:

```powershell
.\game\gradlew.bat -p game lwjgl3:run
.\game\gradlew.bat -p game build
.\game\gradlew.bat -p game core:regressionTest
.\game\gradlew.bat -p game lwjgl3:desktopRegressionTest
```

Linux/macOS dùng `./game/gradlew -p game`. Khi wrapper và dependencies đã được cache, thêm `--offline` để tránh truy cập mạng. Có thể chọn cache riêng bằng `-g .gradle-user-home`; thư mục này được Git bỏ qua. Lệnh dùng trong audit:

```powershell
.\game\gradlew.bat -p game --offline --no-daemon -g .gradle-user-home build
```

`build` compile hai module, chạy `check` và đóng gói desktop. `core:regressionTest` chạy kiểm tra logic Java với assertion được bật, không cần OpenGL hoặc dependency test mới; lỗi làm build thất bại. `test` là task tiêu chuẩn nhưng hiện chưa có suite JUnit. Regression source nằm trong `game/core/src/regression/java/` và được nối vào `check`. JAR chạy được nằm tại `game/lwjgl3/build/libs/`. Task `lwjgl3:run` đặt working directory thành `game/assets/`; chạy từ IDE cũng cần thiết lập như vậy.

`lwjgl3:desktopRegressionTest` chạy riêng với renderer desktop và asset thật trong cửa sổ ẩn, cần OpenGL. Kiểm tra chọn vật gần nhất, nhiều NPC, chuyển phòng; chuyển động/animation sau collision, dialogue mở/đóng khi giữ phím, pause, tủ đồ và gesture chuột qua nhiều frame, hide/show screen. Source nằm trong `game/lwjgl3/src/regression/java/`; task này không tự chạy trong `build` để các kiểm tra core vẫn dùng được trên máy không có môi trường đồ họa.

Điều khiển: WASD di chuyển, Ctrl chạy nhanh, F tương tác, F/Enter tiếp tục thoại; F/E hoặc X đóng tủ đồ, click ô để chuyển item. Esc luôn mở/đóng menu tạm dừng trong cảnh chơi, kể cả khi đang đọc thoại hoặc mở tủ. Player đứng yên trong mọi hội thoại và khi mở tủ đồ; đọc xong mới được di chuyển lại.

Hội thoại dùng giao diện Visual Novel: portrait lớn ở giữa, chữ trực tiếp trên cảnh, HUD giờ/địa điểm và nút Tiếp tục/Menu. F/Enter hiện hết chữ đang chạy; lần nhấn sau mới chuyển trang/câu. Pause giữ nguyên tiến độ typewriter. Trang trong phòng 305 có ví dụ ba câu tiếng Việt, background có sẵn và portrait placeholder trong suốt. Xem [cách thêm hội thoại và chỉnh layout](game/assets/dialogue/README.md).

## Kiến trúc và luồng game

| Thành phần | Trách nhiệm |
| --- | --- |
| `lwjgl3` | Launcher, cấu hình cửa sổ và startup theo hệ điều hành; phụ thuộc `core` |
| `Room304Game` | Chuyển menu/cảnh chơi ở cuối frame; dispose screen cũ khi chuyển và screen hiện hành khi thoát |
| `screen.OpeningScreen`, `audio.OpeningAmbience` | Menu, chữ mở màn C0-01 và các loop nền tuỳ chọn; thiếu asset vẫn chạy |
| `screen.PauseMenuView` | Menu tạm dừng và lớp đen mờ dần khi vào cảnh chơi |
| `GameScreen` | Điều phối update/render, scene, camera và tương tác; sở hữu tài nguyên cấp screen |
| `screen.GameplayInput` | Chụp trạng thái phím một lần mỗi frame; không tự quyết định quyền điều khiển |
| `WorldMap`, `HallwayMap`, `world.BackyardMap` | Art, kích thước, collision, vùng tương tác và spawn |
| `Player`, `Npc`, `Furniture`, `Wardrobe`, `Wall` | Entity, animation, hitbox và hành vi riêng |
| `world.RoomInteractionSystem` | Lọc vật trong tầm và chọn vật có khoảng cách hình học ngắn nhất; giữ kết quả chung cho dấu tương tác và phím F |
| `dialogue/` | Dữ liệu JSON, controller, Visual Novel UI dùng chung, HUD và quản lý asset |
| `world/wardrobe/` | Controller giữ slot và chuyển đồ vào ô trống đầu tiên; item bất biến |
| `dialogue/`, `screen/`, `world/wardrobe/` | Scene2D views hiển thị controller, sở hữu font, chrome texture và listener; mượn portrait cache từ GameAssets |

Mỗi frame: `GameplayInput.capture()` → xử lý pause/chuyển cảnh → cập nhật animation map → phân phối lệnh theo ngữ cảnh → xác định quyền di chuyển → movement/collision → độ dời thực tế → animation → camera/world/UI render. Pause và fade giữ gameplay đóng băng. Dialogue/wardrobe khóa input di chuyển nhưng player vẫn được cập nhật với hướng bằng 0 để animation IDLE tiếp tục chạy.

`GameScreen` phân phối một lệnh F cho đúng ngữ cảnh: tiến thoại, đóng tủ hoặc tương tác trong world. Lệnh mở UI được xử lý trước movement. Cả frame mở và frame đóng UI đều khóa di chuyển; frame kế tiếp nhận lại phím đang giữ. Đổi scene cũng không áp dụng movement vào vị trí vừa teleport trong cùng frame. Không có lệnh ép animation theo dialogue/menu.

`Player.update(...)` nhận hướng và sprint đã được cho phép cùng collision/map bounds, không đọc bàn phím và không biết dialogue. Sau collision và clamp, player lưu độ dời thực tế; `isMoving()` suy ra từ độ dời này. Không đổi vị trí → IDLE; có độ dời → animation chạy theo hướng và quãng đường thực tế. Vì vậy đâm tường không chạy tại chỗ, còn trượt dọc tường dùng hướng thật sự di chuyển. `setPosition()` là teleport và xóa độ dời cũ.

Controller chỉ nhận lệnh nghiệp vụ: `DialogueController.advance()` và `WardrobeController.open()/close()`, không phụ thuộc `Gdx.input`. `WardrobeView` được gắn với controller qua constructor; nút X phát yêu cầu đóng, screen lấy bằng `consumeCloseRequest()` trong update. `render()` không đóng controller. Screen chọn input processor từ trạng thái pause/wardrobe tại một chỗ.

Trong phòng, mọi NPC, tủ và cửa cùng tham gia chọn tương tác. Vùng tương tác mở rộng chỉ lọc các vật trong tầm; khoảng cách được đo từ hitbox player tới hitbox NPC, collision box tủ hoặc đoạn ngưỡng cửa thật tại Y=18. So sánh `dx² + dy²`, không dùng tâm sprite hoặc khoảng cách tới vùng mở rộng, không ưu tiên loại vật. Khi khoảng cách bằng nhau chính xác, giữ vật đang chọn; nếu chưa có thì dùng ID ổn định, độc lập thứ tự danh sách. Một selector duy nhất dùng cho cả dấu tương tác và phím F: refresh tại vị trí hiện tại trước khi thực hiện F, và sau movement để dấu phản ánh vị trí mới. Đổi phòng tạo lại danh sách vật và xóa lựa chọn của phòng cũ.

World render dùng SpriteBatch của screen; UI có Stage/batch/viewport riêng. FitViewport giữ canvas 16:9; world camera có zoom riêng. Thứ tự vẽ trong phòng xử lý wardrobe phía trước/sau player và chèn player theo Y của NPC. Hiện có một NPC; thêm nhiều NPC cần kiểm tra/sắp thứ tự Y trước khi dựa vào thuật toán hiện tại.

## Thêm nội dung

- **Hitbox nhân vật:** player và NPC dùng vùng chân/thân dưới 18×10 pixel của sprite, tương đương 108×60 world unit ở scale ×6. Sprite vẫn 32×32 pixel và căn giữa theo hitbox; spawn ở cửa/hành lang/sân tự tính theo kích thước này.
- **NPC:** thêm cấu hình `Npc` trong map; sprite sheet ngang có các frame vuông. Hitbox cố định theo `Npc.HITBOX_WIDTH/HEIGHT`; overload nhận width/height được giữ tương thích và không đổi hitbox. Vùng tương tác mở rộng từ hitbox. Nếu muốn giữ vị trí hình ảnh khi đổi chiều rộng hitbox, đặt X bằng tâm nhân vật trừ nửa chiều rộng hitbox.
- **Dialogue:** thêm JSON tại `game/assets/dialogue/`, gọi `DialogueLoader.load(...)` khi tạo NPC. Schema hỗ trợ speaker ID, biểu cảm, background, liên kết câu và lựa chọn. Xem [hướng dẫn Dialogue Presentation](game/assets/dialogue/README.md). Getters nội dung controller chỉ được gọi khi `isActive()`.
- **Map/scene:** map giữ art, kích thước, wall, spawn và vùng tương tác; thêm gameplay class mới vào `gameplay/`. Nối update/render/chuyển scene ở screen theo mẫu hiện có. Chỉ tách scene thành class riêng khi logic độc lập tăng đủ lớn.
- **Item:** khai báo trong `WardrobeController`; `WardrobeItem` bất biến. Hotbar dùng index `INVENTORY_SLOTS + i` trong cùng mảng player inventory. Chưa có stacking/equipment/persistence.
- **Story:** lựa chọn dialogue có thể chuyển tới câu khác qua ID trong JSON; UI không áp dụng điều kiện hoặc tác động nhiệm vụ. Logic tiến trình và điều kiện A/B đã có trong story/ và được kiểm thử độc lập; chưa nối vào bản demo. Snapshot JSON là hợp đồng dữ liệu, chưa bật save/load.

## Ownership và quy ước làm việc

Object tạo texture/font/batch/stage chịu trách nhiệm dispose nó. `GameScreen` khởi tạo một lần, `hide()` tháo input processor của chính dialogue/wardrobe/pause stage; `show()` khôi phục stage phù hợp trạng thái hiện tại. `Room304Game` chuyển screen ở cuối frame để không dispose stage ngay trong callback của nút; screen cũ được dispose đúng một lần và không được dùng lại.

Map dispose furniture và NPC; background dùng chung giữ trong GameAssets; không dispose wardrobe lần thứ hai vì đã nằm trong furniture. Trong luồng ứng dụng, entity mượn texture từ GameAssets và không dispose texture. Constructor độc lập trong regression sở hữu GameAssets riêng. Stage dispose batch của Stage, không dispose texture/font dùng bởi actor; view phải dispose các tài nguyên đó. Background/portrait hội thoại mượn từ `GameAssets` (AssetManager cấp ứng dụng), giữ cache khi đóng/đổi hội thoại; font và texture UI được giải phóng cùng `DialogueUI`. Không dispose tài nguyên mượn. Getter bounds hiện trả đối tượng mutable; caller dùng để đọc, không tự sửa tọa độ/hitbox của map.

Không tạo tài nguyên đồ họa hoặc listener lặp lại mỗi frame. Một task root `generateAssetList` sinh `game/assets/assets.txt` khi xử lý resources; không sửa file đó bằng tay. GameAssets dùng AssetManager để chia sẻ texture/audio; asset-manifest.json quản lý đường dẫn và metadata animation.

Đọc [AGENTS/AGENTS.md](AGENTS/AGENTS.md) trước khi làm việc, giữ thay đổi theo một hệ thống, dùng API/abstraction hiện có. Sửa dialogue/inventory cần chạy regression checks. Sửa input/render/lifecycle cần kiểm tra desktop: di chuyển, NPC dialogue, F/Enter đóng thoại, chuyển phòng/sân, tủ đồ/click/hotbar, resize và thoát game. Compile và kiểm tra logic không thay thế visual QA.
