package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.ExpenseScreen;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.ExpandableListAdapter;
import com.example.lucas.haushaltsmanager.ExpandableListAdapterCreator;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.TabOneFabToolbar;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabOneBookings extends Fragment implements TabOneFabToolbar.OnFabToolbarMenuItemClicked {
    private static final String TAG = TabOneBookings.class.getSimpleName();

    private ExpandableListAdapter mListAdapter;
    private ExpandableListView mExpListView;
    private FloatingActionButton fabBig, fabSmallTop, fabSmallLeft;
    private Animation openFabAnim, rotateForwardAnim;
    private boolean fabBigIsAnimated = false;
    private ParentActivity mParent;
    private AccountRepository mAccountRepo;
    private ChildExpenseRepository mChildExpenseRepo;
    private ExpenseRepository mBookingRepo;

    private TabOneFabToolbar mTabOneFabToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParent = (ParentActivity) getActivity();

        mAccountRepo = new AccountRepository(mParent);
        mChildExpenseRepo = new ChildExpenseRepository(mParent);
        mBookingRepo = new ExpenseRepository(mParent);
    }

    /**
     * https://www.captechconsulting.com/blogs/android-expandablelistview-magic
     * Anleitung um eine ExpandableListView ohne indicators zu machen
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        mExpListView = rootView.findViewById(R.id.expandable_list_view);
        mExpListView.setBackgroundColor(Color.WHITE);
        setOnGroupClickListener();
        setOnChildClickListener();
        setOnItemLongClickListener();

        updateListView();

        mTabOneFabToolbar = new TabOneFabToolbar(
                (FABToolbarLayout) rootView.findViewById(R.id.fabtoolbar),
                getContext(),
                this
        );

        return rootView;
    }

    private List<ExpenseObject> getSelectedBookings(HashMap<ExpenseObject, List<ExpenseObject>> bookings) {
        List<ExpenseObject> selectedBookings = new ArrayList<>();
        for (Map.Entry<ExpenseObject, List<ExpenseObject>> booking : bookings.entrySet()) {
            if (booking.getValue().size() != 0) {
                selectedBookings.addAll(booking.getValue());
            } else {
                selectedBookings.add(booking.getKey());
            }
        }

        return selectedBookings;
    }

    private void deleteBookings(HashMap<ExpenseObject, List<ExpenseObject>> bookings) {
        for (Map.Entry<ExpenseObject, List<ExpenseObject>> booking : bookings.entrySet()) {
            if (booking.getValue().size() != 0) {
                for (ExpenseObject child : booking.getValue()) {
                    try {
                        mChildExpenseRepo.delete(child);
                    } catch (CannotDeleteChildExpenseException e) {
                        //todo was soll ich machen wenn ein kind nicht gelöscht werden konnte
                    }
                }
            } else {
                try {
                    mBookingRepo.delete(booking.getKey());
                } catch (CannotDeleteExpenseException e) {
                    //todo was soll ich machen wenn ich eine group buchung nicht gelöscht werden konnte
                }
            }
        }
    }

    /**
     * Methode um herauszufinden, ob wir und im extractChildrenMode befinden.
     *
     * @return True wenn dem so ist, False wenn nicht
     */
    private boolean extractChildMode() {

        return mListAdapter.getSelectedChildCount() > 0
                && mListAdapter.getSelectedParentCount() == 0
                && mListAdapter.getSelectedGroupCount() == 0;
    }

    /**
     * Methode um herauszufinden, ob wir uns im combineBookingsMode sind.
     *
     * @return True wenn dem so ist, False wenn nicht
     */
    private boolean combineBookingsMode() {

        boolean areGroupsSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() == 0
                && mListAdapter.getSelectedGroupCount() > 1;

        boolean areParentsSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() > 1
                && mListAdapter.getSelectedGroupCount() == 0;

        boolean areParentAndGroupsSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() >= 1
                && mListAdapter.getSelectedGroupCount() >= 1;

        return areGroupsSelected || areParentsSelected || areParentAndGroupsSelected;
    }

    /**
     * Methode um herauszufinden, ob wir momentan im addChildMode sind.
     *
     * @return True wenn dem so ist, False wenn nicht
     */
    private boolean addChildMode() {

        boolean isGroupSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() == 0
                && mListAdapter.getSelectedGroupCount() == 1;

        boolean isParentSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() == 1
                && mListAdapter.getSelectedGroupCount() == 0;

        return isGroupSelected ^ isParentSelected;
    }

    /**
     * Methode um herauszufinden ob der User Elemente in der Liste ausgewählt hat oder nicht.
     *
     * @return True wenn Elemente ausgewählt sind, False wenn nicht
     */
    private boolean hasUserSelectedItems() {
        return mListAdapter.getSelectedBookingsCount() != 0;
    }

    /**
     * Methode um einen LongClickListener auf die ExpandableListView zu setzen.
     */
    private void setOnItemLongClickListener() {
        mExpListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                mListAdapter.selectItem(groupPosition, childPosition != -1 ? childPosition : null);

                disableLongClick();

//                fillToolbar();

                mTabOneFabToolbar.show();

                return true;
            }
        });
    }

    /**
     * Methode um einen GroupClickListener auf die ExpandableListView zu setzen.
     */
    private void setOnGroupClickListener() {
        mExpListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {
                ExpenseObject groupExpense = (ExpenseObject) mListAdapter.getGroup(groupPosition);

                if (noBookingsSelected() && isEditable(groupExpense)) {

                    Intent updateParentExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
                    updateParentExpenseIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_UPDATE_PARENT);
                    updateParentExpenseIntent.putExtra(ExpenseScreen.INTENT_BOOKING, groupExpense);

                    startActivity(updateParentExpenseIntent);

                    return true;
                }

                switch (groupExpense.getExpenseType()) {
                    case NORMAL_EXPENSE:
                    case PARENT_EXPENSE:
                    case CHILD_EXPENSE:
                        if (mListAdapter.isItemSelected(groupPosition, null)) {
                            mListAdapter.deselectItem(groupPosition, null);

                            if (noBookingsSelected())
                                enableLongClick();
                        } else
                            mListAdapter.selectItem(groupPosition, null);

                        //todo update toolbar items
                        changeToolbarMenuItems();
                        return groupExpense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE;
                    default:
                        return true;
                }
            }
        });
    }

    /**
     * Methode um einen ChildClickListener auf die ExpandableListView zu setzen.
     */
    private void setOnChildClickListener() {
        mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                ExpenseObject childExpense = (ExpenseObject) mListAdapter.getChild(groupPosition, childPosition);

                if (noBookingsSelected()) {

                    Intent updateChildExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
                    updateChildExpenseIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_UPDATE_CHILD);
                    updateChildExpenseIntent.putExtra(ExpenseScreen.INTENT_BOOKING, childExpense);

                    startActivity(updateChildExpenseIntent);

                    return true;
                }

                if (mListAdapter.isItemSelected(groupPosition, childPosition)) {
                    mListAdapter.deselectItem(groupPosition, childPosition);

                    if (noBookingsSelected())
                        enableLongClick();
                } else
                    mListAdapter.selectItem(groupPosition, childPosition);

                //todo update toolbar items
                changeToolbarMenuItems();
                return true;
            }
        });
    }

    private boolean noBookingsSelected() {
        return mListAdapter.getSelectedBookingsCount() == 0;
    }

    private boolean isEditable(ExpenseObject expense) {
        return !isParent(expense) && !isDateSep(expense);
    }

    private boolean isParent(ExpenseObject expense) {
        return expense.getExpenseType() == ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE;
    }

    private boolean isDateSep(ExpenseObject expense) {
        return expense.getExpenseType() == ExpenseObject.EXPENSE_TYPES.DATE_PLACEHOLDER;
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
     * Methode um die ListView in ihren Ausgangszustand zurückzusetzen.
     */
    private void resetListView() {
        mListAdapter.deselectAll();

        updateListView();

        enableLongClick();
    }

    /**
     * Methode um die ExpandableListView nach einer Änderung neu anzuzeigen.
     */
    public void updateListView() {

        mListAdapter = new ExpandableListAdapterCreator(
                mParent.getExpenses(getFirstOfMonth(), getLastOfMonth()),
                mParent.getActiveAccounts(),
                getContext()
        ).getExpandableListAdapter();

        mExpListView.setAdapter(mListAdapter);

        mListAdapter.notifyDataSetChanged();
    }

    /**
     * Methode um eine Kalendar Objekt zu erstellen, welches dem ersten Tag des Monats entspricht.
     *
     * @return Erster Tag des Monats
     */
    private Calendar getFirstOfMonth() {

        Calendar firstOfMonth = Calendar.getInstance();
        firstOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        firstOfMonth.set(Calendar.MINUTE, 0);
        firstOfMonth.set(Calendar.SECOND, 1);
        firstOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        return firstOfMonth;
    }

    /**
     * Methode um ein Kalendar objekt zu erstellen, welches dem letzten Tag des Monats entspricht.
     *
     * @return Letzter Tag des Monats
     */
    private Calendar getLastOfMonth() {

        int lastDayMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar lastOfMonth = Calendar.getInstance();
        lastOfMonth.set(Calendar.DAY_OF_MONTH, lastDayMonth);

        return lastOfMonth;
    }

    /**
     * Methode die für die Animationen der FloatingActionButtons zuständig ist.
     *
     * @param selectedGroups   Anzahl der ausgewählten Gruppen
     * @param selectedChildren Anzahl der ausgewählten Kinder
     */
    private void animateFabs(int selectedGroups, int selectedChildren, int selectedParents) {

        //wenn keine buchung ausgewählt ist sollen die Buttons in den Normalzustand zurückkehren
        if (selectedChildren == 0 && selectedParents == 0 && selectedGroups == 0) {
//            resetButtonAnimations();
        }

        //wenn eine Buchung ausgewählt ist sollen die Buttons add child und Delete angezeigt werden
        if (selectedChildren == 0 && selectedParents == 0 && selectedGroups == 1) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_add_child_white);

            animatePlusOpen();
        }

        //wenn zwei oder mehr Buchungen ausgewählt sind sollen die Buttons Combine und Delete angezeigt werden
        if (selectedChildren == 0 && selectedParents == 0 && selectedGroups > 1) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_combine_white);

            animatePlusOpen();
        }

        //wenn eine Buchung und ein Parent ausgewählt sind sollen der Button AddToParent angezezeigt werden
        if (selectedChildren == 0 && selectedParents == 1 && selectedGroups > 0) {

            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_merge_bookings_white);
        }

        //wenn eine oder mehrere ChildBuchung/en ausgewählt ist/sind sollen die Buttons extract und deleteAll angezeigt werden
        if (selectedChildren > 0 && selectedParents == 0 && selectedGroups == 0) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_extract_child_white);

            animatePlusOpen();
        }

        //wenn eine ParentBuchung ausgewählt ist sollen die Buttons add Child und deleteAll angezeigt werden
        if (selectedChildren == 0 && selectedParents == 1 && selectedGroups == 0) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_add_child_white);

            animatePlusOpen();
        }

        //wenn eine oder mehrere ParentBuchungen ausgewählt sind sollen die Buttons combine und deleteAll angezeigt werden
        if (selectedChildren == 0 && selectedParents > 1 && selectedGroups == 0) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_merge_bookings_white);

            animatePlusOpen();
        }
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
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um 45°.
     */
    public void animatePlusOpen() {
        if (!fabBigIsAnimated) {
            fabBig.startAnimation(rotateForwardAnim);
            fabBigIsAnimated = true;
        }
    }

    /**
     * Methode die den LöschFab sichtbar und anklickbar macht.
     */
    public void openFabSmallTop() {
        showFab(fabSmallTop);
    }

    /**
     * Methode die den KombinierFab sichtbar und anklickbar macht.
     */
    public void openFabSmallLeft() {
        showFab(fabSmallLeft);
    }

    /**
     * Methode um eine Snackbar anzuzeigen.
     *
     * @param bookings       Buchungen die gelöscht wurden
     * @param message        Nachricht die in der Snackbar angezeigt werden soll
     * @param successMessage Nachricht die angezeigt wird wenn alles erfolgreich bearbeitet wurde
     */
    private void showSnackbar(HashMap<ExpenseObject, List<ExpenseObject>> bookings, @StringRes int message, @StringRes int successMessage) {

        CoordinatorLayout coordinatorLayout = getView().findViewById(R.id.tab_one_bookings_layout);
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.revert_action, new UndoDeletionClickListener(bookings, successMessage))
                .show();
    }

    @Override
    public void onFabToolbarMenuItemClicked(String tag) {

        //todo dem TabOneFragment bescheid geben
        switch (tag) {
            case TabOneFabToolbar.MENU_ITEM_ONE:
                break;
            case TabOneFabToolbar.MENU_ITEM_TWO:
                break;
            case TabOneFabToolbar.MENU_ITEM_THREE:
                break;
            case TabOneFabToolbar.MENU_ITEM_FOUR:
                break;
            default:
                break;
        }
    }

    @Override
    public void onFabClick() {

        if (hasUserSelectedItems()) {

            resetListView();
        } else {

            if (mAccountRepo.getAll().size() != 0) {//todo elegantere Möglichkeit finden den user zu zwingen ein Konto zu erstellen, bevor er eine Buchung erstellt

                Intent createNewBookingIntent = new Intent(getActivity(), ExpenseScreen.class);
                createNewBookingIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_CREATE_BOOKING);
                getActivity().startActivity(createNewBookingIntent);
            } else {

                //todo zeige dem user wie er ein neues Konto anlegen kann
                Toast.makeText(getActivity(), getResources().getString(R.string.no_account), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Klasse die einen ClickListener für gelöschte Buchungen implementiert, welcher die gelöschten GroupBuchungen und ChildBuchungen als Argumente übernimmt.
     */
    class UndoDeletionClickListener implements View.OnClickListener {
        //todo in eigene Klasse extrahieren

        private HashMap<ExpenseObject, List<ExpenseObject>> mDeletedBookings;
        private String mSuccessMessage;

        @SuppressLint("UseSparseArrays")
        UndoDeletionClickListener(HashMap<ExpenseObject, List<ExpenseObject>> deletedBookings, @StringRes int successMessage) {

            mDeletedBookings = new HashMap<>(deletedBookings);
            mSuccessMessage = getString(successMessage);
        }

        @Override
        public void onClick(View v) {
            for (Map.Entry<ExpenseObject, List<ExpenseObject>> bookings : mDeletedBookings.entrySet()) {
                ExpenseObject parent = bookings.getKey();

                if (mBookingRepo.exists(bookings.getKey())) {

                    for (ExpenseObject child : bookings.getValue()) {
                        try {
                            mChildExpenseRepo.addChildToBooking(parent, child);
                        } catch (AddChildToChildException e) {

                            //todo was soll passieren wenn ich versuche ein Kind zu einer Kindbuchung hinzuzufügen
                            Toast.makeText(mParent, getString(R.string.add_child_to_child_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {

                    parent.removeChildren();
                    parent.addChildren(bookings.getValue());
                    mBookingRepo.insert(parent);
                }
            }

            // todo überprüfen ob die buchung wirklich wiederhergestellt wurde
            Toast.makeText(getContext(), mSuccessMessage, Toast.LENGTH_SHORT).show();
            mParent.updateExpenses();
            updateListView();
        }
    }


    /**
     * Methode um herauszufinden, ob der aktuelle tab gerade sichtbar geworden ist oder nicht.
     * Quelle: https://stackoverflow.com/a/9779971
     *
     * @param isVisibleToUser Indikator ob die aktuelle UI für den User sichtbar ist. Default ist True.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {
            if (isVisibleToUser) {
                updateListView();
                //todo nur updaten wenn etwas passiert ist
            }
        }
    }

    /**
     *
     *
     *
     *
     *
     *
     *
     *
     */

    private ImageView getCombineBookingsMenuItem() {
        return getToolbarMenuItem(R.drawable.ic_merge_bookings_white, "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BasicTextInputDialog.TITLE, getResources().getString(R.string.input_title));

                BasicTextInputDialog textInputDialog = new BasicTextInputDialog();
                textInputDialog.setArguments(bundle);
                textInputDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {

                    @Override
                    public void onTextInput(String title) {

                        combineBookings(title);
                    }
                });

                textInputDialog.show(getActivity().getFragmentManager(), "");
            }
        });
    }

    private ImageView getExtractBookingsMenuItem() {
        return getToolbarMenuItem(R.drawable.ic_extract_child_white, "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (Map.Entry<ExpenseObject, List<ExpenseObject>> bookings : mListAdapter.getSelectedBookings().entrySet()) {
                    for (ExpenseObject child : bookings.getValue()) {
                        try {
                            mChildExpenseRepo.extractChildFromBooking(child);
                        } catch (ChildExpenseNotFoundException e) {
                            //todo was soll passieren wenn eine KindBuchung nicht in der Datenbank gefunden werden konnte
                        }
                    }
                }

                mParent.updateExpenses();
                mFabToolbar.hide();
                resetListView();
            }
        });
    }

    private ImageView getAddChildMenuItem() {
        return getToolbarMenuItem(R.drawable.ic_add_child_white, "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExpenseObject parentExpense = (ExpenseObject) mListAdapter.getSelectedBookings().keySet().toArray()[0];

                Intent createChildToBookingIntent = new Intent(getContext(), ExpenseScreen.class);
                createChildToBookingIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_ADD_CHILD);
                createChildToBookingIntent.putExtra(ExpenseScreen.INTENT_BOOKING, parentExpense);

                //todo die Änderung auch mParent mitteilen
                resetListView();
                mFabToolbar.hide();
                getContext().startActivity(createChildToBookingIntent);
            }
        });
    }

    private ImageView getDefaultMenuItem() {
        //todo was ist die default option für die FABToolbar
        return new ImageView(getContext());
    }

    private ImageView getChangeableMenuOption() {
        ImageView imV = getDefaultMenuItem();

        if (combineBookingsMode())
            imV = getCombineBookingsMenuItem();

        if (extractChildMode())
            imV = getExtractBookingsMenuItem();

        if (addChildMode())
            imV = getAddChildMenuItem();

        imV.setId(fabToolbarItemOne);

        return imV;
    }

    private void saveAsTemplate() {
        ExpenseObject expense = ExpenseObject.createDummyExpense();

        //todo wenn mehr als eine Buchung markiert sind dann soll der template button versteckt werden
        TemplateRepository templateRepository = new TemplateRepository(getContext());
        templateRepository.insert(new Template(expense));
    }

    private void saveAsRecurring() {
        ExpenseObject expense = ExpenseObject.createDummyExpense();

        long start = 0;
        long end = 0;
        int freq = 1;

        //todo wenn mehr als eine Buchung markiert ist dann soll der recurring button versteckt werden
        //todo zeige einen alert dialog an welcher den user nach den zeiträumen fragt
        RecurringBookingRepository recurringBookingRepository = new RecurringBookingRepository(getContext());
        recurringBookingRepository.insert(expense, start, freq, end);
    }

    private void combineBookings(String title) {
        ExpenseObject parentBooking = mChildExpenseRepo.combineExpenses(getSelectedBookings(mListAdapter.getSelectedBookings()));

        try {
            parentBooking.setTitle(title);
            mBookingRepo.update(parentBooking);

            mParent.updateExpenses();
        } catch (ExpenseNotFoundException e) {

            Toast.makeText(getContext(), "Ausgabe konnte nicht geupdated werden", Toast.LENGTH_SHORT).show();
            //todo fehlerbehandlung
            //todo übersetzung
        }
        mParent.updateExpenses();
        resetListView();

        mFabToolbar.hide();
    }

    private int fabToolbarItemOne;

    private void changeToolbarMenuItems() {

        int index = mFabToolbarToolbar.indexOfChild(mFabToolbarToolbar.findViewById(fabToolbarItemOne));
        mFabToolbarToolbar.removeViewAt(index);
        mFabToolbarToolbar.addView(getChangeableMenuOption(), index);
    }

    /**
     * Methode um die FABToolbar mit Menuitems zu befüllen
     */
    private void fillToolbar() {
        mFabToolbarToolbar.removeAllViews();
        fabToolbarItemOne = 27;

        ImageView addChild = getChangeableMenuOption();
        addChild.setId(fabToolbarItemOne);
        mFabToolbarToolbar.addView(addChild);

        ImageView saveAsTemplate = getToolbarMenuItem(R.drawable.ic_template_white, "", new View.OnClickListener() {//todo besseres icon für template suchen
            @Override
            public void onClick(View v) {
                saveAsTemplate();

                mFabToolbar.hide();
            }
        });
        mFabToolbarToolbar.addView(saveAsTemplate);

        ImageView saveAsRecurring = getToolbarMenuItem(R.drawable.ic_repeat_white, "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAsRecurring();

                mFabToolbar.hide();
            }
        });
        mFabToolbarToolbar.addView(saveAsRecurring);

        ImageView delete = getToolbarMenuItem(R.drawable.ic_delete_white, "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSnackbar(
                        mListAdapter.getSelectedBookings(),
                        R.string.revert_deletion,
                        R.string.bookings_successfully_restored
                );


                deleteBookings(mListAdapter.getSelectedBookings());

                mParent.updateExpenses();

                resetListView();

                mFabToolbar.hide();
            }
        });
        mFabToolbarToolbar.addView(delete);
    }

    private ImageView getToolbarMenuItem(@DrawableRes int icon, String iconDesc, View.OnClickListener onClickListener) {
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(icon);
        imageView.setContentDescription(iconDesc);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(onClickListener);

        return imageView;
    }
}