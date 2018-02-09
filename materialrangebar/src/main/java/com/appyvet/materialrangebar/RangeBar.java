/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package com.appyvet.materialrangebar;
/*
 * Copyright 2015, Appyvet, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * The MaterialRangeBar is a single or double-sided version of a {@link android.widget.SeekBar}
 * with discrete values. Whereas the thumb for the SeekBar can be dragged to any
 * position in the bar, the RangeBar only allows its thumbs to be dragged to
 * discrete positions (denoted by tick marks) in the bar. When released, a
 * RangeBar thumb will snap to the nearest tick mark.
 * This version is forked from edmodo range bar
 * https://github.com/edmodo/range-bar.git
 * Clients of the RangeBar can attach a
 * {@link OnRangeBarChangeListener} to be notified when the pins
 * have
 * been moved.
 */
public class RangeBar extends View {

    public static final int ANCHOR_CENTER = 0x01;
    public static final int ANCHOR_LEFT = ANCHOR_CENTER << 1;
    public static final int ANCHOR_RIGHT = ANCHOR_CENTER << 2;

    // Member Variables ////////////////////////////////////////////////////////

    private static final String TAG = "RangeBar";

    // Default values for variables
    private static final int DEFAULT_TICK_START = 0;

    private static final int DEFAULT_TICK_END = 5;

    //    private static final int DEFAULT_TICK_INTERVAL = 1;
    private static final int DEFAULT_TICK_COUNT = 10;

    private static final int DEFAULT_TICK_HEIGHT_DP = 1;

    private static final int DEFAULT_BAR_WEIGHT_DP = 2;

    private static final int DEFAULT_CIRCLE_BOUNDARY_SIZE_DP = 0;

    private static final int DEFAULT_BAR_COLOR = Color.LTGRAY;

    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private static final int DEFAULT_TICK_COLOR = Color.BLACK;

    // Corresponds to material indigo 500.
    private static final int DEFAULT_PIN_COLOR = 0xff3f51b5;

    private static final int DEFAULT_CONNECTING_LINE_WEIGHT_DP = 4;

    // Corresponds to material indigo 500.
    private static final int DEFAULT_CONNECTING_LINE_COLOR = 0xff3f51b5;


    private static final int DEFAULT_CIRCLE_SIZE = 16;

    private static final int DEFAULT_BAR_PADDING_BOTTOM_DP = 24;

    // Instance variables for all of the customizable attributes

    private int mTickHeight = DEFAULT_TICK_HEIGHT_DP;

    private float mTickStart = DEFAULT_TICK_START;

    private float mTickEnd = DEFAULT_TICK_END;

    //private int mTickInterval = DEFAULT_TICK_INTERVAL;

    private int mBarWeight = DEFAULT_BAR_WEIGHT_DP;

    private int mBarColor = DEFAULT_BAR_COLOR;

    private int mPinColor = DEFAULT_PIN_COLOR;

    private int mTextColor = DEFAULT_TEXT_COLOR;

    private int mConnectingLineWeight = DEFAULT_CONNECTING_LINE_WEIGHT_DP;

    private int mConnectingLineColor = DEFAULT_CONNECTING_LINE_COLOR;
    private int mConnectingLineColorStart = mConnectingLineColor;
    private int mConnectingLineColorEnd = mConnectingLineColor;


    private int mTickColor = DEFAULT_TICK_COLOR;


    private int mCircleColor = DEFAULT_CONNECTING_LINE_COLOR;

    private int mCircleBoundaryColor = DEFAULT_CONNECTING_LINE_COLOR;

    private int mCircleBoundarySize = DEFAULT_CIRCLE_BOUNDARY_SIZE_DP;


    private int defaultCircleSize = DEFAULT_CIRCLE_SIZE;

    // setTickCount only resets indices before a thumb has been pressed or a
    // setThumbIndices() is called, to correspond with intended usage
    //private boolean mFirstSetTickCount = true;

    private final DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();

    private int mDefaultWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, mDisplayMetrics);

    private int mDefaultHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, mDisplayMetrics);

    private int mTickCount = DEFAULT_TICK_COUNT;

    private PinView mLeftThumb;

    private PinView mRightThumb;

    private Bar mBar;

    private ConnectingLine mConnectingLine;

    private OnRangeBarChangeListener mListener;

    private OnRangeBarTextListener mPinTextListener;

    private SparseArray<Float> mTickMap;

    private float mLeftPos;

    private float mRightPos;

    private boolean mIsRangeBar = true;


    private int mBarPaddingBottom = DEFAULT_BAR_PADDING_BOTTOM_DP;

    private int mActiveConnectingLineColor;

    private int mActiveBarColor;

    private int mActiveTickColor;

    private int mActiveCircleColor;

    //Used for ignoring vertical moves
    private int mDiffX;

    private int mDiffY;

    private int mLastX;

    private int mLastY;

    private IRangeBarFormatter mFormatter;

    private boolean drawTicks = false;

    private boolean mArePinsTemporary = true;

    private PinTextFormatter mPinTextFormatter = new PinTextFormatter() {
        @Override
        public String getText(String value) {
            if (value.length() > 4) {
                return value.substring(0, 4);
            } else {
                return value;
            }
        }
    };

    // Constructors ////////////////////////////////////////////////////////////

    public RangeBar(Context context) {
        super(context);
    }

    public RangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        rangeBarInit(context, attrs);
    }

    public RangeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        rangeBarInit(context, attrs);
    }

    // View Methods ////////////////////////////////////////////////////////////

    @Override
    public Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();

        bundle.putParcelable("instanceState", super.onSaveInstanceState());

        bundle.putInt("TICK_COUNT", mTickCount);
        bundle.putFloat("TICK_START", mTickStart);
        bundle.putFloat("TICK_END", mTickEnd);
        bundle.putInt("TICK_COLOR", mTickColor);

        bundle.putInt("TICK_HEIGHT_DP", mTickHeight);
        bundle.putInt("BAR_WEIGHT", mBarWeight);
        bundle.putInt("BAR_COLOR", mBarColor);
        bundle.putInt("CONNECTING_LINE_WEIGHT", mConnectingLineWeight);
        bundle.putInt("CONNECTING_LINE_COLOR", mConnectingLineColor);

        bundle.putInt("CIRCLE_COLOR", mCircleColor);
        bundle.putInt("CIRCLE_BOUNDARY_COLOR", mCircleBoundaryColor);
        bundle.putInt("CIRCLE_BOUNDARY_WIDTH", mCircleBoundarySize);
        bundle.putInt("BAR_PADDING_BOTTOM", mBarPaddingBottom);
        bundle.putBoolean("IS_RANGE_BAR", mIsRangeBar);
        bundle.putBoolean("ARE_PINS_TEMPORARY", mArePinsTemporary);
        bundle.putFloat("LEFT_INDEX", mLeftPos);
        bundle.putFloat("RIGHT_INDEX", mRightPos);

        //bundle.putBoolean("FIRST_SET_TICK_COUNT", mFirstSetTickCount);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {

            Bundle bundle = (Bundle) state;

            mTickCount = bundle.getInt("TICK_COUNT");
            mTickStart = bundle.getFloat("TICK_START");
            mTickEnd = bundle.getFloat("TICK_END");
            mTickColor = bundle.getInt("TICK_COLOR");
            mTickHeight = bundle.getInt("TICK_HEIGHT_DP");
            mBarWeight = bundle.getInt("BAR_WEIGHT");
            mBarColor = bundle.getInt("BAR_COLOR");
            defaultCircleSize = bundle.getInt("CIRCLE_SIZE");
            mCircleColor = bundle.getInt("CIRCLE_COLOR");
            mCircleBoundaryColor = bundle.getInt("CIRCLE_BOUNDARY_COLOR");
            mCircleBoundarySize = bundle.getInt("CIRCLE_BOUNDARY_WIDTH");
            mConnectingLineWeight = bundle.getInt("CONNECTING_LINE_WEIGHT");
            mConnectingLineColor = bundle.getInt("CONNECTING_LINE_COLOR");

            mBarPaddingBottom = bundle.getInt("BAR_PADDING_BOTTOM");
            mIsRangeBar = bundle.getBoolean("IS_RANGE_BAR");
            mArePinsTemporary = bundle.getBoolean("ARE_PINS_TEMPORARY");

            mLeftPos = bundle.getFloat("LEFT_INDEX");
            mRightPos = bundle.getFloat("RIGHT_INDEX");
            //mFirstSetTickCount = bundle.getBoolean("FIRST_SET_TICK_COUNT");
            adjustPin();
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));

        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width;
        int height;

        // Get measureSpec mode and size values.
        final int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        // The RangeBar width should be as large as possible.
        if (measureWidthMode == MeasureSpec.AT_MOST) {
            width = measureWidth;
        } else if (measureWidthMode == MeasureSpec.EXACTLY) {
            width = measureWidth;
        } else {
            width = mDefaultWidth;
        }

        // The RangeBar height should be as small as possible.
        if (measureHeightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mDefaultHeight, measureHeight);
        } else if (measureHeightMode == MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            height = mDefaultHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final Context ctx = getContext();
        final int barYPos = getYPos();

        if (mLeftPos <= 0) {
            mLeftPos = getMarginLeft();
        }
        if (mRightPos <= 0) {
            mRightPos = getMarginLeft() + getBarLength();
        }

        if (mIsRangeBar) {
            if (customLeftThumb != null) {
                if (!(mLeftThumb instanceof CustomPinView)) {
                    mLeftThumb = new CustomPinView(customLeftThumb, leftAnchor, leftValListener, this);
                }
                mLeftThumb.updateLayout();
            } else {
                mLeftThumb = new DefaultPinView(ctx);
                mLeftThumb.setFormatter(mFormatter);
                ((DefaultPinView) mLeftThumb).init(this, barYPos, mPinColor, mTextColor, defaultCircleSize,
                        mCircleColor, mCircleBoundaryColor, mCircleBoundarySize, mArePinsTemporary);
            }
        }

        if (customRightThumb != null) {
            if (!(mRightThumb instanceof CustomPinView)) {
                mRightThumb = new CustomPinView(customRightThumb, rightAnchor, rightValListener, this);
            }
            mRightThumb.updateLayout();
        } else {
            mRightThumb = new DefaultPinView(ctx);
            mRightThumb.setFormatter(mFormatter);
            ((DefaultPinView) mRightThumb).init(this, barYPos, mPinColor, mTextColor, defaultCircleSize,
                    mCircleColor, mCircleBoundaryColor, mCircleBoundarySize, mArePinsTemporary);
        }

        // Create the underlying bar.
        final int marginLeft = getMarginLeft();
        final int barLength = getBarLength();

        mBar = new Bar(ctx, marginLeft, barYPos, barLength, mTickCount, mTickHeight, mTickColor,
                mBarWeight, mBarColor);

        float oldLeftPos = mLeftPos, oldRightPos = mRightPos;
        int leftTick = 0, rightTick = 0;
        // Initialize thumbs to the desired indices
        if (mIsRangeBar) {
            if (drawTicks) {
                leftTick = findTick4Pos(mLeftPos);
                mLeftPos = findPos4Tick(leftTick);
            }
            mLeftThumb.setX(mLeftPos);
            mLeftThumb.setPinValue(getLeftPinValue());
        }
        if (drawTicks) {
            rightTick = findTick4Pos(mRightPos);
            mRightPos = findPos4Tick(rightTick);
        }

        mRightThumb.setX(mRightPos);
        mRightThumb.setPinValue(getRightPinValue());


        // Call the listener.
        if (oldLeftPos != mLeftPos || oldRightPos != mRightPos) {
            if (mListener != null) {
                float left = drawTicks ? leftTick : mLeftPos;
                float right = drawTicks ? rightTick : mRightPos;
                mListener.onRangeChangeListener(this, drawTicks,
                        (int) left, (int) right,
                        getLeftPinValue(),
                        getRightPinValue());
            }
        }

        // Create the line connecting the two thumbs.
        mConnectingLine = new ConnectingLine(ctx, barYPos, mConnectingLineWeight,
                mConnectingLineColorStart, mConnectingLineColorEnd);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        mBar.draw(canvas);
        if (mIsRangeBar) {
            mConnectingLine.draw(canvas, mLeftThumb, mRightThumb);
            if (drawTicks) {
                mBar.drawTicks(canvas);
            }
            mLeftThumb.draw(canvas);
        } else {
            mConnectingLine.draw(canvas, getMarginLeft(), mRightThumb);
            if (drawTicks) {
                mBar.drawTicks(canvas);
            }
        }
        mRightThumb.draw(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // If this View is not enabled, don't allow for touch interactions.
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mDiffX = 0;
                mDiffY = 0;

                mLastX = (int) event.getX();
                mLastY = (int) event.getY();
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX());
                this.getParent().requestDisallowInterceptTouchEvent(true);
                final float curX = event.getX();
                final float curY = event.getY();
                mDiffX += Math.abs(curX - mLastX);
                mDiffY += Math.abs(curY - mLastY);
                mLastX = (int) curX;
                mLastY = (int) curY;

                if (mDiffX < mDiffY) {
                    //vertical touch
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                } else {
                    //horizontal touch (do nothing as it is needed for RangeBar)
                }
                return true;

            default:
                return false;
        }
    }

    // Public Methods //////////////////////////////////////////////////////////

    /**
     * Sets a listener to receive notifications of changes to the RangeBar. This
     * will overwrite any existing set listeners.
     *
     * @param listener the RangeBar notification listener; null to remove any
     *                 existing listener
     */
    public void setOnRangeBarChangeListener(OnRangeBarChangeListener listener) {
        mListener = listener;
    }

    /**
     * Sets a listener to modify the text
     *
     * @param mPinTextListener the RangeBar pin text notification listener; null to remove any
     *                         existing listener
     */
    public void setPinTextListener(OnRangeBarTextListener mPinTextListener) {
        this.mPinTextListener = mPinTextListener;
    }


    public void setFormatter(IRangeBarFormatter formatter) {
        if (mLeftThumb != null) {
            mLeftThumb.setFormatter(formatter);
        }

        if (mRightThumb != null) {
            mRightThumb.setFormatter(formatter);
        }

        mFormatter = formatter;
    }

    public void setDrawTicks(boolean drawTicks) {
        if (this.drawTicks != drawTicks) {
            this.drawTicks = drawTicks;
            if (drawTicks && mTickCount < 2) {
                throw new IllegalArgumentException("if set drawTicks, tick count must be greater than 1");
            }
        }
    }

    public void setTickConfig(float start, float end, int count) {

        int tickCount = count;
        if (isValidTickCount(tickCount)) {
            mTickCount = tickCount;
            mTickStart = start;
            mTickEnd = end;
            mTickMap.clear();
            createBar();
            createPins();
        } else {
            Log.e(TAG, "tickCount less than 2; invalid tickCount.");
            throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
        }
    }

    /**
     * Sets the height of the ticks in the range bar.
     *
     * @param tickHeight Float specifying the height of each tick mark in dp.
     */
    public void setTickHeight(int tickHeight) {

        mTickHeight = tickHeight;
        createBar();
    }

    /**
     * Set the weight of the bar line and the tick lines in the range bar.
     *
     * @param barWeight Float specifying the weight of the bar and tick lines in
     *                  DP.
     */
    public void setBarWeight(int barWeight) {
        mBarWeight = barWeight;
        createBar();
    }

    /**
     * Set the color of the bar line and the tick lines in the range bar.
     *
     * @param barColor Integer specifying the color of the bar line.
     */
    public void setBarColor(int barColor) {
        mBarColor = barColor;
        createBar();
    }

    /**
     * Set the color of the pins.
     *
     * @param pinColor Integer specifying the color of the pin.
     */
    public void setPinColor(int pinColor) {
        mPinColor = pinColor;
        createPins();
    }

    /**
     * Set the color of the text within the pin.
     *
     * @param textColor Integer specifying the color of the text in the pin.
     */
    public void setPinTextColor(int textColor) {
        mTextColor = textColor;
        createPins();
    }

    /**
     * Set if the view is a range bar or a seek bar.
     *
     * @param isRangeBar Boolean - true sets it to rangebar, false to seekbar.
     */
    public void setRangeBarEnabled(boolean isRangeBar) {
        mIsRangeBar = isRangeBar;
        invalidate();
    }


    /**
     * Set the color of the ticks.
     *
     * @param tickColor Integer specifying the color of the ticks.
     */
    public void setTickColor(int tickColor) {

        mTickColor = tickColor;
        createBar();
    }

    /**
     * Set the color of the selector.
     *
     * @param selectorColor Integer specifying the color of the ticks.
     */
    public void setSelectorColor(int selectorColor) {
        mCircleColor = selectorColor;
        createPins();
    }

    /**
     * Set the color of the selector Boundary.
     *
     * @param selectorBoundaryColor Integer specifying the boundary color of the ticks.
     */
    public void setSelectorBoundaryColor(int selectorBoundaryColor) {
        mCircleBoundaryColor = selectorBoundaryColor;
        createPins();
    }

    /**
     * Set the size of the selector Boundary.
     *
     * @param selectorBoundarySize Integer specifying the boundary size of ticks.
     *                             Value should be in DP
     */
    public void setSelectorBoundarySize(int selectorBoundarySize) {
        mCircleBoundarySize = selectorBoundarySize;
        createPins();
    }

    /**
     * Set the weight of the connecting line between the thumbs.
     *
     * @param connectingLineWeight Float specifying the weight of the connecting
     *                             line. Value should be in DP
     */
    public void setConnectingLineWeight(int connectingLineWeight) {

        mConnectingLineWeight = connectingLineWeight;
        createConnectingLine();
    }

    /**
     * Set the color of the connecting line between the thumbs.
     *
     * @param connectingLineColor Integer specifying the color of the connecting
     *                            line.
     */
    public void setConnectingLineColor(int connectingLineColor) {

        mConnectingLineColor = connectingLineColor;
        createConnectingLine();
    }

    /**
     * Gets the start tick.
     *
     * @return the start tick.
     */
    public float getTickStart() {
        return mTickStart;
    }

    /**
     * Gets the end tick.
     *
     * @return the end tick.
     */
    public float getTickEnd() {
        return mTickEnd;
    }

    /**
     * Gets the tick count.
     *
     * @return the tick count
     */
    public int getTickCount() {
        return mTickCount;
    }

    /**
     * Sets the location of the pins according by the supplied index.
     * Numbered from 0 to mTickCount - 1 from the left.
     *
     * @param leftPinIndex  Integer specifying the index of the left pin
     * @param rightPinIndex Integer specifying the index of the right pin
     */
    public void setRangePinsByTickIndex(int leftPinIndex, int rightPinIndex) {

        if (!drawTicks || tickIndexOutOfRange(leftPinIndex, rightPinIndex)) {
            String msg = "drawTicks = " + drawTicks + " or " + "Pin index left " + leftPinIndex + ", or right " + rightPinIndex + " is out of bounds. Check that it is greater than the minimum (" + mTickStart + ") and less than the maximum value (" + mTickEnd + ")";
            Log.e(TAG, msg);
            throw new IllegalArgumentException(msg);

        } else {
            mLeftPos = findPos4Tick(leftPinIndex);
            mRightPos = findPos4Tick(rightPinIndex);

            createPins();

            if (mListener != null) {
                mListener.onRangeChangeListener(this, drawTicks,
                        leftPinIndex, rightPinIndex,
                        getLeftPinValue(), getRightPinValue());
            }
        }

        invalidate();
        requestLayout();
    }

    private int findPos4Tick(int tickIndex) {
        int len = getBarLength();
        int pos = (int) ((tickIndex * 1.0f) / (mTickCount - 1) * len);
        if (pos > len) pos = len;

        return pos + getMarginLeft();
    }

    //todo: optimize
    private int findTick4Pos(float pos) {
        int len = getBarLength();
        pos -= getMarginLeft();
        int seg = mTickCount - 1;
        for (int i = 0; i < mTickCount; i++) {
            float lt = i * 1.f / seg * len;
            float rt = (i + 1) * 1.f / seg * len;
            if (pos >= lt && pos <= rt) {
                float ld = pos - lt, rd = rt - pos;
                if (ld < rd) return i;
                else return i + 1;
            }
        }

        return 0;
    }

    /**
     * Sets the location of pin according by the supplied index.
     * Numbered from 0 to mTickCount - 1 from the left.
     *
     * @param pinIndex Integer specifying the index of the seek pin
     */
    public void setSeekPinByIndex(int pinIndex) {
        if (!drawTicks || pinIndex < 0 || pinIndex > mTickCount) {
            String msg = "drawTicks = " + drawTicks + "Pin index " + pinIndex + " is out of bounds. Check that it is greater than the minimum (" + 0 + ") and less than the maximum value (" + mTickCount + ")";
            Log.e(TAG, msg);
            throw new IllegalArgumentException(msg);

        } else {

            mRightPos = findPos4Tick(pinIndex);
            createPins();

            if (mListener != null) {
                mListener.onRangeChangeListener(this, drawTicks,
                        getLeftTickIndex(), pinIndex,
                        getLeftPinValue(), getRightPinValue());
            }
        }
        invalidate();
        requestLayout();
    }

    /**
     * Sets the location of pins according by the supplied values.
     *
     * @param leftPinValue  Float specifying the index of the left pin
     * @param rightPinValue Float specifying the index of the right pin
     */
    public void setRangePinsByValue(float leftPinValue, float rightPinValue) {
        if (valueOutOfRange(leftPinValue, rightPinValue)) {
            Log.e(TAG,
                    "Pin value left " + leftPinValue + ", or right " + rightPinValue
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + mTickStart + ") and less than the maximum value ("
                            + mTickEnd + ")");
            throw new IllegalArgumentException(
                    "Pin value left " + leftPinValue + ", or right " + rightPinValue
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + mTickStart + ") and less than the maximum value ("
                            + mTickEnd + ")");
        } else {

            mLeftPos = getPosition4Val(leftPinValue);
            mRightPos = getPosition4Val(rightPinValue);

            adjustPin();
        }
        invalidate();
        requestLayout();
    }

    private void adjustPin() {
        float left = mLeftPos, right = mRightPos;
        if (drawTicks) {
            left = findTick4Pos(mLeftPos);
            right = findTick4Pos(mRightPos);
            mLeftPos = findPos4Tick((int) left);
            mRightPos = findTick4Pos((int) right);
        }

        createPins();

        if (mListener != null) {
            mListener.onRangeChangeListener(this, drawTicks,
                    (int) left, (int) right,
                    getLeftPinValue(),
                    getRightPinValue());
        }
    }

    private float getPosition4Val(float val) {
        if (val < mTickStart) val = mTickStart;

        float pos = ((val - mTickStart) / (mTickEnd - mTickStart) * getBarLength()) + getMarginLeft();

        if (drawTicks) {
            int tick = findTick4Pos(pos);
            pos = findPos4Tick(tick);
        }
        return pos;
    }

    /**
     * Sets the location of pin according by the supplied value.
     *
     * @param pinValue Float specifying the value of the pin
     */
    public void setSeekPinByValue(float pinValue) {
        if (pinValue > mTickEnd || pinValue < mTickStart) {
            String msg = "Pin value " + pinValue + " is out of bounds. Check that it is greater than the minimum (" + mTickStart + ") and less than the maximum value (" + mTickEnd + ")";
            Log.e(TAG, msg);
            throw new IllegalArgumentException(msg);
        } else {
            adjustPin();
        }
        invalidate();
        requestLayout();
    }

    /**
     * Gets the type of the bar.
     *
     * @return true if rangebar, false if seekbar.
     */
    public boolean isRangeBar() {
        return mIsRangeBar;
    }

    /**
     * Gets the value of the left pin.
     *
     * @return the string value of the left pin.
     */
    public float getLeftPinValue() {
        float val = getPinValue(mLeftPos);

        if (val < 0.1f) {
            Log.w("val", "val:" + val);
        }

        return val;
    }

    public String getLeftStringPinValue() {
        float val = getPinValue(mLeftPos);

        if (val < 0.1f) {
            Log.w("val", "val:" + val);
        }

        return mPinTextFormatter.getText(String.valueOf(val));
    }

    /**
     * Gets the value of the right pin.
     *
     * @return the string value of the right pin.
     */
    public float getRightPinValue() {
        return getPinValue(mRightPos);
        //return mPinTextFormatter.getText(String.valueOf(val));
    }

    public String getRightStringPinValue() {
        float val = getPinValue(mRightPos);
        return mPinTextFormatter.getText(String.valueOf(val));
    }

    /**
     * Gets the index of the left-most pin.
     *
     * @return the 0-based index of the left pin
     */
    public int getLeftTickIndex() {
        return findTick4Pos(mLeftPos);
    }

    /**
     * Gets the index of the right-most pin.
     *
     * @return the 0-based index of the right pin
     */
    public int getRightTickIndex() {
        return findTick4Pos(mRightPos);
    }


    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            mBarColor = DEFAULT_BAR_COLOR;
            mConnectingLineColor = DEFAULT_BAR_COLOR;
            mCircleColor = DEFAULT_BAR_COLOR;
            mTickColor = DEFAULT_BAR_COLOR;
        } else {
            mBarColor = mActiveBarColor;
            mConnectingLineColor = mActiveConnectingLineColor;
            mCircleColor = mActiveCircleColor;
            mTickColor = mActiveTickColor;
        }

        createBar();
        createPins();
        createConnectingLine();
        super.setEnabled(enabled);
    }


    public void setPinTextFormatter(PinTextFormatter pinTextFormatter) {
        this.mPinTextFormatter = pinTextFormatter;
    }

    // Private Methods /////////////////////////////////////////////////////////

    /**
     * Does all the functions of the constructor for RangeBar. Called by both
     * RangeBar constructors in lieu of copying the code for each constructor.
     *
     * @param context Context from the constructor.
     * @param attrs   AttributeSet from the constructor.
     */
    private void rangeBarInit(Context context, AttributeSet attrs) {

        if (mTickMap == null) {
            mTickMap = new SparseArray<>();
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RangeBar, 0, 0);

        try {
            final float tickStart = ta.getFloat(R.styleable.RangeBar_mrb_tickStart, DEFAULT_TICK_START);
            final float tickEnd = ta.getFloat(R.styleable.RangeBar_mrb_tickEnd, DEFAULT_TICK_END);
            final int tickCount = ta.getInt(R.styleable.RangeBar_mrb_tickCount, DEFAULT_TICK_COUNT);
            setDrawTicks(ta.getBoolean(R.styleable.RangeBar_mrb_drawTicks, false));
            if (isValidTickCount(tickCount)) {
                mTickCount = tickCount;
                mTickStart = tickStart;
                mTickEnd = tickEnd;
                mLeftPos = getMarginLeft();
                mRightPos = getMarginRight() + getBarLength();

                float left = drawTicks ? 0 : mLeftPos;
                float right = drawTicks ? mTickCount - 1 : mRightPos;
                if (mListener != null) {
                    mListener.onRangeChangeListener(this, drawTicks,
                            (int) left, (int) right, tickStart, tickEnd);
                }

            } else {

                Log.e(TAG, "tickCount less than 2; invalid tickCount. XML input ignored.");
            }

            mTickHeight = (int) ta.getDimension(R.styleable.RangeBar_mrb_tickHeight,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TICK_HEIGHT_DP,
                            mDisplayMetrics)
            );
            mBarWeight = (int) ta.getDimension(R.styleable.RangeBar_mrb_barWeight,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BAR_WEIGHT_DP,
                            mDisplayMetrics)
            );
            defaultCircleSize = (int) ta.getDimension(R.styleable.RangeBar_mrb_selectorSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_SIZE,
                            mDisplayMetrics)
            );
            mCircleBoundarySize = (int) ta.getDimension(R.styleable.RangeBar_mrb_selectorBoundarySize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_BOUNDARY_SIZE_DP,
                            mDisplayMetrics)
            );
            mConnectingLineWeight = (int) ta.getDimension(R.styleable.RangeBar_mrb_connectingLineWeight,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CONNECTING_LINE_WEIGHT_DP,
                            mDisplayMetrics)
            );


            mBarPaddingBottom = (int) ta.getDimension(R.styleable.RangeBar_mrb_rangeBarPaddingBottom,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BAR_PADDING_BOTTOM_DP,
                            mDisplayMetrics)
            );

            mBarColor = ta.getColor(R.styleable.RangeBar_mrb_rangeBarColor, DEFAULT_BAR_COLOR);
            mTextColor = ta.getColor(R.styleable.RangeBar_mrb_pinTextColor, DEFAULT_TEXT_COLOR);
            mPinColor = ta.getColor(R.styleable.RangeBar_mrb_pinColor, DEFAULT_PIN_COLOR);
            mActiveBarColor = mBarColor;


            mCircleColor = ta.getColor(R.styleable.RangeBar_mrb_selectorColor,
                    DEFAULT_CONNECTING_LINE_COLOR);
            mCircleBoundaryColor = ta.getColor(R.styleable.RangeBar_mrb_selectorBoundaryColor,
                    DEFAULT_CONNECTING_LINE_COLOR);


            mActiveCircleColor = mCircleColor;
            mTickColor = ta.getColor(R.styleable.RangeBar_mrb_tickColor, DEFAULT_TICK_COLOR);
            mActiveTickColor = mTickColor;

            mConnectingLineColor = ta.getColor(R.styleable.RangeBar_mrb_connectingLineColor, DEFAULT_CONNECTING_LINE_COLOR);
            mConnectingLineColorStart = ta.getColor(R.styleable.RangeBar_mrb_connectingLineColorStart, DEFAULT_CONNECTING_LINE_COLOR);
            mConnectingLineColorEnd = ta.getColor(R.styleable.RangeBar_mrb_connectingLineColorEnd, DEFAULT_CONNECTING_LINE_COLOR);
            mActiveConnectingLineColor = mConnectingLineColor;

            mIsRangeBar = ta.getBoolean(R.styleable.RangeBar_mrb_rangeBar, true);

        } finally {
            ta.recycle();
        }
    }

    /**
     * Creates a new mBar
     */
    private void createBar() {
        mBar = new Bar(getContext(),
                getMarginLeft(),
                getYPos(),
                getBarLength(),
                mTickCount,
                mTickHeight,
                mTickColor,
                mBarWeight,
                mBarColor);

        invalidate();
    }


    private void createConnectingLine() {

        mConnectingLine = new ConnectingLine(getContext(),
                getYPos(),
                mConnectingLineWeight,
                mConnectingLineColorStart, mConnectingLineColorEnd);
        invalidate();
    }


    private void createPins() {
        Context ctx = getContext();
        float yPos = getYPos();

        if (mIsRangeBar) {
            if (customLeftThumb != null) {
                mLeftThumb = new CustomPinView(customLeftThumb, leftAnchor, leftValListener, this);
            } else {
                mLeftThumb = new DefaultPinView(ctx);
                ((DefaultPinView) mLeftThumb).init(this, yPos, mPinColor, mTextColor, defaultCircleSize, mCircleColor, mCircleBoundaryColor, mCircleBoundarySize, true);
            }
        }

        if (customRightThumb != null) {
            mRightThumb = new CustomPinView(customRightThumb, rightAnchor, rightValListener, this);
        } else {
            mRightThumb = new DefaultPinView(ctx);
            ((DefaultPinView) mRightThumb)
                    .init(this, yPos, mPinColor, mTextColor, defaultCircleSize, mCircleColor, mCircleBoundaryColor, mCircleBoundarySize, true);
        }

        if (mIsRangeBar) {
            mLeftThumb.setX(mLeftPos);
            mLeftThumb.setPinValue(getLeftPinValue());
        }
        mRightThumb.setX(mRightPos);
        mRightThumb.setPinValue(getRightPinValue());

        invalidate();
    }

    /**
     * Get marginLeft in each of the public attribute methods.
     *
     * @return float marginLeft
     */
    private int getMarginLeft() {
        return getPaddingLeft();
    }

    private int getMarginRight() {
        return getPaddingRight();
    }

    /**
     * Get yPos in each of the public attribute methods.
     *
     * @return float yPos
     */
    private int getYPos() {
        return (getHeight() - mBarPaddingBottom);
    }

    /**
     * Get barLength in each of the public attribute methods.
     *
     * @return float barLength
     */
    private int getBarLength() {
        return (getWidth() - getMarginLeft() - getMarginRight());
    }

    /**
     * Returns if either index is outside the range of the tickCount.
     *
     * @param leftThumbIndex  Integer specifying the left thumb index.
     * @param rightThumbIndex Integer specifying the right thumb index.
     * @return boolean If the index is out of range.
     */
    private boolean tickIndexOutOfRange(int leftThumbIndex, int rightThumbIndex) {
        return (leftThumbIndex < 0 || leftThumbIndex >= mTickCount
                || rightThumbIndex < 0
                || rightThumbIndex >= mTickCount);
    }

    /**
     * Returns if either value is outside the range of the tickCount.
     *
     * @param leftThumbValue  Float specifying the left thumb value.
     * @param rightThumbValue Float specifying the right thumb value.
     * @return boolean If the index is out of range.
     */
    private boolean valueOutOfRange(float leftThumbValue, float rightThumbValue) {
        return (leftThumbValue < mTickStart || leftThumbValue > mTickEnd
                || rightThumbValue < mTickStart || rightThumbValue > mTickEnd);
    }

    /**
     * If is invalid tickCount, rejects. TickCount must be greater than 1
     *
     * @param tickCount Integer
     * @return boolean: whether tickCount > 1
     */
    private boolean isValidTickCount(int tickCount) {
        return (tickCount > 1);
    }

    /**
     * Handles a {@link android.view.MotionEvent#ACTION_DOWN} event.
     *
     * @param x the x-coordinate of the down action
     * @param y the y-coordinate of the down action
     */
    private void onActionDown(float x, float y) {
        if (mIsRangeBar) {
            if (!mRightThumb.isPressed() && mLeftThumb.isInTargetZone(x, y)) {

                pressPin(mLeftThumb);

            } else if (!mLeftThumb.isPressed() && mRightThumb.isInTargetZone(x, y)) {

                pressPin(mRightThumb);
            }
        } else {
            if (mRightThumb.isInTargetZone(x, y)) {
                pressPin(mRightThumb);
            }
        }
    }

    /**
     * Handles a {@link android.view.MotionEvent#ACTION_UP} or
     * {@link android.view.MotionEvent#ACTION_CANCEL} event.
     *
     * @param x the x-coordinate of the up action
     * @param y the y-coordinate of the up action
     */
    private void onActionUp(float x, float y) {
        if (mIsRangeBar && mLeftThumb.isPressed()) {
            releasePin(mLeftThumb);
        } else if (mRightThumb.isPressed()) {
            releasePin(mRightThumb);
        } else {

            float leftThumbXDistance = mIsRangeBar ? Math.abs(mLeftThumb.getX() - x) : 0;
            float rightThumbXDistance = Math.abs(mRightThumb.getX() - x);

            if (leftThumbXDistance < rightThumbXDistance) {
                if (mIsRangeBar) {
                    mLeftThumb.setX(x);
                    releasePin(mLeftThumb);
                }
            } else {
                mRightThumb.setX(x);
                releasePin(mRightThumb);
            }

        }

        mLeftPos = (int) mLeftThumb.getX();
        mRightPos = (int) mRightThumb.getX();

        float left = mLeftPos, right = mRightPos;
        if (drawTicks) {
            left = findTick4Pos(mLeftPos);
            right = findTick4Pos(mRightPos);
            mLeftPos = findPos4Tick((int) left);
            mRightPos = findPos4Tick((int) right);
            mLeftThumb.setX(mLeftPos);
            mRightThumb.setX(mRightPos);
        }

        mLeftThumb.setPinValue(getLeftPinValue());
        mRightThumb.setPinValue(getRightPinValue());

        if (mListener != null) {
            mListener.onRangeChangeListener(this, drawTicks,
                    (int) left, (int) right,
                    getLeftPinValue(),
                    getRightPinValue());
        }
    }

    /**
     * Handles a {@link android.view.MotionEvent#ACTION_MOVE} event.
     *
     * @param x the x-coordinate of the move event
     */
    private void onActionMove(float x) {

        PinView leftPin = mLeftThumb, rightPin = mRightThumb;

        if (isRangeBar() && mLeftThumb.getX() >= mRightThumb.getX()) {
            leftPin = mRightThumb;
            rightPin = mLeftThumb;
            if (mLeftThumb.isPressed()) {
                pressPin(leftPin);
                releasePin(rightPin);
            } else {
                releasePin(leftPin);
                pressPin(rightPin);
            }
        }

        // Move the pressed thumb to the new x-position.
        if (mIsRangeBar && leftPin.isPressed()) {
            movePin(leftPin, x);
        } else if (rightPin.isPressed()) {
            movePin(rightPin, x);
        }

        int newLeftPos = mIsRangeBar ? (int) mLeftThumb.getX() : getMarginLeft();
        int newRightPos = (int) mRightThumb.getX();

        /// end added code
        // If either of the indices have changed, update and call the listener.
        if (newLeftPos != mLeftPos || newRightPos != mRightPos) {

            mLeftPos = newLeftPos;
            mRightPos = newRightPos;

            if (mIsRangeBar) {
                leftPin.setPinValue(getLeftPinValue());
            }
            rightPin.setPinValue(getRightPinValue());

        }

        invalidate();

    }


    private void pressPin(final PinView thumb) {
        thumb.press();
    }

    /**
     * Set the thumb to be in the normal/un-pressed state and calls invalidate()
     * to redraw the canvas to reflect the updated state.
     *
     * @param thumb the thumb to release
     */
    private void releasePin(final PinView thumb) {
        if (drawTicks) {
            int leftTick = findTick4Pos(thumb.getX());
            int newLeftPos = findPos4Tick(leftTick);

            if (newLeftPos != (int) thumb.getX()) {
                thumb.setX(newLeftPos);
                thumb.setPinValue(getPinValue(newLeftPos));
            }
        }

        thumb.release();

    }

    /**
     * Set the value on the thumb pin, either from map or calculated from the tick intervals
     * Integer check to format decimals as whole numbers
     *
     * @param pos the pos to set the value for
     */

    private float getPinValue(float pos) {
        float delta = pos - getMarginLeft();
        if (delta < 0) delta = 0;

        Float val = null;
        //val = mTickMap.get(pos);
        if (val == null) {
            val = delta * 1.f / getBarLength() * (mTickEnd - mTickStart) + mTickStart;
            //if (val >= mTickStart && val <= mTickEnd)
            //mTickMap.put(pos, val);
        }
        return val;
    }

    private String getValue4Tick(int tick) {
        float val = (tick == (mTickCount)) ? mTickEnd
                : (tick * 1.f / mTickCount) * (mTickEnd - mTickStart) + mTickStart;

        return String.valueOf(val);
    }

    /**
     * Moves the thumb to the given x-coordinate.
     *
     * @param thumb the thumb to move
     * @param x     the x-coordinate to move the thumb to
     */
    private void movePin(PinView thumb, float x) {
        if (thumb == null) {
            return;
        }

        if (x < mBar.getLeftX()) {
            thumb.setX(mBar.getLeftX());
        } else if (x > mBar.getRightX()) {
            thumb.setX(mBar.getRightX());
        } else {
            thumb.setX(x);
            invalidate();
        }
    }

    // Inner Classes ///////////////////////////////////////////////////////////

    /**
     * A callback that notifies clients when the RangeBar has changed. The
     * listener will only be called when either thumb's index has changed - not
     * for every movement of the thumb.
     */
    public interface OnRangeBarChangeListener {

        void onRangeChangeListener(RangeBar rangeBar, boolean posIsTickIndex, int leftPos,
                                   int rightPos, float leftPinValue, float rightPinValue);
    }

    public interface PinTextFormatter {

        String getText(String value);
    }

    /**
     * @author robmunro
     *         A callback that allows getting pin text exernally
     */
    public interface OnRangeBarTextListener {

        String getPinValue(RangeBar rangeBar, int tickIndex);
    }


    View customLeftThumb, customRightThumb;
    PinView.ValueChanged leftValListener, rightValListener;
    int leftAnchor, rightAnchor;

    /**
     * set custom selectors
     *
     * @param left  the left selector view; if not range bar , just set it null
     * @param right the right selector view
     */
    public void setCustomSelector(@Nullable View left, @Nullable PinView.ValueChanged leftValListener, @Nullable View right, @Nullable PinView.ValueChanged rightValListener) {
        setCustomSelector(left, ANCHOR_CENTER, leftValListener, right, ANCHOR_CENTER, rightValListener);
    }


    public void setCustomSelector(@Nullable View left, int leftAnchor, @Nullable PinView.ValueChanged leftValListener, @Nullable View right, int rightAnchor, @Nullable PinView.ValueChanged rightValListener) {
        customLeftThumb = left;
        this.leftValListener = leftValListener;
        this.leftAnchor = leftAnchor;
        customRightThumb = right;
        this.rightValListener = rightValListener;
        this.rightAnchor = rightAnchor;

        createPins();
    }

    /**
     * set the radius of  selector
     *
     * @param r radius of selector
     */
    public void setPinViewStubRadius(int r) {
        defaultCircleSize = r;
        createBar();

    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(pxValue / scale);
    }
}
