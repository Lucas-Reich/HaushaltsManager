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

public class PriceInputDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();
        final Activity activity = getActivity();
        final ExpenseScreen expenseScreen = (ExpenseScreen) getActivity();
        final EditText input = new EditText(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(args.getString("original_title"));

        builder.setView(input);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                TextView txtView = (TextView) activity.findViewById(args.getInt("button_id"));

                if (input.getText().toString().length() != 0) {

                    expenseScreen.EXPENSE.setPrice(Double.parseDouble(input.getText().toString()));
                    Log.d("PriceInput", "set price to " + expenseScreen.EXPENSE.getPrice());
                    txtView.setText(input.getText().toString());
                    txtView.setTextColor(Color.BLACK);
                } else {

                    expenseScreen.EXPENSE.setPrice(0.0);
                    Log.d("PriceInput", "set price to " + expenseScreen.EXPENSE.getPrice());
                    txtView.setText(args.getString("original_title"));
                    txtView.setTextColor(Color.DKGRAY);
                }
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
}
