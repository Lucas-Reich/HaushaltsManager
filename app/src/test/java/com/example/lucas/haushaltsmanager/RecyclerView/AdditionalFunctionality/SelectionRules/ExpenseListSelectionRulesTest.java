package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AdItem.AdItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ChildExpenseItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ExpenseItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentExpenseItem.ParentExpenseItem;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class ExpenseListSelectionRulesTest {
    private ExpenseListSelectionRules selectionRules;

    @Before
    public void setUp() {
        selectionRules = new ExpenseListSelectionRules();
    }

    @Test
    public void parentCategoryItemCannotBeSelected() {
        ParentExpenseItem parentCategoryItem = new ParentExpenseItem(getDummyParentExpense(), getDummyDateItem());

        boolean parentCanBeSelected = selectionRules.canBeSelected(parentCategoryItem, new ArrayList<IRecyclerItem>());

        assertFalse(parentCanBeSelected);
    }

    @Test
    public void dateItemCannotBeSelected() {
        DateItem dateItem = getDummyDateItem();

        boolean dateItemCanBeSelected = selectionRules.canBeSelected(dateItem, new ArrayList<IRecyclerItem>());

        assertFalse(dateItemCanBeSelected);
    }

    @Test
    public void adItemCannotBeSelected() {
        AdItem adItem = new AdItem();

        boolean parentCanBeSelected = selectionRules.canBeSelected(adItem, new ArrayList<IRecyclerItem>());

        assertFalse(parentCanBeSelected);
    }

    @Test
    public void alreadySelectedItemCannotBeSelected() {
        List<IRecyclerItem> selectedItems = new ArrayList<>();

        ChildExpenseItem selectedItem = new ChildExpenseItem(getDummyExpense(), getDummyParentItem());
        selectedItems.add(selectedItem);

        boolean canBeSelected = selectionRules.canBeSelected(selectedItem, selectedItems);

        assertFalse(canBeSelected);
    }

    @Test
    public void expenseItemCanBeSelected() {
        ExpenseItem expenseItem = new ExpenseItem(getDummyExpense(), getDummyDateItem());

        boolean expenseItemCanBeSelected = selectionRules.canBeSelected(expenseItem, new ArrayList<IRecyclerItem>());

        assertTrue(expenseItemCanBeSelected);
    }

    @Test
    public void childExpenseItemCanBeSelected() {
        ChildExpenseItem childExpenseItem = new ChildExpenseItem(getDummyExpense(), getDummyParentItem());

        boolean childCanBeSelected = selectionRules.canBeSelected(childExpenseItem, new ArrayList<IRecyclerItem>());

        assertTrue(childCanBeSelected);
    }

    @Test
    public void expenseItemWithADifferentParentCanBeSelected() {
        List<IRecyclerItem> selectedItems = new ArrayList<>();
        selectedItems.add(new ExpenseItem(getDummyExpense(), getDummyDateItem()));

        ExpenseItem expenseItem = new ExpenseItem(getDummyExpense(), getDummyDateItem());
        boolean canBeSelected = selectionRules.canBeSelected(expenseItem, selectedItems);

        assertTrue(canBeSelected);
    }

    @Test
    public void childItemWithADifferentParentCannotBeSelected() {
        List<IRecyclerItem> selectedItems = new ArrayList<>();
        selectedItems.add(new ChildExpenseItem(getDummyExpense(), getDummyParentItem()));

        ChildExpenseItem child = new ChildExpenseItem(getDummyExpense(), getDummyParentItem());
        boolean canBeSelected = selectionRules.canBeSelected(child, selectedItems);

        assertFalse(canBeSelected);
    }

    @Test
    public void cannotSelectExpenseItemWhenChildIsSelected() {
        List<IRecyclerItem> selectedItems = new ArrayList<>();
        selectedItems.add(new ChildExpenseItem(getDummyExpense(), getDummyParentItem()));

        ExpenseItem expenseItem = new ExpenseItem(getDummyExpense(), getDummyDateItem());
        boolean canBeSelected = selectionRules.canBeSelected(expenseItem, selectedItems);

        assertFalse(canBeSelected);
    }

    private ParentExpenseItem getDummyParentItem() {
        return new ParentExpenseItem(getDummyParentExpense(), getDummyDateItem());
    }

    private ExpenseObject getDummyExpense() {
        Currency currency = new Currency("Euro", "EUR", "€");

        return new ExpenseObject(
                "Ausgabe",
                new Price(100, currency),
                new Category("Kategorie", new Color(Color.BLACK), ExpenseType.expense(), new ArrayList<Category>()),
                ExpensesDbHelper.INVALID_INDEX,
                currency
        );
    }

    private ParentExpenseObject getDummyParentExpense() {
        return new ParentExpenseObject(
                ExpensesDbHelper.INVALID_INDEX,
                "Parent Ausgabe",
                new Currency("Euro", "EUR", "€"),
                Calendar.getInstance(),
                new ArrayList<ExpenseObject>()
        );
    }

    private DateItem getDummyDateItem() {
        return new DateItem(
                Calendar.getInstance()
        );
    }
}
