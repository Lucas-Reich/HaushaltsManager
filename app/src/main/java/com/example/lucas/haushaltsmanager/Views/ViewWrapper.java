package com.example.lucas.haushaltsmanager.Views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public abstract class ViewWrapper extends View {

    protected RectF mViewBounds;

    public ViewWrapper(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ViewWrapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ViewWrapper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

   protected abstract void init(Context context, AttributeSet attrs, int defStyleAttr);

    /**
     * Methode um herauszufinden wie viel Platz maximal zur verfügung steht
     *
     * @param desiredSize Optimale Größe
     * @param measureSpec Kombinierter Wert aus Platz und Layout verhalten
     * @return Int
     */
    protected int reconcileSize(int desiredSize, int measureSpec) {

        int mode = MeasureSpec.getMode(measureSpec);
        int sizeInPx = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.EXACTLY:
                return sizeInPx;
            case MeasureSpec.AT_MOST:
                return Math.min(sizeInPx, desiredSize);
            case MeasureSpec.UNSPECIFIED:
                return desiredSize;
            default:
                return -1;
        }
    }

    /**
     * Methode um Pixel in DensityPixel umzuwandeln.
     * source: https://stackoverflow.com/a/19953871/9376633
     *
     * @param px Zu konvertierende pixel
     * @return In Dp konvertierte pixel
     */
    protected int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Methode um DensityPixel in Pixel umzuwandeln.
     * source: https://stackoverflow.com/a/19953871/9376633
     *
     * @param dp Zu konvertierende dp
     * @return In px konvertierte dp
     */
    protected static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Methode den von einem String eingenommenen Platz zu ermitteln
     * source: https://stackoverflow.com/a/4795393
     *
     * @param text Text dessen Größe bestimmt werden soll
     * @return Textgröße in einem Rect gespeichert
     */
    protected Rect getTextBounds(String text, float textSize) {

        Rect textBounds = new Rect();

        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), textBounds);

        return textBounds;
    }
}
