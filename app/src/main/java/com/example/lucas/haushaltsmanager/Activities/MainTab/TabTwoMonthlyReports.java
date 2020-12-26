package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Activities.LayoutManagerFactory;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.ItemCreator;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.MonthlyReportListAdapter;

public class TabTwoMonthlyReports extends AbstractTab {
    private RecyclerView mRecyclerView;
    private ParentActivity mParent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParent = (ParentActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_two_monthly_reports, container, false);

        mRecyclerView = rootView.findViewById(R.id.tab_two_recycler_view);

        updateView(rootView);

        return rootView;
    }

    public void updateView(View rootView) {
        MonthlyReportListAdapter adapter = new MonthlyReportListAdapter(
                ItemCreator.createReportItems(mParent.getVisibleExpenses())
        );

        mRecyclerView.setLayoutManager(LayoutManagerFactory.vertical(getContext()));
        mRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
}
