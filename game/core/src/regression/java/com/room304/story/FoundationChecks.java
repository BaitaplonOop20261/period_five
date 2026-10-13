package com.room304.story;

import com.badlogic.gdx.files.FileHandle;
import com.room304.assets.AssetCatalog;
import com.room304.world.NpcDefinition;
import com.room304.dialogue.*;
import static com.room304.story.GameState.*;
import static com.room304.story.StoryDirector.Action.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

/** Real branch regressions, metadata checks and lifecycle contracts, without OpenGL. */
public final class FoundationChecks {
    public static void run(Path fixtures, Path assets) throws Exception {
        checkNewGameAndPhenomenon(); checkBranchGates(); checkEndingA(); checkEndingB();
        checkTicketsAndWarning(); checkSnapshot(); checkDialogueCompletion(); checkAssets(fixtures, assets);
        System.out.println("Foundation checks passed: 8 scenario groups (state, branch gates, A, B, tickets/warning, snapshots, dialogue callbacks, asset/NPC validation).");
    }
    private static void done(StoryDirector d, StoryDirector.Action a) {
        var step = d.begin(a); assert d.complete(step, null); assert !d.complete(step, null);
    }
    private static StoryDirector investigation() {
        GameState s = new GameState(); StoryDirector d = new StoryDirector(s);
        done(d, OPENING); s.enteredRoom(RoomId.ROOM_304); done(d, OPEN_REGISTER);
        s.enteredRoom(RoomId.HALLWAY); done(d, VERIFY_LOOP); done(d, MEET_DUC); done(d, REVEAL_GUARD_DOOR);
        s.enteredRoom(RoomId.GUARD_ROOM); done(d, RECEIVE_KEY);
        assert !StoryConditions.canRetreat(s) : "Key alone is not B";
        s.enteredRoom(RoomId.ROOM_304); done(d, LOCK_REGISTER);
        assert !s.reached(Event.RETREAT_TRIAL_DONE) : "Locking alone is not verification";
        s.enteredRoom(RoomId.GUARD_ROOM); done(d, VERIFY_EXIT);
        assert !StoryConditions.canRetreat(s) : "Hung meeting is still required";
        s.enteredRoom(RoomId.CLASSROOM); done(d, MEET_HUNG);
        assert StoryConditions.canRetreat(s);
        return d;
    }
    private static void reopen(StoryDirector d) {
        d.state().enteredRoom(RoomId.ROOM_304);
        assert d.complete(d.begin(REOPEN_REGISTER), "CONFIRM");
    }
    private static StoryDirector signed() {
        StoryDirector d = investigation(); reopen(d); GameState s = d.state();
        done(d, TAKE_TAPE); done(d, CHECK_POWER);
        s.enteredRoom(RoomId.GUARD_ROOM); done(d, RECEIVE_BATTERIES);
        s.enteredRoom(RoomId.ROOM_304); done(d, HEAR_TAPE);
        s.enteredRoom(RoomId.CLASSROOM); done(d, CONFESS);
        assert !s.knows(Clue.CLASS_PHOTO) && !s.knows(Clue.EVACUATION_AND_REPORT);
        rejects(() -> d.begin(SIGN));
        s.enteredRoom(RoomId.GUARD_ROOM); done(d, READ_RECORDS);
        s.enteredRoom(RoomId.ROOM_304); done(d, PHOTO);
        s.enteredRoom(RoomId.CLASSROOM); done(d, SIGN);
        assert d.activeStep().action() == WARNING && d.blocksExploration();
        assert s.book() == BookLocation.DESK_HUNG && !s.reached(Event.FINAL_WARNING_DONE);
        return d;
    }
    private static StoryDirector ready() {
        StoryDirector d = signed(); GameState s = d.state();
        assert d.complete(d.activeStep(), null);
        s.enteredRoom(RoomId.ROOM_304); assert StoryConditions.canPrepareFinal(s);
        return d;
    }
    private static void checkNewGameAndPhenomenon() {
        GameState s = new GameState(); assert s.items().isEmpty() && s.clues().isEmpty() && s.events().isEmpty();
        assert s.book() == BookLocation.TABLE_304 && s.cabinetLocked() && s.phenomenon() == Phenomenon.NORMAL;
        var d = investigation(); assert d.state().phenomenon() == Phenomenon.SUSPENDED;
        reopen(d); assert d.state().phenomenon() == Phenomenon.ACTIVE;
        d.state().bookCoverClosed = true; assert d.state().phenomenon() == Phenomenon.ACTIVE;
        assert new GameState().clues().isEmpty() : "Replay is a new state";
    }
    private static void checkBranchGates() {
        StoryDirector d = investigation(); reopen(d); done(d, TAKE_TAPE);
        d.state().enteredRoom(RoomId.CLASSROOM); rejects(() -> d.begin(CONFESS));
        assert !d.state().knows(Clue.CASSETTE_CONTENT);
        GameState s = ready().state();
        for (Clue c : Clue.values()) { s.clues.remove(c); assert !StoryConditions.canPrepareFinal(s) : c; s.clues.add(c); }
        for (Event e : List.of(Event.RETREAT_TRIAL_DONE, Event.HUNG_INTRO_DONE, Event.HUNG_CONFESSION_DONE,
            Event.FINAL_WARNING_DONE, Event.INVESTIGATION_STARTED)) {
            s.events.remove(e); assert !StoryConditions.canPrepareFinal(s) : e; s.events.add(e);
        }
        s.enteredRoom(RoomId.HALLWAY); assert !StoryConditions.canPrepareFinal(s);
        s.enteredRoom(RoomId.ROOM_304); s.items.remove(Item.CABINET_KEY); assert !StoryConditions.canPrepareFinal(s);
    }
    private static void checkEndingA() {
        StoryDirector d = ready(); GameState s = d.state();
        rejects(() -> d.begin(REQUEST_A)); done(d, PREPARE_FINAL);
        assert s.ending() == Ending.NONE && s.book() == BookLocation.FINAL_TABLE;
        assert !StoryConditions.canPrepareFinal(s) && StoryConditions.canChooseA(s);
        done(d, REQUEST_A); assert s.ending() == Ending.NONE; rejects(() -> d.begin(FINISH_A));
        done(d, RESOLVE_A); s.enteredRoom(RoomId.GUARD_ROOM); done(d, EXIT_A); done(d, FINISH_A);
        assert s.ending() == Ending.A && !s.has(Item.CABINET_KEY) && s.phenomenon() == Phenomenon.RESOLVED;
        assert ProgressSnapshot.decode(ProgressSnapshot.encode(s), "ending-A").ending() == Ending.A;
    }
    private static void finishB(StoryDirector d) {
        GameState s = d.state(); done(d, REQUEST_B); rejects(() -> d.begin(FINISH_B));
        s.enteredRoom(RoomId.ROOM_304); done(d, LOCK_FOR_B);
        assert s.book() == BookLocation.LOCKED_CABINET && s.cabinetLocked();
        s.enteredRoom(RoomId.GUARD_ROOM); done(d, EXIT_B); done(d, FINISH_B);
        assert s.ending() == Ending.B && !s.has(Item.CABINET_KEY);
        assert ProgressSnapshot.decode(ProgressSnapshot.encode(s), "ending-B").ending() == Ending.B;
        s.events.add(Event.RESOLVED);
        rejects(() -> ProgressSnapshot.decode(ProgressSnapshot.encode(s), "B-cannot-resolve"));
        s.events.remove(Event.RESOLVED);
    }
    private static void checkEndingB() {
        var early = investigation(); finishB(early); assert early.state().clues().size() == 1;
        var late = ready(); done(late, PREPARE_FINAL); finishB(late);
        assert late.state().clues().size() == 5 : "B must preserve all evidence";
        assert !late.state().reached(Event.RESOLVED);
    }
    private static void checkTicketsAndWarning() {
        GameState s = new GameState(); StoryDirector d = new StoryDirector(s);
        var cancelled = d.begin(OPENING); d.cancel(); var next = d.begin(OPENING);
        assert !d.complete(cancelled, null) && s.events().isEmpty(); assert d.complete(next, null);
        assert !d.complete(next, null); StoryDirector initial = d; rejects(() -> initial.begin(OPENING));
        var other = new StoryDirector(new GameState()).begin(OPENING); assert !d.complete(other, null);
        d = investigation(); s = d.state(); s.enteredRoom(RoomId.ROOM_304);
        var confirm = d.begin(REOPEN_REGISTER); StoryDirector director = d;
        rejects(() -> director.complete(confirm, "TYPO")); assert d.activeStep() == confirm;
        assert d.complete(confirm, "CANCEL"); assert s.book() == BookLocation.LOCKED_CABINET;
        assert !s.reached(Event.INVESTIGATION_STARTED); reopen(d);
        assert s.reached(Event.INVESTIGATION_STARTED);
        d = signed(); var warning = d.activeStep(); d.cancel();
        assert d.blocksExploration() : "Cancelled warning must not release exploration after signing";
        assert !d.canBegin(REQUEST_B) && !d.canBegin(PHOTO) : "Only the mandatory warning may resume";
        assert !d.complete(warning, null) : "Cancelled warning ticket must remain stale";
        done(d, WARNING); assert !d.blocksExploration();
    }
    private static void checkSnapshot() {
        var d = ready(); var s = d.state(); var encoded = ProgressSnapshot.encode(s);
        GameState restored = ProgressSnapshot.decode(encoded, "roundtrip");
        assert encoded.equals(ProgressSnapshot.encode(restored)); assert StoryConditions.canPrepareFinal(restored);
        restored.enteredRoom(RoomId.HALLWAY); assert s.room() == RoomId.ROOM_304;
        rejects(() -> ProgressSnapshot.decode(encoded.replace("\"version\":1", "\"version\":99"), "bad-version"));
        rejects(() -> ProgressSnapshot.decode("{}", "missing-fields"));
        rejects(() -> ProgressSnapshot.decode(encoded.replace("CASSETTE_CONTENT", "UNKNOWN_CLUE"), "bad-enum"));
        GameState unknown = new GameState(); unknown.completed.add("UNKNOWN_STEP");
        rejects(() -> ProgressSnapshot.decode(ProgressSnapshot.encode(unknown), "unknown-completed"));
        GameState fakeB = new GameState(); fakeB.book = BookLocation.LOCKED_CABINET;
        fakeB.requestedEnding = fakeB.ending = Ending.B; fakeB.room = RoomId.GUARD_ROOM;
        fakeB.events.add(Event.OUTSIDE_ALL);
        rejects(() -> ProgressSnapshot.decode(ProgressSnapshot.encode(fakeB), "B-without-trial"));
        s.items.remove(Item.CASSETTE); rejects(() -> ProgressSnapshot.decode(ProgressSnapshot.encode(s), "bad-invariant"));
    }
    private static void checkDialogueCompletion() {
        Dialogue data = new Dialogue("callback", List.of(new DialogueLine("Trang", "Một câu.", null)));
        DialogueController c = new DialogueController(); AtomicInteger calls = new AtomicInteger();
        c.start(data, choice -> { assert choice == null; calls.incrementAndGet(); }); c.close(); assert calls.get() == 0;
        c.start(data, choice -> calls.incrementAndGet()); c.start(data); c.advance(); assert calls.get() == 0;
        c.start(data, choice -> { calls.incrementAndGet(); c.start(data); }); c.advance();
        assert calls.get() == 1 && c.isActive() : "Callback can safely start the next dialogue";
        c.close(); c.advance(); assert calls.get() == 1;
        GameState state = new GameState(); state.enteredRoom(RoomId.ROOM_304);
        StoryDirector director = new StoryDirector(state); var ticket = director.begin(PHOTO);
        c.start(data, choice -> director.complete(ticket, choice));
        assert !state.knows(Clue.CLASS_PHOTO);
        c.advance(); assert state.knows(Clue.CLASS_PHOTO) && director.activeStep() == null;
        c.advance(); assert state.clues().size() == 1;
    }
    private static void checkAssets(Path fixtures, Path directory) throws Exception {
        AssetCatalog catalog = AssetCatalog.load(new FileHandle(directory.resolve(AssetCatalog.MANIFEST).toFile()));
        assert catalog.missing(p -> Files.exists(directory.resolve(p)), true).isEmpty();
        assert catalog.missing(p -> !p.equals(catalog.path("font.ui")), true)
            .equals(List.of("font.ui -> " + catalog.path("font.ui"))) : "Missing required diagnostic must identify ID/path";
        for (var e : catalog.entries()) if (e.frameWidth() > 0) {
            var image = ImageIO.read(directory.resolve(e.path()).toFile());
            assert image.getWidth() % e.frameWidth() == 0 && image.getHeight() % e.frameHeight() == 0;
            assert image.getWidth() / e.frameWidth() * (image.getHeight() / e.frameHeight()) >= e.frameCount();
        }
        var npcs = NpcDefinition.load(new FileHandle(directory.resolve(catalog.path("npc.demo.config")).toFile()), catalog);
        assert npcs.stream().anyMatch(n -> n.id().equals("trang"));
        Path invalid = fixtures.resolve("bad-manifest.json");
        Files.writeString(invalid, "{\"version\":1,\"assets\":[{\"id\":\"bad\",\"path\":\"../escape.png\",\"type\":\"texture\"}]}");
        rejects(() -> AssetCatalog.load(new FileHandle(invalid.toFile())));
        String valid = Files.readString(directory.resolve(AssetCatalog.MANIFEST));
        Files.writeString(invalid, valid.replace("map.demo.hall", "map.demo.room"));
        rejects(() -> AssetCatalog.load(new FileHandle(invalid.toFile())));
        Files.writeString(invalid, valid.replace("\"frameDuration\": 0.12", "\"frameDuration\": 0"));
        rejects(() -> AssetCatalog.load(new FileHandle(invalid.toFile())));
    }
    private static void rejects(Runnable operation) {
        try { operation.run(); throw new AssertionError("Invalid operation accepted"); }
        catch (IllegalArgumentException | IllegalStateException expected) {}
    }
}
