package com.example.lucas.haushaltsmanager.Database.Exceptions;

public class EntityCouldNotBeCreatedException extends Exception {
    public EntityCouldNotBeCreatedException(String message, Throwable previous) {
        super(message, previous);
    }
}
