package com.example.lucas.haushaltsmanager.ExpenseImporter;

import android.util.Log;

import androidx.annotation.StringRes;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.KeyMappingInterface;

import java.lang.reflect.InvocationTargetException;

public class HeaderHolder {
    private static final String TAG = HeaderHolder.class.getSimpleName();

    private Class<KeyMappingInterface> clazz;
    private int headerField;

    public HeaderHolder(Class clazz, @StringRes int userVisibleStringRes) {
        this.clazz = clazz;
        headerField = userVisibleStringRes;
    }

    /**
     * Begriff welcher dem User für das zu mappende Feld angezeigt wird.
     *
     * @return String
     */
    @StringRes
    public int getHeaderField() {
        return headerField;
    }

    public KeyMappingInterface generateMapping(String fromValue) {
        try {
            return clazz.getConstructor(String.class).newInstance(fromValue);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            // TODO: Eigentlich sollten alle KeyMappings diesen Constructor haben, sodass diese Exceptions nicht auftreten können.
            Log.i(TAG, "This should never happen!");
        }

        return null;
    }
}
