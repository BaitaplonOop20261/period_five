package com.room304.world;

import com.room304.assets.GameAssets;

import com.badlogic.gdx.math.Rectangle;

/**
 * A wardrobe that the player can interact with by pressing F when nearby.
 * Extends Furniture with an interaction range.
 */
public class Wardrobe extends Furniture {

    private final Rectangle interactionBounds;

    public Wardrobe(String texturePath, float drawX, float drawY,
                    float drawWidth, float drawHeight,
                    Rectangle collisionRect, float interactionRange) {
        this(null, texturePath, drawX, drawY, drawWidth, drawHeight, collisionRect, interactionRange);
    }

    public Wardrobe(GameAssets sharedAssets, String texturePath, float drawX, float drawY,
                    float drawWidth, float drawHeight, Rectangle collisionRect, float interactionRange) {
        super(sharedAssets, texturePath, drawX, drawY, drawWidth, drawHeight, collisionRect);

        Rectangle col = collisionRect != null ? collisionRect : getDrawBounds();
        this.interactionBounds = new Rectangle(
            col.x - interactionRange,
            col.y - interactionRange,
            col.width + interactionRange * 2f,
            col.height + interactionRange * 2f
        );
    }

    public boolean canInteract(Rectangle playerBounds) {
        return interactionBounds.overlaps(playerBounds);
    }
}
