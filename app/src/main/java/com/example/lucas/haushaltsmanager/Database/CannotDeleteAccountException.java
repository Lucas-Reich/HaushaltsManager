package com.example.lucas.haushaltsmanager.Database;

public class CannotDeleteAccountException extends Exception {

    public CannotDeleteAccountException(String message) {

        super(message);
    }
}
