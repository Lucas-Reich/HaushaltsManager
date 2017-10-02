package com.example.lucas.haushaltsmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PieChartTest extends AppCompatActivity implements OnChartValueSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstances) {

        super.onCreate(savedInstances);
        setContentView(R.layout.pie_chart_test);


        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        addDataSet(pieChart);

        pieChart.setOnChartValueSelectedListener(this);
    }


    private void addDataSet(PieChart chart) {

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(8f, 0));
        yValues.add(new Entry(15f, 1));
        yValues.add(new Entry(12f, 2));
        yValues.add(new Entry(25f, 3));
        yValues.add(new Entry(23f, 4));
        yValues.add(new Entry(17f, 5));

        ArrayList<String> xValues = new ArrayList<>();

        xValues.add("January");
        xValues.add("February");
        xValues.add("March");
        xValues.add("April");
        xValues.add("May");
        xValues.add("June");

        PieDataSet pieDataSet = new PieDataSet(yValues, "Election Results");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        chart.setDrawHoleEnabled(false);

        PieData pieData = new PieData(xValues, pieDataSet);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextColor(Color.BLACK);
        pieData.setValueTextSize(18f);

        chart.setDescription("");
        chart.setRotationEnabled(false);
        chart.animateXY(1400, 1400);


        chart.setData(pieData);
        chart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e != null) {

            Log.i("VAL SELECTED", "VALUE: " + e.getVal() + ", xIndex: " + e.getXIndex() + ", DataSet index: " + dataSetIndex);
        }
    }

    @Override
    public void onNothingSelected() {

        Log.i("PieChart", "nothing selected");
    }
}
