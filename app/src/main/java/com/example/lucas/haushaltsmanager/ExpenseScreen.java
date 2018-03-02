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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AccountPickerDialogFragment.OnAccountSelected {

    private creationModes CREATION_MODE;

    private enum creationModes {
        CREATE_EXPENSE_MODE,
        CREATE_CHILD_MODE,
        UPDATE_EXPENSE_MODE,
        UPDATE_CHILD_MODE
    }

    private String TAG = ExpenseScreen.class.getSimpleName();

    public ExpenseObject mExpense;
    private Calendar mCalendar = Calendar.getInstance();
    private Calendar recurringEndDate = Calendar.getInstance();
    private boolean mTemplate = false, mRecurring = false;
    public int frequency = 0;
    Button setDateBtn, accountBtn, saveBtn, categoryTxt, titleTxt, tagTxt, noticeTxt;
    TextView amountTxt, currencyTxt;
    RadioGroup expenseType;
    private ExpenseObject mParentBooking;


    private ExpensesDataSource mDatabase;

    //TODO klasse sollte auch den BasicDialog erweitern
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_screen);

        SharedPreferences preferences = getSharedPreferences("UserSettings", 0);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        final Bundle bundle = getIntent().getExtras();

        saveBtn = (Button) findViewById(R.id.expense_screen_create_booking);
        saveBtn.setOnClickListener(createBookingClickListener);

        if (bundle != null && bundle.get("parentIndex") != null) {

            saveBtn.setText(getString(R.string.add_child_to_booking));

            CREATION_MODE = creationModes.CREATE_CHILD_MODE;
            mParentBooking = mDatabase.getBookingById(bundle.getLong("parentIndex"));

            String title = getResources().getString(R.string.expense_screen_title);
            Account account = mDatabase.getAccountById(preferences.getLong("activeAccount", 0));

            mExpense = new ExpenseObject(title, 0, false, Category.createDummyCategory(this), null, account);
            mExpense.setDateTime(mCalendar);
            mExpense.setNotice("");
            mExpense.setTag("");
        } else if (bundle != null && bundle.get("childExpense") != null) {

            saveBtn.setText(getString(R.string.update_booking));

            CREATION_MODE = creationModes.UPDATE_CHILD_MODE;
            mExpense = mDatabase.getChildBookingById(bundle.getLong("childExpense"));
        } else if (bundle != null && bundle.get("parentExpense") != null) {

            saveBtn.setText(getString(R.string.update_booking));

            CREATION_MODE = creationModes.UPDATE_EXPENSE_MODE;
            mExpense = mDatabase.getBookingById(bundle.getLong("parentExpense"));
        } else {

            saveBtn.setText(getString(R.string.create_booking));

            CREATION_MODE = creationModes.CREATE_EXPENSE_MODE;
            String title = getResources().getString(R.string.expense_screen_title);
            Account account = mDatabase.getAccountById(preferences.getLong("activeAccount", 0));

            mExpense = new ExpenseObject(title, 0, false, Category.createDummyCategory(this), null, account);
            mExpense.setDateTime(mCalendar);
            mExpense.setNotice("");
            mExpense.setTag("");
        }

        //TODO implement the correct Toolbar functionality (back arrow, overflow menu which holds the load mTemplate button)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the displayed date to the current one
        setDateBtn = (Button) findViewById(R.id.expense_screen_date);
        setDateBtn.setText(mExpense.getDisplayableDateTime(ExpenseScreen.this));


        // set the account to the current main account
        accountBtn = (Button) findViewById(R.id.expense_screen_account);
        accountBtn.setText(mExpense.getAccount().getAccountName());

        // set the mExpense type
        expenseType = (RadioGroup) findViewById(R.id.expense_screen_expense_type);
        if (mExpense.getExpenditure())
            expenseType.check(R.id.expense_screen_radio_expense);
        else
            expenseType.check(R.id.expense_screen_radio_income);

        expenseType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.expense_screen_radio_expense) {

                    mExpense.setExpenditure(true);
                    Log.d(TAG, "set expense type to " + mExpense.getExpenditure());
                } else {

                    mExpense.setExpenditure(false);
                    Log.d(TAG, "set expense type to " + mExpense.getExpenditure());
                }
            }
        });


        amountTxt = (TextView) findViewById(R.id.expense_screen_amount);
        amountTxt.setText(String.format("%s", mExpense.getUnsignedPrice()));
        amountTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putDouble("original_title", mExpense.getUnsignedPrice());
                bundle.putInt("button_id", v.getId());

                PriceInputDialogFragment priceDialog = new PriceInputDialogFragment();
                priceDialog.setArguments(bundle);
                priceDialog.show(getFragmentManager(), "expense_screen_price");
            }
        });

        //set account currency symbol
        currencyTxt = (TextView) findViewById(R.id.expense_screen_amount_currency);
        currencyTxt.setText(mExpense.getAccount().getCurrency().getCurrencySymbol());

        //set display CATEGORY
        categoryTxt = (Button) findViewById(R.id.expense_screen_category);
        categoryTxt.setText(mExpense.getCategory().getCategoryName());

        //set display expense title
        titleTxt = (Button) findViewById(R.id.expense_screen_title);
        titleTxt.setText(mExpense.getTitle());

        //TODO change display tag behaviour from just taking the first tag to displaying all tags
        //set display tag
        tagTxt = (Button) findViewById(R.id.expense_screen_tag);
//        tagTxt.setText(mExpense.getTags().get(0));TODO enable

        //set display notice
        noticeTxt = (Button) findViewById(R.id.expense_screen_notice);
        noticeTxt.setText(mExpense.getNotice());

        final CheckBox templateChk = (CheckBox) findViewById(R.id.expense_screen_template);
        templateChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTemplate = true;
                Toast.makeText(ExpenseScreen.this, "Du möchtest die Ausgabe als Vorlage speichern", Toast.LENGTH_SHORT).show();
            }
        });

        final CheckBox recurringChk = (CheckBox) findViewById(R.id.expense_screen_recurring);
        recurringChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO ist kaputt

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
                    mRecurring = true;
                } else {

                    imgFrequency.setVisibility(ImageView.GONE);
                    recurringFrequency.setVisibility(Button.GONE);

                    imgEnd.setVisibility(ImageView.GONE);
                    recurringEnd.setVisibility(Button.GONE);
                    mRecurring = false;
                }
            }
        });

        //ab hier wird der CurrencySelector erstellt

        Spinner currencySelector = (Spinner) findViewById(R.id.expense_screen_select_currency);

        currencySelector.setOnItemSelectedListener(this);

        ArrayList<Currency> currencies = mDatabase.getAllCurrencies();

        List<String> currencyShortNames = new ArrayList<>();
        for (Currency currency : currencies) {

            currencyShortNames.add(currency.getCurrencyShortName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyShortNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        currencySelector.setAdapter(adapter);

        currencySelector.setSelection(((int) preferences.getLong("mainCurrencyIndex", 32)) - 1);
    }

    /**
     * OnClickListener um eine Buchung zu erstellen oder um sie zu updaten.
     */
    private View.OnClickListener createBookingClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            //todo buchungen in der Zukunft sollen als geplante buchung eingefügt werden
            if (!mExpense.isSet())
                return;

            switch (CREATION_MODE) {

                case UPDATE_EXPENSE_MODE:

                    mDatabase.updateBooking(mExpense);
                    Toast.makeText(ExpenseScreen.this, "Updated Booking " + mExpense.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_CHILD_MODE:

                    mDatabase.updateChildBooking(mExpense);
                    Toast.makeText(ExpenseScreen.this, "Updated Booking " + mExpense.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
                case CREATE_CHILD_MODE:

                    mDatabase.addChildToBooking(mExpense, mParentBooking.getIndex());
                    mDatabase.insertConvertExpense(mExpense);
                    Toast.makeText(ExpenseScreen.this, "Added Booking \"" + mExpense.getTitle() + "\" to parent Booking " + mParentBooking.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
                case CREATE_EXPENSE_MODE:

                    mDatabase.createBooking(mExpense);
                    mDatabase.insertConvertExpense(mExpense);
                    Toast.makeText(ExpenseScreen.this, "Created Booking \"" + mExpense.getTitle() + "\"", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new UnsupportedOperationException("ExpenseScreen unterstützt keine anderen Methoden als createExpense, createChildToExpense und updateExpense");
            }

            if (mRecurring) {//todo noch einmal überarbeiten

                // frequency is saved as duration in hours, mEndDate is saved as Calendar object
                long index = mDatabase.createRecurringBooking(mExpense.getIndex(), mCalendar, frequency, recurringEndDate);
                Log.d(TAG, "created mRecurring booking event at index: " + index);
            }

            if (mTemplate) {//todo noch einmal überarbeiten

                long index = mDatabase.createTemplateBooking(mExpense.getIndex());
                Log.d(TAG, "created mTemplate for bookings at index: " + index);
            }

            Intent intent = new Intent(ExpenseScreen.this, MainActivityTab.class);
            ExpenseScreen.this.startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.close();
    }

    //TODO das aussuchen eine Datums sollte genauso wie der ColorPicker funktionieren
    private void updateDate(String caller) {

        if (caller.equals("expenseDate")) {

            new DatePickerDialog(ExpenseScreen.this, d, mExpense.getDateTime().get(Calendar.YEAR), mExpense.getDateTime().get(Calendar.MONTH), mExpense.getDateTime().get(Calendar.DAY_OF_MONTH)).show();
        } else {

            new DatePickerDialog(ExpenseScreen.this, d2, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            Calendar expenditureDate = Calendar.getInstance();
            expenditureDate.set(year, month, dayOfMonth, 0, 0, 0);

            mExpense.setDateTime(expenditureDate);
            Log.d(TAG, "updated date to " + mExpense.getDisplayableDateTime(ExpenseScreen.this));

            Button btn_date = (Button) findViewById(R.id.expense_screen_date);
            btn_date.setText(mExpense.getDisplayableDateTime(ExpenseScreen.this));
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

                Intent chooseCategoryIntent = new Intent(ExpenseScreen.this, Categories.class);
                ExpenseScreen.this.startActivityForResult(chooseCategoryIntent, 1);
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

                bundle.putString("title", getResources().getString(R.string.expense_screen_dsp_account));
                bundle.putParcelable("active_account", mExpense.getAccount());
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

                Category category = data.getParcelableExtra("categoryObj");
                mExpense.setCategory(category);

                categoryTxt.setText(category.getCategoryName());

                if (category.getDefaultExpenseType())
                    expenseType.check(R.id.expense_screen_radio_expense);
                else
                    expenseType.check(R.id.expense_screen_radio_income);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (!mDatabase.isOpen())
            mDatabase.open();

        String curName = parent.getItemAtPosition(position).toString();

        Currency currency = mDatabase.getCurrency(curName);

        TextView expenseCurrency = (TextView) findViewById(R.id.expense_screen_amount_currency);
        expenseCurrency.setText(String.format("%s", currency.getCurrencySymbol()));

        mExpense.setExpenseCurrency(currency);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    /**
     * Methode die den Callback des AccountPickerDialogFragments implementiert
     *
     * @param account Konto das ausgewählt wurde
     * @param tag     Tag mit dem das DialogFragment aufgerufen wurde
     */
    @Override
    public void onAccountSelected(Account account, String tag) {

        if (tag.equals("account_picker")) {

            accountBtn.setText(account.getAccountName());
            currencyTxt.setText(account.getCurrency().getCurrencySymbol());
            mExpense.setAccount(account);

            Log.d(TAG, "set active account to: " + account.getAccountName());
        }
    }
}
