package com.example.lucas.haushaltsmanager.Database;

import android.database.Cursor;

public interface TransformerInterface<T> {
    T transform(Cursor c);
}