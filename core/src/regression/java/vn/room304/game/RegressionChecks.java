package vn.room304.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import vn.room304.game.dialogue.Dialogue;
import vn.room304.game.dialogue.DialogueController;
import vn.room304.game.dialogue.DialogueLine;
import vn.room304.game.dialogue.DialogueLoader;
import vn.room304.game.wardrobe.WardrobeController;
import vn.room304.game.wardrobe.WardrobeItem;

/** Dependency-free logic checks; run with Gradle core:regressionTest (assertions enabled). */
public final class RegressionChecks {

    private RegressionChecks() {
    }

    public static void main(String[] args) throws Exception {
        if (!RegressionChecks.class.desiredAssertionStatus()) {
            throw new IllegalStateException("Run with assertions enabled (-ea).");
        }
        Input previousInput = Gdx.input;
        com.badlogic.gdx.Files previousFiles = Gdx.files;
        try {
            checkTransfers();
            checkInvalidSlots();
            checkFullInventories();
            checkHotbarTransfer();
            checkWardrobeInput();
            checkDialogueProgression();
            checkDialogueLoading(Path.of(args[0]));
            System.out.println("Regression checks passed: 7 scenarios (inventory, input, dialogue, JSON validation).");
        } finally {
            Gdx.input = previousInput;
            Gdx.files = previousFiles;
        }
    }

    private static void checkTransfers() {
        WardrobeController controller = new WardrobeController();
        WardrobeItem item = controller.getWardrobeItem(0);
        WardrobeItem startingItem = controller.getPlayerItem(0);
        controller.transferWardrobeToPlayer(0);
        assert controller.getWardrobeItem(0) == null : "Source slot must be emptied";
        assert controller.getPlayerItem(0) == startingItem : "Occupied slots must be preserved";
        assert controller.getPlayerItem(2) == item : "Transfer must use the first free slot";
        controller.transferPlayerToWardrobe(2);
        assert controller.getPlayerItem(2) == null;
        assert controller.getWardrobeItem(0) == item : "Round trip must preserve item and count";
    }

    private static void checkInvalidSlots() {
        WardrobeController controller = new WardrobeController();
        WardrobeItem item = controller.getWardrobeItem(0);
        WardrobeItem playerItem = controller.getPlayerItem(0);
        controller.transferWardrobeToPlayer(-1);
        controller.transferWardrobeToPlayer(WardrobeController.WARDROBE_SLOTS);
        controller.transferPlayerToWardrobe(-1);
        controller.transferPlayerToWardrobe(WardrobeController.INVENTORY_SLOTS + WardrobeController.HOTBAR_SLOTS);
        controller.transferWardrobeToPlayer(2); // Empty slot.
        controller.transferPlayerToWardrobe(2);
        assert controller.getWardrobeItem(0) == item;
        assert controller.getPlayerItem(0) == playerItem;
        assert controller.getWardrobeItem(-1) == null;
        assert controller.getPlayerItem(36) == null;
    }

    private static void checkFullInventories() throws ReflectiveOperationException {
        WardrobeController controller = new WardrobeController();
        WardrobeItem source = controller.getWardrobeItem(0);
        WardrobeItem occupied = new WardrobeItem("Occupied", 1, "Test fixture");
        Arrays.fill(slots(controller, "playerItems"), occupied);
        controller.transferWardrobeToPlayer(0);
        assert controller.getWardrobeItem(0) == source : "Full inventory must not destroy source";
        for (int i = 0; i < WardrobeController.INVENTORY_SLOTS + WardrobeController.HOTBAR_SLOTS; i++) {
            assert controller.getPlayerItem(i) == occupied : "Full inventory must not overwrite items";
        }
        Arrays.fill(slots(controller, "wardrobeItems"), occupied);
        controller.transferPlayerToWardrobe(0);
        assert controller.getPlayerItem(0) == occupied;
        for (int i = 0; i < WardrobeController.WARDROBE_SLOTS; i++) {
            assert controller.getWardrobeItem(i) == occupied;
        }
    }

    private static void checkHotbarTransfer() throws ReflectiveOperationException {
        WardrobeController controller = new WardrobeController();
        WardrobeItem item = controller.getWardrobeItem(0);
        WardrobeItem occupied = new WardrobeItem("Occupied", 1, "Test fixture");
        Arrays.fill(slots(controller, "playerItems"), 0, WardrobeController.INVENTORY_SLOTS, occupied);
        controller.transferWardrobeToPlayer(0);
        assert controller.getPlayerItem(WardrobeController.INVENTORY_SLOTS) == item;
        controller.transferPlayerToWardrobe(WardrobeController.INVENTORY_SLOTS);
        assert controller.getPlayerItem(WardrobeController.INVENTORY_SLOTS) == null;
        assert controller.getWardrobeItem(0) == item;
    }

    // Capacity fixtures require private storage access; production API stays unchanged.
    private static WardrobeItem[] slots(WardrobeController controller, String name)
        throws ReflectiveOperationException {
        Field field = WardrobeController.class.getDeclaredField(name);
        field.setAccessible(true);
        return (WardrobeItem[]) field.get(controller);
    }

    private static void checkWardrobeInput() {
        WardrobeController controller = new WardrobeController();
        controller.open();
        press(-1);
        controller.update();
        assert controller.isOpen();
        for (int key : new int[] {Input.Keys.F, Input.Keys.ESCAPE, Input.Keys.E}) {
            controller.open();
            press(key);
            controller.update();
            assert !controller.isOpen() : "Each supported close key must close the wardrobe";
        }
    }

    private static void checkDialogueProgression() {
        DialogueController controller = new DialogueController();
        Dialogue dialogue = new Dialogue("test", List.of(
            new DialogueLine("Trang", "First", "portrait.png"),
            new DialogueLine("Trang", "Second", null)));
        controller.start(dialogue);
        assert controller.isActive();
        assert controller.getSpeakerName().equals("Trang");
        assert controller.getSpeakerPortraitTexturePath().equals("portrait.png");
        press(-1);
        controller.update();
        assert controller.getCurrentLine().equals("First") : "No key must preserve current line";
        press(Input.Keys.F);
        controller.update();
        assert controller.getCurrentLine().equals("Second");
        assert controller.getSpeakerPortraitTexturePath() == null;
        press(Input.Keys.ENTER);
        controller.update();
        assert !controller.isActive() : "Last line must close instead of reading past the list";
        controller.update(); // Inactive update is safe.
        controller.start(dialogue);
        assert controller.getCurrentLine().equals("First") : "Restart must reset line index";
        controller.start(new Dialogue("empty", List.of()));
        assert !controller.isActive();
        controller.start(dialogue);
        controller.start(null);
        assert !controller.isActive();
    }

    private static void checkDialogueLoading(Path directory) throws Exception {
        Files.createDirectories(directory);
        Gdx.files = (com.badlogic.gdx.Files) Proxy.newProxyInstance(
            com.badlogic.gdx.Files.class.getClassLoader(), new Class<?>[] {com.badlogic.gdx.Files.class},
            (proxy, method, args) -> {
                if (method.getName().equals("internal")) {
                    return new FileHandle(directory.resolve((String) args[0]).toFile());
                }
                throw new UnsupportedOperationException(method.getName());
            });
        Files.writeString(directory.resolve("valid.json"),
            "{\"id\":\"intro\",\"lines\":[{\"speaker\":\"Trang\",\"text\":\"Hello\"}]}");
        Dialogue loaded = DialogueLoader.load("valid.json");
        assert loaded.getId().equals("intro");
        assert loaded.getLineCount() == 1;
        assert loaded.getLine(0).getText().equals("Hello");
        assert loaded.getLine(0).getPortrait() == null : "Portrait must remain optional";
        String[] invalid = {
            "{}",
            "{\"id\":\"test\",\"lines\":[]}",
            "{\"id\":\"test\",\"lines\":[null]}",
            "{\"id\":\"test\",\"lines\":[{\"speaker\":\"Trang\",\"text\":\" \"}]}",
            "{\"id\":\"test\",\"lines\":[{\"text\":\"Hello\"}]}",
            "{"
        };
        for (int i = 0; i < invalid.length; i++) {
            String path = "invalid-" + i + ".json";
            Files.writeString(directory.resolve(path), invalid[i]);
            expectInvalidDialogue(path);
        }
        expectInvalidDialogue("missing.json");
    }

    private static void expectInvalidDialogue(String path) {
        try {
            DialogueLoader.load(path);
            throw new AssertionError("Invalid dialogue accepted: " + path);
        } catch (IllegalArgumentException expected) {
            assert expected.getMessage().contains(path) : "Diagnostic must identify the asset";
        }
    }

    private static void press(int key) {
        Gdx.input = (Input) Proxy.newProxyInstance(Input.class.getClassLoader(), new Class<?>[] {Input.class},
            (proxy, method, args) -> {
                if (method.getName().equals("isKeyJustPressed")) {
                    return (int) args[0] == key;
                }
                throw new UnsupportedOperationException(method.getName());
            });
    }
}
