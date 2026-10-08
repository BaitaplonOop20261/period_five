package vn.room304.game.lwjgl3;

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
import vn.room304.game.GameScreen;
import vn.room304.game.Npc;
import vn.room304.game.Player;
import vn.room304.game.Room304Game;
import vn.room304.game.WorldMap;
import vn.room304.game.Wall;
import vn.room304.game.dialogue.DialogueController;
import vn.room304.game.dialogue.DialogueLoader;
import vn.room304.game.gameplay.RoomInteractionSystem;
import vn.room304.game.gameplay.RoomInteractionSystem.Kind;
import vn.room304.game.ui.WardrobeView;
import vn.room304.game.wardrobe.WardrobeController;
import vn.room304.game.wardrobe.WardrobeItem;

/** Reproduces user-facing interaction bugs against real assets and the desktop renderer. */
public final class GameplayRegressionChecks {
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
                setScreen(new GameScreen(() -> {}));
                Gdx.app.postRunnable(() -> {
                    try {
                        checkGameplay((GameScreen) getScreen());
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
            + "wardrobe gestures and command routing, screen hide/show, movement restored.");
    }

    private static void checkGameplay(GameScreen screen) throws ReflectiveOperationException {
        screen.resize(960, 540);
        screen.render(1.01f); // Finish the black transition without updating gameplay.
        screen.render(0f);
        Player player = field(screen, "player", Player.class);
        DialogueController dialogue = field(screen, "dialogueController", DialogueController.class);
        WorldMap world = field(screen, "worldMap", WorldMap.class);
        checkPlayerMovement(world);
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

        // Closing dialogue must not move the player or re-trigger an interaction in that frame.
        for (int key : new int[] {Input.Keys.F, Input.Keys.ENTER}) {
            if (!dialogue.isActive()) {
                renderInput(screen, Input.Keys.F);
                check(dialogue.isActive(), "NPC dialogue must be reopenable");
            }
            for (int remaining = 100; dialogue.isActive() && remaining > 0; remaining--) {
                renderInput(screen, key, Input.Keys.D);
                check(player.getX() == x && player.getY() == y, "Player moved on a dialogue advance/close frame");
            }
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
            for (int remaining = 100; dialogue.isActive() && remaining > 0; remaining--) {
                renderInput(screen, Input.Keys.F, Input.Keys.D);
                check(player.getX() == openingX && !player.isMoving(), "Held key must not move on dialogue advance/close");
            }
            check(!dialogue.isActive(), "Repeated dialogue must close");
            renderInput(screen, -1, Input.Keys.D);
            check(player.getX() > openingX && player.isMoving(), "Held movement must resume on the following frame");
        }

        checkWardrobeControls(screen, player);

        player.setPosition(WorldMap.EXIT_CENTER_X - Player.WIDTH / 2f, 24f);
        renderInput(screen, Input.Keys.F);
        check(scene(screen).equals("HALLWAY"), "Door must remain usable after NPC dialogue");
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

    private static void checkWardrobeControls(GameScreen screen, Player player) throws ReflectiveOperationException {
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
            Npc alpha = new Npc("Alpha", dialogue, "npc_trang_idle.png", 360f, 80f, 60f);
            Npc beta = new Npc("Beta", dialogue, "npc_trang_idle.png", 600f, 80f, 60f);
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
            Npc nearWardrobe = new Npc("Near wardrobe", dialogue, "npc_trang_idle.png", 582f, 120f, 60f);
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

    private static void renderInput(GameScreen screen, int justPressed, int... held) {
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

    private static String scene(GameScreen screen) throws ReflectiveOperationException {
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
