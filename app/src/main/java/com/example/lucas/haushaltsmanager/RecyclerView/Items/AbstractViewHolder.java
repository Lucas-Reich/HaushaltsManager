package com.example.lucas.haushaltsmanager.RecyclerView.Items;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(IRecyclerItem item);

    // TODO: Sollte ich noch eine ChildKlasse erstellen, welche die Methode setBackgroundColor vorgibt, sodass man das Item markieren kann?
}
