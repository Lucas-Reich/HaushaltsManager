package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.support.v4.app.Fragment;

public abstract class AbstractMainTab extends Fragment {

    /**
     * Methode um herauszufinden, ob der aktuelle tab gerade sichtbar geworden ist oder nicht.
     * Quelle: https://stackoverflow.com/a/9779971
     *
     * @param isVisibleToUser Indikator ob die aktuelle UI für den User sichtbar ist. Default ist True.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible() && isVisibleToUser)
            updateView();
    }

    public abstract void updateView();
}
