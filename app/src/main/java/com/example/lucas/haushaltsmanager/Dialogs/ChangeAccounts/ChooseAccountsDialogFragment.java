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

import com.example.lucas.haushaltsmanager.Activities.CreateNewAccountActivity;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseAccountsDialogFragment extends DialogFragment {

    String TAG = ChooseAccountsDialogFragment.class.getSimpleName();

    ExpensesDataSource mDatabase;
    SharedPreferences mSettings;
    OnSelectedAccount mCallback;
    ListView mListView;
    Context mContext;
    List<Boolean> mCheckedAccounts;
    SharedPreferences.Editor mSettingsEditor;
    ArrayList<Account> mAccounts;

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

            mCheckedAccounts.add(mSettings.getBoolean(account.getName(), false));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        setUpListView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

                Intent newAccountIntent = new Intent(getActivity(), CreateNewAccountActivity.class);
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

        mListView = new ListView(mContext);
        mListView.setAdapter(adapter);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setItemsCanFocus(false);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(onItemClickListener);
    }

    /**
     * OnItemClickListener für die CheckedTextView, welcher die CheckBox manipuliert, und die callbacks auslöst
     * <p>
     * Anleitung: http://kb4dev.com/tutorial/android-listview/android-listview-with-checkbox
     */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

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

            mSettingsEditor.putBoolean(mAccounts.get(position).getName(), mCheckedAccounts.get(position));
            mCallback.onAccountSelected(mAccounts.get(position).getIndex(), mCheckedAccounts.get(position));
        }
    };

    public interface OnSelectedAccount {
        void onAccountSelected(long accountId, boolean isChecked);
    }

    @Override
    public void onStop() {
        super.onStop();

        mDatabase.close();
    }
}
