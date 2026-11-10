package com.room304.screen;

import com.room304.story.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

/** Shared save preview/recovery UI; reading a backup never overwrites either file. */
final class SaveMenu {
    private SaveMenu(){}
    static String summary(SaveStore.Loaded saved,StoryContent content){
        String room=switch(saved.state().room()){case ROOM_304->"Phòng 304";case HALLWAY->"Hành lang";case CLASSROOM->"Lớp học";case GUARD_ROOM->"Phòng bảo vệ";};
        String time=saved.savedAt()==0?"Bản lưu cũ":DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(saved.savedAt()));
        return room+" · "+time+(saved.objectiveId()==null?"":"\n"+content.text(saved.objectiveId()));
    }
    static void show(UiPanel panel,SaveStore saves,StoryContent content,Consumer<SaveStore.Loaded> load,Runnable back){
        SaveStore.Slot primary=saves.inspect(false),backup=saves.inspect(true);
        List<UiPanel.Option> options=new ArrayList<>();
        StringBuilder body=new StringBuilder();
        if(primary.valid()){
            body.append("Bản chính: ").append(summary(primary.loaded(),content));
            options.add(new UiPanel.Option("Tải bản chính",()->confirm(panel,saves,content,load,back,false)));
        }else body.append(saves.exists()?"Bản chính không đọc được hoặc không tương thích.":"Chưa có bản lưu chính.");
        if(backup.valid()){
            body.append("\n\nBản sao: ").append(summary(backup.loaded(),content));
            options.add(new UiPanel.Option("Khôi phục bản sao",()->confirm(panel,saves,content,load,back,true)));
        }else if(saves.hasBackup())body.append("\n\nBản sao cũng không đọc được hoặc không tương thích.");
        options.add(new UiPanel.Option("Quay lại",back));
        panel.show("Bản lưu",body.toString(),options);
    }
    private static void confirm(UiPanel panel,SaveStore saves,StoryContent content,Consumer<SaveStore.Loaded> load,Runnable back,boolean backup){
        panel.show(backup?"Khôi phục bản sao?":"Tải bản lưu?","Tiến độ chưa lưu sẽ bị thay thế."+(backup?" Bản sao được mở để chơi tiếp; lưu lại khi muốn thay bản chính.":""),
            List.of(new UiPanel.Option("Ở lại",()->show(panel,saves,content,load,back)),
                new UiPanel.Option("Tải",()->{
                    try{load.accept(backup?saves.loadBackup():saves.load());}
                    catch(RuntimeException e){panel.show("Không thể tải","Bản lưu không đọc được hoặc không tương thích. Hãy quay lại để chọn bản khác.",List.of(new UiPanel.Option("Quay lại",back)));}
                })));
    }
}
