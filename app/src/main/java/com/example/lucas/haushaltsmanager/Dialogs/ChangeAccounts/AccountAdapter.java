package com.example.lucas.haushaltsmanager.Dialogs.ChangeAccounts;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.example.lucas.haushaltsmanager.Activities.CreateAccountActivity;
import com.example.lucas.haushaltsmanager.Activities.TransferActivity;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account> {
    @SuppressWarnings("unused")
    private static final String TAG = AccountAdapter.class.getSimpleName();

    private OnDeleteAccountSelected mCallback;

    private static class ViewHolder {
        CheckedTextView account_chk;
        ImageView overflow_menu;
    }

    private List<Boolean> mCheckedItems = new ArrayList<>();

    AccountAdapter(ArrayList<Account> data, Context context) {
        super(context, R.layout.list_view_account_item, data);
    }

    @Override
    @NonNull
    @SuppressWarnings("ConstantConditions")
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Account account = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_account_item, parent, false);

            viewHolder.account_chk = (CheckedTextView) convertView.findViewById(R.id.list_view_account_item_account_chk);
            viewHolder.overflow_menu = (ImageView) convertView.findViewById(R.id.list_view_account_item_overflow);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.account_chk.setChecked(mCheckedItems.get(position));
        viewHolder.account_chk.setText(String.format("   %s", account.getTitle()));//mit einem linebreak (\n) kann man text in der zweiten zeile anzeigen lassen
        viewHolder.overflow_menu.setOnClickListener(new OnAccountOverflowSelectedListener(getContext(), account));

        return convertView;
    }

    /**
     * Methode um den Status der Konten zu setzen
     *
     * @param checkedItems Welche Konten sind aktiv und welche nicht
     */
    void setCheckedItems(List<Boolean> checkedItems) {

        if (checkedItems.size() != getCount())
            throw new ArrayIndexOutOfBoundsException("Not enough checkedItems!");

        mCheckedItems = checkedItems;
    }

    /**
     * Methode die den OnDeleteAccountSelected listener des aufrufenden Fragments setzt.
     * (siehe ChooseAccountsDialogFragment)
     *
     * @param listener Callback listener
     */
    void setOnDeleteAccountListener(OnDeleteAccountSelected listener) {

        mCallback = listener;
    }

    public interface OnDeleteAccountSelected {
        void onDeleteAccountSelected(Account account);
        void onAccountSetMain(Account account);
    }

    /**
     * Klasse die sich mit dem Overflow menu befasst und die aktionen auslöst,
     * die bei einem click auf ein MenuItem ausgelöst wird.
     */
    public class OnAccountOverflowSelectedListener implements View.OnClickListener {

        private String TAG = OnAccountOverflowSelectedListener.class.getSimpleName();

        private Account mAccount;
        private Context mContext;

        OnAccountOverflowSelectedListener(Context context, Account account) {

            mAccount = account;
            mContext = context;
        }

        /**
         * Bei einem Klick auf die drei Punkte soll ein PopupMenu erzeugt werden,
         * indem optionen sind um ein Konto zu bearbeiten.
         *
         * @param view View die die Aktion ausgelöst hat
         */
        @Override
        public void onClick(View view) {

            PopupMenu popupMenu = new PopupMenu(mContext, view);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.edit_account_delete:

                            //user wird nicht noch einmal um betstätigung gefragt!
                            mCallback.onDeleteAccountSelected(mAccount);

                            Log.d(TAG, "onMenuItemClick: delete selected");
                            return true;
                        case R.id.edit_account_edit:

                            Intent updateAccountIntent = new Intent(mContext, CreateAccountActivity.class);
                            updateAccountIntent.putExtra("mode", "update");
                            updateAccountIntent.putExtra("account_id", mAccount.getIndex());
                            mContext.startActivity(updateAccountIntent);

                            Log.d(TAG, "onMenuItemSelected: edit selected");
                            return true;
                        case R.id.edit_account_transfer:

                            Intent transferMoneyBetweenAccountsIntent = new Intent(mContext, TransferActivity.class);
                            transferMoneyBetweenAccountsIntent.putExtra("from_account", mAccount);
                            mContext.startActivity(transferMoneyBetweenAccountsIntent);

                            Log.d(TAG, "onMenuItemSelected: empty selected");
                            return true;
                        case R.id.edit_account_set_main:

                            mCallback.onAccountSetMain(mAccount);

                            Log.d(TAG, "onMenuItemClick: make main selected");
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
}
