package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.ListAdapter.AdapterCreator.MonthlyReportAdapterCreator;
import com.example.lucas.haushaltsmanager.ListAdapter.MonthlyReportAdapter;
import com.example.lucas.haushaltsmanager.R;

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

        updateView();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateView() {

        MonthlyReportAdapter adapter = new MonthlyReportAdapterCreator(
                mParent.getVisibleExpenses()
        ).getAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
}
