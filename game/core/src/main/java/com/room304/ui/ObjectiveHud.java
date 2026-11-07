package com.room304.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

/** Compact objective card drawn in screen space during exploration. */
public final class ObjectiveHud implements Disposable {
    private static final float INSET = 14;
    private static final float WIDTH = 186;
    private static final float PADDING = 9;
    private static final float ROOM_OFFSET = 78;
    private static final float BODY_TOP = 33;
    private static final Color GOLD = new Color(.83f, .66f, .40f, 1);
    private static final Color ROOM = new Color(.84f, .80f, .73f, 1);
    private static final Color TEXT = new Color(.96f, .94f, .90f, 1);

    private final TextureRegion pixel;
    private final PixelFrame frame;
    private final GlyphLayout roomLayout = new GlyphLayout();
    private final GlyphLayout objectiveLayout = new GlyphLayout();

    public ObjectiveHud() {
        Pixmap solid = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        try {
            solid.setColor(Color.WHITE);
            solid.fill();
            pixel = new TextureRegion(new Texture(solid));
        } finally {
            solid.dispose();
        }
        frame = new PixelFrame(pixel, new Color(.105f, .092f, .085f, .82f),
            new Color(.57f, .45f, .29f, 1), false);
    }

    public void draw(Batch batch, BitmapFont font, float viewportHeight, String room, String objective) {
        float top = viewportHeight - INSET;
        boolean hasObjective = !objective.isBlank();
        float height = 28;
        if (hasObjective) {
            UiFont.scale(font, .82f);
            objectiveLayout.setText(font, objective, TEXT, WIDTH - PADDING * 2, Align.left, true);
            height = BODY_TOP + objectiveLayout.height + PADDING;
        }

        float batchColor = batch.getPackedColor();
        batch.setColor(Color.WHITE);
        frame.draw(batch, INSET, top - height, WIDTH, height);
        batch.setColor(GOLD);
        float markerX = INSET + PADDING;
        for (int row = 0; row < 5; row++) {
            int inset = Math.abs(2 - row);
            batch.draw(pixel, markerX + inset, top - 17 + row, 5 - 2 * inset, 1);
        }
        if (hasObjective) {
            batch.setColor(GOLD.r, GOLD.g, GOLD.b, .32f);
            batch.draw(pixel, INSET + PADDING, top - 25, WIDTH - PADDING * 2, 1);
        }
        batch.setColor(Color.WHITE);

        UiFont.scale(font, .58f);
        font.setColor(GOLD);
        font.draw(batch, hasObjective ? "NHIỆM VỤ" : "KHU VỰC", markerX + 10, top - 8);
        UiFont.scale(font, .64f);
        roomLayout.setText(font, room, 0, room.length(), ROOM,
            WIDTH - ROOM_OFFSET - PADDING, Align.right, false, "…");
        font.draw(batch, roomLayout, INSET + ROOM_OFFSET, top - 8);
        if (hasObjective) {
            UiFont.scale(font, .82f);
            font.draw(batch, objectiveLayout, INSET + PADDING, top - BODY_TOP);
        }
        batch.setPackedColor(batchColor);
    }

    @Override public void dispose() {
        pixel.getTexture().dispose();
    }
}
