package com.room304.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.room304.*;
import com.room304.assets.*;
import com.room304.screen.*;
import com.room304.story.*;
import com.room304.world.*;
import java.lang.reflect.Proxy;
import java.nio.file.*;
import java.util.*;
import static com.room304.story.GameState.*;

/** Real OpenGL/input/UI checks. Walking uses reachable cells and the runtime collision controller. */
public final class StoryDesktopChecks {
    private static Throwable failure;
    private static GameScreen screen;
    private static Input original,scripted;
    private static Set<Integer> keys=Set.of();
    private static int frames,interactions;
    private static final Set<String> visualChecks=new HashSet<>();
    private static final Set<RoomId> cameraScenes=new HashSet<>();
    private static int importedCharacterCount;
    private static GameAssets assets;private static StoryContent content;private static SceneCatalog world;private static SaveStore save;
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg=new Lwjgl3ApplicationConfiguration();cfg.setTitle("Room304 story runtime checks");cfg.setWindowedMode(960,540);cfg.setInitialVisible(false);
        new Lwjgl3Application(new ApplicationAdapter(){@Override public void create(){Gdx.app.postRunnable(()->{try{checkAll();}catch(Throwable e){failure=e;}finally{Gdx.app.exit();}});}},cfg);
        if(failure!=null)throw new AssertionError("Story desktop checks failed",failure);
        System.out.println("Story desktop checks passed: A/B/C, "+interactions+" E interactions reached by walking, "+frames+" rendered frames, menu/replay, pause, notes, disk save/load, 16:9 and 4:3.");
    }
    private static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    private static void frame(int...held){keys=new HashSet<>();for(int key:held)keys.add(key);screen.render(1f/60);frames++;observeVisuals();}
    private static void press(int key){frame(key);frame();}
    private static void tick(){screen.render(.25f);frames++;observeVisuals();}
    private static void observeVisuals(){
        if(cameraScenes.add(screen.session().state().room())) {
            FollowCamera camera=camera();var scene=screen.world().scene();
            check(camera.viewportWidth==320&&camera.viewportHeight==240&&camera.zoom==1,"Same camera scale in every story map");
            check(camera.position.x>=160&&camera.position.x<=scene.width-160&&camera.position.y>=120&&camera.position.y<=scene.height-120,"Camera remains inside scene bounds");
        }
        if(!screen.session().dialogue().isActive())return;String id=screen.session().dialogue().getLine().getId();
        if("C1-04-HAUNT-E08".equals(id)){check(screen.world().pose("nga").equals("pose.seated"),"Haunt seated ghost binding");visualChecks.add("haunt");}
        if("C4-05-MAIN-D06".equals(id)){check(screen.world().recording(),"Recording icon persists through tape subtitles");visualChecks.add("record");}
        if("C4-03-MAIN-D02".equals(id)) {
            check("inspection.player_open".equals(screen.dialogueUI().inspectionAsset()),"Unpowered tape inspection must show the open tray");
            if(visualChecks.add("inspection-unpowered"))snapshot("inspection-player-open-16x9");
        }
        if("C4-05-MAIN-D06".equals(id)) {
            check("inspection.player_playing".equals(screen.dialogueUI().inspectionAsset()),"Powered tape playback updates close-up during the same interaction");
            check(!screen.session().state().knows(Clue.CASSETTE_CONTENT),"Artwork must not grant the heard clue early");
            if(visualChecks.add("inspection-playing"))snapshot("inspection-player-playing-16x9");
        }
        if("A-01-MAIN-E03".equals(id)){check(screen.world().pose("nga").equals("walk.down"),"Memory departure pose");visualChecks.add("memory");}
        if("C-02-MAIN-E09".equals(id)){check(screen.world().pose("linh").equals("pose.seated"),"C seated pose");var p=screen.world().actorPosition("linh",screen.session(),screen.player());check(Math.abs(p.x-screen.player().getX())>1||Math.abs(p.y-screen.player().getY())>1,"C visual placement must not mutate Player physics");visualChecks.add("seated");}
    }
    private static void drain(){for(int i=0;i<5000;i++){if(!screen.session().dialogue().isActive()){for(int k=0;k<25;k++)frame();return;}if(!screen.session().dialogue().getLine().getChoices().isEmpty()){for(int k=0;k<3;k++)tick();screen.dialogueUI().continueText();frame();return;}tick();press(Input.Keys.ENTER);}throw new AssertionError("Dialogue stalled "+screen.session().block());}
    private static void choice(String id){drain();var options=screen.session().dialogue().getLine().getChoices();int index=-1;for(int i=0;i<options.size();i++)if(options.get(i).getId().equals(id))index=i;check(index>=0,"Choice missing "+id+" / "+screen.session().block());while(!id.equals(screen.dialogueUI().selectedChoiceId()))press(Input.Keys.DOWN);press(Input.Keys.ENTER);drain();}
    private static void walk(String id) {
        Player player=screen.player();SceneCatalog.Scene scene=screen.world().scene();
        float grid=scene.width<640?2:10;
        RoomInteractionSystem selector;
        try{var field=StoryWorldView.class.getDeclaredField("selector");field.setAccessible(true);selector=(RoomInteractionSystem)field.get(screen.world());}
        catch(ReflectiveOperationException e){throw new AssertionError("Runtime interaction selector unavailable",e);}
        float sx=player.getX(),sy=player.getY();List<Wall> walls=screen.world().walls(screen.session());
        record Cell(int x,int y){}ArrayDeque<Cell> queue=new ArrayDeque<>();Map<Cell,Cell> previous=new HashMap<>();Cell start=new Cell(0,0),goal=null;queue.add(start);previous.put(start,null);
        String originalFacing=player.facing(),goalFacing=null;
        screen.world().update(0,screen.session(),player);
        try {
            while(!queue.isEmpty()) {
                Cell c=queue.remove();float x=sx+c.x*grid,y=sy+c.y*grid;Rectangle feet=new Rectangle(x,y,world.player().footWidth,world.player().footHeight);
                if(screen.world().canStand(x,y)&&walls.stream().noneMatch(w->w.getBounds().overlaps(feet))) {
                    for(String facing:List.of("up","down","left","right")) {
                        player.restoreFacing(facing);selector.update(feet);
                        var selected=selector.getActiveTarget();
                        if(selected!=null&&id.equals(selected.getId())) {
                            float step=world.player().speed/60+1;
                            float stepX=x+switch(facing){case "left"->-step;case "right"->step;default->0;};
                            float stepY=y+switch(facing){case "down"->-step;case "up"->step;default->0;};
                            Rectangle stepped=new Rectangle(stepX,stepY,feet.width,feet.height);
                            if(!screen.world().canStand(stepX,stepY)||walls.stream().anyMatch(w->w.getBounds().overlaps(stepped)))continue;
                            selector.update(stepped);selected=selector.getActiveTarget();
                            if(selected!=null&&id.equals(selected.getId())){goal=c;goalFacing=facing;break;}
                        }
                    }
                    if(goal!=null)break;
                }
                for(int[] offset:new int[][]{{0,1},{1,0},{0,-1},{-1,0}}){Cell n=new Cell(c.x+offset[0],c.y+offset[1]);if(previous.containsKey(n))continue;float nx=sx+n.x*grid,ny=sy+n.y*grid;Rectangle nf=new Rectangle(nx,ny,feet.width,feet.height);if(!screen.world().canStand(nx,ny)||walls.stream().anyMatch(w->w.getBounds().overlaps(nf)))continue;previous.put(n,c);queue.add(n);}
            }
        }finally{player.restoreFacing(originalFacing);screen.world().update(0,screen.session(),player);}
        check(goal!=null,"No reachable interaction cell "+scene.id+"/"+id);List<Cell> path=new ArrayList<>();for(Cell c=goal;previous.get(c)!=null;c=previous.get(c))path.add(c);Collections.reverse(path);
        for(Cell cell:path){float x=sx+cell.x*grid,y=sy+cell.y*grid;for(int i=0;i<12;i++){float dx=x-player.getX(),dy=y-player.getY();if(Math.abs(dx)<1&&Math.abs(dy)<1)break;int key=Math.abs(dx)>1?(dx>0?Input.Keys.D:Input.Keys.A):(dy>0?Input.Keys.W:Input.Keys.S);frame(key);}check(Math.abs(player.getX()-x)<1&&Math.abs(player.getY()-y)<1,"Walking blocked "+id);}
        int facingKey=switch(goalFacing){case "up"->Input.Keys.W;case "down"->Input.Keys.S;case "left"->Input.Keys.A;default->Input.Keys.D;};
        frame(facingKey);frame();check(id.equals(screen.world().target()),"Wrong E target "+id+"; intended facing="+goalFacing+", actual facing="+player.facing()+", actual feet="+player.getBounds()+", planned goal=("+(sx+goal.x*grid)+", "+(sy+goal.y*grid)+"), selected="+screen.world().target());
    }
    private static void interact(String id){check(screen.session().exploring(),"Interaction outside exploration "+id);walk(id);press(Input.Keys.E);interactions++;drain();}
    private static void begin(boolean skip,SaveStore.Loaded saved){if(screen!=null)screen.dispose();screen=new GameScreen(assets,content,world,save,new AudioSettings(),skip,saved,()->{},()->{},e->{});screen.resize(960,540);screen.show();drain();}
    private static void decision(){interact("desk");interact("door.hall");interact("stairs");interact("duc");choice("C2-02-CHOICE03");interact("guard.wall");interact("cabinet");}
    private static void investigate(){decision();choice("C3-04-CHOICE02");choice("C3-04-CHOICE03");}
    private static void guard(){interact("door.hall");interact("guard.wall");}
    private static void room(){interact("door.hall");interact("door.304");}
    private static void ready(){investigate();interact("photo");guard();interact("records");room();interact("crate");interact("player");guard();interact("nam");choice("CHO-NAM-BAT");room();walk("player");press(Input.Keys.E);interactions++;String tapeLine=screen.session().dialogue().getLine().getId();press(Input.Keys.ESCAPE);for(int i=0;i<12;i++)tick();check(tapeLine.equals(screen.session().dialogue().getLine().getId())&&!screen.session().state().knows(Clue.CASSETTE_CONTENT),"Pause must freeze tape and not commit heard");press(Input.Keys.ESCAPE);drain();interact("door.hall");interact("door.hung");interact("hung");choice("C5-01-CHOICE03");choice("C5-01-CHOICE04");room();interact("desk");}
    private static void snapshot(String name){int width=Gdx.graphics.getBackBufferWidth(),height=Gdx.graphics.getBackBufferHeight();byte[] pixels=com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(0,0,width,height,true);Pixmap pix=new Pixmap(width,height,Pixmap.Format.RGBA8888);pix.getPixels().put(pixels);try{PixmapIO.writePNG(Gdx.files.local("../lwjgl3/build/story-"+name+".png"),pix);}finally{pix.dispose();}}
    private static FollowCamera camera() {
        try{var field=StoryWorldView.class.getDeclaredField("camera");field.setAccessible(true);return (FollowCamera)field.get(screen.world());}
        catch(ReflectiveOperationException e){throw new AssertionError("Runtime camera unavailable",e);}
    }
    private static BitSet headerPixels() {
        int width=Gdx.graphics.getBackBufferWidth(),height=Gdx.graphics.getBackBufferHeight();
        float scale=Math.min(width/640f,height/480f);int left=(int)((width-640*scale)/2);
        byte[] pixels=com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(0,0,width,height,false);BitSet mask=new BitSet();
        for(int y=(int)(440*scale);y<(int)(470*scale);y++)for(int x=left+(int)(16*scale);x<left+(int)(260*scale);x++) {
            int index=(y*width+x)*4;
            if(Byte.toUnsignedInt(pixels[index])>245&&Byte.toUnsignedInt(pixels[index+1])>245&&Byte.toUnsignedInt(pixels[index+2])>245)mask.set(y*width+x);
        }
        return mask;
    }
    private static void checkCameraNavigation() {
        FollowCamera camera=camera();check(camera.position.x==160&&camera.position.y==120,"Room304 is shown in full");
        float playerRatio=screen.player().getSpriteHeight()/camera.viewportHeight;
        interact("door.hall");camera=camera();check(screen.session().state().room()==RoomId.HALLWAY,"Enter a large map through its real door");
        check(Math.abs(camera.position.x-screen.player().getX()-screen.player().getBounds().width/2)<.01f,"Camera snaps to the player on map entry");
        check(screen.player().getSpriteHeight()/camera.viewportHeight==playerRatio,"Player has the same screen size as in Room304");
        float center=camera.position.x;BitSet header=headerPixels();check(!header.isEmpty(),"HUD header is visible");snapshot("camera-hallway-center");
        walk("door.hung");check(camera.position.x<center,"Camera follows the player to the left");
        for(int i=0;i<100;i++)frame(Input.Keys.A);frame();check(camera.position.x==160,"Camera stops at the left edge");
        check(header.equals(headerPixels()),"HUD title stays at the same screen pixels while the world scrolls");snapshot("camera-hallway-left");
        walk("guard.wall");check(camera.position.x==480,"Camera follows right and stops at the right edge");
        for(int i=0;i<140;i++)frame(Input.Keys.S);frame();check(camera.position.y==120,"Camera follows down and stops at the bottom edge");
        check(header.equals(headerPixels()),"HUD title remains fixed after scrolling in both axes");snapshot("camera-hallway-right-bottom");
        float x=camera.position.x,y=camera.position.y;
        press(Input.Keys.ESCAPE);frame(Input.Keys.D);check(camera.position.x==x&&camera.position.y==y,"Pause freezes player and camera");press(Input.Keys.ESCAPE);frame();
        Gdx.graphics.setWindowedMode(800,600);Gdx.input=scripted;screen.resize(800,600);frame();
        check(camera.viewportWidth==320&&camera.viewportHeight==240&&camera.position.x==x&&camera.position.y==y,"Resize preserves zoom and camera tracking");snapshot("camera-hallway-4x3");
        Gdx.graphics.setWindowedMode(960,540);Gdx.input=scripted;screen.resize(960,540);frame();
        interact("door.304");check(camera.position.x==160&&camera.position.y==120,"Returning to Room304 resets the camera without carrying the large-map pan");
        System.out.println("Camera navigation passed: fixed player size, follow X/Y, edge clamps, stable HUD pixels, pause/resize and real map transitions.");
    }
    private static byte[] cueFrame(boolean visible) {
        screen.world().render(screen.session(),screen.player(),visible);
        return com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight(),false);
    }
    private static int newCuePixels(byte[] before,byte[] after,boolean yellow) {
        int count=0;
        for(int i=0;i<before.length;i+=4) {
            int r=Byte.toUnsignedInt(after[i]),g=Byte.toUnsignedInt(after[i+1]),b=Byte.toUnsignedInt(after[i+2]);
            if(Math.abs(r-Byte.toUnsignedInt(before[i]))+Math.abs(g-Byte.toUnsignedInt(before[i+1]))+Math.abs(b-Byte.toUnsignedInt(before[i+2]))<20)continue;
            if(yellow?r>160&&g>120&&r>b*1.8f&&g>b*1.5f:r>70&&r>g*1.7f&&r>b*1.7f)count++;
        }
        return count;
    }
    private static void checkInteractionCues() {
        check(screen.world().target()==null,"No cue target at the entry spawn");
        check(Arrays.equals(cueFrame(false),cueFrame(true)),"No marker or glow away from interactables");
        var animation=assets.animation("ui.interaction");
        check(animation!=null&&animation.getKeyFrames().length==4,"The imported mark is animated, not rendered as one whole sheet");
        for(var frame:animation.getKeyFrames())check(frame.getRegionWidth()==8&&frame.getRegionHeight()==25,"Interaction mark frame grid");
        walk("window");byte[] plain=cueFrame(false),visible=cueFrame(true);
        check(newCuePixels(plain,visible,true)>30,"A nearby object has a real yellow glow in the framebuffer");
        check(newCuePixels(plain,visible,false)>5,"A nearby object displays the red interaction asset");
        snapshot("cue-window-16x9");
        press(Input.Keys.E);interactions++;
        check(Arrays.equals(cueFrame(false),cueFrame(true)),"Object cues disappear while its dialogue is open");drain();
        walk("desk");plain=cueFrame(false);visible=cueFrame(true);
        check(newCuePixels(plain,visible,true)>30,"The teacher desk uses its shaped outline");snapshot("cue-desk-16x9");
        walk("trang");plain=cueFrame(false);visible=cueFrame(true);
        check(newCuePixels(plain,visible,false)>5,"A nearby NPC has the interaction mark above the sprite");
        check(newCuePixels(plain,visible,true)==0,"NPCs must not inherit the previous object's yellow outline");snapshot("cue-npc-16x9");
        Gdx.graphics.setWindowedMode(800,600);Gdx.input=scripted;screen.resize(800,600);frame();snapshot("cue-npc-4x3");
        Gdx.graphics.setWindowedMode(960,540);Gdx.input=scripted;screen.resize(960,540);frame();
        System.out.println("Interaction cue checks passed: real 4-frame asset, near/far, object yellow glow, shaped desk, NPC mark without glow, dialogue cleanup, 16:9/4:3.");
    }
    private static void checkWallFurniture() {
        var scene=screen.world().scene();var walls=screen.world().walls(screen.session());
        check(!screen.world().canStand(285,190),"Cannot stand above the lower cabinet inside its upper half");
        check(!screen.world().canStand(234,183),"Cannot stand on the window desk or behind it");
        check(!screen.world().canStand(110,184),"Cannot stand between the teacher desk and the back wall");
        check(!screen.world().canStand(255,190),"Cannot enter the painted back wall");
        for(String id:List.of("desk","lastdesk","cabinet")) {
            var object=scene.object(id);float x=object.x+(object.width-world.player().footWidth)/2;
            float y=(id.equals("desk")?160:object.y)-world.player().footHeight-2;
            check(screen.world().canStand(x,y),"Valid front approach for "+id);
            Player probe=new Player(assets,x,y,world.player());
            try {
                for(int i=0;i<40;i++)probe.update(1f/60,0,1,false,walls,null,List.of(),scene.width,scene.height);
                check(probe.getBounds().y+probe.getBounds().height<=object.y,"Holding W must stop before "+id);
                probe.update(.25f,0,1,false,walls,null,List.of(),scene.width,scene.height);
                check(walls.stream().noneMatch(w->w.getBounds().overlaps(probe.getBounds())),"A slow frame cannot tunnel onto "+id);
                for(int i=0;i<80;i++) {
                    probe.update(1f/60,1,1,false,walls,null,List.of(),scene.width,scene.height);
                    check(walls.stream().noneMatch(w->w.getBounds().overlaps(probe.getBounds())),"Diagonal input cannot enter wall furniture "+id);
                }
            }finally{probe.dispose();}
        }
        walk("lastdesk");snapshot("collision-window-desk-front");
        walk("cabinet.upper");snapshot("collision-cabinet-upper-side");press(Input.Keys.E);interactions++;drain();
        walk("cabinet");snapshot("collision-cabinet-front");
        walk("window");snapshot("collision-window-approach");
        System.out.println("Wall furniture checks passed: full cabinet/desk surfaces, no wall gaps, held W/diagonals/slow frames, reachable lower/upper cabinet and window.");
    }
    private static void checkInspectionUi() {
        walk("window");frame();
        int[] before=roomPixel(330,190);
        float x=screen.player().getX(),y=screen.player().getY();
        String progress=ProgressSnapshot.encode(screen.session().state());
        frame(Input.Keys.E,Input.Keys.D);interactions++;
        check("inspection.window".equals(screen.dialogueUI().inspectionAsset()),"E shows the selected close-up immediately");
        String first=screen.session().dialogue().getLine().getText();
        for(int i=0;i<8;i++)frame(Input.Keys.E,Input.Keys.D);
        check(screen.player().getX()==x&&screen.player().getY()==y,"Inspection blocks movement");
        check(first.equals(screen.session().dialogue().getLine().getText()),"Held E must not skip inspected object text");
        check(progress.equals(ProgressSnapshot.encode(screen.session().state())),"Viewing a window must not grant progress");
        int[] dark=roomPixel(330,190);
        check(dark[0]+dark[1]+dark[2]<(before[0]+before[1]+before[2])*.5f,"Inspection darkens the world behind the artwork");
        screen.dialogueUI().continueText();frame();snapshot("inspection-window-16x9");
        var art=(com.badlogic.gdx.scenes.scene2d.ui.Image)screen.dialogueUI().getStage().getRoot().findActor("inspection-art");
        check(art.isVisible()&&art.getY()>170&&art.getTop()<420,"Close-up fits above the dialogue text");
        int loaded=assets.loadedCount();
        press(Input.Keys.ESCAPE);frame(Input.Keys.D);check(screen.isPaused(),"Pause remains available over inspection");
        press(Input.Keys.ESCAPE);frame();check("inspection.window".equals(screen.dialogueUI().inspectionAsset()),"Resume preserves selected object");
        Gdx.graphics.setWindowedMode(800,600);Gdx.input=scripted;screen.resize(800,600);frame();snapshot("inspection-window-4x3");
        check(art.isVisible(),"Resize preserves close-up");
        Gdx.graphics.setWindowedMode(960,540);Gdx.input=scripted;screen.resize(960,540);frame();
        drain();check(screen.dialogueUI().inspectionAsset()==null,"Finishing interaction removes close-up");
        press(Input.Keys.E);interactions++;
        check("inspection.window".equals(screen.dialogueUI().inspectionAsset()),"Repeated E reopens the same object");
        check(assets.loadedCount()==loaded,"Repeated inspection reuses managed textures");drain();
        press(Input.Keys.N);check(screen.dialogueUI().inspectionAsset()==null,"Notes must not retain the previous object");press(Input.Keys.N);
        walk("cabinet");press(Input.Keys.E);interactions++;
        check("inspection.cabinet_locked".equals(screen.dialogueUI().inspectionAsset()),"Locked cabinet uses its close-up variant");
        screen.dialogueUI().continueText();frame();snapshot("inspection-cabinet-16x9");drain();
        walk("player");press(Input.Keys.E);interactions++;
        check("inspection.player".equals(screen.dialogueUI().inspectionAsset()),"Cassette player uses full-size close-up");
        screen.dialogueUI().continueText();frame();snapshot("inspection-player-16x9");drain();
        walk("trang");press(Input.Keys.E);interactions++;
        check(screen.dialogueUI().inspectionAsset()==null,"NPC dialogue must not reuse object close-up");drain();
        System.out.println("Inspection UI passed: E/held keys, real dim pixels, movement lock, pause/resume, resize 16:9/4:3, repeat/cache, notes and NPC cleanup.");
    }
    private static int[] roomPixel(float worldX,float worldY) {
        int width=Gdx.graphics.getBackBufferWidth(),height=Gdx.graphics.getBackBufferHeight();
        float scale=Math.min(width/640f,height/480f);
        int viewportWidth=Math.round(640*scale),viewportHeight=Math.round(480*scale);
        int x=(width-viewportWidth)/2+(int)(worldX*viewportWidth/640),y=(height-viewportHeight)/2+(int)(worldY*viewportHeight/480);
        byte[] pixel=com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(x,y,1,1,false);
        return new int[]{Byte.toUnsignedInt(pixel[0]),Byte.toUnsignedInt(pixel[1]),Byte.toUnsignedInt(pixel[2])};
    }
    private static void checkImportedIdleAssets() {
        int loaded=0;
        for(var expected:Map.of("linh",6,"trang",6,"duc",6,"nam",1,"hung",1).entrySet()) {
            String actor=expected.getKey(),id="char."+actor+".idle.down";
            AssetCatalog.Entry entry=assets.catalog().entries().stream().filter(e->e.id().equals(id)).findFirst().orElse(null);
            if(entry==null)continue;
            var animation=assets.animation(id);
            if(!Gdx.files.internal(entry.path()).exists()) {
                check(!entry.required()&&animation==null,"Missing optional idle must use fallback: "+id);
                continue;
            }
            int count=expected.getValue();
            check(entry.frameWidth()==32&&entry.frameHeight()==32&&entry.frameCount()==count,"Imported idle metadata: "+id);
            check("Nearest".equals(entry.filter()),"Imported idle manifest filter: "+id);
            check(animation!=null&&animation.getKeyFrames().length==count,"Imported idle loads real frames: "+id);
            Texture sheet=assets.texture(id);
            check(sheet.getWidth()==32*count&&sheet.getHeight()==32,"Imported idle is one exact row: "+id);
            check(sheet.getMinFilter()==Texture.TextureFilter.Nearest&&sheet.getMagFilter()==Texture.TextureFilter.Nearest,"Imported idle GPU filter: "+id);
            for(int i=0;i<count;i++) {
                var region=animation.getKeyFrames()[i];
                check(region.getTexture()==sheet&&region.getRegionWidth()==32&&region.getRegionHeight()==32&&region.getRegionX()==i*32&&region.getRegionY()==0,"Imported idle frame grid: "+id+"/"+i);
            }
            String bound=actor.equals("linh")?world.player().animations.get("idle.down"):world.npc(actor).animations.get("idle.down");
            check(id.equals(bound),"Imported idle bound in actual story profile: "+id);
            loaded++;
        }
        importedCharacterCount=loaded;
        System.out.println("Imported idle checks passed: "+loaded+" present optional sheets, actual story bindings, exact frame grid, real textures and Nearest.");
    }
    private static void snapshotImportedRoom() {
        String background=world.scene(RoomId.ROOM_304).background;
        if(importedCharacterCount!=5||!Gdx.files.internal(assets.catalog().path(background)).exists())return;
        Texture roomTexture=assets.texture(background);
        check(roomTexture.getWidth()==640&&roomTexture.getHeight()==480,"Room304 background must be exported at native 640x480");
        check(screen.player().getSpriteHeight()==32&&world.scene(RoomId.ROOM_304).object("desk").height<screen.player().getSpriteHeight(),"The teacher desk must be shorter than the unchanged player canvas");
        check(roomTexture.getMinFilter()==Texture.TextureFilter.Nearest&&roomTexture.getMagFilter()==Texture.TextureFilter.Nearest,"Room304 pixel background must use Nearest");
        check(screen.session().state().room()==RoomId.ROOM_304&&screen.session().visibleNpcs().containsAll(List.of("trang","duc","nam","hung")),"Five imported characters are present in final room");
        GameState copied=ProgressSnapshot.decode(ProgressSnapshot.encode(screen.session().state()),"imported-room-preview");
        var walls=screen.world().walls(screen.session());
        float x=screen.player().getX(),y=screen.player().getY();
        Rectangle feet=new Rectangle(x,y,world.player().footWidth,world.player().footHeight);
        if(!screen.world().canStand(x,y)||walls.stream().anyMatch(w->w.getBounds().overlaps(feet))) {
            boolean found=false;
            for(int row=40;row<440&&!found;row+=10)for(int column=40;column<600;column+=10) {
                feet.setPosition(column,row);
                if(screen.world().canStand(column,row)&&walls.stream().noneMatch(w->w.getBounds().overlaps(feet))){x=column;y=row;found=true;break;}
            }
            check(found,"Unblocked position for imported room preview");
        }
        // A cloned in-memory save omits the active choice UI without changing the live route or disk save.
        GameScreen preview=new GameScreen(assets,content,world,save,new AudioSettings(),true,new SaveStore.Loaded(copied,x,y,"down"),()->{},()->{},e->{});
        try {
            keys=Set.of();preview.resize(960,540);preview.show();preview.render(0);
            check(preview.session().exploring(),"Imported room preview has no dialogue overlay");
            check(copied.phenomenon()==Phenomenon.ACTIVE,"Imported room preview starts in active phenomenon");
            String progress=ProgressSnapshot.encode(copied);
            int[] neutral=roomPixel(330,190);
            snapshot("room304-neutral-16x9");
            preview.render(.2f);preview.render(.2f);
            int[] cold=roomPixel(330,190);
            check(cold[0]<neutral[0]&&cold[1]<=neutral[1]+1&&Math.abs(cold[2]-neutral[2])<=1,"Opaque room background cold light: neutral="+Arrays.toString(neutral)+", cold="+Arrays.toString(cold));
            check(progress.equals(ProgressSnapshot.encode(copied)),"Cold light preview must preserve copied story progress");
            snapshot("room304-five-characters-16x9");
            System.out.println("Opaque PNG cold light passed at floor (330,190): "+Arrays.toString(neutral)+" -> "+Arrays.toString(cold)+" after 0.4 s.");
        }finally{preview.dispose();screen.show();frame();}
    }
    private static void checkTmx(Path folder) throws Exception {
        SceneCatalog.Scene original=world.scene(RoomId.ROOM_304);StringBuilder xml=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><map version=\"1.10\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\"20\" height=\"15\" tilewidth=\"32\" tileheight=\"32\" infinite=\"0\">");
        xml.append("<objectgroup name=\"collisions\">");for(var b:original.collisions)xml.append("<object x=\"").append(b.x).append("\" y=\"").append(480-b.y-b.height).append("\" width=\"").append(b.width).append("\" height=\"").append(b.height).append("\"/>");xml.append("</objectgroup><objectgroup name=\"spawns\">");
        for(var spawn:original.spawns.entrySet())xml.append("<object name=\"").append(spawn.getKey()).append("\" x=\"").append(spawn.getValue().x).append("\" y=\"").append(480-spawn.getValue().y).append("\"><point/></object>");xml.append("</objectgroup><objectgroup name=\"interactables\">");
        for(var o:original.objects)xml.append("<object name=\"").append(o.id).append("\" x=\"").append(o.x).append("\" y=\"").append(480-o.y-o.height).append("\" width=\"").append(o.width).append("\" height=\"").append(o.height).append("\"/>");xml.append("</objectgroup></map>");
        Path tmx=folder.resolve("geometry.tmx");Files.writeString(tmx,xml.toString());var map=new com.badlogic.gdx.maps.tiled.TmxMapLoader().load(tmx.toAbsolutePath().toString());
        var method=StoryWorldView.class.getDeclaredMethod("importGeometry",SceneCatalog.Scene.class,com.badlogic.gdx.maps.tiled.TiledMap.class);method.setAccessible(true);
        try {
            SceneCatalog.Scene imported=(SceneCatalog.Scene)method.invoke(screen.world(),original,map);check(imported.width==640&&imported.height==480,"TMX pixel scale");check(imported.spawn("entry").y==original.spawn("entry").y,"TMX Y coordinate conversion");check(imported.objects.size()==original.objects.size(),"TMX logical IDs");
            var renderer=new com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer(map,1);try{var camera=new com.badlogic.gdx.graphics.OrthographicCamera();camera.setToOrtho(false,640,480);renderer.setView(camera);renderer.render();}finally{renderer.dispose();}
        }finally{map.dispose();}
        Files.writeString(tmx,xml.toString().replace("name=\"spawns\"","name=\"missing\""));var invalid=new com.badlogic.gdx.maps.tiled.TmxMapLoader().load(tmx.toAbsolutePath().toString());try{try{method.invoke(screen.world(),original,invalid);throw new AssertionError("Missing TMX spawn layer accepted");}catch(java.lang.reflect.InvocationTargetException e){check(e.getCause() instanceof IllegalArgumentException,"TMX clear error");}}finally{invalid.dispose();Files.deleteIfExists(tmx);}
        System.out.println("TMX adapter checks passed: real loader/renderer, pixel scale, Y conversion, ID preservation, missing-layer rejection.");
    }
    private static void menuPress(MenuScreen menu,int key){keys=Set.of(key);menu.render(1f/60);keys=Set.of();menu.render(1f/60);}
    private static void checkAll() throws Exception {
        original=Gdx.input;scripted=(Input)Proxy.newProxyInstance(Input.class.getClassLoader(),new Class<?>[]{Input.class},(proxy,method,args)->{if(method.getName().equals("isKeyPressed")||method.getName().equals("isKeyJustPressed"))return keys.contains((Integer)args[0]);return method.invoke(original,args);});Gdx.input=scripted;
        assets=new GameAssets();assets.validateRequired();AssetCatalog manifest=assets.catalog();content=StoryContent.load(Gdx.files.internal(manifest.path("story.content")));world=SceneCatalog.load(Gdx.files.internal(manifest.path("story.world")),manifest);
        Path folder=Files.createTempDirectory(Path.of("../lwjgl3/build"),"story-save-");save=new SaveStore(folder.resolve("slot.json"));
        try {
            Room304Game app=new Room304Game();try{app.create();check(app.getScreen() instanceof MenuScreen,"Menu startup");keys=Set.of(Input.Keys.ENTER);app.render();check(app.getScreen() instanceof GameScreen,"New game routing");GameScreen current=(GameScreen)app.getScreen();String first=current.session().dialogue().getLine().getId();app.render();app.render();check(first.equals(current.session().dialogue().getLine().getId()),"Held menu Enter must not advance opening");keys=Set.of();app.requestMenu();app.render();check(app.getScreen() instanceof MenuScreen,"Return menu routing");}finally{app.dispose();}
            begin(false,null);checkCameraNavigation();begin(false,null);snapshot("explore-16x9");float x=screen.player().getX();press(Input.Keys.ESCAPE);frame(Input.Keys.D);check(screen.isPaused()&&screen.player().getX()==x,"Pause movement lock");press(Input.Keys.ESCAPE);for(int i=0;i<3;i++)frame();
            press(Input.Keys.N);check(screen.session().isNotes(),"N opens notes");press(Input.Keys.N);check(screen.session().exploring(),"N closes notes");
            press(Input.Keys.ESCAPE);press(Input.Keys.DOWN);press(Input.Keys.ENTER);check(save.exists(),"Pause Save command writes file");press(Input.Keys.ENTER);check(!screen.isPaused(),"Resume after saving");
            save.save(screen.session(),screen.player().getX(),screen.player().getY(),screen.player().facing(),world);SaveStore.Loaded loaded=save.load(world);begin(false,loaded);check(screen.session().state().reached(Event.INITIAL_SCENE_DONE),"Load initial exploration");
            var blockedNpc=world.npcPosition(loaded.state(),"trang");
            try{GameScreen invalid=new GameScreen(assets,content,world,save,new AudioSettings(),false,new SaveStore.Loaded(loaded.state(),blockedNpc.x,blockedNpc.y,loaded.facing()),()->{},()->{},e->{});invalid.dispose();throw new AssertionError("NPC-blocked saved position accepted");}catch(IllegalArgumentException expected){check(expected.getMessage().contains("NPC"),"Clear NPC-blocked load error");}
            check(screen.session().exploring(),"Failed load must preserve prior live screen");
            checkTmx(folder);
            world.npc("trang").animations.put("idle.down","npc.trang.idle");world.npc("trang").canvasWidth=40;world.npc("trang").anchorX=.4f;world.player().animations.put("pose.phone","player.idle");
            begin(true,null);check(assets.loadedCount()>0,"Existing demo sheets load through story profile config");
            world=SceneCatalog.load(Gdx.files.internal(manifest.path("story.world")),manifest);
            checkImportedIdleAssets();begin(true,null);checkWallFurniture();begin(true,null);checkInteractionCues();begin(true,null);checkInspectionUi();begin(true,null);
            ready();snapshotImportedRoom();press(Input.Keys.ESCAPE);for(int i=0;i<3;i++)press(Input.Keys.DOWN);press(Input.Keys.ENTER);press(Input.Keys.ENTER);press(Input.Keys.ENTER);check(!screen.isPaused()&&screen.session().block().equals("FINAL"),"Help during final choice must preserve pending choice");snapshot("final-choices-16x9");choice("C6-01-CHOICE03");check(screen.session().ended()&&screen.session().state().ending()==Ending.A,"A runtime ending");System.out.println("Desktop A complete");
            begin(true,null);decision();choice("C3-04-CHOICE01");check(screen.session().ended()&&screen.session().state().ending()==Ending.B,"B runtime ending");System.out.println("Desktop B complete");
            Path cFile=folder.resolve("c-world.json");Files.writeString(cFile,Gdx.files.internal(manifest.path("story.world")).readString().replace("\"enableC\": false","\"enableC\": true"));world=SceneCatalog.load(new FileHandle(cFile.toFile()),manifest);
            begin(true,null);ready();choice("C6-01-CHOICE05");check(screen.dialogueUI().selectedChoiceId().equals("C-01-CHOICE02"),"C cancel selected first");choice("C-01-CHOICE02");choice("C6-01-CHOICE05");choice("C-01-CHOICE01");check(screen.session().ended()&&screen.session().state().ending()==Ending.C,"C runtime ending");System.out.println("Desktop C complete");
            Gdx.graphics.setWindowedMode(800,600);System.out.println("Input proxy after window resize: "+(Gdx.input==scripted));Gdx.input=scripted;screen.resize(800,600);for(int i=0;i<3;i++)frame();java.util.concurrent.atomic.AtomicBoolean replaySkip=new java.util.concurrent.atomic.AtomicBoolean();MenuScreen end=new MenuScreen(assets,content,new AudioSettings(),save,replaySkip::set,()->{},()->{},Ending.C);try{end.show();end.resize(800,600);end.render(0);snapshot("ending-4x3");menuPress(end,Input.Keys.ENTER);menuPress(end,Input.Keys.DOWN);menuPress(end,Input.Keys.ENTER);check(replaySkip.get(),"Replay skip button callback");}finally{end.dispose();}
            java.util.concurrent.atomic.AtomicBoolean continued=new java.util.concurrent.atomic.AtomicBoolean();MenuScreen main=new MenuScreen(assets,content,new AudioSettings(),save,skip->{},()->{check(save.load(world).state().reached(Event.INITIAL_SCENE_DONE),"Continue file validated");continued.set(true);},()->{},Ending.NONE);
            try{main.show();main.resize(800,600);main.render(0);menuPress(main,Input.Keys.DOWN);menuPress(main,Input.Keys.ENTER);check(continued.get(),"Continue menu command");}finally{main.dispose();}
            check(visualChecks.containsAll(Set.of("haunt","record","memory","seated")),"Missing runtime visual coverage: "+visualChecks);System.out.println("Runtime visual bindings passed: real animation profiles, haunt/record/memory/C seated, unchanged physics.");
            Files.deleteIfExists(cFile);
        }finally{if(screen!=null)screen.dispose();assets.dispose();Gdx.input=original;Files.deleteIfExists(save.path());Files.deleteIfExists(folder.resolve("c-world.json"));Files.deleteIfExists(folder.resolve("geometry.tmx"));Files.deleteIfExists(folder);}
    }
}
