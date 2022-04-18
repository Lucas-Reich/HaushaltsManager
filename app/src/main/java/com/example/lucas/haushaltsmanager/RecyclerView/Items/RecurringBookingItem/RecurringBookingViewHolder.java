package com.example.lucas.haushaltsmanager.RecyclerView.Items.RecurringBookingItem;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryRepository;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.entities.category.Category;

public class RecurringBookingViewHolder extends AbstractViewHolder {
    // TODO: Dieser ViewHolder hat exakt die gleiche Struktur die auch der ExpenseViewHolder hat
    // --> kann man diese beiden ViewHolder irgendwie zusammen legen?
    private static final String TAG = RecurringBookingViewHolder.class.getSimpleName();

    private final RoundedTextView mRoundedTextView;
    private final TextView mTitle;
    private final MoneyTextView mPrice;
    private final TextView mPerson;
    private final CategoryRepository categoryRepository;

    public RecurringBookingViewHolder(View itemView, CategoryRepository categoryRepository) {
        super(itemView);

        mRoundedTextView = itemView.findViewById(R.id.recycler_view_expense_rounded_text_view);
        mTitle = itemView.findViewById(R.id.recycler_view_expense_title);
        mPrice = itemView.findViewById(R.id.recycler_view_expense_price);
        mPerson = itemView.findViewById(R.id.recycler_view_expense_person);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof RecurringBookingItem)) {
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
        }

        RecurringBooking recurringBooking = (RecurringBooking) item.getContent();

        setRoundedTextViewText(categoryRepository.get(recurringBooking.getCategoryId()));
        setTitle(recurringBooking.getTitle());
        setPrice(recurringBooking.getPrice());
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
        mRoundedTextView.setCircleColorConsiderBrightness(category.getColor().getColorInt());
        mRoundedTextView.setCenterText(category.getName().charAt(0) + "");
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    private void setPrice(Price price) {
        mPrice.bind(price);
    }

    private void setPerson(String person) {
        mPerson.setText(person);
    }

    private int getColor(@ColorRes int colorRes) {
        return app.getContext().getResources().getColor(colorRes);
    }
}
