package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.PriceParser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.AbsDoubleParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.BooleanPriceTypeParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.IPriceTypeParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.NumericPriceTypeParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.ArrayList;
import java.util.List;

public class PriceParser implements IParser<Price> {
    private final IParser<Double> valueParser;
    private IParser<Boolean> priceTypeParser = null;

    public PriceParser(IParser<Double> valueParser) {
        this.valueParser = valueParser;
    }

    @Override
    @NonNull
    public List<IRequiredField> getRequiredFields() {
        return new ArrayList<IRequiredField>() {{
            addAll(valueParser.getRequiredFields());
            add(IPriceTypeParser.Companion.getPRICE_TYPE_KEY());
        }};
    }

    @Override
    @NonNull
    public Price parse(@NonNull Line line, @NonNull MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        boolean priceType = isPriceNegative(line, mapping);

        return new Price(
                valueParser.parse(line, mapping),
                priceType
        );
    }

    private boolean isPriceNegative(Line line, MappingList mapping) {
        if (null == priceTypeParser) {
            initializeBooleanParser(mapping);
        }

        return priceTypeParser.parse(line, mapping);
    }

    private void initializeBooleanParser(MappingList mapping) {
        int typeIndex = mapping.getMappingForKey(IPriceTypeParser.Companion.getPRICE_TYPE_KEY());
        int valueIndex = mapping.getMappingForKey(AbsDoubleParser.PRICE_VALUE_KEY);

        if (typeIndex == valueIndex) {
            priceTypeParser = new NumericPriceTypeParser();
            return;
        }

        priceTypeParser = new BooleanPriceTypeParser();
    }
}
