package vn.room304.game.dialogue;

/** One spoken line and its optional portrait reference. */
public class DialogueLine {

    private String speaker;
    private String text;
    private String portrait;

    /** Required by LibGDX Json. */
    public DialogueLine() {
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getText() {
        return text;
    }

    public String getPortrait() {
        return portrait;
    }
}
