package com.example.lucas.haushaltsmanager;

import java.util.Calendar;

class ExpenseObject {

    private Calendar expenditureDate;
    private String expenditureName;
    private int expenditureAmount;
    private long id;
    private boolean expenditure;
    private String category;
    private String tag;
    private String notice;
    private String account;


    public ExpenseObject(String expenditureName, int expenditureAmount, long id, boolean expenditure, String category, String token) {

        this.expenditureName = expenditureName;
        this.expenditureAmount = expenditureAmount;
        this.id = id;
        this.expenditure = expenditure;
        this.category = category;
        this.tag = token;
    }

    public ExpenseObject(String expenditureName, int expenditureAmount, boolean expenditure, String category) {

        this.expenditureName = expenditureName;
        this.expenditureAmount = expenditureAmount;
        this.expenditure = expenditure;
        this.category = category;
    }

    public ExpenseObject() {}

    @Override
    public String toString() {

        String output = expenditureAmount + " x " + expenditureName;
        return output;
    }

    public Calendar getExpenditureDate() {
        return expenditureDate;
    }

    void setExpenditureDate(Calendar expenditureDate) {
        this.expenditureDate = expenditureDate;
    }

    public String getExpenditureName() {
        return expenditureName;
    }

    void setExpenditureName(String expenditureName) {
        this.expenditureName = expenditureName;
    }

    public int getExpenditureAmount() {
        return expenditureAmount;
    }

    void setExpenditureAmount(int expenditureAmount) {
        this.expenditureAmount = expenditureAmount;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public boolean isExpenditure() {
        return expenditure;
    }

    void setExpenditure(boolean expenditure) {
        this.expenditure = expenditure;
    }

    public String getCategory() {
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

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
