package com.example.lucas.haushaltsmanager;


import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExpandableListItemSelector {
    // TODO Kann ich aus dieser Klasse noch eine Unterklasse extrahieren
    private List<ExpListViewSelectedItem> mSelectedItems;
    private ExpandableListAdapter mAdapter;

    public ExpandableListItemSelector(ExpandableListAdapter adapter) {
        mSelectedItems = new ArrayList<>();
        mAdapter = adapter;
    }

    public boolean selectItem(int groupPosition, int childPosition) {
        if (!groupAndChildAreNotSimultaneouslySelected(childPosition))
            return false;

        // Wenn ich ein Kind markieren will, dann ist die hasChildren überprüfung des Parents immer true und ich markiere alle Kinder des Parents
        // und nicht nur das eine Kind
        if (-1 == childPosition && hasChildren(groupPosition))
            mSelectedItems.addAll(parentToSelectedItems(
                    groupPosition
            ));
        else
            mSelectedItems.add(positionToSelectedItem(
                    groupPosition,
                    childPosition
            ));

        return true;
    }

    public void unselectItem(int groupPosition, int childPosition) {
        if (hasChildren(groupPosition))
            removeAllChildren(groupPosition);
        else
            removeItem(groupPosition, childPosition);
    }

    public void unselectAll() {
        mSelectedItems.clear();
    }

    public boolean isItemSelected(int groupPosition, int childPosition) {
        if (hasChildren(groupPosition))
            return allChildrenSelected(groupPosition);
        else
            return isSelected(groupPosition, childPosition);
    }

    public int getSelectedGroupsCount() {
        int counter = 0;
        for (ExpListViewSelectedItem item : mSelectedItems) {
            if (item.isGroup())
                counter++;
        }

        return counter;
    }

    public int getSelectedChildrenCount() {
        int counter = 0;
        for (ExpListViewSelectedItem item : mSelectedItems) {
            if (!item.isGroup())
                counter++;
        }

        return counter;
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    public List<ExpListViewSelectedItem> getSelectedItems() {
        return mSelectedItems;
    }

    private boolean allChildrenSelected(int groupPosition) {
        ExpenseObject group = (ExpenseObject) mAdapter.getGroup(groupPosition);

        int selectedChildrenCount = 0;
        for (ExpListViewSelectedItem selectedItem : mSelectedItems) {
            if (selectedItem.getParent().equals(group))
                selectedChildrenCount++;
        }

        return selectedChildrenCount == mAdapter.getChildrenCount(groupPosition);
    }

    private boolean isSelected(int groupPosition, int childPosition) {
        return mSelectedItems.contains(positionToSelectedItem(
                groupPosition,
                childPosition
        ));
    }

    private void removeAllChildren(int groupPosition) {
        ExpenseObject group = (ExpenseObject) mAdapter.getGroup(groupPosition);

        for (Iterator<ExpListViewSelectedItem> it = mSelectedItems.iterator(); it.hasNext(); ) {
            if (it.next().getParent().equals(group))
                it.remove();
        }
    }

    private void removeItem(int groupPosition, int childPosition) {
        mSelectedItems.remove(positionToSelectedItem(
                groupPosition,
                childPosition
        ));
    }

    private boolean hasChildren(int groupPosition) {
        return 0 != mAdapter.getChildrenCount(groupPosition);
    }

    private boolean groupAndChildAreNotSimultaneouslySelected(int childPosition) {
        return !(getSelectedGroupsCount() > 0 && childPosition != -1) && !(getSelectedChildrenCount() > 0 && childPosition == -1);
    }

    private List<ExpListViewSelectedItem> parentToSelectedItems(int groupPosition) {
        ExpenseObject group = (ExpenseObject) mAdapter.getGroup(groupPosition);

        List<ExpListViewSelectedItem> children = new ArrayList<>();
        for (int childIndex = 0; childIndex < mAdapter.getChildrenCount(groupPosition); childIndex++) {
            children.add(new ExpListViewSelectedItem(
                    getChild(groupPosition, childIndex),
                    group
            ));
        }

        return children;
    }

    private ExpListViewSelectedItem positionToSelectedItem(int groupPosition, int childPosition) {
        ExpenseObject group = (ExpenseObject) mAdapter.getGroup(groupPosition);

        if (-1 == childPosition)
            return new ExpListViewSelectedItem(
                    group,
                    null
            );
        else
            return new ExpListViewSelectedItem(
                    getChild(groupPosition, childPosition),
                    group
            );
    }

    private ExpenseObject getChild(int groupPosition, int childPosition) {
        return (ExpenseObject) mAdapter.getChild(groupPosition, childPosition);
    }
}
