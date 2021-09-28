package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies;

import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.AccountParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.BookingParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.ISaver;

import java.util.ArrayList;
import java.util.List;

public class ImportBookingStrategy implements IImportStrategy {
    private final BookingParser bookingParser;
    private final AccountParser accountParser;
    private final ISaver saver;

    public ImportBookingStrategy(
            BookingParser bookingParser,
            AccountParser accountParser,
            ISaver saver
    ) {
        this.bookingParser = bookingParser;
        this.accountParser = accountParser;
        this.saver = saver;
    }

    public List<IRequiredField> getRequiredFields() {
        return new ArrayList<IRequiredField>() {{
            addAll(bookingParser.getRequiredFields());
            addAll(accountParser.getRequiredFields());
        }};
    }

    public void handle(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        Booking booking = bookingParser.parse(line, mapping);

        Account account = accountParser.parse(line, mapping);

        saver.persist(booking, account);
    }

    @Override
    public void abort() {
        saver.revert();
    }

    @Override
    public void finish() {
        saver.finish();
    }
}
