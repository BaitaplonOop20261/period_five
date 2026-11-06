package com.room304.dialogue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.text.Normalizer;

/** HUD emits commands; it never advances dialogue or changes gameplay state. */
public final class DialogueHud extends Group {
    static final float MENU_WIDTH = 72;
    private final Label time;
    private final Label location;
    private final TextButton next;
    private final Image arrow;
    private boolean continueRequested;
    private boolean menuRequested;

    DialogueHud(DialogueLayout layout, DialogueAssets assets,com.badlogic.gdx.scenes.scene2d.ui.Skin skin) {
        Label.LabelStyle style = skin!=null&&skin.has("hud",Label.LabelStyle.class)?skin.get("hud",Label.LabelStyle.class):new Label.LabelStyle(assets.font, new Color(0.90f, 0.92f, 0.95f, 1));
        time = new Label("", style);
        location = new Label("", style);
        time.setFontScale(layout.hudFontScale);
        location.setFontScale(layout.hudFontScale);
        time.setEllipsis(true);
        location.setEllipsis(true);
        Table info = new Table();
        info.setBackground(com.room304.assets.GameAssets.skinDrawable(skin,"hud-frame",assets.pixel.tint(new Color(0.025f, 0.035f, 0.055f, 0.74f))));
        info.pad(layout.hudPaddingY, layout.hudPaddingX, layout.hudPaddingY, layout.hudPaddingX);
        info.add(time).maxWidth(layout.hudMaxWidth).left().row();
        info.add(location).maxWidth(layout.hudMaxWidth).left().padTop(layout.hudRowGap);
        info.pack();
        info.setPosition(layout.hudLeft, layout.height - layout.hudTop - info.getHeight());
        addActor(info);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = assets.font;
        buttonStyle.fontColor = new Color(.96f, .79f, .50f, 1);
        buttonStyle.disabledFontColor = new Color(.40f, .37f, .31f, 1);
        buttonStyle.up = assets.button;
        buttonStyle.over = assets.buttonFocus;
        buttonStyle.down = assets.buttonPressed;
        buttonStyle.disabled = assets.buttonDisabled;
        if(skin!=null&&skin.has("hud",TextButton.TextButtonStyle.class))buttonStyle=skin.get("hud",TextButton.TextButtonStyle.class);
        next = button("Tiếp tục", buttonStyle, layout.controlFontScale, () -> continueRequested = true);
        next.setName("dialogue-next");
        arrow = new Image(assets.arrow);
        arrow.setColor(.96f, .79f, .50f, 1);
        arrow.setTouchable(Touchable.disabled);
        next.add(arrow).size(5, 7).padLeft(5);
        TextButton.TextButtonStyle menuStyle = new TextButton.TextButtonStyle(buttonStyle);
        menuStyle.fontColor = new Color(.76f, .72f, .65f, 1);
        menuStyle.up = assets.pixel.tint(Color.CLEAR);
        TextButton menu = button("Esc · Menu", menuStyle, layout.controlFontScale, () -> menuRequested = true);
        menu.setName("dialogue-menu");
        Table controls = new Table();
        controls.setName("dialogue-controls");
        controls.add(next).size(layout.controlsWidth, layout.buttonHeight).padRight(layout.hudButtonGap);
        controls.add(menu).size(MENU_WIDTH, layout.buttonHeight);
        controls.pack();
        controls.setPosition(layout.width - layout.controlsRight - controls.getWidth(), layout.controlsBottom);
        addActor(controls);
    }

    private TextButton button(String text, TextButton.TextButtonStyle style, float scale, Runnable request) {
        TextButton button = new TextButton(text, style);
        button.getLabel().setFontScale(scale);
        button.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { request.run(); }
        });
        return button;
    }

    void showContext(String sceneTime, String sceneLocation) {
        time.setText("GIỜ   " + literal(sceneTime));
        location.setText("NƠI   " + literal(sceneLocation));
        Table info = (Table) time.getParent();
        float top = info.getY() + info.getHeight();
        info.pack();
        info.setY(top - info.getHeight());
    }

    void showProgress(boolean complete, boolean choices) {
        next.setText(choices ? "Chọn câu" : complete ? "Tiếp tục" : "Skip");
        arrow.setVisible(complete && !choices);
        next.setDisabled(choices);
    }
    public boolean consumeContinueRequest() {
        boolean request = continueRequested; continueRequested = false; return request;
    }
    public boolean consumeMenuRequest() {
        boolean request = menuRequested; menuRequested = false; return request;
    }
    void clearRequests() { continueRequested = false; menuRequested = false; }
    private static String literal(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFC).replace("[", "[[");
    }
}
