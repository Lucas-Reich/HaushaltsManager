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
    private val iconHolder: ImageView
    private lateinit var widget: Widget

    override fun bind(item: IRecyclerItem) {
        if (item !is CardViewItem) {
            throw IllegalArgumentException(
                String.format(
                    "Could not bind '%s' to ViewHolder of type '%s'!",
                    item.javaClass.toString(),
                    WidgetViewHolder::class.java
                )
            )
        }
        widget = item.content

        setWidgetIcon(item.content.icon)

        itemView.setOnLongClickListener(this)
    }

    override fun onLongClick(v: View): Boolean {
        v.startDragAndDrop(
            ClipData.newPlainText("widget_tag", "widget_tag"),
            View.DragShadowBuilder(v),
            widget.cloneWidget(v.context), // The widget needs to be cloned so that the contained view is newly instantiated, otherwise adding the same widget twice would cause the app to crash
            0
        )

        return true
    }

    private fun setWidgetIcon(widgetIcon: Int) {
        iconHolder.setImageResource(widgetIcon)
    }

    init {
        this.iconHolder = itemView.findViewById(R.id.imageView)
    }
}