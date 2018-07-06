package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Category implements Parcelable {

    private String TAG = Category.class.getSimpleName();

    private long mIndex;
    private String mName;
    private String mColor;
    private boolean mDefaultExpenseType;
    private ArrayList<Category> mChildren;

    public Category(long index, @NonNull String categoryName, @NonNull String color, boolean defaultExpenseType) {

        setIndex(index);
        setName(categoryName);
        setColor(color);
        setDefaultExpenseType(defaultExpenseType);
        mChildren = new ArrayList<>();
    }

    public Category(@NonNull String categoryName, @NonNull String color, Boolean defaultExpenseType) {

        this(-1L, categoryName, color, defaultExpenseType != null ? defaultExpenseType : false);
    }

    /**
     * this constructor converts our parcelable object back into an Category object
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
    public static Category createDummyCategory(Context context) {

        return new Category(-1L, context.getResources().getString(R.string.no_name), "#000000", false);
    }

    public long getIndex() {

        return mIndex;
    }

    private void setIndex(long index) {

        this.mIndex = index;
    }

    @NonNull
    public String getTitle() {

        return mName;
    }

    public void setName(@NonNull String name) {

        this.mName = name;
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

        this.mColor = color;
    }

    public void setColor(@ColorInt int color) {

        this.mColor = "#" + Integer.toHexString(color);
    }

    public boolean getDefaultExpenseType() {

        return this.mDefaultExpenseType;
    }

    public void setDefaultExpenseType(boolean expenseType) {

        this.mDefaultExpenseType = expenseType;
    }

    /**
     * Methode die die Felder der Kategorie checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann die Kategorie ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob die Kategorie in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {

        return !this.mName.isEmpty() && !this.mColor.isEmpty();
    }

    public void addChild(Category child) {
        mChildren.add(child);
    }

    public void addChildren(ArrayList<Category> children) {
        mChildren.addAll(children);
    }

    public ArrayList<Category> getChildren() {
        return mChildren;
    }

    /**
     * Wenn der mIndex der Kategorie größer als null ist, dann gibt es die Kategorie bereits in der Datenbank
     * und man kann sie sicher verwenden.
     *
     * @return boolean
     */
    public boolean isValid() {

        return getIndex() > -1;
    }

    /**
     * Methode die überprüft, ob die angegebene Kategorie die gleiche ist, wie diese.
     *
     * @param otherCategory Andere Kategorie
     * @return boolean
     */
    public boolean equals(Category otherCategory) {

        boolean result = getTitle().equals(otherCategory.getTitle());
        result = result && getColorString().equals(otherCategory.getColorString());
        result = result && (getDefaultExpenseType() == otherCategory.getDefaultExpenseType());

        return result;
    }

    public String toString() {

        return getIndex() + " " + getTitle() + " " + getColorString();
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
