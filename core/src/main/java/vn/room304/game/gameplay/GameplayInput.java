package vn.room304.game.gameplay;

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
    }

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
