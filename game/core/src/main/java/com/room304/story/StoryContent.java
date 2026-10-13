package com.room304.story;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.room304.dialogue.*;
import java.util.*;

/** Authored text and beat metadata; decisions remain Java predicates in StorySession. */
public final class StoryContent {
    private final Map<String, Dialogue> dialogues = new LinkedHashMap<>();
    private final Map<String,String> textIds=new HashMap<>();
    private final Set<String> lineIds=new HashSet<>();
    private final Map<String, String> next = new LinkedHashMap<>();
    private final Map<String, String> strings = new LinkedHashMap<>();
    public static StoryContent load(FileHandle file) {
        try {
            StoryContent book = new StoryContent(); Json json = new Json();
            JsonValue root = new JsonReader().parse(file);
            if (root.getInt("version") != 1) throw new IllegalArgumentException("Unsupported content version");
            Map<String, DialogueSpeaker> speakers = new HashMap<>();
            for (JsonValue v : root.get("speakers")) speakers.put(v.name, json.readValue(DialogueSpeaker.class, v));
            for (JsonValue v : root.get("strings")) {
                if (book.strings.putIfAbsent(v.name, v.asString()) != null) throw new IllegalArgumentException("Duplicate string " + v.name);
            }
            for (JsonValue v : root.get("dialogues")) {
                Dialogue d = json.readValue(Dialogue.class, v); d.setSpeakers(speakers);
                DialogueLoader.validate(d, file.path());
                if (book.dialogues.putIfAbsent(d.getId(), d) != null) throw new IllegalArgumentException("Duplicate block " + d.getId());
                for(int index=0;index<d.getLineCount();index++)book.lineIds.add(d.getLine(index).getId());
                // Authored blocks must terminate. Choice loops are explicit runtime menus, never hidden cycles.
                for (int i = 0; i < d.getLineCount(); i++) {
                    Set<Integer> visited = new HashSet<>(); int cursor = i;
                    while (cursor < d.getLineCount()) {
                        if (!visited.add(cursor)) throw new IllegalArgumentException("Dialogue cycle " + d.getId());
                        String target = d.getLine(cursor).getNext();
                        if ("END".equals(target)) break;
                        cursor = target == null ? cursor + 1 : d.indexOf(target);
                        if (cursor < 0) throw new IllegalArgumentException("Missing dialogue node " + target);
                    }
                }
            }
            for (JsonValue v : root.get("blocks")) {
                String id = v.getString("id"); book.dialogue(id);
                if (book.next.putIfAbsent(id, v.getString("next")) != null) throw new IllegalArgumentException("Duplicate route " + id);
                for (JsonValue target : v.get("targets")) book.dialogue(target.asString());
            }
            if (book.next.size() != book.dialogues.size()) throw new IllegalArgumentException("Missing block route");
            for(var t:book.strings.entrySet())if(t.getKey().startsWith("DOC-"))book.textIds.put(t.getValue(),t.getKey());
            return book;
        } catch (RuntimeException e) { throw new IllegalArgumentException("Invalid story content " + file.path() + ": " + e.getMessage(), e); }
    }
    public Dialogue dialogue(String id) {
        Dialogue d = dialogues.get(id);
        if (d == null) throw new IllegalArgumentException("Missing story block " + id);
        return d;
    }
    public String text(String id) {
        String t = strings.get(id);
        if (t == null) throw new IllegalArgumentException("Missing story string " + id);
        return t;
    }
    public String next(String id) { return next.get(id); }
    public Set<String> blockIds() { return Collections.unmodifiableSet(dialogues.keySet()); }
    public String documentId(String text){return textIds.get(text);}
    public boolean hasLine(String id){return lineIds.contains(id);}
    public Dialogue read(String id, String... textIds) {
        List<DialogueLine> lines = new ArrayList<>();
        for (String textId : textIds) lines.add(new DialogueLine("", text(textId), null));
        return new Dialogue(id, lines);
    }
    public Dialogue menu(String id, String heading, List<DialogueChoice> choices) {
        if (choices.isEmpty()) throw new IllegalArgumentException("Empty menu " + id);
        return new Dialogue(id, List.of(new DialogueLine(id, "", heading.isBlank() ? text("UI-CHOICE-HINT") : heading, choices)));
    }
}
