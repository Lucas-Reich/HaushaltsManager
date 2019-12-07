package com.example.lucas.haushaltsmanager.RecyclerView.Items.CardViewItem;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.View;
import android.widget.ImageView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;

public class CardViewHolder extends AbstractViewHolder implements View.OnLongClickListener {
    private ImageView iconHolder;

    public CardViewHolder(View itemView) {
        super(itemView);

        iconHolder = itemView.findViewById(R.id.imageView);
        itemView.setOnLongClickListener(this);
        itemView.setTag("SOME RANDOM TAG");
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof CardViewItem)) {
            throw new IllegalArgumentException("FAAAAAIL!");
        }

        iconHolder.setImageResource(((CardViewItem) item).getContent());
    }

    @Override
    public boolean onLongClick(View v) {
        // Create a new ClipData.Item from the ImageView object's tag
        ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
        // Instantiates the drag shadow builder.
        View.DragShadowBuilder dragshadow = new View.DragShadowBuilder(v);
        // Starts the drag
        v.startDrag(data        // data to be dragged
                , dragshadow   // drag shadow builder
                , v           // local data about the drag and drop operation
                , 0          // flags (not currently used, set to 0)
        );
        return true;
    }
}
