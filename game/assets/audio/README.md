# Âm thanh

Các thư mục hiện chỉ có `.gitkeep` để Git giữ thư mục trống. Chưa có asset âm thanh.

## C0-01: nền trường sau giờ học

Thêm hai file OGG Vorbis vào `ambience/` theo đúng tên dưới đây rồi khởi động lại game. `OpeningAmbience` tự nạp từng file nếu tồn tại; thiếu một hoặc cả hai file vẫn chơi được và không báo lỗi.

| Đường dẫn tính từ `game/assets/` | Nội dung | Âm lượng trong game |
| --- | --- | --- |
| `audio/ambience/school_birds.ogg` | Chim ngoài sân trường, nhẹ, tự nhiên | 15% |
| `audio/ambience/school_distant_voices.ogg` | Vài người nói chuyện rất xa, không nghe rõ lời | 8% |

Cả hai file phát lặp, vào nhẹ trong 2 giây khi chọn **Bắt đầu**, dừng khi rời mở màn để vào cảnh chơi hoặc về menu; pause/resume theo ứng dụng. Âm thanh trong cảnh chơi sẽ được bổ sung ở task sau. Chuẩn bị loop liền mạch, không có khoảng lặng hay tiếng bật ở điểm nối, âm lượng nguồn cân bằng và không clipping. Khi có asset thật, nghe thử rồi chỉnh mức trong `game/core/src/main/java/com/room304/audio/OpeningAmbience.java` nếu cần.

Không dùng nhạc, tiếng điểm danh, tiếng khóc, tiếng ghế kéo hoặc hội thoại có nghĩa trong C0-01. Nội dung cốt truyện luôn đọc được trên màn hình dù chưa có âm thanh.

## Sound effects

`sfx/` dành cho các hiệu ứng đơn lẻ ở các cảnh sau. C0-01 chưa sử dụng thư mục này. Chỉ thêm asset và nối mã phát khi triển khai cảnh tương ứng; không tự phát tiếng click hoặc hiệu ứng kinh dị ở đoạn mở màn.

Khi bổ sung asset từ bên ngoài, ghi nguồn, giấy phép và credit ở đây. Không cần sửa `game/assets/assets.txt` bằng tay: Gradle sinh lại danh sách khi build.

### Danh sách SFX đã tích hợp

- `NPCs_and_Player_Dialogue.wav`: Hiệu ứng gõ chữ cho lời thoại nhân vật (`audio.sfx.dialogue`).
- `Object_Interactions.wav`: Hiệu ứng gõ chữ cho tương tác đồ vật / chỉ dẫn (`audio.sfx.interaction`).
- `door_slam.wav`: Hiệu ứng âm thanh mở/đóng cửa gỗ (`audio.sfx.door`), nguồn từ freesound_community (#46791). Định dạng WAV PCM 16-bit mono 44.1 kHz. Chỉ phát khi mở cửa di chuyển giữa các phòng (`door.304`, `door.hall`, cửa bảo vệ sau khi hiện `guard.wall`, cửa phòng thầy Hùng `door.hung` khi đã mở khóa). Tuyệt đối không phát khi tương tác với bức tường ở đầu game, cầu thang hoặc khi cửa còn đang khóa.
