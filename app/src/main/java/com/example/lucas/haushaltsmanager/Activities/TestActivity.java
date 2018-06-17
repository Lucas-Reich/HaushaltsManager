package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.test_activity);

        Button button = (Button) findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectMapper mapper = new ObjectMapper();

                TextView textView = (TextView) findViewById(R.id.textView);
                try {
                    textView.setText(mapper.writeValueAsString(ExpenseObject.createDummyExpense(TestActivity.this)));
                    Log.d("Penis", mapper.writeValueAsString(ExpenseObject.createDummyExpense(TestActivity.this)));
                } catch (JsonProcessingException e) {
                    // do nothing
                }
            }
        });
    }
}