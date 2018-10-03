package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.example.lucas.haushaltsmanager.Views.ViewUtils;
import com.lucas.androidcharts.DataSet;
import com.lucas.androidcharts.PieChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    @SuppressWarnings("unused")
    private static final String TAG = ExpandableListAdapter.class.getSimpleName();

    private Context mContext;
    private List<ExpenseObject> mGroupData;
    private HashMap<ExpenseObject, List<ExpenseObject>> mChildData;
    private int mRed, mGreen;

    ExpandableListAdapter(Context context, List<ExpenseObject> mGroupData, HashMap<ExpenseObject, List<ExpenseObject>> mChildData) {

        this.mContext = context;
        this.mGroupData = mGroupData;
        this.mChildData = mChildData;

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

        ExpenseObject groupExpense = (ExpenseObject) getGroup(groupPosition);
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        boolean isSelected = isBookingSelected(groupPosition, null);

        switch (groupExpense.getExpenseType()) {

            case PARENT_EXPENSE:

                convertView = inflater.inflate(R.layout.activity_exp_listview_parent, null);
                if (isExpanded)
                    convertView.findViewById(R.id.exp_listview_paren_divider).setVisibility(View.GONE);
                if (isSelected)
                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.highlighted_item_color));

                PieChart pieChart = convertView.findViewById(R.id.exp_listview_parent_pie_chart);
                TextView txtTitle22 = convertView.findViewById(R.id.exp_listview_parent_title);
                TextView txtPrice = convertView.findViewById(R.id.exp_listview_parent_price);
                TextView txtCurrencySymbol = convertView.findViewById(R.id.exp_listview_parent_currency_symbol);
                TextView txtPerson2 = convertView.findViewById(R.id.exp_listview_parent_person);

                pieChart.setPieData(preparePieData(mChildData.get(groupExpense)));
                txtTitle22.setText(groupExpense.getTitle());
                txtPrice.setText(String.format(mContext.getResources().getConfiguration().locale, "%.2f", groupExpense.getSignedPrice()));
                txtPrice.setTextColor(groupExpense.getSignedPrice() < 0 ? mRed : mGreen);
                txtCurrencySymbol.setText(getMainCurrencySymbol());
                txtCurrencySymbol.setTextColor(groupExpense.getSignedPrice() < 0 ? mRed : mGreen);
                txtPerson2.setText("");// sollen User bei einer Multiuser funktionalität auch bei ParentBuchungen angezeigt werden || falls nicht, einen leeren string übergeben sodass das Format gleich bleibt!
                break;
            case DATE_PLACEHOLDER:

                convertView = inflater.inflate(R.layout.activity_exp_listview_date, null);

                TextView date = convertView.findViewById(R.id.exp_listview_sep_header_date);
                date.setText(groupExpense.getDate());
                break;
            case NORMAL_EXPENSE:

                convertView = inflater.inflate(R.layout.activity_exp_listview_group, null);
                if (isSelected)
                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.highlighted_item_color));

                RoundedTextView roundedTextView = convertView.findViewById(R.id.exp_listview_group_rounded_textview);
                TextView txtTitle2 = convertView.findViewById(R.id.exp_listview_group_title);
                TextView txtPerson = convertView.findViewById(R.id.exp_listview_group_person);
                TextView txtPaidPrice = convertView.findViewById(R.id.exp_listview_group_price);
                TextView txtPaidCurrency = convertView.findViewById(R.id.exp_listview_group_currency_symbol);

                if (ViewUtils.getColorBrightness(groupExpense.getCategory().getColorString()) > 0.5) {
                    roundedTextView.setTextColor(mContext.getResources().getColor(R.color.primary_text_color_dark));
                } else {
                    roundedTextView.setTextColor(mContext.getResources().getColor(R.color.primary_text_color_bright));
                }

                String category = groupExpense.getCategory().getTitle();
                roundedTextView.setCenterText(category.substring(0, 1).toUpperCase());
                roundedTextView.setCircleColor(groupExpense.getCategory().getColorString());
                txtTitle2.setText(groupExpense.getTitle());
                //TODO wenn es eine Multiuser funktionalität muss hier der benutzer eingetragen werden, der das Geld ausgegeben hat
                txtPerson.setText("");
                txtPaidPrice.setText(String.format(mContext.getResources().getConfiguration().locale, "%.2f", groupExpense.getUnsignedPrice()));
                txtPaidPrice.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);
                txtPaidCurrency.setText(getMainCurrencySymbol());
                txtPaidCurrency.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);
                break;

            case TRANSFER_EXPENSE:

                //todo eigenes layout für tramsfer mExpenses definieren
                convertView = inflater.inflate(R.layout.activity_exp_listview_group, null);
                if (isSelected)
                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.highlighted_item_color));

                RoundedTextView roundedTextView3 = convertView.findViewById(R.id.exp_listview_group_rounded_textview);
                TextView txtTitle3 = convertView.findViewById(R.id.exp_listview_group_title);
                TextView txtPerson3 = convertView.findViewById(R.id.exp_listview_group_person);
                TextView txtPaidPrice3 = convertView.findViewById(R.id.exp_listview_group_price);
                TextView txtPaidCurrency3 = convertView.findViewById(R.id.exp_listview_group_currency_symbol);

                String category2 = groupExpense.getCategory().getTitle();
                roundedTextView3.setTextColor(Color.WHITE);
                roundedTextView3.setCenterText(category2.substring(0, 1).toUpperCase());
                roundedTextView3.setCircleColor(groupExpense.getCategory().getColorString());
                txtTitle3.setText(groupExpense.getTitle());
                //TODO wenn es eine Multiuser funktionalität muss hier der benutzer eingetragen werden, der das Geld ausgegeben hat
                txtPerson3.setText("");
                txtPaidPrice3.setText(String.format(mContext.getResources().getConfiguration().locale, "%.2f", groupExpense.getUnsignedPrice()));
                txtPaidPrice3.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);
                txtPaidCurrency3.setText(getMainCurrencySymbol());
                txtPaidCurrency3.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);
                break;
            default:
                throw new UnsupportedOperationException("Für den Buchungstyp: " + groupExpense.getExpenseType().name() + " gibt es keine View methode!");
        }

        return convertView;
    }

    /**
     * Methode um die Datensätze eines PieCharts erstellen soll.
     *
     * @param expenses Buchungen
     * @return DataSets
     */
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

    /**
     * Methode um das Symbol der Hauptwährung aus den SharedPreferences auszulesen.
     *
     * @return MainCurrencySymbol
     */
    private String getMainCurrencySymbol() {
        SharedPreferences preferences = mContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        return preferences.getString("mainCurrencySymbol", "€");
    }

    /**
     * Klasse um nicht bei jedem Child die Objekte neu erstellen zu müssen
     */
    private class ChildViewHolder {

        RoundedTextView roundedTextView;
        TextView txtTitle;
        TextView txtPerson;
        TextView txtPaidPrice;
        TextView txtBaseCurrency;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ExpenseObject childExpense = (ExpenseObject) getChild(groupPosition, childPosition);
        ChildViewHolder childViewHolder;

        if (convertView == null) {

            childViewHolder = new ChildViewHolder();
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_exp_listview_child, null);

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
        //TODO wenn es eine Multiuser funktionalität muss hier der benutzer eingetragen werden, der das Geld ausgegeben hat
        childViewHolder.txtPerson.setText("");
        childViewHolder.txtPaidPrice.setText(String.format(mContext.getResources().getConfiguration().locale, "%.2f", childExpense.getUnsignedPrice()));
        childViewHolder.txtPaidPrice.setTextColor(childExpense.isExpenditure() ? mRed : mGreen);
        childViewHolder.txtBaseCurrency.setText(getMainCurrencySymbol());
        childViewHolder.txtBaseCurrency.setTextColor(childExpense.isExpenditure() ? mRed : mGreen);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }

    private HashMap<ExpenseObject, List<ExpenseObject>> mSelectedBookings = new HashMap<>();
    private int selectedGroups = 0;
    private int selectedChildren = 0;
    private int selectedParents = 0;

    public void selectItem(int groupId, @Nullable Integer childId) {

        selectBooking(groupId, childId);
        notifyDataSetChanged();
    }

    public void deselectItem(int groupId, @Nullable Integer childId) {

        deselectBooking(groupId, childId);
        notifyDataSetChanged();
        //kann ich die view auch irgendwie anders bekommen?
        //kann ich das auch durch das passen der View erledigen
    }

    public boolean isItemSelected(int groupId, @Nullable Integer childId) {
        return isBookingSelected(groupId, childId);
    }

    /**
     * Methode um die angegebenen Buchung in die Liste der ausgwählten Buchungenzu schreiben.
     *
     * @param groupPosition Position der Gruppe
     * @param childPosition Position des Kindes oder NULL, wenn eine Groupbuchung ausgewählt wurde
     */
    private void selectBooking(int groupPosition, @Nullable Integer childPosition) {
        ExpenseObject group = (ExpenseObject) getGroup(groupPosition);
        List<ExpenseObject> existingChildren = mChildData.get(group);

        if (childPosition != null) {
            ExpenseObject child = (ExpenseObject) getChild(groupPosition, childPosition);
            List<ExpenseObject> children = new ArrayList<>();
            children.add(child);

            if (mSelectedBookings.containsKey(group)) {

                List<ExpenseObject> children2 = mSelectedBookings.get(group);
                children2.add(child);
                mSelectedBookings.put(group, children2);
            } else {
                mSelectedBookings.put(group, children);
            }
            selectedChildren++;
        } else {
            if (group.isParent()) {
                mSelectedBookings.put(group, existingChildren);
                selectedParents++;
            } else {

                mSelectedBookings.put(group, new ArrayList<ExpenseObject>());
                selectedGroups++;
            }
        }
    }

    /**
     * Methode um eine Buchung aus der Liste der ausgewählten Buchungen zu entfernen.
     *
     * @param groupPosition Position der Gruppe
     * @param childPosition Position des Kindes oder NULL, wenn eine Groupbuchung ausgewählt wurde
     */
    private void deselectBooking(int groupPosition, @Nullable Integer childPosition) {
        ExpenseObject group = (ExpenseObject) getGroup(groupPosition);

        if (childPosition != null) {

            ExpenseObject child = (ExpenseObject) getChild(groupPosition, childPosition);
            List<ExpenseObject> children = mSelectedBookings.get(group);
            children.remove(child);
            if (children.size() == 0) {
                mSelectedBookings.remove(group);
            } else {
                mSelectedBookings.put(group, children);
            }
            selectedChildren--;
        } else {
            mSelectedBookings.remove(group);

            if (mChildData.get(group).size() != 0) {
                selectedParents--;
            } else {
                selectedGroups--;
            }
        }
    }

    /**
     * Methode um herauszufinden ob die angegebene Buchung bereits ausgewählt ist.
     *
     * @param groupPosition Position der Gruppe
     * @param childPosition Position des Kindes oder NULL, wenn eine Groupbuchung ausgewählt wuerde
     * @return TRUE wenn die Buchung ausgewählt ist, FALSE wenn nicht
     */
    private boolean isBookingSelected(int groupPosition, @Nullable Integer childPosition) {
        ExpenseObject group = (ExpenseObject) getGroup(groupPosition);

        if (!mSelectedBookings.containsKey(group)) {
            return false;
        }

        if (childPosition != null) {
            ExpenseObject child = (ExpenseObject) getChild(groupPosition, childPosition);

            return mSelectedBookings.get(group).contains(child);
        } else {

            return true;
        }
    }

    /**
     * Methode um alle ausgewählten Buchungen abzufragen.
     *
     * @return Ausgewählte Buchugen
     */
    public HashMap<ExpenseObject, List<ExpenseObject>> getSelectedBookings() {
        return mSelectedBookings;
    }

    /**
     * Methode um alle ausgewählten Buchugen abzuwählen
     */
    public void deselectAll() {
        selectedGroups = 0;
        selectedChildren = 0;
        selectedParents = 0;
        mSelectedBookings.clear();
    }

    public int getSelectedBookingsCount() {
        return selectedGroups + selectedChildren + selectedParents;
    }

    public int getSelectedGroupCount() {
        return selectedGroups;
    }

    public int getSelectedChildCount() {
        return selectedChildren;
    }

    public int getSelectedParentCount() {
        return selectedParents;
    }
}
