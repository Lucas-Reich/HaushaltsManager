package com.example.lucas.haushaltsmanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.room.Room;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountDAO;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.AddAndSetDefaultDecorator;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;
import com.example.lucas.haushaltsmanager.Views.SaveFloatingActionButton;

import java.util.Locale;

public class CreateAccountActivity extends AbstractAppCompatActivity implements SaveFloatingActionButton.OnClickListener {
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_MODE_UPDATE = "update";
    public static final String INTENT_MODE_CREATE = "createExpenseItems";
    public static final String INTENT_ACCOUNT = "accountId";

    private Button mAccountNameBtn, mAccountBalanceBtn, mAccountCurrencyBtn;
    private Account mAccount;
    private AccountDAO accountRepo;
    private AddAndSetDefaultDecorator addAndSetDefaultDecorator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        accountRepo = Room.databaseBuilder(this, AppDatabase.class, "expenses")
                .allowMainThreadQueries() // TODO: Remove
                .build().accountDAO();

        mAccountNameBtn = findViewById(R.id.new_account_name);
        mAccountBalanceBtn = findViewById(R.id.new_account_balance);
        mAccountCurrencyBtn = findViewById(R.id.new_account_currency);

        addAndSetDefaultDecorator = new AddAndSetDefaultDecorator(new ActiveAccountsPreferences(this), this);

        resolveMode(getIntent().getExtras());

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mAccount.getName().equals("")) {
            mAccountNameBtn.setHint(mAccount.getName());
        }
        mAccountNameBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString(BasicTextInputDialog.TITLE, getString(R.string.set_account_title));
            args.putString(BasicTextInputDialog.HINT, mAccount.getName());

            BasicTextInputDialog basicDialog = new BasicTextInputDialog();
            basicDialog.setArguments(args);
            basicDialog.setOnTextInputListener(textInput -> {
                mAccount.setName(textInput);
                mAccountNameBtn.setText(mAccount.getName());

                enableFabIfAccountIsSaveable(mAccount);
            });
            basicDialog.show(getFragmentManager(), "create_account_name");
        });

        mAccountBalanceBtn.setHint(MoneyUtils.formatHumanReadable(mAccount.getPrice(), Locale.getDefault()));
        mAccountBalanceBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString(PriceInputDialog.TITLE, getString(R.string.set_Account_balance));
            args.putParcelable(PriceInputDialog.HINT, mAccount.getPrice());

            PriceInputDialog priceInputDialog = new PriceInputDialog();
            priceInputDialog.setArguments(args);
            priceInputDialog.setOnPriceSelectedListener(price -> {
                mAccount.setPrice(price);
                mAccountBalanceBtn.setText(MoneyUtils.formatHumanReadable(mAccount.getPrice(), Locale.getDefault()));

                enableFabIfAccountIsSaveable(mAccount);
            });
            priceInputDialog.show(getFragmentManager(), "create_account_price");
        });

        mAccountCurrencyBtn.setText(new Currency().getShortName().toUpperCase());

        SaveFloatingActionButton saveFloatingActionButton = findViewById(R.id.new_account_save);
        saveFloatingActionButton.setOnClickListener(this);
    }

    public void onCrossClick() {
        showCloseScreenDialog();
    }

    @Override
    public void onCheckClick() {
        BundleUtils bundle = new BundleUtils(getIntent().getExtras());

        switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE)) {
            case INTENT_MODE_CREATE:

                accountRepo.insert(mAccount);

                addAndSetDefaultDecorator.addAccount(mAccount);
                break;
            case INTENT_MODE_UPDATE:

                accountRepo.update(mAccount);
                break;
        }

        Intent startMainTab = new Intent(CreateAccountActivity.this, ParentActivity.class);
        startActivity(startMainTab);
    }

    private void resolveMode(Bundle args) {
        BundleUtils bundle = new BundleUtils(args);

        switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE)) {
            case INTENT_MODE_UPDATE:

                mAccount = (Account) bundle.getParcelable(INTENT_ACCOUNT, null);
                enableFabIfAccountIsSaveable(mAccount);
                break;
            case INTENT_MODE_CREATE:

                mAccount = new Account("", new Price(0));
                break;
            default:
                throw new UnsupportedOperationException("Could not handle intent mode " + bundle.getString(INTENT_MODE, null));
        }
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
