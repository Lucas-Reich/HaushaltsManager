package com.example.lucas.haushaltsmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

public class CreateAccountDialogFragment extends DialogFragment {

    private ExpensesDataSource expensesDataSource;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditText input = new EditText(getContext());
        expensesDataSource = new ExpensesDataSource(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Kontonamen eingeben");

        builder.setView(input);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String newAccount = input.getText().toString();

                expensesDataSource.open();
                expensesDataSource.createAccount(new Account(newAccount, 100));
                expensesDataSource.close();
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
