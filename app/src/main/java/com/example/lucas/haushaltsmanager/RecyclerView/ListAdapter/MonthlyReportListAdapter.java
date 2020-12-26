package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.AppendInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ReportItem.ReportItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ReportItem.ReportItemViewHolder;

import java.util.List;

public class MonthlyReportListAdapter extends RecyclerViewItemHandler {
    public MonthlyReportListAdapter(List<IRecyclerItem> items) {
        super(items, new AppendInsertStrategy());
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ReportItem.VIEW_TYPE) {
            return new ReportItemViewHolder(inflater.inflate(
                    R.layout.timeframe_report_card,
                    parent,
                    false
            ), parent.getContext().getResources());
        }

        return super.onCreateViewHolder(parent, viewType);
    }
}
