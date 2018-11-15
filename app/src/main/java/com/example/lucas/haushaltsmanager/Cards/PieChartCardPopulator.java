package com.example.lucas.haushaltsmanager.Cards;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.ExpenseFilter;
import com.example.lucas.haushaltsmanager.ExpenseSum;
import com.example.lucas.haushaltsmanager.R;
import com.lucas.androidcharts.DataSet;
import com.lucas.androidcharts.PieChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartCardPopulator {
    private CardView mRootView;
    private ViewHolder mViewHolder;
    private boolean mShowExpenditures;

    public PieChartCardPopulator(CardView rootView) {
        mRootView = rootView;

        mShowExpenditures = true;

        initializeViewHolder();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mRootView.setOnClickListener(listener);
    }

    public void setData(ReportInterface report) {
        setCardTitle(report.getCardTitle());

        setPieChart(report);
    }

    public void showIncome() {
        mShowExpenditures = false;
    }

    public void showExpense() {
        mShowExpenditures = true;
    }

    private void setCardTitle(@NonNull String title) {
        mViewHolder.mTitleTxt.setText(title);
    }

    private void setPieChart(ReportInterface report) {
        mViewHolder.mPieChart.setNoDataText(R.string.no_bookings_in_year);
        mViewHolder.mPieChart.setPieData(createDataSets(report.getExpenses()));
    }

    private List<DataSet> createDataSets(List<ExpenseObject> expenses) {
        List<ExpenseObject> expensesWithoutParents = extractChildren(expenses);

        expensesWithoutParents = filterExpenses(expensesWithoutParents, mShowExpenditures);

        HashMap<Category, Double> aggregatedExpenses = sumByCategory(expensesWithoutParents);

        List<DataSet> dataSets = new ArrayList<>();
        for (Map.Entry<Category, Double> entry : aggregatedExpenses.entrySet()) {
            dataSets.add(toDataSet(entry));
        }

        return dataSets;
    }

    private List<ExpenseObject> extractChildren(List<ExpenseObject> expenses) {
        List<ExpenseObject> flatExpenseList = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (expense.isParent())
                flatExpenseList.addAll(expense.getChildren());
            else
                flatExpenseList.add(expense);
        }

        return flatExpenseList;
    }

    private DataSet toDataSet(Map.Entry<Category, Double> entry) {
        return new DataSet(
                entry.getValue().floatValue(),
                entry.getKey().getColorInt(),
                entry.getKey().getTitle()
        );
    }

    private List<ExpenseObject> filterExpenses(List<ExpenseObject> expenses, boolean filter) {
        ExpenseFilter expenseFilter = new ExpenseFilter();

        return expenseFilter.byExpenditureType(expenses, filter);
    }

    private HashMap<Category, Double> sumByCategory(List<ExpenseObject> expenses) {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.byCategory(expenses);
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
