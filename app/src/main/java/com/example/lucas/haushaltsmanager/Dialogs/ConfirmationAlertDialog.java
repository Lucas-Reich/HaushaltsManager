package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.R;

public class ConfirmationAlertDialog extends DialogFragment {
    private static final String TAG = ConfirmationAlertDialog.class.getSimpleName();

    private Context mContext;
    private OnConfirmationResult mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (OnConfirmationResult) context;
            mContext = context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement OnConfirmationResult!");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        String message = args.getString("message");

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(args.getString("title"));

        builder.setMessage(message);

        builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.onConfirmationResult(true, getTag());
            }
        });

        builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.onConfirmationResult(false, getTag());
            }
        });

        return builder.create();
    }

    public interface OnConfirmationResult {
        void onConfirmationResult(boolean result, String tag);
    }
}
