package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.lucas.haushaltsmanager.BundleUtils;
import com.example.lucas.haushaltsmanager.R;

public class ConfirmationDialog extends DialogFragment {
    private static final String TAG = ConfirmationDialog.class.getSimpleName();
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    private Context mContext;
    private OnConfirmationResult mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(args.getString(TITLE, ""));

        builder.setMessage(args.getString(CONTENT, ""));

        builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.onConfirmationResult(true);
            }
        });

        builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.onConfirmationResult(false);
            }
        });

        return builder.create();
    }

    public void setOnConfirmationListener(ConfirmationDialog.OnConfirmationResult listener) {
        mCallback = listener;
    }

    public interface OnConfirmationResult {
        void onConfirmationResult(boolean result);
    }
}
