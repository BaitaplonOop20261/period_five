package vn.room304.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import vn.room304.game.dialogue.Dialogue;

public class Npc {

    private final String name;
    private final Dialogue dialogue;
    private final Rectangle bounds;
    private final Rectangle interactionBounds;

    public Npc(String name, Dialogue dialogue, float x, float y, float width, float height, float interactionRange) {
        this.name = name;
        this.dialogue = dialogue;
        bounds = new Rectangle(x, y, width, height);
        interactionBounds = new Rectangle(
            x - interactionRange,
            y - interactionRange,
            width + interactionRange * 2f,
            height + interactionRange * 2f
        );
    }

    public String getName() {
        return name;
    }

    public Dialogue getDialogue() {
        return dialogue;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean canInteract(Rectangle targetBounds) {
        return interactionBounds.overlaps(targetBounds);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0.1f, 0.55f, 0.9f, 1f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
