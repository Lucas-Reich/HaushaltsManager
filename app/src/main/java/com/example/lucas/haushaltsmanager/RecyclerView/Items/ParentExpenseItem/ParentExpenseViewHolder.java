package com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentExpenseItem;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.androidcharts.DataSet;
import com.example.lucas.androidcharts.PieChart;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentExpenseViewHolder extends AbstractViewHolder {
    private static final String TAG = ParentExpenseViewHolder.class.getSimpleName();

    private PieChart pieChart;
    private TextView title, user;
    private MoneyTextView price;
    private View divider;

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
        if (!(item instanceof ParentExpenseItem)) {
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
        }

        ParentExpenseObject parent = (ParentExpenseObject) item.getContent();

        setPieChart(createPieData(parent.getChildren()));
        setTitle(parent.getTitle());
        setPrice(parent.getPrice());
        setUser("");
        setDivider(((ParentExpenseItem) item).isExpanded());
    }

    private List<DataSet> createPieData(List<ExpenseObject> expenses) {
        List<DataSet> dataSets = new ArrayList<>();
        Map<Category, Integer> summedCategories = new HashMap<>();

        for (ExpenseObject expense : expenses) {
            Category category = expense.getCategory();
            Integer count = summedCategories.get(category);

            if (count != null)
                summedCategories.put(category, count + 1);
            else
                summedCategories.put(category, 1);
        }

        for (Map.Entry<Category, Integer> category : summedCategories.entrySet()) {
            dataSets.add(new DataSet(category.getValue(), category.getKey().getColor().getColorInt(), category.getKey().getTitle()));
        }

        return dataSets;
    }

    private void setPieChart(List<DataSet> data) {
        pieChart.setPieData(data);
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
