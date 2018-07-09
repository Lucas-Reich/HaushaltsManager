package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

public class ChooseCategoryDialog extends DialogFragment {
    private static final String TAG = ChooseCategoryDialog.class.getSimpleName();

    private ExpensesDataSource mDatabase;
    private OnCategoryChosenListener mCallback;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        mDatabase = new ExpensesDataSource(mContext);
        mDatabase.open();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(args.getString("title"));

        final ArrayList<Category> categories = getCategories();

        builder.setSingleChoiceItems(categoriesToStrings(categories), 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int selectedCategoryIndex) {

                if (selectedCategoryIndex == 0) {
                    mCallback.onCategoryChosen(null);
                } else {
                    mCallback.onCategoryChosen(categories.get(selectedCategoryIndex));
                }

                mDatabase.close();
                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabase.close();
                dismiss();
            }
        });

        return builder.create();
    }

    private String[] categoriesToStrings(ArrayList<Category> categories) {

        String[] categoryStrings = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            categoryStrings[i] = categories.get(i).getTitle();
        }

        return categoryStrings;
    }

    private ArrayList<Category> getCategories() {
        Category placeholder = Category.createDummyCategory(mContext);
        placeholder.setName("Nicht Zugeordnet"); // todo

        ArrayList<Category> categories = new ArrayList<>();
        categories.add(placeholder);
        categories.addAll(mDatabase.getAllCategories());

        return categories;
    }

    public void setOnCategoryChosenListener(OnCategoryChosenListener callback) {
        mCallback = callback;
    }

    public interface OnCategoryChosenListener {
        void onCategoryChosen(Category category);
    }

    @Override
    public void onStop() {
        super.onStop();

        mDatabase.close();
    }
}
