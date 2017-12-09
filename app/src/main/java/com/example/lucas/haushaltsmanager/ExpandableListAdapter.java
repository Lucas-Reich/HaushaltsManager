package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {


    private Context mContext;
    private List<ExpenseObject> listDataHeader;
    private HashMap<ExpenseObject, List<ExpenseObject>> listDataChild;
    private String TAG = "ExpandableListAdapter";
    private ArrayList<Long> selectedGroups;

    public ExpandableListAdapter(Context context, List<ExpenseObject> listDataHeader, HashMap<ExpenseObject, List<ExpenseObject>> listChildData) {

        this.mContext = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.selectedGroups = new ArrayList<>();
    }

    @Override
    public int getGroupCount() {

        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
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

        final ExpenseObject header = (ExpenseObject) getGroup(groupPosition);
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (getChildrenCount(groupPosition) == 0) {

            convertView = inflater.inflate(R.layout.activity_test_exp_listview_list_group_child_n, null);

            TextView circleLetter = (TextView) convertView.findViewById(R.id.booking_item_circle);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.booking_item_title);
            TextView txtPerson = (TextView) convertView.findViewById(R.id.booking_item_person);
            TextView txtPaidPrice = (TextView) convertView.findViewById(R.id.booking_item_paid_price);
            TextView txtPaidCurrency = (TextView) convertView.findViewById(R.id.booking_item_currency_paid);
            TextView txtCalcPrice = (TextView) convertView.findViewById(R.id.booking_item_booking_price);
            TextView txtBaseCurrency = (TextView) convertView.findViewById(R.id.booking_item_currency_base);


            if (selectedGroups.contains(getGroupId(groupPosition))) {

                convertView.setBackgroundColor(Color.GREEN);
            }


            String category = header.getCategory().getCategoryName();
            circleLetter.setText(category.substring(0, 1).toUpperCase());
            txtTitle.setText(header.getTitle());
            //TODO wenn es eine Multiuser funktionalität muss hier der benutzer eingetragen werden, der das Geld ausgegeben hat
            txtPerson.setText("");
            txtPaidPrice.setText(String.format("%s", header.getUnsignedPrice()));
            txtPaidCurrency.setText(header.getAccount().getCurrency().getCurrencySymbol());
            //TODO wenn eine buchung in einer Ausländischen währung vorliegt, muss der Preis in der standartwährung ausgegeben werden und auch das standartwährungsreichen angezeigt werden
            txtCalcPrice.setText("");
            txtBaseCurrency.setText("");

        } else {

            convertView = inflater.inflate(R.layout.activity_test_exp_listview_list_group_child_y, null);

            TextView txtTitle = (TextView) convertView.findViewById(R.id.exp_listview_header_name);
            TextView txtTotalAmount = (TextView) convertView.findViewById(R.id.exp_listview_header_total_amount);
            TextView txtBaseCurrency = (TextView) convertView.findViewById(R.id.exp_listview_header_base_currency);

            txtTitle.setText(header.getTitle());
            txtTotalAmount.setText(header.getUnsignedPrice() + "");
            txtBaseCurrency.setText("€");
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ExpenseObject child = (ExpenseObject) getChild(groupPosition, childPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_test_exp_listview_list_item, null);
        }

        TextView circleLetter = (TextView) convertView.findViewById(R.id.exp_listview_item_circle);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.exp_listview_item_title);
        TextView txtPerson = (TextView) convertView.findViewById(R.id.exp_listview_item_person);
        TextView txtPaidPrice = (TextView) convertView.findViewById(R.id.exp_listview_item_paid_price);
        TextView txtBaseCurrency = (TextView) convertView.findViewById(R.id.exp_listview_item_currency_base);
        TextView txtCalcPrice = (TextView) convertView.findViewById(R.id.exp_listview_item_booking_price);
        TextView txtPaidCurrency = (TextView) convertView.findViewById(R.id.exp_listview_item_currency_paid);


        String category = child.getCategory().getCategoryName();

        circleLetter.setText(category.substring(0, 1).toUpperCase());
        txtTitle.setText(child.getTitle());
        //TODO wenn es eine Multiuser funktionalität muss hier der benutzer eingetragen werden, der das Geld ausgegeben hat
        txtPerson.setText("");
        txtPaidPrice.setText(String.format("%s", child.getUnsignedPrice()));
        //TODO währung muss noch dynamisch gemacht werden, um mehrere Währungen zu unterstützen
        txtPaidCurrency.setText(child.getAccount().getCurrency().getCurrencySymbol());
        //TODO wenn eine buchung in einer Ausländischen währung vorliegt, muss der Preis in der standartwährung ausgegeben werden und auch das standartwährungsreichen angezeigt werden
        txtCalcPrice.setText("");
        txtBaseCurrency.setText("");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }


    boolean selectGroup(Long groupId) {

        return this.selectedGroups.add(groupId);
    }

    boolean isSelected(Long groupId) {

        return this.selectedGroups.contains(groupId);
    }

    boolean removeGroup(Long groupId) {

        return this.selectedGroups.remove(groupId);
    }

    boolean deselectAll() {

        this.selectedGroups.clear();
        return true;
    }

    int getSelectedCount() {

        return this.selectedGroups.size();
    }

    ArrayList<ExpenseObject> getSelectedGroupData() {

        ArrayList<ExpenseObject> groupData = new ArrayList<>();

        for (long groupId : this.selectedGroups) {

            groupData.add(listDataHeader.get((int) groupId));
        }

        return groupData;
    }

    long[] getSelectedBookingIds() {

        long bookingIds[] = new long[this.selectedGroups.size()];
        int counter = 0;

        for (long groupId : this.selectedGroups) {

            bookingIds[counter] = this.listDataHeader.get((int) groupId).getIndex();
            counter++;
        }

        return bookingIds;
    }
}
