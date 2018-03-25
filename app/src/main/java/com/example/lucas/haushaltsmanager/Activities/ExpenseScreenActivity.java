package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.MainTab.TabParentActivity;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.AccountPickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.FrequencyAlertDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseScreenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AccountPickerDialog.OnAccountSelected, BasicTextInputDialog.BasicDialogCommunicator, PriceInputDialog.OnPriceSelected, com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog.OnDateSelected, FrequencyAlertDialog.OnFrequencySet {

    private String TAG = ExpenseScreenActivity.class.getSimpleName();

    private creationModes CREATION_MODE;

    private enum creationModes {
        CREATE_EXPENSE_MODE,
        CREATE_CHILD_MODE,
        UPDATE_EXPENSE_MODE,
        UPDATE_CHILD_MODE
    }

    private ExpenseObject mExpense;
    private Calendar mCalendar = Calendar.getInstance();
    private Calendar mRecurringEndDate = Calendar.getInstance();
    private boolean mTemplate = false, mRecurring = false;
    public int frequency = 0;
    private Button mDateBtn, mAccountBtn, mSaveBtn, mCategoryBtn, mTitleBtn, mTagBtn, mNoticeBtn, mRecurringEndBtn, mRecurringFrequency;
    private CheckBox mTemplateChk, mRecurringChk;
    private TextView mAmountTxt, mCurrencyTxt;
    private RadioGroup mExpenseTypeRadio;
    private ExpenseObject mParentBooking;

    private ExpensesDataSource mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_screen);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        //TODO implement the correct Toolbar functionality (back arrow, overflow menu which holds the load mTemplate button)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAmountTxt = (TextView) findViewById(R.id.expense_screen_amount);
        mCurrencyTxt = (TextView) findViewById(R.id.expense_screen_amount_currency);
        mCategoryBtn = (Button) findViewById(R.id.expense_screen_category);
        mExpenseTypeRadio = (RadioGroup) findViewById(R.id.expense_screen_expense_type);
        mTitleBtn = (Button) findViewById(R.id.expense_screen_title);
        mTagBtn = (Button) findViewById(R.id.expense_screen_tag);
        mDateBtn = (Button) findViewById(R.id.expense_screen_date);
        mNoticeBtn = (Button) findViewById(R.id.expense_screen_notice);
        mAccountBtn = (Button) findViewById(R.id.expense_screen_account);
        mSaveBtn = (Button) findViewById(R.id.expense_screen_create_booking);

        mTemplateChk = (CheckBox) findViewById(R.id.expense_screen_template);
        mRecurringChk = (CheckBox) findViewById(R.id.expense_screen_recurring);

        resolveIntent(getIntent().getExtras());

        //ab hier wird der CurrencySelector erstellt

        Spinner currencySelector = (Spinner) findViewById(R.id.expense_screen_select_currency);

        currencySelector.setOnItemSelectedListener(this);

        ArrayList<Currency> currencies = mDatabase.getAllCurrencies();

        List<String> currencyShortNames = new ArrayList<>();
        for (Currency currency : currencies) {

            currencyShortNames.add(currency.getShortName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyShortNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        currencySelector.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        currencySelector.setSelection(((int) preferences.getLong("mainCurrencyIndex", 32)) - 1);
    }

    /**
     * Methode um herauszufinden mit welcher Intention (createChild, createBooking, updateChild, updateBooking)
     * der ExpenseScreen aufgerufen wurde
     */
    public void resolveIntent(Bundle bundle) {
        if (bundle == null)
            return;

        SharedPreferences preferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        if (bundle.getString("mode").equals("updateChild")) {

            mSaveBtn.setText(getString(R.string.update));

            CREATION_MODE = creationModes.UPDATE_CHILD_MODE;
            mExpense = getIntent().getParcelableExtra("updateChildExpense");
            return;
        }

        if (bundle.getString("mode").equals("updateParent")) {

            mSaveBtn.setText(getString(R.string.update));

            CREATION_MODE = creationModes.UPDATE_EXPENSE_MODE;
            mExpense = getIntent().getParcelableExtra("updateParentExpense");
            return;
        }

        if (bundle.getString("mode").equals("addChild")) {

            mSaveBtn.setText(getString(R.string.add_child_to_booking));

            CREATION_MODE = creationModes.CREATE_CHILD_MODE;
            mParentBooking = getIntent().getParcelableExtra("parentBooking");
        }

        if (bundle.getString("mode").equals("createBooking")){

            mSaveBtn.setText(getString(R.string.create_booking));
            CREATION_MODE = creationModes.CREATE_EXPENSE_MODE;
        }

        Account account = mDatabase.getAccountById(preferences.getLong("activeAccount", 0));
        mExpense = ExpenseObject.createDummyExpense(this);
        mExpense.setAccount(account);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mExpense.isExpenditure())
            mExpenseTypeRadio.check(R.id.expense_screen_radio_expense);
        else
            mExpenseTypeRadio.check(R.id.expense_screen_radio_income);

        mExpenseTypeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.expense_screen_radio_expense) {

                    mExpense.setExpenditure(true);
                    Log.d(TAG, "set expense type to " + mExpense.isExpenditure());
                } else {

                    mExpense.setExpenditure(false);
                    Log.d(TAG, "set expense type to " + mExpense.isExpenditure());
                }
            }
        });

        mCurrencyTxt.setText(mExpense.getAccount().getCurrency().getSymbol());

        mAccountBtn.setText(mExpense.getAccount().getName());

        mCategoryBtn.setHint(R.string.choose_category);
        mCategoryBtn.setText(mExpense.getCategory().getName());

        mDateBtn.setText(mExpense.getDisplayableDateTime());

        mTitleBtn.setHint(R.string.input_title);
        mTitleBtn.setText(mExpense.getName());

        mTagBtn.setHint(R.string.input_tag);
//        mTagBtn.setText(mExpense.getTags().get(0));TODO enable

        mNoticeBtn.setHint(R.string.input_notice);
        mNoticeBtn.setText(mExpense.getNotice());

        mAmountTxt.setText(String.format("%s", mExpense.getUnsignedPrice()));
        mAmountTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.input_price));

                PriceInputDialog priceDialog = new PriceInputDialog();
                priceDialog.setArguments(bundle);
                priceDialog.show(getFragmentManager(), "expense_screen_price");
            }
        });

        mTemplateChk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mTemplate = true;
                Toast.makeText(ExpenseScreenActivity.this, "Du möchtest die Ausgabe als Vorlage speichern", Toast.LENGTH_SHORT).show();
            }
        });

        mRecurringChk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //TODO ist kaputt

                ImageView imgFrequency = (ImageView) findViewById(R.id.img_frequency);
                mRecurringFrequency = (Button) findViewById(R.id.expense_screen_recurring_frequency);
                mRecurringFrequency.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putString("title", getString(R.string.input_frequency));

                        FrequencyAlertDialog frequencyDialog = new FrequencyAlertDialog();
                        frequencyDialog.setArguments(bundle);
                        frequencyDialog.show(getFragmentManager(), "expense_screen_frequency");
                    }
                });

                ImageView imgEnd = (ImageView) findViewById(R.id.img_end);
                mRecurringEndBtn = (Button) findViewById(R.id.expense_screen_recurring_end);
                mRecurringEndBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Bundle dateBundle = new Bundle();
                        dateBundle.putString("title", "");

                        DatePickerDialog datePicker = new DatePickerDialog();
                        datePicker.setArguments(dateBundle);
                        datePicker.show(getFragmentManager(), "expense_screen_recurring");
                    }
                });

                //setVisibility of icon and input field to visible
                if (mTemplate) {

                    imgFrequency.setVisibility(ImageView.VISIBLE);
                    mRecurringFrequency.setVisibility(Button.VISIBLE);

                    imgEnd.setVisibility(ImageView.VISIBLE);
                    mRecurringEndBtn.setVisibility(Button.VISIBLE);
                    mRecurring = true;
                } else {

                    imgFrequency.setVisibility(ImageView.GONE);
                    mRecurringFrequency.setVisibility(Button.GONE);

                    imgEnd.setVisibility(ImageView.GONE);
                    mRecurringEndBtn.setVisibility(Button.GONE);
                    mRecurring = false;
                }
            }
        });

        mSaveBtn.setOnClickListener(createBookingClickListener);
    }

    /**
     * OnClickListener um eine Buchung zu erstellen oder um sie zu updaten.
     */
    private View.OnClickListener createBookingClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            //todo buchungen in der Zukunft sollen als geplante buchung eingefügt werden
            //wenn eine Buchung in der Zukunft erstellt wurde soll ein alert dialog den user darauf hinweisen,
            //dass die Buchung als zukünftige Buchung erstellt wurde
            //AlertDialog text: "Du hast eine Buchung in der Zukunft erstellt. Diese wird dann zum entsprechenden Tag in deine Historie eingefügt"
            if (!mExpense.isSet())
                return;

            switch (CREATION_MODE) {

                case UPDATE_EXPENSE_MODE:

                    mDatabase.updateBooking(mExpense);
                    Toast.makeText(ExpenseScreenActivity.this, "Updated Booking " + mExpense.getName(), Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_CHILD_MODE:

                    mDatabase.updateChildBooking(mExpense);
                    Toast.makeText(ExpenseScreenActivity.this, "Updated Booking " + mExpense.getName(), Toast.LENGTH_SHORT).show();
                    break;
                case CREATE_CHILD_MODE:

                    mDatabase.addChildToBooking(mExpense, mParentBooking.getIndex());
                    mDatabase.insertConvertExpense(mExpense);
                    Toast.makeText(ExpenseScreenActivity.this, "Added Booking \"" + mExpense.getName() + "\" to parent Booking " + mParentBooking.getName(), Toast.LENGTH_SHORT).show();
                    break;
                case CREATE_EXPENSE_MODE:

                    mDatabase.createBooking(mExpense);
                    mDatabase.insertConvertExpense(mExpense);
                    Toast.makeText(ExpenseScreenActivity.this, "Created Booking \"" + mExpense.getName() + "\"", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new UnsupportedOperationException("ExpenseScreen unterstützt keine anderen Methoden als createExpense, createChildToExpense, updateExpense und updateChildExpense");
            }

            if (mRecurring) {//todo noch einmal überarbeiten

                // frequency is saved as duration in hours, mEndDate is saved as Calendar object
                long index = mDatabase.createRecurringBooking(mExpense.getIndex(), mCalendar.getTimeInMillis(), frequency, mRecurringEndDate.getTimeInMillis());
                Log.d(TAG, "created mRecurring booking event at index: " + index);
            }

            if (mTemplate) {//todo noch einmal überarbeiten

                long index = mDatabase.createTemplateBooking(mExpense.getIndex());
                Log.d(TAG, "created mTemplate for bookings at index: " + index);
            }

            Intent intent = new Intent(ExpenseScreenActivity.this, TabParentActivity.class);
            ExpenseScreenActivity.this.startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        mDatabase.close();

        super.onDestroy();
    }


    public void expensePopUp(View view) {

        Bundle bundle = new Bundle();
        Button btn = (Button) findViewById(view.getId());
        BasicTextInputDialog basicDialog = new BasicTextInputDialog();

        switch (btn.getId()) {

            case R.id.expense_screen_category:

                Intent chooseCategoryIntent = new Intent(ExpenseScreenActivity.this, ShowCategoriesActivity.class);
                ExpenseScreenActivity.this.startActivityForResult(chooseCategoryIntent, 1);
                break;

            case R.id.expense_screen_title:

                bundle.putString("title", getResources().getString(R.string.input_title));

                basicDialog.setArguments(bundle);
                basicDialog.show(getFragmentManager(), "expense_screen_title");
                break;

            case R.id.expense_screen_tag:

                bundle.putString("title", getResources().getString(R.string.input_tag));

                basicDialog.setArguments(bundle);
                basicDialog.show(getFragmentManager(), "expense_screen_tag");
                break;

            case R.id.expense_screen_date:

                bundle.putString("title", "");

                DatePickerDialog datePicker = new DatePickerDialog();
                datePicker.setArguments(bundle);
                datePicker.show(getFragmentManager(), "expense_screen_date");
                break;

            case R.id.expense_screen_notice:

                bundle.putString("title", getResources().getString(R.string.input_notice));

                basicDialog.setArguments(bundle);
                basicDialog.show(getFragmentManager(), "expense_screen_notice");
                break;

            case R.id.expense_screen_account:

                bundle.putString("title", getResources().getString(R.string.input_account));
                bundle.putParcelable("active_account", mExpense.getAccount());

                AccountPickerDialog accountPicker = new AccountPickerDialog();
                accountPicker.setArguments(bundle);
                accountPicker.show(getFragmentManager(), "expense_screen_account");
                break;
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

                mCategoryBtn.setText(category.getName());

                if (category.getDefaultExpenseType())
                    mExpenseTypeRadio.check(R.id.expense_screen_radio_expense);
                else
                    mExpenseTypeRadio.check(R.id.expense_screen_radio_income);
            }
        }
    }

    /**
     * Callback Methode des Currency selectors
     *
     * @param parent   parent
     * @param view     view
     * @param position Die vom Nutzer ausgewählte position
     * @param id       id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String curName = parent.getItemAtPosition(position).toString();
        Currency currency = mDatabase.getCurrency(curName);

        mExpense.setExpenseCurrency(currency);
        mCurrencyTxt.setText(String.format("%s", mExpense.getExpenseCurrency().getSymbol()));
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

        if (tag.equals("expense_screen_account")) {

            mExpense.setAccount(account);
            mAccountBtn.setText(mExpense.getAccount().getName());
            mCurrencyTxt.setText(mExpense.getAccount().getCurrency().getSymbol());

            Log.d(TAG, "set expense mAccount to: " + mExpense.getAccount().getName());
        }
    }

    /**
     * Methode die den Callback des BasicTextInputDialogs implementiert
     *
     * @param textInput Daten die vom user eigegeben wurden
     * @param tag       Dialog tag
     */
    @Override
    public void onTextInput(String textInput, String tag) {

        if (!textInput.isEmpty()) {

            switch (tag) {

                case "expense_screen_title":

                    mExpense.setName(textInput);
                    mTitleBtn.setText(mExpense.getName());
                    Log.d(TAG, "set expense title to " + mExpense.getName());
                    break;

                case "expense_screen_tag":

                    //todo implement tag functionality
                    break;

                case "expense_screen_notice":

                    mExpense.setNotice(textInput);
                    mNoticeBtn.setText(mExpense.getNotice());
                    Log.d(TAG, "set expense notice to " + mExpense.getNotice());
                    break;
            }
        }
    }

    /**
     * Methode die den Callback des PriceInputDialogs implementiert
     *
     * @param price Preis den der User einegeben hat
     * @param tag   Dialog tag
     */
    @Override
    public void onPriceSelected(double price, String tag) {

        if (tag.equals("expense_screen_price")) {

            mExpense.setPrice(price);
            mAmountTxt.setText(String.format("%s", mExpense.getUnsignedPrice()));

            Log.d(TAG, "set expense amount to " + mExpense.getUnsignedPrice());
        }
    }

    /**
     * Methode die den Callback des DatePickerDialogs implementiert
     *
     * @param date Dateum das der User ausgewählt hat
     * @param tag  Dialog tag
     */
    @Override
    public void onDateSelected(Calendar date, String tag) {

        if (tag.equals("expense_screen_recurring")) {

            mRecurringEndDate = date;
            mRecurringEndBtn.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(mRecurringEndDate.getTimeInMillis())));
            Log.d(TAG, "updated recurring end date to " + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(mRecurringEndDate.getTimeInMillis())));
        } else if (tag.equals("expense_screen_date")) {

            mExpense.setDateTime(date);
            mDateBtn.setText(mExpense.getDisplayableDateTime());
            Log.d(TAG, "updated expense date to " + mExpense.getDisplayableDateTime());
        }
    }

    @Override
    public void onFrequencySet(int frequencyInHours, String tag) {

        if (tag.equals("expense_screen_frequency")) {

            mRecurringFrequency.setText(String.format("%s Tage", frequencyInHours));
        }
    }
}
