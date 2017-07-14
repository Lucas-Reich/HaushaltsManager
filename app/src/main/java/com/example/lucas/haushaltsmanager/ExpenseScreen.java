package com.example.lucas.haushaltsmanager;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ExpenseScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_screen);

        TextView amountPopUp = (TextView) findViewById(R.id.expense_screen_amount);
        amountPopUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

                    Bundle bundle = new Bundle();
                    String dialogTitle = "";
                    bundle.putString("title", dialogTitle);

                    ExpenseInputDialogFragment accountPopUp = new ExpenseInputDialogFragment();
                    accountPopUp.setArguments(bundle);
                    accountPopUp.show(getFragmentManager(), "feuern");
                }
            }
        });

        EditText categoryPopUp = (EditText) findViewById(R.id.expense_screen_category);
        categoryPopUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

                    Bundle bundle = new Bundle();
                    String dialogTitle = "Kategorie";
                    bundle.putString("title", dialogTitle);

                    ExpenseInputDialogFragment accountPopUp = new ExpenseInputDialogFragment();
                    accountPopUp.setArguments(bundle);
                    accountPopUp.show(getFragmentManager(), "feuern");
                }
            }
        });

        EditText tagPopUp = (EditText) findViewById(R.id.expense_screen_tag);
        tagPopUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

                    Bundle bundle = new Bundle();
                    String dialogTitle = "Merkmal";
                    bundle.putString("title", dialogTitle);

                    ExpenseInputDialogFragment accountPopUp = new ExpenseInputDialogFragment();
                    accountPopUp.setArguments(bundle);
                    accountPopUp.show(getFragmentManager(), "feuern");
                }
            }
        });

        EditText titlePopUp = (EditText) findViewById(R.id.expense_screen_title);
        titlePopUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

                    Bundle bundle = new Bundle();
                    String dialogTitle = "Titel";
                    bundle.putString("title", dialogTitle);

                    ExpenseInputDialogFragment accountPopUp = new ExpenseInputDialogFragment();
                    accountPopUp.setArguments(bundle);
                    accountPopUp.show(getFragmentManager(), "feuern");
                }
            }
        });

        EditText datePopUp = (EditText) findViewById(R.id.expense_screen_date);
        datePopUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

                    Bundle bundle = new Bundle();
                    String dialogTitle = "Datum";
                    bundle.putString("title", dialogTitle);

                    ExpenseInputDialogFragment accountPopUp = new ExpenseInputDialogFragment();
                    accountPopUp.setArguments(bundle);
                    accountPopUp.show(getFragmentManager(), "feuern");
                }
            }
        });

        EditText noticePopUp = (EditText) findViewById(R.id.expense_screen_notice);
        noticePopUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

                    Bundle bundle = new Bundle();
                    String dialogTitle = "Notiz";
                    bundle.putString("title", dialogTitle);

                    ExpenseInputDialogFragment accountPopUp = new ExpenseInputDialogFragment();
                    accountPopUp.setArguments(bundle);
                    accountPopUp.show(getFragmentManager(), "feuern");
                }
            }
        });

        EditText accountPopUp = (EditText) findViewById(R.id.expense_screen_account);
        accountPopUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

                    Bundle bundle = new Bundle();
                    String dialogTitle = "Konten";
                    bundle.putString("title", dialogTitle);

                    ExpenseInputDialogFragment accountPopUp = new ExpenseInputDialogFragment();
                    accountPopUp.setArguments(bundle);
                    accountPopUp.show(getFragmentManager(), "feuern");
                }
            }
        });
    }
}
