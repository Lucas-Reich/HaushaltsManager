package com.example.lucas.haushaltsmanager.CardPopulator;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.os.ConfigurationCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.androidcharts.DataSet;
import com.example.lucas.androidcharts.PieChart;
import com.example.lucas.androidcharts.PieSlice;
import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TimeFrameCardPopulator {

    private CardView mRootView;
    private ViewHolder mViewHolder;
    private Resources mResources;

    public TimeFrameCardPopulator(CardView rootView, Resources resources) {
        mRootView = rootView;
        mResources = resources;


        initializeViewHolder();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mRootView.setOnClickListener(listener);
    }

    public void setData(ReportInterface report) {
        setCardTitle(report.getCardTitle());

        setIncome(report.getIncoming(), report.getCurrency());

        setOutgoing(report.getOutgoing(), report.getCurrency());

        setTotal(report.getTotal(), report.getCurrency());

        setTotalBookingsCount(report.getBookingCount());

        setCategory(report.getMostStressedCategory());

        setPieChart(report);
    }

    private void setCardTitle(String title) {
        mViewHolder.mTitle.setText(title);
    }

    private void setIncome(double income, Currency currency) {
        mViewHolder.mInbound.setText(formatMoney(income));
        mViewHolder.mInboundCurrency.setText(currency.getSymbol());
    }

    private void setOutgoing(double outgoing, Currency currency) {
        mViewHolder.mOutbound.setText(formatMoney(outgoing));
        mViewHolder.mOutboundCurrency.setText(currency.getSymbol());
    }

    private void setTotal(double total, Currency currency) {
        mViewHolder.mTotal.setText(formatMoney(total));
        mViewHolder.mTotalCurrency.setText(currency.getSymbol());
    }

    private void setTotalBookingsCount(int bookingsCount) {
        mViewHolder.mBookingsCount.setText(String.format("%s %s", bookingsCount, app.getContext().getString(R.string.bookings)));
    }

    private void setCategory(Category category) {
        mViewHolder.mCategoryColor.setCircleColor(category.getColorString());
        mViewHolder.mCategoryTitle.setText(category.getTitle());
    }

    private void setPieChart(ReportInterface data) {
        mViewHolder.mPieChart.setPieData(preparePieData(data));
        mViewHolder.mPieChart.setOnPieChartClickListener(new PieChart.OnPieChartClickListener() {
            @Override
            public void onSliceClick(PieSlice slice) {
                Toast.makeText(app.getContext(), slice.getLabel(), Toast.LENGTH_SHORT).show();
            }
        });
        mViewHolder.mPieChart.setNoDataText(R.string.no_bookings_in_year);
    }

    private List<DataSet> preparePieData(ReportInterface data) {
        if (data.getBookingCount() == 0)
            return new ArrayList<>();

        List<DataSet> pieData = new ArrayList<>();

        List<ExpenseObject> test = flattenExpenses(data.getExpenses());

        for (Map.Entry<Boolean, Double> entry : sumByExpenseType(test).entrySet()) {
            pieData.add(dataSetFrom(entry));
        }

        return pieData;
    }

    private DataSet dataSetFrom(Map.Entry<Boolean, Double> entry) {
        int color = entry.getKey() ? getColor(R.color.booking_expense) : getColor(R.color.booking_income);
        String label = entry.getKey() ? getString(R.string.expense) : getString(R.string.income);
        float value = Math.abs(entry.getValue().floatValue());

        return new DataSet(
                value,
                color,
                label
        );
    }

    @ColorInt
    private int getColor(@ColorRes int color) {
        return mResources.getColor(color);
    }

    private String getString(@StringRes int string) {
        return mResources.getString(string);
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

    private String formatMoney(double money) {
        return String.format(getDefaultLocale(), "%.2f", money);
    }

    private Locale getDefaultLocale() {
        return ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
    }

    private void initializeViewHolder() {
        mViewHolder = new ViewHolder();

        mViewHolder.mTitle = mRootView.findViewById(R.id.timeframe_report_card_title);

        mViewHolder.mInbound = mRootView.findViewById(R.id.timeframe_report_card_income);
        mViewHolder.mInboundCurrency = mRootView.findViewById(R.id.timeframe_report_card_income_currency);

        mViewHolder.mOutbound = mRootView.findViewById(R.id.timeframe_report_card_expense);
        mViewHolder.mOutboundCurrency = mRootView.findViewById(R.id.timeframe_report_card_expense_currency);

        mViewHolder.mTotal = mRootView.findViewById(R.id.timeframe_report_card_total);
        mViewHolder.mTotalCurrency = mRootView.findViewById(R.id.timeframe_report_card_total_currency);

        mViewHolder.mBookingsCount = mRootView.findViewById(R.id.timeframe_report_card_total_bookings);

        mViewHolder.mCategoryColor = mRootView.findViewById(R.id.timeframe_report_card_category_color);
        mViewHolder.mCategoryTitle = mRootView.findViewById(R.id.timeframe_report_card_category_title);

        mViewHolder.mPieChart = mRootView.findViewById(R.id.timeframe_report_card_pie_chart);
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
