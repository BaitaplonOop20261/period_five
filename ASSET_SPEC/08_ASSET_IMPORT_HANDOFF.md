# Phụ lục bàn giao kỹ thuật

## Bổ sung đợt nhập thử — 13/10/2026

[Biên bản 09](09_IMPORTED_ASSETS_2026-10-13.md) cập nhật file, checksum và bằng chứng kiểm tra của đợt nhập thực tế; phần kiểm kê 11/10 bên dưới giữ lại làm lịch sử đặc tả. Giữ nguyên sáu nguồn PNG trong `added_asset/`, chỉ đổi tên/đường dẫn bản runtime.

- Linh, Đức và Trang: sheet 192×32, sáu frame 32×32, 0,15 giây/frame. Bà Năm và Hùng: ảnh 32×32, một frame/1 giây dùng idle tĩnh để test. Canvas của bộ thử là 32×32; đây là ngoại lệ so với bộ sản xuất 32×48/hai frame, chưa nghiệm thu đủ animation.
- Phòng 304: imagegen vẽ lại theo artstyle ảnh nguồn; bản runtime là PNG RGBA kín 640×480, xuất 1× và lọc Nearest. Bố cục tám bàn và geometry đã nhập giữ nguyên. Bản cuối, checksum, prompt và công cụ xuất đã ghi tại 09. Dùng slot `map.story.room_304` với geometry JSON; chưa sản xuất các TMX/tileset còn thiếu.
- Bounds va chạm/tương tác, spawn và NPC slot của phòng được căn theo ảnh chỉnh sửa; footprint nhân vật 18×10, tốc độ 120 và luật truyện giữ nguyên. Ảnh kín cần kiểm khả năng nhìn rõ đối tượng, lối đi và hiệu ứng lạnh trong runtime.
- Build/core/desktop qua, gồm năm sheet thật, 58 tương tác E, A/B/C và save/load; đã xem screenshot và kiểm RGB nền lạnh. Bằng chứng và giới hạn ghi tại 09. Không đóng gate/checklist sản xuất hoặc các nhóm asset thiếu; không suy nghiệm thu art cuối từ bộ thử.

## Biên bản kiểm kê

Ngày 11/10/2026. Đã đối chiếu CODEX, AGENTS/AGENTS.md, SDD, các phần phạm vi/flow/nhịp hình–âm/chuỗi tài liệu/danh mục asset/phụ lục C của kịch bản 1.0, sơ đồ PDF năm trang qua trích xuất văn bản, tiến độ và hướng dẫn root. Đã đọc manifest, world, visuals, audio, layout, content và các source trực tiếp nạp/vẽ tài nguyên. Không kiểm hình trang PDF và không tạo PNG; sơ đồ chỉ dùng đối chiếu nội dung.

SDD có link tài liệu cũ không hiện diện trong AGENTS; không coi đó là nội dung cần tự sáng tác bổ sung. Kịch bản hợp nhất là căn cứ nội dung; source hiện tại là căn cứ API. Git có nhiều thay đổi/xóa/di chuyển từ trước; lần này chỉ tạo 11 Markdown trong ASSET_SPEC, giữ nguyên các thay đổi đó, không commit/push.

## Sai khác cần nhớ so với mô tả root

| Điểm dễ hiểu sai | Kết quả đối chiếu |
| --- | --- |
| Portrait expressions / height / maxWidth | Field đúng: portraits, portraitHeight, portraitMaxWidth, portraitCenterX, portraitBottom. |
| Điện thoại C “fit 180×245” | drawEffects dùng batch.draw trực tiếp 180×245; xuất đúng tỷ lệ, hoặc bổ sung fit trong presentation nếu sau này thay quy cách. |
| Mọi variant đã hoạt động | Chỉ key do propState trả về được sử dụng. Tủ mở, cổng và tường thành cửa cần binding presentation bổ sung. |
| TMX chỉ cần chép file | Geometry loader đã có; foreground, vạch phấn sau map và ánh lạnh cần kiểm/sửa presentation khi dùng nền kín. |
| sourceRows tự diễn mọi hành động | Đây là dữ liệu đối chiếu nguồn. StoryVisuals/StoryAudio chỉ chạy bindings được cấu hình rõ. |
| NPC walk tự hoạt động | Profile nhận walk nhưng placements không sinh đường đi. 16 sheet walk NPC là P2. |
| C5 phát lại cả băng hoặc ba câu | C5-04-MAIN-D04/D05 chỉ có hai lời đầu; không phát lời thứ ba/kêu cứu. |
| 25 visual cue đều khớp nguồn | C5-05-FULL-E01 và C-02-MAIN-E02 đang có effect camera, nhưng nguồn không yêu cầu chụp ở hai nhịp này. |
| Thiếu font Việt | TTF 569208 byte, header hợp lệ; cmap có các glyph Việt mẫu, OFL là SIL 1.1. Không cần sản xuất lại font. |

Không sửa hai tài liệu root trong nhiệm vụ này vì giới hạn chỉ ASSET_SPEC. Agent thực hiện import sau cần cập nhật chúng nếu được giao sửa dự án.

## Các quyết định cần duyệt

| Hạng mục | PROPOSED | Điểm duyệt |
| --- | --- | --- |
| Animation | 32×48; idle 2 frame/2 FPS; walk 6 frame/8 FPS; pose riêng theo master | Cadence và diện mạo sáu người; duyệt một mẫu trước. |
| Portrait | 150×175, Nearest; khung hiển thị 300×350 | Pixel style và biểu cảm phụ P2. |
| Môi trường | Bốn TMX 20×15; tile 32; sheet chung 512×256 | Thử phòng 304 trước; không làm thêm bốn PNG trùng cảnh. |
| Đồ vật | Kích thước bám rectangle kiểm thử | Kiểm góc nhìn nhỏ có đủ đọc; nếu muốn hình cao hơn phải tách metadata render, không đổi collision theo ảnh. |
| UI | Năm nine-patch, hai icon; atlas dự kiến 256×128 | Màu, tương phản, vùng giãn; pack khác kích thước phải cập nhật đồng bộ. |
| Audio | OGG stereo 44,1 kHz; WAV mono PCM16/44,1 kHz; peak ≤ −3 dBFS | Diễn viên, nguồn, giấy phép, mix thật. Full voice-over chưa chốt. |
| C / CG | Chuẩn bị pose/phone/voice C; bốn CG là P2 | C vẫn tắt mặc định nếu chưa có chỉ đạo khác; có ảnh C không đồng nghĩa được tự bật C. |

UNDECIDED: diện mạo, đồng phục, bảng màu cụ thể, người thu âm và quyền sử dụng nguồn. Không thêm nội dung để lấp những quyết định nghệ thuật này.

## Ma trận bao phủ nguồn

| Nhóm yêu cầu trong flow | Tài nguyên hoặc cách thể hiện |
| --- | --- |
| CHAR-LINH / TRANG / DUC / NAM / HUNG / NGA | 53 sheet, 12 portrait; call/nam_door/tape không tạo thêm nhân vật. |
| MAP-304 / HALL / GUARD / HUNG | Bốn TMX + school.tsx/png; ngoài cổng cùng GUARD_ROOM, sân chỉ qua cửa sổ. |
| PROP-DESKS / BOOKS / KEYBOARD / CHALK | Tileset chứa bàn ghế nền, sách/hộp, bảng chìa, khay phấn; props bàn cuối/ghế/cửa sổ; vạch phấn là geometry. |
| PROP-REGISTER / CABINET / PHOTO / FAMILY | Props trạng thái và document.register/photo/family; ảnh gốc không thay người/tên. |
| PROP-PLAYER / TAPE / BATTERY / CRATE / INVENTORY | Máy/thùng và tài liệu tape/battery/inventory; nhãn EQUIP nằm trên thùng, không thêm ảnh cận trùng chức năng. |
| PROP-PAPERS / KEY / BOARD / EVIDENCE | papers/evidence/signed, giấy gate/report/signed/news; chìa trong pose bà Năm và tileset, không inventory icon thừa. |
| FX-MEMORY / A | effect.memory, Nga walk, giọng “Có.”, chuông thường; ảnh 33 người giữ nguyên, chú thích mới là chữ. |
| B | Tủ khóa/cổng/chìa/tiếng rung/điểm danh; giữ chứng cứ đã biết, không cảnh mới. |
| C | Linh seated, Trang phone, document.c_phone, giọng Linh/“Đủ rồi.”; tên Linh là lớp chữ hiện tượng, không thay giấy/ảnh gốc. |
| UI-DIALOG / DOC / CHOICE / HINT / SUB / RECORD / NAMEFX / TITLE / NOTES / PAUSE / END | Năm nine-patch, hai icon, chữ/hình học dùng chung và CG P2; không bộ inventory hay ảnh từng nút. |
| AUD-SCHOOL / WIND / MESSAGE / DOOR / STEP / PAGE / CAMERA / BELL / BELL-NORMAL / CHAIR / CALL / KNOCK / LOCK / TAPE / ALARM / RUN / WRITE | 37 file theo cue thật; chalk/button/lid/paper xuất phát từ các nhịp nguồn, không BGM tự sáng tác. |

## Kết quả kiểm tra tài liệu

- Đủ 11 file yêu cầu. Danh mục chính có 162 ID/path duy nhất: 152 file sản xuất (115 PNG + 37 audio), 8 file kỹ thuật, 2 file font/OFL có sẵn. Năm slot cũ dự phòng được tách khỏi số lượng sản xuất.
- 53 sheet nhân vật, 204 frame: kích thước sheet đều bằng frameWidth × frameCount trên một hàng; FPS dương. Trong đó có 4 sheet tĩnh một frame và 16 sheet walk NPC P2.
- Mọi ID dành cho artist đều nằm trong master; mọi mục master nằm trong chỉ mục AI và checklist. Ba output Skin/atlas kỹ thuật không giao artist tự viết.
- Các ID đã có manifest giữ nguyên path. Tất cả cue audio được liệt kê đều tồn tại trong content.dialogues; DOC-prefix dùng cho ảnh đều có chuỗi nguồn.
- Font được kiểm header/cmap với mẫu tiếng Việt và đọc giấy phép; chỉ hai mục này được đánh dấu có sẵn.
- Đã kiểm liên kết Markdown nội bộ, cặp code fence, số lượng và checksum 25 file nguồn/tài liệu/config đã theo dõi: không thay đổi.

**Chưa chạy build, desktop hoặc nghiệm thu nghe nhìn trong nhiệm vụ tài liệu này.** Không có file art/audio mới để kiểm trong game; các gate import tại 07 vẫn để trống. Lệnh kiểm thử trong hướng dẫn AI là quy trình cho đợt tích hợp sau, không phải chứng nhận đã chạy lần này.
