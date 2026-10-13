package com.room304.audio;

import com.room304.assets.GameAssets;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

/** Optional C0-01 loops. Missing assets are intentionally silent during development. */
public final class OpeningAmbience implements Disposable {
    private final GameAssets assets;
    private final boolean ownsAssets;
    private final Music birds;
    private final Music distantVoices;
    private float fadeTime;

    public OpeningAmbience() { this(null); }

    public OpeningAmbience(GameAssets sharedAssets) {
        ownsAssets = sharedAssets == null;
        assets = ownsAssets ? new GameAssets() : sharedAssets;
        birds = load("audio.school.birds");
        distantVoices = load("audio.school.voices");
    }

    private Music load(String id) {
        Music music = assets.music(id);
        if (music != null) { music.setLooping(true); music.setVolume(0f); }
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
        stop();
        if (ownsAssets) assets.dispose();
    }
}
