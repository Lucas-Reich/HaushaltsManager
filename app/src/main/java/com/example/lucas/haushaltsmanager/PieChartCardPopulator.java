package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.lucas.androidcharts.DataSet;
import com.lucas.androidcharts.PieChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartCardPopulator {
    private static final String TAG = PieChartCardPopulator.class.getSimpleName();

    private CardView mRootView;
    private ViewHolder mViewHolder;
    private Context mContext;

    public PieChartCardPopulator(CardView rootView, Context context) {
        mRootView = rootView;
        mContext = context;

        initializeViewHolder();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mRootView.setOnClickListener(listener);
    }

    public void setData(List<ExpenseObject> data, String chartTitle) {
        populateView(chartTitle, data);
        mRootView.invalidate();
    }

    private void populateView(String title, List<ExpenseObject> data) {
        setCardTitle(title);

        mViewHolder.mPieChart.setPieData(preparePieData2(data));
        mViewHolder.mPieChart.setNoDataText(R.string.no_bookings_in_year);
    }

    private void setCardTitle(@NonNull String title) {
        mViewHolder.mTitleTxt.setText(title);
    }

    private List<DataSet> preparePieData(List<ExpenseObject> data) {
        List<DataSet> dataSet = new ArrayList<>();

        if (data.size() > 0)
            dataSet.add(new DataSet(
                    getIncomeValue(data),
                    getChartColor(R.color.list_item_highlighted),
                    ""
            ));

        return dataSet;
    }

    private List<DataSet> preparePieData2(List<ExpenseObject> data) {
        List<DataSet> dataSet = new ArrayList<>();
        HashMap<Category, Double> summedCategories = sumByCategory(data);

        for (Map.Entry<Category, Double> entry : summedCategories.entrySet()) {
            dataSet.add(new DataSet(
                    entry.getValue().floatValue(),
                    getChartColor(R.color.list_item_highlighted),
                    ""
            ));
        }

        return dataSet;
    }

    private HashMap<Category, Double> sumByCategory(List<ExpenseObject> expenses) {
        HashMap<Category, Double> categories = new HashMap<>();

        for (ExpenseObject expense : expenses) {
            Category expenseCategory = expense.getCategory();

            if (!categories.containsKey(expenseCategory))
                categories.put(expenseCategory, 0d);

            categories.put(expenseCategory, categories.get(expenseCategory) + expense.getSignedPrice());
        }

        return categories;
    }

    private int getChartColor(@ColorRes int color) {
        return mContext.getResources().getColor(color);
    }

    private float getIncomeValue(List<ExpenseObject> data) {
        float totalIncome = 0f;

        for (ExpenseObject expense : data) {
            if (!expense.isExpenditure())
                totalIncome += expense.getUnsignedPrice();
        }

        return totalIncome;
    }

    private void initializeViewHolder() {
        mViewHolder = new ViewHolder();
        mViewHolder.mTitleTxt = mRootView.findViewById(R.id.pie_chart_card_title);
        mViewHolder.mPieChart = mRootView.findViewById(R.id.pie_chart_card_pie);
    }

    private class ViewHolder {
        TextView mTitleTxt;
        PieChart mPieChart;
    }
}
