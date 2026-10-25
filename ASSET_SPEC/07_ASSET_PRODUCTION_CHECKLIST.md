# Checklist sản xuất và nghiệm thu

Snapshot danh mục ngày11/10/2026:162 mục,160 file chưa có tại thời điểm đó. Hai checkbox [x] duy nhất là font/giấy phép đã kiểm tra file. Checkbox asset biểu thị **file đạt quy cách sản xuất**, không đồng nghĩa runtime đã nghiệm thu; các gate tích hợp phía cuối vẫn để trống. Danh mục chuẩn:01_ASSET_MASTER_LIST.md. Path dưới game/assets/.

## Đợt nhập thử — 13/10/2026

[Biên bản 09](09_IMPORTED_ASSETS_2026-10-13.md) là nơi ghi metadata, checksum, nguồn và kết quả kiểm tra của đợt này. Năm idle hướng xuống đã có bản thử: Linh/Đức/Trang 192×32, sáu frame 32×32, 0,15 giây/frame; Bà Năm/Hùng 32×32, một frame/1 giây. Các nguồn trong `added_asset/` được giữ nguyên. Bộ thử này chưa hoàn thành quy cách sản xuất 32×48, hai frame/2 FPS và không đóng các checkbox tương ứng.

Nền phòng 304 đã được imagegen vẽ lại theo artstyle ảnh nguồn và xuất PNG RGBA kín 640×480 ở 1×, lọc Nearest; checksum cuối ghi tại 09. Bố cục tám bàn và geometry JSON đã nhập giữ nguyên; footprint 18×10 và tốc độ 120 giữ nguyên. Đợt này không bổ sung các TMX/tileset, walk, pose, portrait, UI, audio hoặc asset còn thiếu. Build/core/desktop và kiểm tra ánh lạnh đã qua; đã xem bản 1×/2× Nearest và năm nhân vật trong game. Bằng chứng, phạm vi và giới hạn ghi tại 09; các gate sản xuất đầy đủ vẫn giữ mở.

Thông số, nội dung, trạng thái kiểm kê và tuyến tích hợp của từng ID xem tại [danh mục chuẩn](01_ASSET_MASTER_LIST.md). Checklist này chỉ theo dõi tiến độ theo ưu tiên; giữ ô kiểm hiện có, không coi file nguồn hoặc file tải về là đã tích hợp/đạt nghiệm thu.

## P0

### Nhân vật

- [ ] **char.linh.idle.down** — `sprites/linh/idle_down.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; có bản thử 32×32/sáu frame tại 09, bản sản xuất chưa nghiệm thu; PROPOSED
- [ ] **char.linh.walk.down** — `sprites/linh/walk_down.png` — 32×48/frame; 6 frame; 8 FPS; 192×48; lặp; chưa có; PROPOSED
- [ ] **char.linh.idle.up** — `sprites/linh/idle_up.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.linh.walk.up** — `sprites/linh/walk_up.png` — 32×48/frame; 6 frame; 8 FPS; 192×48; lặp; chưa có; PROPOSED
- [ ] **char.linh.idle.left** — `sprites/linh/idle_left.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.linh.walk.left** — `sprites/linh/walk_left.png` — 32×48/frame; 6 frame; 8 FPS; 192×48; lặp; chưa có; PROPOSED
- [ ] **char.linh.idle.right** — `sprites/linh/idle_right.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.linh.walk.right** — `sprites/linh/walk_right.png` — 32×48/frame; 6 frame; 8 FPS; 192×48; lặp; chưa có; PROPOSED
- [ ] **char.trang.idle.down** — `sprites/trang/idle_down.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; có bản thử 32×32/sáu frame tại 09, bản sản xuất chưa nghiệm thu; PROPOSED
- [ ] **char.trang.idle.up** — `sprites/trang/idle_up.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.trang.idle.left** — `sprites/trang/idle_left.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.trang.idle.right** — `sprites/trang/idle_right.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.duc.idle.down** — `sprites/duc/idle_down.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; có bản thử 32×32/sáu frame tại 09, bản sản xuất chưa nghiệm thu; PROPOSED
- [ ] **char.duc.idle.up** — `sprites/duc/idle_up.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.duc.idle.left** — `sprites/duc/idle_left.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.duc.idle.right** — `sprites/duc/idle_right.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.nam.idle.down** — `sprites/nam/idle_down.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; có bản thử 32×32/một frame tại 09, bản sản xuất chưa nghiệm thu; PROPOSED
- [ ] **char.nam.idle.up** — `sprites/nam/idle_up.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.nam.idle.left** — `sprites/nam/idle_left.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.nam.idle.right** — `sprites/nam/idle_right.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.hung.idle.down** — `sprites/hung/idle_down.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; có bản thử 32×32/một frame tại 09, bản sản xuất chưa nghiệm thu; PROPOSED
- [ ] **char.hung.idle.up** — `sprites/hung/idle_up.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.hung.idle.left** — `sprites/hung/idle_left.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.hung.idle.right** — `sprites/hung/idle_right.png` — 32×48/frame; 2 frame; 2 FPS; 64×48; lặp; chưa có; PROPOSED
- [ ] **char.linh.pose.phone** — `sprites/linh/pose_phone.png` — 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối; chưa có; PROPOSED
- [ ] **char.linh.pose.book** — `sprites/linh/pose_book.png` — 32×48/frame; 1 frame; 1 FPS; 32×48; một lần, giữ frame cuối; chưa có; PROPOSED
- [ ] **char.hung.pose.write** — `sprites/hung/pose_write.png` — 32×48/frame; 6 frame; 8 FPS; 192×48; một lần, giữ frame cuối; chưa có; PROPOSED
- [ ] **char.nga.idle.down** — `sprites/nga/idle_down.png` — 32×48/frame; 1 frame; 1 FPS; 32×48; lặp; chưa có; PROPOSED
- [ ] **char.nga.idle.up** — `sprites/nga/idle_up.png` — 32×48/frame; 1 frame; 1 FPS; 32×48; lặp; chưa có; PROPOSED
- [ ] **char.nga.walk.down** — `sprites/nga/walk_down.png` — 32×48/frame; 6 frame; 8 FPS; 192×48; lặp; chưa có; PROPOSED
- [ ] **char.nga.pose.seated** — `sprites/nga/pose_seated.png` — 32×48/frame; 1 frame; 1 FPS; 32×48; một lần, giữ frame cuối; chưa có; PROPOSED
- [ ] **char.nga.pose.point** — `sprites/nga/pose_point.png` — 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối; chưa có; PROPOSED

### Portrait

- [ ] **portrait.linh.normal** — `portraits/linh_normal.png`
- [ ] **portrait.trang.normal** — `portraits/trang_normal.png`
- [ ] **portrait.duc.normal** — `portraits/duc_normal.png`
- [ ] **portrait.nam.normal** — `portraits/nam_normal.png`
- [ ] **portrait.hung.normal** — `portraits/hung_normal.png`
- [ ] **portrait.nga.normal** — `portraits/nga_normal.png`

### Môi trường

- [ ] **tileset.school** — `maps/tilesets/school.png`

### Bản đồ kỹ thuật

- [ ] **tileset.school.definition** — `maps/tilesets/school.tsx`
- [ ] **map.tmx.room_304** — `maps/room_304.tmx`
- [ ] **map.tmx.hallway** — `maps/hallway.tmx`
- [ ] **map.tmx.guard_room** — `maps/guard_room.tmx`
- [ ] **map.tmx.classroom** — `maps/classroom.tmx`

### Đồ vật

- [ ] **prop.photo** — `sprites/props/photo.png`
- [ ] **prop.register** — `sprites/props/register.png`
- [ ] **prop.desk.empty** — `sprites/props/desk_empty.png`
- [ ] **prop.desk.final** — `sprites/props/desk_final.png`
- [ ] **prop.player** — `sprites/props/player.png`
- [ ] **prop.player.powered** — `sprites/props/player_powered.png`
- [ ] **prop.player.playing** — `sprites/props/player_playing.png`
- [ ] **prop.crate** — `sprites/props/crate.png`
- [ ] **prop.crate.open** — `sprites/props/crate_open.png`
- [ ] **prop.cabinet** — `sprites/props/cabinet.png`
- [ ] **prop.cabinet.locked** — `sprites/props/cabinet_locked.png`
- [ ] **prop.cabinet.upper** — `sprites/props/cabinet_upper.png`
- [ ] **prop.papers** — `sprites/props/papers.png`
- [ ] **prop.lastdesk** — `sprites/props/lastdesk.png`
- [ ] **prop.window** — `sprites/props/window.png`
- [ ] **prop.board** — `sprites/props/board.png`
- [ ] **prop.guard.wall** — `sprites/props/guard_wall.png`
- [ ] **prop.guard.door** — `sprites/props/guard_door.png`
- [ ] **prop.gate.closed** — `sprites/props/gate_closed.png`
- [ ] **prop.gate.open** — `sprites/props/gate_open.png`
- [ ] **prop.evidence** — `sprites/props/evidence.png`
- [ ] **prop.evidence.signed** — `sprites/props/evidence_signed.png`
- [ ] **prop.chair** — `sprites/props/chair.png`

### Hiệu ứng

- [ ] **effect.memory** — `effects/memory.png`

### Tài liệu

- [ ] **document.msg** — `documents/msg.png`
- [ ] **document.photo** — `documents/photo.png`
- [ ] **document.register** — `documents/register.png`
- [ ] **document.news** — `documents/news.png`
- [ ] **document.gate** — `documents/gate.png`
- [ ] **document.report** — `documents/report.png`
- [ ] **document.inventory** — `documents/inventory.png`
- [ ] **document.signed** — `documents/signed.png`
- [ ] **document.family** — `documents/family.png`
- [ ] **document.tape** — `documents/tape.png`
- [ ] **document.battery** — `documents/battery.png`

### Âm thanh

- [ ] **audio.school.birds** — `audio/ambience/school_birds.ogg`
- [ ] **audio.wind** — `audio/ambience/wind.ogg`

### Font có sẵn

- [x] **font.ui** — `fonts/NotoSans-Regular.ttf`
- [x] **font.ui.license** — `fonts/OFL.txt`

## P1

### Nhân vật

- [ ] **char.linh.pose.seated** — `sprites/linh/pose_seated.png`
- [ ] **char.trang.pose.phone** — `sprites/trang/pose_phone.png`
- [ ] **char.duc.pose.photo** — `sprites/duc/pose_photo.png`
- [ ] **char.nam.pose.key** — `sprites/nam/pose_key.png`
- [ ] **char.nam.pose.gate** — `sprites/nam/pose_gate.png`

### Đồ vật

- [ ] **prop.player.open** — `sprites/props/player_open.png`
- [ ] **prop.cabinet.open** — `sprites/props/cabinet_open.png`

### Tài liệu

- [ ] **document.c_phone** — `documents/trang_phone.png`

### UI

- [ ] **ui.art.dialogue_frame** — `ui/source/dialogue_frame.9.png`
- [ ] **ui.art.panel_frame** — `ui/source/panel_frame.9.png`
- [ ] **ui.art.button_normal** — `ui/source/button_normal.9.png`
- [ ] **ui.art.button_focus** — `ui/source/button_focus.9.png`
- [ ] **ui.art.button_pressed** — `ui/source/button_pressed.9.png`
- [ ] **ui.icon.camera** — `ui/camera.png`
- [ ] **ui.icon.record** — `ui/record.png`

### UI kỹ thuật

- [ ] **ui.skin** — `ui/room304-skin.json`
- [ ] **ui.skin.atlas** — `ui/room304-skin.atlas`
- [ ] **ui.skin.page** — `ui/room304-skin.png`

### Âm thanh

- [ ] **audio.fx.message** — `audio/sfx/message.wav`
- [ ] **audio.fx.door_open** — `audio/sfx/door_open.wav`
- [ ] **audio.fx.door_close** — `audio/sfx/door_close.wav`
- [ ] **audio.fx.footstep** — `audio/sfx/footstep.wav`
- [ ] **audio.fx.page** — `audio/sfx/page.wav`
- [ ] **audio.fx.camera** — `audio/sfx/camera.wav`
- [ ] **audio.fx.bell_strange** — `audio/sfx/bell_strange.wav`
- [ ] **audio.fx.bell_normal** — `audio/sfx/bell_normal.wav`
- [ ] **audio.fx.chair** — `audio/sfx/chair.wav`
- [ ] **audio.fx.knock_three** — `audio/sfx/knock_three.wav`
- [ ] **audio.fx.knock_one** — `audio/sfx/knock_one.wav`
- [ ] **audio.fx.lock** — `audio/sfx/lock.wav`
- [ ] **audio.fx.unlock** — `audio/sfx/unlock.wav`
- [ ] **audio.fx.key** — `audio/sfx/key.wav`
- [ ] **audio.fx.key_rattle** — `audio/sfx/key_rattle.wav`
- [ ] **audio.fx.gate** — `audio/sfx/gate.wav`
- [ ] **audio.fx.chalk** — `audio/sfx/chalk.wav`
- [ ] **audio.fx.button** — `audio/sfx/button.wav`
- [ ] **audio.fx.lid** — `audio/sfx/lid.wav`
- [ ] **audio.fx.paper** — `audio/sfx/paper.wav`
- [ ] **audio.fx.tape_hiss** — `audio/sfx/tape_hiss.wav`
- [ ] **audio.fx.tape_stop** — `audio/sfx/tape_stop.wav`
- [ ] **audio.fx.alarm** — `audio/sfx/alarm.wav`
- [ ] **audio.fx.run** — `audio/sfx/run.wav`
- [ ] **audio.fx.write** — `audio/sfx/write.wav`
- [ ] **audio.voice.call** — `audio/voice/call.wav`
- [ ] **audio.voice.hung_assignment** — `audio/tape/hung_assignment.wav`
- [ ] **audio.voice.nga_recorder** — `audio/tape/nga_recorder.wav`
- [ ] **audio.voice.hung_finish** — `audio/tape/hung_finish.wav`
- [ ] **audio.voice.nga_help_1** — `audio/tape/nga_help_1.wav`
- [ ] **audio.voice.nga_help_2** — `audio/tape/nga_help_2.wav`
- [ ] **audio.voice.nga_stayed** — `audio/voice/nga_stayed.wav`
- [ ] **audio.voice.nga_present** — `audio/voice/nga_present.wav`
- [ ] **audio.voice.linh_present** — `audio/voice/linh_present.wav`
- [ ] **audio.voice.enough** — `audio/voice/enough.wav`

## P2

### Nhân vật

- [ ] **char.trang.walk.down** — `sprites/trang/walk_down.png`
- [ ] **char.trang.walk.up** — `sprites/trang/walk_up.png`
- [ ] **char.trang.walk.left** — `sprites/trang/walk_left.png`
- [ ] **char.trang.walk.right** — `sprites/trang/walk_right.png`
- [ ] **char.duc.walk.down** — `sprites/duc/walk_down.png`
- [ ] **char.duc.walk.up** — `sprites/duc/walk_up.png`
- [ ] **char.duc.walk.left** — `sprites/duc/walk_left.png`
- [ ] **char.duc.walk.right** — `sprites/duc/walk_right.png`
- [ ] **char.nam.walk.down** — `sprites/nam/walk_down.png`
- [ ] **char.nam.walk.up** — `sprites/nam/walk_up.png`
- [ ] **char.nam.walk.left** — `sprites/nam/walk_left.png`
- [ ] **char.nam.walk.right** — `sprites/nam/walk_right.png`
- [ ] **char.hung.walk.down** — `sprites/hung/walk_down.png`
- [ ] **char.hung.walk.up** — `sprites/hung/walk_up.png`
- [ ] **char.hung.walk.left** — `sprites/hung/walk_left.png`
- [ ] **char.hung.walk.right** — `sprites/hung/walk_right.png`

### Portrait

- [ ] **portrait.linh.worried** — `portraits/linh_worried.png`
- [ ] **portrait.trang.worried** — `portraits/trang_worried.png`
- [ ] **portrait.duc.grief** — `portraits/duc_grief.png`
- [ ] **portrait.nam.grave** — `portraits/nam_grave.png`
- [ ] **portrait.hung.remorse** — `portraits/hung_remorse.png`
- [ ] **portrait.nga.quiet** — `portraits/nga_quiet.png`

### Minh họa tùy chọn

- [ ] **background.menu** — `ui/menu.png`
- [ ] **background.ending.a** — `endings/a.png`
- [ ] **background.ending.b** — `endings/b.png`
- [ ] **background.ending.c** — `endings/c.png`

## Gate nghiệm thu theo đợt

- [ ] Quy cách PROPOSED của đợt được duyệt; mẫu nhân vật/bảng màu được chốt.
- [ ] Kiểm file thật: decode, kích thước, alpha, frame grid/pivot, audio format và license.
- [ ] Manifest/reference chính xác, không ID/path trùng, không tài nguyên có mà không consumer.
- [ ] Đã xử lý các presentation gap liên quan (tường/cổng/khay, phấn, ánh lạnh, foreground, cue/camera C).
- [ ] Build và regression core/desktop, source parity: ghi lệnh/exit code.
- [ ] Quan sát hiển thị16:9/4:3 và nghe audio thật; không dùng test backend giả thay nghiệm thu.
- [ ] Save/load, input, collision, read/re-read document, pause và replay không regression.
- [ ] A/B hợp lệ, C bật trong test riêng; artwork/audio không đổi ý nghĩa truyện.
- [ ] Đồng bộ master/artist/AI sau thay spec, ghi lỗi chưa xong.

## Biên bản đợt

| Ngày / batch | ID | File + checksum | Spec đã duyệt | Cấu hình/source đổi | Test thực chạy | Lỗi còn mở | Người duyệt |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 13/10/2026 · nhập thử | Năm `char.*.idle.down`; `map.story.room_304` | [Biên bản 09](09_IMPORTED_ASSETS_2026-10-13.md); ảnh phòng cuối đang chờ | Ngoại lệ 32×32; idle tĩnh Năm/Hùng; PNG phòng tạm theo yêu cầu người dùng | Manifest/profile nhân vật; nền phòng và geometry theo ảnh chỉnh | Build/regression/quan sát game: đang chờ ghi kết quả tại 09 | Thiếu bộ sản xuất; quyền sử dụng chưa có thông tin; ảnh phòng cuối/chứng cứ kiểm thử đang chờ | Chưa nghiệm thu art sản xuất |
