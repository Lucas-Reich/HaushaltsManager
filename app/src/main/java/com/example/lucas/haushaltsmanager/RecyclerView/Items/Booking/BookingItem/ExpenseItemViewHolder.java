package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

public class ExpenseItemViewHolder extends AbstractViewHolder {
    private static final String TAG = ExpenseItemViewHolder.class.getSimpleName();

    private final RoundedTextView roundedTextView;
    private final TextView title;
    private final TextView person;
    private final MoneyTextView price;

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
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
        }

        Booking expense = (Booking) item.getContent();

        CategoryDAO categoryRepository = AppDatabase.getDatabase(app.getContext()).categoryDAO();
        Category category = categoryRepository.get(expense.getCategoryId()); // TODO: Do differently
        setRoundedTextViewText(category);
        setTitle(expense.getTitle());
        setPrice(expense.getPrice());
        setPerson("");
    }

    private void setRoundedTextViewText(Category category) {
        roundedTextView.setCircleColorConsiderBrightness(category.getColor().getColorInt());
        roundedTextView.setCenterText(category.getName().charAt(0) + "");
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
