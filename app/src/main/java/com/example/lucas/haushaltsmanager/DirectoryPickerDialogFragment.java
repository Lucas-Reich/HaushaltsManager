package com.example.lucas.haushaltsmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import java.io.File;

public class DirectoryPickerDialogFragment extends DialogFragment {

    OnDirectorySelected mCallback;
    String mDialogTitle;
    Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (OnDirectorySelected) context;
            mContext = context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement BasicDialogCommunicator");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        mDialogTitle = args.getString("title") != null ? args.getString("title") : "";

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

        dialog.setTitle(mDialogTitle);

        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //TODO create Directory lookup
                mCallback.onDirectorySelected(new File(""), getTag());
            }
        });

        dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        return dialog.create();
    }

    public interface OnDirectorySelected {

        void onDirectorySelected(File file, String tag);
    }
}
