package vn.room304.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** Owns the pause overlay and the black layer used to reveal the world. */
public final class PauseMenuView implements Disposable {
    private final Stage stage = new Stage(new FitViewport(960f, 540f));
    private final Texture pixelTexture;
    private final BitmapFont font;
    private final Image overlay;
    private final Table menu = new Table();

    public PauseMenuView(Runnable onResume, Runnable onReturnToMenu) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/NotoSans-Regular.ttf"));
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 22;
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "Tạm dừng Tiếp tục chơi Về menu";
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;
            font = generator.generateFont(parameter);
        } finally {
            generator.dispose();
        }

        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE);
        pixel.fill();
        pixelTexture = new Texture(pixel);
        pixel.dispose();
        TextureRegionDrawable background = new TextureRegionDrawable(pixelTexture);
        overlay = new Image(background);
        overlay.setFillParent(true);
        stage.addActor(overlay);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.up = background.tint(new Color(0.10f, 0.10f, 0.10f, 1f));
        style.over = background.tint(new Color(0.20f, 0.20f, 0.20f, 1f));
        style.down = background.tint(new Color(0.30f, 0.30f, 0.30f, 1f));

        menu.setFillParent(true);
        Label title = new Label("Tạm dừng", new Label.LabelStyle(font, Color.WHITE));
        title.setFontScale(1.5f);
        menu.add(title).padBottom(32f).row();
        menu.add(button("Tiếp tục chơi", style, onResume)).size(260f, 54f)
            .padBottom(12f).row();
        menu.add(button("Về menu", style, onReturnToMenu)).size(260f, 54f);
        stage.addActor(menu);
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

    public Stage getStage() {
        return stage;
    }

    public void render(float delta) {
        overlay.setColor(0f, 0f, 0f, 0.72f);
        menu.setVisible(true);
        stage.act(delta);
        stage.getViewport().apply();
        stage.draw();
    }

    public void renderFade(float opacity) {
        overlay.setColor(0f, 0f, 0f, opacity);
        menu.setVisible(false);
        stage.getViewport().apply();
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        pixelTexture.dispose();
    }
}
