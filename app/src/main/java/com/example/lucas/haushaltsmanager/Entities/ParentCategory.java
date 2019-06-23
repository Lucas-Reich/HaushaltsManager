package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ParentCategory implements Parcelable {
    public static final Creator<ParentCategory> CREATOR = new Creator<ParentCategory>() {
        @Override
        public ParentCategory createFromParcel(Parcel in) {
            return new ParentCategory(in);
        }

        @Override
        public ParentCategory[] newArray(int size) {
            return new ParentCategory[size];
        }
    };

    private String mTitle;
    private Color color;
    private List<Category> mChildren;

    public ParentCategory(Category category, List<Category> children) {
        setTitle(category.getTitle());
        setColor(category.getColor());

        mChildren = children;
    }

    private ParentCategory(Parcel source) {
        setTitle(source.readString());
        setColor((Color) source.readParcelable(Color.class.getClassLoader()));
        source.readList(mChildren, Category.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeParcelable(color, flags);
        dest.writeList(mChildren);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Category> getChildren() {
        return mChildren;
    }
}
