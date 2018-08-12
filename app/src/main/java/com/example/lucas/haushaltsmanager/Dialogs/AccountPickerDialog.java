package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.R;

import java.util.List;

public class AccountPickerDialog extends DialogFragment {
    private static final String TAG = AccountPickerDialog.class.getSimpleName();

    private OnAccountSelected mCallback;
    private long excludedAccountId = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        if (args.containsKey("excluded_account")) {
            excludedAccountId = args.getLong("excluded_account");
        }

        final List<Account> accounts = AccountRepository.getAll();
        removeExcludedAccount(accounts);

        final int activeAccountId = (int) args.getLong("active_account");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString("title"));

        builder.setSingleChoiceItems(accountsToStrings(accounts), activeAccountId, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int selectedAccount) {

                mCallback.onAccountSelected(accounts.get(selectedAccount));
                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        return builder.create();
    }

    private String[] accountsToStrings(List<Account> accounts) {

        String[] accountStrings = new String[accounts.size()];

        for (int i = 0; i < accounts.size(); i++) {

            accountStrings[i] = accounts.get(i).getTitle();
        }

        return accountStrings;
    }

    /**
     * Methode um ein Konto, welches nicht mit in der Übersicht angezeigt werden soll aus der Liste zu löschen.
     *
     * @param accounts Liste der verfügbaren Konten.
     */
    private void removeExcludedAccount(List<Account> accounts) {

        for (Account account : accounts) {
            if (account.getIndex() == excludedAccountId) {
                accounts.remove(account);
                break;
            }
        }
    }

    /**
     * Methode um einen Listener zu registrieren, welcher aufgerufen wird, wenn der User ein Konto ausgewählt hat.
     *
     * @param listener Listener
     */
    public void setOnAccountSelectedListener(AccountPickerDialog.OnAccountSelected listener) {
        mCallback = listener;
    }

    public interface OnAccountSelected {

        void onAccountSelected(Account account);
    }
}
