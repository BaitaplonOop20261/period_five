package vn.room304.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Wall {

    private final Rectangle bounds;

    public Wall(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public float getWidth() {
        return bounds.width;
    }

    public float getHeight() {
        return bounds.height;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 1f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
