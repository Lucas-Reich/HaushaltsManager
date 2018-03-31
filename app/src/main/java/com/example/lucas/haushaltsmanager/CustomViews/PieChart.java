package com.example.lucas.haushaltsmanager.CustomViews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.lucas.haushaltsmanager.DataSet;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PieChart extends View {

    private String TAG = PieChart.class.getSimpleName();

    private RectF mViewBounds;

    private boolean mDrawLegend;
    private TextPaint mNumeratorFontPaint;
    private Rect mNumeratorFontBounds;
    private Paint mNumeratorPaint;
    private RectF mNumeratorRect;
    private float mNumeratorSize;
    private float mLegendFontSize = 15;//todo anpassen
    private Rect legendBounds;

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
    private RectF mPieChartBounds;
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

    public PieChart(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                R.styleable.PieChart,
                defStyleAttr,
                0);

        int holeColor;
        int numeratorColor;
        try {

            mDrawHole = a.getBoolean(R.styleable.PieChart_draw_hole, false);
            mHoleSize = a.getInteger(R.styleable.PieChart_hole_size, 50);
            holeColor = a.getColor(R.styleable.PieChart_hole_color, Color.WHITE);
            mNoData = a.getString(R.styleable.PieChart_no_data_text);
            mLegendPosition = LegendPositions.TOP_LEFT;
            mDrawLegend = a.getBoolean(R.styleable.PieChart_use_legend, false);
            mNumeratorStyle = NumeratorStyles.SQUARE;
            mTransparentCircle = a.getBoolean(R.styleable.PieChart_use_transparent_circle, false);
            mCompressed = a.getBoolean(R.styleable.PieChart_use_compressed, false);
            mLayer = a.getInt(R.styleable.PieChart_layer_depth, 3);
            mSliceMargin = a.getBoolean(R.styleable.PieChart_draw_slice_margin, false);
            mCenterText = a.getString(R.styleable.PieChart_center_text);
            numeratorColor = a.getColor(R.styleable.PieChart_numerator_color, Color.BLACK);
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

        mViewBounds = new RectF();
        mPieChartBounds = new RectF();
        legendBounds = new Rect();

        mPieData.add(new ArrayList<PieSlice>());
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

    public void setPieData(List<DataSet> pieData) {
        if (pieData.size() == 0)
            return;

        mPieData.clear();
        //todo methode schreiben die eine liste von datensets entgegennimmt und daraus slices macht
        sortDataSetAsc(pieData);
    }

    private void sortDataSetAsc(List<DataSet> dataSet) {

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

    //ab hier wird die Größe des PieCharts bestimmt

    private Rect getChartDesiredSize() {

        return new Rect(0, 0, 500, 500);
    }

    private Rect getLegendDesiredSize() {

        //wenn man im collapsed mode ist wird das letzte label nicht mit einbezogen ("Andere") todo
        //        //wenn die legende vertikal angeordnet sein soll dann soll sie so aussehen
        //        // * Label
        //        // * Label
        //        // ...
        //
        //        // und nicht so
        //        // *
        //        // Label
        //        // *
        //        // Label
        //todo offset zwischen den labels einfügen
        int width = 0, height = 0;
        for (PieSlice slice : mPieData.get(mVisibleLayer - 1)) {

            Rect textSize = getTextBounds(slice.getSliceLabel(), mLegendFontSize);
            if (mLegendDirection.equals(LegendDirections.LEFT_TO_RIGHT)) {

                width += mNumeratorSize + textSize.width();
                height = Math.max(textSize.height(), (int) mNumeratorSize);
            } else {

                width = Math.max(textSize.width(), (int) mNumeratorSize);
                height += mNumeratorSize + textSize.height();
            }
        }

        return new Rect(0, 0, width, height);
    }

    /**
     * Methode um die maximale Größe der View zu ermitteln
     *
     * @param widthMeasureSpec  Breiteninformationen
     * @param heightMeasureSpec Höheninformationen
     */
    private void setViewBounds(int widthMeasureSpec, int heightMeasureSpec) {

        mViewBounds.set(0, 0, 0, 0);
        if (isLegendBottom() || isLegendTop()) {

            mViewBounds.right = reconcileSize(Math.max(getChartDesiredSize().width(), getLegendDesiredSize().width()), widthMeasureSpec);
            mViewBounds.bottom = reconcileSize(getChartDesiredSize().height() + getLegendDesiredSize().height(), heightMeasureSpec);
        } else {

            mViewBounds.right = reconcileSize(getChartDesiredSize().width() + getLegendDesiredSize().width(), widthMeasureSpec);
            mViewBounds.bottom = reconcileSize(Math.max(getChartDesiredSize().height(), getLegendDesiredSize().height()), heightMeasureSpec);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setViewBounds(widthMeasureSpec, heightMeasureSpec);

        //padding von der maximalen größe abziehen
        int widthPadding = getPaddingRight() + getPaddingLeft();
        int heightPadding = getPaddingTop() + getPaddingBottom();
        Rect sizeWithPadding = new Rect(0, 0, (int) mViewBounds.width() - widthPadding, (int) mViewBounds.height() - heightPadding);


        //herausfinden wie groß die legende sein kann
        int minChartSize = 500;//todo map to dp
        legendBounds = resolveLegendSize(sizeWithPadding, minChartSize);
        mPieChartBounds = resolveChartSize(legendBounds, sizeWithPadding);

        applyPaddingToChartAndLegend(legendBounds, mPieChartBounds);
        setMeasuredDimension((int) mViewBounds.width(), (int) mViewBounds.height());
    }

    /**
     * Methode um die Größe des PieCharts zu bestimmen.
     *
     * @param legendBounds   Größe der Legende
     * @param availableSpace Verfügbarer platz
     * @return PieChart bounds mit angepasster Größe
     */
    private RectF resolveChartSize(Rect legendBounds, Rect availableSpace) {

        mPieChartBounds.left = 0;
        mPieChartBounds.top = 0;
        if (isLegendBottom() || isLegendTop()) {

            //ist die breite größer als die höhe
            mPieChartBounds.right = availableSpace.width();
            mPieChartBounds.bottom = availableSpace.height() - legendBounds.height();
        } else {

            mPieChartBounds.right = availableSpace.width() - legendBounds.width();
            mPieChartBounds.bottom = availableSpace.height();
        }

        //den chart wieder quadratisch machen
        if (mPieChartBounds.width() != mPieChartBounds.height()) {

            float smallerSide = Math.min(mPieChartBounds.width(), mPieChartBounds.height());
            mPieChartBounds.right = smallerSide;
            mPieChartBounds.bottom = smallerSide;
        }

        return mPieChartBounds;
    }

    /**
     * Methode um die Legende und den PieChart in den ViewBounds zu platzieren.
     *
     * @param legendBounds   Den von der Legende eingenommenen Platz
     * @param pieChartBounds Den von dem PieChart eingenommenen Platz
     */
    private void applyPaddingToChartAndLegend(Rect legendBounds, RectF pieChartBounds) {

        if (!legendBounds.isEmpty()) {
            //offset ist nicht mit eingerechnet wodurch teile der views abgeschnitten sein könnten
            int offset = 0;//todo map to dp
            if (isLegendLeft()) {

                legendBounds.left = getPaddingLeft();
                legendBounds.top = getPaddingTop();
                legendBounds.right += legendBounds.left;
                legendBounds.bottom += legendBounds.top;

                pieChartBounds.left = legendBounds.right + offset;
                pieChartBounds.top += getPaddingTop();
                pieChartBounds.right += legendBounds.width();
            } else if (isLegendTop()) {

                legendBounds.left = getPaddingLeft();
                legendBounds.top = getPaddingTop();
                legendBounds.right += legendBounds.left;
                legendBounds.bottom += legendBounds.top;

                pieChartBounds.left = getPaddingLeft();
                pieChartBounds.top = legendBounds.bottom + offset;
                pieChartBounds.bottom += legendBounds.height();
            } else if (isLegendRight()) {

                pieChartBounds.left = getPaddingLeft();
                pieChartBounds.top = getPaddingTop();

                legendBounds.left = (int) pieChartBounds.right + offset;
                legendBounds.top = getPaddingTop();
                legendBounds.right += legendBounds.left;
                legendBounds.bottom += legendBounds.top;
            } else if (isLegendBottom()) {

                pieChartBounds.left = getPaddingLeft();
                pieChartBounds.top = getPaddingTop();

                legendBounds.left = getPaddingLeft();
                legendBounds.top = (int) pieChartBounds.bottom + offset;
                legendBounds.right += legendBounds.left;
                legendBounds.bottom += legendBounds.top;
            }
        }
    }


    /**
     * Methode um die größe der Legende zu bestimmen.
     * Ist zu wenig platz werden zuerst die Label entfernt,
     * ist immer noch zu wenig platz werden auch die Numeratoren nicht mehr angezeigt.
     * Wenn dann immer noch zu wenig platz sein sollte wird ein leeres Quadrat zurückgegeben.
     *
     * @param availableSpace Maximal verfügbarer Platz
     * @param minChartSize   Größe die der Chart mindestens einnehmen wird
     * @return Platz, welchen die Legende einnimmt
     */
    private Rect resolveLegendSize(Rect availableSpace, int minChartSize) {

        Rect availableLegendSpace = getAvailableLegendSpace(availableSpace, minChartSize);

        for (int i = 0; i < 2; i++) {

            int computedWidth = 0;
            int computedHeight = 0;
            for (PieSlice slice : mPieData.get(mVisibleLayer - 1)) {

                Rect textBounds = getTextBounds(slice.getSliceLabel(), mLegendFontSize);

                if (mLegendDirection.equals(LegendDirections.TOP_TO_BOTTOM)) {

                    computedWidth = Math.max(textBounds.width(), (int) mNumeratorSize);
                    computedHeight += i == 0 ? textBounds.height() + mNumeratorSize : mNumeratorSize;
                } else {

                    computedWidth += i == 0 ? textBounds.width() + mNumeratorSize : mNumeratorSize;
                    computedHeight = Math.max(textBounds.height(), (int) mNumeratorSize);
                }
            }

            if (isElementInBounds(availableLegendSpace, computedWidth, computedHeight)) {

                return new Rect(0, 0, computedWidth, computedHeight);
            }
        }

        return new Rect();
    }

    /**
     * Methode um den Platz zu ermitteln, welcher der Legende zur verfügung steht,
     * basierend auf dem Maximalen Platz und einem weitern Objekt, welches bereits in den Bounds ist
     *
     * @param bounds       Maximaler zur verfügung stehender Platz
     * @param minChartSize Weiteres Objekt, welches in den bounds platziert werden soll
     * @return Verfügbarer Legendenplatz
     */
    private Rect getAvailableLegendSpace(Rect bounds, int minChartSize) {

        Rect availableSpace = new Rect(bounds);
        if (mLegendDirection.equals(LegendDirections.LEFT_TO_RIGHT)) {
            //finde heraus wie viel platz über bzw. unter dem piechart noch zur verfügung steht
            if (isLegendTop()) {
                //finde heraus wie viel platz über dem piechart noch zur verfügung steht
                availableSpace.bottom = bounds.height() - minChartSize;
            } else if (isLegendBottom()) {
                //finde heraus wie viel plart unter dem piechart noch zur verfügung steht
                availableSpace.top = bounds.top + minChartSize;
            }
        } else if (mLegendDirection.equals(LegendDirections.TOP_TO_BOTTOM)) {
            //finde heraus auf welcher seite die legende sein soll
            if (isLegendLeft()) {
                //finde heraus wie viel platz links neben dem piechart noch zur verfügung steht
                availableSpace.right = bounds.width() - minChartSize;
            } else if (isLegendRight()) {
                //finde heraus wie viel platz rechts neben dem piechart noch zur verfügung steht
                availableSpace.left = bounds.left + minChartSize;
            }
        }

        return availableSpace;
    }

    private boolean isLegendTop() {

        return (mLegendPosition.equals(LegendPositions.TOP_LEFT) || mLegendPosition.equals(LegendPositions.TOP_CENTER) || mLegendPosition.equals(LegendPositions.TOP_RIGHT)) && mLegendDirection.equals(LegendDirections.LEFT_TO_RIGHT);
    }

    private boolean isLegendBottom() {

        return (mLegendPosition.equals(LegendPositions.BOTTOM_LEFT) || mLegendPosition.equals(LegendPositions.BOTTOM_CENTER) || mLegendPosition.equals(LegendPositions.BOTTOM_RIGHT)) && mLegendDirection.equals(LegendDirections.LEFT_TO_RIGHT);
    }

    private boolean isLegendLeft() {

        return (mLegendPosition.equals(LegendPositions.TOP_LEFT) || mLegendPosition.equals(LegendPositions.BOTTOM_LEFT)) && mLegendDirection.equals(LegendDirections.TOP_TO_BOTTOM);
    }

    private boolean isLegendRight() {

        return (mLegendPosition.equals(LegendPositions.TOP_RIGHT) || mLegendPosition.equals(LegendPositions.BOTTOM_RIGHT)) && mLegendDirection.equals(LegendDirections.TOP_TO_BOTTOM);
    }

    /**
     * Methode um zu überprüfen, ob ein Element in einem Container platz findet.
     *
     * @param bounds        Container
     * @param elementWidth  Breite des Elements
     * @param elementHeight Höhe des Elements
     * @return Passt das Element in den Container
     */
    private boolean isElementInBounds(Rect bounds, int elementWidth, int elementHeight) {

        return elementWidth <= bounds.width() && elementHeight <= bounds.height();
    }

    /**
     * Methode den von einem String eingenommenen Platz zu ermitteln
     * source: https://stackoverflow.com/a/4795393
     *
     * @param text Text dessen Größe bestimmt werden soll
     * @return Textgröße in einem Rect gespeichert
     */
    private Rect getTextBounds(String text, float textSize) {

        Rect textBounds = new Rect();

        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), textBounds);

        return textBounds;
    }

    /**
     * Methode um herauszufinden wie viel Platz maximal zur verfügung steht
     *
     * @param desiredSize Optimale Größe
     * @param measureSpec Kombinierter Wert aus Platz und Layout verhalten
     * @return Int
     */
    private int reconcileSize(int desiredSize, int measureSpec) {

        int mode = MeasureSpec.getMode(measureSpec);
        int sizeInDp = MeasureSpec.getSize(measureSpec);//todo map to dp

        if (mode == MeasureSpec.EXACTLY)
            return sizeInDp;

        if (mode == MeasureSpec.AT_MOST)
            return Math.min(sizeInDp, desiredSize);

        if (mode == MeasureSpec.UNSPECIFIED)
            return desiredSize;

        return -1;
    }

    /**
     * Methode um Pixel in DensityPixel umzuwandeln.
     * source: https://stackoverflow.com/a/19953871/9376633
     *
     * @param px Zu konvertierende pixel
     * @return In Dp konvertierte pixel
     */
    private int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Methode um DensityPixel in Pixel umzuwandeln.
     * source: https://stackoverflow.com/a/19953871/9376633
     *
     * @param dp Zu konvertierende dp
     * @return In px konvertierte dp
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    //ab hier werden visuelle sachen behandelt

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
                canvas.drawArc(mPieChartBounds, slice.getStartAngle(), slice.getPercentValue() - 2f, true, mSlicePaint);
            else
                canvas.drawArc(mPieChartBounds, slice.getStartAngle(), slice.getPercentValue(), true, mSlicePaint);
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
        mHoleRadius = (float) (mPieChartBounds.width() / 2 * (mHoleSize / 100));

        canvas.drawCircle(mPieChartBounds.centerX(), mPieChartBounds.centerY(), mHoleRadius, mHolePaint);

        if (mTransparentCircle)
            canvas.drawCircle(mPieChartBounds.centerX(), mPieChartBounds.centerY(), mHoleRadius + (mHoleRadius * 0.25f), mTransparentPaint);
    }

    /**
     * Methode um die Legende zu zeichnen
     *
     * @param canvas Canvas Objekt auf dem die Legende gezeichnet werden soll
     *               todo legende in die richtige richtung (mLegendDirection) zeichnen
     */
    private void drawLegend(Canvas canvas) {

        if (legendBounds.isEmpty())
            return;

        boolean drawLabels = true;
        if (mPieData.get(mVisibleLayer - 1).size() * (mNumeratorSize + 5) == legendBounds.width())
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

    //ab hier werden klick ereignisse behandelt

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
     * @return 0 wenn auf den PieChart geklickt wurde, 1 wenn auf die Legende geklickt wurde, -1 wenn "nichts" angeklickt wurde
     */
    private int clickableArea(Point click) {

        if (mDrawHole && Math.sqrt(Math.pow((mPieChartBounds.centerX() - click.x), 2) + Math.pow((mPieChartBounds.centerY() - click.y), 2)) <= mHoleRadius)
            return -1;//hole click

        if (legendBounds.contains(click.x, click.y))
            return 1;//legend click

        if (Math.sqrt(Math.pow((mPieChartBounds.centerX() - click.x), 2) + Math.pow((mPieChartBounds.centerY() - click.y), 2)) <= mPieChartBounds.width() / 2)
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

        double tx = point.x - mPieChartBounds.centerX(), ty = point.y - mPieChartBounds.centerY();
        double length = Math.sqrt(tx * tx + ty * ty);
        double r = Math.acos(ty / length);

        float angle = (float) Math.toDegrees(r);

        if (point.x > mPieChartBounds.centerX())
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

        //todo wenn nur noch ein slice zu sehen ist, kann dieser nicht mehr versteck werden!
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

        //TODO
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
