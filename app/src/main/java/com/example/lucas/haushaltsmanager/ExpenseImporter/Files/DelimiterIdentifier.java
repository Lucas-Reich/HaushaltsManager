package com.example.lucas.haushaltsmanager.ExpenseImporter.Files;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Semicolon;

import java.util.HashMap;
import java.util.Map;

public class DelimiterIdentifier {
    static final DelimiterInterface DEFAULT_DELIMITER = new Comma();
    // TODO: Vielleicht kann ich alle Chars zählen, die keine Buchstaben sind und somit bestimmen, was der Delimiter ist
    //  Beispiel: https://www.geeksforgeeks.org/character-isletterordigit-in-java-with-examples/
    //  Wenn ich das so mache, sollte ich ein paar linien testen, ob der ausgewählte delimiter die ersten paar zeilen (10-50) größtenteils gleichmäßig aufteilt

    private HashMap<DelimiterInterface, Integer> delimiterCount;

    public DelimiterIdentifier() {
        delimiterCount = new HashMap<>();

        // Initialize supported Delimiter with initial count
        delimiterCount.put(new Comma(), 0);
        delimiterCount.put(new Semicolon(), 0);
    }

    public DelimiterInterface identifyDelimiter(String input) {
        countDelimiterOccurrences(input);

        return getDelimiterWithMostOccurrence();
    }

    private DelimiterInterface getDelimiterWithMostOccurrence() {
        DelimiterInterface delimiter = null;
        int previousCount = 0;

        for (Map.Entry<DelimiterInterface, Integer> entry : delimiterCount.entrySet()) {
            if (entry.getValue() > previousCount) {
                delimiter = entry.getKey();
            }
        }

        return delimiter != null ? delimiter : DEFAULT_DELIMITER;
    }

    private void countDelimiterOccurrences(String input) {
        for (Map.Entry<DelimiterInterface, Integer> entry : delimiterCount.entrySet()) {
            int delimiterCount = countCharOccurrence(input, entry.getKey().getDelimiter());
            entry.setValue(delimiterCount);
        }
    }

    private int countCharOccurrence(String string, String target) {
        return string.length() - string.replace(target, "").length();
    }
}
