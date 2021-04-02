package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentBooking;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentExpenseViewHolder extends AbstractViewHolder {
    private final PieChart pieChart;
    private final TextView title, user;
    private final MoneyTextView price;
    private final View divider;

    public ParentExpenseViewHolder(View itemView) {
        super(itemView);

        pieChart = itemView.findViewById(R.id.recycler_view_parent_chart);
        title = itemView.findViewById(R.id.recycler_view_parent_title);
        price = itemView.findViewById(R.id.recycler_view_parent_price);
        user = itemView.findViewById(R.id.recycler_view_parent_user);
        divider = itemView.findViewById(R.id.recycler_view_parent_divider);
    }

    @Override
    public void bind(IRecyclerItem item) {
        ParentBooking parent = guardAgainstWrongInstance(item).getContent();

        setPieChart(parent.getChildren());
        setTitle(parent.getTitle());
        setPrice(parent.getPrice());
        setUser("");
        setDivider(((ParentBookingItem) item).isExpanded());
    }

    private ParentBookingItem guardAgainstWrongInstance(IRecyclerItem item) {
        if (item instanceof ParentBookingItem) {
            return (ParentBookingItem) item;
        }

        throw new IllegalArgumentException(String.format(
                "Could not attach %s to %s",
                item.getClass().getSimpleName(),
                ParentExpenseViewHolder.class.getSimpleName()
        ));
    }

    private void setPieChart(List<ExpenseObject> bookings) {
        PieData data = createPieData(bookings);

        pieChart.setData(data);

        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setTouchEnabled(false);
        pieChart.setDrawHoleEnabled(false);
    }

    private PieData createPieData(List<ExpenseObject> bookings) {
        HashMap<Category, Double> map = new ExpenseSum().byCategory(bookings);

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        for (Map.Entry<Category, Double> entry : map.entrySet()) {

            colors.add(entry.getKey().getColor().getColorInt());
            entries.add(new PieEntry(
                    Math.abs(entry.getValue().floatValue())
            ));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);


        return new PieData(dataSet);
    }

    private void setTitle(String title) {
        this.title.setText(title);
    }

    private void setPrice(Price price) {
        this.price.bind(price);
    }

    private void setUser(String user) {
        this.user.setText(user);
    }

    private void setDivider(boolean isExpanded) {
        if (isExpanded) {
            divider.setVisibility(View.INVISIBLE);
        } else {
            divider.setVisibility(View.VISIBLE);
        }
    }
}
