package vn.room304.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Hallway scene and the three room door interaction zones. */
public class HallwayMap implements Disposable {

    private static final float SCALE = Player.SPRITE_SCALE;
    // Image coordinates start at the top; the floor begins below row 88.
    private static final float FLOOR_TOP_IN_IMAGE = 89f;
    private static final float DOOR_TOP_IN_IMAGE = 32f;
    private static final float DOOR_WIDTH = 40f * SCALE;
    private static final float DOOR_REACH = 12f * SCALE;

    private final Texture background;
    private final float width;
    private final float height;
    private final float floorTop;
    private final Map<Integer, Rectangle> doors = new LinkedHashMap<>();
    private final List<Wall> walls;

    public HallwayMap() {
        background = new Texture("hall_background.png");
        width = background.getWidth() * SCALE;
        height = background.getHeight() * SCALE;
        floorTop = (background.getHeight() - FLOOR_TOP_IN_IMAGE) * SCALE;

        addDoor(306, 118f);
        addDoor(305, 352f);
        addDoor(304, 574f);
        walls = List.of(
            new Wall(0f, 0f, width, 18f),
            new Wall(0f, floorTop, width, height - floorTop),
            new Wall(0f, 18f, 24f, floorTop - 18f),
            new Wall(width - 24f, 18f, 24f, floorTop - 18f)
        );
    }

    private void addDoor(int roomNumber, float centerXInImage) {
        float centerX = centerXInImage * SCALE;
        doors.put(roomNumber, new Rectangle(centerX - DOOR_WIDTH / 2f,
            floorTop - DOOR_REACH, DOOR_WIDTH, DOOR_REACH));
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getDoorCenterX(int roomNumber) {
        Rectangle door = doors.get(roomNumber);
        return door == null ? width / 2f : door.x + door.width / 2f;
    }

    public Rectangle getDoorBounds(int roomNumber) {
        return doors.get(roomNumber);
    }

    public float getDoorStandY() {
        return floorTop - Player.HEIGHT - 2f * SCALE;
    }

    public float getDoorTopY() {
        return height - DOOR_TOP_IN_IMAGE * SCALE;
    }

    public int findNearbyDoor(Rectangle playerBounds) {
        for (Map.Entry<Integer, Rectangle> door : doors.entrySet()) {
            if (door.getValue().overlaps(playerBounds)) {
                return door.getKey();
            }
        }
        return -1;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public void renderBackground(SpriteBatch batch) {
        batch.draw(background, 0f, 0f, width, height);
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
