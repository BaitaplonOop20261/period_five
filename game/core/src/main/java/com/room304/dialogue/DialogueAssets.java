package com.room304.dialogue;

import com.room304.assets.GameAssets;

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
    final TextureRegionDrawable gradient;
    private final Texture pixelTexture;
    private final Texture gradientTexture;

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
            params.borderWidth = 0.7f;
            params.borderColor = new Color(0.025f, 0.03f, 0.05f, 0.8f);
            params.shadowOffsetX = 1;
            params.shadowOffsetY = 2;
            params.shadowColor = new Color(0, 0, 0, 0.75f);
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
        Pixmap fade = new Pixmap(1, 256, Pixmap.Format.RGBA8888);
        for (int y = 0; y < fade.getHeight(); y++) {
            float alpha = (float) y / (fade.getHeight() - 1);
            fade.setColor(0.015f, 0.02f, 0.035f, alpha * alpha * 0.88f);
            fade.drawPixel(0, y);
        }
        gradientTexture = new Texture(fade);
        gradientTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        fade.dispose();
        gradient = new TextureRegionDrawable(gradientTexture);
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
        gradientTexture.dispose();
    }
}
