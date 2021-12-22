package com.example.lucas.haushaltsmanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.AddAndSetDefaultDecorator;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;
import com.example.lucas.haushaltsmanager.Views.SaveFloatingActionButton;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Currency;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import java.util.Locale;

public class CreateAccountActivity extends AbstractAppCompatActivity implements SaveFloatingActionButton.OnClickListener {
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_MODE_UPDATE = "update";
    public static final String INTENT_MODE_CREATE = "createExpenseItems";
    public static final String INTENT_ACCOUNT = "accountId";

    private Button mAccountNameBtn, mAccountBalanceBtn, mAccountCurrencyBtn;
    private Account mAccount;
    private Price initialAccountBalance;
    private AccountDAO accountRepo;
    private BookingDAO bookingRepository;
    private AddAndSetDefaultDecorator addAndSetDefaultDecorator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        accountRepo = AppDatabase.getDatabase(this).accountDAO();
        bookingRepository = AppDatabase.getDatabase(this).bookingDAO();

        initialAccountBalance = new Price(0);

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

        if (!mAccount.isSet()) {
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

        mAccountBalanceBtn.setHint(MoneyUtils.formatHumanReadable(mAccount.getBalance(), Locale.getDefault()));
        mAccountBalanceBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString(PriceInputDialog.TITLE, getString(R.string.set_Account_balance));
            args.putParcelable(PriceInputDialog.HINT, mAccount.getBalance());

            PriceInputDialog priceInputDialog = new PriceInputDialog();
            priceInputDialog.setArguments(args);
            priceInputDialog.setOnPriceSelectedListener(price -> {

                initialAccountBalance = price;
                mAccountBalanceBtn.setText(MoneyUtils.formatHumanReadable(mAccount.getBalance(), Locale.getDefault()));

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

                AppDatabase.getDatabase(this).runInTransaction(() -> {
                    bookingRepository.insert(new Booking(
                            getString(R.string.initial_account_balance_booking_name),
                            initialAccountBalance,
                            app.unassignedCategoryId, // TODO: Which category to take
                            mAccount.getId()
                    ));

                    accountRepo.insert(mAccount);

                    addAndSetDefaultDecorator.addAccount(mAccount);
                });

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
        confirmationDialog.setOnConfirmationListener(closeScreen -> {
            if (closeScreen) {
                finish();
            }
        });
        confirmationDialog.show(getSupportFragmentManager(), "new_account_exit");
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
