package com.example.lucas.haushaltsmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;

public class BasicDialog extends DialogFragment {

    BasicDialogCommunicator mCallback;
    private EditText mTextInput;
    Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (BasicDialogCommunicator) context;
            mContext = context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement BasicDialogCommunicator");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String dialogTitle = bundle.getString("title") != null ? bundle.getString("title") : "";
        mTextInput = new EditText(mContext);

        if (getTag().equals("accountBalance"))
            mTextInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(dialogTitle);

        builder.setView(mTextInput);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.onTextInput(mTextInput.getText().toString(), getTag());
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

        void onTextInput(String data, String tag);
    }
}