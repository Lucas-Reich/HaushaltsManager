package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem

class AppendInsertStrategy : InsertStrategy {
    override fun insert(item: IRecyclerItem, items: MutableList<IRecyclerItem>): Int {
        items.add(item)

        return items.size - 1
    }
}