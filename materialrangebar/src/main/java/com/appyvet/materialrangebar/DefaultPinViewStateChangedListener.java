package com.appyvet.materialrangebar;

import android.view.View;

/**
 * Created by zhangzhenwei on 2018/2/11.
 */

public class DefaultPinViewStateChangedListener implements PinViewStateChangedListener {
    @Override
    public void onVelocityChanged(float velocity, View view) {

    }

    @Override
    public void pressStateChanged(boolean isPressed, View view) {

    }

    @Override
    public String onValueChanged(float value, View view) {
        return String.valueOf(value);
    }

    @Override
    public void layoutUpdated(View view) {

    }

}
