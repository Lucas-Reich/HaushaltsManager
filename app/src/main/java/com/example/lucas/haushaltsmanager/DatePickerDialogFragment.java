package com.example.lucas.haushaltsmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerDialogFragment extends DialogFragment {

    public Dialog OnCreateDialog(Bundle SavedInstances) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DatePicker picker = new DatePicker(getActivity());

        builder.setTitle("Mein Titel");

        builder.setView(picker);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // do smth
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // do smth
            }
        });

        return builder.create();
    }
}
