# Resolution và tiêu chuẩn hình ảnh

Nguồn kỹ thuật: `game/core/src/main/java/com/room304/world/StoryWorldView.java`, `world/Player.java`, `world/SceneCatalog.java`, `dialogue/DialogueUI.java`, `dialogue/DialogueAssets.java`, `screen/UiPanel.java`; các đường dẫn package ngắn tính từ `game/core/src/main/java/com/room304/`. Nguồn cấu hình: `game/assets/config/story-world.json`, `game/assets/story/ui-layout.json`.

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

## Trạng thái xác nhận và đề xuất

| Thông số | CONFIRMED hiện tại | PROPOSED sản xuất / giới hạn |
| --- | --- | --- |
| Viewport | World ban đầu FitViewport(640,480); enter() đặt world size theo scene. UI luôn 640×480. | Giữ bốn scene 640×480; tránh thay tỷ lệ scene làm UI/world scale khác nhau. |
| Màn hình | 4:3 logic, letterbox khi 16:9; không ép integer scaling. | Xem art 1× tại 640×480 và 2× tại 1280×960; kiểm tra thêm 960×540, 800×600, 1280×720. Nearest không loại bỏ rung pixel do scale lẻ. |
| Đơn vị | 1 world unit = 1 px; TMX renderer unitScale=1. | Không nhân scale 6 hoặc dùng PPU khác. |
| Tile/map | TMX orthogonal 32×32; geometry hiện 640×480. | 20×15 ô; tileset 512×256, margin/spacing 0. Tile art là mới; bố cục cuối cần thử. |
| Player/NPC | Canvas 32×48, scale1, chân18×10; player speed120. | Giữ khi import bộ đầu. Đổi hình không đổi footprint/tốc độ. |
| Animation | Grid row-major từ hàng trên, metadata bắt buộc, không có trim/packed regions. | Một hàng; idle2/2FPS, walk6/8FPS; pose từng mục ở master. |
| Portrait | Bounds tối đa300×350, centerX320,bottom70; Scaling.fit. Manifest 6 portrait đang Linear. | 150×175 → đúng 2× trong bounds. Chuyển filter portrait sang Nearest sau duyệt pixel style, không coi Linear đã được thay. |
| Tài liệu | Scaling.fit 640×480; dimAlpha.22; gradient phía dưới cao230. | Ảnh640×480, chi tiết chính nửa trên, lề24; không thay văn bản chứng cứ bằng ảnh. |
| Điện thoại C | drawEffects vẽ ép 180×245. | Xuất180×245 để không méo; không gọi đây là Scaling.fit. |
| Font | Noto Sans TTF, FreeType18 px, Linear; atlas glyph Việt được sinh trong UI. | Giữ font; không yêu cầu pixel font. Test đ/Đ, ă/â/ê/ô/ơ/ư và dấu tiếng Việt. |
| Alpha | Nhân vật/props/portrait texture có thể alpha; Nga thêm alpha runtime .15–.6 tùy cue. | RGBA, nhân vật Nga vẽ phần thân đủ độ đậm; tránh bake alpha thấp rồi nhân alpha lần nữa. |
| Bảng màu | Chưa có art bible/hình mẫu cuối. | 24–32 màu chính dùng chung; sáng thường/lạnh hiện tượng vẫn đủ đọc đường. Màu cụ thể và trang phục UNDECIDED. |

## Tọa độ và điểm neo

PNG có gốc trên trái. Runtime sau TmxMapLoader có Y hướng lên. x/y nhân vật là góc dưới trái vùng chân, không phải góc sprite; sprite vẽ tại `x + footWidth/2 - canvasWidth*scale*anchorX`, `y - canvasHeight*scale*anchorY`. anchorX=.5, anchorY=0. Với canvas32×48/foot18×10, chân ở vùng ảnh x=7..25, y=38..48; điểm giữa đáy (16,48). Giữ giống nhau mọi frame, kể cả pose ngồi; không tự kéo pivot theo hộp pixel không trong suốt.

Props hiện vẽ vừa rectangle tương tác JSON/TMX, chưa có draw offset/canvas riêng. Kích thước prop trong master bám rectangle để giảm scale méo; hình lớn cao hơn rectangle cần thêm metadata trình bày tại SceneCatalog/StoryWorldView, **không tăng collision theo sprite**. Collision là lớp riêng, nhưng sửa rectangle còn đổi vùng tương tác nên phải kiểm tra E.

## Đặt tên / thư mục / quyền sử dụng

Path relative tới game/assets, ASCII lowercase, dấu `/`, không khoảng trắng/`..`. Sprite `sprites/<id>/<state>_<direction>.png`; pose `pose_<name>.png`; portrait `portraits/<id>_<expression>.png`; map/TMX `maps/<room>.tmx`, TSX/PNG chung `maps/tilesets/`; props `sprites/props/`; giấy `documents/`; UI raw `ui/source/`; âm thanh `audio/ambience|sfx|voice|tape/`. Không đổi tên theo tên có dấu của nhân vật. Manifest không chấp nhận hai ID cùng path; tái dùng cùng ID thay vì alias path trùng.

NotoSans-Regular.ttf đã kiểm tra header TTF/cmap có glyph Việt mẫu, OFL.txt có SIL OFL1.1. Đây là kiểm tra file, không thay kiểm tra font render thật; glyph mũi tên không được mặc định là đủ. Asset mới phải ghi người tạo/nguồn/quyền phân phối, thông số export và phiên bản được duyệt.
