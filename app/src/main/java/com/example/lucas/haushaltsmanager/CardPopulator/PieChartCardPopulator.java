package com.example.lucas.haushaltsmanager.CardPopulator;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseFilter;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartCardPopulator {
    private final CardView mRootView;
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
        mViewHolder.titleTxt.setText(title);
    }

    private void setPieChart(ReportInterface report) {
        mViewHolder.pieChart.setNoDataText(app.getContext().getString(R.string.no_bookings_in_year));
        mViewHolder.pieChart.setData(createDataSet(report.getExpenses()));
    }


    private PieData createDataSet(List<ExpenseObject> bookings) {
        List<ExpenseObject> expensesWithoutParents = extractChildren(bookings);

        expensesWithoutParents = filterExpenses(expensesWithoutParents, mShowExpenditures);

        HashMap<Category, Double> aggregatedExpenses = sumByCategory(expensesWithoutParents);
        return createData(aggregatedExpenses);
    }

    private PieData createData(HashMap<Category, Double> aggregatedExpenses) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (Map.Entry<Category, Double> entry : aggregatedExpenses.entrySet()) {
            colors.add(entry.getKey().getColor().getColorInt());
            entries.add(new PieEntry(
                    Math.abs(entry.getValue().floatValue()),
                    entry.getKey().getTitle()
            ));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);

        return new PieData(dataSet);
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

        mViewHolder.titleTxt = mRootView.findViewById(R.id.pie_chart_card_title);

        mViewHolder.pieChart = mRootView.findViewById(R.id.pie_chart_card_pie);
        mViewHolder.pieChart.getLegend().setEnabled(false);
        mViewHolder.pieChart.getDescription().setEnabled(false);
        mViewHolder.pieChart.setDrawEntryLabels(false);
        mViewHolder.pieChart.setTouchEnabled(false);
        mViewHolder.pieChart.setDrawHoleEnabled(false);
    }

    private static class ViewHolder {
        TextView titleTxt;
        PieChart pieChart;
    }
}
