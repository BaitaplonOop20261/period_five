package vn.room304.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import vn.room304.game.dialogue.Dialogue;

public class WorldMap {

    public static final float WIDTH = 1600f;
    public static final float HEIGHT = 900f;

    private final List<Wall> walls;
    private final List<Npc> npcs;

    public WorldMap() {
        walls = new ArrayList<>();
        npcs = new ArrayList<>();

        walls.add(new Wall(200, 200, 400, 30));
        walls.add(new Wall(200, 500, 30, 300));
        walls.add(new Wall(800, 200, 400, 30));

        npcs.add(new Npc(
            "Trang",
            new Dialogue(List.of("Chao mung ban den Room304.")),
            350,
            320,
            45,
            55,
            80
        ));
    }

    public List<Wall> getWalls() {
        return Collections.unmodifiableList(walls);
    }

    public List<Npc> getNpcs() {
        return Collections.unmodifiableList(npcs);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(0, 0, WIDTH, HEIGHT);

        for (Wall wall : walls) {
            wall.render(shapeRenderer);
        }

        for (Npc npc : npcs) {
            npc.render(shapeRenderer);
        }
    }
}
