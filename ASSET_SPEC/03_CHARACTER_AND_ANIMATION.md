# Nhân vật và hoạt ảnh

Danh mục dưới đây sinh cùng dữ liệu với master; mọi số frame/FPS là PROPOSED. Frame32×48/scale1 có căn cứ profile hiện tại, chưa có sprite final. Tất cả 53 file còn thiếu. Tổng204 frame; 49 chuỗi nhiều frame +4 pose/idle tĩnh. PNG Nearest, một hàng, không padding/trim. Đường dẫn dưới game/assets/.

**Ngoại lệ nhập thử 13/10/2026:** câu mô tả/checklist trên là snapshot sản xuất 11/10/2026. Năm file `sprites/{linh,duc,trang,nam,hung}/idle_down.png` hiện đã có để test, nhưng chưa đạt quy cách art sản xuất đề xuất. Linh/Đức/Trang dùng sheet 192×32, 6 frame 32×32, duration 0,15 s/frame (khoảng 6,67 FPS, loop 0,9 s); bà Năm/thầy Hùng dùng PNG 32×32 một frame, duration 1 s/frame và hiển thị tĩnh. Không tạo thêm hướng, walk hoặc pose. Profile của năm nhân vật dùng canvas 32×32, scale 1, giữ footprint 18×10 và anchor (0,5; 0); chỉ bind `idle.down`, các trạng thái chưa có dùng fallback `idle.down` của runtime. Checklist 32×48/2 frame bên dưới tiếp tục giữ nguyên cho bộ sản xuất. Xem [biên bản nhập thử](09_IMPORTED_ASSETS_2026-10-13.md) để đối chiếu file nguồn và checksum.

## Linh

| ID | Path | Key profile | Frame count | FPS / duration s | Sheet px | Ưu tiên / hành động |
| --- | --- | --- | --- | --- | --- | --- |
| char.linh.idle.down | sprites/linh/idle_down.png | idle.down | 2 | 2 / 0.5 | 64×48 | P0 / Linh đứng nhìn xuống. |
| char.linh.walk.down | sprites/linh/walk_down.png | walk.down | 6 | 8 / 0.125 | 192×48 | P0 / Linh đi hướng xuống. |
| char.linh.idle.up | sprites/linh/idle_up.png | idle.up | 2 | 2 / 0.5 | 64×48 | P0 / Linh đứng nhìn lên. |
| char.linh.walk.up | sprites/linh/walk_up.png | walk.up | 6 | 8 / 0.125 | 192×48 | P0 / Linh đi hướng lên. |
| char.linh.idle.left | sprites/linh/idle_left.png | idle.left | 2 | 2 / 0.5 | 64×48 | P0 / Linh đứng nhìn trái. |
| char.linh.walk.left | sprites/linh/walk_left.png | walk.left | 6 | 8 / 0.125 | 192×48 | P0 / Linh đi hướng trái. |
| char.linh.idle.right | sprites/linh/idle_right.png | idle.right | 2 | 2 / 0.5 | 64×48 | P0 / Linh đứng nhìn phải. |
| char.linh.walk.right | sprites/linh/walk_right.png | walk.right | 6 | 8 / 0.125 | 192×48 | P0 / Linh đi hướng phải. |
| char.linh.pose.phone | sprites/linh/pose_phone.png | pose.phone | 4 | 8 / 0.125 | 128×48 | P0 / Giơ điện thoại chụp/ghi âm, hướng xuống chếch phải. |
| char.linh.pose.book | sprites/linh/pose_book.png | pose.book | 1 | 1 / 1 | 32×48 | P0 / Giữ sổ trên tay; ảnh tĩnh. |
| char.linh.pose.seated | sprites/linh/pose_seated.png | pose.seated | 4 | 4 / 0.25 | 128×48 | P1 / C: ngồi bàn cuối, môi mấp máy không tiếng; chuỗi một lần. |

## Trang

| ID | Path | Key profile | Frame count | FPS / duration s | Sheet px | Ưu tiên / hành động |
| --- | --- | --- | --- | --- | --- | --- |
| char.trang.idle.down | sprites/trang/idle_down.png | idle.down | 2 | 2 / 0.5 | 64×48 | P0 / Trang đứng nhìn xuống. |
| char.trang.walk.down | sprites/trang/walk_down.png | walk.down | 6 | 8 / 0.125 | 192×48 | P2 / Trang đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.trang.idle.up | sprites/trang/idle_up.png | idle.up | 2 | 2 / 0.5 | 64×48 | P0 / Trang đứng nhìn lên. |
| char.trang.walk.up | sprites/trang/walk_up.png | walk.up | 6 | 8 / 0.125 | 192×48 | P2 / Trang đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.trang.idle.left | sprites/trang/idle_left.png | idle.left | 2 | 2 / 0.5 | 64×48 | P0 / Trang đứng nhìn trái. |
| char.trang.walk.left | sprites/trang/walk_left.png | walk.left | 6 | 8 / 0.125 | 192×48 | P2 / Trang đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.trang.idle.right | sprites/trang/idle_right.png | idle.right | 2 | 2 / 0.5 | 64×48 | P0 / Trang đứng nhìn phải. |
| char.trang.walk.right | sprites/trang/walk_right.png | walk.right | 6 | 8 / 0.125 | 192×48 | P2 / Trang đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.trang.pose.phone | sprites/trang/pose_phone.png | pose.phone | 4 | 8 / 0.125 | 128×48 | P1 / C: giơ điện thoại đọc tin Linh. |

## Đức

| ID | Path | Key profile | Frame count | FPS / duration s | Sheet px | Ưu tiên / hành động |
| --- | --- | --- | --- | --- | --- | --- |
| char.duc.idle.down | sprites/duc/idle_down.png | idle.down | 2 | 2 / 0.5 | 64×48 | P0 / Đức đứng nhìn xuống. |
| char.duc.walk.down | sprites/duc/walk_down.png | walk.down | 6 | 8 / 0.125 | 192×48 | P2 / Đức đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.duc.idle.up | sprites/duc/idle_up.png | idle.up | 2 | 2 / 0.5 | 64×48 | P0 / Đức đứng nhìn lên. |
| char.duc.walk.up | sprites/duc/walk_up.png | walk.up | 6 | 8 / 0.125 | 192×48 | P2 / Đức đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.duc.idle.left | sprites/duc/idle_left.png | idle.left | 2 | 2 / 0.5 | 64×48 | P0 / Đức đứng nhìn trái. |
| char.duc.walk.left | sprites/duc/walk_left.png | walk.left | 6 | 8 / 0.125 | 192×48 | P2 / Đức đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.duc.idle.right | sprites/duc/idle_right.png | idle.right | 2 | 2 / 0.5 | 64×48 | P0 / Đức đứng nhìn phải. |
| char.duc.walk.right | sprites/duc/walk_right.png | walk.right | 6 | 8 / 0.125 | 192×48 | P2 / Đức đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.duc.pose.photo | sprites/duc/pose_photo.png | pose.photo | 4 | 8 / 0.125 | 128×48 | P1 / Trao ảnh gia đình, không cần ảnh đứng riêng. |

## Bà Năm

| ID | Path | Key profile | Frame count | FPS / duration s | Sheet px | Ưu tiên / hành động |
| --- | --- | --- | --- | --- | --- | --- |
| char.nam.idle.down | sprites/nam/idle_down.png | idle.down | 2 | 2 / 0.5 | 64×48 | P0 / Bà Năm đứng nhìn xuống. |
| char.nam.walk.down | sprites/nam/walk_down.png | walk.down | 6 | 8 / 0.125 | 192×48 | P2 / Bà Năm đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.nam.idle.up | sprites/nam/idle_up.png | idle.up | 2 | 2 / 0.5 | 64×48 | P0 / Bà Năm đứng nhìn lên. |
| char.nam.walk.up | sprites/nam/walk_up.png | walk.up | 6 | 8 / 0.125 | 192×48 | P2 / Bà Năm đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.nam.idle.left | sprites/nam/idle_left.png | idle.left | 2 | 2 / 0.5 | 64×48 | P0 / Bà Năm đứng nhìn trái. |
| char.nam.walk.left | sprites/nam/walk_left.png | walk.left | 6 | 8 / 0.125 | 192×48 | P2 / Bà Năm đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.nam.idle.right | sprites/nam/idle_right.png | idle.right | 2 | 2 / 0.5 | 64×48 | P0 / Bà Năm đứng nhìn phải. |
| char.nam.walk.right | sprites/nam/walk_right.png | walk.right | 6 | 8 / 0.125 | 192×48 | P2 / Bà Năm đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.nam.pose.key | sprites/nam/pose_key.png | pose.key | 4 | 8 / 0.125 | 128×48 | P1 / Trao/nhận chìa ngăn dưới. |
| char.nam.pose.gate | sprites/nam/pose_gate.png | pose.gate | 4 | 8 / 0.125 | 128×48 | P1 / Đưa tay mở cổng, giữ silhouette rõ. |

## Thầy Hùng

| ID | Path | Key profile | Frame count | FPS / duration s | Sheet px | Ưu tiên / hành động |
| --- | --- | --- | --- | --- | --- | --- |
| char.hung.idle.down | sprites/hung/idle_down.png | idle.down | 2 | 2 / 0.5 | 64×48 | P0 / Thầy Hùng đứng nhìn xuống. |
| char.hung.walk.down | sprites/hung/walk_down.png | walk.down | 6 | 8 / 0.125 | 192×48 | P2 / Thầy Hùng đi hướng xuống trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.hung.idle.up | sprites/hung/idle_up.png | idle.up | 2 | 2 / 0.5 | 64×48 | P0 / Thầy Hùng đứng nhìn lên. |
| char.hung.walk.up | sprites/hung/walk_up.png | walk.up | 6 | 8 / 0.125 | 192×48 | P2 / Thầy Hùng đi hướng lên trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.hung.idle.left | sprites/hung/idle_left.png | idle.left | 2 | 2 / 0.5 | 64×48 | P0 / Thầy Hùng đứng nhìn trái. |
| char.hung.walk.left | sprites/hung/walk_left.png | walk.left | 6 | 8 / 0.125 | 192×48 | P2 / Thầy Hùng đi hướng trái trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.hung.idle.right | sprites/hung/idle_right.png | idle.right | 2 | 2 / 0.5 | 64×48 | P0 / Thầy Hùng đứng nhìn phải. |
| char.hung.walk.right | sprites/hung/walk_right.png | walk.right | 6 | 8 / 0.125 | 192×48 | P2 / Thầy Hùng đi hướng phải trong chuyển cảnh; chưa có đường đi tự động, chỉ làm sau bộ đứng. |
| char.hung.pose.write | sprites/hung/pose_write.png | pose.write | 6 | 8 / 0.125 | 192×48 | P0 / Cúi viết/ký trên tờ mới, không sửa sổ. |

## Nga

| ID | Path | Key profile | Frame count | FPS / duration s | Sheet px | Ưu tiên / hành động |
| --- | --- | --- | --- | --- | --- | --- |
| char.nga.idle.down | sprites/nga/idle_down.png | idle.down | 1 | 1 / 1 | 32×48 | P0 / Đứng nhìn xuống; không thương tích, không hình quái vật. |
| char.nga.idle.up | sprites/nga/idle_up.png | idle.up | 1 | 1 / 1 | 32×48 | P0 / Bóng nhìn lên, dùng sau kính cửa. |
| char.nga.walk.down | sprites/nga/walk_down.png | walk.down | 6 | 8 / 0.125 | 192×48 | P0 / A: đi ra cửa. |
| char.nga.pose.seated | sprites/nga/pose_seated.png | pose.seated | 1 | 1 / 1 | 32×48 | P0 / Ngồi; dùng cùng hình cho bóng tường và bàn cuối. |
| char.nga.pose.point | sprites/nga/pose_point.png | pose.point | 4 | 8 / 0.125 | 128×48 | P0 / Giơ tay chỉ thùng bên phải; không chỉ lời giải khác. |

## Liên kết và hành vi đã có

Linh: `story-world.json.player.animations[key]`; năm NPC: `npcs[id].animations[key]`. ID nhân vật `nam` là bà Năm, `hung` là thầy Hùng; không tạo entity riêng cho các speaker `call`, `nam_door`, `hung_tape`, `nga_tape`.

Player dùng hướng chuyển động thật sau collision. NPC đứng theo placement; khi nói sẽ quay về Linh; chưa tự đi theo hoặc tự phát walk chỉ vì đổi room. WorldView dùng `idle.<hướng>` rồi `idle.down` nếu thiếu pose/hướng. `pose.*` một lần rồi giữ cuối, idle/walk loop; metadata Animation mặc định loop không tự biến pose thành loop. Linh đứng cầm sổ tự dùng pose.book; đang đi vẫn dùng walk.

| Pose/cue | Điểm nối hiện tại / phần còn lại |
| --- | --- |
| Linh phone | C1-02-SAVE-E01, C1-04-READ-E09, C3-05-SAVE-E01, C4-01-COUNT-E04, C4-02-TAKE-E01, C5-05-FULL-E01/E11, C4-05-MAIN-E05 và C-02-MAIN-E02 đã có cue. |
| Linh book | C1-04-HAUNT-E11; idle khi sổ CARRIED. Chỉ một pose hướng xuống, không tự có bốn pose cầm sổ. |
| Linh seated / Trang phone | C-02-MAIN-E09 / E11 đã có; chuỗi môi4frame là một lần. Muốn môi loop liên tục cần thêm tùy chọn trình bày, không sửa state C. |
| Đức photo | Đề xuất cue C4-01: chưa có binding pose.photo; nối tại nhịp trao ảnh đúng sourceRows, không mở thêm sự kiện. |
| Bà Năm key/gate | Chưa có binding pose.key/pose.gate; nối C3-01-MAIN-E13, C3-02-MAIN-E08, B-02-MAIN-E01 theo hành động thực; đặt idle ở nhịp sau nếu không muốn giữ pose suốt block. |
| Hùng write | C5-05-FULL-E07, room CLASSROOM, anchor evidence, dx−34,dy−24; hiệu chỉnh theo art, không chuyển player vật lý. |
| Nga seated/point/up/down/walk | C1-04-HAUNT-E08; C4-01-PLACE-E03; C4-05-MAIN-E14; C5-04-CONFESS-E01; C6-01-GATHER-E10; A-01-MAIN-E03. |
| NPC walk P2 | Profile nhận key, nhưng chưa có lộ trình tự động. Nếu thêm dàn cảnh dùng cues actors/to/seconds đúng hành động nguồn, không tạo follow AI. |

Cue actors dùng id,pose,anchor hoặc npc:<id>,dx,dy,alpha,hidden,to,seconds. Vị trí render riêng, không sửa Player/GameState. Reset khi đổi block; clear=true xóa overrides. Các cue có asset chỉ nhận Texture, không tự phát spritesheet effect. Nga không collision/interaction, ghế thật độc lập. Không tạo “bóng Nga” thành NPC khác.

## Portrait

| ID | Path | Kích thước | Ưu tiên / biểu cảm |
| --- | --- | --- | --- |
| portrait.linh.normal | portraits/linh_normal.png | 150×175 PROPOSED | P0 / Linh trung tính; cùng góc mặt, cùng đường vai. |
| portrait.trang.normal | portraits/trang_normal.png | 150×175 PROPOSED | P0 / Trang trung tính; cùng góc mặt, cùng đường vai. |
| portrait.duc.normal | portraits/duc_normal.png | 150×175 PROPOSED | P0 / Đức trung tính; cùng góc mặt, cùng đường vai. |
| portrait.nam.normal | portraits/nam_normal.png | 150×175 PROPOSED | P0 / Bà Năm trung tính; cùng góc mặt, cùng đường vai. |
| portrait.hung.normal | portraits/hung_normal.png | 150×175 PROPOSED | P0 / Thầy Hùng trung tính; cùng góc mặt, cùng đường vai. |
| portrait.nga.normal | portraits/nga_normal.png | 150×175 PROPOSED | P0 / Nga trung tính; cùng góc mặt, cùng đường vai. |
| portrait.linh.worried | portraits/linh_worried.png | 150×175 PROPOSED | P2 / Linh lo lắng; biến thể gương mặt đề xuất. |
| portrait.trang.worried | portraits/trang_worried.png | 150×175 PROPOSED | P2 / Trang lo lắng; biến thể gương mặt đề xuất. |
| portrait.duc.grief | portraits/duc_grief.png | 150×175 PROPOSED | P2 / Đức đau buồn, không ngầm tha thứ; biến thể gương mặt đề xuất. |
| portrait.nam.grave | portraits/nam_grave.png | 150×175 PROPOSED | P2 / Bà Năm nghiêm nghị; biến thể gương mặt đề xuất. |
| portrait.hung.remorse | portraits/hung_remorse.png | 150×175 PROPOSED | P2 / Thầy Hùng nhận trách nhiệm; biến thể gương mặt đề xuất. |
| portrait.nga.quiet | portraits/nga_quiet.png | 150×175 PROPOSED | P2 / Nga bình lặng, không nụ cười tha thứ; biến thể gương mặt đề xuất. |

**Schema đúng:** `content.json.speakers.<id>.portraits.<expression>`. Dòng thoại có `speakerId` và `expression`. `DialogueSpeaker.getPortrait()` tìm biểu cảm rồi normal. Các tên `portraitCenterX`, `portraitBottom`, `portraitHeight`, `portraitMaxWidth` override bounds. Root guide cũ ghi expressions/height/maxWidth là không đúng field hiện hành; dùng tên ở đây. Biểu cảm phụ chỉ là PROPOSED, không tự thay hàng loạt nội dung thoại.
