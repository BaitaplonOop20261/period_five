package com.room304.screen;

/** App settings survive replay; progress and replay availability live elsewhere. */
public final class AudioSettings {
    public float master=.7f,music=.3f,fx=.7f,voice=.8f;
    public boolean muted;
    public float level(String channel) { return muted?0:master*switch(channel) {case "music"->music;case "voice"->voice;default->fx;}; }
    public void change(int index,float delta) {
        switch(index) {case 0->master=clamp(master+delta);case 1->music=clamp(music+delta);case 2->fx=clamp(fx+delta);case 3->voice=clamp(voice+delta);default->muted=!muted;}
    }
    private float clamp(float value) { return Math.max(0,Math.min(1,value)); }
}
