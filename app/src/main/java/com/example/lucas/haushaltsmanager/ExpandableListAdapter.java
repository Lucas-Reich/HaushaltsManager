package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    @SuppressWarnings("unused")
    private static final String TAG = ExpandableListAdapter.class.getSimpleName();

    private Context mContext;
    private List<ExpenseObject> mGroupData;
    private HashMap<ExpenseObject, List<ExpenseObject>> mChildData;
    private ArrayList<ExpenseObject> mSelectedGroups, mSelectedChildren;
    private int mRed, mGreen;

    public ExpandableListAdapter(Context context, List<ExpenseObject> mGroupData, HashMap<ExpenseObject, List<ExpenseObject>> mChildData) {

        this.mContext = context;
        this.mGroupData = mGroupData;
        this.mChildData = mChildData;
        this.mSelectedGroups = new ArrayList<>();
        this.mSelectedChildren = new ArrayList<>();

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

        switch (groupExpense.getExpenseType()) {

            case PARENT_EXPENSE:

                convertView = inflater.inflate(R.layout.activity_exp_listview_parent, null);

                ImageView image = (ImageView) convertView.findViewById(R.id.expandable_icon);
                int imageResourceId = isExpanded ? R.drawable.ic_expand_less_black_24dp : R.drawable.ic_expand_more_black_24dp;
                if (isExpanded)
                    convertView.findViewById(R.id.exp_listview_parent_divider).setVisibility(View.GONE);

                image.setImageResource(imageResourceId);
                image.setVisibility(View.VISIBLE);

                TextView txtTitle = (TextView) convertView.findViewById(R.id.exp_listview_header_name);
                TextView txtTotalAmount = (TextView) convertView.findViewById(R.id.exp_listview_header_total_amount);
                TextView txtBaseCurrency = (TextView) convertView.findViewById(R.id.exp_listview_header_base_currency);

                txtTitle.setText(groupExpense.getTitle());
                txtTotalAmount.setText(String.format(mContext.getResources().getConfiguration().locale, "%.2f", groupExpense.getSignedPrice()));
                txtTotalAmount.setTextColor(groupExpense.getSignedPrice() < 0 ? mRed : mGreen);
                txtBaseCurrency.setText(getMainCurrencySymbol());
                txtBaseCurrency.setTextColor(groupExpense.getSignedPrice() < 0 ? mRed : mGreen);
                break;
            case DATE_PLACEHOLDER:

                convertView = inflater.inflate(R.layout.activity_exp_listview_date, null);

                TextView date = (TextView) convertView.findViewById(R.id.exp_listview_sep_header_date);
                date.setText(groupExpense.getDate());
                break;
            case NORMAL_EXPENSE:

                convertView = inflater.inflate(R.layout.activity_exp_listview_group, null);

                RoundedTextView roundedTextView = (RoundedTextView) convertView.findViewById(R.id.booking_item_circle);
                TextView txtTitle2 = (TextView) convertView.findViewById(R.id.booking_item_title);
                TextView txtPerson = (TextView) convertView.findViewById(R.id.booking_item_person);
                TextView txtPaidPrice = (TextView) convertView.findViewById(R.id.booking_item_paid_price);
                TextView txtPaidCurrency = (TextView) convertView.findViewById(R.id.booking_item_currency_paid);


                //if group is selected by the user the entry has to be highligted on redrawing
                if (mSelectedGroups.contains(getGroupId(groupPosition))) {

                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.highlighted_item_color));
                }


                String category = groupExpense.getCategory().getTitle();
                roundedTextView.setTextColor(Color.WHITE);
                roundedTextView.setCenterText(category.substring(0, 1).toUpperCase());
                roundedTextView.setCircleColor(groupExpense.getCategory().getColor());
                txtTitle2.setText(groupExpense.getTitle());
                //TODO wenn es eine Multiuser funktionalität muss hier der benutzer eingetragen werden, der das Geld ausgegeben hat
                txtPerson.setText("");
                txtPaidPrice.setText(String.format(mContext.getResources().getConfiguration().locale, "%.2f", groupExpense.getUnsignedPrice()));
                txtPaidPrice.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);
                txtPaidCurrency.setText(getMainCurrencySymbol());
                txtPaidCurrency.setTextColor(groupExpense.isExpenditure() ? mRed : mGreen);
                break;

            case TRANSFER_EXPENSE:

                //todo eigenes layout für tramsfer expenses definieren
                convertView = inflater.inflate(R.layout.activity_exp_listview_group, null);

                RoundedTextView roundedTextView3 = (RoundedTextView) convertView.findViewById(R.id.booking_item_circle);
                TextView txtTitle3 = (TextView) convertView.findViewById(R.id.booking_item_title);
                TextView txtPerson3 = (TextView) convertView.findViewById(R.id.booking_item_person);
                TextView txtPaidPrice3 = (TextView) convertView.findViewById(R.id.booking_item_paid_price);
                TextView txtPaidCurrency3 = (TextView) convertView.findViewById(R.id.booking_item_currency_paid);


                //if group is selected by the user the entry has to be highligted on redrawing
                if (mSelectedGroups.contains(getGroupId(groupPosition))) {

                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.highlighted_item_color));
                }


                String category2 = groupExpense.getCategory().getTitle();
                roundedTextView3.setTextColor(Color.WHITE);
                roundedTextView3.setCenterText(category2.substring(0, 1).toUpperCase());
                roundedTextView3.setCircleColor(groupExpense.getCategory().getColor());
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

            childViewHolder.roundedTextView = (RoundedTextView) convertView.findViewById(R.id.exp_list_view_item_circle);
            childViewHolder.txtTitle = (TextView) convertView.findViewById(R.id.exp_list_view_item_title);
            childViewHolder.txtPerson = (TextView) convertView.findViewById(R.id.exp_list_view_item_person);
            childViewHolder.txtPaidPrice = (TextView) convertView.findViewById(R.id.exp_list_view_item_paid_price);
            childViewHolder.txtBaseCurrency = (TextView) convertView.findViewById(R.id.exp_list_view_item_paid_currency);

            convertView.setTag(childViewHolder);
        } else {

            childViewHolder = (ChildViewHolder) convertView.getTag();
        }


        String category = childExpense.getCategory().getTitle();

        childViewHolder.roundedTextView.setTextColor(Color.WHITE);
        childViewHolder.roundedTextView.setCenterText(category.substring(0, 1).toUpperCase());
        childViewHolder.roundedTextView.setCircleColor(childExpense.getCategory().getColor());
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

    /**
     * Methode um ein Group in die Liste der ausgewählten Buchungen zu packen.
     *
     * @param groupExpense Ausgewählte Buchung
     */
    public void selectGroup(ExpenseObject groupExpense) {
        mSelectedGroups.add(groupExpense);
    }

    /**
     * Methode um eine Kindbuchung in die Liste der ausgewählten Buchungen zu packen.
     *
     * @param childExpense Ausgewählte KindBuchung
     */
    public void selectChild(ExpenseObject childExpense) {
        mSelectedChildren.add(childExpense);
    }

    /**
     * Methode um eine überprüfen ob eine Buchung ausgewählt ist oder nicht.
     *
     * @param groupExpense Id der zu überprüfenden GroupBuchung
     * @return True wenn sie ausgewählt ist, False andernfalls
     */
    public boolean isGroupSelected(ExpenseObject groupExpense) {
        return mSelectedGroups.contains(groupExpense);
    }

    /**
     * Methode um eine überprüfen ob eine KindBuchung ausgewählt ist oder nicht.
     *
     * @param childExpense Id der zu überprüfenden KindBuchung
     * @return True wenn sie ausgewählt ist, False andernfalls
     */
    public boolean isChildSelected(ExpenseObject childExpense) {
        return mSelectedChildren.contains(childExpense);
    }

    /**
     * Methode um eine GroupBuchung aus der Liste der ausgewählten Buchungen zu löschen.
     *
     * @param groupExpense Id der zu entfernenden Buchung
     */
    public void removeGroupFromList(ExpenseObject groupExpense) {
        mSelectedGroups.remove(groupExpense);
    }

    /**
     * Methode um eine ChildBuchung aus der Liste der ausgewählten Buchungen zu löschen.
     *
     * @param childExpense Id der zu entfernenden Buchung
     */
    public void removeChildFromList(ExpenseObject childExpense) {
        mSelectedChildren.remove(childExpense);
    }

    /**
     * Methode um die List der ausgewählte Buchungen zu löschen.
     */
    public void deselectAll() {
        mSelectedGroups.clear();
        mSelectedChildren.clear();
    }

    /**
     * Methode um die Anzahl der ausgewählten GroupBuchungen in erfahrung zu bringen.
     *
     * @return Anzahl der ausgewählten GroupBuchungen
     */
    public int getSelectedGroupCount() {
        int count = 0;
        for (ExpenseObject expense : mSelectedGroups) {
            if (!expense.isParent())
                count++;
        }

        return count;
    }

    public int getSelectedParentCount() {
        int count = 0;
        for(ExpenseObject expense : mSelectedGroups) {
            if (expense.isParent())
                count++;
        }

        return count;
    }

    /**
     * Methode um die Anzahl der ausgewählten KindBuchungen in erfahrung zu bringen.
     *
     * @return Anzahl der ausgewählten KindBuchungen
     */
    public int getSelectedChildCount() {
        return mSelectedChildren.size();
    }

    /**
     * Methode um die Anzahl der ausgewählten Buchungen in erfahrung zu bringen.
     *
     * @return Anzahl der ausgewählten Buchungen
     */
    public int getSelectedItemsCount() {
        return mSelectedChildren.size() + mSelectedGroups.size();
    }

    /**
     * Methode um alle ausgewählten GroupBuchungen als ExpenseObject zu erhalten
     *
     * @return Liste der GroupBuchungen
     */
    public ArrayList<ExpenseObject> getSelectedGroupData() {
        return mSelectedGroups;
    }

    /**
     * Methode um alle ausgewählten KindBuchungen als ExpenseObject zu erhalten
     *
     * @return Liste der GroupBuchungen
     */
    public ArrayList<ExpenseObject> getSelectedChildData() {
        return mSelectedChildren;
    }

    /**
     * Methode um alle Kindbuchungen zu einer ParentBuchung zu bekommen.
     *
     * @param parentExpense ParentBuchung
     * @return Liste aller KindBuchungen
     */
    public List<ExpenseObject> getAllChildrenToParent(ExpenseObject parentExpense) {
        return mChildData.get(parentExpense);
    }
}
