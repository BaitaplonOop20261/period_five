package com.room304.dialogue;

import com.room304.assets.GameAssets;
import com.room304.ui.PixelFrame;
import com.room304.ui.UiFont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

/** Exclusive owner of UI resources. World textures are never borrowed or disposed here. */
final class DialogueAssets implements Disposable {
    private final GameAssets scenes;
    private final boolean ownsAssets;
    final BitmapFont font;
    final TextureRegionDrawable pixel;
    final TextureRegionDrawable arrow;
    final TextureRegionDrawable avatarFrameDrawable;
    final PixelFrame frame, nameplate, button, buttonFocus, buttonPressed, buttonDisabled;
    private final Texture pixelTexture;
    private final Texture arrowTexture;
    private final Texture avatarFrameTexture;
    private Texture phoneMsgTexture;

    DialogueAssets(GameAssets sharedAssets, DialogueLayout layout) {
        ownsAssets = sharedAssets == null;
        scenes = ownsAssets ? new GameAssets() : sharedAssets;
        if (!Gdx.files.internal(layout.font).exists()) {
            throw new IllegalArgumentException("Dialogue font not found: " + layout.font);
        }
        font = UiFont.create(Gdx.files.internal(layout.font), layout.fontSize);
        font.getData().markupEnabled = true;
        Pixmap solid = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        solid.setColor(Color.WHITE);
        solid.fill();
        pixelTexture = new Texture(solid);
        solid.dispose();
        pixel = new TextureRegionDrawable(pixelTexture);
        // Use actual pixels for the continue icon; font coverage varies for triangle glyphs.
        Pixmap triangle = new Pixmap(5, 7, Pixmap.Format.RGBA8888);
        triangle.setColor(Color.WHITE);
        triangle.fillTriangle(0, 0, 0, 6, 4, 3);
        arrowTexture = new Texture(triangle);
        triangle.dispose();
        arrow = new TextureRegionDrawable(arrowTexture);
        Color gold = new Color(.70f, .55f, .33f, 1);
        Color dark = new Color(.105f, .092f, .085f, 1);
        frame = new PixelFrame(pixel.getRegion(), new Color(dark.r,dark.g,dark.b,layout.frameAlpha), gold, true);
        nameplate = new PixelFrame(pixel.getRegion(), dark, gold, false);
        nameplate.setLeftWidth(4);
        nameplate.setRightWidth(4);
        nameplate.setTopHeight(2);
        nameplate.setBottomHeight(2);
        button = new PixelFrame(pixel.getRegion(), dark, gold, false);
        buttonFocus = new PixelFrame(pixel.getRegion(), new Color(.25f, .19f, .12f, 1), new Color(.96f, .77f, .46f, 1), false);
        buttonPressed = new PixelFrame(pixel.getRegion(), new Color(.16f, .12f, .08f, 1), gold, false);
        buttonDisabled = new PixelFrame(pixel.getRegion(), dark, new Color(.28f, .25f, .20f, 1), false);

        int aw = 94, ah = 122;
        Pixmap avPix = new Pixmap(aw, ah, Pixmap.Format.RGBA8888);
        for (int y = 0; y < ah; y++) {
            for (int x = 0; x < aw; x++) {
                float dx = (x - aw * 0.5f) / (aw * 0.46f);
                float dy = (y - ah * 0.56f) / (ah * 0.48f);
                float radial = (float) Math.sqrt(dx * dx + dy * dy);

                boolean inRect = x >= 2 && x <= aw - 3 && y >= 2 && y <= ah - 3
                    && !((x == 2 || x == aw - 3) && (y == 2 || y == ah - 3));

                if (!inRect) {
                    // Soft outer ambient shadow around the muted frame
                    float ox = Math.max(0f, Math.max(2 - x, x - (aw - 3)));
                    float oy = Math.max(0f, Math.max(2 - y, y - (ah - 3)));
                    float od = (float) Math.sqrt(ox * ox + oy * oy);
                    float outAlpha = Math.max(0f, 0.22f * (1f - od / 3.2f));
                    avPix.drawPixel(x, y, Color.rgba8888(0.02f, 0.02f, 0.025f, outAlpha));
                    continue;
                }

                // Soft dark shadow/vignette backdrop behind the portrait
                float coreShadow = Math.max(0f, 1f - radial * 0.82f);
                float bottomWeight = 0.75f + 0.35f * ((float) y / ah);
                float edgeSoftness = Math.min(
                    Math.min(x - 2, (aw - 3) - x) / 10f,
                    Math.min(y - 2, (ah - 3) - y) / 10f
                );
                edgeSoftness = Math.max(0.25f, Math.min(1f, edgeSoftness));
                float bgAlpha = (0.14f + 0.48f * coreShadow * bottomWeight) * edgeSoftness;
                avPix.drawPixel(x, y, Color.rgba8888(0.04f, 0.035f, 0.032f, Math.min(0.64f, bgAlpha)));
            }
        }
        // Muted/blurred antique-gold outer & inner frame lines
        float edgeFade = 0.24f;
        avPix.setColor(gold.r, gold.g, gold.b, edgeFade);
        avPix.drawLine(4, 2, aw - 5, 2);
        avPix.drawLine(4, ah - 3, aw - 5, ah - 3);
        avPix.drawLine(2, 4, 2, ah - 5);
        avPix.drawLine(aw - 3, 4, aw - 3, ah - 5);
        avPix.setColor(gold.r, gold.g, gold.b, edgeFade * 0.65f);
        avPix.drawPixel(3, 3);
        avPix.drawPixel(aw - 4, 3);
        avPix.drawPixel(3, ah - 4);
        avPix.drawPixel(aw - 4, ah - 4);
        avPix.setColor(gold.r, gold.g, gold.b, 0.10f);
        avPix.drawRectangle(4, 4, aw - 8, ah - 8);
        avatarFrameTexture = new Texture(avPix);
        avatarFrameTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        avPix.dispose();
        avatarFrameDrawable = new TextureRegionDrawable(avatarFrameTexture);
    }

    com.badlogic.gdx.scenes.scene2d.ui.Skin skin() {return scenes.optionalSkin(font);}

    /** Called only on a line/conversation transition in update, never from draw(). */
    TextureRegionDrawable texture(String path) {
        Texture texture = scenes.optionalTexture(path);
        if (texture == null && ("document.msg".equals(path) || "documents/msg.png".equals(path))) {
            if (phoneMsgTexture == null) phoneMsgTexture = createPhoneMsgTexture(640, 480);
            return phoneMsgTexture == null ? null : new TextureRegionDrawable(phoneMsgTexture);
        }
        return texture == null ? null : new TextureRegionDrawable(texture);
    }

    private static Texture createPhoneMsgTexture(int w, int h) {
        try {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = img.createGraphics();
            g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(new java.awt.Color(15, 18, 26, 220));
            g.fillRect(0, 0, w, h);

            int pw = 280, ph = 380;
            int px = (w - pw) / 2, py = 40;

            g.setColor(new java.awt.Color(40, 44, 52));
            g.fillRoundRect(px, py, pw, ph, 24, 24);
            g.setColor(new java.awt.Color(65, 72, 85));
            g.setStroke(new java.awt.BasicStroke(3));
            g.drawRoundRect(px, py, pw, ph, 24, 24);

            int sx = px + 16, sy = py + 24, sw = pw - 32, sh = ph - 50;
            g.setColor(new java.awt.Color(18, 22, 30));
            g.fillRoundRect(sx, sy, sw, sh, 12, 12);
            g.setColor(new java.awt.Color(45, 52, 65));
            g.setStroke(new java.awt.BasicStroke(2));
            g.drawRoundRect(sx, sy, sw, sh, 12, 12);

            g.setColor(new java.awt.Color(30, 36, 48));
            g.fillRect(sx, sy, sw, 22);
            g.setColor(new java.awt.Color(160, 175, 195));
            g.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 10));
            g.drawString("17:45", sx + 10, sy + 15);
            g.drawString("VinaPhone", sx + 60, sy + 15);
            g.drawString("82%", sx + sw - 32, sy + 15);

            g.setColor(new java.awt.Color(38, 48, 66));
            g.fillRect(sx, sy + 24, sw, 30);
            g.setColor(new java.awt.Color(220, 230, 245));
            g.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
            g.drawString("Hộp thư đến: Thầy Hùng", sx + 12, sy + 44);

            g.setColor(new java.awt.Color(28, 38, 52));
            g.fillRoundRect(sx + 10, sy + 65, sw - 20, 180, 10, 10);
            g.setColor(new java.awt.Color(55, 75, 100));
            g.drawRoundRect(sx + 10, sy + 65, sw - 20, 180, 10, 10);

            g.dispose();
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(img, "PNG", out);
            byte[] bytes = out.toByteArray();
            Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
            Texture tex = new Texture(pixmap);
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            pixmap.dispose();
            return tex;
        } catch (Exception e) {
            return null;
        }
    }

    boolean sameTextureReference(String first, String second) {
        if (first == null || second == null) return false;
        String a = first.contains("/") ? first : scenes.catalog().path(first);
        String b = second.contains("/") ? second : scenes.catalog().path(second);
        return a.equals(b);
    }

    @Override public void dispose() {
        if (ownsAssets) scenes.dispose();
        font.dispose();
        pixelTexture.dispose();
        arrowTexture.dispose();
        if (avatarFrameTexture != null) avatarFrameTexture.dispose();
        if (phoneMsgTexture != null) phoneMsgTexture.dispose();
    }
}
