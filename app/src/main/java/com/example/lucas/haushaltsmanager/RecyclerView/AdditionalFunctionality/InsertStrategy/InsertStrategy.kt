package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem

interface InsertStrategy {
    fun insert(item: IRecyclerItem, items: MutableList<IRecyclerItem>): Int
}