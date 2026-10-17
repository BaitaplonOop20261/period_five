package com.room304.dialogue;

import com.room304.assets.GameAssets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** One reusable Visual Novel presentation. All business commands return to GameScreen. */
public final class DialogueUI implements Disposable {
    private final DialogueLayout layout;
    private final DialogueAssets assets;
    private final com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private final Stage stage;
    private final Table root = new Table();
    private final Image background = new Image();
    private final Image dim;
    private final Image inspection = new Image();
    private final Label inspectionTitle;
    private final Image portrait = new Image();
    private final Label name;
    private final Label text;
    private final Label hint;
    private final Label placeholder;
    private final Table choices = new Table();
    private final ScrollPane choicePane = new ScrollPane(choices);
    private final DialogueHud hud;
    private final GlyphLayout measure = new GlyphLayout();
    private final List<String> pages = new ArrayList<>();
    private Dialogue conversation;
    private DialogueLine line;
    private int pageIndex;
    private int[] characterEnds = new int[0];
    private float revealedCharacters;
    private int renderedCharacters = -1;
    private String choiceRequest;
    private final List<TextButton> choiceButtons = new ArrayList<>();
    private int selectedChoice;
    private long revision = -1;
    private String inspectionReference;

    public DialogueUI(DialogueLayout layout) {
        this(null, layout);
    }

    public DialogueUI(GameAssets sharedAssets, DialogueLayout layout) {
        this.layout = layout;
        assets = new DialogueAssets(sharedAssets, layout);try{skin=assets.skin();}catch(RuntimeException e){assets.dispose();throw e;}
        stage = new Stage(new FitViewport(layout.width, layout.height));
        root.setFillParent(true);
        root.setVisible(false);
        stage.addActor(root);

        background.setScaling(Scaling.fill);
        Container<Image> backgroundClip = new Container<>(background).fill();
        backgroundClip.setClip(true);
        backgroundClip.setBounds(0, 0, layout.width, layout.height);
        backgroundClip.setTouchable(Touchable.disabled);
        root.addActor(backgroundClip);
        dim = new Image(assets.pixel);
        dim.setColor(0.02f, 0.025f, 0.04f, layout.dimAlpha);
        dim.setBounds(0, 0, layout.width, layout.height);
        dim.setTouchable(Touchable.disabled);
        root.addActor(dim);
        inspection.setScaling(Scaling.fit);
        inspection.setName("inspection-art");
        inspection.setTouchable(Touchable.disabled);
        inspection.setVisible(false);
        root.addActor(inspection);
        inspectionTitle = new Label("", new Label.LabelStyle(assets.font, new Color(.86f,.83f,.72f,1)));
        inspectionTitle.setFontScale(.7f);
        inspectionTitle.setAlignment(Align.center);
        inspectionTitle.setEllipsis(true);
        inspectionTitle.setBounds(24, layout.height-64, layout.width-48, 24);
        inspectionTitle.setTouchable(Touchable.disabled);
        inspectionTitle.setVisible(false);
        root.addActor(inspectionTitle);
        portrait.setScaling(Scaling.fit);
        portrait.setTouchable(Touchable.disabled);
        root.addActor(portrait);

        Image gradient = new Image(com.room304.assets.GameAssets.skinDrawable(skin,"dialogue-frame",assets.gradient));
        gradient.setBounds(0, 0, layout.width, layout.gradientHeight);
        gradient.setTouchable(Touchable.disabled);
        root.addActor(gradient);
        Label.LabelStyle bodyStyle = skin!=null&&skin.has("dialogue",Label.LabelStyle.class)?skin.get("dialogue",Label.LabelStyle.class):new Label.LabelStyle(assets.font, new Color(0.98f, 0.98f, 0.96f, 1));
        text = new Label("", bodyStyle);
        text.setWrap(true);
        text.setAlignment(Align.topLeft);
        text.setBounds(layout.textLeft, layout.textBottom, layout.textWidth, textHeight());
        text.setTouchable(Touchable.disabled);
        root.addActor(text);
        name = new Label("", skin!=null&&skin.has("speaker",Label.LabelStyle.class)?skin.get("speaker",Label.LabelStyle.class):new Label.LabelStyle(assets.font, new Color(0.83f, 0.90f, 0.91f, 1)));
        name.setFontScale(layout.nameFontScale);
        name.setEllipsis(true);
        name.setBounds(layout.textLeft, layout.textBottom + textHeight() + layout.nameGap, layout.textWidth, assets.font.getLineHeight());
        name.setTouchable(Touchable.disabled);
        root.addActor(name);
        hint = new Label("", bodyStyle);
        hint.setFontScale(layout.hintFontScale);
        hint.setBounds(layout.textLeft, layout.textBottom - layout.hintBottomGap, layout.textWidth, layout.smallLabelHeight);
        hint.setTouchable(Touchable.disabled);
        root.addActor(hint);
        placeholder = new Label("PORTRAIT TẠM", bodyStyle);
        placeholder.setFontScale(layout.placeholderFontScale);
        placeholder.setBounds((layout.width - layout.placeholderWidth) / 2,
            layout.height - layout.placeholderTop, layout.placeholderWidth, layout.smallLabelHeight);
        placeholder.setVisible(false);
        root.addActor(placeholder);
        choicePane.setScrollingDisabled(true, false);
        choicePane.setFadeScrollBars(false);
        root.addActor(choicePane);
        hud = new DialogueHud(layout, assets,skin);
        root.addActor(hud);
    }

    private float textHeight() { return assets.font.getLineHeight() * layout.textRows; }

    /** Synchronize only in update; textures and labels are untouched in draw(). */
    public void present(DialogueController controller, String worldLocation) {present(controller,worldLocation,null);}
    public void present(DialogueController controller, String worldLocation,String documentBackground) {
        present(controller,worldLocation,documentBackground,null,null);
    }
    public void present(DialogueController controller, String worldLocation,String documentBackground,String inspectionAsset,String objectName) {
        if (!controller.isActive()) { hide(); return; }
        if (controller.getLine().getText().isBlank()) { hide(); return; }
        root.setVisible(true);
        if (conversation != controller.getDialogue()) {
            detachSceneTextures();
            conversation = controller.getDialogue();
            line = null;
        }
        if(!Objects.equals(inspectionReference,inspectionAsset)) {
            inspectionReference=inspectionAsset;
            inspection.setDrawable(assets.texture(inspectionAsset));
        }
        boolean inspecting=inspection.getDrawable()!=null;
        dim.setColor(.02f,.025f,.04f,inspecting?.78f:layout.dimAlpha);
        inspectionTitle.setText(inspecting?escape(objectName==null?"Đồ vật":objectName):"");
        inspectionTitle.setVisible(inspecting);
        layoutInspection();
        if (line == controller.getLine() && revision == controller.getRevision()) return;
        revision = controller.getRevision();
        stage.cancelTouchFocus();
        hud.clearRequests();
        choiceRequest = null;
        line = controller.getLine();
        String path = line.getBackground() == null ? conversation.getBackground() : line.getBackground();
        TextureRegionDrawable backgroundTexture = assets.texture(path==null?documentBackground:path);
        background.setScaling(documentBackground!=null?Scaling.fit:Scaling.fill);
        background.setDrawable(backgroundTexture);
        background.setVisible(!inspecting && backgroundTexture != null);
        DialogueSpeaker speaker = conversation.getSpeaker(line.getSpeakerId());
        String portraitPath = controller.getSpeakerPortraitTexturePath();
        TextureRegionDrawable portraitTexture = assets.texture(portraitPath);
        boolean usePlaceholder = portraitTexture == null && (speaker != null || portraitPath != null);
        if (usePlaceholder) portraitTexture = assets.texture(layout.fallbackPortrait);
        portrait.setDrawable(portraitTexture);
        portrait.setVisible(!inspecting && portraitTexture != null);
        placeholder.setVisible(!inspecting && !layout.fallbackPortrait.isBlank() && (usePlaceholder || assets.sameTextureReference(layout.fallbackPortrait, portraitPath)));
        float height = speaker == null ? layout.portraitHeight : speaker.getHeight(layout);
        float maxWidth = speaker == null ? layout.portraitMaxWidth : speaker.getMaxWidth(layout);
        float center = speaker == null ? layout.portraitCenterX : speaker.getCenterX(layout);
        float bottom = speaker == null ? layout.portraitBottom : speaker.getBottom(layout);
        portrait.setBounds(center - maxWidth / 2, bottom, maxWidth, height);
        name.setText(escape(controller.getSpeakerName() == null ? "" : controller.getSpeakerName()));
        hud.showContext(conversation.getTime() == null ? "—" : conversation.getTime(),
            conversation.getLocation() == null ? worldLocation : conversation.getLocation());
        paginate(Normalizer.normalize(line.getText(), Normalizer.Form.NFC).replace("↑ / ↓","Lên / xuống"));
        buildChoices();
        pageIndex = 0;
        startPage();
    }

    // Split long text at word boundaries, with character-safe fallback for a very long word.
    private void paginate(String fullText) {
        pages.clear();
        String remaining = fullText.strip();
        while (!remaining.isEmpty()) {
            int count = remaining.codePointCount(0, remaining.length());
            int low = 1, high = count, fits = 1;
            while (low <= high) {
                int middle = (low + high) / 2;
                int end = remaining.offsetByCodePoints(0, middle);
                measure.setText(assets.font, escape(remaining.substring(0, end)), Color.WHITE,
                    layout.textWidth, Align.left, true);
                if (measure.height <= textHeight()) { fits = middle; low = middle + 1; }
                else high = middle - 1;
            }
            int end = remaining.offsetByCodePoints(0, fits);
            if (end < remaining.length()) {
                int wordEnd = end;
                while (wordEnd > 0 && !Character.isWhitespace(remaining.charAt(wordEnd - 1))) wordEnd--;
                if (wordEnd > 0) end = wordEnd;
            }
            pages.add(remaining.substring(0, end).strip());
            remaining = remaining.substring(end).strip();
        }
    }

    private void startPage() {
        BreakIterator characters = BreakIterator.getCharacterInstance(Locale.forLanguageTag("vi"));
        characters.setText(pages.get(pageIndex));
        List<Integer> ends = new ArrayList<>();
        for (int end = characters.next(); end != BreakIterator.DONE; end = characters.next()) ends.add(end);
        characterEnds = ends.stream().mapToInt(Integer::intValue).toArray();
        revealedCharacters = 0;
        renderedCharacters = -1;
        refreshText();
    }

    public void update(float delta) {
        if (line == null) return;
        revealedCharacters = Math.min(characterEnds.length, revealedCharacters + Math.max(0, delta) * layout.charactersPerSecond);
        refreshText();
        stage.act(Math.max(0, delta));
    }

    private void refreshText() {
        int count = (int) revealedCharacters;
        boolean complete = count == characterEnds.length;
        if (count != renderedCharacters) {
            renderedCharacters = count;
            String page = pages.get(pageIndex);
            int end = count == 0 ? 0 : characterEnds[count - 1];
            // Transparent unrevealed glyphs preserve wrapping; words do not jump as they appear.
            text.setText(escape(page.substring(0, end)) + "[#FFFFFF00]" + escape(page.substring(end)) + "[]");
        }
        boolean selecting = complete && pageIndex == pages.size() - 1 && !line.getChoices().isEmpty();
        choices.setVisible(selecting);
        choicePane.setVisible(selecting);
        layoutInspection();
        hud.showProgress(complete, selecting);
        hint.setText(selecting ? "Lên / xuống  ·  Chọn     Enter  ·  Xác nhận" : complete
            ? "Enter  ·  Tiếp tục" + (pages.size() > 1 ? "   " + (pageIndex + 1) + "/" + pages.size() : "")
            : "Enter  ·  Hiện toàn bộ câu");
    }

    private void layoutInspection() {
        float bottom=layout.textBottom+textHeight()+layout.nameGap+assets.font.getLineHeight()+12;
        if(choicePane.isVisible()) bottom=Math.max(bottom,choicePane.getTop()+12);
        float height=layout.height-72-bottom;
        inspection.setBounds(24,bottom,layout.width-48,Math.max(0,height));
        inspection.setVisible(inspection.getDrawable()!=null && height>=40);
        inspectionTitle.setVisible(inspection.isVisible());
    }

    /** True only when presentation is finished and the controller may advance. */
    public boolean continueText() {
        if (line == null) return false;
        if (!isTextComplete()) {
            revealedCharacters = characterEnds.length;
            refreshText();
            return false;
        }
        if (pageIndex + 1 < pages.size()) { pageIndex++; startPage(); return false; }
        return line.getChoices().isEmpty();
    }

    private void buildChoices() {
        choices.clearChildren();
        choiceButtons.clear();
        selectedChoice = 0;
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = assets.font;
        style.fontColor = Color.WHITE;
        style.up = assets.pixel.tint(new Color(0.04f, 0.065f, 0.09f, 0.92f));
        style.over = assets.pixel.tint(new Color(0.23f, 0.34f, 0.40f, 0.95f));
        style.down = style.over;
        style.checked = style.over;
        if(skin!=null && skin.has("choice",TextButton.TextButtonStyle.class))style=skin.get("choice",TextButton.TextButtonStyle.class);
        for (DialogueChoice choice : line.getChoices()) {
            TextButton button = new TextButton(escape(choice.getText()), style);
            button.setProgrammaticChangeEvents(false);
            button.setChecked(choiceButtons.isEmpty());
            choiceButtons.add(button);
            button.getLabel().setFontScale(layout.choiceFontScale);
            button.getLabel().setWrap(true);
            button.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    if (choicePane.isVisible() && choiceRequest == null) choiceRequest = choice.getId();
                }
            });
            choices.add(button).width(layout.choiceWidth).minHeight(layout.choiceHeight).padBottom(layout.choiceGap).row();
        }
        choices.pack();
        float bottom = layout.textBottom + textHeight() + layout.choiceTextGap;
        choicePane.setBounds((layout.width - layout.choiceWidth) / 2, bottom, layout.choiceWidth,
            Math.min(choices.getPrefHeight(), Math.max(layout.choiceHeight, layout.height - bottom - layout.choiceTopMargin)));
        choicePane.setScrollY(0);
        choicePane.setVisible(false);
        choices.setVisible(false);
    }

    public boolean isChoosing() { return choicePane.isVisible() && !choiceButtons.isEmpty(); }
    public void moveChoice(int direction) {
        if (!isChoosing() || direction == 0) return;
        selectedChoice = Math.floorMod(selectedChoice + direction, choiceButtons.size());
        for (int i = 0; i < choiceButtons.size(); i++) choiceButtons.get(i).setChecked(i == selectedChoice);
        TextButton selected = choiceButtons.get(selectedChoice);
        choicePane.scrollTo(selected.getX(), selected.getY(), selected.getWidth(), selected.getHeight());
    }
    public String selectedChoiceId() {
        return isChoosing() ? line.getChoices().get(selectedChoice).getId() : null;
    }

    private static String escape(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFC).replace("[", "[[");
    }
    public boolean isTextComplete() { return renderedCharacters == characterEnds.length; }
    public Stage getStage() { return stage; }
    public DialogueHud getHud() { return hud; }
    public String consumeChoiceRequest() { String request = choiceRequest; choiceRequest = null; return request; }
    public void clearCommands() { hud.clearRequests(); choiceRequest = null; stage.cancelTouchFocus(); }

    public String inspectionAsset() { return inspectionReference; }
    private void detachSceneTextures() {
        background.setDrawable(null);portrait.setDrawable(null);inspection.setDrawable(null);
        inspection.setVisible(false);inspectionTitle.setVisible(false);inspectionReference=null;
    }
    private void hide() {
        if (conversation == null) return;
        root.setVisible(false);
        clearCommands();
        detachSceneTextures();
        conversation = null;
        line = null;
        choices.clearChildren();
        choiceButtons.clear();
        pages.clear();
    }
    public void draw() {
        if (conversation == null) return;
        stage.getViewport().apply();
        stage.draw();
    }
    public void resize(int width, int height) {
        if (width > 0 && height > 0) stage.getViewport().update(width, height, true);
    }
    @Override public void dispose() { stage.dispose();if(skin!=null){skin.remove("default-font",com.badlogic.gdx.graphics.g2d.BitmapFont.class);skin.dispose();}assets.dispose(); }
}
