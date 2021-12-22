package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;

public class ErrorAlertDialog extends DialogFragment {
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString(TITLE, "Error"));

        builder.setMessage(args.getString(CONTENT, "Error"));

        builder.setPositiveButton(R.string.btn_ok, (dialog, which) -> dismiss());

        return builder.create();
    }
}
