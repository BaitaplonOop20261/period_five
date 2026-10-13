package com.room304.world;

import com.room304.assets.GameAssets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.room304.dialogue.DialogueLoader;

public class WorldMap implements Disposable {

    /**
     * Bedroom dimensions: 150x100 pixel art scaled x6 = 900x600.
     * Scale 6 provides clean pixel alignment and room proportions.
     */
    public static final float WIDTH = 900f;
    public static final float HEIGHT = 600f;
    public static final float EXIT_CENTER_X = 255f;
    private static final float EXIT_INTERACTION_WIDTH = 150f;

    private final GameAssets assets;
    private final boolean ownsAssets;
    private final Texture background;
    private final List<Furniture> furniture;
    private final Wardrobe wardrobe;
    private final List<Wall> walls;
    private final List<Npc> npcs;
    private final Rectangle exitBounds = new Rectangle(
        EXIT_CENTER_X - EXIT_INTERACTION_WIDTH / 2f, 18f, EXIT_INTERACTION_WIDTH, 42f);
    private final Rectangle exitThresholdBounds = new Rectangle(
        EXIT_CENTER_X - EXIT_INTERACTION_WIDTH / 2f, 18f, EXIT_INTERACTION_WIDTH, 0f);

    public WorldMap() {
        this(true);
    }

    public WorldMap(boolean includeNpc) {
        this(null, includeNpc);
    }

    public WorldMap(GameAssets sharedAssets, boolean includeNpc) {
        ownsAssets = sharedAssets == null;
        assets = ownsAssets ? new GameAssets() : sharedAssets;
        background = assets.texture("map.demo.room");

        furniture = new ArrayList<>();
        walls = new ArrayList<>();
        npcs = new ArrayList<>();

        // 1. Room boundary walls (based on 150x100 art scaled x6)
        // Bottom border (Y = 0..18)
        walls.add(new Wall(0, 0, WIDTH, 18f));
        // Left border (X = 0..24)
        walls.add(new Wall(0, 0, 24f, HEIGHT));
        // Right border (X = 876..900)
        walls.add(new Wall(876f, 0, 24f, HEIGHT));
        // Upper wooden wall with windows (Y = 384..600)
        walls.add(new Wall(0, 384f, WIDTH, 216f));

        // 2. Wardrobe: 150x100 layer drawn at (0, 0, WIDTH, HEIGHT)
        // In image: X=55..92 (width 38 -> 228), Y=3..54 (LibGDX Y=276..582)
        Rectangle wardrobeCollision = new Rectangle(330f, 276f, 228f, 306f);
        wardrobe = new Wardrobe(assets, "prop.demo.wardrobe", 0, 0, WIDTH, HEIGHT, wardrobeCollision, 50f);
        furniture.add(wardrobe);

        // 3. Bed: 150x100 layer drawn at (0, 0, WIDTH, HEIGHT)
        // In image: X=122..146 (width 25 -> 150), Y=23..66 (LibGDX Y=204..462)
        Rectangle bedCollision = new Rectangle(732f, 204f, 150f, 258f);
        Furniture bed = new Furniture(assets, "prop.demo.bed", 0, 0, WIDTH, HEIGHT, bedCollision);
        furniture.add(bed);

        // 4. NPC Trang with animated idle sprite sheet (standing on rug at left)
        if (includeNpc) {
            for (NpcDefinition d : NpcDefinition.load(com.badlogic.gdx.Gdx.files.internal(assets.catalog().path("npc.demo.config")), assets.catalog())) {
                Rectangle feet = new Rectangle(d.x(), d.y(), Npc.HITBOX_WIDTH, Npc.HITBOX_HEIGHT);
                if (feet.x < 0 || feet.y < 0 || feet.x + feet.width > WIDTH || feet.y + feet.height > HEIGHT
                    || walls.stream().anyMatch(w -> w.getBounds().overlaps(feet))
                    || furniture.stream().anyMatch(f -> f.getCollisionBounds() != null && f.getCollisionBounds().overlaps(feet))
                    || npcs.stream().anyMatch(n -> n.getBounds().overlaps(feet)))
                    throw new IllegalArgumentException("Invalid NPC placement " + d.id() + " in " + assets.catalog().path("npc.demo.config"));
                npcs.add(new Npc(assets, d.id(), d.name(), DialogueLoader.load(assets.catalog().path(d.dialogue())),
                    d.animation(), d.x(), d.y(), d.interactionRange()));
            }
            npcs.sort(java.util.Comparator.comparing(Npc::getY).reversed());
        }
    }

    public Rectangle getExitBounds() {
        return exitBounds;
    }

    /** Actual doorway line used for distance; the larger exit bounds only limit reach. */
    public Rectangle getExitThresholdBounds() {
        return exitThresholdBounds;
    }

    public List<Wall> getWalls() {
        return Collections.unmodifiableList(walls);
    }

    public List<Furniture> getFurniture() {
        return Collections.unmodifiableList(furniture);
    }

    public Wardrobe getWardrobe() {
        return wardrobe;
    }

    public List<Npc> getNpcs() {
        return Collections.unmodifiableList(npcs);
    }

    public void update(float delta) {
        for (Npc npc : npcs) {
            npc.update(delta);
        }
    }

    public void renderBackground(SpriteBatch batch) {
        batch.draw(background, 0, 0, WIDTH, HEIGHT);
    }

    public void renderFurnitureExceptWardrobe(SpriteBatch batch) {
        for (Furniture f : furniture) {
            if (f != wardrobe) {
                f.render(batch);
            }
        }
    }

    public void renderWardrobe(SpriteBatch batch) {
        wardrobe.render(batch);
    }

    public void renderNpcs(SpriteBatch batch) {
        for (Npc npc : npcs) {
            npc.render(batch);
        }
    }

    @Override
    public void dispose() {
        for (Furniture f : furniture) {
            f.dispose();
        }
        for (Npc npc : npcs) {
            npc.dispose();
        }
        if (ownsAssets) assets.dispose();
    }
}
