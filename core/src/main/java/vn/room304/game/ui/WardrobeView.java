package vn.room304.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import vn.room304.game.wardrobe.WardrobeController;
import vn.room304.game.wardrobe.WardrobeItem;

import java.util.ArrayList;
import java.util.List;

public class WardrobeView implements Disposable {

    private static final int SLOT_SIZE = 32;

    private final Stage stage;
    private final BitmapFont font;
    private final List<Texture> textures = new ArrayList<>();

    private final Image dimOverlay;
    private final Table mainPanel;
    private final Label tooltipLabel;

    private final Table wardrobeGrid;
    private final Table playerGrid;
    private final Table hotbarGrid;

    private final SlotCell[] wardrobeCells = new SlotCell[WardrobeController.WARDROBE_SLOTS];
    private final SlotCell[] playerCells = new SlotCell[WardrobeController.INVENTORY_SLOTS];
    private final SlotCell[] hotbarCells = new SlotCell[WardrobeController.HOTBAR_SLOTS];

    private static class SlotCell {
        final Table container;
        final Label itemLabel;
        final Label countLabel;

        SlotCell(Table container, Label itemLabel, Label countLabel) {
            this.container = container;
            this.itemLabel = itemLabel;
            this.countLabel = countLabel;
        }
    }

    public WardrobeView() {
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();

        // 1. Semi-transparent background dim
        Texture dimTexture = createSolidTexture(new Color(0f, 0f, 0f, 0.60f));
        dimOverlay = new Image(new TextureRegionDrawable(new TextureRegion(dimTexture)));
        dimOverlay.setFillParent(true);
        dimOverlay.setVisible(false);

        // 2. Minecraft styles
        Color mcTitleColor = new Color(0.24f, 0.24f, 0.24f, 1f);
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, mcTitleColor);
        Label.LabelStyle hintStyle = new Label.LabelStyle(font, new Color(0.40f, 0.40f, 0.40f, 1f));
        Label.LabelStyle tooltipStyle = new Label.LabelStyle(font, new Color(1f, 1f, 0.6f, 1f));

        NinePatchDrawable panelDrawable = createMinecraftPanelDrawable();
        NinePatchDrawable slotDrawable = createMinecraftSlotDrawable(new Color(0.55f, 0.55f, 0.55f, 1f));

        // 3. Main container panel
        mainPanel = new Table();
        mainPanel.setBackground(panelDrawable);
        mainPanel.pad(8f, 12f, 8f, 12f);
        mainPanel.setVisible(false);

        // Header: Title & Close Button
        Table header = new Table();
        Label wardrobeTitle = new Label("Tu Do (Wardrobe)", titleStyle);
        wardrobeTitle.setFontScale(0.95f);
        header.add(wardrobeTitle).left().expandX();

        TextButton.TextButtonStyle closeBtnStyle = new TextButton.TextButtonStyle();
        closeBtnStyle.font = font;
        closeBtnStyle.fontColor = new Color(0.85f, 0.2f, 0.2f, 1f);
        closeBtnStyle.up = createNinePatchDrawable(12, 1, new Color(0.77f, 0.77f, 0.77f, 1f), new Color(0.2f, 0.2f, 0.2f, 1f));
        TextButton closeBtn = new TextButton(" X ", closeBtnStyle);
        closeBtn.getLabel().setFontScale(0.8f);
        header.add(closeBtn).right().size(22f, 20f);
        mainPanel.add(header).growX().padBottom(4f).row();

        // Wardrobe Grid (9x3)
        wardrobeGrid = new Table();
        initSlotGrid(wardrobeGrid, wardrobeCells, 9, 3, slotDrawable);
        mainPanel.add(wardrobeGrid).padBottom(6f).row();

        // Player Inventory Section Title
        Label inventoryTitle = new Label("Tui Do (Inventory)", titleStyle);
        inventoryTitle.setFontScale(0.9f);
        mainPanel.add(inventoryTitle).left().padBottom(3f).row();

        // Player Inventory Grid (9x3)
        playerGrid = new Table();
        initSlotGrid(playerGrid, playerCells, 9, 3, slotDrawable);
        mainPanel.add(playerGrid).padBottom(4f).row();

        // Hotbar Grid (9x1)
        hotbarGrid = new Table();
        initSlotGrid(hotbarGrid, hotbarCells, 9, 1, slotDrawable);
        mainPanel.add(hotbarGrid).padBottom(6f).row();

        // Footer Tooltip / Hint
        tooltipLabel = new Label("[F / ESC / E] Dong  |  Click chuot de chuyen do", hintStyle);
        tooltipLabel.setFontScale(0.8f);
        tooltipLabel.setAlignment(Align.center);
        mainPanel.add(tooltipLabel).growX().center().row();

        // Root table to center on screen
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();
        rootTable.add(mainPanel);

        stage.addActor(dimOverlay);
        stage.addActor(rootTable);
    }

    private void initSlotGrid(Table grid, SlotCell[] cells, int cols, int rows, NinePatchDrawable slotBg) {
        Label.LabelStyle itemStyle = new Label.LabelStyle(font, new Color(1f, 1f, 1f, 1f));
        Label.LabelStyle countStyle = new Label.LabelStyle(font, new Color(1f, 0.9f, 0.2f, 1f));

        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Table slot = new Table();
                slot.setBackground(slotBg);
                slot.setSize(SLOT_SIZE, SLOT_SIZE);

                Label itemLabel = new Label("", itemStyle);
                itemLabel.setFontScale(0.65f);
                itemLabel.setAlignment(Align.center);
                itemLabel.setEllipsis("..");

                Label countLabel = new Label("", countStyle);
                countLabel.setFontScale(0.60f);
                countLabel.setAlignment(Align.bottomRight);

                slot.stack(itemLabel, countLabel).size(SLOT_SIZE - 4, SLOT_SIZE - 4);
                grid.add(slot).size(SLOT_SIZE, SLOT_SIZE).pad(1f);

                cells[index] = new SlotCell(slot, itemLabel, countLabel);
                index++;
            }
            grid.row();
        }
    }

    public void render(WardrobeController controller) {
        boolean open = controller.isOpen();
        dimOverlay.setVisible(open);
        mainPanel.setVisible(open);

        if (!open) {
            return;
        }

        // Update wardrobe slots
        for (int i = 0; i < wardrobeCells.length; i++) {
            WardrobeItem item = controller.getWardrobeItem(i);
            updateCell(wardrobeCells[i], item);
            final int slotIndex = i;
            wardrobeCells[i].container.clearListeners();
            wardrobeCells[i].container.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.transferWardrobeToPlayer(slotIndex);
                }
            });
        }

        // Update player inventory slots
        for (int i = 0; i < playerCells.length; i++) {
            WardrobeItem item = controller.getPlayerItem(i);
            updateCell(playerCells[i], item);
            final int slotIndex = i;
            playerCells[i].container.clearListeners();
            playerCells[i].container.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.transferPlayerToWardrobe(slotIndex);
                }
            });
        }

        // Update hotbar slots (indices 27 to 35 in playerItems)
        for (int i = 0; i < hotbarCells.length; i++) {
            int playerSlotIdx = WardrobeController.INVENTORY_SLOTS + i;
            WardrobeItem item = controller.getPlayerItem(playerSlotIdx);
            updateCell(hotbarCells[i], item);
            final int slotIndex = playerSlotIdx;
            hotbarCells[i].container.clearListeners();
            hotbarCells[i].container.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.transferPlayerToWardrobe(slotIndex);
                }
            });
        }

        stage.act();
        stage.draw();
    }

    private void updateCell(SlotCell cell, WardrobeItem item) {
        if (item != null) {
            String shortName = item.getName();
            if (shortName.length() > 6) {
                shortName = shortName.substring(0, 5) + ".";
            }
            cell.itemLabel.setText(shortName);
            cell.countLabel.setText(item.getCount() > 1 ? String.valueOf(item.getCount()) : "");
        } else {
            cell.itemLabel.setText("");
            cell.countLabel.setText("");
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

    private NinePatchDrawable createMinecraftPanelDrawable() {
        // Classic Minecraft GUI panel: light gray background with 3D beveled borders
        int size = 16;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        // Base background: #C6C6C6
        pixmap.setColor(new Color(0.776f, 0.776f, 0.776f, 1f));
        pixmap.fill();

        // Outer highlight: Top and Left white border
        pixmap.setColor(Color.WHITE);
        pixmap.drawLine(0, 0, size - 1, 0);
        pixmap.drawLine(0, 0, 0, size - 1);

        // Outer shadow: Bottom and Right dark gray border (#373737)
        pixmap.setColor(new Color(0.216f, 0.216f, 0.216f, 1f));
        pixmap.drawLine(size - 1, 0, size - 1, size - 1);
        pixmap.drawLine(0, size - 1, size - 1, size - 1);

        // Inner shadow line (#555555)
        pixmap.setColor(new Color(0.333f, 0.333f, 0.333f, 1f));
        pixmap.drawLine(size - 2, 1, size - 2, size - 2);
        pixmap.drawLine(1, size - 2, size - 2, size - 2);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        textures.add(texture);

        return new NinePatchDrawable(new NinePatch(texture, 4, 4, 4, 4));
    }

    private NinePatchDrawable createMinecraftSlotDrawable(Color fillColor) {
        // Classic Minecraft inset slot: dark shadow on top-left, white highlight on bottom-right
        int size = 16;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        // Slot inner fill: #8B8B8B
        pixmap.setColor(fillColor);
        pixmap.fill();

        // Inset shadow: Top and Left (#373737)
        pixmap.setColor(new Color(0.216f, 0.216f, 0.216f, 1f));
        pixmap.drawLine(0, 0, size - 1, 0);
        pixmap.drawLine(0, 0, 0, size - 1);

        // Inset highlight: Bottom and Right (White)
        pixmap.setColor(Color.WHITE);
        pixmap.drawLine(size - 1, 0, size - 1, size - 1);
        pixmap.drawLine(0, size - 1, size - 1, size - 1);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        textures.add(texture);

        return new NinePatchDrawable(new NinePatch(texture, 2, 2, 2, 2));
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
        return new NinePatchDrawable(new NinePatch(texture, slice, slice, slice, slice));
    }
}
