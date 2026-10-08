package vn.room304.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.List;
import vn.room304.game.dialogue.Dialogue;
import vn.room304.game.dialogue.DialogueController;
import vn.room304.game.dialogue.DialogueLine;
import vn.room304.game.gameplay.BackyardMap;
import vn.room304.game.gameplay.GameplayInput;
import vn.room304.game.gameplay.RoomInteractionSystem;
import vn.room304.game.gameplay.RoomInteractionSystem.Kind;
import vn.room304.game.gameplay.RoomInteractionSystem.Target;
import vn.room304.game.ui.DialogueView;
import vn.room304.game.ui.PauseMenuView;
import vn.room304.game.ui.WardrobeView;
import vn.room304.game.wardrobe.WardrobeController;

public class GameScreen implements Screen {

    // Fixed 16:9 canvas; window size changes only its uniform display scale.
    private static final float VIRTUAL_WIDTH = 960f;
    private static final float VIRTUAL_HEIGHT = VIRTUAL_WIDTH * 9f / 16f;
    // World art uses x6 scaling. Zoom 2 shows 1920x1080 world units.
    private static final float CAMERA_ZOOM = 2f;
    private static final float BACKGROUND_GRAY = 0.15f;
    private static final float FADE_SECONDS = 1f;

    private enum Scene {
        ROOM, HALLWAY, BACKYARD
    }

    private SpriteBatch spriteBatch;
    private Player player;
    private WorldMap worldMap;
    private HallwayMap hallwayMap;
    private BackyardMap backyardMap;
    private Scene scene = Scene.ROOM;
    private int activeRoom = 305;
    private RoomInteractionSystem roomInteractions;
    private DialogueController dialogueController;
    private DialogueView dialogueView;
    private WardrobeController wardrobeController;
    private WardrobeView wardrobeView;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture interactionMark;
    private Animation<TextureRegion> interactionMarkAnimation;
    private float interactionMarkStateTime;
    private final Runnable onReturnToMenu;
    private PauseMenuView pauseMenu;
    private boolean paused;
    private boolean skipNextUpdate;
    private float fadeRemaining = FADE_SECONDS;
    private final GameplayInput input = new GameplayInput();

    public GameScreen(Runnable onReturnToMenu) {
        this.onReturnToMenu = onReturnToMenu;
    }

    @Override
    public void show() {
        if (spriteBatch == null) {
            initialize();
        }
        restoreInputProcessor();
    }

    private void initialize() {
        spriteBatch = new SpriteBatch();
        worldMap = new WorldMap(true);
        hallwayMap = new HallwayMap();
        backyardMap = new BackyardMap();
        player = new Player(WorldMap.EXIT_CENTER_X - Player.WIDTH / 2f, 24f);
        dialogueController = new DialogueController();
        dialogueView = new DialogueView();
        roomInteractions = new RoomInteractionSystem(worldMap);
        wardrobeController = new WardrobeController();
        wardrobeView = new WardrobeView(wardrobeController);
        pauseMenu = new PauseMenuView(() -> setPaused(false), onReturnToMenu);
        interactionMark = new Texture("interaction_mark.png");
        TextureRegion[][] markFrames = TextureRegion.split(interactionMark, 8, 25);
        TextureRegion[] frames = new TextureRegion[markFrames[0].length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = markFrames[0][i];
        }
        interactionMarkAnimation = new Animation<>(0.20f, frames);
        interactionMarkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        camera = new OrthographicCamera();
        camera.zoom = CAMERA_ZOOM;
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    @Override
    public void render(float delta) {
        // Clear the whole window so letterboxing and space outside small maps match.
        ScreenUtils.clear(BACKGROUND_GRAY, BACKGROUND_GRAY, BACKGROUND_GRAY, 1f);

        input.capture(Gdx.input);
        boolean transitioning = fadeRemaining > 0f;
        boolean skipUpdate = skipNextUpdate;
        skipNextUpdate = false;
        if (!transitioning && input.isPauseRequested()) {
            setPaused(!paused);
            skipUpdate = true;
        }
        if (!transitioning && !paused && !skipUpdate) {
            updateGame(delta);
        }
        renderWorld();
        dialogueView.render(dialogueController);
        wardrobeView.render();
        if (transitioning) {
            pauseMenu.renderFade(fadeRemaining / FADE_SECONDS);
            fadeRemaining = Math.max(0f, fadeRemaining - delta);
        } else if (paused) {
            pauseMenu.render(delta);
        }
    }

    private void setPaused(boolean paused) {
        this.paused = paused;
        skipNextUpdate = true;
        if (paused) wardrobeView.getStage().cancelTouchFocus();
        restoreInputProcessor();
    }

    private void restoreInputProcessor() {
        InputProcessor processor = paused ? pauseMenu.getStage()
            : wardrobeController.isOpen() ? wardrobeView.getStage() : null;
        if (Gdx.input.getInputProcessor() != processor) {
            Gdx.input.setInputProcessor(processor);
        }
    }

    private void updateGame(float delta) {
        interactionMarkStateTime += delta;

        if (scene == Scene.ROOM) {
            worldMap.update(delta);
        }

        Scene previousScene = scene;
        boolean wardrobeCloseRequested = wardrobeView.consumeCloseRequest();
        boolean movementWasAllowed = !dialogueController.isActive() && !wardrobeController.isOpen();
        if (dialogueController.isActive()) {
            if (input.isDialogueAdvanceRequested()) {
                dialogueController.advance();
            }
        } else if (wardrobeController.isOpen()) {
            if (input.isWardrobeCloseRequested() || wardrobeCloseRequested) {
                wardrobeController.close();
            }
        } else if (input.isInteractionRequested()) {
            switch (scene) {
                case ROOM -> handleRoomInteraction();
                case HALLWAY -> handleHallwayInteraction();
                case BACKYARD -> handleBackyardInteraction();
            }
        }

        // Opening and closing a modal each consume the frame's controls. Scene teleports do too.
        boolean movementAllowed = movementWasAllowed && !dialogueController.isActive()
            && !wardrobeController.isOpen() && scene == previousScene;
        updatePlayer(delta, movementAllowed);
        if (scene == Scene.ROOM) {
            roomInteractions.update(player.getBounds());
        }

        restoreInputProcessor();
    }

    private void updatePlayer(float delta, boolean movementAllowed) {
        float horizontal = movementAllowed ? input.getHorizontal() : 0f;
        float vertical = movementAllowed ? input.getVertical() : 0f;
        boolean sprinting = movementAllowed && input.isSprinting();
        switch (scene) {
            case ROOM -> player.update(delta, horizontal, vertical, sprinting,
                worldMap.getWalls(), worldMap.getFurniture(), worldMap.getNpcs(), WorldMap.WIDTH, WorldMap.HEIGHT);
            case HALLWAY -> player.update(delta, horizontal, vertical, sprinting,
                hallwayMap.getWalls(), null, List.of(), hallwayMap.getWidth(), hallwayMap.getHeight());
            case BACKYARD -> player.update(delta, horizontal, vertical, sprinting,
                backyardMap.getWalls(), null, List.of(), backyardMap.getWidth(), backyardMap.getHeight());
        }
    }

    private void renderWorld() {
        // Follow the player's sprite center in every scene, including map edges.
        camera.position.set(
            player.getX() + player.getBounds().width / 2f,
            player.getY() + player.getSpriteHeight() / 2f,
            0
        );
        viewport.apply();

        // 1. Render World layers (Background -> Furniture -> Y-Sorted NPCs & Player)
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        if (scene == Scene.HALLWAY) {
            hallwayMap.renderBackground(spriteBatch);
            player.render(spriteBatch);
            if (!dialogueController.isActive() && hallwayMap.findNearbyInteraction(player.getBounds()) != -1) {
                renderHallwayInteractionMarker();
            }
        } else if (scene == Scene.BACKYARD) {
            backyardMap.renderBackground(spriteBatch);
            player.render(spriteBatch);
            if (backyardMap.getEntranceBounds().overlaps(player.getBounds())) {
                renderPassageMarker(backyardMap.getEntranceBounds(),
                    backyardMap.getSpawnY() + Player.SPRITE_HEIGHT + 8f);
            }
        } else {
            renderRoom();
        }

        spriteBatch.end();
    }

    private void renderRoom() {
        worldMap.renderBackground(spriteBatch);
        worldMap.renderFurnitureExceptWardrobe(spriteBatch);

        boolean playerBehindWardrobe = player.getY() >= worldMap.getWardrobe().getCollisionBounds().y;
        if (!playerBehindWardrobe) {
            worldMap.renderWardrobe(spriteBatch);
        }

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

        if (playerBehindWardrobe) {
            worldMap.renderWardrobe(spriteBatch);
        }

        if (!dialogueController.isActive() && !wardrobeController.isOpen()) {
            renderInteractionMarkers();
        }
    }

    private void handleRoomInteraction() {
        roomInteractions.update(player.getBounds());
        Target target = roomInteractions.getActiveTarget();
        if (target == null) return;
        switch (target.getKind()) {
            case NPC -> dialogueController.start(target.getNpc().getDialogue());
            case WARDROBE -> wardrobeController.open();
            case EXIT -> enterHallway();
        }
    }

    private void renderInteractionMarkers() {
        Target target = roomInteractions.getActiveTarget();
        if (target == null) return;
        // The game uses 6x pixel scaling; preserve that scale for the marker.
        final float markerWidth = 8f * Npc.SPRITE_SCALE;
        final float markerHeight = 25f * Npc.SPRITE_SCALE;
        TextureRegion markerFrame = interactionMarkAnimation.getKeyFrame(interactionMarkStateTime);

        if (target.getKind() == Kind.NPC) {
            Rectangle bounds = target.getBounds();
            spriteBatch.draw(markerFrame,
                bounds.x + (bounds.width - markerWidth) / 2f,
                bounds.y + Npc.SPRITE_HEIGHT - 8f,
                markerWidth, markerHeight);
        }

        if (target.getKind() == Kind.WARDROBE) {
            Rectangle bounds = target.getBounds();
            spriteBatch.draw(markerFrame,
                bounds.x + (bounds.width - markerWidth) / 2f,
                bounds.y + bounds.height + 8f,
                markerWidth, markerHeight);
        }

        Rectangle exit = worldMap.getExitBounds();
        if (target.getKind() == Kind.EXIT) {
            spriteBatch.draw(markerFrame, WorldMap.EXIT_CENTER_X - markerWidth / 2f,
                exit.y + exit.height - 8f, markerWidth, markerHeight);
        }
    }

    private void renderHallwayInteractionMarker() {
        int roomNumber = hallwayMap.findNearbyInteraction(player.getBounds());
        if (roomNumber == HallwayMap.BACKYARD_EXIT) {
            Rectangle exit = hallwayMap.getBackyardExitBounds();
            renderPassageMarker(exit, exit.y + exit.height + 8f);
            return;
        }
        float markerWidth = 8f * Npc.SPRITE_SCALE;
        float markerHeight = 25f * Npc.SPRITE_SCALE;
        TextureRegion markerFrame = interactionMarkAnimation.getKeyFrame(interactionMarkStateTime);
        spriteBatch.draw(markerFrame, hallwayMap.getDoorCenterX(roomNumber) - markerWidth / 2f,
            hallwayMap.getDoorTopY() - Player.SPRITE_SCALE, markerWidth, markerHeight);
    }

    private void renderPassageMarker(Rectangle passage, float y) {
        float markerWidth = 8f * Npc.SPRITE_SCALE;
        float markerHeight = 25f * Npc.SPRITE_SCALE;
        TextureRegion markerFrame = interactionMarkAnimation.getKeyFrame(interactionMarkStateTime);
        spriteBatch.draw(markerFrame, passage.x + (passage.width - markerWidth) / 2f,
            y, markerWidth, markerHeight);
    }

    private void handleHallwayInteraction() {
        int roomNumber = hallwayMap.findNearbyInteraction(player.getBounds());
        if (roomNumber == HallwayMap.BACKYARD_EXIT) {
            scene = Scene.BACKYARD;
            player.setPosition(backyardMap.getSpawnX(), backyardMap.getSpawnY());
        } else if (roomNumber == 304) {
            dialogueController.start(new Dialogue("locked_room_door",
                List.of(new DialogueLine("", "Cửa bị khóa rồi", null))));
        } else if (roomNumber == 305 || roomNumber == 306) {
            activeRoom = roomNumber;
            scene = Scene.ROOM;
            worldMap.dispose();
            worldMap = new WorldMap(activeRoom == 305);
            roomInteractions = new RoomInteractionSystem(worldMap);
            player.setPosition(WorldMap.EXIT_CENTER_X - Player.WIDTH / 2f, 24f);
            roomInteractions.update(player.getBounds());
        }
    }

    private void enterHallway() {
        scene = Scene.HALLWAY;
        player.setPosition(hallwayMap.getDoorCenterX(activeRoom) - Player.WIDTH / 2f,
            hallwayMap.getDoorStandY());
    }

    private void handleBackyardInteraction() {
        if (backyardMap.getEntranceBounds().overlaps(player.getBounds())) {
            scene = Scene.HALLWAY;
            Rectangle exit = hallwayMap.getBackyardExitBounds();
            player.setPosition(exit.x + (exit.width - Player.WIDTH) / 2f,
                hallwayMap.getDoorStandY());
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        dialogueView.resize(width, height);
        wardrobeView.resize(width, height);
        pauseMenu.resize(width, height);
    }

    @Override
    public void pause() {
        setPaused(true);
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        if (wardrobeView != null && (Gdx.input.getInputProcessor() == wardrobeView.getStage()
            || Gdx.input.getInputProcessor() == pauseMenu.getStage())) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        hide();
        spriteBatch.dispose();
        worldMap.dispose();
        hallwayMap.dispose();
        backyardMap.dispose();
        player.dispose();
        interactionMark.dispose();
        dialogueView.dispose();
        wardrobeView.dispose();
        pauseMenu.dispose();
    }
}
