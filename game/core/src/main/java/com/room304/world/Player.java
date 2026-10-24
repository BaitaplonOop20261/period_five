package com.room304.world;

import com.room304.assets.GameAssets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
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

    /** 18x10 sprite pixels cover the feet and lower body without blocking at the head. */
    public static final float WIDTH = 18f * SPRITE_SCALE;
    public static final float HEIGHT = 10f * SPRITE_SCALE;
    private static final float SPRINT_MULTIPLIER = 1.8f;
    // Movement animations use their original cadence at this world speed.
    private static final float ANIMATION_REFERENCE_SPEED = 220f;
    private static final float MOVEMENT_SPEED = 280f;
    private static final float HORIZONTAL_MARGIN = 24f;
    private static final float VERTICAL_MARGIN = 18f;

    private float spriteScale = SPRITE_SCALE, speed = MOVEMENT_SPEED, marginX = HORIZONTAL_MARGIN, marginY = VERTICAL_MARGIN;
    private float canvasWidth = SPRITE_WIDTH, canvasHeight = SPRITE_HEIGHT;
    private String facing = "down";
    private float anchorX=.5f,anchorY;
    private final java.util.Map<String, Animation<TextureRegion>> configured = new java.util.HashMap<>();
    private final Rectangle bounds;
    private final Rectangle nextBounds;
    private final Vector2 movement = new Vector2();

    private final GameAssets assets;
    private final boolean ownsAssets;

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> downRunAnimation;
    private final Animation<TextureRegion> backRunAnimation;
    private final Animation<TextureRegion> sideRunAnimation;

    private final String name;
    private float stateTime = 0f;
    private float animationReferenceSpeed=ANIMATION_REFERENCE_SPEED;
    private boolean flipX = false;

    /** Tracks which directional animation to use while moving. */
    private Animation<TextureRegion> currentRunAnimation;

    public Player(float x, float y) { this(null, "Linh", x, y); }

    public Player(String name, float x, float y) { this(null, name, x, y); }

    public Player(GameAssets sharedAssets, float x, float y) {
        this(sharedAssets, "Linh", x, y);
    }

    private Player(GameAssets sharedAssets, String name, float x, float y) {
        this.name = name;
        ownsAssets = sharedAssets == null;
        assets = ownsAssets ? new GameAssets() : sharedAssets;
        bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        nextBounds = new Rectangle(bounds);
        idleAnimation = assets.animation("player.idle");
        downRunAnimation = assets.animation("player.down");
        backRunAnimation = assets.animation("player.back");
        sideRunAnimation = assets.animation("player.side");
        currentRunAnimation = downRunAnimation;
    }

    public Player(GameAssets sharedAssets, float x, float y, SceneCatalog.PlayerDef profile) {
        name = "Linh";
        ownsAssets = false; assets = sharedAssets;
        bounds = new Rectangle(x,y,profile.footWidth,profile.footHeight); nextBounds = new Rectangle(bounds);
        spriteScale = profile.scale; speed = profile.speed;animationReferenceSpeed=profile.speed;anchorX=profile.anchorX;anchorY=profile.anchorY; marginX = marginY = 8;
        canvasWidth = profile.canvasWidth * profile.scale; canvasHeight = profile.canvasHeight * profile.scale;
        idleAnimation = downRunAnimation = backRunAnimation = sideRunAnimation = currentRunAnimation = null;
        for (var entry : profile.animations.entrySet()) {
            Animation<TextureRegion> animation = assets.animation(entry.getValue(),profile.mirrorWalkLeft && entry.getKey().equals("walk.left"));
            if (animation != null) configured.put(entry.getKey(),animation);
        }
    }
    public String facing() { return facing; }
    public void restoreFacing(String value) {
        if (!java.util.Set.of("up","down","left","right").contains(value)) throw new IllegalArgumentException("Invalid facing " + value);
        facing = value;
    }
    public boolean hasSprite() { return frame() != null; }
    private TextureRegion frame() {
        if (!configured.isEmpty()) {
            Animation<TextureRegion> animation = configured.get((isMoving() ? "walk." : "idle.") + facing);
            if (animation == null) animation = configured.get("idle.down");
            return animation == null ? null : animation.getKeyFrame(stateTime,true);
        }
        Animation<TextureRegion> animation = isMoving() ? currentRunAnimation : idleAnimation;
        return animation == null ? null : animation.getKeyFrame(stateTime,true);
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
        return idleAnimation == null ? canvasWidth : idleAnimation.getKeyFrames()[0].getRegionWidth() * spriteScale;
    }

    public float getSpriteHeight() {
        return idleAnimation == null ? canvasHeight : idleAnimation.getKeyFrames()[0].getRegionHeight() * spriteScale;
    }

    public String getName() {
        return name;
    }

    public void update(float delta, float horizontal, float vertical, boolean sprinting,
                       List<Wall> walls, List<Furniture> furniture, List<Npc> npcs,
                       float worldWidth, float worldHeight) {
        if (!Float.isFinite(delta) || delta < 0f) throw new IllegalArgumentException("Invalid movement delta " + delta);
        updateMovement(delta, horizontal, vertical, sprinting, walls, furniture, npcs, worldWidth, worldHeight);
        updateAnimation(delta);
    }

    private void updateMovement(float delta, float horizontal, float vertical, boolean sprinting,
                                List<Wall> walls, List<Furniture> furniture, List<Npc> npcs,
                                float worldWidth, float worldHeight) {
        float previousX = bounds.x;
        float previousY = bounds.y;
        movement.set(horizontal, vertical);
        if (!movement.isZero()) {
            movement.nor().scl(speed * (sprinting ? SPRINT_MULTIPLIER : 1f) * delta);

            // Small swept steps prevent frame stalls from jumping past thin walls.
            int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(movement.x), Math.abs(movement.y)) / (bounds.height / 2f)));
            float dx = movement.x / steps, dy = movement.y / steps;
            for (int i = 0; i < steps; i++) {
                float newX = bounds.x + dx;
                if (!isColliding(newX, bounds.y, walls, furniture, npcs)) bounds.x = newX;
                float newY = bounds.y + dy;
                if (!isColliding(bounds.x, newY, walls, furniture, npcs)) bounds.y = newY;
            }
        }

        bounds.x = MathUtils.clamp(bounds.x, marginX, worldWidth - marginX - bounds.width);
        bounds.y = MathUtils.clamp(bounds.y, marginY, worldHeight - marginY - bounds.height);
        movement.set(bounds.x - previousX, bounds.y - previousY);
    }

    private void updateAnimation(float delta) {
        if (isMoving()) {
            updateDirection();
            stateTime += movement.len() / animationReferenceSpeed;
        } else {
            stateTime += delta;
        }
    }

    public boolean isMoving() {
        return !movement.isZero();
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
        movement.setZero();
    }

    /**
     * Picks the run animation and flip state from displacement after collision.
     * Horizontal displacement takes priority over vertical for animation selection.
     */
    private void updateDirection() {
        if (movement.x > 0) {
            facing = "right";
            currentRunAnimation = sideRunAnimation;
            flipX = false;
        } else if (movement.x < 0) {
            facing = "left";
            currentRunAnimation = sideRunAnimation;
            flipX = true;
        } else if (movement.y > 0) {
            facing = "up";
            currentRunAnimation = backRunAnimation;
        } else if (movement.y < 0) {
            facing = "down";
            currentRunAnimation = downRunAnimation;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = frame();
        if (frame == null) return;

        // Center the 192x192 sprite horizontally over the feet hitbox
        float drawX = bounds.x + bounds.width/2f - getSpriteWidth()*anchorX;
        float drawY = bounds.y - getSpriteHeight()*anchorY;
        float drawWidth = getSpriteWidth();

        if (flipX && configured.isEmpty()) {
            drawX += drawWidth;
            drawWidth = -drawWidth;
        }

        batch.draw(frame, drawX, drawY, drawWidth, getSpriteHeight());
    }

    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void dispose() {
        if (ownsAssets) assets.dispose();
    }
}
