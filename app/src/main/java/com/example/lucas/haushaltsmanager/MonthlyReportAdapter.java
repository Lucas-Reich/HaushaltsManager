package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

class MonthlyReportAdapter extends ArrayAdapter<MonthlyReport> implements View.OnClickListener {

    private String TAG = MonthlyReportAdapter.class.getSimpleName();

    private static class ViewHolder {
        TextView txtMonth;
        TextView txtInbound;
        TextView txtAccountCurrency;
        TextView txtOutbound;
        TextView txtTotal;
        TextView txtTotalBookings;
        TextView colorCategory;
        TextView txtCategory;
        PieChartView pieChart;
    }

    MonthlyReportAdapter(List<MonthlyReport> data, Context context) {

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
            viewHolder.colorCategory = (TextView) convertView.findViewById(R.id.monthly_item_category_color);
            viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.monthly_item_category);
            viewHolder.pieChart = (PieChartView) convertView.findViewById(R.id.monthly_item_pie_chart);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.pieChart = (PieChartView) convertView.findViewById(R.id.monthly_item_pie_chart);
        }

        viewHolder.txtMonth.setText(getMonth(Integer.parseInt(monthlyReport.getMonth())));

        //formatiert die Ausgabe so, dass nur noch zwei nachkommastellen angezeigt werden
        viewHolder.txtInbound.setText(String.format(getContext().getResources().getConfiguration().locale, "%.2f", monthlyReport.countIncomingMoney()));
        viewHolder.txtOutbound.setText(String.format(getContext().getResources().getConfiguration().locale, "%.2f", monthlyReport.countOutgoingMoney()));
        viewHolder.txtTotal.setText(String.format(getContext().getResources().getConfiguration().locale, "%.2f", monthlyReport.calcMonthlyTotal()));

        if (monthlyReport.countBookings() <= 1) {

            viewHolder.txtTotalBookings.setText(String.format("%s  %s", monthlyReport.countBookings(), getContext().getResources().getString(R.string.month_report_booking)));
        } else {

            viewHolder.txtTotalBookings.setText(String.format("%s  %ss", monthlyReport.countBookings(), getContext().getResources().getString(R.string.month_report_booking)));
        }
        viewHolder.txtAccountCurrency.setText(monthlyReport.getCurrency());

        viewHolder.colorCategory.setText("red");
        viewHolder.txtCategory.setText(monthlyReport.getMostStressedCategory());

        preparePieData(viewHolder, monthlyReport);

        return convertView;
    }

    private void preparePieData(ViewHolder viewHolder, MonthlyReport monthlyReport) {

        String[] sliceLabels = new String[]{getContext().getResources().getString(R.string.incoming), getContext().getResources().getString(R.string.outgoing)};
        int[] sliceColors = new int[] {getContext().getResources().getColor(R.color.booking_expense), getContext().getResources().getColor(R.color.booking_income)};
        float[] pieData = new float[] {(float) monthlyReport.countIncomingMoney(),(float) monthlyReport.countOutgoingMoney()};

        viewHolder.pieChart.setPieData(pieData, sliceColors, sliceLabels);
    }

    private String getMonth(int month) {

        return new DateFormatSymbols().getMonths()[month - 1];
    }
}
