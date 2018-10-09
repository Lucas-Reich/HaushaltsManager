package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.AbstractAppCompatActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.util.Calendar;

public class CreateRecurringBooking extends AbstractAppCompatActivity {
    public static final String INTENT_BOOKING = "expense";

    private ExpenseObject mRecurringExpense;
    private Calendar mStartDate;
    private Calendar mEndDate;
    private int mFrequency;

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

    private void setFrequency(int frequency) {
        mFrequency = frequency;
    }

    private void setEndDate(Calendar endDate) {
        mEndDate = endDate;
    }

    private void setStartDate(Calendar startDate) {
        mStartDate = startDate;
    }

    private void saveRecurringBooking() {
        if (mStartDate != null && mEndDate != null && mEndDate.after(mStartDate)) {
            RecurringBookingRepository recurringBookingRepo = new RecurringBookingRepository(this);
            recurringBookingRepo.insert(
                    mRecurringExpense,
                    mStartDate.getTimeInMillis(),
                    mFrequency,
                    mEndDate.getTimeInMillis()
            );
        } else {

            //todo übersetzung
            Toast.makeText(this, "Cannot save RecurringBooking", Toast.LENGTH_SHORT).show();
        }
    }
}
