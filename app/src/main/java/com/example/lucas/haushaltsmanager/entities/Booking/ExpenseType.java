package com.example.lucas.haushaltsmanager.entities.Booking;

import androidx.annotation.NonNull;

public class ExpenseType {
    private final boolean type;

    private ExpenseType(boolean type) {
        this.type = type;
    }

    public static ExpenseType deposit() {
        return new ExpenseType(false);
    }

    public static ExpenseType expense() {
        return new ExpenseType(true);
    }

    public static ExpenseType load(boolean expenseType) {
        return new ExpenseType(expenseType);
    }

    @Override
    @NonNull
    public String toString() {
        return Boolean.toString(type);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExpenseType)) {
            return false;
        }

        return ((ExpenseType) o).type == type;
    }

    public boolean value() {
        return type;
    }
}
