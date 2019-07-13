package com.example.lucas.haushaltsmanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.Common.DefaultDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Repository;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;
import com.example.lucas.haushaltsmanager.Views.SaveFloatingActionButton;

import java.util.Locale;

public class CreateAccountActivity extends AbstractAppCompatActivity {
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_MODE_UPDATE = "update";
    public static final String INTENT_MODE_CREATE = "createExpenseItems";
    public static final String INTENT_ACCOUNT = "accountId";

    private Button mAccountNameBtn, mAccountBalanceBtn, mAccountCurrencyBtn;
    private Account mAccount;
    private AccountRepository mAccountRepo;

    private Repository accountRepository;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        mAccountRepo = new AccountRepository(this);
        accountRepository = new Repository(new DefaultDatabase(this));

        mAccountNameBtn = findViewById(R.id.new_account_name);
        mAccountBalanceBtn = findViewById(R.id.new_account_balance);
        mAccountCurrencyBtn = findViewById(R.id.new_account_currency);

        resolveMode(getIntent().getExtras());

        initializeToolbar();
    }

    private void resolveMode(Bundle args) {
        BundleUtils bundle = new BundleUtils(args);

        switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE)) {
            case INTENT_MODE_UPDATE:

                mAccount = (Account) bundle.getParcelable(INTENT_ACCOUNT, null);
                enableFabIfAccountIsSaveable(mAccount);
                break;
            case INTENT_MODE_CREATE:

                mAccount = Account.createDummyAccount();
                mAccount.setBalance(new Price(0, getDefaultCurrency()));
                mAccount.setName("");
                break;
            default:
                throw new UnsupportedOperationException("Could not handle intent mode " + bundle.getString(INTENT_MODE, null));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mAccount.getTitle().equals(""))
            mAccountNameBtn.setHint(mAccount.getTitle());
        mAccountNameBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString(BasicTextInputDialog.TITLE, getString(R.string.set_account_title));
                args.putString(BasicTextInputDialog.HINT, mAccount.getTitle());

                BasicTextInputDialog basicDialog = new BasicTextInputDialog();
                basicDialog.setArguments(args);
                basicDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {

                    @Override
                    public void onTextInput(String textInput) {

                        mAccount.setName(textInput);
                        mAccountNameBtn.setText(mAccount.getTitle());

                        enableFabIfAccountIsSaveable(mAccount);
                    }
                });
                basicDialog.show(getFragmentManager(), "create_account_name");
            }
        });

        mAccountBalanceBtn.setHint(MoneyUtils.formatHumanReadable(mAccount.getBalance(), Locale.getDefault()));
        mAccountBalanceBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString(PriceInputDialog.TITLE, getString(R.string.set_Account_balance));
                args.putParcelable(PriceInputDialog.HINT, mAccount.getBalance());

                PriceInputDialog priceInputDialog = new PriceInputDialog();
                priceInputDialog.setArguments(args);
                priceInputDialog.setOnPriceSelectedListener(new PriceInputDialog.OnPriceSelected() {
                    @Override
                    public void onPriceSelected(Price price) {

                        mAccount.setBalance(price);
                        mAccountBalanceBtn.setText(MoneyUtils.formatHumanReadable(mAccount.getBalance(), Locale.getDefault()));

                        enableFabIfAccountIsSaveable(mAccount);
                    }
                });
                priceInputDialog.show(getFragmentManager(), "create_account_price");
            }
        });

        mAccountCurrencyBtn.setText(mAccount.getBalance().getCurrency().getShortName().toUpperCase());

        SaveFloatingActionButton saveFloatingActionButton = findViewById(R.id.new_account_save);
        saveFloatingActionButton.setOnClickListener(createAccountClickListener);
    }

    /**
     * OnClickListener der unterscheidet ob ein Konto neu erstellt oder nur geupdated werden soll
     * und die dementsprechende Aktion ausfüht.
     */
    private SaveFloatingActionButton.OnClickListener createAccountClickListener = new SaveFloatingActionButton.OnClickListener() {

        @Override
        public void onCrossClick() {
            showCloseScreenDialog();
        }

        @Override
        public void onCheckClick() {
            BundleUtils bundle = new BundleUtils(getIntent().getExtras());

            switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE)) {
                case INTENT_MODE_CREATE:

                    mAccount = mAccountRepo.create(mAccount);

                    addAccountToPreferences(mAccount);
                    setAsActiveAccount(mAccount);
                    break;
                case INTENT_MODE_UPDATE:

//                    try {
//                        mAccountRepo.update(mAccount);
                        accountRepository.update(mAccount);

//                    } catch (AccountNotFoundException e) {
//
//                        Toast.makeText(CreateAccountActivity.this, getString(R.string.cannot_update_account), Toast.LENGTH_SHORT).show();
//                    }
                    break;
            }

            Intent startMainTab = new Intent(CreateAccountActivity.this, ParentActivity.class);
            startActivity(startMainTab);
        }
    };

    /**
     * Methode um das neu erstellte Konto in der Liste der Aktiven Konten zu speichern.
     *
     * @param account Zu speicherndes Konto
     */
    private void addAccountToPreferences(Account account) {
        new ActiveAccountsPreferences(this).addAccount(account);
    }

    /**
     * Methode um die Standartwährung aus den Preferences auszulesen.
     *
     * @return Standartwährung
     */
    private Currency getDefaultCurrency() {
        return new UserSettingsPreferences(this).getMainCurrency();
    }

    /**
     * Methode um das angebene Konto ala das aktuelle Hauptkonto auszuwählen.
     *
     * @param account Neues Hauptkonto
     */
    private void setAsActiveAccount(Account account) {
        new UserSettingsPreferences(this).setActiveAccount(account);
    }

    private void showCloseScreenDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(ConfirmationDialog.TITLE, getString(R.string.attention));
        bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.abort_action_confirmation_text));

        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        confirmationDialog.setArguments(bundle);
        confirmationDialog.setOnConfirmationListener(new ConfirmationDialog.OnConfirmationResult() {
            @Override
            public void onConfirmationResult(boolean closeScreen) {
                if (closeScreen)
                    finish();
            }
        });
        confirmationDialog.show(getFragmentManager(), "new_account_exit");
    }

    private void enableFabIfAccountIsSaveable(Account account) {
        SaveFloatingActionButton saveFloatingActionButton = findViewById(R.id.new_account_save);

        if (account.isSet()) {
            saveFloatingActionButton.enable();
        } else {
            saveFloatingActionButton.disable();
        }
    }
}
