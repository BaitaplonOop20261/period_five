# Dialogue Presentation — Room304

Một `DialogueUI` dùng chung cho tất cả hội thoại trong `GameScreen`. UI vẽ bằng Scene2D trên `FitViewport` 960 × 540, độc lập với camera world. Portrait ở trung tâm, tên và chữ ở dưới trái, background phủ viewport và HUD thời gian/địa điểm ở trên trái. Không có khung RPG hoặc nút lựa chọn giả.

## Trách nhiệm và file

- `Dialogue`, `DialogueLine`, `DialogueSpeaker`, `DialogueChoice`: dữ liệu JSON; không chứa nhiệm vụ, điều kiện hay code thực thi.
- `DialogueLoader`: kiểm tra nội dung, speaker, ID trùng và liên kết `next`.
- `DialogueController`: bắt đầu/kết thúc, câu hiện tại, liên kết câu và lựa chọn đã chọn. Không phụ thuộc renderer hoặc Player.
- `DialogueUI`: layout, background, portrait, typewriter, xuống dòng/phân trang, hiển thị lựa chọn thực sự có trong dữ liệu.
- `DialogueHud`: hiển thị giờ/địa điểm và phát yêu cầu Tiếp tục/Menu. Không tự thay trạng thái game.
- `DialogueLayout`: cấu hình chung trong `ui-layout.json`.
- `DialogueAssets`: sở hữu font/chrome UI và mượn background/portrait từ `GameAssets` cấp ứng dụng. Nạp khi đổi câu; đóng thoại tháo drawable, giữ cache tới khi ứng dụng dispose. Constructor độc lập sở hữu cache riêng. Xem [manifest và bàn giao](../../../ASSET_INTEGRATION_GUIDE.md).
- `GameScreen`: điều phối F/Enter, yêu cầu chuột, khóa di chuyển và pause. Render không tiến hội thoại hoặc thay state.

`DialogueView` thử nghiệm đã được xóa. Không có adapter hoặc đường render UI cũ.

| Thao tác | File trong lần triển khai này (tính từ gốc repository) |
| --- | --- |
| Tạo presentation | `game/core/src/main/java/com/room304/dialogue/DialogueUI.java`, `DialogueHud.java`, `DialogueAssets.java`, `DialogueLayout.java` trong cùng thư mục |
| Tạo dữ liệu mở rộng | `game/core/src/main/java/com/room304/dialogue/DialogueSpeaker.java`, `DialogueChoice.java` trong cùng thư mục |
| Sửa dữ liệu/controller/loader | `game/core/src/main/java/com/room304/dialogue/Dialogue.java`, `DialogueLine.java`, `DialogueController.java`, `DialogueLoader.java` trong cùng thư mục |
| Tích hợp | `game/core/src/main/java/com/room304/screen/GameScreen.java` |
| Xóa UI cũ | `game/core/src/main/java/com/room304/dialogue/DialogueView.java` |
| Tạo cấu hình/asset | `game/assets/dialogue/ui-layout.json`, `game/assets/portraits/student_placeholder.png` |
| Sửa ví dụ đang chạy | `game/assets/dialogue/npc_trang_intro.json` |
| Bổ sung kiểm tra | `game/core/src/regression/java/com/room304/RegressionChecks.java`, `game/lwjgl3/src/regression/java/com/room304/lwjgl3/GameplayRegressionChecks.java` |
| Tài liệu | Sửa `README.md`; tạo `game/assets/dialogue/README.md`, `game/assets/portraits/README.md` |

Movement, collision, animation, NPC/selector, bản đồ và snapshot bàn phím không thay đổi logic trong lần triển khai này. Không thêm dependency.

## Thêm hội thoại hoặc NPC

Tạo một JSON mới, nạp bằng `DialogueLoader.load("dialogue/<ten>.json")` rồi truyền vào constructor `Npc` hiện có. Không cần tạo class UI, copy layout hoặc sửa renderer. `WorldMap` vẫn quyết định NPC nào có mặt và vị trí sprite; điều kiện mở thoại thuộc gameplay/story.

```json
{
  "id": "npc_minh_intro",
  "time": "17:30",
  "location": "Hành lang",
  "background": "maps/hall_background.png",
  "speakers": {
    "minh": {
      "name": "Minh",
      "portraits": {
        "normal": "portraits/minh_normal.png",
        "happy": "portraits/minh_happy.png"
      },
      "portraitCenterX": 480,
      "portraitBottom": -12,
      "portraitHeight": 518,
      "portraitMaxWidth": 510
    }
  },
  "lines": [
    { "id": "hello", "speakerId": "minh", "text": "Chào cậu!", "next": "bye" },
    { "id": "bye", "speakerId": "minh", "expression": "happy", "text": "Hẹn gặp lại.", "next": "END" }
  ]
}
```

- `next` là ID câu hoặc `END`; bỏ `next` để đi tới câu kế tiếp trong mảng. ID câu phải duy nhất; `END` được dành để kết thúc.
- `speakerId` trỏ vào `speakers`; tên hiển thị và biểu cảm không phụ thuộc tên class NPC. Một hội thoại có thể đổi người nói qua từng câu.
- `expression` mặc định là `normal`; nếu biểu cảm không tồn tại, dùng `normal`.
- Câu có thể ghi `portrait` hoặc `background` để ghi đè. Bỏ background dùng background chung; nếu cả hai thiếu hoặc file không tồn tại, dùng world đang vẽ phía sau. Portrait thiếu dùng PNG placeholder và ghi log đường dẫn.
- Lời dẫn dùng `"speaker": ""`, không có `speakerId` hoặc `portrait`. Tên trực tiếp qua `speaker` cũng dùng được cho các câu ngắn do gameplay tạo.
- `time` là giờ trong cảnh do người viết chỉ định, không phải đồng hồ hệ thống. Bỏ trường này hiển thị `—`; bỏ `location` dùng vị trí do GameScreen cung cấp.
- Font bắt buộc phải tồn tại; tên file lỗi được báo rõ. Font hiện tại là Noto Sans (giấy phép trong `fonts/OFL.txt`), đã dựng sẵn glyph tiếng Việt. Nội dung không kích hoạt markup của Scene2D.
- Font, tốc độ chữ, số hàng, kích thước portrait và các vùng bố cục chỉnh tại `ui-layout.json`. Vị trí portrait từng speaker có thể ghi đè mặc định; tất cả dùng tọa độ UI.

## Lựa chọn đơn giản

Một câu có thể có `choices` thật; chúng xuất hiện khi đã đọc hết trang cuối. Nhấn Tiếp tục không tự chọn câu trả lời. Ví dụ:

```json
"choices": [
  { "id": "ask", "text": "Hỏi thêm", "next": "answer" },
  { "id": "leave", "text": "Tạm biệt", "next": "END" }
]
```

Click phát ID tới GameScreen rồi `DialogueController.choose(id)` chuyển câu. `getLastChoiceId()` cho phép hệ thống truyện đọc kết quả; UI không tự cấp đồ, đổi tiến độ hoặc ending. Đây là liên kết dữ liệu đơn giản; chưa có điều kiện lựa chọn, callback nhiệm vụ hay scripting framework.

## Chơi thử và kiểm tra

Từ thư mục gốc repo:

```powershell
.\game\gradlew.bat -p game build
.\game\gradlew.bat -p game :lwjgl3:desktopRegressionTest
.\game\gradlew.bat -p game :lwjgl3:run
```

Bắt đầu game, đi gần Trang ở phòng 305 và nhấn F. Ví dụ tích hợp có background, portrait PNG trong suốt, tên và ba câu tiếng Việt. Nội dung này chỉ là thử nghiệm giao diện, không thay cho kịch bản truyện chính thức.

- F tương tác. Khi hội thoại đang mở, F/Enter hoặc nút **HIỆN HẾT** hoàn tất chữ của trang; lần nhấn sau mới chuyển trang/câu.
- Câu dài tự xuống dòng và phân trang; một lần bấm không bỏ qua phần chưa đọc.
- **MENU / Esc** mở pause hiện có. Pause dừng typewriter và gameplay; tiếp tục giữ nguyên câu, trang và phần chữ đã hiện.
- Frame mở/đóng hội thoại đều chặn movement; frame sau khi đóng khôi phục điều khiển. Idle vẫn do Player cập nhật.
- Resize giữ canvas UI và tỉ lệ portrait, thêm letterbox khi cửa sổ không phải 16:9.

Regression desktop kiểm tra các thao tác này cùng thay speaker, narration, asset thiếu, câu dài, lựa chọn, hide/show và glyph tiếng Việt. Ảnh render thật được xuất vào `game/lwjgl3/build/dialogue-checks/` (Git bỏ qua).

Chưa triển khai: auto/skip toàn hội thoại, history, save/load, voice, animation portrait, tích hợp điều kiện quest/ending vào nội dung truyện và điều khiển lựa chọn bằng gamepad. Lựa chọn đã hỗ trợ ↑/↓ để đổi highlight, Enter mới để xác nhận; không tự chọn mục đầu.
