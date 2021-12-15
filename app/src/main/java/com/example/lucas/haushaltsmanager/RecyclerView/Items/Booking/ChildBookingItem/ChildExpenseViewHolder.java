package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

public class ChildExpenseViewHolder extends AbstractViewHolder {
    private static final String TAG = ChildExpenseViewHolder.class.getSimpleName();

    private final RoundedTextView roundedTextView;
    private final TextView title;
    private final MoneyTextView price;
    private final TextView person;

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

        Booking expense = (Booking) item.getContent();

        setRoundedTextViewText(AppDatabase.getDatabase(app.getContext()).categoryDAO().get(expense.getCategoryId())); // TODO: Do differently
        setTitle(expense.getTitle());
        setPrice(expense.getPrice());
        setPerson("");
    }

    private void setRoundedTextViewText(Category category) {
        roundedTextView.setCenterText(category.getName().charAt(0) + "");
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
}
