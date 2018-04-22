package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Views.PieChart.PieChart;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * source: https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
 */
public class MonthlyReportAdapter extends RecyclerView.Adapter<MonthlyReportAdapter.MyViewHolder> {

    private Context mContext;
    private List<MonthlyReport> mReports;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView month, inbound, outbound, total, accountCurrency, stressedCategory, totalBookings;
        RoundedTextView categoryColor;
        PieChart pieChart;

        MyViewHolder(View view) {
            super(view);
            month = (TextView) view.findViewById(R.id.monthly_item_month);
            inbound = (TextView) view.findViewById(R.id.monthly_item_inbound);
            outbound = (TextView) view.findViewById(R.id.monthly_item_outbound);
            total = (TextView) view.findViewById(R.id.monthly_item_total);
            totalBookings = (TextView) view.findViewById(R.id.monthly_item_total_bookings);
            accountCurrency = (TextView) view.findViewById(R.id.monthly_item_account_currency);
            stressedCategory = (TextView) view.findViewById(R.id.monthly_item_most_stressed_category);

            categoryColor = (RoundedTextView) view.findViewById(R.id.monthly_item_category_color);

            pieChart = (PieChart) view.findViewById(R.id.monthly_item_pie_chart);
        }
    }

    MonthlyReportAdapter(Context context, List<MonthlyReport> reports) {
        mContext = context;
        mReports = reports;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_report_card, parent, false);

        return new MyViewHolder(itemView);
    }

    public void onBindViewHolder(final MyViewHolder holder, int position) {
        MonthlyReport report = mReports.get(position);
        holder.month.setText(getMonth(Integer.parseInt(report.getMonth())));
        holder.inbound.setText(formatMoney(report.countIncomingMoney()));
        holder.outbound.setText(formatMoney(report.countOutgoingMoney()));
        holder.total.setText(formatMoney(report.calcMonthlyTotal()));
        if (report.countBookings() == 1)
            holder.totalBookings.setText(String.format("%s %s", report.countBookings(), mContext.getResources().getString(R.string.month_report_booking)));
        else
            holder.totalBookings.setText(String.format("%s %s", report.countBookings(), mContext.getResources().getString(R.string.month_report_bookings)));
        holder.accountCurrency.setText(report.getCurrency());

        if (report.countBookings() == 0) {
            holder.categoryColor.setVisibility(View.GONE);

            holder.stressedCategory.setText(report.getMostStressedCategory().getName());
            holder.stressedCategory.setTextColor(Color.RED);
        } else {
            holder.categoryColor.setCircleDiameter(20);
            holder.categoryColor.setCenterText("");
            holder.categoryColor.setCircleColor(report.getMostStressedCategory().getColor());

            holder.stressedCategory.setText(report.getMostStressedCategory().getName());
        }

        holder.pieChart.setPieData(preparePieData(report));
        holder.pieChart.setNoDataText(R.string.no_bookings_in_month);
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }

    /**
     * Methode um die Daten für den PieChart vorzubereiten
     *
     * @param monthlyReport Report, welcher als PieChart dargestellt werden soll
     * @return DataSet's
     */
    private List<DataSet> preparePieData(MonthlyReport monthlyReport) {
        if (monthlyReport.countBookings() == 0)
            return new ArrayList<>();

        List<DataSet> pieData = new ArrayList<>();
        pieData.add(new DataSet((float) monthlyReport.countIncomingMoney(), mContext.getResources().getColor(R.color.booking_income), mContext.getResources().getString(R.string.incoming)));
        pieData.add(new DataSet((float) monthlyReport.countOutgoingMoney(), mContext.getResources().getColor(R.color.booking_expense), mContext.getResources().getString(R.string.outgoing)));

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

    /**
     * Methode um ein Geldbetrag des userlocale entsprechend zu formatieren.
     *
     * @param money Zu formatierender Geldbetrag
     * @return Formatierter Geldbetrag
     */
    private String formatMoney(double money) {

        Locale locale = mContext.getResources().getConfiguration().locale;
        return String.format(locale, "%.2f", money);
    }
}
