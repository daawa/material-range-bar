package com.appyvet.materialrangebar;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangzhenwei on 2018/1/19.
 */

class CustomPinView extends PinView {
    View customView;
    boolean isPressed;
    int layoutWidth, widMode = MeasureSpec.AT_MOST;
    int layoutHeight, heightMode = MeasureSpec.AT_MOST;
    RangeBar bar;

    int width, height;

    public CustomPinView(View cv, RangeBar rangeBar) {
        super(rangeBar.getContext());
        this.bar = rangeBar;
        customView = cv;

        firstLayout();

        bar.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                firstLayout();
                /*
                layoutWidth = bar.getHeight() > 0 ? bar.getHeight() : 10000;
                layoutHeight = bar.getWidth() > 0? bar.getWidth() : 10000;

                if(customView.getLayoutParams() != null){
                    ViewGroup.LayoutParams params = customView.getLayoutParams();
                    if(params.width > 0){
                        layoutHeight = params.width;
                        widMode = MeasureSpec.EXACTLY;
                    }
                    if(params.height > 0){
                        layoutWidth = params.height;
                        heightMode = MeasureSpec.EXACTLY;
                    }
                }

                layoutCustomView(layoutHeight, widMode, layoutWidth, heightMode);

                width = customView.getWidth();
                height = customView.getHeight();
                bar.setPinViewStubRadius(width / 2);
                */
            }
        });
    }

    private void firstLayout(){
        layoutWidth = bar.getHeight() > 0 ? bar.getHeight() : 10000;
        layoutHeight = bar.getWidth() > 0? bar.getWidth() : 10000;

        if(customView.getLayoutParams() != null){
            ViewGroup.LayoutParams params = customView.getLayoutParams();
            if(params.width > 0){
                layoutHeight = params.width;
                widMode = MeasureSpec.EXACTLY;
            }
            if(params.height > 0){
                layoutWidth = params.height;
                heightMode = MeasureSpec.EXACTLY;
            }
        }

        layoutCustomView(layoutHeight, widMode, layoutWidth, heightMode);
        width = customView.getWidth();
        height = customView.getHeight();
        bar.setPinViewStubRadius(width / 2);
    }

    private void layoutCustomView(int w, int wm, int h, int hm) {
        if (w == customView.getWidth() && h == customView.getHeight()) {
            return;
        }
        int oldw = customView.getWidth();
        customView.measure(MeasureSpec.makeMeasureSpec(w, wm), MeasureSpec.makeMeasureSpec(h, hm));
        int left = customView.getLeft() > 0 ? customView.getLeft() : bar.getPaddingLeft();
        left -= (customView.getMeasuredWidth() - oldw) / 2;
        int top = (bar.getHeight() - customView.getMeasuredHeight()) / 2;
        this.customView.layout(left, top, left + customView.getMeasuredWidth(), top + customView.getMeasuredHeight());

    }

    @Override
    public void setFormatter(IRangeBarFormatter mFormatter) {

    }

    @Override
    public void setPinZoom(float zoom, float padding) {
        layoutCustomView((int) (width * zoom), MeasureSpec.EXACTLY, (int) (height * zoom), MeasureSpec.EXACTLY);
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

        int left = (int) x - customView.getWidth() / 2;
        customView.layout(left, customView.getTop(), left + customView.getMeasuredWidth(), customView.getTop() + customView.getMeasuredHeight());
        customView.invalidate();
    }

    @Override
    public float getX() {
        float x = customView.getLeft() + customView.getWidth() / 2;
        //Log.w("getx", "x :" + x);
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
