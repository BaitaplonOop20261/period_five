package com.room304.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

/** All values use a fixed UI canvas, never world coordinates. Editable in JSON. */
public final class DialogueLayout {
    public float width = 960;
    public float height = 540;
    public float portraitCenterX = 480;
    public float portraitBottom = -12;
    public float portraitHeight = 518;
    public float portraitMaxWidth = 510;
    public float textLeft = 170;
    public float textBottom = 66;
    public float textWidth = 650;
    public int textRows = 3;
    public float nameGap = 14;
    public float gradientHeight = 250;
    public float dimAlpha = 0.22f;
    public float hudLeft = 48;
    public float hudTop = 26;
    public float controlsRight = 24;
    public float controlsBottom = 30;
    public float controlsWidth = 98;
    public float buttonHeight = 46;
    public float choiceWidth = 390;
    public float choiceHeight = 44;
    public float choiceGap = 8;
    public float choiceTextGap = 65;
    public float choiceTopMargin = 40;
    public float nameFontScale = 0.75f;
    public float hudFontScale = 0.62f;
    public float controlFontScale = 0.48f;
    public float hintFontScale = 0.48f;
    public float choiceFontScale = 0.65f;
    public float placeholderFontScale = 0.40f;
    public float hudMaxWidth = 300;
    public float hudPaddingX = 14;
    public float hudPaddingY = 9;
    public float hudRowGap = 4;
    public float hudButtonGap = 8;
    public float hintBottomGap = 28;
    public float placeholderTop = 30;
    public float placeholderWidth = 130;
    public float smallLabelHeight = 20;
    public float charactersPerSecond = 32;
    public String font;
    public int fontSize = 22;
    public String fallbackPortrait;

    public static DialogueLayout load(String path) {
        DialogueLayout layout = new Json().fromJson(DialogueLayout.class, Gdx.files.internal(path));
        if (layout == null || layout.width <= 0 || layout.height <= 0 || layout.fontSize <= 0 || layout.textRows < 1
            || layout.textWidth <= 0 || layout.charactersPerSecond <= 0 || layout.portraitHeight <= 0
            || layout.portraitMaxWidth <= 0 || layout.textLeft < 0 || layout.textBottom < 0
            || layout.textLeft + layout.textWidth > layout.width - layout.controlsRight - layout.controlsWidth
            || layout.textBottom + layout.fontSize * layout.textRows + layout.nameGap >= layout.height
            || layout.dimAlpha < 0 || layout.dimAlpha > 1 || layout.buttonHeight <= 0
            || layout.controlsWidth <= 0 || layout.choiceWidth <= 0 || layout.choiceWidth > layout.width
            || layout.choiceHeight <= 0 || layout.gradientHeight <= 0 || layout.gradientHeight > layout.height
            || layout.fallbackPortrait == null
            || layout.font == null || layout.font.isBlank()) {
            throw new IllegalArgumentException("Invalid dialogue layout: " + path);
        }
        return layout;
    }
}
