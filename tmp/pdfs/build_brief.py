from pathlib import Path
import re, html, json
from collections import Counter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether
from reportlab.lib import colors
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4, landscape
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from pypdf import PdfReader

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / 'output/pdf/Room304_Brief_Artist_Audio_Producer.pdf'
OUT.parent.mkdir(parents=True, exist_ok=True)
source = (ROOT / 'ASSET_SPEC/FOR_ARTISTS.md').read_text(encoding='utf-8-sig')
pdfmetrics.registerFont(TTFont('Noto', str(ROOT / 'game/assets/fonts/NotoSans-Regular.ttf')))
pdfmetrics.registerFontFamily('Noto', normal='Noto', bold='Noto', italic='Noto', boldItalic='Noto')
INK = colors.HexColor('#172C35')
TEAL = colors.HexColor('#126C70')
PALE = colors.HexColor('#EDF5F4')
PAGE_W, PAGE_H = landscape(A4)
WIDTH = PAGE_W - 80
styles = {
 'body': ParagraphStyle('body', fontName='Noto', fontSize=10, leading=15, textColor=INK, spaceAfter=9),
 'cell': ParagraphStyle('cell', fontName='Noto', fontSize=8.4, leading=11.7, textColor=INK, splitLongWords=True),
 'headcell': ParagraphStyle('headcell', fontName='Noto', fontSize=8.5, leading=11.7, textColor=colors.white),
 'h1': ParagraphStyle('h1', fontName='Noto', fontSize=22, leading=29, textColor=TEAL, spaceAfter=15, keepWithNext=True),
 'h2': ParagraphStyle('h2', fontName='Noto', fontSize=17, leading=23, textColor=TEAL, spaceAfter=12, keepWithNext=True),
 'label': ParagraphStyle('label', fontName='Noto', fontSize=10, leading=14, textColor=TEAL, spaceAfter=10),
 'title': ParagraphStyle('title', fontName='Noto', fontSize=40, leading=49, textColor=INK, spaceAfter=15),
}

def fmt(s):
    s = s.replace('—', '-').replace('–', '-').replace('−', '-')
    s = html.escape(s)
    s = re.sub(r'\*\*(.*?)\*\*', r'<font color="#126C70">\1</font>', s)
    s = re.sub(r'`(.*?)`', r'<font color="#126C70">\1</font>', s)
    return s

def p(s, style='body'):
    return Paragraph(fmt(s), styles[style])

def grid(rows):
    n = len(rows[0])
    if n == 7:
        weights = [1.45, 1.57, 1.80, .54, .46, .39, 1.30]
    elif n == 5:
        weights = [1.47, 1.67, .80, 3.04, .40]
    elif n == 4:
        weights = [1.46, 1.62, 1.60, 2.70] if rows[0][2]=='Kích thước' else [1.46, 1.62, .80, 3.50]
    elif n == 3:
        weights = [1.70, 1.95, 3.73]
    else:
        weights = [1.5, 6]
    widths = [WIDTH*w/sum(weights) for w in weights]
    cells = [[p(s, 'headcell' if r==0 else 'cell') for s in row] for r,row in enumerate(rows)]
    t = Table(cells, colWidths=widths, repeatRows=1, hAlign='LEFT')
    t.setStyle(TableStyle([
        ('BACKGROUND',(0,0),(-1,0),TEAL),
        ('ROWBACKGROUNDS',(0,1),(-1,-1),[colors.white,PALE]),
        ('VALIGN',(0,0),(-1,-1),'TOP'),
        ('LEFTPADDING',(0,0),(-1,-1),7),('RIGHTPADDING',(0,0),(-1,-1),7),
        ('TOPPADDING',(0,0),(-1,-1),6),('BOTTOMPADDING',(0,0),(-1,-1),6),
        ('LINEBELOW',(0,0),(-1,0),.6,TEAL),
        ('LINEBELOW',(0,1),(-1,-1),.35,colors.HexColor('#D8E5E5')),
    ]))
    return t

story=[]
story += [Spacer(1,34),p('ROOM304 / TÀI LIỆU SẢN XUẤT', 'label'),p('Artist &amp; Audio Producer'.replace('&amp;', '&'),'title'),
          p('Hình ảnh, hoạt ảnh và âm thanh cho game kinh dị tâm lý 2D.', 'h2'),
          Spacer(1,20),p('Bản bàn giao 1.0 | 11/10/2026', 'label'),
          p('Biên soạn từ ASSET_SPEC/FOR_ARTISTS.md. Giữ nguyên danh mục asset, thông số, ID, đường dẫn và câu thoại của bản nguồn. Phần hướng dẫn đọc và quy trình bàn giao là phần biên tập bổ sung.'),
          p('Các thông số PROPOSED vẫn cần duyệt. Tài liệu này không xác nhận thêm quyết định thiết kế hoặc trạng thái hoàn thành asset.'),Spacer(1,20),
          grid([['Người nhận','Phần cần đọc'],['Artist','Quy chuẩn chung; phần 1-5; kiểm tra bàn giao và xuất file.'],['Audio producer','Quy chuẩn chung; phần 6; kiểm tra bàn giao.'],['Người dựng map / tích hợp','Phần 2; đường dẫn ở các bảng; kiểm tra canvas, frame, loop và trạng thái.']]),PageBreak()]

counts=Counter(re.findall(r'\| \[ \].*? · (P[012]) \|',source))
audio=source.split('## 6.')[1].split('## 7.')[0]
audio_counts=Counter(re.findall(r'\| \[ \].*? · (P[012]) \|',audio))
story += [p('Cách đọc và triển khai','h1'),
          grid([['Nhãn','Ý nghĩa'],['CONFIRMED','Đã xác nhận theo thiết kế/bản đang chạy trong tài liệu nguồn.'],['PROPOSED','Đề xuất cần duyệt trước khi sản xuất hàng loạt.'],['UNDECIDED','Chưa chốt; không tự suy diễn thành yêu cầu đã duyệt.'],['P0 / P1 / P2','P0 làm trước; P1 hoàn thiện quan trọng; P2 tùy chọn, chỉ làm khi được chọn.']]),Spacer(1,12),
          p(f'Danh mục có {sum(counts.values())} dòng chưa đánh dấu hoàn tất: P0 {counts["P0"]}, P1 {counts["P1"]}, P2 {counts["P2"]}. Trong đó audio có {sum(audio_counts.values())} file. Tổng này bao gồm các file TSX/TMX do người dựng map thực hiện; không phải tất cả đều là file artist phải vẽ.'),
          p('**Artist:** ưu tiên Linh, phòng 304, ảnh lớp và sổ. Trước khi làm cả bộ, trình một frame đứng và một chân dung để duyệt diện mạo, đồng phục và bảng màu. Giữ riêng đồ tương tác và các trạng thái.'),
          p('**Audio producer:** ưu tiên hai loop nền P0. Mỗi lời băng là một file riêng; chỉ thu các lời trong danh sách, chưa thu toàn bộ hội thoại thường. Thông số thu/xuất và cách phối âm đều là PROPOSED.'),
          p('**Phạm vi C:** kết thúc C hiện tắt mặc định theo bản nguồn. Asset C vẫn có trong danh mục; xác nhận phạm vi giao trước khi sản xuất bộ C. Không nhầm nhãn P1 với việc C đã được bật.'),
          p('**Quy trình bàn giao đề xuất:** duyệt mẫu và thông số → làm P0 → thử asset trong cảnh → hoàn thiện P1 → làm P2 đã chọn → nộp file xuất, bản nguồn, quyền sử dụng và danh sách còn thiếu.'),
          p('Các bảng dưới đây giữ nguyên yêu cầu sản xuất của bản nguồn. [ ] là dòng còn phải làm; [x] là file được bản nguồn ghi nhận đã có và kiểm tra phù hợp. Tất cả đường dẫn asset tính từ game/assets/.'),PageBreak()]

lines=source.splitlines()
i=1
story.append(p('Quy chuẩn chung','h1'))
while i<len(lines):
    line=lines[i].strip()
    if not line:
        i+=1;continue
    if line=='## Xuất file':
        story.extend([Spacer(1,10),p('Xuất file','h2')]);i+=1;continue
    if line.startswith('## '):
        story.append(PageBreak())
        story.append(p(line[3:],'h1'))
        i+=1;continue
    if line.startswith('### '):
        if line!='### Linh':story.append(PageBreak())
        else:story.append(Spacer(1,10))
        story.append(p('1. Nhân vật và hoạt ảnh / '+line[4:],'h2'))
        i+=1;continue
    if line.startswith('|'):
        rows=[]
        while i<len(lines) and lines[i].strip().startswith('|'):
            row=[s.strip() for s in lines[i].strip().strip('|').split('|')]
            if not all(re.fullmatch(r':?-+:?',s.replace(' ','')) for s in row):rows.append(row)
            i+=1
        story.extend([grid(rows),Spacer(1,12)])
        continue
    block=[line]
    i+=1
    while i<len(lines) and lines[i].strip() and not lines[i].strip().startswith(('|','#','- ')):
        block.append(lines[i].strip());i+=1
    text=' '.join(block)
    if text.startswith('- '):text=text[2:]
    story.append(p(text))

def furniture(canvas, doc):
    canvas.setStrokeColor(TEAL)
    canvas.setLineWidth(.6)
    canvas.line(40,PAGE_H-30,PAGE_W-40,PAGE_H-30)
    canvas.setFont('Noto',8)
    canvas.setFillColor(TEAL)
    canvas.drawString(40,PAGE_H-23,'ROOM304  /  ARTIST & AUDIO PRODUCER')
    canvas.setFillColor(INK)
    canvas.drawString(40,22,'BRIEF 1.0  |  11/10/2026  |  Thông số đề xuất cần duyệt')
    canvas.drawRightString(PAGE_W-40,22,str(doc.page))

class BriefDoc(SimpleDocTemplate):
    def afterFlowable(self, flowable):
        if isinstance(flowable,Paragraph) and flowable.style.name in ('h1','h2'):
            title=flowable.getPlainText()
            key='bookmark-'+str(self.seq.nextf('bookmark'))
            self.canv.bookmarkPage(key)
            self.canv.addOutlineEntry(title,key,level=0,closed=False)

doc=BriefDoc(str(OUT),pagesize=(PAGE_W,PAGE_H),rightMargin=40,leftMargin=40,topMargin=48,bottomMargin=42,
             title='Room304 - Tài liệu cho Artist và Audio Producer',author='Room304',pageCompression=1)
doc.build(story,onFirstPage=furniture,onLaterPages=furniture)
reader=PdfReader(str(OUT))
alltext='\n'.join(page.extract_text() or '' for page in reader.pages)
ids=re.findall(r'\| \[[ x]\] ([\w.]+)',source)
missing=[asset_id for asset_id in ids if asset_id not in alltext]
if missing:raise RuntimeError('Missing IDs: '+str(missing))
assert len(ids)==sum(counts.values())+2
print(json.dumps({'output':str(OUT),'pages':len(reader.pages),'asset_rows':sum(counts.values()),'audio_files':sum(audio_counts.values()),'priorities':dict(counts),'all_ids_present':True},ensure_ascii=False))
