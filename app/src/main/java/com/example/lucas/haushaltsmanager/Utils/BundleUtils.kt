package com.example.lucas.haushaltsmanager.Utils

import android.os.Bundle

class BundleUtils(val bundle: Bundle) {
    fun findString(key: String): String {
        if (bundle.containsKey(key)) {
            return bundle.getString(key)!!
        }

        throw IllegalArgumentException("Bundle contains no key with value of '$key'!")
    }

    fun getString(key: String, defaultValue: String): String {
        return bundle.getString(key, defaultValue);
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return bundle.getLong(key, defaultValue)
    }

    fun getParcelable(key: String, defaultValue: Any?): Any? {
        if (bundle.containsKey(key)) {
            return bundle.getParcelable(key)!!
        }

        return defaultValue
    }

    fun hasMapping(key: String): Boolean {
        return bundle.containsKey(key)
    }
}