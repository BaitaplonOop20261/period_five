package vn.room304.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import vn.room304.game.dialogue.Dialogue;

public class Npc implements Disposable {

    public static final float SPRITE_SCALE = 6f;
    public static final float SPRITE_WIDTH = 32f * SPRITE_SCALE;   // 192f
    public static final float SPRITE_HEIGHT = 32f * SPRITE_SCALE;  // 192f

    /** Match the player's 18x10-pixel ground footprint. */
    public static final float HITBOX_WIDTH = 18f * SPRITE_SCALE;
    public static final float HITBOX_HEIGHT = 10f * SPRITE_SCALE;
    private static final float FRAME_DURATION = 0.15f;

    private final String name;
    private final Dialogue dialogue;
    private final Rectangle bounds;
    private final Rectangle interactionBounds;

    private final Texture idleSheet;
    private final Animation<TextureRegion> idleAnimation;
    private float stateTime = 0f;

    public Npc(String name, Dialogue dialogue, String idleTexturePath,
               float x, float y, float interactionRange) {
        this.name = name;
        this.dialogue = dialogue;
        this.bounds = new Rectangle(x, y, HITBOX_WIDTH, HITBOX_HEIGHT);
        this.interactionBounds = new Rectangle(
            x - interactionRange,
            y - interactionRange,
            HITBOX_WIDTH + interactionRange * 2f,
            HITBOX_HEIGHT + interactionRange * 2f
        );

        idleSheet = new Texture(Gdx.files.internal(idleTexturePath));
        int frameSize = idleSheet.getHeight(); // 32px
        int cols = idleSheet.getWidth() / frameSize;
        TextureRegion[][] grid = TextureRegion.split(idleSheet, frameSize, frameSize);
        TextureRegion[] frames = new TextureRegion[cols];
        for (int i = 0; i < cols; i++) {
            frames[i] = grid[0][i];
        }
        idleAnimation = new Animation<>(FRAME_DURATION, frames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public Npc(String name, Dialogue dialogue, String idleTexturePath,
               float x, float y, float width, float height, float interactionRange) {
        this(name, dialogue, idleTexturePath, x, y, interactionRange);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public String getName() {
        return name;
    }

    public Dialogue getDialogue() {
        return dialogue;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getY() {
        return bounds.y;
    }

    public boolean canInteract(Rectangle targetBounds) {
        return interactionBounds.overlaps(targetBounds);
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = idleAnimation.getKeyFrame(stateTime, true);
        float drawX = bounds.x - (SPRITE_WIDTH - bounds.width) / 2f;
        float drawY = bounds.y;
        batch.draw(frame, drawX, drawY, SPRITE_WIDTH, SPRITE_HEIGHT);
    }

    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0.1f, 0.55f, 0.9f, 1f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void dispose() {
        idleSheet.dispose();
    }
}
