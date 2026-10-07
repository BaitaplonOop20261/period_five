package vn.room304.game;

import java.util.List;
import vn.room304.game.dialogue.DialogueController;

public class NpcInteractionSystem {

    private Npc activeNpc;
    private final DialogueController dialogueController;

    public NpcInteractionSystem(DialogueController dialogueController) {
        this.dialogueController = dialogueController;
    }

    public void update(Player player, List<Npc> npcs) {
        Npc nearbyNpc = findNearbyNpc(player, npcs);

        if (nearbyNpc != activeNpc) {
            activeNpc = nearbyNpc;
        }
    }

    public void interact() {
        if (activeNpc != null) {
            dialogueController.start(activeNpc.getDialogue());
        }
    }

    public Npc getActiveNpc() {
        return activeNpc;
    }

    private Npc findNearbyNpc(Player player, List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc.canInteract(player.getBounds())) {
                return npc;
            }
        }

        return null;
    }
}
