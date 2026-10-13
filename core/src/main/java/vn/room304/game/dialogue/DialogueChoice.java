package vn.room304.game.dialogue;

import java.util.List;

/** One player-selectable conversation branch. */
public class DialogueChoice {

    private String id;
    private String text;
    private List<DialogueLine> lines;
    private boolean return_to_choices;
    private boolean close_dialogue;

    /** Required by LibGDX Json. */
    public DialogueChoice() {
    }

    public DialogueChoice(String id, String text, List<DialogueLine> lines, boolean returnToChoices, boolean closeDialogue) {
        this.id = id;
        this.text = text;
        this.lines = lines;
        this.return_to_choices = returnToChoices;
        this.close_dialogue = closeDialogue;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<DialogueLine> getLines() {
        return lines;
    }

    public boolean returnsToChoices() {
        return return_to_choices;
    }

    public boolean closesDialogue() {
        return close_dialogue;
    }
}
