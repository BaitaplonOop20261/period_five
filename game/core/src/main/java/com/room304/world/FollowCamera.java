package com.room304.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/** Constant zoom across maps; scrolling stops at their edges, small maps stay centered. */
public final class FollowCamera extends OrthographicCamera {
    public void follow(float x,float y,float mapWidth,float mapHeight) {
        float halfWidth=viewportWidth*zoom/2,halfHeight=viewportHeight*zoom/2;
        position.set(center(x,mapWidth,halfWidth),center(y,mapHeight,halfHeight),0);
        update();
    }

    private static float center(float target,float mapSize,float halfView) {
        return mapSize<=halfView*2?mapSize/2:MathUtils.clamp(target,halfView,mapSize-halfView);
    }

    public void visibleBounds(Rectangle result) {
        float width=viewportWidth*zoom,height=viewportHeight*zoom;
        result.set(position.x-width/2,position.y-height/2,width,height);
    }
}
