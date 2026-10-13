package com.room304.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import java.util.HashSet;
import java.util.Set;

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

    public static void validate(Dialogue dialogue, String path) {
        if (dialogue == null || dialogue.getId() == null || dialogue.getId().isBlank()) {
            throw new IllegalArgumentException("Dialogue file must define a non-empty id: " + path);
        }
        if (!dialogue.hasLines()) {
            throw new IllegalArgumentException("Dialogue file must contain at least one line: " + path);
        }

        Set<String> ids = new HashSet<>();
        for (int i = 0; i < dialogue.getLineCount(); i++) {
            DialogueLine line = dialogue.getLine(i);
            if (line == null || line.getText() == null || (line.getText().isBlank() && !line.isAutomatic())) {
                throw new IllegalArgumentException("Dialogue line " + i + " is missing speaker or text: " + path);
            }
            if (!Float.isFinite(line.getDuration()) || line.getDuration() < 0
                || (line.isAutomatic() && !line.getChoices().isEmpty()))
                throw new IllegalArgumentException("Invalid beat timing: " + path + " / " + line.getId());
            if (line.getSpeakerId() != null) {
                DialogueSpeaker speaker = dialogue.getSpeaker(line.getSpeakerId());
                if (speaker == null || speaker.getName() == null || speaker.getName().isBlank()) {
                    throw new IllegalArgumentException("Unknown/unnamed speaker " + line.getSpeakerId() + ": " + path);
                }
                speaker.validate(path);
            } else if (line.getSpeaker() == null) {
                throw new IllegalArgumentException("Missing speaker (use an empty string for narration): " + path);
            }
            if (line.getId() != null && (line.getId().isBlank() || "END".equals(line.getId()) || !ids.add(line.getId()))) {
                throw new IllegalArgumentException("Invalid/duplicate line id " + line.getId() + ": " + path);
            }
        }
        for (int i = 0; i < dialogue.getLineCount(); i++) {
            DialogueLine line = dialogue.getLine(i);
            validateTarget(line.getNext(), ids, path);
            Set<String> choices = new HashSet<>();
            for (DialogueChoice choice : line.getChoices()) {
                if (choice == null || choice.getId() == null || choice.getId().isBlank()
                    || !choices.add(choice.getId()) || choice.getText() == null || choice.getText().isBlank()
                    || choice.getNext() == null) {
                    throw new IllegalArgumentException("Invalid/duplicate choice at line " + i + ": " + path);
                }
                validateTarget(choice.getNext(), ids, path);
            }
        }
    }

    private static void validateTarget(String target, Set<String> ids, String path) {
        if (target != null && !"END".equals(target) && !ids.contains(target)) {
            throw new IllegalArgumentException("Unknown next line " + target + ": " + path);
        }
    }
}
