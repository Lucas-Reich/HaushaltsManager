package com.example.lucas.haushaltsmanager.CardPopulator;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseGrouper;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;
import com.example.lucas.haushaltsmanager.entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.entities.Report;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class LineChartCardPopulator {
    // IMPROVEMENT: Ich kann die Linie, basierend auf dem Kontostand einfärben. Wenn der Kontostand kleiner als 0 ist Rot größer oder gleich 0 grün
    // Trello: https://trello.com/c/uAhKQUnK/62-farbe-des-linecharts-im-tabthree-basierend-auf-dem-kontostand-einfärben
    private final CardView mRootView;
    private ViewHolder mViewHolder;
    private Resources mResources;
    private final double mLastYearAccountBalance; // REFACTOR: Geht das auch anders?
    private int mCurrentYear;

    public LineChartCardPopulator(CardView rootView, double lastYearAccountBalance) {
        mRootView = rootView;

        mLastYearAccountBalance = lastYearAccountBalance;
        initializeViewHolder();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mRootView.setOnClickListener(listener);
    }

    public void setData(Report report) {
        setCardTitle(report.getTitle());

        setLineChart(report);
    }

    public void setResources(Resources resources, int year) {
        mResources = resources;
        mCurrentYear = year;
    }

    private void setCardTitle(String title) {
        mViewHolder.mTitle.setText(title);
    }

    private void setLineChart(Report report) {
        mViewHolder.mLineChart.setData(prepareLineData(report));
        mViewHolder.mLineChart.setBackgroundColor(getColorResource(R.color.primaryBackgroundColorBright));

        mViewHolder.mLineChart.setNoDataText(getStringResource(R.string.no_bookings_in_year));
        mViewHolder.mLineChart.setNoDataTextColor(getColorResource(R.color.booking_expense));

        mViewHolder.mLineChart.setTouchEnabled(false);

        Description desc = new Description();
        desc.setText("");
        mViewHolder.mLineChart.setDescription(desc);


        mViewHolder.mLineChart.getAxisRight().setDrawLabels(false);
        mViewHolder.mLineChart.getLegend().setEnabled(false);

        XAxis xAxis = mViewHolder.mLineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(getXAxisLabels());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mViewHolder.mLineChart.invalidate();
    }

    private int getColorResource(@ColorRes int color) {
        return ResourcesCompat.getColor(mResources, color, null);
    }

    private String getStringResource(@StringRes int string) {
        return mResources.getString(string);
    }

    private LineData prepareLineData(Report report) {
        LineDataSet lds = new LineDataSet(getChartEntries(report.getBookings()), "");
        lds.setColor(getColorResource(R.color.colorPrimary));
        lds.setCircleColor(getColorResource(R.color.colorPrimary));
        lds.setValueTextColor(getColorResource(R.color.primary_text_color));

        return new LineData(lds);
    }

    private IAxisValueFormatter getXAxisLabels() {
        final String[] month = getMonthsShortened();

        return (value, axis) -> month[(int) value - 1];
    }

    private String[] getMonthsShortened() {
        String[] months = mResources.getStringArray(R.array.months);

        for (int i = 0; i < months.length; i++) {
            months[i] = months[i].substring(0, 3);
        }

        return months;
    }

    private List<Entry> getChartEntries(List<IBooking> bookings) {
        List<Entry> entries = new ArrayList<>();

        List<List<IBooking>> groupedValues = getAccountBalances(bookings);

        float lastValue = (float) mLastYearAccountBalance;
        for (int i = 0; i < 12; i++) {
            entries.add(new Entry(
                    i + 1,
                    lastValue += (float) sum(groupedValues.get(i))
            ));
        }

        return entries;
    }

    private double sum(List<IBooking> bookings) {
        return new ExpenseSum().sumNew(bookings);
    }

    private List<List<IBooking>> getAccountBalances(List<IBooking> expenses) {
        return new ExpenseGrouper().byMonths(expenses, mCurrentYear);
    }

    private void initializeViewHolder() {
        mViewHolder = new ViewHolder();

        mViewHolder.mTitle = mRootView.findViewById(R.id.timeline_report_card_title);
        mViewHolder.mLineChart = mRootView.findViewById(R.id.timeline_report_card_line_chart);
    }

    private static class ViewHolder {
        TextView mTitle;
        LineChart mLineChart;
    }
}
