package com.example.lucas.haushaltsmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Reports.Year;
import com.lucas.androidcharts.DataSet;
import com.lucas.androidcharts.PieChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartCardPopulator {
    private static final String TAG = PieChartCardPopulator.class.getSimpleName();

    public static final boolean EXPENDITURE_CHART = true;
    public static final boolean INCOME_CHART = false;

    private CardView mRootView;
    private ViewHolder mViewHolder;
    private boolean mChartType;

    public PieChartCardPopulator(CardView rootView) {
        mRootView = rootView;

        initializeViewHolder();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mRootView.setOnClickListener(listener);
    }

    public void setData(Year year, boolean chartType) {
        setCardTitle(year.getCardTitle());

        mChartType = chartType;
        setPieChart(year);
    }

    private void setCardTitle(@NonNull String title) {
        mViewHolder.mTitleTxt.setText(title);
    }

    private void setPieChart(Year year) {
        mViewHolder.mPieChart.setPieData(preparePieData(year));
        mViewHolder.mPieChart.setNoDataText(R.string.no_bookings_in_year);
    }

    private List<DataSet> preparePieData(Year year) {
        if (year.getBookingCount() == 0)
            return new ArrayList<>();

        List<DataSet> dataSet = new ArrayList<>();
        for (Map.Entry<Category, Double> entry : sumByCategory(year.getExpenses()).entrySet()) {
            dataSet.add(new DataSet(
                    entry.getValue().floatValue(),
                    entry.getKey().getColorInt(),
                    entry.getKey().getTitle()
            ));
        }

        return dataSet;
    }

    private HashMap<Category, Double> sumByCategory(List<ExpenseObject> expenses) {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.sumBookingsByCategory(expenses);
    }

    /**
     * Die Ausgabe wird nur angezeigt, wenn sie vom gleichen Typ wie der ChartType ist.
     *
     * @param expense Zu überprüfende Ausgabe
     * @return TRUE wenn die Ausgabe angezeigt werden soll, FALSE wenn nicht.
     */
    private boolean displayExpense(ExpenseObject expense) {
        return expense.isExpenditure() == mChartType;
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
