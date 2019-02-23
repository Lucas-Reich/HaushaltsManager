package com.example.lucas.haushaltsmanager.ListAdapter.AdapterCreator;

import android.content.res.Resources;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Report.Report;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseGrouper;
import com.example.lucas.haushaltsmanager.ListAdapter.MonthlyReportAdapter;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthlyReportAdapterCreator {
    private ExpenseGrouper mExpenseGrouper;
    private List<ExpenseObject> mExpenses;
    private Resources mResources;

    public MonthlyReportAdapterCreator(List<ExpenseObject> expenses, Resources resources) {
        mExpenseGrouper = new ExpenseGrouper();
        mExpenses = extractChildren(expenses);
        mResources = resources;
    }

    private List<ExpenseObject> extractChildren(List<ExpenseObject> expenses) {
        List<ExpenseObject> expensesWithExtractedChildren = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (expense.isParent())
                expensesWithExtractedChildren.addAll(expense.getChildren());
            else
                expensesWithExtractedChildren.add(expense);
        }

        return expensesWithExtractedChildren;
    }

    public MonthlyReportAdapter getAdapter() {
        List<ReportInterface> reports = createMonthlyReports();

        return new MonthlyReportAdapter(
                reports,
                mResources
        );
    }

    private List<ReportInterface> createMonthlyReports() {
        List<ReportInterface> reports = new ArrayList<>();

        for (int i = getCurrentMonth(); i >= 1; i--) {
            reports.add(new Report(
                    getStringifiedMonth(i - 1),
                    groupExpensesByMonth(i - 1),
                    getMainCurrency()
            ));
        }

        return reports;
    }

    private List<ExpenseObject> groupExpensesByMonth(int month) {
        return mExpenseGrouper.byMonth(
                mExpenses,
                month,
                getCurrentYear()
        );
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private String getStringifiedMonth(int month) {
        String[] months = app.getContext().getResources().getStringArray(R.array.months);

        return months[month];
    }

    private int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    private Currency getMainCurrency() {
        return new UserSettingsPreferences(app.getContext()).getMainCurrency();
    }
}
