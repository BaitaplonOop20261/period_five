# Room304 — bộ đồ vật thử nghiệm

Tạo bằng ImageGen dựa trên room_304.png và guard_room_preview.png: pixel art, gỗ cũ màu nâu, kim loại tối, góc nhìn RPG từ trên xuống.

25 PNG RGBA trong `sprites/props/`, kích thước đúng bảng yêu cầu. Bản nguồn độ phân giải cao được thu về kích thước native bằng NearestNeighbor; giữ alpha và lề trong suốt một pixel. Các trạng thái dùng chung khung cắt theo nhóm và điểm đặt giữa cạnh dưới, không có hoạt ảnh.

| File | Kích thước |
| --- | --- |
| photo.png | 54×34 |
| register.png | 80×34 |
| desk_empty.png | 80×34 |
| desk_final.png | 80×34 |
| player.png | 54×28 |
| player_powered.png | 54×28 |
| player_playing.png | 54×28 |
| player_open.png | 54×28 |
| crate.png | 54×28 |
| crate_open.png | 54×28 |
| cabinet.png | 46×22 |
| cabinet_locked.png | 46×22 |
| cabinet_open.png | 46×22 |
| cabinet_upper.png | 46×20 |
| papers.png | 70×32 |
| lastdesk.png | 58×20 |
| window.png | 74×16 |
| board.png | 90×24 |
| guard_wall.png | 46×50 |
| guard_door.png | 46×50 |
| gate_closed.png | 70×24 |
| gate_open.png | 70×24 |
| evidence.png | 74×30 |
| evidence_signed.png | 74×30 |
| chair.png | 22×9 |

`contact-sheet.png`: toàn bộ trạng thái phóng đại 3× bằng NearestNeighbor.

`contact-sheet-continuation.png`: 9 đồ vật bổ sung phóng đại 3×. Các kích thước và trạng thái đã đối chiếu với phần Quy chuẩn chung và mục 3 (trang 13–14) trong `ASSET_SPEC/Room304_Brief_Artist_Audio_Producer.pdf`. Bộ trước làm từ bảng người dùng và cấu hình game; bộ bổ sung giữ đồng bộ phong cách theo yêu cầu, không sửa bộ trước.

`scene-scale-preview.png`: bảy đồ vật mẫu vẽ ở kích thước native trên nền 640×480, để xem tỉ lệ. Vị trí trong ảnh chỉ để so sánh, chưa gắn vào game hoặc sửa collision.

`validation.json`: kích thước, alpha ở góc và vùng có nội dung từng ảnh. `generation-manifest.json`: prompt đầy đủ từng ảnh.

`validation-continuation.json` và `generation-manifest-continuation.json`: thông tin kiểm tra và prompt của 9 PNG bổ sung. Cổng mở để trống lối giữa; tường/cửa dùng cùng khung; chữ ký nằm trên tờ bổ sung riêng của bàn đối chiếu.

Đây là bộ thử nghiệm do AI sinh. Chi tiết trạng thái nhỏ như đèn nguồn, khóa, bốn khe pin và chìa khóa cần xem ở native size khi tích hợp. Các biến thể cùng điểm đặt nhưng không bảo đảm toàn bộ pixel ngoài vùng đổi trạng thái hoàn toàn giống nhau.
