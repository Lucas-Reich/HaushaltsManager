package com.example.lucas.haushaltsmanager;

import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class ExpenseObject {

    private Calendar date;
    private String title = "";
    private double price = 0;
    private long index;
    private boolean expenditure;
    private Category category;
    private List<String> tag = new LinkedList<>();
    private String notice = "";
    private Account account;


    private ExpensesDataSource expensesDataSource;


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

        //String test = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);
        String rightDate = year + "";

        if (month < 10) {

            rightDate += "-0" + month;
        } else {

            rightDate += "-" + month;
        }

        if (day < 10) {

            rightDate += "-0" + day;
        } else {

            rightDate += "-" + day;
        }
        return rightDate;
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

    long getIndex() {
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

    void setCategory(String categoryName, int color) {

        this.category = new Category(categoryName, color);
    }

    List<String> getTags() {
        return tag;
    }

    void setTag(String tag) {

        this.tag.add(tag);
    }

    void setTags(List<String> tags) {

        this.tag = tags;
    }

    String getNotice() {
        return notice;
    }

    void setNotice(String notice) {
        this.notice = notice;
    }

    Account getAccount() {
        return account;
    }

    void setAccount(Account account) {
        this.account = account;
    }

    boolean isSet() {

        return !this.title.isEmpty() && this.price != 0.0 && !this.category.getCategoryName().isEmpty();
    }

    void toConsole() {

        Log.d("ExpenseObject index: ", "" + index);
        Log.d("ExpenseObject cat: ", "" + category.getCategoryName());
        Log.d("ExpenseObject price: ", "" + price);
        Log.d("ExpenseObject expend: ", "" + expenditure);
        Log.d("ExpenseObject title: ", "" + title);
        Log.d("ExpenseObject tag: ", "" + tag);
        Log.d("ExpenseObject date: ", "" + getDate());
        Log.d("ExpenseObject notice: ", "" + notice);
        Log.d("ExpenseObject account: ", "" + account.getAccountName());

    }
}
