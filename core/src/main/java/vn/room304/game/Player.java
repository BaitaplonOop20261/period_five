package vn.room304.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Player {

    private static final float WIDTH = 50f;
    private static final float HEIGHT = 50f;

    private final Rectangle bounds;
    private final Rectangle nextBounds;
    private final Vector2 movement = new Vector2();

    private float speed = 200f;

    public Player(float x, float y) {
        bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        nextBounds = new Rectangle(bounds);
    }

    private boolean isColliding(float newX, float newY, List<Wall> walls, List<Npc> npcs) {
        nextBounds.set(bounds);
        nextBounds.setPosition(newX, newY);

        for (Wall wall : walls) {
            if (nextBounds.overlaps(wall.getBounds())) {
                return true;
            }
        }

        for (Npc npc : npcs) {
            if (nextBounds.overlaps(npc.getBounds())) {
                return true;
            }
        }

        return false;
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void update(float delta, List<Wall> walls, List<Npc> npcs) {
        movement.set(0f, 0f);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.y += 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            movement.y -= 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movement.x -= 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movement.x += 1f;
        }

        if (!movement.isZero()) {
            movement.nor().scl(speed * delta);

            float newX = bounds.x + movement.x;
            if (!isColliding(newX, bounds.y, walls, npcs)) {
                bounds.x = newX;
            }

            float newY = bounds.y + movement.y;
            if (!isColliding(bounds.x, newY, walls, npcs)) {
                bounds.y = newY;
            }
        }

        if (bounds.x < 0) {
            bounds.x = 0;
        }
        if (bounds.x + bounds.width > WorldMap.WIDTH) {
            bounds.x = WorldMap.WIDTH - bounds.width;
        }
        if (bounds.y < 0) {
            bounds.y = 0;
        }
        if (bounds.y + bounds.height > WorldMap.HEIGHT) {
            bounds.y = WorldMap.HEIGHT - bounds.height;
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
