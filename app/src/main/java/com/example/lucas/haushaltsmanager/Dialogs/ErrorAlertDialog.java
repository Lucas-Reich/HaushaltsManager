package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.lucas.haushaltsmanager.R;

public class ErrorAlertDialog extends DialogFragment {
    private static final String TAG = ErrorAlertDialog.class.getSimpleName();

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String title = args.containsKey("title") ? args.getString("title") : "Error";
        String message = args.containsKey("message") ? args.getString("message") : "Error";

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(title);

        builder.setMessage(message);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        return builder.create();
    }
}
