package com.example.lucas.haushaltsmanager.MockDataGenerator;


import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RandomCategoryGenerator {
    private List<Category> mCategories;

    public RandomCategoryGenerator(Context context) {
        generateParentCategories(3, context);
        // TODO: Es sollen basierend auf der zu generierenden Kategorien, die Anzahl der zu generierenden ParentKategorien bestimmt werden

        mCategories = new CategoryRepository(context).getAll();
    }

    public void createdCategories(int count, Context context) {
        ChildCategoryRepository categoryRepository = new ChildCategoryRepository(context);

        for (; count > 0; count--) {
            mCategories.add(categoryRepository.insert(
                    makeCategory(withRandomColor()),
                    withRandomParent()
            ));
        }
    }

    private void generateParentCategories(int count, Context context) {
        RandomParentCategoryGenerator parentCategoryGenerator = new RandomParentCategoryGenerator();
        parentCategoryGenerator.createParentCategories(count, context);
    }

    private Category makeCategory(String color) {
        Random rnd = new Random();

        return new Category(
                String.format("Kategorie %s", 1),
                color,
                rnd.nextBoolean(),
                new ArrayList<Category>()
        );
    }

    private Category withRandomParent() {
        int index = new Random().nextInt(mCategories.size() - 1);

        return mCategories.get(index);
    }

    private String withRandomColor() {
        Random rnd = new Random();

        return String.format(
                Locale.GERMANY,
                "#%06d",
                rnd.nextInt(999999)
        );
    }

    private class RandomParentCategoryGenerator {
        public void createParentCategories(int count, Context context) {
            CategoryRepository categoryRepository = new CategoryRepository(context);

            for (; count >= 0; count--) {
                categoryRepository.insert(makeParentCategory(
                        withRandomColor()
                ));
            }
        }

        private Category makeParentCategory(String color) {
            return new Category(
                    "Kategorie",
                    color,
                    true,
                    new ArrayList<Category>()
            );
        }
    }
}
