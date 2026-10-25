> Ghi chú lịch sử: bố trí, nhãn tương tác và mặc định camera đã được điều chỉnh trong lần ghép 24/10/2026. Xem README.md và story-world.json cho hành vi hiện tại.

# Dấu tương tác và viền đồ vật — 17/10/2026

Yêu cầu: bỏ nhãn tên đồ vật/NPC trên bản đồ, dùng asset interaction mark khi đến gần và thêm viền vàng cho object.

## Asset

- Nguồn người dùng cung cấp: `ASSET_SPEC/added_asset/interaction_mark.png`.
- Runtime: `game/assets/ui/interaction_mark.png`, chép nguyên byte, 379 byte. Dùng ID có sẵn `ui.interaction`; path cũ `sprites/interaction_mark.png` được thay bằng path UI mới.
- Sheet RGBA 32×25, bốn frame 8×25 nằm ngang, 0,2 giây/frame, loop; mỗi frame có dấu chấm than đỏ với viền tối và độ cao thay đổi để tạo chuyển động.
- Manifest: required texture, Nearest; animation mượn texture AssetManager cấp app, không tạo hoặc dispose texture từ renderer.
- SHA-256 nguồn và runtime giống nhau; không chỉnh sửa bitmap hoặc sinh lại asset.

## Hành vi và tỉ lệ

Không vẽ tên nổi của đồ vật, NPC hoặc Linh trong world; tên người nói/đồ vật trong UI thoại và chữ phục vụ cốt truyện vẫn thuộc UI/nội dung truyện.

Chỉ mục tiêu gần nhất đang được selector cho phép E nhận dấu. Mục tiêu giữ cùng điều kiện khoảng cách, hướng và tiến độ với input, không hiện nhiều dấu cho các mục tiêu chồng phạm vi. NPC vắng hoặc chưa được phép tương tác không có dấu.

Dấu đặt giữa đồ vật hoặc trên đỉnh sprite NPC, cách khoảng 15% chiều cao dấu, giữ đúng tỉ lệ frame 8:25 và clamp trong camera. Chiều cao bằng 40% canvas nhân vật, giới hạn 10–18 world unit: với nhân vật 32 thì dấu cao 12,8, rộng khoảng 4,1 world unit. Không nhân kích thước dấu theo scale ×6 của demo cũ. Chuyển động dùng trực tiếp bốn frame của nguồn.

Đồ vật đang chọn có một viền vàng rõ và hai lớp glow nhẹ, pulse chậm. NPC chỉ có dấu chấm than. Viền được vẽ trước actor để người đứng trước vật không bị đường vàng đè lên; dấu vẽ trên actor. Đi xa/chọn mục tiêu khác bỏ hiệu ứng cũ. Khi thoại, ghi chú, pause hoặc fade chuyển phòng đang chặn tương tác, không hiện marker/viền.

## Viền đồ vật trên nền ghép sẵn

Phòng 304 có bàn/tủ/cửa sổ nằm trong PNG nền kín, không có alpha riêng cho từng đồ vật. `SceneCatalog.ObjectDef.highlightOutline` nhận các cặp x/y chuẩn hóa 0–1 theo bounds để bám hình đồ vật. Bàn giáo viên, bàn cuối và hai bàn nhỏ bên phải có contour riêng; các vật hình chữ nhật dùng contour mặc định. Đây là hình học trình bày, không thay collision hoặc bounds tương tác. Loader kiểm số cặp, số điểm, tọa độ hữu hạn và phạm vi.

Khi thay asset/bố cục, cập nhật `highlightOutline` theo hình mới trong `story-world.json`. Không dùng ảnh UI cận cảnh để suy ra viền trên bản đồ vì đó là hai góc nhìn khác nhau. Contour tương đối theo bounds cũng đi cùng geometry khi import TMX.

## Kiểm tra

- `build`: exit 0, compile/đóng gói/core regression; 99/99 khối truyện.
- `lwjgl3:desktopRegressionTest`: exit 0, luồng demo và ownership/input vẫn qua với path marker mới.
- `lwjgl3:storyDesktopTest`: exit 0, A/B/C, 64 tương tác E bằng đi bộ, 11.395 frame, save/load, menu/notes/pause/resize.
- Kiểm tra framebuffer thật: không có cue khi ở xa; object có pixel vàng và pixel đỏ của asset; bàn có viền theo contour; NPC có marker nhưng không còn viền vàng của object trước; mở thoại bỏ cue. Kiểm frame 8×25/four-frame thực, resize 16:9 và 4:3.
- Đã xem `game/lwjgl3/build/story-cue-desk-16x9.png`, `story-cue-window-16x9.png` và `story-cue-npc-4x3.png`: không có nhãn tên, dấu đặt trên đúng mục tiêu, tỉ lệ nhỏ hơn nhân vật và viền khớp bàn/cửa sổ.
- Windows / Temurin 21.0.12.1 / Gradle Wrapper 9.8.0; cấu hình toolchain giữ nguyên.
