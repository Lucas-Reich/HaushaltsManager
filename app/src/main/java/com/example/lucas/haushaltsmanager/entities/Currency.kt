package com.example.lucas.haushaltsmanager.entities

// TODO: Check if I can replace this class with the Java.util implementation?
class Currency {
    fun getName(): String {
        return "Euro"
    }

    fun getShortName(): String {
        return "EUR"
    }

    fun getSymbol(): String {
        return "€"
    }
}