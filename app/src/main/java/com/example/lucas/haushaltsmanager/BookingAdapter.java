package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BookingAdapter extends ArrayAdapter<ExpenseObject> implements View.OnClickListener {

    private ArrayList<ExpenseObject> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtTitle;
        TextView txtPrice;
        TextView txtAccount;
    }

    public BookingAdapter(ArrayList<ExpenseObject> data, Context context) {
        super(context, R.layout.booking_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ExpenseObject expenseObject = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.booking_item, parent, false);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.listview_title);
            viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.listview_price);
            viewHolder.txtAccount = (TextView) convertView.findViewById(R.id.listview_account);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtTitle.setText(expenseObject.getTitle());
        viewHolder.txtPrice.setText(expenseObject.getUnsignedPrice() + "");
        viewHolder.txtAccount.setText(expenseObject.getAccount().getAccountName());

        return convertView;
    }


}
