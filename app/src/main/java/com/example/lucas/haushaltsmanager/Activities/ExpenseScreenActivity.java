package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.MainTab.TabParentActivity;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.AccountPickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.FrequencyAlertDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseScreenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AccountPickerDialog.OnAccountSelected, BasicTextInputDialog.BasicDialogCommunicator, PriceInputDialog.OnPriceSelected, com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog.OnDateSelected, FrequencyAlertDialog.OnFrequencySet {
    private static final String TAG = ExpenseScreenActivity.class.getSimpleName();

    private creationModes CREATION_MODE;

    private enum creationModes {
        CREATE_EXPENSE_MODE,
        ADD_CHILD_MODE,
        UPDATE_EXPENSE_MODE,
        UPDATE_CHILD_MODE
    }

    private ExpenseObject mExpense;
    private Calendar mCalendar = Calendar.getInstance();
    private Calendar mRecurringEndDate = Calendar.getInstance();
    private boolean mTemplate = false, mRecurring = false;
    public int frequency = 0;
    private Button mDateBtn, mAccountBtn, mSaveBtn, mCategoryBtn, mTitleBtn, mNoticeBtn, mRecurringEndBtn, mRecurringFrequency;
    private MultiAutoCompleteTextView mTagAutoCompTxt;
    private CheckBox mTemplateChk, mRecurringChk;
    private TextView mAmountTxt, mCurrencyTxt;
    private RadioGroup mExpenseTypeRadio;
    private ExpenseObject mParentBooking;
    private Toolbar mToolbar;
    private ImageButton mBackArrow;
    private List<Tag> mTags;

    private ExpensesDataSource mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_screen);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        //TODO implement the correct Toolbar functionality (back arrow, overflow menu which holds the load mTemplate button)
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        mAmountTxt = (TextView) findViewById(R.id.expense_screen_amount);
        mCurrencyTxt = (TextView) findViewById(R.id.expense_screen_amount_currency);
        mCategoryBtn = (Button) findViewById(R.id.expense_screen_category);
        mExpenseTypeRadio = (RadioGroup) findViewById(R.id.expense_screen_expense_type);
        mTitleBtn = (Button) findViewById(R.id.expense_screen_title);
        mTagAutoCompTxt = (MultiAutoCompleteTextView) findViewById(R.id.expense_screen_tag);
        mDateBtn = (Button) findViewById(R.id.expense_screen_date);
        mNoticeBtn = (Button) findViewById(R.id.expense_screen_notice);
        mAccountBtn = (Button) findViewById(R.id.expense_screen_account);
        mSaveBtn = (Button) findViewById(R.id.expense_screen_create_booking);

        mTemplateChk = (CheckBox) findViewById(R.id.expense_screen_template);
        mRecurringChk = (CheckBox) findViewById(R.id.expense_screen_recurring);

        resolveIntent(getIntent().getExtras());

        //ab hier wird der CurrencySelector erstellt

        Spinner currencySelector = (Spinner) findViewById(R.id.expense_screen_select_currency);

        currencySelector.setOnItemSelectedListener(this);

        ArrayList<Currency> currencies = mDatabase.getAllCurrencies();

        List<String> currencyShortNames = new ArrayList<>();
        for (Currency currency : currencies) {

            currencyShortNames.add(currency.getShortName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyShortNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        currencySelector.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        currencySelector.setSelection(((int) preferences.getLong("mainCurrencyIndex", 32)) - 1);
    }

    /**
     * Methode um herauszufinden mit welcher Intention (createChild, createBooking, updateChild, updateBooking)
     * der ExpenseScreen aufgerufen wurde
     */
    public void resolveIntent(Bundle bundle) {
        if (bundle == null)
            return;
        else if (!bundle.containsKey("mode"))
            throw new UnsupportedOperationException("Du musst den Modus setzen!");

        if ("updateChild".equals(bundle.getString("mode"))) {

            mSaveBtn.setText(getString(R.string.update));

            CREATION_MODE = creationModes.UPDATE_CHILD_MODE;
            mExpense = getIntent().getParcelableExtra("updateChildExpense");
            Log.d(TAG, "resolveIntent: Updating Child Expense " + mExpense.toString());
            return;
        }

        if ("updateParent".equals(bundle.getString("mode"))) {

            mSaveBtn.setText(getString(R.string.update));

            CREATION_MODE = creationModes.UPDATE_EXPENSE_MODE;
            mExpense = getIntent().getParcelableExtra("updateParentExpense");
            Log.d(TAG, "resolveIntent: Updating Parent Expense " + mExpense.toString());
            return;
        }

        if ("addChild".equals(bundle.getString("mode"))) {

            mSaveBtn.setText(getString(R.string.add_child_to_booking));

            CREATION_MODE = creationModes.ADD_CHILD_MODE;
            mParentBooking = getIntent().getParcelableExtra("parentBooking");
        }

        if ("createBooking".equals(bundle.getString("mode"))) {

            mSaveBtn.setText(getString(R.string.create_booking));
            CREATION_MODE = creationModes.CREATE_EXPENSE_MODE;
        }


        SharedPreferences preferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        Account account = mDatabase.getAccountById(preferences.getLong("activeAccount", 0));
        mExpense = ExpenseObject.createDummyExpense(this);
        mExpense.setAccount(account);
        mExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE);
        Log.d(TAG, "resolveIntent: Creating Expense " + mExpense.toString());
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

        if (mExpense.isExpenditure())
            mExpenseTypeRadio.check(R.id.expense_screen_radio_expense);
        else
            mExpenseTypeRadio.check(R.id.expense_screen_radio_income);

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

        mCurrencyTxt.setText(mExpense.getAccount().getCurrency().getSymbol());

        mAccountBtn.setText(mExpense.getAccount().getName());

        mCategoryBtn.setHint(R.string.choose_category);
        mCategoryBtn.setText(mExpense.getCategory().getName());

        mDateBtn.setText(mExpense.getDisplayableDateTime());

        mTitleBtn.setHint(R.string.input_title);
        mTitleBtn.setText(mExpense.getTitle());

        mTags = mDatabase.getAllTags();
        initializeAutComTxtView(mTags);
        for (Tag tag : mExpense.getTags())//kann ich einfach alle tags einer buchung nehmen oder gibt es fälle, in denen die liste noch nicht initialisiert ist?
            showTagInMultiView(tag);

        mNoticeBtn.setHint(R.string.input_notice);
        mNoticeBtn.setText(mExpense.getNotice());

        mAmountTxt.setText(String.format("%s", mExpense.getUnsignedPrice()));
        mAmountTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.input_price));

                PriceInputDialog priceDialog = new PriceInputDialog();
                priceDialog.setArguments(bundle);
                priceDialog.show(getFragmentManager(), "expense_screen_price");
            }
        });

        mTemplateChk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mTemplate = true;
                Toast.makeText(ExpenseScreenActivity.this, "Du möchtest die Ausgabe als Vorlage speichern", Toast.LENGTH_SHORT).show();
            }
        });

        mRecurringChk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //TODO ist kaputt

                ImageView imgFrequency = (ImageView) findViewById(R.id.img_frequency);
                mRecurringFrequency = (Button) findViewById(R.id.expense_screen_recurring_frequency);
                mRecurringFrequency.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putString("title", getString(R.string.input_frequency));

                        FrequencyAlertDialog frequencyDialog = new FrequencyAlertDialog();
                        frequencyDialog.setArguments(bundle);
                        frequencyDialog.show(getFragmentManager(), "expense_screen_frequency");
                    }
                });

                ImageView imgEnd = (ImageView) findViewById(R.id.img_end);
                mRecurringEndBtn = (Button) findViewById(R.id.expense_screen_recurring_end);
                mRecurringEndBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Bundle dateBundle = new Bundle();
                        dateBundle.putString("title", "");

                        DatePickerDialog datePicker = new DatePickerDialog();
                        datePicker.setArguments(dateBundle);
                        datePicker.show(getFragmentManager(), "expense_screen_recurring");
                    }
                });

                //setVisibility of icon and input field to visible
                if (mTemplate) {

                    imgFrequency.setVisibility(ImageView.VISIBLE);
                    mRecurringFrequency.setVisibility(Button.VISIBLE);

                    imgEnd.setVisibility(ImageView.VISIBLE);
                    mRecurringEndBtn.setVisibility(Button.VISIBLE);
                    mRecurring = true;
                } else {

                    imgFrequency.setVisibility(ImageView.GONE);
                    mRecurringFrequency.setVisibility(Button.GONE);

                    imgEnd.setVisibility(ImageView.GONE);
                    mRecurringEndBtn.setVisibility(Button.GONE);
                    mRecurring = false;
                }
            }
        });

        mSaveBtn.setOnClickListener(createBookingClickListener);
    }

    /**
     * Methode um die in der MultiAutoCompleteTextView gespeicherten Tags an die Buchung zu hängen
     */
    private void addTagsToBooking() {
        mExpense.removeTags();

        String input = mTagAutoCompTxt.getText().toString().replace(" ", "");
        String tags[] = input.split(",");
        for (String tag : tags) {

            Tag tagToAdd = new Tag(tag);
            for (Tag existingTag : mTags) {

                if (existingTag.getName().equals(tag)) {

                    tagToAdd = existingTag;
                    break;
                }
            }

            mExpense.addTag(tagToAdd);
        }
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

                Toast.makeText(ExpenseScreenActivity.this, R.string.error_create_expense_content_missing, Toast.LENGTH_SHORT).show();
                return;
            }

            addTagsToBooking();

            switch (CREATION_MODE) {

                case UPDATE_EXPENSE_MODE:

                    mDatabase.updateBooking(mExpense);
                    //todo falls der preis geupdatet wurde muss auch erneut in eine fremdwährung umgerechnet werden
                    Toast.makeText(ExpenseScreenActivity.this, "Updated Booking " + mExpense.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_CHILD_MODE:

                    mDatabase.updateChildBooking(mExpense);
                    //todo falls der preis geupdatet wurde muss auch erneut in eine fremdwährung umgerechnet werden
                    Toast.makeText(ExpenseScreenActivity.this, "Updated Booking " + mExpense.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
                case ADD_CHILD_MODE:

                    mDatabase.addChildToBooking(mExpense, mParentBooking);
                    mDatabase.insertConvertExpense(mExpense);
                    Toast.makeText(ExpenseScreenActivity.this, "Added Booking \"" + mExpense.getTitle() + "\" to parent Booking " + mParentBooking.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
                case CREATE_EXPENSE_MODE:

                    mExpense = mDatabase.createBooking(mExpense);
                    mDatabase.insertConvertExpense(mExpense);
                    Toast.makeText(ExpenseScreenActivity.this, "Created Booking \"" + mExpense.getTitle() + "\"", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new UnsupportedOperationException("ExpenseScreen unterstützt keine anderen Methoden als createExpense, createChildToExpense, updateExpense und updateChildExpense");
            }
/* todo recurring und template funktionalität noch einmal überarbeiten
            if (mRecurring) {

                // frequency is saved as duration in hours, mEndDate is saved as Calendar object
                long index = mDatabase.createRecurringBooking(mExpense.getIndex(), mCalendar.getTimeInMillis(), frequency, mRecurringEndDate.getTimeInMillis());
                Log.d(TAG, "created mRecurring booking event at index: " + index);
            }

            if (mTemplate) {

                long index = mDatabase.createTemplateBooking(mExpense.getIndex());
                Log.d(TAG, "created mTemplate for bookings at index: " + index);
            }
*/
            Intent intent = new Intent(ExpenseScreenActivity.this, TabParentActivity.class);
            ExpenseScreenActivity.this.startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        mDatabase.close();

        super.onDestroy();
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

                Intent chooseTemplateIntent = new Intent(ExpenseScreenActivity.this, TemplatesActivity.class);
                ExpenseScreenActivity.this.startActivityForResult(chooseTemplateIntent, 2);
                break;
            default:
                throw new UnsupportedOperationException("Du hast auf einen Menüpunkt geklickt, welcher nicht unterstützt wird");
        }

        return true;
    }

    /**
     * Methode um die MultiAutocompleteTextView mit der die Tags angezeigt werden zu initialisieren.
     *
     * @param tags Liste von tags aus denen der User auswählen kann
     */
    private void initializeAutComTxtView(List<Tag> tags) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getTagNames(tags));
        mTagAutoCompTxt.setAdapter(adapter);

        mTagAutoCompTxt.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mTagAutoCompTxt.setHint(R.string.hint_tag_input);
        mTagAutoCompTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String input = s.toString();
                if (input.endsWith(","))
                    removeLastCharacterFromInput();

                if (mTagAutoCompTxt.getText().toString().length() == 0)
                    return;

                String tags[] = input.split(",");
                String lastTag = tags[tags.length - 1];

                if (stringContainsText(lastTag) && lastTag.endsWith(" ")) {

                    if (tagNotExisting(lastTag)) {
                        appendTokenizer();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                //do nothing
            }
        });

        mTagAutoCompTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(v);
                    mTagAutoCompTxt.clearFocus();

                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Methode um das Keyboard zu verstecken.
     *
     * @param view View
     */
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Methode um zu überprüfen ob der einegegbene Tag bereits existiert.
     *
     * @param tagName Name des zu prüfenden Tags.
     * @return TRUE wenn der Tag bereits existiert, FALSE wenn nicht.
     */
    private boolean tagNotExisting(String tagName) {
        for (Tag tag : mTags) {
            if (tag.getName().equals(tagName))
                return false;
        }

        return true;
    }

    /**
     * Methode um aus einer Liste von Tags die Namen zu extrahieren
     *
     * @param tags Liste der Tags
     * @return Array der Tag Namen
     */
    private String[] getTagNames(List<Tag> tags) {
        String[] tagNames = new String[tags.size()];
        for (Tag tag : tags)
            tagNames[tags.indexOf(tag)] = tag.getName();

        return tagNames;
    }

    /**
     * Methode um einen zusätzlichen Separator einzufügen
     */
    private void appendTokenizer() {

        String userInput = mTagAutoCompTxt.getText().toString();
        userInput = removeLastCharacter(userInput);
        mTagAutoCompTxt.setText(String.format("%s, ", userInput));

        placeCursorAtPosition(mTagAutoCompTxt.getText().length());
    }

    /**
     * Methode um den letzten Character eines String zu entfernen.
     *
     * @param text String
     * @return String mit einem Character weniger
     */
    private String removeLastCharacter(String text) {
        return text.substring(0, text.length() - 1);
    }

    /**
     * Methode um den letzten Character des Userinputs zu löschen
     */
    private void removeLastCharacterFromInput() {

        String input = mTagAutoCompTxt.getText().toString();
        mTagAutoCompTxt.setText(removeLastCharacter(input));
        placeCursorAtPosition(input.length() - 1);
    }

    /**
     * Methode um den Cursor an die angegebene Position zu setzen.
     *
     * @param position Angezielte Position des Cursors
     */
    private void placeCursorAtPosition(int position) {
        mTagAutoCompTxt.setSelection(position);
    }

    /**
     * Methode um zu überprüfen ob in einem String Buchustaben stehen oder ob dieser leer ist.
     *
     * @param text Zu überprüfender Text
     * @return TRUE, wenn Buchstaben im string stehen, FALSE wenn nicht
     */
    private boolean stringContainsText(String text) {
        return text.trim().length() > 0;
    }

    /**
     * Methode um ein Tag in der MultiAutoCompleteTextView anzuzeigen.
     *
     * @param tag Tag, welches angezeigt werden soll
     */
    private void showTagInMultiView(Tag tag) {

        String input = mTagAutoCompTxt.getText().toString();
        mTagAutoCompTxt.setText(String.format("%s%s", input, tag.getName()));
        appendTokenizer();
    }

    /**
     * Methode die aufgerufen wird, wenn der user auf ein TextInput feld klickt.
     * Je nachdem welches Feld angeklickt wird muss ein anderer AlertDialog aufgerufen werden.
     *
     * @param view View
     */
    public void expensePopUp(View view) {

        Bundle bundle = new Bundle();
        Button btn = (Button) findViewById(view.getId());
        BasicTextInputDialog basicDialog = new BasicTextInputDialog();

        switch (btn.getId()) {

            case R.id.expense_screen_category:

                Intent chooseCategoryIntent = new Intent(ExpenseScreenActivity.this, CategoryListActivity.class);
                ExpenseScreenActivity.this.startActivityForResult(chooseCategoryIntent, 1);
                break;

            case R.id.expense_screen_title:

                bundle.putString("title", getResources().getString(R.string.input_title));

                basicDialog.setArguments(bundle);
                basicDialog.show(getFragmentManager(), "expense_screen_title");
                break;

            case R.id.expense_screen_date:

                bundle.putString("title", "");

                DatePickerDialog datePicker = new DatePickerDialog();
                datePicker.setArguments(bundle);
                datePicker.show(getFragmentManager(), "expense_screen_date");
                break;

            case R.id.expense_screen_notice:

                bundle.putString("title", getResources().getString(R.string.input_notice));

                basicDialog.setArguments(bundle);
                basicDialog.show(getFragmentManager(), "expense_screen_notice");
                break;

            case R.id.expense_screen_account:

                bundle.putString("title", getResources().getString(R.string.input_account));
                bundle.putParcelable("active_account", mExpense.getAccount());

                AccountPickerDialog accountPicker = new AccountPickerDialog();
                accountPicker.setArguments(bundle);
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

        switch (requestCode) {

            case 1://Category response

                if (resultCode == Activity.RESULT_OK) {

                    Category category = data.getParcelableExtra("categoryObj");
                    mExpense.setCategory(category);

                    mCategoryBtn.setText(category.getName());

                    if (category.getDefaultExpenseType())
                        mExpenseTypeRadio.check(R.id.expense_screen_radio_expense);
                    else
                        mExpenseTypeRadio.check(R.id.expense_screen_radio_income);
                }
                break;
            case 2://Template response

                if (resultCode == Activity.RESULT_OK) {

                    ExpenseObject templateBooking = data.getParcelableExtra("templateObj");
                    //todo setze alle felder in dem expense screen der template booking entsprechend
                }
                break;
            default:

                throw new UnsupportedOperationException("Die Antwort aus mit dem response code: " + requestCode + " kann nicht verarbeitet werden");
        }
    }

    /**
     * Callback Methode des Currency selectors
     *
     * @param parent   parent
     * @param view     view
     * @param position Die vom Nutzer ausgewählte position
     * @param id       id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String curName = parent.getItemAtPosition(position).toString();
        Currency currency = mDatabase.getCurrency(curName);

        mExpense.setExpenseCurrency(currency);
        mCurrencyTxt.setText(String.format("%s", mExpense.getExpenseCurrency().getSymbol()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    /**
     * Methode die den Callback des AccountPickerDialogFragments implementiert
     *
     * @param account Konto das ausgewählt wurde
     * @param tag     Tag mit dem das DialogFragment aufgerufen wurde
     */
    @Override
    public void onAccountSelected(Account account, String tag) {

        if (tag.equals("expense_screen_account")) {

            mExpense.setAccount(account);
            mAccountBtn.setText(mExpense.getAccount().getName());
            mCurrencyTxt.setText(mExpense.getAccount().getCurrency().getSymbol());

            Log.d(TAG, "set expense mAccount to: " + mExpense.getAccount().getName());
        }
    }

    /**
     * Methode die den Callback des BasicTextInputDialogs implementiert
     *
     * @param textInput Daten die vom user eigegeben wurden
     * @param tag       Dialog tag
     */
    @Override
    public void onTextInput(String textInput, String tag) {

        if (!textInput.isEmpty()) {

            switch (tag) {

                case "expense_screen_title":

                    mExpense.setTitle(textInput);
                    mTitleBtn.setText(mExpense.getTitle());
                    Log.d(TAG, "set expense title to " + mExpense.getTitle());
                    break;

                case "expense_screen_tag":

                    //todo tags sollten über eine AutocompleteTextView ausgewählt werden
                    break;

                case "expense_screen_notice":

                    mExpense.setNotice(textInput);
                    mNoticeBtn.setText(mExpense.getNotice());
                    Log.d(TAG, "set expense notice to " + mExpense.getNotice());
                    break;
            }
        }
    }

    /**
     * Methode die den Callback des PriceInputDialogs implementiert
     *
     * @param price Preis den der User einegeben hat
     * @param tag   Dialog tag
     */
    @Override
    public void onPriceSelected(double price, String tag) {

        if (tag.equals("expense_screen_price")) {

            mExpense.setPrice(price);
            mAmountTxt.setText(String.format("%s", mExpense.getUnsignedPrice()));

            Log.d(TAG, "set expense amount to " + mExpense.getUnsignedPrice());
        }
    }

    /**
     * Methode die den Callback des DatePickerDialogs implementiert
     *
     * @param date Dateum das der User ausgewählt hat
     * @param tag  Dialog tag
     */
    @Override
    public void onDateSelected(Calendar date, String tag) {

        if (tag.equals("expense_screen_recurring")) {

            mRecurringEndDate = date;
            mRecurringEndBtn.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(mRecurringEndDate.getTimeInMillis())));
            Log.d(TAG, "updated recurring end date to " + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(mRecurringEndDate.getTimeInMillis())));
        } else if (tag.equals("expense_screen_date")) {

            mExpense.setDateTime(date);
            mDateBtn.setText(mExpense.getDisplayableDateTime());
            Log.d(TAG, "updated expense date to " + mExpense.getDisplayableDateTime());
        }
    }

    @Override
    public void onFrequencySet(int frequencyInHours, String tag) {

        if (tag.equals("expense_screen_frequency")) {

            mRecurringFrequency.setText(String.format("%s Tage", frequencyInHours));
        }
    }
}
