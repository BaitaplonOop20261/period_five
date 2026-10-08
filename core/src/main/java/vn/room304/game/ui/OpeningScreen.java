package vn.room304.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import vn.room304.game.audio.OpeningAmbience;

/** Menu and C0-01, followed by a transition into the existing playable scene. */
public final class OpeningScreen extends ScreenAdapter {
    private static final float FADE_SECONDS = 1f;
    private static final float EXIT_FADE_SECONDS = 0.4f;
    private static final String TITLE = "CĂN PHÒNG 304";
    private static final String SUBTITLE = "Buổi điểm danh cuối cùng";
    private static final String SCHOOL = "TRƯỜNG THPT AN BÌNH";
    private static final String TIME = "16:40 — NĂM 2025";
    private static final String NOTE = "Khu nhà cũ sẽ được sửa chữa trong thời gian tới.";
    private static final String BUTTON_TEXT = "Bắt đầu Thoát Tiếp tục";
    private static final String HINT = "Enter / F / Space hoặc nhấp nút để tiếp tục";

    private enum Step { MENU, LOCATION, NOTE, TRANSITION }

    private final Stage stage = new Stage(new FitViewport(960f, 540f));
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final BitmapFont smallFont;
    private final Texture buttonTexture;
    private final OpeningAmbience ambience;
    private final Runnable onFinished;
    private final Table menu = new Table();
    private final Table opening = new Table();
    private final Table location = new Table();
    private final Label note;
    private final TextButton next;
    private final Label hint;
    private Step step = Step.MENU;
    private float stepTime;

    public OpeningScreen(Runnable onFinished) {
        this.onFinished = onFinished;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/NotoSans-Regular.ttf"));
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + TITLE + SUBTITLE
                + SCHOOL + TIME + NOTE + BUTTON_TEXT + HINT;
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;
            parameter.size = 36;
            titleFont = generator.generateFont(parameter);
            parameter.size = 20;
            bodyFont = generator.generateFont(parameter);
            parameter.size = 14;
            smallFont = generator.generateFont(parameter);
        } finally {
            generator.dispose();
        }

        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE);
        pixel.fill();
        buttonTexture = new Texture(pixel);
        pixel.dispose();
        TextureRegionDrawable background = new TextureRegionDrawable(buttonTexture);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = bodyFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = background.tint(new Color(0.10f, 0.10f, 0.10f, 1f));
        buttonStyle.over = background.tint(new Color(0.20f, 0.20f, 0.20f, 1f));
        buttonStyle.down = background.tint(new Color(0.30f, 0.30f, 0.30f, 1f));

        menu.setFillParent(true);
        menu.add(label(TITLE, titleFont, Color.WHITE)).padBottom(10f).row();
        menu.add(label(SUBTITLE, bodyFont, Color.LIGHT_GRAY)).padBottom(48f).row();
        menu.add(button("Bắt đầu", buttonStyle, this::start)).size(220f, 52f)
            .padBottom(12f).row();
        menu.add(button("Thoát", buttonStyle, () -> Gdx.app.exit())).size(220f, 52f);
        stage.addActor(menu);

        opening.setFillParent(true);
        location.add(label(SCHOOL, titleFont, Color.WHITE)).padBottom(16f).row();
        location.add(label(TIME, bodyFont, Color.LIGHT_GRAY));
        opening.add(location).padBottom(32f).row();
        note = label(NOTE, bodyFont, Color.LIGHT_GRAY);
        note.setWrap(true);
        opening.add(note).width(820f).height(60f).row();
        stage.addActor(opening);

        Table footer = new Table();
        footer.setFillParent(true);
        footer.bottom().padBottom(32f);
        next = button("Tiếp tục", buttonStyle, this::advance);
        footer.add(next).size(200f, 48f).padBottom(12f).row();
        hint = label(HINT, smallFont, Color.GRAY);
        footer.add(hint);
        stage.addActor(footer);

        ambience = new OpeningAmbience();
        showMenu();
    }

    private Label label(String text, BitmapFont font, Color color) {
        Label label = new Label(text, new Label.LabelStyle(font, color));
        label.setAlignment(Align.center);
        return label;
    }

    private TextButton button(String text, TextButton.TextButtonStyle style, Runnable action) {
        TextButton button = new TextButton(text, style);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return button;
    }

    private void start() {
        step = Step.LOCATION;
        stepTime = 0f;
        menu.setVisible(false);
        opening.setVisible(true);
        opening.clearActions();
        opening.getColor().a = 1f;
        location.clearActions();
        location.getColor().a = 0f;
        location.addAction(Actions.fadeIn(FADE_SECONDS));
        note.clearActions();
        note.getColor().a = 0f;
        next.setText("Tiếp tục");
        setAdvanceVisible(false);
        ambience.start();
    }

    private void advance() {
        if (step == Step.MENU || step == Step.TRANSITION || stepTime < FADE_SECONDS) return;
        if (step == Step.LOCATION) {
            step = Step.NOTE;
            stepTime = 0f;
            note.addAction(Actions.fadeIn(FADE_SECONDS));
            setAdvanceVisible(false);
        } else {
            step = Step.TRANSITION;
            stepTime = 0f;
            setAdvanceVisible(false);
            opening.addAction(Actions.fadeOut(EXIT_FADE_SECONDS));
        }
    }

    private void showMenu() {
        step = Step.MENU;
        menu.setVisible(true);
        opening.setVisible(false);
        setAdvanceVisible(false);
        ambience.stop();
    }

    private void setAdvanceVisible(boolean visible) {
        next.setVisible(visible);
        next.setTouchable(visible ? Touchable.enabled : Touchable.disabled);
        hint.setVisible(visible);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (step != Step.MENU) ambience.resume();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        if (step != Step.TRANSITION && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
            && step != Step.MENU) {
            showMenu();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.F)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (step == Step.MENU) start();
            else advance();
        }

        if (step != Step.MENU) {
            stepTime += delta;
            ambience.update(delta);
            setAdvanceVisible(step != Step.TRANSITION && stepTime >= FADE_SECONDS);
        }
        stage.act(delta);
        stage.getViewport().apply();
        stage.draw();
        if (step == Step.TRANSITION && stepTime >= EXIT_FADE_SECONDS) {
            onFinished.run();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        ambience.pause();
    }

    @Override
    public void resume() {
        if (step != Step.MENU) ambience.resume();
    }

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) Gdx.input.setInputProcessor(null);
        ambience.pause();
    }

    @Override
    public void dispose() {
        hide();
        stage.dispose();
        titleFont.dispose();
        bodyFont.dispose();
        smallFont.dispose();
        buttonTexture.dispose();
        ambience.dispose();
    }
}
