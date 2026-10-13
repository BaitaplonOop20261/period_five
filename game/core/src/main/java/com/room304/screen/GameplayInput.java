package com.room304.screen;

import com.badlogic.gdx.Input;

/** One keyboard snapshot per gameplay frame; the screen decides which commands are allowed. */
public final class GameplayInput {
    private float horizontal;
    private float vertical;
    private boolean sprinting;
    private boolean interactionRequested;
    private boolean dialogueAdvanceRequested;
    private boolean wardrobeCloseRequested;
    private boolean pauseRequested;
    private int choiceDirection;
    private boolean choiceConfirmRequested;

    public void capture(Input input) {
        horizontal = (input.isKeyPressed(Input.Keys.D) ? 1f : 0f)
            - (input.isKeyPressed(Input.Keys.A) ? 1f : 0f);
        vertical = (input.isKeyPressed(Input.Keys.W) ? 1f : 0f)
            - (input.isKeyPressed(Input.Keys.S) ? 1f : 0f);
        sprinting = input.isKeyPressed(Input.Keys.CONTROL_LEFT)
            || input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        interactionRequested = input.isKeyJustPressed(Input.Keys.F);
        dialogueAdvanceRequested = interactionRequested || input.isKeyJustPressed(Input.Keys.ENTER);
        wardrobeCloseRequested = interactionRequested || input.isKeyJustPressed(Input.Keys.E);
        pauseRequested = input.isKeyJustPressed(Input.Keys.ESCAPE);
        choiceDirection = (input.isKeyJustPressed(Input.Keys.DOWN) ? 1 : 0)
            - (input.isKeyJustPressed(Input.Keys.UP) ? 1 : 0);
        choiceConfirmRequested = input.isKeyJustPressed(Input.Keys.ENTER);
    }

    public int getChoiceDirection() { return choiceDirection; }
    public boolean isChoiceConfirmRequested() { return choiceConfirmRequested; }

    public float getHorizontal() {
        return horizontal;
    }

    public float getVertical() {
        return vertical;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public boolean isInteractionRequested() {
        return interactionRequested;
    }

    public boolean isDialogueAdvanceRequested() {
        return dialogueAdvanceRequested;
    }

    public boolean isWardrobeCloseRequested() {
        return wardrobeCloseRequested;
    }

    public boolean isPauseRequested() {
        return pauseRequested;
    }
}
