package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Report.Report;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ReportItem.ReportItem;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseGrouper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateReportItemsStrategy implements RecyclerItemCreatorStrategyInterface<ExpenseObject> {
    private final ExpenseGrouper expenseGrouper;

    public CreateReportItemsStrategy() {
        expenseGrouper = new ExpenseGrouper();
    }

    public List<IRecyclerItem> create(List<ExpenseObject> expenses) {
        if (expenses.isEmpty()) {
            return new ArrayList<>();
        }

        List<IRecyclerItem> reportItems = new ArrayList<>();

        expenses = extractChildren(expenses);
        for (int i = getCurrentMonth(); i >= 1; i--) {
            reportItems.add(new ReportItem(new Report(
                    getStringifiedMonth(i - 1),
                    groupExpensesByMonth(i - 1, expenses),
                    getMainCurrency()
            )));
        }

        return reportItems;
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

    private List<ExpenseObject> groupExpensesByMonth(int month, List<ExpenseObject> expenses) {
        return expenseGrouper.byMonth(
                expenses,
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

