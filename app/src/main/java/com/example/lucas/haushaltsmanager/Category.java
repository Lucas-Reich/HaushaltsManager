package com.example.lucas.haushaltsmanager;

public class Category {

    private long index;
    private String categoryName;
    private int color;



    public Category (long index, String categoryName, int color) {

        this.index = index;
        this.categoryName = categoryName;
        this.color = color;
    }

    public Category() {

        this(0, "", 0);
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
