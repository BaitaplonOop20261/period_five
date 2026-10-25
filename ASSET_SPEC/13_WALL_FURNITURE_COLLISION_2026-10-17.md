> Ghi chú lịch sử: bố trí, nhãn tương tác và mặc định camera đã được điều chỉnh trong lần ghép 24/10/2026. Xem README.md và story-world.json cho hành vi hiện tại.

# Va chạm đồ vật sát tường — 17/10/2026

Ảnh lỗi cho thấy Linh đứng trong phần trên của tủ và bàn cạnh cửa sổ. Nguyên nhân: các vùng collision cũ chỉ phủ chân đồ vật, đồng thời để một dải sàn đi được phía sau. Không cần chỉnh PNG hoặc thay vị trí ảnh để sửa lỗi va chạm này.

## Geometry phòng 304

Tọa độ world 320×240, gốc trái dưới; x/y/width giữ nguyên trừ hàng tường sau.

| Vùng | Trước | Sau |
| --- | --- | --- |
| Tường sau | y=200, height=40 | y=192, height=48 |
| Bục/bàn giáo viên | x=79, y=160, width=141, height=20 | height=40, nối kín vào tường |
| Tủ | x=278,5, y=174,5, width=31, height=12,5 | height=65,5, chặn cả ngăn trên và phía sau |
| Bàn cửa sổ | x=227,5, y=166, width=27,5, height=12,5 | height=34, chặn toàn mặt bàn và phía sau |

Các bàn thường, spawn, NPC và ảnh nền giữ dữ liệu hiện có. Không sửa tốc độ, footprint hoặc thuật toán di chuyển của Player.

## Vùng chọn E

Với vật kê tường hoặc xếp theo chiều cao, vùng chọn E cần nằm ở phần người chơi có thể tiếp cận. `SceneCatalog.ObjectDef.interaction` là Box tùy chọn gồm x/y/width/height tính theo world. Không khai báo thì selector dùng bounds đồ vật như trước.

- `cabinet`: vùng chọn phía trước ngăn dưới.
- `cabinet.upper`: vùng chọn bên trái, cho phép nhìn lên ngăn trên từ sàn.
- `window`: vùng chọn trên phần sàn hẹp bên phải bàn, để không bị tủ/bàn giành mục tiêu E.

Selector và input cùng dùng vùng chọn này; marker/viền vàng vẫn dùng bounds ảnh thực, không vẽ viền quanh vùng chọn ẩn. Loader và validator geometry kiểm tra Box; adapter TMX dời/scale Box theo object khi thay bố cục.

## Kiểm chứng

- `build` và `lwjgl3:storyDesktopTest` trả exit 0 trên Temurin 21.0.12.1, Gradle Wrapper 9.8.0.
- Core vẫn qua 99/99 khối truyện; desktop hoàn tất A/B/C, 65 tương tác E và 11.275 frame, save/load, pause, ghi chú, marker/viền và resize.
- Các tọa độ trước đây đứng được trong thân tủ, trên bàn cửa sổ, sau bàn giáo viên và trong tường giờ bị `canStand` từ chối.
- Probe Player thật kiểm tra giữ W, đi chéo và frame chậm 0,25 giây: không xuyên lên đồ vật hoặc vùng tường. Probe mượn asset, không thay tiến độ lượt chơi.
- Kiểm tra đi bộ bằng input thật vẫn chọn được cửa sổ, ngăn dưới và ngăn trên; E ở ngăn trên vẫn mở/đóng thoại bình thường. Fixture tìm đường dùng bước 2 world unit trong phòng nhỏ để không bỏ sót lối đi hẹp hợp lệ.
- Đã xem ảnh `game/lwjgl3/build/story-collision-window-desk-front.png`, `story-collision-cabinet-front.png`, `story-collision-cabinet-upper-side.png` và `story-collision-window-approach.png`.

Cấu hình scene được đọc khi tạo game/screen; mở lại game để nạp va chạm mới. Bản lưu ở vị trí cũ nằm trong vật cản mới sẽ bị kiểm tra hiện có từ chối; không tự sửa hoặc xóa bản lưu.
