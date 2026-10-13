// Read-only parity check: no runtime dependency, no source-design mutation.
import {readFile} from 'node:fs/promises';
import {fileURLToPath} from 'node:url';
import path from 'node:path';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'../..');
const source=await readFile(path.join(root,'AGENTS/kich-ban-va-flow-can-phong-304.md'),'utf8');
const content=JSON.parse(await readFile(path.join(root,'game/assets/story/content.json'),'utf8'));
const rows=new Map();
for(const line of source.split(/\r?\n/)) {
  if(!line.startsWith('|'))continue;
  const cells=line.split('|').slice(1,-1).map(c=>c.trim());
  if(/^[A-Z][A-Z0-9-]*\d|^(UI|DOC|NOTE|OBJ|RE|CHO|TRANSCRIPT)-/.test(cells[0]))if(!rows.has(cells[0]))rows.set(cells[0],cells);
}
let strings=0,beats=0,lines=0;
for(const [id,text] of Object.entries(content.strings)) {
  const cells=rows.get(id);if(cells) {
    const expected=cells.length>=8?cells[2]:cells[1];
    if(expected!==text)throw new Error('Source string changed: '+id);
    strings++;
  }
}
for(const block of content.blocks) for(const beat of block.sourceRows) {
  const row=rows.get(beat.id);if(!row||row.length<8)throw new Error('Source beat missing: '+beat.id);
  for(const [field,index] of Object.entries({visual:3,audio:4,wait:5,effect:6}))if(beat[field]!==row[index])throw new Error('Source metadata changed: '+beat.id+'/'+field);
  beats++;
}
for(const dialogue of content.dialogues)for(const line of dialogue.lines) {
  if(!line.text)continue;
  let expected=content.strings[line.id];
  if(expected==null){const candidates=Object.keys(content.strings).filter(id=>line.id.endsWith('-'+id));if(candidates.length===1)expected=content.strings[candidates[0]];}
  if(expected==null){const row=rows.get(line.id);if(row && ['Thoại','Chữ màn hình'].includes(row[1]))expected=row[2];}
  if(expected==null){if(!Object.values(content.strings).includes(line.text))throw new Error('Text outside authored strings: '+line.id);lines++;continue;}
  if(expected!=null){if(line.speaker && expected.startsWith(line.speaker+':'))expected=expected.slice(line.speaker.length+1).trim();if(expected!==line.text)throw new Error('Runtime text changed: '+line.id);lines++;}
}
console.log('Source parity passed: '+strings+' strings, '+beats+' authored beats, '+lines+' text units checked; '+content.dialogues.length+' blocks.');
