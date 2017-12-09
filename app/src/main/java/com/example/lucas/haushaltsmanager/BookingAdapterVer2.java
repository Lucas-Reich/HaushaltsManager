package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class BookingAdapterVer2 extends ArrayAdapter<ExpenseObject> implements View.OnClickListener {

    private ArrayList<ExpenseObject> dataSet;
    private Context mContext;

    private static class ViewHolder {
        TextView circleLetter;
        TextView txtTitle;
        TextView txtPerson;
        TextView txtCalcPrice;
        TextView txtPaidPrice;
        TextView txtBaseCurrency;
        TextView txtPaidCurrency;
    }

    BookingAdapterVer2(ArrayList<ExpenseObject> data, Context context) {
        super(context, R.layout.booking_item_ver3, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        Toast.makeText(mContext, "du hast gecklickt", Toast.LENGTH_SHORT).show();
    }

    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ExpenseObject expenseObject = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.booking_item_ver3, parent, false);


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

        //TODO wenn eine buchung in einer Ausländischen währung vorliegt, muss der Preis in der standartwährung ausgegeben werden und auch das standartwährungsreichen angezeigt werden
        viewHolder.txtCalcPrice.setText("");
        viewHolder.txtBaseCurrency.setText("");

        return convertView;
    }
}
