package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy

import androidx.recyclerview.widget.RecyclerView
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem

class BookingListInsertStrategy : InsertStrategy {
    override fun insert(item: IRecyclerItem, items: MutableList<IRecyclerItem>): Int {
        val parentItem = item.parent ?: return insertParent(item, items)

        parentItem.addChild(item)

        if (parentItem.isExpanded) {
            return insertItemAfterParent(item, items)
        }

        return RecyclerView.NO_POSITION
    }

    private fun canInsert(dateItem: DateItem, parent: IRecyclerItem): Boolean {
        return dateItem.content.before(parent.content)
    }

    private fun insertItemAfterParent(child: IRecyclerItem, items: MutableList<IRecyclerItem>): Int {
        val afterParentIndex = getAfterParentIndex(child.parent, items)

        if (RecyclerView.NO_POSITION != afterParentIndex) {
            items.add(afterParentIndex, child)
        }

        return afterParentIndex
    }

    private fun getAfterParentIndex(item: IRecyclerItem?, items: List<IRecyclerItem>): Int {
        if (null == item) {
            return RecyclerView.NO_POSITION
        }

        val parentIndex = items.indexOf(item)

        if (parentIndex >= 0) {
            return parentIndex + 1
        }

        return RecyclerView.NO_POSITION
    }

    private fun insertParent(item: IRecyclerItem, items: MutableList<IRecyclerItem>): Int {
        if (item !is DateItem) {
            throw IllegalArgumentException(
                String.format(
                    "BookingListInsertStrategy requires DateItem as parents. Class given: %s",
                    item.javaClass.simpleName
                )
            )
        }

        val parentInsertIndex = getDateInsertIndex(item, items)

        items.add(parentInsertIndex, item)

        return parentInsertIndex
    }

    private fun getDateInsertIndex(dateItem: DateItem, items: List<IRecyclerItem>): Int {
        for (item in items) {
            if (item !is DateItem) {
                continue
            }

            if (canInsert(item, dateItem)) {
                return items.indexOf(item)
            }
        }

        return items.size
    }
}