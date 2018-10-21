package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class Category implements Parcelable {
    private static final String TAG = Category.class.getSimpleName();

    private long mIndex;
    private String mName;
    private String mColor;
    private boolean mDefaultExpenseType;
    private List<Category> mChildren;

    public Category(long index, @NonNull String categoryName, @NonNull String color, boolean defaultExpenseType, List<Category> children) {

        setIndex(index);
        setName(categoryName);
        setColor(color);
        setDefaultExpenseType(defaultExpenseType);
        addChildren(children);
    }

    public Category(@NonNull String categoryName, @NonNull String color, @NonNull Boolean defaultExpenseType, @NonNull List<Category> children) {

        this(-1L, categoryName, color, defaultExpenseType, children);
    }

    /**
     * constructor converts our parcelable object back into an Category object
     * see: http://prasanta-paul.blogspot.de/2010/06/android-parcelable-example.html (Parcelable ArrayList)
     * and: https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents for further explanations (Parcelable Object)
     *
     * @param source .
     */
    public Category(Parcel source) {

        Log.v(TAG, "Recreating Category from parcel data");
        setIndex(source.readLong());
        setName(source.readString());
        setColor(source.readString());
        setDefaultExpenseType(source.readInt() == 1);
    }

    /**
     * Methode um eine Dummy Category zu erstellen
     *
     * @return Category dummy object
     */
    public static Category createDummyCategory() {

        return new Category(-1L, app.getContext().getString(R.string.no_name), "#000000", false, new ArrayList<Category>());
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

    public String getColorString() {

        return mColor;
    }

    /**
     * Methode um den Integerwert der Farbe zu ermitteln.
     *
     * @return ColorInt
     */
    @ColorInt
    public int getColorInt() {

        return (int) Long.parseLong(mColor.substring(1), 16);
    }

    public void setColor(@NonNull String color) {

        mColor = color;
    }

    public void setColor(@ColorInt int color) {

        mColor = "#" + Integer.toHexString(color);
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
        if (mChildren == null)
            mChildren = new ArrayList<>();

        mChildren.addAll(children);
    }

    public List<Category> getChildren() {
        return mChildren;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Category))
            return false;

        Category otherCategory = (Category) obj;

        boolean result = getTitle().equals(otherCategory.getTitle());
        result = result && getColorString().equals(otherCategory.getColorString());
        result = result && (getDefaultExpenseType() == otherCategory.getDefaultExpenseType());

        for (int i = 0; i < mChildren.size(); i++) {
            result = result && mChildren.get(i).equals(otherCategory.getChildren().get(i));
        }

        return result;
    }

    public String toString() {

        return getTitle();
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

        Log.v(TAG, "write to parcel..." + flags);
        dest.writeLong(mIndex);
        dest.writeString(mName);
        dest.writeString(mColor);
        dest.writeInt(mDefaultExpenseType ? 1 : 0);
    }

    /**
     * regenerating the parcelable object back into our Category object
     */
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
}
