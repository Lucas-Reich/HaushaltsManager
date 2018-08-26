package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.lucas.haushaltsmanager.BundleUtils;
import com.example.lucas.haushaltsmanager.R;

import java.util.List;

public class SingleChoiceDialog<T> extends DialogFragment {
    private static final String TAG = SingleChoiceDialog.class.getSimpleName();
    public static final String SELECTED_ENTRY = "selected_entry";

    private SingleChoiceDialog.OnEntrySelected mCallback;
    private List<T> mEntrySet;
    private T mSelectedEntry;
    private AlertDialog.Builder builder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        builder = new AlertDialog.Builder(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        builder.setSingleChoiceItems(entriesToString(mEntrySet), args.getInt(SELECTED_ENTRY, -1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mSelectedEntry = mEntrySet.get(i);
            }
        });

        builder.setPositiveButton(R.string.btn_choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (mCallback != null && mSelectedEntry != null)
                    mCallback.onPositiveClick(mSelectedEntry);

                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dismiss();
            }
        });

        return builder.create();
    }

    /**
     * Methode um die anzuzeigenden Objekte in ein Stringarray umzuwandeln.
     *
     * @param entries Anzuzeigende Objekte
     * @return String repräsentation der anzuzigenden Objekte
     */
    private String[] entriesToString(List<T> entries) {
        String[] stringEntries = new String[entries.size()];

        for (int i = 0; i < entries.size(); i++) {
            stringEntries[i] = entries.get(i).toString();
        }

        return stringEntries;
    }

    /**
     * Methode um nicht bestimmte Objekte nicht in der View mit anzuzeigen.
     *
     * @param excludedEntries Nicht anzuzeigende Objekte
     */
    public void excludeEntries(List<T> excludedEntries) {
        for (T excludedEntry : excludedEntries) {
            if (mEntrySet.contains(excludedEntry)) {
                mEntrySet.remove(excludedEntry);
            }
        }
    }

    public void setContent(List<T> content) {
        mEntrySet = content;
    }

    public void setNeutralButton(String buttonText) {
        builder.setNeutralButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (mCallback != null)
                    mCallback.onNeutralClick();
            }
        });
    }

    public void setTitle(String title) {
        builder.setTitle(title);
    }

    public void createBuilder(Context context) {
        builder = new AlertDialog.Builder(context);
    }

    /**
     * Methode um einen Listener zu registrieren.
     *
     * @param listener Listener welcher aufgerufen werden soll
     */
    public void setOnEntrySelectedListener(SingleChoiceDialog.OnEntrySelected listener) {
        mCallback = listener;
    }

    public interface OnEntrySelected<T> {
        void onPositiveClick(T entry);

        void onNeutralClick();
    }
}
