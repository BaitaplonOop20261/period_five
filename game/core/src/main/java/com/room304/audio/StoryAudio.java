package com.room304.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Disposable;
import com.room304.assets.*;
import com.room304.dialogue.*;
import com.room304.screen.AudioSettings;
import com.room304.story.*;
import java.util.*;
import java.util.function.Function;

/** Optional clips; per-instance sound handles, never shared Sound.stop/pause/dispose. */
public final class StoryAudio implements Disposable {
    private record Binding(String asset,String channel,boolean loop,boolean decorative) {}
    private record Playing(Sound sound,long id,Binding binding) {}
    private final AudioSettings settings;
    private final Function<String,Sound> sounds;
    private final Function<String,Music> musicAssets;
    private final Map<String,List<Binding>> bindings=new HashMap<>();
    private final Map<String,String> ambience=new HashMap<>();
    private final List<Playing> clips=new ArrayList<>();
    private DialogueLine currentLine;
    private Binding spokenBinding,interactionBinding;
    private String currentAmbience,stepAsset;
    private float stepDistance,travel,duck=1;
    private Sound step;private long stepId=-1;
    private Music music;private boolean wasPaused,wasTextComplete;
    public StoryAudio(GameAssets assets,AudioSettings settings,StoryContent content){
        this(Gdx.files.internal(assets.catalog().path("story.audio")),assets.catalog(),assets::sound,assets::music,settings,content);
    }
    /** Same backend contract for deterministic tests without an audio device. */
    public StoryAudio(FileHandle config,AssetCatalog assets,Function<String,Sound> sounds,Function<String,Music> musicAssets,AudioSettings settings,StoryContent content){
        this.settings=settings;this.sounds=sounds;this.musicAssets=musicAssets;
        try{
            JsonValue data=new JsonReader().parse(config);if(data.getInt("version")!=1)throw new IllegalArgumentException("Unsupported audio config");
            for(JsonValue v:data.get("ambience")){String[] key=v.name.split("/",-1);if(key.length!=2)throw new IllegalArgumentException("Invalid ambience key "+v.name);GameState.RoomId.valueOf(key[0]);GameState.Phenomenon.valueOf(key[1]);type(assets,v.asString(),"music");ambience.put(v.name,v.asString());}
            for(JsonValue v:data.get("bindings")){
                String cue=v.getString("cue"),asset=v.getString("asset"),channel=v.getString("channel");type(assets,asset,"sound");
                boolean decorative=v.getBoolean("decorative",false);if(!Set.of("voice","fx").contains(channel)||!content.hasLine(cue)||(decorative&&!channel.equals("fx")))throw new IllegalArgumentException("Invalid audio binding "+cue);
                List<Binding> values=bindings.computeIfAbsent(cue,k->new ArrayList<>());if(values.stream().anyMatch(b->b.asset().equals(asset)))throw new IllegalArgumentException("Duplicate audio binding "+cue+"/"+asset);
                values.add(new Binding(asset,channel,v.getBoolean("loop",false),decorative));
            }
            JsonValue dialogue=data.get("dialogue");if(dialogue!=null){
                String spoken=dialogue.getString("spoken"),other=dialogue.getString("other"),channel=dialogue.getString("channel","fx");
                type(assets,spoken,"sound");type(assets,other,"sound");if(!Set.of("voice","fx").contains(channel))throw new IllegalArgumentException("Invalid dialogue audio channel "+channel);
                spokenBinding=new Binding(spoken,channel,true,false);interactionBinding=new Binding(other,channel,true,false);
            }
            JsonValue steps=data.get("footsteps");if(steps!=null){stepAsset=steps.getString("asset");type(assets,stepAsset,"sound");stepDistance=steps.getFloat("distance");if(!Float.isFinite(stepDistance)||stepDistance<=0)throw new IllegalArgumentException("Invalid footstep distance");}
        }catch(RuntimeException e){throw new IllegalArgumentException("Invalid story audio "+config.path()+": "+e.getMessage(),e);}
    }
    private static void type(AssetCatalog assets,String id,String expected){if(!assets.entry(id).type().equals(expected))throw new IllegalArgumentException("Expected "+expected+": "+id);}
    private static boolean isNaturalDialogue(DialogueController dialogue,DialogueLine line){
        if("call".equals(line.getSpeakerId()))return false;
        String speaker=dialogue.getSpeakerName();
        return speaker!=null&&!speaker.isBlank()&&!"Giọng điểm danh".equals(speaker);
    }
    private float volume(Binding b){return settings.level(b.channel())*(b.decorative()? .4f+(duck-.25f)*.8f:1);}
    public void update(StorySession session,boolean paused,float delta){update(session,paused,delta,0,false);}
    public void update(StorySession session,boolean paused,float delta,float distance){update(session,paused,delta,distance,false);}
    public void update(StorySession session,boolean paused,float delta,float distance,boolean textComplete){
        if(!Float.isFinite(delta)||delta<0||!Float.isFinite(distance)||distance<0)throw new IllegalArgumentException("Invalid audio update");
        boolean reading=session.dialogue().isActive()&&!session.dialogue().getLine().getText().isBlank();if(!paused)duck=reading?.25f:Math.min(1,duck+delta*3);
        String desired=ambience.get(session.state().room().name()+"/"+session.state().phenomenon().name());
        // The first haunt silences school ambience before the register transaction commits.
        if("C1-04-HAUNT".equals(session.block()))desired=null;
        if(!Objects.equals(desired,currentAmbience)){if(music!=null)music.stop();currentAmbience=desired;music=desired==null?null:musicAssets.apply(desired);if(music!=null){music.setLooping(true);if(!paused)music.play();}}
        if(music!=null){music.setVolume(settings.level("music")*duck);if(paused&&!wasPaused)music.pause();else if(!paused&&!music.isPlaying())music.play();}
        DialogueLine active=session.dialogue().isActive()?session.dialogue().getLine():null;
        if(!paused){
            if(active!=currentLine){
                stopClips();currentLine=active;wasTextComplete=textComplete;
                if(!textComplete)playLine(session,active);
            }else if(active!=null){
                if(textComplete&&!wasTextComplete)stopClips();
                else if(!textComplete&&wasTextComplete)playLine(session,active);
                wasTextComplete=textComplete;
            }
        }
        for(Playing p:clips){p.sound().setVolume(p.id(),volume(p.binding()));if(paused&&!wasPaused)p.sound().pause(p.id());else if(!paused&&wasPaused)p.sound().resume(p.id());}
        if(stepAsset!=null){
            if(!paused&&session.exploring()&&distance>0){travel+=distance;if(travel>=stepDistance){travel%=stepDistance;if(step!=null&&stepId>=0)step.stop(stepId);step=sounds.apply(stepAsset);stepId=step==null?-1:step.play(settings.level("fx")*(.4f+(duck-.25f)*.8f));}}
            else if(!paused){travel=0;if(step!=null&&stepId>=0){step.stop(stepId);stepId=-1;}}
            if(step!=null&&stepId>=0){step.setVolume(stepId,settings.level("fx")*(.4f+(duck-.25f)*.8f));if(paused&&!wasPaused)step.pause(stepId);else if(!paused&&wasPaused)step.resume(stepId);}
        }
        wasPaused=paused;
    }
    private void playLine(StorySession session,DialogueLine active){
        List<Binding> list=active==null||active.getId()==null?List.of():bindings.getOrDefault(active.getId(),List.of());
        if(list.isEmpty()&&active!=null&&!active.getText().isBlank()){
            Binding fallback=isNaturalDialogue(session.dialogue(),active)?spokenBinding:interactionBinding;
            if(fallback!=null)list=List.of(fallback);
        }
        for(Binding b:list){Sound sound=sounds.apply(b.asset());if(sound!=null){long id=b.loop()?sound.loop(volume(b)):sound.play(volume(b));if(id>=0)clips.add(new Playing(sound,id,b));}}
    }
    private void stopClips(){for(Playing p:clips)p.sound().stop(p.id());clips.clear();}
    @Override public void dispose(){if(music!=null)music.stop();stopClips();if(step!=null&&stepId>=0)step.stop(stepId);}
}
