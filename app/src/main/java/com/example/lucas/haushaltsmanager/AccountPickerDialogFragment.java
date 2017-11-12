package com.example.lucas.haushaltsmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

public class AccountPickerDialogFragment extends DialogFragment {

    private ExpensesDataSource expensesDataSource;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        expensesDataSource = new ExpensesDataSource(getActivity());
        expensesDataSource.open();

        final Bundle args = getArguments();
        final ExpenseScreen expenseScreen = (ExpenseScreen) getActivity();
        final Account[] accounts = expensesDataSource.getAllAccountsOld();
        final int activeAccount = (int)getActiveAccount(args.getString("current_account"), accounts).getIndex();
        final Button btn = (Button) expenseScreen.findViewById(R.id.expense_screen_account);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString("original_title"));

        builder.setSingleChoiceItems(accountsToStrings(accounts), activeAccount - 1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int selectedAccount) {

                btn.setText(accounts[selectedAccount].getAccountName());
                expenseScreen.EXPENSE.setAccount(accounts[selectedAccount]);


                expensesDataSource.close();
                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                expensesDataSource.close();
                dismiss();
            }
        });

        return builder.create();
    }

    private Account getActiveAccount(String currentAccount, Account[] accounts) {

        for (Account account : accounts) {

            if (account.getAccountName().equals(currentAccount)) {

                return account;
            }
        }

        return new Account();
    }

    private String[] accountsToStrings(Account[] accounts){

        String[] accountStrings = new String[accounts.length];

        for (int i = 0; i < accounts.length; i++) {

            accountStrings[i] = accounts[i].getAccountName();
        }

        return accountStrings;
    }

    public void onStop() {
        super.onStop();

        expensesDataSource.close();
    }
}
