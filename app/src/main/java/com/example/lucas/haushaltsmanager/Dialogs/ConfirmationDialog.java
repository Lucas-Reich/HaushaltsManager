package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;

public class ConfirmationDialog extends DialogFragment {
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    private OnConfirmationResult mCallback;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString(TITLE, ""));

        builder.setMessage(args.getString(CONTENT, ""));

        builder.setPositiveButton(R.string.btn_yes, (dialog, which) -> {
            if (mCallback != null) {
                mCallback.onConfirmationResult(true);
            }
        });

        builder.setNegativeButton(R.string.btn_no, (dialog, which) -> {
            if (mCallback != null) {
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
