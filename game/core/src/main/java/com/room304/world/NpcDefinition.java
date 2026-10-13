package com.room304.world;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.room304.assets.AssetCatalog;
import java.util.*;

/** Data boundary for map-authored NPCs. Runtime currently uses static demo dialogue; story visibility/selection are not integrated. */
public record NpcDefinition(String id, String name, String animation, String dialogue,
                            float x, float y, float interactionRange) {
    public static List<NpcDefinition> load(FileHandle file, AssetCatalog assets) {
        try {
            JsonValue root = new JsonReader().parse(file);
            if (root.getInt("version", 0) != 1 || root.get("npcs") == null || !root.get("npcs").isArray())
                throw new IllegalArgumentException("Expected version 1 and npcs array");
            List<NpcDefinition> result = new ArrayList<>();
            Set<String> ids = new HashSet<>();
            for (JsonValue v : root.get("npcs")) {
                NpcDefinition d = new NpcDefinition(v.getString("id"), v.getString("name"), v.getString("animation"),
                    v.getString("dialogue"), v.getFloat("x"), v.getFloat("y"), v.getFloat("interactionRange"));
                if (!d.id.matches("[a-z0-9_-]+") || !ids.add(d.id) || d.name.isBlank()
                    || !Float.isFinite(d.x) || !Float.isFinite(d.y) || !Float.isFinite(d.interactionRange) || d.interactionRange < 0)
                    throw new IllegalArgumentException("Invalid/duplicate NPC " + d.id);
                AssetCatalog.Entry animation = assets.entry(d.animation);
                if (!animation.type().equals("texture") || animation.frameWidth() <= 0)
                    throw new IllegalArgumentException("NPC " + d.id + " needs an animation: " + d.animation);
                if (!assets.entry(d.dialogue).type().equals("file"))
                    throw new IllegalArgumentException("NPC " + d.id + " needs dialogue data: " + d.dialogue);
                result.add(d);
            }
            return List.copyOf(result);
        } catch (RuntimeException e) { throw new IllegalArgumentException("Invalid NPC config " + file.path() + ": " + e.getMessage(), e); }
    }
}
