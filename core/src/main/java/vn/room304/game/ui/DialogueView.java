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
import com.badlogic.gdx.utils.viewport.FitViewport;
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
    private final Table portraitFrame;
    private final Table dialogueBox;
    private String portraitTexturePath;

    public DialogueView() {
        // Use a stable virtual canvas so fullscreen keeps the same RPG layout.
        stage = new Stage(new FitViewport(960f, 540f));
        font = new BitmapFont();

        // Semi-transparent dark background overlay
        Texture dimTexture = createSolidTexture(new Color(0.10f, 0.06f, 0.03f, 0.48f));
        dimOverlay = new Image(new TextureRegionDrawable(new TextureRegion(dimTexture)));
        dimOverlay.setFillParent(true);
        dimOverlay.setVisible(false);

        // Text styles
        Label.LabelStyle nameStyle = new Label.LabelStyle(font, new Color(1f, 0.86f, 0.48f, 1f));
        Label.LabelStyle lineStyle = new Label.LabelStyle(font, new Color(0.98f, 0.91f, 0.75f, 1f));
        Label.LabelStyle hintStyle = new Label.LabelStyle(font, new Color(0.78f, 0.63f, 0.42f, 1f));

        nameLabel = new Label("", nameStyle);
        lineLabel = new Label("", lineStyle);
        lineLabel.setWrap(true);
        lineLabel.setAlignment(Align.topLeft);

        Label hintLabel = new Label("[F / ENTER] Tiep tuc >", hintStyle);

        portraitImage = new Image();
        portraitImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        portraitImage.setVisible(false);

        portraitFrame = new Table();
        portraitFrame.setBackground(createNinePatchDrawable(
            16, 3,
            new Color(0.20f, 0.11f, 0.06f, 0.98f),
            new Color(0.82f, 0.52f, 0.20f, 1f)
        ));
        portraitFrame.pad(6f);
        portraitFrame.add(portraitImage).size(180f, 180f);

        // Speaker name badge
        nameBadge = new Table();
        nameBadge.setBackground(createNinePatchDrawable(
            10, 1,
            new Color(0.30f, 0.16f, 0.07f, 1f),
            new Color(0.91f, 0.65f, 0.28f, 1f)
        ));
        nameBadge.pad(3f, 12f, 3f, 12f);
        nameBadge.add(nameLabel).left();

        // Main dialogue box container
        dialogueBox = new Table();
        dialogueBox.setBackground(createNinePatchDrawable(
            12, 2,
            new Color(0.18f, 0.10f, 0.06f, 0.97f),
            new Color(0.72f, 0.43f, 0.17f, 1f)
        ));
        dialogueBox.pad(13f, 18f, 11f, 18f);

        // Header: Speaker Badge
        dialogueBox.add(nameBadge).left().padBottom(6f).row();

        // Body: Spoken text taking expanding space, aligned top-left
        dialogueBox.add(lineLabel).grow().top().left().pad(4f, 2f, 4f, 2f).row();

        // Footer: Interaction hint aligned to bottom-right
        dialogueBox.add(hintLabel).right().bottom();

        // RPG layout: the character art sits above the dialogue box.
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.bottom().pad(0f, 28f, 22f, 28f);
        rootTable.add(portraitFrame).center().bottom().padBottom(8f).row();
        rootTable.add(dialogueBox).growX().minHeight(120f).maxHeight(165f).row();
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
        // Keep the portrait compact, like a classic RPG dialogue layout.
        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();
        float portraitSize = Math.min(178f, Math.max(120f, viewportHeight * 0.33f));
        float dialogueHeight = Math.min(155f, Math.max(120f, viewportHeight * 0.30f));
        float dialogueWidth = Math.max(480f, viewportWidth - 56f);

        portraitImage.setSize(portraitSize, portraitSize);
        portraitFrame.getCell(portraitImage).size(portraitSize, portraitSize);
        rootTable.getCell(portraitFrame).size(portraitSize + 12f, portraitSize + 12f);
        rootTable.getCell(dialogueBox).width(dialogueWidth).height(dialogueHeight);
        dialogueBox.getCell(lineLabel).width(dialogueWidth - 44f);
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
