package com.room304.dialogue;

import java.util.HashMap;

/** Authored appearance; independent of world NPC sprites and UI resources. */
public final class DialogueSpeaker {
    private String name;
    private HashMap<String, String> portraits;
    private Float portraitCenterX;
    private Float portraitBottom;
    private Float portraitHeight;
    private Float portraitMaxWidth;

    public String getName() { return name; }
    public String getPortrait(String expression) {
        if (portraits == null) return null;
        String path = portraits.get(expression == null ? "normal" : expression);
        return path == null ? portraits.get("normal") : path;
    }
    public float getCenterX(DialogueLayout layout) {
        return portraitCenterX == null ? layout.portraitCenterX : portraitCenterX;
    }
    public float getBottom(DialogueLayout layout) {
        return portraitBottom == null ? layout.portraitBottom : portraitBottom;
    }
    public float getHeight(DialogueLayout layout) {
        return portraitHeight == null ? layout.portraitHeight : portraitHeight;
    }
    public float getMaxWidth(DialogueLayout layout) {
        return portraitMaxWidth == null ? layout.portraitMaxWidth : portraitMaxWidth;
    }

    void validate(String path) {
        if ((portraitCenterX != null && !Float.isFinite(portraitCenterX))
            || (portraitBottom != null && !Float.isFinite(portraitBottom))
            || (portraitHeight != null && (!Float.isFinite(portraitHeight) || portraitHeight <= 0))
            || (portraitMaxWidth != null && (!Float.isFinite(portraitMaxWidth) || portraitMaxWidth <= 0))) {
            throw new IllegalArgumentException("Invalid portrait placement for speaker " + name + ": " + path);
        }
    }
}
