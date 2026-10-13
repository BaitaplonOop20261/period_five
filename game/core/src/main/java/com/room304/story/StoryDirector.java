package com.room304.story;

import static com.room304.story.GameState.*;
import java.util.*;

/** Java conditions/effects, not a scripting language. Screen executes presentation then completes a ticket. */
public final class StoryDirector {
    public enum Action {
        OPENING("C0-03"), PHOTO("C1-02-SAVE"), OPEN_REGISTER("C1-04"), VERIFY_LOOP("C2-01"),
        MEET_DUC("C2-02-MAIN"), REVEAL_GUARD_DOOR("C2-04"), RECEIVE_KEY("C3-01"),
        LOCK_REGISTER("C3-02-MAIN-E02"), VERIFY_EXIT("C3-02-MAIN-E08"), MEET_HUNG("C3-03"),
        REOPEN_REGISTER("C3-04-CONFIRM"), READ_RECORDS("C3-05"), FAMILY_PHOTO("C4-01"),
        READ_NEWSLETTER("C2-03"), TAKE_TAPE("C4-02"), CHECK_POWER("C4-03"),
        RECEIVE_BATTERIES("C4-04"), HEAR_TAPE("C4-05"), CONFESS("C5-04"),
        SIGN("C5-05"), WARNING("C5-06"), PREPARE_FINAL("C6-01-GATHER"),
        REQUEST_A("C6-01-FINALMENU"), RESOLVE_A("A-01"), EXIT_A("A-01-MAIN-E08"), FINISH_A("A-02"),
        REQUEST_B("B-01-TRANS"), LOCK_FOR_B("B-01-LOCK"), EXIT_B("B-01-EXIT"), FINISH_B("B-02"), REQUEST_C("C-01-CONFIRM"), FINISH_C("C-02");
        private final String contentId;
        Action(String contentId) { this.contentId = contentId; }
        public String contentId() { return contentId; }
    }
    public record Step(long token, Action action, String contentId) {}
    private final GameState state;
    private long sequence;
    private Step active;
    public StoryDirector(GameState state) { this.state = Objects.requireNonNull(state); }
    public GameState state() { return state; }
    public Step activeStep() { return active; }
    public boolean blocksExploration() { return active != null || state.book == BookLocation.DESK_HUNG || state.requestedEnding != Ending.NONE || state.ending != Ending.NONE; }
    public boolean canBegin(Action action) { return active == null && allowed(Objects.requireNonNull(action)); }
    public Step begin(Action action) {
        Objects.requireNonNull(action);
        if (active != null) throw new IllegalStateException("Finish/cancel active step " + active.contentId);
        if (!allowed(action)) throw new IllegalStateException("Story preconditions not met: " + action.contentId);
        active = new Step(++sequence, action, action.contentId);
        return active;
    }
    /** Null/duplicate/stale tickets have no effects. Validation precedes every mutation. */
    public boolean complete(Step step, String choiceId) {
        if (step == null || step != active) return false;
        if (step.action == Action.REOPEN_REGISTER) {
            if (!Set.of("CONFIRM", "CANCEL").contains(choiceId == null ? "" : choiceId))
                throw new IllegalArgumentException("Expected CONFIRM/CANCEL for " + step.contentId);
        } else if (choiceId != null) throw new IllegalArgumentException("Unexpected choice " + choiceId + " for " + step.contentId);
        if (!allowed(step.action)) throw new IllegalStateException("State changed during " + step.contentId);
        active = null;
        if (step.action == Action.REOPEN_REGISTER && "CANCEL".equals(choiceId)) return true;
        apply(step.action);
        state.completed.add(step.contentId);
        // Signing and warning are one continuous transaction; screen must execute this next ticket.
        if (step.action == Action.SIGN) begin(Action.WARNING);
        return true;
    }
    public void cancel() { active = null; }
    /** Stable repeat/knowledge flags, committed by the runtime only after the named block finishes. */
    public void finishedBlock(String id) {
        Event event = switch (id) {
            case "C1-01-MAIN" -> Event.INITIAL_SCENE_DONE;
            case "C1-03-WITH", "C1-03-ALONE" -> Event.PLAYER_EXAMINED;
            case "C2-02-PHOTO" -> Event.DUC_ASKED_PHOTO;
            case "C2-02-NAM" -> Event.DUC_ASKED_NAM;
            case "C3-04-STAY" -> Event.STAY_EXPLAINED;
            case "C5-01-OPEN" -> Event.HUNG_MENU_OPENED;
            case "C5-02-DENY", "C5-02-HINT", "C5-02-REPEAT" -> Event.ASKED_REGISTER;
            case "C5-02-PHOTO" -> Event.ASKED_REGISTER_PHOTO;
            case "C5-03-DENY" -> Event.ASKED_COUNT;
            default -> null;
        };
        if (event != null) state.events.add(event);
    }
    /** Physical staging during a locked sequence; save is permitted only at stable boundaries. */
    public void stageBeat(String id) {
        switch (id) {
            case "C2-01-MAIN-E05" -> state.events.add(Event.CHALK_MARK);
            case "C4-03-MAIN-E01", "C4-05-MAIN-E01" -> state.tape = TapeLocation.PLAYER;
            case "C4-03-END-E01", "C4-05-END-E01" -> state.tape = TapeLocation.CARRIED;
            case "C4-05-MAIN-E01-PINS" -> state.battery = BatteryLocation.PLAYER;
            case "C5-05-FULL-E01" -> state.book = BookLocation.DESK_HUNG;
            case "C5-04-MAIN-E02" -> state.npcStage = NpcStage.CONFRONT;
            default -> { }
        }
    }
    public void closeBook() { if (state.book != BookLocation.CARRIED) throw new IllegalStateException("Book is not carried"); state.bookCoverClosed = true; }
    public void openBook() { if (state.book != BookLocation.CARRIED) throw new IllegalStateException("Book is not carried"); state.bookCoverClosed = false; }
    private boolean allowed(Action a) {
        if (state.ending != Ending.NONE || state.completed.contains(a.contentId)) return false;
        // Cancellation invalidates the callback, but signing still requires its warning.
        if (state.book == BookLocation.DESK_HUNG && a != Action.WARNING
            && !(a == Action.SIGN && active != null && active.action == Action.SIGN)) return false;
        if (state.requestedEnding != Ending.NONE && !Set.of(Action.RESOLVE_A, Action.EXIT_A, Action.FINISH_A,
            Action.LOCK_FOR_B, Action.EXIT_B, Action.FINISH_B, Action.FINISH_C).contains(a)) return false;
        return switch (a) {
            case OPENING -> !state.reached(Event.OPENING_DONE);
            case PHOTO -> !state.knows(Clue.CLASS_PHOTO) && state.room == RoomId.ROOM_304;
            case OPEN_REGISTER -> state.room == RoomId.ROOM_304 && state.book == BookLocation.TABLE_304 && !state.knows(Clue.ALTERED_REGISTER);
            case VERIFY_LOOP -> state.knows(Clue.ALTERED_REGISTER) && state.room == RoomId.HALLWAY;
            case MEET_DUC -> state.reached(Event.LOOP_CONFIRMED) && state.room == RoomId.HALLWAY;
            case REVEAL_GUARD_DOOR -> state.reached(Event.DUC_MET) && state.room == RoomId.HALLWAY;
            case RECEIVE_KEY -> state.reached(Event.GUARD_DOOR_REVEALED) && state.room == RoomId.GUARD_ROOM;
            case LOCK_REGISTER -> state.has(Item.CABINET_KEY) && state.book == BookLocation.CARRIED && state.room == RoomId.ROOM_304;
            case VERIFY_EXIT -> state.book == BookLocation.LOCKED_CABINET && state.cabinetLocked && state.room == RoomId.GUARD_ROOM;
            case MEET_HUNG -> state.reached(Event.RETREAT_TRIAL_DONE) && state.room == RoomId.CLASSROOM;
            case REOPEN_REGISTER -> StoryConditions.canRetreat(state) && state.book == BookLocation.LOCKED_CABINET
                && state.cabinetLocked && state.room == RoomId.ROOM_304;
            case READ_RECORDS -> state.reached(Event.INVESTIGATION_STARTED) && state.room == RoomId.GUARD_ROOM;
            case FAMILY_PHOTO -> state.reached(Event.INVESTIGATION_STARTED) && state.room == RoomId.ROOM_304;
            case READ_NEWSLETTER -> state.knows(Clue.ALTERED_REGISTER) && state.room == RoomId.HALLWAY;
            case TAKE_TAPE -> state.reached(Event.INVESTIGATION_STARTED) && state.room == RoomId.ROOM_304;
            case CHECK_POWER -> state.has(Item.CASSETTE) && state.room == RoomId.ROOM_304;
            case RECEIVE_BATTERIES -> state.reached(Event.RECORDER_CHECKED) && state.room == RoomId.GUARD_ROOM;
            case HEAR_TAPE -> state.has(Item.CASSETTE) && state.has(Item.BATTERY_PACK) && state.room == RoomId.ROOM_304;
            case CONFESS -> state.knows(Clue.CASSETTE_CONTENT) && state.room == RoomId.CLASSROOM;
            case SIGN -> StoryConditions.evidenceComplete(state) && (state.book == BookLocation.CARRIED || state.book == BookLocation.DESK_HUNG) && state.room == RoomId.CLASSROOM;
            case WARNING -> state.knows(Clue.SIGNED_ADDENDUM) && state.book == BookLocation.DESK_HUNG && state.room == RoomId.CLASSROOM;
            case PREPARE_FINAL -> StoryConditions.canPrepareFinal(state);
            case REQUEST_A -> StoryConditions.canChooseA(state);
            case RESOLVE_A -> state.requestedEnding == Ending.A && state.book == BookLocation.FINAL_TABLE && state.room == RoomId.ROOM_304;
            case EXIT_A -> state.requestedEnding == Ending.A && state.reached(Event.RESOLVED) && state.room == RoomId.GUARD_ROOM;
            case FINISH_A -> StoryConditions.canFinishA(state);
            case REQUEST_B -> StoryConditions.canRetreat(state);
            case LOCK_FOR_B -> state.requestedEnding == Ending.B && state.room == RoomId.ROOM_304;
            case EXIT_B -> state.completed.contains(Action.LOCK_FOR_B.contentId) && state.requestedEnding == Ending.B && state.book == BookLocation.LOCKED_CABINET && state.cabinetLocked && state.room == RoomId.GUARD_ROOM;
            case FINISH_B -> StoryConditions.canFinishB(state);
            case REQUEST_C -> StoryConditions.canChooseA(state);
            case FINISH_C -> state.requestedEnding == Ending.C && StoryConditions.fullReady(state)
                && state.reached(Event.FINAL_PREPARED) && state.room == RoomId.ROOM_304
                && state.book == BookLocation.FINAL_TABLE;
        };
    }
    private void apply(Action a) {
        switch (a) {
            case OPENING -> { state.events.add(Event.OPENING_DONE); state.npcStage = NpcStage.INITIAL; }
            case PHOTO -> state.clues.add(Clue.CLASS_PHOTO);
            case OPEN_REGISTER -> { state.clues.add(Clue.ALTERED_REGISTER); state.book = BookLocation.CARRIED; state.bookCoverClosed = false; state.npcStage = NpcStage.LOOP; }
            case VERIFY_LOOP -> state.events.add(Event.LOOP_CONFIRMED);
            case MEET_DUC -> { state.events.add(Event.DUC_MET); state.npcStage = NpcStage.GROUP; }
            case REVEAL_GUARD_DOOR -> state.events.add(Event.GUARD_DOOR_REVEALED);
            case RECEIVE_KEY -> { state.items.add(Item.CABINET_KEY); state.events.add(Event.NAM_MET); }
            case LOCK_REGISTER, LOCK_FOR_B -> { state.book = BookLocation.LOCKED_CABINET; state.cabinetLocked = true; state.bookCoverClosed = true; }
            case VERIFY_EXIT -> state.events.add(Event.RETREAT_TRIAL_DONE);
            case MEET_HUNG -> { state.events.add(Event.HUNG_INTRO_DONE); state.npcStage = NpcStage.DECISION; }
            case REOPEN_REGISTER -> { state.book = BookLocation.CARRIED; state.cabinetLocked = false; state.bookCoverClosed = false; state.events.add(Event.INVESTIGATION_STARTED); state.npcStage = NpcStage.INVESTIGATE; }
            case READ_RECORDS -> state.clues.add(Clue.EVACUATION_AND_REPORT);
            case FAMILY_PHOTO -> { state.clues.add(Clue.CLASS_PHOTO); state.events.addAll(EnumSet.of(Event.DUC_PHOTO_SEEN, Event.NGA_IDENTIFIED)); }
            case READ_NEWSLETTER -> state.events.add(Event.NEWSLETTER_READ);
            case TAKE_TAPE -> { state.items.add(Item.CASSETTE); state.tape = TapeLocation.CARRIED; }
            case CHECK_POWER -> state.events.add(Event.RECORDER_CHECKED);
            case RECEIVE_BATTERIES -> { state.items.add(Item.BATTERY_PACK); state.battery = BatteryLocation.CARRIED; }
            case HEAR_TAPE -> { state.clues.add(Clue.CASSETTE_CONTENT); state.battery = BatteryLocation.PLAYER; }
            case CONFESS -> { state.events.add(Event.HUNG_CONFESSION_DONE); state.npcStage = NpcStage.CONFRONT; }
            case SIGN -> { state.clues.add(Clue.SIGNED_ADDENDUM); state.book = BookLocation.DESK_HUNG; state.supplement = SupplementLocation.DESK_HUNG; }
            case WARNING -> { state.events.add(Event.FINAL_WARNING_DONE); state.book = BookLocation.CARRIED; state.supplement = SupplementLocation.CARRIED; state.npcStage = NpcStage.READY; }
            case PREPARE_FINAL -> { state.book = BookLocation.FINAL_TABLE; state.tape = TapeLocation.FINAL_TABLE; state.records = RecordsLocation.FINAL_TABLE; state.supplement = SupplementLocation.FINAL_TABLE; state.events.add(Event.FINAL_PREPARED); state.npcStage = NpcStage.FINAL; }
            case REQUEST_A -> state.requestedEnding = Ending.A;
            case RESOLVE_A -> state.events.add(Event.RESOLVED);
            case EXIT_A -> { state.events.add(Event.OUTSIDE_ALL); state.book = BookLocation.CARRIED; state.tape = TapeLocation.CARRIED; state.records = RecordsLocation.NAM; state.supplement = SupplementLocation.CARRIED; state.npcStage = NpcStage.EXIT_A; }
            case FINISH_A -> { state.records = RecordsLocation.CARRIED; state.items.remove(Item.CABINET_KEY); state.ending = Ending.A; }
            case REQUEST_B -> { state.requestedEnding = Ending.B; state.npcStage = NpcStage.FINAL; }
            case EXIT_B -> { state.events.add(Event.OUTSIDE_ALL); state.npcStage = NpcStage.EXIT_B; if (state.has(Item.CASSETTE)) state.tape = TapeLocation.CARRIED; if (state.knows(Clue.SIGNED_ADDENDUM)) state.supplement = SupplementLocation.CARRIED; if (state.records == RecordsLocation.FINAL_TABLE) state.records = RecordsLocation.NAM; }
            case FINISH_B -> { state.items.remove(Item.CABINET_KEY); state.ending = Ending.B; }
            case REQUEST_C -> state.requestedEnding = Ending.C;
            case FINISH_C -> state.ending = Ending.C;
        }
    }
}
