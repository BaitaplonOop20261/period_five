package com.room304.story;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.room304.world.SceneCatalog;
import java.nio.charset.StandardCharsets;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import static com.room304.story.GameState.*;

/** One manual slot and its last valid predecessor. Runtime objects are never serialized. */
public final class SaveStore {
    public static final int FORMAT_VERSION=2, CONTENT_VERSION=1;
    public static final class Data {
        public int version,contentVersion;
        public String progress,facing,playthroughId,objectiveId;
        public long savedAt;
        public float x,y;
    }
    public record Loaded(GameState state,float x,float y,String facing,String playthroughId,long savedAt,String objectiveId) {
        public Loaded(GameState state,float x,float y,String facing){this(state,x,y,facing,UUID.randomUUID().toString(),0,null);}
    }
    public record Slot(Loaded loaded,String error){public boolean valid(){return loaded!=null;}}
    private final Path file,backup,migrationMarker;
    public SaveStore(Path file){
        this.file=file.toAbsolutePath().normalize();
        String name=this.file.getFileName().toString();
        backup=this.file.resolveSibling(name.endsWith(".json")?name.substring(0,name.length()-5)+".backup.json":name+".backup");
        migrationMarker=this.file.resolveSibling(name+".migration-done");
    }
    public static Path defaultPath(){return userPath(System.getProperty("os.name"),System.getenv(),System.getProperty("user.home"));}
    static Path userPath(String os,Map<String,String> environment,String home){
        String local=environment.get("LOCALAPPDATA");
        if(os.toLowerCase(Locale.ROOT).contains("win")&&local!=null&&!local.isBlank())return Path.of(local,"Room304","saves","room304.json");
        return Path.of(home,".room304","saves","room304.json");
    }
    public Path path(){return file;}
    public Path backupPath(){return backup;}
    public boolean exists(){return Files.isRegularFile(file);}
    public boolean hasBackup(){return Files.isRegularFile(backup);}
    /** Only called after a user confirms deleting this specific slot. */
    public void delete(boolean useBackup){
        // A deleted migrated slot must not be silently imported again on the next launch.
        if(!Files.exists(migrationMarker))write(migrationMarker,"1\n");
        Path target=useBackup?backup:file;
        try{Files.deleteIfExists(target);}
        catch(IOException e){throw new IllegalStateException("Không thể xóa bản lưu đã chọn.",e);}
    }
    public Slot inspect(boolean useBackup){
        try{return new Slot(read(useBackup?backup:file),null);}
        catch(RuntimeException e){return new Slot(null,e.getMessage());}
    }
    public boolean overwritesAnotherRun(StorySession session){
        Slot slot=inspect(false);if(!slot.valid())slot=inspect(true);
        return slot.valid()&&!slot.loaded().playthroughId().equals(session.playthroughId());
    }
    public void save(StorySession session,float x,float y,String facing,SceneCatalog world){
        if(!session.canSave())throw new IllegalStateException(session.saveUnavailableReason());
        validatePosition(session.state(),x,y,world);
        Data data=new Data();data.version=FORMAT_VERSION;data.contentVersion=CONTENT_VERSION;
        data.progress=ProgressSnapshot.encode(session.state());data.x=x;data.y=y;data.facing=facing;
        data.playthroughId=session.playthroughId();data.savedAt=System.currentTimeMillis();data.objectiveId=session.objectiveId();
        String text=json().toJson(data);decode(text,"save candidate");
        // Never replace a good backup with an unreadable primary.
        Slot previous=inspect(false);
        if(previous.valid())write(backup,encode(previous.loaded()));
        write(file,text);
    }
    private static Json json(){Json json=new Json();json.setUsePrototypes(false);json.setOutputType(JsonWriter.OutputType.json);return json;}
    private static String encode(Loaded saved){
        Data d=new Data();d.version=FORMAT_VERSION;d.contentVersion=CONTENT_VERSION;d.progress=ProgressSnapshot.encode(saved.state());
        d.x=saved.x();d.y=saved.y();d.facing=saved.facing();d.playthroughId=saved.playthroughId();d.savedAt=saved.savedAt();d.objectiveId=saved.objectiveId();
        return json().toJson(d);
    }
    private static void write(Path destination,String text){
        Path temporary=null;
        try{
            Files.createDirectories(destination.getParent());temporary=Files.createTempFile(destination.getParent(),"room304-",".tmp");
            try(FileChannel channel=FileChannel.open(temporary,StandardOpenOption.WRITE)){
                ByteBuffer bytes=StandardCharsets.UTF_8.encode(text);while(bytes.hasRemaining())channel.write(bytes);channel.force(true);
            }
            try{Files.move(temporary,destination,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
            catch(AtomicMoveNotSupportedException e){Files.move(temporary,destination,StandardCopyOption.REPLACE_EXISTING);}
        }catch(IOException e){throw new IllegalStateException("Không thể lưu "+destination+": "+e.getMessage(),e);}
        finally{if(temporary!=null)try{Files.deleteIfExists(temporary);}catch(IOException ignored){}}
    }
    /** Import only into an empty destination; the old slot is never deleted or rewritten. */
    public boolean importLegacy(Path legacy,SceneCatalog world){
        if(Files.exists(migrationMarker)||Files.exists(file)||Files.exists(backup)||!Files.isRegularFile(legacy))return false;
        Loaded saved=read(legacy);validatePosition(saved.state(),saved.x(),saved.y(),world);write(file,encode(saved));write(migrationMarker,"1\n");return true;
    }
    public Loaded load(){return read(file);}
    public Loaded loadBackup(){return read(backup);}
    public Loaded load(SceneCatalog world){Loaded saved=load();validatePosition(saved.state(),saved.x(),saved.y(),world);return saved;}
    private static Loaded read(Path source){
        try{
            if(!Files.isRegularFile(source))throw new IllegalArgumentException("Chưa có file lưu: "+source);
            if(Files.size(source)>1_000_000)throw new IllegalArgumentException("File lưu quá lớn");
            return decode(Files.readString(source,StandardCharsets.UTF_8),source.toString());
        }catch(IOException|RuntimeException e){throw new IllegalArgumentException("Không thể tải "+source+": "+e.getMessage(),e);}
    }
    private static Loaded decode(String text,String source){
        var root=new com.badlogic.gdx.utils.JsonReader().parse(text);
        Data d=new Json().fromJson(Data.class,text);
        if(d==null||(d.version!=1&&d.version!=FORMAT_VERSION))throw new IllegalArgumentException("Phiên bản file lưu không được hỗ trợ");
        if(d.version==FORMAT_VERSION&&d.contentVersion!=CONTENT_VERSION)throw new IllegalArgumentException("Bản lưu thuộc phiên bản cốt truyện khác");
        if(d.version==FORMAT_VERSION){
            for(String field:List.of("version","contentVersion","progress","facing","playthroughId","savedAt","objectiveId","x","y"))
                if(!root.has(field))throw new IllegalArgumentException("Bản lưu thiếu trường "+field);
            var progress=new com.badlogic.gdx.utils.JsonReader().parse(d.progress);
            for(String field:List.of("cabinetLocked","bookCoverClosed"))if(!progress.has(field))throw new IllegalArgumentException("Tiến độ thiếu trường "+field);
        }
        GameState state=ProgressSnapshot.decode(d.progress,source);ProgressSnapshot.validateForResume(state);
        if(!Float.isFinite(d.x)||!Float.isFinite(d.y)||d.x<8||d.y<8)throw new IllegalArgumentException("Vị trí lưu không hợp lệ");
        if(d.facing==null||!Set.of("up","down","left","right").contains(d.facing))throw new IllegalArgumentException("Hướng nhân vật không hợp lệ");
        if(d.version==1)d.playthroughId=UUID.nameUUIDFromBytes(d.progress.getBytes(StandardCharsets.UTF_8)).toString();
        else{UUID.fromString(d.playthroughId);if(d.savedAt<0)throw new IllegalArgumentException("Thời điểm lưu không hợp lệ");}
        if(d.objectiveId!=null&&!d.objectiveId.matches("OBJ-(0[1-9]|1[0-8])"))throw new IllegalArgumentException("Mục tiêu lưu không hợp lệ");
        return new Loaded(state,d.x,d.y,d.facing,d.playthroughId,d.savedAt,d.objectiveId);
    }
    private static void validatePosition(GameState state,float x,float y,SceneCatalog world){
        if(!world.canStand(world.scene(state.room()),x,y))throw new IllegalArgumentException("Vị trí lưu nằm trong tường hoặc ngoài bản đồ");
        Rectangle feet=new Rectangle(x,y,world.player().footWidth,world.player().footHeight);
        for(String id:world.visibleNpcs(state))if(!id.equals("nga")){
            var p=world.npcPosition(state,id);var npc=world.npc(id);
            if(feet.overlaps(new Rectangle(p.x,p.y,npc.footWidth,npc.footHeight)))throw new IllegalArgumentException("Vị trí lưu bị NPC chặn");
        }
    }
}
