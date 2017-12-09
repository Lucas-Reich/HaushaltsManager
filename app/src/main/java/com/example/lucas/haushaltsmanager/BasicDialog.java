package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

public class BasicDialog extends DialogFragment {

    private EditText text;
    BasicDialogCommunicator mCallback;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String dialogTitle = bundle.getString("title") != null ? bundle.getString("title") : "No Title Specified";
        Activity activity = getActivity();
        text = new EditText(activity);

        if (getTag().equals("accountBalance")) {

            text = new EditText(activity);
            text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(dialogTitle);

        builder.setView(text);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.sendData(text.getText().toString(), getTag());
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

    public interface BasicDialogCommunicator {

        void sendData(String data, String tag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (BasicDialogCommunicator) context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement OnHeadLineSelectedListener");
        }
    }
}