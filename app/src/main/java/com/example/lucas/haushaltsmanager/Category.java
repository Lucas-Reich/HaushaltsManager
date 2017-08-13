package com.example.lucas.haushaltsmanager;

class Category {

    private long index;
    private String categoryName;
    private int color;


    Category (long index, String categoryName, int color) {

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
}
