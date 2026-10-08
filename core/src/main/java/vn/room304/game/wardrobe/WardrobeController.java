package vn.room304.game.wardrobe;

public class WardrobeController {

    public static final int WARDROBE_SLOTS = 27;  // 9 x 3 (Minecraft chest slots)
    public static final int INVENTORY_SLOTS = 27; // 9 x 3 (Player inventory slots)
    public static final int HOTBAR_SLOTS = 9;     // 9 x 1 (Player hotbar slots)

    private boolean open = false;
    private final WardrobeItem[] wardrobeItems = new WardrobeItem[WARDROBE_SLOTS];
    private final WardrobeItem[] playerItems = new WardrobeItem[INVENTORY_SLOTS + HOTBAR_SLOTS];

    public WardrobeController() {
        // Preset thematic items in the wardrobe
        wardrobeItems[0] = new WardrobeItem("Ao Khoac Da", 1, "Chiec ao khoac da cu mau nau.");
        wardrobeItems[1] = new WardrobeItem("Chia Khoa Dong", 1, "Chia khoa co khac so 304.");
        wardrobeItems[2] = new WardrobeItem("Nhat Ky", 1, "Cuon nhat ky bi khoa cua phong 304.");
        wardrobeItems[9] = new WardrobeItem("Den Pin", 1, "Den pin cam tay van con pin.");
        wardrobeItems[11] = new WardrobeItem("Bang Gac", 3, "Hop bang gac cu.");
        wardrobeItems[26] = new WardrobeItem("Buc Thu", 1, "La thu roi lai voi dong chu bi nhoi.");

        // Player starting items
        playerItems[0] = new WardrobeItem("The Sinh Vien", 1, "The sinh vien cua ban.");
        playerItems[1] = new WardrobeItem("Dien Thoai", 1, "Dien thoai sap het pin.");
    }

    public void open() {
        this.open = true;
    }

    public void close() {
        this.open = false;
    }

    public void toggle() {
        this.open = !this.open;
    }

    public boolean isOpen() {
        return open;
    }

    public WardrobeItem getWardrobeItem(int index) {
        if (index >= 0 && index < wardrobeItems.length) {
            return wardrobeItems[index];
        }
        return null;
    }

    public WardrobeItem getPlayerItem(int index) {
        if (index >= 0 && index < playerItems.length) {
            return playerItems[index];
        }
        return null;
    }

    public void transferWardrobeToPlayer(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= wardrobeItems.length) return;
        WardrobeItem item = wardrobeItems[slotIndex];
        if (item == null) return;
        for (int i = 0; i < playerItems.length; i++) {
            if (playerItems[i] == null) {
                playerItems[i] = item;
                wardrobeItems[slotIndex] = null;
                return;
            }
        }
    }

    public void transferPlayerToWardrobe(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= playerItems.length) return;
        WardrobeItem item = playerItems[slotIndex];
        if (item == null) return;
        for (int i = 0; i < wardrobeItems.length; i++) {
            if (wardrobeItems[i] == null) {
                wardrobeItems[i] = item;
                playerItems[slotIndex] = null;
                return;
            }
        }
    }
}
