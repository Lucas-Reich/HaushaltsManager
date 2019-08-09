package com.example.lucas.haushaltsmanager.ExpenseImporter;

public interface ISub {
    /**
     * Mit dieser methode wird der Sub benachrichtigt,
     * dass die zu beobachtende Aufgabe einen schritt erfolgreich abgeschlossen hat
     */
    void notifySuccess();

    /**
     * Mit dieser methode wird der Sub benachrichtigt,
     * dass die zu beobachtende Aufgabe einen schritt nicht erfolgreich abgeschlossen hat
     */
    void notifyFailure();
}
