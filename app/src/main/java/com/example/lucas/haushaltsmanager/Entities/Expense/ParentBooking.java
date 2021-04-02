package com.example.lucas.haushaltsmanager.Entities.Expense;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ParentBooking implements Booking {
    private final UUID id;
    private Calendar mDate;
    private final String mTitle;
    private final List<ExpenseObject> children;


    public ParentBooking(
            @NonNull UUID id,
            @NonNull String title,
            @NonNull Calendar date,
            @NonNull List<ExpenseObject> children
    ) {
        this.id = id;
        mTitle = title;
        mDate = date;
        this.children = children;
    }

    public ParentBooking(
            @NonNull String title
    ) {
        this(UUID.randomUUID(), title, Calendar.getInstance(), new ArrayList<ExpenseObject>());
    }

    public ParentBooking(
            @NonNull String title,
            @NonNull Calendar date,
            @NonNull List<ExpenseObject> children
    ) {
        this(UUID.randomUUID(), title, date, children);
    }

    public static ParentBooking fromParentExpense(ExpenseObject parentExpense) {
        assertIsParent(parentExpense);

        return new ParentBooking(
                parentExpense.getId(),
                parentExpense.getTitle(),
                parentExpense.getDate(),
                parentExpense.getChildren()
        );
    }

    public UUID getId() {
        return id;
    }

    public Calendar getDate() {
        return mDate;
    }

    public void setDate(Calendar date) {
        this.mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParentBooking)) {
            return false;
        }

        ParentBooking other = (ParentBooking) obj;

        return other.getTitle().equals(getTitle())
                && other.getChildren().equals(getChildren());
    }

    public Price getPrice() {
        return new Price(calcChildrenPrice());
    }

    public void addChild(ExpenseObject booking) {
        children.add(booking);
    }

    public List<ExpenseObject> getChildren() {
        return children;
    }

    private static void assertIsParent(ExpenseObject expense) {
        if (!expense.isParent()) {
            throw new IllegalArgumentException(String.format("Given Booking %s is not a ParentExpense", expense.getTitle()));
        }
    }

    private double calcChildrenPrice() {
        double calcPrice = 0;
        for (ExpenseObject child : children) {

            calcPrice += child.getSignedPrice();
        }

        return calcPrice;
    }
}
