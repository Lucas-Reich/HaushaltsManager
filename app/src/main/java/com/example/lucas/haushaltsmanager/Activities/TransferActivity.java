package com.example.lucas.haushaltsmanager.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.AccountPickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
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

    //Ausgabe
    private ExpenseObject mFromExpense;
    //Einnahme
    private ExpenseObject mToExpense;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mCalendar = Calendar.getInstance();

        mFromExpense = ExpenseObject.createDummyExpense(this);
        mFromExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.TRANSFER_EXPENSE);

        mToExpense = ExpenseObject.createDummyExpense(this);
        mToExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.TRANSFER_EXPENSE);

        mDateBtn = (Button) findViewById(R.id.transfer_date_btn);
        mFromAccountBtn = (Button) findViewById(R.id.transfer_from_account_btn);
        mToAccountBtn = (Button) findViewById(R.id.transfer_to_account_btn);
        mCreateTransferBtn = (Button) findViewById(R.id.transfer_create_btn);
        mAmountBtn = (Button) findViewById(R.id.transfer_amount_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAmountBtn.setHint(R.string.placeholder_amount);
        mAmountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", "Input Price");

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
                //checke ob alles valid ist (preis größer null, konten sind nicht die gleichen)
                //füge die Ausgaben mFromExpense und mToExpense in die Datenbank ein
            }
        });
    }

    /**
     * Methode um das momentan akitve Konto aus den ScharedPreferences auszulesen und das dementsprechende Konto aus der Datenbank zu holen
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

    @Override
    public void onAccountSelected(Account account, String tag) {

        if (tag.equals("transfers_from_account")) {

            mFromAccount = account;
            mFromExpense.setAccount(account);
            mFromAccountBtn.setText(mFromAccount.getName());
        } else if (tag.equals("transfers_to_account")) {

            mToAccount = account;
            mToExpense.setAccount(account);
            mToAccountBtn.setText(mToAccount.getName());
        }
    }

    @Override
    public void onDateSelected(Calendar date, String tag) {

        if (tag.equals("transfers_date")) {

            mCalendar = date;
            mFromExpense.setDateTime(date);
            mToExpense.setDateTime(date);
            mDateBtn.setText(transformCalendarToReadableDate(date));
        }
    }

    @Override
    public void onPriceSelected(double price, String tag) {

        if (tag.equals("transfers_amount_input")) {

            mFromExpense.setPrice(price);
            mFromExpense.setExpenditure(true);

            mToExpense.setPrice(price);
            mToExpense.setExpenditure(false);

            mAmountBtn.setText(String.format("%s", price));
            //todo formatiere den preis entsprechend der user locale
            //todo füge die Basis währung mit in den Text ein
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.close();
    }
}
