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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

class MonthlyOverviewAdapter extends ArrayAdapter<MonthlyReport> implements View.OnClickListener, OnChartValueSelectedListener {

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

    MonthlyOverviewAdapter(ArrayList<MonthlyReport> data, Context context) {
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
        viewHolder.txtMonth.setText(monthlyReport.getMonth() + "/2017");
        viewHolder.txtInbound.setText(monthlyReport.countIncomingMoney() + "");
        viewHolder.txtOutbound.setText(monthlyReport.countOutgoingMoney() + "");
        viewHolder.txtTotal.setText(monthlyReport.calcMonthlyTotal() + "");
        viewHolder.txtTotalBookings.setText(monthlyReport.countBookings() + " Buchungen");
        viewHolder.txtAccountCurrency.setText(monthlyReport.getCurrency());
        viewHolder.colorCategory.setText("red");
        viewHolder.txtCategory.setText(monthlyReport.getMostStressedCategory());

        viewHolder.pieChart.setUsePercentValues(true);
        viewHolder.pieChart.setOnChartValueSelectedListener(this);
        addDataSet(viewHolder.pieChart);

        return convertView;
    }


    private void addDataSet(PieChart chart) {

        //TODO dataset muss sich die information für das diegramm noch aus dem aktuellen item ziehen
        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(55f, 0));
        yValues.add(new Entry(45f, 1));

        ArrayList<String> xValues = new ArrayList<>();

        xValues.add("Inbound");
        xValues.add("Outbound");

        PieDataSet pieDataSet = new PieDataSet(yValues, "Expenses");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);

        chart.setDrawHoleEnabled(false);

        PieData pieData = new PieData(xValues, pieDataSet);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextColor(Color.BLACK);
        pieData.setValueTextSize(18f);

        chart.setDescription("");
        chart.setRotationEnabled(false);
        chart.animateXY(1400, 1400);


        chart.setData(pieData);
        chart.invalidate();
    }

    @Override//TODO brauche ich überhaut ein klickbares diagramm??
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e != null) {

            Log.i("VAL SELECTED", "VALUE: " + e.getVal() + ", xIndex: " + e.getXIndex() + ", DataSet index: " + dataSetIndex);
        }
    }

    @Override
    public void onNothingSelected() {

        Log.i("PieChart", "nothing selected");
    }
}
