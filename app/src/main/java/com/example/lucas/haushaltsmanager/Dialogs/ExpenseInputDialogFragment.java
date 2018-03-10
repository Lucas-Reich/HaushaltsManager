package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.lucas.haushaltsmanager.Activities.ExpenseScreenActivity;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

public class ExpenseInputDialogFragment extends DialogFragment {

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    //TODO diese klasse sollte durch den Basic Dialog ersetzt werden
    //dieses Dialog fragment ermöglicht die eingabe einer Notiz, eines Titels, der Tags
    public Dialog onCreateDialog(final Bundle savedInstances) {

        final Bundle args = getArguments();
        final Activity activity = getActivity();
        final ExpenseScreenActivity expenseScreen = (ExpenseScreenActivity) getActivity();
        final EditText input = new EditText(mContext);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(args.getString("original_title"));

        builder.setView(input);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Button btn = (Button) activity.findViewById(args.getInt("button_id"));

                if (input.getText().toString().length() != 0) {

                    setExpense(args.getInt("button_id"), expenseScreen.mExpense, input.getText().toString());
                    btn.setText(input.getText().toString());
                    btn.setTextColor(Color.BLACK);
                } else {

                    setExpense(args.getInt("button_id"), expenseScreen.mExpense, "");
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

            default:
                break;
        }
    }
}
