package com.example.lucas.haushaltsmanager;

public class Account {

    private long index;
    private String accountName;
    private int balance;

    public Account(long index, String accountName, int balance) {

        this.index = index;
        this.accountName = accountName;
        this.balance = balance;
    }

    public Account(String accountName, int balance) {

        this(0, accountName, balance);
    }

    public Account() {

        this(0, "", 0);
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
