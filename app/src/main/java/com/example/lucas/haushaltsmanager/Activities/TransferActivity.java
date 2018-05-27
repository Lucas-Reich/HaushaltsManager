package com.example.lucas.haushaltsmanager.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.lucas.haushaltsmanager.Activities.MainTab.TabParentActivity;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.AccountPickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TransferActivity extends AppCompatActivity implements AccountPickerDialog.OnAccountSelected, DatePickerDialog.OnDateSelected, PriceInputDialog.OnPriceSelected {

    private static String TAG = TransferActivity.class.getSimpleName();

    private Button mDateBtn, mFromAccountBtn, mToAccountBtn, mCreateTransferBtn, mAmountBtn;
    private ExpensesDataSource mDatabase;
    private Account mFromAccount, mToAccount;
    private Calendar mCalendar;
    private Toolbar mToolbar;
    private ImageButton mBackArrow;

    //Ausgabe
    private ExpenseObject mFromExpense;
    //Einnahme
    private ExpenseObject mToExpense;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mCalendar = Calendar.getInstance();

        mFromExpense = ExpenseObject.createDummyExpense(this);
        mFromExpense.setPrice(0);
        mFromExpense.setExpenditure(true);
        mFromExpense.setCategory(getTransferCategory());
        mFromExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.TRANSFER_EXPENSE);
        mFromExpense.setTitle(getResources().getString(R.string.transfer));

        mToExpense = ExpenseObject.createDummyExpense(this);
        mToExpense.setPrice(0);
        mToExpense.setExpenditure(false);
        mToExpense.setCategory(getTransferCategory());
        mToExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.TRANSFER_EXPENSE);
        mToExpense.setTitle(getResources().getString(R.string.transfer));

        mDateBtn = (Button) findViewById(R.id.transfer_date_btn);
        mFromAccountBtn = (Button) findViewById(R.id.transfer_from_account_btn);
        mToAccountBtn = (Button) findViewById(R.id.transfer_to_account_btn);
        mCreateTransferBtn = (Button) findViewById(R.id.transfer_create_btn);
        mAmountBtn = (Button) findViewById(R.id.transfer_amount_btn);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("from_account"))
            setFromAccount((Account) bundle.getParcelable("from_account"));

    }

    @Override
    protected void onStart() {
        super.onStart();

        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        mAmountBtn.setHint(R.string.placeholder_amount);
        mAmountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.input_price));

                PriceInputDialog expenseInput = new PriceInputDialog();
                expenseInput.setArguments(bundle);
                expenseInput.show(getFragmentManager(), "transfers_amount_input");
            }
        });

        mFromAccountBtn.setHint(R.string.input_account);
        mFromAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.input_account));
                bundle.putParcelable("active_account", getActiveAccount());
                if (mToAccount != null)
                    bundle.putLong("excluded_account", mToAccount.getIndex());

                AccountPickerDialog accountPicker = new AccountPickerDialog();
                accountPicker.setArguments(bundle);
                accountPicker.show(getFragmentManager(), "transfers_from_account");
            }
        });

        mToAccountBtn.setHint(R.string.input_account);
        mToAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.input_account));
                bundle.putParcelable("active_account", getActiveAccount());
                if (mFromAccount != null)
                    bundle.putLong("excluded_account", mFromAccount.getIndex());

                AccountPickerDialog accountPicker = new AccountPickerDialog();
                accountPicker.setArguments(bundle);
                accountPicker.show(getFragmentManager(), "transfers_to_account");
            }
        });

        mDateBtn.setText(transformCalendarToReadableDate(mCalendar));
        mDateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                Log.d(TAG, "onClick: " + transformCalendarToReadableDate(mCalendar));
                bundle.putLong("current_day", mCalendar.getTimeInMillis());

                DatePickerDialog datePicker = new DatePickerDialog();
                datePicker.setArguments(bundle);
                datePicker.show(getFragmentManager(), "transfers_date");
            }
        });

        mCreateTransferBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //checke ob alles gesetzt ist
                if (mFromExpense.isSet() && mToExpense.isSet()) {

                    mDatabase.createBooking(mFromExpense);
                    mDatabase.createBooking(mToExpense);

                    Intent intent = new Intent(TransferActivity.this, TabParentActivity.class);
                    TransferActivity.this.startActivity(intent);
                } else {

                    showErrorDialog(R.string.error_missing_content);
                    //todo bessere übersetzung einfallen lassen
                }
            }
        });
    }

    /**
     * Methode um die Default TransferKategorie aus der Datenbank zu holen.
     *
     * @return Default TransferKategorie
     */
    private Category getTransferCategory() {

        return mDatabase.getCategoryById(1);
    }

    /**
     * Methode um einen Fehlerdialog anzeigen zu lassen, der den User informiert, dass noch eine Eingabe fehlt, oder die Konten gleich sind.
     *
     * @param message Message id
     */
    private void showErrorDialog(@StringRes int message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.error);

        builder.setMessage(message);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //do nothing
            }
        });

        builder.create();

        builder.show();
    }

    /**
     * Methode um das momentan aktive Konto aus den ScharedPreferences auszulesen und das dementsprechende Konto aus der Datenbank zu holen
     *
     * @return aktives Konto
     */
    private Account getActiveAccount() {

        SharedPreferences preferences = getSharedPreferences("UserSettings", 0);
        long activeAccountId = preferences.getLong("activeAccount", 1);

        return mDatabase.getAccountById(activeAccountId);
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
     * Methode, welche den callback des AccountPickerDialogs implementiert.
     *
     * @param account Konto, welches vom User gewählt wurde
     * @param tag     Tag, welches bei der Dialogerstellung mit gesendet wurde
     */
    @Override
    public void onAccountSelected(Account account, String tag) {

        if (tag.equals("transfers_from_account")) {

            setFromAccount(account);
        } else if (tag.equals("transfers_to_account")) {

            setToAccount(account);
        }

        setToExpense(mFromExpense.getExpenseCurrency(), mFromExpense.getUnsignedPrice());
    }

    /**
     * Methode um das Ausgehende Konto zu setzen
     *
     * @param newAccount Das neue ausgehende Konto
     */
    private void setFromAccount(Account newAccount) {

        mFromAccount = newAccount;
        mFromExpense.setAccount(mFromAccount);
        mFromExpense.setExpenseCurrency(mFromAccount.getCurrency());
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
        mToExpense.setAccount(mToAccount);
        mToExpense.setExpenseCurrency(mToAccount.getCurrency());
        mToAccountBtn.setText(mToAccount.getName());

        Log.d(TAG, "selected " + mToAccount.getName() + " as to account");
    }

    /**
     * Methode, welche den callback des DatePickerDialogs implementiert.
     *
     * @param date Datum, welches vom User ausgewählt wurde
     * @param tag  Tag, welches bei der Dialogerstellung mit gesendet wurde
     */
    @Override
    public void onDateSelected(Calendar date, String tag) {

        if (tag.equals("transfers_date")) {

            mCalendar = date;
            mFromExpense.setDateTime(date);
            mToExpense.setDateTime(date);
            mDateBtn.setText(transformCalendarToReadableDate(date));
        }
    }

    /**
     * Methode, welche den callback des PriceInputDialogs implementiert.
     *
     * @param price Preis, der vom User eingegeben wurde
     * @param tag   Tag, welches bei der Dialogerstellung mit gesendet wurde
     */
    @Override
    public void onPriceSelected(double price, String tag) {

        if (tag.equals("transfers_amount_input")) {

            mFromExpense.setPrice(price);
            mFromExpense.setExpenditure(true);
            mAmountBtn.setText(String.format(this.getResources().getConfiguration().locale, "%.2f %s", mFromExpense.getUnsignedPrice(), mFromExpense.getExpenseCurrency().getSymbol()));

            setToExpense(mFromExpense.getExpenseCurrency(), price);
        }
    }

    /**
     * Methode um den Betrag der ToExpense zu setzen, da dieser eventuell umgerechnet werden muss.
     *
     * @param fromCurrency Ausgangswährung (mFromExpense.getExpenseCurrency)
     * @param newPrice     Den Preis, welcher umgerechnet werden muss
     */
    private void setToExpense(Currency fromCurrency, double newPrice) {

        if (fromCurrency.equals(mToExpense.getExpenseCurrency())) {

            mToExpense.setPrice(newPrice);
            mToExpense.setExpenditure(false);
            return;
        }


        Double conversionRate = getConversionRate(fromCurrency, mToExpense.getExpenseCurrency(), mToExpense.getDateTime().getTimeInMillis());
        if (conversionRate != null) {

            mToExpense.setPrice(newPrice * conversionRate);
            mToExpense.setExpenditure(false);
        } else {

            //flag setzen, welches die toExpense in den convert-expenses-stack schreibt, wenn der user die überweisung betstätigt
            //todo den umrechnungskurs für die buchungen anfordern
        }
    }

    @Nullable
    private Double getConversionRate(Currency fromCurrency, Currency toCurrency, long timestamp) {
        return mDatabase.getExchangeRate(fromCurrency.getIndex(), toCurrency.getIndex(), timestamp);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.close();
    }
}
