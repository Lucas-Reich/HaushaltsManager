package com.example.lucas.haushaltsmanager.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;

import com.example.lucas.haushaltsmanager.Views.PieChart.PieChart;
import com.example.lucas.haushaltsmanager.DataSet;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    /*protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.test_activity);

        PieChart test = (PieChart) findViewById(R.id.test_chart);
        test.setPieData(preparePieData());

        PieChart test2 = (PieChart) findViewById(R.id.test_chart_2);
        test2.setPieData(preparePieData());

        PieChart test3 = (PieChart) findViewById(R.id.test_chart_3);
        test3.setPieData(preparePieData());
    }*/

    private List<DataSet> preparePieData() {
        List<DataSet> pieData = new ArrayList<>();
        pieData.add(new DataSet(30f, Color.YELLOW, "Label"));
        pieData.add(new DataSet(20f, Color.GREEN, "Label"));
        pieData.add(new DataSet(19f, Color.RED, "Label"));
        pieData.add(new DataSet(15f, Color.MAGENTA, "Label"));
        pieData.add(new DataSet(7f, Color.BLACK, "Label"));
        pieData.add(new DataSet(4f, Color.BLUE, "Label"));
        pieData.add(new DataSet(4f, Color.GRAY, "Label"));
        pieData.add(new DataSet(1f, Color.RED, "Label"));

        return pieData;
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.monthly_report_card);

        CardView test = (CardView) findViewById(R.id.monthly_item_card_view);
        PieChart test2 = (PieChart) findViewById(R.id.monthly_item_pie_chart);
        test2.useCompressedChart(true);
        test2.setPieData(preparePieData());
    }
}
