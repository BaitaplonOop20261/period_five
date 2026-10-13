package com.room304.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.ScreenUtils;
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
    private final SaveStore saves;
    private final Consumer<Boolean> start;
    private final Runnable load,menu;
    private final GameState.Ending ending;
    public MenuScreen(GameAssets assets,StoryContent content,AudioSettings audio,SaveStore saves,Consumer<Boolean> start,Runnable load,Runnable menu,GameState.Ending ending) {
        this.panel=new UiPanel(assets);this.content=content;this.audio=audio;this.saves=saves;this.start=start;this.load=load;this.menu=menu;this.ending=ending;
        try{var config=new com.badlogic.gdx.utils.JsonReader().parse(Gdx.files.internal(assets.catalog().path("story.visuals")));panel.background(config.get("menus").getString(ending==GameState.Ending.NONE?"MENU":ending.name(),null));
        home("");}catch(RuntimeException e){panel.dispose();throw e;}
    }
    public void home(String message) {
        if(ending!=GameState.Ending.NONE) {
            String prefix=ending==GameState.Ending.A?"A-02-MAIN":ending==GameState.Ending.B?"B-02-MAIN":"C-02-MAIN";
            String title=content.text(prefix+(ending==GameState.Ending.A?"-TXT10":ending==GameState.Ending.B?"-TXT06":"-TXT15"));
            String body=content.text(prefix+(ending==GameState.Ending.A?"-TXT11":ending==GameState.Ending.B?"-TXT07":"-TXT16"));
            panel.show(title,body,List.of(new UiPanel.Option(content.text("UI-REPLAY"),this::replay),new UiPanel.Option(content.text("UI-MENU"),menu),new UiPanel.Option(content.text("UI-EXIT"),()->Gdx.app.exit())));
        } else {
            List<UiPanel.Option> options=new ArrayList<>();options.add(new UiPanel.Option(content.text("UI-START"),()->start.accept(false)));
            if(saves.exists()) options.add(new UiPanel.Option("Tiếp tục từ bản lưu",load));
            options.add(new UiPanel.Option(content.text("UI-HELP"),()->panel.help(content,()->home(""))));
            options.add(new UiPanel.Option(content.text("UI-AUDIO"),()->panel.audio(content,audio,()->home(""))));
            options.add(new UiPanel.Option(content.text("UI-EXIT"),()->Gdx.app.exit()));panel.show(content.text("UI-TITLE"),message,options);
        }
        input.modeChanged();
    }
    private void replay(){panel.show(content.text("UI-REPLAY-TITLE"),content.text("UI-REPLAY-BODY"),List.of(new UiPanel.Option(content.text("UI-REPLAY-WATCH"),()->start.accept(false)),new UiPanel.Option(content.text("UI-REPLAY-SKIP"),()->start.accept(true)),new UiPanel.Option(content.text("UI-REPLAY-BACK"),()->home(""))));input.modeChanged();}
    @Override public void show(){input.capture(Gdx.input);input.modeChanged();Gdx.input.setInputProcessor(panel.stage());}
    @Override public void render(float delta){ScreenUtils.clear(.02f,.025f,.04f,1);input.capture(Gdx.input);panel.key(input.choiceDirection(),input.pressed(Input.Keys.ENTER));panel.adjustAudio((input.pressed(Input.Keys.RIGHT)?1:0)-(input.pressed(Input.Keys.LEFT)?1:0));panel.draw(delta);panel.executeCommand();}
    @Override public void resize(int w,int h){panel.resize(w,h);}
    @Override public void hide(){if(Gdx.input.getInputProcessor()==panel.stage()) Gdx.input.setInputProcessor(null);panel.clearCommands();}
    @Override public void dispose(){hide();panel.dispose();}
}
