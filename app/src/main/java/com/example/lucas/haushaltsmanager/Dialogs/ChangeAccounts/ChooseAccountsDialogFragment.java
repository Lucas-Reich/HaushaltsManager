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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.CreateAccountActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.R;

import java.util.HashMap;
import java.util.Map;

public class ChooseAccountsDialogFragment extends DialogFragment implements AccountAdapter.OnDeleteAccountSelected {
    private static final String TAG = ChooseAccountsDialogFragment.class.getSimpleName();

    private OnSelectedAccount mCallback;
    private ListView mListView;
    private Context mContext;
    private Map<Account, Boolean> mInitialAccountState;

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
        SharedPreferences preferences = getActivity().getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);

        mInitialAccountState = new HashMap<>();

        for (Account account : AccountRepository.getAll()) {

            mInitialAccountState.put(account, preferences.getBoolean(account.getTitle(), false));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        prepareListView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.choose_account);

        builder.setView(mListView);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                revertUserInteractions();
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
    private void prepareListView() {

        AccountAdapter adapter = new AccountAdapter(mInitialAccountState, mContext);
        adapter.setOnDeleteAccountListener(this);

        mListView = new ListView(mContext);
        mListView.setAdapter(adapter);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setItemsCanFocus(false);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox accountChk = view.findViewById(R.id.listview_account_item_chkbox);
                boolean newVisibilityState = !accountChk.isChecked();

                accountChk.setChecked(newVisibilityState);
                setAccountVisibility(getAccountAtPosition(position), newVisibilityState);
            }
        });
    }

    /**
     * Methode um das Konto an dieser Position der ListView zu bekommen.
     *
     * @param position Position des Kontos
     * @return Konto an dieser Position
     */
    private Account getAccountAtPosition(int position) {
        return (Account) mListView.getItemAtPosition(position);
    }

    /**
     * Methode die den Callback des AccountAdapters implementiert, wenn ein Konto gelöscht werden soll
     *
     * @param account Zu löschendes Konto
     */
    @Override
    public void onDeleteAccountSelected(Account account) {

        try {
            if (isCurrentMainAccount(account)) {

                deleteAccount(account);
                makeAccountMain((Account) mInitialAccountState.keySet().toArray()[0]);
            } else {

                deleteAccount(account);
            }

            Toast.makeText(mContext, mContext.getResources().getString(R.string.deleted_account), Toast.LENGTH_SHORT).show();
        } catch (CannotDeleteAccountException e) {

            Toast.makeText(mContext, mContext.getResources().getString(R.string.failed_to_delete_account), Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    /**
     * Methode um herauszufinden ob das angegebene Konto das aktuelle Hauptkonto ist.
     *
     * @param account Konto, welches überprüft werden soll
     * @return TRUE wenn es das Hautpkonto ist, FALSE wenn nicht
     */
    private boolean isCurrentMainAccount(Account account) {
        SharedPreferences preferences = mContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        return account.getIndex() == preferences.getLong("activeAccount", -1);
    }

    /**
     * Methode um ein Konto zu löschen.
     *
     * @param account Konto, welches gelöscht werden soll
     */
    private void deleteAccount(Account account) throws CannotDeleteAccountException {
        AccountRepository.delete(account);

        SharedPreferences accountPreferences = mContext.getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);
        accountPreferences.edit().remove(account.getTitle()).apply();

        mInitialAccountState.remove(account);
    }

    /**
     * Methode die den Callback des AccountsAdapters implementiert, wenn ein Konto als Hauptkonto ausgewählt wurde
     *
     * @param account Konto das nun das Hautpkonto sein soll
     */
    @Override
    public void onAccountSetMain(Account account) {
        makeAccountMain(account);
        Toast.makeText(mContext, account.getTitle() + mContext.getResources().getString(R.string.changed_main_account), Toast.LENGTH_SHORT).show();
    }

    /**
     * Methode um das gewählte Konto zum Hauptkonto zu machen.
     *
     * @param account Neues Hauptkonto
     */
    private void makeAccountMain(Account account) {
        SharedPreferences preferences = mContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        preferences.edit().putLong("activeAccount", account.getIndex()).apply();
    }

    /**
     * Methode um die Auswahl der einzelnen Konten rückgängig zu machen, und so den ursprügnlichen Zustand wieder her zustellen.
     */
    private void revertUserInteractions() {
        for (Map.Entry<Account, Boolean> entry : mInitialAccountState.entrySet()) {
            setAccountVisibility(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Methode um die Sichtbarkeit eines Kontos (im TabOne) zu verändern.
     *
     * @param account   Konto von dem die Sichtbarkeit angepasst werden soll
     * @param isVisible Sichtabkeit. TRUE für sichtbar, FALSE für nicht sichtbar
     */
    private void setAccountVisibility(Account account, boolean isVisible) {
        SharedPreferences preferences = getActivity().getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);

        preferences.edit().putBoolean(account.getTitle(), isVisible).apply();
        mCallback.onAccountSelected(account.getIndex(), isVisible);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        revertUserInteractions();
    }

    public interface OnSelectedAccount {
        void onAccountSelected(long accountId, boolean isChecked);
    }
}