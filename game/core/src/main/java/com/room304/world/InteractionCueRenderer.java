package com.room304.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.room304.assets.GameAssets;

/** Proximity cues borrow the shared marker texture and the world's renderers. */
final class InteractionCueRenderer {
    private static final float[] RECTANGLE={0,0,1,0,1,1,0,1};
    private final Animation<TextureRegion> mark;

    InteractionCueRenderer(GameAssets assets) { mark=assets.animation("ui.interaction"); }

    /** Called between batch passes, so the glow sits behind foreground actors. */
    void outline(ShapeRenderer shapes,SceneCatalog.ObjectDef object,float time,float scale) {
        float[] points=object.highlightOutline.length==0?RECTANGLE:object.highlightOutline;
        float pulse=.85f+.15f*MathUtils.sin(time*4);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        stroke(shapes,object,points,6*scale,.08f*pulse);
        stroke(shapes,object,points,3*scale,.20f*pulse);
        stroke(shapes,object,points,Math.max(.45f,.8f*scale),.95f*pulse);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void stroke(ShapeRenderer shapes,SceneCatalog.ObjectDef object,float[] points,float width,float alpha) {
        shapes.setColor(1,.82f,.12f,alpha);
        for(int i=0;i<points.length;i+=2) {
            int next=(i+2)%points.length;
            float x=object.x+points[i]*object.width,y=object.y+points[i+1]*object.height;
            float nx=object.x+points[next]*object.width,ny=object.y+points[next+1]*object.height;
            shapes.rectLine(x,y,nx,ny,width);
            shapes.circle(x,y,width/2,8);
        }
    }

    void marker(SpriteBatch batch,float centerX,float top,float actorHeight,float time,Rectangle visible) {
        if(mark==null)return;
        TextureRegion frame=mark.getKeyFrame(time,true);
        float height=MathUtils.clamp(actorHeight*.4f,10,18);
        float width=height*frame.getRegionWidth()/frame.getRegionHeight();
        float gap=height*.15f;
        float x=MathUtils.clamp(centerX-width/2,visible.x+1,visible.x+visible.width-width-1);
        float y=MathUtils.clamp(top+gap,visible.y+1,visible.y+visible.height-height-1);
        batch.setColor(Color.WHITE);
        batch.draw(frame,x,y,width,height);
    }
}
