package com.room304.world;

import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Selects one nearest eligible target for both the interaction marker and input. */
public final class RoomInteractionSystem {
    public enum Kind { NPC, WARDROBE, EXIT, PROP, DOOR }

    public static final class Target {
        private final String id;
        private final Kind kind;
        private final Rectangle bounds;
        private final Predicate<Rectangle> inRange;
        private final Npc npc;

        private Target(String id, Kind kind, Rectangle bounds, Predicate<Rectangle> inRange, Npc npc) {
            this.id = id;
            this.kind = kind;
            this.bounds = bounds;
            this.inRange = inRange;
            this.npc = npc;
        }

        public static Target logical(String id, Kind kind, Rectangle bounds, Predicate<Rectangle> inRange) {
            return new Target(id,kind,bounds,inRange,null);
        }

        public String getId() { return id; }

        public Kind getKind() {
            return kind;
        }

        public Rectangle getBounds() {
            return bounds;
        }

        public Npc getNpc() {
            return npc;
        }
    }

    private final List<Target> targets = new ArrayList<>();
    private Target activeTarget;

    public RoomInteractionSystem(WorldMap map) {
        for (Npc npc : map.getNpcs()) {
            Rectangle bounds = npc.getBounds();
            String id = npc.getId();
            if (targets.stream().anyMatch(target -> target.id.equals(id))
                || id.equals("bedroom_wardrobe") || id.equals("bedroom_exit"))
                throw new IllegalArgumentException("Duplicate/reserved interaction ID " + id);
            targets.add(new Target(id, Kind.NPC, bounds, npc::canInteract, npc));
        }
        Wardrobe wardrobe = map.getWardrobe();
        if (wardrobe != null) {
            Rectangle bounds = wardrobe.getCollisionBounds() != null
                ? wardrobe.getCollisionBounds() : wardrobe.getDrawBounds();
            targets.add(new Target("bedroom_wardrobe", Kind.WARDROBE, bounds,
                wardrobe::canInteract, null));
        }
        targets.add(new Target("bedroom_exit", Kind.EXIT, map.getExitThresholdBounds(),
            playerBounds -> map.getExitBounds().overlaps(playerBounds), null));
    }

    public RoomInteractionSystem(List<Target> targets) { replaceTargets(targets); }
    public void replaceTargets(List<Target> replacement) {
        String previous = activeTarget == null ? null : activeTarget.id;
        java.util.Set<String> ids = new java.util.HashSet<>();
        for (Target target : replacement) if (!ids.add(target.id)) throw new IllegalArgumentException("Duplicate interaction " + target.id);
        targets.clear(); targets.addAll(replacement);
        activeTarget = targets.stream().filter(t -> t.id.equals(previous)).findFirst().orElse(null);
    }

    public void update(Rectangle playerBounds) {
        Target nearest = null;
        float nearestDistance = Float.POSITIVE_INFINITY;
        for (Target target : targets) {
            if (!target.inRange.test(playerBounds)) continue;
            float distance = distanceSquared(playerBounds, target.bounds);
            if (nearest == null || distance < nearestDistance
                || (distance == nearestDistance && winsTie(target, nearest))) {
                nearest = target;
                nearestDistance = distance;
            }
        }
        activeTarget = nearest;
    }

    private boolean winsTie(Target candidate, Target nearest) {
        // Retain the previous target only on an exact tie; even slightly closer targets win.
        if (candidate == activeTarget) return true;
        if (nearest == activeTarget) return false;
        int candidateRank = rank(candidate.kind), nearestRank = rank(nearest.kind);
        return candidateRank != nearestRank ? candidateRank < nearestRank : candidate.id.compareTo(nearest.id) < 0;
    }

    private static int rank(Kind kind) { return kind == Kind.NPC ? 0 : kind == Kind.DOOR || kind == Kind.EXIT ? 2 : 1; }

    public static float distanceSquared(Rectangle player, Rectangle target) {
        float dx = Math.max(0f, Math.max(target.x - player.x - player.width,
            player.x - target.x - target.width));
        float dy = Math.max(0f, Math.max(target.y - player.y - player.height,
            player.y - target.y - target.height));
        return dx * dx + dy * dy;
    }

    public Target getActiveTarget() {
        return activeTarget;
    }
}
