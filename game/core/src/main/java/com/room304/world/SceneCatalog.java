package com.room304.world;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.room304.assets.AssetCatalog;
import com.room304.story.GameState;
import java.util.*;
import static com.room304.story.GameState.*;

/** Four authored scenes, spawn geometry and stage placements; no rendering or story effects. */
public final class SceneCatalog {
    public static final class Point { public float x, y; }
    public static class Box { public float x, y, width, height; public Rectangle bounds() { return new Rectangle(x,y,width,height); } }
    public static final class ObjectDef extends Box {
        public String id, type, name, label, asset, target, spawn, dialogue, requiredEvent, inspection;
        public float range;
        public Box interaction;
        public float[] highlightOutline = new float[0];
        public HashMap<String,String> variants = new HashMap<>(), inspectionVariants = new HashMap<>();
        public Rectangle interactionBounds() { return interaction==null?bounds():interaction.bounds(); }
    }
    public static final class CharacterDef {
        public String id, name, dialogue, requiredEvent; public float canvasWidth=32,canvasHeight=48,footWidth=18,footHeight=10,scale=1,anchorX=.5f,anchorY=0; public HashMap<String,String> animations = new HashMap<>();
    }
    public static final class PlayerDef {
        public float speed, scale, footWidth, footHeight, canvasWidth, canvasHeight; public float anchorX=.5f,anchorY=0;
        public HashMap<String,String> animations = new HashMap<>();
    }
    public static final class Placement { public String id, room, event; public Float x,y; }
    public static final class Scene {
        public String id, name, background, tmx; public float width, height; public HashMap<String,String> backgrounds = new HashMap<>();
        public HashMap<String,Point> spawns, npcSlots;
        public ArrayList<Box> collisions; public ArrayList<ObjectDef> objects;
        public ObjectDef object(String id) { return objects.stream().filter(o -> o.id.equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Missing interaction " + this.id + "/" + id)); }
        public Point spawn(String id) { Point p = spawns.get(id); if (p == null) throw new IllegalArgumentException("Missing spawn " + this.id + "/" + id); return p; }
    }
    public static final class Data {
        public int version; public boolean enableC; public PlayerDef player;
        public ArrayList<CharacterDef> npcs; public HashMap<String,ArrayList<Placement>> placements;
        public ArrayList<Scene> scenes;
    }
    private final Data data;
    private final Map<RoomId,Scene> scenes = new EnumMap<>(RoomId.class);
    private final Map<String,CharacterDef> npcs = new LinkedHashMap<>();
    private SceneCatalog(Data data) { this.data = data; }
    public static SceneCatalog load(FileHandle file, AssetCatalog assets) {
        try {
            Json json = new Json(); JsonValue root = new JsonReader().parse(file);
            JsonValue placementData = root.remove("placements");
            Data d = json.readValue(Data.class, root); d.placements = new HashMap<>();
            for (JsonValue stage : placementData) {
                ArrayList<Placement> values = new ArrayList<>();
                for (JsonValue value : stage) values.add(json.readValue(Placement.class,value));
                d.placements.put(stage.name,values);
            }
            SceneCatalog catalog = new SceneCatalog(d);
            if (d.version != 1 || d.player == null || !positive(d.player.speed) || !positive(d.player.scale)
                || !positive(d.player.footWidth) || !positive(d.player.footHeight) || !positive(d.player.canvasWidth) || !positive(d.player.canvasHeight))
                throw new IllegalArgumentException("Invalid world/player specification");
            for (CharacterDef n : d.npcs) if (n.id == null || !n.id.matches("[a-z0-9_-]+") || n.name == null || n.name.isBlank() || catalog.npcs.putIfAbsent(n.id,n) != null)
                throw new IllegalArgumentException("Invalid/duplicate NPC " + n.id);
            for (Scene s : d.scenes) {
                if (!positive(s.width) || !positive(s.height) || catalog.scenes.putIfAbsent(RoomId.valueOf(s.id),s) != null)
                    throw new IllegalArgumentException("Invalid/duplicate scene " + s.id);
                if (!assets.entry(s.background).type().equals("texture") || !assets.entry(s.tmx).type().equals("file")) throw new IllegalArgumentException("Wrong scene asset type " + s.id);
                for(var bg:s.backgrounds.entrySet()){Phenomenon.valueOf(bg.getKey());texture(bg.getValue(),assets);}
                Set<String> ids = new HashSet<>();
                for (Box b : s.collisions) validateBox(b,s);
                for (ObjectDef o : s.objects) {
                    validateBox(o,s);
                    if(o.interaction!=null)validateBox(o.interaction,s);
                    if (o.id == null || !ids.add(o.id) || !Set.of("PROP","DOOR").contains(o.type) || o.label == null || !Float.isFinite(o.range) || o.range < 0)
                        throw new IllegalArgumentException("Invalid/duplicate interaction " + s.id + "/" + o.id);
                    for(String variant:o.variants.keySet()){if(!Set.of("normal","open","closed","locked","empty","powered","playing","carried","final").contains(variant))throw new IllegalArgumentException("Unknown prop variant "+o.id+"/"+variant);texture(o.variants.get(variant),assets);}
                    if (o.asset != null && !assets.entry(o.asset).type().equals("texture")) throw new IllegalArgumentException("Wrong prop asset " + o.id);
                    if (o.inspection != null) texture(o.inspection,assets);
                    if (o.highlightOutline.length!=0) {
                        if(o.highlightOutline.length<6 || o.highlightOutline.length%2!=0)
                            throw new IllegalArgumentException("Invalid highlight outline " + o.id);
                        for(float coordinate:o.highlightOutline) if(!Float.isFinite(coordinate)||coordinate<0||coordinate>1)
                            throw new IllegalArgumentException("Highlight outline outside object " + o.id);
                    }
                    for (var variant:o.inspectionVariants.entrySet()) {
                        if (!Set.of("normal","open","closed","locked","empty","powered","playing","final").contains(variant.getKey()))
                            throw new IllegalArgumentException("Unknown inspection variant " + o.id + "/" + variant.getKey());
                        texture(variant.getValue(),assets);
                    }
                }
                for (Point p : s.spawns.values()) if (!catalog.canStand(s,p.x,p.y)) throw new IllegalArgumentException("Blocked/outside spawn " + s.id);
                for (var slot : s.npcSlots.entrySet()) if (!catalog.npcs.containsKey(slot.getKey()) || !catalog.npcCanStand(s,slot.getKey(),slot.getValue().x,slot.getValue().y)) throw new IllegalArgumentException("Invalid NPC slot " + s.id + "/" + slot.getKey());
            }
            if (catalog.scenes.size() != RoomId.values().length) throw new IllegalArgumentException("Expected all four rooms");
            for (Scene s : d.scenes) for (ObjectDef o : s.objects) if (o.target != null) catalog.scene(RoomId.valueOf(o.target)).spawn(o.spawn);
            for (NpcStage stage : NpcStage.values()) {
                List<Placement> placements = d.placements.get(stage.name());
                if (placements == null) throw new IllegalArgumentException("Missing placements " + stage);
                Set<String> ids = new HashSet<>();
                for (Placement p : placements) {
                    if (!catalog.npcs.containsKey(p.id) || !ids.add(p.id)) throw new IllegalArgumentException("Unknown/duplicate placement " + p.id);
                    if (!"CURRENT".equals(p.room)) catalog.scene(RoomId.valueOf(p.room));
                    if (p.event != null) Event.valueOf(p.event);
                    if((p.x==null)!=(p.y==null))throw new IllegalArgumentException("Incomplete stage position "+p.id);
                    if(p.x!=null){if(p.room.equals("CURRENT")){for(Scene scene:catalog.scenes.values())if(!catalog.npcCanStand(scene,p.id,p.x,p.y))throw new IllegalArgumentException("Invalid stage position "+p.id);}else if(!catalog.npcCanStand(catalog.scene(RoomId.valueOf(p.room)),p.id,p.x,p.y))throw new IllegalArgumentException("Invalid stage position "+p.id);}
                }
            }
            for (CharacterDef n : d.npcs) {validateAnimations(n.animations,assets);if(!positive(n.canvasWidth)||!positive(n.canvasHeight)||!positive(n.footWidth)||!positive(n.footHeight)||!positive(n.scale)||!anchor(n.anchorX)||!anchor(n.anchorY))throw new IllegalArgumentException("Invalid NPC render profile "+n.id);}
            if(!anchor(d.player.anchorX)||!anchor(d.player.anchorY))throw new IllegalArgumentException("Invalid player anchor");
            validateAnimations(d.player.animations,assets);
            return catalog;
        } catch (RuntimeException e) { throw new IllegalArgumentException("Invalid world config " + file.path() + ": " + e.getMessage(),e); }
    }
    private static void validateAnimations(Map<String,String> animations, AssetCatalog assets) {
        for (var a : animations.entrySet()) {
            if (!a.getKey().matches("(idle|walk)\\.(down|up|left|right)|pose\\.[a-z0-9_-]+")) throw new IllegalArgumentException("Unknown animation state " + a.getKey());
            if (!assets.entry(a.getValue()).type().equals("texture") || assets.entry(a.getValue()).frameWidth() <= 0) throw new IllegalArgumentException("Missing animation metadata " + a.getValue());
        }
    }
    private static void texture(String id,AssetCatalog assets){if(!assets.entry(id).type().equals("texture"))throw new IllegalArgumentException("Expected texture "+id);}
    private static boolean anchor(float v){return Float.isFinite(v)&&v>=0&&v<=1;}
    private static boolean positive(float v) { return Float.isFinite(v) && v > 0; }
    private static void validateBox(Box b,Scene s) {
        if (!Float.isFinite(b.x) || !Float.isFinite(b.y) || !positive(b.width) || !positive(b.height)
            || b.x < 0 || b.y < 0 || b.x+b.width > s.width || b.y+b.height > s.height) throw new IllegalArgumentException("Invalid rectangle in " + s.id);
    }
    public boolean canStand(Scene s,float x,float y) {
        return canStand(s,x,y,data.player.footWidth,data.player.footHeight);
    }
    private boolean npcCanStand(Scene s,String id,float x,float y){CharacterDef n=npc(id);return canStand(s,x,y,n.footWidth,n.footHeight);}
    private boolean canStand(Scene s,float x,float y,float width,float height){
        if (!Float.isFinite(x) || !Float.isFinite(y)||!positive(width)||!positive(height)) return false;
        Rectangle feet = new Rectangle(x,y,width,height);
        return x >= 8 && y >= 8 && x+feet.width <= s.width-8 && y+feet.height <= s.height-8 && s.collisions.stream().noneMatch(b -> b.bounds().overlaps(feet));
    }
    public void validateGeometry(Scene s) {
        if(!positive(s.width)||!positive(s.height))throw new IllegalArgumentException("Invalid map dimensions "+s.id);
        for(Box b:s.collisions)validateBox(b,s);
        for(ObjectDef o:s.objects){validateBox(o,s);if(o.interaction!=null)validateBox(o.interaction,s);}
        for(Point p:s.spawns.values())if(!canStand(s,p.x,p.y))throw new IllegalArgumentException("Blocked/outside spawn "+s.id);
        for(var slot:s.npcSlots.entrySet())if(!npcs.containsKey(slot.getKey())||!npcCanStand(s,slot.getKey(),slot.getValue().x,slot.getValue().y))throw new IllegalArgumentException("Invalid NPC slot "+s.id+"/"+slot.getKey());
    }
    /** Validated TMX geometry also becomes the logical geometry used by saves and routing. */
    public void useGeometry(Scene scene) { scenes.put(RoomId.valueOf(scene.id),scene); }
    public Scene scene(RoomId id) { return Objects.requireNonNull(scenes.get(id),"Unknown room " + id); }
    public CharacterDef npc(String id) { return Objects.requireNonNull(npcs.get(id),"Unknown NPC " + id); }
    public Set<String> npcIds(){return Collections.unmodifiableSet(npcs.keySet());}
    public void validateContent(com.room304.story.StoryContent content) {
        for(CharacterDef n:npcs.values()){if(n.dialogue!=null)content.dialogue(n.dialogue);if(n.requiredEvent!=null)Event.valueOf(n.requiredEvent);}
        for(Scene s:scenes.values())for(ObjectDef o:s.objects){content.text(o.label);if(o.dialogue!=null)content.dialogue(o.dialogue);if(o.requiredEvent!=null)Event.valueOf(o.requiredEvent);}
        for(List<Placement> placements:data.placements.values())for(Placement p:placements){if(p.room.equals("CURRENT")){for(Scene s:scenes.values())if(!s.npcSlots.containsKey(p.id))throw new IllegalArgumentException("Missing NPC slot "+s.id+"/"+p.id);}else if(!scene(RoomId.valueOf(p.room)).npcSlots.containsKey(p.id))throw new IllegalArgumentException("Missing NPC placement slot "+p.room+"/"+p.id);}
    }
    public PlayerDef player() { return data.player; }
    public boolean enableC() { return data.enableC; }
    public Set<RoomId> rooms() { return Collections.unmodifiableSet(scenes.keySet()); }
    public Point npcPosition(GameState state,String id) {
        for(Placement p:data.placements.get(state.npcStage().name()))if(p.id.equals(id)&&(p.room.equals("CURRENT")||p.room.equals(state.room().name()))&&p.x!=null){Point point=new Point();point.x=p.x;point.y=p.y;return point;}
        return Objects.requireNonNull(scene(state.room()).npcSlots.get(id),"Missing NPC slot "+id);
    }
    public List<String> visibleNpcs(GameState state) {
        return data.placements.get(state.npcStage().name()).stream().filter(p -> ("CURRENT".equals(p.room) || p.room.equals(state.room().name()))
            && (p.event == null || state.reached(Event.valueOf(p.event)))).map(p -> p.id).toList();
    }
}
