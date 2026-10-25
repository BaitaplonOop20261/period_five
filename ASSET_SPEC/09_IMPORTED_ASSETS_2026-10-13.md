# Đợt nhập thử asset — 13/10/2026

Đợt này dùng ảnh do người dùng cung cấp trong `ASSET_SPEC/added_asset/`. Các file nguồn được giữ nguyên. Chỉ các asset đã nhận được nhập; không tạo asset còn thiếu. Path runtime trong các bảng tính từ `game/assets/`.

Các đặc tả/checklist 11/10/2026 vẫn là kế hoạch sản xuất với frame nhân vật 32×48 và idle 2 frame/2 FPS. Ảnh nhân vật của đợt 13/10 có quy cách khác nên được ghi nhận là **ngoại lệ nhập thử**, chưa nghiệm thu hoặc đánh dấu hoàn thành bộ art sản xuất. Không sửa gameplay hoặc cốt truyện để khớp ảnh.

## Nhân vật đã nhập

| File nguồn dưới added_asset/ | ID manifest | Path runtime | PNG / grid | Frame count | Duration / cách hiển thị | Byte |
| --- | --- | --- | --- | --- | --- | --- |
| Linh_idle.png | char.linh.idle.down | sprites/linh/idle_down.png | RGBA 192×32; 6 cột × 1 hàng; frame 32×32 | 6 | 0,15 s/frame; loop 0,9 s | 1632 |
| Duc_idle.png | char.duc.idle.down | sprites/duc/idle_down.png | RGBA 192×32; 6 cột × 1 hàng; frame 32×32 | 6 | 0,15 s/frame; loop 0,9 s | 1601 |
| Trang_idle.png | char.trang.idle.down | sprites/trang/idle_down.png | RGBA 192×32; 6 cột × 1 hàng; frame 32×32 | 6 | 0,15 s/frame; loop 0,9 s | 1883 |
| BaNam.png | char.nam.idle.down | sprites/nam/idle_down.png | RGBA 32×32; 1 cột × 1 hàng; frame 32×32 | 1 | 1 s/frame; ảnh tĩnh để test | 792 |
| ThayHung.png | char.hung.idle.down | sprites/hung/idle_down.png | RGBA 32×32; 1 cột × 1 hàng; frame 32×32 | 1 | 1 s/frame; ảnh tĩnh để test | 784 |

Cả năm PNG có alpha 0/255 và nền trong suốt; frame một hàng ngang, không padding hoặc trim. Các bản runtime được chép nguyên byte từ nguồn, chỉ đổi thư mục/tên. Manifest dùng `texture`, `required=false`, `filter=Nearest` và metadata frame ghi rõ. Duration 0,15 s là lựa chọn cho lần nhập thử; PNG nguồn không có metadata cadence.

Ảnh chỉ cung cấp hướng xuống. Profile player Linh và NPC `trang`, `duc`, `nam`, `hung` dùng canvas 32×32/scale 1 để giữ tỷ lệ gốc, bind `idle.down`; footprint 18×10 và anchor (0,5; 0) giữ nguyên. Walk, hướng khác và pose chưa có dùng fallback `idle.down` đã có trong Player/StoryWorldView. Không tự tạo, đảo ảnh hoặc gắn nhãn animation đi cho các idle này. Nga tiếp tục dùng fallback hiện có; không nhập asset Nga mới.

| Nguồn / runtime cùng SHA-256 | SHA-256 |
| --- | --- |
| Linh_idle.png / sprites/linh/idle_down.png | `2F96F4D6AD3E424BCE28734B2BDF06E7DF86B38C49416237BC5B1E3A61044AF7` |
| Duc_idle.png / sprites/duc/idle_down.png | `C42FE0D19396D2769F2DE9AC2F8DCEE1C56BAE70C61985D5DEE889AA05460346` |
| Trang_idle.png / sprites/trang/idle_down.png | `B9E07632A04A6A8F45C7A9A0BD762559B6D210374DCBF39373843EE4A2C78C0C` |
| BaNam.png / sprites/nam/idle_down.png | `DA23B6538DC7A8E4BBE279CEFE448F1CB7986D332F0C9ABA08848E9808F7C6CB` |
| ThayHung.png / sprites/hung/idle_down.png | `3FE098E3C98775EC6D12EF951B4AB8013B81706250DFB4FD39AB2D53BA39DB23` |

## Phòng 304

Nguồn `ASSET_SPEC/added_asset/room_304.png` (640×480) được giữ nguyên. Lần chỉnh bố cục đầu bằng **imagegen tích hợp sẵn** bỏ viền đen lớn, giảm 20 bàn thường còn 8 bàn thành hai dãy đôi/hai hàng và nới lối giữa/hành lang ngang. Bàn riêng bên cửa sổ, bàn giáo viên, tủ và bàn phụ được giữ. Bản 1448×1086 của bước này được lưu tại `ASSET_SPEC/generated/room_304-layout-1448-reference.png` để đối chiếu lịch sử, không còn là texture runtime; prompt bố cục cũ ở [room_304-layout.prompt.txt](room_304-layout.prompt.txt).

Theo yêu cầu tiếp theo, imagegen vẽ lại phòng theo artstyle ảnh nguồn: gỗ nâu cũ, bảng/tủ xanh trầm, viền và cụm pixel rõ, ánh cửa sổ nhẹ; dùng bản bố cục đã chỉnh làm mục tiêu vị trí đồ vật. Bố cục tám bàn, camera và geometry đã nhập giữ nguyên. PNG runtime cuối là **RGBA kín đúng 640×480 ở 1×**, một pixel ảnh ứng với một world unit, lọc **Nearest**. Prompt mới ở [room_304-native-pixel.prompt.txt](room_304-native-pixel.prompt.txt); [công cụ xuất](../game/tools/export-room-map.ps1) chuẩn hóa kích thước bằng nearest-neighbor, không làm mượt. Bản xuất đối chiếu tại `ASSET_SPEC/generated/room_304-pixel-640.png` cùng byte/checksum với runtime.

| Bản | Path | Kích thước / byte | SHA-256 |
| --- | --- | --- | --- |
| Nguồn | `ASSET_SPEC/added_asset/room_304.png` | 640×480 / 275793 | `8A79E414C28B19D28F0AE4CB12D780CF825155DB3F8C7D253F657230141C1AAB` |
| Tham chiếu bố cục lịch sử | `ASSET_SPEC/generated/room_304-layout-1448-reference.png` | 1448×1086 / 1902426 | `D71A16ECD7BFF0D3E1BEAB769E945625B6A32606AC57A7B16F6A2E856E453F4E` |
| Runtime cuối | `game/assets/maps/room_304.png` | RGBA 640×480 / 619347 | `1C4AB9ECC4C0B9538508607DA1619EEED3A1E573A89C551B8E613ACD78D3AFFD` |
| Bản xuất đối chiếu | `ASSET_SPEC/generated/room_304-pixel-640.png` | RGBA 640×480 / 619347 | `1C4AB9ECC4C0B9538508607DA1619EEED3A1E573A89C551B8E613ACD78D3AFFD` |

Runtime dùng ID optional có sẵn `map.story.room_304`, lọc Nearest và vẽ 640×480 vào scene cùng kích thước; nền cuối đã đúng độ phân giải PNG đề xuất. Đây vẫn là đường PNG, chưa phải TMX/tileset hoàn chỉnh. Vùng va chạm và điểm tương tác đã đo theo bố cục được giữ; sai lệch vị trí đồ vật của bản vẽ lại 0–2 px, không cần đổi geometry. ID truyện, footprint nhân vật và tốc độ giữ nguyên. PNG kín được tint lạnh trong 0,3 giây theo hiện tượng, còn sprite/UI giữ màu. Tên nhân vật vẫn hiển thị khi dùng sheet thật; Nga chưa có asset nên giữ fallback không lộ tên trước nhận diện.

Tọa độ dưới đây tính trong runtime 640×480, gốc trái dưới. Đây là bounds tương tác của đợt PNG, thay tọa độ prototype 11/10; collision chân vật tách khỏi chiều cao ảnh. Khi nhận prop/TMX cuối, đối chiếu lại bounds trước khi kéo texture theo đề xuất kích thước cũ.

| Object | x, y | width × height |
| --- | --- | --- |
| door.hall | 300, 36 | 60×22 |
| photo | 106, 413 | 25×34 |
| desk | 195, 324 | 101×62 |
| player (máy cassette) | 574, 262 | 44×50 |
| crate | 574, 196 | 44×47 |
| cabinet | 557, 349 | 62×40 |
| cabinet.upper | 557, 389 | 62×58 |
| lastdesk | 455, 332 | 55×57 |
| window | 447, 411 | 100×32 |

Tám bàn/ghế thường có collision 60×63 ở x=123/210/371/458 và y=104/232. Lối giữa rộng 101 px; hành lang ngang giữa hàng rộng 65 px. Thêm collision cho bục/tủ xanh/đồ bên phải và biên phòng theo ảnh. Bốn spawn giữ nguyên; NPC phòng 304 điều chỉnh bà Năm (100,310), Hùng (330,270), Nga (455,304), Đức ở giai đoạn điều tra (435,304). Không thay NPC room/stage/event hoặc luật truyện. Save cũ có tọa độ chồng collision mới vẫn bị từ chối bằng kiểm tra có sẵn, không tự dịch/xóa save.

## Nguồn và quyền sử dụng

Thông tin nguồn xác nhận được trong đợt này: **ảnh do người dùng cung cấp**. Chưa có tên tác giả, nguồn gốc công cụ hoặc giấy phép riêng được cung cấp; không thấy metadata giấy phép nhúng để dùng làm căn cứ. Không tự gán giấy phép cho các ảnh. Các bản nguồn vẫn ở `ASSET_SPEC/added_asset/` để đối chiếu.

## Kiểm tra và giới hạn

- Đã xem trực tiếp năm ảnh nhân vật, decode PNG, kiểm kích thước/grid/alpha và checksum nguồn bằng runtime.
- Manifest JSON parse được; năm ID mới không trùng ID hoặc path hiện có; `git diff --check` cho phần manifest qua.
- Windows 11 / Temurin JDK 21.0.12.1 / Gradle Wrapper 9.8.0: bản nền 640×480 cuối đã chạy lại `build` và `:lwjgl3:storyDesktopTest`, trả exit 0. `:lwjgl3:desktopRegressionTest` của đợt nhập đầu trả exit 0. Core đạt 99/99 khối; kiểm tra idle thật đạt cả năm sheet, grid/texture/Nearest/profile binding. Desktop khóa kích thước thực tế nền 640×480 và filter Nearest để tránh quay lại ảnh lớn bị thu nhỏ lúc render.
- Desktop truyện hoàn tất A/B/C, 58 tương tác E bằng đi bộ/collision thật, 11.774 frame, menu/pause/ghi chú/replay và disk save/load. Fixture tìm đường dùng selector runtime ở đủ bốn hướng, xác minh ID sau bước đổi hướng rồi mới nhấn E; không teleport hoặc đổi tiến trình khi tìm đường. C được bật riêng trong fixture; config mặc định vẫn false.
- Decode bản PNG cuối xác nhận RGBA 640×480 và cả 307.200 pixel có alpha 255; runtime và bản xuất đối chiếu có cùng checksum. Đã xem ảnh 1× và 2× Nearest, vị trí đồ vật khớp geometry đã nhập trong khoảng 0–2 px. Bảng màu 24–32 màu chính vẫn là đề xuất sản xuất, không phải xác nhận số màu tổng của PNG này.
- Kiểm tra RGB ở điểm nền trống (330,190) trên bản cuối: trung tính `[103,70,46]` → lạnh `[77,61,46]` sau 0,4 giây; xác nhận PNG kín không che tín hiệu lạnh và progress bản sao không đổi.
- Đã xem trực tiếp `game/lwjgl3/build/story-room304-neutral-16x9.png` và `story-room304-five-characters-16x9.png`: năm nhân vật hiển thị đúng, lối đi rõ, nhãn trong canvas, nền lạnh phân biệt được. Ảnh ở trạng thái FINAL dùng bản sao tiến độ để xem nền không có hộp thoại; không phải tuyên bố tự chơi thủ công hết game.
- `node game/tools/check-story-source.mjs` giữ 538 chuỗi, 385 nhịp, 364 đơn vị thoại, 99 khối. `git diff --check` qua. Review độc lập kiểm renderer/batch/ownership và profile không còn phát hiện cần sửa sau khi preview chờ đủ thời gian tint.
- Chưa nghiệm thu Oracle JDK 21.0.11, art/audio cuối, bốn hướng/walk/pose hoặc TMX/tileset. Các kết quả hiện tại xác nhận bộ asset thử đã cung cấp, không đóng gate sản xuất còn thiếu.
- Các yêu cầu sản xuất còn thiếu trong [master](01_ASSET_MASTER_LIST.md), [nhân vật](03_CHARACTER_AND_ANIMATION.md) và [phiếu artist](FOR_ARTISTS.md) giữ nguyên. Chỉ ảnh hướng xuống trong bảng nhập thử đã được thêm, không có bộ bốn hướng, walk, pose hoặc portrait mới.
