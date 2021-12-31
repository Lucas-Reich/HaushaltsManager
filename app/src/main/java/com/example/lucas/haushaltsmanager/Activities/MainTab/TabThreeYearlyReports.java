package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;

import com.example.lucas.haushaltsmanager.CardPopulator.LineChartCardPopulator;
import com.example.lucas.haushaltsmanager.CardPopulator.PieChartCardPopulator;
import com.example.lucas.haushaltsmanager.CardPopulator.TimeFrameCardPopulator;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseGrouper;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;
import com.example.lucas.haushaltsmanager.entities.Report;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import java.util.HashMap;
import java.util.List;

public class TabThreeYearlyReports extends AbstractTab {
    private LineChartCardPopulator mLineChartPopulator;
    private PieChartCardPopulator mIncomeCardPopulator, mExpenseCardPopulator;
    private TimeFrameCardPopulator mTimeFrameCardPopulator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View rootView = inflater.inflate(R.layout.tab_three_yearly_reports, container, false);

        List<Booking> bookings = getVisibleExpenses();

        mTimeFrameCardPopulator = new TimeFrameCardPopulator(
                rootView.findViewById(R.id.tab_three_timeframe_report_card),
                getResources()
        );
        mTimeFrameCardPopulator.setData(createReport(
                getStringifiedYear(),
                bookings
        ));

        mIncomeCardPopulator = new PieChartCardPopulator(
                rootView.findViewById(R.id.tab_three_income_card)
        );
        mIncomeCardPopulator.showIncome();
        mIncomeCardPopulator.setData(createReport(
                getString(R.string.income),
                bookings
        ));

        mExpenseCardPopulator = new PieChartCardPopulator(
                rootView.findViewById(R.id.tab_three_expense_card)
        );
        mExpenseCardPopulator.showExpense();
        mExpenseCardPopulator.setData(createReport(
                getString(R.string.expense),
                bookings
        ));

        mLineChartPopulator = new LineChartCardPopulator(
                rootView.findViewById(R.id.tab_three_line_chart),
                getLastYearAccountBalance(CalendarUtils.getCurrentYear(), bookings)
        );
        mLineChartPopulator.setResources(getResources(), CalendarUtils.getCurrentYear());
        mLineChartPopulator.setData(createReport(
                getString(R.string.account_balance),
                bookings
        ));

        return rootView;
    }

    public void updateView(View rootView) {
        Report report = createReport("", getVisibleExpenses());

        report.setTitle(getStringifiedYear());
        mTimeFrameCardPopulator.setData(report);

        report.setTitle(getString(R.string.account_balance));
        mLineChartPopulator.setData(report);

        report.setTitle(getString(R.string.income));
        mIncomeCardPopulator.setData(report);

        report.setTitle(getString(R.string.expense));
        mExpenseCardPopulator.setData(report);
    }

    private List<Booking> getVisibleExpenses() {
        return AppDatabase.getDatabase(getContext()).bookingDAO().getAll();
    }

    private double getLastYearAccountBalance(int currentYear, List<Booking> bookings) {
        HashMap<Integer, Double> mAccountBalanceYear = new ExpenseSum().byYear(bookings);

        int lastYear = currentYear - 1;

        if (mAccountBalanceYear.containsKey(lastYear)) {
            return mAccountBalanceYear.get(lastYear);
        }

        return 0d;
    }

    private Report createReport(String title, List<Booking> bookings) {
        return new Report(
                title,
                filterByYear(bookings, CalendarUtils.getCurrentYear())
        );
    }

    private List<Booking> filterByYear(List<Booking> bookings, int year) {
        return new ExpenseGrouper().byYear(bookings, year);
    }

    private String getStringifiedYear() {
        return String.valueOf(CalendarUtils.getCurrentYear());
    }
}
