package com.example.lucas.haushaltsmanager.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.lucas.haushaltsmanager.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.AccountPickerDialogFragment;
import com.example.lucas.haushaltsmanager.Dialogs.DirectoryPickerDialogFragment;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class ImportExportActivity extends AppCompatActivity implements AccountPickerDialogFragment.OnAccountSelected, BasicTextInputDialog.BasicDialogCommunicator, DirectoryPickerDialogFragment.OnDirectorySelected {

    String TAG = ImportExportActivity.class.getSimpleName();

    Button mAccountBtn, mFileNameBtn, mDirectoryBtn, mFromDateBtn, mUntilDateBtn, mCreateExportBtn, mImportBtn;
    CheckBox mExpenseChk, mIncomeChk, mSetStartDateChk, mSetEndDateChk;
    ArrayList<ExpenseObject> mExpenses;
    ExpensesDataSource mDatabase;
    Account mChosenAccount;
    Calendar mCalendar;
    Bundle mBundle;

    File mExportsDirectory;
    String mFileName;
    final String mFileExtension = ".csv";


    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mCalendar = Calendar.getInstance();
        mBundle = new Bundle();

        mExportsDirectory = new File(getFilesDir().toString() + "/Exports");
        if (!mExportsDirectory.exists())
            mExportsDirectory.mkdir();

        mFileName = "Export_" + mCalendar.get(Calendar.YEAR) + "_" + mCalendar.get(Calendar.DAY_OF_MONTH) + "_" + (mCalendar.get(Calendar.MONTH) + 1);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        mAccountBtn = (Button) findViewById(R.id.import_export_account_btn);
        mFileNameBtn = (Button) findViewById(R.id.import_export_file_name_btn);
        mDirectoryBtn = (Button) findViewById(R.id.import_export_directory_btn);
        mFromDateBtn = (Button) findViewById(R.id.import_export_from_btn);
        mUntilDateBtn = (Button) findViewById(R.id.import_export_until_btn);
        mCreateExportBtn = (Button) findViewById(R.id.import_export_create_export_btn);
        mImportBtn = (Button) findViewById(R.id.import_export_import_btn);

        mExpenseChk = (CheckBox) findViewById(R.id.import_export_expense_chk);
        mIncomeChk = (CheckBox) findViewById(R.id.import_export_income_chk);
        mSetStartDateChk = (CheckBox) findViewById(R.id.import_export_from_chk);
        mSetEndDateChk = (CheckBox) findViewById(R.id.import_export_until_chk);

        mAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mBundle.clear();
                mBundle.putString("title", getResources().getString(R.string.choose_account));

                AccountPickerDialogFragment accountPicker = new AccountPickerDialogFragment();
                accountPicker.setArguments(mBundle);
                accountPicker.show(getFragmentManager(), "choose_account");
            }
        });

        mFileNameBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mBundle.clear();
                mBundle.putString("title", getResources().getString(R.string.choose_new_file_name));

                BasicTextInputDialog dialog = new BasicTextInputDialog();
                dialog.setArguments(mBundle);
                dialog.show(getFragmentManager(), "choose_file_name");

            }
        });

        mDirectoryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mBundle.clear();
                mBundle.putString("title", getResources().getString(R.string.choose_directory));

                DirectoryPickerDialogFragment directoryPicker = new DirectoryPickerDialogFragment();
                directoryPicker.setArguments(mBundle);
                directoryPicker.show(getFragmentManager(), "choose_export_directory");
            }
        });

        mCreateExportBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                createFile(false);
            }
        });

        mImportBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mBundle.clear();
                mBundle.putString("title", getResources().getString(R.string.choose_directory));

                DirectoryPickerDialogFragment importFilePicker = new DirectoryPickerDialogFragment();
                importFilePicker.setArguments(mBundle);
                importFilePicker.show(getFragmentManager(), "choose_import_directory");
            }
        });
    }

    /**
     * Methode die überprüft ob der angegebene Dateiname keine Probleme macht
     *
     * @param fileName Dateinname
     * @return boolean
     */
    private boolean isFileNameValid(String fileName) {

        File file = new File(String.format("%s/%s%s", mExportsDirectory.toString(), fileName, mFileExtension));
        return !file.exists();
    }

    /**
     * Methode die überprüft ob alle nötigen Informationen gegeben sind, um ein ExpenseObject zu erstellen
     * und ob die Datei eine Csv Datei ist.
     *
     * @return status der Datei
     */
    private boolean isDataFromFileValid(File file) {

        //gucke ob die datei eine von mir erstellte datei ist
        //  -> nein: andere formate werden noch nicht unterstützt

        if (file.isDirectory())
            return false;

        if (!file.getName().contains(mFileExtension))
            return false;

        //todo checke die erste zeile der Datei nach den nötigen Informationen
        return true;
    }

    /**
     * Methode um einen AlertDialog zu erstellen, um den User auf einen Dateinamenskonflikt hinzuweisen.
     */
    private void createErrorAlertDialog(@StringRes int errorMessage) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(R.string.error);

        dialog.setMessage(errorMessage);

        dialog.setPositiveButton(R.string.overwrite_existing_file, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                createFile(true);
            }
        });

        dialog.setNegativeButton(R.string.abort_file_writing, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //do nothing
            }
        });

        dialog.setNeutralButton(R.string.choose_new_file_name, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mBundle.clear();
                mBundle.putString("title", getResources().getString(R.string.input_file_name));

                BasicTextInputDialog dialog1 = new BasicTextInputDialog();
                dialog1.setArguments(mBundle);
                dialog1.show(getFragmentManager(), "choose_file_name");
            }
        });

        dialog.create().show();
    }

    /**
     * Methode um die Daten aus einer Datei (eigenes oder fremdes Format) in die App Datenbankstruktur zu importieren.
     *
     * @param file Pfad zu der zu importierenden Datei
     */
    private void importDataFromCsvFile(File file) {

        throw new UnsupportedOperationException("Importing Data is not supported!");
/*
        ArrayList<ExpenseObject> importetBookingData = new ArrayList<>();

        if (isDataFromFileValid(file)) {

            //öffne die datei

            //lese die erste zeile aus

            //speicher die elemente der ersten zeile (datenbankspaltennamen) in einer variablen

            //gehe durch jede zeile der datei
            for(String rowEntry : String FILECONTENTS) {

                //wandle die raw daten in ExpenseObjects um mithilfe von stringToExpenseObject()
                //speichere diese ExpenseObjects in importedBookingData
                importedBookingData.add(stringToExpense(rowEntry));
            }

            //schreibe die ExpenseObjects aus importedBookingData in die Datenbank (createBookings)
            mDatabase.createBookings(importedBookingData);

            Toast.makeText(this, "Imported the data successfully!", Toast.LENGTH_SHORT).show();
        }
*/
    }//todo

    /**
     * Methode um einen String mit ExpenseObject daten in ein ExpenseObject zu transformieren
     *
     * @param expenseString string expense data
     * @return Umgewandeltes ExpenseObject
     *///sollte ich dieser funktion auch noch die überschriften (erste zeile aus der Datei) mit übergeben?
    private ExpenseObject stringToExpense(String expenseString) {

        throw new UnsupportedOperationException("Transforming a string to an ExpenseObject is not supported!");
/*
        ExpenseObject expense = ExpenseObject.createDummyExpense(this);

        return expense;
*/
    }//todo

    /**
     * Methode um die Buchungen in eine Datei zu schreiben und diese Datei dann zu speichern
     * Anleitung: https://stackoverflow.com/a/30074268/9376633
     */
    private void createFile(boolean bypassValidation) {

        if (!bypassValidation || !isFileNameValid(mFileName)) {

            createErrorAlertDialog(R.string.file_already_existing_err_msg);
            return;
        }


        mExpenses = mDatabase.getBookings();

        //zeige via ladezeiten an wie lange die operation noch dauert
        try {

            PrintWriter printWriter = new PrintWriter(new File(String.format("%s/%s%s", mExportsDirectory.toString(), mFileName, mFileExtension)));
            StringBuilder expensesString = new StringBuilder();

            //muss ich noch ein is_parent feld mit einfügen um die Kinder von ExpenseObjects später wieder zuzuordnen??
            //muss ich auch noch ein parent_id feld einfügen um sicherzustellen dass kinder dem richtigen parent zugewiesen werden??
            expensesString.append("_id").append(",");
            expensesString.append("booking_id").append(",");
            expensesString.append("price").append(",");
            expensesString.append("expenditure").append(",");
            expensesString.append("title").append(",");
            expensesString.append("date").append(",");
            expensesString.append("notice").append(",");
            expensesString.append("exchange_rate").append(",");
            expensesString.append("category_id").append(",");
            expensesString.append("cat_name").append(",");
            expensesString.append("color").append(",");
            expensesString.append("expense_type").append(",");
            expensesString.append("account_id").append(",");
            expensesString.append("acc_name").append(",");
            expensesString.append("balance").append(",");
            expensesString.append("currency_id").append(",");
            expensesString.append("cur_name").append(",");
            expensesString.append("short_name").append(",");
            expensesString.append("symbol").append(",");
            expensesString.append("currency_id").append("\n");

            for (ExpenseObject expense : mExpenses) {

                expensesString.append(expenseObjectToString(expense));
            }

            printWriter.write(expensesString.toString());
            printWriter.close();

            Log.d(TAG, "Exported File to location: " + mExportsDirectory.toString());
            //erstelle Toast bei erfolgreichem speichern mit message "Exported Data Succesfully"

        } catch (FileNotFoundException e) {

            Log.d(TAG, "Error while creating the csv export file: " + e.toString());
        }
    }

    /**
     * Methode um ein ExpenseObject und alle seine Kinder in ein String zu transformieren
     *
     * @param expense ExpenseObject das umgewandelt werden soll
     * @return ExpenseObject mit allen Kindern als String
     */
    private StringBuilder expenseObjectToString(ExpenseObject expense) {

        StringBuilder expenseString = new StringBuilder();

        expenseString.append(expense.getIndex()).append(",");
        expenseString.append(expense.getUnsignedPrice()).append(",");
        expenseString.append(expense.getExpenditure()).append(",");
        expenseString.append(expense.getTitle()).append(",");
        expenseString.append(expense.getDate()).append(",");
        expenseString.append(expense.getNotice()).append(",");
        expenseString.append(expense.getExchangeRate()).append(",");
        expenseString.append(expense.getCategory().getIndex()).append(",");
        expenseString.append(expense.getCategory().getCategoryName()).append(",");
        expenseString.append(expense.getCategory().getColor()).append(",");
        expenseString.append(expense.getCategory().getDefaultExpenseType()).append(",");
        expenseString.append(expense.getAccount().getIndex()).append(",");
        expenseString.append(expense.getAccount().getAccountName()).append(",");
        expenseString.append(expense.getAccount().getBalance()).append(",");
        expenseString.append(expense.getExpenseCurrency().getIndex()).append(",");
        expenseString.append(expense.getExpenseCurrency().getCurrencyName()).append(",");
        expenseString.append(expense.getExpenseCurrency().getCurrencyShortName()).append(",");
        expenseString.append(expense.getExpenseCurrency().getCurrencySymbol()).append(",");
        expenseString.append(expense.getExchangeRate()).append("\n");

        for (ExpenseObject expenseChild : expense.getChildren()) {

            expenseString.append(expenseObjectToString(expenseChild));
        }

        return expenseString;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mDatabase.isOpen())
            mDatabase.close();
    }

    @Override
    public void onAccountSelected(Account account, String tag) {

        if (tag.equals("choose_account")) {

            mChosenAccount = account;
            mAccountBtn.setText(mChosenAccount.getAccountName());
        }
    }

    @Override
    public void onTextInput(String fileName, String tag) {

        if (tag.equals("choose_file_name")) {

            if (isFileNameValid(fileName)) {

                mFileName = fileName;
                mFileNameBtn.setText(mFileName);
            } else {

                createErrorAlertDialog(R.string.file_already_existing_err_msg);
            }
        }
    }

    @Override
    public void onDirectorySelected(File file, String tag) {

        switch (tag) {

            case "choose_export_directory":

                mExportsDirectory = file;
                break;
            case "choose_import_directory":

                importDataFromCsvFile(file);
                break;
        }
    }
}
