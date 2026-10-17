# Tỉ lệ phòng học — 17/10/2026

Theo yêu cầu thu nhỏ phòng và đồ vật vì bàn cao hơn nhân vật, `ROOM_304` và `CLASSROOM` giảm world từ **640×480 xuống 320×240**, tức 50% mỗi chiều. Profile/canvas/scale/footprint/tốc độ của player và từng NPC giữ nguyên so với trước thay đổi này; không sửa các sheet nhân vật. `HALLWAY` và `GUARD_ROOM` giữ dữ liệu cũ.

Ảnh nền `game/assets/maps/room_304.png` vẫn là PNG nguồn runtime 640×480; renderer vẽ vào world 320×240 với Nearest. Không tái sinh hoặc chỉnh sửa nội dung bitmap. Camera FitViewport vẫn phủ khung chơi, nên nhân vật chiếm tỉ lệ lớn hơn so với phòng và bàn ghế. Tám cụm bàn/ghế giữ bố cục.

## Hình học

| Thành phần phòng 304 | Trước | Sau |
| --- | --- | --- |
| World | 640×480 | 320×240 |
| Bàn giáo viên — bounds tương tác | 101×62 | 50,5×31 |
| Bàn/ghế thường — vùng va chạm | 60×63 | 30×31,5 |
| Bàn cuối — bounds tương tác | 55×57 | 27,5×28,5 |
| Tầm E đồ vật | 44 | 22 |
| Canvas Linh | 32×32 | 32×32 |
| Vùng chân Linh | 18×10 | 18×10 |

Bounds đồ vật và collision được nhân 0,5. Spawn, NPC slot và placement có tọa độ riêng được thu theo tâm vùng chân, có bù footprint giữ nguyên: `newX = oldX × 0,5 − footWidth × 0,25`, tương tự cho y. Spawn `ROOM_304/desk` được đặt tại `(140,149)` để tránh mép cụm bàn sau khi thu phòng. Các spawn được validator và kiểm tra desktop xác nhận dùng được.

Tầm NPC trong phòng đi theo tỉ lệ scene. Lối giữa vẫn đi được với footprint 18×10; tầm E, marker, nhãn đồ vật, nhãn nhân vật, camera/record/chair effect và offset pose đều được điều chỉnh theo scene. Ảnh cận cảnh/UI thoại vẫn dùng canvas riêng 640×480.

Tọa độ trong [biên bản 09](09_IMPORTED_ASSETS_2026-10-13.md) là bố cục trước thay đổi. Tọa độ hiện tại lấy từ `game/assets/config/story-world.json`. Bản lưu cũ chứa tọa độ 640×480 có thể bị validator từ chối nếu nằm ngoài phòng hoặc trong vật cản mới; không di chuyển hoặc xóa bản lưu tự động.

## Kiểm tra đã chạy

- `game/gradlew.bat -p game build --console=plain`: exit 0; compile, đóng gói và core regression đạt 99/99 khối truyện.
- `game/gradlew.bat -p game lwjgl3:storyDesktopTest --console=plain`: exit 0; 63 tương tác E bằng đi bộ/collision thật, 10.896 frame, hoàn tất A/B/C, save/load, pause, ghi chú, UI cận cảnh và resize 16:9/4:3.
- Kiểm tra renderer xác nhận bàn giáo viên cao 31 world unit, thấp hơn canvas Linh 32; không tăng kích thước player để đạt tỉ lệ.
- Đối chiếu cấu hình trước/sau: player/NPC profile và hai khu vực ngoài phòng học giữ nguyên dữ liệu.
- Đã xem ảnh thật `game/lwjgl3/build/story-explore-16x9.png` và `story-room304-five-characters-16x9.png` để kiểm tra tỉ lệ, vị trí người và nhãn.
- Windows / Temurin 21.0.12.1 / Gradle Wrapper 9.8.0. Không sửa cấu hình toolchain hoặc kiểm tra JDK của project.
