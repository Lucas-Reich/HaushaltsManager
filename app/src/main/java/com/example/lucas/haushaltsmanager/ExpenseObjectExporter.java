package com.example.lucas.haushaltsmanager;

import android.os.Environment;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Klasse um eine Liste von ExpenseObjects in einem CSV Datei zu schreiben und dieses dann abzuspeichern.
 */
public class ExpenseObjectExporter {

    /**
     * Verzeichniss in dem die Exportierte Datei gespeichert werden soll
     */
    private File mDirectory;

    /**
     * Constructor
     *
     * @param directory Verzeichniss in dem gespeichert werden soll.
     */
    public ExpenseObjectExporter(File directory) {

        assertDirectory(directory);
        mDirectory = directory;
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
     * Methode die eine Liste von Buchungen nimmt und diese in eine Datei schreibt.
     *
     * @param expenses Buchungen die in eine Datei geschrieben werden sollen.
     * @return True bei Erfolg, False bei Misserfolg.
     */
    public boolean convertAndExportExpenses(ArrayList<ExpenseObject> expenses) {
        File file = createFile(mDirectory);

        return file != null && writeExpensesToFile(expenses, file);
    }

    /**
     * Methode um eine Liste von Buchungen in eine Komma seperierten String zu konvertieren.
     *
     * @param expenses Umzuwandelnde Buchungen.
     * @param file     Datei in der die Buchungen gespeichert werden sollen.
     * @return True wenn die Buchungen erfolgreich in die Datei geschrieben werden konnten, False wenn nicht.
     */
    private boolean writeExpensesToFile(ArrayList<ExpenseObject> expenses, File file) {
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

        return "price" + "," +
                "expenditure" + "," +
                "title" + "," +
                "date" + "," +
                //todo tags hinzufügen
                "notice" + "," +
                //todo exchange rate hinzufügen
                //todo Währung der Buchung hinzufügen
                "cat_name" + "," +
                "acc_name" + "," + "\r\n";
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
        expenseString.append(expense.getDate()).append(",");
        //todo tags hinzufügen
        expenseString.append(expense.getNotice()).append(",");
        //todo exchange ratehinzufügen
        //todo Währung der Buchung hinzufügen
        expenseString.append(expense.getCategory().getTitle()).append(",");
        expenseString.append(expense.getAccount().getTitle()).append("\r\n");

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
}
