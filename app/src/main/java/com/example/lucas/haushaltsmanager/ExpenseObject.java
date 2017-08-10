package com.example.lucas.haushaltsmanager;

import java.util.Calendar;
import java.util.List;

class ExpenseObject {

    private Calendar date;// set by default
    private String title = "";// required
    private double price = 0;// required
    private long index;
    private boolean expenditure;// set by default
    private Category category;// required
    private List<String> tag;
    private String notice = "";
    private String account = "";// set by default


    public ExpenseObject(String title, double price, boolean expenditure, Category category, String tag) {

        this.title = title;
        this.price = price;
        this.expenditure = expenditure;
        this.category = category;
        this.tag.add(tag);
    }

    public ExpenseObject(String title, double price, boolean expenditure, Category category) {

        this(title, price, expenditure, category, "");
    }

    ExpenseObject() {

        this("", 0.0, true, new Category(), "");
    }

    @Override
    public String toString() {

        String toDisplay = "Book " + this.title + " " + this.price + " times as an " + this.expenditure;
        String toDisplay2 = "The booking belongs to " + this.category + ", happened at the " + date.get(Calendar.DAY_OF_MONTH) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.YEAR);
        String toDisplay3 = " and has to be inserted to " + this.account;


        return toDisplay + toDisplay2 + toDisplay3;
    }

    //TODO return date String depending on the locale of the user
    //TODO implement a getDisplayDate and getDate method
    String getDate() {
        return date.get(Calendar.DAY_OF_MONTH) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.YEAR);
    }

    void setDate(Calendar date) {
        this.date = date;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    double getPrice() {
        return price;
    }

    void setPrice(double price) {
        this.price = price;
    }

    public long getIndex() {
        return index;
    }

    void setIndex(long index) {
        this.index = index;
    }

    boolean getExpenditure() {
        return expenditure;
    }

    void setExpenditure(boolean expenditure) {
        this.expenditure = expenditure;
    }

    void setExpenditure(int expenditure) {

        this.expenditure = expenditure == 0;
    }

    Category getCategory() {
        return category;
    }

    void setCategory(Category category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tag;
    }

    void setTag(String tag) {

        this.tag.add(tag);
    }

    void setTags(List<String> tags) {

        this.tag = tags;
    }

    public String getNotice() {
        return notice;
    }

    void setNotice(String notice) {
        this.notice = notice;
    }

    String getAccount() {
        return account;
    }

    void setAccount(String account) {
        this.account = account;
    }

    boolean isSet() {

        return !this.title.isEmpty() && this.price != 0 && this.category.getCategoryName().isEmpty();
    }
}
