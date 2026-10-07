package vn.room304.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import java.util.List;

public class Player implements Disposable {

    /**
     * Map scale factor (150x100 pixel art scaled x6 = 900x600).
     * Sprite frame is 32x32 pixels; scaled x6 = 192x192 world units,
     * ensuring 1 sprite pixel equals exactly 1 map pixel (1:1 pixel scale).
     */
    public static final float SPRITE_SCALE = 6f;
    public static final float SPRITE_WIDTH = 32f * SPRITE_SCALE;   // 192f
    public static final float SPRITE_HEIGHT = 32f * SPRITE_SCALE;  // 192f

    /** Feet / base hitbox used for environment collision */
    public static final float WIDTH = 72f;
    public static final float HEIGHT = 36f;
    private static final float FRAME_DURATION = 0.12f;
    private static final float SPRINT_MULTIPLIER = 1.8f;
    // Movement animations use their original cadence at this world speed.
    private static final float ANIMATION_REFERENCE_SPEED = 220f;

    private final Rectangle bounds;
    private final Rectangle nextBounds;
    private final Vector2 movement = new Vector2();

    private float speed = 280f;

    private final Texture idleSheet;
    private final Texture downRunSheet;
    private final Texture backRunSheet;
    private final Texture sideRunSheet;

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> downRunAnimation;
    private final Animation<TextureRegion> backRunAnimation;
    private final Animation<TextureRegion> sideRunAnimation;

    private float stateTime = 0f;
    private boolean flipX = false;
    private boolean moving = false;

    /** Tracks which directional animation to use while moving. */
    private Animation<TextureRegion> currentRunAnimation;

    public Player(float x, float y) {
        bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        nextBounds = new Rectangle(bounds);

        idleSheet = new Texture(Gdx.files.internal("player_idle.png"));
        idleAnimation = buildAnimation(idleSheet);

        downRunSheet = new Texture(Gdx.files.internal("player_down_run.png"));
        downRunAnimation = buildAnimation(downRunSheet);

        backRunSheet = new Texture(Gdx.files.internal("player_back_run.png"));
        backRunAnimation = buildAnimation(backRunSheet);

        sideRunSheet = new Texture(Gdx.files.internal("player_side_run.png"));
        sideRunAnimation = buildAnimation(sideRunSheet);

        currentRunAnimation = downRunAnimation;
    }

    /**
     * Splits a horizontal sprite sheet (1 row, N columns of 32px-wide frames)
     * into a looping Animation.
     */
    private static Animation<TextureRegion> buildAnimation(Texture sheet) {
        int frameSize = sheet.getHeight(); // 32px
        int cols = sheet.getWidth() / frameSize;
        TextureRegion[][] grid = TextureRegion.split(sheet, frameSize, frameSize);
        TextureRegion[] frames = new TextureRegion[cols];
        for (int i = 0; i < cols; i++) {
            frames[i] = grid[0][i];
        }
        Animation<TextureRegion> anim = new Animation<>(FRAME_DURATION, frames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    private boolean isColliding(float newX, float newY, List<Wall> walls, List<Furniture> furniture, List<Npc> npcs) {
        nextBounds.set(bounds);
        nextBounds.setPosition(newX, newY);

        for (Wall wall : walls) {
            if (nextBounds.overlaps(wall.getBounds())) {
                return true;
            }
        }

        if (furniture != null) {
            for (Furniture f : furniture) {
                if (f.getCollisionBounds() != null && nextBounds.overlaps(f.getCollisionBounds())) {
                    return true;
                }
            }
        }

        if (npcs != null) {
            for (Npc npc : npcs) {
                if (nextBounds.overlaps(npc.getBounds())) {
                    return true;
                }
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

    public float getSpriteWidth() {
        return SPRITE_WIDTH;
    }

    public float getSpriteHeight() {
        return SPRITE_HEIGHT;
    }

    public void update(float delta, List<Wall> walls, List<Npc> npcs) {
        update(delta, walls, null, npcs);
    }

    public void update(float delta, List<Wall> walls, List<Furniture> furniture, List<Npc> npcs) {
        update(delta, walls, furniture, npcs, WorldMap.WIDTH, WorldMap.HEIGHT);
    }

    public void update(float delta, List<Wall> walls, List<Furniture> furniture, List<Npc> npcs,
                       float worldWidth, float worldHeight) {
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

        moving = !movement.isZero();
        boolean sprinting = moving && (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT));
        float speedMultiplier = sprinting ? SPRINT_MULTIPLIER : 1f;
        float animationMultiplier = moving ? speed * speedMultiplier / ANIMATION_REFERENCE_SPEED : 1f;
        stateTime += delta * animationMultiplier;

        if (moving) {
            updateDirection();
            movement.nor().scl(speed * speedMultiplier * delta);

            float newX = bounds.x + movement.x;
            if (!isColliding(newX, bounds.y, walls, furniture, npcs)) {
                bounds.x = newX;
            }

            float newY = bounds.y + movement.y;
            if (!isColliding(bounds.x, newY, walls, furniture, npcs)) {
                bounds.y = newY;
            }
        }

        if (bounds.x < 24f) {
            bounds.x = 24f;
        }
        if (bounds.x + bounds.width > worldWidth - 24f) {
            bounds.x = worldWidth - 24f - bounds.width;
        }
        if (bounds.y < 18f) {
            bounds.y = 18f;
        }
        if (bounds.y + bounds.height > worldHeight - 18f) {
            bounds.y = worldHeight - 18f - bounds.height;
        }
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }

    /**
     * Picks the correct run animation and flip state based on raw input direction.
     * Horizontal input takes priority over vertical for animation selection.
     */
    private void updateDirection() {
        if (movement.x > 0) {
            currentRunAnimation = sideRunAnimation;
            flipX = false;
        } else if (movement.x < 0) {
            currentRunAnimation = sideRunAnimation;
            flipX = true;
        } else if (movement.y > 0) {
            currentRunAnimation = backRunAnimation;
        } else if (movement.y < 0) {
            currentRunAnimation = downRunAnimation;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame;
        if (moving) {
            frame = currentRunAnimation.getKeyFrame(stateTime, true);
        } else {
            frame = idleAnimation.getKeyFrame(stateTime, true);
        }

        // Center the 192x192 sprite horizontally over the feet hitbox
        float drawX = bounds.x - (SPRITE_WIDTH - bounds.width) / 2f;
        float drawY = bounds.y;
        float drawWidth = SPRITE_WIDTH;

        if (flipX) {
            drawX += drawWidth;
            drawWidth = -drawWidth;
        }

        batch.draw(frame, drawX, drawY, drawWidth, SPRITE_HEIGHT);
    }

    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void dispose() {
        idleSheet.dispose();
        downRunSheet.dispose();
        backRunSheet.dispose();
        sideRunSheet.dispose();
    }
}
