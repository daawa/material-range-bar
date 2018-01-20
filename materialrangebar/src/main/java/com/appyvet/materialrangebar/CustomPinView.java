package com.appyvet.materialrangebar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import static android.view.View.MeasureSpec.UNSPECIFIED;

/**
 * Created by zhangzhenwei on 2018/1/19.
 */

public class CustomPinView extends PinView {
    View customView;
    boolean isPressed;
    int barHeight;
    RangeBar bar;
    int topPosition;
    public CustomPinView( View cv, RangeBar rangeBar) {
        super(rangeBar.getContext());
        this.bar = rangeBar;
        customView = cv;
        barHeight = bar.getHeight() > 0 ? bar.getHeight() : 1000;
        layoutCustomView(barHeight);

        bar.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                barHeight = bar.getHeight() > 0 ? bar.getHeight() : 1000;
                layoutCustomView(barHeight);
            }
        });
    }

    private void layoutCustomView(int size) {
        customView.measure(MeasureSpec.makeMeasureSpec(size, UNSPECIFIED), MeasureSpec.makeMeasureSpec(size, UNSPECIFIED));

        topPosition = (bar.getHeight() - customView.getMeasuredHeight()) / 2;
        topPosition = topPosition > 0 ? topPosition : 0;
        int left = customView.getLeft() > 0 ? customView.getLeft() : bar.getPaddingLeft();
        this.customView.layout(left, topPosition, left + customView.getMeasuredWidth(), topPosition + customView.getMeasuredHeight());
    }

    @Override
    public void setFormatter(IRangeBarFormatter mFormatter) {

    }

    @Override
    public void setSize(float size, float padding) {
        layoutCustomView((int)size);
    }

    @Override
    public void release() {
        isPressed = false;
    }

    @Override
    public void press() {
        isPressed = true;
    }

    @Override
    public boolean isPressed() {
        return isPressed;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        int left = (int) x;
        customView.layout(left, topPosition, left + customView.getMeasuredWidth(), topPosition + customView.getMeasuredHeight());
        customView.invalidate();
    }

    @Override
    public float getX() {
        float x = super.getX();
        Log.w("getx", "x :" + x);
        return x;
    }


    @Override
    public void setXValue(String x) {

    }

    @Override
    public boolean isInTargetZone(float x, float y) {
        boolean res = (Math.abs(x - customView.getX()) <= customView.getMeasuredWidth()
                && Math.abs(y - customView.getY()) <= customView.getMeasuredHeight());
        return res;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(customView.getLeft(), customView.getTop());
        customView.draw(canvas);
        super.draw(canvas);
        canvas.restore();
    }
}
