# Camera theo nhân vật — 17/10/2026

Theo lựa chọn của người dùng: giữ khung nhìn 320×240 như phòng 304; map rộng cuộn theo nhân vật. Kích thước world, đồ vật, collision, spawn và profile nhân vật giữ dữ liệu hiện có.

## Hành vi

- `StoryWorldView` luôn dùng `FitViewport(320,240)`, zoom 1. Phòng 304 và lớp bên cạnh 320×240 hiện trọn phòng; hành lang và phòng bảo vệ 640×480 cuộn theo Linh.
- `FollowCamera` theo tâm sprite, dừng tại mép bản đồ theo từng trục. Nếu map bằng hoặc nhỏ hơn khung nhìn, camera căn giữa trục đó. Khi chuyển phòng, lần vẽ đầu tiên bám vị trí xuất hiện mới.
- Resize chỉ đổi vùng hiển thị trên cửa sổ, giữ khung nhìn và tỉ lệ nhân vật. Cửa sổ rộng 16:9 có dải trống hai bên khung game 4:3.
- HUD dùng viewport 640×480 riêng; tên phòng, mục tiêu, hướng dẫn, điện thoại và icon ghi âm/chụp ảnh cố định trên màn hình. Hội thoại/ảnh tương tác tiếp tục dùng layout UI riêng.
- Marker vẫn bám mục tiêu E, giới hạn trong vùng camera hiện thấy. Viền vàng giữ độ dày đồng nhất giữa các map.
- `DemoGameScreen` trong regression source dùng cùng camera cho phòng/hành lang/sân sau. Do demo có world scale ×6, khung nhìn là 1920×1440 world unit, tương đương 320×240 của luồng chính. Sân sau chưa phải một RoomId trong cốt truyện chính.

## Kiểm chứng

- Build/core regression qua; không thêm thư viện hoặc thay cấu hình build.
- `lwjgl3:storyDesktopTest` qua với 67 tương tác E, 12.267 frame và A/B/C. Kiểm tra trực tiếp camera theo X/Y, clamp trái/phải/dưới, giữ kích thước nhân vật, chuyển phòng, pause và resize 16:9/4:3.
- So sánh pixel của tiêu đề HUD trước/sau khi camera cuộn: vị trí chữ giữ nguyên.
- `lwjgl3:desktopRegressionTest` qua, gồm đi từ phòng qua hành lang ra sân sau bằng input thật, giữ khung nhìn cùng tỉ lệ, cuộn khi đi và clamp trái/phải/dưới. Đã xem `game/lwjgl3/build/dialogue-checks/camera-yard-follow.png`.
- Ảnh kiểm tra trong `game/lwjgl3/build/`: `story-camera-hallway-center.png`, `story-camera-hallway-left.png`, `story-camera-hallway-right-bottom.png`, `story-camera-hallway-4x3.png`.

Nền hành lang/phòng bảo vệ của cốt truyện còn thiếu asset optional nên vẫn dùng hình học fallback hiện có. Camera đã hoạt động với các bounds này; thêm art không thay đổi tỉ lệ khung nhìn.
