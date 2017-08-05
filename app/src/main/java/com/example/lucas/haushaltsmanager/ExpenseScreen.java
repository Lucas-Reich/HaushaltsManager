package com.example.lucas.haushaltsmanager;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ExpenseScreen extends AppCompatActivity {

    private Calendar CAL = Calendar.getInstance();
    public ExpenseObject EXPENSE = new ExpenseObject();
    private String LOGTAG = "ExpenseScreen: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the displayed date to the current one
        Button setDate = (Button) findViewById(R.id.expense_screen_date);
        String today = CAL.get(Calendar.DAY_OF_MONTH) + "-" + (CAL.get(Calendar.MONTH) + 1) + "-" + CAL.get(Calendar.YEAR);
        setDate.setText(today);
        EXPENSE.setDate(CAL);
        Log.d(LOGTAG, "set date to " + today + "");

        // set the account to the current main account
        Button accountBtn = (Button) findViewById(R.id.expense_screen_account);
        String activeAccount = "Kreditkarte";
        accountBtn.setText(activeAccount);
        EXPENSE.setAccount(activeAccount);
        Log.d(LOGTAG, "set active account to " + activeAccount);

        // set the EXPENSE type to "EXPENSE"
        RadioGroup expenseType = (RadioGroup) findViewById(R.id.expense_screen_expense_type);
        expenseType.check(R.id.expense_screen_radio_expense);
        EXPENSE.setExpenditure(false);
        Log.d(LOGTAG, "set expense type to " + false);

        TextView amount = (TextView) findViewById(R.id.expense_screen_amount);
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EXPENSE.setAmount(1);
                Log.d(LOGTAG, "set amount to 1");
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

                    EXPENSE.setExpenditure(true);
                    Log.d(LOGTAG, "set expense type to " + true);
                } else {

                    EXPENSE.setExpenditure(false);
                    Log.d(LOGTAG, "set expense type to " + false);
                }
            }
        });

        Button saveExpense = (Button) findViewById(R.id.expense_screen_create_booking);
        saveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOGTAG, EXPENSE.toString());
            }
        });
    }

    private void updateDate() {

        new DatePickerDialog(ExpenseScreen.this, d, CAL.get(Calendar.YEAR), CAL.get(Calendar.MONTH), CAL.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            Calendar expenditureDate = Calendar.getInstance();
            expenditureDate.set(year, (month + 1), dayOfMonth);

            Button btn_date = (Button) findViewById(R.id.expense_screen_date);
            btn_date.setText(dayOfMonth + "-" + (month + 1) + "-" + year);

            EXPENSE.setDate(expenditureDate);
            Log.d(LOGTAG, "set date to " + dayOfMonth + "-" + (month + 1) + "-" + year);
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

                Button accountBtn = (Button) findViewById(R.id.expense_screen_account);
                bundle.putString("original_title", "Choose Account");
                bundle.putString("current_account", accountBtn.getText().toString());
                break;
        }

        if (btn.getId() == R.id.expense_screen_account) {

            AccountPickerDialogFragment accountPicker = new AccountPickerDialogFragment();
            accountPicker.setArguments(bundle);
            accountPicker.show(getFragmentManager(), "account_picker");
            Log.d("test", "test");
        } else if (btn.getId() != R.id.expense_screen_date){

            ExpenseInputDialogFragment expenseDialog = new ExpenseInputDialogFragment();
            expenseDialog.setArguments(bundle);
            expenseDialog.show(getFragmentManager(), "expenseDialog");
        }
    }
}
