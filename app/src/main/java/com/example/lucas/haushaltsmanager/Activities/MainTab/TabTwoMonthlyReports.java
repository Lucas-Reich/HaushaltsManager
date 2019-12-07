package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        updateView(rootView);

        return rootView;
    }

    public void updateView(View rootView) {

        MonthlyReportAdapter adapter = new MonthlyReportAdapterCreator(
                mParent.getVisibleExpenses(),
                getResources()
        ).getAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
}
