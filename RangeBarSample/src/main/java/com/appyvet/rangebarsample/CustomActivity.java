package com.appyvet.rangebarsample;

import android.app.Activity;
import android.os.Bundle;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.materialrangebar.DefaultPinViewStateChangedListener;
import com.appyvet.materialrangebar.IRangeBarFormatter;
import com.appyvet.materialrangebar.PinViewStateChangedListener;
import com.appyvet.materialrangebar.RangeBar;

/**
 * Created by zhangzhenwei on 2018/1/23.
 */


public class CustomActivity extends Activity {
    final float DEGREE = 45;

    private RangeBar rangebar;



    //keep one num following dot
    protected IRangeBarFormatter formatter = new IRangeBarFormatter() {
        @Override
        public String format(String value) {
            int index = value.indexOf(".");
            if (index > 0 && index < value.length() - 2) {
                value = value.substring(0, index + 2);
            }
            return value;
        }
    };

    PinViewStateChangedListener listenerLeft= new DefaultPinViewStateChangedListener() {
        @Override
        public String onValueChanged(float value, View v) {
            String val = formatter.format(String.valueOf(value));
            TextView t = v.findViewById(R.id.text);
            t.setText("￥" + val);

            return val;
        }

        @Override
        public void layoutUpdated(View v) {
            TextView t = v.findViewById(R.id.text);
            if(v.getLeft() < rangebar.getLeft()){
                int delta = rangebar.getLeft() - v.getLeft();
                t.setTranslationX(delta);
            } else {
                t.setTranslationX(0);
            }
        }

        @Override
        public void onVelocityChanged(float velocity, View view) {
            float degree = velocity / RangeBar.MAX_VELOCITY * DEGREE;
            view.findViewById(R.id.icon).setRotation(degree);
        }

        @Override
        public void pressStateChanged(boolean isPressed, final View view) {
            if(!isPressed){
                SpringAnimation animation = new SpringAnimation(view.findViewById(R.id.icon), DynamicAnimation.ROTATION, 0);
                animation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY).setStiffness(SpringForce.STIFFNESS_LOW);
                animation.start();
            }
        }
    };

    PinViewStateChangedListener listenerRight = new DefaultPinViewStateChangedListener() {
        @Override
        public String onValueChanged(float value, View v) {
            String val = formatter.format(String.valueOf(value));
            TextView t = v.findViewById(R.id.text);
            t.setText("￥" + val);
            t.setTranslationX(0);
            return val;
        }

        @Override
        public void layoutUpdated(View v) {
            TextView t = v.findViewById(R.id.text);
            if(v.getRight() > rangebar.getRight()){
                int delta = v.getRight() - rangebar.getRight();
                t.setTranslationX(-delta);
            } else {
                t.setTranslationX(0);
            }
        }

        @Override
        public void onVelocityChanged(float velocity, View view) {
            float degree = velocity / RangeBar.MAX_VELOCITY * DEGREE;
            view.findViewById(R.id.icon).setRotation(degree);
        }

        @Override
        public void pressStateChanged(boolean isPressed, final View view) {
            if(!isPressed){
                SpringAnimation animation = new SpringAnimation(view.findViewById(R.id.icon), DynamicAnimation.ROTATION, 0);
                animation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY).setStiffness(SpringForce.STIFFNESS_LOW);
                animation.start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom);

        final TextView indexButton = findViewById(R.id.setIndex);
        final TextView valueButton = findViewById(R.id.setValue);

        rangebar = findViewById(R.id.rangebar1);

        final int left = R.layout.tag_selector_price_left;
        final int right = R.layout.tag_selector_price_right;

        rangebar.setCustomSelector(
                left, RangeBar.ANCHOR_RIGHT, listenerLeft,
                right, RangeBar.ANCHOR_LEFT, listenerRight);

        rangebar.setPinTextFormatter(new RangeBar.PinTextFormatter() {
            @Override
            public String getText(String value) {
                int index = value.indexOf(".");
                if (index > 0 && index < value.length() - 2) {
                    value = value.substring(0, index + 2);
                }
                return value;
            }
        });

        rangebar.setTickConfig(5, 2000, 10);
//        rangebar.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                rangebar.setRangePinsByValue(5.0f, 2000.f);
//            }
//        }, 2000);


        // Setting Index Values -------------------------------
        final EditText leftIndexValue = findViewById(R.id.leftIndexValue);
        final EditText rightIndexValue = findViewById(R.id.rightIndexValue);

        // Sets the display values of the indices
        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, boolean drawTicks, int leftPinIndex,
                                              int rightPinIndex, float leftPinValue, float rightPinValue) {
                leftIndexValue.setText("" + leftPinIndex);
                rightIndexValue.setText("" + rightPinIndex);

                Toast.makeText(getApplication(), " left " + leftPinValue + " right:" + rightPinValue, Toast.LENGTH_SHORT).show();
            }
        });




        indexButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String leftIndex = leftIndexValue.getText().toString();
                String rightIndex = rightIndexValue.getText().toString();

                try {
                    if (!leftIndex.isEmpty() && !rightIndex.isEmpty()) {
                        int leftIntIndex = Integer.parseInt(leftIndex);
                        int rightIntIndex = Integer.parseInt(rightIndex);
                        rangebar.setRangePinsByTickIndex(leftIntIndex, rightIntIndex);
                    }
                } catch (IllegalArgumentException e) {
                }
            }
        });




        final TextView barWeight = findViewById(R.id.barWeight);
        SeekBar barWeightSeek = findViewById(R.id.barWeightSeek);
        barWeightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar barWeightSeek, int progress, boolean fromUser) {
                rangebar.setBarWeight(getValueInDP(progress));
                barWeight.setText("barWeight = " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final TextView connectingLineWeight = findViewById(R.id.connectingLineWeight);
        SeekBar connectingLineWeightSeek = findViewById(R.id.connectingLineWeightSeek);
        connectingLineWeightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar connectingLineWeightSeek, int progress,
                                          boolean fromUser) {
                rangebar.setConnectingLineWeight(getValueInDP(progress));
                connectingLineWeight.setText("connectingLineWeight = " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private int getValueInDP(int value) {
        int valueInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics());
        return valueInDp;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

