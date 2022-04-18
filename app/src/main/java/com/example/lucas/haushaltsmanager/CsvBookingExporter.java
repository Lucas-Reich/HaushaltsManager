package com.example.lucas.haushaltsmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.CachedAccountReadRepositoryDecorator;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.CachedCategoryReadRepositoryDecorator;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Directory;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.category.Category;

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
        FileOutputStream outputFile = null;

        try {
            outputFile = new FileOutputStream(file);

            outputFile.write(getCsvHeader().getBytes());
            for (Booking booking : bookings) {
                Account account = cachedAccountRepository.get(booking.getId());
                Category category = cachedCategoryRepository.get(booking.getId());

                String stringifiedBooking = bookingToStringTransformer.transform(booking, category, account);

                outputFile.write(stringifiedBooking.getBytes());
            }
            outputFile.close();
            return true;
        } catch (Exception e) {

            return false;
        } finally {
            if (outputFile != null) {
                try {
                    outputFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getCsvHeader() {
        return app.getStringResource(R.string.export_price) + ","
                + app.getStringResource(R.string.export_expenditure) + ","
                + app.getStringResource(R.string.export_title) + ","
                + app.getStringResource(R.string.export_date) + ","
                + app.getStringResource(R.string.export_currency_name) + ","
                + app.getStringResource(R.string.export_category_name) + ","
                + app.getStringResource(R.string.export_account_name) + ","
                + "\r\n";
    }

    private String createOutputFileName() {
        return String.format(EXPORT_FILE_NAME, CalendarUtils.getCurrentDate());
    }
}

