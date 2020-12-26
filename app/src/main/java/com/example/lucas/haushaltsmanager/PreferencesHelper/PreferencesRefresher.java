package com.example.lucas.haushaltsmanager.PreferencesHelper;

import android.content.Context;

public class PreferencesRefresher {
    private final SharedPreferences preferences;
    private RebuildStrategyInterface rebuildStrategy;

    public PreferencesRefresher(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void refresh(Context context) {
        clearCurrentPreferences();

        if (null != rebuildStrategy) {
            rebuildStrategy.rebuild(context, preferences);
        }
    }

    public void setRebuildStrategy(RebuildStrategyInterface rebuildStrategy) {
        this.rebuildStrategy = rebuildStrategy;
    }

    private void clearCurrentPreferences() {
        preferences.clear();
    }
}
