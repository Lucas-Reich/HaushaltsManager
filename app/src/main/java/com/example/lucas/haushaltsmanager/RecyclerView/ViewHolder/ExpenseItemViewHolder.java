package com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Utils.ViewUtils;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

import androidx.annotation.ColorRes;

public class ExpenseItemViewHolder extends AbstractViewHolder {
    private static final String TAG = ExpenseItemViewHolder.class.getSimpleName();

    private RoundedTextView roundedTextView;
    private TextView title;
    private TextView person;
    private MoneyTextView price;

    public ExpenseItemViewHolder(View itemView) {
        super(itemView);

        roundedTextView = itemView.findViewById(R.id.recycler_view_expense_rounded_text_view);
        title = itemView.findViewById(R.id.recycler_view_expense_title);
        price = itemView.findViewById(R.id.recycler_view_expense_price);
        person = itemView.findViewById(R.id.recycler_view_expense_person);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof ExpenseItem)) {
            throw new IllegalArgumentException(String.format("Wrong type given in %s", TAG));
        }

        ExpenseObject expense = (ExpenseObject) item.getContent();

        setRoundedTextViewText(expense.getCategory());
        setTitle(expense.getTitle());
        setPrice(expense.getPrice());
        setPerson("");

        setBackgroundColor();
    }

    private void setBackgroundColor() {
        if (itemView.isSelected())
            itemView.setBackgroundColor(getColor(R.color.list_item_highlighted));
        else
            itemView.setBackgroundColor(getColor(R.color.list_item_background));
    }

    private void setRoundedTextViewText(Category category) {
        if (ViewUtils.getColorBrightness(category.getColorString()) > 0.5) {
            roundedTextView.setTextColor(getColor(R.color.primary_text_color_dark));
        } else {
            roundedTextView.setTextColor(getColor(R.color.primary_text_color_bright));
        }
        roundedTextView.setCenterText(category.getTitle().charAt(0) + "");
        roundedTextView.setCircleColor(category.getColorInt());
    }

    private void setTitle(String title) {
        this.title.setText(title);
    }

    private void setPrice(Price price) {
        this.price.bind(price);
    }

    private void setPerson(String person) {
        this.person.setText(person);
    }

    private int getColor(@ColorRes int colorRes) {
        return app.getContext().getResources().getColor(colorRes);
    }
}
