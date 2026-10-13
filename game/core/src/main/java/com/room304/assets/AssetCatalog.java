package com.room304.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.*;

/** Metadata only: no GPU/audio resources. Paths are relative to game/assets. */
public final class AssetCatalog {
    public static final String MANIFEST = "config/asset-manifest.json";
    public record Entry(String id, String path, String type, boolean required, String filter,
                        int frameWidth, int frameHeight, int frameCount, float frameDuration) {}
    private final Map<String, Entry> entries;
    private final Map<String, Entry> paths;

    private AssetCatalog(Map<String, Entry> entries, Map<String, Entry> paths) {
        this.entries = Collections.unmodifiableMap(entries);
        this.paths = Collections.unmodifiableMap(paths);
    }
    public static AssetCatalog load() { return load(Gdx.files.internal(MANIFEST)); }
    public static AssetCatalog load(FileHandle file) {
        try {
            JsonValue root = new JsonReader().parse(file);
            if (root.getInt("version", 0) != 1 || root.get("assets") == null || !root.get("assets").isArray())
                throw new IllegalArgumentException("Expected version 1 and assets array");
            Map<String, Entry> ids = new LinkedHashMap<>(), paths = new LinkedHashMap<>();
            for (JsonValue value : root.get("assets")) {
                Entry e = new Entry(value.getString("id"), value.getString("path"), value.getString("type"),
                    value.getBoolean("required", true), value.getString("filter", "Nearest"),
                    value.getInt("frameWidth", 0), value.getInt("frameHeight", 0), value.getInt("frameCount", 0),
                    value.getFloat("frameDuration", 0));
                if (!e.id.matches("[a-z0-9_.-]+") || !validPath(e.path)
                    || !Set.of("texture", "music", "sound", "file").contains(e.type)
                    || !Set.of("Nearest", "Linear").contains(e.filter))
                    throw new IllegalArgumentException("Invalid entry " + e.id + " / " + e.path);
                boolean animated = e.frameWidth != 0 || e.frameHeight != 0 || e.frameCount != 0 || e.frameDuration != 0;
                if (animated && (!e.type.equals("texture") || e.frameWidth <= 0 || e.frameHeight <= 0
                    || e.frameCount <= 0 || !Float.isFinite(e.frameDuration) || e.frameDuration <= 0))
                    throw new IllegalArgumentException("Invalid animation " + e.id);
                if (ids.putIfAbsent(e.id, e) != null || paths.putIfAbsent(e.path, e) != null)
                    throw new IllegalArgumentException("Duplicate ID/path " + e.id + " / " + e.path);
            }
            return new AssetCatalog(ids, paths);
        } catch (RuntimeException e) { throw new IllegalArgumentException("Invalid asset manifest " + file.path() + ": " + e.getMessage(), e); }
    }
    public static boolean validPath(String path) {
        return path != null && path.matches("[a-zA-Z0-9_./-]+") && !path.startsWith("/")
            && Arrays.stream(path.split("/", -1)).noneMatch(p -> p.isEmpty() || p.equals(".") || p.equals(".."));
    }
    public Entry entry(String id) {
        Entry e = entries.get(id);
        if (e == null) throw new IllegalArgumentException("Unknown asset ID " + id + " in " + MANIFEST);
        return e;
    }
    public String path(String id) { return entry(id).path; }
    public Entry byPath(String path) { return paths.get(path); }
    public Collection<Entry> entries() { return entries.values(); }
    public List<String> missing(java.util.function.Predicate<String> exists, boolean requiredOnly) {
        List<String> result = new ArrayList<>();
        for (Entry e : entries.values()) if ((!requiredOnly || e.required) && !exists.test(e.path))
            result.add(e.id + " -> " + e.path);
        return List.copyOf(result);
    }
}
