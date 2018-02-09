package com.appyvet.materialrangebar;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static com.appyvet.materialrangebar.RangeBar.ANCHOR_CENTER;
import static com.appyvet.materialrangebar.RangeBar.ANCHOR_LEFT;
import static com.appyvet.materialrangebar.RangeBar.ANCHOR_RIGHT;

/**
 * Created by zhangzhenwei on 2018/1/19.
 */

class CustomPinView extends PinView {

    View customView;
    int anchor = ANCHOR_CENTER;

    float pinViewStubRadius;
    float mExpandedPinRadiusStart;
    float mExpandedPinRadius;

    int layoutWidth = ViewGroup.LayoutParams.WRAP_CONTENT, widMode = MeasureSpec.UNSPECIFIED;
    int layoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT, heightMode = MeasureSpec.UNSPECIFIED;

    RangeBar bar;

    int width, height;

    public CustomPinView(View cv, int anchor, ValueChanged listener, RangeBar rangeBar) {
        super(rangeBar.getContext());
        this.anchor = anchor;
        this.listener = listener;
        this.bar = rangeBar;
        customView = cv;

        firstLayout();
    }

    @Override
    public void invalidate() {
        bar.invalidate();
    }

    private void firstLayout() {

        if (customView.getLayoutParams() != null) {
            ViewGroup.LayoutParams params = customView.getLayoutParams();
            if (params.width > 0) {
                layoutWidth = params.width;
                widMode = MeasureSpec.EXACTLY;
            }
            if (params.height > 0) {
                layoutHeight = params.height;
                heightMode = MeasureSpec.EXACTLY;
            }
        }

        layoutCustomView(layoutWidth, widMode, layoutHeight, heightMode);
        width = customView.getWidth();
        height = customView.getHeight();
        bar.setPinViewStubRadius(width / 2);
        pinViewStubRadius = width / 2;
        setTemporaryPinsSizeRatio(1.0f, 1.0f);

    }

    private void layoutCustomView(int w, int wm, int h, int hm) {
        if (w == customView.getWidth() && h == customView.getHeight()) {
            return;
        }
        int oldWidth = customView.getWidth();
        customView.measure(MeasureSpec.makeMeasureSpec(w, wm), MeasureSpec.makeMeasureSpec(h, hm));
        int left = customView.getLeft() > 0 ? customView.getLeft() : bar.getPaddingLeft();
        left -= (customView.getMeasuredWidth() - oldWidth) / 2;
        int top = (bar.getHeight() - customView.getMeasuredHeight()) / 2;
        this.customView.layout(left, top, left + customView.getMeasuredWidth(), top + customView.getMeasuredHeight());

        Log.w("layout", "wid: " + customView.getWidth() + "measured wid:" + customView.getMeasuredWidth() + " anchor:"  + anchor);
        invalidate();
    }

    @Override
    public void setFormatter(IRangeBarFormatter mFormatter) {

    }

    public void setTemporaryPinsSizeRatio(float from, float to) {
        mExpandedPinRadiusStart = pinViewStubRadius * from;
        mExpandedPinRadius = pinViewStubRadius * to;
    }

    private void setPinZoom(float zoom) {
        layoutCustomView((int) (width * zoom), MeasureSpec.EXACTLY, (int) (height * zoom), MeasureSpec.EXACTLY);
    }

    @Override
    public void release() {
        customView.setPressed(false);
        if (mExpandedPinRadius != mExpandedPinRadiusStart) {
            ValueAnimator animator = ValueAnimator.ofFloat(mExpandedPinRadius, mExpandedPinRadiusStart);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = (Float) (animation.getAnimatedValue());
                    float zoom = val / pinViewStubRadius;
                    CustomPinView.this.setPinZoom(zoom);
                    invalidate();
                }
            });
            animator.start();
        } else {
            invalidate();
        }
    }

    @Override
    public void press() {
        customView.setPressed(true);
        if (mExpandedPinRadius != mExpandedPinRadiusStart) {
            ValueAnimator animator = ValueAnimator.ofFloat(mExpandedPinRadiusStart, mExpandedPinRadius);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = (Float) (animation.getAnimatedValue());
                    float zoom = val / pinViewStubRadius;
                    CustomPinView.this.setPinZoom(zoom);
                    invalidate();
                }
            });
            animator.start();
        }

    }

    @Override
    public boolean isPressed() {
        return customView.isPressed();
    }

    @Override
    public void setX(float x) {

        int left ;
        switch (anchor) {
            case ANCHOR_RIGHT:
                left = (int) x - customView.getWidth();
                break;
            case ANCHOR_LEFT:
                left = (int) x;
                break;
            default: //ANCHOR_CENTER
                left = (int) x - customView.getWidth() / 2;
        }
        //customView.setLeft(left);
        customView.layout(left, customView.getTop(), left + customView.getMeasuredWidth(), customView.getTop() + customView.getMeasuredHeight());
        customView.invalidate();
    }

    @Override
    public float getX() {

        float x ;
        int left = customView.getLeft();
        switch (anchor) {
            case ANCHOR_RIGHT:
                x = customView.getRight();
                break;
            case ANCHOR_LEFT:
                x = left;
                break;
            default: //ANCHOR_CENTER
                x = left + customView.getWidth() / 2;
        }

        return x;
    }

    String val;

    @Override
    public void setValue(String x) {
        val = x;
    }

    @Override
    public String getValue() {
        return val;
    }

    @Override
    public void updateLayout() {
        layoutCustomView(layoutWidth, widMode, layoutHeight, heightMode);
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
