package com.example.lucas.haushaltsmanager;

import java.util.Calendar;

class ExpenseObject {

    private Calendar date;// set by default
    private String title = "";// required
    private double amount = 0;// required
    private long index;
    private boolean expenditure;// set by default
    private String category = "";// required
    private String tag = "";
    private String notice = "";
    private String account = "";// set by default


    public ExpenseObject(String title, int amount, long index, boolean expenditure, String category, String token) {

        this.title = title;
        this.amount = amount;
        this.index = index;
        this.expenditure = expenditure;
        this.category = category;
        this.tag = token;
    }

    public ExpenseObject(String title, int amount, boolean expenditure, String category) {

        this.title = title;
        this.amount = amount;
        this.expenditure = expenditure;
        this.category = category;
    }

    ExpenseObject() {}

    @Override
    public String toString() {

        String toDisplay = "Book " + this.title + " " + this.amount + " times as an " + this.expenditure;
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

    double getAmount() {
        return amount;
    }

    void setAmount(double amount) {
        this.amount = amount;
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

    String getCategory() {
        return category;
    }

    void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    void setTag(String tag) {
        this.tag = tag;
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

        return !this.title.isEmpty() && this.amount != 0 && !this.category.isEmpty();
    }
}
