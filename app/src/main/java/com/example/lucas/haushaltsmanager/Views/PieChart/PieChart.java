package com.example.lucas.haushaltsmanager.Views.PieChart;

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
import com.example.lucas.haushaltsmanager.Views.PieChart.Legend.Legend;
import com.example.lucas.haushaltsmanager.Views.PieChart.Legend.LegendItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PieChart extends View {

    private String TAG = PieChart.class.getSimpleName();

    private RectF mViewBounds;

    private Legend mLegend;
    private boolean mDrawLegend;

    /**
     * Flag die angibt, ob die Labels neben den Legendenelementen angezeigt werden sollen oder nicht.
     */
    private boolean mDrawLegendLabels = true;
    private TextPaint mNumeratorFontPaint;
    private Rect mNumeratorFontBounds;
    private Paint mNumeratorPaint;
    private RectF mNumeratorRect;
    private float mNumeratorSize;
    private float mLegendFontSize = 15f;

    /**
     * Abstand zwischen den einzelnen Legendenelementen
     */
    private int mNumeratorItemOffset = dpToPx(5);

    /**
     * Legendencontainer
     */
    private Rect mLegendBounds;

    private int mNumeratorChartPadding = dpToPx(10);

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
    private float mDataTotal;
    private Paint mSlicePaint;
    private RectF mPieChartBounds;
    private String mNoDataText;
    private boolean mPercentageValues = false;
    private boolean mSliceMargin;
    private boolean mDrawCenterText = false;
    private String mCenterText = "";
    private float mFontSize = 35f;
    private Paint mFontPaint;
    private Boolean mTouchEnabled = false;

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

        mNoDataText = getResources().getString(R.string.no_data);
        try {

            mDrawHole = a.getBoolean(R.styleable.PieChart_draw_hole, false);
            mHoleSize = a.getInteger(R.styleable.PieChart_hole_size, 50);
            holeColor = a.getColor(R.styleable.PieChart_hole_color, Color.WHITE);
            mNoDataText = a.getString(R.styleable.PieChart_no_data_text);
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

            mNumeratorSize = dpToPx(20);

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
        mLegendBounds = new Rect();

        mFontPaint = new Paint();
        mFontPaint.setAntiAlias(true);
        mFontPaint.setColor(Color.RED);
        mFontPaint.setTextSize(mFontSize);

        mLegend = new Legend(mLegendDirection);
    }

    public void setHoleColor(@ColorInt int color) {

        this.mHolePaint.setColor(color);
    }

    public int getHoleColor() {

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

    public int getNumeratorColor() {

        return this.mNumeratorPaint.getColor();
    }

    public void drawHole(boolean drawHole) {

        if (drawHole != mDrawHole) {

            mDrawHole = drawHole;
            invalidate();
            requestLayout();
        }
    }

    public void setCenterText(@NonNull String centerText) {

        mCenterText = centerText;
    }

    @NonNull
    public String getCenterText() {

        return mCenterText;
    }

    public void enableCenterText() {

        mDrawCenterText = true;
    }

    public void disableCenterText() {

        mDrawCenterText = false;
    }

    public boolean isCenterTextDrawingEnabled() {

        return mDrawCenterText;
    }

    public void setCenterTextColor(@ColorInt int textColor) {

        mFontPaint.setColor(textColor);
    }

    public void setNoDataText(@NonNull String noDataText) {

        mNoDataText = noDataText;
    }


    //Ab hier werden neue PieDaten gesetzt


    /**
     * Methode um den PieChart mit einem neuen Datensatz zu befüllen
     *
     * @param pieData Liste mit Datensätzen
     */
    public void setPieData(List<DataSet> pieData) {
        if (pieData.size() == 0)
            return;

        mTouchEnabled = true;

        mDataTotal = getTotal(pieData);
        preparePieData(pieData);
        convertToSlices(pieData);

        if (mCompressed)
            createCompressedSlices(getTotal(pieData));

        invalidate();
    }

    /**
     * Methode um bereits existierende Daten zu löschen und die vom User gegebene Liste aufsteigend zu sortieren
     *
     * @param dataSets Vom User gegebene Liste
     */
    private void preparePieData(List<DataSet> dataSets) {
        mPieData.clear();
        Collections.sort(dataSets);
    }

    /**
     * Methode um die vom User gegebenen Datensätze in PieSlice Objekte umzuwandeln
     *
     * @param dataSets Vom User gegebene Daten
     */
    private void convertToSlices(List<DataSet> dataSets) {

        float startAngle = 0;
        List<PieSlice> defaultLayer = new ArrayList<>();
        for (DataSet dataSet : dataSets) {

            float percentValue = getPieArea(mDataTotal, dataSet.getValue());
            PieSlice slice = new PieSlice(dataSet.getValue(), percentValue, startAngle);
            slice.setWeight(mLayer);
            slice.setSliceColor(dataSet.getColor());
            slice.setSliceLabel(dataSet.getLabel());

            defaultLayer.add(slice);
            startAngle += percentValue;
        }

        mPieData.add(defaultLayer);
    }

    /**
     * Methode um einen Prozentualen Wert zu einem absoluten Wert zu bekommen
     *
     * @param total     Totale Kreisfläche
     * @param dataValue Wert welcher auf die Kreisfläche umgerechnet werden soll
     * @return Eingenommener Wert in Prozent
     */
    private float getPieArea(float total, float dataValue) {

        return dataValue / total * 360f;
    }

    /**
     * Methode um einer Liste von PieSlices die passenden Gewichte zuzuordnen
     */
    private List<PieSlice> assignWeightsToSlices(List<PieSlice> pieSlices) {

        float tempTotal = 0;
        int currentLayer = mLayer;
        for (PieSlice slice : pieSlices) {

            if (slice.getAbsValue() + tempTotal <= mDataTotal * Math.pow(0.33, currentLayer - 1))
                slice.setWeight(currentLayer);
            else
                slice.setWeight(--currentLayer);

            addSliceToLayer(slice);
            tempTotal += slice.getAbsValue();
        }

        return pieSlices;
    }

    /**
     * Methode um ein gewogenes slice in das richtige Datenlayer zu packen.
     *
     * @param slice Slice, welches einsortiert werden soll.
     */
    private void addSliceToLayer(PieSlice slice) {
        mPieData.get(slice.getWeight()).add(slice);
    }

    /**
     * Methode um den PieSlices ein passendes Gewicht zuzuordnen. Dabei werden in einem Layer ca. 2/3 des Gesamtvolumens angezeigt.
     * Außerdem werden die Slices basierend auf ihrem Gewicht in die passende liste in mPieData eingeordnet.
     */
    private void createCompressedSlices(float total) {

        //todo gewichts zuordnung noch einmal überarbeiten

        //die layer information wird mit leeren listen befüllt die dann später die slices der verschiedenen layer enthalten
        for (int i = 0; i < mLayer; i++) {

            mPieData.add(new ArrayList<PieSlice>());
        }

        //hier werden den slices passende gewichte zugeordnet und außerdem werden sie gleich richtig sortiert
        float tempTotal = 0;
        int currentWeight = mLayer;
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
        tempTotal = total;
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
                //insertOthersPlaceholder(layerSlicesList);
            }

            tempTotal -= currentTotal;
        }
    }

    /**
     * Methode um einen Platzhalten in die Liste der Slice einzufügen,
     * welcher die momentan nicht sichtbaren Slices enthält
     */
    private void insertOthersPlaceholder(List<PieSlice> layerSlicesList) {
/*
        PieSlice slice = new PieSlice(tempTotal - currentTotal, getPieArea(tempTotal, tempTotal - currentTotal), startAngle);
        slice.setSliceLabel(getResources().getString(R.string.others));
        layerSlicesList.add(slice);
        */
    }

    /**
     * Methode um den aufsummierten Wert der dataSets zu erhalten.
     *
     * @param dataSets Aufzusummierende Datensätze
     * @return Aufummierter Wert
     */
    private float getTotal(List<DataSet> dataSets) {
        float total = 0;
        for (DataSet dataSet : dataSets)
            total += dataSet.getValue();

        return total;
    }


    //ab hier wird die Größe des PieCharts bestimmt


    /**
     * Methode um den minimalen Platz des PieCharts in pixeln zu ermitteln
     *
     * @return PieChartbounds
     */
    private Rect getChartDesiredSize() {

        return new Rect(0, 0, dpToPx(200), dpToPx(200));
    }

    /**
     * Methode um den Minimalen Platz der Legende in pixeln zu ermitteln
     *
     * @return Legendenbounds
     */
    private Rect getLegendDesiredSize() {
        if (mPieData.isEmpty())
            return new Rect();

        int width = 0, height = 0;
        for (PieSlice slice : getVisibleSlices()) {

            Rect textSize = getTextBounds(slice.getLabel(), mLegendFontSize);
            if (mLegendDirection.equals(LegendDirections.LEFT_TO_RIGHT)) {

                width += mNumeratorSize + textSize.width() + mNumeratorItemOffset;
                height = (int) Math.max(textSize.height(), mNumeratorSize) + mNumeratorChartPadding;
            } else {

                width = (int) mNumeratorSize + textSize.width() + mNumeratorChartPadding;
                height += Math.max(mNumeratorSize, textSize.height()) + mNumeratorItemOffset;
            }
        }

        return new Rect(0, 0, width, height);
    }

    /**
     * Methode um die größe der View zu ermitteln
     *
     * @param widthMeasureSpec  Breiteninformationen
     * @param heightMeasureSpec Höheninformationen
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMaxViewBounds(widthMeasureSpec, heightMeasureSpec);
        fillLegend();

        //padding von der maximalen größe abziehen
        int widthPadding = getPaddingRight() + getPaddingLeft();
        int heightPadding = getPaddingTop() + getPaddingBottom();
        Rect sizeWithPadding = new Rect(0, 0, (int) mViewBounds.width() - widthPadding, (int) mViewBounds.height() - heightPadding);


        //herausfinden wie groß die legende sein kann
        int minChartSize = dpToPx(200);
        resolveLegendSize(sizeWithPadding, minChartSize);
        mPieChartBounds = resolveChartSize(mLegendBounds, sizeWithPadding);

        applyPaddingToChartAndLegend(mLegendBounds, mPieChartBounds);
        setMeasuredDimension((int) mViewBounds.width(), (int) mViewBounds.height());
    }

    /**
     * Methode um die maximale Größe der View zu ermitteln
     *
     * @param widthMeasureSpec  Breiteninformationen
     * @param heightMeasureSpec Höheninformationen
     */
    private void setMaxViewBounds(int widthMeasureSpec, int heightMeasureSpec) {

        mViewBounds.set(0, 0, 0, 0);
        if (isLegendBottom() || isLegendTop()) {

            mViewBounds.right = reconcileSize(Math.max(getChartDesiredSize().width(), getLegendDesiredSize().width()), widthMeasureSpec);
            mViewBounds.bottom = reconcileSize(getChartDesiredSize().height() + getLegendDesiredSize().height(), heightMeasureSpec);
        } else {

            mViewBounds.right = reconcileSize(getChartDesiredSize().width() + getLegendDesiredSize().width(), widthMeasureSpec);
            mViewBounds.bottom = reconcileSize(Math.max(getChartDesiredSize().height(), getLegendDesiredSize().height()), heightMeasureSpec);
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
     */
    private void resolveLegendSize(Rect availableSpace, int minChartSize) {
        Rect availableLegendSpace = getAvailableLegendSpace(availableSpace, minChartSize);
        mLegendBounds.setEmpty();

        if (mPieData.isEmpty())
            return;

        for (int i = 0; i < 2; i++) {
            if (i > 0)
                mDrawLegendLabels = false;

            for (PieSlice slice : getVisibleSlices()) {
                Rect textBounds = getTextBounds(slice.getLabel(), mLegendFontSize);

                if (mLegendDirection.equals(LegendDirections.LEFT_TO_RIGHT)) {

                    mLegendBounds.right += i == 0 ? textBounds.width() + mNumeratorSize + mNumeratorItemOffset : mNumeratorSize + mNumeratorItemOffset;
                    mLegendBounds.bottom = Math.max(textBounds.height(), (int) mNumeratorSize) + mNumeratorChartPadding;
                } else {

                    mLegendBounds.right = i == 0 ? textBounds.width() + (int) mNumeratorSize + mNumeratorChartPadding : (int) mNumeratorSize + mNumeratorChartPadding;
                    mLegendBounds.bottom += Math.max(textBounds.height(), mNumeratorSize) + mNumeratorItemOffset;
                }
            }

            if (availableLegendSpace.contains(mLegendBounds))
                return;

            mLegendBounds.setEmpty();
        }
    }

    /**
     * Methode um das Legendenelement mit LegendenItems zu befüllen
     */
    private void fillLegend() {

        for (PieSlice slice : getVisibleSlices()) {

            Rect textBounds = getTextBounds(slice.getLabel(), mLegendFontSize);
            int width = (int) mNumeratorSize + textBounds.width();
            int height = (int) Math.max(mNumeratorSize, textBounds.height());
            Rect bounds = new Rect(0, 0, width, height);

            LegendItem legendItem = new LegendItem(
                    slice.getLabel(),
                    slice.getColor(),
                    mNumeratorStyle);
            legendItem.setBounds(bounds);

            mLegend.addItem(legendItem);
        }
    }

    /**
     * Methode um die Größe des PieCharts zu bestimmen.
     * Ist der PieChart nach der bestimmung nicht mehr quadratisch, wird dies gemacht.
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
            if (isLegendLeft()) {

                legendBounds.left = getPaddingLeft();
                legendBounds.top = getPaddingTop();
                legendBounds.right += legendBounds.left;
                legendBounds.bottom += legendBounds.top;

                pieChartBounds.left = legendBounds.right;
                pieChartBounds.top += getPaddingTop();
                pieChartBounds.right += legendBounds.width();
            } else if (isLegendTop()) {

                legendBounds.left = getPaddingLeft();
                legendBounds.top = getPaddingTop();
                legendBounds.right += legendBounds.left;
                legendBounds.bottom += legendBounds.top;

                pieChartBounds.left = getPaddingLeft();
                pieChartBounds.top = legendBounds.bottom;
                pieChartBounds.bottom += legendBounds.height();
            } else if (isLegendRight()) {

                pieChartBounds.left = getPaddingLeft();
                pieChartBounds.top = getPaddingTop();

                legendBounds.left = (int) pieChartBounds.right;
                legendBounds.top = getPaddingTop();
                legendBounds.right += legendBounds.left;
                legendBounds.bottom += legendBounds.top;
            } else if (isLegendBottom()) {

                pieChartBounds.left = getPaddingLeft();
                pieChartBounds.top = getPaddingTop();

                legendBounds.left = getPaddingLeft();
                legendBounds.top = (int) pieChartBounds.bottom;
                legendBounds.right += legendBounds.left;
                legendBounds.bottom += legendBounds.top;
            }
        }
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
     * Methode um herauszufinden wie viel Platz maximal zur verfügung steht
     *
     * @param desiredSize Optimale Größe
     * @param measureSpec Kombinierter Wert aus Platz und Layout verhalten
     * @return Int
     */
    private int reconcileSize(int desiredSize, int measureSpec) {

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
    public int pxToDp(int px) {
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

    //ab hier werden visuelle sachen behandelt

    /**
     * Methode um die momentan sichtbaren PieSlices aus dem gesamten Datensatz zu erhalten.
     *
     * @return Momentan sichtbare PieSlices
     */
    private List<PieSlice> getVisibleSlices() {
        if (mPieData.isEmpty())
            return new ArrayList<>();
        else
            return mPieData.get(mVisibleLayer - 1);
    }

    /**
     * Methode um das Kreisdiagramm zu zeichnen
     *
     * @param canvas Zeichen Objekt auf dem der Kreis gezeichet wird
     */
    @Override
    protected void onDraw(Canvas canvas) {

        if (mPieData.size() == 0) {
            drawNoDataText(canvas);
            return;
        }

        for (PieSlice slice : getVisibleSlices()) {

            mSlicePaint.setColor(slice.getColor());
            if (mSliceMargin)
                canvas.drawArc(mPieChartBounds, slice.getStartAngle(), slice.getPercentValue() - 2f, true, mSlicePaint);
            else
                canvas.drawArc(mPieChartBounds, slice.getStartAngle(), slice.getPercentValue(), true, mSlicePaint);
        }

        if (mDrawHole)
            drawHole(canvas);

        if (mDrawLegend)
            drawLegend(canvas);

        if (mDrawCenterText)
            drawCenterText(canvas, mCenterText);
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
     * Methode um einen Default text anzeigen zu lassen, wenn dem Kreisdiagramm keine Daten zugrunde legen.
     *
     * @param canvas Objekt, auf dem der Text angezeigt werden soll.
     */
    private void drawNoDataText(Canvas canvas) {

        drawCenterText(canvas, mNoDataText);
    }

    /**
     * Methode um innerhalb des Kreises einen Text anzeigen zu lassen.
     * VORSICHT: Der angezeigt Text wird mittig platziert und wird nicht weitergehend platziert.
     * Ist der Text zu lang kann er in das Diagramm hineinragen oder gar aus der View hinaus.
     *
     * @param canvas        Canvas auf der der Text angezeigt wird
     * @param displayString Text der angezeigt werden soll
     */
    private void drawCenterText(Canvas canvas, String displayString) {

        Rect textBounds = getTextBounds(displayString, mFontSize);
        canvas.drawText(displayString, mViewBounds.centerX() - textBounds.width() / 2, mViewBounds.centerY() - textBounds.height() / 2, mFontPaint);
    }

    /**
     * Methode um Legendenelemente eines PieCharts anzeigen zu lassen.
     *
     * @param canvas Canvas auf dem die Legende sichtbar sein soll
     */
    private void drawLegend2(Canvas canvas) {
        if (isLegendHidden())
            return;

        for (PieSlice slice : getVisibleSlices()) {
            drawLegendItem(canvas, slice);
        }
    }

    /**
     * Methode um ein einzelnes Legendenelement zu zeichnen.
     *
     * @param canvas Canvas auf dem das Legendelement sichtbar sein soll
     * @param slice  PieSlice, welcher angezeigt weden soll
     */
    private void drawLegendItem(Canvas canvas, PieSlice slice) {

        drawLegendItemIcon(canvas, slice);
        if (mDrawLegendLabels)
            drawLegendLabel(canvas, slice.getLabel());
    }

    /**
     * Methode um die Legende zu zeichnen
     *
     * @param canvas Canvas Objekt auf dem die Legende gezeichnet werden soll
     *               todo legende in die richtige richtung (mLegendDirection) zeichnen
     */
    private void drawLegend(Canvas canvas) {
        if (isLegendHidden())
            return;

        for (PieSlice slice : getVisibleSlices()) {
            drawLegendItemIcon(canvas, slice);

            if (mDrawLegendLabels)
                drawLegendLabel(canvas, slice.getLabel());


            mNumeratorRect.left += mNumeratorItemOffset;
            mNumeratorRect.right += mNumeratorItemOffset;
        }

        mNumeratorRect.set(0, 0, mNumeratorSize, mNumeratorSize);
    }

    /**
     * Methode die überprüft, ob die Legende sichtbar ist oder nicht.
     * Dabei wird der tatsächlich ausgerechnete Platz der Legende als Referenz genommen.
     *
     * @return Boolean
     */
    private boolean isLegendHidden() {

        return mLegendBounds.isEmpty();
    }

    /**
     * Methode um die Icons der Legendenelemente auf eine canvas zu zeichnen.
     *
     * @param canvas Hauptcanvas
     */
    private void drawLegendItemIcon(Canvas canvas, PieSlice slice) {
        mNumeratorPaint.setColor(slice.getColor());

        switch (mNumeratorStyle) {

            case CIRCLE:
                canvas.drawCircle(
                        mNumeratorRect.centerX(),
                        mNumeratorRect.centerY(),
                        mNumeratorSize / 2,
                        mNumeratorPaint);
                break;

            case SQUARE:
                canvas.drawRect(mNumeratorRect, mNumeratorPaint);
                break;
        }

        if (mLegendDirection == LegendDirections.LEFT_TO_RIGHT) {

            mNumeratorRect.left += mNumeratorSize;
            mNumeratorRect.right += mNumeratorSize;
        } else {

            mNumeratorRect.top += mNumeratorSize;
            mNumeratorRect.bottom += mNumeratorSize;
        }
    }

    /**
     * Methode um ein einzelnes Label auf einer Canvas zu zeichnen
     *
     * @param canvas Canvas auf der das Label angezeigt werden soll
     * @param label  Text der angezezeigt werden soll
     */
    private void drawLegendLabel(Canvas canvas, String label) {
        mNumeratorFontPaint.getTextBounds(label, 0, label.length(), mNumeratorFontBounds);

        if (true) {

            canvas.drawText(label, mNumeratorRect.right, mNumeratorRect.bottom, mNumeratorFontPaint);
        } else {

            //canvas.drawText(label, );
        }
    }

    //ab hier werden klick Ereignisse behandelt

    /**
     * Methode um Touch aktionen des Users abzufangen
     *
     * @param event Interaktionsart des Users
     * @return Boolean ob das event gehandelt wurde
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mTouchEnabled)
            return true;

        Point click = new Point((int) Math.floor(event.getX()), (int) Math.floor(event.getY()));

        //User nimmt den Finger vom Display
        if (event.getAction() == MotionEvent.ACTION_UP) {

            switch (clickableArea(click)) {

                case 0://PieChart
                    Log.d(TAG, "onTouchEvent: Du hast den PieChart angeklickt!");

                    float pointAngle = getAngleForPoint(click);

                    for (PieSlice slice : getVisibleSlices()) {

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

                    for (PieSlice slice : getVisibleSlices()) {

                        mNumeratorFontPaint.getTextBounds(slice.getLabel(), 0, slice.getLabel().length(), mNumeratorFontBounds);

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

        if (mLegendBounds.contains(click.x, click.y))
            return 1;//mLegend click

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

        for (PieSlice slice : getVisibleSlices()) {

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

        for (PieSlice slice : getVisibleSlices()) {

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
        //todo die Slices werden beim aus und einklappen nicht richtig berechnet, sie kommen nicht auf 360°
        stopOngoingAnimations();

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

    /**
     * Methode um alle laufenden Animationen anzuhalten
     */
    private void stopOngoingAnimations() {
        if (mAnimator != null)
            mAnimator.end();
    }
}
