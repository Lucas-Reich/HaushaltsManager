package com.example.lucas.haushaltsmanager.entities

import com.example.lucas.haushaltsmanager.App.app

/**
 * TODO: Remove this class if no errors are reported regarding the Java.Currency implementation
 */
class Currency {
    fun getName(): String {
        return app.getDefaultCurrency().displayName
    }

    fun getShortName(): String {
        return app.getDefaultCurrency().currencyCode
    }

    fun getSymbol(): String {
        return app.getDefaultCurrency().symbol
    }
}