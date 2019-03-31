package com.example.lucas.haushaltsmanager.Entities.Expense;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;

import java.util.Calendar;
import java.util.List;

public class ParentExpenseObject implements Booking {
    private List<ExpenseObject> mChildren;
    private String mTitle;
    private Currency mCurrency;

    @Deprecated
    // Ich würde gerne die Parentinformationen deprecaten, da das ParentExpenseObject keine referenz auf eine Buchung brauch
    private ExpenseObject mParent;
    private Calendar mDate;

    public ParentExpenseObject(ExpenseObject parent, List<ExpenseObject> children) {
        mParent = parent;
        mTitle = parent.getTitle();
        mCurrency = new UserSettingsPreferences(app.getContext()).getMainCurrency();
        mDate = parent.getDateTime();

        mChildren = children;
    }

    public static ParentExpenseObject fromParentExpense(ExpenseObject parentExpense) {
        assertIsParent(parentExpense);

        return new ParentExpenseObject(
                parentExpense,
                parentExpense.getChildren()
        );
    }

    private static void assertIsParent(ExpenseObject expense) {
        if (!expense.isParent()) {
            throw new IllegalArgumentException(String.format("Given Booking %s is not a ParentExpense", expense.getTitle()));
        }
    }

    public ExpenseObject getParent() {
        return mParent;
    }

    public void addChild(ExpenseObject child) {
        mChildren.add(child);
    }

    public List<ExpenseObject> getChildren() {
        return mChildren;
    }

    public Calendar getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public Currency getCurrency() {
        return mCurrency;
    }

    public Price getPrice() {
        return new Price(calcChildrenPrice(), mCurrency);
    }

    public boolean isExpenditure() {
        return getPrice().isNegative();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParentExpenseObject)) {
            return false;
        }

        ParentExpenseObject other = (ParentExpenseObject) obj;

        return other.getParent().equals(getParent())
                && other.getTitle().equals(getTitle())
                && other.getCurrency().equals(getCurrency())
                && other.getChildren().equals(getChildren());
    }

    private double calcChildrenPrice() {
        double calcPrice = 0;
        for (ExpenseObject child : mChildren) {

            calcPrice += child.getSignedPrice();
        }

        return calcPrice;
    }
}
