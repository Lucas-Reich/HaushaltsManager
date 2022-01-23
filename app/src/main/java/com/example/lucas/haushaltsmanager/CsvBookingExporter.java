package com.example.lucas.haushaltsmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.CachedAccountReadRepositoryDecorator;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.CachedCategoryReadRepositoryDecorator;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Directory;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CsvBookingExporter {
    private static final String EXPORT_FILE_NAME = "Export_%s.csv";

    private final Directory targetDirectory;
    private final AccountDAO cachedAccountRepository;
    private final CategoryDAO cachedCategoryRepository;
    private final BookingToStringTransformer bookingToStringTransformer;

    public CsvBookingExporter(
            @NonNull Directory targetDirectory,
            @NonNull AccountDAO accountRepository,
            @NonNull CategoryDAO categoryRepository
    ) {
        guardAgainstInvalidDirectory(targetDirectory);
        this.targetDirectory = targetDirectory;

        cachedAccountRepository = new CachedAccountReadRepositoryDecorator(accountRepository);
        cachedCategoryRepository = new CachedCategoryReadRepositoryDecorator(categoryRepository);
        bookingToStringTransformer = new BookingToStringTransformer();
    }

    @Nullable
    public File writeToFile(@NonNull List<Booking> bookings) {
        File file = FileUtils.create(createOutputFileName(), targetDirectory);

        if (file != null && writeExpensesToFile(bookings, file)) {
            return file;
        }

        return null;
    }

    private void guardAgainstInvalidDirectory(Directory directory) throws IllegalArgumentException {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("The given object is not a Directory!");
        }
    }

    private boolean writeExpensesToFile(List<Booking> bookings, File file) {
        FileOutputStream fileOutput = null;

        try {

            fileOutput = new FileOutputStream(file);

            fileOutput.write(getCsvHeader().getBytes());
            for (Booking booking : bookings) {
                Account account = cachedAccountRepository.get(booking.getId());
                Category category = cachedCategoryRepository.get(booking.getId());

                String stringifiedBooking = bookingToStringTransformer.transform(booking, category, account);

                fileOutput.write(stringifiedBooking.getBytes());
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

    private String getCsvHeader() {
        return getStringResource(R.string.export_price) + ","
                + getStringResource(R.string.export_expenditure) + ","
                + getStringResource(R.string.export_title) + ","
                + getStringResource(R.string.export_date) + ","
                + getStringResource(R.string.export_currency_name) + ","
                + getStringResource(R.string.export_category_name) + ","
                + getStringResource(R.string.export_account_name) + ","
                + "\r\n";
    }

    private String getStringResource(@StringRes int id) {
        return app.getContext().getString(id);
    }

    private String createOutputFileName() {
        String fileName = CalendarUtils.getCurrentDate();

        return String.format(EXPORT_FILE_NAME, fileName);
    }
}

