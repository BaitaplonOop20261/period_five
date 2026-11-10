package com.room304.screen;

import com.room304.story.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

/** Lobby-only save management. Deleting and loading always require explicit confirmation. */
final class SaveMenu {
    private SaveMenu(){}
    static String summary(SaveStore.Loaded saved,StoryContent content){
        String room=switch(saved.state().room()){case ROOM_304->"Phòng 304";case HALLWAY->"Hành lang";case CLASSROOM->"Lớp học";case GUARD_ROOM->"Phòng bảo vệ";};
        String time=saved.savedAt()==0?"Bản lưu cũ":DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(saved.savedAt()));
        return room+" · "+time+(saved.objectiveId()==null?"":"\n"+content.text(saved.objectiveId()));
    }
    static void manage(UiPanel panel,SaveStore saves,StoryContent content,Runnable back,Consumer<Runnable> navigation){
        navigation.accept(back);
        List<UiPanel.Option> options=new ArrayList<>();StringBuilder body=new StringBuilder();
        if(saves.exists()){
            appendSlot(body,"Bản chính",saves.inspect(false),content);
            options.add(new UiPanel.Option("Xóa bản chính",()->confirmDelete(panel,saves,content,back,navigation,false)));
        }
        if(saves.hasBackup()){
            if(!body.isEmpty())body.append("\n\n");
            appendSlot(body,"Backup",saves.inspect(true),content);
            options.add(new UiPanel.Option("Xóa backup",()->confirmDelete(panel,saves,content,back,navigation,true)));
        }
        if(body.isEmpty())body.append("Chưa có bản lưu.");
        options.add(new UiPanel.Option("Quay lại",back));panel.show("Bản lưu",body.toString(),options);
    }
    private static void appendSlot(StringBuilder body,String label,SaveStore.Slot slot,StoryContent content){
        body.append(label).append(": ").append(slot.valid()?summary(slot.loaded(),content):"Không đọc được hoặc không tương thích.");
    }
    private static void confirmDelete(UiPanel panel,SaveStore saves,StoryContent content,Runnable back,Consumer<Runnable> navigation,boolean backup){
        Runnable cancel=()->manage(panel,saves,content,back,navigation);navigation.accept(cancel);
        panel.show(backup?"Xóa backup?":"Xóa bản chính?","Bản lưu đã chọn sẽ bị xóa. Thao tác này không thể hoàn tác.",
            List.of(new UiPanel.Option("Hủy",cancel),new UiPanel.Option("Xóa",()->{
                try{saves.delete(backup);cancel.run();}
                catch(RuntimeException e){failure(panel,"Không thể xóa","Không thể xóa bản lưu đã chọn. Hãy thử lại.",cancel,navigation);}
            })));
    }
    static void confirmLoad(UiPanel panel,SaveStore saves,Consumer<SaveStore.Loaded> load,Runnable back,Consumer<Runnable> navigation,boolean backup){
        navigation.accept(back);
        panel.show(backup?"Khôi phục backup?":"Chơi tiếp?","Bạn có muốn chơi tiếp từ "+(backup?"bản backup không?":"bản lưu chính không?"),
            List.of(new UiPanel.Option("Hủy",back),new UiPanel.Option("Chơi tiếp",()->{
                try{load.accept(backup?saves.loadBackup():saves.load());}
                catch(RuntimeException e){failure(panel,"Không thể tải","Bản lưu không đọc được hoặc không tương thích. Bạn có thể kiểm tra trong Cài đặt.",back,navigation);}
            })));
    }
    private static void failure(UiPanel panel,String title,String message,Runnable back,Consumer<Runnable> navigation){
        navigation.accept(back);panel.show(title,message,List.of(new UiPanel.Option("Quay lại",back)));
    }
}
