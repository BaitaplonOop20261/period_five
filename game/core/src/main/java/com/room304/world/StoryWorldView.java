package com.room304.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.room304.assets.GameAssets;
import com.room304.story.*;
import java.util.*;
import static com.room304.story.GameState.*;

/** Asset-independent geometry and fallback presentation. TMX replaces geometry without replacing routing. */
public final class StoryWorldView implements Disposable {
    private static final float INTERACTION_MARK_HEIGHT = 18f;
    private final GameAssets assets;
    private final SceneCatalog catalog;
    private final StoryContent content;
    private final BitmapFont font; // borrowed from the screen's UI panel
    private final SpriteBatch batch=new SpriteBatch();
    private final ShapeRenderer shapes=new ShapeRenderer();
    private final FollowCamera camera=new FollowCamera();
    private final FitViewport hudViewport=new FitViewport(640,480);
    private final FitViewport viewport=new FitViewport(640,480,camera);
    private final RoomInteractionSystem selector=new RoomInteractionSystem(List.of());
    private final Rectangle visibleBounds=new Rectangle();
    private Animation<TextureRegion> interactionMarkAnimation;
    private final Map<String,Animation<TextureRegion>> animations=new HashMap<>();
    private final Map<String,Texture> textures=new HashMap<>();
    private SceneCatalog.Scene scene;
    private TiledMap map;
    private final java.util.Map<RoomId,TiledMap> mapCache=new EnumMap<>(RoomId.class);
    private OrthogonalTiledMapRenderer renderer;
    private List<Wall> walls=List.of();
    private float time,effectTime;
    private final StoryVisuals visuals;
    private final Map<String,StoryVisuals.Actor> poses=new HashMap<>();
    private final Map<String,Float> poseTimes=new HashMap<>();
    private String visualBlock,lastVisualLine,effect,effectAsset,recordAsset,chairAsset;
    private boolean recording,chairShift;
    public StoryWorldView(GameAssets assets,SceneCatalog catalog,StoryContent content,BitmapFont font) {
        this.assets=assets;this.catalog=catalog;this.content=content;this.font=font;
        try{visuals=StoryVisuals.load(Gdx.files.internal(assets.catalog().path("story.visuals")),assets.catalog(),content,catalog);
        interactionMarkAnimation=assets.animation("ui.interaction");
        for(var a:catalog.player().animations.entrySet()){Animation<TextureRegion> animation=assets.animation(a.getValue(),catalog.player().mirrorWalkLeft && a.getKey().equals("walk.left"));if(animation!=null)animations.put("linh/"+a.getKey(),animation);}
        for(String npc:catalog.npcIds()) for(var a:catalog.npc(npc).animations.entrySet()) {
            Animation<TextureRegion> animation=assets.animation(a.getValue());if(animation!=null) animations.put(npc+"/"+a.getKey(),animation);
        }}catch(RuntimeException e){dispose();throw e;}
    }
    public SceneCatalog.Scene scene() { return scene; }
    public StoryVisuals visuals(){return visuals;}
    private Texture texture(String id){if(id==null)return null;if(!textures.containsKey(id))textures.put(id,assets.texture(id));return textures.get(id);}
    public boolean recording(){return recording;}
    public String pose(String id){StoryVisuals.Actor a=poses.get(id);return a==null?"idle.down":a.pose();}
    public void enter(RoomId id) {
        SceneCatalog.Scene candidate=catalog.scene(id);TiledMap nextMap=null;OrthogonalTiledMapRenderer nextRenderer=null;
        String path=assets.catalog().path(candidate.tmx);
        try {
            if(Gdx.files.internal(path).exists()) {
                nextMap=mapCache.get(id);if(nextMap==null) nextMap=new TmxMapLoader().load(path);
                candidate=importGeometry(candidate,nextMap);
                nextRenderer=new OrthogonalTiledMapRenderer(nextMap,1f);
            }
            Texture background=assets.texture(candidate.background);
            textures.put(candidate.background,background);
            for(String assetId:candidate.backgrounds.values())texture(assetId);
            if(candidate.backgroundRegion!=null){
                var region=candidate.backgroundRegion;List<String> backgrounds=new ArrayList<>(candidate.backgrounds.values());backgrounds.add(candidate.background);
                for(String assetId:backgrounds){Texture image=textures.get(assetId);if(image!=null && ((long)region.x+region.width>image.getWidth() || (long)region.y+region.height>image.getHeight()))throw new IllegalArgumentException("Background region outside image: "+assetId);}
            }
            for(var o:candidate.objects)for(String assetId:o.variants.values())texture(assetId);
            for(SceneCatalog.ObjectDef o:candidate.objects) if(o.asset!=null && !textures.containsKey(o.asset)) textures.put(o.asset,assets.texture(o.asset));
        } catch(RuntimeException e) { if(nextRenderer!=null) nextRenderer.dispose();if(nextMap!=null && !mapCache.containsValue(nextMap)) nextMap.dispose();throw new IllegalArgumentException("Invalid scene asset " + id + " / " + path + ": " + e.getMessage(),e); }
        if(renderer!=null) renderer.dispose();if(nextMap!=null)mapCache.put(id,nextMap);
        scene=candidate;catalog.useGeometry(candidate);map=nextMap;renderer=nextRenderer;
        walls=scene.collisions.stream().map(b->new Wall(b.x,b.y,b.width,b.height)).toList();
        viewport.setWorldSize(scene.width,scene.height);camera.position.set(scene.width/2,scene.height/2,0);camera.update();
    }
    private SceneCatalog.Scene importGeometry(SceneCatalog.Scene original,TiledMap map) {
        MapProperties p=map.getProperties();
        if(!"orthogonal".equals(p.get("orientation")) || ((Number)p.get("tilewidth")).intValue()!=32 || ((Number)p.get("tileheight")).intValue()!=32)
            throw new IllegalArgumentException("Expected finite orthogonal TMX with 32x32 tiles");
        com.badlogic.gdx.utils.Json json=new com.badlogic.gdx.utils.Json();SceneCatalog.Scene result=json.fromJson(SceneCatalog.Scene.class,json.toJson(original));
        result.width=((Number)p.get("width")).intValue()*32;result.height=((Number)p.get("height")).intValue()*32;
        result.collisions.clear();for(MapObject o:layer(map,"collisions").getObjects()) {
            if(!(o instanceof RectangleMapObject r)) throw new IllegalArgumentException("Collision must be rectangle: " + o.getName());
            Rectangle b=r.getRectangle();SceneCatalog.Box box=new SceneCatalog.Box();box.x=b.x;box.y=b.y;box.width=b.width;box.height=b.height;result.collisions.add(box);
        }
        result.spawns.clear();for(MapObject o:layer(map,"spawns").getObjects()) {
            String name=o.getName();if(name==null || result.spawns.containsKey(name)) throw new IllegalArgumentException("Missing/duplicate spawn name");
            SceneCatalog.Point point=new SceneCatalog.Point();
            if(o instanceof PointMapObject po) {point.x=po.getPoint().x;point.y=po.getPoint().y;}
            else if(o instanceof RectangleMapObject ro) {point.x=ro.getRectangle().x;point.y=ro.getRectangle().y;}
            else throw new IllegalArgumentException("Spawn must be point/rectangle " + name);
            result.spawns.put(name,point);
        }
        Map<String,Rectangle> objects=new HashMap<>();for(String layerName:List.of("interactables","doors")) {
            MapLayer layer=map.getLayers().get(layerName);if(layer==null && layerName.equals("doors")) continue;if(layer==null) throw new IllegalArgumentException("Missing layer " + layerName);
            for(MapObject o:layer.getObjects()) {
                String id=o.getProperties().get("id",String.class);if(id==null) id=o.getName();
                if(!(o instanceof RectangleMapObject r) || id==null || objects.putIfAbsent(id,r.getRectangle())!=null) throw new IllegalArgumentException("Invalid/duplicate interaction " + id);
                SceneCatalog.ObjectDef def=result.object(id);Rectangle b=r.getRectangle();def.x=b.x;def.y=b.y;def.width=b.width;def.height=b.height;
                String type=o.getProperties().get("type",String.class);if(type!=null && !type.equals(def.type)) throw new IllegalArgumentException("Wrong interaction type " + id);
                String target=o.getProperties().get("targetRoom",String.class),spawn=o.getProperties().get("spawnId",String.class);
                if(target!=null) {RoomId.valueOf(target);def.target=target;def.spawn=spawn;catalog.scene(RoomId.valueOf(target)).spawn(spawn);}
            }
        }
        for(SceneCatalog.ObjectDef o:result.objects) if(!objects.containsKey(o.id)) throw new IllegalArgumentException("TMX missing interaction " + o.id);
        MapLayer npcs=map.getLayers().get("npcs");
        if(npcs!=null) for(MapObject o:npcs.getObjects()) {
            catalog.npc(o.getName());SceneCatalog.Point point=new SceneCatalog.Point();
            if(o instanceof PointMapObject po){point.x=po.getPoint().x;point.y=po.getPoint().y;}else throw new IllegalArgumentException("NPC slot must be point " + o.getName());result.npcSlots.put(o.getName(),point);
        }
        for(String spawn:original.spawns.keySet()) if(!result.spawns.containsKey(spawn)) throw new IllegalArgumentException("TMX missing spawn " + spawn);
        catalog.validateGeometry(result);
        return result;
    }
    private static MapLayer layer(TiledMap map,String name) {MapLayer layer=map.getLayers().get(name);if(layer==null) throw new IllegalArgumentException("Missing layer " + name);return layer;}
    private boolean canStand(SceneCatalog.Scene s,float x,float y) {return catalog.canStand(s,x,y);}
    public boolean canStand(float x,float y) {return canStand(scene,x,y);}
    public void update(float delta,StorySession session,Player player) {
        time+=delta;updateVisuals(session);List<RoomInteractionSystem.Target> targets=new ArrayList<>();
        for(SceneCatalog.ObjectDef o:scene.objects) if(o.requiredEvent==null||session.state().reached(Event.valueOf(o.requiredEvent))) targets.add(RoomInteractionSystem.Target.logical(o.id,o.type.equals("DOOR")?RoomInteractionSystem.Kind.DOOR:RoomInteractionSystem.Kind.PROP,o.interactionBounds(),feet->(o.usableFrom==null||o.usableFrom.bounds().contains(feet))&&inRange(feet,o.interactionBounds(),o.range,player.facing())));
        for(String id:session.visibleNpcs()) if(!id.equals("nga") && (catalog.npc(id).requiredEvent==null||session.state().reached(Event.valueOf(catalog.npc(id).requiredEvent)))) {
            SceneCatalog.Point p=catalog.npcPosition(session.state(),id);Rectangle feet=new Rectangle(p.x,p.y,catalog.npc(id).footWidth,catalog.npc(id).footHeight);
            targets.add(RoomInteractionSystem.Target.logical(id,RoomInteractionSystem.Kind.NPC,feet,b->inRange(b,feet,42,player.facing())));
        }
        selector.replaceTargets(targets);selector.update(player.getBounds());
    }
    private boolean inRange(Rectangle player,Rectangle target,float range,String facing) {
        if(RoomInteractionSystem.distanceSquared(player,target)>range*range) return false;
        float dx=target.x+target.width/2-player.x-player.width/2,dy=target.y+target.height/2-player.y-player.height/2;
        return switch(facing) {case "up"->dy>=-8;case "down"->dy<=8;case "left"->dx<=8;default->dx>=-8;};
    }
    public String target() {return selector.getActiveTarget()==null?null:selector.getActiveTarget().getId();}
    public List<Wall> walls(StorySession session) {
        List<Wall> result=new ArrayList<>(walls);
        for(String id:session.visibleNpcs()) if(!id.equals("nga")) {SceneCatalog.Point p=catalog.npcPosition(session.state(),id);result.add(new Wall(p.x,p.y,catalog.npc(id).footWidth,catalog.npc(id).footHeight));}
        return result;
    }
    public void render(StorySession session,Player player,boolean interactionsEnabled) {
        viewport.setWorldSize(scene.followCamera?480:scene.width,scene.followCamera?360:scene.height);viewport.apply();if(scene.followCamera)camera.follow(player.getX()+player.getBounds().width/2,player.getY()+player.getSpriteHeight()/2,scene.width,scene.height);else{camera.position.set(scene.width/2,scene.height/2,0);camera.update();}shapes.setProjectionMatrix(camera.combined);batch.setProjectionMatrix(camera.combined);
        boolean cold=session.state().phenomenon()==Phenomenon.ACTIVE || (session.block()!=null && session.block().startsWith("C1-04-HAUNT"));
        shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.setColor(cold?new Color(.08f,.12f,.16f,1):new Color(.18f,.17f,.13f,1));shapes.rect(0,0,scene.width,scene.height);
        shapes.setColor(.09f,.09f,.10f,1);for(SceneCatalog.Box b:scene.collisions) shapes.rect(b.x,b.y,b.width,b.height);
        for(SceneCatalog.ObjectDef o:scene.objects) {shapes.setColor(o.type.equals("DOOR")?.28f:.30f,.28f,.22f,1);shapes.rect(o.x,o.y,o.width,o.height);}
        if(session.state().reached(Event.CHALK_MARK) && session.state().room()==RoomId.HALLWAY){shapes.setColor(Color.WHITE);shapes.rect(306,286,28,3);}
        shapes.end();
        if(renderer!=null){renderer.setView(camera);renderer.render();}
        batch.begin();Texture bg=textures.get(scene.backgrounds.getOrDefault(session.state().phenomenon().name(),scene.background));
        if(renderer==null && bg!=null){
            var region=scene.backgroundRegion;
            if(region==null)batch.draw(bg,0,0,scene.width,scene.height);
            else batch.draw(bg,0,0,scene.width,scene.height,region.x,region.y,region.width,region.height,false,false);
        }
        // Keep placeholders for missing items, without covering furniture painted into the map.
        if(renderer==null && bg!=null){
            batch.end();shapes.begin(ShapeRenderer.ShapeType.Filled);
            for(SceneCatalog.ObjectDef o:scene.objects)if(!o.inBackground && textures.get(o.variants.getOrDefault(propState(o.id,session.state()),o.asset))==null){shapes.setColor(o.type.equals("DOOR")?.28f:.30f,.28f,.22f,1);shapes.rect(o.x,o.y,o.width,o.height);}
            shapes.end();batch.begin();
        }
        record DepthDraw(float y,Runnable draw) {}
        List<DepthDraw> depthDraws=new ArrayList<>();
        for(SceneCatalog.ObjectDef o:scene.objects){Texture t=textures.get(o.variants.getOrDefault(propState(o.id,session.state()),o.asset));if(t!=null){if(o.depthSortY==null)batch.draw(t,o.x,o.y,o.width,o.height);else depthDraws.add(new DepthDraw(o.depthSortY,()->batch.draw(t,o.x,o.y,o.width,o.height)));}}
        List<String> actors=new ArrayList<>(session.visibleNpcs());actors.add("linh");for(String id:poses.keySet())if(!actors.contains(id))actors.add(id);actors.removeIf(id->poses.containsKey(id)&&poses.get(id).hidden());actors.sort(Comparator.comparingDouble(id->-actorPosition(id,session,player).y));
        for(String actor:actors) {
            depthDraws.add(new DepthDraw(actorPosition(actor,session,player).y,()->drawActor(actor,session,player)));
        }
        depthDraws.sort(Comparator.comparingDouble(draw->-draw.y()));
        for(DepthDraw draw:depthDraws)draw.draw().run();
        drawEffects(session);
        font.setColor(Color.LIGHT_GRAY);font.getData().setScale(.68f);
        String selected=target();
        if(selected!=null && interactionsEnabled && session.exploring() && selector.getActiveTarget().getKind()!=RoomInteractionSystem.Kind.NPC) {
            SceneCatalog.ObjectDef object=scene.objects.stream().filter(o->o.id.equals(selected)).findFirst().orElse(null);
            Rectangle b=object==null?selector.getActiveTarget().getBounds():object.bounds();
            String label=object==null?catalog.npc(selected).name:(object.name==null?object.id:object.name);
            if(selected.equals("guard.wall") && (!session.state().reached(Event.GUARD_DOOR_REVEALED)||session.state().phenomenon()!=Phenomenon.ACTIVE))label="Bức tường";
            GlyphLayout measured=new GlyphLayout(font,label);font.setColor(Color.YELLOW);
            font.draw(batch,label,Math.max(8,Math.min(b.x+(b.width-measured.width)/2,scene.width-8-measured.width)),Math.min(scene.height-60,b.y+b.height+18));
        }
        if(interactionsEnabled && session.exploring()) drawInteractionMark(session,player);
        batch.end();hudViewport.apply();batch.setProjectionMatrix(hudViewport.getCamera().combined);batch.begin();
        if(!session.overlay().equals("NONE")){font.setColor(session.overlay().equals("CLEAR_LINH")?Color.WHITE:Color.GRAY);font.draw(batch,session.overlay().equals("CLEAR_LINH")?"33. Linh":"Linh",260,390);}
        if(session.exploring()){font.setColor(Color.WHITE);font.getData().setScale(1);font.draw(batch,scene.name,16,scene.height-16);font.getData().setScale(.8f);font.draw(batch,session.objective(),16,scene.height-43);font.getData().setScale(.65f);font.draw(batch,"WASD / phím hướng · E tương tác · N ghi chú · Esc tạm dừng",16,24);}font.getData().setScale(1);batch.end();
    }
    private void drawInteractionMark(StorySession session,Player player) {
        RoomInteractionSystem.Target target=selector.getActiveTarget();
        if(target==null || interactionMarkAnimation==null) return;
        TextureRegion frame=interactionMarkAnimation.getKeyFrame(time,true);
        float height=INTERACTION_MARK_HEIGHT,width=height*frame.getRegionWidth()/frame.getRegionHeight();
        float centerX,bottom;
        if(target.getKind()==RoomInteractionSystem.Kind.NPC) {
            var npc=catalog.npc(target.getId());var position=actorPosition(target.getId(),session,player);
            centerX=position.x+npc.footWidth/2+npc.canvasWidth*npc.scale*(.5f-npc.anchorX);
            bottom=position.y+npc.canvasHeight*npc.scale*(1-npc.anchorY)+22;
        } else {
            var object=scene.object(target.getId());
            centerX=object.x+object.width/2;bottom=object.y+object.height+22;
        }
        // Keep the original frame aspect ratio and leave room for the target's name.
        camera.visibleBounds(visibleBounds);
        float x=MathUtils.clamp(centerX-width/2,visibleBounds.x+4,visibleBounds.x+visibleBounds.width-width-4);
        float y=MathUtils.clamp(bottom,visibleBounds.y+4,visibleBounds.y+visibleBounds.height-height-4);
        batch.setColor(Color.WHITE);batch.draw(frame,x,y,width,height);
    }
    private void updateVisuals(StorySession session){
        if(!Objects.equals(visualBlock,session.block())){poses.clear();poseTimes.clear();recording=false;effect=effectAsset=recordAsset=null;lastVisualLine=null;visualBlock=session.block();}
        String line=session.dialogue().isActive()?session.dialogue().getLine().getId():null;
        if(Objects.equals(line,lastVisualLine))return;lastVisualLine=line;
        StoryVisuals.Cue cue=visuals.cue(line);if(cue==null)return;
        if(cue.clear()){poses.clear();poseTimes.clear();recording=false;effect=effectAsset=null;}
        for(StoryVisuals.Actor actor:cue.actors()){poses.put(actor.id(),actor);poseTimes.put(actor.id(),time);}
        if(cue.effect()!=null){effect=cue.effect();effectAsset=cue.asset();effectTime=time;if(effect.equals("record")){recording=true;recordAsset=cue.asset();}if(effect.equals("chair")){chairShift=true;chairAsset=cue.asset();}}
        // Borrow optional art during update, never allocate textures in draw.
        if(cue.asset()!=null)texture(cue.asset());
        texture(scene.backgrounds.get(session.state().phenomenon().name()));
        for(SceneCatalog.ObjectDef o:scene.objects)texture(o.variants.get(propState(o.id,session.state())));
    }
    public static String propState(String id,GameState state){
        return switch(id){
            case "desk"->state.book()==BookLocation.FINAL_TABLE?"final":state.book()==BookLocation.TABLE_304?"normal":"empty";
            case "cabinet"->state.cabinetLocked()?"locked":"closed";
            case "crate"->state.has(Item.CASSETTE)?"open":"closed";
            case "player"->state.tapeLocation()==TapeLocation.PLAYER?"playing":state.batteryLocation()==BatteryLocation.PLAYER?"powered":"empty";
            case "evidence"->state.knows(Clue.SIGNED_ADDENDUM)?"final":"normal";
            default->"normal";
        };
    }
    public static String inspectionAsset(SceneCatalog.ObjectDef object,GameState state) {
        String variant=propState(object.id,state);
        if(object.id.equals("cabinet")&&!state.cabinetLocked()) variant="open";
        if(object.id.equals("player")) {
            if(state.batteryLocation()==BatteryLocation.PLAYER) variant=state.tapeLocation()==TapeLocation.PLAYER?"playing":"powered";
            else variant=state.tapeLocation()==TapeLocation.PLAYER||state.reached(Event.RECORDER_CHECKED)?"open":"empty";
        }
        if(object.id.equals("guard.wall")) variant=state.reached(Event.GUARD_DOOR_REVEALED)&&state.phenomenon()==Phenomenon.ACTIVE?"open":"closed";
        if(object.id.equals("gate")) variant=state.phenomenon()==Phenomenon.ACTIVE?"closed":"open";
        return object.inspectionVariants.getOrDefault(variant,object.inspection);
    }
    private SceneCatalog.Point anchor(String id,float footWidth){
        if(id.startsWith("npc:"))return scene.npcSlots.get(id.substring(4));
        SceneCatalog.ObjectDef o=scene.object(id);SceneCatalog.Point p=new SceneCatalog.Point();p.x=o.x+(o.width-footWidth)/2;p.y=o.y-12;return p;
    }
    public SceneCatalog.Point actorPosition(String id,StorySession session,Player player){
        SceneCatalog.Point p=new SceneCatalog.Point();if(id.equals("linh")){p.x=player.getX();p.y=player.getY();}else{SceneCatalog.Point slot=catalog.npcPosition(session.state(),id);p.x=slot.x;p.y=slot.y;}
        StoryVisuals.Actor a=poses.get(id);if(a==null)return p;float fw=id.equals("linh")?catalog.player().footWidth:catalog.npc(id).footWidth;
        if(a.anchor()!=null){SceneCatalog.Point start=anchor(a.anchor(),fw);p.x=start.x;p.y=start.y;}p.x+=a.dx();p.y+=a.dy();
        if(a.to()!=null){SceneCatalog.Point end=anchor(a.to(),fw);float t=Math.min(1,(time-poseTimes.get(id))/a.seconds());p.x=MathUtils.lerp(p.x,end.x,t);p.y=MathUtils.lerp(p.y,end.y,t);}
        return p;
    }
    private void drawActor(String id,StorySession session,Player player){
        boolean bookPose=id.equals("linh")&&!poses.containsKey(id)&&!player.isMoving()&&session.state().book()==BookLocation.CARRIED&&animations.containsKey("linh/pose.book");
        if(id.equals("linh")&&!poses.containsKey(id)&&player.hasSprite()&&!bookPose){player.render(batch);drawActorName("Linh",player.getX()+player.getBounds().width/2,player.getY()+player.getSpriteHeight()*(1-catalog.player().anchorY));return;}
        SceneCatalog.Point p=actorPosition(id,session,player);StoryVisuals.Actor a=poses.get(id);
        String state=a==null?(id.equals("linh")?bookPose?"pose.book":(player.isMoving()?"walk.":"idle.")+player.facing():"idle."+npcFacing(id,session,player)):a.pose();
        float width,height,fw,ax,ay;String name;if(id.equals("linh")){var n=catalog.player();width=n.canvasWidth*n.scale;height=n.canvasHeight*n.scale;fw=n.footWidth;ax=n.anchorX;ay=n.anchorY;name="Linh";}else{var n=catalog.npc(id);width=n.canvasWidth*n.scale;height=n.canvasHeight*n.scale;fw=n.footWidth;ax=n.anchorX;ay=n.anchorY;name=n.name;}
        float alpha=a==null?(id.equals("nga")?.5f:1):a.alpha();Animation<TextureRegion> anim=animations.get(id+"/"+state);
        String facing=state.substring(state.lastIndexOf('.')+1);if(!Set.of("up","down","left","right").contains(facing))facing="down";
        if(anim==null)anim=animations.get(id+"/idle."+facing);if(anim==null)anim=animations.get(id+"/idle.down");
        if(anim!=null){batch.setColor(1,1,1,alpha);batch.draw(anim.getKeyFrame(a==null?time:time-poseTimes.get(id),!state.startsWith("pose.")),p.x+fw/2-width*ax,p.y-height*ay,width,height);batch.setColor(Color.WHITE);drawActorName(name,p.x+fw/2+width*(.5f-ax),p.y+height*(1-ay));}
        else{Color color=id.equals("linh")?new Color(.38f,.72f,.83f,alpha):id.equals("nga")?new Color(.6f,.7f,.8f,alpha):new Color(.74f,.60f,.38f,alpha);if(state.equals("pose.seated"))height*=.65f;drawBody(p.x+fw/2-width*ax,p.y-height*ay,width,height,name,color,facing);}
    }
    private String npcFacing(String id,StorySession session,Player player){
        if(!session.dialogue().isActive()||!id.equals(session.dialogue().getLine().getSpeakerId()))return "down";
        SceneCatalog.Point p=catalog.npcPosition(session.state(),id);float dx=player.getX()-p.x,dy=player.getY()-p.y;return Math.abs(dx)>Math.abs(dy)?dx>0?"right":"left":dy>0?"up":"down";
    }
    private void drawEffects(StorySession session){
        float elapsed=time-effectTime;
        if(recording){Texture icon=textures.get(recordAsset);if(icon!=null)batch.draw(icon,420,scene.height-105,icon.getWidth(),icon.getHeight());font.setColor(Color.SALMON);font.getData().setScale(.8f);font.draw(batch,content.text("UI-RECORD-ICON"),420,scene.height-70);font.getData().setScale(1);}
        if("memory".equals(effect)){SceneCatalog.ObjectDef door=scene.object("door.hall"),desk=scene.object("lastdesk");Texture art=textures.get(effectAsset);for(int i=0;i<3;i++){float t=Math.min(1,Math.max(0,(elapsed-i*.2f)/2));float x=MathUtils.lerp(desk.x-i*42,door.x+i*12,t),y=MathUtils.lerp(desk.y-28,door.y+32,t);if(art!=null){batch.setColor(1,1,1,.25f*(1-t));batch.draw(art,x,y,art.getWidth(),art.getHeight());batch.setColor(Color.WHITE);}else drawBody(x,y,24,40,"",new Color(.6f,.7f,.8f,.25f*(1-t)),"down");}}
        if("phone".equals(effect)){Texture art=textures.get(effectAsset);if(art!=null)batch.draw(art,230,175,180,245);}
        if("camera".equals(effect)&&elapsed<.45f){Texture art=textures.get(effectAsset);if(art!=null)batch.draw(art,470,scene.height-120,art.getWidth(),art.getHeight());font.setColor(Color.WHITE);font.getData().setScale(.8f);font.draw(batch,"Đã chụp",470,scene.height-95);font.getData().setScale(1);}
        if(scene.id.equals("ROOM_304")&&(chairShift||session.state().knows(Clue.ALTERED_REGISTER))){var d=scene.object("lastdesk");batch.end();shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.setColor(.45f,.4f,.3f,1);Texture chair=textures.get(chairAsset);if(chair==null&&!scene.object("lastdesk").inBackground)shapes.rect(d.x+16,d.y-30,22,9);shapes.end();batch.begin();if(chair!=null)batch.draw(chair,d.x+16,d.y-30,chair.getWidth(),chair.getHeight());}
    }
    private void drawBody(float x,float y,float width,float height,String name,Color color,String facing) {
        // Geometry only: no generated substitute game artwork.
        batch.end();Gdx.gl.glEnable(GL20.GL_BLEND);Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.setColor(color);shapes.rect(x,y,width,height);
        shapes.setColor(1,1,1,color.a);float cx=x+width/2,cy=y+height/2;switch(facing){case "up"->shapes.rect(cx-2,y+height-5,4,4);case "down"->shapes.rect(cx-2,y+1,4,4);case "left"->shapes.rect(x-6,cy,4,4);default->shapes.rect(x+width-10,cy,4,4);}shapes.end();batch.begin();
        drawActorName(name,x+width/2,y+height);
    }
    private void drawActorName(String name,float centerX,float topY) {
        if(name.isEmpty())return;
        font.setColor(Color.WHITE);font.getData().setScale(.7f);
        GlyphLayout measured=new GlyphLayout(font,name);
        font.draw(batch,name,centerX-measured.width/2,topY+14);
        font.getData().setScale(1);
    }
    public void resize(int width,int height){if(width>0&&height>0){viewport.update(width,height,true);hudViewport.update(width,height,true);}}
    @Override public void dispose(){if(renderer!=null)renderer.dispose();for(TiledMap cached:mapCache.values())cached.dispose();mapCache.clear();batch.dispose();shapes.dispose();}
}
