package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.StringRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Klasse um eine Liste von ExpenseObjects in einem CSV Datei zu schreiben und dieses dann abzuspeichern.
 */
public class ExpenseObjectExporter {

    /**
     * Verzeichnis in dem die Exportierte Datei gespeichert werden soll
     */
    private File mDirectory;

    /**
     * Liste aller Konten
     */
    private List<Account> mAccounts;

    /**
     * Constructor
     *
     * @param directory Verzeichniss in dem gespeichert werden soll.
     */
    public ExpenseObjectExporter(File directory, Context context) {

        assertDirectory(directory);
        mDirectory = directory;
        initializeAccountList(context);
    }

    /**
     * Methode die eine Liste von Buchungen nimmt und diese in eine Datei schreibt.
     *
     * @param expenses Buchungen die in eine Datei geschrieben werden sollen.
     * @return Die erstellte Datei wird zurückgegeben. Falls die Datei nicht erstellt werden konnte wird NULL zurückgegeben.
     */
    public File convertAndExportExpenses(List<ExpenseObject> expenses) {
        File file = createFile(mDirectory);

        if (file != null && writeExpensesToFile(expenses, file)) {
            return file;
        }

        return null;
    }

    /**
     * Methode um zu überprüfen ob das angegebene Verzeichniss auch wirklich ein Verzeichniss ist.
     * Falls nicht wird eine Exception ausgelöst.
     *
     * @param directory Verzeichniss das üerprüft werden soll.
     */
    private void assertDirectory(File directory) throws IllegalArgumentException {
        if (directory != null && !directory.isDirectory())
            throw new IllegalArgumentException("The given object is not a Directory!");
    }

    /**
     * Methode um eine Liste von Buchungen in eine Komma seperierten String zu konvertieren.
     *
     * @param expenses Umzuwandelnde Buchungen.
     * @param file     Datei in der die Buchungen gespeichert werden sollen.
     * @return True wenn die Buchungen erfolgreich in die Datei geschrieben werden konnten, False wenn nicht.
     */
    private boolean writeExpensesToFile(List<ExpenseObject> expenses, File file) {
        FileOutputStream fileOutput = null;

        try {

            fileOutput = new FileOutputStream(file);

            fileOutput.write(getCsvHeader().getBytes());
            for (ExpenseObject expense : expenses) {

                fileOutput.write(expenseToString(expense).getBytes());
            }
            fileOutput.close();
            return true;
        } catch (Exception e) {

            return false;
        } finally {
            if (fileOutput != null) {
                try {
                    fileOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Methode um den String header der CSV Datei zu bekommen.
     *
     * @return StringHeader
     */
    private String getCsvHeader() {
        return getStringResource(R.string.export_price) + "," +
                getStringResource(R.string.export_expenditure) + "," +
                getStringResource(R.string.export_title) + "," +
                getStringResource(R.string.export_date) + "," +
                getStringResource(R.string.export_notice) + "," +
                getStringResource(R.string.export_currency_name) + "," +
                getStringResource(R.string.export_category_name) + "," +
                getStringResource(R.string.export_account_name) + "," + "\r\n";
    }

    private String getStringResource(@StringRes int id) {
        return app.getContext().getString(id);
    }

    /**
     * Methode um ein ExpenseObject und alle seine Kinder in ein String zu transformieren
     *
     * @param expense ExpenseObject das umgewandelt werden soll
     * @return ExpenseObject mit allen Kindern als String
     */
    private String expenseToString(ExpenseObject expense) {
        StringBuilder expenseString = new StringBuilder();

        expenseString.append(expense.getUnsignedPrice()).append(",");
        expenseString.append(expense.isExpenditure()).append(",");
        expenseString.append(expense.getTitle()).append(",");
        expenseString.append(expense.getDateString()).append(",");
        expenseString.append(expense.getNotice()).append(",");
        expenseString.append(expense.getCurrency().getName());
        expenseString.append(expense.getCategory().getTitle()).append(",");
        Account account = getAccount(expense.getAccountId());
        expenseString.append(account != null ? account.getTitle() : "").append("\r\n");

        for (ExpenseObject child : expense.getChildren()) {

            expenseString.append(expenseToString(child));
        }

        return expenseString.toString();
    }

    /**
     * Methode um eine Neue Datei im angegebenen Verzeichniss anzulegen.
     *
     * @param directory Verzeichniss in dem die Datei angelegt werden soll.
     * @return Angelegte Datei oder null, falls die Datei schon existieren sollte.
     */
    private File createFile(File directory) {
        if (hasEnoughFreeSpace() && isMediumAvailable() && hasWritePermission()) {
            File file = new File(directory.getAbsolutePath() + "/" + createFileName());

            try {
                if (file.createNewFile())
                    return file;
            } catch (IOException e) {

                return null;
            }
        }

        return null;
    }

    /**
     * Methode um zu überprüfen ob auf dem Medium noch genug freier Speicherplatz zu verfügung steht.
     * Dabei müssen noch mehr als 10% des Totalen Speicherplatzes zur verfügung stehen.
     * (Android Doc.: https://developer.android.com/training/data-storage/files#GetFreeSpace)
     *
     * @return True wenn dem so ist, False wenn nicht.
     */
    private boolean hasEnoughFreeSpace() {
        long totalStorageSpace = mDirectory.getTotalSpace();
        long freeStorageSpace = mDirectory.getFreeSpace();

        return freeStorageSpace / totalStorageSpace <= 0.9;

    }

    /**
     * Methode um zu überprüfen ob der angegebene Speicherplatz zur Verfügung steht.
     *
     * @return True wenn es erreichbar ist, False wenn nicht.
     */
    private boolean isMediumAvailable() {
        String externalStoragePath = Environment.getExternalStorageDirectory().getPath();

        if (mDirectory.getPath().contains(externalStoragePath))
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        else
            return true;
    }

    /**
     * Methode um zu überprüfen ob der User Schreibberechtigung für das angegebene Verzeichniss hat.
     *
     * @return True bei einer Schreibberechtigung, False bei keiner
     */
    private boolean hasWritePermission() {

        return mDirectory.canWrite();
    }

    /**
     * Methode um eine Dateinamen zu erstellen, welcher auf der aktuellen Zeit basiert.
     *
     * @return Dateiname
     */
    private String createFileName() {
        String prefix = "Export_";
        String suffix = ".csv";

        Calendar date = Calendar.getInstance();
        String fileName = date.get(Calendar.DAY_OF_MONTH) + "_"
                + date.get(Calendar.MONTH) + "_"
                + date.get(Calendar.YEAR) + "_"
                + date.get(Calendar.HOUR_OF_DAY) + ":"
                + date.get(Calendar.MINUTE) + ":"
                + date.get(Calendar.SECOND);

        return prefix.concat(fileName).concat(suffix);
    }

    /**
     * Methode um das Konto zu einer Konto id zu bekommen.
     *
     * @param accountId Id des Kontos
     * @return Konto mit der angegebenen Id
     */
    private Account getAccount(long accountId) {
        for (Account account : mAccounts) {
            if (account.getIndex() == accountId) {
                return account;
            }
        }

        return null;
    }

    /**
     * Methode alle Konten in einer lokalen Liste zu speichern
     */
    private void initializeAccountList(Context context) {
        mAccounts = new AccountRepository(context).getAll();
    }
}
