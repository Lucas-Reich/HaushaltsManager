package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

public class ChooseCategoryDialog extends DialogFragment {
    private static final String TAG = ChooseCategoryDialog.class.getSimpleName();

    private OnCategoryChosenListener mCallback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
        Category placeholder = Category.createDummyCategory();
        placeholder.setName(app.getContext().getString(R.string.no_assignment));

        ArrayList<Category> categories = new ArrayList<>();
        categories.add(placeholder);
        categories.addAll(CategoryRepository.getAll());

        return categories;
    }

    public void setOnCategoryChosenListener(OnCategoryChosenListener callback) {
        mCallback = callback;
    }

    public interface OnCategoryChosenListener {
        void onCategoryChosen(Category category);
    }
}
