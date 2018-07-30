package com.example.lucas.haushaltsmanager.Activities;

import android.content.Context;
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
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.ChildCategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Dialogs.AccountPickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TransferActivity extends AppCompatActivity {
    private static final String TAG = TransferActivity.class.getSimpleName();

    private Button mDateBtn, mFromAccountBtn, mToAccountBtn, mCreateTransferBtn, mAmountBtn;
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

                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.input_account));
                try {
                    bundle.putParcelable("active_account", getActiveAccount());
                } catch (AccountNotFoundException e) {

                    //do nothing
                }
                if (mToAccount != null)
                    bundle.putLong("excluded_account", mToAccount.getIndex());

                AccountPickerDialog accountPicker = new AccountPickerDialog();
                accountPicker.setArguments(bundle);
                accountPicker.setOnAccountSelectedListener(new AccountPickerDialog.OnAccountSelected() {
                    @Override
                    public void onAccountSelected(Account account) {

                        setFromAccount(account);
                        setToExpense(mFromExpense.getUnsignedPrice());
                    }
                });
                accountPicker.show(getFragmentManager(), "transfers_from_account");
            }
        });

        mToAccountBtn.setHint(R.string.input_account);
        mToAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.input_account));
                try {
                    bundle.putParcelable("active_account", getActiveAccount());
                } catch (AccountNotFoundException e) {

                    //do nothing
                }
                if (mFromAccount != null)
                    bundle.putLong("excluded_account", mFromAccount.getIndex());

                AccountPickerDialog accountPicker = new AccountPickerDialog();
                accountPicker.setArguments(bundle);
                accountPicker.setOnAccountSelectedListener(new AccountPickerDialog.OnAccountSelected() {
                    @Override
                    public void onAccountSelected(Account account) {

                        setToAccount(account);
                        setToExpense(mFromExpense.getUnsignedPrice());
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
                Log.d(TAG, "onClick: " + transformCalendarToReadableDate(mCalendar));
                bundle.putLong("current_day", mCalendar.getTimeInMillis());

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


                    ExpenseObject parent = ChildExpenseRepository.combineExpenses(bookings);
                    parent.setTitle(String.format("%s\n%s -> %s", getString(R.string.transfer), mFromAccount.getTitle(), mToAccount.getTitle()));
                    ExpenseRepository.update(parent);

                    Intent intent = new Intent(TransferActivity.this, ParentActivity.class);
                    TransferActivity.this.startActivity(intent);
                } else {

                    showErrorDialog(R.string.error_missing_content);
                }
            }
        });
    }

    /**
     * Methode um die Default TransferKategorie aus der Datenbank zu holen.
     *
     * @return Default TransferKategorie
     */
    private Category getTransferCategory() throws ChildCategoryNotFoundException {
        return ChildCategoryRepository.get(1);
    }

    /**
     * Methode um einen Fehlerdialog anzeigen zu lassen, der den User informiert, dass noch eine Eingabe fehlt, oder die Konten gleich sind.
     *
     * @param message Message id
     *///todo durch ErrorAlertDialog ersetzen
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
    private Account getActiveAccount() throws AccountNotFoundException {

        SharedPreferences preferences = getSharedPreferences("UserSettings", 0);
        long activeAccountId = preferences.getLong("activeAccount", 1);

        return AccountRepository.get(activeAccountId);
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
        mFromExpense.setAccount(mFromAccount);
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
        mToExpense.setAccount(mToAccount);
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
