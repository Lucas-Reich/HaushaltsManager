package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields.PriceType
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField

interface IPriceTypeParser : IParser<Boolean> {
    companion object {
        val PRICE_TYPE_KEY: IRequiredField = PriceType()

        const val VALUE_POSITIVE = false
        const val VALUE_NEGATIVE = true
    }
}