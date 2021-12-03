package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.StringRes;
import androidx.room.Room;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Currency;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class CsvBookingExporter {
    private final File targetDirectory;
    private final List<Account> accounts;
    private final List<Category> categories;

    public CsvBookingExporter(File targetDirectory, Context context) {

        guardAgainstInvalidDirectory(targetDirectory);
        this.targetDirectory = targetDirectory;

        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "expenses").build();

        accounts = database.accountDAO().getAll();
        categories = database.categoryDAO().getAll();
    }

    /**
     * Methode die eine Liste von Buchungen nimmt und diese in eine Datei schreibt.
     *
     * @param bookings Buchungen die in eine Datei geschrieben werden sollen.
     * @return Die erstellte Datei wird zurückgegeben. Falls die Datei nicht erstellt werden konnte wird NULL zurückgegeben.
     */
    public File writeToFile(List<Booking> bookings) {
        File file = createFile(targetDirectory);

        if (file != null && writeExpensesToFile(bookings, file)) {
            return file;
        }

        return null;
    }

    /**
     * Methode um zu überprüfen ob das angegebene Verzeichnis auch wirklich ein Verzeichnis ist.
     * Falls nicht wird eine Exception ausgelöst.
     *
     * @param directory Verzeichnis das überprüft werden soll.
     */
    private void guardAgainstInvalidDirectory(File directory) throws IllegalArgumentException {
        if (directory != null && !directory.isDirectory())
            throw new IllegalArgumentException("The given object is not a Directory!");
    }

    /**
     * Methode um eine Liste von Buchungen in eine Komma seperierten String zu konvertieren.
     *
     * @param bookings Umzuwandelnde Buchungen.
     * @param file     Datei in der die Buchungen gespeichert werden sollen.
     * @return True wenn die Buchungen erfolgreich in die Datei geschrieben werden konnten, False wenn nicht.
     */
    private boolean writeExpensesToFile(List<Booking> bookings, File file) {
        FileOutputStream fileOutput = null;

        try {

            fileOutput = new FileOutputStream(file);

            fileOutput.write(getCsvHeader().getBytes());
            for (Booking booking : bookings) {

                fileOutput.write(bookingToString(booking).getBytes());
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
     * @param booking ExpenseObject das umgewandelt werden soll
     * @return ExpenseObject mit allen Kindern als String
     */
    private String bookingToString(Booking booking) {
        StringBuilder expenseString = new StringBuilder();

        expenseString.append(booking.getUnsignedPrice()).append(",");
        expenseString.append(booking.isExpenditure()).append(",");
        expenseString.append(booking.getTitle()).append(",");
        expenseString.append(booking.getDateString()).append(",");
        expenseString.append(new Currency().getName()).append(",");

        Category category = getCategory(booking.getCategoryId());
        expenseString.append(category != null ? category.getName() : "").append(",");

        Account account = getAccount(booking.getAccountId());
        expenseString.append(account != null ? account.getName() : "").append("\r\n");

        return expenseString.toString();
    }

    /**
     * Methode um eine Neue Datei im angegebenen Verzeichnis anzulegen.
     *
     * @param directory Verzeichnis in dem die Datei angelegt werden soll.
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
        long totalStorageSpace = targetDirectory.getTotalSpace();
        long freeStorageSpace = targetDirectory.getFreeSpace();

        return freeStorageSpace / totalStorageSpace <= 0.9;

    }

    /**
     * Methode um zu überprüfen ob der angegebene Speicherplatz zur Verfügung steht.
     *
     * @return True wenn es erreichbar ist, False wenn nicht.
     */
    private boolean isMediumAvailable() {
        String externalStoragePath = Environment.getExternalStorageDirectory().getPath();

        if (targetDirectory.getPath().contains(externalStoragePath))
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        else
            return true;
    }

    /**
     * Methode um zu überprüfen ob der User Schreibberechtigung für das angegebene Verzeichnis hat.
     *
     * @return True bei einer Schreibberechtigung, False bei keiner
     */
    private boolean hasWritePermission() {

        return targetDirectory.canWrite();
    }

    /**
     * Methode um eine Dateinamen zu erstellen, welcher auf der aktuellen Zeit basiert.
     *
     * @return Dateiname
     */
    private String createFileName() {
        Calendar date = Calendar.getInstance();
        String fileName = date.get(Calendar.DAY_OF_MONTH) + "_"
                + date.get(Calendar.MONTH) + "_"
                + date.get(Calendar.YEAR) + "_"
                + date.get(Calendar.HOUR_OF_DAY) + ":"
                + date.get(Calendar.MINUTE) + ":"
                + date.get(Calendar.SECOND);

        return String.format("Export_%s.csv", fileName);
    }

    private Category getCategory(UUID id) {
        for (Category category : categories) {
            if (category.getId() == id) {
                return category;
            }
        }

        return null; // This should never happen
    }

    private Account getAccount(UUID id) {
        for (Account account : accounts) {
            if (account.getId() == id) {
                return account;
            }
        }

        return null; // This should never happen
    }
}
