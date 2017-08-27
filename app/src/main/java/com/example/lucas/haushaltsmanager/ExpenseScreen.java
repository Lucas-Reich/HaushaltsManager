package com.example.lucas.haushaltsmanager;

import android.app.DatePickerDialog;//
import android.content.Intent;
import android.os.Bundle;//
import android.support.annotation.IdRes;//
import android.support.v7.app.AppCompatActivity;//
import android.support.v7.widget.Toolbar;//
import android.util.Log;
import android.view.View;//
import android.widget.Button;//
import android.widget.CheckBox;//
import android.widget.DatePicker;//
import android.widget.RadioGroup;//
import android.widget.TextView;//
import android.widget.Toast;//

import java.util.Calendar;

public class ExpenseScreen extends AppCompatActivity {

    public ExpenseObject EXPENSE = new ExpenseObject();
    private Calendar CAL = Calendar.getInstance();
    private String LOGTAG = "ExpenseScreen: ";
    private ExpensesDataSource expensesDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        //TODO change activeAccount from an hardcoded var to an account which the users decided beforehand
        expensesDataSource = new ExpensesDataSource(this);
        expensesDataSource.open();
        Account activeAccount = expensesDataSource.getAccountByName("Kreditkarte");
        expensesDataSource.close();




        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_screen);

        //TODO implement the correct Toolbar functionality (back arrow, overflow menu which holds the load template button)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the displayed date to the current one
        Button setDate = (Button) findViewById(R.id.expense_screen_date);
        //TODO today should be a string which looks like 01.01.2017 and not 1-1-2017
        String today = CAL.get(Calendar.DAY_OF_MONTH) + "-" + (CAL.get(Calendar.MONTH) + 1) + "-" + CAL.get(Calendar.YEAR);
        setDate.setText(today);
        EXPENSE.setDate(CAL);
        Log.d(LOGTAG, "set date to " + today + "");

        // set the account to the current main account
        Button accountBtn = (Button) findViewById(R.id.expense_screen_account);
        accountBtn.setText(activeAccount.getAccountName());
        EXPENSE.setAccount(activeAccount);
        Log.d(LOGTAG, "set active account to " + activeAccount.getAccountName());

        // set the EXPENSE type to "EXPENSE"
        RadioGroup expenseType = (RadioGroup) findViewById(R.id.expense_screen_expense_type);
        expenseType.check(R.id.expense_screen_radio_expense);
        EXPENSE.setExpenditure(false);
        Log.d(LOGTAG, "set expense type to " + false);


        //TODO implement AlertDialog which enables the user to input a number for the expense amount
        TextView amount = (TextView) findViewById(R.id.expense_screen_amount);
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("original_title", "0,00€");
                bundle.putInt("button_id", v.getId());

                PriceInputDialogFragment2 priceDialog = new PriceInputDialogFragment2();
                priceDialog.setArguments(bundle);
                priceDialog.show(getFragmentManager(), "expense_screen_price");
            }
        });

        //TODO implement the template functionality
        CheckBox template = (CheckBox) findViewById(R.id.expense_screen_template);
        template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ExpenseScreen.this, "Du möchtest die Ausgabe als Vorlage speichern", Toast.LENGTH_SHORT).show();
            }
        });

        //TODO implement the recurring event functionality
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

                if (EXPENSE.isSet()) {

                    expensesDataSource.open();
                    EXPENSE.setIndex(expensesDataSource.createBooking(EXPENSE));
                    expensesDataSource.getBookingById(EXPENSE.getIndex()).toConsole();
                    expensesDataSource.close();

                    Toast.makeText(ExpenseScreen.this, "Created booking \"" + EXPENSE.getTitle() + "\"", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(ExpenseScreen.this, MainActivityTab.class);
                    intent.putExtra("key", 10); //Optional parameters
                    ExpenseScreen.this.startActivity(intent);

                } else {

                    Toast.makeText(ExpenseScreen.this, getResources().getString(R.string.expense_screen_error_create), Toast.LENGTH_LONG).show();
                    EXPENSE.toConsole();
                }
            }

        });




        Button createAcc = (Button) findViewById(R.id.create_account);
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAccountDialogFragment createAccount = new CreateAccountDialogFragment();
                createAccount.show(getFragmentManager(), "create_new_account");
            }
        });



        Button createCat = (Button) findViewById(R.id.create_category);
        createCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateCategoryAlertDialog categoryDialog = new CreateCategoryAlertDialog();
                categoryDialog.show(getFragmentManager(), "create_new_category");
            }
        });


    }

    //TODO extract the input date logic to the DatePickerDialogFragment
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

        switch (btn.getId()) {

            case R.id.expense_screen_amount:

                break;

            case R.id.expense_screen_category:

                //TODO choose category from an given activity
                bundle.putString("original_title", getResources().getString(R.string.expense_screen_dsp_category));
                bundle.putInt("button_id", view.getId());
                break;

            case R.id.expense_screen_title:

                bundle.putString("original_title", getResources().getString(R.string.expense_screen_dsp_title));
                bundle.putInt("button_id", R.id.expense_screen_title);
                break;

            case R.id.expense_screen_tag:

                bundle.putString("original_title", getResources().getString(R.string.expense_screen_dsp_tag));
                bundle.putInt("button_id", view.getId());
                break;

            case R.id.expense_screen_date:

                updateDate();
                break;

            case R.id.expense_screen_notice:

                bundle.putString("original_title", getResources().getString(R.string.expense_screen_dsp_notice));
                bundle.putInt("button_id", view.getId());
                break;

            case R.id.expense_screen_account:

                Button accountBtn = (Button) findViewById(R.id.expense_screen_account);
                bundle.putString("original_title", getResources().getString(R.string.expense_screen_dsp_account));
                bundle.putString("current_account", accountBtn.getText().toString());
                break;
        }

        if (btn.getId() == R.id.expense_screen_account) {

            AccountPickerDialogFragment accountPicker = new AccountPickerDialogFragment();
            accountPicker.setArguments(bundle);
            accountPicker.show(getFragmentManager(), "account_picker");
        } else if (btn.getId() != R.id.expense_screen_date) {

            ExpenseInputDialogFragment expenseDialog = new ExpenseInputDialogFragment();
            expenseDialog.setArguments(bundle);
            expenseDialog.show(getFragmentManager(), "expenseDialog");
        }
    }
}
