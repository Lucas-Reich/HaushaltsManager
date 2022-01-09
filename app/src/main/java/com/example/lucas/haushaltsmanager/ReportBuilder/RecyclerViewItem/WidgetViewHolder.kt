package com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem

import android.content.ClipData
import android.view.View
import android.widget.ImageView
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem
import com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems.CardViewItem
import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.Widget

class WidgetViewHolder(itemView: View) : AbstractViewHolder(itemView), View.OnLongClickListener {
    private val iconHolder: ImageView = itemView.findViewById(R.id.imageView)
    private var widget: Widget? = null

    override fun bind(item: IRecyclerItem) {
        if (item !is CardViewItem) {
            throw IllegalArgumentException(String.format(
                "Could not bind '%s' to ViewHolder of type '%s'!",
                item.javaClass.toString(),
                WidgetViewHolder::class.java
            ))
        }
        widget = item.content

        setWidgetIcon(item.content.icon)
    }

    override fun onLongClick(v: View): Boolean {
        v.startDragAndDrop(
            ClipData.newPlainText("widget_tag", "widget_tag"),
            View.DragShadowBuilder(v),
            widget,
            0
        )

        return true
    }

    private fun setWidgetIcon(widgetIcon: Int) {
        iconHolder.setImageResource(widgetIcon)
    }
}