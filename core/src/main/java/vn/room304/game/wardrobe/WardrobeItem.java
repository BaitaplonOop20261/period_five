package vn.room304.game.wardrobe;

public class WardrobeItem {

    private final String name;
    private final int count;
    private final String description;

    public WardrobeItem(String name, int count, String description) {
        this.name = name;
        this.count = count;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }
}
