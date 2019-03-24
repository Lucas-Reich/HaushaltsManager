package com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import java.util.Calendar;

public class DateViewHolder extends AbstractViewHolder {
    private static final String TAG = DateViewHolder.class.getSimpleName();

    private TextView mDate;

    public DateViewHolder(View itemView) {
        super(itemView);

        mDate = itemView.findViewById(R.id.recycler_view_date_text);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof DateItem)) {
            throw new IllegalArgumentException(String.format("Wrong type given in %s", TAG));
        }

        setDateText(((DateItem) item).getContent());
    }

    private void setDateText(Calendar date) {
        String humanReadableDate = formatDate(date);

        mDate.setText(humanReadableDate);
    }

    private String formatDate(Calendar date) {
        return CalendarUtils.formatHumanReadable(date);
    }
}
