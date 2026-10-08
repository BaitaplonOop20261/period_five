package vn.room304.game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

/** Optional C0-01 loops. Missing assets are intentionally silent during development. */
public final class OpeningAmbience implements Disposable {
    private final Music birds;
    private final Music distantVoices;
    private float fadeTime;

    public OpeningAmbience() {
        birds = load("audio/ambience/school_birds.ogg");
        distantVoices = load("audio/ambience/school_distant_voices.ogg");
    }

    private Music load(String path) {
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            return null;
        }
        Music music = Gdx.audio.newMusic(file);
        music.setLooping(true);
        music.setVolume(0f);
        return music;
    }

    public void start() {
        stop();
        fadeTime = 0f;
        play(birds);
        play(distantVoices);
    }

    public void update(float delta) {
        fadeTime = Math.min(2f, fadeTime + delta);
        float fade = fadeTime / 2f;
        if (birds != null) birds.setVolume(0.15f * fade);
        if (distantVoices != null) distantVoices.setVolume(0.08f * fade);
    }

    private void play(Music music) {
        if (music != null) music.play();
    }

    public void pause() {
        if (birds != null) birds.pause();
        if (distantVoices != null) distantVoices.pause();
    }

    public void resume() {
        play(birds);
        play(distantVoices);
    }

    public void stop() {
        if (birds != null) {
            birds.stop();
            birds.setVolume(0f);
        }
        if (distantVoices != null) {
            distantVoices.stop();
            distantVoices.setVolume(0f);
        }
    }

    @Override
    public void dispose() {
        if (birds != null) birds.dispose();
        if (distantVoices != null) distantVoices.dispose();
    }
}
