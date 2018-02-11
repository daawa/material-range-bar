package com.appyvet.materialrangebar;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static com.appyvet.materialrangebar.RangeBar.ANCHOR_CENTER;
import static com.appyvet.materialrangebar.RangeBar.ANCHOR_LEFT;
import static com.appyvet.materialrangebar.RangeBar.ANCHOR_RIGHT;

/**
 * Created by zhangzhenwei on 2018/1/19.
 */

class CustomPinView extends FrameLayout implements PinView {
    protected PinViewStateChangedListener listener;

    private FrameLayout container;
    View customView;
    int anchor = ANCHOR_CENTER;

    float pinViewStubRadius;
    float mExpandedPinRadiusStart;
    float mExpandedPinRadius;

    int layoutWidth = ViewGroup.LayoutParams.WRAP_CONTENT, widMode = View.MeasureSpec.UNSPECIFIED;
    int layoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT, heightMode = View.MeasureSpec.UNSPECIFIED;

    RangeBar bar;

    int width, height;

    public CustomPinView(int layoutId, int anchor, PinViewStateChangedListener listener, RangeBar rangeBar) {
        super(rangeBar.getContext());
        this.container = this;
        this.container.setClipChildren(false);
        this.container.setClipToPadding(false);

        this.anchor = anchor;
        this.listener = listener;
        this.bar = rangeBar;
        this.customView = LayoutInflater.from(bar.getContext()).inflate(layoutId, this.container, false);
        this.container.addView(customView);
        //this.container.setBackgroundColor(Color.YELLOW);
        firstLayout();
    }

    public void invalidate() {
        bar.invalidate();
    }

    private void firstLayout() {
        layoutCustomView(layoutWidth, widMode, layoutHeight, heightMode);
        width = container.getWidth();
        height = container.getHeight();
        bar.setPinViewStubRadius(width / 2);
        pinViewStubRadius = width / 2;
        setTemporaryPinsSizeRatio(1.0f, 1.0f);

    }

    private void layoutCustomView(int w, int wm, int h, int hm) {
        if (w == container.getWidth() && h == container.getHeight()) {
            return;
        }
        //int oldWidth = container.getWidth();
        container.measure(View.MeasureSpec.makeMeasureSpec(w, wm), View.MeasureSpec.makeMeasureSpec(h, hm));
        float ap = getAnchor();
        if(ap < bar.getPaddingLeft()){
            ap = (bar.getPaddingLeft());
        } else if(ap > bar.getWidth() - bar.getPaddingRight()){
            ap = (bar.getWidth() - bar.getPaddingRight());
        }

        setAnchor(ap);

//        int left = container.getLeft() ;
//        left -= (container.getMeasuredWidth() - oldWidth) / 2;
//        int top = (bar.getHeight() - container.getMeasuredHeight()) / 2;
//        this.container.layout(left, top, left + container.getMeasuredWidth(), top + container.getMeasuredHeight());

    }

    @Override
    public void setFormatter(IRangeBarFormatter mFormatter) {

    }

    public void setTemporaryPinsSizeRatio(float from, float to) {
        mExpandedPinRadiusStart = pinViewStubRadius * from;
        mExpandedPinRadius = pinViewStubRadius * to;
    }

    private void setPinZoom(float zoom) {
        layoutCustomView((int) (width * zoom), View.MeasureSpec.EXACTLY, (int) (height * zoom), View.MeasureSpec.EXACTLY);
    }

    @Override
    public void release() {
        container.setPressed(false);
        if(listener != null){
            listener.pressStateChanged(isPressed(), customView);
        }

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
        if(listener != null){
            listener.pressStateChanged(isPressed(), customView);
        }

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
    public void setAnchor(float x) {

        int left;
        switch (anchor) {
            case ANCHOR_RIGHT:
                left = (int) x - container.getWidth();
                break;
            case ANCHOR_LEFT:
                left = (int) x;
                break;
            default: //ANCHOR_CENTER
                left = (int) x - container.getWidth() / 2;
        }

//        if(left < 0){
//            int l  = container.getLeft();
//            Log.w("SetX", " left: " + left + " layout.left:" + l );
//        }

        //customView.setLeft(left);
        container.layout(left, container.getTop(), left + container.getMeasuredWidth(), container.getTop() + container.getMeasuredHeight());
//        if(left < 0){
//            int l  = container.getLeft();
//            Log.w("SetX", " left: " + left + " layouted.left:" + l );
//        }
        //container.invalidate();
    }


    @Override
    public void setVelocity(float velocity) {

        if (listener != null) {
            listener.onVelocityChanged(velocity, customView);
        }

        //customView.findViewById(R.id.icon).setRotation(degree);
    }


    @Override
    public float getAnchor() {

        float x;
        int left = container.getLeft();
        switch (anchor) {
            case ANCHOR_RIGHT:
                x = container.getRight();
                break;
            case ANCHOR_LEFT:
                x = left;
                break;
            default: //ANCHOR_CENTER
                x = left + container.getWidth() / 2;
        }

        return x;
    }

    String value;

    @Override
    public void setPinValue(float x) {
        String old = getValue();

        String val = null;

        if (listener != null) {
            val = listener.onValueChanged(x, customView);
        }

        val = val == null ? String.valueOf(x) : val;

        if (old == null || old.length() != (val == null ? "" : val).length()) {
            updateLayout();
        }

        this.value = val;
    }


    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void updateLayout() {
        layoutCustomView(layoutWidth, widMode, layoutHeight, heightMode);
    }

    @Override
    public boolean isInTargetZone(float x, float y) {
        boolean res = (x >= container.getLeft()) && x <= container.getRight();
        return res;
    }

    @Override
    public void draw(Canvas canvas) {
//        int left = container.getLeft();
//        Log.w("Draw", "left:" + left);

        canvas.save();
        canvas.translate(container.getLeft(), container.getTop());
//        float de = customView.getRotation();
//        Matrix matrix = new Matrix();
//        //matrix.postRotate(de);
//        matrix.preRotate(de);
//        canvas.concat(matrix);
        super.draw(canvas);
        canvas.restore();
    }

}
