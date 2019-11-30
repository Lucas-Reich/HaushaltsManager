package com.example.lucas.haushaltsmanager.PreferencesHelper;

import android.content.Context;

import com.example.lucas.haushaltsmanager.PreferencesHelper.SharedPreferences;

public interface RebuildStrategyInterface {
    void rebuild(Context context, SharedPreferences preferences);
}
