package vn.room304.game.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

/** Loads validated dialogue data from LibGDX internal assets. */
public final class DialogueLoader {

    private static final Json JSON = new Json();

    private DialogueLoader() {
    }

    public static Dialogue load(String internalPath) {
        FileHandle file = Gdx.files.internal(internalPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Dialogue file not found: " + internalPath);
        }

        final Dialogue dialogue;
        try {
            dialogue = JSON.fromJson(Dialogue.class, file);
        } catch (SerializationException exception) {
            throw new IllegalArgumentException("Could not read dialogue file: " + internalPath, exception);
        }

        validate(dialogue, internalPath);
        return dialogue;
    }

    private static void validate(Dialogue dialogue, String path) {
        if (dialogue == null || dialogue.getId() == null || dialogue.getId().isBlank()) {
            throw new IllegalArgumentException("Dialogue file must define a non-empty id: " + path);
        }
        if (!dialogue.hasLines()) {
            throw new IllegalArgumentException("Dialogue file must contain at least one line: " + path);
        }

        for (int i = 0; i < dialogue.getLineCount(); i++) {
            DialogueLine line = dialogue.getLine(i);
            if (line == null || line.getSpeaker() == null || line.getSpeaker().isBlank()
                || line.getText() == null || line.getText().isBlank()) {
                throw new IllegalArgumentException("Dialogue line " + i + " is missing speaker or text: " + path);
            }
        }
    }
}
