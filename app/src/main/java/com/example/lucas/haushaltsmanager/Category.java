package com.example.lucas.haushaltsmanager;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;

class Category implements Parcelable {

    private long index;
    private String categoryName;
    private String color;
    private boolean defaultExpenseType;

    private String TAG = Category.class.getSimpleName();


    Category(Long index, @NonNull String categoryName, @NonNull String color, boolean defaultExpenseType) {

        this.index = index != null ? index : -1;
        this.categoryName = categoryName;
        this.color = color;
        this.defaultExpenseType = defaultExpenseType;
    }

    Category(@NonNull String categoryName, @NonNull String color, Boolean defaultExpenseType) {

        this(null, categoryName, color, defaultExpenseType != null ? defaultExpenseType : false);
    }

    Category() {

        this(null, "", "#000000", false);
    }

    /**
     * Methode um eine Dummy Category zu erstellen
     *
     * @return Category dummy object
     */
    static Category createDummyCategory() {

        return new Category(null, "dummy", "#000000", false);
    }

    long getIndex() {
        return index;
    }

    @NonNull
    String getCategoryName() {
        return categoryName;
    }

    void setCategoryName(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }

    String getColor() {
        return this.color;
    }

    void setColor(@NonNull String color) {

        this.color = color;
    }

    void setColor(@ColorInt int color) {

        this.color = "#" + Integer.toHexString(color);
    }

    boolean getDefaultExpenseType() {

        return this.defaultExpenseType;
    }

    void setDefaultExpenseType(boolean expenseType) {

        this.defaultExpenseType = expenseType;
    }


    //make class Parcelable

    /**
     * This will be only used by ParcelableCategories
     * see: http://prasanta-paul.blogspot.de/2010/06/android-parcelable-example.html (Parcelable ArrayList)
     * and: https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents for further explanations (Parcelable Object)
     * <p>
     * this constructor converts our parcelable object back into an Category object
     *
     * @param source .
     */
    public Category(Parcel source) {

        Log.v(TAG, "ParcelData (Parcel source): time to put back parcel data");
        index = source.readLong();
        categoryName = source.readString();
        color = source.readString();
        defaultExpenseType = source.readInt() == 1;
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
        dest.writeString(categoryName);
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
