package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;

public class ExpenseInputDialogFragment extends DialogFragment {


    public interface NoticeDialogListener {

        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }


    NoticeDialogListener mListener;


    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {

            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {

            throw new ClassCastException(activity.toString() + "must implement NoticeDialogListener");
        }
    }


    public Dialog onCreateDialog(final Bundle savedInstances) {

        final Bundle args = getArguments();

        final Activity activ = getActivity();
        TextView amount = (TextView) activ.findViewById(R.id.expense_screen_amount);
        amount.setText("Test");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton(R.string.expense_pop_up_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                args.putString("user_input", input.getText().toString());
                mListener.onDialogPositiveClick(ExpenseInputDialogFragment.this);
            }
        });

        builder.setNegativeButton(R.string.expense_pop_up_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                args.putString("user_input", "");
                mListener.onDialogNegativeClick(ExpenseInputDialogFragment.this);
            }
        });

        builder.setTitle(args.getString("original_title"));

        return builder.create();
    }
}
