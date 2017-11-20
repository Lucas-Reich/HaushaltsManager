package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseScreen extends AppCompatActivity {

    public ExpenseObject EXPENSE = new ExpenseObject();
    private Calendar CAL = Calendar.getInstance();
    private String TAG = "ExpenseScreen: ";
    private ExpensesDataSource expensesDataSource;
    private boolean template = false, recurring = false;
    private Calendar recurringEndDate = Calendar.getInstance();
    public int frequency = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("UserSettings", 0);

        /**
         * wenn ExpenseScreen von einer bereits bestehenden Buchung aufgerufen wird, dann soll diese buchung aus der datenbank geholt werden
         */
        expensesDataSource = new ExpensesDataSource(this);
        expensesDataSource.open();

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.get("parentExpense") != null) {

            EXPENSE = expensesDataSource.getBookingById(bundle.getLong("parentExpense"));
        } else if (bundle != null && bundle.get("childExpense") != null) {

            EXPENSE = expensesDataSource.getChildBookingById(bundle.getLong("childExpense"));
        } else {

            // dummy expense befülllen
            EXPENSE.setCategory(new Category(getResources().getString(R.string.expense_screen_dsp_category), 0));// dummy Kategorie
            EXPENSE.setTitle(getResources().getString(R.string.expense_screen_title));
            EXPENSE.setTag(getResources().getString(R.string.expense_screen_dsp_tag));
            EXPENSE.setPrice(0);
            EXPENSE.setNotice(getResources().getString(R.string.expense_screen_dsp_notice));
            EXPENSE.setTag(getResources().getString(R.string.expense_screen_dsp_tag));
            EXPENSE.setDate(CAL);
            EXPENSE.setAccount(expensesDataSource.getAccountById(preferences.getLong("activeAccount", 0)));
            EXPENSE.setExpenditure(true);

        }
        expensesDataSource.close();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_screen);

        //TODO implement the correct Toolbar functionality (back arrow, overflow menu which holds the load template button)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the displayed date to the current one
        Button setDate = (Button) findViewById(R.id.expense_screen_date);
        setDate.setText(EXPENSE.getDate().get(Calendar.YEAR) + "-" + EXPENSE.getDate().get(Calendar.MONTH) + "-" + EXPENSE.getDate().get(Calendar.DAY_OF_MONTH));
        //setDate.setText(DateUtils.formatDateTime(this, EXPENSE.getDate().getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
        Log.d(TAG, "set date to " + setDate.getText());


        // set the account to the current main account
        Button accountBtn = (Button) findViewById(R.id.expense_screen_account);
        accountBtn.setText(EXPENSE.getAccount().getAccountName());
        Log.d(TAG, "set active account to: " + EXPENSE.getAccount().getAccountName());

        // set the EXPENSE type
        RadioGroup expenseType = (RadioGroup) findViewById(R.id.expense_screen_expense_type);
        if (EXPENSE.getExpenditure()) {

            expenseType.check(R.id.expense_screen_radio_expense);
        } else {

            expenseType.check(R.id.expense_screen_radio_income);
        }
        Log.d(TAG, "set expense type to " + EXPENSE.getExpenditure());

        expenseType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.expense_screen_radio_expense) {

                    EXPENSE.setExpenditure(true);
                    Log.d(TAG, "set expense type to " + EXPENSE.getExpenditure());
                } else {

                    EXPENSE.setExpenditure(false);
                    Log.d(TAG, "set expense type to " + EXPENSE.getExpenditure());
                }
            }
        });


        //TODO implement AlertDialog which enables the user to input a number for the expense amount
        TextView amount = (TextView) findViewById(R.id.expense_screen_amount);
        amount.setText(EXPENSE.getPrice() + "");
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putDouble("original_title", EXPENSE.getPrice());
                bundle.putInt("button_id", v.getId());

                PriceInputDialogFragment priceDialog = new PriceInputDialogFragment();
                priceDialog.setArguments(bundle);
                priceDialog.show(getFragmentManager(), "expense_screen_price");
            }
        });

        //set account currency symbol
        TextView expenseCurrency = (TextView) findViewById(R.id.expense_screen_amount_currency);
        expenseCurrency.setText(EXPENSE.getAccount().getCurrencySym());

        //set display category
        TextView category = (TextView) findViewById(R.id.expense_screen_category);
        category.setText(EXPENSE.getCategory().getCategoryName());

        //set display expense title
        TextView expenseTitle = (TextView) findViewById(R.id.expense_screen_title);
        expenseTitle.setText(EXPENSE.getTitle());

        //TODO change display tag behaviour from just taking the first tag to displaying all tags
        //set display tag
        TextView expenseTag = (TextView) findViewById(R.id.expense_screen_tag);
        expenseTag.setText(EXPENSE.getTags().get(0));

        //set display notice
        TextView expenseNotice = (TextView) findViewById(R.id.expense_screen_notice);
        expenseNotice.setText(EXPENSE.getNotice());

        final CheckBox templateChk = (CheckBox) findViewById(R.id.expense_screen_template);
        templateChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                template = true;
                Toast.makeText(ExpenseScreen.this, "Du möchtest die Ausgabe als Vorlage speichern", Toast.LENGTH_SHORT).show();
            }
        });

        final CheckBox recurringChk = (CheckBox) findViewById(R.id.expense_screen_recurring);
        recurringChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView imgFrequency = (ImageView) findViewById(R.id.img_frequency);
                Button recurringFrequency = (Button) findViewById(R.id.expense_screen_recurring_frequency);
                recurringFrequency.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FrequencyAlertDialog freqDia = new FrequencyAlertDialog();
                        freqDia.show(getFragmentManager(), "expense_screen_frequency_dialog");
                    }
                });

                ImageView imgEnd = (ImageView) findViewById(R.id.img_end);
                Button recurringEnd = (Button) findViewById(R.id.expense_screen_recurring_end);
                recurringEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        updateDate("frequencyDate");
                    }
                });
                if (recurringChk.isChecked()) {

                    imgFrequency.setVisibility(ImageView.VISIBLE);
                    recurringFrequency.setVisibility(Button.VISIBLE);

                    imgEnd.setVisibility(ImageView.VISIBLE);
                    recurringEnd.setVisibility(Button.VISIBLE);
                    recurring = true;
                } else {

                    imgFrequency.setVisibility(ImageView.GONE);
                    recurringFrequency.setVisibility(Button.GONE);

                    imgEnd.setVisibility(ImageView.GONE);
                    recurringEnd.setVisibility(Button.GONE);
                    recurring = false;
                }
            }
        });

        Button saveExpense = (Button) findViewById(R.id.expense_screen_create_booking);
        saveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (EXPENSE.isSet()) {

                    expensesDataSource.open();
                    if (bundle.get("parentIndex") != null) {

                        EXPENSE.setIndex(expensesDataSource.createChildBooking(EXPENSE, bundle.getLong("parentIndex")));

                        Toast.makeText(ExpenseScreen.this, "Added Child " + EXPENSE.getTitle() + " to its Parent " + bundle.getLong("parentIndex"), Toast.LENGTH_SHORT).show();
                    } else {

                        EXPENSE.setIndex(expensesDataSource.createBooking(EXPENSE));
                        expensesDataSource.getBookingById(EXPENSE.getIndex()).toConsole();


                        Toast.makeText(ExpenseScreen.this, "Created booking \"" + EXPENSE.getTitle() + "\"", Toast.LENGTH_SHORT).show();
                    }

                    if (recurring) {

                        // frequency is saved as duration in hours, endDate is saved as Calendar object
                        long index = expensesDataSource.createRecurringBooking(EXPENSE.getIndex(), CAL, frequency, recurringEndDate);
                        Log.d(TAG, "created recurring booking event at index: " + index);
                    }

                    if (template) {

                        long index = expensesDataSource.createTemplateBooking(EXPENSE.getIndex());
                        Log.d(TAG, "created template for bookings at index: " + index);
                    }
                    expensesDataSource.close();
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


        Button createChild = (Button) findViewById(R.id.create_child);
        createChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent addChild = new Intent(ExpenseScreen.this, ExpenseScreen.class);
                addChild.putExtra("parentIndex", EXPENSE.getIndex());
                startActivity(addChild);
            }
        });
    }


    //TODO extract the input date logic to the DatePickerDialogFragment
    private void updateDate(String caller) {

        if (caller.equals("expenseDate")) {

            new DatePickerDialog(ExpenseScreen.this, d, EXPENSE.getDate().get(Calendar.YEAR), EXPENSE.getDate().get(Calendar.MONTH), EXPENSE.getDate().get(Calendar.DAY_OF_MONTH)).show();
        } else {

            new DatePickerDialog(ExpenseScreen.this, d2, CAL.get(Calendar.YEAR), CAL.get(Calendar.MONTH), CAL.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            Calendar expenditureDate = Calendar.getInstance();
            expenditureDate.set(year, month, dayOfMonth, 0, 0, 0);

            Button btn_date = (Button) findViewById(R.id.expense_screen_date);
            btn_date.setText(DateUtils.formatDateTime(getBaseContext(), expenditureDate.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));

            EXPENSE.setDate(expenditureDate);
            Log.d(TAG, "updated date to " + EXPENSE.getDate().get(Calendar.DAY_OF_MONTH) + "-" + EXPENSE.getDate().get(Calendar.MONTH) + "-" + EXPENSE.getDate().get(Calendar.YEAR));
        }
    };

    DatePickerDialog.OnDateSetListener d2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            recurringEndDate.set(year, month, dayOfMonth, 0, 0, 0);

            Button recurringEnd = (Button) findViewById(R.id.expense_screen_recurring_end);
            recurringEnd.setText(DateUtils.formatDateTime(getBaseContext(), recurringEndDate.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
        }
    };


    public void expensePopUp(View view) {

        Bundle bundle = new Bundle();
        Button btn = (Button) findViewById(view.getId());

        switch (btn.getId()) {

            case R.id.expense_screen_amount:

                break;

            case R.id.expense_screen_category:

                Intent bla = new Intent(ExpenseScreen.this, Categories.class);
                ExpenseScreen.this.startActivityForResult(bla, 1);
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

                updateDate("expenseDate");
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
        } else if (btn.getId() != R.id.expense_screen_date && btn.getId() != R.id.expense_screen_category) {

            ExpenseInputDialogFragment expenseDialog = new ExpenseInputDialogFragment();
            expenseDialog.setArguments(bundle);
            expenseDialog.show(getFragmentManager(), "expenseDialog");
        }
    }


    /**
     * Methode um die Daten die von dieser Activity aufgerufende Activities zurückgegeben werden weiter zu verarbeiten.
     *
     * @param requestCode der Anfrage Code der beim aufrufen der 2. Activity erstellt wird, siehe case: R.id.expense_screen_category
     * @param resultCode  der Code der von der Aufgerufenden Kategorie zurückgegeben wird, um den status der daten zu signalisieren
     * @param data        die Daten die von der aufgerufenden KAtegorie zurückgegebn werden
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == Activity.RESULT_OK) {

                Category result = data.getParcelableExtra("categoryObj");
                EXPENSE.setCategory(result);

                Button category = (Button) findViewById(R.id.expense_screen_category);
                category.setText(result.getCategoryName());
            }
        }
    }
}
