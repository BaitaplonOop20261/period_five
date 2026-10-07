package vn.room304.game.gameplay;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import java.util.List;
import vn.room304.game.Player;
import vn.room304.game.Wall;

/** Backyard background, ground collision and entrance back to the hallway. */
public class BackyardMap implements Disposable {

    // Door art is twice the hallway's pixel size; halve its render scale to match.
    private static final float SCALE = Player.SPRITE_SCALE / 2f;
    private static final float GROUND_TOP_IN_IMAGE = 241f;
    private static final float DOOR_CENTER_X_IN_IMAGE = 635f;

    private final Texture background;
    private final float width;
    private final float height;
    private final float groundTop;
    private final Rectangle entranceBounds;
    private final List<Wall> walls;

    public BackyardMap() {
        background = new Texture("back_yard.png");
        width = background.getWidth() * SCALE;
        height = background.getHeight() * SCALE;
        groundTop = (background.getHeight() - GROUND_TOP_IN_IMAGE) * SCALE;
        entranceBounds = new Rectangle((DOOR_CENTER_X_IN_IMAGE - 26f) * SCALE,
            groundTop - 12f * SCALE, 52f * SCALE, 12f * SCALE);
        walls = List.of(
            new Wall(0f, 0f, width, 18f),
            new Wall(0f, groundTop, width, height - groundTop),
            new Wall(0f, 18f, 24f, groundTop - 18f),
            new Wall(width - 24f, 18f, 24f, groundTop - 18f)
        );
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Rectangle getEntranceBounds() {
        return entranceBounds;
    }

    public float getSpawnX() {
        return DOOR_CENTER_X_IN_IMAGE * SCALE - Player.WIDTH / 2f;
    }

    public float getSpawnY() {
        return groundTop - Player.HEIGHT - 2f * SCALE;
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
