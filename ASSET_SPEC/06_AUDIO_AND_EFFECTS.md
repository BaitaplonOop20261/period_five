# Âm thanh và hiệu ứng

37 file PROPOSED: 2 ambience,25 FX,10 giọng. Chưa có clip thật; story-audio.json hiện version1, ambience={},bindings=[]. Không coi cấu hình rỗng hoặc test backend giả là nghiệm thu âm thanh. Paths dưới game/assets/.

Nền OGG Vorbis stereo44,1kHz, loop20–40s; clip WAV PCM16 mono44,1kHz, peak≤−3dBFS, không clipping. Đây là đề xuất sản xuất, chưa kiểm âm; thời lượng FX phải khớp nhịp thật. `audio.school.birds` là bản **mix sẵn chim + tiếng trường xa**, do StoryAudio chỉ có một Music ambience cùng lúc. Slot cũ audio.school.voices không cần thu thêm/không tự layer; muốn hai stem độc lập phải mở rộng mixing presentation. Không có nhạc menu mới trong phạm vi nguồn.

| ID | File | Nội dung / định dạng | Loop | Cue runtime đã xác minh |
| --- | --- | --- | --- | --- |
| audio.school.birds | audio/ambience/school_birds.ogg | Bản mix chim + tiếng trường xa không rõ lời, không tiếng gọi tên; dùng một loop tổng. OGG Vorbis stereo 44,1 kHz; loop 20–40 s | Có | ambience[ROOM/Phenomenon] |
| audio.wind | audio/ambience/wind.ogg | Gió nhỏ của hiện tượng, không nhạc giật mình. OGG Vorbis stereo 44,1 kHz; loop 20–40 s | Có | ambience[ROOM/Phenomenon] |
| audio.fx.message | audio/sfx/message.wav | Tin nhắn; dùng lại đầu game và C. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C0-02-MAIN-E01, C-02-MAIN-E11 |
| audio.fx.door_open | audio/sfx/door_open.wav | Bản lề cửa mở nhỏ. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C0-03-MAIN-E07, C-02-MAIN-E05 |
| audio.fx.door_close | audio/sfx/door_close.wav | Cửa khép trên băng, không suy Nga ra ngoài. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-E09 |
| audio.fx.footstep | audio/sfx/footstep.wav | Một bước chân thường, không tạo bước chân thứ ba trước hiện tượng. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | footsteps.distance (đề xuất45 px) |
| audio.fx.page | audio/sfx/page.wav | Lật giấy/sổ; không âm chạy chữ. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C1-04-READ-E01, C6-01-GATHER-E10 |
| audio.fx.camera | audio/sfx/camera.wav | Chụp nhỏ. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C1-02-SAVE-E01, C1-04-READ-E09, C2-03-DOC-E02, C3-05-SAVE-E01, C4-01-COUNT-E04, C4-02-TAKE-E01, C5-05-FULL-E11 |
| audio.fx.bell_strange | audio/sfx/bell_strange.wav | Chuông trường sai giờ, một lần C1. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C1-04-HAUNT-E02 |
| audio.fx.bell_normal | audio/sfx/bell_normal.wav | Chuông bình thường của A. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | A-02-MAIN-E08 |
| audio.fx.chair | audio/sfx/chair.wav | Ghế bàn cuối kéo; ngắn, không lặp vô hạn. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C1-04-HAUNT-E03, C4-05-MAIN-E14 |
| audio.fx.knock_three | audio/sfx/knock_three.wav | Ba tiếng gõ liên tiếp từ cuối hành lang. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C2-04-MAIN-E01 |
| audio.fx.knock_one | audio/sfx/knock_one.wav | Một tiếng trong tủ khóa. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C3-04-MAIN-E01 |
| audio.fx.lock | audio/sfx/lock.wav | Ổ khóa khép lại. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C3-02-MAIN-E02, B-01-LOCK-E02 |
| audio.fx.unlock | audio/sfx/unlock.wav | Mở khóa ngăn dưới. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C3-04-CONFIRM-E01 |
| audio.fx.key | audio/sfx/key.wav | Chìa chạm tay/móc; bản ngắn. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C3-01-MAIN-E13, B-02-MAIN-E01 |
| audio.fx.key_rattle | audio/sfx/key_rattle.wav | Chìa rung nhẹ, không jump scare. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | B-02-MAIN-E03 |
| audio.fx.gate | audio/sfx/gate.wav | Cổng mở về bên ngoài. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C3-02-MAIN-E08, B-01-EXIT-E03 |
| audio.fx.chalk | audio/sfx/chalk.wav | Phấn kẻ một vạch nhỏ. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C2-01-MAIN-E05 |
| audio.fx.button | audio/sfx/button.wav | Nút cassette cơ/stop. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-03-MAIN-E01, C4-05-END-E01, C5-04-MAIN-E06 |
| audio.fx.lid | audio/sfx/lid.wav | Nắp khay pin khép. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-03-END-E01, C4-05-MAIN-E01 |
| audio.fx.paper | audio/sfx/paper.wav | Xếp giấy/trao túi pin. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-04-MAIN-E03, C4-05-MAIN-E09, C5-05-FULL-E01 |
| audio.fx.tape_hiss | audio/sfx/tape_hiss.wav | Rè ngắn lúc bắt đầu ghi. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-E05 |
| audio.fx.tape_stop | audio/sfx/tape_stop.wav | Nhiễu băng rồi dừng; không tiếng tử vong. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-E13 |
| audio.fx.alarm | audio/sfx/alarm.wav | Còi chỉ có trên băng năm 2015. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-E10 |
| audio.fx.run | audio/sfx/run.wav | Tiếng người chạy xa trên băng, vô danh. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-E10 |
| audio.fx.write | audio/sfx/write.wav | Bút viết/ký trên tờ mới. WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C5-05-FULL-E07 |
| audio.voice.call | audio/voice/call.wav | Giọng điểm danh: “Nguyễn Thu Nga.” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C1-04-HAUNT-D06, C5-06-MAIN-D01, C6-01-GATHER-D11, B-02-MAIN-D04 |
| audio.voice.hung_assignment | audio/tape/hung_assignment.wav | Hùng trên băng: “Nga ở lại cất giúp thầy bộ tư liệu nhé. Xong rồi em xuống.” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-D06, C5-04-MAIN-D04 |
| audio.voice.nga_recorder | audio/tape/nga_recorder.wav | Nga trên băng: “Dạ. Em cất máy ghi âm luôn phải không thầy?” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-D07, C5-04-MAIN-D05 |
| audio.voice.hung_finish | audio/tape/hung_finish.wav | Hùng trên băng: “Ừ. Để băng chạy hết rồi cất giúp thầy.” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-D08 |
| audio.voice.nga_help_1 | audio/tape/nga_help_1.wav | Nga trên băng: “Có ai ngoài đó không?” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-D11 |
| audio.voice.nga_help_2 | audio/tape/nga_help_2.wav | Nga trên băng: “Em vẫn ở trong lớp! Có ai nghe không?” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C4-05-MAIN-D12 |
| audio.voice.nga_stayed | audio/voice/nga_stayed.wav | Nga: “Thầy bảo em ở lại.” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C5-04-CONFESS-D02 |
| audio.voice.nga_present | audio/voice/nga_present.wav | Nga ở A: “Có.” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | A-01-MAIN-D02 |
| audio.voice.linh_present | audio/voice/linh_present.wav | Linh ở C: “Có.” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C-02-MAIN-D01 |
| audio.voice.enough | audio/voice/enough.wav | Giọng điểm danh ở C: “Đủ rồi.” WAV PCM16 mono 44,1 kHz; cắt riêng từng cue | Không | C-02-MAIN-D04 |

## Binding

`game/core/src/main/java/com/room304/audio/StoryAudio.java` đọc config/story-audio.json. Ambience `ROOM_304/NORMAL`, `HALLWAY/NORMAL`… → music ID; ACTIVE dùng audio.wind, NORMAL/SUSPENDED/RESOLVED dùng school mix nếu cảnh nguồn cho phép. Không tự bật nền trường trong B cut cuối304 chỉ vì có event mới; nghe/so sánh sourceRows. C1-04-HAUNT chủ động tắt ambience trước commit sổ.

Bindings có `{cue,asset,channel,loop,decorative}`: cue là ID **line** thật trong content.dialogues, không phải SFX string/alias scene; voice hoặc fx; decorative chỉ dùng cho fx; cùng cue được có nhiều asset khác nhau. Các cue trong bảng là nơi bắt đầu nối, không tuyên bố đã gắn. Tìm thêm nhịp lặp qua sourceRows.audio khi nhập từng nhóm; không tự phát mọi FX trên mọi dòng.

Một Sound instance dừng khi chuyển line; hết clip chữ vẫn chờ Enter. Pause giữ instance; không gọi Sound.stop() toàn cục làm tắt người dùng khác. Loop mặc địnhfalse; chỉ loop FX khi nguồn yêu cầu duy trì đúng một nhịp. Hiệu ứng tự động0,2–0,5s có thể cắt clip dài: trim clip hoặc phối đúng nhịp được chốt, không đổi thời gian gameplay để chạy hết file bất kỳ. Footstep.distance45px là PROPOSED, tương ứng 2 bước/cycle6frame8FPS ở speed120; lấy quãng đường sau collision, không tạo tiếng khi đứng chạm tường/pause/teleport.

**Băng:** C4-05 có5 lời riêng + cửa/giấy + còi/chạy + nhiễu/dừng. C5-04 runtime/source chỉ phát lại **hai lời đầu** (D04,D05), không phát lời thứ ba hoặc tiếng kêu cứu. Không làm một track dài tự chạy sang câu sau. Giọng điểm danh chỉ đúng tên Nga, trừ “Đủ rồi.” ở C; không thêm32 tên hoặc tiếng chết. C đoạn Linh mấp máy phải im lặng.

MIX-01: khi có thoại/phụ đề, ambience25% mức người dùng, FX decorative40%; hồi về trong0,25s. Voice không bị duck như decorative FX. “Âm lượng0” không được khóa gate băng hoặc làm mất phụ đề. Không thu full voice mọi hội thoại thường ở giai đoạn này: phạm vi lồng tiếng toàn bộ và diễn viên UNDECIDED; mười câu giọng trên là bộ ưu tiên có mục đích trực tiếp trong truyện.

## Hiệu ứng hình ảnh — không thêm bộ art vô ích

| Asset / phần | Binding / tình trạng |
| --- | --- |
| effect.memory → effects/memory.png | 24×40 PNG PROPOSED; A-01-MAIN-E03, runtime vẽ3 bản dịch chuyển/fade, không animsheet. |
| prop.chair → sprites/props/chair.png | 22×9 PNG; camera không rung mạnh, ghế thật tách bóng. |
| ui.icon.camera → ui/camera.png | 16×16; camera cue native px .45s; không flash nhanh. |
| ui.icon.record → ui/record.png | 16×16; record giữ qua các dòng trong block. Waveform chưa có. |
| Tên Linh / ánh lạnh / mờ chuyển | Chữ/geometry; không PNG riêng. TMX opaque cần thêm pass tint/overlay. |
| C viewpoint | Pose/phone đã có cue; đổi góc nhìn vẫn cần chỉnh presentation, không cần map/nhân vật mới. |

- [ ] Nghe thật loop không click, peak không vỡ, voice/subtitle khớp.
- [ ] Tắt âm/đổi mức/pause/đổi line/chơi lại không sót clip.
- [ ] Không hiện tượng sớm trước C1-04; không chuông/tiếng gọi lặp sai khi đọc lại.
- [ ] A/B/C giữ đúng cảnh âm và sự im lặng, không tự thêm nhạc/giọng làm đổi nghĩa.
