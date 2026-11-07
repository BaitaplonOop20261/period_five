package com.room304.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
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
    private static GameAssets assets;private static StoryContent content;private static SceneCatalog world;private static SaveStore save;
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg=new Lwjgl3ApplicationConfiguration();cfg.setTitle("Room304 story runtime checks");cfg.setWindowedMode(960,540);cfg.setInitialVisible(false);
        new Lwjgl3Application(new ApplicationAdapter(){@Override public void create(){Gdx.app.postRunnable(()->{try{checkAll();}catch(Throwable e){failure=e;}finally{Gdx.app.exit();}});}},cfg);
        if(failure!=null)throw new AssertionError("Story desktop checks failed",failure);
        System.out.println("Story desktop checks passed: A/B/C, "+interactions+" E/Enter interactions reached by walking, "+frames+" rendered frames, menu/replay, pause, notes, disk save/load, 16:9 and 4:3.");
    }
    private static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    private static void frame(int...held){keys=new HashSet<>();for(int key:held)keys.add(key);screen.render(1f/60);frames++;observeVisuals();}
    private static void press(int key){frame(key);frame();}
    private static void tick(){screen.render(.25f);frames++;observeVisuals();}
    private static void observeVisuals(){
        if(!screen.session().dialogue().isActive()) {
            if(screen.session().exploring() && "GUARD_ROOM".equals(screen.world().scene().id)
                && "OBJ-09".equals(screen.session().objectiveId()) && visualChecks.add("objective-guard")) {
                snapshot("objective-guard");
            }
            return;
        }
        String id=screen.session().dialogue().getLine().getId();
        if("C1-04-READ-T02".equals(id)){check(StoryWorldView.OPEN_REGISTER_ART.equals(screen.dialogueUI().inspectionAsset()),"Reading the register must show open pages");if(visualChecks.add("open-register"))snapshot("register-open");}
        if("C1-04-HAUNT-E08".equals(id)){check(screen.world().pose("nga").equals("pose.seated"),"Haunt seated ghost binding");visualChecks.add("haunt");}
        if("C4-05-MAIN-D06".equals(id)){check(screen.world().recording(),"Recording icon persists through tape subtitles");check("inspection.player_playing".equals(screen.dialogueUI().inspectionAsset()),"Tape close-up plays during tape dialogue");visualChecks.add("record");}
        if("A-01-MAIN-E03".equals(id)){check(screen.world().pose("nga").equals("walk.down"),"Memory departure pose");visualChecks.add("memory");}
        if("C-02-MAIN-E09".equals(id)){check(screen.world().pose("linh").equals("pose.seated"),"C seated pose");var p=screen.world().actorPosition("linh",screen.session(),screen.player());check(Math.abs(p.x-screen.player().getX())>1||Math.abs(p.y-screen.player().getY())>1,"C visual placement must not mutate Player physics");visualChecks.add("seated");}
    }
    private static void drain(){for(int i=0;i<5000;i++){if(!screen.session().dialogue().isActive()){for(int k=0;k<25;k++)frame();return;}if(!screen.session().dialogue().getLine().getChoices().isEmpty()){for(int k=0;k<3;k++)tick();screen.dialogueUI().continueText();frame();return;}tick();if(screen.session().dialogue().isActive())press(Input.Keys.ENTER);}throw new AssertionError("Dialogue stalled "+screen.session().block());}
    private static void choice(String id){drain();var options=screen.session().dialogue().getLine().getChoices();int index=-1;for(int i=0;i<options.size();i++)if(options.get(i).getId().equals(id))index=i;check(index>=0,"Choice missing "+id+" / "+screen.session().block());while(!id.equals(screen.dialogueUI().selectedChoiceId()))press(Input.Keys.DOWN);press(Input.Keys.ENTER);drain();}
    private static void walk(String id) {
        Player player=screen.player();SceneCatalog.Scene scene=screen.world().scene();Rectangle target;
        if(screen.session().visibleNpcs().contains(id)){SceneCatalog.Point p=world.npcPosition(screen.session().state(),id);target=new Rectangle(p.x,p.y,world.player().footWidth,world.player().footHeight);}else target=scene.object(id).interactionBounds();
        float sx=player.getX(),sy=player.getY();List<Wall> walls=screen.world().walls(screen.session());boolean approachFromLeft=Set.of("cabinet.upper","player","crate").contains(id),approachFromRight=Set.of("window","desk").contains(id)&&scene.id.equals("ROOM_304"),approachFromAbove=id.equals("door.hall")&&target.y<32;
        record Cell(int x,int y){}ArrayDeque<Cell> queue=new ArrayDeque<>();Map<Cell,Cell> previous=new HashMap<>();Cell start=new Cell(0,0),goal=null;queue.add(start);previous.put(start,null);
        while(!queue.isEmpty()) {
            Cell c=queue.remove();float x=sx+c.x*10,y=sy+c.y*10;Rectangle feet=new Rectangle(x,y,world.player().footWidth,world.player().footHeight);
            boolean below=feet.y+feet.height<=target.y-2 && target.y-feet.y-feet.height<=22 && Math.abs(feet.x+feet.width/2-target.x-target.width/2)<=12;
            boolean left=feet.x+feet.width<=target.x-2 && target.x-feet.x-feet.width<=22 && Math.abs(feet.y+feet.height/2-target.y-target.height/2)<=12;
            boolean above=feet.y>=target.y+target.height+2 && feet.y-target.y-target.height<=22 && feet.x+feet.width>target.x && feet.x<target.x+target.width;
            boolean right=feet.x>=target.x+target.width+2 && feet.x-target.x-target.width<=22 && Math.abs(feet.y+feet.height/2-target.y-target.height/2)<=12;
            if(approachFromLeft?left:approachFromRight?right:approachFromAbove?above:below){goal=c;break;}
            for(int[] offset:new int[][]{{0,1},{1,0},{0,-1},{-1,0}}){Cell n=new Cell(c.x+offset[0],c.y+offset[1]);if(previous.containsKey(n))continue;float nx=sx+n.x*10,ny=sy+n.y*10;Rectangle nf=new Rectangle(nx,ny,feet.width,feet.height);if(!screen.world().canStand(nx,ny)||walls.stream().anyMatch(w->w.getBounds().overlaps(nf)))continue;previous.put(n,c);queue.add(n);}
        }
        check(goal!=null,"No reachable interaction cell "+scene.id+"/"+id);List<Cell> path=new ArrayList<>();for(Cell c=goal;previous.get(c)!=null;c=previous.get(c))path.add(c);Collections.reverse(path);
        for(Cell cell:path){float x=sx+cell.x*10,y=sy+cell.y*10;for(int i=0;i<12;i++){float dx=x-player.getX(),dy=y-player.getY();if(Math.abs(dx)<1&&Math.abs(dy)<1)break;int key=Math.abs(dx)>1?(dx>0?Input.Keys.D:Input.Keys.A):(dy>0?Input.Keys.W:Input.Keys.S);frame(key);}check(Math.abs(player.getX()-x)<1&&Math.abs(player.getY()-y)<1,"Walking blocked "+id);}
        frame(approachFromLeft?Input.Keys.D:approachFromRight?Input.Keys.A:approachFromAbove?Input.Keys.S:Input.Keys.W);frame();check(id.equals(screen.world().target()),"Wrong E target "+id+" / "+screen.world().target());
    }
    private static void interact(String id){check(screen.session().exploring(),"Interaction outside exploration "+id);walk(id);press(interactions%2==0?Input.Keys.ENTER:Input.Keys.E);interactions++;drain();}
    private static void begin(boolean skip,SaveStore.Loaded saved){if(screen!=null)screen.dispose();screen=new GameScreen(assets,content,world,save,new AudioSettings(),skip,saved,()->{},()->{},e->{});screen.resize(960,540);screen.show();drain();}
    private static void decision(){interact("desk");interact("door.hall");interact("stairs");interact("duc");choice("C2-02-CHOICE03");interact("guard.wall");interact("cabinet");}
    private static void investigate(){decision();choice("C3-04-CHOICE02");choice("C3-04-CHOICE03");}
    private static void guard(){interact("door.hall");interact("guard.wall");}
    private static void room(){interact("door.hall");interact("door.304");}
    private static void ready(){investigate();interact("photo");guard();interact("records");room();interact("crate");interact("player");guard();interact("nam");choice("CHO-NAM-BAT");room();walk("player");press(Input.Keys.E);interactions++;String tapeLine=screen.session().dialogue().getLine().getId();press(Input.Keys.ESCAPE);for(int i=0;i<12;i++)tick();check(tapeLine.equals(screen.session().dialogue().getLine().getId())&&!screen.session().state().knows(Clue.CASSETTE_CONTENT),"Pause must freeze tape and not commit heard");press(Input.Keys.ESCAPE);drain();interact("door.hall");interact("door.hung");interact("hung");choice("C5-01-CHOICE03");choice("C5-01-CHOICE04");room();interact("desk");}
    private static void snapshot(String name){int width=Gdx.graphics.getBackBufferWidth(),height=Gdx.graphics.getBackBufferHeight();byte[] pixels=com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(0,0,width,height,true);Pixmap pix=new Pixmap(width,height,Pixmap.Format.RGBA8888);pix.getPixels().put(pixels);try{PixmapIO.writePNG(Gdx.files.local("../lwjgl3/build/story-"+name+".png"),pix);}finally{pix.dispose();}}
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
    private static byte[] interactionPixels(String id) throws ReflectiveOperationException {
        float x,y,width;
        if(screen.session().visibleNpcs().contains(id)) {
            var npc=world.npc(id);var p=screen.world().actorPosition(id,screen.session(),screen.player());
            width=npc.canvasWidth*npc.scale;x=p.x+npc.footWidth/2-width*npc.anchorX;
            y=p.y+npc.canvasHeight*npc.scale*(1-npc.anchorY)+20;
        } else {var object=screen.world().scene().object(id);x=object.x;y=object.y+object.height+20;width=object.width;}
        var field=StoryWorldView.class.getDeclaredField("viewport");field.setAccessible(true);
        var viewport=(com.badlogic.gdx.utils.viewport.Viewport)field.get(screen.world());
        var lower=viewport.project(new com.badlogic.gdx.math.Vector3(x,y,0));
        var upper=viewport.project(new com.badlogic.gdx.math.Vector3(x+width,y+24,0));
        return com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels((int)lower.x,(int)lower.y,(int)Math.ceil(upper.x-lower.x),(int)Math.ceil(upper.y-lower.y),false);
    }
    private static boolean hasInteractionMark(String id) throws ReflectiveOperationException {
        byte[] shown=interactionPixels(id);
        screen.world().render(screen.session(),screen.player(),false);
        byte[] hidden=interactionPixels(id);
        screen.world().render(screen.session(),screen.player(),true);
        return !Arrays.equals(shown,hidden);
    }
    private static void checkInteractionMarks() throws ReflectiveOperationException {
        screen.player().setPosition(488,240);screen.player().restoreFacing("right");frame();
        check("player".equals(screen.world().target()),"Closer cassette player wins overlapping interaction ranges");
        byte[] first=interactionPixels("player");check(hasInteractionMark("player"),"Selected object renders the actual marker pixels");
        check(!hasInteractionMark("crate"),"Only one of the nearby objects receives a marker");
        snapshot("interaction-player-16x9");tick();
        check(!Arrays.equals(first,interactionPixels("player")),"Interaction marker advances through its animation frames");
        screen.player().setPosition(488,239);frame();check("player".equals(screen.world().target()),"Exact distance tie retains the selected object");
        screen.player().setPosition(488,238);frame();check("crate".equals(screen.world().target()),"Slightly closer crate immediately replaces the previous target");
        check(hasInteractionMark("crate")&&!hasInteractionMark("player"),"Marker follows the selected target without leaving the old marker");
        snapshot("interaction-crate-16x9");
        screen.world().render(screen.session(),screen.player(),false);
        check(!hasInteractionMark("crate"),"Paused/transition presentation suppresses the marker");frame();
        press(Input.Keys.N);screen.world().render(screen.session(),screen.player(),true);
        check(!hasInteractionMark("crate"),"Notes suppress interaction markers");press(Input.Keys.N);
        press(Input.Keys.E);check(screen.session().dialogue().isActive(),"E interacts with the marked crate");
        screen.world().render(screen.session(),screen.player(),true);
        check(!hasInteractionMark("crate"),"Dialogue suppresses interaction markers");drain();
        walk("trang");check(hasInteractionMark("trang"),"NPC receives the same interaction marker above its name");snapshot("interaction-npc-16x9");
        Gdx.graphics.setWindowedMode(800,600);Gdx.input=scripted;screen.resize(800,600);frame();
        check(hasInteractionMark("trang"),"Marker remains aligned after 4:3 resize");snapshot("interaction-npc-4x3");
        Gdx.graphics.setWindowedMode(960,540);Gdx.input=scripted;screen.resize(960,540);
        screen.player().setPosition(300,90);frame();check(screen.world().target()==null,"No interaction selected outside all ranges");
        int[] objectiveBorder = roomPixel(100, 465.5f);
        int width = Gdx.graphics.getBackBufferWidth();
        // The objective HUD also disappears when interaction is disabled. Compare the scene below it.
        int heightBelowHud = Gdx.graphics.getBackBufferHeight() * 3 / 4;
        byte[] far = com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(0, 0, width, heightBelowHud, false);
        screen.world().render(screen.session(),screen.player(),false);
        check(Arrays.equals(far, com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(0, 0, width, heightBelowHud, false)),
            "No marker rendered when far from interactables");
        check(!Arrays.equals(objectiveBorder, roomPixel(100, 465.5f)), "Objective HUD hides when exploration controls are disabled");
        System.out.println("Interaction markers passed: real animated pixels, nearest/tied/overlapping objects, E target, NPC, modal suppression, far range and resize.");
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
        check(screen.dialogueUI().inspectionAsset()==null,"NPC dialogue must not reuse object close-up");
        checkDialogueFrame();drain();
        System.out.println("Inspection UI passed: E/held keys, real dim pixels, movement lock, pause/resume, resize 16:9/4:3, repeat/cache, notes and NPC cleanup.");
    }
    private static Rectangle actorBounds(com.badlogic.gdx.scenes.scene2d.Actor actor) {
        var position=actor.localToStageCoordinates(new com.badlogic.gdx.math.Vector2());
        return new Rectangle(position.x,position.y,actor.getWidth(),actor.getHeight());
    }
    private static void click(com.badlogic.gdx.scenes.scene2d.Stage stage,String actorName) {
        var actor=stage.getRoot().findActor(actorName);
        check(actor!=null&&actor.getWidth()>0&&actor.getHeight()>0,"Click target must be laid out: "+actorName);
        var point=actor.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(actor.getWidth()/2,actor.getHeight()/2));
        stage.stageToScreenCoordinates(point);
        stage.touchDown(Math.round(point.x),Math.round(point.y),0,Input.Buttons.LEFT);
        stage.touchUp(Math.round(point.x),Math.round(point.y),0,Input.Buttons.LEFT);
    }
    private static void checkDialogueFrame() {
        var ui=screen.dialogueUI();var stage=ui.getStage();
        var frame=(com.badlogic.gdx.scenes.scene2d.ui.Image)stage.getRoot().findActor("dialogue-frame");
        var batch=new com.badlogic.gdx.graphics.g2d.SpriteBatch();
        try {
            stage.getViewport().apply();com.badlogic.gdx.utils.ScreenUtils.clear(.8f,.5f,.2f,1);
            batch.setProjectionMatrix(stage.getCamera().combined);batch.begin();frame.draw(batch,1);batch.end();
            int[] expected={71,49,29};
            for(float sampleY:new float[]{100,frame.getY()+2}) {
                int[] actual=roomPixel(320,sampleY);
                for(int channel=0;channel<3;channel++)check(Math.abs(actual[channel]-expected[channel])<=4,"Dialogue body must blend at 75% opacity without overlapping fills or shadow");
            }
            int[] border=roomPixel(frame.getX(),100);
            check(border[0]>160&&border[1]>120,"Dialogue border remains solid gold");
        } finally {batch.dispose();}
        frame();
        var line=screen.session().dialogue().getLine();
        click(stage,"dialogue-next");frame();
        check(ui.isTextComplete()&&line==screen.session().dialogue().getLine(),"Mouse reveal must not skip the NPC line");
        for(int[] size:new int[][]{{640,480},{1280,960},{800,600},{960,540},{1280,720}}) {
            Gdx.graphics.setWindowedMode(size[0],size[1]);Gdx.input=scripted;screen.resize(size[0],size[1]);frame();
            Rectangle panel=actorBounds(stage.getRoot().findActor("dialogue-frame"));
            Rectangle body=actorBounds(stage.getRoot().findActor("dialogue-text"));
            Rectangle controls=actorBounds(stage.getRoot().findActor("dialogue-controls"));
            Rectangle hint=actorBounds(stage.getRoot().findActor("dialogue-hint"));
            var speaker=stage.getRoot().findActor("dialogue-speaker");Rectangle tab=actorBounds(speaker);
            check(panel.contains(body)&&panel.contains(controls)&&panel.contains(hint),"Text/footer must stay inside the dialogue frame after resize");
            check(body.y>controls.y+controls.height&&hint.x+hint.width<controls.x,"Body and footer controls must not overlap");
            check(speaker.isVisible()&&tab.y<panel.y+panel.height&&tab.y+tab.height>panel.y+panel.height,"Speaker tab must attach to top border");
            if(size[0]==960||size[0]==800)snapshot("dialogue-trang-"+size[0]+"x"+size[1]);
        }
        click(stage,"dialogue-menu");frame();check(screen.isPaused(),"Footer Menu must open pause by mouse after resize");
        press(Input.Keys.ESCAPE);frame();check(line==screen.session().dialogue().getLine(),"Pause/resume preserves framed dialogue");
        click(stage,"dialogue-next");frame();
        check(line!=screen.session().dialogue().getLine(),"Footer Next must advance a completed line by mouse");
        Gdx.graphics.setWindowedMode(960,540);Gdx.input=scripted;screen.resize(960,540);frame();
        System.out.println("Dialogue frame passed: body/footer containment, attached name tab, five window sizes, mouse reveal/next/menu and pause/resume.");
    }
    private static void checkPauseMenuUi() {
        float x=screen.player().getX(),y=screen.player().getY();press(Input.Keys.ESCAPE);
        var stage=(com.badlogic.gdx.scenes.scene2d.Stage)Gdx.input.getInputProcessor();
        for(int[] size:new int[][]{{640,480},{1280,960},{800,600},{960,540},{1280,720}}) {
            Gdx.graphics.setWindowedMode(size[0],size[1]);Gdx.input=scripted;screen.resize(size[0],size[1]);frame(Input.Keys.D);
            var card=(com.badlogic.gdx.scenes.scene2d.ui.Table)stage.getRoot().findActor("menu-panel");
            Rectangle bounds=actorBounds(card);
            var title=(com.badlogic.gdx.scenes.scene2d.ui.Label)stage.getRoot().findActor("menu-title");
            check(title.getText().toString().equals("Tạm dừng"),"Pause header must contain only the approved title");
            long labels=java.util.stream.StreamSupport.stream(card.getChildren().spliterator(),false).filter(a->a instanceof com.badlogic.gdx.scenes.scene2d.ui.Label).count();
            check(labels==1,"No room-name subtitle above pause heading");
            check(bounds.width<400&&bounds.height<=440&&Math.abs(bounds.x+bounds.width/2-320)<1&&Math.abs(bounds.y+bounds.height/2-240)<1,"Pause card fits and stays centered after resize");
            check(bounds.contains(actorBounds(title))&&bounds.contains(actorBounds(stage.getRoot().findActor("menu-footer"))),"Pause title and shortcuts fit inside frame");
            for(int i=0;i<5;i++)check(bounds.contains(actorBounds(stage.getRoot().findActor("menu-option-"+i))),"Pause action stays inside frame");
            check(screen.isPaused()&&screen.player().getX()==x&&screen.player().getY()==y,"Pause menu must keep movement locked");
            if(size[0]==960||size[0]==800)snapshot("pause-menu-"+size[0]+"x"+size[1]);
        }
        Gdx.graphics.setWindowedMode(960,540);Gdx.input=scripted;screen.resize(960,540);frame();
        click(stage,"menu-option-2");frame();frame();
        check(((com.badlogic.gdx.scenes.scene2d.ui.Label)stage.getRoot().findActor("menu-title")).getText().toString().equals("Hướng dẫn"),"Mouse opens framed Help");
        snapshot("pause-help-16x9");click(stage,"menu-option-0");frame();frame();
        click(stage,"menu-option-3");frame();frame();
        check(((com.badlogic.gdx.scenes.scene2d.ui.Label)stage.getRoot().findActor("menu-title")).getText().toString().equals("Âm thanh"),"Mouse opens framed Audio");
        var master=(com.badlogic.gdx.scenes.scene2d.ui.TextButton)stage.getRoot().findActor("menu-option-0");String before=master.getText().toString();
        press(Input.Keys.RIGHT);
        check(!before.equals(((com.badlogic.gdx.scenes.scene2d.ui.TextButton)stage.getRoot().findActor("menu-option-0")).getText().toString()),"Audio Right changes master volume");
        snapshot("pause-audio-16x9");
        for(int i=0;i<4;i++)press(Input.Keys.DOWN);
        String mute=((com.badlogic.gdx.scenes.scene2d.ui.TextButton)stage.getRoot().findActor("menu-option-4")).getText().toString();press(Input.Keys.ENTER);
        check(!mute.equals(((com.badlogic.gdx.scenes.scene2d.ui.TextButton)stage.getRoot().findActor("menu-option-4")).getText().toString()),"Framed Audio mute still toggles");
        press(Input.Keys.UP);press(Input.Keys.ENTER);
        click(stage,"menu-option-4");frame();frame();
        check(((com.badlogic.gdx.scenes.scene2d.ui.Label)stage.getRoot().findActor("menu-title")).getText().toString().equals("Về menu chính?"),"Mouse opens leave confirmation");
        click(stage,"menu-option-0");frame();frame();click(stage,"menu-option-0");frame();frame();
        check(!screen.isPaused()&&screen.session().exploring(),"Stay then Resume returns to exploration");
        System.out.println("Pause menu passed: title without room subtitle, centered frame at five sizes, mouse Help/Audio/leave/resume, keyboard volume/mute/back and movement lock.");
    }
    private static int[] roomPixel(float worldX,float worldY) {
        int width=Gdx.graphics.getBackBufferWidth(),height=Gdx.graphics.getBackBufferHeight();
        float scale=Math.min(width/640f,height/480f);
        int viewportWidth=Math.round(640*scale),viewportHeight=Math.round(480*scale);
        int x=(width-viewportWidth)/2+(int)(worldX*viewportWidth/640),y=(height-viewportHeight)/2+(int)(worldY*viewportHeight/480);
        byte[] pixel=com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(x,y,1,1,false);
        return new int[]{Byte.toUnsignedInt(pixel[0]),Byte.toUnsignedInt(pixel[1]),Byte.toUnsignedInt(pixel[2])};
    }
    private static void checkLobbyUi() throws Exception {
        byte[] saved=Files.readAllBytes(save.path());
        var starts=new java.util.concurrent.atomic.AtomicInteger();var loaded=new java.util.concurrent.atomic.AtomicBoolean();
        AudioSettings audio=new AudioSettings();
        MenuScreen main=new MenuScreen(assets,content,audio,save,skip->{check(!skip,"Play starts with opening");starts.incrementAndGet();},()->loaded.set(true),()->{},Ending.NONE);
        try {
            main.show();keys=Set.of();var stage=(com.badlogic.gdx.scenes.scene2d.Stage)Gdx.input.getInputProcessor();
            String[] labels={"Play","Cài đặt","Hướng dẫn","Thoát"};
            for(int[] size:new int[][]{{960,540},{1280,720},{800,600},{640,480},{1280,960}}) {
                Gdx.graphics.setWindowedMode(size[0],size[1]);Gdx.input=scripted;main.resize(size[0],size[1]);main.render(0);
                check(stage.getViewport().getWorldWidth()==960&&stage.getViewport().getWorldHeight()==540,"Lobby keeps 16:9 art canvas");
                check(stage.getRoot().findActor("menu-panel")==null,"Lobby must use side navigation instead of a centered modal");
                for(int i=0;i<labels.length;i++) {
                    var button=(com.badlogic.gdx.scenes.scene2d.ui.TextButton)stage.getRoot().findActor("menu-option-"+i);Rectangle b=actorBounds(button);
                    check(button.getText().toString().equals(labels[i]),"Lobby option order: "+labels[i]);
                    check(b.x>=0&&b.x+b.width<320&&b.y>=0&&b.y+b.height<325,"Lobby buttons fit below title in left third");
                }
                check(stage.getRoot().findActor("menu-option-4")==null,"Existing save must not add Continue in this version");
                if(size[0]==960||size[0]==1280&&size[1]==720||size[0]==800)snapshot("lobby-"+size[0]+"x"+size[1]);
            }
            Gdx.graphics.setWindowedMode(960,540);Gdx.input=scripted;main.resize(960,540);main.render(0);
            menuPress(main,Input.Keys.UP);check(((com.badlogic.gdx.scenes.scene2d.ui.TextButton)stage.getRoot().findActor("menu-option-3")).isChecked(),"Lobby keyboard wraps to Exit");menuPress(main,Input.Keys.DOWN);
            click(stage,"menu-option-1");main.render(0);main.render(0);
            check(((com.badlogic.gdx.scenes.scene2d.ui.Label)stage.getRoot().findActor("menu-title")).getText().toString().equals("Cài đặt"),"Settings appears above Help and opens by mouse");
            click(stage,"menu-option-0");main.render(0);main.render(0);menuPress(main,Input.Keys.RIGHT);
            check(Math.abs(audio.master-.8f)<.001f,"Lobby Settings contains working Audio controls");snapshot("lobby-settings-audio");
            menuPress(main,Input.Keys.ESCAPE);menuPress(main,Input.Keys.ESCAPE);
            check(stage.getRoot().findActor("lobby-actions")!=null,"Esc returns from Audio through Settings to lobby");
            click(stage,"menu-option-2");main.render(0);main.render(0);
            check(((com.badlogic.gdx.scenes.scene2d.ui.Label)stage.getRoot().findActor("menu-title")).getText().toString().equals("Hướng dẫn"),"Lobby Help opens by mouse");menuPress(main,Input.Keys.ESCAPE);
            click(stage,"menu-option-0");main.render(0);main.render(0);check(starts.get()==1,"Play starts by mouse");
            menuPress(main,Input.Keys.ENTER);check(starts.get()==2&&!loaded.get(),"Play starts by keyboard even when a save exists");
            check(Arrays.equals(saved,Files.readAllBytes(save.path())),"Lobby Play must preserve the existing save file");
        } finally {main.dispose();}
        System.out.println("Lobby passed: original title art, Play/Settings/Help/Exit order, no Continue with existing save, five sizes, keyboard/mouse, nested Audio, Esc and save preservation.");
    }
    private static void checkAll() throws Exception {
        original=Gdx.input;scripted=(Input)Proxy.newProxyInstance(Input.class.getClassLoader(),new Class<?>[]{Input.class},(proxy,method,args)->{if(method.getName().equals("isKeyPressed")||method.getName().equals("isKeyJustPressed"))return keys.contains((Integer)args[0]);return method.invoke(original,args);});Gdx.input=scripted;
        assets=new GameAssets();assets.validateRequired();AssetCatalog manifest=assets.catalog();content=StoryContent.load(Gdx.files.internal(manifest.path("story.content")));world=SceneCatalog.load(Gdx.files.internal(manifest.path("story.world")),manifest);
        Path folder=Files.createTempDirectory("room304-story-save-");save=new SaveStore(folder.resolve("slot.json"));
        try {
            Room304Game app = new Room304Game();
            try {
                app.create();
                check(app.getScreen() instanceof MenuScreen, "Menu startup");
                keys = Set.of(Input.Keys.ENTER);
                app.render();
                check(app.getScreen() instanceof GameScreen, "New game routing");
                GameScreen current = (GameScreen) app.getScreen();
                String first = current.session().dialogue().getLine().getId();
                app.render();
                app.render();
                check(first.equals(current.session().dialogue().getLine().getId()), "Held menu Enter must not advance opening");
                current.dialogueUI().continueText();
                app.render();
                snapshot("opening-without-location");
                keys = Set.of();
                app.requestMenu();
                app.render();
                check(app.getScreen() instanceof MenuScreen, "Return menu routing");
            } finally {
                app.dispose();
            }
            var side=assets.animation("char.linh.walk.side");
            var mirrored=assets.animation("char.linh.walk.side",true);
            check(!side.getKeyFrame(0).isFlipX() && mirrored.getKeyFrame(0).isFlipX(),"Opposite walking directions must mirror independently");
            check(!assets.animation("char.linh.walk.side").getKeyFrame(0).isFlipX(),"Mirroring must not mutate shared sprite frames");
            begin(false,null);snapshot("explore-16x9");
            checkPauseMenuUi();checkInteractionMarks();begin(false,null);checkInspectionUi();begin(false,null);
            var cameraField=StoryWorldView.class.getDeclaredField("camera");cameraField.setAccessible(true);
            var follow=(FollowCamera)cameraField.get(screen.world());
            for(RoomId roomId:world.rooms()) {
                screen.world().enter(roomId);screen.session().state().enteredRoom(roomId);
                screen.player().setPosition(300,200);frame();float cameraStart=follow.position.x;
                check(follow.viewportWidth==480&&follow.viewportHeight==360,"Follow camera uses 480x360 in "+roomId);
                screen.player().setPosition(400,200);frame();check(follow.position.x>cameraStart,"Camera follows player in "+roomId);
                screen.player().setPosition(16,16);frame();check(follow.position.x==240&&follow.position.y==180,"Camera stops at lower-left map edge in "+roomId);
                var scene=screen.world().scene();screen.player().setPosition(scene.width-32,scene.height-58);frame();
                check(follow.position.x==scene.width-240&&follow.position.y==scene.height-180,"Camera stops at upper-right map edge in "+roomId);
            }
            begin(false,null);frame();
            screen.player().setPosition(112,368);frame();snapshot("desk-behind");
            screen.player().setPosition(112,318);frame();snapshot("desk-front");
            begin(false,null);
            walk("window");press(Input.Keys.E);frame();
            check("document.window".equals(screen.world().visuals().document(screen.session().dialogue().getLine().getId(),screen.session().block())),"Window interaction detail image");
            snapshot("window-detail");drain();
            walk("photo");press(Input.Keys.E);frame();
            check("document.photo".equals(screen.world().visuals().document(screen.session().dialogue().getLine().getId(),screen.session().block())),"Photo interaction detail image");
            snapshot("photo-detail");
            begin(false,null);
            screen.player().setPosition(115,310);screen.player().restoreFacing("up");frame();
            check(!"desk".equals(screen.world().target()),"Teacher desk must not be usable from below the podium");
            for(var object:screen.world().scene().objects)walk(object.id);
            walk("window");snapshot("room304-window");walk("cabinet.upper");snapshot("room304-cabinet");
            Gdx.graphics.setWindowedMode(800,600);Gdx.input=scripted;screen.resize(800,600);frame();snapshot("room304-4x3");
            Gdx.graphics.setWindowedMode(960,540);Gdx.input=scripted;begin(false,null);
            var room=screen.world().scene();var tables=room.collisions.subList(room.collisions.size()-20,room.collisions.size());
            for(var table:tables){Player probe=new Player(assets,table.x+(table.width-world.player().footWidth)/2,table.y-world.player().footHeight-2,world.player());try{for(int i=0;i<8;i++)probe.update(.25f,0,1,false,screen.world().walls(screen.session()),null,List.of(),room.width,room.height);check(probe.getBounds().y+probe.getBounds().height<=table.y,"Player crossed classroom table "+table.x+"/"+table.y);}finally{probe.dispose();}}
            // North boundaries align the bottom of the feet with the painted trim.
            Player wallProbe=new Player(assets,280,320,world.player());
            try {
                for(int i=0;i<8;i++)wallProbe.update(.25f,0,1,true,screen.world().walls(screen.session()),null,List.of(),room.width,room.height);
                check(wallProbe.getY()<=384 && wallProbe.getY()>=379,"Linh must stop at the red boundary below the board");
                wallProbe.setPosition(520,320);
                for(int i=0;i<8;i++)wallProbe.update(.25f,0,1,true,screen.world().walls(screen.session()),null,List.of(),room.width,room.height);
                check(wallProbe.getY()<=377 && wallProbe.getY()>=372,"Linh must stop at the lower red boundary beside the podium");
                wallProbe.setPosition(40,310);
                for(int i=0;i<8;i++)wallProbe.update(.25f,0,1,true,screen.world().walls(screen.session()),null,List.of(),room.width,room.height);
                check(wallProbe.getY()<=340 && wallProbe.getY()>=335,"Linh crossed the added west boundary");
                wallProbe.setPosition(580,330);
                for(int i=0;i<8;i++)wallProbe.update(.25f,0,1,true,screen.world().walls(screen.session()),null,List.of(),room.width,room.height);
                check(wallProbe.getY()<=364 && wallProbe.getY()>=359,"Linh crossed the cupboard boundary");
                wallProbe.setPosition(40,180);
                for(int i=0;i<8;i++)wallProbe.update(.25f,-1,0,true,screen.world().walls(screen.session()),null,List.of(),room.width,room.height);
                check(wallProbe.getX()>=24,"Linh crosses west wall");
                wallProbe.setPosition(590,180);
                for(int i=0;i<8;i++)wallProbe.update(.25f,1,0,true,screen.world().walls(screen.session()),null,List.of(),room.width,room.height);
                check(wallProbe.getX()+world.player().footWidth<=628,"Linh crosses east wall");
            } finally {wallProbe.dispose();}
            System.out.println("Room304 layout checks passed: all nine props/door reached by walking, all 20 student tables and room walls block movement, 16:9 and 4:3.");
            float x=screen.player().getX();press(Input.Keys.ESCAPE);frame(Input.Keys.D);check(screen.isPaused()&&screen.player().getX()==x,"Pause movement lock");press(Input.Keys.ESCAPE);for(int i=0;i<3;i++)frame();
            walk("trang");frame(Input.Keys.ENTER);check(screen.session().dialogue().isActive(),"Enter opens NPC dialogue");var firstNpcLine=screen.session().dialogue().getLine();for(int i=0;i<3;i++)frame(Input.Keys.ENTER);check(firstNpcLine==screen.session().dialogue().getLine(),"Held interaction Enter must not skip first NPC line");frame();drain();
            press(Input.Keys.N);check(screen.session().isNotes(),"N opens notes");press(Input.Keys.N);check(screen.session().exploring(),"N closes notes");
            press(Input.Keys.ESCAPE);press(Input.Keys.DOWN);press(Input.Keys.ENTER);check(save.exists(),"Pause Save command writes file");press(Input.Keys.ENTER);check(!screen.isPaused(),"Resume after saving");
            save.save(screen.session(),screen.player().getX(),screen.player().getY(),screen.player().facing(),world);SaveStore.Loaded loaded=save.load(world);begin(false,loaded);check(screen.session().state().reached(Event.INITIAL_SCENE_DONE),"Load initial exploration");
            var blockedNpc=world.npcPosition(loaded.state(),"trang");
            try{GameScreen invalid=new GameScreen(assets,content,world,save,new AudioSettings(),false,new SaveStore.Loaded(loaded.state(),blockedNpc.x,blockedNpc.y,loaded.facing()),()->{},()->{},e->{});invalid.dispose();throw new AssertionError("NPC-blocked saved position accepted");}catch(IllegalArgumentException expected){check(expected.getMessage().contains("NPC"),"Clear NPC-blocked load error");}
            check(screen.session().exploring(),"Failed load must preserve prior live screen");
            checkTmx(folder);
            world.npc("trang").animations.put("idle.down","npc.trang.idle");world.npc("trang").canvasWidth=40;world.npc("trang").anchorX=.4f;world.player().animations.put("pose.phone","player.idle");
            begin(true,null);check(assets.loadedCount()>0,"Existing demo sheets load through story profile config");
            ready();press(Input.Keys.ESCAPE);for(int i=0;i<3;i++)press(Input.Keys.DOWN);press(Input.Keys.ENTER);press(Input.Keys.ENTER);press(Input.Keys.ENTER);check(!screen.isPaused()&&screen.session().block().equals("FINAL"),"Help during final choice must preserve pending choice");snapshot("final-choices-16x9");choice("C6-01-CHOICE03");check(screen.session().ended()&&screen.session().state().ending()==Ending.A,"A runtime ending");System.out.println("Desktop A complete");
            begin(true,null);decision();choice("C3-04-CHOICE01");check(screen.session().ended()&&screen.session().state().ending()==Ending.B,"B runtime ending");System.out.println("Desktop B complete");
            Path cFile=folder.resolve("c-world.json");Files.writeString(cFile,Gdx.files.internal(manifest.path("story.world")).readString().replace("\"enableC\": false","\"enableC\": true"));world=SceneCatalog.load(new FileHandle(cFile.toFile()),manifest);
            begin(true,null);ready();choice("C6-01-CHOICE05");check(screen.dialogueUI().selectedChoiceId().equals("C-01-CHOICE02"),"C cancel selected first");choice("C-01-CHOICE02");choice("C6-01-CHOICE05");choice("C-01-CHOICE01");check(screen.session().ended()&&screen.session().state().ending()==Ending.C,"C runtime ending");System.out.println("Desktop C complete");
            Gdx.graphics.setWindowedMode(800,600);System.out.println("Input proxy after window resize: "+(Gdx.input==scripted));Gdx.input=scripted;screen.resize(800,600);for(int i=0;i<3;i++)frame();java.util.concurrent.atomic.AtomicBoolean replaySkip=new java.util.concurrent.atomic.AtomicBoolean();MenuScreen end=new MenuScreen(assets,content,new AudioSettings(),save,replaySkip::set,()->{},()->{},Ending.C);try{end.show();end.resize(800,600);end.render(0);snapshot("ending-4x3");menuPress(end,Input.Keys.ENTER);menuPress(end,Input.Keys.DOWN);menuPress(end,Input.Keys.ENTER);check(replaySkip.get(),"Replay skip button callback");}finally{end.dispose();}
            checkLobbyUi();
            check(visualChecks.containsAll(Set.of("open-register","haunt","record","memory","seated")),"Missing runtime visual coverage: "+visualChecks);System.out.println("Runtime visual bindings passed: open register, playback, real animation profiles, haunt/memory/C seated, unchanged physics.");
            Files.deleteIfExists(cFile);
        }finally{if(screen!=null)screen.dispose();assets.dispose();Gdx.input=original;Files.deleteIfExists(save.path());Files.deleteIfExists(folder.resolve("c-world.json"));Files.deleteIfExists(folder.resolve("geometry.tmx"));
            // OneDrive can mark a newly created fixture directory read-only on Windows.
            var dos=Files.getFileAttributeView(folder,java.nio.file.attribute.DosFileAttributeView.class);if(dos!=null)dos.setReadOnly(false);
            Files.deleteIfExists(folder);}
    }
}
