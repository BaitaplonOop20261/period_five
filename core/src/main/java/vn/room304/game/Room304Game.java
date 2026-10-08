package vn.room304.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import vn.room304.game.ui.OpeningScreen;

public class Room304Game extends Game {
    private enum Destination { GAME, MENU }

    private Destination requestedScreen;

    @Override
    public void create() {
        setScreen(createMenu());
    }

    private OpeningScreen createMenu() {
        return new OpeningScreen(() -> requestedScreen = Destination.GAME);
    }

    @Override
    public void render() {
        super.render();
        // Switch only after the current screen and its input callbacks finish.
        if (requestedScreen != null) {
            Destination destination = requestedScreen;
            requestedScreen = null;
            Screen previous = getScreen();
            Screen next = destination == Destination.GAME
                ? new GameScreen(() -> requestedScreen = Destination.MENU)
                : createMenu();
            setScreen(next);
            previous.dispose();
        }
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
