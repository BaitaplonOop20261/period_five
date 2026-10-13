package com.room304.story;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.util.*;
import static com.room304.story.GameState.*;

/** Versioned progress contract used by SaveStore; transient runtime objects are never serialized. */
public final class ProgressSnapshot {
    private ProgressSnapshot() {}
    public static final class Data {
        public int version;
        public String room, book, ending, requestedEnding, npcStage, tape, battery, records, supplement;
        public boolean cabinetLocked, bookCoverClosed;
        public String[] items, clues, events, completed;
    }
    public static String encode(GameState s) {
        if (s.book == BookLocation.DESK_HUNG || (s.requestedEnding != Ending.NONE && s.ending == Ending.NONE))
            throw new IllegalStateException("Snapshot requires a stable exploration/ending boundary");
        Data d = new Data();
        d.version = 1; d.room = s.room.name(); d.book = s.book.name(); d.ending = s.ending.name();
        d.requestedEnding = s.requestedEnding.name(); d.npcStage = s.npcStage.name();
        d.tape = s.tape.name(); d.battery = s.battery.name(); d.records = s.records.name(); d.supplement = s.supplement.name();
        d.cabinetLocked = s.cabinetLocked; d.bookCoverClosed = s.bookCoverClosed;
        d.items = s.items.stream().map(Enum::name).toArray(String[]::new);
        d.clues = s.clues.stream().map(Enum::name).toArray(String[]::new);
        d.events = s.events.stream().map(Enum::name).toArray(String[]::new);
        d.completed = s.completed.toArray(String[]::new);
        Json json = new Json(); json.setOutputType(JsonWriter.OutputType.json);
        return json.toJson(d);
    }
    public static GameState decode(String text, String source) {
        try {
            Data d = new Json().fromJson(Data.class, text);
            if (d == null || d.version != 1) throw new IllegalArgumentException("Unsupported snapshot version");
            GameState s = new GameState();
            s.room = RoomId.valueOf(d.room); s.book = BookLocation.valueOf(d.book);
            s.ending = Ending.valueOf(d.ending); s.requestedEnding = Ending.valueOf(d.requestedEnding);
            s.npcStage = NpcStage.valueOf(d.npcStage); s.tape = TapeLocation.valueOf(d.tape);
            s.battery = BatteryLocation.valueOf(d.battery); s.records = RecordsLocation.valueOf(d.records);
            s.supplement = SupplementLocation.valueOf(d.supplement);
            s.cabinetLocked = d.cabinetLocked; s.bookCoverClosed = d.bookCoverClosed;
            readEnums(d.items, Item.class, s.items); readEnums(d.clues, Clue.class, s.clues); readEnums(d.events, Event.class, s.events);
            if (d.completed == null) throw new IllegalArgumentException("Missing completed steps");
            for (String id : d.completed) if (id == null
                || Arrays.stream(StoryDirector.Action.values()).noneMatch(a -> a.contentId().equals(id))
                || !s.completed.add(id))
                throw new IllegalArgumentException("Invalid/duplicate completed ID " + id);
            validate(s);
            return s;
        } catch (RuntimeException e) { throw new IllegalArgumentException("Invalid progress snapshot " + source + ": " + e.getMessage(), e); }
    }
    private static <E extends Enum<E>> void readEnums(String[] names, Class<E> type, Set<E> target) {
        if (names == null) throw new IllegalArgumentException("Missing " + type.getSimpleName() + " set");
        for (String name : names) if (!target.add(Enum.valueOf(type, name))) throw new IllegalArgumentException("Duplicate " + name);
    }
    private static void require(boolean condition, String reason) { if (!condition) throw new IllegalArgumentException(reason); }
    private static void validate(GameState s) {
        require(s.book != BookLocation.DESK_HUNG && (s.requestedEnding == Ending.NONE || s.ending != Ending.NONE), "Unstable scene boundary");
        require(!s.reached(Event.INITIAL_SCENE_DONE)||s.reached(Event.OPENING_DONE),"Initial room before opening");
        require(s.knows(Clue.ALTERED_REGISTER)==(s.book!=BookLocation.TABLE_304),"Register knowledge/location conflict");
        require(s.ending!=Ending.NONE||(!s.reached(Event.OUTSIDE_ALL)&&!s.reached(Event.RESOLVED)),"Exit/resolution without completed ending");
        require(s.supplement!=SupplementLocation.DESK_HUNG,"Unfinished signed document handover");
        require(!s.reached(Event.LOOP_CONFIRMED)||s.knows(Clue.ALTERED_REGISTER),"Loop without register");
        require(!s.reached(Event.DUC_MET)||s.reached(Event.LOOP_CONFIRMED),"Duc before loop");
        require(!s.reached(Event.GUARD_DOOR_REVEALED)||s.reached(Event.DUC_MET),"Door before Duc");
        require(!s.reached(Event.NAM_MET)||s.reached(Event.GUARD_DOOR_REVEALED),"Key meeting before door");
        require(!s.has(Item.CASSETTE)||s.reached(Event.INVESTIGATION_STARTED),"Tape before investigation");
        require(!s.reached(Event.RECORDER_CHECKED)||s.has(Item.CASSETTE),"Power check without tape");
        require(!s.has(Item.BATTERY_PACK)||s.reached(Event.RECORDER_CHECKED),"Batteries before power check");
        require(!s.knows(Clue.EVACUATION_AND_REPORT)||s.reached(Event.INVESTIGATION_STARTED),"Records before investigation");
        require(!s.reached(Event.DUC_PHOTO_SEEN)||(s.reached(Event.INVESTIGATION_STARTED)&&s.knows(Clue.CLASS_PHOTO)),"Invalid family photo");
        require(!s.reached(Event.FINAL_PREPARED)||(StoryConditions.evidenceComplete(s)&&s.reached(Event.FINAL_WARNING_DONE)&&s.reached(Event.INVESTIGATION_STARTED)),"Prepared without all gates");
        require(s.has(Item.CABINET_KEY)==(s.reached(Event.NAM_MET)&&(s.ending==Ending.NONE||s.ending==Ending.C)),"Key ownership conflicts with progress");
        NpcStage expected=s.reached(Event.OUTSIDE_ALL)?s.requestedEnding==Ending.A?NpcStage.EXIT_A:NpcStage.EXIT_B:
            s.reached(Event.FINAL_PREPARED)||s.requestedEnding==Ending.B?NpcStage.FINAL:s.reached(Event.FINAL_WARNING_DONE)?NpcStage.READY:
            s.reached(Event.HUNG_CONFESSION_DONE)?NpcStage.CONFRONT:s.reached(Event.INVESTIGATION_STARTED)?NpcStage.INVESTIGATE:
            s.reached(Event.HUNG_INTRO_DONE)?NpcStage.DECISION:s.reached(Event.DUC_MET)?NpcStage.GROUP:
            s.knows(Clue.ALTERED_REGISTER)?NpcStage.LOOP:s.reached(Event.OPENING_DONE)?NpcStage.INITIAL:NpcStage.INTRO;
        require(s.npcStage==expected,"NPC stage conflicts with story progress");
        for(StoryDirector.Action action:StoryDirector.Action.values())if(s.completed.contains(action.contentId())) {
            boolean applied=switch(action) {
                case OPENING->s.reached(Event.OPENING_DONE);case PHOTO->s.knows(Clue.CLASS_PHOTO);case OPEN_REGISTER->s.knows(Clue.ALTERED_REGISTER);
                case VERIFY_LOOP->s.reached(Event.LOOP_CONFIRMED);case MEET_DUC->s.reached(Event.DUC_MET);case REVEAL_GUARD_DOOR->s.reached(Event.GUARD_DOOR_REVEALED);
                case RECEIVE_KEY,LOCK_REGISTER->s.reached(Event.NAM_MET);case VERIFY_EXIT->s.reached(Event.RETREAT_TRIAL_DONE);case MEET_HUNG->s.reached(Event.HUNG_INTRO_DONE);
                case REOPEN_REGISTER->s.reached(Event.INVESTIGATION_STARTED);case READ_RECORDS->s.knows(Clue.EVACUATION_AND_REPORT);case FAMILY_PHOTO->s.reached(Event.DUC_PHOTO_SEEN);
                case READ_NEWSLETTER->s.reached(Event.NEWSLETTER_READ);case TAKE_TAPE->s.has(Item.CASSETTE);case CHECK_POWER->s.reached(Event.RECORDER_CHECKED);
                case RECEIVE_BATTERIES->s.has(Item.BATTERY_PACK);case HEAR_TAPE->s.knows(Clue.CASSETTE_CONTENT);case CONFESS->s.reached(Event.HUNG_CONFESSION_DONE);
                case SIGN->s.knows(Clue.SIGNED_ADDENDUM);case WARNING->s.reached(Event.FINAL_WARNING_DONE);case PREPARE_FINAL->s.reached(Event.FINAL_PREPARED);
                case REQUEST_A->s.requestedEnding==Ending.A;case RESOLVE_A->s.reached(Event.RESOLVED);case EXIT_A->s.reached(Event.OUTSIDE_ALL)&&s.requestedEnding==Ending.A;case FINISH_A->s.ending==Ending.A;
                case REQUEST_B,LOCK_FOR_B->s.requestedEnding==Ending.B;case EXIT_B->s.reached(Event.OUTSIDE_ALL)&&s.requestedEnding==Ending.B;case FINISH_B->s.ending==Ending.B;
                case REQUEST_C->s.requestedEnding==Ending.C;case FINISH_C->s.ending==Ending.C;
            };require(applied,"Completed action without its effect: "+action.contentId());
        }
        require(!s.knows(Clue.CASSETTE_CONTENT) || (s.has(Item.CASSETTE) && s.has(Item.BATTERY_PACK)), "Heard tape without tape/batteries");
        require(!s.reached(Event.HUNG_CONFESSION_DONE) || s.knows(Clue.CASSETTE_CONTENT), "Confession without tape content");
        require(!s.knows(Clue.SIGNED_ADDENDUM) || StoryConditions.evidenceComplete(s), "Signed without evidence/confession");
        require(!s.reached(Event.FINAL_WARNING_DONE) || s.knows(Clue.SIGNED_ADDENDUM), "Warning without signing");
        require(!s.reached(Event.RETREAT_TRIAL_DONE) || (s.reached(Event.NAM_MET) && s.knows(Clue.ALTERED_REGISTER)), "Trial without key meeting/register");
        require(!s.reached(Event.HUNG_INTRO_DONE) || s.reached(Event.RETREAT_TRIAL_DONE), "Hung intro before trial");
        require(!s.reached(Event.INVESTIGATION_STARTED) || s.reached(Event.HUNG_INTRO_DONE), "Investigation before intro");
        require(s.book != BookLocation.LOCKED_CABINET || s.cabinetLocked, "Register in unlocked cabinet");
        require(s.has(Item.CASSETTE) == (s.tape != TapeLocation.CRATE), "Tape ownership/location conflict");
        require(s.has(Item.BATTERY_PACK) == (s.battery != BatteryLocation.GUARD_ROOM), "Battery ownership/location conflict");
        require(s.knows(Clue.SIGNED_ADDENDUM) == (s.supplement != SupplementLocation.NONE), "Supplement knowledge/location conflict");
        require(s.book != BookLocation.FINAL_TABLE || s.reached(Event.FINAL_PREPARED), "Final table without preparation");
        require(s.ending == Ending.NONE || s.ending == Ending.C || (s.ending == s.requestedEnding && s.reached(Event.OUTSIDE_ALL)
            && s.room == RoomId.GUARD_ROOM && !s.has(Item.CABINET_KEY)), "Ending without exit/key return");
        require(s.ending != Ending.A || (s.reached(Event.RESOLVED) && s.clues.size() == Clue.values().length && s.book == BookLocation.CARRIED), "Incomplete A");
        require(s.ending != Ending.C || (s.requestedEnding == Ending.C && StoryConditions.fullReady(s)
            && s.reached(Event.FINAL_PREPARED) && s.book == BookLocation.FINAL_TABLE
            && s.room == RoomId.ROOM_304 && !s.reached(Event.OUTSIDE_ALL) && !s.reached(Event.RESOLVED)), "Incomplete C");
        require(s.ending != Ending.B || (s.book == BookLocation.LOCKED_CABINET
            && s.knows(Clue.ALTERED_REGISTER) && s.reached(Event.NAM_MET)
            && s.reached(Event.RETREAT_TRIAL_DONE) && s.reached(Event.HUNG_INTRO_DONE)
            && !s.reached(Event.RESOLVED)), "Incomplete B");
    }
}
