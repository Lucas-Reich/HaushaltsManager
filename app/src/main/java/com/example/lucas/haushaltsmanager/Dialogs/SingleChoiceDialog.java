package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;

import com.example.lucas.haushaltsmanager.BundleUtils;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class SingleChoiceDialog<T extends Parcelable> extends DialogFragment {
    private static final String TAG = SingleChoiceDialog.class.getSimpleName();
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String SELECTED_ENTRY = "selected_entry";
    public static final String EXCLUDED_ENTRIES = "excluded_entries";
    public static final String ON_EMPTY_LIST_MESSAGE = "empty_list_message";

    private SingleChoiceDialog.OnEntrySelected mCallback;
    private Context mContext;
    private List<T> mEntrySet;
    private T mSelectedEntry;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils<T> args = new BundleUtils<>(getArguments());
        mEntrySet = args.getParcelableArrayList(CONTENT, new ArrayList<T>());
        removeExcludedEntries(args.getParcelableArrayList(EXCLUDED_ENTRIES, new ArrayList<T>()));
        //todo kann ich anstatt getParcelableArrayList auch getParcelableArray nehmen?

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(args.getString(TITLE, ""));

        //todo wenn die mEntrySet Liste leer ist soll stattdessen ein text angezeigt werden dass der user mit dem ok click ein neues objekt erstellen kann
        builder.setSingleChoiceItems(entriesToString(mEntrySet), args.getInt(SELECTED_ENTRY, -1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mSelectedEntry = mEntrySet.get(i);
            }
        });

        builder.setPositiveButton(R.string.btn_choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (mCallback != null)
                    mCallback.onEntrySelected(mSelectedEntry);

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

    private String[] entriesToString(List<T> entries) {
        String[] stringEntries = new String[entries.size()];

        for (int i = 0; i < entries.size(); i++) {
            stringEntries[i] = entries.get(i).toString();
        }

        return stringEntries;
    }

    private void removeExcludedEntries(List<T> excludedEntries) {
        for (T excludedEntry : excludedEntries) {
            if (mEntrySet.contains(excludedEntry)) {
                mEntrySet.remove(excludedEntry);
            }
        }
    }

    public void setOnEntrySelectedListener(SingleChoiceDialog.OnEntrySelected listener) {
        mCallback = listener;
    }

    public interface OnEntrySelected<T> {
        void onEntrySelected(T entry);
    }
}
