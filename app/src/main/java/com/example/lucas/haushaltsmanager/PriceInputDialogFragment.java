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
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class PriceInputDialogFragment extends DialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();
        final Activity activity = getActivity();
        final ExpenseScreen expenseScreen = (ExpenseScreen) getActivity();
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(args.getDouble("original_title") + "");

        builder.setView(input);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                TextView txtView = (TextView) activity.findViewById(args.getInt("button_id"));

                if (input.getText().toString().length() != 0) {

                    expenseScreen.EXPENSE.setPrice(Double.parseDouble(input.getText().toString()));
                    Log.d("PriceInput", "set price to " + expenseScreen.EXPENSE.getUnsignedPrice());
                    txtView.setText(String.format("%s", expenseScreen.EXPENSE.getUnsignedPrice()));
                    txtView.setTextColor(Color.BLACK);
                } else {

                    expenseScreen.EXPENSE.setPrice(0.0);
                    Log.d("PriceInput", "set price to " + expenseScreen.EXPENSE.getUnsignedPrice());
                    txtView.setText(String.format("%s", args.getDouble("original_title")));
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

        //when user clicks ok on keyboard input gets send to activity
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE) {

                    TextView txtView = (TextView) activity.findViewById(args.getInt("button_id"));

                    if (input.getText().toString().length() != 0) {

                        expenseScreen.EXPENSE.setPrice(Double.parseDouble(input.getText().toString()));
                        Log.d("PriceInput", "set price to " + expenseScreen.EXPENSE.getUnsignedPrice());
                        txtView.setText(String.format("%s", expenseScreen.EXPENSE.getUnsignedPrice()));
                        txtView.setTextColor(Color.BLACK);
                    } else {

                        expenseScreen.EXPENSE.setPrice(0.0);
                        Log.d("PriceInput", "set price to " + expenseScreen.EXPENSE.getUnsignedPrice());
                        txtView.setText(String.format("%s", args.getDouble("original_title")));
                        txtView.setTextColor(Color.DKGRAY);
                    }
                    dismiss();
                    return false;
                }

                return false;
            }
        });


        return builder.create();
    }
}
