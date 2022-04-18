package com.example.lucas.haushaltsmanager.RecyclerView.Items.CategoryItem;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.entities.category.Category;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

public class CategoryViewHolder extends AbstractViewHolder {
    private static final String TAG = CategoryViewHolder.class.getSimpleName();

    private final RoundedTextView mRoundedTextView;
    private final TextView mTitle;

    public CategoryViewHolder(View itemView) {
        super(itemView);

        mRoundedTextView = itemView.findViewById(R.id.recycler_view_parent_category_rounded_text_view);
        mTitle = itemView.findViewById(R.id.recycler_view_parent_category_title);
    }

    @Override
    public void bind(IRecyclerItem item) {
        Category category = getCategoryFromContent(item);

        setRoundedTextView(category.getColor().getColorInt(), category.getName().charAt(0));
        setTitle(category.getName());
    }

    private Category getCategoryFromContent(IRecyclerItem item) throws IllegalArgumentException {
        if (item instanceof CategoryItem) {
            return (Category) item.getContent();
        }

        throw new IllegalArgumentException(String.format(
                "Could not attach %s to %s",
                item.getClass().getSimpleName(), TAG)
        );
    }

    private void setRoundedTextView(int color, char character) {
        mRoundedTextView.setCircleColorConsiderBrightness(color);
        mRoundedTextView.setCenterText(character + "");
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }
}
