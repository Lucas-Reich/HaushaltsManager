package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils.DelimiterIdentifier;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Semicolon;

import java.util.HashMap;
import java.util.Map;

public class CSVDelimiterIdentifier implements IDelimiterIdentifier {
    public static final IDelimiter DEFAULT_DELIMITER = new Comma();

    private HashMap<IDelimiter, Integer> validDelimiter;

    public CSVDelimiterIdentifier() {
        validDelimiter = new HashMap<>();

        // Add supported CSV delimiter
        validDelimiter.put(new Comma(), 0);
        validDelimiter.put(new Semicolon(), 0);
    }

    public IDelimiter identifyDelimiter(String input) {
        countDelimiterOccurrences(input);

        return getDelimiterWithMostOccurrence();
    }

    private IDelimiter getDelimiterWithMostOccurrence() {
        IDelimiter delimiter = null;
        int previousCount = 0;

        for (Map.Entry<IDelimiter, Integer> entry : validDelimiter.entrySet()) {
            if (entry.getValue() > previousCount) {
                delimiter = entry.getKey();
            }
        }

        return delimiter != null ? delimiter : DEFAULT_DELIMITER;
    }

    private void countDelimiterOccurrences(String input) {
        for (Map.Entry<IDelimiter, Integer> delimiter : validDelimiter.entrySet()) {
            int delimiterCount = countCharOccurrence(input, delimiter.getKey().getDelimiter());
            delimiter.setValue(delimiterCount);
        }
    }

    private int countCharOccurrence(String string, String target) {
        return string.length() - string.replace(target, "").length();
    }
}
