package com.example.lucas.haushaltsmanager.Activities;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ColorPickerDialog;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;

public class CreateCategoryActivity extends AppCompatActivity implements BasicTextInputDialog.BasicDialogCommunicator {
    private static final String TAG = CreateCategoryActivity.class.getSimpleName();

    private Category mCategory;
    private Button mCatNameBtn, mCatColorBtn, mCreateBtn;
    private RadioGroup mDefaultExpenseRadioGrp;
    private ExpensesDataSource mDatabase;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_new_category);

        mCategory = Category.createDummyCategory(this);
        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mCatNameBtn = (Button) findViewById(R.id.new_category_name);
        mCatColorBtn = (Button) findViewById(R.id.new_category_color);
        mCreateBtn = (Button) findViewById(R.id.new_category_create);

        mDefaultExpenseRadioGrp = (RadioGroup) findViewById(R.id.new_category_expense_type);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCatNameBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("title", getResources().getString(R.string.category_name));

                DialogFragment basicDialog = new BasicTextInputDialog();
                basicDialog.setArguments(args);
                basicDialog.show(getFragmentManager(), "categoryName");
            }
        });

        mCatColorBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ColorPickerDialog dialog = new ColorPickerDialog(CreateCategoryActivity.this, Color.WHITE, new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {

                        mCategory.setColor(color);
                        Log.d(TAG, "set category color to: " + Integer.toHexString(color));
                    }
                });
                dialog.show();
            }
        });

        mDefaultExpenseRadioGrp.check(R.id.new_category_expense);
        mCategory.setDefaultExpenseType(true);
        mDefaultExpenseRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.new_category_expense) {

                    mCategory.setDefaultExpenseType(true);
                } else {

                    mCategory.setDefaultExpenseType(false);
                }

                Log.d(TAG, "set expense type to : " + mCategory.getDefaultExpenseType());
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!mCatNameBtn.getText().toString().isEmpty()) {
                    mDatabase.createCategory(mCategory);
                    finish();
                } else {

                    Toast.makeText(CreateCategoryActivity.this, getResources().getString(R.string.error_category_name_missing), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.close();
    }

    @Override
    public void onTextInput(String textInput, String tag) {

        mCategory.setName(textInput);
        mCatNameBtn.setText(textInput);

        Log.d(TAG, "set category name to: " + textInput);
    }
}
