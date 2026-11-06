package com.room304.dialogue;

import com.room304.assets.GameAssets;
import com.room304.ui.PixelFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

/** Exclusive owner of UI resources. World textures are never borrowed or disposed here. */
final class DialogueAssets implements Disposable {
    private final GameAssets scenes;
    private final boolean ownsAssets;
    final BitmapFont font;
    final TextureRegionDrawable pixel;
    final TextureRegionDrawable arrow;
    final PixelFrame frame, nameplate, button, buttonFocus, buttonPressed, buttonDisabled;
    private final Texture pixelTexture;
    private final Texture arrowTexture;

    DialogueAssets(GameAssets sharedAssets, DialogueLayout layout) {
        ownsAssets = sharedAssets == null;
        scenes = ownsAssets ? new GameAssets() : sharedAssets;
        if (!Gdx.files.internal(layout.font).exists()) {
            throw new IllegalArgumentException("Dialogue font not found: " + layout.font);
        }
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(layout.font));
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
            params.size = layout.fontSize;
            // Pre-bake Vietnamese, including đ/Đ and the complete tone-mark block.
            StringBuilder characters = new StringBuilder(FreeTypeFontGenerator.DEFAULT_CHARS);
            for (char c = 0x00a0; c <= 0x01ff; c++) characters.append(c);
            for (char c = 0x1ea0; c <= 0x1ef9; c++) characters.append(c);
            characters.append("‘’“”…–—→");
            params.characters = characters.toString();
            params.borderWidth = 0;
            params.borderColor = new Color(0.025f, 0.03f, 0.05f, 0.8f);
            params.shadowOffsetX = 1;
            params.shadowOffsetY = 1;
            params.shadowColor = new Color(0, 0, 0, 0.35f);
            params.minFilter = Texture.TextureFilter.Linear;
            params.magFilter = Texture.TextureFilter.Linear;
            font = generator.generateFont(params);
            font.getData().markupEnabled = true;
        } finally {
            generator.dispose();
        }
        Pixmap solid = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        solid.setColor(Color.WHITE);
        solid.fill();
        pixelTexture = new Texture(solid);
        solid.dispose();
        pixel = new TextureRegionDrawable(pixelTexture);
        // Use actual pixels for the continue icon; font coverage varies for triangle glyphs.
        Pixmap triangle = new Pixmap(5, 7, Pixmap.Format.RGBA8888);
        triangle.setColor(Color.WHITE);
        triangle.fillTriangle(0, 0, 0, 6, 4, 3);
        arrowTexture = new Texture(triangle);
        triangle.dispose();
        arrow = new TextureRegionDrawable(arrowTexture);
        Color gold = new Color(.70f, .55f, .33f, 1);
        Color dark = new Color(.105f, .092f, .085f, 1);
        frame = new PixelFrame(pixel.getRegion(), new Color(dark.r,dark.g,dark.b,layout.frameAlpha), gold, true);
        nameplate = new PixelFrame(pixel.getRegion(), dark, gold, false);
        nameplate.setLeftWidth(4);
        nameplate.setRightWidth(4);
        nameplate.setTopHeight(2);
        nameplate.setBottomHeight(2);
        button = new PixelFrame(pixel.getRegion(), dark, gold, false);
        buttonFocus = new PixelFrame(pixel.getRegion(), new Color(.25f, .19f, .12f, 1), new Color(.96f, .77f, .46f, 1), false);
        buttonPressed = new PixelFrame(pixel.getRegion(), new Color(.16f, .12f, .08f, 1), gold, false);
        buttonDisabled = new PixelFrame(pixel.getRegion(), dark, new Color(.28f, .25f, .20f, 1), false);
    }

    com.badlogic.gdx.scenes.scene2d.ui.Skin skin() {return scenes.optionalSkin(font);}

    /** Called only on a line/conversation transition in update, never from draw(). */
    TextureRegionDrawable texture(String path) {
        Texture texture = scenes.optionalTexture(path);
        return texture == null ? null : new TextureRegionDrawable(texture);
    }


    boolean sameTextureReference(String first, String second) {
        if (first == null || second == null) return false;
        String a = first.contains("/") ? first : scenes.catalog().path(first);
        String b = second.contains("/") ? second : scenes.catalog().path(second);
        return a.equals(b);
    }

    @Override public void dispose() {
        if (ownsAssets) scenes.dispose();
        font.dispose();
        pixelTexture.dispose();
        arrowTexture.dispose();
    }
}
