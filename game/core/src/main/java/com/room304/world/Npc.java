package com.room304.world;

import com.room304.assets.GameAssets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.room304.dialogue.Dialogue;

public class Npc implements Disposable {

    public static final float SPRITE_SCALE = 6f;
    public static final float SPRITE_WIDTH = 32f * SPRITE_SCALE;   // 192f
    public static final float SPRITE_HEIGHT = 32f * SPRITE_SCALE;  // 192f

    /** Match the player's 18x10-pixel ground footprint. */
    public static final float HITBOX_WIDTH = 18f * SPRITE_SCALE;
    public static final float HITBOX_HEIGHT = 10f * SPRITE_SCALE;

    private final String id;
    private final GameAssets assets;
    private final boolean ownsAssets;
    private final String name;
    private final Dialogue dialogue;
    private final Rectangle bounds;
    private final Rectangle interactionBounds;

    private final Animation<TextureRegion> idleAnimation;
    private float stateTime = 0f;

    public Npc(String name, Dialogue dialogue, String idleTexturePath,
               float x, float y, float interactionRange) {
        this(null, name + ":" + Float.toHexString(x) + ":" + Float.toHexString(y), name,
            dialogue, idleTexturePath, x, y, interactionRange);
    }

    public Npc(GameAssets sharedAssets, String id, String name, Dialogue dialogue, String idleTexturePath,
               float x, float y, float interactionRange) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("NPC needs a stable ID");
        this.id = id;
        ownsAssets = sharedAssets == null;
        assets = ownsAssets ? new GameAssets() : sharedAssets;
        this.name = name;
        this.dialogue = dialogue;
        this.bounds = new Rectangle(x, y, HITBOX_WIDTH, HITBOX_HEIGHT);
        this.interactionBounds = new Rectangle(
            x - interactionRange,
            y - interactionRange,
            HITBOX_WIDTH + interactionRange * 2f,
            HITBOX_HEIGHT + interactionRange * 2f
        );

        var entry = assets.catalog().byPath(idleTexturePath);
        idleAnimation = assets.animation(entry == null ? idleTexturePath : entry.id());
        if (idleAnimation == null) throw new IllegalArgumentException("Missing NPC animation " + id + " -> " + idleTexturePath);
    }

    public Npc(String name, Dialogue dialogue, String idleTexturePath,
               float x, float y, float width, float height, float interactionRange) {
        this(name, dialogue, idleTexturePath, x, y, interactionRange);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public Dialogue getDialogue() {
        return dialogue;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getSpriteWidth() { return idleAnimation.getKeyFrames()[0].getRegionWidth() * SPRITE_SCALE; }
    public float getSpriteHeight() { return idleAnimation.getKeyFrames()[0].getRegionHeight() * SPRITE_SCALE; }

    public float getY() {
        return bounds.y;
    }

    public boolean canInteract(Rectangle targetBounds) {
        return interactionBounds.overlaps(targetBounds);
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = idleAnimation.getKeyFrame(stateTime, true);
        float drawX = bounds.x - (getSpriteWidth() - bounds.width) / 2f;
        float drawY = bounds.y;
        batch.draw(frame, drawX, drawY, getSpriteWidth(), getSpriteHeight());
    }

    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0.1f, 0.55f, 0.9f, 1f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void dispose() {
        if (ownsAssets) assets.dispose();
    }
}
