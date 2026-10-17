# Ảnh cận cảnh khi tương tác E — 17/10/2026

Nguồn: PNG có sẵn trong `ASSET_SPEC/download`, do người dùng yêu cầu tích hợp. File nguồn giữ nguyên. 24 ảnh được chép nguyên byte vào `game/assets/ui/inspection/`, giữ kích thước và alpha; không biến thành sprite kích thước nhỏ. Các thông tin nguồn tác giả/giấy phép chưa được bổ sung trong thư mục nguồn.

## Hành vi

- Nhấn E ở đồ vật gần nhất mở tương tác truyện hiện có, đồng thời làm tối cảnh 78% và hiện ảnh cận cảnh trên lời thoại.
- Ảnh giữ tỉ lệ bằng Scaling.fit, lọc Nearest; nhân vật portrait được ẩn khi có ảnh đồ vật. Ảnh thu lại khi hiện lựa chọn để tránh che nút; nếu không còn đủ chỗ thì tạm ẩn ảnh.
- Enter/nút Tiếp tục giữ cách đọc thoại, chọn và hoàn tất hiện có. Esc mở pause; tiếp tục vẫn giữ ảnh đang xem. Di chuyển bị khóa trong tương tác. Giữ E không bỏ qua câu đầu.
- Trả về khám phá, mở ghi chú hoặc tương tác NPC sẽ bỏ ảnh cũ. Nhịp tự động không có chữ vẫn hiện cảnh truyện thay vì che cảnh bằng ảnh đồ vật.
- Ảnh đổi theo vị trí sổ, khóa tủ, đã lấy băng, pin đã lắp và hồ sơ đã ký. Băng đã lắp nhưng chưa có pin hiện máy mở khay; chỉ hiện máy đang phát khi băng và pin cùng ở máy. Việc vẽ ảnh không cấp manh mối hoặc hoàn tất bước truyện.

## Ánh xạ

Đường dẫn trong bảng tính từ `game/assets/ui/inspection/`.

| Khu vực / object ID | Ảnh và biến thể |
| --- | --- |
| ROOM_304 / photo | `photo.png` |
| ROOM_304 / desk | `register.png`; sổ đã lấy: `desk_empty.png`; chuẩn bị cuối: `desk_final.png` |
| ROOM_304 / player | `player.png`; mở khay/chưa có nguồn: `player_open.png`; có pin: `player_powered.png`; băng phát: `player_playing.png` |
| ROOM_304 / crate | `crate.png`; đã lấy cassette: `crate_open.png` |
| ROOM_304 / cabinet | `cabinet.png`; khóa: `cabinet_locked.png`; đã mở: `cabinet_open.png` |
| ROOM_304 / cabinet.upper | `cabinet_upper.png` |
| ROOM_304 / lastdesk | `lastdesk.png` |
| ROOM_304 / window | `window.png` |
| HALLWAY / board | `board.png` |
| HALLWAY / guard.wall | `guard_wall.png`; cửa lộ ra trong hiện tượng: `guard_door.png` |
| GUARD_ROOM / records | `papers.png` |
| GUARD_ROOM / gate | `gate_closed.png`; hiện tượng tạm dừng/đã giải: `gate_open.png` |
| CLASSROOM / evidence | `evidence.png`; đã ký: `evidence_signed.png` |

Ảnh cửa/tường/cổng được dùng khi tương tác đó có lời thoại/lựa chọn. Lượt đi qua cửa chỉ chuyển phòng vẫn giữ hành vi di chuyển hiện có. `chair.png` là đồ trang trí chưa có target E riêng; `guard_room_preview.png` là ảnh tham khảo cả phòng. Hai file này giữ ở nguồn, không thêm target truyện hoặc thay bản đồ trong đợt này.

## Điểm cấu hình và ownership

- `config/asset-manifest.json`: ID `inspection.<tên_file>`, type texture, required true, filter Nearest.
- `config/story-world.json`: `inspection` là ảnh mặc định; `inspectionVariants` ghi đè theo trạng thái `normal/open/closed/locked/empty/powered/playing/final`. ID/type và tên variant được kiểm tra khi đọc scene.
- `SceneCatalog.ObjectDef` chỉ giữ reference. `GameScreen` giữ ngữ cảnh object đã nhấn E; `StoryWorldView.inspectionAsset` chỉ đọc GameState để chọn ảnh. Logic và callback truyện giữ trong StorySession/StoryDirector.
- `DialogueUI` sở hữu actor UI; texture mượn GameAssets/AssetManager cấp ứng dụng. Mở lại cùng đồ vật dùng texture đã cache, không tạo texture mỗi frame.
- Bỏ một khai báo `char.trang.idle.down` trùng hoàn toàn trong manifest sau lần pull/ghép thay đổi cục bộ; giữ nguyên metadata và sheet của Trang.

## Kiểm chứng

- `build`: compile, đóng gói và core regression; thêm kiểm tra 24 PNG RGBA required, binding cho mọi PROP và các biến thể; 99/99 khối truyện vẫn được kiểm tra.
- `lwjgl3:desktopRegressionTest`: regression demo cho input/collision/thoại/pause/lifecycle.
- `lwjgl3:storyDesktopTest`: OpenGL/input/UI thật; thêm E mở ảnh, giữ E, khóa di chuyển, pixel nền tối, pause/tiếp tục, resize 16:9 và 4:3, cache khi mở lại, bỏ ảnh khi đọc ghi chú/NPC và đổi ảnh máy cassette trong cùng tương tác. Luồng A/B/C, save/load và chơi lại vẫn chạy.
- Ảnh kiểm tra được xuất vào `game/lwjgl3/build/story-inspection-*.png` để xem bố cục. Các lệnh chạy với Temurin 21.0.12.1 có trên máy; cấu hình toolchain của project không bị sửa.
