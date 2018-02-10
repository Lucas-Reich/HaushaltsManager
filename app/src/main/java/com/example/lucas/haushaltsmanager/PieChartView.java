package com.example.lucas.haushaltsmanager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PieChartView extends View {

    private String TAG = PieChartView.class.getSimpleName();

    private RectF mBackgroundBounds;

    private boolean mDrawLegend;
    private TextPaint mNumeratorFontPaint;
    private Rect mNumeratorFontBounds;
    private Paint mNumeratorPaint;
    private RectF mNumeratorRect;
    private float mNumeratorSize;
    private Rect mLegendBounds;

    private NumeratorStyles mNumeratorStyle;

    public enum NumeratorStyles {
        CIRCLE, SQUARE
    }

    private LegendPositions mLegendPosition;

    public enum LegendPositions {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    private LegendDirections mLegendDirection;

    public enum LegendDirections {
        LEFT_TO_RIGHT, TOP_TO_BOTTOM
    }

    private List<List<PieSlice>> mPieData = new ArrayList<>();
    private float[] mAbsoluteValues;
    private Paint mSlicePaint;
    private RectF mChartBounds;
    private Random mRnd = new Random();
    private String mNoData = getResources().getString(R.string.no_data);
    private boolean mPercentageValues = false;
    private boolean mSliceMargin;
    private String mCenterText = "";

    private boolean mDrawHole;
    private float mHoleRadius;
    private double mHoleSize;
    private Paint mHolePaint;

    private boolean mCompressed;
    private int mLayer;
    private int mVisibleLayer = 1;

    private boolean mTransparentCircle;
    private Paint mTransparentPaint;

    private ValueAnimator mAnimator;

    public PieChartView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * Methode um die View klasse zu initialisieren
     *
     * @param context      Context
     * @param attrs        Attribute, die im XML Dokument gesetzt wurden
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PieChartView,
                defStyleAttr,
                0);

        int holeColor;
        int numeratorColor;
        try {

            mDrawHole = a.getBoolean(R.styleable.PieChartView_draw_hole, false);
            mHoleSize = a.getInteger(R.styleable.PieChartView_hole_size, 50);
            holeColor = a.getColor(R.styleable.PieChartView_hole_color, Color.WHITE);
            mNoData = a.getString(R.styleable.PieChartView_no_data_text);
            mLegendPosition = LegendPositions.TOP_LEFT;
            mDrawLegend = a.getBoolean(R.styleable.PieChartView_use_legend, false);
            mNumeratorStyle = NumeratorStyles.SQUARE;
            mTransparentCircle = a.getBoolean(R.styleable.PieChartView_use_transparent_circle, false);
            mCompressed = a.getBoolean(R.styleable.PieChartView_use_compressed, false);
            mLayer = a.getInt(R.styleable.PieChartView_layer_depth, 3);
            mSliceMargin = a.getBoolean(R.styleable.PieChartView_draw_slice_margin, false);
            mCenterText = a.getString(R.styleable.PieChartView_center_text);
            numeratorColor = a.getColor(R.styleable.PieChartView_numerator_color, Color.BLACK);
            mLegendDirection = LegendDirections.LEFT_TO_RIGHT;
        } finally {

            a.recycle();
        }

        if (mDrawHole) {

            mHolePaint = new Paint();
            mHolePaint.setAntiAlias(true);
            mHolePaint.setStyle(Paint.Style.FILL);
            mHolePaint.setColor(holeColor);
        }

        if (mTransparentCircle && mDrawHole) {

            mTransparentPaint = new Paint();
            mTransparentPaint.setAntiAlias(true);
            mTransparentPaint.setStyle(Paint.Style.FILL);
            mTransparentPaint.setColor(holeColor);
            mTransparentPaint.setAlpha(100);
        }

        if (mDrawLegend) {

            mNumeratorSize = 50f;

            mNumeratorPaint = new Paint();
            mNumeratorPaint.setAntiAlias(true);
            mNumeratorPaint.setStyle(Paint.Style.FILL);
            mNumeratorPaint.setColor(numeratorColor);

            mNumeratorFontPaint = new TextPaint();
            mNumeratorFontPaint.setAntiAlias(true);
            mNumeratorFontPaint.setColor(Color.BLACK);
            mNumeratorFontPaint.setTextSize(Math.round(mNumeratorSize * 0.3 * getResources().getDisplayMetrics().scaledDensity));

            mNumeratorFontBounds = new Rect();

            mNumeratorRect = new RectF(0, 0, mNumeratorSize, mNumeratorSize);
        }

        mSlicePaint = new Paint();
        mSlicePaint.setAntiAlias(true);
        mSlicePaint.setDither(true);
        mSlicePaint.setStyle(Paint.Style.FILL);

        mBackgroundBounds = new RectF();
        mChartBounds = new RectF();
        mLegendBounds = new Rect();
    }

    public void setHoleColor(@ColorInt int color) {

        this.mHolePaint.setColor(color);
    }

    public @ColorInt
    int getHoleColor() {

        return this.mHolePaint.getColor();
    }

    public void useCompressedChart(boolean value) {

        this.mCompressed = value;
        invalidate();
        requestLayout();
    }

    public boolean isCompressed() {

        return this.mCompressed;
    }

    public boolean isHoleEnabled() {

        return mDrawHole;
    }

    public void setNumeratorColor(@ColorInt int color) {

        this.mNumeratorPaint.setColor(color);
        invalidate();
    }

    public @ColorInt
    int getNumeratorColor() {

        return this.mNumeratorPaint.getColor();
    }

    /**
     * Methode um den Slices eine Farbe zuzuweisen.
     * Ist dies geschehen wird das PieChart neu gezeichnet.
     *
     * @param colors Array mit Slicefarben
     */
    public void setSliceColors(@ColorInt int[] colors) {

        setSliceColorsPriv(colors);
        invalidate();
    }

    /**
     * Methode um den einzelnen Slices eine Farbe zuzuweisen.
     * Dabei bekommt der erste Slice die erste Farbe usw..
     * Sind nicht genügend Farben angegeben werden zufällige ausgewählt.
     *
     * @param colors Array mit Slicefarben
     */
    private void setSliceColorsPriv(@ColorInt int[] colors) {

        int counter = 0;

        for (List<PieSlice> layerSlices : mPieData) {

            for (PieSlice slice : layerSlices) {

                if (counter >= colors.length)
                    slice.setSliceColor(Color.rgb((int) slice.getAbsValue(), mRnd.nextInt(), mRnd.nextInt()));
                else
                    slice.setSliceColor(colors[counter]);

                counter++;
            }
        }
    }

    private void setSliceLabels(String[] sliceLabels) {

        int counter = 0;

        for (List<PieSlice> layerSlices : mPieData) {

            for (PieSlice slice : layerSlices) {

                if (counter >= sliceLabels.length)
                    slice.setSliceLabel("");
                else
                    slice.setSliceLabel(sliceLabels[counter]);
                counter++;
            }
        }
    }

    public void drawHole(boolean drawHole) {

        if (drawHole != mDrawHole) {

            mDrawHole = drawHole;
            invalidate();
            requestLayout();
        }
    }

    /**
     * Methode die aus einem Datenset und Farbinformationen die einzelnen Kreissegmente erstellt
     *
     * @param pieData Datenset der anzuzeigenden Elemente
     * @param colors  Farbset der einzelnen Kreissegmente
     */
    public void setPieData(float[] pieData, @ColorInt int[] colors, String[] sliceLabels) {

        if (pieData.length == 0)
            return;

        mPieData.clear();

        Arrays.sort(pieData);
        mAbsoluteValues = pieData;
        float total = getTotal();
        float startAngle = 0;
        List<PieSlice> defaultLayer = new ArrayList<>();

        for (float absoluteValue : mAbsoluteValues) {

            float percentValue = getPieArea(total, absoluteValue);
            PieSlice slice = new PieSlice(absoluteValue, percentValue, startAngle);
            slice.setWeight(mLayer);

            defaultLayer.add(slice);
            startAngle += percentValue;
        }

        mPieData.add(defaultLayer);

        setSliceColorsPriv(colors);

        setSliceLabels(sliceLabels);

        if (mCompressed)
            assignWeights();

        invalidate();
    }

    /**
     * Methode um das Kreisdiagramm mit neuen Werten zu befüllen
     *
     * @param pieData Neue Werte die in das Kreisdiagramm übernommern werden sollen
     */
    public void setPieData(float[] pieData) {

        setPieData(pieData, new int[]{ }, new String[]{ });
    }

    /**
     * Methode um einen Prozentualen Wert zu einem absoluten Wert zu bekommen
     *
     * @param pieTotal  Totale Kreisfläche
     * @param dataValue Wert welcher auf die Kreisfläche umgerechnet werden soll
     * @return Eingenommener Wert in Prozent
     */
    private float getPieArea(float pieTotal, float dataValue) {

        return dataValue / pieTotal * 360;
    }

    /**
     * Methode um den PieSlices ein passendes Gewicht zuzuordnen. Dabei werden in einem Layer ca. 2/3 des Gesamtvolumens angezeigt.
     * Außerdem werden die Slices basierend auf ihrem Gewicht in die passende liste in mPieData eingeordnet.
     */
    private void assignWeights() {

        int currentWeight = mLayer;
        float total = getTotal();
        float tempTotal = 0;

        //die layer information wird mit leeren listen befüllt die dann später die slices der verschiedenen layer enthalten
        for (int i = 0; i < mLayer; i++) {

            mPieData.add(new ArrayList<PieSlice>());
        }

        //hier werden den slices passende gewichte zugeordnet und außerdem werden sie gleich richtig sortiert
        for (PieSlice slice : mPieData.get(0)) {

            if (slice.getAbsValue() + tempTotal <= total * Math.pow(0.33, currentWeight - 1))
                slice.setWeight(currentWeight);
            else
                slice.setWeight(--currentWeight);

            mPieData.get(slice.getWeight()).add(slice);
            tempTotal += slice.getAbsValue();
        }
        mPieData.remove(0);

        //den Slices muss basierend auf ihrem gewicht ein neuer Bereich zugewiesen werden, außerdem muss für jedes außer das letzte layer ein platzhalter mit eingefügt werden
        tempTotal = getTotal();
        for (List<PieSlice> layerSlicesList : mPieData) {

            float startAngle = 0, currentTotal = 0;

            for (PieSlice slice : layerSlicesList) {

                slice.setStartAngle(startAngle);
                slice.setPercentValue(getPieArea(tempTotal, slice.getAbsValue()));

                startAngle += slice.getPercentValue();
                currentTotal += slice.getAbsValue();
            }

            if (mPieData.size() - 1 != mPieData.indexOf(layerSlicesList)) {

                PieSlice slice = new PieSlice(tempTotal - currentTotal, getPieArea(tempTotal, tempTotal - currentTotal), startAngle);
                slice.setSliceLabel(getResources().getString(R.string.others));
                layerSlicesList.add(slice);
            }

            tempTotal -= currentTotal;
        }
    }

    /**
     * Methode um den aufsummierten Wert der Werte in mPieData zu bekommen
     *
     * @return Aufsummierte Gesamtanzahl aus mPieData
     */
    private float getTotal() {

        float total = 0;

        for (float val : mAbsoluteValues)
            total += val;

        return total;
    }

    /**
     * Methode welche die Dimensionen der View berechnet
     *
     * @param widthMeasureSpec  Maximale Breite der View
     * @param heightMeasureSpec Maximale Höhe der View
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//todo padding considern
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mLegendBounds.set(0, 0, 0, 0);
        mBackgroundBounds.set(0, 0, widthSize, heightSize);


        int chartDesiredWidth = 250, chartDesiredHeight = 250;
        if (mDrawLegend)
            //measureLegend(widthSize / 2 - chartDesiredWidth, heightSize / 2 - chartDesiredHeight, chartDesiredWidth); todo deprecated
            measureLegend(widthSize - chartDesiredWidth, heightSize - chartDesiredHeight, chartDesiredWidth);

        int pieRadius = heightSize > widthSize ? widthSize / 2 : heightSize / 2;

        mChartBounds.set(0, mLegendBounds.height(), pieRadius, pieRadius + mLegendBounds.height());

        //Measure width
        if (widthMode == MeasureSpec.AT_MOST)
            mBackgroundBounds.right = Math.min((int) mChartBounds.width() + mLegendBounds.width(), widthSize);
        else if (widthMode == MeasureSpec.UNSPECIFIED)
            mBackgroundBounds.right = (int) mChartBounds.width() + mLegendBounds.width();

        //Measure height
        if (heightMode == MeasureSpec.AT_MOST)
            mBackgroundBounds.bottom = Math.min(mChartBounds.height() + mLegendBounds.height(), heightSize);
        else if (heightMode == MeasureSpec.UNSPECIFIED)
            mBackgroundBounds.bottom = mChartBounds.height() + mLegendBounds.height();

        setMeasuredDimension((int) mBackgroundBounds.width(), (int) mBackgroundBounds.height());
    }

    /**
     * Methode um die größe der Legende zu bestimmen.
     * Ist zu wenig platz werden zuerst die Label entfernt, ist immer noch zu wenig platz werden auch die Numeratoren nicht mehr angezeigt
     *
     * @param maxWidth     Maximale Breite die der Legende (in px) zur verfügung steht
     * @param maxHeight    Maximale Höhe die der Legende (in px) zur verfügung steht
     * @param minChartSize Größe die der Chart mindestens einnehmen wird
     */
    private void measureLegend(int maxWidth, int maxHeight, int minChartSize) {

        int counter = 1, width = 0, height = (int) mNumeratorSize;

        while (counter < 3) {

            for (PieSlice slice : mPieData.get(mVisibleLayer - 1)) {

                mNumeratorFontPaint.getTextBounds(slice.getSliceLabel(), 0, slice.getSliceLabel().length(), mNumeratorFontBounds);
                width += counter > 1 ? mNumeratorSize + 5 : mNumeratorFontBounds.width() + mNumeratorSize + 5;

                if (mNumeratorFontBounds.height() > mNumeratorSize)
                    height = mNumeratorFontBounds.height();
            }

            if (width <= maxWidth && height <= maxHeight)
                break;
            else if (counter == 2)
                height = 0;

            counter++;
            width = 0;
        }

        Point startCoord = getLegendStart(maxWidth + minChartSize, maxHeight + minChartSize);

        if (mLegendDirection == LegendDirections.LEFT_TO_RIGHT)
            mLegendBounds.set(startCoord.x, startCoord.y, startCoord.x + width, startCoord.y + height);
        else
            mLegendBounds.set(startCoord.x, startCoord.y, startCoord.x + height, startCoord.y + width);
    }

    /**
     * Methode um das Kreisdiagramm zu zeichnen
     *
     * @param canvas Zeichen Objekt auf dem der Kreis gezeichet wird
     */
    @Override
    protected void onDraw(Canvas canvas) {

        if (mPieData.size() == 0)
            return;

        for (PieSlice slice : mPieData.get(mVisibleLayer - 1)) {

            mSlicePaint.setColor(slice.getSliceColor());
            if (mSliceMargin)
                canvas.drawArc(mChartBounds, slice.getStartAngle(), slice.getPercentValue() - 2f, true, mSlicePaint);
            else
                canvas.drawArc(mChartBounds, slice.getStartAngle(), slice.getPercentValue(), true, mSlicePaint);
        }

        if (mDrawHole)
            drawHole(canvas);

        if (mDrawLegend)
            drawLegend(canvas);
    }

    /**
     * Method um innnerhalb des Kreisdiagramms ein Loch einzufügen, sodass das Diagramm wie eine "Donut" aussieht
     *
     * @param canvas Das Object auf dem gezeichnet wurde
     */
    private void drawHole(Canvas canvas) {
        mHoleRadius = (float) (mChartBounds.width() / 2 * (mHoleSize / 100));

        canvas.drawCircle(mChartBounds.centerX(), mChartBounds.centerY(), mHoleRadius, mHolePaint);

        if (mTransparentCircle)
            canvas.drawCircle(mChartBounds.centerX(), mChartBounds.centerY(), mHoleRadius + (mHoleRadius * 0.25f), mTransparentPaint);
    }

    /**
     * Methode um die Legende zu zeichnen
     *
     * @param canvas Canvas Objekt auf dem die Legende gezeichnet werden soll
     */
    private void drawLegend(Canvas canvas) {

        if (mLegendBounds.isEmpty())
            return;

        boolean drawLabels = true;
        if (mPieData.get(mVisibleLayer - 1).size() * (mNumeratorSize + 5) == mLegendBounds.width())
            drawLabels = false;

        for (PieSlice slice : mPieData.get(mVisibleLayer - 1)) {
            mNumeratorFontPaint.getTextBounds(slice.getSliceLabel(), 0, slice.getSliceLabel().length(), mNumeratorFontBounds);
            mNumeratorPaint.setColor(slice.getSliceColor());

            switch (mNumeratorStyle) {

                case CIRCLE:
                    canvas.drawCircle(mNumeratorRect.centerX(), mNumeratorRect.centerY(), mNumeratorSize / 2, mNumeratorPaint);
                    break;

                case SQUARE:
                    canvas.drawRect(mNumeratorRect, mNumeratorPaint);
                    break;
            }

            mNumeratorRect.left += mNumeratorSize;
            mNumeratorRect.right += mNumeratorSize;


            if (drawLabels) {

                canvas.drawText(slice.getSliceLabel(), mNumeratorRect.left, mNumeratorSize - ((mNumeratorSize - mNumeratorFontBounds.height()) / 2), mNumeratorFontPaint);
                mNumeratorRect.left += mNumeratorFontBounds.width() + 5;
                mNumeratorRect.right += mNumeratorFontBounds.width() + 5;
            }
        }

        mNumeratorRect.set(0, 0, mNumeratorSize, mNumeratorSize);
    }

    /**
     * Methode um die Anfangskoordinate der Legende zu bestimmen
     *
     * @return Punkt der die Startkoordinate enthält
     */
    private Point getLegendStart(int width, int height) {

        switch (mLegendPosition) {

            case TOP_LEFT:
                return new Point(0, 0);

            case TOP_CENTER:
                return new Point(width / 2, 0);

            case TOP_RIGHT:
                return new Point();

            case BOTTOM_LEFT:
                return new Point(0, height);

            case BOTTOM_CENTER:
                return new Point(width / 2, height);

            case BOTTOM_RIGHT:
                return new Point();

            default:
                return new Point();
        }
    }

    /**
     * Methode um Touch aktionen des Users abzufangen
     *
     * @param event Interaktionsart des Users
     * @return Boolean ob das event gehandelt wurde
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Point click = new Point((int) Math.floor(event.getX()), (int) Math.floor(event.getY()));

        //User tippt auf das Display
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            //todo implement
        }

        //User nimmt den Finger vom Display
        if (event.getAction() == MotionEvent.ACTION_UP) {

            switch (clickableArea(click)) {

                case 0://PieChart
                    Log.d(TAG, "onTouchEvent: Du hast den PieChart angeklickt!");

                    float pointAngle = getAngleForPoint(click);

                    for (PieSlice slice : mPieData.get(mVisibleLayer - 1)) {

                        if (pointAngle > slice.getStartAngle() && pointAngle < slice.getEndAngle()) {

                            Log.d(TAG, "Du bist auf Ebene " + mVisibleLayer + " und hast gerade in den Bereich mit dem Wert " + slice.getAbsValue() + " geklickt!");

                            if (slice.getWeight() != mVisibleLayer && mCompressed)
                                mVisibleLayer++;
                            break;
                        }
                    }
                    break;
                case 1://Legend
                    Log.d(TAG, "onTouchEvent: du hast die Legende angeklickt");

                    int counterRight = 0, counterLeft;

                    for (PieSlice slice : mPieData.get(mVisibleLayer - 1)) {

                        mNumeratorFontPaint.getTextBounds(slice.getSliceLabel(), 0, slice.getSliceLabel().length(), mNumeratorFontBounds);

                        counterRight += mNumeratorFontBounds.right + mNumeratorSize;
                        counterLeft = counterRight - (mNumeratorFontBounds.width() + (int) mNumeratorSize);

                        if (click.x >= counterLeft && click.x <= counterRight) {

                            Log.d(TAG, "In der Legende hast du auf das Element mit dem Wert " + slice.getAbsValue() + " geklickt!");
                            fadeSliceInOut(slice);
                            break;
                        }
                    }
                    break;
                default://nothing
                    break;
            }
            invalidate();
        }

        return true;
    }

    /**
     * Methode gibt einen code für den angeklickten Punkt zurück
     *
     * @param click Punkt auf den gecklickt wurde
     * @return 0 wenn auf den PieChart geklickt wurde, 1 wenn auf die Legende geklickt wurde, 0 wenn "nichts" angeklickt wurde
     */
    private int clickableArea(Point click) {

        if (mDrawHole && Math.sqrt(Math.pow((mChartBounds.centerX() - click.x), 2) + Math.pow((mChartBounds.centerY() - click.y), 2)) <= mHoleRadius)
            return -1;//hole click

        if (mLegendBounds.contains(click.x, click.y))
            return 1;//legend click

        if (Math.sqrt(Math.pow((mChartBounds.centerX() - click.x), 2) + Math.pow((mChartBounds.centerY() - click.y), 2)) <= mChartBounds.width() / 2)
            return 0;//chart click

        return -1;//nothing click
    }

    /**
     * Methode um den Winkel eines gegebenen Punktes im bezug zum Mittelpunkt zu berechnen, jedoch nur wenn sich der Punkt innerhalb des Kreises befindet
     *
     * @param point Punkt auf den gecklickt wurde
     * @return Winkel eines Punktes bezogen auf den Mittelpunkt oder -1 falls der Punkt nicht innerhalb der Kreises liegt
     */
    private float getAngleForPoint(Point point) {

        double tx = point.x - mChartBounds.centerX(), ty = point.y - mChartBounds.centerY();
        double length = Math.sqrt(tx * tx + ty * ty);
        double r = Math.acos(ty / length);

        float angle = (float) Math.toDegrees(r);

        if (point.x > mChartBounds.centerX())
            angle = 360f - angle;

        angle += 90f;

        if (angle > 360f)
            angle -= 360f;

        return angle;
    }

    /**
     * Methode um den totalen Wert der aktuell sichtbaren PieSlices zu errechnen.
     * Diese Methode wird zum animieren benutzt und nutzt den mAnimValue des PieSlices.
     *
     * @return LayerTotal wert
     */
    private float getLayerTotal() {

        float total = 0;

        for (PieSlice slice : mPieData.get(mVisibleLayer - 1)) {

            total += slice.getAnimValue();
        }

        return total;
    }

    /**
     * Methode um die SliceSize des aktuell sichtbarem layers im PieChart zu errechnen.
     * Diese Methode wird zum animieren benutzt und nutzt den mAnimValue des PieSlices.
     */
    private void resolveSliceSize() {

        float total = getLayerTotal();
        int startAngle = 0;

        for (PieSlice slice : mPieData.get(mVisibleLayer - 1)) {

            slice.setPercentValue(getPieArea(total, slice.getAnimValue()));
            slice.setStartAngle(startAngle);

            startAngle += slice.getPercentValue();
        }
    }

    /**
     * Mit dieser Methode werden die einzelnen PieSlices animiert.
     *
     * @param slice PieSlice der animiert weden soll
     */
    private void fadeSliceInOut(final PieSlice slice) {

        if (mAnimator != null)
            mAnimator.end();

        if (slice.getPercentValue() > 0f)
            mAnimator = ValueAnimator.ofFloat(slice.getAbsValue(), 0f);
        else
            mAnimator = ValueAnimator.ofFloat(0f, slice.getAbsValue());

        mAnimator.setDuration(1000); // 1 sekunde
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();
                slice.setAnimValue(value);

                resolveSliceSize();

                invalidate();
            }
        });
        mAnimator.start();
    }

    private void crossLegendElem(final PieSlice slice) {


    }

    /**
     * Klasse um die Datensets des Kreisdiagramms zu sortieren
     */
    private class PieSlice {

        /**
         * Originaler vom User gegebener Wert
         */
        private float mAbsValue;

        /**
         * Basiert auf mAbsValue und ist zum animieren gedacht
         */
        private float mAnimValue;

        /**
         * Prozentualer Anteil am Kreis
         */
        private float mPercentValue;

        /**
         * Winkel ab dem der Bereich des Segments beginnt
         */
        private float mStartAngle;

        /**
         * Winkel ab dem der Bereich des Segments endet
         */
        private float mEndAngle;

        /**
         * Farbe des Kreissegments
         */
        private int mSliceColor;

        /**
         * Bezeichnung des Kreissegments
         */
        private String mSliceLabel;

        /**
         * Platz den das Kreissegment einnimt unterteilt in gewichte (Default 1).
         */
        private int mSliceWeight;

        PieSlice(float absValue, float percentValue, float startAngle) {

            constructor(absValue, percentValue, startAngle);
        }

        private void constructor(float absValue, float percentValue, float startAngle) {

            this.mAbsValue = absValue;
            this.mAnimValue = absValue;
            this.mPercentValue = percentValue;
            this.mStartAngle = startAngle;
            this.mEndAngle = mStartAngle + percentValue;
            this.mSliceColor = Color.WHITE;
            mSliceWeight = 0;
        }

        void setWeight(int weight) {

            this.mSliceWeight = weight;
        }

        int getWeight() {

            return this.mSliceWeight;
        }

        float getAbsValue() {

            return mAbsValue;
        }

        float getAnimValue() {

            return this.mAnimValue;
        }

        void setAnimValue(float animValue) {

            this.mAnimValue = animValue >= 0 ? animValue : 0f;
        }

        int getSliceColor() {

            return this.mSliceColor;
        }

        void setSliceColor(@ColorInt int color) {

            this.mSliceColor = color;
        }

        @NonNull
        String getSliceLabel() {

            return this.mSliceLabel != null ? mSliceLabel : "";
        }

        void setSliceLabel(@NonNull String label) {

            this.mSliceLabel = label;
        }

        float getPercentValue() {

            return mPercentValue;
        }

        void setPercentValue(float mCalcValue) {

            this.mPercentValue = mCalcValue;
            this.mEndAngle = this.mStartAngle + mCalcValue;
        }

        float getStartAngle() {

            return mStartAngle;
        }

        void setStartAngle(float mStartAngle) {

            this.mStartAngle = mStartAngle;
        }

        float getEndAngle() {

            return mEndAngle;
        }
    }
}
