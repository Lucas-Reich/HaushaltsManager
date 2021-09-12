package com.example.lucas.haushaltsmanager.RecyclerView.Items.ReportItem;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.entities.Report.ReportInterface;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

public class ReportItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 11;

    private final ReportInterface report;

    public ReportItem(ReportInterface report) {
        this.report = report;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public ReportInterface getContent() {
        return report;
    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ReportItem)) {
            return false;
        }

        ReportItem other = (ReportItem) obj;

        return report.equals(other.getContent());
    }

    @Override
    @NonNull
    public String toString() {
        return report.toString();
    }
}
