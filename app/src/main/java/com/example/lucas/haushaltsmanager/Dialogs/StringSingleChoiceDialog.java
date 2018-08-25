package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.lucas.haushaltsmanager.BundleUtils;
import com.example.lucas.haushaltsmanager.R;

public class StringSingleChoiceDialog extends DialogFragment {
    private static final String TAG = StringSingleChoiceDialog.class.getSimpleName();
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String SELECTED_ENTRY = "selected_entry";

    private OnEntrySelected mCallback;
    private Context mContext;
    private String[] mEntrySet;
    private String mSelectedEntry = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //todo kann ich den dialog so schreiben dass er alles anzeigen kann (also nicht nur strings)
        BundleUtils args = new BundleUtils(getArguments());
        mEntrySet = args.getStringArray(CONTENT, new String[]{ });

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(args.getString(TITLE, ""));

        builder.setSingleChoiceItems(mEntrySet, args.getInt(SELECTED_ENTRY, -1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mSelectedEntry = mEntrySet[i];
            }
        });

        builder.setPositiveButton(R.string.btn_choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (mCallback != null)
                    mCallback.onEntrySelected(mSelectedEntry);
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //do nothing
            }
        });

        return builder.create();
    }

    public void setOnEntrySelectedListener(StringSingleChoiceDialog.OnEntrySelected listener) {
        mCallback = listener;
    }

    public interface OnEntrySelected {
        void onEntrySelected(String entry);
    }
}
