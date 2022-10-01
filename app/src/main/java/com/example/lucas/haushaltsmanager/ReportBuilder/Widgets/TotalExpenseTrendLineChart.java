package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TotalExpenseTrendLineChart implements Widget {
    private final LineChart lineChart;

    private final int defaultTextColor = R.color.primary_text_color;
    private final int defaultGridLineColor = R.color.grey;
    private final int defaultZeroLineColor = R.color.colorAccent;
    private final int defaultLineColor = R.color.colorPrimary;

    public TotalExpenseTrendLineChart(Context context) {
        this.lineChart = new LineChart(context);
        this.lineChart.setNoDataText(context.getString(R.string.no_data));
        this.lineChart.getLegend().setEnabled(false);
        this.lineChart.setHighlightPerTapEnabled(false);
        this.lineChart.setHighlightPerDragEnabled(false);
        this.lineChart.getDescription().setEnabled(false);
        this.lineChart.getAxisRight().setEnabled(false);

        YAxis yAxis = this.lineChart.getAxisLeft();
        yAxis.setEnabled(true);
        yAxis.enableGridDashedLine(10F, 10F, 0F);
        yAxis.setDrawLabels(true);
        yAxis.setDrawZeroLine(true);
        yAxis.setZeroLineWidth(1F);
        yAxis.setZeroLineColor(context.getColor(defaultZeroLineColor));
        yAxis.setTextColor(context.getColor(defaultTextColor));
        yAxis.setGridColor(context.getColor(defaultGridLineColor));

        XAxis xAxis = this.lineChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.enableGridDashedLine(2F, 7F, 0F);
        xAxis.setGridColor(context.getColor(defaultGridLineColor));
        xAxis.setTextColor(context.getColor(defaultTextColor));
        xAxis.setLabelRotationAngle(315f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // TODO: Does this still work if I have bookings from multiple years?
                return new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date((long) value));
            }
        });
        xAxis.setTextColor(context.getColor(defaultTextColor));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    @Override
    public View getView() {
        return this.lineChart;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_line_chart_black;
    }

    @Override
    public void updateView(List<Booking> bookings) {
        LineData data = createDataFromBookings(bookings);

        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
    }

    public Widget cloneWidget(Context context) {
        return new TotalExpenseTrendLineChart(context);
    }

    private LineData createDataFromBookings(List<Booking> bookings) {
        LineDataSet lds = createDataSet(bookings);
        lds.setDrawIcons(false);
        lds.setForm(Legend.LegendForm.LINE);
        lds.setLineWidth(2.5F);
        lds.setDrawValues(false);
        lds.setColor(lineChart.getContext().getColor(defaultLineColor));

        return new LineData(lds);
    }

    private LineDataSet createDataSet(List<Booking> bookings) {
        List<Entry> dataSet = new ArrayList<>();

        float totalAmount = 0;
        Long previousDate = null;
        for (Booking booking : bookings) {
            totalAmount += booking.getPrice().getPrice();

            if (null != previousDate && isSameDay(previousDate, booking.getDate().getTimeInMillis())) {
                Entry previousEntry = dataSet.get(dataSet.size() - 1);
                previousEntry.setY(previousEntry.getY() + (float) booking.getPrice().getPrice());
            } else {
                dataSet.add(new Entry(booking.getDate().getTimeInMillis(), totalAmount));
            }

            previousDate = booking.getDate().getTimeInMillis();
        }

        return new LineDataSet(dataSet, "");
    }

    private boolean isSameDay(long dayOne, long dayTwo) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        return fmt.format(new Date(dayOne)).equals(fmt.format(new Date(dayTwo)));
    }
}
