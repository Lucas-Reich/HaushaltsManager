package com.example.lucas.haushaltsmanager;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Report.Report;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthlyReportAdapterCreator {
    private static final String TAG = MonthlyReportAdapterCreator.class.getSimpleName();

    private ExpenseGrouper mExpenseGrouper;
    private List<ExpenseObject> mExpenses;
    private List<Long> mActiveAccounts;
    private Context mContext;

    public MonthlyReportAdapterCreator(List<ExpenseObject> expenses, Context context, List<Long> activeAccounts) {

        mExpenseGrouper = new ExpenseGrouper();
        mActiveAccounts = activeAccounts;
        mExpenses = extractChildren(expenses);
        mContext = context;
    }

    private List<ExpenseObject> extractChildren(List<ExpenseObject> expenses) {
        List<ExpenseObject> expensesWithExtractedChildren = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (expense.isParent()) {
                for (ExpenseObject child : expense.getChildren())
                    if (isExpenseVisible(child)) {
                        expensesWithExtractedChildren.add(child);
                    }
            } else {
                if (isExpenseVisible(expense)) {
                    expensesWithExtractedChildren.add(expense);
                }
            }
        }

        return expensesWithExtractedChildren;
    }

    public MonthlyReportAdapter getAdapter() {
        List<ReportInterface> reports = createMonthlyReports();

        return new MonthlyReportAdapter(
                mContext,
                reports
        );
    }

    private List<ReportInterface> createMonthlyReports() {
        List<ReportInterface> reports = new ArrayList<>();

        for (int i = getCurrentMonth(); i >= 1; i--) {
            reports.add(new Report(
                    getStringifiedMonth(i - 1),
                    groupExpensesByMonth(i),
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
        String[] months = mContext.getResources().getStringArray(R.array.months);

        return months[month];
    }

    private int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    private Currency getMainCurrency() {
        return new UserSettingsPreferences(mContext).getMainCurrency();
    }

    private Boolean isExpenseVisible(ExpenseObject expense) {
        return mActiveAccounts.contains(expense.getAccountId());
    }
}
