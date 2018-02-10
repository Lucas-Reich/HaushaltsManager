package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class BookingAdapter extends ArrayAdapter<ExpenseObject> implements View.OnClickListener {

    private Context mContext;
    private SharedPreferences preferences;

    private static class ViewHolder {
        TextView circleLetter;
        TextView txtTitle;
        TextView txtPerson;
        TextView txtCalcPrice;
        TextView txtPaidPrice;
        TextView txtBaseCurrency;
        TextView txtPaidCurrency;
    }

    BookingAdapter(ArrayList<ExpenseObject> data, Context context) {
        super(context, R.layout.booking_item, data);

        this.mContext = context;
        this.preferences = context.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {

        Toast.makeText(mContext, "du hast gecklickt", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ExpenseObject expenseObject = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.booking_item, parent, false);


            viewHolder.circleLetter = (TextView) convertView.findViewById(R.id.booking_item_circle);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.booking_item_title);
            viewHolder.txtPerson = (TextView) convertView.findViewById(R.id.booking_item_person);
            viewHolder.txtPaidPrice = (TextView) convertView.findViewById(R.id.booking_item_paid_price);
            viewHolder.txtBaseCurrency = (TextView) convertView.findViewById(R.id.booking_item_currency_base);
            viewHolder.txtCalcPrice = (TextView) convertView.findViewById(R.id.booking_item_booking_price);
            viewHolder.txtPaidCurrency = (TextView) convertView.findViewById(R.id.booking_item_currency_paid);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        String category = expenseObject.getCategory().getCategoryName();

        viewHolder.circleLetter.setText(category.substring(0, 1).toUpperCase());
        viewHolder.txtTitle.setText(expenseObject.getTitle());
        //TODO wenn es eine Multiuser funktionalität muss hier der benutzer eingetragen werden, der das Geld ausgegeben hat
        viewHolder.txtPerson.setText("");
        viewHolder.txtPaidPrice.setText(String.format("%s", expenseObject.getUnsignedPrice()));
        viewHolder.txtPaidCurrency.setText(expenseObject.getAccount().getCurrency().getCurrencySymbol());

        if (expenseObject.getExpenseCurrency().getIndex() == preferences.getLong("mainCurrencyIndex", 0)) {

            viewHolder.txtCalcPrice.setText("");
            viewHolder.txtBaseCurrency.setText("");
        } else {

            viewHolder.txtCalcPrice.setText(String.format("%s", expenseObject.getCalcPrice()));
            viewHolder.txtBaseCurrency.setText(String.format("%s", expenseObject.getExpenseCurrency().getCurrencySymbol()));
        }

        return convertView;
    }
}
