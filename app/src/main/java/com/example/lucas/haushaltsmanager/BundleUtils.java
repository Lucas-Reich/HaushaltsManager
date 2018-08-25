package com.example.lucas.haushaltsmanager;

import android.os.Bundle;

public class BundleUtils {
    private Bundle mBundle;

    public BundleUtils(Bundle bundle) {
        mBundle = bundle;
    }

    /**
     * Methode um einen bestimmten Key aus dem Bundle zu erhalten. Existiert dieser key nicht wird der Defaultwert zurückgegeben
     *
     * @param key Key zu einem Value
     * @param def Wert der zurückgeben soll wenn der Key nicht existiert
     * @return Key value oder Default
     */
    public String getString(String key, String def) {
        String value = mBundle.getString(key);

        return value == null ? def : value;
    }
}
