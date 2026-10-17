# Môi trường, bản đồ và vật thể

**Bổ sung 13/10/2026:** theo yêu cầu người dùng, `maps/room_304.png` đã được vẽ lại theo artstyle ảnh cung cấp và xuất PNG RGBA kín 640×480 ở 1×, lọc Nearest; chưa dựng TMX/tileset thiếu. Bản vẽ lại giữ bố cục và geometry đã nhập. Tọa độ prototype trong bảng 11/10 dưới đây được thay bằng geometry thực tế trong `config/story-world.json` cho phòng này: tám bàn/ghế 60×63 tại x=123/210/371/458, y=104/232; lối giữa 101 px và lối ngang 65 px. Các object giữ ID nhưng bounds đã căn theo ảnh; xem bảng đầy đủ ở [biên bản 09](09_IMPORTED_ASSETS_2026-10-13.md). Không dùng tọa độ prototype bên dưới để ghi đè bản phòng đã import.

## Phương án sản xuất chính

CONFIRMED: bốn RoomId, TMX orthogonal finite, tile 32×32, renderer unitScale1; mỗi vùng một màn hình. PROPOSED: giữ 20×15 ô =640×480 và một tileset512×256 (16×8 ô, 128 vị trí, không bắt buộc vẽ đầy). Không sản xuất bốn PNG toàn cảnh trùng tilemap. Các slot `map.story.*` vẫn giữ trong manifest làm fallback/đường thay thế; không xóa chúng. Menu/ending art là P2 riêng, không thay map.

| ID | Path dưới game/assets/ | Quy cách | Vai trò |
| --- | --- | --- | --- |
| tileset.school | maps/tilesets/school.png | 512×256 PNG RGBA; tile 32×32; 16×8 ô | Bộ chung cho bốn phòng; chỉ vẽ các ô được giao, ô dư trong suốt. |
| tileset.school.definition | maps/tilesets/school.tsx | TSX; tile 32×32; 128 ô; margin/spacing 0 | File lắp tileset; người dựng bản đồ thực hiện. |
| map.tmx.room_304 | maps/room_304.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Dựng Phòng 304 từ school.tsx; không vẽ lại PNG toàn cảnh. |
| map.tmx.hallway | maps/hallway.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Dựng Hành lang tầng ba từ school.tsx; không vẽ lại PNG toàn cảnh. |
| map.tmx.guard_room | maps/guard_room.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Dựng Phòng bảo vệ từ school.tsx; không vẽ lại PNG toàn cảnh. |
| map.tmx.classroom | maps/classroom.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Dựng Lớp học bên cạnh từ school.tsx; không vẽ lại PNG toàn cảnh. |

TSX tham chiếu `school.png` tương đối cùng thư mục; TMX tham chiếu `tilesets/school.tsx`. Tileset source/PNG là dependency của TmxMapLoader, không gọi thêm GameAssets.texture cho cùng sheet. Không bật infinite map, image collection tileset, isometric hoặc trim/rotate tiles. Giữ Nearest trên texture map; loader trực tiếp không dùng filter của manifest PNG.

## Hợp đồng lớp

| Layer | Kiểu / dữ liệu | Trạng thái |
| --- | --- | --- |
| ground | Tile layer sàn | SDD CONFIRMED; đặt trước decor. |
| decor | Tile layer đồ nền không tương tác | SDD CONFIRMED; không bake ghế dịch/sổ/đồ có trạng thái. |
| collisions | Object layer rectangle; x/y/width/height hợp lệ | Runtime bắt buộc, không đọc collision từ pixel/tile tự động. |
| spawns | Object layer point hoặc rectangle; name=ID spawn | Runtime bắt buộc, góc footprint sau đổi Y; đủ tất cả ID gốc. |
| interactables | Object layer rectangle; property id hoặc name; type PROP/DOOR nếu khai | Runtime bắt buộc; mọi object logic phải có đúng một lần. |
| doors | Object layer rectangle giống interactables; targetRoom/spawnId nếu override | Optional; không lặp ID với interactables. |
| npcs | Object layer point; name=trang/duc/nam/hung/nga | Optional; thay npcSlots nhưng placements có x/y vẫn override. |
| foreground | Tile layer đồ che phía trước nhân vật | PROPOSED khi cần che; chưa có render pass sau actor. Để riêng, không coi thứ tự trong TMX là đã giải quyết occlusion. |

Object layers đặt cấp map, không lồng group. `importGeometry()` trong StoryWorldView clone scene JSON, thay collisions/spawns/object rectangles và optional NPC slots, rồi SceneCatalog.validateGeometry. Không tự thêm object gameplay bằng TMX: `result.object(id)` phải tìm được trong JSON. Không đổi targetRoom để vượt story gate.

| RoomId | Spawn bắt buộc | Object bắt buộc |
| --- | --- | --- |
| ROOM_304 | entry, desk, cabinet, family | door.hall, photo, desk, player, crate, cabinet, cabinet.upper, lastdesk, window |
| HALLWAY | entry, room304, hung, guard, chalk | door.304, door.hung, stairs, guard.wall, board |
| GUARD_ROOM | entry, gate, outside | door.hall, records, gate |
| CLASSROOM | entry, table | door.hall, evidence |

## Mặt bằng và tọa độ tham chiếu

Tọa độ sau đây CONFIRMED là layout kiểm thử hiện tại, giữ để import đợt đầu; không phải yêu cầu nghệ thuật bất biến. Đơn vị px, origin dưới trái. Tiled Y xuống: rectangle xuất ở yTiled=480−yRuntime−height; point yTiled=480−yRuntime. Loader đã đổi Y, không lật lần hai.

| Phòng / object | x, y | w × h | Asset hiện tại |
| --- | --- | --- | --- |
| ROOM_304/door.hall | 300, 36 | 60×22 | Hình học/chữ |
| ROOM_304/photo | 45, 340 | 54×34 | prop.photo |
| ROOM_304/desk | 240, 340 | 80×34 | prop.register |
| ROOM_304/player | 510, 270 | 54×28 | prop.player |
| ROOM_304/crate | 510, 190 | 54×28 | prop.crate |
| ROOM_304/cabinet | 535, 370 | 46×22 | prop.cabinet |
| ROOM_304/cabinet.upper | 535, 420 | 46×20 | Hình học/chữ |
| ROOM_304/lastdesk | 435, 355 | 58×20 | Hình học/chữ |
| ROOM_304/window | 375, 420 | 74×16 | Hình học/chữ |
| HALLWAY/door.304 | 300, 300 | 60×24 | Hình học/chữ |
| HALLWAY/door.hung | 120, 300 | 60×24 | Hình học/chữ |
| HALLWAY/stairs | 25, 195 | 46×60 | Hình học/chữ |
| HALLWAY/guard.wall | 560, 220 | 46×50 | Hình học/chữ |
| HALLWAY/board | 365, 300 | 90×24 | Hình học/chữ |
| GUARD_ROOM/door.hall | 300, 400 | 60×24 | Hình học/chữ |
| GUARD_ROOM/records | 65, 330 | 70×32 | prop.papers |
| GUARD_ROOM/gate | 295, 60 | 70×24 | Hình học/chữ |
| CLASSROOM/door.hall | 300, 36 | 60×22 | Hình học/chữ |
| CLASSROOM/evidence | 430, 320 | 74×30 | Hình học/chữ |

| Room / spawn | x,y |
| --- | --- |
| ROOM_304/entry | 315, 90 |
| ROOM_304/desk | 278, 298 |
| ROOM_304/cabinet | 550, 327 |
| ROOM_304/family | 458, 317 |
| HALLWAY/entry | 315, 250 |
| HALLWAY/room304 | 315, 250 |
| HALLWAY/hung | 138, 250 |
| HALLWAY/guard | 510, 230 |
| HALLWAY/chalk | 310, 250 |
| GUARD_ROOM/entry | 314, 350 |
| GUARD_ROOM/gate | 318, 120 |
| GUARD_ROOM/outside | 318, 36 |
| CLASSROOM/entry | 315, 90 |
| CLASSROOM/table | 310, 245 |

Cửa 304 dưới; bàn giáo viên trên; ảnh trái; máy/thùng/tủ phải; bàn cuối gần cửa sổ. Hành lang có bảng tin/khay phấn và tường biến thành cửa cuối phải. Bảo vệ gồm cả trong/ngoài cổng; không thêm sân map. Lớp Hùng có sách/hộp và bàn đối chiếu. Lối đi tối thiểu64 px theo yêu cầu hai ô nhân vật; không phủ props lên spawn.

Tileset: hàng0 sàn,1 tường/góc,2 cầu thang/khung cửa,3 bàn ghế nền/bàn phụ,4 sách/hộp/bảng chìa,5 khay phấn/ngăn pin/khung tủ,6 bên ngoài cổng,7 dự phòng. Chìa/thẻ/ngăn pin/sách là chi tiết của tileset/pose, không thêm inventory icon. Cây/sân chỉ thấy qua cửa sổ, không thêm cảnh ngoài phạm vi. Không bake vật đang tương tác vào decor rồi vẽ nó lần hai.

## Props, cảnh và giới hạn phải xử lý khi import

| ID | PNG dưới game/assets/ | Kích thước PROPOSED | Binding |
| --- | --- | --- | --- |
| prop.photo | sprites/props/photo.png | 54×34 | ROOM_304/photo |
| prop.register | sprites/props/register.png | 80×34 | ROOM_304/desk:normal |
| prop.desk.empty | sprites/props/desk_empty.png | 80×34 | ROOM_304/desk:empty |
| prop.desk.final | sprites/props/desk_final.png | 80×34 | ROOM_304/desk:final |
| prop.player | sprites/props/player.png | 54×28 | ROOM_304/player:empty |
| prop.player.powered | sprites/props/player_powered.png | 54×28 | ROOM_304/player:powered |
| prop.player.playing | sprites/props/player_playing.png | 54×28 | ROOM_304/player:playing |
| prop.player.open | sprites/props/player_open.png | 54×28 | PROP_CUE |
| prop.crate | sprites/props/crate.png | 54×28 | ROOM_304/crate:closed |
| prop.crate.open | sprites/props/crate_open.png | 54×28 | ROOM_304/crate:open |
| prop.cabinet | sprites/props/cabinet.png | 46×22 | ROOM_304/cabinet:closed |
| prop.cabinet.locked | sprites/props/cabinet_locked.png | 46×22 | ROOM_304/cabinet:locked |
| prop.cabinet.open | sprites/props/cabinet_open.png | 46×22 | PROP_CUE |
| prop.cabinet.upper | sprites/props/cabinet_upper.png | 46×20 | ROOM_304/cabinet.upper |
| prop.papers | sprites/props/papers.png | 70×32 | GUARD_ROOM/records |
| prop.lastdesk | sprites/props/lastdesk.png | 58×20 | ROOM_304/lastdesk |
| prop.window | sprites/props/window.png | 74×16 | ROOM_304/window |
| prop.board | sprites/props/board.png | 90×24 | HALLWAY/board |
| prop.guard.wall | sprites/props/guard_wall.png | 46×50 | PROP_CUE |
| prop.guard.door | sprites/props/guard_door.png | 46×50 | PROP_CUE |
| prop.gate.closed | sprites/props/gate_closed.png | 70×24 | PROP_CUE |
| prop.gate.open | sprites/props/gate_open.png | 70×24 | PROP_CUE |
| prop.evidence | sprites/props/evidence.png | 74×30 | CLASSROOM/evidence:normal |
| prop.evidence.signed | sprites/props/evidence_signed.png | 74×30 | CLASSROOM/evidence:final |
| prop.chair | sprites/props/chair.png | 22×9 | CHAIR |

`objects[].asset` và `variants` là texture tĩnh, chưa đọc animation prop. `propState()` đang chọn: desk normal/empty/final; cabinet closed/locked; crate closed/open theo có cassette; player empty/powered/playing theo vị trí pin/băng; evidence normal/final theo giấy ký; mọi ID khác normal. Dùng prop.register/desk.empty/desk.final như **ba ảnh trọn bàn**, không chồng icon sổ lên bàn đã vẽ. Prop.photo dùng bản thu nhỏ document.photo để giữ 33 người nhất quán.

Các dòng PROP_CUE chưa có điều khiển hình tự động: guard.wall/guard.door, gate.closed/open, cabinet.open, player.open. Thêm override **presentation** theo cue/state thật trong StoryVisuals/StoryWorldView, không thêm cờ chuyện mới hoặc đổi gate. Cửa bảo vệ hiện theo GUARD_DOOR_REVEALED + hiện tượng ACTIVE; hiện renderer mới đổi nhãn, chưa đổi texture. Với cổng và mở ngăn, bám nhịp nguồn, không suy luôn mở từ một ảnh có sẵn. Khay đóng/lắp pin/trao đồ giữ bằng pose + cận tài liệu, không cần animation cơ khí dài.

`prop.chair` → asset của effect chair tại C1-04-HAUNT-E03 và C4-05-MAIN-E14, vẽ đúng kích thước22×9. Vạch phấn là geometry hiện vẽ **trước TMX/PNG**, nền kín có thể che mất; cần đưa nét phấn qua pass overlay trong StoryWorldView.render, không giao artist vẽ lại. Nền lạnh hiện chỉ đổi màu nền fallback; TMX kín không tự lạnh. Áp tint/fade presentation 0,2–0,4s theo cue/phenomenon khi import, giữ chữ và lối đi sáng. PNG scene.backgrounds có các key NORMAL/ACTIVE/SUSPENDED/RESOLVED nhưng bị bỏ qua khi TMX được nạp.

Tất cả tile layers hiện render trước props/actors; sửa pass foreground nếu thực tế cần, không tăng collision vì hình đồ vật cao hơn. Resize vẫn FitViewport; NPC stage x/y trong story-world phải kiểm tra cùng map mới. New Game xóa cache map của lượt, **khởi động lại ứng dụng** sau thay texture/config để chắc chắn không dùng cache AssetManager cũ.

Ghi chú thứ tự vẽ: props hiện vẽ trước toàn bộ actor; chỉ các actor được sắp theo Y. Nếu art cao cần che nhân vật ở phía sau, xử lý thứ tự props/actor trong StoryWorldView, hoặc giữ phần che ở pass foreground đã tách; đừng sửa collision để che lỗi hình.
