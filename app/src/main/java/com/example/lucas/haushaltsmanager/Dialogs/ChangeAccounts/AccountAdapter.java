package com.example.lucas.haushaltsmanager.Dialogs.ChangeAccounts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account> {

    private static String TAG = AccountAdapter.class.getSimpleName();

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

        viewHolder.account_chk.setText(String.format("   %s", account.getAccountName()));
        viewHolder.account_chk.setChecked(mCheckedItems.get(position));
        viewHolder.overflow_menu.setOnClickListener(new OnAccountOverflowSelectedListener(getContext(), account));

        return convertView;
    }

    void setCheckedItems(List<Boolean> checkedItems) {

        if (checkedItems.size() != getCount())
            throw new ArrayIndexOutOfBoundsException("Not enough checkedItems!");

        mCheckedItems = checkedItems;
    }
}
