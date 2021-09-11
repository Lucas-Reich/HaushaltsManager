package com.example.lucas.haushaltsmanager.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryDAO;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ErrorAlertDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.SingleChoiceDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.Entities.Booking.ParentBooking;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TransferActivity extends AppCompatActivity {
    private static final String TAG = TransferActivity.class.getSimpleName();

    private Button mDateBtn, mFromAccountBtn, mToAccountBtn, mCreateTransferBtn, mAmountBtn;
    private Account mFromAccount, mToAccount;
    private Calendar mCalendar;
    //Ausgabe
    private Booking mFromExpense;
    //Einnahme
    private Booking mToExpense;
    private AccountDAO accountRepo;
    private CategoryDAO categoryRepo;
    private ExpenseRepository mBookingRepo;

    @Override
    protected void onStart() {
        super.onStart();

        mAmountBtn.setHint(R.string.placeholder_amount);
        mAmountBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(PriceInputDialog.TITLE, getResources().getString(R.string.input_price));

            PriceInputDialog expenseInput = new PriceInputDialog();
            expenseInput.setArguments(bundle);
            expenseInput.setOnPriceSelectedListener(price -> {
                mFromExpense.setPrice(price);
                mAmountBtn.setText(String.format(
                        getResources().getConfiguration().locale, "%.2f %s",
                        mFromExpense.getUnsignedPrice(),
                        new Currency().getSymbol())
                );

                setToExpense(price.getUnsignedValue());
            });
            expenseInput.show(getFragmentManager(), "transfers_amount_input");
        });

        mFromAccountBtn.setHint(R.string.input_account);
        mFromAccountBtn.setOnClickListener(v -> {
            SingleChoiceDialog<Account> accountPicker = new SingleChoiceDialog<>();
            accountPicker.createBuilder(TransferActivity.this);
            accountPicker.setTitle(getString(R.string.input_account));

            List<Account> accounts = accountRepo.getAll();
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
        });

        mToAccountBtn.setHint(R.string.input_account);
        mToAccountBtn.setOnClickListener(v -> {
            SingleChoiceDialog<Account> accountPicker = new SingleChoiceDialog<>();
            accountPicker.createBuilder(TransferActivity.this);
            accountPicker.setTitle(getString(R.string.choose_account));

            List<Account> accounts = accountRepo.getAll();
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
        });

        mDateBtn.setText(transformCalendarToReadableDate(mCalendar));
        mDateBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong(DatePickerDialog.CURRENT_DAY_IN_MILLIS, mCalendar.getTimeInMillis());

            DatePickerDialog datePicker = new DatePickerDialog();
            datePicker.setArguments(bundle);
            datePicker.setOnDateSelectedListener(date -> {
                mCalendar = date;
                mFromExpense.setDate(date);
                mToExpense.setDate(date);
                mDateBtn.setText(transformCalendarToReadableDate(date));
            });
            datePicker.show(getFragmentManager(), "transfers_date");
        });

        mCreateTransferBtn.setOnClickListener(v -> {
            //checke ob alles gesetzt ist
            if (mFromExpense.isSet() && mToExpense.isSet()) {
                ParentBooking parent = new ParentBooking(String.format("%s\n%s -> %s", getString(R.string.transfer), mFromAccount.getName(), mToAccount.getName()));
                parent.addChild(mFromExpense);
                parent.addChild(mToExpense);
                mBookingRepo.insert(parent);

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
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);

        accountRepo = Room.databaseBuilder(this, AppDatabase.class, "expenses")
                .allowMainThreadQueries() // TODO: Remove
                .build().accountDAO();
        categoryRepo = Room.databaseBuilder(this, AppDatabase.class, "expenses")
                .allowMainThreadQueries() // TODO: Remove
                .build().categoryDAO();
        mBookingRepo = new ExpenseRepository(this);

        mCalendar = Calendar.getInstance();

        mFromExpense = new Booking(
                "",
                new Price(0, true),
                getTransferCategory(),
                UUID.randomUUID()
        );
        mFromExpense.setExpenseType(Booking.EXPENSE_TYPES.TRANSFER_EXPENSE);

        mToExpense = new Booking(
                "",
                new Price(0, true),
                getTransferCategory(),
                UUID.randomUUID()
        );
        mToExpense.setExpenseType(Booking.EXPENSE_TYPES.TRANSFER_EXPENSE);

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

    private Category getTransferCategory() {
        return categoryRepo.get(app.transferCategoryId);
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
        mToExpense.setTitle(String.format("%s %s", getString(R.string.transfer_from), mFromAccount.getName()));
        mFromExpense.setAccount(mFromAccount);
        mFromAccountBtn.setText(mFromAccount.getName());

        Log.d(TAG, "selected " + mFromAccount.getName() + " as from account");
    }

    /**
     * Methode um das Eingehende Konto zu setzen
     *
     * @param newAccount Das neue eingehende Konto
     */
    private void setToAccount(Account newAccount) {

        mToAccount = newAccount;
        mFromExpense.setTitle(String.format("%s %s", getString(R.string.transfer_to), mToAccount.getName()));
        mToExpense.setAccount(mToAccount);
        mToAccountBtn.setText(mToAccount.getName());

        Log.d(TAG, "selected " + mToAccount.getName() + " as to account");
    }

    private void setToExpense(double newPrice) {
        mToExpense.setPrice(new Price(
                newPrice,
                false
        ));
    }
}
