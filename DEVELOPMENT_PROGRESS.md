# Room304 — Tiến độ phát triển

Cập nhật 11/10/2026. Phạm vi: game truyện chức năng từ menu tới ending bằng fallback, tài nguyên nghe nhìn hoàn chỉnh bổ sung sau. Gameplay A/B và C cấu hình bật đã được kiểm chứng tự động trong runtime. Không gọi đây là bản nghiệm thu mỹ thuật/audio hoặc bản phát hành Windows.

Đã đọc CODEX.md, AGENTS/AGENTS.md, AGENTS/SDD.md, toàn bộ kịch bản/flow liên quan, tiến độ và guide cũ; khảo sát source/asset theo dependency. Giữ migration/dirty worktree có sẵn. CODEX.md và tài liệu nguồn trong AGENTS/ không bị sửa; không thêm dependency, không tạo art/audio giả, không commit/push/đổi remote.

## Kết quả theo giai đoạn

1. Discovery: nền state/director/snapshot tồn tại nhưng app cũ chỉ chạy demo 305/306/sân sau; NPC chưa đổi theo story; chưa có full dialogue/notes/save/endings trong runtime.
2. Architecture: dùng lại AssetManager, Player collision, nearest selector, DialogueController/UI, GameState/StoryDirector/ProgressSnapshot. StorySession nối cùng một state qua bốn room; không thêm state/quest engine song song.
3. Implementation: 99 khối JSON nguyên văn, routing và choice/gate/callback; bốn scene logic/spawn/collision/NPC stage; menu/pause/notes/doc/objective/end/replay; local file save/load; TMX/skin/audio integration points và optional fallback.
4. Verification: build, core regression, legacy desktop và story desktop qua. Thực thi đủ 99 khối trong core (hai biến thể ALONE dùng fixture NPC vắng); 24 biến thể thứ tự điều tra; A/B/C qua OpenGL, 58 tương tác E đi tới bằng movement/collision, 14.300 frame.
5. Handover: ASSET_INTEGRATION_GUIDE.md đã viết lại theo source/config/asset thực tế, gồm các trường hợp cần sửa presentation Java, bảng thiếu và checklist chưa nghiệm thu asset.

## Trạng thái

DONE chỉ trong phạm vi và bằng chứng ghi tại hàng. PARTIAL còn phần code/nghiệm thu; BLOCKED_BY_ASSETS thiếu nghe nhìn; BLOCKED_BY_REQUIREMENTS chưa chốt specs; TODO chưa làm.

| Nhiệm vụ | Trạng thái | Phạm vi và bằng chứng |
|---|---|---|
| Full text/data C0→C6/A/B/C | DONE | 99 khối, 422 đơn vị dialogue; source parity 538 strings, 385 source beats, 364 text units; không tự sáng tác truyện. |
| Story progression/choice/quest objectives | DONE | StorySession + Director chạy trong GameScreen; đủ gate, hai thử loop, xác minh rút lui, mở lại có hủy, cassette-owned khác heard, confess/sign/warning/final. Core chạy 24 biến thể và mọi khối. |
| Ending A/B | DONE | A đầy đủ; B EARLY/HEARD/FULL, từ tủ và final; không chốt trước trial/evidence; source vật giữ/khóa/ra ngoài/trả chìa. A/B desktop qua. |
| Ending C | DONE | enableC=false mặc định; bật fixture để chơi; cancel được chọn đầu, hủy không tập hợp lại; xác nhận mới C, Linh ở lại, không thêm báo tử. Core/desktop và snapshot C qua. |
| Runtime screen/menu/pause/end/replay | DONE | Menu thực mở New Game; Enter còn giữ không advance C0. Ending UI và Replay skip callback qua keyboard; fresh replay bắt đầu C1-01. Pause/help giữa final choice giữ pending choice. |
| Player movement/input/collision | DONE | WASD/arrows, E/Enter/N/Esc theo mode, normalized/substeps, khóa modal/fade; legacy thin-wall/collision và mới đi tới 58 điểm E bằng runtime physics. Canvas/footprint tách asset. |
| Bốn room/door/spawn/interaction | DONE | 304/HALLWAY/GUARD_ROOM/CLASSROOM; geometry JSON cấu hình, 0,35 s fade, gate đúng story. TMX fixture real loader/renderer xác minh scale, Y, ID và missing-layer rejection. Đây là logic, chưa map final. |
| NPC ID/stage/presence | DONE | Config stage/room/event và x/y override; Đức bàn cuối trước confess, lớp sau đó, family chuyển tạm; nhóm ending ngoài cổng. Không thoại từ NPC vắng; app validate refs/slots. |
| Common DialogueUI/controller | DONE | Một UI, speaker/portrait optional, pagination/typewriter, keyboard/click commands, choices theo flags, callback one-shot, mandatory scene không bị Esc hủy. Legacy và story desktop qua. |
| Notes/document/transcript/objective/evidence | DONE | Data nguyên văn, knowledge lọc nội dung, xem lại không cấp chứng cứ, đóng bìa không tắt hiện tượng; N runtime + book/notes/core variants qua. Không có inventory/quest state trùng. |
| Save/load logic + UI | DONE | Local one-slot atomic replace, version1 progress/room/position/facing; pause Save, Continue command; missing/corrupt/future/invalid position/state rejection; mature save load rồi chơi tới A; không lặp intro/events. Chỉ stable exploration, không mid-dialogue. |
| Asset manifest/cache/missing fallback | DONE | Required chỉ font + năm data/config file story; art optional. PNG/music/sound/atlas manager cấp app, grid/path/type/ref validation; cache/dispose legacy qua. Main gameplay bằng shapes/text không cần asset story. |
| TMX adapter | DONE | Import finite orthogonal32, collision/spawn/interactables/doors/npcs; logical geometry cập nhật cho routing/save; cache từng room/lượt. Fixture loader/renderer qua; map final ở hàng dưới. |
| UI skin integration | DONE | Code có style menu/body/speaker/HUD/choice và drawable menu/dialogue/HUD; optional skin thiếu vẫn hoạt động, atlas/font ownership giữ nguyên. Asset skin cuối chưa nghiệm thu ở hàng nghe nhìn. |
| Audio/MIX-01 pipeline | DONE | Runtime hỗ trợ nhiều clip/line, voice/FX riêng, ambience, footsteps theo displacement thực, pause/mute/duck25%/FX40%/restore0,25s. Backend regression kiểm tra play/stop/volume/ownership. Config rỗng và clip thật chưa có; chưa nghiệm thu bằng tai. |
| Animation/portrait/document/props art | BLOCKED_BY_ASSETS | Avatar/canvas technical và named NPC fallback, đủ logic. Directional idle cuối/poses, portraits, giấy hình, props-state, FX-memories/ghế/bóng đầy đủ chưa có. Có 25 visual cue bindings cho pose/anchor/bóng/chụp/ghi/viết/memory/C; idle theo hướng người nói, pose.* hỗ trợ sheet. Có config scene/prop variants/document/menu images; chỉ cần asset và metadata chuẩn. |
| Bốn TMX/TSX/tilesets/nghe nhìn cuối | BLOCKED_BY_ASSETS | PNG/TMX truyện chưa có, palette test không là art cuối. Import paths/layers/spawns/IDs có trong guide; cần test lại reachability sau thay collision. |
| Specs bộ art cuối | BLOCKED_BY_REQUIREMENTS | Frame layout/count/duration/anchor/size chưa chốt. SDD32×48 không biến thành số frame giả; guide phân biệt metadata demo và test canvas. |
| Mid-cutscene save/migration/multi-slot/autosave | TODO | Chưa nằm trong contract version1. Lưu stable exploration đủ resume lượt chơi; nếu mở rộng phải lưu cursor/giao dịch rõ, không serialize runtime graph. |
| Async loading cho asset lớn | TODO | Load đồng bộ hiện đủ cho fallback/bộ nhỏ; AssetManager.update loading screen khi cần, giữ ownership hiện tại. |
| Máy Windows thứ hai / Oracle21.0.11 | TODO | Build/test trên Temurin21.0.12; chưa nghiệm thu môi trường JDK/vender chốt hoặc installer trên máy khác. |
| Đo thời lượng A với người chơi thật | TODO | Chưa đo mục tiêu15–20 phút; automated stepping không là phép đo nhịp đọc của người. |
| Visual presentation bindings | DONE | StoryVisuals đọc 25 cues; real GL đã kiểm tra ghost/record/memory/C seated và chạy sheet demo qua profile runtime. Visual placements chỉ vẽ, không đổi Player physics hoặc tiến trình. Document refs + invalid cue/anchor có regression. |
| Bàn giao code/asset | DONE | Guide A–G, path thật, bảng tồn tại/thiếu/tạm/unknown, instructions và các schema/config và phạm vi cần Java khi đổi loại hiệu ứng; checklist asset chưa đánh dấu qua. |

## Quyết định kiến trúc

- Kịch bản 1.0 ưu tiên khi SDD cũ khác; điều kiện/effect Java if/switch theo SDD, chữ/references/geometry/audio/assets bằng JSON. Không scripting engine/ECS/library mới.
- Save được bật theo yêu cầu mới, thay “không lưu” của nguồn. Data vẫn giữ chuỗi nguồn để parity; UI xác nhận về menu dùng thông báo có save mới. Slot cũ không bị xóa khi New Game.
- GameState là tiến trình duy nhất; phenomenon/flags/objectives/notes suy ra, không serialize textures/NPC/Player/Screen. Snapshot completed/milestone/npcStage/ownership được kiểm tra nhất quán.
- SIGN→WARNING là giao dịch liên tục; vật có thể được staging trước nhưng chỉ commit tại mốc đã chọn. Cảnh đang đọc không save hoặc cancel; caller không được tạo callback trùng.
- C có đủ nội dung, mặc định off. UI không đưa C trước full_ready/warning/final_prepared; hủy về đúng final menu.
- Runtime pixel1/Fit640×480; geometry và footprint18×10/speed120 là cấu hình kỹ thuật test, không spec final đã duyệt. NPC positions theo stage, scene slots và transient family presence, không clone NPC state.
- Main app không còn demo305/306/backyard. DemoGameScreen chuyển sang regression source; lớp/assets demo cũ giữ để kiểm thử lịch sử. OpeningScreen cũ chưa được app gọi.
- Config có thể thêm NPC/prop thoại đơn giản bằng dialogue/requiredEvent và placements; action truyện quan trọng hoặc RoomId mới cần Java gates/tests. Pose/anchor/document/props/background/cue art đã có mapping config; hiệu ứng mới ngoài schema có thể cần Java presentation nhỏ, không viết lại logic truyện.

## Kiểm thử đã thực hiện

Lệnh tổng thể cuối (từ root), sau sửa fixture resize, trả exit 0: build/core/legacy desktop/story desktop đều qua. StoryDesktopTest cũng đã qua khi chạy riêng:
~~~powershell
$env:GRADLE_USER_HOME = "$PWD\.gradle-user-home"
.\game\gradlew.bat -p game --offline --no-daemon -g .gradle-user-home build :lwjgl3:desktopRegressionTest :lwjgl3:storyDesktopTest --console=plain
.\game\gradlew.bat -p game --offline --no-daemon -g .gradle-user-home :lwjgl3:storyDesktopTest --console=plain
node game/tools/check-story-source.mjs
~~~

- Core: 9 nhóm regression cũ + 8 foundation + PresentationChecks; A/B/C, strict gate, ticket/stale/cancel/one-shot, snapshots, notes/book, 24 biến thể, file persistence và load-to-A; 99/99 block coverage. C4-02-ALONE/C4-03-ALONE được test bằng world fixture loại Đức, không tuyên bố là đường canonical khi source đặt Đức ở304.
- Desktop legacy: nearest/ties/order, map round trip, animation/collision, modal/held input/pause/wardrobe, managed texture identity/dispose. Main lifecycle checks đã chuyển sang MenuScreen mới.
- Desktop story: A/B/C, 58 E interactions đạt bằng walking thật qua Player.update, 14.300 frames; New Game menu và held Enter; N; pause save; load; pause giữa băng giữ cursor và chưa cấp heard; help/back/resume ở final; Continue UI; Ending/replay skip UI; C cancel và default-off trong core.
- TMX: TmxMapLoader + Orthogonal renderer thật trên fixture XML không tileset; scale1/Y conversion/ID preservation và missing-spawns rejection. Chưa thử bốn map final/TSX vì thiếu asset.
- PNG UI được chụp và xem: game/lwjgl3/build/story-explore-16x9.png, story-final-choices-16x9.png, story-ending-4x3.png (800×600 thực). Đã sửa nhãn tràn mép và glyph mũi tên thiếu bằng chữ “Lên / xuống”. Đây là kiểm thử tự động + xem ảnh, không tuyên bố tự tay chơi hết game.
- Source parity read-only: 538 strings, 385 metadata beats, 364 text units, 99 blocks khớp nguồn; tool chỉ đọc source/data.

Môi trường: Temurin JDK21.0.12, Gradle wrapper9.8.0, gdx/FreeType1.14.2, LWJGL3.4.3. AGENTS yêu cầu Oracle21.0.11; chưa đổi dependency/vendor guard để test qua. Standard test không có JUnit; meaningful checks là Java regression/assertion và real OpenGL tasks.

## Các lỗi tìm thấy và đã xử lý

- Nền cũ chưa nối StoryDirector vào GameScreen: đã tích hợp cùng session/state/NPC/dialogue/events và all endings.
- Nested JSON placements bị JsonValue cast: đọc placement list rõ bằng LibGDX Json, có validation.
- Avatar/NPC slot chồng collision bàn cuối: sửa geometry và stage position, kiểm chứng reachability desktop.
- Enter còn giữ từ menu tác động C0: prime/suppress controls khi show/screen switch; test hold-menu-Enter qua.
- Label đồ vật tràn canvas và font thiếu glyph ↑↓: label bounded + tên rõ, UI dùng chữ hướng; PNG đã xem.
- Load thất bại do vị trí/map/skin có thể bỏ tài nguyên screen đang tạo: thêm cleanup trước khi báo lỗi; slot cũ và màn trước không bị replace trên requestLoad lỗi.
- Snapshot nhận tương quan milestone/ownership/npcStage/completed mâu thuẫn: bổ sung invariant và negative cases; invariant prepared kiểm tra evidence/warning, không bắt giữ chìa đã trả ở endingA/B.
- Regression cũ gọi OpeningScreen.onFinished đã không còn runtime: cập nhật sang app request/menu mới, giữ DemoGameScreen fixture.
- Test UI mở rộng bị cleanup che assertion; resize LWJGL thay Gdx.input, nên Replay/Continue không nhận input giả: cleanup chỉ file fixture đã tạo và khôi phục proxy sau resize. Không sửa nút runtime để “làm test qua”. Test cuối Replay skip/Continue qua.

## Giới hạn/rủi ro còn lại và bước tiếp

Không có lỗi nghiêm trọng đã biết làm kẹt các đường chơi đã kiểm chứng. Điều này không thay nghiệm thu bản đồ/art/audio mới: collision mới có thể làm NPC/object không tới được hoặc save cũ bị từ chối. File save ở working directory; chưa có migration sau thay geometry, không auto-correct vị trí hoặc xóa slot.

Presentation code đã nối profile riêng, pose, anchor, visual movement, state variants, document/menu images, ghost/memory/chair/camera/recording/phone fallback và skin chrome. Bố cục hiện là geometry kiểm thử; cần hiệu chỉnh config theo art/map thật. Skin/Music/Sound thật và final tileset/layer occlusion chưa nghiệm thu; DONE logic không có nghĩa đã nghe/nhìn bộ cuối.

Sau nhận asset: xác nhận thông số → đăng ký manifest/config → import TMX theo IDs/spawns → link animations/portraits/skin/audio → hiệu chỉnh visual bindings trong story-visuals.json → chạy các regression và toàn bộ đường A/B/C/tắt âm/save-load → xem PNG/nghe thật → nghiệm thu Oracle21.0.11 và Windows máy thứ hai → đo thời lượng. Chi tiết tại ASSET_INTEGRATION_GUIDE.md.

## Lượt hoàn thiện điểm ghép nghe nhìn (11/10/2026)

Đã bổ sung config/story-visuals.json, document/menu/effect manifest entries; mở schema pose.*, canvas/anchor/footprint riêng NPC, background/prop variants. Tài liệu xem lần đầu và xem lại dùng cùng DOC-* lookup; không cấp chứng cứ khi đọc lại. Audio một nhịp nhiều clip và footsteps từ displacement, backend test không cần clip thật. Snapshot version1 và nội dung nguồn giữ nguyên. Core/backend đã kiểm tra concurrent clips, MIX-01, pause/mute/restore, bước chân và borrowed ownership.

Sửa lỗi tìm thấy khi mở rộng: animation profile dùng cadence theo speed của profile thay vì tốc độ demo220; footprint NPC được validate riêng; alpha marker của bóng không còn trắng đục; cleanup view/menu khi nạp metadata/art lỗi, New Game báo lỗi trên menu đang có; load từ vị trí NPC chiếm bị từ chối và giữ màn trước. Xem PNG thấy world header/controls trùng HUD thoại: chỉ vẽ world HUD lúc khám phá. Bóng không gắn nhãn tên trước cảnh nhận diện. Kiểm chứng cuối sau các sửa này được ghi ở mục kiểm thử; không có commit/push.

Kiểm chứng cuối lượt bổ sung: lệnh build + desktopRegressionTest + storyDesktopTest trả exit0 sau sửa HUD/load/cadence. Core 99/99 khối, 24 biến thể vẫn qua; PresentationChecks qua; desktop 58 tương tác E, 14.300 frames và các assertion ghost/record/memory/C seated qua. Ảnh final choices16:9 và ending4:3 đã xem lại, world HUD không còn chồng HUD thoại. Source parity 538/385/364/99 qua. Chưa có clip/skin/map/art cuối để nghiệm thu bằng tai hoặc trên máy thứ hai.

## Tích hợp thay đổi từ remote (13/10/2026)

Đã xử lý xung đột giữa bản `FULL` chuyển project vào `game/` và các cập nhật trên `origin/main`. Giữ một hệ thống `com.room304.dialogue`; chuyển ba lựa chọn và tám câu thoại Trang sang JSON mẫu trong `game/assets/dialogue/`, giữ nguyên nội dung được đóng góp. Hai nhánh hỏi quay lại menu, nhánh cuối đóng hội thoại. Đây là mẫu của `DemoGameScreen` trong regression, không thay nội dung truyện chính.

Ghép tên Linh và bảng tên vào màn demo, dùng chiều cao sprite thực tế, đường dẫn font từ catalog và giải phóng tài nguyên tại renderer. Input mẫu thống nhất W/S hoặc phím hướng để chọn, F/Enter để xác nhận. Regression kiểm tra hai nhánh hỏi, quay lại menu, lựa chọn thoát, khóa di chuyển và bật/tắt bảng tên. Các lớp `vn.room304.game` ngoài project không được khôi phục sau migration.

Kiểm chứng trên Windows 11 với Temurin JDK 21.0.12.1 và Gradle Wrapper 9.8.0: `build :lwjgl3:desktopRegressionTest :lwjgl3:storyDesktopTest` trả exit 0. Core đạt 99/99 khối; desktop A/B/C, lưu/đọc, 58 tương tác E và 14.300 frame qua. `node game/tools/check-story-source.mjs` vẫn đạt 538 chuỗi, 385 nhịp, 364 đơn vị thoại và 99 khối. Đã xem ảnh `game/lwjgl3/build/dialogue-checks/dialogue-choices.png`: đủ ba nút, chữ tiếng Việt trong canvas. Review độc lập không tìm thấy lỗi cần sửa trong phần ghép renderer/input/Player/màn demo. Chưa xác minh Oracle JDK 21.0.11 hoặc bộ art/audio cuối.
