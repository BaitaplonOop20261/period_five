package com.room304.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.room304.assets.GameAssets;
import com.room304.ui.PixelFrame;
import com.room304.ui.UiFont;
import java.util.List;
import java.util.ArrayList;
import com.room304.story.StoryContent;

/** Functional menus and overlays. Commands are queued and run by the caller after drawing. */
public final class UiPanel implements Disposable {
    public record Option(String text,Runnable action,boolean enabled) {
        public Option(String text,Runnable action){this(text,action,true);}
    }
    private final Stage stage;
    private final BitmapFont font;
    private final Texture pixel;
    private final Texture arrowTexture;
    private final TextureRegionDrawable white, arrowDrawable;
    private final Drawable frame, keycap;
    private final ScrollPane.ScrollPaneStyle scrollStyle;
    private final boolean pauseOverlay;
    private final TextButton.TextButtonStyle buttonStyle;
    private final TextButton.TextButtonStyle lobbyButtonStyle;
    private final Skin skin;
    private final GameAssets assets;
    private final com.badlogic.gdx.scenes.scene2d.utils.Drawable defaultBackground;
    private Label.LabelStyle labels;
    private final Table root=new Table();
    private final List<TextButton> buttons=new ArrayList<>();
    private final List<Image> markers=new ArrayList<>();
    private List<Option> options=List.of();
    private int selected;
    private Runnable command;
    private AudioSettings audioSettings;
    private StoryContent audioContent;
    private Runnable audioBack;
    private ScrollPane optionsPane;
    private boolean hasBackgroundArt;
    public UiPanel(GameAssets assets) { this(assets,false); }
    public UiPanel(GameAssets assets,boolean pauseOverlay) { this(assets,pauseOverlay,new FitViewport(640,480)); }
    public UiPanel(GameAssets assets,FitViewport viewport) { this(assets,false,viewport); }
    private UiPanel(GameAssets assets,boolean pauseOverlay,FitViewport viewport) {
        stage=new Stage(viewport);
        this.assets=assets;
        this.pauseOverlay=pauseOverlay;
        font = UiFont.create(Gdx.files.internal(assets.catalog().path("font.ui")), 18);
        Pixmap image=new Pixmap(1,1,Pixmap.Format.RGBA8888);image.setColor(Color.WHITE);image.fill();pixel=new Texture(image);image.dispose();
        white=new TextureRegionDrawable(pixel);
        Pixmap triangle=new Pixmap(5,7,Pixmap.Format.RGBA8888);triangle.setColor(Color.WHITE);triangle.fillTriangle(0,0,0,6,4,3);
        arrowTexture=new Texture(triangle);triangle.dispose();arrowDrawable=new TextureRegionDrawable(arrowTexture);
        Color dark=new Color(.105f,.092f,.085f,1),gold=new Color(.70f,.55f,.33f,1),bright=new Color(.96f,.77f,.46f,1);
        TextButton.TextButtonStyle fallback=new TextButton.TextButtonStyle();fallback.font=font;fallback.fontColor=new Color(.94f,.91f,.85f,1);
        fallback.checkedFontColor=fallback.overFontColor=bright;
        fallback.disabledFontColor=new Color(.43f,.41f,.37f,1);
        fallback.up=new PixelFrame(white.getRegion(),dark,new Color(.46f,.37f,.24f,1),false);
        fallback.checked=fallback.over=new PixelFrame(white.getRegion(),new Color(.25f,.19f,.12f,1),bright,false);
        fallback.down=new PixelFrame(white.getRegion(),new Color(.16f,.12f,.08f,1),gold,false);
        try {skin=assets.optionalSkin(font);}catch(RuntimeException e){stage.dispose();font.dispose();pixel.dispose();arrowTexture.dispose();throw e;}buttonStyle=skin!=null&&skin.has("default",TextButton.TextButtonStyle.class)?skin.get("default",TextButton.TextButtonStyle.class):fallback;
        lobbyButtonStyle=new TextButton.TextButtonStyle(fallback);
        lobbyButtonStyle.up=white.tint(new Color(.04f,.035f,.025f,.12f));
        lobbyButtonStyle.over=white.tint(new Color(.20f,.15f,.09f,.6f));
        lobbyButtonStyle.checked=new PixelFrame(white.getRegion(),new Color(.20f,.15f,.09f,.7f),bright,false);
        labels=skin!=null&&skin.has("default",Label.LabelStyle.class)?skin.get("default",Label.LabelStyle.class):new Label.LabelStyle(font,new Color(.96f,.93f,.85f,1));
        frame=GameAssets.skinDrawable(skin,"menu-frame",new PixelFrame(white.getRegion(),dark,gold,true));
        PixelFrame keyFrame=new PixelFrame(white.getRegion(),dark,gold,false);
        keyFrame.setLeftWidth(3);keyFrame.setRightWidth(3);keyFrame.setTopHeight(1);keyFrame.setBottomHeight(1);keycap=keyFrame;
        scrollStyle=new ScrollPane.ScrollPaneStyle();
        BaseDrawable track=(BaseDrawable)white.tint(new Color(.32f,.27f,.19f,.35f)),knob=(BaseDrawable)white.tint(gold);
        track.setMinWidth(2);knob.setMinWidth(2);knob.setMinHeight(18);scrollStyle.vScroll=track;scrollStyle.vScrollKnob=knob;
        defaultBackground=white.tint(new Color(.02f,.018f,.016f,pauseOverlay?.64f:1));
        root.setFillParent(true);root.setBackground(defaultBackground);stage.addActor(root);
    }
    public void background(String id){Texture art=assets.optionalTexture(id);hasBackgroundArt=art!=null;root.setBackground(art==null?defaultBackground:new TextureRegionDrawable(art));}
    public BitmapFont font() { return font; }
    public Stage stage() { return stage; }
    public void show(String title,String body,List<Option> options) {
        prepare(options);
        if(hasBackgroundArt) {
            Image dim=new Image(white);dim.setColor(0,0,0,.62f);dim.setBounds(0,0,stage.getWidth(),stage.getHeight());dim.setTouchable(Touchable.disabled);root.addActor(dim);
        }
        float panelWidth=body.length()>120||title.length()>36?500:344,innerWidth=panelWidth-56;
        Table card=new Table();card.setName("menu-panel");card.setBackground(frame);card.pad(24,28,20,28);
        Label heading=new Label(title,labels);heading.setName("menu-title");UiFont.scale(heading,1.3f);heading.setWrap(true);heading.setAlignment(Align.center);heading.setWidth(innerWidth);
        card.add(heading).width(innerWidth).padBottom(12).row();
        card.add(separator()).width(innerWidth).height(5).padBottom(18).row();
        float fixedHeight=24+20+heading.getPrefHeight()+12+5+18+16+5+12+20;
        if(!body.isBlank()) {
            Label message=new Label(body,new Label.LabelStyle(font,new Color(.84f,.80f,.73f,1)));UiFont.scale(message,.88f);message.setWrap(true);message.setAlignment(Align.center);message.setWidth(innerWidth);
            float bodyHeight=Math.min(110,message.getPrefHeight());
            card.add(scroll(message)).width(innerWidth).height(bodyHeight).padBottom(12).row();fixedHeight+=bodyHeight+12;
        }
        Table choices=new Table();
        for(int i=0;i<options.size();i++) {
            TextButton b=menuButton(i,innerWidth,false);
            float buttonHeight=Math.max(36,b.getLabel().getPrefHeight()+12);
            choices.add(b).width(innerWidth).height(buttonHeight).padBottom(i+1<options.size()?8:0).row();
        }
        choices.pack();optionsPane=scroll(choices);optionsPane.setName("menu-options");
        card.add(optionsPane).width(innerWidth).height(Math.min(choices.getPrefHeight(),Math.max(36,440-fixedHeight))).row();
        card.add(separator()).width(innerWidth).height(5).padTop(16).padBottom(12).row();
        Table footer=new Table();footer.setName("menu-footer");
        footer.add(shortcut("↑ ↓","Chọn",26)).padRight(12);
        footer.add(shortcut("Enter","Xác nhận",32));
        if(pauseOverlay)footer.add(shortcut("Esc","Tiếp tục",24)).padLeft(12);
        card.add(footer).height(20).row();
        root.add(card).width(panelWidth);
    }
    /** Title art stays behind native, interactive left-side navigation. */
    public void lobby(String fallbackTitle,List<Option> options) {
        lobby(fallbackTitle,options,"");
    }
    public void lobby(String fallbackTitle,List<Option> options,String detail) {
        prepare(options);
        if(!hasBackgroundArt) {
            Label title=new Label(fallbackTitle,labels);title.setWrap(true);UiFont.scale(title,1.6f);title.setBounds(44,322,305,170);title.setTouchable(Touchable.disabled);root.addActor(title);
        }
        Table choices=new Table();choices.setName("lobby-actions");
        for(int i=0;i<options.size();i++)choices.add(menuButton(i,240,true)).width(240).height(34).padBottom(i+1<options.size()?5:0).row();
        choices.pack();optionsPane=scroll(choices);optionsPane.setName("menu-options");optionsPane.setBounds(40,114,240,choices.getPrefHeight());root.addActor(optionsPane);
        Label hint=new Label("Lên / xuống · Chọn    Enter · Xác nhận",new Label.LabelStyle(font,new Color(.84f,.80f,.70f,1)));
        UiFont.scale(hint,.7f);hint.setBounds(22,28,480,22);hint.setTouchable(Touchable.disabled);root.addActor(hint);
        Label summary=new Label(detail,labels);summary.setName("save-summary");UiFont.scale(summary,.65f);summary.setWrap(true);
        summary.setBounds(40,62,260,44);summary.setTouchable(Touchable.disabled);root.addActor(summary);
    }
    private void prepare(List<Option> options) {
        audioSettings=null;stage.cancelTouchFocus();stage.setScrollFocus(null);command=null;this.options=List.copyOf(options);selected=0;buttons.clear();markers.clear();root.clear();
        while(selected<options.size()-1&&!options.get(selected).enabled())selected++;
    }
    private TextButton menuButton(int index,float width,boolean lobby) {
        TextButton button=new TextButton(options.get(index).text(),lobby?lobbyButtonStyle:buttonStyle);
        button.setName("menu-option-"+index);button.setProgrammaticChangeEvents(false);
        button.setDisabled(!options.get(index).enabled());
        Label label=button.getLabel();UiFont.scale(label,lobby?1.05f:.9f);label.setWrap(true);label.setWidth(width-64);label.setAlignment(lobby?Align.left:Align.center);
        Image marker=new Image(arrowDrawable);marker.setColor(.96f,.77f,.46f,1);marker.setTouchable(Touchable.disabled);marker.setVisible(index==selected);markers.add(marker);
        button.clearChildren();button.pad(6,12,6,12);button.add(marker).size(5,7).padLeft(lobby?12:8).padRight(lobby?18:8);button.add(label).growX();button.add().width(21);
        button.setChecked(index==selected);buttons.add(button);
        button.addListener(new InputListener(){@Override public boolean mouseMoved(InputEvent event,float x,float y){if(!button.isDisabled())select(index);return false;}});
        button.addListener(new ChangeListener(){@Override public void changed(ChangeEvent event,Actor actor){if(button.isDisabled())return;select(index);if(command==null)command=UiPanel.this.options.get(index).action();}});
        return button;
    }
    private ScrollPane scroll(Actor content) {
        ScrollPane pane=new ScrollPane(content,scrollStyle);pane.setScrollingDisabled(true,false);pane.setFadeScrollBars(false);pane.setOverscroll(false,false);
        pane.addListener(new InputListener(){@Override public void enter(InputEvent event,float x,float y,int pointer,Actor from){if(pointer==-1)stage.setScrollFocus(pane);}});
        return pane;
    }
    private Table separator() {
        Table rule=new Table();Image left=new Image(white),right=new Image(white),mark=new Image(white);
        left.setColor(.50f,.39f,.24f,1);right.setColor(left.getColor());mark.setColor(.70f,.55f,.33f,1);
        rule.add(left).growX().height(1);rule.add(mark).size(5).padLeft(10).padRight(10);rule.add(right).growX().height(1);rule.setTouchable(Touchable.disabled);return rule;
    }
    private Table shortcut(String key,String description,float width) {
        Actor keyActor;
        if(key.equals("↑ ↓")) {
            Table directions=new Table();directions.setBackground(keycap);directions.pad(0);
            Image up=new Image(arrowDrawable),down=new Image(arrowDrawable);
            for(Image arrow:List.of(up,down)){arrow.setSize(5,7);arrow.setOrigin(Align.center);arrow.setColor(.90f,.84f,.72f,1);}
            up.setRotation(90);down.setRotation(270);directions.add(up).size(5,7).padRight(5);directions.add(down).size(5,7);keyActor=directions;
        } else {
            Label keyLabel=new Label(key,new Label.LabelStyle(font,new Color(.90f,.84f,.72f,1)));keyLabel.getStyle().background=keycap;UiFont.scale(keyLabel,.62f);keyLabel.setAlignment(Align.center);keyActor=keyLabel;
        }
        Label hint=new Label(description,new Label.LabelStyle(font,new Color(.83f,.79f,.72f,1)));UiFont.scale(hint,.62f);
        Table group=new Table();group.add(keyActor).size(width,18).padRight(5);group.add(hint);group.setTouchable(Touchable.disabled);return group;
    }
    private void select(int index) {
        selected=index;
        for(int i=0;i<buttons.size();i++){buttons.get(i).setChecked(i==selected);markers.get(i).setVisible(i==selected);}
    }
    public void key(int direction,boolean confirm) {
        if(options.isEmpty()) return;
        if(direction!=0) {int next=selected;for(int i=0;i<options.size();i++){next=Math.floorMod(next+direction,options.size());if(options.get(next).enabled())break;}select(next);optionsPane.scrollTo(buttons.get(selected).getX(),buttons.get(selected).getY(),buttons.get(selected).getWidth(),buttons.get(selected).getHeight());}
        if(confirm && command==null && options.get(selected).enabled()) command=options.get(selected).action();
    }
    public void help(StoryContent content,Runnable back) {
        StringBuilder text=new StringBuilder();for(int i=1;i<=5;i++) text.append(content.text("UI-HELP-0"+i)).append("\n");
        text.append("Esc → Lưu game: lưu khi khám phá. Tiếp tục ở menu chính để tải lại; có thể mở bản sao lưu trong màn tải. Chơi mới giữ bản lưu cũ cho đến khi bạn lưu đè.");
        show(content.text("UI-HELP-TITLE"),text.toString().replace("↑ / ↓","Lên / xuống").replace("Esc →","Esc >"),List.of(new Option(content.text("UI-BACK"),back)));
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
        settings.change(selected,direction*.1f);audio(content,settings,back);select(previous);optionsPane.scrollTo(buttons.get(selected).getX(),buttons.get(selected).getY(),buttons.get(selected).getWidth(),buttons.get(selected).getHeight());
    }
    public void draw(float delta) { stage.act(Math.max(0,delta));stage.getViewport().apply();stage.draw(); }
    public void executeCommand() { Runnable next=command;command=null;if(next!=null) next.run(); }
    public void clearCommands() { command=null;stage.cancelTouchFocus(); }
    public void resize(int width,int height) { if(width>0 && height>0) stage.getViewport().update(width,height,true); }
    @Override public void dispose() { stage.dispose();if(skin!=null){skin.remove("default-font",BitmapFont.class);skin.dispose();}font.dispose();pixel.dispose();arrowTexture.dispose(); }
}
