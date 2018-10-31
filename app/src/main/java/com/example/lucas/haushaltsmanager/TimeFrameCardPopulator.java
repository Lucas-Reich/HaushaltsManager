package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Reports.Year;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.lucas.androidcharts.DataSet;
import com.lucas.androidcharts.PieChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeFrameCardPopulator {

    private CardView mRootView;
    private ViewHolder mViewHolder;
    private Context mContext;

    public TimeFrameCardPopulator(CardView rootView, Context context) {
        mRootView = rootView;
        mContext = context;

        initializeViewHolder();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mRootView.setOnClickListener(listener);
    }

    public void setData(Year data) {
        populateView(data);
        mRootView.invalidate();
    }

    private void populateView(Year year) {
        setCardTitle(year.getCardTitle());

        setIncome(year.getIncoming(), year.getCurrency());

        setOutgoing(year.getOutgoing(), year.getCurrency());

        setTotal(year.getTotal(), year.getCurrency());

        setTotalBookingsCount(year.getBookingCount());

        setCategory(year.getMostStressedCategory());

        setPieChart(year);
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
        mViewHolder.mBookingsCount.setText(String.format("%s %s", bookingsCount, mContext.getString(R.string.bookings)));
    }

    private void setCategory(Category category) {
        mViewHolder.mCategoryColor.setCircleColor(category.getColorString());
        mViewHolder.mCategoryTitle.setText(category.getTitle());
    }

    private void setPieChart(Year yearReport) {
        mViewHolder.mPieChart.setPieData(preparePieData(yearReport));
        mViewHolder.mPieChart.setNoDataText(R.string.no_bookings_in_year);
//        mViewHolder.mPieChart.useCompressedChart(true);
    }

    private List<DataSet> preparePieData(Year year) {
        if (year.getBookingCount() == 0)
            return new ArrayList<>();

        List<DataSet> pieData = new ArrayList<>();
        for (Map.Entry<Category, Double> set : sumByCategory(year.getExpenses()).entrySet()) {
            pieData.add(new DataSet(
                    set.getValue().floatValue(),
                    set.getKey().getColorInt(),
                    set.getKey().getTitle()
            ));
        }

        return pieData;
    }

    private HashMap<Category, Double> sumByCategory(List<ExpenseObject> expenses) {
        HashMap<Category, Double> categories = new HashMap<>();

        for (ExpenseObject expense : expenses) {
            Category expenseCategory = expense.getCategory();

            if (!categories.containsKey(expenseCategory))
                categories.put(expenseCategory, 0d);

            categories.put(expenseCategory, categories.get(expenseCategory) + expense.getSignedPrice());
        }

        return categories;
    }

    private String formatMoney(double money) {
        return String.format(mContext.getResources().getConfiguration().locale, "%.2f", money);
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
