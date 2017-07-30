package com.example.lucas.haushaltsmanager;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ExpenseScreen extends AppCompatActivity implements ExpenseInputDialogFragment.NoticeDialogListener, AccountPickerDialogFragment.NoticeDialogListener {

    private Calendar cal = Calendar.getInstance();
    private ExpenseObject expense = new ExpenseObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the displayed date to the current one
        Button setDate = (Button) findViewById(R.id.expense_screen_date);
        String today = cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR);
        setDate.setText(today);

        // set the account to the current main account
        Button setAccout = (Button) findViewById(R.id.expense_screen_account);
        String activeAccount = "Kreditkarte";
        setAccout.setText(activeAccount);

        // set the expense type to "expense"
        RadioGroup expenseType = (RadioGroup) findViewById(R.id.expense_screen_expense_type);
        expenseType.check(R.id.expense_screen_radio_expense);

        TextView amount = (TextView) findViewById(R.id.expense_screen_amount);
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ExpenseScreen.this, "Du möchtest den Betrag setzen", Toast.LENGTH_SHORT).show();
            }
        });

        CheckBox template = (CheckBox) findViewById(R.id.expense_screen_template);
        template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ExpenseScreen.this, "Du möchtest die Ausgabe als Vorlage speichern", Toast.LENGTH_SHORT).show();
            }
        });

        CheckBox recurring = (CheckBox) findViewById(R.id.expense_screen_recurring);
        recurring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ExpenseScreen.this, "Du möchtest die Ausgabe als wiederkehrendes Event speichern", Toast.LENGTH_SHORT).show();
            }
        });

        expenseType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.expense_screen_radio_expense) {

                    Toast.makeText(ExpenseScreen.this, "Das ist eine Ausgabe", Toast.LENGTH_SHORT).show();
                    expense.setExpenditure(true);
                } else {

                    Toast.makeText(ExpenseScreen.this, "Das ist eine Einnahme", Toast.LENGTH_SHORT).show();
                    expense.setExpenditure(false);
                }
            }
        });
    }

    public void onDialogPositiveClick(DialogFragment dialog) {

        Bundle args = dialog.getArguments();
        Button callingButton = (Button) findViewById(args.getInt("button_id"));

        if (args.getString("user_input").length() == 0) {

            callingButton.setText(args.getString("original_title"));
            callingButton.setTextColor(Color.DKGRAY);
        } else {

            callingButton.setText(args.getString("user_input"));
            callingButton.setTextColor(Color.BLACK);
        }

        switch (callingButton.getId()) {

            case R.id.expense_screen_title:

                expense.setExpenditureName(callingButton.getText().toString());
                break;

            case R.id.expense_screen_tag:

                expense.setTag(callingButton.getText().toString());
                break;

            case R.id.expense_screen_notice:

                expense.setNotice(callingButton.getText().toString());
        }
    }

    public void onDialogNegativeClick(DialogFragment dialog) {

        // do nothing
    }

    public void onItemSelected(DialogFragment dialog) {

        Button account_btn = (Button) findViewById(R.id.expense_screen_account);

        expense.setAccount(account_btn.getText().toString());
    }

    private void updateDate() {

        new DatePickerDialog(ExpenseScreen.this, d, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            Calendar expenditureDate = Calendar.getInstance();
            expenditureDate.set(year, (month + 1), dayOfMonth);

            Button btn_date = (Button) findViewById(R.id.expense_screen_date);
            btn_date.setText(dayOfMonth + "-" + (month + 1) + "-" + year);

            expense.setExpenditureDate(expenditureDate);
        }
    };

    public void expensePopUp(View view) {

        Bundle bundle = new Bundle();
        Button btn = (Button) findViewById(view.getId());

        switch(btn.getId()) {

            case R.id.expense_screen_amount:

                break;

            case R.id.expense_screen_category:

                bundle.putString("original_title", "Kategorie wählen");
                bundle.putInt("button_id", view.getId());
                break;

            case R.id.expense_screen_title:

                bundle.putString("original_title", "Titel eingeben");
                bundle.putInt("button_id", R.id.expense_screen_title);
                break;

            case R.id.expense_screen_tag:

                bundle.putString("original_title", "Merkmale hinzufügen");
                bundle.putInt("button_id", view.getId());
                break;

            case R.id.expense_screen_date:

                updateDate();
                break;

            case R.id.expense_screen_notice:

                bundle.putString("original_title", "Notiz eingeben");
                bundle.putInt("button_id", view.getId());
                break;

            case R.id.expense_screen_account:

                bundle.putString("original_title", "Choose Account");
                break;
        }


        if (btn.getId() == R.id.expense_screen_account) {

            AccountPickerDialogFragment accountPicker = new AccountPickerDialogFragment();
            accountPicker.setArguments(bundle);
            accountPicker.show(getFragmentManager(), "account_picker");
        } else if (btn.getId() != R.id.expense_screen_date){

            ExpenseInputDialogFragment expenseDialog = new ExpenseInputDialogFragment();
            expenseDialog.setArguments(bundle);
            expenseDialog.show(getFragmentManager(), "expenseDialog");
        }
    }
}
