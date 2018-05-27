package com.example.lucas.haushaltsmanager.Dialogs.ChangeAccounts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.CreateAccountActivity;
import com.example.lucas.haushaltsmanager.Database.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseAccountsDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener, AccountAdapter.OnDeleteAccountSelected {
    private static String TAG = ChooseAccountsDialogFragment.class.getSimpleName();

    ExpensesDataSource mDatabase;
    SharedPreferences mSettings;
    OnSelectedAccount mCallback;
    ListView mListView;
    Context mContext;
    List<Boolean> mCheckedAccounts;
    SharedPreferences.Editor mSettingsEditor;
    ArrayList<Account> mAccounts;
    AlertDialog.Builder builder;

    /**
     * Standart Fragment Methode die genutzt wird, um zu checken ob die aufrufende Activity auch das interface inplementiert.
     *
     * @param context Kontext
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (OnSelectedAccount) context;
            mContext = context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement OnSelectedAccountListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new ExpensesDataSource(mContext);
        mDatabase.open();

        mSettings = getActivity().getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);
        mSettingsEditor = mSettings.edit();

        mCheckedAccounts = new ArrayList<>();

        mAccounts = mDatabase.getAllAccounts();
        for (Account account : mAccounts) {

            mCheckedAccounts.add(mSettings.getBoolean(account.getTitle(), false));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        setUpListView();

        builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.choose_account);

        builder.setView(mListView);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mSettingsEditor.apply();
                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        builder.setNeutralButton(R.string.btn_new_acc, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent newAccountIntent = new Intent(getActivity(), CreateAccountActivity.class);
                newAccountIntent.putExtra("mode", "create");
                getActivity().startActivity(newAccountIntent);
            }
        });

        return builder.create();
    }

    /**
     * Methode um die ListView zu erzeugen und mit Funktionalität zu versorgen.
     */
    private void setUpListView() {

        AccountAdapter adapter = new AccountAdapter(mAccounts, mContext);
        adapter.setCheckedItems(mCheckedAccounts);
        adapter.setOnDeleteAccountListener(this);

        mListView = new ListView(mContext);
        mListView.setAdapter(adapter);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setItemsCanFocus(false);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(this);
    }

    /**
     * Wenn in der ListView ein Konto ausgewählt wurde wird hier die Checkbox manipuliert und der callback ausgelöst
     *
     * @param parent   parent
     * @param view     view
     * @param position position
     * @param id       id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CheckedTextView accountChk = (CheckedTextView) view.findViewById(R.id.list_view_account_item_account_chk);
        if (accountChk.isChecked()) {

            mCheckedAccounts.set(position, false);
            accountChk.setChecked(false);
        } else {

            mCheckedAccounts.set(position, true);
            accountChk.setChecked(true);
        }

        mSettingsEditor.putBoolean(mAccounts.get(position).getTitle(), mCheckedAccounts.get(position));
        mCallback.onAccountSelected(mAccounts.get(position).getIndex(), mCheckedAccounts.get(position));
    }

    /**
     * Methode die den Callback des AccountAdapters implementiert, wenn ein Konto gelöscht werden soll
     *
     * @param account Zu löschendes Konto
     */
    @Override
    public void onDeleteAccountSelected(Account account) {

        try {
            mDatabase.deleteAccount(account.getIndex());

            SharedPreferences preferences = mContext.getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(account.getTitle());
            editor.apply();
            //was passiert wenn ich das aktuelle acktive konto lösche
            Toast.makeText(mContext, mContext.getResources().getString(R.string.deleted_account), Toast.LENGTH_SHORT).show();
        } catch (CannotDeleteAccountException e) {

            Toast.makeText(mContext, mContext.getResources().getString(R.string.failed_to_delete_account), Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    /**
     * Methode die den Callback des AccountsAdapters implementiert, wenn ein Konto als Hauptkonto ausgewählt wurde
     *
     * @param account Konto das nun das Hautpkonto sein soll
     */
    @Override
    public void onAccountSetMain(Account account) {

        SharedPreferences preferences = mContext.getSharedPreferences("UserSettings",  Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("activeAccount", account.getIndex());
        editor.apply();

        Toast.makeText(mContext, account.getTitle() + mContext.getResources().getString(R.string.changed_main_account), Toast.LENGTH_SHORT).show();
    }

    public interface OnSelectedAccount {
        void onAccountSelected(long accountId, boolean isChecked);
    }

    @Override
    public void onStop() {
        super.onStop();

        //todo Wenn Konten an oder abgewählt wurden und der User neben den Dialog tippt und sich das Fenster somit schließt müssen auch alle änderungen
        // die der User gemacht hat wieder rückgängig gemacht werden (ausgenommen delete und set main account)
        mDatabase.close();
    }
}
