package com.example.lucas.haushaltsmanager.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RoundedTextView extends View {

    private String TAG = RoundedTextView.class.getSimpleName();

    Paint mCirclePaint;
    RectF mViewBounds;

    String mCenterText = "U";
    Paint mTextPaint;
    Rect mTextBounds;

    Paint mStrokePaint;

    int mSolidColor, mStrokeColor;

    public RoundedTextView(Context context) {
        super(context);
        init();
    }

    public RoundedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mCirclePaint = new Paint();
        mSolidColor = Color.RED;
        mCirclePaint.setColor(mSolidColor);
        mCirclePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mViewBounds = new RectF();

        mStrokePaint = new Paint();
        mStrokeColor = Color.GREEN;
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(50f);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));//make text bold
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //TODO consider padding
        //TODO use density pixels
        //TODO wenn die width oder height größer ist als die andere soll der sich der kreis automatisch in der mitte platzieren

        int desiredWidth = 90, desiredHeight = 90;
        int actualWidth = 0, actualHeight = 0;

        //Measure width
        if (widthMode == MeasureSpec.UNSPECIFIED)
            actualWidth = desiredWidth;
        else if (widthMode == MeasureSpec.AT_MOST)
            actualWidth = Math.min(desiredWidth, widthSize);
        else if (widthMode == MeasureSpec.EXACTLY)
            actualWidth = widthSize;

        //Measure height
        if (heightMode == MeasureSpec.UNSPECIFIED)
            actualHeight = desiredHeight;
        else if (heightMode == MeasureSpec.AT_MOST)
            actualHeight = Math.min(desiredHeight, heightSize);
        else if (heightMode == MeasureSpec.EXACTLY)
            actualHeight = heightSize;


        setViewBounds(actualWidth, actualHeight);
        measureTextSize(mCenterText);
        setMeasuredDimension(actualWidth, actualHeight);
    }

    private void setViewBounds(int width, int height) {

        int widthAndHeight = width < height ? width : height;
        mViewBounds.set(0,0,widthAndHeight, widthAndHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(mViewBounds.centerX(), mViewBounds.centerY() ,mViewBounds.width() / 2, mCirclePaint);
        canvas.drawText(mCenterText, mViewBounds.centerX() - (mTextBounds.width() / 2), mViewBounds.centerY() + (mTextBounds.width() / 2), mTextPaint);
    }

    private void measureTextSize(String text) {

        mTextBounds = new Rect(0,0, (int) mViewBounds.right, (int) mViewBounds.bottom);
        mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);
    }

    public void setCenterText(@NonNull String centerText) {

        mCenterText = centerText.toUpperCase();
    }

    public void setTextColor(@ColorInt int textColor) {

        mTextPaint.setColor(textColor);
    }

    public String getCenterText() {

        return mCenterText;
    }

    public void setCircleColor(String circleColor) {

        mCirclePaint.setColor(Color.parseColor(circleColor));
    }
}
