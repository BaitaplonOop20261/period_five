package com.room304.dialogue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.text.Normalizer;

/** HUD emits commands; it never advances dialogue or changes gameplay state. */
public final class DialogueHud extends Group {
    private final Label time;
    private final Label location;
    private final TextButton next;
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
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.disabledFontColor = Color.GRAY;
        buttonStyle.up = assets.pixel.tint(new Color(0.025f, 0.035f, 0.055f, 0.65f));
        buttonStyle.over = assets.pixel.tint(new Color(0.20f, 0.28f, 0.34f, 0.9f));
        buttonStyle.down = assets.pixel.tint(new Color(0.30f, 0.39f, 0.44f, 0.95f));
        if(skin!=null&&skin.has("hud",TextButton.TextButtonStyle.class))buttonStyle=skin.get("hud",TextButton.TextButtonStyle.class);
        next = button("TIẾP TỤC", buttonStyle, layout.controlFontScale, () -> continueRequested = true);
        TextButton menu = button("MENU / Esc", buttonStyle, layout.controlFontScale, () -> menuRequested = true);
        Table controls = new Table();
        controls.add(next).size(layout.controlsWidth, layout.buttonHeight).padBottom(layout.hudButtonGap).row();
        controls.add(menu).size(layout.controlsWidth, layout.buttonHeight);
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
        next.setText(complete ? "TIẾP TỤC" : "HIỆN HẾT");
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
