package com.example.lucas.haushaltsmanager.RecyclerView.Items.CardViewItem;

import android.content.ClipData;
import android.view.View;
import android.widget.ImageView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.ReportBuilder.IChart;

public class CardViewHolder extends AbstractViewHolder implements View.OnLongClickListener {
    private IChart chart;

    public CardViewHolder(View itemView) {
        super(itemView);

        itemView.setOnLongClickListener(this);
        itemView.setTag("Do I really need this tag?");
    }

    public void bind(IRecyclerItem item) {
        CardViewItem cardView = castToCardViewItem(item);
        chart = cardView.getContent();

        ImageView iconHolder = itemView.findViewById(R.id.imageView);
        iconHolder.setImageResource(chart.getImage());
    }

    public boolean onLongClick(View view) {
        String viewTag = view.getTag().toString();

        view.startDrag(
                ClipData.newPlainText(viewTag, viewTag)        // data to be dragged
                , new View.DragShadowBuilder(view)   // drag shadow builder
                , chart      // local data about the drag and drop operation
                , 0          // flags (not currently used, set to 0)
        );

        return true;
    }

    private CardViewItem castToCardViewItem(IRecyclerItem item) {
        if (item instanceof CardViewItem) {
            return (CardViewItem) item;
        }

        throw new IllegalArgumentException("FAAAAAIL!");
    }
}
