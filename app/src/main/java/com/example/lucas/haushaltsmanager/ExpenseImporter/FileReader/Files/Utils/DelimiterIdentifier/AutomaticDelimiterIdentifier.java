package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils.DelimiterIdentifier;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;

public class AutomaticDelimiterIdentifier implements IDelimiterIdentifier {
    // TODO: Vielleicht kann ich alle Chars zählen, die keine Buchstaben sind und somit bestimmen, was der Delimiter ist
    //  Beispiel: https://www.geeksforgeeks.org/character-isletterordigit-in-java-with-examples/
    //  Wenn ich das so mache, sollte ich ein paar linien testen, ob der ausgewählte delimiter die ersten paar zeilen (10-50) größtenteils gleichmäßig aufteilt

    @Override
    public IDelimiter identifyDelimiter(String input) {
        throw new RuntimeException("not implemented");
    }
}
