package com.room304.story;

import com.room304.dialogue.*;
import com.room304.world.SceneCatalog;
import java.util.*;
import java.util.function.BiConsumer;
import static com.room304.story.GameState.*;
import static com.room304.story.StoryDirector.Action.*;

/** Runtime story orchestration. One state/director/controller across all rooms; no rendering. */
public final class StorySession {
    public record Note(String id,String title,List<String> text) {}
    private final StoryContent content;
    private final SceneCatalog world;
    private final StoryDirector director;
    private final DialogueController dialogue = new DialogueController();
    private final BiConsumer<RoomId,String> movePlayer;
    private final java.util.function.Consumer<String> onBlock;
    private String block, cue, objectiveOverride;
    private float elapsed;
    private boolean ended, notes, gathered, ngaVisible;
    private String overlay="NONE";
    public String overlay() {return overlay;}
    public StorySession(StoryContent content,SceneCatalog world,GameState state,BiConsumer<RoomId,String> movePlayer) {
        this(content,world,state,movePlayer,id -> {});
    }
    /** Optional diagnostics observer; never changes progression. */
    public StorySession(StoryContent content,SceneCatalog world,GameState state,BiConsumer<RoomId,String> movePlayer,java.util.function.Consumer<String> onBlock) {
        this.content=content; this.world=world; this.director=new StoryDirector(state); this.movePlayer=movePlayer;this.onBlock=onBlock;
    }
    public GameState state() { return director.state(); }
    public StoryDirector director() { return director; }
    public DialogueController dialogue() { return dialogue; }
    public String cue() { return cue; }
    public String block() { return block; }
    public boolean ended() { return ended; }
    public boolean isNotes() { return notes; }
    public boolean exploring() { return !dialogue.isActive() && !director.blocksExploration() && !ended; }
    public boolean canSave() { return exploring() && !notes && state().reached(Event.INITIAL_SCENE_DONE); }
    public List<String> visibleNpcs() {
        List<String> ids = new ArrayList<>(world.visibleNpcs(state()));
        if (block != null && block.startsWith("C4-01") && state().room() == RoomId.ROOM_304 && !ids.contains("duc")) ids.add("duc");
        if (block != null && block.startsWith("C3-01") && !ids.contains("nam")) ids.add("nam");
        if (block != null && block.equals("C3-03-MAIN") && !ids.contains("hung")) ids.add("hung");
        if (block != null && block.equals("C6-01-GATHER") && gathered) {ids = new ArrayList<>(List.of("trang","duc","nam","hung"));if(ngaVisible)ids.add("nga");}
        if(state().requestedEnding()==Ending.B) ids.remove("nga");
        return List.copyOf(ids);
    }
    public boolean present(String npc) { return visibleNpcs().contains(npc); }
    public void start(boolean skipOpening) {
        if (skipOpening) { action(OPENING); move(RoomId.ROOM_304,"entry"); play("C1-01-MAIN"); }
        else play("C0-01-MAIN");
    }
    private void move(RoomId room,String spawn) {
        world.scene(room).spawn(spawn); movePlayer.accept(room,spawn); state().enteredRoom(room);
    }
    private void action(StoryDirector.Action action) {
        StoryDirector.Step step = director.activeStep();
        if (step == null) step = director.begin(action);
        if (step.action() != action) throw new IllegalStateException("Unexpected pending story action " + step.action());
        if (!director.complete(step, action == REOPEN_REGISTER ? "CONFIRM" : null)) throw new IllegalStateException("Stale story completion");
    }
    private void play(String id) {
        if (id.equals("C5-05-FULL")) director.begin(SIGN);
        onBlock.accept(id);block=id; elapsed=0; Dialogue data=content.dialogue(id);
        dialogue.start(data,choice -> { director.finishedBlock(id); afterBlock(id); });
        enteredLine();
    }
    private void enteredLine() {
        elapsed=0;cue=dialogue.isActive()?dialogue.getLine().getCue():null;
        if(cue==null)return;
        switch(cue){case "C5-06-MAIN-E02"->overlay="FAINT_LINH";case "C5-06-MAIN-E07"->overlay="NONE";case "C-02-MAIN-E02"->overlay="CLEAR_LINH";case "C6-01-GATHER-E02"->gathered=true;case "C6-01-GATHER-E10"->ngaVisible=true;default->{}}
    }
    public void update(float delta) {
        if (!Float.isFinite(delta) || delta < 0) throw new IllegalArgumentException("Invalid story delta");
        if (!dialogue.isActive()) return;
        elapsed += delta;
        if (dialogue.getLine().isAutomatic() && elapsed >= dialogue.getLine().getDuration()) advance();
    }
    public void advance() {
        if (!dialogue.isActive() || !dialogue.getLine().getChoices().isEmpty() || elapsed < dialogue.getLine().getDuration()) return;
        String finished=dialogue.getLine().getCue();
        if (finished != null) afterBeat(finished);
        dialogue.advance(); enteredLine();
    }
    public void choose(String id) { dialogue.choose(id); enteredLine(); }
    public void close() { dialogue.close(); director.cancel(); cue=null; }
    private void explore() { block=null; cue=null; notes=false; dialogue.close(); }
    private void afterBeat(String id) {
        director.stageBeat(id);
        switch (id) {
            case "C0-03-MAIN-E10" -> { action(OPENING); move(RoomId.ROOM_304,"entry"); }
            case "C1-02-SAVE-E01", "C4-01-COUNT-E04" -> action(PHOTO);
            case "C1-04-HAUNT-E11" -> action(OPEN_REGISTER);
            case "C2-01-MAIN-E01", "C2-01-MAIN-E07" -> move(RoomId.HALLWAY,"chalk");
            case "C2-01-MAIN-E11" -> action(VERIFY_LOOP);
            case "C2-02-MAIN-E10" -> action(MEET_DUC);
            case "C2-03-DOC-E02" -> action(READ_NEWSLETTER);
            case "C2-04-MAIN-E01" -> action(REVEAL_GUARD_DOOR);
            case "C3-01-MAIN-E13" -> { action(RECEIVE_KEY); move(RoomId.ROOM_304,"cabinet"); }
            case "C3-02-MAIN-E02" -> action(LOCK_REGISTER);
            case "C3-02-MAIN-E05" -> move(RoomId.GUARD_ROOM,"gate");
            case "C3-02-MAIN-E08" -> action(VERIFY_EXIT);
            case "C3-03-MAIN-E10" -> action(MEET_HUNG);
            case "C3-04-CONFIRM-E04" -> action(REOPEN_REGISTER);
            case "C3-05-SAVE-E01" -> action(READ_RECORDS);
            case "C4-01-TRANS-E02" -> move(RoomId.ROOM_304,"family");
            case "C4-01-PLACE-E03" -> action(FAMILY_PHOTO);
            case "C4-02-TAKE-E01" -> action(TAKE_TAPE);
            case "C4-03-END-E01" -> action(CHECK_POWER);
            case "C4-04-MAIN-E03" -> action(RECEIVE_BATTERIES);
            case "C4-05-MAIN-E01" -> director.stageBeat("C4-05-MAIN-E01-PINS");
            case "C4-05-END-E01" -> action(HEAR_TAPE);
            case "C5-04-CONFESS-E16" -> action(CONFESS);
            case "C5-05-FULL-E11" -> action(SIGN);
            case "C5-06-MAIN-E11" -> action(WARNING);
            case "C6-01-GATHER-E12" -> action(PREPARE_FINAL);
            case "A-01-MAIN-E03" -> action(RESOLVE_A);
            case "A-01-MAIN-E08" -> { move(RoomId.GUARD_ROOM,"outside"); action(EXIT_A); }
            case "A-02-MAIN-E08" -> action(FINISH_A);
            case "B-01-ALREADY-E02", "B-01-LOCK-E02" -> action(LOCK_FOR_B);
            case "B-01-EXIT-E01" -> move(RoomId.GUARD_ROOM,"gate");
            case "B-01-EXIT-E03" -> { move(RoomId.GUARD_ROOM,"outside"); action(EXIT_B); }
            case "B-02-MAIN-E05" -> action(FINISH_B);
            case "C-02-MAIN-E14" -> action(FINISH_C);
            default -> { }
        }
    }
    private void afterBlock(String id) {
        switch (id) {
            case "C0-01-MAIN" -> play("C0-02-MAIN");
            case "C0-02-MAIN" -> play("C0-03-MAIN");
            case "C0-03-MAIN" -> play("C1-01-MAIN");
            case "C1-02-WITH" -> play(state().knows(Clue.ALTERED_REGISTER)?"C1-02-WITHREAD":"C1-02-SAVE");
            case "C1-02-ALONE" -> play(state().knows(Clue.ALTERED_REGISTER)?"C1-02-ALONEREAD":"C1-02-SAVE");
            case "C1-04-READ" -> play(state().knows(Clue.CLASS_PHOTO)?"C1-04-PHOTO":"C1-04-HAUNT");
            case "C2-02-MAIN", "C2-02-PHOTO", "C2-02-NAM" -> ducQuestions();
            case "C2-03-DOC" -> play(present("trang")?"C2-03-WITH":"C2-03-ALONE");
            case "C2-03-WITH" -> { if (present("duc") && state().reached(Event.DUC_MET)) play("C2-03-DUC"); else explore(); }
            case "C3-01-MAIN" -> explore(); // The test begins only when E targets the lower cabinet.
            case "C3-02-MAIN" -> { move(RoomId.CLASSROOM,"table"); play("C3-03-MAIN"); }
            case "C3-03-MAIN" -> { move(RoomId.ROOM_304,"cabinet"); play("C3-04-MAIN"); }
            case "C3-04-MAIN" -> decision();
            case "C3-04-CONFIRMBOX" -> reopenMenu();
            case "C3-05-MAIN" -> play(state().knows(Clue.CASSETTE_CONTENT)?"C3-05-HEARD":"C3-05-UNHEARD");
            case "C4-01-MAIN" -> play(state().knows(Clue.CLASS_PHOTO)?"C4-01-PLACE":"C4-01-COUNT");
            case "C4-01-PLACE" -> play(state().knows(Clue.CASSETTE_CONTENT)?"C4-01-HEARD":state().has(Item.CASSETTE)?"C4-01-OWNED":"C4-01-FIND");
            case "C4-02-MAIN" -> play(present("duc")?"C4-02-DUC1":"C4-02-LABEL");
            case "C4-02-LABEL" -> play(present("duc")?"C4-02-DUC2":"C4-02-ALONE");
            case "C4-03-MAIN" -> play(present("duc")?"C4-03-WITH":"C4-03-ALONE");
            case "C4-05-MAIN" -> play(state().knows(Clue.EVACUATION_AND_REPORT)?"C4-05-RECORDS":"C4-05-NORECORDS");
            case "C5-01-MENU" -> hungMenu();
            case "C5-02-DENY", "C5-02-REPEAT" -> {
                if (state().knows(Clue.CLASS_PHOTO) && !state().reached(Event.ASKED_REGISTER_PHOTO)) play("C5-02-PHOTO"); else registerHint();
            }
            case "C5-02-PHOTO" -> registerHint();
            case "C5-03-DENY", "C5-03-REPEAT" -> play(state().knows(Clue.CASSETTE_CONTENT)?"C5-03-HEARD":"C5-03-UNHEARD");
            case "C5-04-MAIN" -> play(state().knows(Clue.EVACUATION_AND_REPORT)?"C5-04-REPORT":"C5-04-NOREPORT");
            case "C6-01-MISS-PHOTO", "C6-01-MISS-RECORDS", "C6-01-MISS-TAPE", "C6-01-MISS-CONFESS", "C6-01-MISS-SIGNED" -> missingMenu(id);
            case "C6-01-FINALMENU" -> finalMenu();
            case "B-01-TRANS" -> play(state().knows(Clue.SIGNED_ADDENDUM)?"B-01-FULL":state().knows(Clue.CASSETTE_CONTENT)?"B-01-HEARD":"B-01-EARLY");
            case "B-01-EARLY", "B-01-HEARD", "B-01-FULL" -> play(state().book()==BookLocation.LOCKED_CABINET?"B-01-ALREADY":"B-01-LOCK");
            case "C-01-MAIN" -> confirmC();
            default -> {
                String target = content.next(id).split(";")[0].trim();
                switch (target) {
                    case "EXP" -> explore();
                    case "RETURN-HUNG" -> play("C5-01-MENU");
                    case "UI-END" -> { dialogue.close(); block=null; cue=null; ended=true; }
                    case "C5-06" -> play("C5-06-MAIN");
                    case "A-02" -> play("A-02-MAIN");
                    case "B-02" -> play("B-02-MAIN");
                    default -> play(target);
                }
            }
        }
    }
    private void registerHint() {
        if (state().knows(Clue.CASSETTE_CONTENT)) { director.finishedBlock("C5-02-HINT"); play("C5-01-MENU"); }
        else play("C5-02-HINT");
    }
    private DialogueChoice option(String id) { return new DialogueChoice(id,content.text(id),"END"); }
    private void menu(String id,String heading,List<String> ids,java.util.function.Consumer<String> chosen) {
        block=id; elapsed=0; cue=null;
        dialogue.start(content.menu(id,heading,ids.stream().map(this::option).toList()),chosen);
    }
    private void ducQuestions() {
        List<String> ids=new ArrayList<>();
        if (!state().reached(Event.DUC_ASKED_PHOTO)) ids.add("C2-02-CHOICE01");
        if (!state().reached(Event.DUC_ASKED_NAM)) ids.add("C2-02-CHOICE02");
        ids.add("C2-02-CHOICE03");
        menu("DUC-QUESTIONS","",ids,c -> play(switch(c) { case "C2-02-CHOICE01" -> "C2-02-PHOTO"; case "C2-02-CHOICE02" -> "C2-02-NAM"; default -> "C2-04-MAIN"; }));
    }
    private void decision() {
        menu("DECIDE","",List.of("C3-04-CHOICE01","C3-04-CHOICE02"),c -> {
            if (c.equals("C3-04-CHOICE01")) beginB();
            else play(state().reached(Event.STAY_EXPLAINED)?"C3-04-CONFIRMBOX":"C3-04-STAY");
        });
    }
    private void reopenMenu() {
        menu("REOPEN","",List.of("C3-04-CHOICE03","C3-04-CHOICE04"),c -> { if(c.equals("C3-04-CHOICE03")) play("C3-04-CONFIRM"); else decision(); });
    }
    private void hungMenu() {
        List<String> ids=new ArrayList<>(); ids.add("C5-01-CHOICE01");
        if(state().knows(Clue.EVACUATION_AND_REPORT)) ids.add("C5-01-CHOICE02");
        if(state().knows(Clue.CASSETTE_CONTENT) && !state().reached(Event.HUNG_CONFESSION_DONE)) ids.add("C5-01-CHOICE03");
        if(state().reached(Event.HUNG_CONFESSION_DONE) && !state().knows(Clue.SIGNED_ADDENDUM)) ids.add("C5-01-CHOICE04");
        if(state().knows(Clue.SIGNED_ADDENDUM)) ids.add("C5-01-CHOICE05"); ids.add("C5-01-CHOICE06");
        menu("HUNG-MENU","",ids,c -> { switch(c) {
            case "C5-01-CHOICE01" -> play(state().reached(Event.HUNG_CONFESSION_DONE)?"C5-02-CONFIRMED":state().reached(Event.ASKED_REGISTER)?"C5-02-REPEAT":"C5-02-DENY");
            case "C5-01-CHOICE02" -> play(state().reached(Event.HUNG_CONFESSION_DONE)?"C5-03-CONFIRMED":state().reached(Event.ASKED_COUNT)?"C5-03-REPEAT":"C5-03-DENY");
            case "C5-01-CHOICE03" -> play("C5-04-MAIN");
            case "C5-01-CHOICE04" -> play(!state().knows(Clue.CLASS_PHOTO)?"C5-05-MISSINGPHOTO":!state().knows(Clue.EVACUATION_AND_REPORT)?"C5-05-MISSINGRECORDS":"C5-05-FULL");
            case "C5-01-CHOICE05" -> readDocs("DOC-SIGNED",this::hungMenu);
            default -> explore();
        }});
    }
    private void beginB() { action(REQUEST_B); move(RoomId.ROOM_304,"cabinet"); play("B-01-TRANS"); }
    private void missingMenu(String id) {
        objectiveOverride=switch(id) { case "C6-01-MISS-PHOTO" -> "OBJ-14"; case "C6-01-MISS-RECORDS" -> "OBJ-08"; case "C6-01-MISS-CONFESS" -> "OBJ-13"; case "C6-01-MISS-SIGNED" -> "OBJ-15"; default -> tapeObjective(); };
        menu("MISSING","",List.of("C6-01-CHOICE01","C6-01-CHOICE02"),c -> { if(c.equals("C6-01-CHOICE02")) beginB(); else explore(); });
    }
    private void finalMenu() {
        List<String> ids=new ArrayList<>(List.of("C6-01-CHOICE03","C6-01-CHOICE04"));
        if(world.enableC()) ids.add("C6-01-CHOICE05");
        menu("FINAL","",ids,c -> { switch(c) {
            case "C6-01-CHOICE03" -> { action(REQUEST_A); play("A-01-MAIN"); }
            case "C6-01-CHOICE04" -> beginB();
            case "C6-01-CHOICE05" -> play("C-01-MAIN");
            default -> throw new IllegalArgumentException("Invalid final choice");
        }});
    }
    private void confirmC() {
        // Cancel is deliberately the first highlighted option.
        menu("CONFIRM-C","",List.of("C-01-CHOICE02","C-01-CHOICE01"),c -> { if(c.equals("C-01-CHOICE01")) { if(!world.enableC()) throw new IllegalStateException("C disabled"); action(REQUEST_C); play("C-02-MAIN"); } else play("C6-01-FINALMENU"); });
    }
    private void say(String id,Runnable after) {
        String t=content.text(id); int split=t.indexOf(':'); String speaker=split<0?"":t.substring(0,split); String text=split<0?t:t.substring(split+1).strip();
        block=id; elapsed=0; cue=null;
        dialogue.start(new Dialogue(id,List.of(new DialogueLine(speaker,text,null))),c -> after.run());
    }
    private void say(String id) { say(id,this::explore); }
    private void read(String id,List<String> text,Runnable after) {
        block=id; cue=null; elapsed=0; dialogue.start(new Dialogue(id,text.stream().map(t -> new DialogueLine(content.documentId(t),"",t,null)).toList()),c -> after.run());
    }
    private List<String> document(String prefix) {
        if(prefix.equals("TRANSCRIPT")) return List.of("C4-05-MAIN-TXT04","C4-05-MAIN-D06","C4-05-MAIN-D07","C4-05-MAIN-D08","C4-05-MAIN-SFX09","C4-05-MAIN-SFX10","C4-05-MAIN-D11","C4-05-MAIN-D12","C4-05-MAIN-SFX13").stream().map(content::text).toList();
        int count=switch(prefix) { case "DOC-MSG","DOC-PHOTO","DOC-NEWS" -> 3; case "DOC-REGISTER","DOC-REPORT","DOC-INVENTORY" -> 5; case "DOC-GATE" -> 4; case "DOC-SIGNED" -> 7; default -> 1; };
        List<String> lines=new ArrayList<>(); for(int i=1;i<=count;i++) lines.add(content.text(prefix+"-"+String.format(java.util.Locale.ROOT,"%02d",i))); return lines;
    }
    private void readDocs(String prefix,Runnable after) { read(prefix,document(prefix),after); }
    private void objectRead(String reminder,List<String> pages) {
        say(reminder,() -> menu("OBJECT","",List.of("CHO-OBJECT-READ","CHO-OBJECT-BACK"),c -> { if(c.equals("CHO-OBJECT-READ")) read(reminder,pages,this::explore); else explore(); }));
    }
    public void interact(String id) {
        if(!exploring()) return; objectiveOverride=null;
        if (visibleNpcs().contains(id)) { interactNpc(id); return; }
        SceneCatalog.ObjectDef object=world.scene(state().room()).object(id); // Never execute a remote/unknown object.
        if(object.requiredEvent!=null&&!state().reached(Event.valueOf(object.requiredEvent)))return;
        if(state().reached(Event.NAM_MET) && !state().reached(Event.RETREAT_TRIAL_DONE) && !id.equals("cabinet") && !id.equals("cabinet.upper")) { say("RE-TEST-FIRST"); return; }
        switch(id) {
            case "photo" -> { if(state().knows(Clue.CLASS_PHOTO)) objectRead("RE-PHOTO",document("DOC-PHOTO")); else play(present("trang")?"C1-02-WITH":"C1-02-ALONE"); }
            case "desk" -> {
                if(!state().knows(Clue.ALTERED_REGISTER)) play("C1-04-READ");
                else if(state().reached(Event.INVESTIGATION_STARTED)) prepare(); else say("RE-DESK-EARLY");
            }
            case "player" -> {
                if(state().knows(Clue.CASSETTE_CONTENT)) objectRead("RE-PLAYER-HEARD",document("TRANSCRIPT"));
                else if(state().has(Item.BATTERY_PACK)) play("C4-05-MAIN");
                else if(state().reached(Event.RECORDER_CHECKED)) say("RE-PLAYER-POWER");
                else if(state().has(Item.CASSETTE)) play("C4-03-MAIN");
                else if(state().reached(Event.PLAYER_EXAMINED)) say("RE-PLAYER-EMPTY");
                else play(present("trang")?"C1-03-WITH":"C1-03-ALONE");
            }
            case "crate" -> {
                if(!state().reached(Event.INVESTIGATION_STARTED)) say("RE-CRATE-EARLY");
                else if(state().has(Item.CASSETTE)) { List<String> pages=new ArrayList<>(document("DOC-TAPE")); pages.addAll(document("DOC-INVENTORY")); objectRead("RE-CRATE",pages); }
                else play("C4-02-MAIN");
            }
            case "cabinet" -> {
                if(!state().knows(Clue.ALTERED_REGISTER)) say("RE-CABINET-LOCKED");
                else if(!state().has(Item.CABINET_KEY)) say("RE-CABINET-ASK");
                else if(!state().reached(Event.RETREAT_TRIAL_DONE)) play("C3-02-MAIN");
                else if(!state().reached(Event.INVESTIGATION_STARTED)) decision();
                else menu("CABINET","",List.of("CHO-CAB-LEAVE","CHO-CAB-STAY"),c -> { if(c.equals("CHO-CAB-LEAVE")) beginB(); else explore(); });
            }
            case "cabinet.upper" -> say(state().reached(Event.NAM_MET)?"RE-CAB-UPPER":"RE-CAB-UPPER-EARLY");
            case "board" -> { if(!state().knows(Clue.ALTERED_REGISTER)) say("WORLD-NOTICE"); else if(state().reached(Event.NEWSLETTER_READ)) objectRead("RE-NEWS",document("DOC-NEWS")); else play("C2-03-DOC"); }
            case "stairs" -> stairs();
            case "guard.wall" -> {
                if(state().phenomenon()!=Phenomenon.ACTIVE || !state().reached(Event.GUARD_DOOR_REVEALED)) say("RE-WALL");
                else { passage("guard.wall"); if(!state().reached(Event.NAM_MET)) play("C3-01-MAIN"); }
            }
            case "gate" -> { if(state().phenomenon()==Phenomenon.ACTIVE) { move(RoomId.ROOM_304,"entry"); say("RE-GATE-LOOP"); } else if(StoryConditions.canRetreat(state())) decision(); else say("RE-TEST-FIRST"); }
            case "door.hung" -> {
                if(!state().knows(Clue.ALTERED_REGISTER)) say("RE-HUNG-EARLY");
                else if(!state().reached(Event.RETREAT_TRIAL_DONE)) say("RE-HUNG-LOOP");
                else passage("door.hung");
            }
            case "door.304" -> passage(id);
            case "door.hall" -> passage(id);
            case "records" -> { if(state().reached(Event.INVESTIGATION_STARTED)) records(); else say("RE-TEST-FIRST"); }
            case "evidence" -> { if(state().knows(Clue.SIGNED_ADDENDUM)) readDocs("DOC-SIGNED",this::explore); else interactNpc("hung"); }
            case "lastdesk" -> say(state().knows(Clue.ALTERED_REGISTER)?"RE-ENDDESK-HAUNT":"RE-ENDDESK-NORMAL");
            case "window" -> say(state().phenomenon()==Phenomenon.ACTIVE?"RE-WINDOW-HAUNT":"RE-WINDOW-NORMAL");
            default -> {if(object.dialogue!=null)play(object.dialogue);else throw new IllegalArgumentException("Unknown interaction "+id+" (configure dialogue)");}
        }
    }
    private void passage(String id) {SceneCatalog.ObjectDef o=world.scene(state().room()).object(id);move(RoomId.valueOf(o.target),o.spawn);}
    private void stairs() {
        if(!state().knows(Clue.ALTERED_REGISTER)) play("C1-01-EXIT");
        else if(state().phenomenon()==Phenomenon.ACTIVE) {
            if(!state().reached(Event.LOOP_CONFIRMED)) play("C2-01-MAIN");
            else { move(RoomId.HALLWAY,"chalk"); say(state().reached(Event.GUARD_DOOR_REVEALED)?"RE-STAIR-DOOR":"RE-STAIR-LOOP"); }
        } else passage("stairs");
    }
    private void interactNpc(String id) {
        if(!present(id)) throw new IllegalArgumentException("NPC absent " + id);
        SceneCatalog.CharacterDef definition=world.npc(id);if(definition.requiredEvent!=null&&!state().reached(Event.valueOf(definition.requiredEvent)))return;
        switch(id) {
            case "trang" -> say(trangReminder());
            case "duc" -> { if(!state().reached(Event.DUC_MET)) play("C2-02-MAIN"); else say(state().reached(Event.FINAL_WARNING_DONE)?"RE-DUC-READY":state().knows(Clue.CASSETTE_CONTENT)?"RE-DUC-HEARD":state().reached(Event.DUC_PHOTO_SEEN)?"RE-DUC-FAMILY":"RE-DUC-START",this::ducMenu); }
            case "nam" -> say(state().reached(Event.FINAL_WARNING_DONE)?"RE-NAM-READY":state().knows(Clue.CASSETTE_CONTENT)?"RE-NAM-HEARD":state().has(Item.BATTERY_PACK)?"RE-NAM-BAT":state().knows(Clue.EVACUATION_AND_REPORT)?"RE-NAM-RECORDS":"RE-NAM-KEY",this::namMenu);
            case "hung" -> play(state().knows(Clue.SIGNED_ADDENDUM)?"C5-01-SIGNED":state().reached(Event.HUNG_CONFESSION_DONE)?"C5-01-AFTER":state().reached(Event.HUNG_MENU_OPENED)?"C5-01-MENU":"C5-01-OPEN");
            default -> {if(definition.dialogue!=null)play(definition.dialogue);}
        }
    }
    private void ducMenu() {
        List<String> ids=new ArrayList<>(); if(state().reached(Event.INVESTIGATION_STARTED) && !state().reached(Event.DUC_PHOTO_SEEN)) ids.add("CHO-DUC-FAMILY"); ids.add("CHO-DUC-LEAVE");
        menu("DUC","",ids,c -> { if(c.equals("CHO-DUC-FAMILY")) play(state().room()==RoomId.CLASSROOM?"C4-01-TRANS":"C4-01-MAIN"); else explore(); });
    }
    private void namMenu() {
        List<String> ids=new ArrayList<>(); if(state().reached(Event.INVESTIGATION_STARTED)) ids.add("CHO-NAM-RECORDS");
        if(state().reached(Event.RECORDER_CHECKED) && !state().has(Item.BATTERY_PACK)) ids.add("CHO-NAM-BAT"); ids.add("CHO-NAM-LEAVE");
        menu("NAM","",ids,c -> { if(c.equals("CHO-NAM-BAT")) play("C4-04-MAIN"); else if(c.equals("CHO-NAM-RECORDS")) records(); else explore(); });
    }
    private void records() { if(!state().knows(Clue.EVACUATION_AND_REPORT)) play("C3-05-MAIN"); else { List<String> text=new ArrayList<>(document("DOC-GATE"));text.addAll(document("DOC-REPORT"));read("RECORDS",text,this::explore); } }
    private void prepare() {
        if(StoryConditions.canPrepareFinal(state())) play("C6-01-GATHER");
        else play(!state().knows(Clue.CLASS_PHOTO)?"C6-01-MISS-PHOTO":!state().knows(Clue.EVACUATION_AND_REPORT)?"C6-01-MISS-RECORDS":!state().knows(Clue.CASSETTE_CONTENT)?"C6-01-MISS-TAPE":!state().reached(Event.HUNG_CONFESSION_DONE)?"C6-01-MISS-CONFESS":"C6-01-MISS-SIGNED");
    }
    private String trangReminder() {
        GameState s=state();
        if(s.reached(Event.FINAL_WARNING_DONE)) return "RE-TRANG-READY";
        if(s.reached(Event.HUNG_CONFESSION_DONE)) return !s.knows(Clue.CLASS_PHOTO)?"RE-TRANG-PHOTO":!s.knows(Clue.EVACUATION_AND_REPORT)?"RE-TRANG-RECORDS":"RE-TRANG-SIGN";
        if(s.knows(Clue.CASSETTE_CONTENT)) return "RE-TRANG-CONFESS";
        if(s.has(Item.CASSETTE)) return "RE-TRANG-UNHEARD";
        if(s.reached(Event.INVESTIGATION_STARTED)) return s.knows(Clue.EVACUATION_AND_REPORT)?"RE-TRANG-TAPE":"RE-TRANG-RECORDS";
        if(s.reached(Event.NAM_MET)) return "RE-TRANG-LOCK";
        if(s.reached(Event.DUC_MET)) return "RE-TRANG-DOOR";
        if(s.reached(Event.LOOP_CONFIRMED)) return "RE-TRANG-DUC";
        if(s.knows(Clue.ALTERED_REGISTER)) return "RE-TRANG-LOOP";
        return s.knows(Clue.CLASS_PHOTO)?"RE-TRANG-START2":"RE-TRANG-START";
    }
    private String tapeObjective() { return state().has(Item.BATTERY_PACK)?"OBJ-12":state().reached(Event.RECORDER_CHECKED)?"OBJ-11":state().has(Item.CASSETTE)?"OBJ-10":"OBJ-09"; }
    public String objectiveId() {
        GameState s=state(); if(s.ending()!=Ending.NONE) return null;
        if(s.requestedEnding()!=Ending.NONE) return "OBJ-18";
        if(s.reached(Event.FINAL_PREPARED)) return "OBJ-17";
        if(objectiveOverride!=null) return objectiveOverride;
        if(s.reached(Event.INVESTIGATION_STARTED)) {
            if(s.reached(Event.FINAL_WARNING_DONE)) return "OBJ-16";
            if(s.reached(Event.HUNG_CONFESSION_DONE)) return !s.knows(Clue.CLASS_PHOTO)?"OBJ-14":!s.knows(Clue.EVACUATION_AND_REPORT)?"OBJ-08":"OBJ-15";
            if(s.knows(Clue.CASSETTE_CONTENT)) return "OBJ-13";
            if(s.has(Item.CASSETTE)) return tapeObjective();
            return s.knows(Clue.EVACUATION_AND_REPORT)?"OBJ-09":"OBJ-08";
        }
        if(s.reached(Event.HUNG_INTRO_DONE)) return "OBJ-07";
        if(s.reached(Event.RETREAT_TRIAL_DONE)) return "OBJ-06";
        if(s.reached(Event.NAM_MET)) return "OBJ-05";
        if(s.reached(Event.DUC_MET)) return "OBJ-04";
        if(s.reached(Event.LOOP_CONFIRMED)) return "OBJ-03";
        if(s.knows(Clue.ALTERED_REGISTER)) return "OBJ-02";
        return s.reached(Event.INITIAL_SCENE_DONE)?"OBJ-01":null;
    }
    public String objective() { String id=objectiveId();return id==null?"":content.text(id); }
    public List<Note> notes() {
        List<Note> result=new ArrayList<>(); GameState s=state();
        result.add(new Note("NOTE-MESSAGE",content.text("NOTE-MESSAGE"),document("DOC-MSG")));
        addNote(result,s.knows(Clue.CLASS_PHOTO),"PHOTO",s.knows(Clue.SIGNED_ADDENDUM)?"NOTE-PHOTO-02":"NOTE-PHOTO-01","DOC-PHOTO");
        addNote(result,s.knows(Clue.ALTERED_REGISTER),"REGISTER",s.reached(Event.HUNG_CONFESSION_DONE)?"NOTE-REGISTER-02":"NOTE-REGISTER-01","DOC-REGISTER");
        addNote(result,s.reached(Event.LOOP_CONFIRMED),"LOOP","NOTE-LOOP-01",null);
        if(s.reached(Event.DUC_MET)) {
            List<String> text=new ArrayList<>(List.of(content.text("NOTE-DUC-01")));
            if(s.knows(Clue.CASSETTE_CONTENT)) { String first=content.text("NOTE-DUC-01"); text=new ArrayList<>(List.of(first.substring(0,first.indexOf("Gia đình")),content.text("NOTE-DUC-02"))); }
            result.add(new Note("DUC",content.text("NOTE-DUC-TITLE"),text));
        }
        addNote(result,s.reached(Event.NAM_MET),"ESCAPE",s.reached(Event.RETREAT_TRIAL_DONE)?"NOTE-ESCAPE-01":"NOTE-ESCAPE-BEFORE",null);
        addNote(result,s.reached(Event.HUNG_INTRO_DONE),"HUNG",s.reached(Event.HUNG_CONFESSION_DONE)?"NOTE-CONFESSION-01":"NOTE-HUNG-01",null);
        if(s.knows(Clue.EVACUATION_AND_REPORT)) {
            List<String> text=new ArrayList<>(List.of(content.text("NOTE-RECORDS-01")));
            if(s.knows(Clue.CASSETTE_CONTENT)) text.add(content.text("NOTE-RECORDS-02"));
            if(s.reached(Event.HUNG_CONFESSION_DONE)) text.add(content.text("NOTE-RECORDS-03"));
            text.addAll(document("DOC-GATE"));text.addAll(document("DOC-REPORT"));result.add(new Note("RECORDS",content.text("NOTE-RECORDS-TITLE"),text));
        }
        addNote(result,s.reached(Event.DUC_PHOTO_SEEN),"FAMILY","NOTE-FAMILY-01","DOC-FAMILY");
        if(s.has(Item.CASSETTE)) {
            List<String> text=new ArrayList<>(List.of(content.text("NOTE-TAPE-01")));
            if(s.knows(Clue.CASSETTE_CONTENT)) { text.set(0,content.text("DOC-TAPE-01")); text.add(content.text("DOC-INVENTORY-04"));text.add(content.text("NOTE-TAPE-02")); }
            text.addAll(document("DOC-INVENTORY"));result.add(new Note("TAPE",content.text("NOTE-TAPE-TITLE"),text));
        }
        addNote(result,s.reached(Event.RECORDER_CHECKED),"POWER",s.knows(Clue.CASSETTE_CONTENT)?"NOTE-POWER-03":s.has(Item.BATTERY_PACK)?"NOTE-POWER-02":"NOTE-POWER-01",null);
        addNote(result,s.knows(Clue.SIGNED_ADDENDUM),"SIGNED","NOTE-SIGNED-01","DOC-SIGNED");
        addNote(result,s.reached(Event.NEWSLETTER_READ)||s.reached(Event.FINAL_WARNING_DONE),"WARNING",s.reached(Event.FINAL_WARNING_DONE)?"NOTE-WARNING-02":"NOTE-WARNING-01",s.reached(Event.NEWSLETTER_READ)?"DOC-NEWS":null);
        if(s.knows(Clue.CASSETTE_CONTENT)) result.add(new Note("TRANSCRIPT",content.text("TRANSCRIPT-TITLE"),document("TRANSCRIPT")));
        if(StoryConditions.fullReady(s)) result.add(new Note("READY",content.text("OBJ-16"),List.of(content.text("NOTE-READY"))));
        return List.copyOf(result);
    }
    private void addNote(List<Note> out,boolean available,String id,String body,String doc) {
        if(!available) return; List<String> lines=new ArrayList<>(List.of(content.text(body))); if(doc!=null) lines.addAll(document(doc));out.add(new Note(id,content.text("NOTE-"+id+"-TITLE"),lines));
    }
    public void toggleNotes() {
        if(notes) { notes=false;explore();return; }
        if(!exploring()) return; notes=true; notesMenu();
    }
    private void notesMenu() {
        List<Note> available=notes(); List<DialogueChoice> options=new ArrayList<>();
        for(Note n:available) options.add(new DialogueChoice(n.id(),n.title(),"END"));
        if(state().book()==BookLocation.CARRIED) options.add(new DialogueChoice("BOOK",content.text("UI-BOOK-ITEM"),"END"));
        options.add(new DialogueChoice("BACK",content.text("UI-BACK"),"END"));
        block="NOTES";elapsed=0;cue=null;dialogue.start(content.menu("NOTES",content.text("UI-NOTES-TITLE"),options),c -> {
            if(c.equals("BOOK")) bookMenu(); else if(c.equals("BACK")) explore();
            else read(c,available.stream().filter(n->n.id().equals(c)).findFirst().orElseThrow().text(),this::notesMenu);
        });
    }
    private void bookMenu() {
        say(state().reached(Event.FINAL_WARNING_DONE)?"RE-REGISTER-WARN":state().reached(Event.HUNG_CONFESSION_DONE)?"RE-REGISTER-CONFESS":"RE-REGISTER",() ->
            menu("BOOK","",List.of("CHO-REG-READ","CHO-REG-CLOSE","CHO-REG-BACK"),c -> {
                if(c.equals("CHO-REG-READ")) { director.openBook(); readDocs("DOC-REGISTER",this::bookMenu); }
                else if(c.equals("CHO-REG-CLOSE")) { director.closeBook(); say(state().reached(Event.NAM_MET)?present("trang")?"RE-CLOSE-KNOWN-WITH":"RE-CLOSE-KNOWN-ALONE":present("trang")?"RE-CLOSE-UNKNOWN-WITH":"RE-CLOSE-UNKNOWN-ALONE"); }
                else explore();
            }));
    }
}
