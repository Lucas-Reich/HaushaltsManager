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

public class TabThreeYearlyReports extends Fragment {
    private static final String TAG = TabThreeYearlyReports.class.getSimpleName();

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
        extendedYearlyChart.setData(createYearReportInterface(getStringifiedYear()));


        PieChartCardPopulator incomeCard = new PieChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_income_card)
        );
        incomeCard.setData(createYearReportInterface(
                getString(R.string.income)),
                PieChartCardPopulator.INCOME_CHART
        );

        PieChartCardPopulator expenseCard = new PieChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_expense_card)
        );
        expenseCard.setData(createYearReportInterface(
                getString(R.string.expense)),
                PieChartCardPopulator.EXPENDITURE_CHART
        );

        LineChartCardPopulator lineChartCard = new LineChartCardPopulator(
                (CardView) rootView.findViewById(R.id.tab_three_line_chart),
                getContext()
        );
        lineChartCard.setData(createYearReportInterface(
                String.format("%s - %s", getStringifiedYear(), getString(R.string.account_balance))
        ));

        return rootView;
    }

    public void updateView() {

        //todo Die Datengrundlage muss der Karten muss angepasst werden wenn der user Konten an oder abwählt
    }

    /**
     * Methode um herauszufinden, ob der aktuelle tab gerade sichtbar geworden ist oder nicht.
     * Quelle: https://stackoverflow.com/a/9779971
     *
     * @param isVisibleToUser Indikator ob die aktuelle UI für den User sichtbar ist. Default ist True.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {
            if (isVisibleToUser) {
                updateView();
                //todo die view sollte nur geupdated werden wenn es auch wirklich veränderungen gab
            }
        }
    }

    private ReportInterface createYearReportInterface(String title) {
        return new Report(
                title,
                getExpensesInYear(mParent.getExpenses(), getCurrentYear()),
                mUserPreferences.getMainCurrency()
        );
    }

    private List<ExpenseObject> getExpensesInYear(List<ExpenseObject> expenses, int year) {
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
