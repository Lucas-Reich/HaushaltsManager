package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Views.PieChart;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonthlyReportAdapter extends ArrayAdapter<MonthlyReport> implements View.OnClickListener {

    private String TAG = MonthlyReportAdapter.class.getSimpleName();

    private static class ViewHolder {
        TextView txtMonth;
        TextView txtInbound;
        TextView txtAccountCurrency;
        TextView txtOutbound;
        TextView txtTotal;
        TextView txtTotalBookings;
        RoundedTextView colorCategory;
        TextView txtCategory;
        PieChart pieChart;
    }

    public MonthlyReportAdapter(List<MonthlyReport> data, Context context) {

        super(context, R.layout.monthly_overview_item_v2, data);
    }

    @Override
    public void onClick(View v) {

        Log.d(TAG, "onClick: du hast geklickt");
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        MonthlyReport monthlyReport = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.monthly_overview_item_v2, parent, false);

            viewHolder.txtMonth = (TextView) convertView.findViewById(R.id.monthly_item_month);
            viewHolder.txtInbound = (TextView) convertView.findViewById(R.id.monthly_item_inbound);
            viewHolder.txtOutbound = (TextView) convertView.findViewById(R.id.monthly_item_outbound);
            viewHolder.txtTotal = (TextView) convertView.findViewById(R.id.monthly_item_total);
            viewHolder.txtTotalBookings = (TextView) convertView.findViewById(R.id.monthly_item_total_bookings);
            viewHolder.txtAccountCurrency = (TextView) convertView.findViewById(R.id.monthly_item_account_currency);
            viewHolder.colorCategory = (RoundedTextView) convertView.findViewById(R.id.monthly_item_category_color);
            viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.monthly_item_category);
            viewHolder.pieChart = (PieChart) convertView.findViewById(R.id.monthly_item_pie_chart);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.pieChart = (PieChart) convertView.findViewById(R.id.monthly_item_pie_chart);
        }

        viewHolder.txtMonth.setText(getMonth(Integer.parseInt(monthlyReport.getMonth())));
        viewHolder.txtInbound.setText(formatMoney(monthlyReport.countIncomingMoney()));
        viewHolder.txtOutbound.setText(formatMoney(monthlyReport.countOutgoingMoney()));
        viewHolder.txtTotal.setText(formatMoney(monthlyReport.calcMonthlyTotal()));

        if (monthlyReport.countBookings() <= 1) {

            viewHolder.txtTotalBookings.setText(String.format("%s  %s", monthlyReport.countBookings(), getContext().getResources().getString(R.string.month_report_booking)));
        } else {

            viewHolder.txtTotalBookings.setText(String.format("%s  %ss", monthlyReport.countBookings(), getContext().getResources().getString(R.string.month_report_booking)));
        }
        viewHolder.txtAccountCurrency.setText(monthlyReport.getCurrency());

        viewHolder.colorCategory.setCenterText("");
        viewHolder.colorCategory.setCircleColor("#EF1616");// dynamisch machen
        viewHolder.txtCategory.setText(monthlyReport.getMostStressedCategory());
        viewHolder.pieChart.setPieData(preparePieData(monthlyReport));

        return convertView;
    }

    /**
     * Methode um ein Geldbetrag des userlocale entsprechend zu formatieren.
     *
     * @param money Zu formatierender Geldbetrag
     * @return Formatierter Geldbetrag
     */
    private String formatMoney(double money) {

        Locale locale = getContext().getResources().getConfiguration().locale;
        return String.format(locale, "%.2f", money);
    }

    /**
     * Methode um die Daten für den PieChart vorzubereiten
     *
     * @param monthlyReport Report, welcher als PieChart dargestellt werden soll
     * @return DataSet's
     */
    private List<DataSet> preparePieData(MonthlyReport monthlyReport) {

        List<DataSet> pieData = new ArrayList<>();
        pieData.add(new DataSet((float) monthlyReport.countIncomingMoney(), getContext().getResources().getColor(R.color.booking_income), getContext().getResources().getString(R.string.incoming)));
        pieData.add(new DataSet((float) monthlyReport.countOutgoingMoney(), getContext().getResources().getColor(R.color.booking_expense), getContext().getResources().getString(R.string.outgoing)));

        return pieData;
    }

    /**
     * Methode um den Name eines Monats zu bekommen
     *
     * @param month Monatzahl
     * @return Monatname
     */
    private String getMonth(int month) {

        return new DateFormatSymbols().getMonths()[month - 1];
    }
}
