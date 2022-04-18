package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ParentBookingDAO;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.SingleChoiceDialog;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;
import com.example.lucas.haushaltsmanager.Views.SaveFloatingActionButton;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Currency;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking;
import com.example.lucas.haushaltsmanager.entities.category.Category;
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBooking;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ExpenseScreen extends AbstractAppCompatActivity {
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_BOOKING = "booking";
    public static final String INTENT_MODE_UPDATE_CHILD = "updateChild";
    public static final String INTENT_MODE_UPDATE_PARENT = "updateParent";
    public static final String INTENT_MODE_ADD_CHILD = "addChild";
    public static final String INTENT_MODE_CREATE_BOOKING = "createBooking";
    private Button mPriceBtn, mTitleTxt, mAccountTxt, mCategoryTxt, mDateTxt, mIncomeBtn, mExpenseBtn, mCurrencyBtn;
    private Booking mExpense, mParentBooking;

    private UserSettingsPreferences mUserPreferences;

    private BookingDAO bookingRepository;
    private ParentBookingDAO parentBookingRepository;
    private AccountDAO accountRepo;
    private CategoryRepository categoryRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_screen);

        mUserPreferences = new UserSettingsPreferences(this);

        bookingRepository = AppDatabase.getDatabase(this).bookingDAO();
        accountRepo = AppDatabase.getDatabase(this).accountDAO();
        categoryRepository = new CategoryRepository(AppDatabase.getDatabase(this).categoryDAO());
        parentBookingRepository = AppDatabase.getDatabase(this).parentBookingDAO();

        initializeToolbar();

        mPriceBtn = findViewById(R.id.expense_screen_price);
        mCurrencyBtn = findViewById(R.id.expense_screen_currency);
        mTitleTxt = findViewById(R.id.expense_screen_title);
        mAccountTxt = findViewById(R.id.expense_screen_account);
        mCategoryTxt = findViewById(R.id.expense_screen_category);
        mDateTxt = findViewById(R.id.expense_screen_date);

        mIncomeBtn = findViewById(R.id.expense_screen_income);
        mExpenseBtn = findViewById(R.id.expense_screen_expense);

        resolveIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mPriceBtn.setHint(String.format(getResources().getConfiguration().locale, "%.2f", mExpense.getUnsignedPrice()));
        mPriceBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(PriceInputDialog.TITLE, getString(R.string.input_price));
            bundle.putParcelable(PriceInputDialog.HINT, mExpense.getPrice());

            PriceInputDialog priceInput = new PriceInputDialog();
            priceInput.setArguments(bundle);
            priceInput.setOnPriceSelectedListener(this::setPrice);
            priceInput.show(getSupportFragmentManager(), "expense_screen_price");
        });

        setExpenseCurrency();

        mTitleTxt.setHint(mExpense.getTitle());
        mTitleTxt.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(BasicTextInputDialog.TITLE, getString(R.string.input_title));
            bundle.putString(BasicTextInputDialog.HINT, mExpense.getTitle());

            BasicTextInputDialog titleDialog = new BasicTextInputDialog();
            titleDialog.setArguments(bundle);
            titleDialog.setOnTextInputListener(this::setTitle);
            titleDialog.show(getSupportFragmentManager(), "expense_screen_title");
        });

        mCategoryTxt.setHint(categoryRepository.get(mExpense.getCategoryId()).getName());
        mCategoryTxt.setOnClickListener(v -> {
            Intent categoryIntent = new Intent(ExpenseScreen.this, CategoryList.class);
            startActivityForResult(categoryIntent, 1);
        });

        mDateTxt.setHint(mExpense.getDisplayableDateTime());
        mDateTxt.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog();
            datePicker.setOnDateSelectedListener(this::setDate);
            datePicker.show(getFragmentManager(), "expense_screen_date");
        });

        mAccountTxt.setHint(getExpenseAccount(mExpense.getAccountId()).getName());
        mAccountTxt.setOnClickListener(v -> {
            List<Account> accounts = accountRepo.getAll();

            SingleChoiceDialog<Account> accountPicker = new SingleChoiceDialog<>();
            accountPicker.createBuilder(ExpenseScreen.this);
            accountPicker.setTitle(getString(R.string.input_account));
            accountPicker.setContent(accounts, accounts.indexOf(getExpenseAccount(mExpense.getAccountId())));
            accountPicker.setOnEntrySelectedListener(account -> setAccount((Account) account));
            accountPicker.show(getFragmentManager(), "expense_screen_account");
        });

        SaveFloatingActionButton saveFab = findViewById(R.id.expense_screen_save);
        saveFab.setOnClickListener(getOnSaveClickListener());

        showExpenditureType();
        mIncomeBtn.setOnClickListener(v -> setPrice(new Price(mExpense.getUnsignedPrice())));

        mExpenseBtn.setOnClickListener(v -> setPrice(new Price(-mExpense.getUnsignedPrice())));

        enableFabIfBookingIsSaveable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        switch (requestCode) {
            case 1:
                Category category = data.getParcelableExtra("categoryObj");
                setCategory(category);

                break;
            case 2:
                TemplateBooking template = data.getParcelableExtra("templateObj");
                showExpenseOnScreen(template);

                break;
            default:
                throw new UnsupportedOperationException("Could not resolve action with result code: " + requestCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expense_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 5:
                Intent chooseTemplateIntent = new Intent(this, TemplatesActivity.class);
                startActivityForResult(chooseTemplateIntent, 2);

                break;
            case android.R.id.home:
                onBackPressed();

                break;
            default:
                throw new UnsupportedOperationException("Du hast auf einen Menüpunkt geklickt, welcher nicht unterstützt wird");
        }

        return true;
    }

    private void resolveIntent() {
        BundleUtils bundle = new BundleUtils(getIntent().getExtras());

        switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE_BOOKING)) {
            case INTENT_MODE_UPDATE_PARENT:
            case INTENT_MODE_UPDATE_CHILD:
                mExpense = (Booking) bundle.getParcelable(INTENT_BOOKING, null);
                break;
            case INTENT_MODE_ADD_CHILD:
                mParentBooking = (Booking) bundle.getParcelable(INTENT_BOOKING, null);
            case INTENT_MODE_CREATE_BOOKING:
                mExpense = new Booking(
                        getString(R.string.no_name),
                        new Price(0D),
                        UUID.randomUUID(),
                        mUserPreferences.getActiveAccount().getId()
                );
                break;
            default:
                throw new UnsupportedOperationException("Could not handle intent mode " + bundle.getString(INTENT_MODE, null));
        }
    }

    private Account getExpenseAccount(UUID accountId) {
        try {
            return accountRepo.get(accountId);
        } catch (Exception e) {
            return mUserPreferences.getActiveAccount();
        }
    }

    private void showCloseScreenDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(ConfirmationDialog.TITLE, getString(R.string.attention));
        bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.abort_action_confirmation_text));

        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        confirmationDialog.setArguments(bundle);
        confirmationDialog.setOnConfirmationListener(closeScreen -> {
            if (closeScreen)
                finish();
        });
        confirmationDialog.show(getSupportFragmentManager(), "expense_screen_exit");
    }

    private SaveFloatingActionButton.OnClickListener getOnSaveClickListener() {
        return new SaveFloatingActionButton.OnClickListener() {
            @Override
            public void onCrossClick() {
                showCloseScreenDialog();
            }

            @Override
            public void onCheckClick() {
                BundleUtils bundle = new BundleUtils(getIntent().getExtras());

                switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE_BOOKING)) {
                    case INTENT_MODE_UPDATE_PARENT:
                    case INTENT_MODE_UPDATE_CHILD:
                        bookingRepository.update(mExpense);
                        break;
                    case INTENT_MODE_ADD_CHILD:
                        ParentBooking parentBooking = new ParentBooking(""); // TODO: How to add a title for the new parent booking
                        parentBooking.addChild(mParentBooking);
                        parentBooking.addChild(mExpense);

                        parentBookingRepository.insert(parentBooking, parentBooking.getChildren());
                    case INTENT_MODE_CREATE_BOOKING:
                        bookingRepository.insert(mExpense);
                        break;
                    default:
                        throw new UnsupportedOperationException("Could not handle intent mode " + bundle.getString(INTENT_MODE, null));
                }

                Intent intent = new Intent(ExpenseScreen.this, ParentActivity.class);
                startActivity(intent);
            }
        };
    }

    private void showExpenseOnScreen(TemplateBooking expense) {
        setPrice(expense.getPrice());
        setTitle(expense.getTitle());
        setCategory(expense.getCategory());
        setDate(expense.getDate());
        setAccount(getExpenseAccount(expense.getAccountId()));
        setExpenseCurrency();
    }

    private void setPrice(Price price) {
        mExpense.setPrice(price);

        showPrice();
        showExpenditureType();
    }

    private void showPrice() {
        mPriceBtn.setText(MoneyUtils.formatHumanReadable(mExpense.getPrice(), Locale.getDefault()));

        enableFabIfBookingIsSaveable();
    }

    private void setExpenseCurrency() {
        mCurrencyBtn.setText(new Currency().getShortName());
    }

    private void showExpenditureType() {
        LinearLayout ll = findViewById(R.id.expense_screen_bottom_toolbar);
        ll.setBackgroundColor(
                mExpense.isExpenditure()
                        ? getColorRes(R.color.booking_expense)
                        : getColorRes(R.color.booking_income)
        );
    }

    private void setCategory(Category category) {
        mExpense.setCategoryId(category.getId());
        mCategoryTxt.setText(category.getName());

        // Es kann sein, dass der DefaultExpenseType der Kategorie anders ist, als der der Buchung, von daher muss hier der Preis neu gesetzt werden
        setPrice(Price.fromValueWithCategory(mExpense.getPrice().getAbsoluteValue(), category));

        enableFabIfBookingIsSaveable();
    }

    private void setTitle(String title) {
        mExpense.setTitle(title);
        mTitleTxt.setText(mExpense.getTitle());

        enableFabIfBookingIsSaveable();
    }

    private void setDate(Calendar date) {
        mExpense.setDate(date);
        mDateTxt.setText(mExpense.getDisplayableDateTime());
    }

    private void setAccount(Account account) {
        mExpense.setAccountId(account.getId());
        mAccountTxt.setText(account.getName());
    }

    private void enableFabIfBookingIsSaveable() {
        SaveFloatingActionButton saveFab = findViewById(R.id.expense_screen_save);

        if (mExpense.isSet()) {
            saveFab.enable();
        } else {
            saveFab.disable();
        }
    }
}
