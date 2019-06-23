package com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentCategoryItem;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

public class ParentCategoryViewHolder extends AbstractViewHolder {
    private static final String TAG = ParentCategoryViewHolder.class.getSimpleName();

    private RoundedTextView mRoundedTextView;
    private TextView mTitle;
    private View mDivider;

    public ParentCategoryViewHolder(View itemView) {
        super(itemView);

        mRoundedTextView = itemView.findViewById(R.id.recycler_view_parent_category_rounded_text_view);
        mTitle = itemView.findViewById(R.id.recycler_view_parent_category_title);
        mDivider = itemView.findViewById(R.id.recycler_view_parent_category_divider);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof ParentCategoryItem)) {
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
        }

        Category category = (Category) item.getContent();

        setRoundedTextView(category.getColor().getColorInt(), category.getTitle().charAt(0));
        setTitle(category.getTitle());
        setDivider(item.isExpanded());
    }

    private void setRoundedTextView(int color, char character) {
        mRoundedTextView.setCircleColorConsiderBrightness(color);
        mRoundedTextView.setCenterText(character + "");
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    private void setDivider(boolean isExpanded) {
        if (isExpanded) {
            mDivider.setVisibility(View.INVISIBLE);
        } else {
            mDivider.setVisibility(View.VISIBLE);
        }
    }
}
