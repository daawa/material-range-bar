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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * Class representing the blue connecting line between the two thumbs.
 */
public class ConnectingLine {

    // Member Variables ////////////////////////////////////////////////////////

    private final Paint mPaint;

    private final float mY;

    private int colorStart, colorEnd;

    // Constructor /////////////////////////////////////////////////////////////

    /**
     * Constructor for connecting line
     *
     * @param ctx                  the context for the line
     * @param y                    the y co-ordinate for the line
     * @param connectingLineWeight the weight of the line
     * @param connectingLineColorStart  the color of the line
     * @param colorEnd the end color if connecting line is gradient
     */
    public ConnectingLine(Context ctx, float y, float connectingLineWeight,
            int connectingLineColorStart, int colorEnd) {

        //final Resources res = ctx.getResources();

        // Initialize the paint, set values
        mPaint = new Paint();
        //mPaint.setColor(connectingLineColorStart);
        this.colorStart = connectingLineColorStart;
        this.colorEnd = colorEnd;
        mPaint.setStrokeWidth(connectingLineWeight);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mY = y;
    }

    // Package-Private Methods /////////////////////////////////////////////////

    /**
     * Draw the connecting line between the two thumbs in rangebar.
     *
     * @param canvas     the Canvas to draw to
     * @param leftThumb  the left thumb
     * @param rightThumb the right thumb
     */
    public void draw(Canvas canvas, PinView leftThumb, PinView rightThumb) {
        Shader shader = new LinearGradient(leftThumb.getAnchor(), 0, rightThumb.getAnchor(), 0, colorStart/*Color.parseColor("#aa00ff00")*/, colorEnd/*Color.parseColor("#ffff0000")*/, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        canvas.drawLine(leftThumb.getAnchor(), mY, rightThumb.getAnchor(), mY, mPaint);
    }

    /**
     * Draw the connecting line between for single slider.
     *
     * @param canvas     the Canvas to draw to
     * @param rightThumb the right thumb
     * @param leftMargin the left margin
     */
    public void draw(Canvas canvas, float leftMargin, PinView rightThumb) {
        canvas.drawLine(leftMargin, mY, rightThumb.getAnchor(), mY, mPaint);
    }
}
