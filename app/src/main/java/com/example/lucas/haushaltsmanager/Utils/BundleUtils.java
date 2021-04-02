package com.example.lucas.haushaltsmanager.Utils;

import android.os.Bundle;

public class BundleUtils {
    private final Bundle mBundle;

    public BundleUtils(Bundle bundle) {
        mBundle = bundle == null ? new Bundle() : bundle;
    }

    public String getString(String key, String defaultValue) {

        return mBundle.containsKey(key) ? mBundle.getString(key) : defaultValue;
    }

    public long getLong(String key, long defaultValue) {

        return mBundle.containsKey(key) ? mBundle.getLong(key) : defaultValue;
    }

    public Object getParcelable(String key, Object defaultValue) {

        return mBundle.containsKey(key) ? mBundle.getParcelable(key) : defaultValue;
    }

    public boolean hasMapping(String key) {
        return mBundle.containsKey(key);
    }
}
