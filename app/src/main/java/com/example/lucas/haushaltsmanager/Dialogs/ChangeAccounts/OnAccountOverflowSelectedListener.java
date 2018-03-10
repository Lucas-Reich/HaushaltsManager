package com.example.lucas.haushaltsmanager.Dialogs.ChangeAccounts;

import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.PopupMenu;

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
                        //wenn diser menüpunkt ausgewählt wurde soll noch einmal nachgefragt werden ob der user sich sicher ist und wenn ja soll das konto direkt gelöscht werden
                        Log.d(TAG, "onMenuItemSelected: delete selected");
                        return true;
                    case R.id.edit_account_edit:
                        //wenn dieser menüpunkt ausgewählt wurde dann soll die CreateNewAccountActivity aufgehen, die werte sollen bereits eingefügt sein und der button heißt "Update"
                        Log.d(TAG, "onMenuItemSelected: edit selected");
                        return true;
                    case R.id.edit_account_transfer:
                        //wenn dieser menüpunkt ausgewählt wurde, dann sich ein fenster öffnen in dem es möglich ist überweisungen von einem Konto zu einerm anderen zu machen
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
