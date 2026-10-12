package vn.room304.game.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import java.util.List;

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
        if (dialogue.hasChoices()) {
            validateChoices(dialogue.getChoices(), path);
            if (dialogue.getExitChoice() != null) {
                validateChoice(dialogue.getExitChoice(), path);
            }
        } else if (dialogue.hasLines()) {
            validateLines(dialogue.getLines(), path);
        } else {
            throw new IllegalArgumentException("Dialogue file must contain lines or choices: " + path);
        }
    }

    private static void validateChoices(List<DialogueChoice> choices, String path) {
        for (int i = 0; i < choices.size(); i++) {
            DialogueChoice choice = choices.get(i);
            try {
                validateChoice(choice, path);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Invalid choice " + i + " in " + path, exception);
            }
        }
    }

    private static void validateChoice(DialogueChoice choice, String path) {
        if (choice == null || choice.getId() == null || choice.getId().isBlank()
            || choice.getText() == null || choice.getText().isBlank()
            || choice.getLines() == null || choice.getLines().isEmpty()) {
            throw new IllegalArgumentException("Choice must define id, text, and lines: " + path);
        }
        validateLines(choice.getLines(), path);
    }

    private static void validateLines(List<DialogueLine> lines, String path) {
        for (int i = 0; i < lines.size(); i++) {
            DialogueLine line = lines.get(i);
            if (line == null || line.getSpeaker() == null || line.getSpeaker().isBlank()
                || line.getText() == null || line.getText().isBlank()) {
                throw new IllegalArgumentException("Dialogue line " + i + " is missing speaker or text: " + path);
            }
        }
    }
}
