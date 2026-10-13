package com.room304.story;

import java.util.*;

/** Progress of ONE playthrough. No textures, UI, timers or duplicated player coordinates. */
public final class GameState {
    public enum RoomId { ROOM_304, HALLWAY, CLASSROOM, GUARD_ROOM }
    public enum BookLocation { TABLE_304, CARRIED, LOCKED_CABINET, DESK_HUNG, FINAL_TABLE }
    public enum Item { CABINET_KEY, CASSETTE, BATTERY_PACK }
    public enum Clue { CLASS_PHOTO, ALTERED_REGISTER, EVACUATION_AND_REPORT, CASSETTE_CONTENT, SIGNED_ADDENDUM }
    public enum Event {
        OPENING_DONE, LOOP_CONFIRMED, DUC_MET, GUARD_DOOR_REVEALED, NAM_MET,
        RETREAT_TRIAL_DONE, HUNG_INTRO_DONE, INVESTIGATION_STARTED, RECORDER_CHECKED,
        HUNG_CONFESSION_DONE, FINAL_WARNING_DONE, DUC_PHOTO_SEEN, NGA_IDENTIFIED,
        NEWSLETTER_READ, FINAL_PREPARED, OUTSIDE_ALL, RESOLVED,
        INITIAL_SCENE_DONE, PLAYER_EXAMINED, CHALK_MARK, DUC_ASKED_PHOTO, DUC_ASKED_NAM,
        STAY_EXPLAINED, HUNG_MENU_OPENED, ASKED_REGISTER, ASKED_REGISTER_PHOTO, ASKED_COUNT
    }
    public enum Ending { NONE, A, B, C }
    public enum Phenomenon { NORMAL, ACTIVE, SUSPENDED, RESOLVED }
    public enum NpcStage { INTRO, INITIAL, LOOP, GROUP, DECISION, INVESTIGATE, CONFRONT, READY, FINAL, EXIT_A, EXIT_B }
    public enum TapeLocation { CRATE, CARRIED, PLAYER, FINAL_TABLE }
    public enum BatteryLocation { GUARD_ROOM, CARRIED, PLAYER }
    public enum RecordsLocation { GUARD_ROOM, FINAL_TABLE, NAM, CARRIED }
    public enum SupplementLocation { NONE, DESK_HUNG, CARRIED, FINAL_TABLE }

    RoomId room = RoomId.HALLWAY;
    BookLocation book = BookLocation.TABLE_304;
    final EnumSet<Item> items = EnumSet.noneOf(Item.class);
    final EnumSet<Clue> clues = EnumSet.noneOf(Clue.class);
    final EnumSet<Event> events = EnumSet.noneOf(Event.class);
    final Set<String> completed = new LinkedHashSet<>();
    Ending requestedEnding = Ending.NONE;
    Ending ending = Ending.NONE;
    NpcStage npcStage = NpcStage.INTRO;
    boolean cabinetLocked = true;
    boolean bookCoverClosed = true;
    TapeLocation tape = TapeLocation.CRATE;
    BatteryLocation battery = BatteryLocation.GUARD_ROOM;
    RecordsLocation records = RecordsLocation.GUARD_ROOM;
    SupplementLocation supplement = SupplementLocation.NONE;

    public RoomId room() { return room; }
    public BookLocation book() { return book; }
    public Ending ending() { return ending; }
    public Ending requestedEnding() { return requestedEnding; }
    public NpcStage npcStage() { return npcStage; }
    public boolean cabinetLocked() { return cabinetLocked; }
    public boolean bookCoverClosed() { return bookCoverClosed; }
    public TapeLocation tapeLocation() { return tape; }
    public BatteryLocation batteryLocation() { return battery; }
    public RecordsLocation recordsLocation() { return records; }
    public SupplementLocation supplementLocation() { return supplement; }
    public boolean has(Item item) { return items.contains(item); }
    public boolean knows(Clue clue) { return clues.contains(clue); }
    public boolean reached(Event event) { return events.contains(event); }
    public Set<Item> items() { return Collections.unmodifiableSet(items); }
    public Set<Clue> clues() { return Collections.unmodifiableSet(clues); }
    public Set<Event> events() { return Collections.unmodifiableSet(events); }
    public Set<String> completedSteps() { return Collections.unmodifiableSet(completed); }
    /** Called only AFTER the screen validates map/spawn and successfully places the player. */
    public void enteredRoom(RoomId room) { this.room = Objects.requireNonNull(room); }
    public Phenomenon phenomenon() {
        if (reached(Event.RESOLVED)) return Phenomenon.RESOLVED;
        if (!knows(Clue.ALTERED_REGISTER)) return Phenomenon.NORMAL;
        return book == BookLocation.LOCKED_CABINET && cabinetLocked ? Phenomenon.SUSPENDED : Phenomenon.ACTIVE;
    }
}
