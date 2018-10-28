package com.example.lucas.haushaltsmanager;

import android.support.v7.widget.CardView;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.List;

public class PieChartCardInflater {
    private static final String TAG = PieChartCardInflater.class.getSimpleName();

    private CardView mView;
    private List<ExpenseObject> mData;

    public PieChartCardInflater(CardView view, List<ExpenseObject> data) {
        mView = view;
        mData = data;
    }
}
