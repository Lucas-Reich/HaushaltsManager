package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.Cards.LineChartCardPopulator;
import com.example.lucas.haushaltsmanager.Cards.PieChartCardPopulator;
import com.example.lucas.haushaltsmanager.Cards.TimeFrameCardPopulator;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Report.Report;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.ExpenseGrouper;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;

import java.util.Calendar;
import java.util.List;

public class TabThreeYearlyReports extends AbstractMainTab {
    private ParentActivity mParent;
    private UserSettingsPreferences mUserPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParent = (ParentActivity) getActivity();
        mUserPreferences = new UserSettingsPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View rootView = inflater.inflate(R.layout.tab_three_yearly_reports, container, false);

        TimeFrameCardPopulator extendedYearlyChart = new TimeFrameCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_timeframe_report_card),
                getContext()
        );
        extendedYearlyChart.setData(createReport(
                getStringifiedYear(),
                mParent.getExpenses()
        ));

        PieChartCardPopulator incomeCard = new PieChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_income_card)
        );
        incomeCard.showIncome();
        incomeCard.setData(createReport(
                getString(R.string.income),
                mParent.getExpenses()
        ));

        PieChartCardPopulator expenseCard = new PieChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_expense_card)
        );
        expenseCard.showExpense();
        expenseCard.setData(createReport(
                getString(R.string.expense),
                mParent.getExpenses()
        ));

        LineChartCardPopulator lineChartCard = new LineChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_line_chart),
                getContext()
        );
        lineChartCard.setData(createReport(
                getString(R.string.account_balance),
                mParent.getExpenses()
        ));

        return rootView;
    }

    public void updateView() {
        // TODO: Wenn die sichtbaren Konten geupdated wurden müssen die Ausgaben von dem Parent abgefragt werden und angezeigt werden
    }

    private ReportInterface createReport(String title, List<ExpenseObject> expenses) {
        return new Report(
                title,
                filterByYear(expenses, getCurrentYear()),
                mUserPreferences.getMainCurrency()
        );
    }

    private List<ExpenseObject> filterByYear(List<ExpenseObject> expenses, int year) {
        ExpenseGrouper expenseGrouper = new ExpenseGrouper();

        return expenseGrouper.byYear(expenses, year);
    }

    private String getStringifiedYear() {
        return String.valueOf(getCurrentYear());
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}
