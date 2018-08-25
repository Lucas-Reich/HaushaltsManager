package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.example.lucas.haushaltsmanager.BundleUtils;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.R;

import java.util.List;

public class AccountPickerDialog extends DialogFragment {
    private static final String TAG = AccountPickerDialog.class.getSimpleName();
    public static final String TITLE = "title";
    public static final String ACTIVE_ACCOUNT = "active_account";
    public static final String EXCLUDED_ACCOUNT = "excluded_account";

    private OnAccountSelected mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //todo kann eventuell durch StringSingleChoiceDialog ersetzt werden
        BundleUtils args = new BundleUtils(getArguments());

        final List<Account> accounts = AccountRepository.getAll();
        removeExcludedAccount(accounts, args.getLong(EXCLUDED_ACCOUNT, -1));

        final int activeAccountId = (int) args.getLong(ACTIVE_ACCOUNT, -1);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString(TITLE, ""));

        builder.setSingleChoiceItems(accountsToStrings(accounts), activeAccountId, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int selectedAccount) {

                if (mCallback != null)
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
    private void removeExcludedAccount(List<Account> accounts, long excludedAccountId) {

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
