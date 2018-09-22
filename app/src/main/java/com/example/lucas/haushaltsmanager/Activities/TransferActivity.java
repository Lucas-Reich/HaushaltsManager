package com.example.lucas.haushaltsmanager.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.ChildCategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ErrorAlertDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.SingleChoiceDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.UserSettingsPreferences;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransferActivity extends AppCompatActivity {
    private static final String TAG = TransferActivity.class.getSimpleName();

    private Button mDateBtn, mFromAccountBtn, mToAccountBtn, mCreateTransferBtn, mAmountBtn;
    private Account mFromAccount, mToAccount;
    private Calendar mCalendar;
    //Ausgabe
    private ExpenseObject mFromExpense;
    //Einnahme
    private ExpenseObject mToExpense;
    private AccountRepository mAccountRepo;
    private ChildExpenseRepository mChildExpenseRepo;
    private ChildCategoryRepository mChildCategoryRepo;
    private ExpenseRepository mBookingRepo;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//fixme megadumm wenn ich unter dieser version bin dann kann ich die activity nicht aufrufen da die onCreate methode nicht aufgerufen wird
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);

        mAccountRepo = new AccountRepository(this);
        mChildExpenseRepo = new ChildExpenseRepository(this);
        mChildCategoryRepo = new ChildCategoryRepository(this);
        mBookingRepo = new ExpenseRepository(this);

        mCalendar = Calendar.getInstance();

        try {
            mFromExpense = ExpenseObject.createDummyExpense();
            mFromExpense.setPrice(0);
            mFromExpense.setExpenditure(true);
            mFromExpense.setCategory(getTransferCategory());
            mFromExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.TRANSFER_EXPENSE);

            mToExpense = ExpenseObject.createDummyExpense();
            mToExpense.setPrice(0);
            mToExpense.setExpenditure(false);
            mToExpense.setCategory(getTransferCategory());
            mToExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.TRANSFER_EXPENSE);
        } catch (ChildCategoryNotFoundException e) {

            //todo vor dem relase entfernen
            Toast.makeText(this, "Überweisungskategorie wurde nicht gefunden", Toast.LENGTH_SHORT).show();
            finish();
        }

        mDateBtn = findViewById(R.id.transfer_date_btn);
        mFromAccountBtn = findViewById(R.id.transfer_from_account_btn);
        mToAccountBtn = findViewById(R.id.transfer_to_account_btn);
        mCreateTransferBtn = findViewById(R.id.transfer_create_btn);
        mAmountBtn = findViewById(R.id.transfer_amount_btn);

        initializeToolbar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("from_account"))
            setFromAccount((Account) bundle.getParcelable("from_account"));

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAmountBtn.setHint(R.string.placeholder_amount);
        mAmountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString(PriceInputDialog.TITLE, getResources().getString(R.string.input_price));

                PriceInputDialog expenseInput = new PriceInputDialog();
                expenseInput.setArguments(bundle);
                expenseInput.setOnPriceSelectedListener(new PriceInputDialog.OnPriceSelected() {
                    @Override
                    public void onPriceSelected(double price) {
                        SharedPreferences preferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

                        mFromExpense.setPrice(price);
                        mFromExpense.setExpenditure(true);
                        mAmountBtn.setText(String.format(getResources().getConfiguration().locale, "%.2f %s", mFromExpense.getUnsignedPrice(), preferences.getString("mainCurrencySymbol", "€")));

                        setToExpense(price);
                    }
                });
                expenseInput.show(getFragmentManager(), "transfers_amount_input");
            }
        });

        mFromAccountBtn.setHint(R.string.input_account);
        mFromAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SingleChoiceDialog<Account> accountPicker = new SingleChoiceDialog<>();
                accountPicker.createBuilder(TransferActivity.this);
                accountPicker.setTitle(getString(R.string.input_account));

                List<Account> accounts = mAccountRepo.getAll();
                accounts.remove(mToAccount);
                accountPicker.setContent(accounts, -1);
                accountPicker.setOnEntrySelectedListener(new SingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onPositiveClick(Object fromAccount) {

                        setFromAccount((Account) fromAccount);
                        setToExpense(mFromExpense.getUnsignedPrice());
                    }

                    @Override
                    public void onNeutralClick() {

                        //do nothing
                    }
                });
                accountPicker.show(getFragmentManager(), "transfers_from_account");
            }
        });

        mToAccountBtn.setHint(R.string.input_account);
        mToAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SingleChoiceDialog<Account> accountPicker = new SingleChoiceDialog<>();
                accountPicker.createBuilder(TransferActivity.this);
                accountPicker.setTitle(getString(R.string.choose_account));

                List<Account> accounts = mAccountRepo.getAll();
                accounts.remove(mFromAccount);
                accountPicker.setContent(accounts, -1);
                accountPicker.setOnEntrySelectedListener(new SingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onPositiveClick(Object toAccount) {

                        setToAccount((Account) toAccount);
                        setToExpense(mFromExpense.getUnsignedPrice());
                    }

                    @Override
                    public void onNeutralClick() {

                        //do nothing
                    }
                });
                accountPicker.show(getFragmentManager(), "transfers_to_account");
            }
        });

        mDateBtn.setText(transformCalendarToReadableDate(mCalendar));
        mDateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putLong(DatePickerDialog.CURRENT_DAY, mCalendar.getTimeInMillis());

                DatePickerDialog datePicker = new DatePickerDialog();
                datePicker.setArguments(bundle);
                datePicker.setOnDateSelectedListener(new DatePickerDialog.OnDateSelected() {
                    @Override
                    public void onDateSelected(Calendar date) {

                        mCalendar = date;
                        mFromExpense.setDateTime(date);
                        mToExpense.setDateTime(date);
                        mDateBtn.setText(transformCalendarToReadableDate(date));
                    }
                });
                datePicker.show(getFragmentManager(), "transfers_date");
            }
        });

        mCreateTransferBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //checke ob alles gesetzt ist
                if (mFromExpense.isSet() && mToExpense.isSet()) {

                    ArrayList<ExpenseObject> bookings = new ArrayList<>();
                    bookings.add(mFromExpense);
                    bookings.add(mToExpense);


                    ExpenseObject parent = mChildExpenseRepo.combineExpenses(bookings);
                    parent.setTitle(String.format("%s\n%s -> %s", getString(R.string.transfer), mFromAccount.getTitle(), mToAccount.getTitle()));
                    try {
                        mBookingRepo.update(parent);
                    } catch (ExpenseNotFoundException e) {

                        Toast.makeText(TransferActivity.this, "Titel konnte nicht geupdated werden", Toast.LENGTH_SHORT).show();
                        //todo fehlerbehandlung
                        //todo übersetzung
                    }

                    Intent intent = new Intent(TransferActivity.this, ParentActivity.class);
                    TransferActivity.this.startActivity(intent);
                } else {

                    Bundle bundle = new Bundle();
                    bundle.putString(ErrorAlertDialog.TITLE, getString(R.string.error));
                    bundle.putString(ErrorAlertDialog.CONTENT, getString(R.string.error_missing_content));

                    ErrorAlertDialog errorAlert = new ErrorAlertDialog();
                    errorAlert.setArguments(bundle);
                    errorAlert.show(getFragmentManager(), "transfer_activity_error");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Methode um eine Toolbar anzuzeigen die den Titel und einen Zurückbutton enthält.
     */
    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        //schatten der toolbar
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setElevation(10.f);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Methode um die Default TransferKategorie aus der Datenbank zu holen.
     *
     * @return Default TransferKategorie
     */
    private Category getTransferCategory() throws ChildCategoryNotFoundException {
        return mChildCategoryRepo.get(1);
    }

    /**
     * Methode um das momentan aktive Konto aus den ScharedPreferences auszulesen und das dementsprechende Konto aus der Datenbank zu holen
     *
     * @return aktives Konto
     */
    private Account getActiveAccount() {

        UserSettingsPreferences preferences = new UserSettingsPreferences(this);
        return preferences.getActiveAccount();
    }

    /**
     * Methode um ein Calendar Object in einen String zu transformieren, der basierend auf der User Locale formatiert wird.
     *
     * @param calendar Datum das transformiert werden soll
     * @return Formatierter und transformierter Datumsstring
     */
    private String transformCalendarToReadableDate(Calendar calendar) {

        return DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(calendar.getTimeInMillis()));
    }

    /**
     * Methode um das Ausgehende Konto zu setzen
     *
     * @param newAccount Das neue ausgehende Konto
     */
    private void setFromAccount(Account newAccount) {

        mFromAccount = newAccount;
        mToExpense.setTitle(String.format("%s %s", getString(R.string.transfer_from), mFromAccount.getTitle()));
        mFromExpense.setAccountId(mFromAccount.getIndex());
        mFromAccountBtn.setText(mFromAccount.getTitle());

        Log.d(TAG, "selected " + mFromAccount.getTitle() + " as from account");
    }

    /**
     * Methode um das Eingehende Konto zu setzen
     *
     * @param newAccount Das neue eingehende Konto
     */
    private void setToAccount(Account newAccount) {

        mToAccount = newAccount;
        mFromExpense.setTitle(String.format("%s %s", getString(R.string.transfer_to), mToAccount.getTitle()));
        mToExpense.setAccountId(mToAccount.getIndex());
        mToAccountBtn.setText(mToAccount.getTitle());

        Log.d(TAG, "selected " + mToAccount.getTitle() + " as to account");
    }

    /**
     * Methode um den Betrag der ToExpense zu setzen, da dieser eventuell umgerechnet werden muss.
     *
     * @param newPrice Den Preis, welcher umgerechnet werden muss
     */
    private void setToExpense(double newPrice) {

        mToExpense.setPrice(newPrice);
        mToExpense.setExpenditure(false);
    }
}
