package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.lucas.haushaltsmanager.AbstractAppCompatActivity;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

public class CreateRecurringBooking extends AbstractAppCompatActivity {
    public static final String INTENT_BOOKING = "expense";

    private ExpenseObject mRecurringExpense;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recurring_booking);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle)
            mRecurringExpense = bundle.getParcelable(INTENT_BOOKING);

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO den user nach den Information für die wiederkerhende Buchung fragen
        //getStartTimeInMillis

        //getFrequencyInInt

        //getEndTimeInMillis
    }
}
