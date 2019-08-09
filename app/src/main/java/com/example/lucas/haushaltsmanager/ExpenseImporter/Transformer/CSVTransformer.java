package com.example.lucas.haushaltsmanager.ExpenseImporter.Transformer;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidLineException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.DelimiterIdentifier;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;

public class CSVTransformer implements ITransformer {
    private final String header;
    private DelimiterIdentifier delimiterIdentifier;

    public CSVTransformer(String header) {
        this.header = header;

        delimiterIdentifier = new DelimiterIdentifier();
    }

    @Override
    public Line transform(String input) throws InvalidLineException {
        // TODO: Wie kann ich es vermeiden hier noch einmal den Delimiter zu bestimmen?
        DelimiterInterface delimiter = delimiterIdentifier.identifyDelimiter(input);

        return new Line(
                header,
                input,
                delimiter
        );
    }
}
