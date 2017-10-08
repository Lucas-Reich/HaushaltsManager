package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

class MonthlyReportAdapter extends ArrayAdapter<MonthlyReport> implements View.OnClickListener {

    private ArrayList<MonthlyReport> dataSet;
    private Context mContext;

    private static class ViewHolder {
        TextView txtMonth;
        TextView txtInbound;
        TextView txtAccountCurrency;
        TextView txtOutbound;
        TextView txtTotal;
        TextView txtTotalBookings;
        TextView colorCategory;
        TextView txtCategory;
        PieChart pieChart;
    }

    MonthlyReportAdapter(ArrayList<MonthlyReport> data, Context context) {
        super(context, R.layout.monthly_overview_item_v1, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        Toast.makeText(mContext, "du hast gecklickt", Toast.LENGTH_SHORT).show();
    }

    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        MonthlyReport monthlyReport = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.monthly_overview_item_v1, parent, false);

            viewHolder.txtMonth = (TextView) convertView.findViewById(R.id.monthly_item_month);
            viewHolder.txtInbound = (TextView) convertView.findViewById(R.id.monthly_item_inbound);
            viewHolder.txtOutbound = (TextView) convertView.findViewById(R.id.monthly_item_outbound);
            viewHolder.txtTotal = (TextView) convertView.findViewById(R.id.monthly_item_total);
            viewHolder.txtTotalBookings = (TextView) convertView.findViewById(R.id.monthly_item_total_bookings);
            viewHolder.txtAccountCurrency = (TextView) convertView.findViewById(R.id.monthly_item_account_currency);
            viewHolder.colorCategory = (TextView) convertView.findViewById(R.id.monthly_item_category_color);
            viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.monthly_item_category);
            viewHolder.pieChart = (PieChart) convertView.findViewById(R.id.monthly_item_pie_chart);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        //TODO setText mit platzhaltern füllen, sodass sich die ide nicht mehr beschwehrt
        viewHolder.txtMonth.setText(getMonth(Integer.parseInt(monthlyReport.getMonth())));
        viewHolder.txtInbound.setText(monthlyReport.countIncomingMoney() + "");
        viewHolder.txtOutbound.setText(monthlyReport.countOutgoingMoney() + "");
        viewHolder.txtTotal.setText(monthlyReport.calcMonthlyTotal() + "");

        if (monthlyReport.countBookings() <= 1) {

            viewHolder.txtTotalBookings.setText(monthlyReport.countBookings() + " " + mContext.getResources().getString(R.string.month_report_booking));
        } else {

            viewHolder.txtTotalBookings.setText(monthlyReport.countBookings() + " " + mContext.getResources().getString(R.string.month_report_bookings));
        }
        viewHolder.txtAccountCurrency.setText(monthlyReport.getCurrency());

        viewHolder.colorCategory.setText("red");
        viewHolder.txtCategory.setText(monthlyReport.getMostStressedCategory());

        viewHolder.pieChart.setUsePercentValues(true);
        addDataSet(viewHolder.pieChart, monthlyReport.countIncomingMoney(), monthlyReport.countOutgoingMoney());

        return convertView;
    }


    private void addDataSet(PieChart chart, double incoming, double outgoing) {

        ArrayList<Entry> yValues = new ArrayList<>();

        double totalMoney = incoming + outgoing;

        yValues.add(new Entry((float) (totalMoney / incoming), 0));
        yValues.add(new Entry((float) (totalMoney / outgoing), 1));

        ArrayList<String> xValues = new ArrayList<>();

        xValues.add("Outbound");
        xValues.add("Inbound");

        PieDataSet pieDataSet = new PieDataSet(yValues, "Expenses");
        pieDataSet.setValueTextSize(12);

        pieDataSet.setColors(new int[]{Color.rgb(235, 49, 50), Color.rgb(117, 165, 73)});

        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);

        chart.setDrawHoleEnabled(false);

        PieData pieData = new PieData(xValues, pieDataSet);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueTextSize(11f);

        chart.setDescription("");
        chart.setRotationEnabled(false);

        chart.setNoDataText("No Available Data");
        chart.setDrawSliceText(false);

        chart.setTouchEnabled(false);


        chart.setData(pieData);
        chart.invalidate();
    }

    private String getMonth(int month) {

        return new DateFormatSymbols().getMonths()[month - 1];
    }
}
