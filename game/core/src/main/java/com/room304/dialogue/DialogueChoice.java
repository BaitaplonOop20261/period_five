package com.room304.dialogue;

/** A displayed option and a line reference, never executable story conditions. */
public final class DialogueChoice {
    private String id;
    private String text;
    private String next;

    public DialogueChoice() {}
    public DialogueChoice(String id, String text, String next) { this.id = id; this.text = text; this.next = next; }

    public String getId() { return id; }
    public String getText() { return text; }
    public String getNext() { return next; }
}
