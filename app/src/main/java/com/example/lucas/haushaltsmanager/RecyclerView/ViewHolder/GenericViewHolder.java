package com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

public class GenericViewHolder extends AbstractViewHolder {
    private TextView mText;

    public GenericViewHolder(View itemView) {
        super(itemView);

        mText = itemView.findViewById(R.id.recycler_view_generic_text);
    }

    public void bind(IRecyclerItem item) {
        setText(item.getContent().toString());
    }

    private void setText(String text) {
        mText.setText(text);
    }
}
