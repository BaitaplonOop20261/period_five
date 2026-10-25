# Room304 — Tích hợp asset và bàn giao runtime

Cập nhật 17/10/2026. Đọc CODEX.md, AGENTS/AGENTS.md, AGENTS/SDD.md và AGENTS/kich-ban-va-flow-can-phong-304.md trước khi sửa. Kịch bản 1.0 là nguồn chữ/nhịp/nhánh. Tài liệu này mô tả code hiện tại, không thay nguồn thiết kế.

**Ảnh tương tác 17/10:** 24 PNG gốc từ `ASSET_SPEC/download` đã được chép nguyên vào `game/assets/ui/inspection/`. Manifest khai báo `inspection.*` là texture required, lọc Nearest. Mỗi object trong `story-world.json` có `inspection` và có thể có `inspectionVariants`, độc lập với `asset`/`variants` dùng để vẽ trên bản đồ. `GameScreen` giữ object được chọn bằng E trong chuỗi tương tác; `StoryWorldView.inspectionAsset` chọn ảnh theo tiến độ; `DialogueUI` vẽ lớp tối 78%, ảnh Scaling.fit và lời thoại/lựa chọn. Không dùng kích thước ảnh cận cảnh làm hitbox. Xem [bảng file, hành vi và kiểm thử](ASSET_SPEC/10_INTERACTION_CLOSEUPS_2026-10-17.md).

**Tỉ lệ phòng học 17/10:** `ROOM_304` và `CLASSROOM` hiện có world 320×240, geometry/đồ vật giảm 50% mỗi chiều. Texture phòng 304 vẫn 640×480; renderer dùng Nearest để vẽ vào bounds mới. Profile sprite/footprint/tốc độ nhân vật giữ nguyên. Nhãn, hiệu ứng và offset pose theo tỉ lệ scene để khớp bàn/cửa; ảnh UI cận cảnh dùng layout riêng. Các tọa độ 640×480 trong biên bản 09 là lịch sử trước lần thu nhỏ này; xem [biên bản 11](ASSET_SPEC/11_CLASSROOM_SCALE_2026-10-17.md) khi đặt thêm asset/TMX.

**Dấu tương tác 17/10:** nhãn tên trong world và chữ E nổi được thay bằng animation `ui.interaction` từ `added_asset/interaction_mark.png`, runtime `ui/interaction_mark.png`. `InteractionCueRenderer` vẽ dấu ở mục tiêu selector đang chọn và viền vàng cho object; không cấp state hoặc đổi target. `highlightOutline` trong object cấu hình contour chuẩn hóa theo bounds cho đồ vật có chân bàn; mặc định là viền chữ nhật. Marker/viền ẩn trong các UI chặn input. Xem [biên bản 12](ASSET_SPEC/12_INTERACTION_CUES_2026-10-17.md).

**Va chạm sát tường 17/10:** phòng 304 đã chặn kín phía sau bục/bàn giáo viên, bàn cạnh cửa sổ và toàn bộ tủ. Tường sau bắt đầu ở y=192; không để vùng chân lọt vào phần trên đồ vật. Trường object `interaction` tùy chọn giữ một Box chọn E riêng cho cửa sổ/hai ngăn tủ; ảnh, viền và marker vẫn bám bounds hình thật. Khi import TMX, vùng chọn này được dời/scale theo object. Xem [biên bản 13](ASSET_SPEC/13_WALL_FURNITURE_COLLISION_2026-10-17.md).

**Camera 17/10:** world viewport cố định 320×240, không đổi theo kích thước scene. `FollowCamera` theo tâm sprite Linh và clamp theo hai mép mỗi trục; map nhỏ hơn khung nhìn được căn giữa. HUD dùng viewport 640×480 riêng để chữ/điện thoại/icon ghi âm không trôi theo world. Viền vàng giữ độ dày theo khung nhìn, marker giới hạn trong vùng camera đang thấy. Không thu nhỏ geometry map rộng. Demo sân sau dùng chung camera với khung 1920×1440 world unit ở art scale ×6, tương đương 320×240. Xem [biên bản 14](ASSET_SPEC/14_FOLLOW_CAMERA_2026-10-17.md).

**Đợt nhập thử 13/10:** năm idle 32×32 và nền PNG phòng 304 chỉnh từ ảnh người dùng đã được nối vào manifest/profile/runtime. Quy cách nhân vật khác đề xuất art cuối: học sinh 6 frame/0,15 giây; bà Năm/Hùng 1 frame tĩnh. Nền phòng đã được vẽ lại theo artstyle nguồn, xuất PNG RGBA kín 640×480 ở 1×, lọc Nearest; bố cục tám bàn và geometry đã nhập giữ nguyên. Bảng prototype và kích thước đề xuất 11/10 bên dưới cần đối chiếu với [biên bản 09](ASSET_SPEC/09_IMPORTED_ASSETS_2026-10-13.md) trước khi nhận thêm prop/TMX. Build/core/desktop qua trên Windows/Temurin 21.0.12.1, gồm 58 tương tác E và A/B/C/save-load; bản cuối khóa kiểm tra kích thước 640×480 và Nearest. Giữ asset thiếu optional/fallback; không coi bộ thử là art/audio cuối.

Game hiện chạy bốn khu vực bằng geometry/chữ và chơi tới A/B/C. C mặc định ẩn bằng enableC=false. Không có art/audio cuối, không tuyên bố đã nghiệm thu nghe nhìn. Save được bổ sung theo yêu cầu mới của người dùng, thay quy định không lưu của bản kịch bản. Lưu tại mốc khám phá; không ghi giữa giao dịch hội thoại/cutscene.

## A. Kiến trúc và ownership

Luồng chính:

1. game/core/src/main/java/com/room304/Room304Game.java tạo một GameAssets, đọc StoryContent và SceneCatalog, validate references, tạo SaveStore và AudioSettings.
2. MenuScreen → GameScreen → trang ending trong MenuScreen. Callback chỉ yêu cầu đổi screen; app đổi và dispose screen trước ở cuối frame. Replay có xem/bỏ C0; New Game từ menu luôn xem C0.
3. GameScreen sở hữu Player, StorySession, StoryWorldView, DialogueUI, UiPanel, StoryAudio. GameState nằm duy nhất trong StoryDirector của session; renderer/UI không có bản state riêng. NPC/quest/objective/evidence suy ra từ state, không có QuestManager/inventory thứ hai.
4. StorySession.interact nhận ID do selector chọn, kiểm tra room/presence, mở dialogue hoặc chuyển room. StoryDirector kiểm tra gate và hoàn tất action một lần bằng ticket. Callback ký luôn nối warning, không cho khám phá ở khoảng giữa. Text/document/choice lấy từ story/content.json.
5. Player collision dùng footprint độc lập sprite. Animation lấy metadata manifest; khi chưa cấu hình animation, renderer vẽ avatar hình học với hướng nhìn. NPC placement lấy npcStage + event trong story-world.json; optional x/y theo stage ghi đè slot (Đức ở bàn cuối, nhóm ending đứng phía ngoài cổng).

Asset flow: manifest ID → AssetCatalog.Entry → GameAssets/AssetManager → Texture hoặc Animation<TextureRegion> → WorldView/Player/DialogueUI. Portrait lấy speakerId/expression từ nội dung → DialogueController → DialogueUI.present. UI thiếu portrait chỉ hiện speaker/chữ, không cần ảnh giả.

- assets/AssetCatalog.java: version/type/path/duplicate/grid validation. Internal path tính từ game/assets, dấu /, không traversal hoặc absolute path.
- assets/GameAssets.java: texture/music/sound và atlas Skin cache bởi một AssetManager cấp app. Optional thiếu trả null/log ID và path; required thiếu báo lỗi trước menu. File optional tồn tại nhưng hỏng báo lỗi rõ; không nuốt lỗi định dạng thành cảnh sai.
- StoryWorldView sở hữu TMX và renderer. TMX nạp qua LibGDX TmxMapLoader, cache từng room trong một lượt, không nạp lại texture/map mỗi lần đi qua cửa. Renderer được thay khi chuyển room; map cache dispose cùng màn chơi. PNG/prop/NPC texture mượn manager app, không dispose bởi entity.
- DialogueUI/UiPanel sở hữu Stage, font FreeType và chrome pixel/gradient kỹ thuật. Optional Skin sở hữu styles; atlas mượn manager. default-font của Skin mượn font Việt của view và được tháo trước Skin.dispose. App dispose manager sau screen cuối.
- StoryAudio mượn Music/Sound, stop clip khi đổi nhịp/screen. Thiếu audio không ảnh hưởng thời gian/gate/story. Pause giữ nhịp, typewriter, vị trí và clip. MIX-01 nền 25%, FX decorative 40%, hồi nền trong 0,25 giây; voice không duck theo FX.
- Asset lớn hiện nạp đồng bộ trên transition/update. Chưa có async loading screen; nếu asset thực gây giật, thêm màn loading dùng AssetManager.update, giữ cùng ownership.

Các file demo WorldMap/HallwayMap/BackyardMap/NpcDefinition/Wardrobe được giữ cho regression lịch sử; OpeningScreen cũ còn trong source nhưng không được app gọi. DemoGameScreen nằm tại game/lwjgl3/src/regression/java/com/room304/screen/DemoGameScreen.java. Runtime app không đi 305/306/sân sau; không ghép tủ inventory demo vào truyện.

## B. Thư mục và cấu hình

| Đường dẫn | Vai trò |
|---|---|
| game/assets/config/asset-manifest.json | ID → path/type/filter/required và frame metadata. Font, dữ liệu story, dấu tương tác và 24 ảnh inspection là required; legacy art optional. |
| game/assets/config/story-world.json | Bốn RoomId, geometry, spawn, collision, object IDs, target/spawn của cửa, NPC IDs/placements, player speed/canvas/footprint/animation, enableC. |
| game/assets/story/content.json | 99 khối; speakers, strings, choices, dialogues, route targets, sourceRows visual/audio/wait/effect. Thoại/tài liệu/subtitle nguyên văn. |
| game/assets/story/ui-layout.json | Font, canvas 640×480, pagination, portrait bounds, choice/control sizes. fallbackPortrait rỗng. |
| game/assets/config/story-visuals.json | 25 cue bindings, document/line lookup, menu/ending ảnh; không chứa story actions. |
| game/assets/config/story-audio.json | version 1; ambience map và bindings theo line/cue ID. Mặc định rỗng vì chưa có clip. |
| game/assets/maps/ | PNG demo giữ nguyên; bốn PNG/TMX truyện có path trong bảng C. TSX/tilesets có thể đặt maps/tilesets/. |
| game/assets/sprites/ | Giữ bộ demo; thêm sprites/linh/, sprites/trang/, sprites/duc/, sprites/nam/, sprites/hung/, sprites/nga/, sprites/props/ khi nhận file. ID mới do manifest; không cần di chuyển file cũ. |
| game/assets/portraits/ | PNG trong suốt: linh_normal.png…; biểu cảm có thể thêm <id>_<expression>.png và speaker.expressions. |
| game/assets/fonts/ | NotoSans-Regular.ttf đang có. Đổi font.ui và font trong story/ui-layout.json cùng lúc. |
| game/assets/ui/ | Optional room304-skin.json, room304-skin.atlas và PNG atlas. Missing → Scene2D fallback. |
| game/assets/audio/ | ambience/, sfx/, voice/, tape/; dùng tên không dấu, metadata và binding phải khớp clip nhận được. |
| game/assets/saves/room304.json | Khi chạy bằng Gradle, local save nằm ở đây; đã ignore Git. Khi chạy JAR, saves/ tương đối working directory. Chưa có installer chuyển save sang user-profile. |

Quy tắc: lowercase ASCII, /, ID ổn định độc lập tên hiển thị. Đổi tên file trong manifest; không rải path sang screen. Không đổi enum RoomId chỉ vì tên file map khác.

## C. Asset thực tế và phần thiếu

Bảng này là manifest hiện tại. Có file không đồng nghĩa asset nghe nhìn cuối đã được nghiệm thu. Toàn bộ optional thiếu được gameplay thay bằng geometry/chữ/im lặng; chỉ bộ demo có grid/frame đã đo.

| Asset | Trạng thái | Đường dẫn dự kiến/thực tế | Yêu cầu kỹ thuật | Chức năng |
|---|---|---|---|---|
| map.demo.room | Có, asset demo giữ nguyên | game/assets/maps/bedroom_background.png | PNG; kích thước bộ cuối chưa chốt | Regression demo |
| map.demo.hall | Có, asset demo giữ nguyên | game/assets/maps/hall_background.png | PNG; kích thước bộ cuối chưa chốt | Regression demo |
| map.demo.yard | Có, asset demo giữ nguyên | game/assets/maps/back_yard.png | PNG; kích thước bộ cuối chưa chốt | Regression demo |
| prop.demo.bed | Có, asset demo giữ nguyên | game/assets/sprites/bed.png | PNG; kích thước bộ cuối chưa chốt | Regression demo |
| prop.demo.wardrobe | Có, asset demo giữ nguyên | game/assets/sprites/wardrobe.png | PNG; kích thước bộ cuối chưa chốt | Regression demo |
| font.ui | Có | game/assets/fonts/NotoSans-Regular.ttf | JSON/TTF theo đuôi file | Render/UI |
| dialogue.demo.trang | Có, asset demo giữ nguyên | game/assets/dialogue/npc_trang_intro.json | JSON/TTF theo đuôi file | Regression demo |
| ui.dialogue.layout | Có | game/assets/dialogue/ui-layout.json | JSON/TTF theo đuôi file | Render/UI |
| portrait.placeholder | Có, asset demo giữ nguyên | game/assets/portraits/student_placeholder.png | PNG; kích thước bộ cuối chưa chốt | Dialogue |
| audio.school.birds | Thiếu; optional/fallback | game/assets/audio/ambience/school_birds.ogg | OGG; sample rate/channels chưa chốt | Audio |
| audio.school.voices | Thiếu; optional/fallback | game/assets/audio/ambience/school_distant_voices.ogg | OGG; sample rate/channels chưa chốt | Audio |
| player.idle | Có, asset demo giữ nguyên | game/assets/sprites/player_idle.png | 32×32 px/frame; 7 frame; 0.12 s/frame (bộ demo) | Render/UI |
| player.down | Có, asset demo giữ nguyên | game/assets/sprites/player_down_run.png | 32×32 px/frame; 6 frame; 0.12 s/frame (bộ demo) | Render/UI |
| player.back | Có, asset demo giữ nguyên | game/assets/sprites/player_back_run.png | 32×32 px/frame; 6 frame; 0.12 s/frame (bộ demo) | Render/UI |
| player.side | Có, asset demo giữ nguyên | game/assets/sprites/player_side_run.png | 32×32 px/frame; 7 frame; 0.12 s/frame (bộ demo) | Render/UI |
| npc.trang.idle | Có, asset demo giữ nguyên | game/assets/sprites/npc_trang_idle.png | 32×32 px/frame; 5 frame; 0.15 s/frame (bộ demo) | Render/UI |
| ui.interaction | Có | game/assets/sprites/interaction_mark.png | 8×25 px/frame; 4 frame; 0.2 s/frame (bộ demo) | Render/UI |
| npc.demo.config | Có, asset demo giữ nguyên | game/assets/config/demo-npcs.json | JSON/TTF theo đuôi file | Regression demo |
| dialogue.demo.locked-door | Có, asset demo giữ nguyên | game/assets/dialogue/demo_locked_door.json | JSON/TTF theo đuôi file | Regression demo |
| story.content | Có | game/assets/story/content.json | JSON/TTF theo đuôi file | Runtime truyện |
| story.world | Có | game/assets/config/story-world.json | JSON/TTF theo đuôi file | Runtime truyện |
| story.ui.layout | Có | game/assets/story/ui-layout.json | JSON/TTF theo đuôi file | Runtime truyện |
| portrait.linh.normal | Thiếu; optional/fallback | game/assets/portraits/linh_normal.png | PNG; kích thước bộ cuối chưa chốt | Dialogue |
| portrait.trang.normal | Thiếu; optional/fallback | game/assets/portraits/trang_normal.png | PNG; kích thước bộ cuối chưa chốt | Dialogue |
| portrait.duc.normal | Thiếu; optional/fallback | game/assets/portraits/duc_normal.png | PNG; kích thước bộ cuối chưa chốt | Dialogue |
| portrait.nam.normal | Thiếu; optional/fallback | game/assets/portraits/nam_normal.png | PNG; kích thước bộ cuối chưa chốt | Dialogue |
| portrait.hung.normal | Thiếu; optional/fallback | game/assets/portraits/hung_normal.png | PNG; kích thước bộ cuối chưa chốt | Dialogue |
| portrait.nga.normal | Thiếu; optional/fallback | game/assets/portraits/nga_normal.png | PNG; kích thước bộ cuối chưa chốt | Dialogue |
| map.story.room_304 | Thiếu; optional/fallback | game/assets/maps/room_304.png | PNG; kích thước bộ cuối chưa chốt | World |
| map.tmx.room_304 | Thiếu; optional/fallback | game/assets/maps/room_304.tmx | TMX orthogonal, tile 32×32 | World |
| map.story.hallway | Thiếu; optional/fallback | game/assets/maps/hallway.png | PNG; kích thước bộ cuối chưa chốt | World |
| map.tmx.hallway | Thiếu; optional/fallback | game/assets/maps/hallway.tmx | TMX orthogonal, tile 32×32 | World |
| map.story.guard_room | Thiếu; optional/fallback | game/assets/maps/guard_room.png | PNG; kích thước bộ cuối chưa chốt | World |
| map.tmx.guard_room | Thiếu; optional/fallback | game/assets/maps/guard_room.tmx | TMX orthogonal, tile 32×32 | World |
| map.story.classroom | Thiếu; optional/fallback | game/assets/maps/classroom.png | PNG; kích thước bộ cuối chưa chốt | World |
| map.tmx.classroom | Thiếu; optional/fallback | game/assets/maps/classroom.tmx | TMX orthogonal, tile 32×32 | World |
| prop.photo | Thiếu; optional/fallback | game/assets/sprites/props/photo.png | PNG; kích thước bộ cuối chưa chốt | Render/UI |
| prop.register | Thiếu; optional/fallback | game/assets/sprites/props/register.png | PNG; kích thước bộ cuối chưa chốt | Render/UI |
| prop.player | Thiếu; optional/fallback | game/assets/sprites/props/player.png | PNG; kích thước bộ cuối chưa chốt | Render/UI |
| prop.crate | Thiếu; optional/fallback | game/assets/sprites/props/crate.png | PNG; kích thước bộ cuối chưa chốt | Render/UI |
| prop.cabinet | Thiếu; optional/fallback | game/assets/sprites/props/cabinet.png | PNG; kích thước bộ cuối chưa chốt | Render/UI |
| prop.papers | Thiếu; optional/fallback | game/assets/sprites/props/papers.png | PNG; kích thước bộ cuối chưa chốt | Render/UI |
| story.audio | Có | game/assets/config/story-audio.json | JSON/TTF theo đuôi file | Runtime truyện |
| ui.skin | Thiếu; UI kỹ thuật | game/assets/ui/room304-skin.json | JSON/TTF theo đuôi file | Menu/dialogue style |

| document.msg | Thiếu; optional/fallback | game/assets/documents/msg.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| document.photo | Thiếu; optional/fallback | game/assets/documents/photo.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| document.register | Thiếu; optional/fallback | game/assets/documents/register.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| document.news | Thiếu; optional/fallback | game/assets/documents/news.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| document.gate | Thiếu; optional/fallback | game/assets/documents/gate.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| document.report | Thiếu; optional/fallback | game/assets/documents/report.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| document.inventory | Thiếu; optional/fallback | game/assets/documents/inventory.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| document.signed | Thiếu; optional/fallback | game/assets/documents/signed.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| document.family | Thiếu; optional/fallback | game/assets/documents/family.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| effect.memory | Thiếu; optional/fallback | game/assets/effects/memory.png | PNG; kích thước/anchor bộ cuối chưa chốt | Presentation |
| document.c_phone | Thiếu; optional/fallback | game/assets/documents/trang_phone.png | PNG; kích thước/anchor bộ cuối chưa chốt | Document/UI |
| story.visuals | Có, cấu hình runtime | game/assets/config/story-visuals.json | JSON version1 | Visual bindings |
| background.menu | Thiếu; optional/fallback | game/assets/ui/menu.png | PNG; kích thước/anchor bộ cuối chưa chốt | Presentation |
| background.ending.a | Thiếu; optional/fallback | game/assets/endings/a.png | PNG; kích thước/anchor bộ cuối chưa chốt | Presentation |
| background.ending.b | Thiếu; optional/fallback | game/assets/endings/b.png | PNG; kích thước/anchor bộ cuối chưa chốt | Presentation |
| background.ending.c | Thiếu; optional/fallback | game/assets/endings/c.png | PNG; kích thước/anchor bộ cuối chưa chốt | Presentation |

Các yêu cầu thiết kế còn thiếu bộ art (một số đã có manifest texture, các biến thể còn cần khai thêm) (không tự đặt frame/kích thước):

| Asset nguồn | Trạng thái | Đường dẫn đề xuất | Yêu cầu kỹ thuật đã biết | Chức năng |
|---|---|---|---|---|
| CHAR-LINH | Thiếu bộ cuối; avatar hình học | game/assets/sprites/linh/ | Đi/idle bốn hướng, cầm sổ/điện thoại, C ngồi; SDD đề xuất 32×48, layout/frame/duration chưa chốt | Player, cutscene |
| CHAR-TRANG/DUC/NAM/HUNG | Thiếu bộ cuối; hộp + tên | game/assets/sprites/{trang,duc,nam,hung}/ | Đứng/đi; Đức đưa ảnh, bà đưa chìa/mở cổng, Hùng viết; thông số chưa chốt | NPC/staging |
| CHAR-NGA | Thiếu; bóng hình học theo cue | game/assets/sprites/nga/ | Ngồi/chỉ thùng/đứng sau kính-Hùng/đi ra; không quái đuổi | Haunt, evidence, A |
| FX-MEMORY | Thiếu; cốt truyện đi qua nhịp không chặn | game/assets/effects/memory/ | Bóng học sinh không tên; độ mờ, không cần chân dung/tên mới | A-01 |
| PROP-DESKS/BOOKS/KEYBOARD/BOARD/CHALK | Thiếu art; collision/nhãn/vạch kỹ thuật | game/assets/sprites/props/ hoặc maps/tilesets/ | Đồ nền/ghế/vạch; thông số ảnh và layout chưa chốt | Bốn map, loop |
| PROP-FAMILY | Thiếu; tài liệu chữ DOC-FAMILY | game/assets/portraits/family_photo.png | Nga/Đức lúc nhỏ; không thêm tình tiết | C4-01 tùy chọn |
| PROP-TAPE/BATTERY/KEY | Thiếu; vật/position/notes có state | game/assets/sprites/props/{tape,battery,key}.png | 4 pin AA 1,5V; chìa ngăn dưới; băng đúng nhãn | Evidence; không đồ trùng |
| PROP-INVENTORY/PAPERS/SIGNED | Thiếu hình; DOC-INVENTORY/GATE/REPORT/SIGNED có chữ đủ | game/assets/documents/ | Giữ nhãn, hai nguồn giấy + bản sao; frame/size chưa chốt | Documents/signing |
| PROP-REGISTER/PHOTO/CABINET/PLAYER/CRATE variants | Thiếu art trạng thái | game/assets/sprites/props/ | Mở/đóng/khóa; hai lớp mực; đèn/khay pin; ảnh 33 người. Không suy grid | Staging theo sourceRows |
| AUD-WIND/SCHOOL | Thiếu; im lặng | game/assets/audio/ambience/ | OGG loop; volume MIX-01; format cuối chưa chốt | normal/ACTIVE/SUSPENDED |
| AUD-MESSAGE/DOOR/STEP/PAGE/CAMERA/BELL/BELL-NORMAL/CHAIR/KNOCK/LOCK/WRITE | Thiếu; có chữ/phụ đề/nhịp | game/assets/audio/sfx/ | Clip riêng; không lặp trên từng ký tự, chuông ma một lần | Source beat IDs |
| AUD-CALL/TAPE/ALARM/RUN | Thiếu; chữ nguyên văn luôn hiện | game/assets/audio/{voice,tape}/ | Không thêm lời; băng chỉ chạy trong nhịp hiển thị; đối chất không phát lại cầu cứu | C1/C4/C5/C6/B |
| C-VOICE-ENOUGH/C-PHONE | Thiếu; thoại/tin nhắn bằng chữ | game/assets/audio/voice/c_enough.ogg; documents/trang_phone.png | “Đủ rồi”; tin nhắn đúng C-02; không tự kết luận chết/cứu | C default-off |
| UI-DIALOG/DOC/CHOICE/HINT/SUB/RECORD/NAMEFX/TITLE/NOTES/PAUSE/END | Có UI kỹ thuật; art thiếu | game/assets/ui/; documents/; effects/ | 16:9 và 4:3, chữ Việt, không timer lựa chọn; recording/name overlay art chưa chốt | Common views |
| Ending artwork | Chưa có yêu cầu art riêng | game/assets/endings/ nếu nhận | Tên/câu kết có chữ đủ; kích thước/style cần người dùng quyết định | Trang ending |

ALL-CHAR/PROP-EVIDENCE là nhóm, không yêu cầu file nhóm riêng. Những path “đề xuất” chưa được đăng ký sẽ không tự render chỉ bằng đặt file; làm theo E.

## D. Quy cách kỹ thuật và tọa độ

- Runtime pixel units, FitViewport 640×480; resize letterbox đồng nhất. Geometry JSON hiện 640×480 và margin 8. Đây là không gian test; kích thước final theo TMX/thiết kế, không bắt asset khớp layout tạm.
- Player hiện speed 120 px/s, footprint 18×10, canvas 32×48, scale 1 trong story-world.json. Các giá trị này là cấu hình test minh bạch, không thông số final đã được phê duyệt. x/y là góc dưới trái footprint; sprite neo đáy theo y và canh giữa footprint theo x. Collision không thay khi frame/portrait đổi.
- NPC có canvasWidth/canvasHeight/footWidth/footHeight/scale/anchorX/anchorY riêng trong npcs[]. Giá trị hiện tại 32×48/18×10 là geometry kiểm thử. Điểm x/y vẫn góc footprint; ảnh vẽ tại x+footWidth/2-canvasWidth×scale×anchorX, y-canvasHeight×scale×anchorY. Anchor chuẩn hóa 0..1; mặc định (.5,0), không phải anchor bộ art cuối đã chốt. Chỉnh JSON, không sửa renderer/collision Java.
- Spritesheet PNG grid row-major, hàng trên trước. Khai frameWidth, frameHeight, frameCount, frameDuration rõ trong manifest. Sheet phải chia hết cell, count không vượt số cell. Chưa hỗ trợ packed character atlas/region offsets/trimming. Tách từng sheet/state hoặc thêm adapter Animation/TextureAtlas trong GameAssets khi nhận atlas — không suy layout từ kích thước.
- Player animation keys: idle.down/up/left/right, walk.down/up/left/right. Map keys → manifest ID trong player.animations. Idle riêng hướng cần file thật. Không tự flip sheet trái/phải trong profile mới; cung cấp state phù hợp. Thiếu animation → avatar fallback hoặc idle.down nếu có. Frame time độc lập dialogue.
- Player/NPC animations nhận 8 idle/walk hướng và pose.<id> (ASCII lowercase). Pose có thể dùng phone/book/write/seated/point theo bindings hiện tại. Pose chơi một lần và giữ frame cuối; idle/walk loop. NPC có thoại quay về Linh nếu không có cue pose ghi đè. Linh idle cầm sổ dùng pose.book khi được cấu hình, walk dùng hướng bình thường. Thời gian profile movement theo tốc độ thật của profile; collision không phụ thuộc frame.
- Portrait: PNG alpha, speaker id linh/trang/duc/nam/hung/nga; speaker.portrait nhận manifest ID; expression → speakers[id].expressions. line.speakerId/expression liên kết. DialogueSpeaker cho phép height/maxWidth/centerX/bottom; mặc định từ layout. Kích thước gốc và anchor art cuối chưa xác định. Các speaker call/nam_door/hung_tape/nga_tape phân biệt nguồn âm, không tự lấy NPC ở phòng khác.
- UI font Việt cần glyph đầy đủ. Font hiện thiếu glyph mũi tên trong ảnh kiểm thử nên view hiển thị “Lên / xuống”; dữ liệu nguyên văn giữ nguyên. Không chuyển glyph thiếu thành ô lỗi.
- OGG/WAV theo backend LibGDX; loop dùng Music, clip nhịp dùng Sound. Sample rate/channels/loudness chưa chốt; không áp preset tùy ý. File nghe nhìn optional, metadata/file hỏng phải báo.

## E. Quy trình tích hợp cụ thể

### 1. Nhận và kiểm tra

Đọc yêu cầu nguồn 7.1 và sourceRows.visual/audio/wait của khối liên quan. Ghi filename/kích thước/grid/cadence/alpha/anchor thật từ người dùng; thiếu thông tin thì ghi BLOCKED_BY_REQUIREMENTS, không suy số frame. Giữ bản gốc; không xóa bộ demo. Đặt file vào thư mục tương ứng, đăng ký ID/type/filter/required=false trong manifest trước khi liên kết.

### 2. Sprite/player/NPC/props

Ví dụ khai một sheet đã có thông số xác nhận (các số dưới phải thay bằng số thực, không dùng làm spec): id player.final.walk.down, path sprites/linh/walk_down.png, type texture, filter Nearest, required false, kèm bốn field frame. Map player.animations[walk.down] tới ID đó; canvas/scale/footprint là các field tách biệt. Dùng một frame idle vẫn cần metadata/count=1 và duration dương.

NPC đã có ID, chỉ cập nhật npcs[].animations. placements[].x/y cho vị trí riêng từng stage; nếu không có thì dùng npcSlots của room/TMX. Thêm NPC đơn giản: npcs[] có id/name/animations và dialogue (block ID), optional requiredEvent, placements ở stage/room thích hợp và npcSlots trong scene. WorldView tự đọc danh sách ID. Với NPC mới có hành động truyện quan trọng, thêm gate/action có test trong StoryDirector/StorySession; không nhét mutation vào UI.

Props có o.asset mặc định và o.variants → manifest texture; vẽ vào rectangle JSON/TMX, collision vẫn riêng. Scene.backgrounds map NORMAL/ACTIVE/SUSPENDED/RESOLVED → texture optional, nền base khi không khai variant. StoryWorldView.propState chọn desk normal/empty/final theo sổ, cabinet locked/closed theo khóa thật, crate closed/open theo đã lấy băng, player empty/powered/playing theo pin/băng tại máy, evidence normal/final theo bản ký; prop khác normal. Đăng ký texture rồi sửa JSON, không sửa Java cho các trạng thái này. Nếu thêm trạng thái gameplay mới hoặc prop với quy tắc khác, thêm nhánh nhỏ tại propState và test tương ứng; không viết lại engine. PNG variant hiện chỉ thay nền PNG, TMX vẫn ưu tiên nếu được nạp.

### 2b. Visual cue bindings (đã chạy trong runtime)

File game/assets/config/story-visuals.json có cues theo ID line thực (không alias scene), 25 binding hiện tại. Schema một cue:

~~~json
{
  "cue": "C5-05-FULL-E07",
  "room": "CLASSROOM",
  "actors": [{"id": "hung", "pose": "pose.write", "anchor": "evidence", "dx": -34, "dy": -24}]
}
~~~

Các số ví dụ là geometry fallback hiện tại, không quy cách art cuối. actors[].id nhận linh hoặc NPC ID; pose là idle/walk hướng hay pose.*; anchor là object ID trong room hoặc npc:<id>. Không có anchor dùng vị trí vật lý/stage gốc. dx/dy pixel offsets, alpha 0..1, hidden ẩn; to là anchor đích và seconds là thời gian dịch chuyển trình bày. Overrides giữ qua các line trong cùng block, reset khi đổi block; clear=true xóa overrides/effect. Vị trí trình bày không sửa Player/GameState/collision; cutscene đang khóa input và không save giữa cảnh. Điều chỉnh binding theo art thật, không tạo AI theo đuôi.

Effect có camera/chair/record/memory/phone; optional asset là texture manifest. Thiếu dùng nhãn/hình học/im lặng. Icon ghi âm giữ xuyên subtitle C4-05-MAIN; tên Linh dùng overlay hiện tại, không sửa trang chụp. FX memory dùng sprite không tên, alpha/dịch chuyển kỹ thuật; ảnh phone fit khung 180×245 của fallback. Camera/record/chair và memory texture dùng pixel size gốc; nếu cần art atlas/hiệu ứng phức tạp mới, chỉnh riêng StoryWorldView.drawEffects với metadata đã xác nhận. Không thêm hiệu ứng nhấp nháy nhanh. Các bounds/fade/profile hiện là kiểm thử, cần nghiệm thu alignment 4:3/16:9.

Đã có các binding bóng C1-04, C4-01/C4-05/C5-04, Hùng viết, Nga ngồi/chuyển ra A và Linh ngồi C. lineDocuments/menus cũng ở file này; validation bắt cue/actor/anchor/room/texture ID sai và trùng cue. Thêm pose sheet chỉ sửa manifest + npcs/player.animations; thêm vị trí/pose ở nhịp có sẵn chỉ sửa cues.

### 3. Import bốn TMX

Entry map.tmx.<room> đã có; đặt đúng path trong C hoặc sửa manifest. Dùng finite orthogonal, tile 32×32, pixel scale 1. Không nhân ×6. TmxMapLoader đổi hệ y từ Tiled xuống dưới sang LibGDX lên trên; JSON/spawn/runtime là bottom-left. Tile layers vẽ theo thứ tự map. Parser dùng layer cấp map, không tìm object layers lồng trong group.

Các object layers cần có:

- collisions: chỉ rectangle, bounded, footprint không chồng spawn. Không suy collision từ tile art.
- spawns: point hoặc rectangle; name là spawn ID, x/y là góc dưới trái footprint. Cần đầy đủ tên đang có trong từng scene (danh sách sau).
- interactables: rectangle, name hoặc property id khớp object ID. Có thể tách cửa vào doors optional; mỗi ID đúng một lần. Tất cả logical objects hiện có phải tồn tại trong TMX; thiếu báo lỗi. type optional phải đúng PROP/DOOR; targetRoom/spawnId optional override destination và được validate.
- npcs optional: point tên NPC ID override npcSlots. Slot JSON tiếp tục dùng nếu không override. Slot nằm trên vị trí đứng hợp lệ.

| RoomId | Spawn bắt buộc | Object ID bắt buộc |
|---|---|---|
| ROOM_304 | entry, desk, cabinet, family | door.hall, photo, desk, player, crate, cabinet, cabinet.upper, lastdesk, window |
| HALLWAY | entry, room304, hung, guard, chalk | door.304, door.hung, stairs, guard.wall, board |
| GUARD_ROOM | entry, gate, outside | door.hall, records, gate |
| CLASSROOM | entry, table | door.hall, evidence |

Door thường lấy target/spawn từ config; stairs/guard.wall/hung door có story gate, không bypass bằng đổi tile. Spawn outside vẫn thuộc GUARD_ROOM, không thêm sân map riêng. Chuyển cảnh dùng fade 0,35 s và khóa input. Geometry TMX hợp lệ được cài vào SceneCatalog để routing/save thống nhất; màn load kiểm tra vị trí bằng geometry hiện tại và footprint NPC đang hiện, giữ màn cũ nếu không hợp lệ. Cache TMX theo lượt, muốn hot reload phải bắt đầu lượt mới.

Adapter và runtime test: world/StoryWorldView.java; lwjgl3/src/regression/java/com/room304/lwjgl3/StoryDesktopChecks.java. Đã test real TmxMapLoader + renderer bằng fixture không tileset, scale/Y conversion/IDs và thiếu layer. Bốn map final, TSX/tileset, occlusion/layering vẫn cần nghiệm thu khi có asset. Xem tài liệu LibGDX: https://libgdx.com/wiki/graphics/2d/tile-maps.

### 4. Portrait/document

Cập nhật story/content.json.speakers và line expression; không gắn portrait theo chỉ số NPC trong list. Thiếu portrait vẫn đọc được. Background từng dialogue có thể dùng line.background hoặc dialogue.background qua manifest ID/raw path hợp lệ. Hình giấy/ảnh/tin nhắn muốn hiện cùng chữ: gắn background vào dialogue/document line tương ứng, giữ text nguyên văn và pagination. config/story-visuals.json.documents map DOC-* → texture manifest, lineDocuments nối các line đọc lần đầu tới DOC-* (bao gồm __n của trang được mở rộng). StorySession.read gắn ID DOC-* theo nội dung nguồn khi xem lại; cùng lookup chạy qua GameScreen → DialogueUI.present. Ảnh giấy fit trong viewport, giữ text/pagination; optional missing chỉ còn chữ. Không sửa clue khi xem lại. DOC-FAMILY và C-PHONE có binding/path riêng.

### 5. Skin

Đặt ui/room304-skin.json (+ cùng basename .atlas nếu dùng atlas). GameAssets.optionalSkin dùng LibGDX Skin và cache atlas. default-font được view tạo bằng FreeType: không khai lại tên này trong JSON; style tham chiếu default-font. Skin có thể có default TextButtonStyle/LabelStyle cho menu; dialogue LabelStyle tên dialogue và TextButtonStyle tên choice. Không có style → fallback hiện hành. checked/over/down cần rõ, chữ Việt/wrap phải đủ; skin chưa có art thật để nghiệm thu.

Geometry/typewriter/pagination/portrait nằm ở story/ui-layout.json. Thêm LabelStyle speaker/hud và TextButtonStyle hud để thay tên/HUD/control. Drawable dialogue-frame thay gradient trong bounds layout.gradientHeight; hud-frame thay nền info; menu-frame thay nền UiPanel. Drawable có thể là region/ninepatch/sprite/texture đã nạp vào Skin. Thiếu style/drawable dùng fallback. Tint dim và geometry đọc từ layout; thay bố cục căn bản ngoài các bounds hiện có vẫn cần presentation Java, không đổi callback/gate. Menu/ending ảnh theo story-visuals.json.menus, không bắt buộc Skin.

### 6. Audio

Đăng ký loop type music/clip type sound trong manifest rồi sửa config/story-audio.json. ambience keys ROOM_304/NORMAL, ROOM_304/ACTIVE, HALLWAY/ACTIVE… gồm RoomId/Phenomenon; value là music ID. bindings là list {cue,asset,channel,loop,decorative}. cue phải là ID line thực trong content.dialogues, không chỉ ID alias scene. channel voice hoặc fx; loop optional false; decorative true áp MIX FX 40%.

Một line có nhiều bindings với các asset ID khác nhau; không đăng ký trùng asset trên cùng cue. Băng đầy đủ cần clip tách theo các câu và phụ đề; dùng sound cùng line ở C4-05 và đoạn đầu C5-04. Sound dừng khi chuyển line, hết clip vẫn giữ chữ; không chơi một file dài tự chạy qua các lựa chọn. Voice và FX trên cùng line chạy đồng thời, mỗi clip giữ Sound instance ID riêng; stop/pause/resume không tác động clip khác dùng cùng Sound. Thêm footsteps {asset:<sound ID>,distance:<pixel distance đã chốt>} ở root audio config. GameScreen chuyển displacement thực sau collision, không đếm held key/chuyển scene/pause; không có bước chân thứ ba tự sinh trước hiện tượng. Audio nền trường dừng trong C1-04-HAUNT trước commit sổ. Menu không cần âm để chơi; nếu yêu cầu nhạc menu riêng sau này, thêm binding lifecycle MenuScreen chứ không đưa vào story callbacks. Backend giả lập đã kiểm tra API/volume/ownership; chưa nghe clip/mix thật. Default config rỗng → im lặng, volume UI vẫn hoạt động.

### 7. Build/kiểm thử và lỗi thường gặp

Từ root:
~~~powershell
$env:GRADLE_USER_HOME = "$PWD\.gradle-user-home"
.\game\gradlew.bat -p game --offline --no-daemon -g .gradle-user-home build :lwjgl3:desktopRegressionTest :lwjgl3:storyDesktopTest --console=plain
.\game\gradlew.bat -p game :lwjgl3:run
node game/tools/check-story-source.mjs
~~~

Không dùng --offline khi dependency chưa có cache. Source tests nằm core/src/regression và lwjgl3/src/regression; Gradle test thông thường không có JUnit. Không gọi build success là đã chạy desktop.

- Missing required: kiểm tra ID/path/case; required chỉ dành cho file thực sự cần. Optional missing log là fallback dự kiến.
- Sprite grid mismatch: kiểm tra cell/count/duration, PNG decoded dimension; không tự split hàng sai.
- Missing node/speaker/scene: sửa reference/duplicate trong JSON; validation thất bại trước chơi.
- TMX blocked spawn/missing layer/interaction: sửa object geometry/ID và y conversion, không bỏ guard.
- Save bị từ chối: đọc hết dialogue/notes/cutscene rồi pause lưu. Save lỗi/phiên bản mới/vị trí không hợp lệ hiện thông báo, không xóa slot hoặc ép state để load.
- Chữ/choice bị cắt: kiểm tra font Việt, layout bounds, wrap, 16:9/4:3, scroll đến item đang chọn; không giảm font để giấu lỗi bố cục.
- Băng không phát: kiểm tra binding cue thực, Sound type, file existence, mute và channel; story vẫn phải chơi được khi tắt âm.

## F. Tiếp tục sau khi nhận asset

| Hạng mục | Đã có | Còn thiếu / bước tiếp / kiểm chứng |
|---|---|---|
| World final | Geometry/door/spawn/collision JSON; TMX adapter/cache | Bốn TMX/TSX/tilesets + normal/cold art. Import theo E; kiểm tra 58 tương tác thực, tránh softlock vì placement/collision mới. |
| Character rendering | Player directional/pose/grid; NPC profile/anchor/cue hook; fallback theo stage | Bộ cuối. Link JSON/profile/cues, kiểm tra movement idle/collision/Y-sort và không NPC xuyên phòng. |
| Cutscene hình học/art | Nhịp/sourceRows, modal lock/fade, name overlay, 25 pose/effect bindings và geometry fallback | Chưa art cuối. Điều chỉnh anchor/offset/profile/effect texture trong story-visuals.json, không thêm story mutation; A không bắt tha thứ, C không có báo tử. |
| UI/document art | Common DialogueUI/notes/objective/choices/menu/end/save | Skin/portraits/giấy. Cập nhật lookup/config đã có; kiểm tra page/scroll/keyboard/held input. |
| Audio | Binding validation, mute/channels, pause, per-line stop, MIX-01 | Clips còn thiếu; mix/hearing/loop/tape runtime cần nghe thật; footsteps/multi-layer đã có schema/API, chỉ cần đăng ký clip/config; mix thật cần nghe nghiệm thu. |
| Saves | Version1 progress JSON + scene/position/facing; atomic file; corrupt/missing checks | Chưa migration version2/autosave/mid-cutscene save/cloud/multi-slot. Không serialize Screen/Player/Texture. Nếu đổi map x/y, load invalid phải báo; thiết kế migration khi thay geometry. |
| New content | Config generic dialogue/requiredEvent cho NPC/prop đơn giản | Story action quan trọng thêm Java gate/effect và scenario test; mới RoomId cần enum/routing/scene/ending constraints. Không thêm scripting engine. |
| Release | JAR build có sẵn | Oracle21.0.11 và máy Windows thứ hai chưa chạy; đo A 15–20 phút bằng người thật chưa có. Không đánh dấu nghiệm thu release chỉ từ desktop automation. |

Kiểm chứng code bổ sung: PresentationChecks trong core kiểm tra document/cue/anchor validation, voice+FX cùng nhịp, duck/pause/mute/restore, footsteps và không dispose borrowed assets. StoryDesktopChecks quan sát haunt/record/memory/C seated qua GameScreen và render sheets demo qua profile; đây không phải nghiệm thu art/audio cuối.

Các staging physical commits gom ở cuối một đoạn bắt buộc để không lưu/cancel giao dịch dở. SOURCE visual có thể biểu diễn vật di chuyển trước commit; không diễn giải hình cầm là ownership mới. Enter chỉ tiến một nhịp/page, E không advance dialogue, N chỉ notes, Esc pause không hủy scene bắt buộc. Save không lưu trace/callback/elapsed; load chỉ vào stable exploration, không lặp flag đã completed.

## G. Checklist sau bổ sung asset

- [ ] Đọc nguồn, xác nhận grid/frame/anchor/scale thay vì đoán.
- [ ] Manifest/assets và data references hợp lệ; optional missing không crash; không required art giả.
- [ ] Bốn TMX/PNG đúng layer/IDs/spawns; collision/footprint và vị trí save hợp lệ.
- [ ] Player idle bốn hướng và movement/collision không lệ thuộc dialogue/frame.
- [ ] NPC stage/presence đúng; không thoại/portrait của người vắng.
- [ ] Từng document/ảnh/băng/cầu cứu/tin nhắn giữ đúng chữ, pause không chạy vượt.
- [ ] Skin/fonts/portrait/choice wrap/scroll nghiệm thu 960×540 và 800×600; click và keyboard cùng đúng.
- [ ] E/Enter còn giữ qua modal/screen không tác động thêm; callbacks/events không lặp.
- [ ] Đường A thường/đảo thứ tự/family muộn; B EARLY/HEARD/FULL; C tắt/bật/hủy/xác nhận qua.
- [ ] Save/load tại các mốc khám phá; load từ trước chữ ký tới A; corrupt/future/missing file giữ thông báo và slot.
- [ ] Music/Sound/ducking/pause/mute nghe thật; không cue chỉ có âm; dispose/replay không giữ clip.
- [ ] Build, core regression, legacy desktop, story desktop và source parity qua sau asset mới.
- [ ] Gói Windows chạy trên máy thứ hai, đúng JDK yêu cầu; đo thời lượng đọc/di chuyển.
- [ ] Cập nhật DEVELOPMENT_PROGRESS.md bằng bằng chứng mới; không đánh dấu các checkbox chưa kiểm chứng.
