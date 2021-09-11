package com.example.lucas.haushaltsmanager.Utils.ExpenseUtils;

import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.Entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.Entities.Booking.ParentBooking;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpenseFilter {

    public List<Booking> byExpenditureType(List<Booking> expenses, boolean filter) {
        List<Booking> filteredExpenses = new ArrayList<>();

        for (Booking expense : expenses) {
            if (expense.getPrice().isNegative() != filter) {
                continue;
            }

            filteredExpenses.add(expense);
        }

        return filteredExpenses;
    }

    public List<IBooking> byAccountWithParents(List<IBooking> bookings, List<UUID> accounts) {
        List<IBooking> filteredExpenses = new ArrayList<>();

        for (IBooking booking : bookings) {
            IBooking visibleExpense = getVisibleExpense(booking, accounts);
            if (null != visibleExpense) {
                filteredExpenses.add(visibleExpense);
            }
        }

        return filteredExpenses;
    }

    public List<IBooking> byAccountNew(List<IBooking> bookings, List<UUID> accounts) {
        List<IBooking> filteredExpenses = new ArrayList<>();

        for (IBooking booking : bookings) {
            if (booking instanceof ParentBooking) {
                ParentBooking parent = byAccount((ParentBooking) booking, accounts);
                filteredExpenses.add(parent);
                continue;
            }

            if (!hasAccount((Booking) booking, accounts)) {
                continue;
            }

            filteredExpenses.add(booking);
        }

        return filteredExpenses;
    }

    private ParentBooking byAccount(ParentBooking parent, List<UUID> accountIds) {
        List<Booking> childrenWithCorrectAccount = new ArrayList<>();

        for (Booking child : parent.getChildren()) {
            if (!hasAccount(child, accountIds)) {
                continue;
            }

            childrenWithCorrectAccount.add(child);
        }

        return new ParentBooking(
                parent.getId(),
                parent.getTitle(),
                parent.getDate(),
                childrenWithCorrectAccount
        );
    }

    private boolean hasAccount(Booking expense, List<UUID> accounts) {
        return accounts.contains(expense.getAccountId());
    }

    private IBooking getVisibleExpense(IBooking booking, List<UUID> accounts) {
        if (booking instanceof ParentBooking) {
            ParentBooking parent = removeInvisibleChildren((ParentBooking) booking, accounts);

            if (parent.getChildren().size() != 0) {
                return parent;
            }
        } else {
            if (hasAccount((Booking) booking, accounts)) {
                return booking;
            }
        }

        return null;
    }

    private ParentBooking removeInvisibleChildren(ParentBooking expense, List<UUID> accounts) {
        List<Booking> visibleChildren = new ArrayList<>();
        for (Booking child : expense.getChildren()) {
            if (hasAccount(child, accounts)) {
                visibleChildren.add(child);
            }
        }

        return new ParentBooking(
                expense.getId(),
                expense.getTitle(),
                expense.getDate(),
                visibleChildren
        );
    }
}
