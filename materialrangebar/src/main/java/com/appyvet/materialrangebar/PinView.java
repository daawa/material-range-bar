package com.appyvet.materialrangebar;

import android.content.Context;
import android.graphics.Point;
import android.view.View;

/**
 * Created by zhangzhenwei on 2018/1/19.
 */

public abstract class PinView extends View {
    protected ValueChanged listener;

    public PinView(Context context) {
        super(context);
    }


    public abstract void setFormatter(com.appyvet.materialrangebar.IRangeBarFormatter mFormatter);

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
    public final void setPinValue(float x){
        String old = getValue();

        String val = null;
        if(listener != null){
            val = listener.onValueChanged(x);
        }
        val = val == null? String.valueOf(x): val;

        setValue(val);
        if(old == null || old.length() != (getValue() == null? "" : getValue()).length()){
            updateLayout();
        }
    }

    public abstract void setValue(String val);
    public abstract String getValue();

    public abstract void updateLayout();

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

    public interface ValueChanged{
        String onValueChanged(float value);
    }
}
