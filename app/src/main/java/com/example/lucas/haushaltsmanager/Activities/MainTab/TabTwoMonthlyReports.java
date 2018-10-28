package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.MonthlyReportAdapter;
import com.example.lucas.haushaltsmanager.MonthlyReportAdapterCreator;
import com.example.lucas.haushaltsmanager.R;

public class TabTwoMonthlyReports extends Fragment {
    private static final String TAG = TabTwoMonthlyReports.class.getSimpleName();

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

    /**
     * Methode um die ExpandableListView nach eine Änderung neu anzuzeigen
     */
    public void updateView() {

        //fixme: irgendwie kann meine ChartLibrary nicht mehr auf ihre eigenen value files mehr zugreifen
        //da array in dem ich die style attribute für meinen chart definiere kann nicht mehr gefunden werden

        MonthlyReportAdapter adapter = new MonthlyReportAdapterCreator(
                mParent.getExpenses(),
                getContext(),
                mParent.getActiveAccounts()
        ).getAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }


    /**
     * Methode um herauszufinden, ob der aktuelle tab gerade sichtbar geworden ist oder nicht.
     * Quelle: https://stackoverflow.com/a/9779971
     *
     * @param isVisibleToUser Indikator ob die aktuelle UI für den User sichtbar ist. Default ist True.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {
            if (isVisibleToUser) {
                updateView();
                //todo nur updaten wenn etwas passiert ist
            }
        }
    }
}
