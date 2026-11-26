package com.room304.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.room304.assets.GameAssets;
import com.room304.ui.PixelFrame;
import com.room304.ui.UiFont;

/** Close-up of the original register. Only the caller decides when a photo counts. */
public final class RegisterTiltView implements Disposable {
    private static final Color INK = Color.valueOf("263438");
    private static final Color PAPER = Color.valueOf("dfcaa3");
    private static final Color OLD_INK = Color.valueOf("24495b");
    private static final Color CREAM = Color.valueOf("f1e8d5");
    private static final Color BRASS = Color.valueOf("be9b67");
    private static final Color DARK = Color.valueOf("242522");
    private static final Color BUTTON = Color.valueOf("33332e");
    private static final Color FOCUS = Color.valueOf("524531");
    private static final Color RULE = Color.valueOf("746b56");
    private static final String SEARCH_HINT = "Giữ phím trái/phải hoặc A/D để nghiêng sổ.";
    private static final String INK_HINT = "Đã thấy vết mực. Nghiêng thêm để đọc rõ.";
    private static final String PHOTO_HINT = "Nét mực đã rõ. Chụp lại cả trang sổ.";

    private final Stage stage;
    private final Group book = new Group();
    private final BitmapFont font;
    private final Texture pixel;
    private final Texture register;
    private final float[] vertices = new float[20];
    private final Label oldInk, hint;
    private final TextButton left, right, photo;
    private float tilt, visibility;
    private boolean photoRequested;

    public RegisterTiltView(GameAssets assets, BitmapFont font) {
        this.font = font;
        register = assets.texture("document.register_open");
        stage = new Stage(new FitViewport(640, 480));
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();

        Actor desk = new Actor() {
            @Override public void draw(Batch batch, float parentAlpha) { drawDesk(batch); }
        };
        desk.setTouchable(Touchable.disabled);
        stage.addActor(desk);
        label(stage.getRoot(), "Soi trang sổ", 24, 423, 345, 46, 1.55f, CREAM);
        label(stage.getRoot(), "Ánh sáng cửa sổ", 394, 428, 219, 30, .85f, BRASS).setAlignment(Align.right);

        book.setBounds(20, 119, 600, 294);
        book.setOrigin(Align.center);
        book.setTouchable(Touchable.disabled);
        stage.addActor(book);
        Actor paper = new Actor() {
            @Override public void draw(Batch batch, float parentAlpha) { drawPages(batch); }
        };
        book.addActor(paper);
        label(book, "SỔ LỚP 12A3", 66, 239, 200, 32, 1.17f, INK);
        label(book, "Ngày 11/09/2015", 320, 240, 212, 31, .88f, INK);
        label(book, "HỌ VÀ TÊN", 73, 183, 180, 25, .72f, INK);
        label(book, "ĐIỂM DANH", 331, 183, 170, 25, .72f, INK);
        label(book, "33. Nguyễn Thu Nga", 66, 121, 228, 46, 1.16f, INK);
        label(book, "Số học sinh cần sơ tán: 32", 65, 48, 225, 24, .78f, INK);
        oldInk = label(book, "Có mặt", 340, 109, 150, 38, 1.25f, OLD_INK);
        oldInk.getColor().a = 0;
        label(book, "Vắng", 332, 126, 145, 38, 1.25f, INK);

        hint = label(stage.getRoot(), SEARCH_HINT, 25, 74, 475, 32, .88f, CREAM);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = CREAM;
        style.disabledFontColor = Color.valueOf("aaa597");
        TextureRegion region = new TextureRegion(pixel);
        style.up = new PixelFrame(region, BUTTON, RULE, false);
        style.over = new PixelFrame(region, FOCUS, BRASS, false);
        style.down = new PixelFrame(region, FOCUS, CREAM, false);
        style.disabled = new PixelFrame(region, DARK, RULE, false);
        left = button("tiltLeft", "Nghiêng trái", 25, 174, style);
        right = button("tiltRight", "Nghiêng phải", 209, 174, style);
        photo = button("photograph", "Chụp trang sổ  [Enter]", 397, 217, style);
        photo.setDisabled(true);
        photo.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (!photo.isDisabled()) photoRequested = true;
            }
        });
    }

    private Label label(Group parent, String text, int x, int y, int width, int height, float scale, Color color) {
        Label label = new Label(text, new Label.LabelStyle(font, color));
        UiFont.scale(label, scale);
        label.setBounds(x, y, width, height);
        label.setTouchable(Touchable.disabled);
        parent.addActor(label);
        return label;
    }

    private TextButton button(String name, String text, int x, int width, TextButton.TextButtonStyle style) {
        TextButton button = new TextButton(text, style);
        button.setName(name);
        button.setBounds(x, 20, width, 42);
        UiFont.scale(button.getLabel(), .91f);
        stage.addActor(button);
        return button;
    }

    private void drawDesk(Batch batch) {
        float previous = batch.getPackedColor();
        fill(batch, 0, 0, 640, 480, DARK, 1);
        // Window light crosses the desk in two panes. It stays still as the book moves.
        light(batch, 155, 487, 225, 125, 431, .10f);
        light(batch, 258, 590, 225, 125, 431, .08f);
        fill(batch, 24, 422, 592, 1, BRASS, .56f);
        fill(batch, 24, 111, 592, 1, BRASS, .56f);
        // The marker follows the angle, including angles where no ink is visible.
        fill(batch, 494, 88, 119, 2, RULE, 1);
        for (int tick = 0; tick < 5; tick++)
            fill(batch, 494 + tick * 29.5f, 86, 1, 7, BRASS, .65f);
        fill(batch, 551 + tilt * 58, 83, 5, 12, CREAM, 1);
        batch.setPackedColor(previous);
    }

    private void drawPages(Batch batch) {
        float previous = batch.getPackedColor();
        if (register != null) {
            batch.setColor(0, 0, 0, .42f);
            batch.draw(register, 4, -7, 600, 294);
            batch.setColor(Color.WHITE);
            batch.draw(register, 0, 0, 600, 294);
        } else {
            fill(batch, 22, 12, 557, 266, RULE, 1);
            fill(batch, 30, 19, 267, 253, PAPER, 1);
            fill(batch, 302, 19, 268, 253, PAPER, 1);
            for (int y = 47; y <= 220; y += 17) {
                fill(batch, 47, y, 239, 1, RULE, .55f);
                fill(batch, 316, y, 237, 1, RULE, .55f);
            }
        }
        // Grazing light moves across the attendance column as the right edge is lifted.
        float beamX = 450 - 72 * tilt;
        light(batch, beamX - 39, beamX, 47, 32, 258, .05f + visibility * .25f);
        batch.setPackedColor(previous);
    }

    private void fill(Batch batch, float x, float y, float width, float height, Color color, float alpha) {
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(pixel, x, y, width, height);
    }

    /** A quadrilateral with a bright edge and a feathered edge, using the same white pixel as the controls. */
    private void light(Batch batch, float bottomLeft, float topLeft, float width, float bottom, float top, float alpha) {
        float clear = Color.toFloatBits(1, .92f, .71f, 0);
        float bright = Color.toFloatBits(1, .92f, .71f, alpha);
        vertex(0, bottomLeft, bottom, clear);
        vertex(5, topLeft, top, clear);
        vertex(10, topLeft + width, top, bright);
        vertex(15, bottomLeft + width, bottom, bright);
        batch.draw(pixel, vertices, 0, vertices.length);
    }

    private void vertex(int i, float x, float y, float color) {
        vertices[i] = x;
        vertices[i + 1] = y;
        vertices[i + 2] = color;
        // All four vertices sample the single white texel.
    }

    public Stage stage() { return stage; }

    public void resize(int w, int h) {
        if (w > 0 && h > 0) stage.getViewport().update(w, h, true);
    }

    public void draw(float delta, float tilt, float visibility, boolean canPhoto) {
        this.tilt = MathUtils.clamp(tilt, -1, 1);
        this.visibility = MathUtils.clamp(visibility, 0, 1);
        book.setRotation(this.tilt * 5);
        book.setScale(1 - Math.abs(this.tilt) * .065f, 1);
        oldInk.getColor().a = this.visibility;
        photo.setDisabled(!canPhoto);
        hint.setText(canPhoto ? PHOTO_HINT : this.visibility > .17f ? INK_HINT : SEARCH_HINT);
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    public float pointerDirection() {
        return (right.getClickListener().isPressed() ? 1 : 0) - (left.getClickListener().isPressed() ? 1 : 0);
    }

    public boolean consumePhoto() {
        boolean result = photoRequested;
        photoRequested = false;
        return result;
    }

    public void clearCommands() {
        stage.cancelTouchFocus();
        photoRequested = false;
    }

    @Override public void dispose() {
        stage.dispose();
        pixel.dispose();
    }
}
