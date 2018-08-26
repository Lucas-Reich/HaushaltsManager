package com.example.lucas.haushaltsmanager;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

public class BundleUtils<T extends Parcelable> {
    private Bundle mBundle;

    public BundleUtils(Bundle bundle) {
        mBundle = bundle == null ? new Bundle() : bundle;
    }

    /**
     * Methode um einen bestimmten Key aus dem Bundle zu erhalten.
     * Existiert dieser key nicht wird der Defaultwert zurückgegeben.
     *
     * @param key Key zu einem Value
     * @param def Wert der zurückgeben soll wenn der Key nicht existiert
     * @return Key value oder Default
     */
    public String getString(String key, String def) {

        return mBundle.containsKey(key) ? mBundle.getString(key) : def;
    }

    /**
     * Methode um einen bestimmten Key aus dem Bundle zu erhalten.
     * Existiert dieser key nicht wird der Defaultwert zurückgegeben, welcher als String resource gespeichert ist.
     *
     * @param key Key zu einem Value
     * @param def String Resource welche zurückgegeben werden soll wenn der Key nicht existiert
     * @return Key value oder Default
     */
    public String getString(String key, @StringRes int def) {

        String defaultString = Resources.getSystem().getString(def);
        return getString(key, defaultString);
    }

    public int getInt(String key, int def) {

        return mBundle.containsKey(key) ? mBundle.getInt(key) : def;
    }

    public String[] getStringArray(String key, String[] def) {

        return mBundle.containsKey(key) ? mBundle.getStringArray(key) : def;
    }

    public long getLong(String key, long def) {

        return mBundle.containsKey(key) ? mBundle.getLong(key) : def;
    }

    public List<T> getParcelableArrayList(String key, List<T> def) {

        if (mBundle.containsKey(key)) {
            return mBundle.getParcelableArrayList(key);
        } else {
            return def;
        }
//        return mBundle.containsKey(key) ? mBundle.getParcelableArrayList(key) : def;
    }
}
