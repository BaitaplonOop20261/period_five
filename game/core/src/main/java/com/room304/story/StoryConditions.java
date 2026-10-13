package com.room304.story;

import static com.room304.story.GameState.*;

/** Shared predicates from script 1.0 §6; owning a tape does not mean hearing it. */
public final class StoryConditions {
    private StoryConditions() {}
    public static boolean canRetreat(GameState s) {
        return s.ending() == Ending.NONE && s.has(Item.CABINET_KEY) && s.reached(Event.NAM_MET)
            && s.reached(Event.RETREAT_TRIAL_DONE) && s.reached(Event.HUNG_INTRO_DONE);
    }
    public static boolean evidenceComplete(GameState s) {
        return s.knows(Clue.CLASS_PHOTO) && s.knows(Clue.ALTERED_REGISTER)
            && s.knows(Clue.EVACUATION_AND_REPORT) && s.knows(Clue.CASSETTE_CONTENT)
            && s.reached(Event.HUNG_CONFESSION_DONE);
    }
    public static boolean fullReady(GameState s) {
        return evidenceComplete(s) && s.knows(Clue.SIGNED_ADDENDUM) && s.reached(Event.FINAL_WARNING_DONE)
            && s.reached(Event.INVESTIGATION_STARTED) && s.has(Item.CABINET_KEY);
    }
    public static boolean canPrepareFinal(GameState s) {
        return s.ending() == Ending.NONE && s.requestedEnding() == Ending.NONE && s.room() == RoomId.ROOM_304
            && s.book() == BookLocation.CARRIED && canRetreat(s) && fullReady(s);
    }
    public static boolean canChooseA(GameState s) {
        return s.ending() == Ending.NONE && s.requestedEnding() == Ending.NONE && s.room() == RoomId.ROOM_304
            && s.book() == BookLocation.FINAL_TABLE && s.reached(Event.FINAL_PREPARED) && fullReady(s);
    }
    public static boolean canFinishA(GameState s) {
        return s.ending() == Ending.NONE && s.requestedEnding() == Ending.A && fullReady(s)
            && s.reached(Event.FINAL_PREPARED) && s.reached(Event.RESOLVED) && s.reached(Event.OUTSIDE_ALL)
            && s.room() == RoomId.GUARD_ROOM && s.book() == BookLocation.CARRIED;
    }
    public static boolean canFinishB(GameState s) {
        return canRetreat(s) && s.requestedEnding() == Ending.B && s.reached(Event.OUTSIDE_ALL)
            && s.room() == RoomId.GUARD_ROOM && s.book() == BookLocation.LOCKED_CABINET && s.cabinetLocked();
    }
}
