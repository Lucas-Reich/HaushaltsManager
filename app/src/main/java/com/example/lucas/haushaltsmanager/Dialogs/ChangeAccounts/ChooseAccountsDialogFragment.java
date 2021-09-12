package com.example.lucas.haushaltsmanager.Dialogs.ChangeAccounts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.room.Room;

import com.example.lucas.haushaltsmanager.Activities.CreateAccountActivity;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountDAO;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChooseAccountsDialogFragment extends DialogFragment implements AccountAdapter.OnDeleteAccountSelected {
    private OnSelectedAccount mCallback;
    private ListView mListView;
    private Map<Account, Boolean> mInitialAccountState;
    private AccountDAO accountRepo;
    private ActiveAccountsPreferences mAccountPreferences;
    private UserSettingsPreferences mUserPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountRepo = Room.databaseBuilder(getActivity(), AppDatabase.class, "expenses")
                .allowMainThreadQueries()
                .build().accountDAO();

        mAccountPreferences = new ActiveAccountsPreferences(getActivity());
        mUserPreferences = new UserSettingsPreferences(getActivity());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mInitialAccountState = new HashMap<>();

        for (Account account : accountRepo.getAll()) {
            mInitialAccountState.put(account, mAccountPreferences.isActive(account));
        }

        prepareListView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.choose_account);

        builder.setView(mListView);

        builder.setPositiveButton(R.string.btn_ok, (dialog, which) -> dismiss());

        builder.setNegativeButton(R.string.btn_cancel, (dialog, which) -> {
            revertUserInteractions();
            dismiss();
        });

        builder.setNeutralButton(R.string.btn_new_acc, (dialog, which) -> {
            Intent newAccountIntent = new Intent(getActivity(), CreateAccountActivity.class);
            newAccountIntent.putExtra(CreateAccountActivity.INTENT_MODE, CreateAccountActivity.INTENT_MODE_CREATE);
            getActivity().startActivity(newAccountIntent);
        });

        return builder.create();
    }

    /**
     * Methode die den Callback des AccountAdapters implementiert, wenn ein Konto gelöscht werden soll
     *
     * @param account Account to delete
     */
    @Override
    public void onDeleteAccountSelected(Account account) {
        if (isCurrentMainAccount(account)) {

            deleteAccount(account);

            Account newMainAccount = getNewMainAccountSafe();
            if (newMainAccount != null)
                makeAccountMain(newMainAccount);
        } else {

            deleteAccount(account);
        }

        Toast.makeText(getActivity(), getString(R.string.deleted_account), Toast.LENGTH_SHORT).show();

        dismiss();
    }

    /**
     * Methode die den Callback des AccountsAdapters implementiert, wenn ein Konto als Hauptkonto ausgewählt wurde
     *
     * @param account Konto das nun das Hautpkonto sein soll
     */
    @Override
    public void onAccountSetMain(Account account) {
        makeAccountMain(account);
        Toast.makeText(getActivity(), account.getName() + getString(R.string.changed_main_account), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        revertUserInteractions();
    }

    public void setOnAccountSelectedListener(OnSelectedAccount listener) {
        mCallback = listener;
    }

    /**
     * Methode um die ListView zu erzeugen und mit Funktionalität zu versorgen.
     */
    private void prepareListView() {

        AccountAdapter adapter = new AccountAdapter(mInitialAccountState, getActivity());
        adapter.setOnDeleteAccountListener(this);

        mListView = new ListView(getActivity());
        mListView.setAdapter(adapter);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setItemsCanFocus(false);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            CheckBox accountChk = view.findViewById(R.id.listview_account_item_chkbox);
            boolean newVisibilityState = !accountChk.isChecked();

            accountChk.setChecked(newVisibilityState);
            setAccountVisibility(getAccountAtPosition(position), newVisibilityState);
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

    private Account getNewMainAccountSafe() {
        if (mInitialAccountState.size() == 0)
            return null;

        return (Account) mInitialAccountState.keySet().toArray()[0];
    }

    /**
     * Methode um herauszufinden ob das angegebene Konto das aktuelle Hauptkonto ist.
     *
     * @param account Konto, welches überprüft werden soll
     * @return TRUE wenn es das Hauptkonto ist, FALSE wenn nicht
     */
    private boolean isCurrentMainAccount(Account account) {
        return account.equals(mUserPreferences.getActiveAccount());
    }

    /**
     * Methode um ein Konto zu löschen.
     *
     * @param account Konto, welches gelöscht werden soll
     */
    private void deleteAccount(Account account) {
        accountRepo.delete(account);

        mAccountPreferences.removeAccount(account);

        mInitialAccountState.remove(account);
    }

    /**
     * Methode um das gewählte Konto zum Hauptkonto zu machen.
     *
     * @param account Neues Hauptkonto
     */
    private void makeAccountMain(Account account) {
        mUserPreferences.setActiveAccount(account);
    }

    /**
     * Methode um die Auswahl der einzelnen Konten rückgängig zu machen, und so den ursprügnlichen Zustand wieder her zustellen.
     */
    private void revertUserInteractions() {
        for (Map.Entry<Account, Boolean> entry : mInitialAccountState.entrySet()) {
            setAccountVisibility(entry.getKey(), entry.getValue());
        }
    }

    private void setAccountVisibility(Account account, boolean isVisible) {
        mAccountPreferences.changeVisibility(account, isVisible);

        if (null != mCallback) {
            mCallback.onAccountSelected(account.getId(), isVisible);
        }
    }

    public interface OnSelectedAccount {
        void onAccountSelected(UUID accountId, boolean isChecked);
    }
}