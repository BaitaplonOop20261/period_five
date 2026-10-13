# Room304 — Phiếu sản xuất cho artist

**Quy chuẩn bắt buộc khi xuất file**

CONFIRMED = đã xác nhận từ thiết kế/bản đang chạy. **PROPOSED = đề xuất cần duyệt trước khi sản xuất hàng loạt**; chưa phải quyết định đã duyệt. UNDECIDED = chưa chốt. Kích thước trong các bảng sản xuất dưới đây là PROPOSED; không tự đổi giữa chừng.

| Hạng mục | Quy chuẩn |
| --- | --- |
| Phong cách | CONFIRMED: pixel art 2D nhìn từ trên xuống, kinh dị tâm lý; không gore/đuổi bắt. PROPOSED: bảng màu chung 24–32 màu chính. |
| Màn hình / background | CONFIRMED: khung nhìn 640×480 (4:3), hiển thị 16:9 có viền. PROPOSED: giữ kích thước này; CG tùy chọn 640×480. |
| Bản đồ / tile | CONFIRMED: tile 32×32. PROPOSED: mỗi phòng 20×15 ô, một tileset 512×256 dùng chung. Không vẽ trùng toàn cảnh PNG. |
| Nhân vật | CONFIRMED: khung hiện tại 32×48. PROPOSED: dùng đúng 32×48 cho mọi frame; chân giữa đáy tại (16,48), không trim. |
| Portrait | PROPOSED: 150×175, PNG trong suốt; cùng vị trí đầu/vai giữa các biểu cảm. |
| Spritesheet | PROPOSED: một hàng ngang; thứ tự từ trái sang phải; không padding, không đảo hướng tự động. |
| Animation | PROPOSED: idle 2 frame/2 FPS (0,5 s); walk 6 frame/8 FPS (0,125 s); pose xem từng dòng. |
| Định dạng / alpha | PNG RGBA, nền trong suốt cho nhân vật/portrait/prop/UI; sàn nền kín. Không JPG, không matte trắng, không làm nhòe viền. |
| Lọc và scale | Nearest cho pixel art đề xuất; xuất 1×, không phóng to trước. Chữ dùng font có sẵn. |
| Tên / thư mục | Chữ thường không dấu, dấu gạch dưới; giữ đúng từng path bên dưới. Tất cả path bắt đầu từ game/assets/. |
| Chữ trên ảnh | Không vẽ nút, phụ đề, hội thoại hay toàn bộ nội dung giấy. Chỉ vẽ dấu mực/chữ ký/chi tiết nhận dạng được yêu cầu ở bảng tài liệu. |

**Cách dùng checklist:** mỗi ô [ ] là file còn phải làm; [x] chỉ file đã có và kiểm tra phù hợp. P0 làm trước; P1 hoàn thiện quan trọng; P2 tùy chọn. Asset C vẫn liệt kê dù kết thúc C hiện tắt mặc định.

**Cần duyệt trước:** diện mạo/đồng phục sáu nhân vật và bảng màu chưa có thiết kế mẫu (UNDECIDED); số frame/FPS, portrait 150×175, tileset 512×256, kích thước đồ vật và cách phối âm là PROPOSED. Duyệt một frame đứng và một chân dung trước khi vẽ cả bộ. Không tự thêm tên người, nhân vật hay sự kiện.

## 1. Nhân vật và hoạt ảnh

Năm người có bốn hướng; mỗi hướng xuất file riêng. Nga chỉ cần hai hướng đứng và đi xuống vì xuất hiện theo cảnh. “Một lần” = diễn động tác rồi giữ hình cuối; không quay về tư thế ban đầu ở frame cuối. Ảnh 1 frame là pose tĩnh, FPS trong bảng không có nghĩa phải vẽ thêm. Tất cả PNG trong suốt, toàn thân 32×48; dù ngồi vẫn giữ canvas và điểm chân. **Walk của Trang/Đức/bà Năm/Hùng là P2** cho dàn cảnh sau, không cần làm trước bộ đứng.

### Linh

Nhân vật người chơi; cầm sổ/chụp bằng điện thoại; cuối C ngồi im, môi mấp máy không phát tiếng.

| Kiểm / ID / ưu tiên | File cần vẽ | Nội dung | Frame px | Frame | FPS | Sheet px / cách chạy |
| --- | --- | --- | --- | --- | --- | --- |
| [ ] char.linh.idle.down · P0 | sprites/linh/idle_down.png | Linh đứng nhìn xuống. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.linh.walk.down · P0 | sprites/linh/walk_down.png | Linh đi hướng xuống. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.linh.idle.up · P0 | sprites/linh/idle_up.png | Linh đứng nhìn lên. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.linh.walk.up · P0 | sprites/linh/walk_up.png | Linh đi hướng lên. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.linh.idle.left · P0 | sprites/linh/idle_left.png | Linh đứng nhìn trái. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.linh.walk.left · P0 | sprites/linh/walk_left.png | Linh đi hướng trái. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.linh.idle.right · P0 | sprites/linh/idle_right.png | Linh đứng nhìn phải. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.linh.walk.right · P0 | sprites/linh/walk_right.png | Linh đi hướng phải. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.linh.pose.phone · P0 | sprites/linh/pose_phone.png | Giơ điện thoại chụp/ghi âm, hướng xuống chếch phải. | 32×48 | 4 | 8 | 128×48; một lần, giữ cuối |
| [ ] char.linh.pose.book · P0 | sprites/linh/pose_book.png | Giữ sổ trên tay; ảnh tĩnh. | 32×48 | 1 | 1 | 32×48; một lần, giữ cuối |
| [ ] char.linh.pose.seated · P1 | sprites/linh/pose_seated.png | C: ngồi bàn cuối, môi mấp máy không tiếng; chuỗi một lần. | 32×48 | 4 | 4 | 128×48; một lần, giữ cuối |

### Trang

Bạn đi cùng Linh; cuối C nhìn vào phòng và đọc điện thoại, không trở thành nhân vật điều khiển.

| Kiểm / ID / ưu tiên | File cần vẽ | Nội dung | Frame px | Frame | FPS | Sheet px / cách chạy |
| --- | --- | --- | --- | --- | --- | --- |
| [ ] char.trang.idle.down · P0 | sprites/trang/idle_down.png | Trang đứng nhìn xuống. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.trang.walk.down · P2 | sprites/trang/walk_down.png | Trang đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.trang.idle.up · P0 | sprites/trang/idle_up.png | Trang đứng nhìn lên. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.trang.walk.up · P2 | sprites/trang/walk_up.png | Trang đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.trang.idle.left · P0 | sprites/trang/idle_left.png | Trang đứng nhìn trái. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.trang.walk.left · P2 | sprites/trang/walk_left.png | Trang đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.trang.idle.right · P0 | sprites/trang/idle_right.png | Trang đứng nhìn phải. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.trang.walk.right · P2 | sprites/trang/walk_right.png | Trang đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.trang.pose.phone · P1 | sprites/trang/pose_phone.png | C: giơ điện thoại đọc tin Linh. | 32×48 | 4 | 8 | 128×48; một lần, giữ cuối |

### Đức

Em trai Nga; ảnh gia đình có phiên bản còn nhỏ, không phải một nhân vật mới trong thế giới.

| Kiểm / ID / ưu tiên | File cần vẽ | Nội dung | Frame px | Frame | FPS | Sheet px / cách chạy |
| --- | --- | --- | --- | --- | --- | --- |
| [ ] char.duc.idle.down · P0 | sprites/duc/idle_down.png | Đức đứng nhìn xuống. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.duc.walk.down · P2 | sprites/duc/walk_down.png | Đức đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.duc.idle.up · P0 | sprites/duc/idle_up.png | Đức đứng nhìn lên. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.duc.walk.up · P2 | sprites/duc/walk_up.png | Đức đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.duc.idle.left · P0 | sprites/duc/idle_left.png | Đức đứng nhìn trái. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.duc.walk.left · P2 | sprites/duc/walk_left.png | Đức đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.duc.idle.right · P0 | sprites/duc/idle_right.png | Đức đứng nhìn phải. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.duc.walk.right · P2 | sprites/duc/walk_right.png | Đức đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.duc.pose.photo · P1 | sprites/duc/pose_photo.png | Trao ảnh gia đình, không cần ảnh đứng riêng. | 32×48 | 4 | 8 | 128×48; một lần, giữ cuối |

### Bà Năm

Bà Năm, bảo vệ trường; giữ chìa và mở cổng.

| Kiểm / ID / ưu tiên | File cần vẽ | Nội dung | Frame px | Frame | FPS | Sheet px / cách chạy |
| --- | --- | --- | --- | --- | --- | --- |
| [ ] char.nam.idle.down · P0 | sprites/nam/idle_down.png | Bà Năm đứng nhìn xuống. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.nam.walk.down · P2 | sprites/nam/walk_down.png | Bà Năm đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.nam.idle.up · P0 | sprites/nam/idle_up.png | Bà Năm đứng nhìn lên. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.nam.walk.up · P2 | sprites/nam/walk_up.png | Bà Năm đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.nam.idle.left · P0 | sprites/nam/idle_left.png | Bà Năm đứng nhìn trái. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.nam.walk.left · P2 | sprites/nam/walk_left.png | Bà Năm đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.nam.idle.right · P0 | sprites/nam/idle_right.png | Bà Năm đứng nhìn phải. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.nam.walk.right · P2 | sprites/nam/walk_right.png | Bà Năm đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.nam.pose.key · P1 | sprites/nam/pose_key.png | Trao/nhận chìa ngăn dưới. | 32×48 | 4 | 8 | 128×48; một lần, giữ cuối |
| [ ] char.nam.pose.gate · P1 | sprites/nam/pose_gate.png | Đưa tay mở cổng, giữ silhouette rõ. | 32×48 | 4 | 8 | 128×48; một lần, giữ cuối |

### Thầy Hùng

Thầy Hùng; thu xếp sách, viết xác nhận trên tờ giấy mới; không vẽ cảnh được tha thứ.

| Kiểm / ID / ưu tiên | File cần vẽ | Nội dung | Frame px | Frame | FPS | Sheet px / cách chạy |
| --- | --- | --- | --- | --- | --- | --- |
| [ ] char.hung.idle.down · P0 | sprites/hung/idle_down.png | Thầy Hùng đứng nhìn xuống. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.hung.walk.down · P2 | sprites/hung/walk_down.png | Thầy Hùng đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.hung.idle.up · P0 | sprites/hung/idle_up.png | Thầy Hùng đứng nhìn lên. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.hung.walk.up · P2 | sprites/hung/walk_up.png | Thầy Hùng đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.hung.idle.left · P0 | sprites/hung/idle_left.png | Thầy Hùng đứng nhìn trái. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.hung.walk.left · P2 | sprites/hung/walk_left.png | Thầy Hùng đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.hung.idle.right · P0 | sprites/hung/idle_right.png | Thầy Hùng đứng nhìn phải. | 32×48 | 2 | 2 | 64×48; lặp |
| [ ] char.hung.walk.right · P2 | sprites/hung/walk_right.png | Thầy Hùng đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.hung.pose.write · P0 | sprites/hung/pose_write.png | Cúi viết/ký trên tờ mới, không sửa sổ. | 32×48 | 6 | 8 | 192×48; một lần, giữ cuối |

### Nga

Nguyễn Thu Nga, nữ sinh lớp 12A3. Bóng ngồi trên tường khi ghế thật trống, chỉ thùng, đứng sau kính/sau Hùng, đi ra ở A. Không vết thương; độ mờ sẽ được điều chỉnh khi hiển thị.

| Kiểm / ID / ưu tiên | File cần vẽ | Nội dung | Frame px | Frame | FPS | Sheet px / cách chạy |
| --- | --- | --- | --- | --- | --- | --- |
| [ ] char.nga.idle.down · P0 | sprites/nga/idle_down.png | Đứng nhìn xuống; không thương tích, không hình quái vật. | 32×48 | 1 | 1 | 32×48; lặp |
| [ ] char.nga.idle.up · P0 | sprites/nga/idle_up.png | Bóng nhìn lên, dùng sau kính cửa. | 32×48 | 1 | 1 | 32×48; lặp |
| [ ] char.nga.walk.down · P0 | sprites/nga/walk_down.png | A: đi ra cửa. | 32×48 | 6 | 8 | 192×48; lặp |
| [ ] char.nga.pose.seated · P0 | sprites/nga/pose_seated.png | Ngồi; dùng cùng hình cho bóng tường và bàn cuối. | 32×48 | 1 | 1 | 32×48; một lần, giữ cuối |
| [ ] char.nga.pose.point · P0 | sprites/nga/pose_point.png | Giơ tay chỉ thùng bên phải; không chỉ lời giải khác. | 32×48 | 4 | 8 | 128×48; một lần, giữ cuối |

### Chân dung và biểu cảm

150×175 px, một ảnh PNG trong suốt mỗi dòng; không khung, không tên viết trên hình. Bộ trung tính P0 đủ cho mọi hội thoại; biểu cảm phụ P2 chỉ làm sau khi duyệt. Giọng điểm danh, tiếng sau cửa, Hùng/Nga trên băng **không cần chân dung mới**.

| Kiểm / ID / ưu tiên | File | Biểu cảm |
| --- | --- | --- |
| [ ] portrait.linh.normal · P0 | portraits/linh_normal.png | Linh trung tính; cùng góc mặt, cùng đường vai. |
| [ ] portrait.trang.normal · P0 | portraits/trang_normal.png | Trang trung tính; cùng góc mặt, cùng đường vai. |
| [ ] portrait.duc.normal · P0 | portraits/duc_normal.png | Đức trung tính; cùng góc mặt, cùng đường vai. |
| [ ] portrait.nam.normal · P0 | portraits/nam_normal.png | Bà Năm trung tính; cùng góc mặt, cùng đường vai. |
| [ ] portrait.hung.normal · P0 | portraits/hung_normal.png | Thầy Hùng trung tính; cùng góc mặt, cùng đường vai. |
| [ ] portrait.nga.normal · P0 | portraits/nga_normal.png | Nga trung tính; cùng góc mặt, cùng đường vai. |
| [ ] portrait.linh.worried · P2 | portraits/linh_worried.png | Linh lo lắng; biến thể gương mặt đề xuất. |
| [ ] portrait.trang.worried · P2 | portraits/trang_worried.png | Trang lo lắng; biến thể gương mặt đề xuất. |
| [ ] portrait.duc.grief · P2 | portraits/duc_grief.png | Đức đau buồn, không ngầm tha thứ; biến thể gương mặt đề xuất. |
| [ ] portrait.nam.grave · P2 | portraits/nam_grave.png | Bà Năm nghiêm nghị; biến thể gương mặt đề xuất. |
| [ ] portrait.hung.remorse · P2 | portraits/hung_remorse.png | Thầy Hùng nhận trách nhiệm; biến thể gương mặt đề xuất. |
| [ ] portrait.nga.quiet · P2 | portraits/nga_quiet.png | Nga bình lặng, không nụ cười tha thứ; biến thể gương mặt đề xuất. |

## 2. Môi trường và bản đồ

**Chọn tileset + TMX cho cả bốn phòng.** Vẽ một bộ gạch, người dựng bản đồ ghép lại; không vẽ thêm bốn background toàn cảnh giống hệt. Bản PNG toàn cảnh chỉ là phương án thay thế nếu nhóm đổi quyết định sau.

| Kiểm / ID / ưu tiên | File | Kích thước | Nội dung |
| --- | --- | --- | --- |
| [ ] tileset.school · P0 | maps/tilesets/school.png | 512×256 PNG RGBA; tile 32×32; 16×8 ô | Bộ chung cho bốn phòng; chỉ vẽ các ô được giao, ô dư trong suốt. |
| [ ] tileset.school.definition · P0 | maps/tilesets/school.tsx | TSX; tile 32×32; 128 ô; margin/spacing 0 | File lắp tileset; người dựng bản đồ thực hiện. |
| [ ] map.tmx.room_304 · P0 | maps/room_304.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Dựng Phòng 304 từ school.tsx; không vẽ lại PNG toàn cảnh. |
| [ ] map.tmx.hallway · P0 | maps/hallway.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Dựng Hành lang tầng ba từ school.tsx; không vẽ lại PNG toàn cảnh. |
| [ ] map.tmx.guard_room · P0 | maps/guard_room.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Dựng Phòng bảo vệ từ school.tsx; không vẽ lại PNG toàn cảnh. |
| [ ] map.tmx.classroom · P0 | maps/classroom.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Dựng Lớp học bên cạnh từ school.tsx; không vẽ lại PNG toàn cảnh. |

| Khu vực | Bố cục phải có |
| --- | --- |
| Phòng 304 | Cửa phía dưới; bàn giáo viên/sổ phía trên; ảnh tường trái; bàn phụ/máy bên phải, thùng dưới; tủ góc trên phải; bàn cuối gần cửa sổ. Chừa lối rộng ít nhất 64 px. |
| Hành lang | Cửa 304 giữa; lớp Hùng trái; cầu thang đầu trái; bảng tin và khay phấn gần 304; mảng tường/cửa bảo vệ cuối phải. Vòng lặp dùng nguyên hành lang. |
| Bảo vệ | Bàn giấy trái, bảng chìa cạnh bà Năm, ngăn pin dưới bàn; cổng phía dưới. Vẽ cả ngưỡng ngoài và đường ngoài trường trong cùng khung. |
| Lớp Hùng | Sách/đồ đang đóng thùng; bàn Hùng giữa, bàn đối chiếu bên phải, cửa phía dưới. Dùng lại khi thú nhận và ký. |

**Chia bộ gạch school.png (16 cột × 8 hàng, mỗi ô 32×32):** hàng 0 sàn/biến thể; hàng 1 tường/góc/chân tường; hàng 2 bậc cầu thang và khung cửa; hàng 3 bàn ghế nền/bàn phụ; hàng 4 sách, hộp đóng gói, bảng chìa; hàng 5 khay phấn, ngăn pin, khung tủ; hàng 6 ánh chiều/lối ngoài cổng; hàng 7 dự phòng. Một vật lớn có thể chiếm nhiều ô liền nhau; ô chưa dùng để trong suốt, không buộc vẽ đủ 128 ô. Chìa có thẻ ngăn dưới và viên phấn nằm trong các ô đồ vật tương ứng. Không bịa chữ trên thẻ ngoài tên ngăn.

**Tách lớp:** `ground` sàn; `decor` tường/đồ nền không tương tác; nhân vật, ghế trượt, sổ, máy, thùng, tủ, cổng và cửa hiện ra để riêng. Không vẽ sẵn nhân vật/bóng, vạch phấn hiện tượng hoặc đồ đã lấy vào nền. Phần che trước nhân vật để layer `foreground` riêng khi bàn giao, chưa gộp nền; không phụ thuộc nó để đi được. Người dựng map thêm lớp va chạm/điểm đứng, artist không cần làm nếu chỉ nhận phần vẽ. Không thêm sân, tầng dưới hoặc phòng năm 2015.

## 3. Đồ vật và trạng thái

Mỗi dòng là **một PNG trong suốt, một ảnh tĩnh**. Cùng đồ vật giữ canvas/điểm đặt giống nhau giữa trạng thái. Kích thước bám vùng vẽ nhỏ hiện tại; cần xem mẫu trong phòng trước khi sản xuất hết. Dùng góc nhìn từ trên, không vẽ tủ đứng 96 px rồi thu nhỏ vào khung 46×22. Các cận cảnh lớn nằm ở phần tài liệu.

| Kiểm / ID / ưu tiên | File | Px | Phải vẽ |
| --- | --- | --- | --- |
| [ ] prop.photo · P0 | sprites/props/photo.png | 54×34 | Ảnh treo 304; thu nhỏ bản document.photo. |
| [ ] prop.register · P0 | sprites/props/register.png | 80×34 | Bàn giáo viên + sổ, trạng thái ban đầu. |
| [ ] prop.desk.empty · P0 | sprites/props/desk_empty.png | 80×34 | Cùng bàn, đã lấy sổ; giữ nền bàn đồng nhất. |
| [ ] prop.desk.final · P0 | sprites/props/desk_final.png | 80×34 | Cùng bàn + sổ, băng và giấy ở buổi điểm danh cuối. |
| [ ] prop.player · P0 | sprites/props/player.png | 54×28 | Máy cassette thiếu nguồn, đèn tắt. |
| [ ] prop.player.powered · P0 | sprites/props/player_powered.png | 54×28 | Cùng máy, đèn nguồn sáng. |
| [ ] prop.player.playing · P0 | sprites/props/player_playing.png | 54×28 | Cùng máy, có băng đang chạy; ảnh trạng thái tĩnh. |
| [ ] prop.player.open · P1 | sprites/props/player_open.png | 54×28 | Mở khay pin trống, bốn vị trí AA; chi tiết lớn xem document.battery. |
| [ ] prop.crate · P0 | sprites/props/crate.png | 54×28 | Thùng thiết bị còn buộc dây, có nhãn. |
| [ ] prop.crate.open · P0 | sprites/props/crate_open.png | 54×28 | Thùng mở, đã lấy một băng, phiếu kiểm kê còn bên trong. |
| [ ] prop.cabinet · P0 | sprites/props/cabinet.png | 46×22 | Ngăn dưới đóng, chưa khóa. |
| [ ] prop.cabinet.locked · P0 | sprites/props/cabinet_locked.png | 46×22 | Ngăn dưới đóng khóa; cùng vị trí ổ khóa. |
| [ ] prop.cabinet.open · P1 | sprites/props/cabinet_open.png | 46×22 | Ngăn dưới mở, chỗ đặt sổ nằm phẳng. |
| [ ] prop.cabinet.upper · P0 | sprites/props/cabinet_upper.png | 46×20 | Ngăn trên; cùng chất liệu tủ, không dùng cất sổ. |
| [ ] prop.papers · P0 | sprites/props/papers.png | 70×32 | Hai giấy gốc ở bàn bảo vệ. |
| [ ] prop.lastdesk · P0 | sprites/props/lastdesk.png | 58×20 | Bàn cuối, ghế để riêng. |
| [ ] prop.window · P0 | sprites/props/window.png | 74×16 | Cửa sổ, sân chỉ thấy qua đây; không vẽ bản đồ sân. |
| [ ] prop.board · P0 | sprites/props/board.png | 90×24 | Bảng tin, giấy sửa chữa và nội san cùng vị trí. |
| [ ] prop.guard.wall · P0 | sprites/props/guard_wall.png | 46×50 | Mảng tường trước khi cửa hiện. |
| [ ] prop.guard.door · P0 | sprites/props/guard_door.png | 46×50 | Cửa bảo vệ thay đúng mảng tường; tay nắm rõ. |
| [ ] prop.gate.closed · P0 | sprites/props/gate_closed.png | 70×24 | Cổng đóng; nhìn rõ ngưỡng trong/ngoài. |
| [ ] prop.gate.open · P0 | sprites/props/gate_open.png | 70×24 | Cùng cổng mở, không che đường qua. |
| [ ] prop.evidence · P0 | sprites/props/evidence.png | 74×30 | Bàn đối chiếu trước khi ký; không hiện giấy đã ký sớm. |
| [ ] prop.evidence.signed · P0 | sprites/props/evidence_signed.png | 74×30 | Cùng bàn có tờ bổ sung, ký Hùng, không sửa trang cũ. |
| [ ] prop.chair · P0 | sprites/props/chair.png | 22×9 | Ghế thật ở bàn cuối, tách khỏi nền để dịch chuyển. |

**Đồ không cần file icon riêng:** chìa/thẻ = bảng chìa trong school.png + động tác bà Năm; băng = ảnh cận documents/tape.png và máy/bàn ở trên; bốn pin = documents/battery.png + ngăn pin trong tileset; điện thoại = động tác cầm và ảnh tài liệu; sách/hộp = tileset; vạch phấn = nét trắng được hiển thị khi cần. Không phải vẽ lại thành bộ inventory. Ảnh gia đình, bản ghi, báo cáo, kiểm kê và giấy ký xem phần 4.

## 4. Tài liệu, vật chứng, hiệu ứng và ending

Các ảnh tài liệu 640×480: dành vùng quan trọng ở **nửa trên**, lề 24 px; nửa dưới bị khung chữ che. Game hiển thị đầy đủ nội dung chữ, không viết toàn bộ văn bản vào ảnh. Ngoại lệ thị giác: hai lớp mực “có mặt” bị “vắng” đè lên, ngày 11/09/2015 và chữ ký “Hùng” có thể vẽ trên giấy; không tự bổ sung danh sách 32 tên. Ảnh lớp phải có **đúng 33 người đếm được**, không chỉ ghi con số 33. Chú thích mới ở ending A sẽ được hiển thị ngoài ảnh gốc, không vẽ lại ảnh đổi người/tên.

| Kiểm / ID / ưu tiên | File | Px | Nội dung |
| --- | --- | --- | --- |
| [ ] effect.memory · P0 | effects/memory.png | 24×40 | Bóng học sinh vô danh, không thương tích; tái dùng vài bóng mờ, không phải 32 nạn nhân. |
| [ ] document.msg · P0 | documents/msg.png | 640×480 | Khung điện thoại nhận tin thầy Hùng; không viết sẵn nội dung tin. |
| [ ] document.photo · P0 | documents/photo.png | 640×480 | Ảnh lớp 12A3: đúng 33 học sinh đếm được, mép chú thích bong; không thêm tên. |
| [ ] document.register · P0 | documents/register.png | 640×480 | Trang sổ 11/09/2015, hai lớp mực có mặt/vắng ở dòng Nga; không vẽ tên Linh cố định. |
| [ ] document.news · P0 | documents/news.png | 640×480 | Mẩu nội san học sinh, giấy cũ; chữ cảnh báo do game hiện. |
| [ ] document.gate · P0 | documents/gate.png | 640×480 | Bản ghi bà Năm: 32 người ngoài sân, không phải danh sách 32 tên. |
| [ ] document.report · P0 | documents/report.png | 640×480 | Báo cáo sơ tán sai; không vẽ cảnh cháy hoặc nạn nhân. |
| [ ] document.inventory · P0 | documents/inventory.png | 640×480 | Phiếu thu hồi 01 băng từ máy ghi âm tại 304. |
| [ ] document.signed · P0 | documents/signed.png | 640×480 | Tờ mới năm 2025; chữ ký Hùng, không tự đặt ngày/tháng. |
| [ ] document.family · P0 | documents/family.png | 640×480 | Nga và Đức trước 2015; Đức là em nhỏ (7 tuổi lúc Nga mất). |
| [ ] document.tape · P0 | documents/tape.png | 640×480 | Cận băng gốc duy nhất; nhãn Tập phát thanh — 12A3 — 11/09/2015. |
| [ ] document.battery · P0 | documents/battery.png | 640×480 | Cận khay và bộ bốn pin AA 1,5 V, dấu +/− rõ, không thêm kiểu pin. |
| [ ] document.c_phone · P1 | documents/trang_phone.png | 180×245 | C: điện thoại Trang nhận tin Linh; nền và bong bóng trống, chữ do game hiện. |
| [ ] background.menu · P2 | ui/menu.png | 640×480 | Menu: tái bố cục phòng 304, chừa giữa cho chữ; không thêm cảnh. Có thể giữ nền màu, không bắt buộc vẽ CG riêng. |
| [ ] background.ending.a · P2 | endings/a.png | 640×480 | A: phòng/cổng yên, chứng cứ còn giữ; không minh họa đã được tha thứ. Có thể giữ nền màu, không bắt buộc vẽ CG riêng. |
| [ ] background.ending.b · P2 | endings/b.png | 640×480 | B: phòng 304 trống, tủ khóa, buổi điểm danh chưa dứt. Có thể giữ nền màu, không bắt buộc vẽ CG riêng. |
| [ ] background.ending.c · P2 | endings/c.png | 640×480 | C: Linh ngồi bàn cuối nhìn từ phía Trang; không hình tử vong. Có thể giữ nền màu, không bắt buộc vẽ CG riêng. |

Cận cảnh điện thoại Trang 180×245: chừa bong bóng trống; tin cần hiện là **“Đừng trả lời khi nghe họ gọi tên tớ.”**, người gửi Linh; không vẽ chữ lần thứ hai. Chỉ có sáu nhân vật có tên; bóng ký ức không phải nạn nhân bổ sung. Không cần bộ tranh cutscene: dùng nhân vật/đồ vật/bốn phòng đã liệt kê. CG menu/A/B/C là P2, không bắt buộc để hoàn thiện nội dung.

## 5. Giao diện

Không vẽ chữ, tên, mục tiêu, phím E, lựa chọn, thanh âm lượng, tên Linh hiện trên sổ hoặc từng nút menu thành ảnh. Dùng chung khung cho menu mới/tiếp tục, pause, trợ giúp, lưu/tải, ghi chú và ending. Nameplate là tên trên khung thoại; không cần ảnh riêng. Khung xem giấy nằm trong ảnh giấy/phần chữ; không có bộ khung cho từng tài liệu.

| Kiểm / ID / ưu tiên | File | Px | Nội dung |
| --- | --- | --- | --- |
| [ ] ui.art.dialogue_frame · P1 | ui/source/dialogue_frame.9.png | 66×66 | Khung thoại; ruột 64×64, góc giữ 8 px, phần giữa giãn. Không vẽ chữ. |
| [ ] ui.art.panel_frame · P1 | ui/source/panel_frame.9.png | 34×34 | Khung menu/pause/ghi chú/HUD dùng chung; ruột 32×32, góc giữ 6 px. Không vẽ chữ. |
| [ ] ui.art.button_normal · P1 | ui/source/button_normal.9.png | 34×34 | Nút thường; ruột 32×32, góc giữ 6 px. Không vẽ chữ. |
| [ ] ui.art.button_focus · P1 | ui/source/button_focus.9.png | 34×34 | Nút được chọn/rê chuột; cùng khung nút thường. Không vẽ chữ. |
| [ ] ui.art.button_pressed · P1 | ui/source/button_pressed.9.png | 34×34 | Nút đang nhấn; cùng khung nút thường. Không vẽ chữ. |
| [ ] ui.icon.camera · P1 | ui/camera.png | 16×16 | Biểu tượng đã chụp, không flash trắng. |
| [ ] ui.icon.record · P1 | ui/record.png | 16×16 | Biểu tượng ghi âm; dấu đỏ, không chữ. |

**9-patch:** 66×66 gồm ruột 64×64 + viền trong suốt 1 px; giữ góc 8 px. Bốn khung 34×34 gồm ruột 32×32 + viền 1 px; giữ góc 6 px. Trên/trái: vạch đen 1 px đánh dấu phần giữa được giãn; dưới/phải: vùng chữ. Với khung 66, đoạn giữa tọa độ 9–56; với khung 34 là 7–26 (tọa độ tính từ 0 trên ảnh xuất). Không tô kín viền điều khiển. Khung thoại phải đọc được khi giãn 640×230, nút khi giãn 530×38 hoặc 510×38.

## 6. Âm thanh — giao riêng cho người làm audio

Không yêu cầu họa sĩ đồ họa thực hiện phần này. Tất cả thông số thu/xuất dưới đây là PROPOSED. Nền OGG stereo 44,1 kHz, loop 20–40 giây nối không tiếng click; thoại/hiệu ứng WAV PCM16 mono 44,1 kHz. Peak tối đa −3 dBFS, không clipping; không bake reverb nặng hoặc nhạc vào giọng. Mỗi lời băng là file riêng, không thu một track chạy liền cả cảnh. Clip hiệu ứng ngắn theo hành động; độ dài cuối cần nghe thử cùng cảnh. Không thu toàn bộ hội thoại thường khi chưa có quyết định lồng tiếng đầy đủ.

| Kiểm / ID / ưu tiên | File | Loại / định dạng | Nội dung | Loop |
| --- | --- | --- | --- | --- |
| [ ] audio.school.birds · P0 | audio/ambience/school_birds.ogg | Nền / OGG | Bản mix chim + tiếng trường xa không rõ lời, không tiếng gọi tên; dùng một loop tổng. | Có |
| [ ] audio.wind · P0 | audio/ambience/wind.ogg | Nền / OGG | Gió nhỏ của hiện tượng, không nhạc giật mình. | Có |
| [ ] audio.fx.message · P1 | audio/sfx/message.wav | Hiệu ứng / WAV | Tin nhắn; dùng lại đầu game và C. | Không |
| [ ] audio.fx.door_open · P1 | audio/sfx/door_open.wav | Hiệu ứng / WAV | Bản lề cửa mở nhỏ. | Không |
| [ ] audio.fx.door_close · P1 | audio/sfx/door_close.wav | Hiệu ứng / WAV | Cửa khép trên băng, không suy Nga ra ngoài. | Không |
| [ ] audio.fx.footstep · P1 | audio/sfx/footstep.wav | Hiệu ứng / WAV | Một bước chân thường, không tạo bước chân thứ ba trước hiện tượng. | Không |
| [ ] audio.fx.page · P1 | audio/sfx/page.wav | Hiệu ứng / WAV | Lật giấy/sổ; không âm chạy chữ. | Không |
| [ ] audio.fx.camera · P1 | audio/sfx/camera.wav | Hiệu ứng / WAV | Chụp nhỏ. | Không |
| [ ] audio.fx.bell_strange · P1 | audio/sfx/bell_strange.wav | Hiệu ứng / WAV | Chuông trường sai giờ, một lần C1. | Không |
| [ ] audio.fx.bell_normal · P1 | audio/sfx/bell_normal.wav | Hiệu ứng / WAV | Chuông bình thường của A. | Không |
| [ ] audio.fx.chair · P1 | audio/sfx/chair.wav | Hiệu ứng / WAV | Ghế bàn cuối kéo; ngắn, không lặp vô hạn. | Không |
| [ ] audio.fx.knock_three · P1 | audio/sfx/knock_three.wav | Hiệu ứng / WAV | Ba tiếng gõ liên tiếp từ cuối hành lang. | Không |
| [ ] audio.fx.knock_one · P1 | audio/sfx/knock_one.wav | Hiệu ứng / WAV | Một tiếng trong tủ khóa. | Không |
| [ ] audio.fx.lock · P1 | audio/sfx/lock.wav | Hiệu ứng / WAV | Ổ khóa khép lại. | Không |
| [ ] audio.fx.unlock · P1 | audio/sfx/unlock.wav | Hiệu ứng / WAV | Mở khóa ngăn dưới. | Không |
| [ ] audio.fx.key · P1 | audio/sfx/key.wav | Hiệu ứng / WAV | Chìa chạm tay/móc; bản ngắn. | Không |
| [ ] audio.fx.key_rattle · P1 | audio/sfx/key_rattle.wav | Hiệu ứng / WAV | Chìa rung nhẹ, không jump scare. | Không |
| [ ] audio.fx.gate · P1 | audio/sfx/gate.wav | Hiệu ứng / WAV | Cổng mở về bên ngoài. | Không |
| [ ] audio.fx.chalk · P1 | audio/sfx/chalk.wav | Hiệu ứng / WAV | Phấn kẻ một vạch nhỏ. | Không |
| [ ] audio.fx.button · P1 | audio/sfx/button.wav | Hiệu ứng / WAV | Nút cassette cơ/stop. | Không |
| [ ] audio.fx.lid · P1 | audio/sfx/lid.wav | Hiệu ứng / WAV | Nắp khay pin khép. | Không |
| [ ] audio.fx.paper · P1 | audio/sfx/paper.wav | Hiệu ứng / WAV | Xếp giấy/trao túi pin. | Không |
| [ ] audio.fx.tape_hiss · P1 | audio/sfx/tape_hiss.wav | Hiệu ứng / WAV | Rè ngắn lúc bắt đầu ghi. | Không |
| [ ] audio.fx.tape_stop · P1 | audio/sfx/tape_stop.wav | Hiệu ứng / WAV | Nhiễu băng rồi dừng; không tiếng tử vong. | Không |
| [ ] audio.fx.alarm · P1 | audio/sfx/alarm.wav | Hiệu ứng / WAV | Còi chỉ có trên băng năm 2015. | Không |
| [ ] audio.fx.run · P1 | audio/sfx/run.wav | Hiệu ứng / WAV | Tiếng người chạy xa trên băng, vô danh. | Không |
| [ ] audio.fx.write · P1 | audio/sfx/write.wav | Hiệu ứng / WAV | Bút viết/ký trên tờ mới. | Không |
| [ ] audio.voice.call · P1 | audio/voice/call.wav | Giọng / WAV | Giọng điểm danh: “Nguyễn Thu Nga.” | Không |
| [ ] audio.voice.hung_assignment · P1 | audio/tape/hung_assignment.wav | Giọng / WAV | Hùng trên băng: “Nga ở lại cất giúp thầy bộ tư liệu nhé. Xong rồi em xuống.” | Không |
| [ ] audio.voice.nga_recorder · P1 | audio/tape/nga_recorder.wav | Giọng / WAV | Nga trên băng: “Dạ. Em cất máy ghi âm luôn phải không thầy?” | Không |
| [ ] audio.voice.hung_finish · P1 | audio/tape/hung_finish.wav | Giọng / WAV | Hùng trên băng: “Ừ. Để băng chạy hết rồi cất giúp thầy.” | Không |
| [ ] audio.voice.nga_help_1 · P1 | audio/tape/nga_help_1.wav | Giọng / WAV | Nga trên băng: “Có ai ngoài đó không?” | Không |
| [ ] audio.voice.nga_help_2 · P1 | audio/tape/nga_help_2.wav | Giọng / WAV | Nga trên băng: “Em vẫn ở trong lớp! Có ai nghe không?” | Không |
| [ ] audio.voice.nga_stayed · P1 | audio/voice/nga_stayed.wav | Giọng / WAV | Nga: “Thầy bảo em ở lại.” | Không |
| [ ] audio.voice.nga_present · P1 | audio/voice/nga_present.wav | Giọng / WAV | Nga ở A: “Có.” | Không |
| [ ] audio.voice.linh_present · P1 | audio/voice/linh_present.wav | Giọng / WAV | Linh ở C: “Có.” | Không |
| [ ] audio.voice.enough · P1 | audio/voice/enough.wav | Giọng / WAV | Giọng điểm danh ở C: “Đủ rồi.” | Không |

## 7. Kiểm tra bàn giao

- [ ] P0: xong các dòng P0 ở trên, ưu tiên Linh + phòng 304 + ảnh lớp/sổ trước.
- [ ] P1: xong pose/cue/UI/audio và bộ C đã giao.
- [ ] P2: chỉ làm các dòng P2 đã được chọn sản xuất.
- [ ] Giữ đúng 33 người trong ảnh, bốn pin AA, một băng gốc; không vẽ tên Linh lên ảnh/sổ chụp cố định.
- [ ] Frame/pivot/alpha không nhảy; các trạng thái giữ cùng canvas.
- [ ] Nộp nguồn vẽ/thu và thông tin quyền sử dụng cùng đợt bàn giao; không lấy asset demo làm final.

| Đã có / ID | File | Xác minh |
| --- | --- | --- |
| [x] font.ui | fonts/NotoSans-Regular.ttf | TTF hợp lệ, kiểm tra cmap có chữ Việt mẫu; không cần vẽ lại. |
| [x] font.ui.license | fonts/OFL.txt | SIL OFL 1.1 đã đọc, giữ nguyên. |

## Xuất file

Giữ đúng canvas/frame; xếp frame một hàng từ trái sang phải, không khoảng cách, không trim. Xuất PNG 1×, giữ transparency; không tự scale, không đổi tên. Đặt đúng đường dẫn dưới **game/assets/** như từng bảng. Giữ bản nguồn vẽ riêng và báo rõ những dòng chưa làm.
