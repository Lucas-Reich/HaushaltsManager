package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.R;

public class Category implements Parcelable {

    private String TAG = Category.class.getSimpleName();

    private long index;
    private String name;
    private String color;
    private boolean defaultExpenseType;

    public Category(long index, @NonNull String categoryName, @NonNull String color, boolean defaultExpenseType) {

        setIndex(index);
        setName(categoryName);
        setColor(color);
        setDefaultExpenseType(defaultExpenseType);
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
        ;
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

        return index;
    }

    private void setIndex(long index) {

        this.index = index;
    }

    @NonNull
    public String getName() {

        return name;
    }

    public void setName(@NonNull String name) {

        this.name = name;
    }

    public String getColor() {

        return this.color;
    }

    public void setColor(@NonNull String color) {

        this.color = color;
    }

    public void setColor(@ColorInt int color) {

        this.color = "#" + Integer.toHexString(color);
    }

    public boolean getDefaultExpenseType() {

        return this.defaultExpenseType;
    }

    public void setDefaultExpenseType(boolean expenseType) {

        this.defaultExpenseType = expenseType;
    }

    /**
     * Methode die die Felder der Kategorie checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann die Kategorie ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob die Kategorie in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {

        return !this.name.isEmpty() && !this.color.isEmpty();
    }

    /**
     * Wenn der index der Kategorie größer als null ist, dann gibt es die Kategorie bereits in der Datenbank
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

        boolean result = getName().equals(otherCategory.getName());
        result = result && getColor().equals(otherCategory.getColor());
        result = result && (getDefaultExpenseType() == otherCategory.getDefaultExpenseType());

        return result;
    }

    public String toString() {

        return getIndex() + " " + getName() + " " + getColor();
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
        dest.writeLong(index);
        dest.writeString(name);
        dest.writeString(color);
        dest.writeInt(defaultExpenseType ? 1 : 0);
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
