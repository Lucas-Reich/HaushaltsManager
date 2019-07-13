package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;

import javax.annotation.Nullable;

public interface IAccountRepository {
    /**
     * Diese Methode überprüft, ob es ein Konto mit der angegeben id gibt.
     *
     * @param accountId Zu übeprüfende Konto id
     * @return TRUE, wenn das Konto existiert oder FALSE, wenn es nicht existiert.
     */
    boolean exists(long accountId);

    /**
     * Diese Methode sucht nach dem angegebenen Konto.
     * Falls es dieses existiert wird es zurückgegeben, falls nicht wird NULL zurückgegeben.
     *
     * @param accountId Konto, welches zurückgegeben werden soll
     * @return Ein Konto falls es existiert, NULL falls nicht.
     */
    @Nullable
    Account find(long accountId);

    /**
     * Diese Methode sucht nach dem angegebenen Konto.
     * Sie geht davon aus, dass das Konto existiert und löst eine Exception aus falls es nicht gefunden werden konnte.
     *
     * @param accountId Konto, welches zurückgegeben werden soll.
     * @return Konto
     * @throws AccountNotFoundException Existiert das angegebene Konto nicht wird eine Exception ausgelöst.
     */
    @NonNull
    Account get(long accountId) throws AccountNotFoundException;

    /**
     * Diese Methode löscht das angegebene Konto aus der Datenbank.
     * Wurde es erfolgreich gelöscht oder konnte nicht gefunden werden wird TRUE zurückgegeben.
     * Ist das Konto noch mit Buchungen verknüpft oder wird eine SQL Exception ausgelöst wird FALSE zurückgegeben.
     *
     * @param accountId Id des zu löschenden Kontos
     * @return TRUE wenn das Konto gelöscht wurde oder FALSE, wenn ein Fehler aufgetreten ist
     */
    boolean delete(long accountId);

    /**
     * Diese Methode updated das angegeben Konto.
     * Wurde es erfolgreich geupdated, wird TRUE zurückgegeben.
     * Konto es nicht gefunden werden oder eine SQL Exception wurde ausgelöst, wird FALSE zurückgegeben.
     *
     * @param account Upzudatendes Konto
     * @return TRUE, wenn das Konto geupdated werden konnte oder FALSE wenn nicht.
     */
    boolean update(Account account);

    /**
     * Diese Methode speichert das angegebene Konto in der Datenbank.
     *
     * @param account Zu speicherndes Konto
     * @return Konto, mit dem index der Datenbank
     */
    Account save(Account account);
}
