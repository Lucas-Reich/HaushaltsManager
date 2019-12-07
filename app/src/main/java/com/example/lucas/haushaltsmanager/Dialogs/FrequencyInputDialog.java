package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import com.example.lucas.haushaltsmanager.Entities.Frequency;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;

import java.util.Calendar;

public class FrequencyInputDialog extends DialogFragment {
    public static final String TITLE = "title";

    private OnFrequencySelected mCallback;
    private int mSelectedRecurrence;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString("title", "Frequency"));

        final String[] items = getActivity().getResources().getStringArray(R.array.recurrence_options);

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSelectedRecurrence = which;
            }
        });

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mCallback != null) {
                    int field = recurrenceToField(mSelectedRecurrence);

                    mCallback.onFrequencySet(new Frequency(field, 1), items[mSelectedRecurrence]);
                }

                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        return builder.create();
    }

    private int recurrenceToField(int selectedRecurrence) {
        switch (selectedRecurrence) {
            case 0:
                return Calendar.DATE;
            case 1:
                return Calendar.WEEK_OF_YEAR;
            case 2:
                return Calendar.MONTH;
            case 3:
                return Calendar.YEAR;
            default:
                return Calendar.MONTH;
        }
    }

    public void setOnFrequencySet(OnFrequencySelected listener) {
        mCallback = listener;
    }

    public interface OnFrequencySelected {
        void onFrequencySet(Frequency frequency, String text);
    }

}
