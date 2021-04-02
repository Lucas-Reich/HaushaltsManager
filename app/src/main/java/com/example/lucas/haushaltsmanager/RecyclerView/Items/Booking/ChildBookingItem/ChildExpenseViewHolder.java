package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

public class ChildExpenseViewHolder extends AbstractViewHolder {
    private static final String TAG = ChildExpenseViewHolder.class.getSimpleName();

    private RoundedTextView roundedTextView;
    private TextView title;
    private MoneyTextView price;
    private TextView person;

    public ChildExpenseViewHolder(View itemView) {
        super(itemView);

        roundedTextView = itemView.findViewById(R.id.recycler_view_expense_rounded_text_view);
        title = itemView.findViewById(R.id.recycler_view_child_title);
        price = itemView.findViewById(R.id.recycler_view_child_price);
        person = itemView.findViewById(R.id.recycler_view_child_person);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof ChildExpenseItem)) {
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
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
        roundedTextView.setCenterText(category.getTitle().charAt(0) + "");
        roundedTextView.setCircleColorConsiderBrightness(category.getColor().getColorInt());
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
