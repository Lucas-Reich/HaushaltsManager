package com.example.lucas.haushaltsmanager;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class CreateNewCategoryActivity extends AppCompatActivity implements BasicDialog.BasicDialogCommunicator {

    private String TAG = CreateNewCategoryActivity.class.getSimpleName();
    private Category CATEGORY;
    Button categoryNameBtn, categoryColorBtn, createCategoryBtn;
    RadioGroup expenseType;
    ExpensesDataSource database;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.activity_new_category);

        CATEGORY = new Category(getResources().getString(R.string.no_name), "#000000", false);
        database = new ExpensesDataSource(this);

        categoryNameBtn = (Button) findViewById(R.id.new_category_name);
        categoryNameBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("title", getResources().getString(R.string.category_name));

                DialogFragment basicDialog = new BasicDialog();
                basicDialog.setArguments(args);
                basicDialog.show(getFragmentManager(), "categoryName");
            }
        });

        categoryColorBtn = (Button) findViewById(R.id.new_category_color);
        categoryColorBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ColorPickerDialog dialog = new ColorPickerDialog(CreateNewCategoryActivity.this, Color.WHITE, new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {

                        CATEGORY.setColor(color);
                        Log.d(TAG, "set CATEGORY color to: " + Integer.toHexString(color));
                    }
                });
                dialog.show();
            }
        });

        expenseType = (RadioGroup) findViewById(R.id.new_category_expense_type);
        expenseType.check(R.id.new_category_expense);
        expenseType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.new_category_expense) {

                    CATEGORY.setDefaultExpenseType(true);
                } else {

                    CATEGORY.setDefaultExpenseType(false);
                }

                Log.d(TAG, "set expense type to : " + CATEGORY.getDefaultExpenseType());
            }
        });

        createCategoryBtn = (Button) findViewById(R.id.new_category_create);
        createCategoryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                database.open();
                database.createCategory(CATEGORY);
                database.close();

                Intent startCategories = new Intent(CreateNewCategoryActivity.this, Categories.class);
                CreateNewCategoryActivity.this.startActivity(startCategories);
            }
        });
    }

    @Override
    public void onTextInput(String data, String tag) {

        CATEGORY.setCategoryName(data);
        categoryNameBtn.setText(data);

        Log.d(TAG, "set CATEGORY name to: " + data);
    }
}
