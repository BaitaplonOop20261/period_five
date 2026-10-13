# Room304 — Hướng dẫn AI tích hợp asset

Snapshot ngày11/10/2026, tài liệu-only. **Không có asset/config/code nào được import hoặc sửa trong lần lập đặc tả.** Danh mục chuẩn [01_ASSET_MASTER_LIST.md](01_ASSET_MASTER_LIST.md), bản người sản xuất [FOR_ARTISTS.md](FOR_ARTISTS.md). 162 mục:152 file sản xuất (115PNG+37audio),8 file kỹ thuật,2 font/OFL có sẵn. Frame/FPS/kích thước art cuối đều PROPOSED trừ xác nhận được ghi rõ; kiểm tra phê duyệt của người dùng trước khi coi là hợp đồng cuối.

## 1. Nguồn và phạm vi

Đọc CODEX trước khi thực hiện. Cốt truyện lấy `AGENTS/kich-ban-va-flow-can-phong-304.md` bản1.0; sơ đồ PDF5trang khớp nguồn này. SDD là thiết kế cũ: không sao chép lớp/API dự kiến sang runtime. Hai file root DEVELOPMENT_PROGRESS.md và ASSET_INTEGRATION_GUIDE.md là báo cáo, không mạnh hơn source. Tài liệu này đối chiếu trực tiếp source; tên field portrait và cách vẽ phone có hiệu chỉnh ở dưới.

Bốn phòng ROOM_304/HALLWAY/GUARD_ROOM/CLASSROOM; sáu nhân vật linh/trang/duc/nam/hung/nga. Không thêm sân/map cháy/32 nạn nhân. C đã có kịch bản và đường runtime nhưng mặc định `enableC=false`; kiểm tra C bằng fixture/lượt riêng, trả lại cấu hình phát hành sau test. Không đổi cờ này chỉ vì nhận ảnh C.

## 2. Kiến trúc, load và ownership

Tất cả source ngắn trong tài liệu tính từ `game/core/src/main/java/com/room304/`; path asset tính từ `game/assets/`.

| Source/file | Trách nhiệm thật |
| --- | --- |
| Room304Game.java | Tạo GameAssets chung, StoryContent/SceneCatalog/StoryVisuals validation, SaveStore; đổi screen và dispose. |
| assets/AssetCatalog.java | Đọc config/asset-manifest.json version1; ID/type/path/filter/duplicate/frame metadata validation. |
| assets/GameAssets.java | Một AssetManager cấp app; texture(id),animation(id),optionalTexture(ref),music(id),sound(id),optionalSkin(font),catalog(),dispose(). |
| world/Player.java | Movement/collision/facing và sprite profile; nhận animation qua metadata, không sửa footprint theo art. |
| world/SceneCatalog.java | Geometry bốn scene, profile và placements/NPC stage. |
| world/StoryWorldView.java | TmxMapLoader/cache, renderer, props/actors/effects; importGeometry/propState/drawActor/drawEffects. |
| world/StoryVisuals.java | config/story-visuals.json: documents,lineDocuments,menus,cues; validation cue/actor/anchor/texture. |
| dialogue/DialogueSpeaker.java, DialogueUI.java, DialogueAssets.java, DialogueHud.java | Portrait, pagination, khung thoại/HUD, font và optional Skin. |
| screen/UiPanel.java, MenuScreen.java, GameScreen.java | Menu/pause/notes/settings/save UI; MenuScreen cũng chứa trang ending, không có EndingScreen riêng. |
| audio/StoryAudio.java | config/story-audio.json, per-line Sound và một ambience Music; pause/duck/dispose sound handles. |
| story/StorySession.java, StoryDirector.java, GameState.java | Routing/transactions/gates/state; renderer chỉ đọc, không được đổi nội dung/điều kiện khi import. |
| story/SaveStore.java, ProgressSnapshot.java | Snapshot version1 ở mốc EXP, không serialize texture; kiểm vị trí với geometry hiện tại. |

Luồng texture: manifest ID → AssetCatalog.Entry → GameAssets cache/load đồng bộ → Texture/Animation<TextureRegion> → renderer/UI. Nạp trong update/transition, không tạo Texture/Font mỗi render. Manifest không tự liên kết asset vào game: **phải thêm reference tại profile/object/speaker/cue**.

Optional chưa có file trả null/log lần đầu; required thiếu báo ID/path trước menu. ID chưa đăng ký hoặc file đã có nhưng hỏng vẫn là lỗi, không phải fallback. Có6required hiện tại: font.ui và story.content/world/ui.layout/audio/visuals. Giữ art optional trong quá trình nhập từng đợt.

TMX là ngoại lệ ownership có chủ đích: StoryWorldView nạp bằng TmxMapLoader trực tiếp, cache theo room/lượt, tạo renderer khi đổi room, dispose renderer cũ và map cache cuối lượt. TSX/tileset do map sở hữu, **không tạo AssetManager thứ hai hoặc load lại tileset texture qua manager app**. Skin mỗi view mượn atlas từ manager và default-font do view tạo; tháo default-font trước dispose Skin; entity/view không dispose texture dùng chung. App dispose manager sau screen.

## 3. Chỉ mục import đầy đủ

Bảng sau bao phủ mọi file trong master, cả file kỹ thuật/giấy phép không cần manifest. Các path chưa có file là kế hoạch. `CHAR` metadata chính xác ở cột quy cách, tất cả sheet một hàng; `pose.*` play-once do WorldView quyết định, không có field loop trong manifest.

### Nhân vật

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| char.linh.idle.down | sprites/linh/idle_down.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.player.animations["idle.down"] → Player/StoryWorldView |
| char.linh.walk.down | sprites/linh/walk_down.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.player.animations["walk.down"] → Player/StoryWorldView |
| char.linh.idle.up | sprites/linh/idle_up.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.player.animations["idle.up"] → Player/StoryWorldView |
| char.linh.walk.up | sprites/linh/walk_up.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.player.animations["walk.up"] → Player/StoryWorldView |
| char.linh.idle.left | sprites/linh/idle_left.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.player.animations["idle.left"] → Player/StoryWorldView |
| char.linh.walk.left | sprites/linh/walk_left.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.player.animations["walk.left"] → Player/StoryWorldView |
| char.linh.idle.right | sprites/linh/idle_right.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.player.animations["idle.right"] → Player/StoryWorldView |
| char.linh.walk.right | sprites/linh/walk_right.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.player.animations["walk.right"] → Player/StoryWorldView |
| char.trang.idle.down | sprites/trang/idle_down.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[trang].animations["idle.down"] → Player/StoryWorldView |
| char.trang.walk.down | sprites/trang/walk_down.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[trang].animations["walk.down"] → Player/StoryWorldView |
| char.trang.idle.up | sprites/trang/idle_up.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[trang].animations["idle.up"] → Player/StoryWorldView |
| char.trang.walk.up | sprites/trang/walk_up.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[trang].animations["walk.up"] → Player/StoryWorldView |
| char.trang.idle.left | sprites/trang/idle_left.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[trang].animations["idle.left"] → Player/StoryWorldView |
| char.trang.walk.left | sprites/trang/walk_left.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[trang].animations["walk.left"] → Player/StoryWorldView |
| char.trang.idle.right | sprites/trang/idle_right.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[trang].animations["idle.right"] → Player/StoryWorldView |
| char.trang.walk.right | sprites/trang/walk_right.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[trang].animations["walk.right"] → Player/StoryWorldView |
| char.duc.idle.down | sprites/duc/idle_down.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[duc].animations["idle.down"] → Player/StoryWorldView |
| char.duc.walk.down | sprites/duc/walk_down.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[duc].animations["walk.down"] → Player/StoryWorldView |
| char.duc.idle.up | sprites/duc/idle_up.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[duc].animations["idle.up"] → Player/StoryWorldView |
| char.duc.walk.up | sprites/duc/walk_up.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[duc].animations["walk.up"] → Player/StoryWorldView |
| char.duc.idle.left | sprites/duc/idle_left.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[duc].animations["idle.left"] → Player/StoryWorldView |
| char.duc.walk.left | sprites/duc/walk_left.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[duc].animations["walk.left"] → Player/StoryWorldView |
| char.duc.idle.right | sprites/duc/idle_right.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[duc].animations["idle.right"] → Player/StoryWorldView |
| char.duc.walk.right | sprites/duc/walk_right.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[duc].animations["walk.right"] → Player/StoryWorldView |
| char.nam.idle.down | sprites/nam/idle_down.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[nam].animations["idle.down"] → Player/StoryWorldView |
| char.nam.walk.down | sprites/nam/walk_down.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[nam].animations["walk.down"] → Player/StoryWorldView |
| char.nam.idle.up | sprites/nam/idle_up.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[nam].animations["idle.up"] → Player/StoryWorldView |
| char.nam.walk.up | sprites/nam/walk_up.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[nam].animations["walk.up"] → Player/StoryWorldView |
| char.nam.idle.left | sprites/nam/idle_left.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[nam].animations["idle.left"] → Player/StoryWorldView |
| char.nam.walk.left | sprites/nam/walk_left.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[nam].animations["walk.left"] → Player/StoryWorldView |
| char.nam.idle.right | sprites/nam/idle_right.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[nam].animations["idle.right"] → Player/StoryWorldView |
| char.nam.walk.right | sprites/nam/walk_right.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[nam].animations["walk.right"] → Player/StoryWorldView |
| char.hung.idle.down | sprites/hung/idle_down.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[hung].animations["idle.down"] → Player/StoryWorldView |
| char.hung.walk.down | sprites/hung/walk_down.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[hung].animations["walk.down"] → Player/StoryWorldView |
| char.hung.idle.up | sprites/hung/idle_up.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[hung].animations["idle.up"] → Player/StoryWorldView |
| char.hung.walk.up | sprites/hung/walk_up.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[hung].animations["walk.up"] → Player/StoryWorldView |
| char.hung.idle.left | sprites/hung/idle_left.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[hung].animations["idle.left"] → Player/StoryWorldView |
| char.hung.walk.left | sprites/hung/walk_left.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[hung].animations["walk.left"] → Player/StoryWorldView |
| char.hung.idle.right | sprites/hung/idle_right.png | 32×48/frame; 2 frame; 2 FPS; 64×48; lặp | Thêm; texture | story-world.npcs[hung].animations["idle.right"] → Player/StoryWorldView |
| char.hung.walk.right | sprites/hung/walk_right.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[hung].animations["walk.right"] → Player/StoryWorldView |
| char.linh.pose.phone | sprites/linh/pose_phone.png | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối | Thêm; texture | story-world.player.animations["pose.phone"] → Player/StoryWorldView |
| char.linh.pose.book | sprites/linh/pose_book.png | 32×48/frame; 1 frame; 1 FPS; 32×48; một lần, giữ frame cuối | Thêm; texture | story-world.player.animations["pose.book"] → Player/StoryWorldView |
| char.linh.pose.seated | sprites/linh/pose_seated.png | 32×48/frame; 4 frame; 4 FPS; 128×48; một lần, giữ frame cuối | Thêm; texture | story-world.player.animations["pose.seated"] → Player/StoryWorldView |
| char.trang.pose.phone | sprites/trang/pose_phone.png | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối | Thêm; texture | story-world.npcs[trang].animations["pose.phone"] → Player/StoryWorldView |
| char.duc.pose.photo | sprites/duc/pose_photo.png | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối | Thêm; texture | story-world.npcs[duc].animations["pose.photo"] → Player/StoryWorldView |
| char.nam.pose.key | sprites/nam/pose_key.png | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối | Thêm; texture | story-world.npcs[nam].animations["pose.key"] → Player/StoryWorldView |
| char.nam.pose.gate | sprites/nam/pose_gate.png | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối | Thêm; texture | story-world.npcs[nam].animations["pose.gate"] → Player/StoryWorldView |
| char.hung.pose.write | sprites/hung/pose_write.png | 32×48/frame; 6 frame; 8 FPS; 192×48; một lần, giữ frame cuối | Thêm; texture | story-world.npcs[hung].animations["pose.write"] → Player/StoryWorldView |
| char.nga.idle.down | sprites/nga/idle_down.png | 32×48/frame; 1 frame; 1 FPS; 32×48; lặp | Thêm; texture | story-world.npcs[nga].animations["idle.down"] → Player/StoryWorldView |
| char.nga.idle.up | sprites/nga/idle_up.png | 32×48/frame; 1 frame; 1 FPS; 32×48; lặp | Thêm; texture | story-world.npcs[nga].animations["idle.up"] → Player/StoryWorldView |
| char.nga.walk.down | sprites/nga/walk_down.png | 32×48/frame; 6 frame; 8 FPS; 192×48; lặp | Thêm; texture | story-world.npcs[nga].animations["walk.down"] → Player/StoryWorldView |
| char.nga.pose.seated | sprites/nga/pose_seated.png | 32×48/frame; 1 frame; 1 FPS; 32×48; một lần, giữ frame cuối | Thêm; texture | story-world.npcs[nga].animations["pose.seated"] → Player/StoryWorldView |
| char.nga.pose.point | sprites/nga/pose_point.png | 32×48/frame; 4 frame; 8 FPS; 128×48; một lần, giữ frame cuối | Thêm; texture | story-world.npcs[nga].animations["pose.point"] → Player/StoryWorldView |

### Portrait

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| portrait.linh.normal | portraits/linh_normal.png | 150×175 PNG RGBA | Đã có; texture | content.speakers.linh.portraits.normal → DialogueSpeaker/DialogueUI |
| portrait.trang.normal | portraits/trang_normal.png | 150×175 PNG RGBA | Đã có; texture | content.speakers.trang.portraits.normal → DialogueSpeaker/DialogueUI |
| portrait.duc.normal | portraits/duc_normal.png | 150×175 PNG RGBA | Đã có; texture | content.speakers.duc.portraits.normal → DialogueSpeaker/DialogueUI |
| portrait.nam.normal | portraits/nam_normal.png | 150×175 PNG RGBA | Đã có; texture | content.speakers.nam.portraits.normal → DialogueSpeaker/DialogueUI |
| portrait.hung.normal | portraits/hung_normal.png | 150×175 PNG RGBA | Đã có; texture | content.speakers.hung.portraits.normal → DialogueSpeaker/DialogueUI |
| portrait.nga.normal | portraits/nga_normal.png | 150×175 PNG RGBA | Đã có; texture | content.speakers.nga.portraits.normal → DialogueSpeaker/DialogueUI |
| portrait.linh.worried | portraits/linh_worried.png | 150×175 PNG RGBA | Thêm; texture | content.speakers.linh.portraits.worried → DialogueSpeaker/DialogueUI |
| portrait.trang.worried | portraits/trang_worried.png | 150×175 PNG RGBA | Thêm; texture | content.speakers.trang.portraits.worried → DialogueSpeaker/DialogueUI |
| portrait.duc.grief | portraits/duc_grief.png | 150×175 PNG RGBA | Thêm; texture | content.speakers.duc.portraits.grief → DialogueSpeaker/DialogueUI |
| portrait.nam.grave | portraits/nam_grave.png | 150×175 PNG RGBA | Thêm; texture | content.speakers.nam.portraits.grave → DialogueSpeaker/DialogueUI |
| portrait.hung.remorse | portraits/hung_remorse.png | 150×175 PNG RGBA | Thêm; texture | content.speakers.hung.portraits.remorse → DialogueSpeaker/DialogueUI |
| portrait.nga.quiet | portraits/nga_quiet.png | 150×175 PNG RGBA | Thêm; texture | content.speakers.nga.portraits.quiet → DialogueSpeaker/DialogueUI |

### Môi trường

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| tileset.school | maps/tilesets/school.png | 512×256 PNG RGBA; tile 32×32; 16×8 ô | Dependency/nguồn; không cần entry riêng | school.tsx image source → TmxMapLoader/OrthogonalTiledMapRenderer |

### Bản đồ kỹ thuật

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| tileset.school.definition | maps/tilesets/school.tsx | TSX; tile 32×32; 128 ô; margin/spacing 0 | Dependency/nguồn; không cần entry riêng | TMX tileset source → TmxMapLoader |
| map.tmx.room_304 | maps/room_304.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Đã có; file | story-world.scenes[ROOM_304].tmx → StoryWorldView.enter/importGeometry |
| map.tmx.hallway | maps/hallway.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Đã có; file | story-world.scenes[HALLWAY].tmx → StoryWorldView.enter/importGeometry |
| map.tmx.guard_room | maps/guard_room.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Đã có; file | story-world.scenes[GUARD_ROOM].tmx → StoryWorldView.enter/importGeometry |
| map.tmx.classroom | maps/classroom.tmx | TMX finite orthogonal; 20×15 ô; 640×480 px | Đã có; file | story-world.scenes[CLASSROOM].tmx → StoryWorldView.enter/importGeometry |

### Đồ vật

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| prop.photo | sprites/props/photo.png | 54×34 PNG RGBA; 1 ảnh | Đã có; texture | story-world object ROOM_304/photo → StoryWorldView.propState/render |
| prop.register | sprites/props/register.png | 80×34 PNG RGBA; 1 ảnh | Đã có; texture | story-world object ROOM_304/desk:normal → StoryWorldView.propState/render |
| prop.desk.empty | sprites/props/desk_empty.png | 80×34 PNG RGBA; 1 ảnh | Thêm; texture | story-world object ROOM_304/desk:empty → StoryWorldView.propState/render |
| prop.desk.final | sprites/props/desk_final.png | 80×34 PNG RGBA; 1 ảnh | Thêm; texture | story-world object ROOM_304/desk:final → StoryWorldView.propState/render |
| prop.player | sprites/props/player.png | 54×28 PNG RGBA; 1 ảnh | Đã có; texture | story-world object ROOM_304/player:empty → StoryWorldView.propState/render |
| prop.player.powered | sprites/props/player_powered.png | 54×28 PNG RGBA; 1 ảnh | Thêm; texture | story-world object ROOM_304/player:powered → StoryWorldView.propState/render |
| prop.player.playing | sprites/props/player_playing.png | 54×28 PNG RGBA; 1 ảnh | Thêm; texture | story-world object ROOM_304/player:playing → StoryWorldView.propState/render |
| prop.player.open | sprites/props/player_open.png | 54×28 PNG RGBA; 1 ảnh | Thêm; texture | Chưa tự bind; presentation override tại StoryVisuals/StoryWorldView, xem bảng giới hạn |
| prop.crate | sprites/props/crate.png | 54×28 PNG RGBA; 1 ảnh | Đã có; texture | story-world object ROOM_304/crate:closed → StoryWorldView.propState/render |
| prop.crate.open | sprites/props/crate_open.png | 54×28 PNG RGBA; 1 ảnh | Thêm; texture | story-world object ROOM_304/crate:open → StoryWorldView.propState/render |
| prop.cabinet | sprites/props/cabinet.png | 46×22 PNG RGBA; 1 ảnh | Đã có; texture | story-world object ROOM_304/cabinet:closed → StoryWorldView.propState/render |
| prop.cabinet.locked | sprites/props/cabinet_locked.png | 46×22 PNG RGBA; 1 ảnh | Thêm; texture | story-world object ROOM_304/cabinet:locked → StoryWorldView.propState/render |
| prop.cabinet.open | sprites/props/cabinet_open.png | 46×22 PNG RGBA; 1 ảnh | Thêm; texture | Chưa tự bind; presentation override tại StoryVisuals/StoryWorldView, xem bảng giới hạn |
| prop.cabinet.upper | sprites/props/cabinet_upper.png | 46×20 PNG RGBA; 1 ảnh | Thêm; texture | story-world object ROOM_304/cabinet.upper → StoryWorldView.propState/render |
| prop.papers | sprites/props/papers.png | 70×32 PNG RGBA; 1 ảnh | Đã có; texture | story-world object GUARD_ROOM/records → StoryWorldView.propState/render |
| prop.lastdesk | sprites/props/lastdesk.png | 58×20 PNG RGBA; 1 ảnh | Thêm; texture | story-world object ROOM_304/lastdesk → StoryWorldView.propState/render |
| prop.window | sprites/props/window.png | 74×16 PNG RGBA; 1 ảnh | Thêm; texture | story-world object ROOM_304/window → StoryWorldView.propState/render |
| prop.board | sprites/props/board.png | 90×24 PNG RGBA; 1 ảnh | Thêm; texture | story-world object HALLWAY/board → StoryWorldView.propState/render |
| prop.guard.wall | sprites/props/guard_wall.png | 46×50 PNG RGBA; 1 ảnh | Thêm; texture | Chưa tự bind; presentation override tại StoryVisuals/StoryWorldView, xem bảng giới hạn |
| prop.guard.door | sprites/props/guard_door.png | 46×50 PNG RGBA; 1 ảnh | Thêm; texture | Chưa tự bind; presentation override tại StoryVisuals/StoryWorldView, xem bảng giới hạn |
| prop.gate.closed | sprites/props/gate_closed.png | 70×24 PNG RGBA; 1 ảnh | Thêm; texture | Chưa tự bind; presentation override tại StoryVisuals/StoryWorldView, xem bảng giới hạn |
| prop.gate.open | sprites/props/gate_open.png | 70×24 PNG RGBA; 1 ảnh | Thêm; texture | Chưa tự bind; presentation override tại StoryVisuals/StoryWorldView, xem bảng giới hạn |
| prop.evidence | sprites/props/evidence.png | 74×30 PNG RGBA; 1 ảnh | Thêm; texture | story-world object CLASSROOM/evidence:normal → StoryWorldView.propState/render |
| prop.evidence.signed | sprites/props/evidence_signed.png | 74×30 PNG RGBA; 1 ảnh | Thêm; texture | story-world object CLASSROOM/evidence:final → StoryWorldView.propState/render |
| prop.chair | sprites/props/chair.png | 22×9 PNG RGBA; 1 ảnh | Thêm; texture | story-visuals effect chair.asset → drawEffects |

### Hiệu ứng

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| effect.memory | effects/memory.png | 24×40 PNG RGBA; 1 ảnh | Đã có; texture | story-visuals A-01-MAIN-E03.effect=memory/asset → drawEffects native24×40 |

### Tài liệu

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| document.msg | documents/msg.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.documents[DOC-MSG] + lineDocuments → DialogueUI.present |
| document.photo | documents/photo.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.documents[DOC-PHOTO] + lineDocuments → DialogueUI.present |
| document.register | documents/register.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.documents[DOC-REGISTER] + lineDocuments → DialogueUI.present |
| document.news | documents/news.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.documents[DOC-NEWS] + lineDocuments → DialogueUI.present |
| document.gate | documents/gate.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.documents[DOC-GATE] + lineDocuments → DialogueUI.present |
| document.report | documents/report.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.documents[DOC-REPORT] + lineDocuments → DialogueUI.present |
| document.inventory | documents/inventory.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.documents[DOC-INVENTORY] + lineDocuments → DialogueUI.present |
| document.signed | documents/signed.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.documents[DOC-SIGNED] + lineDocuments → DialogueUI.present |
| document.family | documents/family.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.documents[DOC-FAMILY] + lineDocuments → DialogueUI.present |
| document.tape | documents/tape.png | 640×480 PNG; 1 ảnh | Thêm; texture | story-visuals.documents[DOC-TAPE] + lineDocuments → DialogueUI.present |
| document.battery | documents/battery.png | 640×480 PNG; 1 ảnh | Thêm; texture | story-visuals.documents[DOC-BATTERY] + lineDocuments → DialogueUI.present |
| document.c_phone | documents/trang_phone.png | 180×245 PNG RGBA; 1 ảnh | Đã có; texture | story-visuals C-02-MAIN-E11.effect=phone/asset → drawEffects180×245 |

### UI

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| ui.art.dialogue_frame | ui/source/dialogue_frame.9.png | 66×66 PNG RGBA, gồm viền 9-patch 1 px | Dependency/nguồn; không cần entry riêng | Đầu vào pack atlas; không nạp raw .9.png vào game |
| ui.art.panel_frame | ui/source/panel_frame.9.png | 34×34 PNG RGBA, gồm viền 9-patch 1 px | Dependency/nguồn; không cần entry riêng | Đầu vào pack atlas; không nạp raw .9.png vào game |
| ui.art.button_normal | ui/source/button_normal.9.png | 34×34 PNG RGBA, gồm viền 9-patch 1 px | Dependency/nguồn; không cần entry riêng | Đầu vào pack atlas; không nạp raw .9.png vào game |
| ui.art.button_focus | ui/source/button_focus.9.png | 34×34 PNG RGBA, gồm viền 9-patch 1 px | Dependency/nguồn; không cần entry riêng | Đầu vào pack atlas; không nạp raw .9.png vào game |
| ui.art.button_pressed | ui/source/button_pressed.9.png | 34×34 PNG RGBA, gồm viền 9-patch 1 px | Dependency/nguồn; không cần entry riêng | Đầu vào pack atlas; không nạp raw .9.png vào game |
| ui.icon.camera | ui/camera.png | 16×16 PNG RGBA; 1 ảnh | Thêm; texture | story-visuals effect camera.asset → drawEffects native16×16 |
| ui.icon.record | ui/record.png | 16×16 PNG RGBA; 1 ảnh | Thêm; texture | story-visuals effect record.asset → drawEffects native16×16 |

### UI kỹ thuật

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| ui.skin | ui/room304-skin.json | Skin JSON; default-font do game cấp | Đã có; file | GameAssets.optionalSkin → DialogueUI/DialogueHud/UiPanel |
| ui.skin.atlas | ui/room304-skin.atlas | TextureAtlas; split/pad từ 9-patch | Dependency/nguồn; không cần entry riêng | GameAssets.optionalSkin → DialogueUI/DialogueHud/UiPanel |
| ui.skin.page | ui/room304-skin.png | 256×128 PNG RGBA; Nearest; pack không rotate | Dependency/nguồn; không cần entry riêng | GameAssets.optionalSkin → DialogueUI/DialogueHud/UiPanel |

### Minh họa tùy chọn

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| background.menu | ui/menu.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.menus[MENU] → MenuScreen/UiPanel |
| background.ending.a | endings/a.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.menus[A] → MenuScreen/UiPanel |
| background.ending.b | endings/b.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.menus[B] → MenuScreen/UiPanel |
| background.ending.c | endings/c.png | 640×480 PNG; 1 ảnh | Đã có; texture | story-visuals.menus[C] → MenuScreen/UiPanel |

### Âm thanh

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| audio.school.birds | audio/ambience/school_birds.ogg | OGG Vorbis stereo 44,1 kHz; loop 20–40 s | Đã có; music | story-audio.ambience[ROOM/Phenomenon] → StoryAudio Music |
| audio.wind | audio/ambience/wind.ogg | OGG Vorbis stereo 44,1 kHz; loop 20–40 s | Thêm; music | story-audio.ambience[ROOM/Phenomenon] → StoryAudio Music |
| audio.fx.message | audio/sfx/message.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C0-02-MAIN-E01, C-02-MAIN-E11 |
| audio.fx.door_open | audio/sfx/door_open.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C0-03-MAIN-E07, C-02-MAIN-E05 |
| audio.fx.door_close | audio/sfx/door_close.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-E09 |
| audio.fx.footstep | audio/sfx/footstep.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.footsteps.asset → distance sau collision |
| audio.fx.page | audio/sfx/page.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C1-04-READ-E01, C6-01-GATHER-E10 |
| audio.fx.camera | audio/sfx/camera.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C1-02-SAVE-E01, C1-04-READ-E09, C2-03-DOC-E02, C3-05-SAVE-E01, C4-01-COUNT-E04, C4-02-TAKE-E01, C5-05-FULL-E11 |
| audio.fx.bell_strange | audio/sfx/bell_strange.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C1-04-HAUNT-E02 |
| audio.fx.bell_normal | audio/sfx/bell_normal.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue A-02-MAIN-E08 |
| audio.fx.chair | audio/sfx/chair.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C1-04-HAUNT-E03, C4-05-MAIN-E14 |
| audio.fx.knock_three | audio/sfx/knock_three.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C2-04-MAIN-E01 |
| audio.fx.knock_one | audio/sfx/knock_one.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C3-04-MAIN-E01 |
| audio.fx.lock | audio/sfx/lock.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C3-02-MAIN-E02, B-01-LOCK-E02 |
| audio.fx.unlock | audio/sfx/unlock.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C3-04-CONFIRM-E01 |
| audio.fx.key | audio/sfx/key.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C3-01-MAIN-E13, B-02-MAIN-E01 |
| audio.fx.key_rattle | audio/sfx/key_rattle.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue B-02-MAIN-E03 |
| audio.fx.gate | audio/sfx/gate.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C3-02-MAIN-E08, B-01-EXIT-E03 |
| audio.fx.chalk | audio/sfx/chalk.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C2-01-MAIN-E05 |
| audio.fx.button | audio/sfx/button.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-03-MAIN-E01, C4-05-END-E01, C5-04-MAIN-E06 |
| audio.fx.lid | audio/sfx/lid.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-03-END-E01, C4-05-MAIN-E01 |
| audio.fx.paper | audio/sfx/paper.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-04-MAIN-E03, C4-05-MAIN-E09, C5-05-FULL-E01 |
| audio.fx.tape_hiss | audio/sfx/tape_hiss.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-E05 |
| audio.fx.tape_stop | audio/sfx/tape_stop.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-E13 |
| audio.fx.alarm | audio/sfx/alarm.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-E10 |
| audio.fx.run | audio/sfx/run.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-E10 |
| audio.fx.write | audio/sfx/write.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C5-05-FULL-E07 |
| audio.voice.call | audio/voice/call.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C1-04-HAUNT-D06, C5-06-MAIN-D01, C6-01-GATHER-D11, B-02-MAIN-D04 |
| audio.voice.hung_assignment | audio/tape/hung_assignment.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-D06, C5-04-MAIN-D04 |
| audio.voice.nga_recorder | audio/tape/nga_recorder.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-D07, C5-04-MAIN-D05 |
| audio.voice.hung_finish | audio/tape/hung_finish.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-D08 |
| audio.voice.nga_help_1 | audio/tape/nga_help_1.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-D11 |
| audio.voice.nga_help_2 | audio/tape/nga_help_2.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C4-05-MAIN-D12 |
| audio.voice.nga_stayed | audio/voice/nga_stayed.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C5-04-CONFESS-D02 |
| audio.voice.nga_present | audio/voice/nga_present.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue A-01-MAIN-D02 |
| audio.voice.linh_present | audio/voice/linh_present.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C-02-MAIN-D01 |
| audio.voice.enough | audio/voice/enough.wav | WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Thêm; sound | story-audio.bindings → StoryAudio Sound; cue C-02-MAIN-D04 |

### Font có sẵn

| ID | Path | Quy cách | Manifest | Reference → consumer |
| --- | --- | --- | --- | --- |
| font.ui | fonts/NotoSans-Regular.ttf | TTF; font UI 18 px hiện tại | Đã có; file | font.ui + story/ui-layout.json.font → UiPanel/DialogueAssets |
| font.ui.license | fonts/OFL.txt | UTF-8; SIL OFL 1.1 | Dependency/nguồn; không cần entry riêng | Giấy phép đi kèm, không nạp runtime |

## 4. Quy trình nhập một đợt

1. Nhận file + tác giả/quyền sử dụng + spec được duyệt; giữ file nguồn, không ghi đè/xóa bộ demo. Đối chiếu FOR_ARTISTS và master; sai đề xuất đã duyệt thì báo chênh lệch, không tự co ảnh hoặc đoán frame.
2. Decode file kiểm PNG RGBA/kích thước/frame grid/alpha; WAV PCM16/44,1k hoặc OGG44,1k; nghe vòng loop. Kiểm PNG thật, không chỉ extension. Font nếu thay phải kiểm glyph Việt và OFL/license.
3. Đặt đúng path trong chỉ mục. Ghi checksum/batch version. Các ảnh có cùng ID không tạo alias trùng path.
4. Thêm/cập nhật manifest trước khi thêm reference; texture Nearest cho pixel art, type file cho TMX/Skin, sound cho clip, music cho ambience; required=false trong giai đoạn nhập dần. Không thêm raw 9-patch/atlas dependency vào loader texture lần nữa.
5. Nối đúng JSON profile/object/speaker/cue; giữ nguyên flags, roomID, collision, speed, điều kiện ending. Các hook còn thiếu chỉ chỉnh presentation nếu phạm vi công việc được giao cho phép; không giả vờ đã cấu hình được.
6. Kiểm toàn bộ ID/reference, frameCount≤cells và dimension chia hết frame. Với các sheet đã chốt ở đây yêu cầu đúng kích thước một hàng, không chỉ pass kiểm tra chia hết.
7. Khởi động lại app để bỏ cache. Build/core regression rồi desktop tests; xem/nghe thực tế đúng cue. Kiểm first-read và notes read lại, pause/resume, scene return, save/load.
8. Ghi kết quả/lỗi và cập nhật checkbox chỉ nhóm đã nghiệm thu. Nếu đổi spec, sửa master trước và đồng bộ tất cả tài liệu có nhắc lại.

## 5. Player và NPC animation

Ví dụ **đề xuất**, chỉ ghi sau khi đã nhận sheet đúng:

```json
{ "id":"char.linh.walk.down", "path":"sprites/linh/walk_down.png", "type":"texture", "required":false, "filter":"Nearest", "frameWidth":32, "frameHeight":48, "frameCount":6, "frameDuration":0.125 }
```

Trong `config/story-world.json`, đặt `player.animations["walk.down"]="char.linh.walk.down"`; các key còn lại dùng chính bảng trên. NPC dùng `npcs[]` tìm theo id rồi sửa animations, không dựa chỉ số. Một frame vẫn có frameWidth/Height/count1 và duration dương (bộ đề xuất1s). Giữ canvas32×48/scale1/anchor(.5,0); vùng chân18×10 độc lập. Sprite position=`x+footWidth/2−canvasWidth*scale*anchorX, y−canvasHeight*scale*anchorY`. X/Y là góc footprint, không tâm hình.

Profile không tự flip trái/phải. Idle theo hướng thực; thiếu pose chọn idle hướng rồi idle.down; thiếu hình hoàn toàn dùng avatar hình học. `pose.*` trong WorldView chơi một lần giữ frame cuối; muốn môi Linh C loop liên tục cần chỉnh đúng policy presentation, không giả định Animation mặc định LOOP đã đủ. NPC walk P2 chưa có lộ trình; placements chuyển vị trí qua cảnh không tự animate đi. Nếu muốn đi ở nhịp cụ thể, dùng actor cue to/seconds, khóa input hiện có; không tạo AI theo đuôi.

`config/story-visuals.json.cues` đã bind phone/book/write/seated/point, Nga đi A; thêm sheet + profile là đủ cho các cue đó. Pose Đức photo và bà Năm key/gate cần thêm cue tại nhịp trao ảnh/chìa/mở cổng từ sourceRows. Actor{id,pose,anchor,dx,dy,alpha,hidden,to,seconds}; anchor object ID hoặc npc:<id>, room bắt buộc khi có anchor. Overrides giữ trong block, clear/reset đúng nhịp. Không thay vị trí player vật lý hoặc save để khớp art.

## 6. Portrait và biểu cảm

**Field thật là `speakers.<id>.portraits`**, không phải expressions. `game/assets/story/content.json` hiện chỉ normal cho6 người; các dòng có speakerId/expression. Thêm biểu cảm PROPOSED vào portraits, chỉ đổi expression ở dòng phù hợp; không đổi text/choices/sourceRows. Speaker call/nam_door/hung_tape/nga_tape không được tự gắn portrait người đang đứng trong phòng.

```json
"linh": { "name":"Linh", "portraits": { "normal":"portrait.linh.normal", "worried":"portrait.linh.worried" } }
```

DialogueSpeaker.getPortrait fallback normal. Field override đúng: portraitCenterX,portraitBottom,portraitHeight,portraitMaxWidth. Mặc định từ story/ui-layout.json:320, 70, 350, 300; portrait150×175 fit2×. Manifest hiện Linear, chuyển Nearest khi duyệt pixel portrait. Thiếu hình thì chỉ tên/chữ, fallbackPortrait đang rỗng.

## 7. Map/background/props

Phương án chính4 TMX + tileset school 512×256/TSX,32px tile,20×15. Không sản xuất thêm4PNG cảnh. Entry map.tmx đã có; TmxMapLoader nạp file nếu tồn tại và TMX ưu tiên nềnPNG. Các slot PNG dự phòng: map.story.room_304 → maps/room_304.png; hallway → maps/hallway.png; guard_room → maps/guard_room.png; classroom → maps/classroom.png. Nếu nhóm chọn đường PNG thay thế, xuất640×480 PROPOSED, dùng scene.background/backgrounds, giữ JSON geometry; đừng âm thầm đổi phương án sản xuất.

TMX layer cấp map: ground/decor tile; collisions rectangle; spawns point/rectangle có name; interactables rectangle có id hoặc name; optional doors tách cửa; optional npcs point. Tất cả logical objects/spawns dưới đây phải có, không thêm object mới chỉ trong map. Loader đổi Y, không đổi lần nữa. targetRoom/spawnId chỉ override khi hợp lệ, không vượt StorySession gate.

| Room | Spawns | Objects |
| --- | --- | --- |
| ROOM_304 | entry, desk, cabinet, family | door.hall, photo, desk, player, crate, cabinet, cabinet.upper, lastdesk, window |
| HALLWAY | entry, room304, hung, guard, chalk | door.304, door.hung, stairs, guard.wall, board |
| GUARD_ROOM | entry, gate, outside | door.hall, records, gate |
| CLASSROOM | entry, table | door.hall, evidence |

Tiled rectangle y=480−yRuntime−h; point y=480−yRuntime. Collision từ rectangle riêng, không suy alpha; kiểm npcSlots và placements.x/y overrides sau đổi geometry. Cổng outside nằm cùng GUARD_ROOM. Map cache theo lượt; pack đầy đủ TSX/PNG relative paths. Hiện renderer.render() vẽ tất cả tile layers **trước actor**: foreground thật cần pass sau actor. Không cho artist bake vật động vào ground/decor.

Props texture tĩnh vẽ vừa object x/y/w/h; bảng master dùng đúng bounds hiện tại để tránh méo. `variants` có keys normal/open/closed/locked/empty/powered/playing/carried/final nhưng **chỉ những key propState trả về mới được sử dụng**.

| Object | Mapping đề xuất đủ bằng config |
| --- | --- |
| ROOM_304/desk | normal→prop.register; empty→prop.desk.empty; final→prop.desk.final |
| ROOM_304/player | empty→prop.player; powered→prop.player.powered; playing→prop.player.playing |
| ROOM_304/crate | closed→prop.crate; open→prop.crate.open |
| ROOM_304/cabinet | closed→prop.cabinet; locked→prop.cabinet.locked |
| CLASSROOM/evidence | normal→prop.evidence; final→prop.evidence.signed |
| photo/papers và các static còn lại | asset tương ứng theo chỉ mục; cabinet.upper,lastdesk,window,board thêm asset trên object hiện có. |

prop.chair nối cue effect chair.asset tại C1-04-HAUNT-E03/C4-05-MAIN-E14, vẽ native22×9. PROP_CUE chưa được chọn bằng config hiện hành: xem bảng giới hạn §12. Không dùng variant open để giả vờ máy/tủ có animation tự động; ở runtime state chỉ đổi sau giao dịch thoại ở nhiều nhịp.

## 8. Tài liệu, điện thoại và CG

story-visuals.documents đang có9 DOC-prefix; lineDocuments43 line đã nối đọc lần đầu (kể cả hậu tố __n); StorySession.read gán ID DOC theo text nguồn để notes/xem lại dùng chung. Giữ các mapping sẵn có. Thêm document.tape với DOC-TAPE và line C4-02-MAIN-T01 (ghi đè lookup chỉ dòng nhãn băng, giữ các dòng __n kiểm kê); thêm document.battery với DOC-BATTERY và C4-03-MAIN-T03. DOC-EQUIP không thêm ảnh: nội dung nhãn nằm trong prop.crate.

GameScreen truyền documentBackground vào DialogueUI.present, Scaling.fit640×480. line.background/conversation.background nếu có được ưu tiên. Chữ gốc vẫn render/phân trang; không xóa text để dùng chữ trên PNG. Ảnh 33 người, sổ 2 mực, giấy 32 người, bản ký 2025/Hùng, ảnh gia đình Nga/Đức phải giữ đúng nghĩa. Overlay tên Linh là hiện tượng, không sửa document.photo/register/signed.

C-phone dùng config cue C-02-MAIN-E11.effect=phone,asset=document.c_phone; renderer **stretch180×245**, xuất đúng kích thước này. Chữ C vẫn là TXT12/13, không bake vào ảnh lần nữa. effect.memory24×40 dùng A-01-MAIN-E03, vẽ3bản alpha/move; không spritesheet. Hiện có pose/phone C nhưng chưa có shot camera góc Trang, xem §12.

CG 4 ảnh optional đúng chỉ mục640×480; config menus MENU/A/B/C đã có ID. MenuScreen→UiPanel.background vẽ vào canvas; không có file CG vẫn chạy chữ/fallback. Không cần tranh cutscene mới ngoài nội dung trên.

## 9. Skin

Pack5 raw 9-patch vào ui/room304-skin.atlas + ui/room304-skin.png (PROPOSED256×128), create ui/room304-skin.json. Repo không có task pack/gdx-tools; dùng công cụ phù hợp có sẵn, kiểm split/pad, padding2,no rotate/trim,Nearest. Atlas tự strip viền1px .9.png; đừng nạp raw như texture rồi kéo méo góc. File generated khác kích thước đề xuất phải cập nhật spec.

Style names: LabelStyle default/dialogue/speaker/hud; TextButtonStyle default/choice/hud, font=default-font. up=button_normal,checked/over=button_focus,down=button_pressed. Drawables dialogue-frame→dialogue_frame; menu-frame/hud-frame→panel_frame. Dùng NinePatch từ atlas splits, alias resource thay vì duplicate path. **Không khai BitmapFont default-font**: GameAssets.optionalSkin gắn font Việt của view. Atlas cache dùng chung, không tự dispose texture.

Không có nameplate/document-frame hook độc lập; tên dùng LabelStyle speaker, tài liệu dùng ảnh+chữ. Không cần vẽ tất cả nút/text/vector. Camera/record là texture16×16 riêng, thêm asset vào cue effect đúng loại. Nếu cần layout mới ngoài bounds config thì sửa riêng DialogueUI/DialogueHud/UiPanel, giữ callbacks/input; không tạo DialogueUI thứ hai.

## 10. Audio

37 file và cue đầy đủ ở chỉ mục. story-audio.json hiện trống; thêm entry music/sound trong manifest rồi bind. Mẫu minh họa, không ghi nguyên nếu batch chưa có asset:

```json
{ "version":1, "ambience":{"ROOM_304/NORMAL":"audio.school.birds","ROOM_304/ACTIVE":"audio.wind"}, "bindings":[{"cue":"C4-05-MAIN-D06","asset":"audio.voice.hung_assignment","channel":"voice","loop":false,"decorative":false}], "footsteps":{"asset":"audio.fx.footstep","distance":45} }
```

Ambience có thể khai cho cả4 room/NORMAL/ACTIVE/SUSPENDED/RESOLVED theo nguồn. Một Music duy nhất, school_birds là mix sẵn chim+trường xa; slot audio.school.voices hiện bỏ không, không layer tự động. Footsteps45px PROPOSED; code nhận displacement sau collision, không phím giữ/pause/teleport.

Sound theo lineID dừng khi đổi line; nhiều asset trên cùng cue được, trùng asset/cue bị từ chối. channel voice/fx, decorative chỉfx. C4-05 đủ5 lời băng, C5-04 **chỉ hai lời đầu** D04/D05. Không phát đoạn kêu cứu lại tại C5. FX autobeat ngắn phải trim hợp lý; không đổi thời lượng/gate để theo một file dài. Chữ vẫn chờ Enter sau khi clip hết. MIX-01 nền25%,decorativeFX40%, hồi.25s; pause/resume theo instance, chưa test clip thật. Mở màn/beforebook không thêm giọng gọi/bước chân thứ ba; C mấp máy không tiếng. Giọng/menu nhạc ngoài danh mục chưa có yêu cầu, không tự thêm.

## 11. Không làm hỏng gameplay

Không đổi GameState, story flags, nội dung/choices/sourceRows, điều kiện A/B/C, thứ tự callback/transaction để khớp ảnh. Không suy collision từ canvas; không sửa speed/footprint khi thay sprite. Không hardcode path Java nếu manifest/config có hook. Không tạo manager thứ hai, không entity.dispose texture mượn; không bỏ fallback optional. Save không chứa texture/animation, không tăng version snapshot chỉ vì thay PNG. Với geometry mới, kiểm vị trí load bằng cùng SceneCatalog; không tự dịch save người chơi về (0,0).

## 12. Những phần chưa thể hoàn thiện chỉ bằng thay file

Các điểm dưới đây **đã thấy trong source**, không phải yêu cầu refactor chung. Nhiệm vụ hiện tại chỉ ghi nhận, chưa sửa code. Khi được giao tích hợp, giải quyết phần presentation có liên quan rồi kiểm thử; nếu phạm vi chỉ nhận ảnh, báo PARTIAL thay vì DONE.

| Hạng mục | Hiện trạng | Vị trí / cách tiếp tục | Kiểm chứng |
| --- | --- | --- | --- |
| Ánh lạnh trên TMX | Màu fallback bị tile kín che; background variants chỉ áp dụng PNG. | world/StoryWorldView.render/updateVisuals; thêm tint/fade presentation theo nguồn, không đổi phenomenon. | C1-04 lạnh dần .2–.4s; khóa sổ trở lại thường; chữ/lối đi rõ. |
| Phấn hành lang | Nét geometry hiện vẽ trước map/PNG, có thể bị nền kín che. | world/StoryWorldView.render: đưa nét qua overlay sau nền. | Đánh dấu C2-01 rồi vòng lại vẫn thấy đúng vạch; save/load giữ theo event. |
| Foreground | renderer.render() hiện vẽ mọi tile layer trước actor. | world/StoryWorldView.render: chọn layer ground/decor trước, foreground sau actor nếu art cần. | Đi trước/sau vật không sai che khuất; không đổi collision. |
| Tường thành cửa / cổng / mở khay-ngăn | propState chỉ hỗ trợ một số stable variants; guard.wall hiện chỉ đổi nhãn. | world/StoryVisuals + StoryWorldView: presentation override cho prop cue; giữ selector/story gates, không thêm story flags chỉ để đổi hình. | Đúng C2-04, C3-02, mở lại sổ, máy thiếu pin, B khóa thật; không mở ảnh trước mốc. |
| Cue chưa đúng hình nguồn | C5-05-FULL-E01 và C-02-MAIN-E02 đang có effect camera dù nguồn lần lượt đặt tư liệu và tên Linh hiện rõ. | config/story-visuals.json: chỉnh presentation cue khi import theo sourceRows; không gắn tiếng shutter chỉ vì cue hiện có là camera. | C5-E01 giấy/đặt đồ; C-E02 tên hiện, không cảnh Linh chụp ảnh tự phát. |
| Di chuyển NPC / pose thêm | Walk keys được đọc nhưng placements không tạo đường đi; photo/key/gate chưa có cue. | config/story-visuals.json actors/to/seconds; thêm pose binding đúng nhịp; chưa cần follow AI. | Không teleport trong khung đang phải đi; không nhân đôi NPC/vật chứng. |
| Môi Linh C / góc Trang | pose.seated chơi một lần; camera hiện một góc top-down. | world/StoryWorldView.drawActor/render + StoryVisuals presentation nếu cần loop/shot; asset C sẵn không tự đổi camera. | C im tiếng, Linh bị giữ chứ không chết, góc phía Trang nhìn vào lớp đúng nguồn. |
| Vạch âm ghi | Icon/chữ recording có; chưa có waveform chuyển động. | world/StoryWorldView.drawEffects: geometry theo nhịp băng; không cần art waveform mới. | Chạy/dừng đúng băng/ghi âm, pause giữ, không phụ thuộc volume để mở gate. |
| Skin raw 9-patch | Chưa có task pack/dependency gdx-tools trong build. | Dùng công cụ pack tương thích sẵn có; không thêm dependency runtime không cần thiết. | Atlas split/pad thật, styles render đúng góc ở nút38px. |
| Menu audio riêng / nhiều ambience stem | StoryAudio phục vụ GameScreen và một Music ambience. | Nếu có yêu cầu mới mới nối MenuScreen/mixer; bộ hiện tại dùng school mix sẵn. | Không phát chồng/leak sau menu/replay. |

## 13. Build và nghiệm thu

Từ root repository, dùng môi trường trong CODEX/README (Oracle JDK21.0.11 quy định; không âm thầm sửa version/guard). Các task dưới đây đã xác minh tồn tại trong build.gradle, **không phải kết quả chạy của lần viết tài liệu**. `build` kéo `:core:regressionTest` qua check; không coi `test NO-SOURCE` là đã chạy regression.

```powershell
$env:GRADLE_USER_HOME = "$PWD\.gradle-user-home"
.\game\gradlew.bat -p game --offline --no-daemon -g .gradle-user-home build --console=plain
# Chỉ tiếp tục khi LASTEXITCODE = 0
.\game\gradlew.bat -p game --offline --no-daemon -g .gradle-user-home :lwjgl3:desktopRegressionTest :lwjgl3:storyDesktopTest --console=plain
node game/tools/check-story-source.mjs
# Chỉ mở game sau build thành công; cần OpenGL/audio backend thật
.\game\gradlew.bat -p game --offline --no-daemon -g .gradle-user-home :lwjgl3:run
```

Nếu cache chưa có, bỏ --offline theo quyền truy cập môi trường; không đổi dependency để né lỗi môi trường. Đọc exit code từng lệnh; Gradle success không chứng minh desktop task đã chạy hoặc đã nghe audio. Khi đóng gói chạy working directory chứa assets; Gradle run đặt tại game/assets. Save ở saves/room304.json tương đối working directory, snapshot1. Không dùng save người dùng làm fixture/xóa save hiện có.

- [ ] File load đúng ID/case, optional thiếu fallback, file hỏng báo rõ; atlas/TMX dependency đóng gói đủ.
- [ ] 4 hướng walk/idle, pivot không rung, pose một lần giữ cuối, ngồi không đổi chân; đứng đụng tường không chạy chân/footstep giả.
- [ ] Tất cả cửa/spawn/NPC footprints/scene return; tile Y/scale và foreground đúng; không đổi geometry theo alpha.
- [ ] Portrait biểu cảm + fallback tên; tài liệu đọc lần đầu và notes read lại, đúng ảnh/không lặp chữ.
- [ ] UI 640×480,1280×960,800×600,960×540,1280×720; choices/notes/pause resize không cắt chữ Việt/nút.
- [ ] Nghe clip thật: loop/cue/duck, không clipping, pause/volume0, đổi line/màn/replay không rò âm.
- [ ] Lưu/tải tại EXP sau di chuyển/đổi phòng/nhận vật, không chạy lại event đã xong; save lỗi/khác version bị xử lý đúng.
- [ ] A: ảnh+sổ+bản ghi/báo cáo+băng đã nghe+Hùng nhận+giấy ký+cảnh báo, đặt sổ FINAL_TABLE ở304, chọn chờ Nga; giữ tư liệu, không tha thứ thay nhân vật.
- [ ] B: sau thử khóa/gặp Hùng; thử rút lui sớm và muộn, cất/khóa thật, cả nhóm ngoài cổng rồi trả chìa; tiếng gọi cuối không quay lại gameplay.
- [ ] C: test bản riêng enableC=true; hủy xác nhận không đổi state; xác nhận thì Linh ở lại/phone đúng; default phát hành vẫnfalse trừ chỉ đạo khác.
- [ ] Thứ tự điều tra khác nhau (băng trước hồ sơ, ảnh gia đình/nội san bỏ qua), thiếu mỗi điều kiện không chốt ending.
- [ ] Giữ E/Enter không callback kép; scene pause khóa input; replay/newgame không sót pose/âm/event.

Tests tham chiếu: game/core/src/regression/java/com/room304/RegressionChecks.java; story/FoundationChecks.java, StoryRuntimeChecks.java, PresentationChecks.java; game/lwjgl3/src/regression/java/com/room304/lwjgl3/GameplayRegressionChecks.java, StoryDesktopChecks.java. Desktop fixture trước đây không có tileset/art/audio final; phải kiểm asset mới thật. Không viết “đã chơi bằng tay” nếu chỉ chạy script.

## 14. Bàn giao sau mỗi nhóm

Ghi batch/ngày, ID/path, spec được duyệt, checksum/nguồn/license; entry manifest và reference config đã đổi; source presentation nào buộc sửa và vì sao; test/lệnh/exit code, đoạn chơi và kích thước cửa sổ thực sự quan sát; lỗi còn mở. Cập nhật master và07checklist, FOR_ARTISTS nếu đổi kích thước; cập nhật DEVELOPMENT_PROGRESS.md/ASSET_INTEGRATION_GUIDE.md trong **nhiệm vụ import sau này** khi có thay đổi thực. Không ghi DONE chỉ vì file đã được chép hoặc build qua.

Trong lần soạn đặc tả này chỉ thay ASSET_SPEC; các đề xuất chưa import. Tên field đúng là portraits/portraitHeight..., điện thoại C stretch180×245, đoạn C5 phát lại2 lời. Những khác biệt với root guide được ghi ở08 để agent sau không làm theo mô tả cũ.

Ghi chú thứ tự vẽ: props hiện vẽ trước toàn bộ actor; chỉ các actor được sắp theo Y. Nếu art cao cần che nhân vật ở phía sau, xử lý thứ tự props/actor trong StoryWorldView, hoặc giữ phần che ở pass foreground đã tách; đừng sửa collision để che lỗi hình.
