package com.room304.story;

import com.badlogic.gdx.files.FileHandle;
import com.room304.assets.AssetCatalog;
import com.room304.world.SceneCatalog;
import java.nio.file.*;
import java.util.*;
import static com.room304.story.GameState.*;

/** Drives the same session, content, interaction selectors and callbacks as the playable screen. */
public final class StoryRuntimeChecks {
    public static StoryContent content; public static SceneCatalog world;
    private static final Set<String> visited=new TreeSet<>();
    public static void run(Path fixtures,Path assets) throws Exception {
        AssetCatalog catalog=AssetCatalog.load(new FileHandle(assets.resolve(AssetCatalog.MANIFEST).toFile()));
        content=StoryContent.load(new FileHandle(assets.resolve(catalog.path("story.content")).toFile()));
        world=SceneCatalog.load(new FileHandle(assets.resolve(catalog.path("story.world")).toFile()),catalog);
        world.validateContent(content);PresentationChecks.run(fixtures,assets,catalog,content,world);assert content.blockIds().size()==99;
        StorySession immediate=toDecision();choice(immediate,"C3-04-CHOICE01");assert immediate.ended() && immediate.state().ending()==Ending.B;
        StorySession early=investigate(false,false);early.interact("cabinet");choice(early,"CHO-CAB-LEAVE");drain(early);assert early.ended() && early.state().ending()==Ending.B;
        StorySession a=ready(false);a.interact("desk");drain(a);choice(a,"C6-01-CHOICE03");drain(a);assert a.ended() && a.state().ending()==Ending.A;
        StorySession b=ready(true);b.interact("desk");drain(b);choice(b,"C6-01-CHOICE04");drain(b);assert b.ended() && b.state().ending()==Ending.B && b.state().clues().size()==5;
        StorySession c=investigate(false,false);c.interact("crate");drain(c);go(c,RoomId.CLASSROOM);c.interact("hung");drain(c);assert c.dialogue().getLine().getChoices().stream().noneMatch(o->o.getId().equals("C5-01-CHOICE03"));
        choice(c,"C5-01-CHOICE06");assert !c.state().knows(Clue.CASSETTE_CONTENT);
        StorySession cancel=toDecision();choice(cancel,"C3-04-CHOICE02");drain(cancel);choice(cancel,"C3-04-CHOICE04");assert !cancel.state().reached(Event.INVESTIGATION_STARTED) && cancel.state().book()==BookLocation.LOCKED_CABINET;
        StorySession notes=investigate(false,false);notes.toggleNotes();choice(notes,"BOOK");drain(notes);choice(notes,"CHO-REG-CLOSE");drain(notes);assert notes.state().phenomenon()==Phenomenon.ACTIVE && notes.state().bookCoverClosed();
        checkOptionalRoutes();
        checkMissingAndPermutations();
        checkRemainingBlocks(fixtures,assets,catalog);
        checkInvalidSnapshots();
        checkSaves(fixtures);
        StorySession disabled=ready(false);disabled.interact("desk");drain(disabled);assert disabled.dialogue().getLine().getChoices().stream().noneMatch(o->o.getId().equals("C6-01-CHOICE05"));
        Path enabledFile=Files.createTempFile(fixtures,"world-c-",".json");SceneCatalog original=world;
        try {
            Files.writeString(enabledFile,Files.readString(assets.resolve(catalog.path("story.world"))).replace("\"enableC\": false","\"enableC\": true"));
            world=SceneCatalog.load(new FileHandle(enabledFile.toFile()),catalog);
            StorySession cc=ready(false);cc.interact("desk");drain(cc);choice(cc,"C6-01-CHOICE05");assert cc.block().equals("CONFIRM-C");
            assert cc.dialogue().getLine().getChoices().get(0).getId().equals("C-01-CHOICE02");choice(cc,"C-01-CHOICE02");assert cc.block().equals("FINAL")&&!cc.ended();
            choice(cc,"C6-01-CHOICE05");choice(cc,"C-01-CHOICE01");assert cc.ended()&&cc.state().ending()==Ending.C&&!cc.state().reached(Event.OUTSIDE_ALL)&&cc.state().has(Item.CABINET_KEY);
            GameState decoded=ProgressSnapshot.decode(ProgressSnapshot.encode(cc.state()),"C ending");assert decoded.ending()==Ending.C;
        } finally {world=original;Files.deleteIfExists(enabledFile);}
        Set<String> missing=new TreeSet<>(content.blockIds());missing.removeAll(visited);System.out.println("Unvisited story blocks: "+missing);
        System.out.println("Authored runtime block coverage: "+visited.size()+"/"+content.blockIds().size());
        System.out.println("Story runtime checks passed: full A, early/late B, owned/unheard gate, reopen cancel, notes/book, all 99 blocks loaded.");
    }
    private static void checkOptionalRoutes() {
        StorySession s=newGame();s.interact("player");drain(s);s.interact("photo");drain(s);s.interact("desk");drain(s);s.interact("door.hall");s.interact("board");drain(s);s.interact("stairs");drain(s);s.interact("duc");drain(s);choice(s,"C2-02-CHOICE01");choice(s,"C2-02-CHOICE02");choice(s,"C2-02-CHOICE03");s.interact("guard.wall");drain(s);s.interact("cabinet.upper");drain(s);s.interact("cabinet");drain(s);choice(s,"C3-04-CHOICE02");choice(s,"C3-04-CHOICE03");
        s.interact("duc");drain(s);choice(s,"CHO-DUC-FAMILY");assert s.state().reached(Event.DUC_PHOTO_SEEN);
        go(s,RoomId.GUARD_ROOM);s.interact("records");drain(s);go(s,RoomId.CLASSROOM);s.interact("hung");drain(s);choice(s,"C5-01-CHOICE01");choice(s,"C5-01-CHOICE01");choice(s,"C5-01-CHOICE02");choice(s,"C5-01-CHOICE02");choice(s,"C5-01-CHOICE06");
        go(s,RoomId.ROOM_304);s.interact("desk");drain(s);assert s.block().equals("MISSING");choice(s,"C6-01-CHOICE01");
        StorySession family=investigate(false,true);assert family.state().knows(Clue.CLASS_PHOTO)&&family.state().reached(Event.NGA_IDENTIFIED);
        for(boolean reverse:List.of(false,true)) {
            StorySession r=ready(reverse);go(r,RoomId.CLASSROOM);r.interact("hung");drain(r);choice(r,"C5-01-CHOICE01");choice(r,"C5-01-CHOICE06");r.toggleNotes();for(StorySession.Note note:r.notes())assert !note.text().isEmpty();r.toggleNotes();
        }
    }
    private static void checkMissingAndPermutations() {
        StorySession e=newGame();e.interact("door.hall");e.interact("stairs");drain(e);assert !e.state().knows(Clue.ALTERED_REGISTER)&&e.exploring();
        StorySession earlyNews=toDecision();choice(earlyNews,"C3-04-CHOICE02");choice(earlyNews,"C3-04-CHOICE03");go(earlyNews,RoomId.HALLWAY);earlyNews.interact("board");drain(earlyNews);assert earlyNews.state().reached(Event.NEWSLETTER_READ)&&!earlyNews.state().reached(Event.FINAL_WARNING_DONE);
        StorySession gates=investigate(false,false);gates.interact("desk");drain(gates);assert gates.block().equals("MISSING");choice(gates,"C6-01-CHOICE01");gates.interact("photo");drain(gates);gates.interact("desk");drain(gates);choice(gates,"C6-01-CHOICE01");
        for(int variant=0;variant<24;variant++) {
            boolean recordsFirst=(variant&1)!=0,familyLate=(variant&2)!=0,photoEarly=(variant&4)!=0,exitB=variant>=12;
            StorySession s=investigate(photoEarly,false);
            if(!photoEarly && !familyLate){s.interact("duc");drain(s);choice(s,"CHO-DUC-FAMILY");}
            if(recordsFirst){go(s,RoomId.GUARD_ROOM);s.interact("records");drain(s);go(s,RoomId.ROOM_304);}
            s.interact("player");drain(s); // Alone in investigation, cassette still absent.
            s.interact("crate");drain(s);s.interact("player");drain(s);go(s,RoomId.GUARD_ROOM);s.interact("nam");drain(s);choice(s,"CHO-NAM-BAT");go(s,RoomId.ROOM_304);s.interact("player");drain(s);
            if(variant==0){s.interact("cabinet");choice(s,"CHO-CAB-LEAVE");assert s.ended()&&s.state().ending()==Ending.B;continue;}
            if(recordsFirst && s.state().knows(Clue.CLASS_PHOTO)){s.interact("desk");drain(s);choice(s,"C6-01-CHOICE01");}
            go(s,RoomId.CLASSROOM);s.interact("hung");drain(s);
            if(recordsFirst){choice(s,"C5-01-CHOICE02");choice(s,"C5-01-CHOICE02");}
            choice(s,"C5-01-CHOICE03");assert s.state().reached(Event.HUNG_CONFESSION_DONE);
            choice(s,"C5-01-CHOICE06");
            if(s.state().knows(Clue.CLASS_PHOTO)&&s.state().knows(Clue.EVACUATION_AND_REPORT)){go(s,RoomId.ROOM_304);s.interact("desk");drain(s);choice(s,"C6-01-CHOICE01");go(s,RoomId.CLASSROOM);}
            if(familyLate){s.interact("duc");drain(s);choice(s,"CHO-DUC-FAMILY");assert s.state().room()==RoomId.ROOM_304&&!s.present("duc")&&s.state().reached(Event.DUC_PHOTO_SEEN);go(s,RoomId.CLASSROOM);}
            if(!recordsFirst){s.interact("hung");drain(s);choice(s,"C5-01-CHOICE04");assert !s.state().knows(Clue.SIGNED_ADDENDUM);go(s,RoomId.GUARD_ROOM);s.interact("records");drain(s);go(s,RoomId.CLASSROOM);}
            s.interact("hung");drain(s);choice(s,"C5-01-CHOICE04");assert s.state().reached(Event.FINAL_WARNING_DONE)&&s.exploring();
            GameState restored=ProgressSnapshot.decode(ProgressSnapshot.encode(s.state()),"permutation "+variant);assert restored.completedSteps().equals(s.state().completedSteps());
            s.interact("hung");drain(s);choice(s,"C5-01-CHOICE02");choice(s,"C5-01-CHOICE06");go(s,RoomId.ROOM_304);s.interact("desk");drain(s);choice(s,exitB?"C6-01-CHOICE04":"C6-01-CHOICE03");assert s.ended()&&s.state().ending()==(exitB?Ending.B:Ending.A);
        }
    }
    private static void checkRemainingBlocks(Path fixtures,Path assets,AssetCatalog assetsCatalog) throws Exception {
        StorySession photo=newGame();photo.interact("desk");drain(photo);photo.interact("photo");drain(photo);assert photo.state().knows(Clue.CLASS_PHOTO);photo.interact("door.hall");photo.interact("stairs");drain(photo);photo.interact("duc");drain(photo);choice(photo,"C2-02-CHOICE03");photo.interact("board");drain(photo);
        StorySession owned=investigate(false,false);owned.interact("crate");drain(owned);owned.interact("duc");drain(owned);choice(owned,"CHO-DUC-FAMILY");assert owned.state().has(Item.CASSETTE)&&!owned.state().knows(Clue.CASSETTE_CONTENT);
        // Source always places DUC at 304 for the first tape/power check. These ALONE
        // variants are defensive authored branches; exercise them with an absent-NPC config.
        com.badlogic.gdx.utils.JsonValue data=new com.badlogic.gdx.utils.JsonReader().parse(Files.readString(assets.resolve(assetsCatalog.path("story.world"))));
        com.badlogic.gdx.utils.JsonValue placements=data.get("placements").get("INVESTIGATE");for(com.badlogic.gdx.utils.JsonValue p=placements.child;p!=null;){var next=p.next;if(p.getString("id").equals("duc"))p.remove();p=next;}
        Path absent=Files.createTempFile(fixtures,"world-absent-",".json");SceneCatalog previous=world;
        try{Files.writeString(absent,data.toJson(com.badlogic.gdx.utils.JsonWriter.OutputType.json));world=SceneCatalog.load(new FileHandle(absent.toFile()),assetsCatalog);StorySession alone=investigate(false,false);alone.interact("crate");drain(alone);alone.interact("player");drain(alone);assert alone.state().reached(Event.RECORDER_CHECKED);}
        finally{world=previous;Files.deleteIfExists(absent);}
    }
    private static void checkInvalidSnapshots() {
        GameState ghost=new GameState();ghost.completed.add(StoryDirector.Action.PHOTO.contentId());rejectSnapshot(ghost);
        GameState noOpening=new GameState();noOpening.events.add(Event.INITIAL_SCENE_DONE);rejectSnapshot(noOpening);
        GameState wrongStage=ready(false).state();wrongStage.npcStage=NpcStage.INTRO;rejectSnapshot(wrongStage);
        GameState falseExit=new GameState();falseExit.events.add(Event.OUTSIDE_ALL);falseExit.npcStage=NpcStage.EXIT_B;rejectSnapshot(falseExit);
    }
    private static void rejectSnapshot(GameState state) {try{ProgressSnapshot.decode(ProgressSnapshot.encode(state),"invalid fixture");throw new AssertionError("Inconsistent snapshot accepted");}catch(IllegalArgumentException expected){}}
    private static void checkSaves(Path fixtures) throws Exception {
        Path folder=Files.createTempDirectory(fixtures,"save-check-");Path file=folder.resolve("slot.json");SaveStore store=new SaveStore(file);
        try {
            try{store.load(world);throw new AssertionError("Missing save accepted");}catch(IllegalArgumentException expected){}
            StorySession s=investigate(false,false);SceneCatalog.Point p=world.scene(s.state().room()).spawn("entry");store.save(s,p.x,p.y,"left",world);String original=Files.readString(file);
            SaveStore.Loaded read=store.load(world);StorySession restored=new StorySession(content,world,read.state(),(room,spawn)->world.scene(room).spawn(spawn));assert restored.canSave()&&restored.state().book()==BookLocation.CARRIED&&read.facing().equals("left");
            restored.interact("crate");try{store.save(restored,p.x,p.y,"left",world);throw new AssertionError("Mid-dialogue save accepted");}catch(IllegalStateException expected){}assert Files.readString(file).equals(original);drain(restored);assert restored.state().has(Item.CASSETTE);
            store.save(restored,p.x,p.y,"up",world);assert store.load(world).state().has(Item.CASSETTE);
            StorySession mature=ready(false);SceneCatalog.Point checkpoint=world.scene(mature.state().room()).spawn("entry");store.save(mature,checkpoint.x,checkpoint.y,"up",world);var matureLoad=store.load(world);StorySession continued=new StorySession(content,world,matureLoad.state(),(room,spawn)->world.scene(room).spawn(spawn),visited::add);continued.interact("desk");drain(continued);choice(continued,"C6-01-CHOICE03");assert continued.ended()&&continued.state().ending()==Ending.A;
            Files.writeString(file,"{broken");try{store.load(world);throw new AssertionError("Corrupt save accepted");}catch(IllegalArgumentException expected){}
            Files.writeString(file,original.replace("\"version\":1","\"version\":99"));try{store.load(world);throw new AssertionError("Future save accepted");}catch(IllegalArgumentException expected){}
            com.badlogic.gdx.utils.Json json=new com.badlogic.gdx.utils.Json();SaveStore.Data outside=json.fromJson(SaveStore.Data.class,original);outside.x=99999;Files.writeString(file,json.toJson(outside));try{store.load(world);throw new AssertionError("Outside save accepted");}catch(IllegalArgumentException expected){}
        }finally{Files.deleteIfExists(file);Files.deleteIfExists(folder);}
    }
    public static void drain(StorySession s) {
        for(int i=0;i<3000 && s.dialogue().isActive();i++) {
            if(content.blockIds().contains(s.block()))visited.add(s.block());
            if(!s.dialogue().getLine().getChoices().isEmpty()) return;
            s.update(5);s.advance();
        }
        if(s.dialogue().isActive() && s.dialogue().getLine().getChoices().isEmpty()) throw new AssertionError("Sequence stuck " + s.block());
    }
    public static void choice(StorySession s,String id) { s.choose(id);drain(s); }
    public static void go(StorySession s,RoomId room) { assert s.exploring();s.state().enteredRoom(room); }
    public static StorySession newGame() { StorySession s=new StorySession(content,world,new GameState(),(room,spawn)->world.scene(room).spawn(spawn),visited::add);s.start(false);drain(s);assert s.exploring();return s; }
    public static StorySession toDecision() {
        StorySession s=newGame();s.interact("desk");drain(s);s.interact("door.hall");s.interact("stairs");drain(s);s.interact("duc");drain(s);choice(s,"C2-02-CHOICE03");s.interact("guard.wall");drain(s);assert s.state().room()==RoomId.ROOM_304;s.interact("cabinet");drain(s);assert s.block().equals("DECIDE");return s;
    }
    public static StorySession investigate(boolean photo,boolean family) {
        StorySession s=toDecision();choice(s,"C3-04-CHOICE02");choice(s,"C3-04-CHOICE03");assert s.canSave();
        if(photo) {s.interact("photo");drain(s);}
        if(family) {s.interact("duc");drain(s);choice(s,"CHO-DUC-FAMILY");}
        return s;
    }
    public static StorySession ready(boolean reverse) {
        StorySession s=investigate(!reverse,false);
        if(!reverse){go(s,RoomId.GUARD_ROOM);s.interact("records");drain(s);go(s,RoomId.ROOM_304);}
        s.interact("crate");drain(s);s.interact("player");drain(s);go(s,RoomId.GUARD_ROOM);s.interact("nam");drain(s);choice(s,"CHO-NAM-BAT");go(s,RoomId.ROOM_304);s.interact("player");drain(s);go(s,RoomId.CLASSROOM);s.interact("hung");drain(s);choice(s,"C5-01-CHOICE03");
        assert s.block().equals("HUNG-MENU");choice(s,"C5-01-CHOICE04");
        if(reverse) {
            assert !s.state().knows(Clue.SIGNED_ADDENDUM);go(s,RoomId.ROOM_304);s.interact("photo");drain(s);go(s,RoomId.GUARD_ROOM);s.interact("records");drain(s);go(s,RoomId.CLASSROOM);s.interact("hung");drain(s);choice(s,"C5-01-CHOICE04");
        }
        assert s.state().reached(Event.FINAL_WARNING_DONE) && s.exploring();go(s,RoomId.ROOM_304);return s;
    }
}
