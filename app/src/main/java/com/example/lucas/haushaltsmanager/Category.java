package com.example.lucas.haushaltsmanager;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class Category implements Parcelable {

    private long index;
    private String categoryName;
    private int color;

    private String TAG = "Category: ";


    Category(long index, String categoryName, int color) {

        this.index = index;
        this.categoryName = categoryName;
        this.color = color;
    }

    Category(String categoryName, int color) {

        this(0, categoryName, color);
    }

    Category() {

        this(0, "", 0);
    }

    long getIndex() {
        return index;
    }

    void setIndex(long index) {
        this.index = index;
    }

    String getCategoryName() {
        return categoryName;
    }

    void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    int getColor() {
        return color;
    }

    void setColor(int color) {
        this.color = color;
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
        color = source.readInt();
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
        dest.writeInt(color);
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
