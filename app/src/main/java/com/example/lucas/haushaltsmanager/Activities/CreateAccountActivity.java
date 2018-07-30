package com.example.lucas.haushaltsmanager.Activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.R;

public class CreateAccountActivity extends AppCompatActivity {
    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    private Button mAccountNameBtn;
    private Button mAccountBalanceBtn, mCreateAccountBtn;
    private Account mAccount;
    private ImageButton mBackArrow;

    private enum CREATION_MODES {
        CREATE_ACCOUNT,
        UPDATE_ACCOUNT
    }

    private CREATION_MODES mCreationMode;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            switch (bundle.getString("mode")) {

                case "update":
                    mCreationMode = CREATION_MODES.UPDATE_ACCOUNT;

                    try {
                        long accountId = bundle.getLong("account_id");
                        mAccount = AccountRepository.get(accountId);
                    } catch (AccountNotFoundException e) {

                        Toast.makeText(this, getString(R.string.account_not_found), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                case "create":
                    mCreationMode = CREATION_MODES.CREATE_ACCOUNT;

                    try {
                        mAccount = Account.createDummyAccount();
                        mAccount.setCurrency(getDefaultCurrency());
                    } catch (CurrencyNotFoundException e) {

                        Toast.makeText(this, getString(R.string.no_default_currency), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }

        mAccountNameBtn = (Button) findViewById(R.id.new_account_name);
        mAccountBalanceBtn = (Button) findViewById(R.id.new_account_balance);
        mCreateAccountBtn = (Button) findViewById(R.id.new_account_create);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mAccountNameBtn.setHint(mAccount.getTitle());
        mAccountNameBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("title", getResources().getString(R.string.set_account_title));
                args.putString("hint", mAccount.getTitle());

                BasicTextInputDialog basicDialog = new BasicTextInputDialog();
                basicDialog.setOnTextInputListener(new BasicTextInputDialog.BasicDialogCommunicator() {

                    @Override
                    public void onTextInput(String textInput) {

                        mAccount.setName(textInput);
                        mAccountNameBtn.setText(mAccount.getTitle());

                        Log.d(TAG, "set Account name to" + mAccount.getTitle());
                    }
                });
                basicDialog.setArguments(args);
                basicDialog.show(getFragmentManager(), "create_account_name");
            }
        });

        mAccountBalanceBtn.setHint(String.format(this.getResources().getConfiguration().locale, "%.2f", mAccount.getBalance()));
        mAccountBalanceBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("title", getResources().getString(R.string.set_Account_balance));

                PriceInputDialog priceInputDialog = new PriceInputDialog();
                priceInputDialog.setArguments(args);
                priceInputDialog.setOnPriceSelectedListener(new PriceInputDialog.OnPriceSelected() {
                    @Override
                    public void onPriceSelected(double price) {

                        mAccount.setBalance(price);
                        mAccountBalanceBtn.setText(String.format(getResources().getConfiguration().locale, "%.2f", mAccount.getBalance()));

                        Log.d(TAG, "set account balance to " + mAccount.getBalance());
                    }
                });
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

            SharedPreferences activeAccounts = getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);
            SharedPreferences.Editor activeAccountsEditor = activeAccounts.edit();
            activeAccountsEditor.putBoolean(mAccount.getTitle(), true);
            activeAccountsEditor.apply();

            switch (mCreationMode) {

                case CREATE_ACCOUNT:

                    Account account = AccountRepository.insert(mAccount);

                    SharedPreferences userSettings = getSharedPreferences("UserSettings", ContextWrapper.MODE_PRIVATE);
                    SharedPreferences.Editor userSettingsEditor = userSettings.edit();
                    userSettingsEditor.putLong("activeAccount", account.getIndex());
                    userSettingsEditor.apply();
                    break;
                case UPDATE_ACCOUNT:

                    try {
                        AccountRepository.update(mAccount);

                    } catch (AccountNotFoundException e) {

                        Toast.makeText(CreateAccountActivity.this, getString(R.string.cannot_update_account), Toast.LENGTH_SHORT).show();
                        //todo Fehlermdelud verbessern
                    }
                    break;
            }

            Intent startMainTab = new Intent(CreateAccountActivity.this, ParentActivity.class);
            CreateAccountActivity.this.startActivity(startMainTab);
        }
    };

    private Currency getDefaultCurrency() throws CurrencyNotFoundException {
        SharedPreferences preferences = this.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        long mainCurrencyIndex = preferences.getLong("mainCurrencyIndex", 32);
        return CurrencyRepository.get(mainCurrencyIndex);
    }
}
