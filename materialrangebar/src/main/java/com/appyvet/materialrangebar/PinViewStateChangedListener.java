package com.appyvet.materialrangebar;

import android.view.View;

/**
 * Created by zhangzhenwei on 2018/2/11.
 */


public interface PinViewStateChangedListener extends PinView.ValueChanged {
    void onVelocityChanged(float velocity, View view);

    void pressStateChanged(boolean isPressed, View view);

    void layoutUpdated(View view);
}