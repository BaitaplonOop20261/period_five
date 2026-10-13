package com.room304.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.room304.assets.GameAssets;
import java.util.List;
import java.util.ArrayList;
import com.room304.story.StoryContent;

/** Functional menus and overlays. Commands are queued and run by the caller after drawing. */
public final class UiPanel implements Disposable {
    public record Option(String text,Runnable action) {}
    private final Stage stage=new Stage(new FitViewport(640,480));
    private final BitmapFont font;
    private final Texture pixel;
    private final TextButton.TextButtonStyle buttonStyle;
    private final Skin skin;
    private final GameAssets assets;
    private final com.badlogic.gdx.scenes.scene2d.utils.Drawable defaultBackground;
    private Label.LabelStyle labels;
    private final Table root=new Table();
    private final List<TextButton> buttons=new ArrayList<>();
    private List<Option> options=List.of();
    private int selected;
    private Runnable command;
    private AudioSettings audioSettings;
    private StoryContent audioContent;
    private Runnable audioBack;
    private ScrollPane optionsPane;
    public UiPanel(GameAssets assets) {
        this.assets=assets;
        FreeTypeFontGenerator generator=new FreeTypeFontGenerator(Gdx.files.internal(assets.catalog().path("font.ui")));
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter p=new FreeTypeFontGenerator.FreeTypeFontParameter();p.size=18;
            StringBuilder chars=new StringBuilder(FreeTypeFontGenerator.DEFAULT_CHARS);
            for(char c=0x00a0;c<=0x01ff;c++) chars.append(c);for(char c=0x1ea0;c<=0x1ef9;c++) chars.append(c);chars.append("‘’“”…–—↑↓←→×");p.characters=chars.toString();
            p.minFilter=p.magFilter=Texture.TextureFilter.Linear;font=generator.generateFont(p);
        } finally { generator.dispose(); }
        Pixmap image=new Pixmap(1,1,Pixmap.Format.RGBA8888);image.setColor(Color.WHITE);image.fill();pixel=new Texture(image);image.dispose();
        TextureRegionDrawable drawable=new TextureRegionDrawable(pixel);
        TextButton.TextButtonStyle fallback=new TextButton.TextButtonStyle();fallback.font=font;fallback.fontColor=Color.WHITE;
        fallback.up=drawable.tint(new Color(.07f,.10f,.14f,1));fallback.checked=fallback.over=drawable.tint(new Color(.20f,.32f,.40f,1));fallback.down=fallback.over;
        try {skin=assets.optionalSkin(font);}catch(RuntimeException e){stage.dispose();font.dispose();pixel.dispose();throw e;}buttonStyle=skin!=null&&skin.has("default",TextButton.TextButtonStyle.class)?skin.get("default",TextButton.TextButtonStyle.class):fallback;
        labels=skin!=null&&skin.has("default",Label.LabelStyle.class)?skin.get("default",Label.LabelStyle.class):new Label.LabelStyle(font,Color.WHITE);
        defaultBackground=GameAssets.skinDrawable(skin,"menu-frame",drawable.tint(new Color(.02f,.025f,.04f,.95f)));root.setFillParent(true);root.setBackground(defaultBackground);stage.addActor(root);
    }
    public void background(String id){Texture art=assets.optionalTexture(id);root.setBackground(art==null?defaultBackground:new TextureRegionDrawable(art));}
    public BitmapFont font() { return font; }
    public Stage stage() { return stage; }
    public void show(String title,String body,List<Option> options) {
        audioSettings=null;stage.cancelTouchFocus();command=null;this.options=List.copyOf(options);selected=0;buttons.clear();root.clear();
        Label heading=new Label(title,labels);heading.setWrap(true);heading.setAlignment(Align.center);
        root.add(heading).width(580).padBottom(18).row();
        if(!body.isBlank()) {
            Label message=new Label(body,new Label.LabelStyle(font,Color.LIGHT_GRAY));message.setWrap(true);message.setAlignment(Align.center);
            ScrollPane scroll=new ScrollPane(message);scroll.setScrollingDisabled(true,false);scroll.setFadeScrollBars(false);
            root.add(scroll).width(570).height(Math.min(160,Math.max(52,body.length()/45*24))).padBottom(12).row();
        }
        Table choices=new Table();
        for(int i=0;i<options.size();i++) {
            final int index=i;TextButton b=new TextButton(options.get(i).text(),buttonStyle);b.setProgrammaticChangeEvents(false);b.getLabel().setWrap(true);b.setChecked(i==0);buttons.add(b);
            b.addListener(new ChangeListener(){@Override public void changed(ChangeEvent e,Actor actor){if(command==null) command=UiPanel.this.options.get(index).action();}});
            choices.add(b).width(510).minHeight(38).padBottom(5).row();
        }
        ScrollPane scroll=new ScrollPane(choices);optionsPane=scroll;scroll.setScrollingDisabled(true,false);scroll.setFadeScrollBars(false);root.add(scroll).width(540).maxHeight(270).row();
        Label hint=new Label("Phím lên / xuống · Chọn     Enter · Xác nhận",new Label.LabelStyle(font,Color.LIGHT_GRAY));hint.setFontScale(.75f);root.add(hint).padTop(10);
    }
    public void key(int direction,boolean confirm) {
        if(options.isEmpty()) return;
        if(direction!=0) {selected=Math.floorMod(selected+direction,options.size());for(int i=0;i<buttons.size();i++) buttons.get(i).setChecked(i==selected);optionsPane.scrollTo(buttons.get(selected).getX(),buttons.get(selected).getY(),buttons.get(selected).getWidth(),buttons.get(selected).getHeight());}
        if(confirm && command==null) command=options.get(selected).action();
    }
    public void help(StoryContent content,Runnable back) {
        StringBuilder text=new StringBuilder();for(int i=1;i<=5;i++) text.append(content.text("UI-HELP-0"+i)).append("\n");
        text.append("Esc → Lưu game: lưu tại mốc khám phá. Tiếp tục từ menu để tải lại.");
        show(content.text("UI-HELP-TITLE"),text.toString(),List.of(new Option(content.text("UI-BACK"),back)));
    }
    public void audio(StoryContent content,AudioSettings settings,Runnable back) {
        List<Option> choices=new ArrayList<>();String[] ids={"MASTER","MUSIC","FX","VOICE"};float[] values={settings.master,settings.music,settings.fx,settings.voice};
        for(int i=0;i<4;i++){final int channel=i;choices.add(new Option(content.text("UI-AUDIO-"+ids[i])+" · "+Math.round(values[i]*100)+"%",()->{settings.change(channel,.1f);audio(content,settings,back);}));}
        choices.add(new Option(content.text("UI-AUDIO-MUTE")+" · "+content.text(settings.muted?"UI-ON":"UI-OFF"),()->{settings.muted=!settings.muted;audio(content,settings,back);}));
        choices.add(new Option(content.text("UI-BACK"),back));show(content.text("UI-AUDIO-TITLE"),content.text("UI-AUDIO-HINT").replace("↑ / ↓","Lên / xuống").replace("← / →","Trái / phải")+"\n"+content.text("UI-AUDIO-SUB"),choices);
        audioSettings=settings;audioContent=content;audioBack=back;
    }
    public void adjustAudio(int direction) {
        if(audioSettings==null || direction==0 || selected>3) return;
        int previous=selected;AudioSettings settings=audioSettings;StoryContent content=audioContent;Runnable back=audioBack;
        settings.change(selected,direction*.1f);audio(content,settings,back);selected=previous;for(int i=0;i<buttons.size();i++) buttons.get(i).setChecked(i==selected);
    }
    public void draw(float delta) { stage.act(Math.max(0,delta));stage.getViewport().apply();stage.draw(); }
    public void executeCommand() { Runnable next=command;command=null;if(next!=null) next.run(); }
    public void clearCommands() { command=null;stage.cancelTouchFocus(); }
    public void resize(int width,int height) { if(width>0 && height>0) stage.getViewport().update(width,height,true); }
    @Override public void dispose() { stage.dispose();if(skin!=null){skin.remove("default-font",BitmapFont.class);skin.dispose();}font.dispose();pixel.dispose(); }
}
