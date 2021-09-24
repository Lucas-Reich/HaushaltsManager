package com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem;

import android.view.View;
import android.widget.TextView;

import androidx.room.Room;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Views.MoneyTextView;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.TemplateBooking;

import java.util.UUID;

public class TemplateViewHolder extends AbstractViewHolder {
    // TODO: Diese Klasse ist fast die gleiche wie ExpenseItemViewHolder.
    //  Kann ich die beiden zusammenführen?
    private static final String TAG = TemplateViewHolder.class.getSimpleName();

    private final RoundedTextView roundedTextView;
    private final TextView title, user;
    private final MoneyTextView money;

    public TemplateViewHolder(View itemView) {
        super(itemView);

        roundedTextView = itemView.findViewById(R.id.recycler_view_template_rounded_text_view);
        title = itemView.findViewById(R.id.recycler_view_template_title);
        user = itemView.findViewById(R.id.recycler_view_template_user);
        money = itemView.findViewById(R.id.recycler_view_template_price);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof TemplateItem)) {
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
        }

        TemplateBooking templateBooking = (TemplateBooking) item.getContent();

        setRoundedTextView(getCategory(templateBooking.getCategoryId()));
        setTitle(templateBooking.getTitle());
        setPrice(templateBooking.getPrice());
        setUser("");
    }

    private Category getCategory(UUID categoryId) {
        return Room.databaseBuilder(app.getContext(), AppDatabase.class, "expenses")
                .allowMainThreadQueries() // TODO: Remove
                .build().categoryDAO().get(categoryId);
    }

    private void setRoundedTextView(Category category) {
        roundedTextView.setCircleColorConsiderBrightness(category.getColor().getColorInt());
        roundedTextView.setCenterText(category.getName().charAt(0) + "");
    }

    private void setTitle(String title) {
        this.title.setText(title);
    }

    private void setPrice(Price price) {
        this.money.bind(price);
    }

    private void setUser(String user) {
        this.user.setText(user);
    }
}
