package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class PriceInputDialogFragment2 extends DialogFragment {


    private double price = 0.0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();
        final Activity activity = getActivity();
        final ExpenseScreen expenseScreen = (ExpenseScreen) getActivity();
        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(args.getString("original_title"));

        builder.setView(R.layout.price_input);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    private void calcPrice(double number) {

        price += number;
    }
}
