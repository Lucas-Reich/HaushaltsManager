package com.example.lucas.haushaltsmanager;

import java.util.Calendar;

public class ExpenseObject {

    private Calendar expendituredate;
    private String expenditureName;
    private int expenditureAmount;
    private long id;
    private boolean expenditure;
    private String category;
    private String token;


    public ExpenseObject(String expenditureName, int expenditureAmount, long id, boolean expenditure, String category, String token) {

        this.expenditureName = expenditureName;
        this.expenditureAmount = expenditureAmount;
        this.id = id;
        this.expenditure = expenditure;
        this.category = category;
        this.token = token;
    }

    public ExpenseObject(String expenditureName, int expenditureAmount, boolean expenditure, String category) {

        this.expenditureName = expenditureName;
        this.expenditureAmount = expenditureAmount;
        this.expenditure = expenditure;
        this.category = category;
    }

    public Calendar getExpendituredate() {
        return expendituredate;
    }

    void setExpendituredate(Calendar expendituredate) {
        this.expendituredate = expendituredate;
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

    public String getToken() {
        return token;
    }

    void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {

        String output = expenditureAmount + " x " + expenditureName;
        return output;
    }
}
