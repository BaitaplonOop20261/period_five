package com.room304.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import com.room304.screen.DemoGameScreen;
import com.room304.world.Npc;
import com.room304.world.Player;
import com.room304.Room304Game;
import com.room304.world.WorldMap;
import com.room304.world.Wall;
import com.room304.dialogue.DialogueController;
import com.room304.dialogue.DialogueLoader;
import com.room304.dialogue.Dialogue;
import com.room304.dialogue.DialogueLine;
import com.room304.dialogue.DialogueUI;
import com.room304.dialogue.DialogueHud;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Json;
import com.room304.world.RoomInteractionSystem;
import com.room304.world.RoomInteractionSystem.Kind;
import com.room304.world.wardrobe.WardrobeView;
import com.room304.world.wardrobe.WardrobeController;
import com.room304.world.wardrobe.WardrobeItem;

/** Reproduces user-facing interaction bugs against real assets and the desktop renderer. */
public final class GameplayRegressionChecks {
    private static final String ASK_ABANDONED = "ask_why_abandoned";
    private static final String ASK_STRANGE = "ask_anything_strange";
    private static final String FINISH_CHAT = "finish_intro_chat";
    private static Throwable failure;

    private GameplayRegressionChecks() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Room304 gameplay regression checks");
        config.setWindowedMode(960, 540);
        config.setInitialVisible(false);
        new Lwjgl3Application(new Room304Game() {
            @Override
            public void create() {
                setScreen(new DemoGameScreen(() -> {}));
                Gdx.app.postRunnable(() -> {
                    try {
                        checkGameplay((DemoGameScreen) getScreen());
                    } catch (Throwable error) {
                        failure = error;
                    } finally {
                        Gdx.app.exit();
                    }
                });
            }
        }, config);
        if (failure != null) {
            throw new AssertionError("Desktop gameplay regression failed", failure);
        }
        System.out.println("Desktop gameplay checks passed: overlapping door/NPC ranges, "
            + "nearest geometry across all targets, exact ties, NPC order independence, "
            + "room round trip, actual movement/animation/collision, dialogue opening/closing locks, pause, "
            + "Trang choices and topic return/exit, nameplate visibility, wardrobe gestures and command routing, "
            + "screen hide/show, movement restored.");
    }

    private static void checkApplicationAssetLifecycle() throws ReflectiveOperationException {
        Room304Game app = new Room304Game();
        app.create();
        var assets = field(app, "assets", com.room304.assets.GameAssets.class);
        try {
            check(app.getScreen() instanceof com.room304.screen.MenuScreen, "Application must start at menu");
            app.requestNewGame(false);
            app.render();
            check(app.getScreen() instanceof com.room304.screen.GameScreen, "Menu callback must enter game after frame");
            var texture = assets.texture("player.idle");
            app.requestMenu();
            app.render();
            check(app.getScreen() instanceof com.room304.screen.MenuScreen, "Return must dispose gameplay and recreate menu");
            app.requestNewGame(false);
            app.render();
            check(assets.texture("player.idle") == texture, "Replay must preserve shared texture identity");
        } finally { app.dispose(); }
        try { assets.texture("player.idle"); throw new AssertionError("Disposed application assets remained usable"); }
        catch (IllegalStateException expected) {}
    }

    private static void checkManagedAssetsAndSweptCollision() {
        var assets = new com.room304.assets.GameAssets();
        try {
            assets.validateRequired();
            var sheet = assets.texture("npc.trang.idle");
            int count = assets.loadedCount();
            WorldMap first = new WorldMap(assets, true);
            int withRoom = assets.loadedCount();
            first.dispose();
            WorldMap second = new WorldMap(assets, true);
            check(assets.loadedCount() == withRoom && withRoom > count, "Changing room must reuse cached textures");
            check(assets.texture("npc.trang.idle") == sheet, "Borrower dispose must not destroy shared textures");
            second.dispose();
            Player p = new Player(assets, 50, 100);
            p.update(1f, 1, 0, true, List.of(new Wall(250, 0, 2, 500)), null, List.of(), 1200, 600);
            check(p.getX() + Player.WIDTH <= 250, "Large delta must not tunnel through a thin wall");
            p.dispose();
            check(assets.texture("npc.trang.idle") == sheet, "Player disposal must not clear shared assets");
        } finally { assets.dispose(); assets.dispose(); }
    }

    private static void checkGameplay(DemoGameScreen screen) throws ReflectiveOperationException {
        screen.resize(960, 540);
        screen.render(1.01f); // Finish the black transition without updating gameplay.
        screen.render(0f);
        Player player = field(screen, "player", Player.class);
        DialogueController dialogue = field(screen, "dialogueController", DialogueController.class);
        DialogueUI ui = field(screen, "dialogueUI", DialogueUI.class);
        WorldMap world = field(screen, "worldMap", WorldMap.class);
        check(player.getName().equals("Linh"), "Default player name must identify Linh");
        check(screen.isNameplatesVisible(), "Nameplates must be visible by default");
        screen.setNameplatesVisible(false);
        screen.render(0f);
        check(!screen.isNameplatesVisible(), "Nameplates must be hideable while rendering");
        screen.setNameplatesVisible(true);
        screen.render(0f);
        checkPlayerMovement(world);
        checkManagedAssetsAndSweptCollision();
        checkApplicationAssetLifecycle();
        screen.show();
        checkDistanceSelection(world);
        checkMultipleNpcsAndWardrobe();

        // The real bedroom spawn is inside both the door zone and Trang's interaction range.
        check(world.getNpcs().get(0).canInteract(player.getBounds()), "Fixture must overlap Trang's interaction range");
        check(field(screen, "roomInteractions", RoomInteractionSystem.class).getActiveTarget().getKind() == Kind.EXIT,
            "Door must be the selected marker target when it is nearer");
        renderInput(screen, Input.Keys.F);
        check(scene(screen).equals("HALLWAY"), "F at the bedroom door must exit despite nearby Trang");
        check(!dialogue.isActive(), "Exiting must not also open NPC dialogue");
        renderInput(screen, Input.Keys.F);
        check(scene(screen).equals("ROOM"), "Hallway door must lead back into the bedroom");

        world = field(screen, "worldMap", WorldMap.class);
        // Stay inside both interaction zones, but move closer to Trang than the doorway.
        player.setPosition(WorldMap.EXIT_CENTER_X - Player.WIDTH / 2f, 54f);
        check(world.getExitBounds().overlaps(player.getBounds())
            && world.getNpcs().get(0).canInteract(player.getBounds()), "Fixture must remain in both zones");
        screen.render(0f);
        check(field(screen, "roomInteractions", RoomInteractionSystem.class).getActiveTarget().getKind() == Kind.NPC,
            "NPC must be the selected marker target when it is nearer");
        float openingX = player.getX();
        float openingY = player.getY();
        renderInput(screen, Input.Keys.F, Input.Keys.D, Input.Keys.CONTROL_LEFT);
        check(dialogue.isActive() && scene(screen).equals("ROOM"),
            "F must talk to nearer Trang even inside the door interaction zone");
        check(!player.isMoving(), "Opening dialogue while sprinting must switch to idle immediately");
        check(player.getX() == openingX && player.getY() == openingY, "Opening dialogue must block movement before it happens");
        check(!ui.isTextComplete(), "Opening F must not reveal or skip the first line");
        check(Gdx.input.getInputProcessor() == ui.getStage(), "Dialogue must own its input stage");
        String firstLine = dialogue.getCurrentLine();
        renderInput(screen, Input.Keys.F);
        check(ui.isTextComplete() && dialogue.getCurrentLine().equals(firstLine), "First F must reveal, not advance");
        float x = player.getX();
        float y = player.getY();
        String line = dialogue.getCurrentLine();
        float idleStart = field(player, "stateTime", Float.class);
        Animation<?> idle = field(player, "idleAnimation", Animation.class);
        Object firstFrame = idle.getKeyFrame(idleStart, true);
        for (int direction : new int[] {Input.Keys.W, Input.Keys.A, Input.Keys.S, Input.Keys.D}) {
            renderInput(screen, -1, direction, Input.Keys.CONTROL_LEFT);
            check(player.getX() == x && player.getY() == y, "Movement/sprint must be blocked during dialogue");
            check(dialogue.getCurrentLine().equals(line), "Movement must not advance dialogue");
            check(!player.isMoving(), "Held movement must not replace idle during dialogue");
        }
        float idleEnd = field(player, "stateTime", Float.class);
        check(idleEnd > idleStart && idle.getKeyFrame(idleEnd, true) != firstFrame,
            "Idle must advance to another frame while dialogue blocks movement");

        renderInput(screen, Input.Keys.ESCAPE);
        renderInput(screen, -1, Input.Keys.D);
        check(field(player, "stateTime", Float.class) == idleEnd, "Pause must freeze dialogue idle animation");
        renderInput(screen, Input.Keys.ESCAPE);
        renderInput(screen, -1); // Consume the resume frame's input guard.
        renderInput(screen, -1);
        check(field(player, "stateTime", Float.class) > idleEnd && dialogue.isActive(),
            "Resuming must restart idle and preserve dialogue");

        advanceToTrangChoices(screen, dialogue, ui, player);
        capture("dialogue-choices.png");
        check(ASK_ABANDONED.equals(ui.selectedChoiceId()), "Initial Trang choice must be the first topic");
        renderLockedInput(screen, player, Input.Keys.S);
        check(ASK_STRANGE.equals(ui.selectedChoiceId()), "S must highlight the second topic");
        renderLockedInput(screen, player, Input.Keys.W);
        check(ASK_ABANDONED.equals(ui.selectedChoiceId()), "W must highlight the previous topic");
        renderLockedInput(screen, player, Input.Keys.W);
        check(FINISH_CHAT.equals(ui.selectedChoiceId()), "W from the first topic must wrap to exit");
        renderLockedInput(screen, player, Input.Keys.S);
        check(ASK_ABANDONED.equals(ui.selectedChoiceId()), "S from exit must wrap to the first topic");
        renderLockedInput(screen, player, Input.Keys.ENTER);
        check(dialogue.isActive() && !ui.isChoosing() && ASK_ABANDONED.equals(dialogue.getLastChoiceId()),
            "Enter must open the highlighted first topic");
        advanceToTrangChoices(screen, dialogue, ui, player);
        check(ASK_ABANDONED.equals(ui.selectedChoiceId()), "Returning from a topic must reset the menu highlight");
        renderLockedInput(screen, player, Input.Keys.S);
        renderLockedInput(screen, player, Input.Keys.F);
        check(dialogue.isActive() && !ui.isChoosing() && ASK_STRANGE.equals(dialogue.getLastChoiceId()),
            "F must open the highlighted second topic");
        advanceToTrangChoices(screen, dialogue, ui, player);
        closeTrangDialogue(screen, dialogue, ui, player, Input.Keys.ENTER);
        check(FINISH_CHAT.equals(dialogue.getLastChoiceId()), "Exit must finish with the authored choice ID");

        // Closing dialogue must not move the player or re-trigger an interaction in that frame.
        for (int key : new int[] {Input.Keys.F, Input.Keys.ENTER}) {
            if (!dialogue.isActive()) {
                renderInput(screen, Input.Keys.F);
                check(dialogue.isActive(), "NPC dialogue must be reopenable");
            }
            closeTrangDialogue(screen, dialogue, ui, player, key);
            check(player.getX() == x && player.getY() == y, "Player moved on a dialogue advance/close frame");
            check(!dialogue.isActive(), "F and Enter must close dialogue normally");
            check(scene(screen).equals("ROOM"), "Closing dialogue must not leave the room");
        }
        renderInput(screen, -1, Input.Keys.D);
        check(player.getX() > x, "Movement must resume after dialogue closes");

        for (int cycle = 0; cycle < 5; cycle++) {
            player.setPosition(openingX, openingY);
            renderInput(screen, Input.Keys.F, Input.Keys.D);
            check(dialogue.isActive() && !player.isMoving(), "Repeated opening with held movement must stay idle");
            check(player.getX() == openingX, "Repeated opening must not leak movement");
            closeTrangDialogue(screen, dialogue, ui, player, Input.Keys.F);
            check(player.getX() == openingX && !player.isMoving(), "Held key must not move on dialogue advance/close");
            check(!dialogue.isActive(), "Repeated dialogue must close");
            renderInput(screen, -1, Input.Keys.D);
            check(player.getX() > openingX && player.isMoving(), "Held movement must resume on the following frame");
        }

        checkDialoguePresentation(screen, dialogue, ui);
        checkWardrobeControls(screen, player);

        player.setPosition(WorldMap.EXIT_CENTER_X - Player.WIDTH / 2f, 24f);
        renderInput(screen, Input.Keys.F);
        check(scene(screen).equals("HALLWAY"), "Door must remain usable after NPC dialogue");
    }

    private static void advanceToTrangChoices(DemoGameScreen screen, DialogueController controller,
                                              DialogueUI ui, Player player) {
        for (int remaining = 100; controller.isActive() && !ui.isChoosing() && remaining > 0; remaining--) {
            renderLockedInput(screen, player, Input.Keys.F);
        }
        check(controller.isActive() && ui.isChoosing() && controller.getLine().getId().equals("school"),
            "Trang topic dialogue must return to the visible menu");
    }

    private static void closeTrangDialogue(DemoGameScreen screen, DialogueController controller,
                                          DialogueUI ui, Player player, int key) {
        for (int remaining = 100; controller.isActive() && remaining > 0; remaining--) {
            if (ui.isChoosing()) {
                for (int selection = 0; !FINISH_CHAT.equals(ui.selectedChoiceId()) && selection < 3; selection++) {
                    renderLockedInput(screen, player, Input.Keys.W);
                }
                check(FINISH_CHAT.equals(ui.selectedChoiceId()), "Closing must explicitly highlight Trang's exit");
            }
            renderLockedInput(screen, player, key);
        }
        check(!controller.isActive(), "Trang dialogue must close after selecting exit");
    }

    private static void renderLockedInput(DemoGameScreen screen, Player player, int key) {
        float x = player.getX(), y = player.getY();
        renderInput(screen, key, Input.Keys.D);
        check(player.getX() == x && player.getY() == y && !player.isMoving(),
            "Dialogue navigation, choice, advance and close must block held movement");
    }

    private static void checkPlayerMovement(WorldMap world) throws ReflectiveOperationException {
        Player player = new Player(200f, 100f);
        try {
            float delta = 1f / 30f;
            float clock = field(player, "stateTime", Float.class);
            player.update(delta, 0f, 0f, false, List.of(), null, List.of(), WorldMap.WIDTH, WorldMap.HEIGHT);
            check(!player.isMoving() && field(player, "stateTime", Float.class) > clock, "Stationary player must animate idle");
            player.update(delta, 1f, 0f, false, List.of(), null, List.of(), WorldMap.WIDTH, WorldMap.HEIGHT);
            check(player.isMoving() && Math.abs(player.getX() - 200f - 280f * delta) < 0.001f,
                "Successful movement must retain speed and select running");
            clock = field(player, "stateTime", Float.class);
            player.update(0f, 1f, 0f, true, List.of(), null, List.of(), WorldMap.WIDTH, WorldMap.HEIGHT);
            check(!player.isMoving() && field(player, "stateTime", Float.class) == clock,
                "Intent without elapsed time must not count as actual motion");

            player.setPosition(200f, 100f);
            Wall right = new Wall(200f + Player.WIDTH, 50f, 10f, 200f);
            player.update(delta, 1f, 0f, true, List.of(right), null, List.of(), WorldMap.WIDTH, WorldMap.HEIGHT);
            check(player.getX() == 200f && !player.isMoving(), "Wall-blocked sprint intent must result in idle");
            clock = field(player, "stateTime", Float.class);
            player.update(delta, 1f, 1f, false, List.of(right), null, List.of(), WorldMap.WIDTH, WorldMap.HEIGHT);
            check(player.getX() == 200f && player.getY() > 100f && player.isMoving(), "Diagonal collision must retain wall sliding");
            check(field(player, "currentRunAnimation", Animation.class) == field(player, "backRunAnimation", Animation.class),
                "Animation direction must follow actual slide, not blocked horizontal intent");
            float expectedClock = clock + (player.getY() - 100f) / 220f;
            check(Math.abs(field(player, "stateTime", Float.class) - expectedClock) < 0.0001f,
                "Running cadence must follow actual displacement");

            player.setPosition(200f, 100f);
            Wall above = new Wall(180f, 100f + Player.HEIGHT, Player.WIDTH + 40f, 10f);
            player.update(delta, 1f, 1f, false, List.of(right, above), null, List.of(), WorldMap.WIDTH, WorldMap.HEIGHT);
            check(!player.isMoving() && player.getX() == 200f && player.getY() == 100f, "A blocked corner must be idle");
            player.setPosition(24f, 100f);
            player.update(delta, -1f, 0f, false, List.of(), null, List.of(), WorldMap.WIDTH, WorldMap.HEIGHT);
            check(player.getX() == 24f && !player.isMoving(), "World clamp must also count as no movement");

            Rectangle wardrobe = world.getWardrobe().getCollisionBounds();
            player.setPosition(wardrobe.x - Player.WIDTH, wardrobe.y + 10f);
            player.update(delta, 1f, 0f, false, world.getWalls(), world.getFurniture(), List.of(), WorldMap.WIDTH, WorldMap.HEIGHT);
            check(!player.isMoving(), "Furniture-blocked input must be idle");
            Rectangle npc = world.getNpcs().get(0).getBounds();
            player.setPosition(npc.x - Player.WIDTH, npc.y);
            player.update(delta, 1f, 0f, false, world.getWalls(), null, world.getNpcs(), WorldMap.WIDTH, WorldMap.HEIGHT);
            check(!player.isMoving(), "NPC collision must also determine actual movement");
        } finally {
            player.dispose();
        }
    }

    private static void checkDialoguePresentation(DemoGameScreen screen, DialogueController controller,
                                                  DialogueUI ui) throws ReflectiveOperationException {
        Dialogue sample = DialogueLoader.load("dialogue/npc_trang_intro.json");
        controller.start(sample);
        screen.render(0);
        Image portrait = field(ui, "portrait", Image.class);
        Image background = field(ui, "background", Image.class);
        Label text = field(ui, "text", Label.class);
        check(portrait.isVisible() && portrait.getDrawable() != null, "A portrait must be visible");
        portrait.validate();
        check(Math.abs(portrait.getImageWidth() / portrait.getImageHeight() - 0.6f) < 0.001f,
            "Portrait must retain its PNG aspect ratio");
        check(background.isVisible(), "Authored background must be visible");
        check(field(ui, "name", Label.class).getText().toString().equals("Trang"), "Speaker name must come from data");
        screen.render(0.20f);
        check(!ui.isTextComplete(), "Typewriter must reveal gradually");
        float revealed = field(ui, "revealedCharacters", Float.class);
        screen.pause();
        screen.render(0.4f);
        check(field(ui, "revealedCharacters", Float.class) == revealed, "Pause must freeze typewriter");
        renderInput(screen, Input.Keys.ESCAPE);
        renderInput(screen, -1);
        DialogueHud hud = ui.getHud();
        click(ui.getStage(), field(hud, "next", TextButton.class));
        renderInput(screen, -1, Input.Keys.D);
        check(ui.isTextComplete() && controller.getLine().getId().equals("greeting"), "Next click must reveal current line");
        capture("dialogue-16x9.png");
        Gdx.graphics.setWindowedMode(1280, 720);
        screen.resize(1280, 720);
        ui.draw();
        check(ui.getStage().getViewport().getWorldWidth() == 960, "Resize must preserve fixed UI canvas");
        check(portrait.getWidth() == 510 && portrait.getHeight() == 518, "Resize must preserve portrait layout");
        Gdx.graphics.setWindowedMode(800, 600);
        screen.resize(800, 600);
        screen.render(0);
        capture("dialogue-4x3.png");
        check(text.getX() + text.getWidth() <= 960, "Text must remain within the UI canvas");
        Gdx.graphics.setWindowedMode(960, 540);
        screen.resize(960, 540);
        click(ui.getStage(), field(hud, "next", TextButton.class));
        renderInput(screen, -1);
        check(controller.getLine().getId().equals("school") && !ui.isTextComplete(), "Next after reveal must advance and restart typewriter");

        Dialogue fallback = new Dialogue("fallback", List.of(
            new DialogueLine("Linh", "Chữ tiếng Việt: Đường, trường, ắ, ặ, ễ, ự. [nội dung]", "portraits/missing.png"),
            new DialogueLine("", "Thông báo không có chân dung.", null)));
        controller.start(fallback);
        screen.render(0);
        check(!background.isVisible(), "Absent background must expose existing world");
        check(portrait.isVisible() && field(ui, "placeholder", Label.class).isVisible(), "Missing portrait must use clear fallback");
        BitmapFont font = text.getStyle().font;
        for (char letter : "Đđăâêôơưắặễự".toCharArray()) check(font.getData().hasGlyph(letter), "Missing Vietnamese glyph: " + letter);
        renderInput(screen, Input.Keys.F);
        renderInput(screen, Input.Keys.F);
        check(!portrait.isVisible(), "Narration must clear the previous portrait");
        controller.start(new Dialogue("long", List.of(new DialogueLine("Trang",
            "Một câu dài cần tự xuống dòng, giữ dấu tiếng Việt và không tràn khỏi màn hình. ".repeat(30), null))));
        screen.render(0);
        check(field(ui, "pages", List.class).size() > 1, "Long dialogue must paginate");
        for (int attempts = 0; controller.isActive() && attempts < 200; attempts++) renderInput(screen, Input.Keys.F);
        check(!controller.isActive(), "Long dialogue must finish through every page");

        Dialogue branch = new Json().fromJson(Dialogue.class, """
            {"id":"choice-test", "lines":[
              {"id":"ask", "speaker":"Trang", "text":"Một lựa chọn thử nghiệm.",
               "choices":[{"id":"yes", "text":"Tiếp tục", "next":"reply"}]},
              {"id":"reply", "speaker":"Linh", "text":"Đã chọn đúng câu trả lời.", "next":"END"}]}
            """);
        controller.start(branch);
        screen.render(0);
        renderInput(screen, Input.Keys.F);
        Table choiceTable = field(ui, "choices", Table.class);
        check(choiceTable.isVisible(), "Real choices must appear only after text finishes");
        check(field(hud, "next", TextButton.class).isDisabled(), "Next must not skip real choices");
        click(ui.getStage(), choiceTable.getChildren().first());
        renderInput(screen, -1, Input.Keys.D);
        check(controller.getLine().getId().equals("reply"), "Choice click must be routed through controller");
        check(controller.getLastChoiceId().equals("yes"), "Choice ID must be retained for caller");
        controller.close();
        screen.render(0);
        check(Gdx.input.getInputProcessor() == null, "Close must restore gameplay input");
        check(!field(ui, "root", Table.class).isVisible(), "Close must hide the complete overlay");
        check(field(ui, "conversation", Dialogue.class) == null, "Close must release conversation resource ownership");
        Object resources = field(ui, "assets", Object.class);
        var cache = field(resources, "scenes", com.room304.assets.GameAssets.class);
        int cached = cache.loadedCount();
        check(cached > 0, "Closing dialogue must retain shared texture cache");
        check(field(ui, "portrait", Image.class).getDrawable() == null
            && field(ui, "background", Image.class).getDrawable() == null,
            "Closing dialogue must detach actor references to cached textures");
        Dialogue keyboardBranch = new Json().fromJson(Dialogue.class, """
            {"id":"keyboard-choice", "lines":[{"speaker":"Trang", "text":"Chọn rõ ràng.",
             "choices":[{"id":"first","text":"Một","next":"END"},
                        {"id":"second","text":"Hai","next":"END"}]}]}
            """);
        controller.start(keyboardBranch); screen.render(0); renderInput(screen, Input.Keys.F);
        check(controller.isActive() && ui.selectedChoiceId().equals("first"), "First choice is highlighted but not selected");
        renderInput(screen, Input.Keys.DOWN);
        check(ui.selectedChoiceId().equals("second"), "Arrow moves choice highlight");
        renderInput(screen, -1, Input.Keys.ENTER);
        check(controller.isActive(), "Held Enter must not confirm a choice");
        renderInput(screen, Input.Keys.ENTER);
        check(!controller.isActive() && controller.getLastChoiceId().equals("second"), "Fresh Enter confirms highlighted choice");
        // A different speaker uses the same UI and retained asset cache.
        controller.start(fallback);
        screen.render(0);
        check(field(screen, "dialogueUI", DialogueUI.class) == ui, "Different speakers must reuse one UI instance");
        check(cache.loadedCount() == cached, "Reopening the same presentation must not reload textures");
        screen.hide();
        check(Gdx.input.getInputProcessor() == null, "Hide must release dialogue input");
        screen.show();
        check(Gdx.input.getInputProcessor() == ui.getStage(), "Show must restore dialogue input");
        controller.close();
        screen.render(0);
    }

    private static void click(Stage stage, Actor actor) {
        Vector2 point = screenCoordinates(stage, actor);
        stage.touchDown(Math.round(point.x), Math.round(point.y), 0, Input.Buttons.LEFT);
        stage.touchUp(Math.round(point.x), Math.round(point.y), 0, Input.Buttons.LEFT);
    }

    private static void capture(String name) {
        Pixmap pixels = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        try { PixmapIO.writePNG(Gdx.files.local("../lwjgl3/build/dialogue-checks/" + name), pixels, -1, true); }
        finally { pixels.dispose(); }
    }

    private static void checkWardrobeControls(DemoGameScreen screen, Player player) throws ReflectiveOperationException {
        WardrobeController controller = field(screen, "wardrobeController", WardrobeController.class);
        WardrobeView view = field(screen, "wardrobeView", WardrobeView.class);
        player.setPosition(560f, 210f);
        renderInput(screen, Input.Keys.F, Input.Keys.D);
        check(controller.isOpen() && !player.isMoving() && player.getX() == 560f, "Opening wardrobe must lock movement immediately");
        check(Gdx.input.getInputProcessor() == view.getStage(), "Wardrobe must own input while open");
        Object batch = field(screen, "spriteBatch", Object.class);
        screen.hide();
        check(Gdx.input.getInputProcessor() == null, "Hidden screen must release its stage");
        screen.show();
        check(field(screen, "spriteBatch", Object.class) == batch, "Show must reuse resources");
        check(Gdx.input.getInputProcessor() == view.getStage(), "Show must restore wardrobe controls");

        Object[] slots = field(view, "wardrobeCells", Object[].class);
        Actor slot = field(slots[0], "container", Actor.class);
        Object listener = slot.getListeners().first();
        WardrobeItem item = controller.getWardrobeItem(0);
        Vector2 click = screenCoordinates(view.getStage(), slot);
        view.getStage().touchDown(Math.round(click.x), Math.round(click.y), 0, Input.Buttons.LEFT);
        for (int frame = 0; frame < 3; frame++) {
            renderInput(screen, -1, Input.Keys.D);
            check(!player.isMoving() && player.getX() == 560f, "Wardrobe must stay idle while movement is held");
            check(slot.getListeners().first() == listener, "Slot listener must survive across gesture frames");
        }
        view.getStage().touchUp(Math.round(click.x), Math.round(click.y), 0, Input.Buttons.LEFT);
        renderInput(screen, -1);
        check(controller.getWardrobeItem(0) == null && controller.getPlayerItem(2) == item,
            "Mouse release after several frames must transfer exactly one item");

        renderInput(screen, Input.Keys.ESCAPE);
        renderInput(screen, Input.Keys.F, Input.Keys.D);
        check(controller.isOpen() && player.getX() == 560f, "Pause must not route F to wardrobe or movement");
        renderInput(screen, Input.Keys.ESCAPE);
        renderInput(screen, -1); // Consume the existing resume guard.

        Actor close = field(view, "closeButton", Actor.class);
        click = screenCoordinates(view.getStage(), close);
        view.getStage().touchDown(Math.round(click.x), Math.round(click.y), 0, Input.Buttons.LEFT);
        view.getStage().touchUp(Math.round(click.x), Math.round(click.y), 0, Input.Buttons.LEFT);
        view.render();
        view.render();
        check(controller.isOpen(), "Drawing the view must not consume business commands");
        renderInput(screen, -1, Input.Keys.D);
        check(!controller.isOpen() && !player.isMoving() && player.getX() == 560f,
            "X close must be routed during update and consume movement for that frame");
        check(Gdx.input.getInputProcessor() == null, "X close must release stage in the same update");
        for (int key : new int[] {Input.Keys.E, Input.Keys.F}) {
            renderInput(screen, Input.Keys.F, Input.Keys.D);
            check(controller.isOpen(), "Wardrobe must reopen");
            renderInput(screen, key, Input.Keys.D);
            check(!controller.isOpen() && !player.isMoving() && player.getX() == 560f,
                "Keyboard close must have the same movement lock as X");
        }
        renderInput(screen, -1, Input.Keys.D);
        check(player.getX() > 560f && player.isMoving(), "Movement must resume after wardrobe closes");
    }

    private static Vector2 screenCoordinates(Stage stage, Actor actor) {
        Vector2 center = actor.localToStageCoordinates(new Vector2(actor.getWidth() / 2f, actor.getHeight() / 2f));
        return stage.stageToScreenCoordinates(center);
    }

    private static void checkDistanceSelection(WorldMap world) {
        RoomInteractionSystem selector = new RoomInteractionSystem(world);
        float x = WorldMap.EXIT_CENTER_X - Player.WIDTH / 2f;
        selector.update(new Rectangle(x, 24f, Player.WIDTH, Player.HEIGHT));
        check(selector.getActiveTarget().getKind() == Kind.EXIT, "Door at distance 6 must beat NPC at distance 36");
        selector.update(new Rectangle(x, 39f, Player.WIDTH, Player.HEIGHT));
        check(selector.getActiveTarget().getKind() == Kind.EXIT, "Exact tie must keep previous door target");
        selector.update(new Rectangle(x, 54f, Player.WIDTH, Player.HEIGHT));
        check(selector.getActiveTarget().getKind() == Kind.NPC, "NPC at distance 6 must beat door at distance 36");
        selector.update(new Rectangle(x, 39f, Player.WIDTH, Player.HEIGHT));
        check(selector.getActiveTarget().getKind() == Kind.NPC, "Exact tie must also keep previous NPC target");
        selector.update(new Rectangle(x, 38.99f, Player.WIDTH, Player.HEIGHT));
        check(selector.getActiveTarget().getKind() == Kind.EXIT, "Even a slightly nearer door must win");
        selector.update(new Rectangle(24f, 250f, Player.WIDTH, Player.HEIGHT));
        check(selector.getActiveTarget() == null, "Out-of-range targets must not be selected");
    }

    @SuppressWarnings("unchecked")
    private static void checkMultipleNpcsAndWardrobe() throws ReflectiveOperationException {
        WorldMap fixture = new WorldMap(false);
        try {
            // Populate a test map without adding a test-only mutation API to WorldMap.
            List<Npc> npcs = (List<Npc>) field(fixture, "npcs", List.class);
            var dialogue = DialogueLoader.load("dialogue/npc_trang_intro.json");
            Npc alpha = new Npc("Alpha", dialogue, "sprites/npc_trang_idle.png", 360f, 80f, 60f);
            Npc beta = new Npc("Beta", dialogue, "sprites/npc_trang_idle.png", 600f, 80f, 60f);
            npcs.add(alpha);
            npcs.add(beta);
            RoomInteractionSystem selector = new RoomInteractionSystem(fixture);
            selector.update(new Rectangle(492f, 80f, Player.WIDTH, Player.HEIGHT));
            check(selector.getActiveTarget().getNpc() == beta, "Nearer second NPC must beat first matching NPC");
            Rectangle tie = new Rectangle(480f, 80f, Player.WIDTH, Player.HEIGHT);
            selector.update(tie);
            check(selector.getActiveTarget().getNpc() == beta, "Equal NPC distances must keep previous target");
            RoomInteractionSystem fresh = new RoomInteractionSystem(fixture);
            fresh.update(tie);
            check(fresh.getActiveTarget().getNpc() == alpha, "Fresh exact tie must use stable IDs");
            Collections.reverse(npcs);
            RoomInteractionSystem reversed = new RoomInteractionSystem(fixture);
            reversed.update(tie);
            check(reversed.getActiveTarget().getNpc() == alpha, "NPC list order must not affect equal-distance selection");
            reversed.update(new Rectangle(481f, 80f, Player.WIDTH, Player.HEIGHT));
            check(reversed.getActiveTarget().getNpc() == beta, "Slightly nearer NPC must replace old selection");

            npcs.remove(alpha);
            npcs.remove(beta);
            alpha.dispose();
            beta.dispose();
            Npc nearWardrobe = new Npc("Near wardrobe", dialogue, "sprites/npc_trang_idle.png", 582f, 120f, 60f);
            npcs.add(nearWardrobe);
            selector = new RoomInteractionSystem(fixture);
            Rectangle player = new Rectangle(560f, 210f, Player.WIDTH, Player.HEIGHT);
            check(nearWardrobe.canInteract(player) && fixture.getWardrobe().canInteract(player),
                "Wardrobe comparison fixture must be eligible for both targets");
            selector.update(player);
            check(selector.getActiveTarget().getKind() == Kind.WARDROBE,
                "Nearer wardrobe edge must beat NPC, even though wardrobe center is farther away");
        } finally {
            fixture.dispose();
        }
    }

    private static void renderInput(DemoGameScreen screen, int justPressed, int... held) {
        Input previous = Gdx.input;
        Gdx.input = (Input) Proxy.newProxyInstance(Input.class.getClassLoader(), new Class<?>[] {Input.class},
            (proxy, method, args) -> {
                if (method.getName().equals("isKeyJustPressed")) return ((Integer) args[0]) == justPressed;
                if (method.getName().equals("isKeyPressed")) {
                    for (int key : held) if (key == (Integer) args[0]) return true;
                    return false;
                }
                return method.invoke(previous, args);
            });
        try {
            screen.render(1f / 30f);
        } finally {
            Gdx.input = previous;
        }
    }

    private static String scene(DemoGameScreen screen) throws ReflectiveOperationException {
        return field(screen, "scene", Object.class).toString();
    }

    // Read existing state for assertions without adding test-only production getters.
    private static <T> T field(Object owner, String name, Class<T> type) throws ReflectiveOperationException {
        Field field = owner.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return type.cast(field.get(owner));
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
