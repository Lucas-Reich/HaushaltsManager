package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;

public class ErrorAlertDialog extends DialogFragment {
    private static final String TAG = ErrorAlertDialog.class.getSimpleName();
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString(TITLE, "Error"));

        builder.setMessage(args.getString(CONTENT, "Error"));

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        return builder.create();
    }
}
