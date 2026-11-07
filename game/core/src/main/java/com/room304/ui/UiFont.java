package com.room304.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/** Builds high resolution glyphs while keeping UI measurements in logical units. */
public final class UiFont {
    private static final int DENSITY = 2;

    private UiFont() {}

    public static BitmapFont create(FileHandle file, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);
        try {
            var parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameters.size = size * DENSITY;
            parameters.gamma = 1.3f;
            parameters.minFilter = Texture.TextureFilter.Linear;
            parameters.magFilter = Texture.TextureFilter.Linear;

            StringBuilder characters = new StringBuilder(FreeTypeFontGenerator.DEFAULT_CHARS);
            for (char c = 0x00a0; c <= 0x01ff; c++) characters.append(c);
            for (char c = 0x1ea0; c <= 0x1ef9; c++) characters.append(c);
            parameters.characters = characters.append("‘’“”…–—↑↓←→×").toString();

            BitmapFont font = generator.generateFont(parameters);
            font.setUseIntegerPositions(false);
            scale(font, 1);
            return font;
        } finally {
            generator.dispose();
        }
    }

    /** Sets a logical scale on a font created by this class. */
    public static void scale(BitmapFont font, float multiplier) {
        font.getData().setScale(multiplier / DENSITY);
    }

    /** Configures a label relative to its style's resting font scale, including custom skins. */
    public static void scale(Label label, float multiplier) {
        BitmapFont font = label.getStyle().font;
        label.setFontScale(font.getScaleX() * multiplier, font.getScaleY() * multiplier);
    }
}
