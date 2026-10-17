# Tổng quan sản xuất asset Room304

Đối chiếu ngày 11/10/2026: CODEX, AGENTS/AGENTS.md, SDD, kịch bản/flow 1.0, sơ đồ PDF 5 trang (đã trích xuất chữ), hai tài liệu bàn giao và runtime/config liên quan. PDF khớp flow 1.0; SDD có thông tin cũ về save/C/API, dùng runtime cho kỹ thuật và flow cho nội dung. Không sửa nguồn trong lần lập tài liệu này.

- Artist mở [FOR_ARTISTS.md](FOR_ARTISTS.md): tất cả file cần vẽ/thu và thông số.
- AI mở [FOR_AI_INTEGRATION.md](FOR_AI_INTEGRATION.md): mapping, giới hạn và quy trình import.
- [01_ASSET_MASTER_LIST.md](01_ASSET_MASTER_LIST.md) là danh mục chuẩn; [07](07_ASSET_PRODUCTION_CHECKLIST.md) theo dõi nghiệm thu.
- [Đợt nhập thử 13/10/2026](09_IMPORTED_ASSETS_2026-10-13.md) ghi file thật, quy cách ngoại lệ, checksum và kết quả kiểm tra của các ảnh đã nhận.

## Cập nhật nhập thử — 13/10/2026

Đã nhận năm ảnh nhân vật và ảnh phòng 304 trong `added_asset/`; giữ nguyên toàn bộ file nguồn. Linh, Đức và Trang dùng sheet 192×32, sáu frame 32×32, 0,15 giây/frame. Bà Năm và thầy Hùng dùng ảnh 32×32, một frame/1 giây để test idle. Đây là bộ thử hướng xuống, khác quy cách sản xuất 32×48/hai frame đề xuất bên dưới; các hạng mục sản xuất còn thiếu và gate nghiệm thu vẫn mở.

Phòng 304 dùng nền PNG RGBA kín 640×480, xuất 1× và lọc Nearest, được imagegen vẽ lại theo artstyle ảnh nguồn. Bố cục tám bàn và geometry của lần nhập trước giữ nguyên. Checksum bản cuối, prompt và công cụ xuất ghi tại biên bản 09; giữ chân 18×10 và tốc độ 120. Không sản xuất các TMX/tileset hoặc asset khác còn thiếu trong đợt này. Build/core/desktop đã qua, đã xem ảnh 1×/2× Nearest và kiểm ánh lạnh trên PNG; bằng chứng ở 09 không thay nghiệm thu bộ art đầy đủ.

Các số lượng sau là **snapshot kế hoạch sản xuất ngày 11/10/2026**, không phải kiểm kê trạng thái file sau đợt nhập thử.

Số lượng từng nhóm và phân bố P0/P1/P2 xem tại [danh mục chuẩn](01_ASSET_MASTER_LIST.md).

**152 đầu ra sản xuất**, gồm 115 PNG và 37 audio. **8 file kỹ thuật** do người dựng map/AI tạo; **2 file font/OFL đã có**. Tổng 162 mục; 160 thiếu. 53 sheet nhân vật / 204 frame (kể cả ảnh tĩnh một frame); trong đó 16 sheet walk NPC / 96 frame là P2 chưa có chuyển động tự động. Các số này tính cả 6 portrait phụ và 4 CG tùy chọn. Không phải toàn bộ đều bắt buộc trước bản art đầu.

## Quyết định sản xuất đề xuất

Giữ 640×480, tile 32×32, nhân vật 32×48 và 4 phòng 20×15 ô. Dựng bốn TMX dùng chung một tileset 512×256; **không giao vẽ thêm bốn PNG toàn cảnh**. Props có trạng thái đặt riêng. Portrait 150×175 để lên đúng khung 300×350 ở 2×; idle 2 frame/2 FPS, walk 6 frame/8 FPS. Đây là PROPOSED cho art cuối, dựa trên kích thước runtime hiện tại.

## Thứ tự bàn giao

1. Duyệt diện mạo sáu nhân vật, bảng màu và quy cách PROPOSED; làm Linh idle/walk xuống + một portrait + góc phòng 304 trước.
2. P0: bộ Linh, idle NPC/Nga, tileset/bốn phòng, ảnh 33 người/sổ hai lớp mực, máy–thùng–tủ và các giấy gốc.
3. P1: cue pose, Skin, âm thanh sự kiện/giọng, điện thoại/pose C.
4. P2: NPC walk, biểu cảm phụ, CG menu/ending khi cần.

Gameplay A/B/C và save đã có trong báo cáo tiến độ; **không coi đó là nghiệm thu art/audio**. Một số trình bày cần nối thêm code có phạm vi nhỏ: lớp trước nhân vật, cửa hiện/khay mở theo cue, ánh sáng TMX, vạch âm ghi, chuyển góc nhìn C. Chi tiết trong hướng dẫn AI; chưa triển khai ở nhiệm vụ tài liệu này.
