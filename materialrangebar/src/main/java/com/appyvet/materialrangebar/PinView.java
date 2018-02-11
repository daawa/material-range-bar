package com.appyvet.materialrangebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

/**
 * Created by zhangzhenwei on 2018/1/19.
 */

public interface PinView {

    float getX();
    void setX(float x);
    void setVelocity(float velocity);
    boolean isPressed();
    void draw(Canvas canvas);


    /**
     * Release the pin, sets pressed state to false
     */
    void release();

    /**
     * Sets the state of the pin to pressed
     */
    void press();

    /**
     * Set the value of the pin
     *
     * @param x String value of the pin
     */
    void setPinValue(float x) ;

     String getValue();

     void updateLayout();

    /**
     * Determines if the input coordinate is close enough to this thumb to
     * consider it a press.
     *
     * @param x the x-coordinate of the user touch
     * @param y the y-coordinate of the user touch
     * @return true if the coordinates are within this thumb's target area;
     * false otherwise
     */
     boolean isInTargetZone(float x, float y);

    void setFormatter(com.appyvet.materialrangebar.IRangeBarFormatter mFormatter);

    interface ValueChanged {
        String onValueChanged(float value, View view);
    }
}
