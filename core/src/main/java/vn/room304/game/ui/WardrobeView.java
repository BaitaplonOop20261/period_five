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
import com.badlogic.gdx.utils.viewport.FitViewport;
import vn.room304.game.wardrobe.WardrobeController;
import vn.room304.game.wardrobe.WardrobeItem;

import java.util.ArrayList;
import java.util.List;

public class WardrobeView implements Disposable {

    private static final int SLOT_SIZE = 36;

    private final Stage stage;
    private final BitmapFont font;
    private final List<Texture> textures = new ArrayList<>();

    private final Image dimOverlay;
    private final Table mainPanel;
    private final Label tooltipLabel;
    private final TextButton closeButton;
    private boolean wardrobeCloseRequested;

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
        // Keep the wardrobe panel at a predictable size in fullscreen.
        stage = new Stage(new FitViewport(960f, 540f));
        font = new BitmapFont();

        // 1. Semi-transparent background dim
        Texture dimTexture = createSolidTexture(new Color(0f, 0f, 0f, 0.60f));
        dimOverlay = new Image(new TextureRegionDrawable(new TextureRegion(dimTexture)));
        dimOverlay.setFillParent(true);
        dimOverlay.setVisible(false);

        // Warm wood and brass colors drawn from the wardrobe artwork.
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, new Color(1f, 0.87f, 0.58f, 1f));
        Label.LabelStyle sectionStyle = new Label.LabelStyle(font, new Color(0.97f, 0.80f, 0.52f, 1f));
        Label.LabelStyle hintStyle = new Label.LabelStyle(font, new Color(0.79f, 0.66f, 0.49f, 1f));

        NinePatchDrawable panelDrawable = createWoodPanelDrawable();
        NinePatchDrawable slotDrawable = createWoodSlotDrawable();

        // 3. Main container panel
        mainPanel = new Table();
        mainPanel.setBackground(panelDrawable);
        mainPanel.pad(12f, 16f, 12f, 16f);
        mainPanel.setVisible(false);

        // Header: Title & Close Button
        Table header = new Table();
        Label wardrobeTitle = new Label("TU DO PHONG 304", titleStyle);
        wardrobeTitle.setFontScale(1.15f);
        header.add(wardrobeTitle).left().expandX();

        TextButton.TextButtonStyle closeBtnStyle = new TextButton.TextButtonStyle();
        closeBtnStyle.font = font;
        closeBtnStyle.fontColor = new Color(1f, 0.83f, 0.56f, 1f);
        closeBtnStyle.overFontColor = Color.WHITE;
        closeBtnStyle.up = createNinePatchDrawable(12, 2,
            new Color(0.30f, 0.15f, 0.07f, 1f), new Color(0.84f, 0.55f, 0.24f, 1f));
        closeButton = new TextButton("X", closeBtnStyle);
        closeButton.getLabel().setFontScale(0.9f);
        header.add(closeButton).right().size(28f, 26f);
        mainPanel.add(header).growX().padBottom(10f).row();

        Label wardrobeSectionTitle = new Label("DO TRONG TU", sectionStyle);
        wardrobeSectionTitle.setFontScale(0.95f);
        mainPanel.add(wardrobeSectionTitle).left().padBottom(5f).row();

        // Wardrobe Grid (9x3)
        wardrobeGrid = new Table();
        initSlotGrid(wardrobeGrid, wardrobeCells, 9, 3, slotDrawable);
        mainPanel.add(wardrobeGrid).padBottom(8f).row();

        // Player Inventory Section Title
        Label inventoryTitle = new Label("DO CUA BAN", sectionStyle);
        inventoryTitle.setFontScale(0.95f);
        mainPanel.add(inventoryTitle).left().padBottom(5f).row();

        // Player Inventory Grid (9x3)
        playerGrid = new Table();
        initSlotGrid(playerGrid, playerCells, 9, 3, slotDrawable);
        mainPanel.add(playerGrid).padBottom(6f).row();

        // Hotbar Grid (9x1)
        hotbarGrid = new Table();
        initSlotGrid(hotbarGrid, hotbarCells, 9, 1, slotDrawable);
        mainPanel.add(hotbarGrid).padTop(3f).padBottom(8f).row();

        // Footer Tooltip / Hint
        tooltipLabel = new Label("CLICK DE CHUYEN DO  |  F / ESC DE DONG", hintStyle);
        tooltipLabel.setFontScale(0.85f);
        tooltipLabel.setAlignment(Align.center);
        mainPanel.add(tooltipLabel).growX().center().row();

        // Root table to center on screen
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();
        rootTable.add(mainPanel);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                wardrobeCloseRequested = true;
            }
        });

        stage.addActor(dimOverlay);
        stage.addActor(rootTable);
    }

    private void initSlotGrid(Table grid, SlotCell[] cells, int cols, int rows, NinePatchDrawable slotBg) {
        Label.LabelStyle itemStyle = new Label.LabelStyle(font, new Color(1f, 0.93f, 0.79f, 1f));
        Label.LabelStyle countStyle = new Label.LabelStyle(font, new Color(1f, 0.77f, 0.32f, 1f));

        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Table slot = new Table();
                slot.setBackground(slotBg);
                slot.setSize(SLOT_SIZE, SLOT_SIZE);

                Label itemLabel = new Label("", itemStyle);
                itemLabel.setFontScale(0.7f);
                itemLabel.setAlignment(Align.center);
                itemLabel.setEllipsis("..");

                Label countLabel = new Label("", countStyle);
                countLabel.setFontScale(0.65f);
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
        if (wardrobeCloseRequested) {
            controller.close();
            wardrobeCloseRequested = false;
        }
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

    private NinePatchDrawable createWoodPanelDrawable() {
        int size = 24;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.20f, 0.105f, 0.045f, 1f));
        pixmap.fill();
        pixmap.setColor(new Color(0.48f, 0.275f, 0.10f, 1f));
        pixmap.fillRectangle(3, 3, size - 6, size - 6);
        pixmap.setColor(new Color(0.69f, 0.43f, 0.17f, 1f));
        pixmap.drawLine(4, 5, size - 5, 5);
        pixmap.drawLine(4, size - 6, size - 5, size - 6);
        pixmap.setColor(new Color(0.34f, 0.18f, 0.07f, 1f));
        pixmap.drawLine(5, 10, size - 6, 10);
        pixmap.drawLine(5, 17, size - 6, 17);
        pixmap.setColor(new Color(0.88f, 0.62f, 0.27f, 1f));
        pixmap.drawPixel(2, 2);
        pixmap.drawPixel(size - 3, 2);
        pixmap.drawPixel(2, size - 3);
        pixmap.drawPixel(size - 3, size - 3);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        textures.add(texture);
        return new NinePatchDrawable(new NinePatch(texture, 7, 7, 7, 7));
    }

    private NinePatchDrawable createWoodSlotDrawable() {
        int size = 12;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.75f, 0.48f, 0.20f, 1f));
        pixmap.fill();
        pixmap.setColor(new Color(0.28f, 0.15f, 0.075f, 1f));
        pixmap.fillRectangle(2, 2, size - 4, size - 4);
        pixmap.setColor(new Color(0.12f, 0.075f, 0.045f, 1f));
        pixmap.fillRectangle(3, 3, size - 6, size - 6);
        pixmap.setColor(new Color(0.91f, 0.65f, 0.30f, 1f));
        pixmap.drawPixel(1, size - 2);
        pixmap.drawPixel(size - 2, 1);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        textures.add(texture);
        return new NinePatchDrawable(new NinePatch(texture, 3, 3, 3, 3));
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
