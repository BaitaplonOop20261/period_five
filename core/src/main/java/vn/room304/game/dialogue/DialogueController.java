package vn.room304.game.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class DialogueController {

    private Dialogue dialogue;
    private int lineIndex;

    public void start(Dialogue dialogue) {
        if (dialogue == null || !dialogue.hasLines()) {
            close();
            return;
        }
        this.dialogue = dialogue;
        lineIndex = 0;
    }

    public void update() {
        if (!isActive()) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            advanceOrClose();
        }
    }

    public boolean isActive() {
        return dialogue != null;
    }

    public String getSpeakerName() {
        return dialogue.getLine(lineIndex).getSpeaker();
    }

    public String getSpeakerPortraitTexturePath() {
        return dialogue.getLine(lineIndex).getPortrait();
    }

    public String getCurrentLine() {
        return dialogue.getLine(lineIndex).getText();
    }

    private void advanceOrClose() {
        int nextLineIndex = lineIndex + 1;
        if (nextLineIndex < dialogue.getLineCount()) {
            lineIndex = nextLineIndex;
            return;
        }

        close();
    }

    private void close() {
        dialogue = null;
        lineIndex = 0;
    }
}
