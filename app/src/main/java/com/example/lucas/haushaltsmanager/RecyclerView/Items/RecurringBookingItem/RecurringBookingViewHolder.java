package com.example.lucas.haushaltsmanager.RecyclerView.Items.RecurringBookingItem;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

import java.util.UUID;

public class RecurringBookingViewHolder extends AbstractViewHolder {
    // TODO: Dieser ViewHolder hat exakt die gleiche Struktur die auch der ExpenseViewHolder hat
    // --> kann man diese beiden ViewHolder irgendwie zusammen legen?
    private static final String TAG = RecurringBookingViewHolder.class.getSimpleName();

    private RoundedTextView mRoundedTextView;
    private TextView mTitle;
    private MoneyTextView mPrice;
    private TextView mPerson;

    public RecurringBookingViewHolder(View itemView) {
        super(itemView);

        mRoundedTextView = itemView.findViewById(R.id.recycler_view_expense_rounded_text_view);
        mTitle = itemView.findViewById(R.id.recycler_view_expense_title);
        mPrice = itemView.findViewById(R.id.recycler_view_expense_price);
        mPerson = itemView.findViewById(R.id.recycler_view_expense_person);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof RecurringBookingItem)) {
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
        }

        RecurringBooking recurringBooking = (RecurringBooking) item.getContent();

        setRoundedTextViewText(getCategory(recurringBooking.getCategoryId()));
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

    private Category getCategory(UUID categoryId) {
        return AppDatabase.getDatabase(app.getContext()).categoryDAO().get(categoryId);
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
