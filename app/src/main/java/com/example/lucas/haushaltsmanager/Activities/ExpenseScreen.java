package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.CategoryList.CategoryList;
import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.SingleChoiceDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;
import com.example.lucas.haushaltsmanager.Views.SaveFloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class ExpenseScreen extends AbstractAppCompatActivity {
    private static final String TAG = ExpenseScreen.class.getSimpleName();

    public static final String INTENT_MODE = "mode";
    public static final String INTENT_BOOKING = "booking";
    public static final String INTENT_MODE_UPDATE_CHILD = "updateChild";
    public static final String INTENT_MODE_UPDATE_PARENT = "updateParent";
    public static final String INTENT_MODE_ADD_CHILD = "addChild";
    public static final String INTENT_MODE_CREATE_BOOKING = "createBooking";

    private Button mPriceBtn, mTitleTxt, mNoticeTxt, mAccountTxt, mCategoryTxt, mDateTxt, mIncomeBtn, mExpenseBtn, mCurrencyBtn;
    private ExpenseObject mExpense, mParentBooking;

    private UserSettingsPreferences mUserPreferences;

    private ExpenseRepository mExpenseRepo;
    private ChildExpenseRepository mChildExpenseRepo;
    private AccountRepository mAccountRepo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_screen);

        mUserPreferences = new UserSettingsPreferences(this);

        mExpenseRepo = new ExpenseRepository(this);
        mChildExpenseRepo = new ChildExpenseRepository(this);
        mAccountRepo = new AccountRepository(this);

        initializeToolbar();

        mPriceBtn = findViewById(R.id.expense_screen_price);
        mCurrencyBtn = findViewById(R.id.expense_screen_currency);
        mTitleTxt = findViewById(R.id.expense_screen_title);
        mNoticeTxt = findViewById(R.id.expense_screen_notice);
        mAccountTxt = findViewById(R.id.expense_screen_account);
        mCategoryTxt = findViewById(R.id.expense_screen_category);
        mDateTxt = findViewById(R.id.expense_screen_date);

        mIncomeBtn = findViewById(R.id.expense_screen_income);
        mExpenseBtn = findViewById(R.id.expense_screen_expense);

        resolveIntent();
    }

    private void resolveIntent() {
        BundleUtils bundle = new BundleUtils(getIntent().getExtras());

        switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE_BOOKING)) {
            case INTENT_MODE_UPDATE_PARENT:
            case INTENT_MODE_UPDATE_CHILD:
                mExpense = (ExpenseObject) bundle.getParcelable(INTENT_BOOKING, null);
                break;
            case INTENT_MODE_ADD_CHILD:
                mParentBooking = (ExpenseObject) bundle.getParcelable(INTENT_BOOKING, null);
            case INTENT_MODE_CREATE_BOOKING:
                mExpense = new ExpenseObject(
                        getString(R.string.no_name),
                        new Price(0D, true, mUserPreferences.getMainCurrency()),
                        Category.createDummyCategory(),
                        mUserPreferences.getActiveAccount().getIndex(),
                        mUserPreferences.getMainCurrency()
                );

                break;
            default:
                throw new UnsupportedOperationException("Could not handle intent mode " + bundle.getString(INTENT_MODE, null));
        }
    }

    private Account getExpenseAccount(long accountId) {
        try {

            return mAccountRepo.get(accountId);
        } catch (AccountNotFoundException e) {

            return mUserPreferences.getActiveAccount();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mPriceBtn.setHint(String.format(getResources().getConfiguration().locale, "%.2f", mExpense.getUnsignedPrice()));
        mPriceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(PriceInputDialog.TITLE, getString(R.string.input_price));
                bundle.putString(PriceInputDialog.HINT, mExpense.getUnsignedPrice() + "");

                PriceInputDialog priceInput = new PriceInputDialog();
                priceInput.setArguments(bundle);
                priceInput.setOnPriceSelectedListener(new PriceInputDialog.OnPriceSelected() {
                    @Override
                    public void onPriceSelected(double price) {
                        setPrice(new Price(price, true, getDefaultCurrency()));
                    }
                });
                priceInput.show(getFragmentManager(), "expense_screen_price");
            }
        });

        setExpenseCurrency();

        mTitleTxt.setHint(mExpense.getTitle());
        mTitleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BasicTextInputDialog.TITLE, getString(R.string.input_title));
                bundle.putString(BasicTextInputDialog.HINT, mExpense.getTitle());

                BasicTextInputDialog titleDialog = new BasicTextInputDialog();
                titleDialog.setArguments(bundle);
                titleDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {
                    @Override
                    public void onTextInput(String textInput) {
                        setTitle(textInput);
                    }
                });
                titleDialog.show(getFragmentManager(), "expense_screen_title");
            }
        });

        mCategoryTxt.setHint(mExpense.getCategory().getTitle());
        mCategoryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(ExpenseScreen.this, CategoryList.class);
                startActivityForResult(categoryIntent, 1);
            }
        });

        mDateTxt.setHint(mExpense.getDisplayableDateTime());
        mDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog();
                datePicker.setOnDateSelectedListener(new DatePickerDialog.OnDateSelected() {
                    @Override
                    public void onDateSelected(Calendar date) {
                        setDate(date);
                    }
                });
                datePicker.show(getFragmentManager(), "expense_screen_date");
            }
        });

        mNoticeTxt.setHint(mExpense.getNotice());
        mNoticeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BasicTextInputDialog.TITLE, getString(R.string.input_notice));
                bundle.putString(BasicTextInputDialog.HINT, mExpense.getNotice());

                BasicTextInputDialog noticeDialog = new BasicTextInputDialog();
                noticeDialog.setArguments(bundle);
                noticeDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {
                    @Override
                    public void onTextInput(String textInput) {
                        setNotice(textInput);
                    }
                });
                noticeDialog.show(getFragmentManager(), "expense_screen_notice");
            }
        });

        mAccountTxt.setHint(getExpenseAccount(mExpense.getAccountId()).getTitle());
        mAccountTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Account> accounts = mAccountRepo.getAll();

                SingleChoiceDialog<Account> accountPicker = new SingleChoiceDialog<>();
                accountPicker.createBuilder(ExpenseScreen.this);
                accountPicker.setTitle(getString(R.string.input_account));
                accountPicker.setContent(accounts, accounts.indexOf(getExpenseAccount(mExpense.getAccountId())));
                accountPicker.setOnEntrySelectedListener(new SingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onPositiveClick(Object account) {

                        setAccount((Account) account);
                    }

                    @Override
                    public void onNeutralClick() {

                        //do nothing
                    }
                });
                accountPicker.show(getFragmentManager(), "expense_screen_account");
            }
        });

        SaveFloatingActionButton saveFab = findViewById(R.id.expense_screen_save);
        saveFab.setOnClickListener(getOnSaveClickListener());

        showExpenditureType();
        mIncomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrice(new Price(mExpense.getUnsignedPrice(), false, getDefaultCurrency()));
            }
        });

        mExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrice(new Price(mExpense.getUnsignedPrice(), true, getDefaultCurrency()));
            }
        });

        enableFabIfBookingIsSaveable();
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
        confirmationDialog.show(getFragmentManager(), "expense_screen_exit");
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

                        try {
                            mExpenseRepo.update(mExpense);
                            Toast.makeText(ExpenseScreen.this, "Updated Booking " + mExpense.getTitle(), Toast.LENGTH_SHORT).show();
                        } catch (ExpenseNotFoundException e) {

                            Toast.makeText(ExpenseScreen.this, R.string.could_not_update_booking, Toast.LENGTH_SHORT).show();
                            // TODO: Was soll passieren, wenn die zu updatende Buchung nicht gefunden werden konnte?
                        }
                        break;
                    case INTENT_MODE_UPDATE_CHILD:

                        try {
                            mChildExpenseRepo.update(mExpense);
                            Toast.makeText(ExpenseScreen.this, "Updated Booking " + mExpense.getTitle(), Toast.LENGTH_SHORT).show();
                        } catch (ChildExpenseNotFoundException e) {

                            Toast.makeText(ExpenseScreen.this, R.string.could_not_update_booking, Toast.LENGTH_SHORT).show();
                            // TODO: Was soll passieren, wenn die zu updatende KindBuchung nicht gefunden werden konnte?
                        }
                        break;
                    case INTENT_MODE_ADD_CHILD:

                        try {
                            mChildExpenseRepo.addChildToBooking(mExpense, mParentBooking);
                            Toast.makeText(ExpenseScreen.this, "Added Booking \"" + mExpense.getTitle() + "\" to parent Booking " + mParentBooking.getTitle(), Toast.LENGTH_SHORT).show();
                        } catch (AddChildToChildException e) {

                            Log.e(TAG, "Could not addItem Child " + mExpense.getTitle() + " to parent " + mParentBooking.getTitle(), e);
                            // TODO: Was soll passieren, wenn zu der ParentBuchung keine KindBuchung hinzugefügt werden kann?
                        }
                        break;
                    case INTENT_MODE_CREATE_BOOKING:

                        mExpense = mExpenseRepo.insert(mExpense);
                        break;
                    default:
                        throw new UnsupportedOperationException("Could not handle intent mode " + bundle.getString(INTENT_MODE, null));
                }

                Intent intent = new Intent(ExpenseScreen.this, ParentActivity.class);
                startActivity(intent);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expense_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.template:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case 1:
                Category category = data.getParcelableExtra("categoryObj");
                setCategory(category);

                break;
            case 2:
                ExpenseObject template = data.getParcelableExtra("templateObj");
                showExpenseOnScreen(template);

                break;
            default:
                throw new UnsupportedOperationException("Could not resolve action with result code: " + requestCode);
        }
    }

    private void showExpenseOnScreen(ExpenseObject expense) {
        setPrice(expense.getPrice());
        setTitle(expense.getTitle());
        setCategory(expense.getCategory());
        setDate(expense.getDateTime());
        setNotice(expense.getNotice());
        setAccount(getExpenseAccount(expense.getAccountId()));
        setExpenseCurrency();
    }

    private void setPrice(Price price) {
        mExpense.setPrice(price);

        showPrice();
        showExpenditureType();
    }

    private void showPrice() {
        mPriceBtn.setText(MoneyUtils.formatHumanReadable(mExpense.getPrice()));

        enableFabIfBookingIsSaveable();
    }

    private void setExpenseCurrency() {
        mExpense.setCurrency(mUserPreferences.getMainCurrency());
        mCurrencyBtn.setText(mExpense.getCurrency().getShortName());
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
        mExpense.setCategory(category);
        mCategoryTxt.setText(mExpense.getCategory().getTitle());

        // Es kann sein, dass der DefaultExpenseType der Kategorie anders ist, als der der Buchung, von daher muss hier der Preis neu gesetzt werden
        setPrice(new Price(mExpense.getPrice().getUnsignedValue(), mExpense.getCategory().getDefaultExpenseType(), getDefaultCurrency()));

        enableFabIfBookingIsSaveable();
    }

    private void setTitle(String title) {
        mExpense.setTitle(title);
        mTitleTxt.setText(mExpense.getTitle());

        enableFabIfBookingIsSaveable();
    }

    private void setDate(Calendar date) {
        mExpense.setDateTime(date);
        mDateTxt.setText(mExpense.getDisplayableDateTime());
    }

    private void setNotice(String notice) {
        mExpense.setNotice(notice);
        mNoticeTxt.setText(mExpense.getNotice());
    }

    private void setAccount(Account account) {
        mExpense.setAccount(account);
        mAccountTxt.setText(account.getTitle());
    }

    private Currency getDefaultCurrency() {
        return new UserSettingsPreferences(this).getMainCurrency();
    }

    private void enableFabIfBookingIsSaveable() {
        SaveFloatingActionButton saveFab = findViewById(R.id.expense_screen_save);

        if (isBookingSaveable()) {
            saveFab.enable();
        } else {
            saveFab.disable();
        }
    }

    private boolean isBookingSaveable() {
        return mExpense.isSet();
    }
}
