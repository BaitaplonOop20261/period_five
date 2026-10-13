package com.room304.dialogue;

import java.util.List;
import java.util.HashMap;

/** JSON-friendly dialogue content, independent of its loading source. */
public class Dialogue {

    private String id;
    private List<DialogueLine> lines;
    private HashMap<String, DialogueSpeaker> speakers;
    private String background;
    private String time;
    private String location;

    /** Required by LibGDX Json. */
    public Dialogue() {
    }

    public Dialogue(String id, List<DialogueLine> lines) {
        this.id = id;
        this.lines = lines;
    }

    public void setSpeakers(java.util.Map<String, DialogueSpeaker> speakers) { this.speakers = new HashMap<>(speakers); }

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

    public int indexOf(String lineId) {
        for (int i = 0; i < getLineCount(); i++) {
            if (lineId.equals(getLine(i).getId())) return i;
        }
        return -1;
    }

    public DialogueSpeaker getSpeaker(String speakerId) {
        return speakers == null ? null : speakers.get(speakerId);
    }

    public String getBackground() { return background; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
}
