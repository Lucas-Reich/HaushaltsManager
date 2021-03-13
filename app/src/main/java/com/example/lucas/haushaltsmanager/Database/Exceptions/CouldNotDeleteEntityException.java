package com.example.lucas.haushaltsmanager.Database.Exceptions;

public class CouldNotDeleteEntityException extends Exception {

    public CouldNotDeleteEntityException(String message, Throwable previous) {
        super(message, previous);
    }
}
