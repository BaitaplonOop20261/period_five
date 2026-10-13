package com.room304.dialogue;

public class DialogueController {

    private Dialogue dialogue;
    private int lineIndex;
    private String lastChoiceId;
    private long revision;
    private java.util.function.Consumer<String> onFinished;

    public void start(Dialogue dialogue) { start(dialogue, null); }

    public void start(Dialogue dialogue, java.util.function.Consumer<String> onFinished) {
        close();
        if (dialogue == null || !dialogue.hasLines()) {
            close();
            return;
        }
        this.dialogue = dialogue;
        this.onFinished = onFinished;
        lineIndex = 0;
        lastChoiceId = null;
        revision++;
    }

    public void advance() {
        if (!isActive()) {
            return;
        }

        // Choices require an explicit selection; Next cannot silently pick a branch.
        if (!getLine().getChoices().isEmpty()) return;
        follow(getLine().getNext());
    }

    public boolean isActive() {
        return dialogue != null;
    }

    public String getSpeakerName() {
        DialogueSpeaker speaker = dialogue.getSpeaker(getLine().getSpeakerId());
        return speaker == null ? getLine().getSpeaker() : speaker.getName();
    }

    public String getSpeakerPortraitTexturePath() {
        if (getLine().getPortrait() != null) return getLine().getPortrait();
        DialogueSpeaker speaker = dialogue.getSpeaker(getLine().getSpeakerId());
        return speaker == null ? null : speaker.getPortrait(getLine().getExpression());
    }

    public String getCurrentLine() {
        return getLine().getText();
    }

    public Dialogue getDialogue() { return dialogue; }
    public DialogueLine getLine() { return isActive() ? dialogue.getLine(lineIndex) : null; }
    public String getLastChoiceId() { return lastChoiceId; }
    public long getRevision() { return revision; }

    public void choose(String choiceId) {
        if (!isActive()) return;
        for (DialogueChoice choice : getLine().getChoices()) {
            if (choice.getId().equals(choiceId)) {
                lastChoiceId = choiceId;
                follow(choice.getNext());
                return;
            }
        }
        throw new IllegalArgumentException("Unknown choice " + choiceId + " in " + dialogue.getId());
    }

    private void follow(String next) {
        if ("END".equals(next)) { finish(); return; }
        int nextLineIndex = next == null ? lineIndex + 1 : dialogue.indexOf(next);
        if (nextLineIndex < 0) throw new IllegalArgumentException("Unknown line " + next + " in " + dialogue.getId());
        if (nextLineIndex < dialogue.getLineCount()) {
            lineIndex = nextLineIndex;
            revision++;
            return;
        }

        finish();
    }

    private void finish() {
        var callback = onFinished;
        String choice = lastChoiceId;
        close();
        if (callback != null) callback.accept(choice);
    }

    /** Cancellation/replacement/disposal never reports successful completion. */
    public void close() {
        onFinished = null;
        dialogue = null;
        lineIndex = 0;
        revision++;
    }
}
