package com.room304.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import java.util.*;

/** One owner per application (or standalone test). Borrowers never dispose managed resources. */
public final class GameAssets implements Disposable {
    private final AssetCatalog catalog = AssetCatalog.load();
    private final AssetManager manager = new AssetManager();
    private final Set<String> missing = new HashSet<>();
    private boolean disposed;
    public AssetCatalog catalog() { return catalog; }
    public void validateRequired() {
        List<String> absent = catalog.missing(p -> Gdx.files.internal(p).exists(), true);
        if (!absent.isEmpty()) throw new IllegalArgumentException("Missing required assets in " + AssetCatalog.MANIFEST + ": " + absent);
    }
    public Texture texture(String id) { return load(catalog.entry(id), Texture.class); }
    public Music music(String id) { return load(catalog.entry(id), Music.class); }
    public Sound sound(String id) { return load(catalog.entry(id), Sound.class); }
    /** Existing dialogue JSON may reference paths. New manifest IDs are also accepted. */
    public Texture optionalTexture(String reference) {
        if (reference == null || reference.isBlank()) return null;
        if (!reference.contains("/")) return texture(reference);
        if (!AssetCatalog.validPath(reference)) throw new IllegalArgumentException("Invalid texture reference " + reference);
        AssetCatalog.Entry e = catalog.byPath(reference);
        return load(e == null ? new AssetCatalog.Entry(reference, reference, "texture", false, "Linear", 0, 0, 0, 0) : e, Texture.class);
    }
    private <T> T load(AssetCatalog.Entry e, Class<T> type) {
        if (disposed) throw new IllegalStateException("GameAssets already disposed");
        String expected = type == Texture.class ? "texture" : type == Music.class ? "music" : "sound";
        if (!e.type().equals(expected)) throw new IllegalArgumentException("Asset type mismatch " + e.id() + ": " + e.type());
        if (!Gdx.files.internal(e.path()).exists()) {
            if (e.required()) throw new IllegalArgumentException("Missing required asset " + e.id() + " -> " + e.path());
            if (missing.add(e.path())) Gdx.app.log("Assets", "Missing optional asset " + e.id() + " -> " + e.path());
            return null;
        }
        if (!manager.isLoaded(e.path(), type)) {
            if (type == Texture.class) {
                TextureParameter p = new TextureParameter();
                p.minFilter = p.magFilter = Texture.TextureFilter.valueOf(e.filter());
                manager.load(e.path(), Texture.class, p);
            } else manager.load(e.path(), type);
            try { manager.finishLoadingAsset(e.path()); }
            catch (RuntimeException ex) { throw new IllegalArgumentException("Could not load " + e.id() + " -> " + e.path(), ex); }
        }
        return manager.get(e.path(), type);
    }
    /** Explicit grid metadata; row-major, top row first. No inference of future frame layout. */
    public Animation<TextureRegion> animation(String id) { return animation(id,false); }
    public Animation<TextureRegion> animation(String id,boolean mirrorX) {
        AssetCatalog.Entry e = catalog.entry(id);
        Texture sheet = texture(id);
        if (sheet == null) return null;
        if (e.frameWidth() <= 0 || sheet.getWidth() % e.frameWidth() != 0 || sheet.getHeight() % e.frameHeight() != 0
            || e.frameCount() > (sheet.getWidth() / e.frameWidth()) * (sheet.getHeight() / e.frameHeight()))
            throw new IllegalArgumentException("Sprite grid mismatch " + id + " -> " + e.path());
        TextureRegion[][] grid = TextureRegion.split(sheet, e.frameWidth(), e.frameHeight());
        TextureRegion[] frames = new TextureRegion[e.frameCount()];
        for (int i = 0; i < frames.length; i++) frames[i] = grid[i / grid[0].length][i % grid[0].length];
        if(mirrorX)for(TextureRegion frame:frames)frame.flip(true,false);
        Animation<TextureRegion> animation = new Animation<>(e.frameDuration(), frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }
    /** Per-view Skin borrows the generated Vietnamese font. Caller owns/disposes the Skin. */
    public com.badlogic.gdx.scenes.scene2d.ui.Skin optionalSkin(com.badlogic.gdx.graphics.g2d.BitmapFont font) {
        String path=catalog.path("ui.skin");com.badlogic.gdx.files.FileHandle file=Gdx.files.internal(path);if(!file.exists())return null;
        com.badlogic.gdx.utils.JsonValue definition=new com.badlogic.gdx.utils.JsonReader().parse(file);
        for(com.badlogic.gdx.utils.JsonValue section:definition)if(section.name.endsWith("BitmapFont")&&section.has("default-font"))throw new IllegalArgumentException("Skin default-font is supplied by Room304; use another name for custom fonts: "+path);
        com.badlogic.gdx.scenes.scene2d.ui.Skin skin=new com.badlogic.gdx.scenes.scene2d.ui.Skin();
        try {
            var atlas=file.sibling(file.nameWithoutExtension()+".atlas");if(atlas.exists()){String atlasPath=atlas.path();if(!manager.isLoaded(atlasPath,com.badlogic.gdx.graphics.g2d.TextureAtlas.class)){manager.load(atlasPath,com.badlogic.gdx.graphics.g2d.TextureAtlas.class);manager.finishLoadingAsset(atlasPath);}skin.addRegions(manager.get(atlasPath,com.badlogic.gdx.graphics.g2d.TextureAtlas.class));}
            skin.add("default-font",font);skin.load(file);return skin;
        }catch(RuntimeException e){skin.remove("default-font",com.badlogic.gdx.graphics.g2d.BitmapFont.class);skin.dispose();throw new IllegalArgumentException("Invalid UI skin "+path,e);}
    }
    public static com.badlogic.gdx.scenes.scene2d.utils.Drawable skinDrawable(com.badlogic.gdx.scenes.scene2d.ui.Skin skin,String name,com.badlogic.gdx.scenes.scene2d.utils.Drawable fallback){
        if(skin==null)return fallback;
        if(skin.has(name,com.badlogic.gdx.scenes.scene2d.utils.Drawable.class)||skin.has(name,TextureRegion.class)||skin.has(name,Texture.class)||skin.has(name,com.badlogic.gdx.graphics.g2d.NinePatch.class)||skin.has(name,com.badlogic.gdx.graphics.g2d.Sprite.class))return skin.getDrawable(name);
        return fallback;
    }
    public int loadedCount() { return manager.getLoadedAssets(); }
    @Override public void dispose() { if (!disposed) { disposed = true; manager.dispose(); } }
}
