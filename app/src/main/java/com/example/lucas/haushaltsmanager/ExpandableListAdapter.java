package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.CustomViews.CircularTextView;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private String TAG = ExpandableListAdapter.class.getSimpleName();

    private Context mContext;
    private List<ExpenseObject> mGroupData;
    private HashMap<ExpenseObject, List<ExpenseObject>> mChildData;
    private ArrayList<Integer> mSelectedGroups;
    private int mRed, mGreen;

    public ExpandableListAdapter(Context context, List<ExpenseObject> mGroupData, HashMap<ExpenseObject, List<ExpenseObject>> mChildData) {

        this.mContext = context;
        this.mGroupData = mGroupData;
        this.mChildData = mChildData;
        this.mSelectedGroups = new ArrayList<>();

        this.mRed = context.getResources().getColor(R.color.booking_expense);
        this.mGreen = context.getResources().getColor(R.color.booking_income);
    }

    @Override
    public int getGroupCount() {

        return this.mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this.mChildData.get(this.mGroupData.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return this.mGroupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return this.mChildData.get(this.mGroupData.get(groupPosition)).get(childPosition);
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

        //todo ich MUSS die convertView wiederbenutzen und sie nicht jedes mal wieder neu initialisieren
        final ExpenseObject groupExpense = (ExpenseObject) getGroup(groupPosition);
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (groupExpense.getExpenseType()) {

            case PARENT_EXPENSE:

                convertView = inflater.inflate(R.layout.activity_test_exp_listview_list_group_child_y, null);

                ImageView image = (ImageView) convertView.findViewById(R.id.expandable_icon);
                int imageResourceId = isExpanded ? R.drawable.ic_expand_less_black_24dp : R.drawable.ic_expand_more_black_24dp;

                image.setImageResource(imageResourceId);
                image.setVisibility(View.VISIBLE);

                TextView txtTitle = (TextView) convertView.findViewById(R.id.exp_listview_header_name);
                TextView txtTotalAmount = (TextView) convertView.findViewById(R.id.exp_listview_header_total_amount);
                TextView txtBaseCurrency = (TextView) convertView.findViewById(R.id.exp_listview_header_base_currency);

                txtTitle.setText(groupExpense.getName());
                txtTotalAmount.setText(String.format(String.format(mContext.getResources().getConfiguration().locale, "%.2f", groupExpense.getSignedPrice())));
                txtTotalAmount.setTextColor(groupExpense.getSignedPrice() < 0 ? mRed : mGreen);
                txtBaseCurrency.setText("€");
                txtBaseCurrency.setTextColor(groupExpense.getSignedPrice() < 0 ? mRed : mGreen);
                break;
            case DATE_PLACEHOLDER:

                convertView = inflater.inflate(R.layout.activity_test_exp_listview_list_group_sep_date, null);

                TextView date = (TextView) convertView.findViewById(R.id.exp_listview_sep_header_date);

                date.setText(groupExpense.getDate());
                break;
            case NORMAL_EXPENSE:

                SharedPreferences preferences = mContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

                convertView = inflater.inflate(R.layout.activity_test_exp_listview_list_group_child_n, null);

                CircularTextView circleLetter = (CircularTextView) convertView.findViewById(R.id.booking_item_circle);
                TextView txtTitle2 = (TextView) convertView.findViewById(R.id.booking_item_title);
                TextView txtPerson = (TextView) convertView.findViewById(R.id.booking_item_person);
                TextView txtPaidPrice = (TextView) convertView.findViewById(R.id.booking_item_paid_price);
                TextView txtPaidCurrency = (TextView) convertView.findViewById(R.id.booking_item_currency_paid);
                TextView txtCalcPrice = (TextView) convertView.findViewById(R.id.booking_item_booking_price);
                TextView txtBaseCurrency2 = (TextView) convertView.findViewById(R.id.booking_item_currency_base);


                //if group is selected by the user the entry has to be highligted on redrawing
                if (mSelectedGroups.contains(getGroupId(groupPosition))) {

                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.highlighted_item_color));
                }


                String category = groupExpense.getCategory().getName();
                circleLetter.setText(category.substring(0, 1).toUpperCase());
                circleLetter.setSolidColor(groupExpense.getCategory().getColor());
                txtTitle2.setText(groupExpense.getName());
                //TODO wenn es eine Multiuser funktionalität muss hier der benutzer eingetragen werden, der das Geld ausgegeben hat
                txtPerson.setText("");
                txtPaidPrice.setText(String.format(mContext.getResources().getConfiguration().locale, "%.2f", groupExpense.getUnsignedPrice()));
                txtPaidPrice.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);
                txtPaidCurrency.setText(groupExpense.getExpenseCurrency().getSymbol());
                txtPaidCurrency.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);

                if (groupExpense.getExpenseCurrency().getIndex() == preferences.getLong("mainCurrencyIndex", 0)) {
                    //booking currency is the same as the base currency

                    txtCalcPrice.setText("");
                    txtBaseCurrency2.setText("");
                } else {
                    //booking currency is not the same currency as the base currency

                    txtPaidPrice.setText(String.format(mContext.getResources().getConfiguration().locale, "%.2f", groupExpense.getUnsignedPrice()));
                    txtPaidCurrency.setText(groupExpense.getExpenseCurrency().getSymbol());
                    txtCalcPrice.setText(String.format(mContext.getResources().getConfiguration().locale, "%.2f", groupExpense.getCalcPrice()));
                    txtBaseCurrency2.setText(preferences.getString("mainCurrency", "€"));
                }
                break;
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ExpenseObject childExpense = (ExpenseObject) getChild(groupPosition, childPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_test_exp_listview_list_item, null);
        }

        CircularTextView circleLetter = (CircularTextView) convertView.findViewById(R.id.exp_listview_item_circle);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.exp_listview_item_title);
        TextView txtPerson = (TextView) convertView.findViewById(R.id.exp_listview_item_person);
        TextView txtPaidPrice = (TextView) convertView.findViewById(R.id.exp_listview_item_paid_price);
        TextView txtBaseCurrency = (TextView) convertView.findViewById(R.id.exp_listview_item_currency_base);
        TextView txtCalcPrice = (TextView) convertView.findViewById(R.id.exp_listview_item_booking_price);
        TextView txtPaidCurrency = (TextView) convertView.findViewById(R.id.exp_listview_item_currency_paid);


        String category = childExpense.getCategory().getName();

        circleLetter.setText(category.substring(0, 1).toUpperCase());
        circleLetter.setSolidColor(childExpense.getCategory().getColor());
        txtTitle.setText(childExpense.getName());
        //TODO wenn es eine Multiuser funktionalität muss hier der benutzer eingetragen werden, der das Geld ausgegeben hat
        txtPerson.setText("");
        txtPaidPrice.setText(String.format("%s", childExpense.getUnsignedPrice()));
        txtPaidPrice.setTextColor(childExpense.isExpenditure() ? mRed : mGreen);
        txtPaidCurrency.setText(childExpense.getAccount().getCurrency().getSymbol());
        txtPaidCurrency.setTextColor(childExpense.isExpenditure() ? mRed : mGreen);
        txtCalcPrice.setText("");
        txtBaseCurrency.setText("");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }

    public boolean selectGroup(int groupId) {

        return this.mSelectedGroups.add(groupId);
    }

    public ExpenseObject getExpense(int expenseId) {

        return this.mGroupData.get(expenseId);
    }

    public boolean isSelected(int groupId) {

        return this.mSelectedGroups.contains(groupId);
    }

    public boolean removeGroupFromList(int groupId) {

        return this.mSelectedGroups.remove((Object) groupId);
    }

    public void deselectAll() {

        this.mSelectedGroups.clear();
    }

    public int getSelectedCount() {

        return this.mSelectedGroups.size();
    }

    public void clearSelected() {

        this.mSelectedGroups.clear();
    }

    public ArrayList<ExpenseObject> getSelectedGroupData() {

        ArrayList<ExpenseObject> groupData = new ArrayList<>();

        for (int groupId : this.mSelectedGroups) {

            groupData.add(mGroupData.get(groupId));
        }

        return groupData;
    }

    public long[] getSelectedBookingIds() {

        long bookingIds[] = new long[this.mSelectedGroups.size()];
        int counter = 0;

        for (long groupId : this.mSelectedGroups) {

            bookingIds[counter] = this.mGroupData.get((int) groupId).getIndex();
            counter++;
        }

        return bookingIds;
    }
}
