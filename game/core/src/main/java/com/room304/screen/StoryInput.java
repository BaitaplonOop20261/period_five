package com.room304.screen;

import com.badlogic.gdx.Input;
import java.util.*;

/** Key edges use held state, so mode changes can suppress controls until release. */
public final class StoryInput {
    private static final int[] COMMANDS={Input.Keys.E,Input.Keys.ENTER,Input.Keys.N,Input.Keys.ESCAPE,Input.Keys.UP,Input.Keys.DOWN,Input.Keys.LEFT,Input.Keys.RIGHT,Input.Keys.W,Input.Keys.A,Input.Keys.S,Input.Keys.D};
    private final Set<Integer> held=new HashSet<>(),pressed=new HashSet<>(),suppressed=new HashSet<>();
    private Input input;
    public void capture(Input input) {
        this.input=input;pressed.clear();
        for(int key:COMMANDS) {boolean down=input.isKeyPressed(key);if(down && !held.contains(key) && !suppressed.contains(key)) pressed.add(key);if(down) held.add(key);else {held.remove(key);suppressed.remove(key);}}
    }
    public void modeChanged() { suppressed.addAll(held);pressed.clear(); }
    public boolean pressed(int key) { return pressed.contains(key); }
    public int choiceDirection() { return pressed(Input.Keys.DOWN)?1:pressed(Input.Keys.UP)?-1:0; }
    public float horizontal() { return (down(Input.Keys.D)||down(Input.Keys.RIGHT)?1:0)-(down(Input.Keys.A)||down(Input.Keys.LEFT)?1:0); }
    public float vertical() { return (down(Input.Keys.W)||down(Input.Keys.UP)?1:0)-(down(Input.Keys.S)||down(Input.Keys.DOWN)?1:0); }
    private boolean down(int key) { return input!=null && input.isKeyPressed(key) && !suppressed.contains(key); }
}
