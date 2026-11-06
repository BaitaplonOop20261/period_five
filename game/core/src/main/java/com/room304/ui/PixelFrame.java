package com.room304.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

/** Pixel borders drawn with the UI's shared white pixel; corners never stretch. */
public final class PixelFrame extends BaseDrawable {
    private final TextureRegion pixel;
    private final Color fill, border;
    private final boolean decorated;

    public PixelFrame(TextureRegion pixel, Color fill, Color border, boolean decorated) {
        this.pixel = pixel;
        this.fill = fill;
        this.border = border;
        this.decorated = decorated;
        setMinWidth(28);
        setMinHeight(24);
        setLeftWidth(12);
        setRightWidth(12);
        setTopHeight(6);
        setBottomHeight(6);
    }

    @Override public void draw(Batch batch, float x, float y, float width, float height) {
        float packed = batch.getPackedColor();
        float alpha = batch.getColor().a;
        batch.setColor(.025f, .02f, .018f, .7f * alpha);
        // Keep the shadow outside the body so it cannot darken a translucent fill.
        batch.draw(pixel, x + 2, y - 3, width, 3);
        batch.draw(pixel, x + width, y, 2, height - 3);
        batch.setColor(fill.r, fill.g, fill.b, fill.a * alpha);
        // Non-overlapping bands preserve the requested alpha across the entire panel.
        batch.draw(pixel, x + 3, y, width - 6, 1);
        batch.draw(pixel, x + 2, y + 1, width - 4, 1);
        batch.draw(pixel, x + 1, y + 2, width - 2, 1);
        batch.draw(pixel, x, y + 3, width, height - 6);
        batch.draw(pixel, x + 1, y + height - 3, width - 2, 1);
        batch.draw(pixel, x + 2, y + height - 2, width - 4, 1);
        batch.draw(pixel, x + 3, y + height - 1, width - 6, 1);
        batch.setColor(border.r, border.g, border.b, border.a * alpha);
        outline(batch, x, y, width, height, 3);
        if (decorated) {
            batch.setColor(border.r, border.g, border.b, .48f * alpha);
            outline(batch, x + 4, y + 4, width - 8, height - 8, 1);
            for (int cornerX = 0; cornerX < 2; cornerX++) {
                for (int cornerY = 0; cornerY < 2; cornerY++) {
                    float cx = cornerX == 0 ? x + 11 : x + width - 16;
                    float cy = cornerY == 0 ? y + 11 : y + height - 12;
                    batch.draw(pixel, cx, cy, 5, 1);
                    batch.draw(pixel, cornerX == 0 ? cx : cx + 4,
                        cornerY == 0 ? cy : cy - 4, 1, 5);
                }
            }
        }
        batch.setPackedColor(packed);
    }

    private void outline(Batch batch, float x, float y, float w, float h, int cut) {
        batch.draw(pixel, x + cut, y, w - cut * 2, 1);
        batch.draw(pixel, x + cut, y + h - 1, w - cut * 2, 1);
        batch.draw(pixel, x, y + cut, 1, h - cut * 2);
        batch.draw(pixel, x + w - 1, y + cut, 1, h - cut * 2);
        for (int step = 1; step < cut; step++) {
            batch.draw(pixel, x + step, y + cut - step, 1, 1);
            batch.draw(pixel, x + w - step - 1, y + cut - step, 1, 1);
            batch.draw(pixel, x + step, y + h - cut + step - 1, 1, 1);
            batch.draw(pixel, x + w - step - 1, y + h - cut + step - 1, 1, 1);
        }
    }
}
