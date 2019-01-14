package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.CardPopulator.LineChartCardPopulator;
import com.example.lucas.haushaltsmanager.CardPopulator.PieChartCardPopulator;
import com.example.lucas.haushaltsmanager.CardPopulator.TimeFrameCardPopulator;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Report.Report;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.ExpenseGrouper;
import com.example.lucas.haushaltsmanager.ExpenseSum;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import java.util.HashMap;
import java.util.List;

public class TabThreeYearlyReports extends AbstractTab {
    private ParentActivity mParent;
    private UserSettingsPreferences mUserPreferences;
    private LineChartCardPopulator mLineChartPopulator;
    private PieChartCardPopulator mIncomeCardPopulator, mExpenseCardPopulator;
    private TimeFrameCardPopulator mTimeFrameCardPopulator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParent = (ParentActivity) getActivity();
        mUserPreferences = new UserSettingsPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View rootView = inflater.inflate(R.layout.tab_three_yearly_reports, container, false);

        List<ExpenseObject> expenses = mParent.getVisibleExpenses();

        mTimeFrameCardPopulator = new TimeFrameCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_timeframe_report_card),
                getResources()
        );
        mTimeFrameCardPopulator.setData(createReport(
                getStringifiedYear(),
                expenses
        ));

        mIncomeCardPopulator = new PieChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_income_card)
        );
        mIncomeCardPopulator.showIncome();
        mIncomeCardPopulator.setData(createReport(
                getString(R.string.income),
                expenses
        ));

        mExpenseCardPopulator = new PieChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_expense_card)
        );
        mExpenseCardPopulator.showExpense();
        mExpenseCardPopulator.setData(createReport(
                getString(R.string.expense),
                expenses
        ));

        mLineChartPopulator = new LineChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_line_chart),
                getLastYearAccountBalance(CalendarUtils.getCurrentYear(), expenses)
        );
        mLineChartPopulator.setResources(mParent.getResources(), CalendarUtils.getCurrentYear()); // TODO: Kann ich das mit dem Jahr anders machen. Es wird nur für die GroupFunkion benutzt
        mLineChartPopulator.setData(createReport(
                getString(R.string.account_balance),
                expenses
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

    private double getLastYearAccountBalance(int currentYear, List<ExpenseObject> expenses) {
        // TODO: Würde ich gerne anders machen
        HashMap<Integer, Double> mAccountBalanceYear = new ExpenseSum().byYear(expenses);

        int lastYear = currentYear - 1;

        if (mAccountBalanceYear.containsKey(lastYear))
            return mAccountBalanceYear.get(lastYear);

        return 0d;
    }

    private ReportInterface createReport(String title, List<ExpenseObject> expenses) {
        return new Report(
                title,
                filterByYear(expenses, CalendarUtils.getCurrentYear()),
                mUserPreferences.getMainCurrency()
        );
    }

    private List<ExpenseObject> filterByYear(List<ExpenseObject> expenses, int year) {
        return new ExpenseGrouper().byYear(expenses, year);
    }

    private String getStringifiedYear() {
        return String.valueOf(CalendarUtils.getCurrentYear());
    }
}
