package com.example.lucas.haushaltsmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;

public class AccountPickerDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.d("test", "bla");
        final Bundle args = getArguments();
        final ExpenseScreen expenseScreen = (ExpenseScreen) getActivity();
        final String[] accounts = getResources().getStringArray(R.array.dummy_accounts);
        int selectedAccount = getIndexOfCurrentAccount(args.getString("current_account"), accounts);
        final Button btn = (Button) expenseScreen.findViewById(R.id.expense_screen_account);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString("original_title"));

        builder.setSingleChoiceItems(accounts, selectedAccount, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int selectedAccount) {

                btn.setText(accounts[selectedAccount]);
                expenseScreen.EXPENSE.setAccount(accounts[selectedAccount]);
                Log.d("ExpenseScreen", "set account to " + accounts[selectedAccount]);
                dismiss();
            }
        });

        builder.setNegativeButton(R.string.expense_pop_up_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        return builder.create();
    }

    private int getIndexOfCurrentAccount(String currentAccount, String[] accounts) {

        for(int i = 0; i < accounts.length; i++) {

            if (accounts[i].equals(currentAccount)) {

                return i;
            }
        }

        return -1;
    }
}
