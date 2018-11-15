package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.lucas.haushaltsmanager.ExpenseSum;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TabThreeYearlyReports extends AbstractTab {
    private ParentActivity mParent;
    private UserSettingsPreferences mUserPreferences;
    private HashMap<Integer, Double> mAccountBalanceYear; // TODO: Würde ich gerne anders machen
    private LineChartCardPopulator mLineChartPopulator;
    private PieChartCardPopulator mIncomeCardPopulator, mExpenseCardPopulator;
    private TimeFrameCardPopulator mTimeFrameCardPopulator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParent = (ParentActivity) getActivity();
        mUserPreferences = new UserSettingsPreferences(getContext());

        mAccountBalanceYear = new ExpenseSum().byYear(mParent.getExpenses());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View rootView = inflater.inflate(R.layout.tab_three_yearly_reports, container, false);

        mTimeFrameCardPopulator = new TimeFrameCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_timeframe_report_card),
                getContext()
        );
        mTimeFrameCardPopulator.setData(createReport(
                getStringifiedYear(),
                mParent.getExpenses()
        ));

        mIncomeCardPopulator = new PieChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_income_card)
        );
        mIncomeCardPopulator.showIncome();
        mIncomeCardPopulator.setData(createReport(
                getString(R.string.income),
                mParent.getExpenses()
        ));

        mExpenseCardPopulator = new PieChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_expense_card)
        );
        mExpenseCardPopulator.showExpense();
        mExpenseCardPopulator.setData(createReport(
                getString(R.string.expense),
                mParent.getExpenses()
        ));

        mLineChartPopulator = new LineChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_line_chart),
                mAccountBalanceYear.get(getCurrentYear() - 1)
        );
        mLineChartPopulator.setResources(mParent.getResources(), getCurrentYear()); // TODO: Kann ich das mit dem Jahr anders machen. Es wird nur für die GroupFunkion benutzt
        mLineChartPopulator.setData(createReport(
                getString(R.string.account_balance),
                mParent.getExpenses()
        ));

        return rootView;
    }

    public void updateView() {
        ReportInterface report = createReport("", mParent.getVisibleExpenses());

        report.setCardTitle(getStringifiedYear());
        mTimeFrameCardPopulator.setData(report);

        report.setCardTitle(getString(R.string.account_balance));
        mLineChartPopulator.setData(report);

        report.setCardTitle(getString(R.string.income));
        mIncomeCardPopulator.setData(report);

        report.setCardTitle(getString(R.string.expense));
        mExpenseCardPopulator.setData(report);
    }

    private ReportInterface createReport(String title, List<ExpenseObject> expenses) {
        return new Report(
                title,
                filterByYear(expenses, getCurrentYear()),
                mUserPreferences.getMainCurrency()
        );
    }

    private List<ExpenseObject> filterByYear(List<ExpenseObject> expenses, int year) {
        return new ExpenseGrouper().byYear(expenses, year);
    }

    private String getStringifiedYear() {
        return String.valueOf(getCurrentYear());
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}
