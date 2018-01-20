package com.appyvet.materialrangebar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import static android.view.View.MeasureSpec.UNSPECIFIED;

/**
 * Created by zhangzhenwei on 2018/1/19.
 */

public class CustomPinView  extends PinView {
    View customView;
    boolean isPressed;

    public CustomPinView(Context context, View cv) {
        super(context);
        this.customView = cv;
//        TextView view  = new TextView(context);
//        view.setText("TE");
//        view.setBackgroundColor(Color.BLUE);
//        this.customView = view;
        this.customView.measure(MeasureSpec.makeMeasureSpec(1000, UNSPECIFIED),MeasureSpec.makeMeasureSpec(1000, UNSPECIFIED));
        this.customView.layout(100, 200, 100 + customView.getMeasuredWidth(), 100 + customView.getMeasuredHeight());
    }

    @Override
    public void setFormatter(IRangeBarFormatter mFormatter) {

    }

    @Override
    public void setSize(float size, float padding) {
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
    public boolean isPressed(){
        return  isPressed;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        int left = (int)x;
        customView.layout(left,120, left + customView.getMeasuredWidth(), 120 + customView.getMeasuredHeight());
        customView.invalidate();
    }

    @Override
    public float getX() {
        float x =  super.getX();
        Log.w("getx", "x :" + x);
        return x;
    }


    @Override
    public void setXValue(String x) {

    }

    @Override
    public boolean isInTargetZone(float x, float y) {
        boolean res =  (Math.abs(x - customView.getX()) <= customView.getMeasuredWidth()
                && Math.abs(y - customView.getY() ) <= customView.getMeasuredHeight());
        return res;
    }

    @Override
    public void draw(Canvas canvas){
        canvas.save();
        canvas.translate(customView.getLeft(), customView.getTop());
        customView.draw(canvas);
        super.draw(canvas);
        canvas.restore();
    }
}
