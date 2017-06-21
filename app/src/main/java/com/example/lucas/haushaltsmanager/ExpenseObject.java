package com.example.lucas.haushaltsmanager;

public class ExpenseObject {

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

    public String getExpenditureName() {
        return expenditureName;
    }

    public void setExpenditureName(String expenditureName) {
        this.expenditureName = expenditureName;
    }

    public int getExpenditureAmount() {
        return expenditureAmount;
    }

    public void setExpenditureAmount(int expenditureAmount) {
        this.expenditureAmount = expenditureAmount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isExpenditure() {
        return expenditure;
    }

    public void setExpenditure(boolean expenditure) {
        this.expenditure = expenditure;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {

        String output = expenditureAmount + " x " + expenditureName;
        return output;
    }
}
