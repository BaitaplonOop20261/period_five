package com.room304;

import com.badlogic.gdx.*;
import com.room304.assets.GameAssets;
import com.room304.screen.*;
import com.room304.story.*;
import com.room304.world.SceneCatalog;
import static com.room304.story.GameState.Ending;

/** Owns shared assets and defers screen replacement until callbacks/render complete. */
public class Room304Game extends Game {
    private GameAssets assets;
    private StoryContent content;
    private SceneCatalog world;
    private SaveStore saves;
    private final AudioSettings audio=new AudioSettings();
    private Runnable requestedScreen;
    private final SaveStore suppliedSaves;
    public Room304Game(){this(null);}
    /** Allows desktop regression to use an isolated slot instead of the user's profile. */
    public Room304Game(SaveStore saves){suppliedSaves=saves;}
    @Override public void create() {
        assets=new GameAssets();
        try {assets.validateRequired();content=StoryContent.load(Gdx.files.internal(assets.catalog().path("story.content")));world=SceneCatalog.load(Gdx.files.internal(assets.catalog().path("story.world")),assets.catalog());world.validateContent(content);com.room304.world.StoryVisuals.load(com.badlogic.gdx.Gdx.files.internal(assets.catalog().path("story.visuals")),assets.catalog(),content,world);saves=suppliedSaves==null?new SaveStore(SaveStore.defaultPath()):suppliedSaves;
            String notice="";try{if(suppliedSaves==null)saves.importLegacy(Gdx.files.local("saves/room304.json").file().toPath(),world);}catch(RuntimeException e){notice="Không thể chuyển bản lưu cũ: "+e.getMessage();}
            MenuScreen initial=createMenu(Ending.NONE);setScreen(initial);if(!notice.isBlank())initial.home(notice);}
        catch(RuntimeException e){assets.dispose();throw e;}
    }
    private MenuScreen createMenu(Ending ending){return new MenuScreen(assets,content,audio,saves,skip->requestNewGame(skip),this::requestLoaded,()->requestMenu(),ending);}
    public void requestNewGame(boolean skipOpening){requestedScreen=()->{try{replace(new GameScreen(assets,content,world,saves,audio,skipOpening,null,this::requestMenu,this::requestEnding));}catch(RuntimeException e){reportError(e);}};}
    public void requestMenu(){requestedScreen=()->replace(createMenu(Ending.NONE));}
    private void requestEnding(Ending ending){requestedScreen=()->replace(createMenu(ending));}
    public void requestLoad(){if(!(getScreen() instanceof MenuScreen))return;try{requestLoaded(saves.load());}catch(RuntimeException e){reportError(e);}}
    private void requestLoaded(SaveStore.Loaded saved){if(!(getScreen() instanceof MenuScreen))return;requestedScreen=()->{if(!(getScreen() instanceof MenuScreen))return;try{replace(new GameScreen(assets,content,world,saves,audio,false,saved,this::requestMenu,this::requestEnding));}catch(RuntimeException e){reportError(e);}};}
    private void reportError(RuntimeException e){if(getScreen() instanceof MenuScreen m)m.error(e.getMessage());else if(getScreen() instanceof GameScreen g)g.error(e.getMessage());}
    private void replace(Screen next){Screen previous=getScreen();setScreen(next);if(previous!=null)previous.dispose();}
    @Override public void render(){super.render();Runnable command=requestedScreen;requestedScreen=null;if(command!=null)command.run();}
    @Override public void dispose(){if(getScreen()!=null)getScreen().dispose();if(assets!=null)assets.dispose();}
}
