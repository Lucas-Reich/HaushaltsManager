package com.example.lucas.haushaltsmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AccountPickerDialogFragment extends DialogFragment {

    private ExpensesDataSource expensesDataSource;
    private String TAG = AccountPickerDialogFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        expensesDataSource = new ExpensesDataSource(getActivity());
        expensesDataSource.open();

        final Bundle args = getArguments();
        final ExpenseScreen expenseScreen = (ExpenseScreen) getActivity();
        final ArrayList<Account> accounts = expensesDataSource.getAllAccounts();
        final Account activeAccount = args.getParcelable("active_account");
        final Button accountBtn = (Button) expenseScreen.findViewById(R.id.expense_screen_account);
        final TextView currencyBtn = (TextView) expenseScreen.findViewById(R.id.expense_screen_amount_currency);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString("original_title"));

        int activeAccountId = activeAccount != null ? (int) activeAccount.getIndex() - 1 : -1;
        builder.setSingleChoiceItems(accountsToStrings(accounts), activeAccountId, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int selectedAccount) {

                accountBtn.setText(accounts.get(selectedAccount).getAccountName());
                currencyBtn.setText(accounts.get(selectedAccount).getCurrency().getCurrencySymbol());
                expenseScreen.mExpense.setAccount(accounts.get(selectedAccount));
                Log.d(TAG, "set active account to: " + accounts.get(selectedAccount).getAccountName() + ", " + accounts.get(selectedAccount).getIndex());

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

    private String[] accountsToStrings(ArrayList<Account> accounts) {

        String[] accountStrings = new String[accounts.size()];

        for (int i = 0; i < accounts.size(); i++) {

            accountStrings[i] = accounts.get(i).getAccountName();
        }

        return accountStrings;
    }

    public void onStop() {
        super.onStop();

        expensesDataSource.close();
    }
}
