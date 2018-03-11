package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

public class AccountPickerDialog extends DialogFragment {

    private String TAG = AccountPickerDialog.class.getSimpleName();

    private ExpensesDataSource mDatabase;
    private OnAccountSelected mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (OnAccountSelected) context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement OnAccountSelected");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDatabase = new ExpensesDataSource(getActivity());
        mDatabase.open();

        final Bundle args = getArguments();
        final ArrayList<Account> accounts = mDatabase.getAllAccounts();
        final Account activeAccount = args.getParcelable("active_account");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString("title"));

        int activeAccountId = activeAccount != null ? (int) activeAccount.getIndex() - 1 : -1;
        builder.setSingleChoiceItems(accountsToStrings(accounts), activeAccountId, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int selectedAccount) {

                mCallback.onAccountSelected(accounts.get(selectedAccount), getTag());
                mDatabase.close();
                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabase.close();
                dismiss();
            }
        });

        return builder.create();
    }

    private String[] accountsToStrings(ArrayList<Account> accounts) {

        String[] accountStrings = new String[accounts.size()];

        for (int i = 0; i < accounts.size(); i++) {

            accountStrings[i] = accounts.get(i).getName();
        }

        return accountStrings;
    }

    public void onStop() {
        super.onStop();

        mDatabase.close();
    }

    public interface OnAccountSelected {

        void onAccountSelected(Account account, String tag);
    }
}
