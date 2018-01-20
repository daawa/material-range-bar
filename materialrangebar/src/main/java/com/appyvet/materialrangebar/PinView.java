package com.appyvet.materialrangebar;

import android.content.Context;
import android.view.View;

/**
 * Created by zhangzhenwei on 2018/1/19.
 */

public abstract class PinView extends View {
    public PinView(Context context) {
        super(context);
    }

    public abstract void setFormatter(com.appyvet.materialrangebar.IRangeBarFormatter mFormatter);

    /**
     * Set size of the pin and padding for use when animating pin enlargement on press
     *
     * @param size    the size of the pin radius
     * @param padding the size of the padding
     */
    public abstract void setSize(float size, float padding) ;

    /**
     * Release the pin, sets pressed state to false
     */
    public abstract void release();

    /**
     * Sets the state of the pin to pressed
     */
    public abstract void press();

    /**
     * Set the value of the pin
     *
     * @param x String value of the pin
     */
    public abstract void setXValue(String x);

    /**
     * Determines if the input coordinate is close enough to this thumb to
     * consider it a press.
     *
     * @param x the x-coordinate of the user touch
     * @param y the y-coordinate of the user touch
     * @return true if the coordinates are within this thumb's target area;
     * false otherwise
     */
    public abstract boolean isInTargetZone(float x, float y);
}
