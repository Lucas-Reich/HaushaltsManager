package com.example.lucas.haushaltsmanager;

import java.util.Calendar;

class ExpenseObject {

    private Calendar date;
    private String title = "";
    private int amount;
    private long id;
    private boolean expenditure;
    private String category = "";
    private String tag = "";
    private String notice = "";
    private String account = "";


    public ExpenseObject(String title, int amount, long id, boolean expenditure, String category, String token) {

        this.title = title;
        this.amount = amount;
        this.id = id;
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

        return amount + " x " + title;
    }

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

    int getAmount() {
        return amount;
    }

    void setAmount(int amount) {
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
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
}
