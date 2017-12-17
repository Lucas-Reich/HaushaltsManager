package com.example.lucas.haushaltsmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

public class CreateCategoryAlertDialog extends DialogFragment {

    private ExpensesDataSource expensesDataSource;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditText input = new EditText(getContext());
        expensesDataSource = new ExpensesDataSource(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Bitte den Namen der Neuen Kategorie eingeben");

        builder.setView(input);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String newCategory = input.getText().toString();

                expensesDataSource.open();
                expensesDataSource.createCategory(newCategory, "#000000");//TODO let user decide for color
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
