package com.room304.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.room304.assets.GameAssets;
import com.room304.story.*;
import java.util.*;
import java.util.function.Consumer;

/** Main menu and completed-ending page share functional Scene2D controls. */
public final class MenuScreen extends ScreenAdapter {
    private final UiPanel panel;
    private final StoryInput input=new StoryInput();
    private final StoryContent content;
    private final AudioSettings audio;
    private final Consumer<Boolean> start;
    private final Runnable menu;
    private final SaveStore saves;
    private final Consumer<SaveStore.Loaded> load;
    private Runnable back;
    private final GameState.Ending ending;
    public MenuScreen(GameAssets assets,StoryContent content,AudioSettings audio,SaveStore saves,Consumer<Boolean> start,Runnable load,Runnable menu,GameState.Ending ending) {
        this(assets,content,audio,saves,start,saved->load.run(),menu,ending);
    }
    public MenuScreen(GameAssets assets,StoryContent content,AudioSettings audio,SaveStore saves,Consumer<Boolean> start,Consumer<SaveStore.Loaded> load,Runnable menu,GameState.Ending ending) {
        this.saves=saves;this.load=load;
        this.panel=ending==GameState.Ending.NONE?new UiPanel(assets,new FitViewport(960,540)):new UiPanel(assets);this.content=content;this.audio=audio;this.start=start;this.menu=menu;this.ending=ending;
        try{var config=new com.badlogic.gdx.utils.JsonReader().parse(Gdx.files.internal(assets.catalog().path("story.visuals")));panel.background(config.get("menus").getString(ending==GameState.Ending.NONE?"MENU":ending.name(),null));
        home("");}catch(RuntimeException e){panel.dispose();throw e;}
    }
    public void home(String message) {
        back=null;
        if(ending!=GameState.Ending.NONE) {
            String prefix=ending==GameState.Ending.A?"A-02-MAIN":ending==GameState.Ending.B?"B-02-MAIN":"C-02-MAIN";
            String title=content.text(prefix+(ending==GameState.Ending.A?"-TXT10":ending==GameState.Ending.B?"-TXT06":"-TXT15"));
            String body=content.text(prefix+(ending==GameState.Ending.A?"-TXT11":ending==GameState.Ending.B?"-TXT07":"-TXT16"));
            panel.show(title,body,List.of(new UiPanel.Option(content.text("UI-REPLAY"),this::replay),new UiPanel.Option(content.text("UI-MENU"),menu),new UiPanel.Option(content.text("UI-EXIT"),()->Gdx.app.exit())));
        } else if(!message.isBlank()) {
            back=()->home("");panel.show(content.text("UI-TITLE"),message,List.of(new UiPanel.Option(content.text("UI-BACK"),back)));
        } else {
            List<UiPanel.Option> options=new ArrayList<>();SaveStore.Slot slot=saves.inspect(false);
            boolean hasSave=saves.exists()||saves.hasBackup();
            options.add(new UiPanel.Option(hasSave?"Chơi tiếp":"Chơi",this::play));
            options.add(new UiPanel.Option(content.text("UI-SETTINGS"),this::settings));options.add(new UiPanel.Option(content.text("UI-HELP"),this::help));
            options.add(new UiPanel.Option(content.text("UI-EXIT"),()->Gdx.app.exit()));
            if(!saves.exists())slot=saves.inspect(true);
            panel.lobby(content.text("UI-TITLE"),options,slot.valid()?SaveMenu.summary(slot.loaded(),content):"");
        }
        input.modeChanged();
    }
    private void play(){
        if(!saves.exists()&&!saves.hasBackup()){start.accept(false);return;}
        SaveMenu.confirmLoad(panel,saves,load,()->home(""),this::navigateBack,!saves.exists());
    }
    private void navigateBack(Runnable action){back=action;input.modeChanged();}
    private void settings(){back=()->home("");panel.show(content.text("UI-SETTINGS"),"",List.of(
        new UiPanel.Option(content.text("UI-AUDIO"),this::audio),new UiPanel.Option("Bản lưu",this::saves),
        new UiPanel.Option("Khôi phục backup",()->SaveMenu.confirmLoad(panel,saves,load,this::settings,this::navigateBack,true),saves.hasBackup()),
        new UiPanel.Option(content.text("UI-BACK"),back)));input.modeChanged();}
    private void saves(){SaveMenu.manage(panel,saves,content,this::settings,this::navigateBack);}
    private void audio(){back=this::settings;panel.audio(content,audio,back);input.modeChanged();}
    private void help(){back=()->home("");panel.help(content,back);input.modeChanged();}
    private void replay(){back=()->home("");panel.show(content.text("UI-REPLAY-TITLE"),content.text("UI-REPLAY-BODY"),List.of(new UiPanel.Option(content.text("UI-REPLAY-WATCH"),()->start.accept(false)),new UiPanel.Option(content.text("UI-REPLAY-SKIP"),()->start.accept(true)),new UiPanel.Option(content.text("UI-REPLAY-BACK"),back)));input.modeChanged();}
    @Override public void show(){input.capture(Gdx.input);input.modeChanged();Gdx.input.setInputProcessor(panel.stage());}
    public void error(String message){home(message);Gdx.input.setInputProcessor(panel.stage());}
    @Override public void render(float delta){ScreenUtils.clear(.02f,.025f,.04f,1);input.capture(Gdx.input);if(input.pressed(Input.Keys.ESCAPE)&&back!=null)back.run();panel.key(input.choiceDirection(),input.pressed(Input.Keys.ENTER));panel.adjustAudio((input.pressed(Input.Keys.RIGHT)?1:0)-(input.pressed(Input.Keys.LEFT)?1:0));panel.draw(delta);panel.executeCommand();}
    @Override public void resize(int w,int h){panel.resize(w,h);}
    @Override public void hide(){if(Gdx.input.getInputProcessor()==panel.stage()) Gdx.input.setInputProcessor(null);panel.clearCommands();}
    @Override public void dispose(){hide();panel.dispose();}
}
