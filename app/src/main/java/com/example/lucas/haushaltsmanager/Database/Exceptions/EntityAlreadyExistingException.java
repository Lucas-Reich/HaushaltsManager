package com.example.lucas.haushaltsmanager.Database.Exceptions;

public class EntityAlreadyExistingException extends Exception {
    public EntityAlreadyExistingException(String message) {
        super(message);
    }
}
