package com.example.lucas.haushaltsmanager.Activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.lucas.haushaltsmanager.Activities.MainTab.TabParentActivity;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class CreateNewAccountActivity extends AppCompatActivity implements OnItemSelectedListener, BasicTextInputDialog.BasicDialogCommunicator, PriceInputDialog.OnPriceSelected {

    private String TAG = CreateNewAccountActivity.class.getSimpleName();

    Button mAccountNameBtn;
    Button mAccountBalanceBtn, mCreateAccountBtn;
    Spinner mAccountCurrencySpin;
    ExpensesDataSource mDatabase;
    Account mAccount;

    private enum CREATION_MODES {
        CREATE_ACCOUNT,
        UPDATE_ACCOUNT
    }

    private CREATION_MODES mCreationMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_ver2);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            switch (bundle.getString("mode")) {

                case "update":
                    mCreationMode = CREATION_MODES.UPDATE_ACCOUNT;

                    long accountId = bundle.getLong("account_id");
                    mAccount = mDatabase.getAccountById(accountId);
                    break;
                case "create":
                    mCreationMode = CREATION_MODES.CREATE_ACCOUNT;

                    mAccount = Account.createDummyAccount(this);
                    break;
                default:
                    break;
            }
        }

        mAccountNameBtn = (Button) findViewById(R.id.new_account_name);
        mAccountBalanceBtn = (Button) findViewById(R.id.new_account_balance);
        mCreateAccountBtn = (Button) findViewById(R.id.new_account_create);

        mAccountCurrencySpin = (Spinner) findViewById(R.id.new_account_currency);
        mAccountCurrencySpin.setOnItemSelectedListener(this);
        refreshSpinnerContents();
        setVisibleSpinnerItem(mAccount.getCurrency().getShortName());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAccountNameBtn.setHint(mAccount.getName());
        mAccountNameBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("title", "Set Account getName");

                BasicTextInputDialog basicDialog = new BasicTextInputDialog();
                basicDialog.setArguments(args);
                basicDialog.show(getFragmentManager(), "create_account_name");
            }
        });

        mAccountBalanceBtn.setHint(mAccount.getBalance() + "");
        mAccountBalanceBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("title", "Set Account Balance");

                PriceInputDialog priceInputDialog = new PriceInputDialog();
                priceInputDialog.setArguments(args);
                priceInputDialog.show(getFragmentManager(), "create_account_price");
            }
        });

        if (mCreationMode == CREATION_MODES.UPDATE_ACCOUNT)
            mCreateAccountBtn.setText(R.string.update);
        else
            mCreateAccountBtn.setText(R.string.btn_save);
        mCreateAccountBtn.setOnClickListener(createAccountClickListener);
    }

    /**
     * OnClickListener der unterscheidet ob ein Konto neu erstellt oder nur geupdated werden soll
     * und die dementsprechende Aktion ausfüht.
     */
    private View.OnClickListener createAccountClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!mAccount.isSet())
                return;

            switch (mCreationMode) {

                case CREATE_ACCOUNT:

                    SharedPreferences activeAccounts = getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);
                    SharedPreferences.Editor activeAccountsEditor = activeAccounts.edit();
                    activeAccountsEditor.putBoolean(mAccount.getName(), true);
                    activeAccountsEditor.apply();

                    Account account = mDatabase.createAccount(mAccount);

                    SharedPreferences userSettings = getSharedPreferences("UserSettings", ContextWrapper.MODE_PRIVATE);
                    SharedPreferences.Editor userSettingsEditor = userSettings.edit();
                    userSettingsEditor.putLong("activeAccount", account.getIndex());
                    userSettingsEditor.apply();
                    break;
                case UPDATE_ACCOUNT:

                    mDatabase.updateAccount(mAccount);
                    break;
            }

            Intent startMainTab = new Intent(CreateNewAccountActivity.this, TabParentActivity.class);
            CreateNewAccountActivity.this.startActivity(startMainTab);
        }
    };

    /**
     * Methode um die in dem Spinner angezeigten Währungen neu zu laden
     */
    private void refreshSpinnerContents() {

        ArrayList<Currency> currencies = mDatabase.getAllCurrencies();

        List<String> currencyShortNames = new ArrayList<>();
        for (Currency currency : currencies) {

            currencyShortNames.add(currency.getShortName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyShortNames);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAccountCurrencySpin.setAdapter(dataAdapter);
    }

    /**
     * Methode um das sichtbare Element des Spinners auf das visibleItem zu setzen
     *
     * @param visibleItem Anzuzeigendes Element
     */
    private void setVisibleSpinnerItem(String visibleItem) {

        int index = 0;
        for (int i = 0; i < mAccountCurrencySpin.getCount(); i++) {
            if (mAccountCurrencySpin.getItemAtPosition(i).equals(visibleItem))
                index = i;
        }

        mAccountCurrencySpin.setSelection(index);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String currencyName = parent.getItemAtPosition(position).toString();

        this.mAccount.setCurrency(mDatabase.getCurrency(currencyName));
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public void onTextInput(String textInput, String tag) {

        if (tag.equals("create_account_name")) {

            mAccount.setName(textInput);
            mAccountNameBtn.setText(mAccount.getName());

            Log.d(TAG, "set Account name to" + mAccount.getName());
        }
    }

    @Override
    public void onPriceSelected(double price, String tag) {

        if (tag.equals("create_account_price")) {

            mAccount.setBalance(price);
            mAccountBalanceBtn.setText(String.format(this.getResources().getConfiguration().locale, "%.2f", mAccount.getBalance()));

            Log.d(TAG, "set account balance to " + mAccount.getBalance());
        }
    }
}
