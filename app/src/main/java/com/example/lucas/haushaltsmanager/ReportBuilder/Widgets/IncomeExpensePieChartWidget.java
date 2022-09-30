package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IncomeExpensePieChartWidget implements Widget {
    private final PieChart pieChart;
    private final float textSize;
    private final int textColor;

    public IncomeExpensePieChartWidget(Context context) {
        textColor = context.getColor(R.color.white);
        textSize = 15F;

        this.pieChart = new PieChart(context);
        this.pieChart.setNoDataText(context.getString(R.string.no_data));
        this.pieChart.setTouchEnabled(false);
        this.pieChart.setDrawHoleEnabled(false);
        this.pieChart.setDrawCenterText(false);
        this.pieChart.setDrawEntryLabels(true);
        this.pieChart.getLegend().setEnabled(false);
        this.pieChart.setEntryLabelColor(this.textColor);
        this.pieChart.setEntryLabelTextSize(this.textSize);
    }

    @Override
    public View getView() {
        return this.pieChart;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_pie_chart;
    }

    @Override
    public void updateView(List<Booking> bookings) {
        PieData pieData = createDataFromBookings(bookings);

        this.pieChart.setData(pieData);
        pieChart.notifyDataSetChanged();
    }

    public Widget cloneWidget(Context context) {
        return new IncomeExpensePieChartWidget(context);
    }

    private PieData createDataFromBookings(List<Booking> bookings) {
        ExpenseSum sum = new ExpenseSum();

        List<PieEntry> dataSet = new ArrayList<>();
        dataSet.add(new PieEntry(
                (float) Math.abs(sum.byExpenditureType(true, bookings)),
                pieChart.getContext().getString(R.string.expense)
        ));
        dataSet.add(new PieEntry(
                (float) Math.abs(sum.byExpenditureType(false, bookings)),
                pieChart.getContext().getString(R.string.income)
        ));

        return createConfiguredPieData(dataSet);
    }

    private PieData createConfiguredPieData(List<PieEntry> entries) {
        PieDataSet pds = new PieDataSet(entries, "");
        pds.setColors(
                pieChart.getContext().getColor(R.color.booking_expense),
                pieChart.getContext().getColor(R.color.booking_income)
        );
        pds.setValueTextSize(textSize);
        pds.setValueTextColor(textColor);
        pds.setSliceSpace(3F);
        pds.setHighlightEnabled(false);
        pds.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return MoneyUtils.formatHumanReadableWithCurrency(new Price(value), Locale.getDefault());
            }
        });

        return new PieData(pds);
    }
}
