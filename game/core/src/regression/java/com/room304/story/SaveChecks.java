package com.room304.story;

import java.nio.file.*;
import java.util.*;
import com.badlogic.gdx.utils.Json;
import static com.room304.story.GameState.*;

/** Save compatibility, recovery and impossible resume states. All files live in test fixtures. */
final class SaveChecks {
    private static void reject(Runnable action){
        try{action.run();throw new AssertionError("Invalid save accepted");}catch(IllegalArgumentException|IllegalStateException expected){}
    }
    static void run(Path fixtures)throws Exception{
        var world=StoryRuntimeChecks.world;
        Path dir=Files.createTempDirectory(fixtures,"save-v2-");
        SaveStore store=new SaveStore(dir.resolve("room304.json"));
        try{
            StorySession s=StoryRuntimeChecks.investigate(false,false);
            var point=world.scene(s.state().room()).spawn("entry");
            store.save(s,point.x,point.y,"left",world);String first=Files.readString(store.path());
            assert store.load().savedAt()>0&&store.load().playthroughId().equals(s.playthroughId());
            assert !store.overwritesAnotherRun(s)&&store.overwritesAnotherRun(StoryRuntimeChecks.newGame());
            s.interact("crate");StoryRuntimeChecks.drain(s);store.save(s,point.x,point.y,"right",world);
            assert store.load().state().has(Item.CASSETTE)&&!store.loadBackup().state().has(Item.CASSETTE);
            byte[] backup=Files.readAllBytes(store.backupPath());
            Files.writeString(store.path(),"{broken");assert !store.inspect(false).valid()&&store.inspect(true).valid();
            store.loadBackup();assert Files.readString(store.path()).equals("{broken");
            store.save(s,point.x,point.y,"up",world);assert Arrays.equals(backup,Files.readAllBytes(store.backupPath()));
            String good=Files.readString(store.path());
            s.toggleNotes();reject(()->store.save(s,point.x,point.y,"up",world));s.toggleNotes();
            assert good.equals(Files.readString(store.path()));
            reject(()->store.save(s,Float.NaN,point.y,"up",world));reject(()->store.save(s,point.x,point.y,"bad",world));
            var npc=world.npcPosition(s.state(),"duc");reject(()->store.save(s,npc.x,npc.y,"up",world));
            for(String change:List.of(good.replace("\"contentVersion\":1","\"contentVersion\":99"),good.replace("\"version\":2","\"version\":99"))){
                Files.writeString(store.path(),change);reject(store::load);
            }
            Files.writeString(store.path()," ".repeat(1_000_001));reject(store::load);
            Files.writeString(store.path(),good);

            // A blocked backup destination must abort before touching the primary.
            Files.delete(store.backupPath());Files.createDirectory(store.backupPath());
            reject(()->store.save(s,point.x,point.y,"up",world));assert good.equals(Files.readString(store.path()));
            Files.delete(store.backupPath());

            SaveStore.Data legacy=new SaveStore.Data();legacy.version=1;
            legacy.progress=ProgressSnapshot.encode(s.state()).replace("\"cabinetLocked\":false,","").replace("\"bookCoverClosed\":false,","");legacy.x=point.x;legacy.y=point.y;legacy.facing="up";
            Path old=dir.resolve("legacy.json");Files.writeString(old,new Json().toJson(legacy));byte[] oldBytes=Files.readAllBytes(old);
            SaveStore migrated=new SaveStore(dir.resolve("new/room304.json"));
            assert migrated.importLegacy(old,world)&&!migrated.importLegacy(old,world);
            assert migrated.load().state().has(Item.CASSETTE)&&Arrays.equals(oldBytes,Files.readAllBytes(old));
            assert Files.readString(migrated.path()).contains("\"version\":2");
            migrated.delete(false);assert !migrated.exists()&&!migrated.importLegacy(old,world):"Deleted slot must not reappear from legacy save";
            assert Arrays.equals(oldBytes,Files.readAllBytes(old));
            migrated.save(s,point.x,point.y,"up",world);migrated.save(s,point.x,point.y,"left",world);
            String kept=Files.readString(migrated.path());migrated.delete(true);
            assert !migrated.hasBackup()&&kept.equals(Files.readString(migrated.path())):"Deleting backup must preserve primary";
            migrated.delete(false);migrated.delete(true);assert !migrated.exists()&&!migrated.hasBackup();
            assert SaveStore.userPath("Windows 11",Map.of("LOCALAPPDATA",dir.toString()),"unused").equals(dir.resolve("Room304/saves/room304.json"));
            assert SaveStore.userPath("Linux",Map.of(),dir.toString()).equals(dir.resolve(".room304/saves/room304.json"));

            // Reproduce the formerly accepted completed LOCK_REGISTER, before the actual trial.
            StorySession key=StoryRuntimeChecks.newGame();key.interact("desk");StoryRuntimeChecks.drain(key);
            key.interact("door.hall");key.interact("stairs");StoryRuntimeChecks.drain(key);
            key.interact("duc");StoryRuntimeChecks.drain(key);StoryRuntimeChecks.choice(key,"C2-02-CHOICE03");
            key.interact("guard.wall");StoryRuntimeChecks.drain(key);assert key.canSave();
            ProgressSnapshot.validateForResume(key.state());
            key.state().completed.add(StoryDirector.Action.LOCK_REGISTER.contentId());
            reject(()->ProgressSnapshot.validateForResume(key.state()));
            SaveStore.Data bad=new Json().fromJson(SaveStore.Data.class,good);bad.progress=ProgressSnapshot.encode(key.state());
            Json full=new Json();full.setUsePrototypes(false);Files.writeString(store.path(),full.toJson(bad));reject(store::load);
            Files.writeString(store.path(),good);
            key.state().completed.remove(StoryDirector.Action.LOCK_REGISTER.contentId());
            key.state().events.add(Event.RETREAT_TRIAL_DONE);key.state().book=BookLocation.LOCKED_CABINET;
            reject(()->ProgressSnapshot.validateForResume(key.state()));
            StorySession ready=StoryRuntimeChecks.ready(false);ready.state().events.remove(Event.FINAL_WARNING_DONE);
            reject(()->ProgressSnapshot.validateForResume(ready.state()));
            ready.state().events.add(Event.FINAL_WARNING_DONE);ready.interact("desk");StoryRuntimeChecks.drain(ready);
            reject(()->ProgressSnapshot.validateForResume(ready.state()));
            System.out.println("Save v2 checks passed: backup recovery, corrupt primary, write failure, v1 migration, stable path, resume invariants.");
        }finally{
            try(var paths=Files.walk(dir)){for(Path path:paths.sorted(Comparator.reverseOrder()).toList())Files.delete(path);}
        }
    }
}
