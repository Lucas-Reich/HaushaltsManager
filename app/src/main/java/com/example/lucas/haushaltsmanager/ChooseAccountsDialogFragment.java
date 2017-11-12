package com.example.lucas.haushaltsmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class ChooseAccountsDialogFragment extends DialogFragment {

    ExpensesDataSource expensesDataSource;
    boolean checkedItems[];
    SharedPreferences settings;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        settings = getActivity().getSharedPreferences("ActiveAccounts", 0);
        expensesDataSource = new ExpensesDataSource(getActivity());

        expensesDataSource.open();
        final String accounts[] = expensesDataSource.getAccountNames();
        expensesDataSource.close();

        final SharedPreferences.Editor editor = settings.edit();


        checkedItems = new boolean[accounts.length];
        for (int i = 0; i < accounts.length; i++) {

            checkedItems[i] = settings.getBoolean(accounts[i], false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.account);

        builder.setMultiChoiceItems(accounts, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                editor.putBoolean(accounts[which], isChecked);
                //TODO die buchungen der MainActivityTab live neuladen beim click auf ein Konto
            }
        });

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                editor.apply();
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
}
