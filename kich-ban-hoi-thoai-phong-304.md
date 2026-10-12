# Căn Phòng 304 — Kịch bản hội thoại

**Bản hội thoại v0.2 — 08/10/2026.** Viết theo [Buổi Điểm Danh Cuối Cùng, cốt truyện v0.2](demo-cot-truyen-can-phong-304.md), giữ sự thật và sáu cảnh chính của bản đó. Bản này bổ sung mở màn, bối cảnh và dàn cảnh âm thanh cho đoạn đầu. Đây là lời thoại để nhóm đọc, chỉnh và triển khai; chưa phải dữ liệu đã nạp vào game.

Mạch bắt buộc gồm **kết thúc A và B**, theo [phạm vi bản nộp](ke-hoach-ky-thuat-java.md). Kết thúc C được viết riêng ở cuối và chỉ đưa vào game khi đã hoàn thiện. Mục tiêu của bản rút gọn là một lượt khoảng 15–20 phút; thời lượng thực tế cần đo khi chơi thử.

**Bối cảnh trong truyện vẫn là năm 2025**, mười năm sau sự việc ngày 11/09/2015. Nga là chị của Đức. Tài liệu này giữ trong `docs/` trên máy, theo quy ước của nhóm.

## Cách đọc

- **Tên nhân vật:** câu hiển thị trong hộp thoại. Mỗi lượt nói là một nhịp; giao diện có thể xuống dòng hoặc chia trang theo kích thước hộp thoại.
- *[Hình ảnh, âm thanh hoặc hành động]*: chỉ dẫn dàn cảnh, không phải lời nhân vật. Âm thanh cần thiết luôn đi kèm phụ đề hoặc dấu hiệu nhìn thấy.
- **Lựa chọn:** chữ người chơi nhìn thấy. Phần kết quả ngay sau đó dành cho người viết và người lập trình.
- Mã như `C1-01` giúp nhóm tìm đúng đoạn. Điều kiện và kết quả không xuất hiện như thuật ngữ kỹ thuật trong lời thoại.
- Không tự chạy lại đoạn dài mỗi lần nhấn tương tác. Các câu nhắc ngắn ở cuối tài liệu dùng cho lần nói chuyện tiếp theo.

Giọng nhân vật: Linh quan sát và hỏi thẳng; Trang thực tế, có chút hài hước lúc chưa gặp nguy hiểm; Đức nói ít nhưng muốn người khác nhớ đúng về chị; bà Năm kể điều tận mắt thấy; Hùng né tránh trước khi nhận trách nhiệm; Nga chỉ nói những câu ngắn gắn với ký ức.

## Cảnh 0 — Mở màn: Ở lại sau giờ học

### Người chơi cần hiểu gì trước khi được điều khiển?

Người chơi là **Linh, học sinh lớp 12, cùng Trang phụ trách bản tin trường THPT An Bình**. Khu nhà cũ sắp sửa chữa; hai bạn được nhờ chụp ảnh, ghi lại tư liệu trong phòng 304 trước khi đồ đạc được chuyển đi. Họ ở lại vì một công việc bình thường, chưa biết về buổi điểm danh hay trách nhiệm của Hùng.

**Thời điểm được chọn cho phần mở màn:** khoảng **16:40, một buổi chiều năm 2025**, sau giờ tan học. Không chốt thêm ngày/tháng hiện tại. Mốc 16:40 là bối cảnh, không phải đồng hồ giới hạn thời gian chơi.

**Đường đi của đoạn đầu:** màn đen giới thiệu → xem tin nhắn giao việc → hành lang trước cửa 304 → vào phòng → tự khám phá → mở sổ và gặp hiện tượng đầu tiên. Dùng lại bản đồ hành lang và 304; cảnh sân trường chỉ được gợi bằng ánh sáng và âm thanh ngoài cửa sổ.

### C0-01 — Màn đen và địa điểm

**Kích hoạt:** chọn “Bắt đầu” từ menu. Chưa có quyền di chuyển.

*[Màn đen. Tiếng chim và vài tiếng nói chuyện xa bắt đầu rất nhẹ. Không có tiếng điểm danh, tiếng khóc hoặc tiếng ghế tự kéo.]*

Chữ hiện ở giữa màn hình:

> TRƯỜNG THPT AN BÌNH
>
> 16:40 — NĂM 2025

Sau đó thêm dòng:

> Khu nhà cũ sẽ được sửa chữa trong thời gian tới.

**Nhịp trình bày:** chữ hiện dần trong khoảng một giây; người chơi đọc và bấm tiếp. Khoảng 5–8 giây là nhịp dựng tham khảo, không tự tắt chữ khi người chơi chưa đọc xong.

**Âm thanh:** cảm giác một ngôi trường đang vắng dần sau giờ học. Tiếng nói chuyện không có lời rõ nghĩa, không phải hội thoại cần nghe để hiểu nhiệm vụ. Nhạc chưa cần vào; tiếng môi trường đủ tạo nền.

### C0-02 — Vì sao hai bạn còn ở trường?

**Kích hoạt:** tiếp tục từ `C0-01`. Mở một khung tin nhắn đơn giản trên nền tối, không dựng hoạt ảnh điện thoại phức tạp.

*[Tiếng báo tin nhắn một nhịp, mức vừa phải. Tên người gửi: “Thầy Hùng”. Tin nhắn đã nhận trước lúc hai bạn tới 304, không phải thầy gửi trong thời gian bị mắc kẹt.]*

> Hai em chụp giúp thầy tư liệu trong phòng 304 để làm bản tin về khu nhà cũ nhé. Đồ ở đó sắp chuyển đi.
>
> Thầy để sổ lớp cũ trên bàn. Chụp cả trang tên lớp; chụp xong cứ để nguyên đồ. Thầy đang thu xếp sách ở lớp bên cạnh.

**Trang:** Tụi mình chụp xong hôm nay, mai còn chọn ảnh cho bản tin.

**Linh:** Ừ. Tớ muốn có vài tấm trước khi căn phòng bị dọn hết.

**Kết quả:** người chơi biết mục đích, biết sổ do Hùng để ngoài tủ và biết vị trí hiện tại của thầy. Tin nhắn không nói Nga, vụ cháy hoặc dòng điểm danh bị sửa.

### C0-03 — Hành lang trước cửa 304

**Kích hoạt:** đóng tin nhắn. Chuyển từ nền tối sang bản đồ hành lang tầng ba trong khoảng nửa giây. Linh và Trang đứng trước cửa 304.

**Bố cục hình ảnh:**

- Cửa sổ hành lang ở một phía, hắt nắng vàng nhạt thành những dải dài trên sàn. Vẫn còn nhìn rõ nhân vật và biển phòng.
- Tường sơn cũ hơi bong, bảng tin có giấy bạc màu. Gần cửa có vài thùng được dán nhãn chuyển đồ.
- Cửa 304 khép hờ; biển “304” rõ hơn các chi tiết nền. Phía trong tối hơn hành lang vì đồ đạc che một phần ánh sáng, chưa phải tối đen.
- Linh gần cửa hơn, Trang ở sau một bước. Không có bóng Nga, chữ tự hiện hoặc đồ vật tự dịch chuyển ở đây.

*[Tiếng bước chân hai người chậm lại rồi dừng. Tiếng chim và tiếng nói dưới sân vẫn ở xa. Một luồng gió làm giấy trên bảng tin rung nhẹ.]*

**Trang:** Trường vắng nhanh thật. Lúc nãy dưới sân còn đông người mà.

**Linh:** Mình lên tầng ba lâu hơn tớ tưởng.

**Trang:** Thầy bảo cứ để nguyên đồ. Cậu đừng thấy món nào lạ rồi tháo ra đấy.

**Linh:** Tớ mang điện thoại để chụp ảnh, có mang tua vít đâu.

**Trang:** Tớ nhắc trước cho chắc.

*[Linh đẩy cửa. Một tiếng bản lề khô, ngắn; cửa mở bình thường.]*

**Trang:** Đây rồi. Phòng 304.

**Kết quả:** hiện tên game **“CĂN PHÒNG 304 — BUỔI ĐIỂM DANH CUỐI CÙNG”** trên hình hành lang trong một nhịp ngắn, rồi chuyển vào phòng để chạy `C1-01`. Tên game không che biển phòng hoặc lời thoại. Đoạn mở màn không cần lựa chọn đúng/sai.

### Nhịp chung của mở màn

| Nhịp | Người chơi nhìn thấy | Âm thanh chủ đạo | Cách chuyển tiếp |
|---|---|---|---|
| Địa điểm | Màn đen, tên trường, năm và giờ | Chim, tiếng nói rất xa | Bấm tiếp sau khi đọc. |
| Giao việc | Tin nhắn của Hùng | Một tiếng báo tin nhắn; nền trường vẫn nhẹ | Đọc xong tin và hai lượt thoại, bấm tiếp. |
| Trước cửa | Nắng hành lang, hai bạn, biển 304 | Bước chân, gió nhẹ, tiếng sân trường | Thoại ngắn; cửa mở trong dàn cảnh. |
| Tên game | Tên game trên cảnh đang có | Một tiếng bản lề; nền tự nhiên | Chuyển vào 304, chạy `C1-01`. |
| Bắt đầu chơi | Linh và Trang trong phòng; sổ, ảnh, máy | Môi trường nhỏ hơn vì ở trong phòng | Kết thúc thoại vào phòng mới giao quyền điều khiển. |

**Thời lượng mở màn dự kiến:** khoảng 45–90 giây tuỳ tốc độ đọc. Nếu cho bỏ qua mở màn ở lượt chơi lại, đặt thẳng hai người vào `C1-01`, giữ mục tiêu chụp tư liệu và cho xem lại tin nhắn. Không đánh dấu đã đọc ảnh, sổ hoặc đã gặp hiện tượng chỉ vì bỏ qua mở màn.

## Cảnh 1 — Một chỗ ngồi còn trống

### C1-01 — Vào phòng 304

**Kích hoạt:** kết thúc `C0-03`, hoặc tới đây trực tiếp nếu bỏ qua mở màn. Linh và Trang đứng trong phòng 304; chưa có hiện tượng. Sổ nằm trên bàn giáo viên, máy ghi âm không có băng, ảnh lớp treo trên tường.

**Căn phòng lúc bước vào:** cửa ở cạnh dưới bản đồ; bàn giáo viên và sổ ở phía trên, ảnh lớp treo trên tường, máy ghi âm nằm trên bàn phụ. Bàn cuối ở gần cửa sổ để sau này có bóng đổ lên tường. Tủ có ngăn dưới ở bên cạnh; chưa có dấu hiệu cho biết nó quan trọng. Lối đi đủ rõ để Linh tới từng đồ vật, không phải tìm vật tương tác trong bóng tối.

*[Nắng cuối chiều. Một vài bàn ghế được kê sát nhau để làm kho. Quạt trần đứng im, không phát tiếng động cơ. Trang phủi bụi trên mép bàn.]*

**Âm thanh:** tiếng chim và sân trường nhỏ hơn ngoài hành lang, vẫn nghe được. Có tiếng bước chân khi Linh di chuyển, tiếng giấy khi xem tài liệu. Chưa có chuông bất thường, nhạc dồn dập hoặc giọng người lạ.

**Trang:** Chụp ảnh, ghi tên đồ, rồi về. Tớ đã hứa sẽ có mặt ở nhà trước bữa tối.

**Linh:** Một phòng kho thôi mà. Mười phút là xong.

**Trang:** Cậu nói câu đó trước mỗi lần tụi mình mất một tiếng.

**Linh:** Thầy Hùng bảo sổ cũ đang ở trên bàn. Mình chụp cả trang tên lớp nhé.

**Trang:** Được. Cậu xem sổ, tớ xem còn món nào chưa ghi vào danh sách.

**Kết quả:** sau câu cuối của Trang, hiện mục tiêu “Chụp lại tư liệu trong phòng 304.” Hiện nhắc điều khiển một lần: “WASD / phím mũi tên: di chuyển · E: tương tác”. Trả quyền di chuyển lúc này; người chơi được xem ảnh, máy hoặc sổ theo thứ tự mình chọn. Không có đồng hồ đếm ngược.

**Trong lúc khám phá:** ánh sáng và âm thanh giữ ổn định dù người chơi đọc lâu. Biểu tượng tương tác hiện khi tới gần đồ vật; không có tiếng lạ phát theo bộ đếm để thúc người chơi mở sổ. Ảnh, máy và sổ đều dễ nhận ra; lần mở đúng trang sổ mới chuyển không khí sang bất thường.

**Nếu thử ra ngoài trước khi đọc sổ:** cửa vẫn mở bình thường, hành lang giữ nắng và âm thanh sau giờ học; Trang ở lại 304. Chưa có Đức xuất hiện, cửa phòng bảo vệ đổi chỗ hoặc vòng lặp cầu thang. Tương tác lối xuống chỉ hiện **Linh:** “Mình còn chưa chụp trang tên lớp. Xong việc rồi xuống.” Người chơi trở lại phòng để tiếp tục; không coi đây là ending B. Khi quay vào, không chạy lại lời thoại vào phòng.

### C1-02 — Ảnh lớp

**Kích hoạt:** tương tác ảnh lớp, trước hoặc sau khi hiện tượng bắt đầu. Lần đầu lưu manh mối ảnh; không nhận trùng.

**Linh:** 12A3… năm học 2015–2016.

**Trang:** Ba mươi ba học sinh. Tớ vừa đếm cả hàng cuối.

**Linh:** Còn tên người trong ảnh không?

**Trang:** Mép giấy bong mất một đoạn. Chỉ còn họ với tên lót ở chỗ này.

**Linh:** Chụp lại trước. Mình sẽ đối chiếu với sổ.

**Nếu lần đầu xem ảnh diễn ra sau khi nhóm đã tách và Trang không ở 304:** thay cuộc trao đổi trên bằng **Linh:** “12A3, năm học 2015–2016. Ba mươi ba học sinh. Mình chụp ảnh để đối chiếu với sổ.” Nếu đã đọc sổ, nối **Linh:** “Sổ ghi ba mươi hai người cần sơ tán. Sĩ số trong ảnh chưa cho biết hôm đó ai có mặt.”

**Nếu Trang có mặt và đã đọc trang sơ tán, nối thêm:**

**Trang:** Nhưng trong sổ chỉ ghi ba mươi hai người cần sơ tán.

**Linh:** Ảnh cho mình sĩ số lớp. Chưa đủ để biết hôm đó ai đi học.

**Kết quả:** Linh chụp ảnh; manh mối ghi rõ “Ảnh lớp có 33 học sinh”, chưa kết luận cả 33 đã có mặt trong buổi xảy ra cháy.

### C1-03 — Máy ghi âm chưa dùng được

**Kích hoạt:** kiểm tra máy trước khi có băng.

**Linh:** Máy cassette. Ngăn băng trống rồi.

**Trang:** Danh sách kiểm kê ghi có cả hộp băng. Chắc người ta xếp riêng lúc dọn đồ.

**Linh:** Để xem sau. Giờ mình còn chưa biết cần nghe băng nào.

**Nếu Trang không có mặt:** dùng **Linh:** “Ngăn băng trống. Danh sách kiểm kê có hộp băng của 12A3. Mình kiểm tra thùng thiết bị phát thanh.”

**Kết quả:** chỉ giới thiệu đồ vật; không bắt tìm pin trước khi biết cuộn băng liên quan. Nếu đã tìm băng, dùng đoạn `C4-03` thay đoạn này.

### C1-04 — Dòng tên bị sửa

**Kích hoạt:** lần đầu mở sổ tới trang 11/09/2015. Đoạn này vẫn chạy được khi người chơi chưa xem ảnh.

**Linh:** Ngày mười một tháng chín. “Số học sinh cần sơ tán: ba mươi hai.”

**Trang:** Dừng ở dòng cuối một chút.

**Linh:** Nguyễn Thu Nga. “Vắng.”

**Trang:** Có chữ khác bên dưới. Nghiêng sổ ra phía cửa sổ đi.

**Linh:** “Có mặt”… bị viết đè lên.

**Trang:** Chụp cả trang. Đừng tẩy hay sửa gì.

*[Linh chụp toàn bộ trang sổ bằng điện thoại; bản chụp được giữ cùng ghi chú.]*

**Nếu đã có manh mối ảnh:**

**Linh:** Lớp có ba mươi ba người. Có thể một người nghỉ, nhưng dòng này cần hỏi lại.

*[Tiếng sân trường và tiếng chim dừng trong một nhịp. Linh chưa kịp đóng sổ. Chuông vang từ phía hành lang. Phụ đề: “Tiếng chuông trường — sau giờ tan học”. Ghế cuối lớp kéo nhẹ trên sàn. Phụ đề: “Tiếng ghế kéo — bàn cuối”.]*

**Trang:** Cậu có chạm vào cái ghế đó không?

**Linh:** Tớ đang đứng đây.

**Giọng điểm danh:** Nguyễn Thu Nga.

*[Im lặng. Một bóng nữ sinh ngồi ở bàn cuối hiện trên tường; chiếc ghế thật vẫn trống.]*

**Trang:** Linh… mình ra ngoài trước đi.

**Linh:** Mang sổ theo. Mình tìm bà Năm hỏi.

**Kết quả:** lưu manh mối sổ bị sửa; Linh mang sổ theo, kích hoạt ký ức một lần và chuyển hành lang sang trạng thái bất thường. Cửa 304 vẫn đi qua được; vòng lặp xảy ra ở cầu thang, không phải cửa tự khoá. Đây chưa phải chỗ cho người chơi đáp “Có”.

### Dàn cảnh cụ thể của hiện tượng đầu tiên

Đoạn này bổ sung cách thể hiện `C1-04`, không phải một sự kiện thứ hai. Chỉ chạy khi Linh mở trang **11/09/2015**. Trước đó, mở các đồ vật khác không gây tiếng chuông hoặc bóng Nga.

| Nhịp sự kiện | Hình ảnh / hành động | Âm thanh / phụ đề | Quyền điều khiển |
|---|---|---|---|
| Đọc trang sổ | Linh ở bàn giáo viên; Trang nhìn vào trang giấy. Hiện rõ hai chữ “có mặt” dưới “vắng”. | Một tiếng lật giấy; môi trường trường học vẫn bình thường. | Tạm dừng di chuyển; người chơi tự bấm tiếp từng lượt thoại. |
| Sau câu cuối về trang sổ | Giữ nguyên khung hình khoảng một giây. Hai người chưa có động tác hoảng sợ. | Tiếng chim và tiếng sân trường tắt, để một khoảng lặng ngắn. | Chạy nhịp dàn cảnh, không yêu cầu bấm đúng lúc. |
| Chuông | Ánh nắng chuyển hơi lạnh và nhạt hơn; không tắt cả màn hình. | Một hồi chuông ngắn khoảng hai giây, vang hơn tiếng nền nhưng không thành cú giật âm lượng. Phụ đề nói rõ “sau giờ tan học”. | Chưa trả quyền di chuyển. |
| Ghế cuối | Ghế dịch ra khỏi bàn một đoạn nhỏ, đủ nhìn ra khoảng trống. | Tiếng chân ghế cọ sàn khoảng một giây, có phụ đề vị trí. | Giữ khung phòng để nhìn thấy cả bàn giáo viên và bàn cuối. |
| Hai bạn phản ứng | Trang hỏi “Cậu có chạm vào cái ghế đó không?”; Linh đáp. | Giảm hiệu ứng xuống khi đọc thoại. Không chồng thêm tiếng thì thầm. | Bấm tiếp thoại theo tốc độ đọc. |
| Gọi tên | Ghế thật vẫn trống. | Một giọng nam từ trong phòng, xa và hơi vang, đọc đúng “Nguyễn Thu Nga”. Câu hiện trong hộp thoại với tên “Giọng điểm danh”. | Chưa có lựa chọn trả lời. |
| Không có người đáp | Bóng nữ sinh ngồi hiện trên tường gần bàn cuối trong khoảng một đến hai giây; giữ một khung rõ trước khi mờ đi. | Im lặng; không có câu nói của Nga ở đoạn này. | Không yêu cầu phản xạ hoặc đi tới bóng để qua cảnh. |
| Quyết định ra ngoài | Trang nói ra ngoài; Linh mang sổ và đề nghị tìm bà Năm. | Một nền gió rất nhỏ thay nền sân trường. Lời thoại vẫn dễ đọc/nghe. | Đọc xong mới trả quyền di chuyển. |
| Đi qua cửa | Nhân vật ra hành lang. Ở đây nắng nhạt, tiếng học sinh dưới sân đã mất; cửa 304 vẫn phía sau. | Bước chân và gió, không phát lại cả đoạn gọi tên. | Mục tiêu: “Tìm bà Năm ở phòng bảo vệ.” Đi cầu thang mới chạy `C2-01`. |

**Cảm giác cần đạt:** lúc mới bước vào, người chơi nhận ra một phòng kho ở trường mình; sau khi đọc sổ, những âm thanh quen thuộc mất đi và một tiếng chuông xuất hiện sai giờ. Sự bất an đến từ thay đổi có nguyên nhân và chỗ ngồi trống, không cần quái vật lao vào màn hình.

### Các lớp âm thanh cần cho đoạn đầu

| Lớp âm thanh | Dùng ở đâu | Cách dùng |
|---|---|---|
| Môi trường trường sau giờ học | `C0-01` đến lúc đọc đúng trang sổ | Một vòng nền nhẹ có chim và tiếng nói xa không rõ lời. Khi vào phòng giảm xuống; lúc hiện tượng bắt đầu dừng vòng này. |
| Báo tin nhắn | `C0-02` | Một lần, ngắn. Nội dung giao việc luôn đọc được trên màn hình. |
| Bước chân | Hành lang, khám phá 304 | Chỉ có khi nhân vật bước; dừng khi đứng yên/đọc thoại. Không giả tiếng bước chân người thứ ba trước khi mở sổ. |
| Mở cửa | Cuối `C0-03` | Một tiếng bản lề khô, nhỏ, khớp động tác đẩy cửa. Không kéo thành tiếng thét. |
| Giấy / lật sổ | Xem ảnh, tư liệu, mở sổ | Hiệu ứng ngắn lúc mở, không lặp ở mỗi chữ xuất hiện. |
| Chuông trường | Mốc bất thường ở `C1-04` | Một lần theo sự kiện; không chạy theo giờ thật của máy hay một bộ đếm trong phòng. |
| Ghế kéo | Sau chuông | Khớp với một lần ghế dịch; phụ đề chỉ vị trí bàn cuối. |
| Giọng điểm danh | Sau hai lượt phản ứng | Một câu ngắn; không đọc đồng thời với lời Trang hoặc Linh. Có thể thu giọng nam, thêm độ vang nhẹ; không cần thu toàn bộ thoại mở màn. |
| Gió nhẹ trong ký ức | Sau lần gọi tên, sang hành lang | Một nền nhỏ, tương phản với tiếng sân trường trước đó. Không có tiếng nói mang thông tin cốt truyện bị giấu trong nền. |

**Nếu dùng nhạc:** chỉ thêm một nền rất nhẹ sau hiện tượng; tránh giai điệu rõ hoặc nhịp dồn ở đoạn giới thiệu. Bản đầu có thể dùng tiếng môi trường và hiệu ứng mà vẫn giữ được nhịp này. Các lớp trên là mô tả nội dung cần chuẩn bị, chưa phải file âm thanh đã có.

**Khi tắt âm thanh:** vẫn có tin nhắn, phụ đề chuông/ghế, màu ánh sáng đổi, ghế dịch và bóng trên tường. Người chơi hiểu sự kiện và biết mục tiêu tiếp theo. Lời thoại Linh–Trang vẫn hiện đủ bằng chữ nếu nhóm chưa thu giọng.

## Cảnh 2 — Tầng ba không chịu kết thúc

### C2-01 — Cầu thang đưa về chỗ cũ

**Kích hoạt:** lần đầu đi qua lối cầu thang sau chuông. Nhân vật được đặt lại gần cửa 304.

**Trang:** Sao lại là cửa này?

**Linh:** Mình vừa đi xuống một tầng.

**Trang:** Hay có hai phòng cùng số?

*[Linh lấy mẩu phấn trong khay cạnh cửa và đánh một vạch lên tường.]*

**Linh:** Tớ đánh dấu ở đây. Thử thêm một lần thôi.

*[Cảnh chuyển ngắn. Hai người trở lại trước vạch phấn. Không yêu cầu người chơi đi lặp nhiều lần.]*

**Trang:** Vạch vẫn ở đây.

**Linh:** Không phải đi nhầm. Mình đang quay lại cùng một chỗ.

**Trang:** Vậy đừng đi cầu thang nữa. Tìm người khác trước.

**Kết quả:** xác nhận vòng lặp; Đức xuất hiện trong hành lang. Nếu thử cầu thang tiếp, chỉ hiện câu: “Lối này vẫn đưa mình về 304.”

### C2-02 — Gặp Đức

**Kích hoạt:** nói chuyện lần đầu với Đức ở hành lang.

**Đức:** Hai bạn cũng không xuống được à?

**Linh:** Cậu bị như vậy từ lúc nào?

**Đức:** Từ khi chuông reo. Trước đó tớ đang chờ bà Năm lấy ảnh cũ.

**Trang:** Tụi tớ đang chụp tư liệu phòng 304.

**Đức:** Ban nãy có ai gọi tên chị tớ trong đó.

**Linh:** Nguyễn Thu Nga?

**Đức:** Ừ. Chị tớ học lớp 12A3. Chị mất mười năm trước.

**Linh:** Tớ xin lỗi. Tụi tớ vừa tìm thấy tên chị ấy trong sổ.

**Đức:** Tớ biết mọi người kể chị tự quay vào lấy đồ. Nhà tớ chưa bao giờ biết chị quay lại lấy cái gì.

**Lựa chọn — hỏi cả hai được, không chặn nhau:**

1. **“Cậu đến lấy ảnh của chị à?”**

   **Đức:** Khu này sắp sửa. Tớ muốn xin ảnh trước khi họ dọn hết.

   **Trang:** Nếu thấy ảnh lớp, tụi tớ sẽ chụp cho cậu một bản.

2. **“Bà Năm đang ở đâu?”**

   **Đức:** Phòng bảo vệ. Nhưng cầu thang cứ đưa tớ về đây.

   **Linh:** Mình thử tìm lối khác. Cậu đi cùng tụi tớ nhé.

**Kết quả:** biết quan hệ Nga–Đức; Đức theo nhóm trong các đoạn chuyển cảnh, không cần hệ thống AI đi theo. Đến chỗ tương tác, cậu đứng tại vị trí định sẵn.

### C2-03 — Mẩu nội san

**Kích hoạt:** đọc mẩu nội san trên bảng tin. Bảng tin sáng hơn nền để dễ nhận ra.

*[Nội dung hiện như tài liệu, không có người đọc thành tiếng.]*

> Chuyện lạ sau giờ học
>
> “Nếu nghe tiếng điểm danh sau giờ tan học, đừng trả lời thay một cái tên không phải của mình.”
>
> Ghi chép của học sinh: “Tớ suýt đáp khi họ gọi Nguyễn Thu Nga. Trên trang sổ, tên tớ hiện dưới dòng đó. Tớ im lặng. Chữ mờ đi.”

**Trang:** Nghe giống đúng chuyện mình vừa gặp.

**Linh:** Chưa biết ai viết. Nhưng không ai trả lời thay người khác nhé.

**Đức:** Kể cả tên chị tớ. Tớ muốn nghe chị, không muốn ai mắc lại thay chị.

**Nếu chưa gặp Đức:** bỏ câu của Đức, không cho người chưa được giới thiệu tự tham gia đoạn này. **Nếu đọc muộn khi Trang và Đức không ở hành lang:** chỉ hiện tài liệu và **Linh:** “Cảnh báo nói đừng trả lời thay tên người khác. Mình ghi lại.”

**Kết quả:** lưu cảnh báo. Đây là thông tin mở rộng; nếu người chơi bỏ qua bảng tin, Trang vẫn nhắc quy luật và cảnh 5 vẫn minh hoạ trước lựa chọn nguy hiểm.

### C2-04 — Tiếng gõ ở cuối hành lang

**Kích hoạt:** sau khi đã gặp Đức và xác nhận vòng lặp. Không bắt buộc đọc nội san.

*[Phụ đề: “Ba tiếng gõ — từ cuối hành lang”. Cánh cửa cuối hành lang rung nhẹ. Biển “Phòng bảo vệ” hiện ra trên cửa.]*

**Đức:** Ở cửa bên kia. Không phải dưới cầu thang.

**Trang:** Lúc mình đi qua, chỗ đó là tường mà.

**Linh:** Có người bên trong. Bà Năm ơi!

**Bà Năm, sau cửa:** Bà đây. Mấy đứa còn ở trong trường à?

**Đức:** Bà mở giúp tụi con với!

**Kết quả:** lối phòng bảo vệ xuất hiện; người chơi nhìn thấy vị trí mà không cần nghe âm thanh. Sang cảnh 3 khi tương tác cửa.

## Cảnh 3 — Chìa khoá và lối rút lui

### C3-01 — Bà Năm nhận ra cuốn sổ

**Kích hoạt:** gặp bà Năm lần đầu. Bà đứng cạnh bảng chìa khoá trong phòng bảo vệ.

**Bà Năm:** Bà thử mở cổng ba lần rồi. Cứ mở ra là lại thấy hành lang tầng ba.

**Trang:** Tụi con đi cầu thang cũng vậy.

**Bà Năm:** Cái sổ trên tay con… lấy ở phòng 304 phải không?

**Linh:** Dạ. Con mở trang này thì nghe tiếng điểm danh.

**Bà Năm:** Trước đây bà cũng nghe. Mấy lần dọn kho, ai để sổ ngoài tủ là tối đó có tiếng gọi.

**Đức:** Lần này bà có nghe tên chị Nga không?

**Bà Năm:** Có. Bà còn tưởng có người mở máy ghi âm.

**Linh:** Bà làm gì để tiếng đó ngừng?

**Bà Năm:** Cất sổ đúng ngăn dưới, rồi khoá lại. Bà chỉ biết làm vậy thì yên. Chưa lần nào cả khu bị giữ như hôm nay.

**Linh:** Mình thử được không bà?

**Bà Năm:** Được. Bà có chìa đây. Đi cùng bà, đừng tách nhau ra.

**Kết quả:** nhận chìa khoá; nhóm chuyển về 304 để thử. Chìa khoá dùng nhiều lần, không mất sau khi khoá tủ.

### C3-02 — Thử cất sổ

**Kích hoạt:** tương tác đúng ngăn tủ cùng bà Năm. Nếu chỉ đóng bìa sổ, dùng câu nhắc ở mục cuối.

**Bà Năm:** Ngăn này. Để sổ nằm phẳng rồi khoá cửa lại.

*[Linh đặt sổ vào ngăn dưới và xoay chìa. Tiếng kéo ghế ngừng. Ánh sáng ngoài cửa trở lại màu chiều.]*

**Trang:** Cậu nghe thấy không?

**Linh:** Không còn tiếng gọi nữa.

**Đức:** Cầu thang cũng đổi rồi. Tớ nhìn thấy tầng dưới.

**Bà Năm:** Khoan, để bà xem cổng.

*[Cảnh ngắn: cổng hiện trên lối ra trong bản đồ phòng bảo vệ và mở được.]*

**Bà Năm:** Ra ngoài được rồi.

**Trang:** Vậy cách này có tác dụng thật.

**Linh:** Mình giữ chìa. Nếu cần, vẫn có thể khoá lại lần nữa.

**Kết quả:** đã kiểm chứng cách tạm ngừng; sổ đang ở trong tủ, chưa được giải thoát. Đi qua lớp học hiện tại để gặp Hùng trước lựa chọn tiếp tục/rút lui.

### C3-03 — Gặp thầy Hùng

**Kích hoạt:** nhóm gặp Hùng tại lớp học hiện tại sau lần thử. Bàn có sách đang đóng thùng trước khi thầy nghỉ hưu.

**Thầy Hùng:** Các em ở đây à? Thầy vừa đi dọc hành lang mà không tìm được cầu thang.

**Trang:** Tụi em cũng bị giữ lại. Bà Năm vừa giúp cất sổ.

**Thầy Hùng:** Sổ 12A3? Thầy lấy ra kiểm tra sáng nay. Chắc quên cất.

**Linh:** Trang ngày xảy ra cháy có một dòng bị sửa. Tên Nguyễn Thu Nga.

**Thầy Hùng:** Hồ sơ ghi em ấy đã về trước. Chuyện đó xảy ra lâu rồi.

**Đức:** Em là em trai chị Nga. Nhà em vẫn nghe người ta nói chị tự quay vào lấy đồ.

**Thầy Hùng:** Đức… thầy biết. Nhưng thầy không nghĩ các em nên ở lại đây lúc này.

**Linh:** Tụi em chưa biết đã xảy ra chuyện gì. Tụi em chỉ muốn hỏi đúng người.

**Kết quả:** Hùng chưa thú nhận. Không ghi vào sổ manh mối rằng Hùng đã sửa sổ; hiện mới biết thầy từng chủ nhiệm và có mặt ở trường.

### C3-04 — Có thể ra về

**Kích hoạt:** nhóm trở lại gần cửa 304, sổ đã khoá và đường ra đang mở. Đây là điểm kết thúc B sớm nhất.

*[Một tiếng gõ nhẹ từ ngăn tủ. Phụ đề: “Một tiếng gõ — trong tủ đã khoá”.]*

**Đức:** Khoá lại thì yên. Nhưng chị tớ vẫn ở trong đó à?

**Bà Năm:** Bà không biết, con à.

**Đức:** Nếu tụi mình đi, lần sau người ta vẫn kể chị tự quay vào đó thôi, đúng không?

**Trang:** Linh, cổng đang mở. Cậu muốn làm gì?

**Lựa chọn:**

1. **“Rời trường. Giữ sổ trong tủ.”** → `B-01`, biến thể rút lui sớm.
2. **“Ở lại tìm xem Nga đã ở đâu.”** → đoạn bên dưới; chưa mở sổ ngay.

**Nếu chọn ở lại:**

**Linh:** Tớ muốn kiểm tra các tư liệu còn lại. Nhưng mở sổ ra, có thể mình lại không đi được.

**Trang:** Tớ ở cùng. Có chìa thì mình vẫn có đường rút lui.

**Đức:** Tớ cũng ở lại. Tớ muốn biết chị đã ở đâu.

**Bà Năm:** Bà đưa được sổ về ngăn này. Còn giấy tờ cũ, bà lấy cho các con xem.

**Thầy Hùng:** Thầy ở lớp bên cạnh. Để thầy xem lại những gì còn giữ được.

*[Hộp xác nhận: “Mở lại sổ sẽ kích hoạt ký ức và chặn đường ra. Bạn vẫn giữ chìa khoá để cất sổ và rút lui.”]*

**Lựa chọn xác nhận:**

- **“Mở sổ và tiếp tục điều tra.”** → Linh mở tủ, lấy sổ, ký ức hoạt động trở lại; chuyển `C3-05`.
- **“Chưa mở. Xem lại quyết định.”** → trở về hai lựa chọn ban đầu; không mất chìa, không kích hoạt sự kiện.

**Kết quả:** sau khi mở lại, Trang và Hùng ở lớp học hiện tại; bà Năm ở phòng bảo vệ; Đức ở 304. Vị trí đổi theo cảnh, không cần NPC tự đi qua bản đồ.

### C3-05 — Ba mươi hai người ở cổng

**Kích hoạt:** kiểm tra sổ bảo vệ hoặc hỏi bà Năm về lần sơ tán. Lưu cả bản ghi ở cổng và bản báo cáo của Hùng như một manh mối đối chiếu.

**Linh:** Ngày xảy ra cháy, bà ở cổng phải không?

**Bà Năm:** Ừ. Bà đếm học sinh lúc các cháu ra sân.

**Linh:** Lớp 12A3 có bao nhiêu người ra ngoài?

**Bà Năm:** Ba mươi hai. Đây, bà ghi ngay lúc đó.

**Linh:** Còn giấy này ghi “12A3 đã đủ”. Có tên thầy Hùng.

**Linh:** Phần nguyên nhân ghi chập điện ở hành lang. Không phải cháy bắt đầu trong lớp.

**Bà Năm:** Thầy báo vậy. Bà nghĩ thầy đã kiểm tra danh sách lớp.

**Linh:** Bà có thấy Nga đi qua cổng trước đó không?

**Bà Năm:** Bà không nhớ thấy con bé. Nhưng bà không thể nhìn hết mọi người cả buổi chiều.

**Nếu chưa nghe băng:**

**Linh:** Vậy mình biết có ba mươi hai người được đếm bên ngoài. Còn Nga ở đâu, phải tìm bằng chứng khác.

**Bà Năm:** Đúng. Điều nào không thấy, bà không dám nhận là đã thấy.

**Nếu đã nghe băng:**

**Linh:** Băng ghi Nga vẫn ở trong lớp khi còi vang. Bản ghi này xác nhận bên ngoài mới có ba mươi hai người.

**Bà Năm:** Vậy con giữ cả hai để đối chiếu. Bà chỉ xác nhận được số người bà đếm.

*[Linh chụp bản ghi ở cổng và báo cáo để mang đi đối chiếu; tài liệu gốc vẫn được giữ.]*

**Kết quả:** lưu bản ghi. Nếu chưa nghe băng, mục tiêu là “Tìm bằng chứng về vị trí của Nga lúc báo động”; nếu đã nghe, là “Đối chiếu tư liệu với thầy Hùng”. Nếu chưa xem ảnh, ghi chú nhắc ảnh lớp trong 304; không phát lời Trang khi cô ở phòng khác.

## Cảnh 4 — Cuộn băng chưa phát hết

### C4-01 — Ảnh gia đình của Đức

**Kích hoạt:** nói với Đức tại 304 sau quyết định tiếp tục. Nếu chưa xem ảnh lớp, đoạn này vẫn cho kiểm tra ảnh cùng Đức.

**Đức:** Tớ có ảnh của chị. Mẹ bảo mang theo để hỏi cho đúng người.

*[Đức đưa ảnh hai chị em. Dòng chú thích ngắn: “Ảnh gia đình — Nga và Đức, trước năm 2015”.]*

**Linh:** Đây là cậu hồi nhỏ à?

**Đức:** Ừ. Hồi chị mất, tớ mới bảy tuổi.

**Linh:** Người đứng cuối hàng trong ảnh lớp giống chị cậu.

**Đức:** Là chị. Chị hay đứng phía sau vì cao hơn mấy bạn.

**Linh:** Mình đặt ảnh cạnh bàn cuối một chút nhé?

**Đức:** Được. Tớ muốn chị biết tớ vẫn nhận ra chị.

*[Ảnh được đặt cạnh bàn. Bóng trên tường giơ tay về phía thùng “Thiết bị phát thanh 12A3”. Không hiện thêm lời giải bằng ma thuật.]*

**Linh:** Đức, nhìn cái thùng kia.

**Đức:** Tớ thấy. Chị đang chỉ về phía đó.

**Linh:** Mình kiểm tra nhãn trước.

**Kết quả:** xác định Nga trong ảnh lớp, lưu thông tin Đức. Nếu chưa xem ảnh trước đó, Linh đếm 33 học sinh, chụp ảnh và lưu manh mối ngay trong đoạn này. Bóng là chỉ dẫn tìm kiếm; người chơi vẫn phải đọc nhãn và nghe băng.

**Nếu đã có băng nhưng chưa nghe:** thay ba câu cuối bằng **Linh:** “Chiếc thùng có cuộn băng mình vừa tìm. Thử nghe nó ở đây.” **Nếu đã nghe băng:** thay bằng **Linh:** “Thùng này giữ cuộn băng mình đã nghe. Tớ đã lưu đoạn chị ở lại.” Không yêu cầu phát lại để tiến độ được công nhận.

### C4-02 — Hộp băng đúng ngày

**Kích hoạt:** kiểm tra thùng thiết bị. Có thể thực hiện trước khi xem bóng; không phải chờ một thứ tự duy nhất.

**Linh:** “Tập phát thanh — 12A3 — 11/09/2015.”

**Đức:** Đúng ngày xảy ra cháy.

**Linh:** Bên dưới có giấy kiểm kê: “Băng lấy từ máy ghi âm thu hồi tại 304.”

**Đức:** Vậy nó vẫn ở đây từ lúc người ta dọn phòng.

**Linh:** Mình đem ra máy thử. Không kéo băng ra nhé.

**Nếu Đức chưa đứng tại 304 khi lấy băng:** chỉ hiện hai lượt của Linh đọc nhãn và giấy kiểm kê, rồi “Mình đem ra máy thử.” Các câu của Đức không phát khi cậu không ở đó.

**Kết quả:** nhận cuộn băng gốc một lần. Đây là vật được thu hồi và lưu giữ, không phải băng mới do Nga tạo ra.

### C4-03 — Máy hết pin

**Kích hoạt:** tương tác máy khi có băng nhưng chưa có pin phù hợp.

*[Linh lắp băng và nhấn nút. Máy không sáng.]*

**Linh:** Không lên nguồn. Khay pin trống.

**Đức:** Có hỏi bà Năm được không? Bà giữ đồ dùng ở phòng bảo vệ.

**Linh:** Ừ. Mình ghi lại loại pin trên khay, khỏi lấy nhầm.

**Nếu Đức không có mặt:** chỉ giữ hai câu của Linh và đổi câu cuối thành “Mình hỏi bà Năm xem có đúng loại pin này không.”

**Kết quả:** lưu việc máy thiếu nguồn; mở câu hỏi xin pin. Không yêu cầu mua pin hay thêm bản đồ.

### C4-04 — Xin pin ở phòng bảo vệ

**Kích hoạt:** đã biết máy thiếu nguồn, nói với bà Năm. Pin đúng loại dùng cho máy trong 304.

**Linh:** Bà có pin dự phòng loại này không? Tụi con tìm được băng ghi ngày xảy ra cháy.

**Bà Năm:** Có. Bộ mới trong ngăn bên trái. Con lấy cả bộ nhé.

**Linh:** Bà biết ngày đó lớp có tập phát thanh không?

**Bà Năm:** Có nghe nhạc từ tầng ba. Bà không ở trong lớp, không biết tập đến lúc nào.

**Linh:** Dạ. Tụi con nghe thử rồi đem những gì biết được đối chiếu lại.

**Kết quả:** nhận pin một lần. Khi đã nhận, bà chỉ nhắc: “Pin con cầm rồi. Máy ở phòng 304.”

### C4-05 — Phát băng

**Kích hoạt:** tương tác máy khi có băng và pin. Đức đứng tại 304. Lắp đồ tự động, không có câu đố chọn đúng khe.

**Linh:** Máy lên nguồn rồi. Tớ ghi lại đoạn phát bằng điện thoại, để còn đối chiếu.

**Đức:** Cứ để băng chạy. Tớ muốn nghe.

*[Điện thoại Linh hiện biểu tượng ghi âm. Phụ đề “Băng cassette — bản thu ngày 11/09/2015”. Tên người nói có hậu tố “trên băng” để phân biệt với hiện tại.]*

**Thầy Hùng, trên băng:** Nga ở lại cất giúp thầy bộ tư liệu nhé. Xong rồi em xuống.

**Nga, trên băng:** Dạ. Em cất máy ghi âm luôn phải không thầy?

**Thầy Hùng, trên băng:** Ừ. Để băng chạy hết rồi cất giúp thầy.

*[Tiếng cửa khép. Giấy được xếp. Sau một khoảng chuyển ngắn: “Còi báo động; tiếng người chạy ngoài hành lang”.]*

**Nga, trên băng:** Có ai ngoài đó không?

**Nga, trên băng:** Em vẫn ở trong lớp! Có ai nghe không?

*[Băng nhiễu rồi dừng. Không phát hoặc miêu tả trực tiếp cái chết. Ghế cuối dịch khỏi bàn. Bóng Nga đứng sau kính cửa lớp.]*

**Đức:** Chị chưa ra ngoài.

**Linh:** Đúng. Lúc còi vang, chị vẫn ở đây, làm việc thầy vừa giao.

**Đức:** Vậy chị không tự quay lại.

**Nếu đã đọc bản ghi và báo cáo:** **Linh:** Mình mang đoạn này cho thầy Hùng nghe. Thầy cần trả lời chuyện báo đủ người.

**Nếu chưa đọc:** **Linh:** Mình mang đoạn này cho thầy Hùng nghe. Thầy phải giải thích chuyện nhờ chị ở lại.

**Kết quả:** lưu “đã nghe băng” và bản thu lại trên điện thoại. Mang được băng gốc, ảnh và ghi chú tới lớp học hiện tại. Lần phát sau dùng câu ngắn hoặc cho đọc lại bản chép, không ép nghe toàn cảnh.

## Cảnh 5 — Người đã nói “đủ rồi”

### C5-01 — Chọn điều muốn hỏi thầy

**Kích hoạt:** tương tác Hùng tại lớp học hiện tại, từ sau lần gặp `C3-03`. Trang đứng cạnh bàn tư liệu. Khi chuẩn bị đối chất bằng băng, Đức được chuyển tới đây trong cảnh ngắn và ở lại tới hết `C5-06`, kể cả khi Linh đi tìm tư liệu còn thiếu rồi trở lại.

**Thầy Hùng:** Các em tìm được gì rồi?

**Linh:** Tụi em muốn đối chiếu từng phần. Có điều trong hồ sơ chưa khớp.

**Lựa chọn:**

- **“Hỏi về dòng tên Nga bị sửa.”** → `C5-02`; có sổ bị sửa. Câu đối chiếu ảnh chỉ thêm khi đã xem ảnh.
- **“Hỏi về bản báo lớp đã đủ.”** → `C5-03`; chỉ hiện khi đã đọc bản ghi 32 người và bản báo cáo.
- **“Cho thầy nghe băng ngày 11/09.”** → `C5-04`; chỉ hiện khi đã nghe băng, không chỉ nhặt được nó.
- **“Đối chiếu đủ tư liệu để viết bản xác nhận.”** → `C5-05`; hiện sau khi Hùng đã nhận trách nhiệm. Nếu thiếu tư liệu, đoạn này chỉ nêu thứ còn thiếu.
- **“Để em kiểm tra thêm.”** → đóng hộp thoại, trả quyền di chuyển.

**Kết quả:** mỗi câu hỏi quay về menu hoặc trả quyền điều khiển; không bắt hỏi theo thứ tự. Thông tin mới làm thay đổi câu trả lời. Cách nói gay gắt hay nhẹ nhàng không phải chìa khoá mở ending A.

### C5-02 — Dòng tên Nga

**Kích hoạt:** chọn hỏi về sổ. Nếu Hùng chưa thú nhận:

**Linh:** Tên Nga bị sửa từ “có mặt” thành “vắng”. Ai sửa dòng này ạ?

**Thầy Hùng:** Sau tai nạn, sổ được rà lại. Có người nói Nga đã về trước.

**Linh:** Đó là người tận mắt thấy chị ấy đi, hay chỉ nghĩ chị ấy đã về?

**Thầy Hùng:** Thầy… không hỏi rõ lúc đó.

**Nếu đã xem ảnh lớp:**

**Linh:** Ảnh lớp có ba mươi ba học sinh. Tụi em cần biết vì sao hồ sơ chỉ tính ba mươi hai.

**Thầy Hùng:** Ảnh không cho biết hôm ấy ai đi học.

**Linh:** Dạ. Nên tụi em đang tìm thêm, chứ chưa kết luận từ ảnh.

**Nếu chưa nghe băng, nối thêm:**

**Trang:** Trong đồ kiểm kê có thiết bị phát thanh của lớp. Mình kiểm tra lại phần đó nhé.

**Kết quả:** chưa mở xác nhận và chưa kết luận thầy là người sửa. Có thể hỏi tiếp, đi tìm băng hoặc quay lại khi có bằng chứng.

**Nếu Hùng đã thú nhận, thay toàn bộ đoạn trên bằng:**

**Thầy Hùng:** Thầy đã sửa dòng đó. Nga có mặt; thầy đã ghi em vắng để che việc thầy báo sai.

**Linh:** Tụi em sẽ giữ nguyên trang gốc. Việc đính chính phải ghi ở bản bổ sung.

### C5-03 — “Đủ” là bao nhiêu người?

**Kích hoạt:** có bản ghi và báo cáo. Nếu Hùng chưa thú nhận, dùng đoạn bên dưới, kể cả khi Linh đã nghe băng nhưng chưa chọn phát cho thầy.

**Linh:** Bà Năm ghi ba mươi hai học sinh ra sân. Trong báo cáo, thầy ghi lớp đã đủ.

**Thầy Hùng:** Lúc đó thầy nghĩ Nga đã về.

**Linh:** Thầy có kiểm tra em ấy trước khi báo không?

**Thầy Hùng:** Không. Mọi người đang chạy ra, rất hỗn loạn.

**Linh:** Vậy “đã đủ” là điều thầy nghĩ, chưa phải điều đã kiểm tra.

**Thầy Hùng:** Thầy không còn nhớ rõ mọi việc.

**Nếu chưa nghe băng:** **Trang:** Mình giữ cả hai bản. Cần biết Nga đang ở đâu khi còi vang.

**Nếu đã nghe băng:** **Trang:** Mình có đoạn ghi âm rồi. Cho thầy nghe để đối chiếu nhé.

**Kết quả:** chưa có xác nhận đầy đủ. Gợi tìm thiết bị nếu chưa nghe băng; nếu đã nghe, nhắc chọn phát đoạn giao việc trong cùng menu.

**Nếu Hùng đã thú nhận, dùng đoạn ngắn:**

**Linh:** Bản ghi ở cổng và lời thầy xác nhận đều là ba mươi hai người ra ngoài.

**Thầy Hùng:** Đúng. Thầy đã không báo còn thiếu Nga. Thầy sẽ ghi rõ điều đó.

### C5-04 — Không thể nói là “đã về”

**Kích hoạt:** đã nghe băng; chọn phát đoạn giao việc cho Hùng. Không cần đã hỏi hai câu trước. Hùng nhận trách nhiệm ở đoạn này một lần.

**Linh:** Thầy nghe đoạn này giúp em.

**Thầy Hùng, trên băng:** Nga ở lại cất giúp thầy bộ tư liệu nhé. Xong rồi em xuống.

**Nga, trên băng:** Dạ. Em cất máy ghi âm luôn phải không thầy?

*[Dừng ở đoạn giao việc. Không phát lại tiếng kêu cứu.]*

**Linh:** Chính thầy nhờ chị ấy ở lại. Sau đó, băng còn ghi giọng Nga khi còi báo động vang.

**Thầy Hùng:** Thầy… đã nhận ra giọng em ấy.

**Nếu đã đọc báo cáo:** **Linh:** Vì sao thầy lại báo lớp đã đủ?

**Nếu chưa đọc:** **Linh:** Vì sao hồ sơ lại ghi chị đã rời lớp trước báo động?

*[Đèn tắt một nhịp. Bóng sau lưng Hùng. Phụ đề hiển thị trọn câu của Nga.]*

**Nga:** Thầy bảo em ở lại.

*[Đèn sáng lại. Hùng nhìn vào bàn, không nhìn bóng.]*

**Thầy Hùng:** Đúng. Thầy giao em cất tư liệu rồi xuống văn phòng.

**Thầy Hùng:** Khi đếm ở sân, thầy chỉ có ba mươi hai học sinh. Có người nói Nga đã về. Thầy đã báo lớp đủ người.

**Linh:** Nhưng thầy biết thầy vừa giao việc cho chị ấy.

**Thầy Hùng:** Thầy biết. Thầy vẫn bám vào lời đó, thay vì báo còn thiếu một người.

**Đức:** Rồi thầy sửa tên chị em thành vắng?

**Thầy Hùng:** Thầy sửa. Thầy sợ phải nhận rằng mình đã báo sai.

**Thầy Hùng:** Trong tường trình, thầy viết Nga đã rời lớp trước báo động. Thầy không có bằng chứng cho điều đó.

**Đức:** Nhà em nghe câu “chị tự quay lại” suốt mười năm.

**Thầy Hùng:** Thầy đã để lời đó tồn tại. Thầy không có lời giải thích nào làm nó đúng được.

**Đức:** Em chưa biết nói gì với thầy. Nhưng thầy phải nói lại với mẹ em, với nhà trường.

**Linh:** Mình không biết nếu báo sớm hơn thì chuyện sẽ ra sao. Nhưng việc bỏ sót và sửa hồ sơ phải được ghi đúng.

**Thầy Hùng:** Thầy sẽ viết bản xác nhận. Ghi cả việc giao Nga ở lại, báo đủ khi chưa đủ và sửa sổ sau đó.

**Kết quả:** Hùng nhận trách nhiệm; chưa tự sinh đầy đủ chứng cứ nếu người chơi chưa đọc ảnh hoặc bản ghi. Mở `C5-05`. Đức không tha thứ ngay, Hùng không được miễn trách nhiệm vì đã thú nhận.

### C5-05 — Bản bổ sung, không xoá trang cũ

**Kích hoạt:** chọn đối chiếu để viết xác nhận sau `C5-04`.

**Nếu còn thiếu tư liệu, chỉ hiện câu tương ứng rồi trả quyền điều tra:**

- Chưa kiểm tra ảnh: **Trang:** Mình còn cần đối chiếu sĩ số trên ảnh lớp. Ảnh treo trong 304.
- Chưa đọc sổ bị sửa: **Trang:** Chụp lại nguyên trang 11/09 trước. Sổ đang ở cùng mình.
- Chưa đọc bản ghi và báo cáo: **Trang:** Mình cần bản ghi ở cổng để đối chiếu số người ra ngoài. Hỏi bà Năm nhé.
- Chưa nghe băng: **Trang:** Mình chưa kiểm tra băng. Không thể viết kết luận chỉ từ nhãn hộp. Máy ở 304.

Trường hợp cuối là câu dự phòng nếu nhóm đổi thứ tự cảnh; với mạch hiện tại, nghe băng là điều kiện để tới đây. **Không nhận bản hoàn chỉnh khi còn thiếu điều kiện.**

**Khi đủ tư liệu:**

**Trang:** Ảnh lớp, trang sổ gốc, bản ghi ở cổng, băng. Mình có đủ để đối chiếu từng lời thầy vừa nói.

**Thầy Hùng:** Buổi đó có ba mươi ba học sinh. Thầy đã kiểm tra lớp trước khi tập phát thanh.

**Linh:** Bên ngoài chỉ đếm được ba mươi hai. Nga vẫn ở trong phòng khi còi vang.

**Thầy Hùng:** Thầy xác nhận. Việc ghi Nga vắng và việc báo đủ đều là sai.

**Trang:** Trang cũ giữ nguyên. Thầy viết thêm ngày hôm nay, ký tên và ghi rõ đây là đính chính.

*[Hùng viết. Nội dung tài liệu hiện để người chơi đọc.]*

> Xác nhận bổ sung về buổi học ngày 11/09/2015
>
> Lớp 12A3 có 33 học sinh tham gia buổi tập phát thanh. Tôi giao Nguyễn Thu Nga ở lại phòng 304 cất tư liệu.
>
> Khi sơ tán, chỉ có 32 học sinh được đếm bên ngoài. Tôi báo lớp đã đủ mà không kiểm tra vị trí của Nga. Băng ghi âm cho thấy em còn trong lớp khi báo động vang lên.
>
> Sau tai nạn, tôi sửa dòng điểm danh của Nga thành “vắng” và viết em đã rời lớp trước báo động. Thông tin đó không đúng. Tôi nhận trách nhiệm về việc báo sai và sửa hồ sơ.
>
> Thầy Hùng — xác nhận và ký tên trong năm 2025.

**Linh:** Em chụp lại bản này. Băng và trang sổ gốc cũng phải giữ.

**Thầy Hùng:** Thầy sẽ đem việc này tới nhà trường và gia đình Nga. Thầy biết còn phải làm ngoài căn phòng này.

**Kết quả:** nhận xác nhận đầy đủ một lần; điện thoại lưu bản chụp. Không sửa hoặc xoá dòng gốc của Nga.

### C5-06 — Cái tên không phải của Linh

**Kích hoạt:** ngay sau khi đã có xác nhận đầy đủ; xảy ra một lần trước cảnh cuối. Không cho đáp trong đoạn minh hoạ này.

*[Giọng điểm danh vọng từ hành lang. Mực trên sổ hiện một dòng tên Linh mờ dưới dòng thứ 33.]*

**Giọng điểm danh:** Nguyễn Thu Nga.

**Linh:** Tên tớ… sao lại ở đây?

**Trang:** Đó không phải tên cậu vừa nghe. Đừng trả lời.

**Đức:** Cứ để chị tớ trả lời tên của chị.

*[Linh im lặng. Tên Linh mờ đi.]*

**Linh:** Mất rồi.

**Trang:** Cậu thấy nó đổi tên người ngồi chỗ đó rồi đấy. Mình không đáp thay ai.

**Thầy Hùng:** Thầy sẽ nói đúng việc thầy đã làm. Phần trả lời tên, để Nga tự trả lời.

**Kết quả:** đã nhìn thấy nguy cơ trả lời thay. Mở mục tiêu “Trở lại 304 với bản xác nhận.” Trong bản A/B, quy luật này vẫn tạo không khí nhưng không xuất hiện lựa chọn C chưa làm xong.

## Cảnh 6 — Gọi đủ tên

### C6-01 — Chuẩn bị buổi điểm danh

**Kích hoạt:** chọn chuẩn bị tại bàn giáo viên trong 304. Khi chưa đủ điều kiện, Linh tự xem ghi chú; chưa chuyển các NPC sang phòng và chưa chạy điểm danh cuối.

**Hiện một câu ứng với điều còn thiếu rồi mở lựa chọn “Tiếp tục tìm” / “Cất sổ và rời trường”:**

- Chưa xem ảnh: **Linh:** Mình chưa đối chiếu sĩ số. Ảnh lớp ở trên tường 304.
- Chưa đọc bản ghi và báo cáo: **Linh:** Cần bản ghi số người ở cổng và báo cáo của thầy. Hỏi bà Năm ở phòng bảo vệ.
- Chưa nghe băng: **Linh:** Mình chưa nghe băng ngày 11/09. Kiểm tra thùng thiết bị và máy trong phòng này.
- Chưa có lời nhận trách nhiệm: **Linh:** Thầy Hùng chưa xác nhận chuyện giao Nga ở lại và báo sai. Mình đem băng tới lớp bên cạnh.
- Đủ tư liệu nhưng chưa có xác nhận: **Linh:** Mình cần thầy viết và ký bản bổ sung. Thầy ở lớp bên cạnh.

Đọc trang sổ gốc là mốc đã bắt buộc để tới đây. Cảnh `C5-06` chạy liền sau nhận xác nhận, trước khi trả quyền đi về 304; không cần người chơi tự tìm một cảnh báo còn thiếu. “Tiếp tục tìm” trả quyền di chuyển; “Cất sổ và rời trường” tới `B-01` và thực hiện khoá tủ.

**Nếu đủ:** cả nhóm được đặt vào 304 trong một cảnh chuyển ngắn. Bà Năm đứng gần tủ, Hùng cạnh bàn giáo viên, Trang cạnh Linh, Đức nhìn về bàn cuối.

**Trang:** Để trang gốc ở đây. Bản bổ sung đặt cạnh nó.

**Linh:** Bản ghi ở cổng và cuộn băng cũng ở đây. Mình không cần phát lại tiếng kêu cứu.

**Thầy Hùng:** Nga, em có mặt trong buổi học. Thầy đã nhờ em ở lại.

**Thầy Hùng:** Em chưa được đếm ở sân. Thầy đã báo đủ và viết sai sự thật sau đó.

**Đức:** Chị không tự quay lại. Tụi em biết rồi.

*[Ghế cuối lớp dừng chuyển động. Bóng Nga ngồi thẳng. Giọng điểm danh đọc tới tên cuối; các tên trước thể hiện bằng đoạn âm thanh ngắn, không cần thêm NPC.]*

**Giọng điểm danh:** Nguyễn Thu Nga.

**Lựa chọn trong bản bắt buộc:**

1. **“Im lặng, chờ Nga tự trả lời.”** → `A-01`.
2. **“Cất sổ, khoá tủ và rời trường.”** → `B-01`, biến thể đã điều tra đầy đủ.

**Khi đã triển khai trọn kết thúc C mới thêm:**

3. **“Trả lời ‘Có’ thay Nga — chấp nhận nguy cơ bị giữ lại.”** → `C-01`; có xác nhận riêng trong phần bổ sung.

**Kết quả:** lựa chọn A chỉ xuất hiện sau khi đã đủ chứng cứ, xác nhận và cảnh cảnh báo. Nếu người chơi chưa đủ, vẫn có thể tiếp tục tìm hoặc chọn cất sổ để tới B; không có nút đoán đúng để bỏ qua điều tra.

## Kết thúc A — Đủ cả ba mươi ba

### A-01 — Nga tự trả lời

**Kích hoạt:** đủ điều kiện của `C6-01`, chọn chờ.

*[Một nhịp im lặng, không đếm giờ và không bắt bấm đúng thời điểm.]*

**Nga:** Có.

*[Tiếng lật sổ ngừng. Không còn quay lại tên đầu. Những bóng học sinh của ký ức rời bàn và đi qua cửa; đây không phải các nạn nhân khác. Nga đi sau cùng.]*

**Đức:** Chị Nga.

*[Nga dừng trước Đức một nhịp. Không có câu nói tha thứ hoặc xác nhận ai đã được tha thứ. Bóng em mờ đi ở cửa.]*

**Trang:** Lần này… không bắt đầu lại nữa.

**Linh:** Cửa đang mở. Mình ra ngoài nhé.

### A-02 — Sau cánh cổng

*[Nhóm đứng ở lối cổng trên bản đồ phòng bảo vệ; không dựng thêm khu vực chơi.]*

**Thầy Hùng:** Đức, thầy sẽ liên hệ gia đình em. Thầy phải nói lại những gì thầy đã viết.

**Đức:** Em sẽ nói với mẹ là mình tìm được băng. Còn chuyện của thầy, thầy phải tự nói.

**Thầy Hùng:** Thầy hiểu.

**Trang:** Bản chụp với đoạn ghi lại vẫn còn trong máy chứ?

**Linh:** Còn. Mình giữ chúng, cùng băng gốc. Ngày mai đưa cả bộ tới nhà trường.

**Đức:** Trong ảnh lớp… ghi đủ tên chị giúp tớ nhé.

**Linh:** Ừ. Nguyễn Thu Nga. Lớp 12A3.

*[Khung cuối dùng ảnh lớp đã có, thêm chú thích đầy đủ. Tiếng chuông chuyển sang buổi học bình thường.]*

**Chữ kết:**

> ĐỦ CẢ BA MƯƠI BA
>
> Buổi điểm danh đã kết thúc. Việc đính chính mới bắt đầu.

**Kết quả:** ending A. Không tuyên bố nhà trường đã giải quyết xong hoặc Nga đã tha thứ.

## Kết thúc B — Căn phòng tiếp tục đóng

### B-01 — Quyết định rút lui

**Kích hoạt:** chọn rút lui tại `C3-04`, `C6-01` hoặc tương tác tủ bất kỳ lúc nào sau khi đã được hướng dẫn. Bà Năm và các nhân vật được tập hợp bằng cảnh chuyển ngắn.

**Nếu rút lui sớm, chưa nghe băng:**

**Linh:** Mình ra trước. Tớ giữ ảnh trang sổ để còn hỏi lại.

**Đức:** Cậu đừng bỏ tên chị khỏi ghi chú nhé.

**Linh:** Tớ không bỏ. Chỉ là hôm nay mình chưa biết đủ.

**Nếu đã nghe băng nhưng chưa có xác nhận đầy đủ:**

**Linh:** Mình ra ngoài trước. Đoạn ghi lại vẫn còn; chuyện này chưa dừng ở đây.

**Đức:** Ít nhất tớ biết chị đã ở trong lớp khi còi vang.

**Trang:** Giữ băng và ghi chú. Những chỗ chưa khớp mình chưa thể coi là đã xong.

**Nếu đã có xác nhận đầy đủ:**

**Linh:** Tớ muốn đưa mọi người ra trước. Bản xác nhận và băng vẫn phải tới nhà trường.

**Thầy Hùng:** Thầy sẽ làm việc đó. Khoá căn phòng không thay đổi những gì thầy đã làm.

**Đức:** Còn chị… vẫn chưa trả lời được tên mình.

**Phần chung:**

**Bà Năm:** Cất sổ đúng ngăn. Con giữ chìa mà, khoá lại đi.

*[Nếu sổ đã được khoá trong lần thử và chưa mở lại, bỏ thao tác lặp; bà chỉ kiểm tra ổ khoá. Nếu sổ đang ở ngoài, Linh cất và khoá. Tiếng điểm danh ngừng.]*

**Trang:** Cổng mở rồi. Mình đi cùng nhau.

**Kết quả:** mọi người ra ngoài an toàn. Sổ ở đúng ngăn đã khoá; chứng cứ và ký ức của nhóm không bị xoá. Lượng thông tin trong thoại thay đổi theo tiến độ, không tạo thêm các ending B riêng.

### B-02 — Chìa khoá trên bảng

*[Bà Năm treo chìa khoá trở lại bảng trong phòng bảo vệ. Linh chỉ trả chìa sau khi nhóm đã ra ngoài.]*

**Bà Năm:** Bà sẽ ghi lại chuyện hôm nay. Không mở cái ngăn đó một mình nữa.

*[Đèn tắt. Chìa khoá rung nhẹ. Cắt về hình phòng 304 đã có.]*

**Giọng điểm danh:** Nguyễn Thu Nga.

*[Không có tiếng trả lời.]*

**Chữ kết:**

> CĂN PHÒNG TIẾP TỤC ĐÓNG
>
> Mọi người đã ra ngoài. Buổi điểm danh còn thiếu một người.

**Kết quả:** ending B bỏ ngỏ. Không trách móc người chơi vì chọn rút lui; không gọi đây là giải thoát.

## Phần bổ sung — Kết thúc C: Người trả lời thay

### C-01 — Xác nhận lựa chọn nguy hiểm

**Kích hoạt:** chỉ khi ending C đã được triển khai và người chơi chọn đáp thay ở `C6-01`. Trước đó phải đã xem `C5-06`; không để cảnh nội san là cảnh báo duy nhất.

*[Hộp xác nhận: “Bạn đã thấy tên Linh xuất hiện khi có nguy cơ đáp thay. Trả lời tên Nga có thể giữ Linh lại trong buổi điểm danh.”]*

**Lựa chọn:**

- **“Vẫn trả lời ‘Có’ thay Nga.”** → `C-02`.
- **“Không trả lời thay. Quay lại.”** → trở về lựa chọn tại `C6-01`, chưa thay đổi sổ.

### C-02 — Một người khác ở bàn cuối

**Linh:** Có.

*[Tên Linh hiện rõ dưới số thứ tự 33.]*

**Giọng điểm danh:** Đủ rồi.

*[Cửa mở. Trang bước tới cửa rồi quay lại; Linh đứng trước khung cửa nhưng không thể đi qua.]*

**Trang:** Linh, đi thôi.

**Linh:** Tớ… không bước qua được.

**Đức:** Tên Linh ở chỗ tên chị…

*[Khung nhìn chuyển sang phía Trang. Linh giờ ngồi ở bàn cuối; miệng mấp máy nhưng không nghe được tiếng.]*

**Trang:** Linh! Nhìn tớ này!

*[Cắt sang điện thoại Trang. Tin nhắn từ Linh:]*

> Đừng trả lời khi nghe họ gọi tên tớ.

**Chữ kết:**

> NGƯỜI TRẢ LỜI THAY
>
> Cửa đã mở. Linh vẫn ở lại.

**Kết quả:** ending C; xác nhận bị giữ lại, không xác nhận Linh chết. Nếu có chức năng chơi lại từ trước lựa chọn cuối thì đó là thao tác ngoài truyện. Bản A/B chưa cần checkpoint hoặc lưu ra file chỉ để dùng đoạn này.

## Câu nhắc khi tương tác lại

Các câu này thay đoạn dài đã xem. Chọn câu phù hợp tiến độ; không đọc cả bảng mỗi lần tương tác.

| Đối tượng / tình trạng | Câu hiển thị |
|---|---|
| Trang, còn ở đầu cuộc điều tra, chưa xem ảnh | **Trang:** Mình xem ảnh lớp rồi đối chiếu với sổ nhé. |
| Trang, chưa kiểm chứng cách rút lui | **Trang:** Bà Năm giữ chìa. Mình thử cách cất sổ trước. |
| Trang, thiếu bản ghi ở cổng | **Trang:** Bà Năm có giấy đếm người ra sân. Mình cần đọc bản đó. |
| Trang, chưa tìm băng | **Trang:** Nhãn thùng ghi thiết bị phát thanh 12A3. Kiểm tra thùng trong 304 nhé. |
| Trang, có băng nhưng chưa nghe | **Trang:** Nhặt được băng chưa đủ. Mình thử phát trên máy trong 304. |
| Trang, đã nghe băng nhưng Hùng chưa nhận trách nhiệm | **Trang:** Mình cho thầy Hùng nghe đoạn giao việc để đối chiếu nhé. |
| Trang, đủ tư liệu nhưng chưa có bản xác nhận | **Trang:** Đặt tư liệu cạnh nhau và nhờ thầy viết bản bổ sung. |
| Trang, đã có bản xác nhận và đã thấy cảnh báo | **Trang:** Mình mang cả bộ về 304. Để Nga tự trả lời tên mình. |
| Đức, chưa nhận ra ảnh cùng Linh và chưa nghe băng | **Đức:** Tớ muốn biết chị đã ở đâu lúc cháy. |
| Đức, đã nhận ra ảnh ở `C4-01` nhưng chưa nghe băng | **Đức:** Tớ nhận ra chị trong ảnh. Nhưng ảnh không cho biết chị ở đâu lúc cháy. |
| Đức, sau khi nghe băng | **Đức:** Nhà tớ cần nghe lại đoạn này. Đừng để nó bị mất nữa nhé. |
| Bà Năm, đã giao chìa | **Bà Năm:** Chìa con giữ rồi. Đúng ngăn dưới, rồi khoá lại. |
| Bà Năm, đã giao pin | **Bà Năm:** Pin con cầm rồi. Máy ở phòng 304. |
| Hùng, đã thú nhận nhưng chưa đủ tư liệu | **Thầy Hùng:** Thầy sẽ viết những việc thầy đã làm. Các em đem bản ghi ra đối chiếu nhé. |
| Hùng, đã có xác nhận | **Thầy Hùng:** Bản bổ sung đã ký. Thầy đi cùng các em về 304. |
| Ảnh lớp đã đọc | **Linh:** Ba mươi ba học sinh. Mình đã lưu ảnh và ghi chú. |
| Thùng đã lấy băng | **Linh:** Mình lấy đúng cuộn băng rồi. Giấy kiểm kê vẫn ở đây. |
| Máy, thiếu băng | **Linh:** Ngăn băng trống. Kiểm tra thùng thiết bị phát thanh. |
| Máy, có băng nhưng chưa biết thiếu pin | Chạy `C4-03` một lần. |
| Máy, đã biết thiếu pin và chưa nhận pin | **Linh:** Cần bộ pin đúng loại. Mình đã ghi lại loại để hỏi bà Năm. |
| Máy, đủ đồ nhưng chưa nghe | Chạy `C4-05`. |
| Máy, đã nghe | **Linh:** Mình đã lưu đoạn ghi lại. Có thể xem bản chép trong ghi chú. |
| Đóng bìa sổ khi hiện tượng đang hoạt động | **Trang:** Đóng bìa chưa đủ. Phải cất đúng ngăn và khoá tủ. |
| Tủ, trước khi có chìa / chưa biết cách rút lui | **Linh:** Ngăn dưới bị khoá. Hỏi bà Năm trước. |
| Cầu thang sau khi đã xác nhận vòng lặp | **Linh:** Lối này vẫn đưa mình về 304. Tìm cửa phòng bảo vệ. |

**Nói với Trang khi cô đang ở phòng khác:** game chỉ mở hội thoại khi Linh tới đúng vị trí của Trang. Các gợi ý trong bảng có thể hiện như ghi chú mục tiêu sau cảnh tương ứng, không phát tiếng một NPC vắng mặt.

**Tủ sau lần thử:** khi sổ đang ở ngoài và nhóm đã biết cách khoá, hiện hai lựa chọn “Cất sổ và rời trường” → `B-01`, “Tiếp tục tìm” → đóng hộp thoại. Khi sổ vẫn đang khoá sau lần thử, dùng quyết định ở `C3-04`. Không xoá manh mối nếu cất sổ.

## Ghi chú triển khai cho nhóm

### Mạch chính và điều kiện

```text
Mở màn: địa điểm → tin nhắn → hành lang → vào 304
    → khám phá 304 → mở sổ → hành lang lặp → gặp Đức → gặp bà Năm
    → thử cất sổ → gặp Hùng → quyết định
                                  ├─ rời trường → B
                                  └─ mở lại sổ → tìm tư liệu + nghe băng
                                                      → Hùng nhận trách nhiệm
                                                      → đủ tư liệu → bản xác nhận
                                                      → cảnh báo → trở lại 304
                                                                         ├─ chờ Nga → A
                                                                         ├─ cất sổ → B
                                                                         └─ đáp thay → C (bổ sung)

Sau khi đã biết cách cất sổ, có thể trở lại tủ để chọn B trong lúc điều tra.
```

| Mốc cần ghi nhớ | Khi đạt | Dùng để làm gì |
|---|---|---|
| Đã xem ảnh lớp | `C1-02` hoặc xem cùng Đức ở `C4-01` | Đối chiếu sĩ số; cần cho bộ tư liệu A. |
| Đã đọc sổ bị sửa | `C1-04` | Kích hoạt ký ức, giữ trang gốc; cần cho A. |
| Đã gặp Đức | `C2-02` | Mở lối tới bà Năm và các cảnh có Đức. |
| Có chìa và đã thử khoá tủ | `C3-01` + `C3-02` | Cho chọn B một cách đã được kiểm chứng. |
| Đã đọc bản ghi và báo cáo | `C3-05` | Đối chiếu 32 người ra ngoài với lời “đủ”; cần cho A. |
| Có băng | `C4-02` | Cho lắp băng; chưa đồng nghĩa biết nội dung. |
| Biết máy thiếu nguồn / có pin | `C4-03` / `C4-04` | Cho phát băng khi đủ cả băng và pin. |
| Đã nghe băng | `C4-05` | Mở đối chất với Hùng; cần cho A. |
| Hùng đã nhận trách nhiệm | `C5-04` | Mở đối chiếu để viết xác nhận; không mất khi hỏi lại. |
| Có xác nhận đầy đủ | `C5-05`, đủ ảnh + sổ + bản ghi + băng đã nghe + lời nhận trách nhiệm | Mở phần chuẩn bị giải thoát. |
| Đã thấy nguy cơ đáp thay | `C5-06` | Luôn xảy ra trước lựa chọn cuối, kể cả không đọc nội san. |

**A:** đủ bộ tư liệu, xác nhận đầy đủ, đã thấy cảnh báo và chọn chờ Nga. **B:** đã có chìa, biết và kiểm chứng cách cất sổ, chủ động chọn rút lui, sổ thực sự được cất đúng ngăn và khoá. **C:** phần bổ sung đã hoàn thiện, cảnh báo đã hiện và người chơi xác nhận trả lời thay.

### Thứ tự điều tra và những đoạn có thể bỏ qua

- Ảnh, hộp băng và bản ghi được kiểm tra theo thứ tự khác vẫn có tác dụng. Đồ vật đã nhặt không mất khi chuyển phòng. Các điều kiện chưa đạt được nhắc bằng vị trí cụ thể.
- Thùng băng nhận ra nhờ nhãn ngày 11/09 và giấy kiểm kê; hình bóng chỉ gợi hướng. Không cần xem bóng rồi mới cho lấy băng.
- Nhìn ảnh gia đình và đọc nội san làm rõ nhân vật/không khí; không thêm câu đố bắt buộc. Các thông tin cần thiết vẫn có trong gặp Đức, băng, xác nhận và cảnh cảnh báo.
- Hỏi Hùng về sổ hoặc số người trước băng là tuỳ chọn. Khi có băng, có thể đi thẳng tới `C5-04`. Hỏi lại sau thú nhận phải dùng câu mới, không lặp lời chối.
- Các nhánh chỉ thay sắc thái hoặc cung cấp thông tin không cần hệ thống điểm thiện cảm. Chỉ lựa chọn rút lui, chờ hoặc đáp thay mới quyết định kết thúc.
- Trang không nói qua bản đồ. Khi hội thoại cần các nhân vật, dùng chuyển cảnh ngắn để đặt họ ở cùng phòng; sau đó trả điều khiển và giữ vị trí theo mốc.

### Nhịp và trải nghiệm đọc

- Không tự bỏ qua lời thoại khi giữ phím tương tác; người chơi đọc xong mới chuyển lượt. Không có giới hạn thời gian để chọn câu trả lời.
- Cảnh điểm danh đầu và băng dài chỉ chạy đầy đủ lần đầu. Cho đọc lại ghi chú/bản chép nếu người chơi quên, tránh phát lặp tiếng kêu cứu.
- Các câu in trên tài liệu là nội dung để đọc, không bắt nhóm làm giọng đọc. Game vẫn chơi được khi tắt âm thanh.
- Ưu tiên làm sáu cảnh và A/B bằng hình tạm. Câu nhắc, việc đọc lại và lời thoại biến thể dùng chung hộp thoại; không thêm màn hình hoặc minigame riêng.
- Có thể rút bớt câu hỏi tuỳ chọn khi đo thời lượng, nhưng giữ lần thử khoá tủ, chứng cứ băng, lời nhận trách nhiệm, bản bổ sung và cảnh báo trước đoạn cuối.

### Danh sách rà khi đưa hội thoại vào game

- [ ] Mở sổ trước xem ảnh vẫn có thể quay lại đọc ảnh và hoàn thành A.
- [ ] Lấy băng trước xem bóng vẫn phát được khi có pin.
- [ ] Hỏi Hùng khi thiếu băng không khoá nhánh; nghe băng rồi hỏi lại mở được thú nhận.
- [ ] Có băng nhưng chưa nghe không mở lời đối chất hoặc bản xác nhận.
- [ ] Thiếu bản ghi ở cổng không tự nhận xác nhận hoàn chỉnh; câu nhắc chỉ đúng nơi cần quay lại.
- [ ] Chuyển phòng không mất đồ, không nhận trùng đồ và không phát lại sự kiện đã xong.
- [ ] B từ cảnh 3 không nói trước việc Hùng sửa sổ; B muộn giữ đúng lượng thông tin đã biết.
- [ ] B kết thúc với sổ trong đúng ngăn đã khoá; đóng bìa đơn thuần không kết thúc.
- [ ] A không xảy ra khi thiếu chứng cứ, và không buộc Nga nói đã tha thứ.
- [ ] Lựa chọn C không xuất hiện trong bản chỉ triển khai A/B.
- [ ] Mọi tín hiệu cần cho tiến độ đọc được khi tắt âm thanh; lời thoại chỉ phát từ nhân vật có mặt.




