package com.example.lucas.haushaltsmanager.RecyclerView.Items.ReportItem;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportItemViewHolder extends AbstractViewHolder {
    private final Resources resources;

    private final TextView title;
    private final MoneyTextView income;
    private final MoneyTextView expense;
    private final MoneyTextView total;
    private final TextView bookingCount;
    private final RoundedTextView categoryColor;
    private final TextView categoryTitle;
    private final PieChart pieChart;

    public ReportItemViewHolder(View itemView, Resources resources) {
        super(itemView);

        this.resources = resources;

        title = itemView.findViewById(R.id.timeframe_report_card_title);

        income = itemView.findViewById(R.id.time_frame_report_card_income);

        expense = itemView.findViewById(R.id.time_frame_report_card_expense);

        total = itemView.findViewById(R.id.time_frame_report_card_total);

        bookingCount = itemView.findViewById(R.id.timeframe_report_card_total_bookings);

        categoryColor = itemView.findViewById(R.id.timeframe_report_card_category_color);
        categoryTitle = itemView.findViewById(R.id.timeframe_report_card_category_title);

        pieChart = itemView.findViewById(R.id.timeframe_report_card_pie_chart);
    }

    @Override
    public void bind(IRecyclerItem item) {
        ReportInterface report = castToReportItem(item).getContent();

        setCardTitle(report.getCardTitle());
        setIncome(new Price(report.getIncoming(), report.getCurrency()));
        setOutgoing(new Price(report.getOutgoing(), report.getCurrency()));
        setTotal(new Price(report.getTotal(), report.getCurrency()));
        setTotalBookingsCount(report.getBookingCount());
        setCategory(report.getMostStressedCategory());
        setPieChart(report);
    }

    private ReportItem castToReportItem(IRecyclerItem item) {
        if (item instanceof ReportItem) {
            return (ReportItem) item;
        }

        throw new IllegalArgumentException(String.format(
                "Could not attach %s to %s",
                item.getClass(),
                getClass()
        ));
    }

    private void setCardTitle(String title) {
        this.title.setText(title);
    }

    private void setIncome(Price income) {
        this.income.bind(income);
    }

    private void setOutgoing(Price outgoing) {
        expense.bind(outgoing);
    }

    private void setTotal(Price total) {
        this.total.bind(total);
    }

    private void setTotalBookingsCount(int bookingsCount) {
        bookingCount.setText(String.format("%s %s", bookingsCount, getString(R.string.bookings)));
    }

    private void setCategory(Category category) {
        categoryColor.setCircleColor(category.getColor().getColorString());
        categoryTitle.setText(category.getTitle());
    }

    private void setPieChart(ReportInterface report) {
        pieChart.setData(preparePieData(report));
        pieChart.setDrawHoleEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setNoDataText(getString(R.string.no_bookings_in_year));
        pieChart.setNoDataTextColor(getColor(R.color.text_color_alert));
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true); // Muss aktiviert sein, sonst kann ich den Listener nicht setzen
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(app.getContext(), "" + e.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                // Do nothing
            }
        });
    }

    private PieData preparePieData(ReportInterface report) {
        if (report.getBookingCount() == 0) {
            return null;
        }

        List<PieEntry> pieData = new ArrayList<>();
        List<ExpenseObject> expenses = flattenExpenses(report.getExpenses());
        for (Map.Entry<Boolean, Double> entry : sumByExpenseType(expenses).entrySet()) {
            pieData.add(dataSetFrom(entry));
        }

        PieDataSet pds = new PieDataSet(pieData, "");
        pds.setColors(getColor(R.color.booking_income), getColor(R.color.booking_expense));
        pds.setDrawValues(false);

        return new PieData(pds);
    }

    private PieEntry dataSetFrom(Map.Entry<Boolean, Double> entry) {
        float value = Math.abs(entry.getValue().floatValue());

        return new PieEntry(
                value,
                "" // Es sollen keine Labels angezeigt werden
        );
    }

    @ColorInt
    private int getColor(@ColorRes int color) {
        return resources.getColor(color);
    }

    private String getString(@StringRes int string) {
        return resources.getString(string);
    }

    private List<ExpenseObject> flattenExpenses(List<ExpenseObject> expenses) {
        List<ExpenseObject> extractedChildren = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (expense.isParent())
                extractedChildren.addAll(expense.getChildren());
            else
                extractedChildren.add(expense);
        }

        return extractedChildren;
    }

    private HashMap<Boolean, Double> sumByExpenseType(List<ExpenseObject> expenses) {
        ExpenseSum expenseSum = new ExpenseSum();

        HashMap<Boolean, Double> summedExpenses = new HashMap<>();
        summedExpenses.put(true, expenseSum.byExpenditureType(true, expenses));
        summedExpenses.put(false, expenseSum.byExpenditureType(false, expenses));

        return summedExpenses;
    }
}
