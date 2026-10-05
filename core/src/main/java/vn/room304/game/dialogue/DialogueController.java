package vn.room304.game.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import vn.room304.game.Npc;

public class DialogueController {

    private Npc speaker;
    private Dialogue dialogue;
    private int lineIndex;

    public void start(Npc speaker) {
        this.speaker = speaker;
        dialogue = speaker.getDialogue();
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
        return speaker != null && dialogue != null;
    }

    public String getSpeakerName() {
        return speaker.getName();
    }

    public String getCurrentLine() {
        return dialogue.getLine(lineIndex);
    }

    private void advanceOrClose() {
        int nextLineIndex = lineIndex + 1;
        if (dialogue.hasLine(nextLineIndex)) {
            lineIndex = nextLineIndex;
            return;
        }

        close();
    }

    private void close() {
        speaker = null;
        dialogue = null;
        lineIndex = 0;
    }
}
