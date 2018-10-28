package com.example.lucas.haushaltsmanager.Activities.CategoryList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.AbstractAppCompatActivity;
import com.example.lucas.haushaltsmanager.Activities.CreateCategory;
import com.example.lucas.haushaltsmanager.CategoryAdapter;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;

import java.util.List;

import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_CATEGORY;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_MODE;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_MODE_UPDATE;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_PARENT;

public class CategoryList extends AbstractAppCompatActivity {
    private static final String TAG = CategoryList.class.getSimpleName();

    private FloatingActionButton mFabMain, mFabDelete;
    private ExpandableListView mExpListView;
    private CategoryAdapter mListAdapter;

    // TODO sollte ich statdessen die FABToolbar implementieren?
    private Animation openFabAnim, closeFabAnim, rotateForwardAnim, rotateBackwardAnim;
    private boolean mIsMainFabAnimated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        initializeToolbar();

        mExpListView = findViewById(R.id.categories_exp_list_view);

        mFabMain = findViewById(R.id.categories_fab);

        mFabDelete = findViewById(R.id.categories_fab_delete);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListView();

        mExpListView.setBackgroundColor(Color.WHITE);
        mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Category clickedCategory = (Category) mListAdapter.getChild(groupPosition, childPosition);

                if (areChildrenSelected()) {

                    if (mListAdapter.isChildSelected(clickedCategory)) {

                        v.setBackgroundColor(Color.WHITE);
                        mListAdapter.deselectChild(clickedCategory);
                        animateFab(mListAdapter.getSelectedChildItemCount());
                    } else {
                        v.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));
                        mListAdapter.selectChild(clickedCategory);
                    }
                } else {

                    if (getCallingActivity() != null) {

                        Intent returnCategoryIntent = new Intent();
                        returnCategoryIntent.putExtra("categoryObj", clickedCategory);
                        setResult(Activity.RESULT_OK, returnCategoryIntent);
                        finish();
                    } else {

                        Intent updateCategoryIntent = new Intent(CategoryList.this, CreateCategory.class);
                        updateCategoryIntent.putExtra(INTENT_MODE, INTENT_MODE_UPDATE);
                        updateCategoryIntent.putExtra(INTENT_CATEGORY, clickedCategory);
                        updateCategoryIntent.putExtra(INTENT_PARENT, (Category) mListAdapter.getGroup(groupPosition));
                        CategoryList.this.startActivity(updateCategoryIntent);
                    }
                }

                return true;
            }
        });

        mExpListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                switch (ExpandableListView.getPackedPositionType(id)) {

                    case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
                        // do nothing

                        return false;
                    case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
                        Category childCategory = (Category) mListAdapter.getChild(groupPosition, childPosition);

                        mListAdapter.selectChild(childCategory);
                        view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));

                        disableLongClick();
                        animateFab(mListAdapter.getSelectedChildItemCount());

                        return true;
                    default:

                        return false;
                }
            }
        });

        mFabDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ChildCategoryRepository childRepo = new ChildCategoryRepository(CategoryList.this);

                try {

                    for (Category childCategory : mListAdapter.getSelectedChildData())
                        childRepo.delete(childCategory);

                    animateFab(mListAdapter.getSelectedChildItemCount());
                    updateListView();
                } catch (CannotDeleteChildCategoryException e) {

                    //todo ich sollte den try catch nur um die for schleife machen und die categorien die nicht gelöscht werden konnten speichern und etwas mit ihnen machen
                    Toast.makeText(CategoryList.this, getString(R.string.failed_to_delete_category), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mFabMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (areChildrenSelected()) {

                    resetActivityViewState();
                } else {

                    Intent createCategoryIntent = new Intent(CategoryList.this, CreateCategory.class);
                    createCategoryIntent.putExtra(CreateCategory.INTENT_MODE, CreateCategory.INTENT_MODE_CREATE);
                    CategoryList.this.startActivity(createCategoryIntent);
                }
            }
        });


        openFabAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        closeFabAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForwardAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackwardAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
    }

    private void resetActivityViewState() {

        mListAdapter.deselectAll();
        enableLongClick();
        mListAdapter.notifyDataSetChanged();
        animateFab(mListAdapter.getSelectedChildItemCount());
    }

    private boolean areChildrenSelected() {

        return mListAdapter.getSelectedChildItemCount() > 0;
    }

    /**
     * Helper Methode um den Longclick der ExpandableListView zu deaktivieren
     */
    private void disableLongClick() {
        mExpListView.setLongClickable(false);
    }

    /**
     * Helper Methode um den Longclick der ExpandableListView zu aktivieren
     */
    private void enableLongClick() {
        mExpListView.setLongClickable(true);
    }

    /**
     * Methode um die ListView nach einer Änderung anzuzeigen
     */
    private void updateListView() {

        mListAdapter = new CategoryAdapter(getAllParentCategories(), this);

        mExpListView.setAdapter(mListAdapter);

        mListAdapter.notifyDataSetChanged();
    }

    private List<Category> getAllParentCategories() {
        CategoryRepository categoryRepo = new CategoryRepository(this);
        return categoryRepo.getAll();
    }

    private void animateFab(int selectedChildrenCount) {

        if (selectedChildrenCount == 0) {
            animatePlusClose();
            closeFabDelete();
        } else {

            animatePlusOpen();
            openFabDelete();
        }
    }

    /**
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um 45°.
     */
    public void animatePlusOpen() {
        if (!mIsMainFabAnimated) {
            mFabMain.startAnimation(rotateForwardAnim);
            mIsMainFabAnimated = true;
        }
    }

    /**
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um -45°.
     */
    public void animatePlusClose() {
        if (mIsMainFabAnimated) {
            mFabMain.startAnimation(rotateBackwardAnim);
            mIsMainFabAnimated = false;
        }
    }

    /**
     * Methode die den KombinierFab sichtbar und anklickbar macht.
     */
    public void openFabDelete() {
        showFab(mFabDelete);
    }

    /**
     * Methode die den KombinierFab unsichtbar und nicht mehr anklickbar macht.
     */
    public void closeFabDelete() {
        closeFab(mFabDelete);
    }

    /**
     * Methode um einen FloatingActionButton anzuzeigen.
     *
     * @param fab FAB
     */
    public void showFab(FloatingActionButton fab) {

        if (fab.getVisibility() != View.VISIBLE) {

            fab.setVisibility(View.VISIBLE);
            fab.startAnimation(openFabAnim);
            fab.setClickable(true);
        }
    }

    /**
     * Methode um einen FloatingActinButton zu verstecken.
     *
     * @param fab FAB
     */
    public void closeFab(FloatingActionButton fab) {

        if (fab.getVisibility() != View.GONE) {

            fab.setVisibility(View.GONE);
            fab.startAnimation(closeFabAnim);
            fab.setClickable(false);
        }
    }
}
