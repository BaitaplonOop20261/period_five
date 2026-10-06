package vn.room304.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import vn.room304.game.dialogue.DialogueController;
import vn.room304.game.ui.DialogueView;
import vn.room304.game.ui.WardrobeView;
import vn.room304.game.wardrobe.WardrobeController;

public class GameScreen implements Screen {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private Player player;
    private WorldMap worldMap;
    private NpcInteractionSystem npcInteractionSystem;
    private DialogueController dialogueController;
    private DialogueView dialogueView;
    private WardrobeController wardrobeController;
    private WardrobeView wardrobeView;
    private Viewport viewport;
    private Texture interactionMark;
    private Animation<TextureRegion> interactionMarkAnimation;
    private float interactionMarkStateTime;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        worldMap = new WorldMap();
        player = new Player(350f, 120f);
        dialogueController = new DialogueController();
        dialogueView = new DialogueView();
        npcInteractionSystem = new NpcInteractionSystem(dialogueController);
        wardrobeController = new WardrobeController();
        wardrobeView = new WardrobeView();
        interactionMark = new Texture("interaction_mark.png");
        TextureRegion[][] markFrames = TextureRegion.split(interactionMark, 8, 25);
        TextureRegion[] frames = new TextureRegion[markFrames[0].length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = markFrames[0][i];
        }
        interactionMarkAnimation = new Animation<>(0.20f, frames);
        interactionMarkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        viewport = new FitViewport(800, 450, new OrthographicCamera());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.08f, 0.08f, 1f);

        // Update world animations (e.g. NPC Trang idle)
        worldMap.update(delta);
        interactionMarkStateTime += delta;

        if (dialogueController.isActive()) {
            dialogueController.update();
        } else if (wardrobeController.isOpen()) {
            wardrobeController.update();
        } else {
            player.update(delta, worldMap.getWalls(), worldMap.getFurniture(), worldMap.getNpcs());
            npcInteractionSystem.update(player, worldMap.getNpcs());

            // Check wardrobe interaction: press F when near wardrobe to open
            if (worldMap.getWardrobe() != null && worldMap.getWardrobe().canInteract(player.getBounds())) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                    wardrobeController.open();
                    Gdx.input.setInputProcessor(wardrobeView.getStage());
                }
            }
        }

        // Restore input processor when wardrobe closes
        if (!wardrobeController.isOpen() && Gdx.input.getInputProcessor() == wardrobeView.getStage()) {
            Gdx.input.setInputProcessor(null);
        }

        // Camera smoothly follows player within bedroom world bounds
        float halfVw = viewport.getWorldWidth() / 2f;
        float halfVh = viewport.getWorldHeight() / 2f;
        float targetCamX = player.getX() + player.getBounds().width / 2f;
        float targetCamY = player.getY() + player.getSpriteHeight() / 2f;

        float minX = halfVw;
        float maxX = Math.max(halfVw, WorldMap.WIDTH - halfVw);
        float minY = halfVh;
        float maxY = Math.max(halfVh, WorldMap.HEIGHT - halfVh);

        viewport.getCamera().position.set(
            MathUtils.clamp(targetCamX, minX, maxX),
            MathUtils.clamp(targetCamY, minY, maxY),
            0
        );
        viewport.getCamera().update();

        // 1. Render World layers (Background -> Furniture -> Y-Sorted NPCs & Player)
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        worldMap.renderBackground(spriteBatch);
        worldMap.renderFurniture(spriteBatch);

        // Y-sort between Player and NPCs for realistic depth
        boolean playerDrawn = false;
        for (Npc npc : worldMap.getNpcs()) {
            if (!playerDrawn && player.getY() >= npc.getY()) {
                player.render(spriteBatch);
                playerDrawn = true;
            }
            npc.render(spriteBatch);
        }
        if (!playerDrawn) {
            player.render(spriteBatch);
        }

        if (!dialogueController.isActive() && !wardrobeController.isOpen()) {
            renderInteractionMarkers();
        }

        spriteBatch.end();

        // 2. Render UI layers
        dialogueView.render(dialogueController);
        wardrobeView.render(wardrobeController);
    }

    private void renderInteractionMarkers() {
        // The game uses 6x pixel scaling; preserve that scale for the marker.
        final float markerWidth = 8f * Npc.SPRITE_SCALE;
        final float markerHeight = 25f * Npc.SPRITE_SCALE;
        TextureRegion markerFrame = interactionMarkAnimation.getKeyFrame(interactionMarkStateTime);

        Npc activeNpc = npcInteractionSystem.getActiveNpc();
        if (activeNpc != null) {
            Rectangle bounds = activeNpc.getBounds();
            spriteBatch.draw(markerFrame,
                bounds.x + (bounds.width - markerWidth) / 2f,
                bounds.y + Npc.SPRITE_HEIGHT - 8f,
                markerWidth, markerHeight);
        }

        Wardrobe wardrobe = worldMap.getWardrobe();
        if (wardrobe != null && wardrobe.canInteract(player.getBounds())) {
            Rectangle bounds = wardrobe.getCollisionBounds() != null
                ? wardrobe.getCollisionBounds()
                : wardrobe.getDrawBounds();
            spriteBatch.draw(markerFrame,
                bounds.x + (bounds.width - markerWidth) / 2f,
                bounds.y + bounds.height + 8f,
                markerWidth, markerHeight);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        dialogueView.resize(width, height);
        wardrobeView.resize(width, height);
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
        spriteBatch.dispose();
        worldMap.dispose();
        player.dispose();
        interactionMark.dispose();
        dialogueView.dispose();
        wardrobeView.dispose();
    }
}
