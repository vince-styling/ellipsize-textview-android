/**
 * Copyright 2013 Vince Styling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vincestyling.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.vincestyling.android.R;

import java.util.ArrayList;

/**
 * This class provides a Textview-like control which allows you to set max number
 * of lines to wrap an input string, then ellipsizes the last line if there's not
 * enough room to handle the entire input string.
 * <p/>
 * As you know, the Android UI TextView also allow we to set MaxLines and EllipsizeMode,
 * but we can't find out how to know if TextView ellipsized, we want to know it because
 * we can do something for it such as show an expand/collapse Button by the ellipsize
 * state, that was why we wrote this widget.
 * <p/>
 * We implement this class Like TextView, also offer LineSpacing, TextSize, TextColor
 * to let you customize like you do with TextView via StyledAttributes, of course you
 * can declare more attributes as you wanted.
 * <p/>
 * When onMeasure calling, we use Paint.breakText() to calculate the input string then store
 * every line's start&end index. after that, we got the actually line count(EntireLineCount)
 * by the entire input string, according to that we can measure width simply done by
 * Paint.measureText() method. Use EntireLineCount we can decide should expand or not
 * and measure height. when measure is done, we will inform the ellipsize mode via
 * OnMeasureDoneListener who want to know it was changed. you can toggle the mode to
 * expand/collapse via click-handler if there's not enough space for the
 * input string(when EntireLineCount > MaxLineCount).
 * <p/>
 * Notes : This widget is pretty basic, it just support left-to-right text and
 * ellipsize text end. it handled Chinese or some languages like Chinese,
 * you can modify the breakText logic code to implement yourself.
 *
 * @author Vince
 */
public class EllipsizeEndTextView extends View {
    public final static String NEW_LINE_STR = "\n";

    private int mLineSpacing;

    // the inform listener when measure is done
    private OnMeasureDoneListener mOnMeasureDoneListener;

    public EllipsizeEndTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.EllipsizeEndTextView);

        mStrEllipsis = "...";
        mMaxLineCount = typeArray.getInteger(R.styleable.EllipsizeEndTextView_maxLines, 5);
        mLineSpacing = typeArray.getDimensionPixelSize(R.styleable.EllipsizeEndTextView_lineSpacing, 0);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(typeArray.getColor(R.styleable.EllipsizeEndTextView_textColor, Color.BLACK));
        mPaint.setTextSize(typeArray.getDimensionPixelSize(R.styleable.EllipsizeEndTextView_textSize, 0));

        typeArray.recycle();
    }

    // the text ascent by Paint, use to compute the text height
    private int mAscent;

    private int mMaxLineCount;

    // when measure's done, drawLineCount will decide by expand mode
    private int mDrawLineCount;

    // the input string
    private String mText;

    private Paint mPaint;

    private boolean mExpanded = false;

    private String mStrEllipsis;

    // Beginning and end indices for the input string
    private ArrayList<int[]> mLines;

    /**
     * Sets the text to display in this widget.
     *
     * @param text The text to display.
     */
    public void setText(String text) {
        mText = text;
        requestLayout();
        invalidate();
    }

    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        if (mOnMeasureDoneListener != null) mOnMeasureDoneListener.onMeasureDone(this);
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // Format the text using this exact width, and the current mode.
            breakWidth(specSize);
            // We were told how big to be.
            return specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // Use the AT_MOST size - if we had very short text, we may need even less
            // than the AT_MOST value, so return the minimum.
            return Math.min(breakWidth(specSize), specSize);
        } else {
            // We're not given any width - so in this case we assume we have an unlimited width?
            return breakWidth(specSize);
        }
    }

    /**
     * use Paint.breakText() to calculate widget entire line count
     * and measure first line width
     *
     * @param availableWidth The available width
     * @return The width of the view
     */
    private int breakWidth(int availableWidth) {
        int maxWidth = availableWidth - getPaddingLeft() - getPaddingRight();

        // we assume the width always equals first measure, so we just measure once
        if (mLines == null) {
            // If maxWidth is -1, interpret that as meaning to render the string on a single
            // line. Skip everything.
            if (maxWidth == -1) {
                mLines = new ArrayList<int[]>(1);
                mLines.add(new int[]{0, mText.length()});
            } else {
                int index = 0;
                int newlineIndex = 0;
                int endCharIndex = 0;
                mLines = new ArrayList<int[]>(mMaxLineCount * 2);

                // breakText line by line and store line's indices
                while (index < mText.length()) {
                    if (index == newlineIndex) {
                        newlineIndex = mText.indexOf(NEW_LINE_STR, newlineIndex);
                        endCharIndex = (newlineIndex != -1) ? newlineIndex : mText.length();
                    }

                    int charCount = mPaint.breakText(mText, index, endCharIndex, true, maxWidth, null);
                    if (charCount > 0) {
                        mLines.add(new int[]{index, index + charCount});
                        index += charCount;
                    }

                    if (index == newlineIndex) {
                        newlineIndex++;
                        index++;
                    }
                }
            }
        }

        int widthUsed;
        // If we required only one line, return its length, otherwise we used
        // whatever the maxWidth supplied was.
        switch (mLines.size()) {
            case 1:
                widthUsed = (int) (mPaint.measureText(mText) + 0.5f);
                break;
            case 0:
                widthUsed = 0;
                break;
            default:
                widthUsed = maxWidth;
                break;
        }

        return widthUsed + getPaddingLeft() + getPaddingRight();
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        mAscent = (int) mPaint.ascent();
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be, so nothing to do.
            result = specSize;
        } else {
            // The lines should already be broken up. Calculate our max desired height
            // for our current mode.
            if (mExpanded) {
                mDrawLineCount = mLines.size();
            } else if (mLines.size() > mMaxLineCount) {
                mDrawLineCount = mMaxLineCount;
            } else {
                // when collapse mode on, but entire line count less then or equals
                // max line count, we should turn expand mode on
                mDrawLineCount = mLines.size();
                mExpanded = true;
            }

            int textHeight = (int) (-mAscent + mPaint.descent());
            result = getPaddingTop() + getPaddingBottom();
            if (mDrawLineCount > 0) {
                result += mDrawLineCount * textHeight + (mDrawLineCount - 1) * mLineSpacing;
            } else {
                result += textHeight;
            }

            // Respect AT_MOST value if that was what is called for by measureSpec.
            if (specMode == MeasureSpec.AT_MOST) result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int renderWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        float x = getPaddingLeft();
        float y = getPaddingTop() - mAscent;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mDrawLineCount; i++) {
            // obtain current line to draw
            sb.append(mText, mLines.get(i)[0], mLines.get(i)[1]);

            // draw the ellipsis if necessary
            if (!mExpanded && mDrawLineCount - i == 1) {
                float lineDrawWidth = mPaint.measureText(sb, 0, sb.length());
                float ellipsisWidth = mPaint.measureText(mStrEllipsis);

                // delete one char then measure until enough to draw ellipsize text
                while (lineDrawWidth + ellipsisWidth > renderWidth) {
                    sb.deleteCharAt(sb.length() - 1);
                    lineDrawWidth = mPaint.measureText(sb, 0, sb.length());
                }
                sb.append(mStrEllipsis);
            }

            // draw the current line.
            canvas.drawText(sb, 0, sb.length(), x, y, mPaint);

            y += (-mAscent + mPaint.descent()) + mLineSpacing;
            // stop if canvas not enough space to draw next line
            if (y > canvas.getHeight()) break;

            // clean the line buffer
            sb.delete(0, sb.length());
        }
    }

    public void elipsizeSwitch() {
        if (mExpanded) collapse();
        else expand();
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void expand() {
        mExpanded = true;
        requestLayout();
        invalidate();
    }

    public void collapse() {
        mExpanded = false;
        requestLayout();
        invalidate();
    }

    public void setOnMeasureDoneListener(OnMeasureDoneListener onMeasureDoneListener) {
        mOnMeasureDoneListener = onMeasureDoneListener;
    }

    public interface OnMeasureDoneListener {
        void onMeasureDone(View v);
    }

}
