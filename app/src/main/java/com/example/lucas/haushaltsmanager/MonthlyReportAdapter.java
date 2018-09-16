package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.lucas.androidcharts.DataSet;
import com.lucas.androidcharts.PieChart;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * source: https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
 */
public class MonthlyReportAdapter extends RecyclerView.Adapter<MonthlyReportAdapter.ViewHolder> {

    private Context mContext;
    private List<MonthlyReport> mReports;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView month, inbound, outbound, total, accountCurrency, stressedCategory, totalBookings;
        RoundedTextView categoryColor;
        PieChart pieChart;

        ViewHolder(View view) {
            super(view);
            month = view.findViewById(R.id.monthly_item_month);
            inbound = view.findViewById(R.id.monthly_item_inbound);
            outbound = view.findViewById(R.id.monthly_item_outbound);
            total = view.findViewById(R.id.monthly_item_total);
            totalBookings = view.findViewById(R.id.monthly_item_total_bookings);
            accountCurrency = view.findViewById(R.id.monthly_item_account_currency);
            stressedCategory = view.findViewById(R.id.monthly_item_most_stressed_category);

            categoryColor = view.findViewById(R.id.monthly_item_category_color);

            pieChart = view.findViewById(R.id.monthly_item_pie_chart);
        }
    }

    MonthlyReportAdapter(Context context, List<MonthlyReport> reports) {
        mContext = context;
        mReports = reports;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_report_card, parent, false);

        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        MonthlyReport report = mReports.get(position);

        viewHolder.month.setText(getMonth(Integer.parseInt(report.getCardTitle())));

        viewHolder.inbound.setText(formatMoney(report.getIncoming()));
        viewHolder.inbound.setTextColor(mContext.getResources().getColor(R.color.booking_income));

        viewHolder.outbound.setText(formatMoney(report.getOutgoing()));
        viewHolder.outbound.setTextColor(mContext.getResources().getColor(R.color.booking_expense));

        viewHolder.total.setText(formatMoney(report.getTotal()));
        if (report.getBookingCount() == 1)
            viewHolder.totalBookings.setText(String.format("%s %s", report.getBookingCount(), mContext.getResources().getString(R.string.month_report_booking)));
        else
            viewHolder.totalBookings.setText(String.format("%s %s", report.getBookingCount(), mContext.getResources().getString(R.string.month_report_bookings)));
        viewHolder.accountCurrency.setText(report.getCurrency().getSymbol());

        if (report.getBookingCount() == 0) {
            viewHolder.categoryColor.setVisibility(View.GONE);

            viewHolder.stressedCategory.setText(report.getMostStressedCategory().getTitle());
            viewHolder.stressedCategory.setTextColor(Color.RED);
        } else {
            viewHolder.categoryColor.setCircleDiameter(20);
            viewHolder.categoryColor.setCenterText("");
            viewHolder.categoryColor.setCircleColor(report.getMostStressedCategory().getColorString());

            viewHolder.stressedCategory.setText(report.getMostStressedCategory().getTitle());
        }

        viewHolder.pieChart.setPieData(preparePieData(report));
        viewHolder.pieChart.setNoDataText(R.string.no_bookings_in_month);
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
        if (monthlyReport.getBookingCount() == 0)
            return new ArrayList<>();

        List<DataSet> pieData = new ArrayList<>();
        pieData.add(new DataSet((float) monthlyReport.getIncoming(), mContext.getResources().getColor(R.color.booking_income), mContext.getResources().getString(R.string.incoming)));
        pieData.add(new DataSet((float) monthlyReport.getOutgoing(), mContext.getResources().getColor(R.color.booking_expense), mContext.getResources().getString(R.string.outgoing)));

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
