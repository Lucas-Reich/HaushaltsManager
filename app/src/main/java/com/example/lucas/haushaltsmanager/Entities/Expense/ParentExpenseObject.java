package com.example.lucas.haushaltsmanager.Entities.Expense;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.Calendar;
import java.util.List;

public class ParentExpenseObject implements Booking {
    private final long mIndex;
    private final Calendar mDate;
    private final String mTitle;
    private final List<ExpenseObject> mChildren;
    private final Currency mCurrency;


    public ParentExpenseObject(long index, String title, Currency currency, Calendar date, List<ExpenseObject> children) {
        mIndex = index;
        mTitle = title;
        mCurrency = currency;
        mDate = date;
        mChildren = children;
    }

    public static ParentExpenseObject fromParentExpense(ExpenseObject parentExpense) {
        assertIsParent(parentExpense);

        return new ParentExpenseObject(
                parentExpense.getIndex(),
                parentExpense.getTitle(),
                parentExpense.getCurrency(),
                parentExpense.getDate(),
                parentExpense.getChildren()
        );
    }

    public Calendar getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParentExpenseObject)) {
            return false;
        }

        ParentExpenseObject other = (ParentExpenseObject) obj;

        return other.getTitle().equals(getTitle())
                && other.getCurrency().equals(getCurrency())
                && other.getChildren().equals(getChildren());
    }

    public Currency getCurrency() {
        return mCurrency;
    }

    public Price getPrice() {
        return new Price(calcChildrenPrice(), mCurrency);
    }

    public long getIndex() {
        return mIndex;
    }

    public void addChild(ExpenseObject child) {
        mChildren.add(child);
    }

    public List<ExpenseObject> getChildren() {
        return mChildren;
    }

    private static void assertIsParent(ExpenseObject expense) {
        if (!expense.isParent()) {
            throw new IllegalArgumentException(String.format("Given Booking %s is not a ParentExpense", expense.getTitle()));
        }
    }

    private double calcChildrenPrice() {
        double calcPrice = 0;
        for (ExpenseObject child : mChildren) {

            calcPrice += child.getSignedPrice();
        }

        return calcPrice;
    }
}
