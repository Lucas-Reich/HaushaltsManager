package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ColorPickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.entities.Booking.ExpenseType;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreateCategory extends AbstractAppCompatActivity {
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_MODE_UPDATE = "update_category";
    public static final String INTENT_MODE_CREATE = "create_category";

    public static final String INTENT_CATEGORY = "category";

    private Category mCategory;
    private Button mTitleBtn, mExpenseBtn, mIncomeBtn;
    private RoundedTextView mColorView;
    private FloatingActionButton mSaveFAB;

    private CategoryDAO categoryRepo;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_new_category);

        categoryRepo = AppDatabase.getDatabase(this).categoryDAO();

        initializeToolbar();

        mTitleBtn = findViewById(R.id.create_category_title);
        mColorView = findViewById(R.id.create_category_color);

        mIncomeBtn = findViewById(R.id.create_category_income);
        mExpenseBtn = findViewById(R.id.create_category_expense);

        mSaveFAB = findViewById(R.id.create_category_save);

        resolveIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mTitleBtn.setHint(mCategory.getName());
        mTitleBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(BasicTextInputDialog.TITLE, getString(R.string.category_name));
            bundle.putString(BasicTextInputDialog.HINT, mCategory.getName());

            BasicTextInputDialog nameDialog = new BasicTextInputDialog();
            nameDialog.setArguments(bundle);
            nameDialog.setOnTextInputListener(this::setCategoryTitle);
            nameDialog.show(getFragmentManager(), "create_category_name");
        });

        setColor(mCategory.getColor());
        mColorView.setOnClickListener(v -> {
            ColorPickerDialog colorPickerDialog = new ColorPickerDialog(CreateCategory.this, Color.WHITE);
            colorPickerDialog.setOnColorSelectedListener(this::setColor);
            colorPickerDialog.show();
        });

        setExpenditureType(mCategory.getDefaultExpenseType());

        mIncomeBtn.setOnClickListener(v -> setExpenditureType(ExpenseType.Companion.deposit()));

        mExpenseBtn.setOnClickListener(v -> setExpenditureType(ExpenseType.Companion.expense()));

        mSaveFAB.setOnClickListener(getOnSaveClickListener());
    }

    private void resolveIntent() {
        BundleUtils bundle = new BundleUtils(getIntent().getExtras());

        switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE)) {
            case INTENT_MODE_UPDATE:
                mCategory = (Category) bundle.getParcelable(INTENT_CATEGORY, null);
                break;
            case INTENT_MODE_CREATE:
                mCategory = new Category(
                        getString(R.string.no_name),
                        Color.Companion.black(),
                        ExpenseType.Companion.expense()
                );
                break;
            default:
                throw new UnsupportedOperationException("Could not handle intent mode " + bundle.getString(INTENT_MODE, null));
        }
    }

    private View.OnClickListener getOnSaveClickListener() {
        return v -> {
            if (!isCategorySavable()) {
                showCloseScreenDialog();
                return;
            }

            BundleUtils bundle = new BundleUtils(getIntent().getExtras());

            switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE)) {
                case INTENT_MODE_CREATE:
                    categoryRepo.insert(mCategory);
                    break;
                case INTENT_MODE_UPDATE:
                    categoryRepo.update(mCategory);
                    break;
            }

            finish();
        };
    }

    private void showCloseScreenDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(ConfirmationDialog.TITLE, getString(R.string.attention));
        bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.abort_action_confirmation_text));

        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        confirmationDialog.setArguments(bundle);
        confirmationDialog.setOnConfirmationListener(closeScreen -> {
            if (closeScreen)
                finish();
        });
        confirmationDialog.show(getFragmentManager(), "create_category_exit");
    }

    private void setCategoryTitle(String title) {
        mCategory.setName(title);
        mTitleBtn.setText(mCategory.getName());

        if (isCategorySavable()) {
            runCrossToCheckAnimation();
        } else {
            runCheckToCrossAnimation();
        }
    }

    private void setColor(Color color) {
        mCategory.setColor(color);
        mColorView.setCircleColor(mCategory.getColor().getColor());
    }

    private void setExpenditureType(ExpenseType expenditureType) {
        mCategory.setDefaultExpenseType(expenditureType);

        LinearLayout ll = findViewById(R.id.create_category_bottom_bar);
        ll.setBackgroundColor(expenditureType.getType()
                ? getColorRes(R.color.booking_expense)
                : getColorRes(R.color.booking_income)
        );
    }

    private void runCrossToCheckAnimation() {
        // TODO animate transition
        mSaveFAB.setImageDrawable(getDrawableRes(R.drawable.ic_check_white_24dp));
    }

    private void runCheckToCrossAnimation() {
        // TODO animate transition
        mSaveFAB.setImageDrawable(getDrawableRes(R.drawable.ic_cross_white));
    }

    private boolean isCategorySavable() {
        return mCategory.isSet();
    }
}
