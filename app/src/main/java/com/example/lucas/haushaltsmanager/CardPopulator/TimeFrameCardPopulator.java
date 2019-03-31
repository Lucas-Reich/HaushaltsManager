package com.example.lucas.haushaltsmanager.CardPopulator;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.widget.CardView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TimeFrameCardPopulator {
    private ViewHolder mViewHolder;
    private Resources mResources;

    public TimeFrameCardPopulator(CardView rootView, Resources resources) {
        initializeViewHolder(rootView);

        mResources = resources;
    }

    public void setData(ReportInterface report) {
        setCardTitle(report.getCardTitle());

        setIncome(new Price(report.getIncoming(), report.getCurrency()));

        setOutgoing(new Price(report.getOutgoing(), report.getCurrency()));

        setTotal(new Price(report.getTotal(), report.getCurrency()));

        setTotalBookingsCount(report.getBookingCount());

        setCategory(report.getMostStressedCategory());

        setPieChart(report);
    }

    private void setCardTitle(String title) {
        mViewHolder.mTitle.setText(title);
    }

    private void setIncome(Price income) {
        mViewHolder.mInbound.setText(MoneyUtils.formatHumanReadable(income, Locale.getDefault()));
        mViewHolder.mInbound.setTextColor(getColor(R.color.booking_income));

        mViewHolder.mInboundCurrency.setText(income.getCurrency().getSymbol());
        mViewHolder.mInboundCurrency.setTextColor(getColor(R.color.booking_income));
    }

    private void setOutgoing(Price outgoing) {
        mViewHolder.mOutbound.setText(MoneyUtils.formatHumanReadable(outgoing, Locale.getDefault()));
        mViewHolder.mOutbound.setTextColor(getColor(R.color.booking_expense));

        mViewHolder.mOutboundCurrency.setText(outgoing.getCurrency().getSymbol());
        mViewHolder.mOutboundCurrency.setTextColor(getColor(R.color.booking_expense));
    }

    private void setTotal(Price total) {
        // TODO: sollte ich den Preis in normaler Farbe anzeigen, wenn er 0 ist?
        int color = total.getSignedValue() >= 0 ? R.color.booking_income : R.color.booking_expense;

        mViewHolder.mTotal.setText(MoneyUtils.formatHumanReadable(total, Locale.getDefault()));
        mViewHolder.mTotal.setTextColor(getColor(color));

        mViewHolder.mTotalCurrency.setText(total.getCurrency().getSymbol());
        mViewHolder.mTotalCurrency.setTextColor(getColor(color));
    }

    private void setTotalBookingsCount(int bookingsCount) {
        mViewHolder.mBookingsCount.setText(String.format("%s %s", bookingsCount, app.getContext().getString(R.string.bookings)));
    }

    private void setCategory(Category category) {
        mViewHolder.mCategoryColor.setCircleColor(category.getColorString());
        mViewHolder.mCategoryTitle.setText(category.getTitle());
    }

    private void setPieChart(ReportInterface report) {
        mViewHolder.mPieChart.setData(preparePieData(report));
        mViewHolder.mPieChart.setDrawHoleEnabled(false);
        mViewHolder.mPieChart.getLegend().setEnabled(false);
        mViewHolder.mPieChart.getDescription().setEnabled(false);
        mViewHolder.mPieChart.setNoDataText(mResources.getString(R.string.no_bookings_in_year));
        mViewHolder.mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(app.getContext(), "" + e.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                // Do nothing
            }
        });
    }

    private PieData preparePieData(ReportInterface report) {
        if (report.getBookingCount() == 0) {
            return new PieData(new PieDataSet(new ArrayList<PieEntry>(), ""));
        }

        List<PieEntry> pieData = new ArrayList<>();
        List<ExpenseObject> expenses = flattenExpenses(report.getExpenses());
        for (Map.Entry<Boolean, Double> entry : sumByExpenseType(expenses).entrySet()) {
            pieData.add(dataSetFrom(entry));
        }

        PieDataSet pds = new PieDataSet(pieData, "");
        pds.setColors(getColor(R.color.booking_income), getColor(R.color.booking_expense));
        pds.setDrawValues(false);

        return new PieData(pds);
    }

    private PieEntry dataSetFrom(Map.Entry<Boolean, Double> entry) {
        float value = Math.abs(entry.getValue().floatValue());

        return new PieEntry(
                value,
                "" // Es sollen keine Labels angezeigt werden
        );
    }

    @ColorInt
    private int getColor(@ColorRes int color) {
        return mResources.getColor(color);
    }

    private List<ExpenseObject> flattenExpenses(List<ExpenseObject> expenses) {
        List<ExpenseObject> extractedChildren = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (expense.isParent())
                extractedChildren.addAll(expense.getChildren());
            else
                extractedChildren.add(expense);
        }

        return extractedChildren;
    }

    private HashMap<Boolean, Double> sumByExpenseType(List<ExpenseObject> expenses) {
        ExpenseSum expenseSum = new ExpenseSum();

        HashMap<Boolean, Double> summedExpenses = new HashMap<>();
        summedExpenses.put(true, expenseSum.byExpenditureType(true, expenses));
        summedExpenses.put(false, expenseSum.byExpenditureType(false, expenses));

        return summedExpenses;
    }

    private void initializeViewHolder(CardView rootView) {
        mViewHolder = new ViewHolder();

        mViewHolder.mTitle = rootView.findViewById(R.id.timeframe_report_card_title);

        mViewHolder.mInbound = rootView.findViewById(R.id.timeframe_report_card_income);
        mViewHolder.mInboundCurrency = rootView.findViewById(R.id.timeframe_report_card_income_currency);

        mViewHolder.mOutbound = rootView.findViewById(R.id.timeframe_report_card_expense);
        mViewHolder.mOutboundCurrency = rootView.findViewById(R.id.timeframe_report_card_expense_currency);

        mViewHolder.mTotal = rootView.findViewById(R.id.timeframe_report_card_total);
        mViewHolder.mTotalCurrency = rootView.findViewById(R.id.timeframe_report_card_total_currency);

        mViewHolder.mBookingsCount = rootView.findViewById(R.id.timeframe_report_card_total_bookings);

        mViewHolder.mCategoryColor = rootView.findViewById(R.id.timeframe_report_card_category_color);
        mViewHolder.mCategoryTitle = rootView.findViewById(R.id.timeframe_report_card_category_title);

        mViewHolder.mPieChart = rootView.findViewById(R.id.timeframe_report_card_pie_chart);
    }

    private class ViewHolder {
        TextView mTitle;
        TextView mInbound;
        TextView mInboundCurrency;
        TextView mOutbound;
        TextView mOutboundCurrency;
        TextView mTotal;
        TextView mTotalCurrency;
        TextView mBookingsCount;
        RoundedTextView mCategoryColor;
        TextView mCategoryTitle;
        PieChart mPieChart;
    }
}
