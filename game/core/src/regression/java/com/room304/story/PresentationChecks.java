package com.room304.story;

import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.files.FileHandle;
import com.room304.assets.AssetCatalog;
import com.room304.audio.StoryAudio;
import com.room304.screen.AudioSettings;
import com.room304.world.*;
import java.lang.reflect.Proxy;
import java.nio.file.*;
import java.util.*;

/** Binding, mixing and ownership checks against the actual runtime adapters. */
final class PresentationChecks {
    private static final class Backend {
        int plays,pauses,resumes,stops,disposes;boolean playing;float volume;
        Object proxy(Class<?> type){return Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(p,m,a)->{
            switch(m.getName()){
                case "play","loop" -> {plays++;playing=true;if(a!=null&&a.length>0&&a[0] instanceof Float)volume=(Float)a[0];if(m.getReturnType()==long.class)return (long)plays;}
                case "setVolume" -> volume=((Number)a[a.length-1]).floatValue();
                case "pause" -> {pauses++;playing=false;}
                case "resume" -> {resumes++;playing=true;}
                case "stop" -> {if(type==Sound.class)assert a.length==1 : "Never stop a shared Sound globally";stops++;playing=false;}
                case "dispose" -> disposes++;
                case "isPlaying" -> {return playing;}
                case "isLooping" -> {return true;}
                case "getVolume","getPosition" -> {return volume;}
            }return null;
        });}
    }
    static void run(Path fixtures,Path assetRoot,AssetCatalog assets,StoryContent content,SceneCatalog world)throws Exception{
        Path visualFile=assetRoot.resolve(assets.path("story.visuals"));StoryVisuals v=StoryVisuals.load(new FileHandle(visualFile.toFile()),assets,content,world);
        assert v.document("DOC-SIGNED-07").equals("document.signed");assert v.document("C3-05-MAIN-T05__7").equals("document.report");assert v.cue("C-02-MAIN-E09").actors().get(0).pose().equals("pose.seated");assert v.document("UNKNOWN")==null;
        String raw=Files.readString(visualFile);Path bad=fixtures.resolve("visual-invalid.json");
        try{Files.writeString(bad,raw.replace("C-02-MAIN-E09","NO-SUCH-CUE"));reject(()->StoryVisuals.load(new FileHandle(bad.toFile()),assets,content,world));Files.writeString(bad,raw.replace("\"anchor\": \"lastdesk\"","\"anchor\": \"no-such-object\""));reject(()->StoryVisuals.load(new FileHandle(bad.toFile()),assets,content,world));}finally{Files.deleteIfExists(bad);}
        String manifest=Files.readString(assetRoot.resolve(AssetCatalog.MANIFEST));int end=manifest.lastIndexOf(']');
        Path fakeManifest=fixtures.resolve("audio-manifest.json");Path audioConfig=fixtures.resolve("audio-config.json");
        try{
            Files.writeString(fakeManifest,manifest.substring(0,end)+",{\"id\":\"test.voice\",\"path\":\"audio/test_voice.wav\",\"type\":\"sound\",\"required\":false},{\"id\":\"test.fx\",\"path\":\"audio/test_fx.wav\",\"type\":\"sound\",\"required\":false},{\"id\":\"test.step\",\"path\":\"audio/test_step.wav\",\"type\":\"sound\",\"required\":false}"+manifest.substring(end));
            AssetCatalog catalog=AssetCatalog.load(new FileHandle(fakeManifest.toFile()));Backend voice=new Backend(),fx=new Backend(),steps=new Backend(),music=new Backend();
            Map<String,Sound> sounds=Map.of("test.voice",(Sound)voice.proxy(Sound.class),"test.fx",(Sound)fx.proxy(Sound.class),"test.step",(Sound)steps.proxy(Sound.class));Music loop=(Music)music.proxy(Music.class);
            StorySession session=new StorySession(content,world,new GameState(),(r,s)->{});session.start(true);String cue=session.dialogue().getLine().getId();
            String config="{\"version\":1,\"ambience\":{\"ROOM_304/NORMAL\":\"audio.school.birds\"},\"footsteps\":{\"asset\":\"test.step\",\"distance\":10},\"bindings\":[{\"cue\":\""+cue+"\",\"asset\":\"test.voice\",\"channel\":\"voice\"},{\"cue\":\""+cue+"\",\"asset\":\"test.fx\",\"channel\":\"fx\",\"decorative\":true}]}";
            Files.writeString(audioConfig,config);AudioSettings settings=new AudioSettings();settings.master=settings.music=settings.voice=settings.fx=1;
            StoryAudio audio=new StoryAudio(new FileHandle(audioConfig.toFile()),catalog,sounds::get,id->loop,settings,content);
            try{
                audio.update(session,false,.01f);assert voice.plays==1&&fx.plays==1;assert Math.abs(voice.volume-1)<.001f&&Math.abs(fx.volume-.4f)<.001f&&Math.abs(music.volume-.25f)<.001f;
                audio.update(session,false,.01f);assert voice.plays==1&&fx.plays==1 : "Clip retriggered every frame";
                audio.update(session,true,.25f);audio.update(session,true,.25f);assert voice.pauses==1&&fx.pauses==1;
                audio.update(session,false,.01f);assert voice.resumes==1&&fx.resumes==1;
                settings.muted=true;audio.update(session,false,.01f);assert voice.volume==0&&music.volume==0;settings.muted=false;
                for(int i=0;i<30&&session.dialogue().isActive();i++){session.update(5);session.advance();}assert session.exploring();
                audio.update(session,false,.25f);assert voice.stops==1&&fx.stops==1&&Math.abs(music.volume-1)<.001f;
                audio.update(session,false,.01f,9);audio.update(session,false,.01f,0);assert steps.plays==0 : "Idle/collision must not emit steps";
                audio.update(session,false,.01f,10);assert steps.plays==1;audio.update(session,true,.1f,100);assert steps.plays==1&&steps.pauses==1;
                audio.update(session,false,.01f,0);assert steps.stops==1;
            }finally{audio.dispose();}
            assert voice.disposes==0&&fx.disposes==0&&music.disposes==0&&steps.disposes==0 : "Adapter disposed borrowed assets";
            Files.writeString(audioConfig,config.replace(cue,"NO-SUCH-CUE"));reject(()->new StoryAudio(new FileHandle(audioConfig.toFile()),catalog,sounds::get,id->loop,settings,content));
            Files.writeString(audioConfig,config.replace("\"distance\":10","\"distance\":0"));reject(()->new StoryAudio(new FileHandle(audioConfig.toFile()),catalog,sounds::get,id->loop,settings,content));
        }finally{Files.deleteIfExists(fakeManifest);Files.deleteIfExists(audioConfig);}
        System.out.println("Presentation checks passed: document/pose refs, invalid bindings, concurrent voice/FX, MIX-01, pause/mute/restore, distance footsteps, borrowed ownership.");
    }
    private static void reject(Runnable run){try{run.run();throw new AssertionError("Invalid presentation binding accepted");}catch(IllegalArgumentException expected){assert expected.getMessage()!=null;}}
}
