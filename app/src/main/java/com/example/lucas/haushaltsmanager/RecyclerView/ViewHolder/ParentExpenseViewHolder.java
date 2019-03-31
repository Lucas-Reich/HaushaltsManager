package com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.androidcharts.DataSet;
import com.example.lucas.androidcharts.PieChart;
import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentExpenseItem;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.ColorRes;

public class ParentExpenseViewHolder extends AbstractViewHolder {
    private static final String TAG = ParentExpenseViewHolder.class.getSimpleName();

    private PieChart mPieChart;
    private TextView mTitle, mPrice, mCurrency, mUser;
    private View mDivider;

    public ParentExpenseViewHolder(View itemView) {
        super(itemView);

        mPieChart = itemView.findViewById(R.id.recycler_view_parent_chart);
        mTitle = itemView.findViewById(R.id.recycler_view_parent_title);
        mPrice = itemView.findViewById(R.id.recycler_view_parent_price);
        mCurrency = itemView.findViewById(R.id.recycler_view_parent_currency);
        mUser = itemView.findViewById(R.id.recycler_view_parent_user);
        mDivider = itemView.findViewById(R.id.recycler_view_parent_divider);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof ParentExpenseItem)) {
            throw new IllegalArgumentException(String.format("Wrong type given in %s", TAG));
        }

        ParentExpenseObject parent = (ParentExpenseObject) item.getContent();

        setPieChart(createPieData(parent.getChildren()));
        setTitle(parent.getTitle());
        setPrice(parent.getPrice());
        setCurrency(parent.getCurrency(), parent.isExpenditure());
        setUser("");
        setDivider(item.isExpanded());
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
            dataSets.add(new DataSet(category.getValue(), category.getKey().getColorInt(), category.getKey().getTitle()));
        }

        return dataSets;
    }

    private void setPieChart(List<DataSet> data) {
        mPieChart.setPieData(data);
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    private void setPrice(Price price) {
        mPrice.setText(MoneyUtils.formatHumanReadable(price, Locale.getDefault()));

        if (price.isNegative())
            mPrice.setTextColor(getColor(R.color.booking_expense));
        else
            mPrice.setTextColor(getColor(R.color.booking_income));
    }

    private void setCurrency(Currency currency, boolean isExpenditure) {
        mCurrency.setText(currency.getSymbol());

        if (isExpenditure)
            mCurrency.setTextColor(getColor(R.color.booking_expense));
        else
            mCurrency.setTextColor(getColor(R.color.booking_income));
    }

    private void setUser(String user) {
        mUser.setText(user);
    }

    private void setDivider(boolean isExpanded) {
        if (isExpanded) {
            mDivider.setVisibility(View.INVISIBLE);
        } else {
            mDivider.setVisibility(View.VISIBLE);
        }
    }

    private int getColor(@ColorRes int colorRes) {
        return app.getContext().getResources().getColor(colorRes);
    }
}
