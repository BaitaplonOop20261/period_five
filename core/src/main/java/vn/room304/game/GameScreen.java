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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.List;
import vn.room304.game.dialogue.Dialogue;
import vn.room304.game.dialogue.DialogueController;
import vn.room304.game.dialogue.DialogueLine;
import vn.room304.game.gameplay.BackyardMap;
import vn.room304.game.ui.DialogueView;
import vn.room304.game.ui.WardrobeView;
import vn.room304.game.wardrobe.WardrobeController;

public class GameScreen implements Screen {

    // Fixed 16:9 canvas; window size changes only its uniform display scale.
    private static final float VIRTUAL_WIDTH = 960f;
    private static final float VIRTUAL_HEIGHT = VIRTUAL_WIDTH * 9f / 16f;
    // World art uses x6 scaling. Zoom 2 shows 1920x1080 world units.
    private static final float CAMERA_ZOOM = 2f;
    private static final float BACKGROUND_GRAY = 0.15f;

    private enum RoomInteraction {
        NONE, NPC, WARDROBE, EXIT
    }

    private enum Scene {
        ROOM, HALLWAY, BACKYARD
    }

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private Player player;
    private WorldMap worldMap;
    private HallwayMap hallwayMap;
    private BackyardMap backyardMap;
    private Scene scene = Scene.ROOM;
    private int activeRoom = 305;
    private NpcInteractionSystem npcInteractionSystem;
    private DialogueController dialogueController;
    private DialogueView dialogueView;
    private WardrobeController wardrobeController;
    private WardrobeView wardrobeView;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture interactionMark;
    private Animation<TextureRegion> interactionMarkAnimation;
    private float interactionMarkStateTime;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        worldMap = new WorldMap(true);
        hallwayMap = new HallwayMap();
        backyardMap = new BackyardMap();
        player = new Player(WorldMap.EXIT_CENTER_X - Player.WIDTH / 2f, 24f);
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
        camera = new OrthographicCamera();
        camera.zoom = CAMERA_ZOOM;
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    @Override
    public void render(float delta) {
        // Clear the whole window so letterboxing and space outside small maps match.
        ScreenUtils.clear(BACKGROUND_GRAY, BACKGROUND_GRAY, BACKGROUND_GRAY, 1f);

        interactionMarkStateTime += delta;

        if (scene == Scene.ROOM) {
            worldMap.update(delta);
        }

        if (dialogueController.isActive()) {
            dialogueController.update();
            if (scene == Scene.ROOM) {
                player.update(delta, worldMap.getWalls(), worldMap.getFurniture(), worldMap.getNpcs());
                npcInteractionSystem.update(player, worldMap.getNpcs());
            }
        } else if (wardrobeController.isOpen()) {
            wardrobeController.update();
        } else if (scene == Scene.HALLWAY) {
            updateHallway(delta);
        } else if (scene == Scene.BACKYARD) {
            updateBackyard(delta);
        } else {
            player.update(delta, worldMap.getWalls(), worldMap.getFurniture(), worldMap.getNpcs());
            npcInteractionSystem.update(player, worldMap.getNpcs());
            handleRoomInteraction();
        }

        // Restore input processor when wardrobe closes
        if (!wardrobeController.isOpen() && Gdx.input.getInputProcessor() == wardrobeView.getStage()) {
            Gdx.input.setInputProcessor(null);
        }

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

        spriteBatch.end();

        // 2. Render UI layers
        dialogueView.render(dialogueController);
        wardrobeView.render(wardrobeController);
    }

    private RoomInteraction getRoomInteractionTarget() {
        // Preserve the existing priority for overlapping interaction ranges.
        if (npcInteractionSystem.getActiveNpc() != null) {
            return RoomInteraction.NPC;
        }
        Wardrobe wardrobe = worldMap.getWardrobe();
        if (wardrobe != null && wardrobe.canInteract(player.getBounds())) {
            return RoomInteraction.WARDROBE;
        }
        if (worldMap.getExitBounds().overlaps(player.getBounds())) {
            return RoomInteraction.EXIT;
        }
        return RoomInteraction.NONE;
    }

    private void handleRoomInteraction() {
        if (!Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            return;
        }
        switch (getRoomInteractionTarget()) {
            case NPC -> npcInteractionSystem.interact();
            case WARDROBE -> {
                wardrobeController.open();
                Gdx.input.setInputProcessor(wardrobeView.getStage());
            }
            case EXIT -> enterHallway();
            case NONE -> { }
        }
    }

    private void renderInteractionMarkers() {
        RoomInteraction target = getRoomInteractionTarget();
        // The game uses 6x pixel scaling; preserve that scale for the marker.
        final float markerWidth = 8f * Npc.SPRITE_SCALE;
        final float markerHeight = 25f * Npc.SPRITE_SCALE;
        TextureRegion markerFrame = interactionMarkAnimation.getKeyFrame(interactionMarkStateTime);

        Npc activeNpc = npcInteractionSystem.getActiveNpc();
        if (target == RoomInteraction.NPC) {
            Rectangle bounds = activeNpc.getBounds();
            spriteBatch.draw(markerFrame,
                bounds.x + (bounds.width - markerWidth) / 2f,
                bounds.y + Npc.SPRITE_HEIGHT - 8f,
                markerWidth, markerHeight);
        }

        Wardrobe wardrobe = worldMap.getWardrobe();
        if (target == RoomInteraction.WARDROBE) {
            Rectangle bounds = wardrobe.getCollisionBounds() != null
                ? wardrobe.getCollisionBounds()
                : wardrobe.getDrawBounds();
            spriteBatch.draw(markerFrame,
                bounds.x + (bounds.width - markerWidth) / 2f,
                bounds.y + bounds.height + 8f,
                markerWidth, markerHeight);
        }

        Rectangle exit = worldMap.getExitBounds();
        if (target == RoomInteraction.EXIT) {
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

    private void updateHallway(float delta) {
        player.update(delta, hallwayMap.getWalls(), null, List.of(),
            hallwayMap.getWidth(), hallwayMap.getHeight());
        if (!Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            return;
        }

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
            player.setPosition(WorldMap.EXIT_CENTER_X - Player.WIDTH / 2f, 24f);
        }
    }

    private void enterHallway() {
        scene = Scene.HALLWAY;
        player.setPosition(hallwayMap.getDoorCenterX(activeRoom) - Player.WIDTH / 2f,
            hallwayMap.getDoorStandY());
    }

    private void updateBackyard(float delta) {
        player.update(delta, backyardMap.getWalls(), null, List.of(),
            backyardMap.getWidth(), backyardMap.getHeight());
        if (backyardMap.getEntranceBounds().overlaps(player.getBounds())
            && Gdx.input.isKeyJustPressed(Input.Keys.F)) {
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
        hallwayMap.dispose();
        backyardMap.dispose();
        player.dispose();
        interactionMark.dispose();
        dialogueView.dispose();
        wardrobeView.dispose();
    }
}
