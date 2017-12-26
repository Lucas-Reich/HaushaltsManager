package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class ExpenseInputDialogFragment extends DialogFragment {


    //TODO diese klasse sollte durch den Basic Dialog ersetzt werden
    //dieses Dialog fragment ermöglicht die eingabe einer Notiz, eines Titels, der Tags
    public Dialog onCreateDialog(final Bundle savedInstances) {

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

                Button btn = (Button) activity.findViewById(args.getInt("button_id"));

                if (input.getText().toString().length() != 0) {

                    setExpense(args.getInt("button_id"), expenseScreen.EXPENSE, input.getText().toString());
                    btn.setText(input.getText().toString());
                    btn.setTextColor(Color.BLACK);
                } else {

                    setExpense(args.getInt("button_id"), expenseScreen.EXPENSE, "");
                    btn.setText(args.getString("original_title"));
                    btn.setTextColor(Color.DKGRAY);
                }

                Log.d("ExpenseScreen", "set button text to " + btn.getText().toString());
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

    private void setExpense(int buttonId, ExpenseObject expense, String btnText) {

        switch (buttonId) {

            case R.id.expense_screen_title:

                expense.setTitle(btnText);
                break;

            case R.id.expense_screen_tag:

                expense.setTag(btnText);
                break;

            case R.id.expense_screen_notice:

                expense.setNotice(btnText);
                break;

            //TODO remove CATEGORY from ExpenseInputDialogFragment
            case R.id.expense_screen_category:


                ExpensesDataSource expenseDataSource = new ExpensesDataSource(getContext());
                expenseDataSource.open();

                expense.setCategory(expenseDataSource.getCategoryByName(btnText));

                expenseDataSource.close();
                break;

            default:
                break;
        }
    }
}
