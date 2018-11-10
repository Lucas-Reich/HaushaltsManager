package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Reports.Year;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LineChartCardPopulator {
    private CardView mRootView;
    private ViewHolder mViewHolder;
    private Context mContext;

    public LineChartCardPopulator(CardView rootView, Context context) {
        mRootView = rootView;
        mContext = context;

        initializeViewHolder();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mRootView.setOnClickListener(listener);
    }

    public void setData(Year year) {
        setCardTitle(year.getCardTitle());

        setLineChart(year);
    }

    private void setCardTitle(String title) {
        mViewHolder.mTitle.setText(title);
    }

    private void setLineChart(Year year) {
        mViewHolder.mLineChart.setData(prepareLineData(year));
        mViewHolder.mLineChart.setBackgroundColor(getColorResource(R.color.primaryBackgroundColor));

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
    }

    private int getColorResource(@ColorRes int color) {
        return mContext.getResources().getColor(color);
    }

    private String getStringResource(@StringRes int string) {
        return mContext.getString(string);
    }

    private LineData prepareLineData(Year year) {
        LineDataSet lds = new LineDataSet(getChartEntries(year), "");
        lds.setColor(getColorResource(R.color.colorPrimary));
        lds.setCircleColor(getColorResource(R.color.colorAccent));
        lds.setValueTextColor(getColorResource(R.color.primary_text_color));

        return new LineData(lds);
    }

    private IAxisValueFormatter getXAxisLabels() {
        final String[] month = getMonthsShortened();

        return new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return month[(int) value - 1];
            }
        };
    }

    private String[] getMonthsShortened() {
        String[] months = mContext.getResources().getStringArray(R.array.months);

        for (int i = 0; i < months.length; i++) {
            months[i] = months[i].substring(0, 3);
        }

        return months;
    }

    // TODO: Sollte ich vielleicht den Kontostand anzeigen?
    private List<Entry> getChartEntries(Year year) {
        List<Entry> entries = new ArrayList<>();

        for (int month = 0; month < 12; month++) {
            entries.add(new Entry(
                    month + 1,
                    sumIncomeByMonth(year.getExpenses(), month)
            ));
        }


        return entries;
    }

    private int sumIncomeByMonth(List<ExpenseObject> expenses, int month) {
        double income = 0;

        for (ExpenseObject expense : expenses) {
            if (isExpenseInMonth(expense, month) && isExpenseIncome(expense))
                income += expense.getUnsignedPrice();
        }

        return (int) income;
    }

    private boolean isExpenseIncome(ExpenseObject expense) {
        return !expense.isExpenditure();
    }

    private boolean isExpenseInMonth(ExpenseObject expense, int month) {
        return expense.getDateTime().get(Calendar.MONTH) == month;
    }

    private void initializeViewHolder() {
        mViewHolder = new ViewHolder();

        mViewHolder.mTitle = mRootView.findViewById(R.id.timeline_report_card_title);
        mViewHolder.mLineChart = mRootView.findViewById(R.id.timeline_report_card_line_chart);
    }

    private class ViewHolder {
        TextView mTitle;
        LineChart mLineChart;
    }
}
