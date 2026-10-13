package vn.room304.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.List;
import vn.room304.game.Npc;
import vn.room304.game.Player;

/**
 * Hiển thị bảng tên (nameplate) nhân vật ở ngay trên đầu trong không gian thế giới.
 */
public class CharacterNameplateRenderer implements Disposable {

    private static final float PADDING_X = 8f;
    private static final float PADDING_Y = 4f;
    private static final float HEAD_OFFSET_Y = 6f;

    private final FreeTypeFontGenerator fontGenerator;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final List<Texture> textures = new ArrayList<>();

    private final NinePatch playerBadge;
    private final NinePatch npcBadge;

    private final Color playerTextColor = new Color(0.95f, 0.98f, 1f, 1f);
    private final Color npcTextColor = new Color(1f, 0.91f, 0.70f, 1f);

    public CharacterNameplateRenderer() {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/NotoSans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        parameter.incremental = true;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.borderWidth = 1f;
        parameter.borderColor = new Color(0.08f, 0.05f, 0.03f, 0.85f);
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.shadowColor = new Color(0f, 0f, 0f, 0.5f);
        font = fontGenerator.generateFont(parameter);

        // Huy hiệu người chơi: Nền tối kết hợp viền xanh dịu nổi bật vai trò nhân vật chính
        playerBadge = createNinePatch(
            8, 1,
            new Color(0.10f, 0.13f, 0.17f, 0.82f),
            new Color(0.55f, 0.78f, 0.95f, 0.90f)
        );

        // Huy hiệu NPC: Nền gỗ nâu trầm kết hợp viền vàng ấm đồng điệu phong cách phòng 304
        npcBadge = createNinePatch(
            8, 1,
            new Color(0.18f, 0.10f, 0.06f, 0.82f),
            new Color(0.91f, 0.65f, 0.28f, 0.90f)
        );
    }

    private NinePatch createNinePatch(int size, int border, Color bgColor, Color borderColor) {
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
        return new NinePatch(texture, slice, slice, slice, slice);
    }

    public void render(SpriteBatch batch, Player player) {
        if (player == null || player.getName() == null || player.getName().isEmpty()) {
            return;
        }
        float centerX = player.getX() + player.getBounds().width / 2f;
        float topY = player.getY() + Player.SPRITE_HEIGHT + HEAD_OFFSET_Y;
        renderNameplate(batch, player.getName(), centerX, topY, playerBadge, playerTextColor);
    }

    public void render(SpriteBatch batch, Npc npc) {
        if (npc == null || npc.getName() == null || npc.getName().isEmpty()) {
            return;
        }
        float centerX = npc.getBounds().x + npc.getBounds().width / 2f;
        float topY = npc.getY() + Npc.SPRITE_HEIGHT + HEAD_OFFSET_Y;
        renderNameplate(batch, npc.getName(), centerX, topY, npcBadge, npcTextColor);
    }

    public void renderNameplate(SpriteBatch batch, String name, float centerX, float bottomY,
                                NinePatch badge, Color textColor) {
        font.setColor(textColor);
        layout.setText(font, name);
        float badgeWidth = layout.width + PADDING_X * 2f;
        float badgeHeight = layout.height + PADDING_Y * 2f;
        float badgeX = centerX - badgeWidth / 2f;
        float badgeY = bottomY;

        // Vẽ viền và nền badge
        badge.draw(batch, badgeX, badgeY, badgeWidth, badgeHeight);

        // Vẽ tên căn giữa
        float textX = centerX - layout.width / 2f;
        float textY = badgeY + badgeHeight - PADDING_Y;
        font.draw(batch, layout, textX, textY);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        font.dispose();
        fontGenerator.dispose();
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
    }
}
