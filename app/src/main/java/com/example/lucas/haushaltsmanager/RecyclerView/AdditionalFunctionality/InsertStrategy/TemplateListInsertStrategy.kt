package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem

class TemplateListInsertStrategy : InsertStrategy {
    override fun insert(item: IRecyclerItem, items: MutableList<IRecyclerItem>): Int {
        guardAgainstIncorrectInstance(item)

        items.add(item)

        return items.size - 1
    }

    private fun guardAgainstIncorrectInstance(item: IRecyclerItem) {
        if (item is TemplateItem) {
            return
        }

        throw IllegalArgumentException(
            String.format(
                "TemplateListInsertStrategy requires TemplateItems. Class given: %s",
                item.javaClass.simpleName
            )
        )
    }
}