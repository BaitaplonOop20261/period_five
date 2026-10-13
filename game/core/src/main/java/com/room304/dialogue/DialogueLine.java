package com.room304.dialogue;

/** One spoken line and its optional portrait reference. */
public class DialogueLine {

    private String speaker;
    private String text;
    private String portrait;
    private String id;
    private String speakerId;
    private String expression;
    private String background;
    private String next;
    private float duration;
    private boolean automatic;
    private String cue;
    private java.util.List<DialogueChoice> choices;

    /** Required by LibGDX Json. */
    public DialogueLine() {
    }

    public DialogueLine(String speaker, String text, String portrait) {
        this.speaker = speaker;
        this.text = text;
        this.portrait = portrait;
    }

    public DialogueLine(String id, String speaker, String text, java.util.List<DialogueChoice> choices) {
        this(speaker, text, null); this.id = id; this.choices = choices;
    }
    public float getDuration() { return duration; }
    public boolean isAutomatic() { return automatic; }
    public String getCue() { return cue; }

    public String getSpeaker() {
        return speaker;
    }

    public String getText() {
        return text;
    }

    public String getPortrait() {
        return portrait;
    }

    public String getId() { return id; }
    public String getSpeakerId() { return speakerId; }
    public String getExpression() { return expression; }
    public String getBackground() { return background; }
    public String getNext() { return next; }
    public java.util.List<DialogueChoice> getChoices() {
        return choices == null ? java.util.List.of() : choices;
    }
}
