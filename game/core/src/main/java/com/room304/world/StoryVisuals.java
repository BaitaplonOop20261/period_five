package com.room304.world;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.room304.assets.AssetCatalog;
import com.room304.story.StoryContent;
import java.util.*;

/** Presentation bindings only. No conditions, actions, ownership or save state. */
public final class StoryVisuals {
    public record Actor(String id,String pose,String anchor,float dx,float dy,float alpha,String to,float seconds,boolean hidden) {}
    public record Cue(List<Actor> actors,String effect,String asset,boolean clear) {}
    private final Map<String,Cue> cues=new HashMap<>();
    private final Map<String,String> blockImages=new HashMap<>();
    private final Map<String,String> documents=new LinkedHashMap<>(),lineDocuments=new HashMap<>(),menus=new HashMap<>();
    public static StoryVisuals load(FileHandle file,AssetCatalog assets,StoryContent content,SceneCatalog world){
        try{
            StoryVisuals result=new StoryVisuals();JsonValue root=new JsonReader().parse(file);
            if(root.has("blockImages"))for(JsonValue v:root.get("blockImages")){
                texture(v.asString(),assets);
                if(!content.blockIds().contains(v.name))content.text(v.name);
                result.blockImages.put(v.name,v.asString());
            }
            if(root.getInt("version")!=1)throw new IllegalArgumentException("Unsupported visual config");
            for(JsonValue v:root.get("documents")){texture(v.asString(),assets);content.text(v.name+"-01");result.documents.put(v.name,v.asString());}
            for(JsonValue v:root.get("lineDocuments")){if(!content.hasLine(v.name)||!result.documents.containsKey(v.asString()))throw new IllegalArgumentException("Invalid document binding "+v.name);result.lineDocuments.put(v.name,v.asString());}
            for(JsonValue v:root.get("menus")){if(!Set.of("MENU","A","B","C").contains(v.name))throw new IllegalArgumentException("Unknown menu "+v.name);texture(v.asString(),assets);result.menus.put(v.name,v.asString());}
            for(JsonValue v:root.get("cues")){
                String cue=v.getString("cue");if(!content.hasLine(cue))throw new IllegalArgumentException("Unknown visual cue "+cue);
                String room=v.getString("room",null),effect=v.getString("effect",null),asset=v.getString("asset",null);
                SceneCatalog.Scene scene=room==null?null:world.scene(com.room304.story.GameState.RoomId.valueOf(room));
                if(effect!=null&&!Set.of("camera","chair","record","memory","phone").contains(effect))throw new IllegalArgumentException("Unknown visual effect "+effect);
                if(asset!=null)texture(asset,assets);
                List<Actor> actors=new ArrayList<>();Set<String> ids=new HashSet<>();
                for(JsonValue a:v.get("actors")){
                    String id=a.getString("id"),pose=a.getString("pose","idle.down"),anchor=a.getString("anchor",null),to=a.getString("to",null);
                    if(!ids.add(id)||(!id.equals("linh")&&!world.npcIds().contains(id))||!pose.matches("(idle|walk)\\.(down|up|left|right)|pose\\.[a-z0-9_-]+"))throw new IllegalArgumentException("Invalid visual actor "+cue+"/"+id);
                    if(anchor!=null)validateAnchor(scene,anchor);if(to!=null)validateAnchor(scene,to);
                    float dx=a.getFloat("dx",0),dy=a.getFloat("dy",0),alpha=a.getFloat("alpha",1),seconds=a.getFloat("seconds",0);
                    if(!Float.isFinite(dx)||!Float.isFinite(dy)||!Float.isFinite(alpha)||alpha<0||alpha>1||!Float.isFinite(seconds)||seconds<0||(to!=null&&seconds<=0))throw new IllegalArgumentException("Invalid visual geometry "+cue+"/"+id);
                    actors.add(new Actor(id,pose,anchor,dx,dy,alpha,to,seconds,a.getBoolean("hidden",false)));
                }
                if(result.cues.putIfAbsent(cue,new Cue(List.copyOf(actors),effect,asset,v.getBoolean("clear",false)))!=null)throw new IllegalArgumentException("Duplicate visual cue "+cue);
            }
            return result;
        }catch(RuntimeException e){throw new IllegalArgumentException("Invalid story visuals "+file.path()+": "+e.getMessage(),e);}
    }
    private static void validateAnchor(SceneCatalog.Scene scene,String anchor){
        if(scene==null)throw new IllegalArgumentException("Visual anchors require room");
        if(anchor.startsWith("npc:")){if(!scene.npcSlots.containsKey(anchor.substring(4)))throw new IllegalArgumentException("Missing NPC visual anchor "+anchor);}
        else scene.object(anchor);
    }
    private static void texture(String id,AssetCatalog assets){if(!assets.entry(id).type().equals("texture"))throw new IllegalArgumentException("Expected visual texture "+id);}
    public Cue cue(String id){return cues.get(id);}
    public String menu(String id){return menus.get(id);}
    public String document(String lineId,String blockId){
        String image=document(lineId);
        return image!=null?image:blockImages.get(blockId);
    }
    public String document(String lineId){
        String doc=lineDocuments.get(lineId);if(doc!=null)return documents.get(doc);
        if(lineId!=null)for(var entry:documents.entrySet())if(lineId.startsWith(entry.getKey()+"-"))return entry.getValue();
        return null;
    }
}
