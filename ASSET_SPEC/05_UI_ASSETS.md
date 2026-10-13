# UI, tài liệu và font

## Bounds thực tế

`game/assets/story/ui-layout.json` CONFIRMED: canvas 640×480; font18; portrait centerX320,bottom70,maxWidth300,height350; textLeft24,textBottom40,textWidth484,textRows4; gradientHeight230; choice530×38,gap6; HUD maxWidth220,left18,top18; control80×36 ở right12,bottom16. `UiPanel.java` dùng nút510px rộng, tối thiểu38px, vùng cuộn540px. Game không có layout adaptive riêng cho16:9, dùng FitViewport letterbox.

Portrait150×175 (PROPOSED) scale2× vừa300×350. Chữ font Noto Sans hiện Linear, hình pixel đề xuất Nearest; manifest portrait hiện Linear nên phải cập nhật có chủ đích. Các hộp chữ vẫn phải đọc được khi alpha/dim phủ tài liệu. Pixel art bị scale lẻ ở800×600 hoặc960×540 là giới hạn hiện có; không tuyên bố nearest đồng nghĩa integer scaling.

## Art dùng chung

| ID | Path dưới game/assets/ | Thông số PROPOSED | Dùng cho |
| --- | --- | --- | --- |
| ui.art.dialogue_frame | ui/source/dialogue_frame.9.png | 66×66 PNG RGBA, gồm viền 9-patch 1 px | Khung thoại; ruột 64×64, góc giữ 8 px, phần giữa giãn. Không vẽ chữ. |
| ui.art.panel_frame | ui/source/panel_frame.9.png | 34×34 PNG RGBA, gồm viền 9-patch 1 px | Khung menu/pause/ghi chú/HUD dùng chung; ruột 32×32, góc giữ 6 px. Không vẽ chữ. |
| ui.art.button_normal | ui/source/button_normal.9.png | 34×34 PNG RGBA, gồm viền 9-patch 1 px | Nút thường; ruột 32×32, góc giữ 6 px. Không vẽ chữ. |
| ui.art.button_focus | ui/source/button_focus.9.png | 34×34 PNG RGBA, gồm viền 9-patch 1 px | Nút được chọn/rê chuột; cùng khung nút thường. Không vẽ chữ. |
| ui.art.button_pressed | ui/source/button_pressed.9.png | 34×34 PNG RGBA, gồm viền 9-patch 1 px | Nút đang nhấn; cùng khung nút thường. Không vẽ chữ. |
| ui.icon.camera | ui/camera.png | 16×16 PNG RGBA; 1 ảnh | Biểu tượng đã chụp, không flash trắng. |
| ui.icon.record | ui/record.png | 16×16 PNG RGBA; 1 ảnh | Biểu tượng ghi âm; dấu đỏ, không chữ. |
| ui.skin | ui/room304-skin.json | Skin JSON; default-font do game cấp | AI tích hợp tạo từ 5 ảnh khung; không giao artist viết. |
| ui.skin.atlas | ui/room304-skin.atlas | TextureAtlas; split/pad từ 9-patch | File pack sinh ra, cùng basename Skin. |
| ui.skin.page | ui/room304-skin.png | 256×128 PNG RGBA; Nearest; pack không rotate | Trang atlas dự kiến đủ 5 khung; AI tạo khi pack, không vẽ thêm. |

Raw .9.png **không được load trực tiếp rồi mong tự giãn**. AI pack bằng công cụ TexturePacker tương thích libGDX; strip viền điều khiển và ghi split/pad trong atlas. Hiện repo chưa có Gradle task/gdx-tools để pack; kiểm tra công cụ có sẵn trước, không thêm dependency game chỉ để pack. Trang256×128 là kích thước pack đề xuất, chưa có output; nếu pack cần lớn hơn cập nhật master/artist/AI đồng bộ. Pack padding2, không rotate/trim, filter Nearest.

Khung66×66: ruột64×64, border1, giữ8px mỗi góc, vạch top/left và vùng bottom/right9..56. Khung34×34: ruột32×32, giữ6px, đoạn7..26. Vùng trong không được đặt chữ vĩnh viễn. Button focus cần tương phản bằng viền, không chỉ đổi màu rất nhẹ.

| Skin resource | Nguồn / liên kết | Consumer |
| --- | --- | --- |
| Drawable dialogue-frame | NinePatch từ dialogue_frame | DialogueUI: nền khung640×gradientHeight. |
| Drawable menu-frame / hud-frame | Cùng NinePatch panel_frame, alias drawable; không tạo ảnh trùng | UiPanel / DialogueHud. |
| TextButtonStyle default / choice / hud | up button_normal, checked/over button_focus, down button_pressed; default-font | Menu/pause/notes / lựa chọn thoại / nút HUD. |
| LabelStyle default / dialogue / speaker / hud | default-font, màu đủ tương phản | Tiêu đề menu / thân thoại / tên / HUD. |

`GameAssets.optionalSkin(font)` đọc ui.skin JSON và cùng basename .atlas; atlas cache trong AssetManager. Skin mỗi view mượn default-font, không định nghĩa lại BitmapFont default-font trong JSON; tháo font trước dispose Skin. Page atlas do atlas quản lý; không load lại bằng một Texture mới. Drawable có thể là NinePatch/region/texture/sprite đã nạp.

Camera/record icons không nằm trong atlas Skin ở phương án này: `story-visuals.json.cues[].asset` tương ứng effect camera/record. Từng icon16×16 vẽ native px. `record` hiện có chữ nhưng chưa có waveform động; không giao một spritesheet waveform không có consumer.

## Không cần file ảnh riêng

UI-NAMEFX (tên Linh mờ/rõ), E/outline, địa điểm/thời gian, subtitle, objectives, choices/text, notes/transcript, controls, sliders/audio/save/load, tên ending đều là chữ/hình học. Nameplate dùng LabelStyle speaker trên khung thoại; không có hook drawable nameplate riêng. Document viewer dùng ảnh giấy + chữ/phân trang; không có hook document-frame riêng. Inventory/grid icon không có trong gameplay chính. Không yêu cầu artist vẽ các bộ này lần hai.

Ảnh nền menu/A/B/C optional từ story-visuals.menus; nếu có, UiPanel.background thay menu-frame và kéo vào canvas 640×480. Giữ phần giữa ít chi tiết, không bake tiêu đề. Nếu không làm CG riêng giữ fallback; muốn bỏ dùng slot ảnh có thể bỏ key menus tương ứng trong config sau khi kiểm tra parser, không xóa manifest cũ tùy tiện.

## Giấy/ảnh và chữ

| ID | Path | Px PROPOSED | Nội dung |
| --- | --- | --- | --- |
| document.msg | documents/msg.png | 640×480 | Khung điện thoại nhận tin thầy Hùng; không viết sẵn nội dung tin. |
| document.photo | documents/photo.png | 640×480 | Ảnh lớp 12A3: đúng 33 học sinh đếm được, mép chú thích bong; không thêm tên. |
| document.register | documents/register.png | 640×480 | Trang sổ 11/09/2015, hai lớp mực có mặt/vắng ở dòng Nga; không vẽ tên Linh cố định. |
| document.news | documents/news.png | 640×480 | Mẩu nội san học sinh, giấy cũ; chữ cảnh báo do game hiện. |
| document.gate | documents/gate.png | 640×480 | Bản ghi bà Năm: 32 người ngoài sân, không phải danh sách 32 tên. |
| document.report | documents/report.png | 640×480 | Báo cáo sơ tán sai; không vẽ cảnh cháy hoặc nạn nhân. |
| document.inventory | documents/inventory.png | 640×480 | Phiếu thu hồi 01 băng từ máy ghi âm tại 304. |
| document.signed | documents/signed.png | 640×480 | Tờ mới năm 2025; chữ ký Hùng, không tự đặt ngày/tháng. |
| document.family | documents/family.png | 640×480 | Nga và Đức trước 2015; Đức là em nhỏ (7 tuổi lúc Nga mất). |
| document.tape | documents/tape.png | 640×480 | Cận băng gốc duy nhất; nhãn Tập phát thanh — 12A3 — 11/09/2015. |
| document.battery | documents/battery.png | 640×480 | Cận khay và bộ bốn pin AA 1,5 V, dấu +/− rõ, không thêm kiểu pin. |
| document.c_phone | documents/trang_phone.png | 180×245 | C: điện thoại Trang nhận tin Linh; nền và bong bóng trống, chữ do game hiện. |

Bản cận tài liệu640×480 có chi tiết chính nửa trên, lề24; image Scaling.fit sau lớp world nhưng trước khung chữ/dim. Đừng đưa toàn bộ transcript vào PNG: các dòng DOC-* đã được game hiển thị. Chỉ vẽ dấu mực/có mặt bị vắng đè, ngày và chữ ký khi cần nhận biết. Ảnh lớp33 người và ảnh gia đình2 người không được “sửa lịch sử” ở C; chú thích A nằm ngoài ảnh gốc. DOC-EQUIP là nhãn/chi tiết trên prop.crate, không cần ảnh cận thứ hai.

Điện thoại C180×245 vẽ ép đúng rectangle trong drawEffects; text Linh/tin nhắn ở C-02-MAIN-TXT12/13 hiển thị bởi DialogueUI, không được bake thêm vào PNG. Bố trí phone effect và khung chữ cần xem thực tế vì chỉ asset chưa tạo chuyển camera sang Trang.

## Nghiệm thu còn mở

- [ ] Font Việt không ô vuông, không cắt dấu; không giả định glyph mũi tên đầy đủ.
- [ ] Khung/nút không méo góc, focus bằng bàn phím thấy rõ.
- [ ] Portrait không che chữ; cận ảnh vẫn đếm33; hai lớp mực nhận biết được.
- [ ] 640×480,1280×960,800×600,960×540,1280×720; resize trong choice/notes/pause.
- [ ] Enter/E không trùng input, load/save/pause hoạt động như trước.
