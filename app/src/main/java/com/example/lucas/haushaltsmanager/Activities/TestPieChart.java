package com.example.lucas.haushaltsmanager.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.lucas.haushaltsmanager.CustomViews.PieChart;
import com.example.lucas.haushaltsmanager.DataSet;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class TestPieChart extends AppCompatActivity {

    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.test_pie_chart);


        PieChart test = (PieChart) findViewById(R.id.test_chart);
        //test.setPieData(preparePieData()); todo einschalten, wenn setPieData(List<DataSet>) funktioniert

        float[] pieData = {30, 20, 19, 15, 7, 4, 4, 1};
        int[] sliceColors = {Color.YELLOW, Color.GREEN, Color.RED, Color.MAGENTA, Color.BLACK, Color.BLUE, Color.GRAY, Color.RED};
        String[] sliceLabels = {"Label", "Label", "Label", "Label", "Label", "Label", "Label", "Label"};

        test.setPieData(pieData, sliceColors, sliceLabels);
        test.setSliceColors(sliceColors);
    }

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
}
