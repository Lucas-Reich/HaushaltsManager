package com.example.lucas.haushaltsmanager.ListAdapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.os.ConfigurationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.lucas.androidcharts.DataSet;
import com.example.lucas.androidcharts.PieChart;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.ViewUtils;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecurringBookingAdapter extends BaseExpandableListAdapter {
    private List<RecurringBooking> mRecurringBookings;
    private int mRed, mGreen, mDarkTextColor, mBrightTextColor;
    private LayoutInflater mInflater;
    private Currency mMainCurrency;

    public RecurringBookingAdapter(Context context, List<RecurringBooking> recurringBookings) {
        mRecurringBookings = recurringBookings;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMainCurrency = new UserSettingsPreferences(context).getMainCurrency();
        mRed = context.getResources().getColor(R.color.booking_expense);
        mGreen = context.getResources().getColor(R.color.booking_income);
        mDarkTextColor = context.getResources().getColor(R.color.primary_text_color_dark);
        mBrightTextColor = context.getResources().getColor(R.color.primary_text_color_bright);
    }

    @Override
    public int getGroupCount() {
        return mRecurringBookings.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int counter = 0;
        for (RecurringBooking booking : mRecurringBookings)
            counter += booking.getExpense().getChildren().size();

        return counter;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mRecurringBookings.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mRecurringBookings.get(groupPosition).getExpense().getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        RecurringBooking recurringBooking = (RecurringBooking) getGroup(groupPosition);
        ExpenseObject groupExpense = recurringBooking.getExpense();

        switch (groupExpense.getExpenseType()) {

            case PARENT_EXPENSE:

                convertView = mInflater.inflate(R.layout.activity_exp_listview_parent, parent, false);

                PieChart pieChart = convertView.findViewById(R.id.exp_listview_parent_pie_chart);
                TextView txtTitle22 = convertView.findViewById(R.id.exp_listview_parent_title);
                TextView txtPrice = convertView.findViewById(R.id.exp_listview_parent_price);
                TextView txtCurrencySymbol = convertView.findViewById(R.id.exp_listview_parent_currency_symbol);
                TextView txtPerson2 = convertView.findViewById(R.id.exp_listview_parent_person);

                pieChart.setPieData(preparePieData(recurringBooking.getExpense().getChildren()));
                txtTitle22.setText(groupExpense.getTitle());
                txtPrice.setText(String.format(getDefaultLocale(), "%.2f", groupExpense.getSignedPrice()));
                txtPrice.setTextColor(groupExpense.getSignedPrice() < 0 ? mRed : mGreen);
                txtCurrencySymbol.setText(getMainCurrencySymbol());
                txtCurrencySymbol.setTextColor(groupExpense.getSignedPrice() < 0 ? mRed : mGreen);
                txtPerson2.setText("");
                break;
            case NORMAL_EXPENSE:

                convertView = mInflater.inflate(R.layout.activity_exp_listview_group, parent, false);

                RoundedTextView roundedTextView = convertView.findViewById(R.id.exp_listview_group_rounded_textview);
                TextView txtTitle2 = convertView.findViewById(R.id.exp_listview_group_title);
                TextView txtPerson = convertView.findViewById(R.id.exp_listview_group_person);
                TextView txtPaidPrice = convertView.findViewById(R.id.exp_listview_group_price);
                TextView txtPaidCurrency = convertView.findViewById(R.id.exp_listview_group_currency_symbol);

                if (ViewUtils.getColorBrightness(groupExpense.getCategory().getColorString()) > 0.5) {
                    roundedTextView.setTextColor(mDarkTextColor);
                } else {
                    roundedTextView.setTextColor(mBrightTextColor);
                }

                String category = groupExpense.getCategory().getTitle();
                roundedTextView.setCenterText(category.substring(0, 1).toUpperCase());
                roundedTextView.setCircleColor(groupExpense.getCategory().getColorString());
                txtTitle2.setText(groupExpense.getTitle());
                txtPerson.setText("");
                txtPaidPrice.setText(String.format(getDefaultLocale(), "%.2f", groupExpense.getUnsignedPrice()));
                txtPaidPrice.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);
                txtPaidCurrency.setText(getMainCurrencySymbol());
                txtPaidCurrency.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);
                break;
            default:
                throw new UnsupportedOperationException("Booking type:  " + groupExpense.getExpenseType().name() + " is not supported!");
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ExpenseObject childExpense = (ExpenseObject) getChild(groupPosition, childPosition);
        ChildViewHolder childViewHolder;

        if (convertView == null) {

            childViewHolder = new ChildViewHolder();
            convertView = mInflater.inflate(R.layout.activity_exp_listview_child, parent, false);

            childViewHolder.roundedTextView = convertView.findViewById(R.id.exp_list_view_item_circle);
            childViewHolder.txtTitle = convertView.findViewById(R.id.exp_list_view_item_title);
            childViewHolder.txtPerson = convertView.findViewById(R.id.exp_list_view_item_person);
            childViewHolder.txtPaidPrice = convertView.findViewById(R.id.exp_list_view_item_paid_price);
            childViewHolder.txtBaseCurrency = convertView.findViewById(R.id.exp_list_view_item_paid_currency);

            convertView.setTag(childViewHolder);
        } else {

            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        String category = childExpense.getCategory().getTitle();

        childViewHolder.roundedTextView.setTextColor(Color.WHITE);
        childViewHolder.roundedTextView.setCenterText(category.substring(0, 1).toUpperCase());
        childViewHolder.roundedTextView.setCircleColor(childExpense.getCategory().getColorString());
        childViewHolder.roundedTextView.setCircleDiameter(33);
        childViewHolder.txtTitle.setText(childExpense.getTitle());
        childViewHolder.txtPerson.setText("");
        childViewHolder.txtPaidPrice.setText(String.format(getDefaultLocale(), "%.2f", childExpense.getUnsignedPrice()));
        childViewHolder.txtPaidPrice.setTextColor(childExpense.isExpenditure() ? mRed : mGreen);
        childViewHolder.txtBaseCurrency.setText(getMainCurrencySymbol());
        childViewHolder.txtBaseCurrency.setTextColor(childExpense.isExpenditure() ? mRed : mGreen);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private List<DataSet> preparePieData(List<ExpenseObject> expenses) {
        List<DataSet> dataSets = new ArrayList<>();
        Map<Category, Integer> summedCategories = new HashMap<>();

        for (ExpenseObject expense : expenses) {
            Category category = expense.getCategory();
            Integer count = summedCategories.get(category);

            if (count != null)
                summedCategories.put(category, count + 1);
            else
                summedCategories.put(category, 1);
        }

        for (Map.Entry<Category, Integer> category : summedCategories.entrySet()) {
            dataSets.add(new DataSet(category.getValue(), category.getKey().getColorInt(), category.getKey().getTitle()));
        }

        return dataSets;
    }

    private String getMainCurrencySymbol() {
        return mMainCurrency.getSymbol();
    }

    private Locale getDefaultLocale() {
        return ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
    }

    private class ChildViewHolder {
        RoundedTextView roundedTextView;
        TextView txtTitle;
        TextView txtPerson;
        TextView txtPaidPrice;
        TextView txtBaseCurrency;
    }
}
