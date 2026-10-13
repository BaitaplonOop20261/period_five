# Danh mục asset chuẩn — Room304

Ngày kiểm kê: 11/10/2026. Đây là nguồn chuẩn của ID, đường dẫn và quy cách cho bộ ASSET_SPEC. Mọi path trong bảng tính từ `game/assets/`. ID mới là ID sản xuất đề xuất, **chưa được thêm vào manifest**. ID/path đã tồn tại được giữ nguyên. Không đổi tên theo tên hiển thị tiếng Việt.

| Nhóm | Tổng file | Cần làm | P0 / P1 / P2 |
| --- | --- | --- | --- |
| Nhân vật | 53 | 53 | 32 / 5 / 16 |
| Portrait | 12 | 12 | 6 / 0 / 6 |
| Môi trường | 1 | 1 | 1 / 0 / 0 |
| Bản đồ kỹ thuật | 5 | 5 | 5 / 0 / 0 |
| Đồ vật | 25 | 25 | 23 / 2 / 0 |
| Hiệu ứng | 1 | 1 | 1 / 0 / 0 |
| Tài liệu | 12 | 12 | 11 / 1 / 0 |
| UI | 7 | 7 | 0 / 7 / 0 |
| UI kỹ thuật | 3 | 3 | 0 / 3 / 0 |
| Minh họa tùy chọn | 4 | 4 | 0 / 0 / 4 |
| Âm thanh | 37 | 37 | 2 / 35 / 0 |
| Font có sẵn | 2 | 0 | 2 / 0 / 0 |

Tổng **162 file**: 152 đầu ra sản xuất của artist/âm thanh (115 PNG + 37 audio), 8 đầu ra kỹ thuật (4 TMX + 1 TSX + 3 Skin/atlas), 2 file font/giấy phép có sẵn. 160 file chưa tồn tại; trong đó có hạng mục P2 tùy chọn, không phải 160 hình phải vẽ. 53 sheet nhân vật gồm **204 frame**, tính cả 4 sheet một frame; 49 sheet có nhiều frame. Không tính portrait, prop, tile và trang UI vào tổng frame animation.

P0 = bộ lõi cần ưu tiên; P1 = hoàn thiện trình bày/cue, gồm phần C; P2 = nâng cấp tùy chọn (NPC đi trong chuyển cảnh, biểu cảm phụ, CG riêng). “Optional” trong runtime là khả năng chạy fallback, không phải được bỏ yêu cầu kể chuyện. C đã có kịch bản, hiện `enableC=false`; vẫn kiểm kê tài nguyên C.

CONFIRMED = có căn cứ đang tồn tại; PROPOSED = quy cách sản xuất cần duyệt, chưa áp dụng; UNDECIDED = phần thiết kế chưa có căn cứ chốt. Các canvas 32×48, 640×480 và rectangle prop có căn cứ runtime; việc giữ chúng làm kích thước art cuối cùng vẫn là PROPOSED. MISSING/PRESENT nói về file, không nói gameplay hoàn thành.

## Nhân vật

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| char.linh.idle.down | sprites/linh/idle_down.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Linh đứng nhìn xuống. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.linh.walk.down | sprites/linh/walk_down.png | P0 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Linh đi hướng xuống. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.linh.idle.up | sprites/linh/idle_up.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Linh đứng nhìn lên. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.linh.walk.up | sprites/linh/walk_up.png | P0 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Linh đi hướng lên. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.linh.idle.left | sprites/linh/idle_left.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Linh đứng nhìn trái. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.linh.walk.left | sprites/linh/walk_left.png | P0 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Linh đi hướng trái. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.linh.idle.right | sprites/linh/idle_right.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Linh đứng nhìn phải. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.linh.walk.right | sprites/linh/walk_right.png | P0 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Linh đi hướng phải. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.trang.idle.down | sprites/trang/idle_down.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Trang đứng nhìn xuống. | [ ] MISSING | §7.1 CHAR-TRANG; CHAR |
| char.trang.walk.down | sprites/trang/walk_down.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Trang đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-TRANG; CHAR |
| char.trang.idle.up | sprites/trang/idle_up.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Trang đứng nhìn lên. | [ ] MISSING | §7.1 CHAR-TRANG; CHAR |
| char.trang.walk.up | sprites/trang/walk_up.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Trang đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-TRANG; CHAR |
| char.trang.idle.left | sprites/trang/idle_left.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Trang đứng nhìn trái. | [ ] MISSING | §7.1 CHAR-TRANG; CHAR |
| char.trang.walk.left | sprites/trang/walk_left.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Trang đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-TRANG; CHAR |
| char.trang.idle.right | sprites/trang/idle_right.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Trang đứng nhìn phải. | [ ] MISSING | §7.1 CHAR-TRANG; CHAR |
| char.trang.walk.right | sprites/trang/walk_right.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Trang đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-TRANG; CHAR |
| char.duc.idle.down | sprites/duc/idle_down.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Đức đứng nhìn xuống. | [ ] MISSING | §7.1 CHAR-DUC; CHAR |
| char.duc.walk.down | sprites/duc/walk_down.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Đức đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-DUC; CHAR |
| char.duc.idle.up | sprites/duc/idle_up.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Đức đứng nhìn lên. | [ ] MISSING | §7.1 CHAR-DUC; CHAR |
| char.duc.walk.up | sprites/duc/walk_up.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Đức đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-DUC; CHAR |
| char.duc.idle.left | sprites/duc/idle_left.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Đức đứng nhìn trái. | [ ] MISSING | §7.1 CHAR-DUC; CHAR |
| char.duc.walk.left | sprites/duc/walk_left.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Đức đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-DUC; CHAR |
| char.duc.idle.right | sprites/duc/idle_right.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Đức đứng nhìn phải. | [ ] MISSING | §7.1 CHAR-DUC; CHAR |
| char.duc.walk.right | sprites/duc/walk_right.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Đức đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-DUC; CHAR |
| char.nam.idle.down | sprites/nam/idle_down.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Bà Năm đứng nhìn xuống. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.nam.walk.down | sprites/nam/walk_down.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Bà Năm đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.nam.idle.up | sprites/nam/idle_up.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Bà Năm đứng nhìn lên. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.nam.walk.up | sprites/nam/walk_up.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Bà Năm đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.nam.idle.left | sprites/nam/idle_left.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Bà Năm đứng nhìn trái. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.nam.walk.left | sprites/nam/walk_left.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Bà Năm đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.nam.idle.right | sprites/nam/idle_right.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Bà Năm đứng nhìn phải. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.nam.walk.right | sprites/nam/walk_right.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Bà Năm đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.hung.idle.down | sprites/hung/idle_down.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Thầy Hùng đứng nhìn xuống. | [ ] MISSING | §7.1 CHAR-HUNG; CHAR |
| char.hung.walk.down | sprites/hung/walk_down.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Thầy Hùng đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-HUNG; CHAR |
| char.hung.idle.up | sprites/hung/idle_up.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Thầy Hùng đứng nhìn lên. | [ ] MISSING | §7.1 CHAR-HUNG; CHAR |
| char.hung.walk.up | sprites/hung/walk_up.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Thầy Hùng đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-HUNG; CHAR |
| char.hung.idle.left | sprites/hung/idle_left.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Thầy Hùng đứng nhìn trái. | [ ] MISSING | §7.1 CHAR-HUNG; CHAR |
| char.hung.walk.left | sprites/hung/walk_left.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Thầy Hùng đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-HUNG; CHAR |
| char.hung.idle.right | sprites/hung/idle_right.png | P0 | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp — PROPOSED | Thầy Hùng đứng nhìn phải. | [ ] MISSING | §7.1 CHAR-HUNG; CHAR |
| char.hung.walk.right | sprites/hung/walk_right.png | P2 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | Thầy Hùng đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. | [ ] MISSING | §7.1 CHAR-HUNG; CHAR |
| char.linh.pose.phone | sprites/linh/pose_phone.png | P0 | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối — PROPOSED | Giơ điện thoại chụp/ghi âm, hướng xuống chếch phải. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.linh.pose.book | sprites/linh/pose_book.png | P0 | 32×48/frame; 1 frame; 1 FPS; 32×48; một lần, giữ frame cuối — PROPOSED | Giữ sổ trên tay; ảnh tĩnh. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.linh.pose.seated | sprites/linh/pose_seated.png | P1 | 32×48/frame; 4 frame; 4 FPS; 128×48; một lần, giữ frame cuối — PROPOSED | C: ngồi bàn cuối, môi mấp máy không tiếng; chuỗi một lần. | [ ] MISSING | §7.1 CHAR-LINH; CHAR |
| char.trang.pose.phone | sprites/trang/pose_phone.png | P1 | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối — PROPOSED | C: giơ điện thoại đọc tin Linh. | [ ] MISSING | §7.1 CHAR-TRANG; CHAR |
| char.duc.pose.photo | sprites/duc/pose_photo.png | P1 | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối — PROPOSED | Trao ảnh gia đình, không cần ảnh đứng riêng. | [ ] MISSING | §7.1 CHAR-DUC; CHAR |
| char.nam.pose.key | sprites/nam/pose_key.png | P1 | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối — PROPOSED | Trao/nhận chìa ngăn dưới. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.nam.pose.gate | sprites/nam/pose_gate.png | P1 | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối — PROPOSED | Đưa tay mở cổng, giữ silhouette rõ. | [ ] MISSING | §7.1 CHAR-NAM; CHAR |
| char.hung.pose.write | sprites/hung/pose_write.png | P0 | 32×48/frame; 6 frame; 8 FPS; 192×48; một lần, giữ frame cuối — PROPOSED | Cúi viết/ký trên tờ mới, không sửa sổ. | [ ] MISSING | §7.1 CHAR-HUNG; CHAR |
| char.nga.idle.down | sprites/nga/idle_down.png | P0 | 32×48/frame; 1 frame; 1 FPS; 32×48; lặp — PROPOSED | Đứng nhìn xuống; không thương tích, không hình quái vật. | [ ] MISSING | §7.1 CHAR-NGA; CHAR |
| char.nga.idle.up | sprites/nga/idle_up.png | P0 | 32×48/frame; 1 frame; 1 FPS; 32×48; lặp — PROPOSED | Bóng nhìn lên, dùng sau kính cửa. | [ ] MISSING | §7.1 CHAR-NGA; CHAR |
| char.nga.walk.down | sprites/nga/walk_down.png | P0 | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp — PROPOSED | A: đi ra cửa. | [ ] MISSING | §7.1 CHAR-NGA; CHAR |
| char.nga.pose.seated | sprites/nga/pose_seated.png | P0 | 32×48/frame; 1 frame; 1 FPS; 32×48; một lần, giữ frame cuối — PROPOSED | Ngồi; dùng cùng hình cho bóng tường và bàn cuối. | [ ] MISSING | §7.1 CHAR-NGA; CHAR |
| char.nga.pose.point | sprites/nga/pose_point.png | P0 | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối — PROPOSED | Giơ tay chỉ thùng bên phải; không chỉ lời giải khác. | [ ] MISSING | §7.1 CHAR-NGA; CHAR |

## Portrait

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| portrait.linh.normal | portraits/linh_normal.png | P0 | 150×175 PNG RGBA — PROPOSED | Linh trung tính; cùng góc mặt, cùng đường vai. | [ ] MISSING | §7.1 CHAR-LINH / UI-DIALOG; PORTRAIT |
| portrait.trang.normal | portraits/trang_normal.png | P0 | 150×175 PNG RGBA — PROPOSED | Trang trung tính; cùng góc mặt, cùng đường vai. | [ ] MISSING | §7.1 CHAR-TRANG / UI-DIALOG; PORTRAIT |
| portrait.duc.normal | portraits/duc_normal.png | P0 | 150×175 PNG RGBA — PROPOSED | Đức trung tính; cùng góc mặt, cùng đường vai. | [ ] MISSING | §7.1 CHAR-DUC / UI-DIALOG; PORTRAIT |
| portrait.nam.normal | portraits/nam_normal.png | P0 | 150×175 PNG RGBA — PROPOSED | Bà Năm trung tính; cùng góc mặt, cùng đường vai. | [ ] MISSING | §7.1 CHAR-NAM / UI-DIALOG; PORTRAIT |
| portrait.hung.normal | portraits/hung_normal.png | P0 | 150×175 PNG RGBA — PROPOSED | Thầy Hùng trung tính; cùng góc mặt, cùng đường vai. | [ ] MISSING | §7.1 CHAR-HUNG / UI-DIALOG; PORTRAIT |
| portrait.nga.normal | portraits/nga_normal.png | P0 | 150×175 PNG RGBA — PROPOSED | Nga trung tính; cùng góc mặt, cùng đường vai. | [ ] MISSING | §7.1 CHAR-NGA / UI-DIALOG; PORTRAIT |
| portrait.linh.worried | portraits/linh_worried.png | P2 | 150×175 PNG RGBA — PROPOSED | Linh lo lắng; biến thể gương mặt đề xuất. | [ ] MISSING | §7.1 CHAR-LINH / UI-DIALOG; PORTRAIT |
| portrait.trang.worried | portraits/trang_worried.png | P2 | 150×175 PNG RGBA — PROPOSED | Trang lo lắng; biến thể gương mặt đề xuất. | [ ] MISSING | §7.1 CHAR-TRANG / UI-DIALOG; PORTRAIT |
| portrait.duc.grief | portraits/duc_grief.png | P2 | 150×175 PNG RGBA — PROPOSED | Đức đau buồn, không ngầm tha thứ; biến thể gương mặt đề xuất. | [ ] MISSING | §7.1 CHAR-DUC / UI-DIALOG; PORTRAIT |
| portrait.nam.grave | portraits/nam_grave.png | P2 | 150×175 PNG RGBA — PROPOSED | Bà Năm nghiêm nghị; biến thể gương mặt đề xuất. | [ ] MISSING | §7.1 CHAR-NAM / UI-DIALOG; PORTRAIT |
| portrait.hung.remorse | portraits/hung_remorse.png | P2 | 150×175 PNG RGBA — PROPOSED | Thầy Hùng nhận trách nhiệm; biến thể gương mặt đề xuất. | [ ] MISSING | §7.1 CHAR-HUNG / UI-DIALOG; PORTRAIT |
| portrait.nga.quiet | portraits/nga_quiet.png | P2 | 150×175 PNG RGBA — PROPOSED | Nga bình lặng, không nụ cười tha thứ; biến thể gương mặt đề xuất. | [ ] MISSING | §7.1 CHAR-NGA / UI-DIALOG; PORTRAIT |

## Môi trường

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| tileset.school | maps/tilesets/school.png | P0 | 512×256 PNG RGBA; tile 32×32; 16×8 ô — PROPOSED | Bộ chung cho bốn phòng; chỉ vẽ các ô được giao, ô dư trong suốt. | [ ] MISSING | SDD §5; flow §2 MAP-304/HALL/GUARD/HUNG; TILESET |

## Bản đồ kỹ thuật

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| tileset.school.definition | maps/tilesets/school.tsx | P0 | TSX; tile 32×32; 128 ô; margin/spacing 0 — PROPOSED | File lắp tileset; người dựng bản đồ thực hiện. | [ ] MISSING | SDD §5; flow §2 MAP-304/HALL/GUARD/HUNG; TMX |
| map.tmx.room_304 | maps/room_304.tmx | P0 | TMX finite orthogonal; 20×15 ô; 640×480 px — PROPOSED | Dựng Phòng 304 từ school.tsx; không vẽ lại PNG toàn cảnh. | [ ] MISSING | SDD §5; flow §2 MAP-304/HALL/GUARD/HUNG; TMX |
| map.tmx.hallway | maps/hallway.tmx | P0 | TMX finite orthogonal; 20×15 ô; 640×480 px — PROPOSED | Dựng Hành lang tầng ba từ school.tsx; không vẽ lại PNG toàn cảnh. | [ ] MISSING | SDD §5; flow §2 MAP-304/HALL/GUARD/HUNG; TMX |
| map.tmx.guard_room | maps/guard_room.tmx | P0 | TMX finite orthogonal; 20×15 ô; 640×480 px — PROPOSED | Dựng Phòng bảo vệ từ school.tsx; không vẽ lại PNG toàn cảnh. | [ ] MISSING | SDD §5; flow §2 MAP-304/HALL/GUARD/HUNG; TMX |
| map.tmx.classroom | maps/classroom.tmx | P0 | TMX finite orthogonal; 20×15 ô; 640×480 px — PROPOSED | Dựng Lớp học bên cạnh từ school.tsx; không vẽ lại PNG toàn cảnh. | [ ] MISSING | SDD §5; flow §2 MAP-304/HALL/GUARD/HUNG; TMX |

## Đồ vật

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| prop.photo | sprites/props/photo.png | P0 | 54×34 PNG RGBA; 1 ảnh — PROPOSED | Ảnh treo 304; thu nhỏ bản document.photo. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.register | sprites/props/register.png | P0 | 80×34 PNG RGBA; 1 ảnh — PROPOSED | Bàn giáo viên + sổ, trạng thái ban đầu. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.desk.empty | sprites/props/desk_empty.png | P0 | 80×34 PNG RGBA; 1 ảnh — PROPOSED | Cùng bàn, đã lấy sổ; giữ nền bàn đồng nhất. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.desk.final | sprites/props/desk_final.png | P0 | 80×34 PNG RGBA; 1 ảnh — PROPOSED | Cùng bàn + sổ, băng và giấy ở buổi điểm danh cuối. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.player | sprites/props/player.png | P0 | 54×28 PNG RGBA; 1 ảnh — PROPOSED | Máy cassette thiếu nguồn, đèn tắt. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.player.powered | sprites/props/player_powered.png | P0 | 54×28 PNG RGBA; 1 ảnh — PROPOSED | Cùng máy, đèn nguồn sáng. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.player.playing | sprites/props/player_playing.png | P0 | 54×28 PNG RGBA; 1 ảnh — PROPOSED | Cùng máy, có băng đang chạy; ảnh trạng thái tĩnh. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.player.open | sprites/props/player_open.png | P1 | 54×28 PNG RGBA; 1 ảnh — PROPOSED | Mở khay pin trống, bốn vị trí AA; chi tiết lớn xem document.battery. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.crate | sprites/props/crate.png | P0 | 54×28 PNG RGBA; 1 ảnh — PROPOSED | Thùng thiết bị còn buộc dây, có nhãn. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.crate.open | sprites/props/crate_open.png | P0 | 54×28 PNG RGBA; 1 ảnh — PROPOSED | Thùng mở, đã lấy một băng, phiếu kiểm kê còn bên trong. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.cabinet | sprites/props/cabinet.png | P0 | 46×22 PNG RGBA; 1 ảnh — PROPOSED | Ngăn dưới đóng, chưa khóa. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.cabinet.locked | sprites/props/cabinet_locked.png | P0 | 46×22 PNG RGBA; 1 ảnh — PROPOSED | Ngăn dưới đóng khóa; cùng vị trí ổ khóa. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.cabinet.open | sprites/props/cabinet_open.png | P1 | 46×22 PNG RGBA; 1 ảnh — PROPOSED | Ngăn dưới mở, chỗ đặt sổ nằm phẳng. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.cabinet.upper | sprites/props/cabinet_upper.png | P0 | 46×20 PNG RGBA; 1 ảnh — PROPOSED | Ngăn trên; cùng chất liệu tủ, không dùng cất sổ. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.papers | sprites/props/papers.png | P0 | 70×32 PNG RGBA; 1 ảnh — PROPOSED | Hai giấy gốc ở bàn bảo vệ. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.lastdesk | sprites/props/lastdesk.png | P0 | 58×20 PNG RGBA; 1 ảnh — PROPOSED | Bàn cuối, ghế để riêng. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.window | sprites/props/window.png | P0 | 74×16 PNG RGBA; 1 ảnh — PROPOSED | Cửa sổ, sân chỉ thấy qua đây; không vẽ bản đồ sân. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.board | sprites/props/board.png | P0 | 90×24 PNG RGBA; 1 ảnh — PROPOSED | Bảng tin, giấy sửa chữa và nội san cùng vị trí. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.guard.wall | sprites/props/guard_wall.png | P0 | 46×50 PNG RGBA; 1 ảnh — PROPOSED | Mảng tường trước khi cửa hiện. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.guard.door | sprites/props/guard_door.png | P0 | 46×50 PNG RGBA; 1 ảnh — PROPOSED | Cửa bảo vệ thay đúng mảng tường; tay nắm rõ. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.gate.closed | sprites/props/gate_closed.png | P0 | 70×24 PNG RGBA; 1 ảnh — PROPOSED | Cổng đóng; nhìn rõ ngưỡng trong/ngoài. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.gate.open | sprites/props/gate_open.png | P0 | 70×24 PNG RGBA; 1 ảnh — PROPOSED | Cùng cổng mở, không che đường qua. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.evidence | sprites/props/evidence.png | P0 | 74×30 PNG RGBA; 1 ảnh — PROPOSED | Bàn đối chiếu trước khi ký; không hiện giấy đã ký sớm. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.evidence.signed | sprites/props/evidence_signed.png | P0 | 74×30 PNG RGBA; 1 ảnh — PROPOSED | Cùng bàn có tờ bổ sung, ký Hùng, không sửa trang cũ. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |
| prop.chair | sprites/props/chair.png | P0 | 22×9 PNG RGBA; 1 ảnh — PROPOSED | Ghế thật ở bàn cuối, tách khỏi nền để dịch chuyển. | [ ] MISSING | flow §2/§7.1 PROP-*; PROP |

## Hiệu ứng

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| effect.memory | effects/memory.png | P0 | 24×40 PNG RGBA; 1 ảnh — PROPOSED | Bóng học sinh vô danh, không thương tích; tái dùng vài bóng mờ, không phải 32 nạn nhân. | [ ] MISSING | flow A-01; FX-MEMORY; MEMORY |

## Tài liệu

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| document.msg | documents/msg.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Khung điện thoại nhận tin thầy Hùng; không viết sẵn nội dung tin. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.photo | documents/photo.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Ảnh lớp 12A3: đúng 33 học sinh đếm được, mép chú thích bong; không thêm tên. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.register | documents/register.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Trang sổ 11/09/2015, hai lớp mực có mặt/vắng ở dòng Nga; không vẽ tên Linh cố định. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.news | documents/news.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Mẩu nội san học sinh, giấy cũ; chữ cảnh báo do game hiện. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.gate | documents/gate.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Bản ghi bà Năm: 32 người ngoài sân, không phải danh sách 32 tên. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.report | documents/report.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Báo cáo sơ tán sai; không vẽ cảnh cháy hoặc nạn nhân. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.inventory | documents/inventory.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Phiếu thu hồi 01 băng từ máy ghi âm tại 304. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.signed | documents/signed.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Tờ mới năm 2025; chữ ký Hùng, không tự đặt ngày/tháng. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.family | documents/family.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Nga và Đức trước 2015; Đức là em nhỏ (7 tuổi lúc Nga mất). | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.tape | documents/tape.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Cận băng gốc duy nhất; nhãn Tập phát thanh — 12A3 — 11/09/2015. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.battery | documents/battery.png | P0 | 640×480 PNG; 1 ảnh — PROPOSED | Cận khay và bộ bốn pin AA 1,5 V, dấu +/− rõ, không thêm kiểu pin. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; DOCUMENT |
| document.c_phone | documents/trang_phone.png | P1 | 180×245 PNG RGBA; 1 ảnh — PROPOSED | C: điện thoại Trang nhận tin Linh; nền và bong bóng trống, chữ do game hiện. | [ ] MISSING | flow §5 DOC-*; §8 C-PHONE; PHONE |

## UI

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| ui.art.dialogue_frame | ui/source/dialogue_frame.9.png | P1 | 66×66 PNG RGBA, gồm viền 9-patch 1 px — PROPOSED | Khung thoại; ruột 64×64, góc giữ 8 px, phần giữa giãn. Không vẽ chữ. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; SKIN_SOURCE |
| ui.art.panel_frame | ui/source/panel_frame.9.png | P1 | 34×34 PNG RGBA, gồm viền 9-patch 1 px — PROPOSED | Khung menu/pause/ghi chú/HUD dùng chung; ruột 32×32, góc giữ 6 px. Không vẽ chữ. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; SKIN_SOURCE |
| ui.art.button_normal | ui/source/button_normal.9.png | P1 | 34×34 PNG RGBA, gồm viền 9-patch 1 px — PROPOSED | Nút thường; ruột 32×32, góc giữ 6 px. Không vẽ chữ. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; SKIN_SOURCE |
| ui.art.button_focus | ui/source/button_focus.9.png | P1 | 34×34 PNG RGBA, gồm viền 9-patch 1 px — PROPOSED | Nút được chọn/rê chuột; cùng khung nút thường. Không vẽ chữ. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; SKIN_SOURCE |
| ui.art.button_pressed | ui/source/button_pressed.9.png | P1 | 34×34 PNG RGBA, gồm viền 9-patch 1 px — PROPOSED | Nút đang nhấn; cùng khung nút thường. Không vẽ chữ. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; SKIN_SOURCE |
| ui.icon.camera | ui/camera.png | P1 | 16×16 PNG RGBA; 1 ảnh — PROPOSED | Biểu tượng đã chụp, không flash trắng. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; ICON |
| ui.icon.record | ui/record.png | P1 | 16×16 PNG RGBA; 1 ảnh — PROPOSED | Biểu tượng ghi âm; dấu đỏ, không chữ. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; ICON |

## UI kỹ thuật

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| ui.skin | ui/room304-skin.json | P1 | Skin JSON; default-font do game cấp — PROPOSED | AI tích hợp tạo từ 5 ảnh khung; không giao artist viết. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; SKIN |
| ui.skin.atlas | ui/room304-skin.atlas | P1 | TextureAtlas; split/pad từ 9-patch — PROPOSED | File pack sinh ra, cùng basename Skin. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; SKIN |
| ui.skin.page | ui/room304-skin.png | P1 | 256×128 PNG RGBA; Nearest; pack không rotate — PROPOSED | Trang atlas dự kiến đủ 5 khung; AI tạo khi pack, không vẽ thêm. | [ ] MISSING | flow UI-DIALOG/CHOICE/HINT/RECORD/PAUSE; SKIN |

## Minh họa tùy chọn

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| background.menu | ui/menu.png | P2 | 640×480 PNG; 1 ảnh — PROPOSED | Menu: tái bố cục phòng 304, chừa giữa cho chữ; không thêm cảnh. Có thể giữ nền màu, không bắt buộc vẽ CG riêng. | [ ] MISSING | flow UI-TITLE/UI-END (ảnh riêng tùy chọn); MENU |
| background.ending.a | endings/a.png | P2 | 640×480 PNG; 1 ảnh — PROPOSED | A: phòng/cổng yên, chứng cứ còn giữ; không minh họa đã được tha thứ. Có thể giữ nền màu, không bắt buộc vẽ CG riêng. | [ ] MISSING | flow UI-TITLE/UI-END (ảnh riêng tùy chọn); MENU |
| background.ending.b | endings/b.png | P2 | 640×480 PNG; 1 ảnh — PROPOSED | B: phòng 304 trống, tủ khóa, buổi điểm danh chưa dứt. Có thể giữ nền màu, không bắt buộc vẽ CG riêng. | [ ] MISSING | flow UI-TITLE/UI-END (ảnh riêng tùy chọn); MENU |
| background.ending.c | endings/c.png | P2 | 640×480 PNG; 1 ảnh — PROPOSED | C: Linh ngồi bàn cuối nhìn từ phía Trang; không hình tử vong. Có thể giữ nền màu, không bắt buộc vẽ CG riêng. | [ ] MISSING | flow UI-TITLE/UI-END (ảnh riêng tùy chọn); MENU |

## Âm thanh

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| audio.school.birds | audio/ambience/school_birds.ogg | P0 | OGG Vorbis stereo 44,1 kHz; loop 20–40 s — PROPOSED | Bản mix chim + tiếng trường xa không rõ lời, không tiếng gọi tên; dùng một loop tổng. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.wind | audio/ambience/wind.ogg | P0 | OGG Vorbis stereo 44,1 kHz; loop 20–40 s — PROPOSED | Gió nhỏ của hiện tượng, không nhạc giật mình. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.message | audio/sfx/message.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Tin nhắn; dùng lại đầu game và C. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.door_open | audio/sfx/door_open.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Bản lề cửa mở nhỏ. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.door_close | audio/sfx/door_close.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Cửa khép trên băng, không suy Nga ra ngoài. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.footstep | audio/sfx/footstep.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Một bước chân thường, không tạo bước chân thứ ba trước hiện tượng. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.page | audio/sfx/page.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Lật giấy/sổ; không âm chạy chữ. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.camera | audio/sfx/camera.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Chụp nhỏ. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.bell_strange | audio/sfx/bell_strange.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Chuông trường sai giờ, một lần C1. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.bell_normal | audio/sfx/bell_normal.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Chuông bình thường của A. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.chair | audio/sfx/chair.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Ghế bàn cuối kéo; ngắn, không lặp vô hạn. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.knock_three | audio/sfx/knock_three.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Ba tiếng gõ liên tiếp từ cuối hành lang. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.knock_one | audio/sfx/knock_one.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Một tiếng trong tủ khóa. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.lock | audio/sfx/lock.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Ổ khóa khép lại. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.unlock | audio/sfx/unlock.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Mở khóa ngăn dưới. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.key | audio/sfx/key.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Chìa chạm tay/móc; bản ngắn. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.key_rattle | audio/sfx/key_rattle.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Chìa rung nhẹ, không jump scare. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.gate | audio/sfx/gate.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Cổng mở về bên ngoài. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.chalk | audio/sfx/chalk.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Phấn kẻ một vạch nhỏ. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.button | audio/sfx/button.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Nút cassette cơ/stop. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.lid | audio/sfx/lid.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Nắp khay pin khép. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.paper | audio/sfx/paper.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Xếp giấy/trao túi pin. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.tape_hiss | audio/sfx/tape_hiss.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Rè ngắn lúc bắt đầu ghi. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.tape_stop | audio/sfx/tape_stop.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Nhiễu băng rồi dừng; không tiếng tử vong. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.alarm | audio/sfx/alarm.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Còi chỉ có trên băng năm 2015. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.run | audio/sfx/run.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Tiếng người chạy xa trên băng, vô danh. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.fx.write | audio/sfx/write.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Bút viết/ký trên tờ mới. | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.call | audio/voice/call.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Giọng điểm danh: “Nguyễn Thu Nga.” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.hung_assignment | audio/tape/hung_assignment.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Hùng trên băng: “Nga ở lại cất giúp thầy bộ tư liệu nhé. Xong rồi em xuống.” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.nga_recorder | audio/tape/nga_recorder.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Nga trên băng: “Dạ. Em cất máy ghi âm luôn phải không thầy?” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.hung_finish | audio/tape/hung_finish.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Hùng trên băng: “Ừ. Để băng chạy hết rồi cất giúp thầy.” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.nga_help_1 | audio/tape/nga_help_1.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Nga trên băng: “Có ai ngoài đó không?” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.nga_help_2 | audio/tape/nga_help_2.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Nga trên băng: “Em vẫn ở trong lớp! Có ai nghe không?” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.nga_stayed | audio/voice/nga_stayed.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Nga: “Thầy bảo em ở lại.” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.nga_present | audio/voice/nga_present.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Nga ở A: “Có.” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.linh_present | audio/voice/linh_present.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Linh ở C: “Có.” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |
| audio.voice.enough | audio/voice/enough.wav | P1 | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue — PROPOSED | Giọng điểm danh ở C: “Đủ rồi.” | [ ] MISSING | flow §4/§8, cue trong 06; AUDIO |

## Font có sẵn

| ID | File (dưới game/assets/) | Ưu tiên | Quy cách | Nội dung | Trạng thái | Căn cứ / tuyến tích hợp |
| --- | --- | --- | --- | --- | --- | --- |
| font.ui | fonts/NotoSans-Regular.ttf | P0 | TTF; font UI 18 px hiện tại — CONFIRMED | Noto Sans, có giấy phép SIL OFL; không cần artist vẽ font. | [x] PRESENT | runtime fonts/; SIL OFL; FONT |
| font.ui.license | fonts/OFL.txt | P0 | UTF-8; SIL OFL 1.1 — CONFIRMED | Giữ kèm font khi phân phối. | [x] PRESENT | runtime fonts/; SIL OFL; FONT |

## Các slot có sẵn nhưng không giao vẽ trong phương án chính

| ID hiện tại | Path | Xử lý |
| --- | --- | --- |
| audio.school.voices | audio/ambience/school_distant_voices.ogg | UNDECIDED / ambience riêng tiếng người; phương án chính đã mix vào audio.school.birds, không thu lại. |
| map.story.room_304 | maps/room_304.png | UNDECIDED / phương án PNG thay TMX; không vẽ trùng cảnh tileset. Nếu đổi hướng sản xuất, dùng 640×480 PROPOSED và sửa danh mục trước. |
| map.story.hallway | maps/hallway.png | UNDECIDED / phương án PNG thay TMX; không vẽ trùng cảnh tileset. Nếu đổi hướng sản xuất, dùng 640×480 PROPOSED và sửa danh mục trước. |
| map.story.guard_room | maps/guard_room.png | UNDECIDED / phương án PNG thay TMX; không vẽ trùng cảnh tileset. Nếu đổi hướng sản xuất, dùng 640×480 PROPOSED và sửa danh mục trước. |
| map.story.classroom | maps/classroom.png | UNDECIDED / phương án PNG thay TMX; không vẽ trùng cảnh tileset. Nếu đổi hướng sản xuất, dùng 640×480 PROPOSED và sửa danh mục trước. |

Các file cấu hình truyện và bộ demo không phải tài nguyên sản xuất: `story.content`, `story.world`, `story.ui.layout`, `story.audio`, `story.visuals`; ảnh bedroom/hall/back_yard, bed/wardrobe, sprites player_* 32×32, npc_trang_idle, interaction_mark, student_placeholder. Giữ nguyên cho regression; không đánh dấu chúng là art cuối.

Tên/ngoại hình chi tiết của sáu nhân vật, đồng phục, bảng màu chính xác, diễn viên và quyền sử dụng nguồn audio: **UNDECIDED**. Không tạo thêm nhân vật/32 nạn nhân, sân riêng, bản đồ cháy hoặc cảnh giải cứu. Duyệt silhouette của frame đầu trước khi làm cả bộ.
