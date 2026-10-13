package com.room304.story;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.room304.world.SceneCatalog;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import static com.room304.story.GameState.*;

/** Single-slot stable-boundary saves. Atomic replacement keeps the previous slot on failed writes. */
public final class SaveStore {
    public static final class Data {
        public int version; public String progress, facing; public float x,y;
    }
    public record Loaded(GameState state,float x,float y,String facing) {}
    private final Path file;
    public SaveStore(Path file) { this.file=file.toAbsolutePath().normalize(); }
    public Path path() { return file; }
    public boolean exists() { return Files.isRegularFile(file); }
    public void save(StorySession session,float x,float y,String facing,SceneCatalog world) {
        if(!session.canSave()) throw new IllegalStateException("Chỉ lưu khi khám phá; hoàn tất đoạn đang đọc trước khi lưu.");
        if(!world.canStand(world.scene(session.state().room()),x,y)) throw new IllegalArgumentException("Vị trí lưu không hợp lệ.");
        if(!java.util.Set.of("up","down","left","right").contains(facing)) throw new IllegalArgumentException("Hướng nhân vật không hợp lệ.");
        Data data=new Data();data.version=1;data.progress=ProgressSnapshot.encode(session.state());data.x=x;data.y=y;data.facing=facing;
        // Validate before touching the existing slot.
        ProgressSnapshot.decode(data.progress,"save candidate");
        Json json=new Json();json.setOutputType(JsonWriter.OutputType.json);String text=json.toJson(data);
        Path temporary=null;
        try {
            Files.createDirectories(file.getParent());temporary=Files.createTempFile(file.getParent(),"room304-", ".tmp");
            Files.writeString(temporary,text,StandardCharsets.UTF_8);
            try { Files.move(temporary,file,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE); }
            catch(AtomicMoveNotSupportedException e) { Files.move(temporary,file,StandardCopyOption.REPLACE_EXISTING); }
        } catch(IOException e) { throw new IllegalStateException("Không thể lưu " + file + ": " + e.getMessage(),e); }
        finally { if(temporary!=null) try { Files.deleteIfExists(temporary); } catch(IOException ignored) { } }
    }
    public Loaded load(SceneCatalog world) {Loaded loaded=load();if(!world.canStand(world.scene(loaded.state().room()),loaded.x(),loaded.y()))throw new IllegalArgumentException("Vị trí lưu nằm trong tường hoặc ngoài bản đồ");return loaded;}
    /** Rendering caller validates against imported TMX geometry before installing the new screen. */
    public Loaded load() {
        try {
            if(!exists()) throw new IllegalArgumentException("Chưa có file lưu: " + file);
            if(Files.size(file)>1_000_000) throw new IllegalArgumentException("File lưu quá lớn");
            Data data=new Json().fromJson(Data.class,Files.readString(file,StandardCharsets.UTF_8));
            if(data==null || data.version!=1) throw new IllegalArgumentException("Phiên bản file lưu không được hỗ trợ");
            GameState state=ProgressSnapshot.decode(data.progress,file.toString());
            if(state.ending()!=Ending.NONE || state.requestedEnding()!=Ending.NONE || !state.reached(Event.INITIAL_SCENE_DONE)) throw new IllegalArgumentException("File lưu không nằm ở mốc khám phá");
            if(!Float.isFinite(data.x)||!Float.isFinite(data.y)||data.x<8||data.y<8) throw new IllegalArgumentException("Vị trí lưu không hợp lệ");
            if(!java.util.Set.of("up","down","left","right").contains(data.facing)) throw new IllegalArgumentException("Hướng nhân vật không hợp lệ");
            return new Loaded(state,data.x,data.y,data.facing);
        } catch(IOException|RuntimeException e) { throw new IllegalArgumentException("Không thể tải " + file + ": " + e.getMessage(),e); }
    }
}
