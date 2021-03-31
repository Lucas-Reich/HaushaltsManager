package com.example.lucas.haushaltsmanager.RecyclerView.Items;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(IRecyclerItem item);

    // TODO: Sollte ich noch eine ChildKlasse erstellen, welche die Methode setBackgroundColor vorgibt, sodass man das Item markieren kann?
}
