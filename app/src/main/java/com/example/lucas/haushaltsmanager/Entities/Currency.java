package com.example.lucas.haushaltsmanager.Entities;

import androidx.annotation.NonNull;

// TODO: Kann ich die Currency durch die Java.util implementierung der Currency austauschen?
public class Currency {
    @NonNull
    public String getName() {
        return "Euro";
    }

    @NonNull
    public String getShortName() {
        return "EUR";
    }

    @NonNull
    public String getSymbol() {
        return "€";
    }
}
