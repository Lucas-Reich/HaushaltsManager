package com.example.lucas.haushaltsmanager.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ColorPickerDialog;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;

public class CreateCategoryActivity extends AppCompatActivity {
    private static final String TAG = CreateCategoryActivity.class.getSimpleName();

    private Category mCategory;
    private ImageButton mBackArrow;
    private Button mCatNameBtn, mCatColorBtn, mCreateBtn;
    private RadioGroup mDefaultExpenseRadioGrp;
    private ExpensesDataSource mDatabase;

    private creationModes CREATION_MODE;

    private enum creationModes {
        UPDATE_CATEGORY,
        CREATE_CATEGORY
    }

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_new_category);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mCatNameBtn = (Button) findViewById(R.id.new_category_name);
        mCatColorBtn = (Button) findViewById(R.id.new_category_color);
        mCreateBtn = (Button) findViewById(R.id.new_category_create);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDefaultExpenseRadioGrp = (RadioGroup) findViewById(R.id.new_category_expense_type);

        resolveIntent(getIntent().getExtras());
    }

    @SuppressWarnings("ConstantConditions")
    private void resolveIntent(Bundle bundle) {
        if (bundle == null || !bundle.containsKey("mode"))
            throw new UnsupportedOperationException("Du musst den Modus setzten!");

        switch (bundle.getString("mode")) {
            case "updateCategory":

                CREATION_MODE = creationModes.UPDATE_CATEGORY;
                mCategory = bundle.getParcelable("updateCategory");

                mCreateBtn.setText(R.string.update);
                break;
            case "createCategory":

                CREATION_MODE = creationModes.CREATE_CATEGORY;
                mCategory = Category.createDummyCategory(this);

                mCreateBtn.setText(R.string.create_category);
                break;
            default:
                throw new UnsupportedOperationException("Modus " + bundle.getString("modus") + " wird nicht unterstützt!");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mCatNameBtn.setHint(mCategory.getTitle());
        mCatNameBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("title", getResources().getString(R.string.category_name));

                BasicTextInputDialog basicDialog = new BasicTextInputDialog();
                basicDialog.setOnTextInputListener(new BasicTextInputDialog.BasicDialogCommunicator() {

                    @Override
                    public void onTextInput(String textInput) {

                        mCategory.setName(textInput);
                        mCatNameBtn.setText(textInput);

                        Log.d(TAG, "set category name to: " + textInput);
                    }
                });
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

        if (mCategory.getDefaultExpenseType())
            mDefaultExpenseRadioGrp.check(R.id.new_category_expense);
        else
            mDefaultExpenseRadioGrp.check(R.id.new_category_income);
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

                switch (CREATION_MODE) {
                    case CREATE_CATEGORY:

                        Category parent = mDatabase.createCategory(Category.createDummyCategory(CreateCategoryActivity.this));//todo dummy parent durch einen richtigen austauschen
                        mDatabase.addCategoryToParent(parent, mCategory);
                        break;
                    case UPDATE_CATEGORY:

                        mDatabase.updateCategory(mCategory);
                        break;
                }

                finish();
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
}
