package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.SingleChoiceDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;

import java.util.Calendar;

public class ExpenseScreen extends AbstractAppCompatActivity {
    private static final String TAG = ExpenseScreen.class.getSimpleName();

    public static final String INTENT_MODE = "mode";
    public static final String INTENT_BOOKING = "booking";
    public static final String INTENT_MODE_UPDATE_CHILD = "updateChild";
    public static final String INTENT_MODE_UPDATE_PARENT = "updateParent";
    public static final String INTENT_MODE_ADD_CHILD = "addChild";
    public static final String INTENT_MODE_CREATE_BOOKING = "createBooking";

    private ExpenseObject mExpense;
    private Button mDateBtn, mAccountBtn, mSaveBtn, mCategoryBtn, mTitleBtn, mNoticeBtn;
    private TextView mPriceTxt, mCurrencySymbolTxt;
    private RadioGroup mExpenseTypeRadio;
    private ExpenseObject mParentBooking;
    private UserSettingsPreferences userPreferences;

    private AccountRepository mAccountRepo;
    private ChildExpenseRepository mChildExpenseRepo;
    private ExpenseRepository mBookingRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_screen);

        userPreferences = new UserSettingsPreferences(this);

        mAccountRepo = new AccountRepository(this);
        mChildExpenseRepo = new ChildExpenseRepository(this);
        mBookingRepo = new ExpenseRepository(this);

        initializeToolbar();

        mPriceTxt = findViewById(R.id.expense_screen_amount);
        mCurrencySymbolTxt = findViewById(R.id.expense_screen_currency_symbol);
        mCategoryBtn = findViewById(R.id.expense_screen_category);
        mExpenseTypeRadio = findViewById(R.id.expense_screen_expense_type);
        mTitleBtn = findViewById(R.id.expense_screen_title);
        mDateBtn = findViewById(R.id.expense_screen_date);
        mNoticeBtn = findViewById(R.id.expense_screen_notice);
        mAccountBtn = findViewById(R.id.expense_screen_account);
        mSaveBtn = findViewById(R.id.expense_screen_create_booking);

        resolveIntent();
    }

    private void resolveIntent() {
        BundleUtils bundle = new BundleUtils(getIntent().getExtras());

        switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE_BOOKING)) {
            case INTENT_MODE_UPDATE_PARENT:
            case INTENT_MODE_UPDATE_CHILD:
                mSaveBtn.setText(getString(R.string.update));
                mExpense = (ExpenseObject) bundle.getParcelable(INTENT_BOOKING, null);

                break;
            case INTENT_MODE_ADD_CHILD:
                mParentBooking = (ExpenseObject) bundle.getParcelable(INTENT_BOOKING, null);
            case INTENT_MODE_CREATE_BOOKING:
                mSaveBtn.setText(getString(R.string.btn_save));
                mExpense = new ExpenseObject(
                        getString(R.string.no_name),
                        0D,
                        true,
                        Category.createDummyCategory(),
                        userPreferences.getActiveAccount().getIndex(),
                        userPreferences.getMainCurrency()
                );

                break;
            default:
                throw new UnsupportedOperationException("Could not handle intent mode " + bundle.getString(INTENT_MODE, null));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        setExpenseType(mExpense.isExpenditure());
        mExpenseTypeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.expense_screen_radio_expense) {

                    mExpense.setExpenditure(true);
                    Log.d(TAG, "set expense type to " + mExpense.isExpenditure());
                } else {

                    mExpense.setExpenditure(false);
                    Log.d(TAG, "set expense type to " + mExpense.isExpenditure());
                }
            }
        });

        mCurrencySymbolTxt.setText(mExpense.getCurrency().getSymbol());

        try {
            Account account = mAccountRepo.get(mExpense.getAccountId());
            mAccountBtn.setText(account.getTitle());

        } catch (AccountNotFoundException e) {

            //Kann das Konto nicht gefunden werden, wird der Button hint angezeigt
        }

        mCategoryBtn.setHint(mExpense.getCategory().getTitle());

        mDateBtn.setText(mExpense.getDisplayableDateTime());

        mTitleBtn.setHint(mExpense.getTitle());

        mNoticeBtn.setHint(mExpense.getNotice());

        mPriceTxt.setHint(String.format(getResources().getConfiguration().locale, "%.2f", mExpense.getUnsignedPrice()));
        mPriceTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString(PriceInputDialog.TITLE, getString(R.string.input_price));

                PriceInputDialog priceDialog = new PriceInputDialog();
                priceDialog.setArguments(bundle);
                priceDialog.setOnPriceSelectedListener(new PriceInputDialog.OnPriceSelected() {
                    @Override
                    public void onPriceSelected(double price) {

                        setPrice(price);
                    }
                });
                priceDialog.show(getFragmentManager(), "expense_screen_price");
            }
        });

        mSaveBtn.setOnClickListener(createBookingClickListener);
    }

    /**
     * OnClickListener um eine Buchung zu erstellen oder um sie zu updaten.
     */
    private View.OnClickListener createBookingClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            //todo buchungen in der Zukunft sollen als geplante buchung eingefügt werden
            //wenn eine Buchung in der Zukunft erstellt wurde soll ein alert dialog den user darauf hinweisen,
            //dass die Buchung als zukünftige Buchung erstellt wurde
            //AlertDialog text: "Du hast eine Buchung in der Zukunft erstellt. Diese wird dann zum entsprechenden Tag in deine Historie eingefügt"
            if (!mExpense.isSet()) {

                Toast.makeText(ExpenseScreen.this, R.string.error_create_expense_content_missing, Toast.LENGTH_SHORT).show();
                return;
            }

            BundleUtils bundle = new BundleUtils(getIntent().getExtras());

            switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE_BOOKING)) {
                case INTENT_MODE_UPDATE_PARENT:

                    try {
                        mBookingRepo.update(mExpense);
                        Toast.makeText(ExpenseScreen.this, "Updated Booking " + mExpense.getTitle(), Toast.LENGTH_SHORT).show();
                    } catch (ExpenseNotFoundException e) {

                        Toast.makeText(ExpenseScreen.this, "Buchung konnte nicht geupdated werden", Toast.LENGTH_SHORT).show();
                        //todo fehlerbehandlung
                        //todo übersetzung
                    }
                    break;
                case INTENT_MODE_UPDATE_CHILD:

                    try {
                        mChildExpenseRepo.update(mExpense);
                        Toast.makeText(ExpenseScreen.this, "Updated Booking " + mExpense.getTitle(), Toast.LENGTH_SHORT).show();
                    } catch (ChildExpenseNotFoundException e) {

                        Toast.makeText(ExpenseScreen.this, "KindBuchung konnte nicht geupdated werden", Toast.LENGTH_SHORT).show();
                        //todo fehlerbehandlung
                        //todo übersetzung
                    }
                    break;
                case INTENT_MODE_ADD_CHILD:

                    try {
                        mChildExpenseRepo.addChildToBooking(mExpense, mParentBooking);
                        Toast.makeText(ExpenseScreen.this, "Added Booking \"" + mExpense.getTitle() + "\" to parent Booking " + mParentBooking.getTitle(), Toast.LENGTH_SHORT).show();
                    } catch (AddChildToChildException e) {

                        Log.e(TAG, "Could not add Child " + mExpense.getTitle() + " to parent " + mParentBooking.getTitle(), e);
                        //todo ferhlerbehandlung
                    }
                    break;
                case INTENT_MODE_CREATE_BOOKING:

                    mExpense = mBookingRepo.insert(mExpense);
                    Toast.makeText(ExpenseScreen.this, "Created Booking \"" + mExpense.getTitle() + "\"", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new UnsupportedOperationException(
                            "ExpenseScreen unterstützt keine anderen Methoden als createExpense, createChildToExpense, updateExpense und updateChildExpense. Modus: "
                                    + bundle.getString(INTENT_MODE, INTENT_MODE_CREATE_BOOKING)
                    );
            }

            Intent intent = new Intent(ExpenseScreen.this, ParentActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expense_screen_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.template:
                Intent chooseTemplateIntent = new Intent(ExpenseScreen.this, TemplatesActivity.class);
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

    /**
     * Methode die aufgerufen wird, wenn der user auf ein TextInput feld klickt.
     * Je nachdem welches Feld angeklickt wird muss ein anderer AlertDialog aufgerufen werden.
     *
     * @param view View
     */
    public void expensePopUp(View view) {

        Bundle bundle = new Bundle();
        Button btn = findViewById(view.getId());
        BasicTextInputDialog basicDialog = new BasicTextInputDialog();

        switch (btn.getId()) {

            case R.id.expense_screen_category:

                Intent chooseCategoryIntent = new Intent(ExpenseScreen.this, CategoryListActivity.class);
                ExpenseScreen.this.startActivityForResult(chooseCategoryIntent, 1);
                break;

            case R.id.expense_screen_title:

                bundle.putString(BasicTextInputDialog.TITLE, getResources().getString(R.string.input_title));

                basicDialog.setArguments(bundle);
                basicDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {

                    @Override
                    public void onTextInput(String textInput) {

                        setTitle(textInput);
                    }
                });
                basicDialog.show(getFragmentManager(), "expense_screen_title");
                break;

            case R.id.expense_screen_date:

                DatePickerDialog datePicker = new DatePickerDialog();
                datePicker.setOnDateSelectedListener(new DatePickerDialog.OnDateSelected() {
                    @Override
                    public void onDateSelected(Calendar date) {

                        setDate(date);
                    }
                });
                datePicker.show(getFragmentManager(), "expense_screen_date");
                break;

            case R.id.expense_screen_notice:

                bundle.putString(BasicTextInputDialog.TITLE, getResources().getString(R.string.input_notice));

                basicDialog.setArguments(bundle);
                basicDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {

                    @Override
                    public void onTextInput(String textInput) {

                        setNotice(textInput);
                    }
                });
                basicDialog.show(getFragmentManager(), "expense_screen_notice");
                break;

            case R.id.expense_screen_account:

                SingleChoiceDialog<Account> accountPicker = new SingleChoiceDialog<>();
                accountPicker.createBuilder(ExpenseScreen.this);
                accountPicker.setTitle(getString(R.string.input_account));
                accountPicker.setContent(mAccountRepo.getAll(), (int) mExpense.getAccountId());
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
                break;
        }
    }


    /**
     * Methode um die Daten die von dieser Activity aufgerufende Activities zurückgegeben werden weiter zu verarbeiten.
     *
     * @param requestCode der Anfrage Code der beim aufrufen der 2. Activity erstellt wird, siehe case: R.id.expense_screen_category
     * @param resultCode  der Code der von der Aufgerufenden Kategorie zurückgegeben wird, um den status der daten zu signalisieren
     * @param data        die Daten die von der aufgerufenden KAtegorie zurückgegebn werden
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case 1://Category response
                Category category = data.getParcelableExtra("categoryObj");
                setCategory(category);

                break;
            case 2://Template response
                ExpenseObject templateBooking = data.getParcelableExtra("templateObj");
                showExpenseOnScreen(templateBooking);

                break;
            default:
                throw new UnsupportedOperationException("Die Antwort aus mit dem response code: " + requestCode + " kann nicht verarbeitet werden");
        }
    }

    /**
     * Methode um die Felder des ExpenseScreens mit den Daten einer Buchung auszufüllen.
     *
     * @param expense Anzuzeigende Ausgabe
     */
    private void showExpenseOnScreen(ExpenseObject expense) {
        //CLEANUP Sollte diese funktion nicht nur die hints setzen?
        setPrice(expense.getUnsignedPrice());
        setExpenseType(expense.isExpenditure());
        setCategory(expense.getCategory());
        setTitle(expense.getTitle());
        //funktion setzt das Datum der Buchung nicht neu, da bei einer Buchung immer das aktuellst Datum genommen werden sollte. Außer wenn es explizit vom user geändert wird.
        setNotice(expense.getNotice());
        try {
            Account account = mAccountRepo.get(expense.getAccountId());
            setAccount(account);

        } catch (AccountNotFoundException e) {

            //Wird das Konto nicht gefunden, wird der Button hint angezeigt.
        }
        setExpenseCurrency();
    }

    /**
     * Methode, welche den angezeigten Preis und den Preis der zu speichernden Ausgabe anpasst.
     *
     * @param price Preis
     */
    private void setPrice(double price) {
        mExpense.setPrice(price);
        mPriceTxt.setText(String.format("%s", mExpense.getUnsignedPrice()));
    }

    /**
     * Methode, welche den angezeigten Ausgabentyp und den Ausgabentype der zu speichernden Ausgabe anpasst.
     *
     * @param expenseType Boolean
     */
    private void setExpenseType(boolean expenseType) {
        mExpense.setExpenditure(expenseType);
        mExpenseTypeRadio.check(mExpense.isExpenditure()
                ? R.id.expense_screen_radio_expense
                : R.id.expense_screen_radio_income
        );
    }

    /**
     * Methode, welche die angezeigte Kategorie und die Kategorie der zu speichernden Ausgabe anpasst.
     *
     * @param category Kategorie
     */
    private void setCategory(Category category) {
        mExpense.setCategory(category);
        mCategoryBtn.setText(mExpense.getCategory().getTitle());
        mExpenseTypeRadio.check(mExpense.getCategory().getDefaultExpenseType()
                ? R.id.expense_screen_radio_expense
                : R.id.expense_screen_radio_income
        );
    }

    /**
     * Method, welche den angezeigten Titel und den Titel der zu speichernden Ausgabe anpasst.
     *
     * @param title Titel
     */
    private void setTitle(String title) {
        mExpense.setTitle(title);
        mTitleBtn.setText(mExpense.getTitle());
    }

    /**
     * Nethode, welche das angezeigte Datum und das Datum der zu speichernden Ausgabe anpasst.
     *
     * @param date Datum
     */
    private void setDate(Calendar date) {
        mExpense.setDateTime(date);
        mDateBtn.setText(mExpense.getDisplayableDateTime());
    }

    /**
     * Methode, welche die angezeigte Notiz und die Notiz der zu speichernden Ausgabe anpasst
     *
     * @param notice Notiz
     */
    private void setNotice(String notice) {
        mExpense.setNotice(notice);
        mNoticeBtn.setText(mExpense.getNotice());
    }

    /**
     * Methode, welche das angezeigte Konto und das Konto der zu speichernden Ausgabe anpasst.
     *
     * @param account Konto
     */
    private void setAccount(Account account) {
        mExpense.setAccountId(account.getIndex());
        mAccountBtn.setText(account.getTitle());
    }

    /**
     * Methode, welche die angezeigte Währung und die Währung der zu speichernden Ausgabe anpasst.
     */
    private void setExpenseCurrency() {
        mExpense.setCurrency(userPreferences.getMainCurrency());
        mCurrencySymbolTxt.setText(mExpense.getCurrency().getSymbol());
    }
}
