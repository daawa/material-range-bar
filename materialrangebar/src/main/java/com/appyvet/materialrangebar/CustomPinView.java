package com.appyvet.materialrangebar;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.TextView;

import static android.view.View.MeasureSpec.UNSPECIFIED;

/**
 * Created by zhangzhenwei on 2018/1/19.
 */

public class CustomPinView  extends AbstractPinView {
    View customView;
    public CustomPinView(Context context, View customView) {
        super(context);
        //this.customView = customView;
        TextView view  = new TextView(context);
        view.setText("TEst");
        this.customView = view;
        customView.measure(MeasureSpec.makeMeasureSpec(0, UNSPECIFIED),MeasureSpec.makeMeasureSpec(0, UNSPECIFIED));
    }

    @Override
    public void setFormatter(IRangeBarFormatter mFormatter) {

    }

    @Override
    public void setSize(float size, float padding) {

    }

    @Override
    public void release() {

    }

    @Override
    public void press() {

    }

    @Override
    public void setXValue(String x) {

    }

    @Override
    public boolean isInTargetZone(float x, float y) {
        return (Math.abs(x - getX()) <= getWidth()
                && Math.abs(y - getY() ) <= getHeight());
    }

    @Override
    public void draw(Canvas canvas){
        //todo:
        customView.draw(canvas);

        super.draw(canvas);
    }
}
