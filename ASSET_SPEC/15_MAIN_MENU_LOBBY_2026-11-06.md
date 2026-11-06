# Lobby đầu game — 06/11/2026

`game/assets/ui/menu.png` là ảnh nền pixel art và logo tĩnh “CĂN PHÒNG 304 — BUỔI ĐIỂM DANH CUỐI CÙNG”, tạo bằng công cụ ImageGen tích hợp từ mẫu đã được người dùng duyệt. Ảnh trường học và logo là hình mới tạo; ảnh Dead Cells do người dùng cung cấp chỉ được tham khảo bố cục menu bên trái và nền lớn, không đưa logo, nhân vật hay artwork Dead Cells vào game.

Các nút không nằm trong ảnh: `MenuScreen` gọi `UiPanel.lobby` để vẽ và xử lý **Play → Cài đặt → Hướng dẫn → Thoát** bằng Scene2D. Cài đặt mở mục Âm thanh/Quay lại; âm thanh dùng controller đã có. Esc quay lại từng trang. Canvas lobby 960×540 dùng FitViewport để giữ ảnh và vùng bấm cùng tỉ lệ; thoại/pause giữ canvas riêng 640×480. Menu ending vẫn dùng modal.

Play luôn bắt đầu lượt mới, có mở màn. Phiên bản này không hiện Continue kể cả khi có save; file save được giữ, lưu/tải qua pause vẫn còn. Tham số save/load của constructor MenuScreen giữ tương thích cho bản Continue sau.

Manifest dùng ID `background.menu`, path `ui/menu.png`, filter Nearest và optional fallback có chữ nếu thiếu ảnh. AssetManager sở hữu texture; UI mượn texture, không giải phóng ảnh dùng chung. Không tạo font/texture khi vẽ mỗi frame.

## Prompt tạo asset cuối

Công cụ: **ImageGen tích hợp**, chế độ chỉnh ảnh từ mockup đã duyệt; nền không trong suốt. Input: mockup đầu tiên của lobby đã được người dùng chấp nhận. Output được copy vào `game/assets/ui/menu.png`, không dùng đường dẫn sinh ảnh cá nhân ở runtime.

```text
Precise production asset edit of this approved original game title-screen concept. Output the BACKGROUND ART WITH THE STATIC GAME LOGO ONLY, for use as a game texture. Preserve the full original landscape composition, dimensions/aspect ratio, pixel art Vietnamese school at orange sunset, tree silhouettes, weathered school building, lit upper right window, dark quiet left third, and the complete upper-left title logo and subtitle exactly unchanged: 'CĂN PHÒNG', '304', 'BUỔI ĐIỂM DANH CUỐI CÙNG', including the horizontal gold title divider. Remove ALL of the interactive menu UI below the subtitle: the selected Play rectangle, triangle, the words Play, Hướng dẫn, Âm thanh, Thoát, all menu row lines, all button decoration, and the bottom-left keyboard hint. Reconstruct only the existing dark tree-shadow background in those removed areas, maintaining the same original smooth dark vignette for later code-rendered buttons. The region below the subtitle must contain NO text, NO buttons, NO menu outlines and NO footer. Only the top-left static title/logo remains as text. Do NOT add 'Cài đặt', Continue, version numbers, extra labels, or new objects. Preserve the image's professional pixel artwork, lighting, color palette, existing school architecture and original approved title composition with maximum fidelity. This clean art will be integrated into the game; all menu actions and keyboard hints will be drawn separately by code.
```
