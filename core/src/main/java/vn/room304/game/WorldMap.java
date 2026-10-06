package vn.room304.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import vn.room304.game.dialogue.Dialogue;

public class WorldMap implements Disposable {

    /**
     * Bedroom dimensions: 150x100 pixel art scaled x6 = 900x600.
     * Scale 6 provides clean pixel alignment and room proportions.
     */
    public static final float WIDTH = 900f;
    public static final float HEIGHT = 600f;

    private final Texture background;
    private final List<Furniture> furniture;
    private final Wardrobe wardrobe;
    private final List<Wall> walls;
    private final List<Npc> npcs;

    public WorldMap() {
        background = new Texture("bedroom_background.png");

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
        wardrobe = new Wardrobe("wardrobe.png", 0, 0, WIDTH, HEIGHT, wardrobeCollision, 50f);
        furniture.add(wardrobe);

        // 3. Bed: 150x100 layer drawn at (0, 0, WIDTH, HEIGHT)
        // In image: X=122..146 (width 25 -> 150), Y=23..66 (LibGDX Y=204..462)
        Rectangle bedCollision = new Rectangle(732f, 204f, 150f, 258f);
        Furniture bed = new Furniture("bed.png", 0, 0, WIDTH, HEIGHT, bedCollision);
        furniture.add(bed);

        // 4. NPC Trang with animated idle sprite sheet (standing on rug at left)
        npcs.add(new Npc(
            "Trang",
            new Dialogue(List.of("Chao mung ban den Room304.")),
            "npc_trang_idle.png",
            "npc_trang_dialogueUI.png",
            180f, 120f,
            60f
        ));
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

    public void renderFurniture(SpriteBatch batch) {
        for (Furniture f : furniture) {
            f.render(batch);
        }
    }

    public void renderNpcs(SpriteBatch batch) {
        for (Npc npc : npcs) {
            npc.render(batch);
        }
    }

    @Override
    public void dispose() {
        background.dispose();
        for (Furniture f : furniture) {
            f.dispose();
        }
        for (Npc npc : npcs) {
            npc.dispose();
        }
    }
}
