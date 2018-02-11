package com.appyvet.rangebarsample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.materialrangebar.DefaultPinViewStateChangedListener;
import com.appyvet.materialrangebar.IRangeBarFormatter;
import com.appyvet.materialrangebar.PinView;
import com.appyvet.materialrangebar.PinViewStateChangedListener;
import com.appyvet.materialrangebar.RangeBar;

/**
 * Created by zhangzhenwei on 2018/1/23.
 */


public class CustomActivity extends Activity {
    final float DEGREE = 45;
    final float MAX_VELOCIYT = 2000;

    // Sets the initial values such that the image will be drawn
    private static final int INDIGO_500 = 0xff3f51b5;

    // Sets variables to save the colors of each attribute
    private int mBarColor;

    private int mConnectingLineColor;

    private int mPinColor;
    private int mTextColor;

    private int mTickColor;

    // Initializes the RangeBar in the application
    private RangeBar rangebar;

    private int mSelectorColor;

    private int mSelectorBoundaryColor;

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

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("BAR_COLOR", mBarColor);
        bundle.putInt("CONNECTING_LINE_COLOR", mConnectingLineColor);
    }

    PinViewStateChangedListener listener = new DefaultPinViewStateChangedListener() {
        float velocity;
        @Override
        public String onValueChanged(float value, View view) {
            String val = formatter.format(String.valueOf(value));
            TextView t = view.findViewById(R.id.text);
            t.setText("$" + val);
            return val;
        }

        @Override
        public void onVelocityChanged(float velocity, View view) {
            float degree = velocity / MAX_VELOCIYT * DEGREE;
            view.findViewById(R.id.icon).setRotation(degree);
            this.velocity = velocity;
        }

        @Override
        public void pressStateChanged(boolean isPressed, final View view) {
            if(!isPressed){
                //view.findViewById(R.id.icon).setRotation(0);
                SpringAnimation animation = new SpringAnimation(view.findViewById(R.id.icon), DynamicAnimation.ROTATION, 0);
                animation.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY).setStiffness(SpringForce.STIFFNESS_VERY_LOW);
                animation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                    @Override
                    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                        Log.w("Spring", "value:" + value + " velocity:" + velocity);
                        float rot = view.findViewById(R.id.icon).getRotation();
                        Log.w("Spring"," view rot:" + rot);
                        rangebar.invalidate();
                    }
                });
                animation.start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Removes title bar and sets content view
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom);


        final TextView indexButton = findViewById(R.id.setIndex);
        final TextView valueButton = findViewById(R.id.setValue);
        final TextView rangeButton = findViewById(R.id.enableRange);
        final TextView disabledButton = findViewById(R.id.disable);

        rangebar = findViewById(R.id.rangebar1);

        //ViewGroup group = new FrameLayout(this);

        final int left = R.layout.tag_selector_price_left;
        final int right = R.layout.tag_selector_price_right;

//        right.setBackgroundColor(Color.CYAN:i);
//        left.setBackgroundColor(Color.parseColor("#a0aa0000"));
        rangebar.setCustomSelector(
                left, RangeBar.ANCHOR_RIGHT, listener,
                right, RangeBar.ANCHOR_LEFT, listener);

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
        //rangebar.setDrawTicks(true);
        //rangebar.setTemporaryPinsSizeRatio(1.5f);
        //rangebar.setTemporaryPins(false);

//        rangebar.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                rangebar.setRangePinsByValue(200, 1200);
//            }
//        }, 3000);


        rangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rangebar.setRangeBarEnabled(!rangebar.isRangeBar());
            }
        });
        disabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rangebar.setEnabled(!rangebar.isEnabled());
            }
        });

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

        // Sets the indices by values based upon input from the user
        valueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Gets the String values of all the texts
                String leftValue = leftIndexValue.getText().toString();
                String rightValue = rightIndexValue.getText().toString();

                // Catches any IllegalArgumentExceptions; if fails, should throw
                // a dialog warning the user
                try {
                    if (!leftValue.isEmpty() && !rightValue.isEmpty()) {
                        float leftIntIndex = Float.parseFloat(leftValue);
                        float rightIntIndex = Float.parseFloat(rightValue);
                        rangebar.setRangePinsByValue(leftIntIndex, rightIntIndex);
                    }
                } catch (IllegalArgumentException e) {
                }
            }
        });

        // Setting Number Attributes -------------------------------

        // Sets tickStart
        final TextView tickStart = findViewById(R.id.tickStart);
        SeekBar tickStartSeek = findViewById(R.id.tickStartSeek);
        tickStartSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar tickCountSeek, int progress, boolean fromUser) {
                try {
                    rangebar.setTickConfig(progress, rangebar.getTickEnd(), rangebar.getTickCount());
                } catch (IllegalArgumentException e) {
                }
                tickStart.setText("tickStart = " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        final TextView tickEnd = findViewById(R.id.tickEnd);
        SeekBar tickEndSeek = findViewById(R.id.tickEndSeek);
        tickEndSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar tickCountSeek, int progress, boolean fromUser) {
                try {
                    rangebar.setTickConfig(rangebar.getTickStart(), progress, rangebar.getTickCount());
                } catch (IllegalArgumentException e) {
                }
                tickEnd.setText("tickEnd = " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final TextView tickCount = findViewById(R.id.tickInterval);
        SeekBar tickIntervalSeek = findViewById(R.id.tickIntervalSeek);
        tickIntervalSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar tickCountSeek, int progress, boolean fromUser) {
                try {
                    rangebar.setTickConfig(rangebar.getTickStart(), rangebar.getTickEnd(), progress);
                } catch (IllegalArgumentException e) {
                }
                tickCount.setText("tickCount = " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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

        // Sets connectingLineWeight
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

