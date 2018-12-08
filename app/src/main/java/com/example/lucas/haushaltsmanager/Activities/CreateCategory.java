package com.example.lucas.haushaltsmanager.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ColorPickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.SingleChoiceDialog;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Views.RoundedTextView;

import java.util.ArrayList;

public class CreateCategory extends AbstractAppCompatActivity {
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_MODE_UPDATE = "update_category";
    public static final String INTENT_MODE_CREATE = "create_category";

    public static final String INTENT_PARENT = "parent";
    public static final String INTENT_CATEGORY = "category";

    private Category mCategory, mParentCategory;
    private Button mTitleBtn, mParentBtn, mExpenseBtn, mIncomeBtn;
    private RoundedTextView mColorView;
    private FloatingActionButton mSaveFAB;
    private boolean parentCategoryCreated = false;

    private CategoryRepository mCategoryRepo;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_new_category);

        mCategoryRepo = new CategoryRepository(this);

        initializeToolbar();

        mTitleBtn = findViewById(R.id.create_category_title);
        mColorView = findViewById(R.id.create_category_color);
        mParentBtn = findViewById(R.id.create_category_parent);

        mIncomeBtn = findViewById(R.id.create_category_income);
        mExpenseBtn = findViewById(R.id.create_category_expense);

        mSaveFAB = findViewById(R.id.create_category_save);

        resolveIntent();
    }

    private void resolveIntent() {
        BundleUtils bundle = new BundleUtils(getIntent().getExtras());

        switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE)) {
            case INTENT_MODE_UPDATE:
                mParentCategory = (Category) bundle.getParcelable(INTENT_PARENT, null);
                mCategory = (Category) bundle.getParcelable(INTENT_CATEGORY, null);
                break;
            case INTENT_MODE_CREATE:
                mCategory = Category.createDummyCategory();
                mCategory.setDefaultExpenseType(true);
                break;
            default:
                throw new UnsupportedOperationException("Could not handle intent mode " + bundle.getString(INTENT_MODE, null));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mTitleBtn.setHint(mCategory.getTitle());
        mTitleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BasicTextInputDialog.TITLE, getResources().getString(R.string.category_name));
                bundle.putString(BasicTextInputDialog.HINT, mCategory.getTitle());

                BasicTextInputDialog nameDialog = new BasicTextInputDialog();
                nameDialog.setArguments(bundle);
                nameDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {
                    @Override
                    public void onTextInput(String textInput) {
                        setCategoryTitle(textInput);
                    }
                });
                nameDialog.show(getFragmentManager(), "create_category_name");
            }
        });

        setColor(mCategory.getColorInt());
        mColorView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(CreateCategory.this, Color.WHITE);
                colorPickerDialog.setOnColorSelectedListener(new ColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        setColor(color);
                    }
                });
                colorPickerDialog.show();
            }
        });

        mParentBtn.setHint(getString(R.string.choose_parent_category_hint));
        if (mParentCategory != null)
            setParent(mParentCategory);
        mParentBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SingleChoiceDialog<Category> categoryPicker = new SingleChoiceDialog<>();
                categoryPicker.createBuilder(CreateCategory.this);
                categoryPicker.setTitle(getString(R.string.choose_parent_category));
                categoryPicker.setContent(mCategoryRepo.getAll(), -1);
                categoryPicker.setNeutralButton(getString(R.string.create_new));
                categoryPicker.setOnEntrySelectedListener(new SingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onPositiveClick(Object category) {
                        setParent((Category) category);
                    }

                    @Override
                    public void onNeutralClick() {
                        Bundle bundle = new Bundle();
                        bundle.putString(BasicTextInputDialog.TITLE, getString(R.string.new_parent_category_name));

                        BasicTextInputDialog textInputDialog = new BasicTextInputDialog();
                        textInputDialog.setArguments(bundle);
                        textInputDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {
                            @Override
                            public void onTextInput(String categoryTitle) {
                                setParent(mCategoryRepo.insert(new Category(
                                        categoryTitle,
                                        "#000000",
                                        false,
                                        new ArrayList<Category>()))
                                );

                                parentCategoryCreated = true;
                            }
                        });
                        textInputDialog.show(getFragmentManager(), "create_category_parent_name");
                    }
                });
                categoryPicker.show(getFragmentManager(), "create_category_parent");
            }
        });

        setExpenditureType(mCategory.getDefaultExpenseType());

        mIncomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpenditureType(false);
            }
        });

        mExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpenditureType(true);
            }
        });

        mSaveFAB.setOnClickListener(getOnSaveClickListener());
    }

    private View.OnClickListener getOnSaveClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCategorySavable()) {
                    showCloseScreenDialog();
                    return;
                }

                BundleUtils bundle = new BundleUtils(getIntent().getExtras());

                switch (bundle.getString(INTENT_MODE, INTENT_MODE_CREATE)) {
                    case INTENT_MODE_CREATE:
                        if (parentCategoryCreated)
                            setParentCategoryColor();

                        ChildCategoryRepository childRepo = new ChildCategoryRepository(CreateCategory.this);
                        childRepo.insert(mParentCategory, mCategory);
                        break;
                    case INTENT_MODE_UPDATE:

                        try {
                            mCategoryRepo.update(mCategory);
                        } catch (CategoryNotFoundException e) {

                            Toast.makeText(CreateCategory.this, getString(R.string.category_not_found), Toast.LENGTH_SHORT).show();
                            // TODO: Fehlerbehandlung wenn versucht wird eine nicht existierende Kategorie zu updaten
                        }
                        break;
                }

                finish();
            }
        };
    }

    private void setParentCategoryColor() {
        try {

            mParentCategory.setColor(mCategory.getColorInt());
            mCategoryRepo.update(mParentCategory);
        } catch (CategoryNotFoundException e) {

            // TODO: Was sollte passieren, wenn die ParentKategorie nicht gefunden werden konnte?
        }
    }

    private void showCloseScreenDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(ConfirmationDialog.TITLE, getString(R.string.attention));
        bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.abort_action_confirmation_text));

        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        confirmationDialog.setArguments(bundle);
        confirmationDialog.setOnConfirmationListener(new ConfirmationDialog.OnConfirmationResult() {
            @Override
            public void onConfirmationResult(boolean closeScreen) {
                if (closeScreen)
                    finish();
            }
        });
        confirmationDialog.show(getFragmentManager(), "create_category_exit");
    }

    private void setCategoryTitle(String title) {
        mCategory.setName(title);
        mTitleBtn.setText(mCategory.getTitle());

        if (isCategorySavable())
            runCrossToCheckAnimation();
        else
            runCheckToCrossAnimation();
    }

    private void setParent(Category parent) {
        mParentCategory = parent;
        mParentBtn.setText(mParentCategory.getTitle());

        if (isCategorySavable())
            runCrossToCheckAnimation();
        else
            runCheckToCrossAnimation();
    }

    private void setColor(int color) {
        mCategory.setColor(color);
        mColorView.setCircleColor(mCategory.getColorString());
    }

    private void setExpenditureType(boolean expenditureType) {
        mCategory.setDefaultExpenseType(expenditureType);

        LinearLayout ll = findViewById(R.id.create_category_bottom_bar);
        ll.setBackgroundColor(expenditureType
                ? getResources().getColor(R.color.booking_expense)
                : getResources().getColor(R.color.booking_income)
        );
    }

    private void runCrossToCheckAnimation() {
        // TODO animate transition
        mSaveFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white_24dp));
    }

    private void runCheckToCrossAnimation() {
        // TODO animate transition
        mSaveFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_cross_white));
    }

    private boolean isCategorySavable() {
        return mCategory.isSet() && mParentCategory != null;
    }
}
