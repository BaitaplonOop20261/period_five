package com.room304.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.room304.assets.GameAssets;
import com.room304.audio.StoryAudio;
import com.room304.dialogue.*;
import com.room304.story.*;
import com.room304.world.*;
import java.util.*;
import java.util.function.Consumer;
import static com.room304.story.GameState.*;

/** Playable story runtime. Presentation borrows assets; all progress belongs to StorySession. */
public final class GameScreen extends ScreenAdapter {
    private final GameAssets assets;
    private final StoryContent content;
    private final SceneCatalog catalog;
    private final SaveStore saves;
    private final AudioSettings audio;
    private final Runnable menu,load;
    private final Consumer<Ending> finish;
    private UiPanel panel;
    private DialogueUI dialogueUI;
    private StoryWorldView world;
    private Player player;
    private StoryAudio sound;
    private StorySession session;
    private final StoryInput input=new StoryInput();
    private final ShapeRenderer fade=new ShapeRenderer();
    private final FitViewport fadeViewport=new FitViewport(640,480);
    private boolean paused,finished,skipFrame;
    private float fadeRemaining,walkDistance;
    private String mode="";
    private SceneCatalog.ObjectDef inspectedObject;
    public GameScreen(GameAssets assets,StoryContent content,SceneCatalog catalog,SaveStore saves,AudioSettings audio,boolean skipOpening,SaveStore.Loaded saved,Runnable menu,Runnable load,Consumer<Ending> finish) {
        this.assets=assets;this.content=content;this.catalog=catalog;this.saves=saves;this.audio=audio;this.menu=menu;this.load=load;this.finish=finish;
        try {panel=new UiPanel(assets);dialogueUI=new DialogueUI(assets,DialogueLayout.load(assets.catalog().path("story.ui.layout")));
        world=new StoryWorldView(assets,catalog,content,panel.font());
        GameState state=saved==null?new GameState():saved.state();world.enter(state.room());
        SceneCatalog.Point initial=world.scene().spawn("entry");player=new Player(assets,initial.x,initial.y,catalog.player());
        session=new StorySession(content,catalog,state,this::movePlayer);
        if(saved==null) session.start(skipOpening);
        else {if(!world.canStand(saved.x(),saved.y())) throw new IllegalArgumentException("Bản đồ hiện tại không chấp nhận vị trí trong save.");player.setPosition(saved.x(),saved.y());player.restoreFacing(saved.facing());if(world.walls(session).stream().anyMatch(w->w.getBounds().overlaps(player.getBounds())))throw new IllegalArgumentException("Vị trí trong save bị NPC hoặc vật cản chặn.");}
        sound=new StoryAudio(assets,audio,content);world.update(0,session,player);sync();input.capture(Gdx.input);input.modeChanged();
        }catch(RuntimeException e){dispose();throw e;}
    }
    private void movePlayer(RoomId room,String spawn) {
        if(world.scene()==null || !world.scene().id.equals(room.name())) world.enter(room);
        SceneCatalog.Point point=world.scene().spawn(spawn);if(!world.canStand(point.x,point.y)) throw new IllegalArgumentException("Spawn bị chặn: "+room+"/"+spawn);
        player.setPosition(point.x,point.y);player.restoreFacing("up");fadeRemaining=.35f;skipFrame=true;input.modeChanged();
    }
    public StorySession session(){return session;}
    public Player player(){return player;}
    public StoryWorldView world(){return world;}
    public DialogueUI dialogueUI(){return dialogueUI;}
    public boolean isPaused(){return paused;}
    private void sync() {
        String next=paused?"PAUSE":fadeRemaining>0?"FADE":session.dialogue().isActive()?"DIALOGUE":"EXPLORE";
        if(!next.equals(mode)){mode=next;input.modeChanged();dialogueUI.clearCommands();panel.clearCommands();}
        InputProcessor processor=paused?panel.stage():session.dialogue().isActive()&&fadeRemaining==0?dialogueUI.getStage():null;
        if(Gdx.input.getInputProcessor()!=processor) Gdx.input.setInputProcessor(processor);
        if(session.exploring()||session.isNotes())inspectedObject=null;
        dialogueUI.present(session.dialogue(),world.scene().name,session.dialogue().isActive()?world.visuals().document(session.dialogue().getLine().getId(),session.block()):null,inspectedObject==null?null:StoryWorldView.inspectionAsset(inspectedObject,session.state()),inspectedObject==null?null:inspectedObject.name);
    }
    @Override public void show(){sync();}
    @Override public void render(float delta) {
        ScreenUtils.clear(.025f,.03f,.04f,1);delta=Math.min(Math.max(0,delta),.25f);input.capture(Gdx.input);
        walkDistance=0;boolean skipped=skipFrame;skipFrame=false;
        if(input.pressed(Input.Keys.ESCAPE)||dialogueUI.getHud().consumeMenuRequest()){if(paused) resumePlay();else pausePlay();skipped=true;}
        if(!paused && fadeRemaining==0 && !skipped) update(delta);
        sound.update(session,paused,delta,walkDistance);world.render(session,player);dialogueUI.draw();
        if(paused){panel.key(input.choiceDirection(),input.pressed(Input.Keys.ENTER));panel.adjustAudio((input.pressed(Input.Keys.RIGHT)?1:0)-(input.pressed(Input.Keys.LEFT)?1:0));panel.draw(delta);panel.executeCommand();}
        else if(fadeRemaining>0) {
            fadeViewport.apply();Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
            fade.setProjectionMatrix(fadeViewport.getCamera().combined);fade.begin(ShapeRenderer.ShapeType.Filled);fade.setColor(new Color(0,0,0,fadeRemaining/.35f));fade.rect(0,0,640,480);fade.end();Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
            fadeRemaining=Math.max(0,fadeRemaining-delta);
        }
        sync();if(session.ended()&&!finished){finished=true;finish.accept(session.state().ending());}
    }
    private void update(float delta) {
        boolean exploration=session.exploring();long revision=session.dialogue().getRevision();
        session.update(delta);sync();dialogueUI.update(delta);
        boolean automaticChanged=revision!=session.dialogue().getRevision();
        if(!automaticChanged) {
            if(input.pressed(Input.Keys.N)){session.toggleNotes();input.modeChanged();}
            else if(session.dialogue().isActive()) {
                dialogueUI.moveChoice(input.choiceDirection());String choice=dialogueUI.consumeChoiceRequest();
                boolean confirm=input.pressed(Input.Keys.ENTER)||dialogueUI.getHud().consumeContinueRequest();
                if(choice==null && confirm && dialogueUI.isChoosing()) choice=dialogueUI.selectedChoiceId();
                if(choice!=null) session.choose(choice);else if(confirm && dialogueUI.continueText()) session.advance();
            } else if(exploration && (input.pressed(Input.Keys.E)||input.pressed(Input.Keys.ENTER))) {world.update(0,session,player);String target=world.target();if(target!=null){inspectedObject=world.scene().objects.stream().filter(o->o.id.equals(target)).findFirst().orElse(null);session.interact(target);}}
        }
        boolean move=exploration && session.exploring() && fadeRemaining==0 && revision==session.dialogue().getRevision() && !input.pressed(Input.Keys.N);
        float beforeX=player.getX(),beforeY=player.getY();
        player.update(delta,move?input.horizontal():0,move?input.vertical():0,false,world.walls(session),null,List.of(),world.scene().width,world.scene().height);
        if(move)walkDistance=com.badlogic.gdx.math.Vector2.dst(beforeX,beforeY,player.getX(),player.getY());
        world.update(delta,session,player);sync();
    }
    private void pausePlay(){paused=true;input.modeChanged();dialogueUI.clearCommands();pauseMenu("");sync();}
    private void resumePlay(){paused=false;skipFrame=true;input.modeChanged();panel.clearCommands();sync();}
    private void pauseMenu(String message) {
        List<UiPanel.Option> options=new ArrayList<>();options.add(new UiPanel.Option(content.text("UI-RESUME"),this::resumePlay));
        options.add(new UiPanel.Option("Lưu game",()->{try {saves.save(session,player.getX(),player.getY(),player.facing(),catalog);pauseMenu("Đã lưu tiến độ.");}catch(RuntimeException e){pauseMenu(e.getMessage());}}));
        if(saves.exists()) options.add(new UiPanel.Option("Tải bản lưu",()->panel.show("Tải bản lưu?","Tiến độ chưa lưu sẽ bị thay thế.",List.of(new UiPanel.Option("Tải",load),new UiPanel.Option("Ở lại",()->pauseMenu(""))))));
        options.add(new UiPanel.Option(content.text("UI-HELP"),()->panel.help(content,()->pauseMenu(""))));options.add(new UiPanel.Option(content.text("UI-AUDIO"),()->panel.audio(content,audio,()->pauseMenu(""))));
        options.add(new UiPanel.Option(content.text("UI-MENU"),()->panel.show(content.text("UI-LEAVE-TITLE"),"Tiến độ chưa lưu sẽ mất; bản lưu hiện có được giữ lại.",List.of(new UiPanel.Option("Ở lại",()->pauseMenu("")),new UiPanel.Option("Về menu",menu)))));
        panel.show(content.text("UI-PAUSE-TITLE"),message,options);
    }
    public void error(String message){paused=true;pauseMenu(message);sync();}
    @Override public void resize(int w,int h){world.resize(w,h);dialogueUI.resize(w,h);panel.resize(w,h);if(w>0&&h>0)fadeViewport.update(w,h,true);}
    @Override public void pause(){pausePlay();}
    @Override public void hide(){if((dialogueUI!=null&&Gdx.input.getInputProcessor()==dialogueUI.getStage())||(panel!=null&&Gdx.input.getInputProcessor()==panel.stage()))Gdx.input.setInputProcessor(null);if(dialogueUI!=null)dialogueUI.clearCommands();if(panel!=null)panel.clearCommands();}
    @Override public void dispose(){hide();if(session!=null)session.close();if(sound!=null)sound.dispose();if(world!=null)world.dispose();if(player!=null)player.dispose();if(dialogueUI!=null)dialogueUI.dispose();if(panel!=null)panel.dispose();fade.dispose();}
}
