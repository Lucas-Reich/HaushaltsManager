package com.example.lucas.haushaltsmanager.RecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.InsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewSelectedItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules.SelectionRules;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import java.util.ArrayList;

public class MockItemHandler extends RecyclerViewSelectedItemHandler {
    public MockItemHandler(InsertStrategy insertStrategy, SelectionRules selectionRules) {
        super(new ArrayList<IRecyclerItem>(), insertStrategy, selectionRules);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
    }

    // TODO: Ich sollte vielleicht die notify...Changed methoden überschreiben, sodass ich keinen Context in den Unit tests brauche.
    //  Um dies zu tun kann ich einfach eine neue Methode erstellen (z.B. notifyItemHasChanged(int position)),
    //  welche dann die eigentliche notify Methode aufruft. In den Tests kann ich dann die neu erstellte Methode überschreiben,
    //  sodass sie keinen Call mehr an die richtige Methode macht.
}
