package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.R;

public class TabThree extends Fragment {
    private static final String TAG = TabThree.class.getSimpleName();

    private CardView mIncomeCard, mExpenseCard, mTimeframeCard;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View rootView = inflater.inflate(R.layout.tab_three_ambigious_reports_ver2, container, false);

        mIncomeCard = rootView.findViewById(R.id.tab_three_income_card);
        mExpenseCard = rootView.findViewById(R.id.tab_three_expense_card);
        mTimeframeCard = rootView.findViewById(R.id.tab_three_timeframe_report_card);

        return rootView;
    }

    public void updateView() {

        //todo
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
                //todo die view sollte nur geupdated werden wenn es auch wirklich veränderungen gab
            }
        }
    }
}
