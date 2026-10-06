package vn.room304.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import vn.room304.game.dialogue.DialogueController;

import java.util.ArrayList;
import java.util.List;

public class DialogueView implements Disposable {

    private final Stage stage;
    private final BitmapFont font;
    private final List<Texture> textures = new ArrayList<>();
    private final Image dimOverlay;
    private final Table rootTable;
    private final Table nameBadge;
    private final Label nameLabel;
    private final Label lineLabel;
    private final Image portraitImage;
    private final Table dialogueBox;
    private String portraitTexturePath;

    public DialogueView() {
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();

        // Semi-transparent dark background overlay
        Texture dimTexture = createSolidTexture(new Color(0f, 0f, 0f, 0.45f));
        dimOverlay = new Image(new TextureRegionDrawable(new TextureRegion(dimTexture)));
        dimOverlay.setFillParent(true);
        dimOverlay.setVisible(false);

        // Text styles
        Label.LabelStyle nameStyle = new Label.LabelStyle(font, new Color(1f, 0.88f, 0.35f, 1f));
        Label.LabelStyle lineStyle = new Label.LabelStyle(font, new Color(0.96f, 0.96f, 0.98f, 1f));
        Label.LabelStyle hintStyle = new Label.LabelStyle(font, new Color(0.65f, 0.72f, 0.82f, 0.85f));

        nameLabel = new Label("", nameStyle);
        lineLabel = new Label("", lineStyle);
        lineLabel.setWrap(true);
        lineLabel.setAlignment(Align.topLeft);

        Label hintLabel = new Label("[F / ENTER] Tiep tuc >", hintStyle);

        portraitImage = new Image();
        portraitImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        portraitImage.setVisible(false);

        // Speaker name badge
        nameBadge = new Table();
        nameBadge.setBackground(createNinePatchDrawable(
            10, 1,
            new Color(0.14f, 0.22f, 0.36f, 0.95f),
            new Color(0.50f, 0.72f, 0.98f, 0.90f)
        ));
        nameBadge.pad(3f, 12f, 3f, 12f);
        nameBadge.add(nameLabel).left();

        // Main dialogue box container
        dialogueBox = new Table();
        dialogueBox.setBackground(createNinePatchDrawable(
            12, 2,
            new Color(0.06f, 0.08f, 0.13f, 0.94f),
            new Color(0.42f, 0.62f, 0.90f, 0.95f)
        ));
        dialogueBox.pad(14f, 18f, 12f, 18f);

        // Header: Speaker Badge
        dialogueBox.add(nameBadge).left().padBottom(6f).row();

        // Body: Spoken text taking expanding space, aligned top-left
        dialogueBox.add(lineLabel).grow().top().left().pad(4f, 2f, 4f, 2f).row();

        // Footer: Interaction hint aligned to bottom-right
        dialogueBox.add(hintLabel).right().bottom();

        // Keep the portrait beside the dialogue so the composition scales cleanly in fullscreen.
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.bottom().pad(0f, 28f, 22f, 28f);
        rootTable.add(portraitImage).bottom().padRight(14f);
        rootTable.add(dialogueBox).growX().minHeight(145f).maxHeight(230f);
        rootTable.setVisible(false);

        stage.addActor(dimOverlay);
        stage.addActor(rootTable);
    }

    public void render(DialogueController dialogueController) {
        boolean active = dialogueController.isActive();
        dimOverlay.setVisible(active);
        rootTable.setVisible(active);

        if (active) {
            String speaker = dialogueController.getSpeakerName();
            boolean hasSpeaker = speaker != null && !speaker.trim().isEmpty();
            nameLabel.setText(hasSpeaker ? speaker : "");
            nameBadge.setVisible(hasSpeaker);
            lineLabel.setText(dialogueController.getCurrentLine());

            updatePortrait(dialogueController.getSpeakerPortraitTexturePath());
        }

        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        float portraitSize = Math.min(220f, Math.max(120f, height * 0.32f));
        portraitImage.setSize(portraitSize, portraitSize);
        dialogueBox.getCell(lineLabel).width(Math.max(280f, width * 0.45f));
    }

    private void updatePortrait(String texturePath) {
        if (texturePath == null || texturePath.trim().isEmpty()) {
            portraitImage.setDrawable(null);
            portraitImage.setVisible(false);
            return;
        }

        if (texturePath.equals(portraitTexturePath)) {
            portraitImage.setVisible(true);
            return;
        }

        Texture portraitTexture = new Texture(texturePath);
        portraitTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textures.add(portraitTexture);
        portraitTexturePath = texturePath;
        portraitImage.setDrawable(new TextureRegionDrawable(new TextureRegion(portraitTexture)));
        portraitImage.setVisible(true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
    }

    private Texture createSolidTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textures.add(texture);
        return texture;
    }

    private NinePatchDrawable createNinePatchDrawable(int size, int border, Color bgColor, Color borderColor) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(borderColor);
        pixmap.fill();
        pixmap.setColor(bgColor);
        pixmap.fillRectangle(border, border, size - (border * 2), size - (border * 2));

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        textures.add(texture);

        int slice = border + 1;
        NinePatch patch = new NinePatch(texture, slice, slice, slice, slice);
        return new NinePatchDrawable(patch);
    }
}
