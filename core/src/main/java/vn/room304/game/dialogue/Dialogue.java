package vn.room304.game.dialogue;

import java.util.List;

public class Dialogue {

    private final List<String> lines;

    public Dialogue(List<String> lines) {
        this.lines = List.copyOf(lines);
    }

    public String getLine(int index) {
        return lines.get(index);
    }

    public boolean hasLine(int index) {
        return index >= 0 && index < lines.size();
    }
}
