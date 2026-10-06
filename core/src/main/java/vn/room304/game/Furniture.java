package vn.room304.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

/**
 * A static piece of furniture in the world with a texture and collision bounds.
 * The collision bounds can be a subset of the visual bounds (e.g. only the base).
 */
public class Furniture implements Disposable {

    private final Texture texture;
    private final Rectangle drawBounds;
    private final Rectangle collisionBounds;

    /**
     * @param texturePath   Internal path to the furniture sprite.
     * @param drawX         World X where the sprite is drawn.
     * @param drawY         World Y where the sprite is drawn.
     * @param drawWidth     Visual width in world units.
     * @param drawHeight    Visual height in world units.
     * @param collisionRect Collision rectangle in world coordinates, or null for no collision.
     */
    public Furniture(String texturePath, float drawX, float drawY,
                     float drawWidth, float drawHeight, Rectangle collisionRect) {
        this.texture = new Texture(texturePath);
        this.drawBounds = new Rectangle(drawX, drawY, drawWidth, drawHeight);
        this.collisionBounds = collisionRect;
    }

    public Rectangle getCollisionBounds() {
        return collisionBounds;
    }

    public Rectangle getDrawBounds() {
        return drawBounds;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
