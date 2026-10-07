package vn.room304.game.dialogue;

import java.util.List;

/** JSON-friendly dialogue content, independent of its loading source. */
public class Dialogue {

    private String id;
    private List<DialogueLine> lines;

    /** Required by LibGDX Json. */
    public Dialogue() {
    }

    public Dialogue(String id, List<DialogueLine> lines) {
        this.id = id;
        this.lines = lines;
    }

    public String getId() {
        return id;
    }

    public boolean hasLines() {
        return lines != null && !lines.isEmpty();
    }

    public int getLineCount() {
        return lines == null ? 0 : lines.size();
    }

    public DialogueLine getLine(int index) {
        return lines.get(index);
    }
}
