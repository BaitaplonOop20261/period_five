package vn.room304.game.dialogue;

import java.util.ArrayList;
import java.util.List;

public class DialogueController {

    private Dialogue dialogue;
    private DialogueChoice activeChoice;
    private List<DialogueLine> activeLines;
    private List<DialogueChoice> availableChoices = List.of();
    private int lineIndex;
    private int selectedChoiceIndex;
    private boolean choosing;

    public void start(Dialogue dialogue) {
        if (dialogue == null || (!dialogue.hasLines() && !dialogue.hasChoices())) {
            close();
            return;
        }
        this.dialogue = dialogue;
        activeChoice = null;
        activeLines = dialogue.getLines();
        if (dialogue.hasChoices()) {
            availableChoices = new ArrayList<>(dialogue.getChoices());
            if (dialogue.getExitChoice() != null) availableChoices.add(dialogue.getExitChoice());
        } else {
            availableChoices = List.of();
        }
        lineIndex = 0;
        selectedChoiceIndex = 0;
        choosing = dialogue.hasChoices();
    }

    public void advance() {
        if (!isActive() || choosing) {
            return;
        }

        if (lineIndex + 1 < activeLines.size()) {
            lineIndex++;
        } else if (activeChoice != null && activeChoice.returnsToChoices()) {
            activeChoice = null;
            activeLines = null;
            lineIndex = 0;
            selectedChoiceIndex = 0;
            choosing = true;
        } else {
            close();
        }
    }

    public void choose(int index) {
        if (!choosing || dialogue == null) return;
        if (index < 0 || index >= availableChoices.size()) return;

        activeChoice = availableChoices.get(index);
        activeLines = activeChoice.getLines();
        lineIndex = 0;
        choosing = false;
    }

    public int getSelectedChoiceIndex() {
        return selectedChoiceIndex;
    }

    public void setSelectedChoiceIndex(int index) {
        if (!choosing || availableChoices.isEmpty()) {
            selectedChoiceIndex = 0;
            return;
        }
        if (index >= 0 && index < availableChoices.size()) {
            this.selectedChoiceIndex = index;
        }
    }

    public void selectPreviousChoice() {
        if (!choosing || availableChoices.isEmpty()) return;
        selectedChoiceIndex = (selectedChoiceIndex - 1 + availableChoices.size()) % availableChoices.size();
    }

    public void selectNextChoice() {
        if (!choosing || availableChoices.isEmpty()) return;
        selectedChoiceIndex = (selectedChoiceIndex + 1) % availableChoices.size();
    }

    public void confirmChoice() {
        if (!choosing || availableChoices.isEmpty()) return;
        choose(selectedChoiceIndex);
    }

    public List<DialogueChoice> getAvailableChoices() {
        return availableChoices;
    }

    public boolean isChoosing() {
        return choosing;
    }

    public boolean isActive() {
        return dialogue != null;
    }

    public String getSpeakerName() {
        if (activeLines == null || lineIndex < 0 || lineIndex >= activeLines.size()) {
            return "";
        }
        return activeLines.get(lineIndex).getSpeaker();
    }

    public String getSpeakerPortraitTexturePath() {
        if (activeLines == null || lineIndex < 0 || lineIndex >= activeLines.size()) {
            return null;
        }
        return activeLines.get(lineIndex).getPortrait();
    }

    public String getCurrentLine() {
        if (activeLines == null || lineIndex < 0 || lineIndex >= activeLines.size()) {
            return "";
        }
        return activeLines.get(lineIndex).getText();
    }

    private void close() {
        dialogue = null;
        activeChoice = null;
        activeLines = null;
        availableChoices = List.of();
        lineIndex = 0;
        selectedChoiceIndex = 0;
        choosing = false;
    }
}
