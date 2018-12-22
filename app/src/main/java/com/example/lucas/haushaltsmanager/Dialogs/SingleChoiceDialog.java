package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.lucas.haushaltsmanager.R;

import java.util.List;

public class SingleChoiceDialog<T> extends DialogFragment {
    private static final String TAG = SingleChoiceDialog.class.getSimpleName();

    private SingleChoiceDialog.OnEntrySelected mCallback;
    private List<T> mEntrySet;
    private T mSelectedEntry;
    private AlertDialog.Builder builder;

    /**
     * Methode um den Builder zu initialisieren.
     * Ich musste diese Funktion erstellen, da ich hier keinen Konstruktor benutzen kann.
     *
     * @param context Context
     */
    public void createBuilder(Context context) {
        builder = new AlertDialog.Builder(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

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
     * Inhalt, welcher in der SingleChoice lister angezeigt wird.
     *
     * @param content Lister mit anzuzeigenden Objekten
     */
    public void setContent(List<T> content, int selectedEntry) {
        mEntrySet = content;

        builder.setSingleChoiceItems(stringfy(content), selectedEntry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mSelectedEntry = mEntrySet.get(i);
            }
        });
    }

    /**
     * Methode um die anzuzeigenden Objekte in ein Stringarray umzuwandeln.
     *
     * @param entries Anzuzeigende Objekte
     * @return String repräsentation der anzuzigenden Objekte
     */
    private String[] stringfy(List<T> entries) {
        String[] stringEntries = new String[entries.size()];

        for (int i = 0; i < entries.size(); i++) {
            stringEntries[i] = entries.get(i).toString();
        }

        return stringEntries;
    }

    /**
     * Methode um den Neutralen Button des AlertDialogs anzuzeigen.
     *
     * @param buttonText Text, welcher vom Button angezeigt wird
     */
    public void setNeutralButton(String buttonText) {
        builder.setNeutralButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (mCallback != null)
                    mCallback.onNeutralClick();
            }
        });
    }

    /**
     * Methode um den Title des AlertDialogs zu setzen.
     *
     * @param title Titel
     */
    public void setTitle(String title) {
        builder.setTitle(title);
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
