package vn.room304.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
            if (activeNpc != null) {
                Gdx.app.log("Interaction", "Press F to talk to " + activeNpc.getName());
            }
        }

        if (activeNpc != null && Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            dialogueController.start(activeNpc);
        }
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
