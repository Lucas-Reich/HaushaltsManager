package com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

public class ChildCategoryViewHolder extends AbstractViewHolder {
    private static final String TAG = ChildCategoryViewHolder.class.getSimpleName();

    private RoundedTextView mRoundedTextView;
    private TextView mTitle;

    public ChildCategoryViewHolder(View itemView) {
        super(itemView);

        mRoundedTextView = itemView.findViewById(R.id.recycler_view_child_category_rounded_text_view);
        mTitle = itemView.findViewById(R.id.recycler_view_child_category_title);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof ChildCategoryItem)) {
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
        }

        Category category = (Category) item.getContent();

        setRoundedTextView(category);
        setTitle(category.getTitle());

        setBackgroundColor();
    }

    private void setBackgroundColor() {
        if (itemView.isSelected()) {
            itemView.setBackgroundColor(getColor(R.color.list_item_highlighted));
        } else {
            itemView.setBackgroundColor(getColor(R.color.list_item_background));
        }
    }

    private void setRoundedTextView(Category category) {
        mRoundedTextView.setCircleColorConsiderBrightness(category.getColor().getColorInt());
        mRoundedTextView.setCenterText(category.getTitle().charAt(0) + "");
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    private int getColor(@ColorRes int colorRes) {
        return app.getContext().getResources().getColor(colorRes);
    }
}
