package vn.room304.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import vn.room304.game.dialogue.DialogueController;
import vn.room304.game.ui.DialogueView;

public class GameScreen implements Screen {

    private ShapeRenderer shapeRenderer;
    private Player player;
    private WorldMap worldMap;
    private NpcInteractionSystem npcInteractionSystem;
    private DialogueController dialogueController;
    private DialogueView dialogueView;
    private Viewport viewport;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        worldMap = new WorldMap();
        player = new Player(100, 100);
        dialogueController = new DialogueController();
        dialogueView = new DialogueView();
        npcInteractionSystem = new NpcInteractionSystem(dialogueController);
        viewport = new FitViewport(800, 450, new OrthographicCamera());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);

        if (dialogueController.isActive()) {
            dialogueController.update();
        } else {
            player.update(delta, worldMap.getWalls(), worldMap.getNpcs());
            npcInteractionSystem.update(player, worldMap.getNpcs());
        }

        viewport.getCamera().position.set(
            player.getX() + 25,
            player.getY() + 25,
            0
        );

        viewport.getCamera().update();

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        worldMap.render(shapeRenderer);

        shapeRenderer.setColor(1, 1, 1, 1);
        player.render(shapeRenderer);

        shapeRenderer.end();

        dialogueView.render(dialogueController);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        dialogueView.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        dialogueView.dispose();
    }
}
