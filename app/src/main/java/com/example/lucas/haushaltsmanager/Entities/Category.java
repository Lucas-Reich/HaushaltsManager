package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Category implements Parcelable {
    public static final Creator<Category> CREATOR = new Creator<Category>() {

        @Override
        public Category createFromParcel(Parcel in) {

            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {

            return new Category[size];
        }
    };

    private long mIndex;
    private String mName;
    private boolean mDefaultExpenseType;
    private List<Category> mChildren;
    private Color color;

    public Category(long index, @NonNull String name, Color color, boolean defaultExpenseType, List<Category> children) {
        setIndex(index);
        setName(name);
        setColor(color);
        setDefaultExpenseType(defaultExpenseType);
        addChildren(children);
    }

    @Deprecated
    public Category(@NonNull String categoryName, @NonNull String color, @NonNull Boolean defaultExpenseType, @NonNull List<Category> children) {

        this(ExpensesDbHelper.INVALID_INDEX, categoryName, new Color(color), defaultExpenseType, children);
    }

    public Category(@NonNull String name, @NonNull Color color, @NonNull Boolean defaultExpenseType, @NonNull List<Category> children) {
        this(ExpensesDbHelper.INVALID_INDEX, name, color, defaultExpenseType, children);
    }

    /**
     * constructor converts our parcelable object back into an Category object
     * see: http://prasanta-paul.blogspot.de/2010/06/android-parcelable-example.html (Parcelable ArrayList)
     * and: https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents for further explanations (Parcelable Object)
     *
     * @param source .
     */
    private Category(Parcel source) {
        setIndex(source.readLong());
        setName(source.readString());
        setColor((Color) source.readParcelable(Color.class.getClassLoader()));
        setDefaultExpenseType(source.readInt() == 1);
        // TODO: Müssten hier nicht noch die Kinder initialisiert werden
    }

    /**
     * Methode um eine Dummy Category zu erstellen
     *
     * @return Category dummy object
     */
    public static Category createDummyCategory() {

        return new Category(-1L, app.getContext().getString(R.string.no_name), new Color(Color.BLACK), false, new ArrayList<Category>());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Category)) {
            return false;
        }

        Category otherCategory = (Category) obj;

        return getTitle().equals(otherCategory.getTitle())
                && otherCategory.getColor().equals(color)
                && (getDefaultExpenseType() == otherCategory.getDefaultExpenseType())
                && mChildren.equals(otherCategory.getChildren());
    }

    public String toString() {

        return getTitle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getIndex(),
                getTitle(),
                getColor(),
                getDefaultExpenseType(),
                getChildren()
        );
    }

    /**
     * can be ignored mostly
     *
     * @return int
     */
    @Override
    public int describeContents() {

        return 0;
    }

    /**
     * converting the custom object into an parcelable object
     *
     * @param dest  destination Parcel
     * @param flags flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mIndex);
        dest.writeString(mName);
        dest.writeParcelable(color, flags);
        dest.writeInt(mDefaultExpenseType ? 1 : 0);
    }

    public long getIndex() {

        return mIndex;
    }

    private void setIndex(long index) {

        mIndex = index;
    }

    @NonNull
    public String getTitle() {

        return mName;
    }

    public void setName(@NonNull String name) {

        mName = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean getDefaultExpenseType() {

        return mDefaultExpenseType;
    }

    public void setDefaultExpenseType(boolean expenseType) {

        mDefaultExpenseType = expenseType;
    }

    /**
     * Methode die die Felder der Kategorie checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann die Kategorie ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob die Kategorie in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {
        return !mName.equals(app.getContext().getString(R.string.no_name));
    }

    public void addChild(Category child) {
        mChildren.add(child);
    }

    public void addChildren(List<Category> children) {
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }

        mChildren.addAll(children);
    }

    public List<Category> getChildren() {
        return mChildren;
    }
}
