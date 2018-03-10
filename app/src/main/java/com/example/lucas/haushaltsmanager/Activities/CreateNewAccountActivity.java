package com.example.lucas.haushaltsmanager.Activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.lucas.haushaltsmanager.Activities.MainTab.TabParentActivity;
import com.example.lucas.haushaltsmanager.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class CreateNewAccountActivity extends AppCompatActivity implements OnItemSelectedListener, BasicTextInputDialog.BasicDialogCommunicator {

    SharedPreferences preferences;
    Button accountName;
    Button accountBalance;
    ExpensesDataSource database;
    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        database = new ExpensesDataSource(this);
        database.open();


        Currency defCurrency = database.getCurrency(preferences.getLong("mainCurrencyIndex", 1));
        account = new Account(getResources().getString(R.string.no_name), null, defCurrency);

        setContentView(R.layout.activity_new_account);

        accountName = (Button) findViewById(R.id.new_account_name);
        accountName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("title", "Set Account name");

                DialogFragment basicDialog = new BasicTextInputDialog();
                basicDialog.setArguments(args);
                basicDialog.show(getFragmentManager(), "accountName");
            }
        });

        accountBalance = (Button) findViewById(R.id.new_account_balance);
        accountBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("title", "Set Account Balance");

                DialogFragment basicDialog = new BasicTextInputDialog();
                basicDialog.setArguments(args);
                basicDialog.show(getFragmentManager(), "accountBalance");
            }
        });

        Button createAccount = (Button) findViewById(R.id.new_account_create);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences settings = getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                editor.putBoolean(account.getAccountName(), true);
                editor.apply();

                database.createAccount(account);
                Intent startMainTab = new Intent(CreateNewAccountActivity.this, TabParentActivity.class);
                CreateNewAccountActivity.this.startActivity(startMainTab);
            }
        });

        Spinner accountCurrency = (Spinner) findViewById(R.id.new_account_currency);

        accountCurrency.setOnItemSelectedListener(this);

        ArrayList<Currency> currencies = database.getAllCurrencies();

        List<String> currencyShortNames = new ArrayList<>();
        for (Currency currency : currencies) {

            currencyShortNames.add(currency.getCurrencyShortName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyShortNames);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        accountCurrency.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String currencyName = parent.getItemAtPosition(position).toString();

        this.account.setCurrency(database.getCurrency(currencyName));
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public void onTextInput(String data, String tag) {

        if (tag.equals("accountName")) {

            account.setAccountName(data);

            accountName = (Button) findViewById(R.id.new_account_name);
            accountName.setText(data);
        } else {

            account.setBalance(Integer.parseInt(data));

            accountBalance = (Button) findViewById(R.id.new_account_balance);
            accountBalance.setText(data);
        }
    }
}
