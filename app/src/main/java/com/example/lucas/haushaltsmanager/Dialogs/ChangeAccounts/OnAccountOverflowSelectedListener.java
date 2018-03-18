package com.example.lucas.haushaltsmanager.Dialogs.ChangeAccounts;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.lucas.haushaltsmanager.Activities.CreateNewAccountActivity;
import com.example.lucas.haushaltsmanager.Activities.TransferActivity;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.R;


public class OnAccountOverflowSelectedListener implements View.OnClickListener {

    private static String TAG = OnAccountOverflowSelectedListener.class.getSimpleName();

    private Account mAccount;
    private Context mContext;

    OnAccountOverflowSelectedListener(Context context, Account account) {

        mAccount = account;
        mContext = context;
    }

    @Override
    public void onClick(View view) {

        PopupMenu popupMenu = new PopupMenu(mContext, view);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.edit_account_delete:
                        //TODO lösche das Konto
                        //wenn diser menüpunkt ausgewählt wurde soll noch einmal nachgefragt werden ob der user sich sicher ist und wenn ja soll das konto direkt gelöscht werden
                        return true;
                    case R.id.edit_account_edit:

                        Intent updateAccountIntent = new Intent(mContext, CreateNewAccountActivity.class);
                        updateAccountIntent.putExtra("mode", "update");
                        updateAccountIntent.putExtra("account_id", mAccount.getIndex());
                        mContext.startActivity(updateAccountIntent);

                        Log.d(TAG, "onMenuItemSelected: edit selected");
                        return true;
                    case R.id.edit_account_transfer:

                        Intent transferMoneyBetweenAccountsIntent = new Intent(mContext, TransferActivity.class);
                        transferMoneyBetweenAccountsIntent.putExtra("from_account_id", mAccount.getIndex());
                        mContext.startActivity(transferMoneyBetweenAccountsIntent);

                        Log.d(TAG, "onMenuItemSelected: empty selected");
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.inflate(R.menu.edit_account_details);

        popupMenu.show();
    }
}
