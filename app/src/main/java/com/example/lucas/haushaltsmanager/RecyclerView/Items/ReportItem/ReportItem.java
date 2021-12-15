package com.example.lucas.haushaltsmanager.RecyclerView.Items.ReportItem;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IExpandableRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.Report;

public class ReportItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 11;

    private final Report report;

    public ReportItem(Report report) {
        this.report = report;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Report getContent() {
        return report;
    }

    @Override
    public IExpandableRecyclerItem getParent() {
        return null;
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
