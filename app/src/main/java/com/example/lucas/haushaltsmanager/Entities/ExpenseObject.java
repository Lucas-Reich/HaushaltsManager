package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseObject implements Parcelable {

    public enum EXPENSE_TYPES {
        DUMMY_EXPENSE,
        DATE_PLACEHOLDER,
        PARENT_EXPENSE,
        NORMAL_EXPENSE,
        TRANSFER_EXPENSE
    }

    private EXPENSE_TYPES expenseType;

    /**
     * Database index of expense
     */
    private long index;

    /**
     * Title of expense
     */
    private String title;

    /**
     * Price by user
     */
    private double price;

    /**
     * Date of expense
     */
    private Calendar date;

    /**
     * Type of expense
     * (true = expense, false = income)
     */
    private boolean expenditure;

    /**
     * Category of expense
     */
    private Category category;

    /**
     * Tags assigned to expense
     */
    private List<Tag> tags = new ArrayList<>();

    /**
     * Notice from user
     */
    private String notice;

    /**
     * Account where booking happened
     */
    private Account account;

    /**
     * Currency of expense
     */
    private Currency expenseCurrency;

    /**
     * Children of expense
     */
    private List<ExpenseObject> children = new ArrayList<>();

    private String TAG = ExpenseObject.class.getSimpleName();

    public ExpenseObject(long index, @NonNull String expenseName, double price, Calendar date, boolean expenditure, @NonNull Category category, String notice, @NonNull Account account, Currency expenseCurrency, @NonNull EXPENSE_TYPES expenseType) {

        setIndex(index);
        setName(expenseName);
        setPrice(price);
        setDateTime(date != null ? date : Calendar.getInstance());
        setExpenditure(expenditure);
        setCategory(category);
        setNotice(notice != null ? notice : "");
        setAccount(account);
        setExpenseCurrency(expenseCurrency != null ? expenseCurrency : account.getCurrency());
        setExpenseType(expenseType);
    }

    public ExpenseObject(@NonNull String title, double price, boolean expenditure, @NonNull Category category, Currency expenseCurrency, @NonNull Account account) {

        this(-1, title, price, Calendar.getInstance(), expenditure, category, null, account, expenseCurrency, EXPENSE_TYPES.NORMAL_EXPENSE);
    }

    /**
     * This will be only used by ParcelableCategories
     * see: http://prasanta-paul.blogspot.de/2010/06/android-parcelable-example.html (Parcelable ArrayList)
     * and: https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents for further explanations (Parcelable Object)
     * <p>
     * this constructor converts our parcelable object back into an Category object
     *
     * @param source .
     */
    public ExpenseObject(Parcel source) {

        Calendar cal = Calendar.getInstance();

        Log.v(TAG, "Recreating ExpenseObject from parcel data");
        cal.setTimeInMillis(source.readLong());
        setDateTime(cal);
        setName(source.readString());
        setPrice(source.readDouble());
        setIndex(source.readLong());
        setExpenditure(source.readByte() != 0);
        setCategory((Category) source.readParcelable(Category.class.getClassLoader()));
        source.readList(this.tags, Tag.class.getClassLoader());
        setNotice(source.readString());
        setAccount((Account) source.readParcelable(Account.class.getClassLoader()));
        source.readList(this.children, ExpenseObject.class.getClassLoader());
        setExpenseCurrency((Currency) source.readParcelable(Currency.class.getClassLoader()));
    }

    /**
     * Methode um eine dummy Expense zu erstellen
     *
     * @param context Context
     * @return dummy Expense
     */
    public static ExpenseObject createDummyExpense(@NonNull Context context) {

        return new ExpenseObject(-1, context.getResources().getString(R.string.no_name), 0, Calendar.getInstance(), false, Category.createDummyCategory(context), null, Account.createDummyAccount(context), Currency.createDummyCurrency(context), EXPENSE_TYPES.DUMMY_EXPENSE);
    }

    public double getExchangeRate() {

        return this.expenseCurrency.getRateToBase();
    }

    @Nullable
    public Double getCalcPrice() {

        DecimalFormat df = new DecimalFormat("#.##");
        double rate;

        if (this.expenseCurrency.getRateToBase() != 0) {

            rate = this.expenseCurrency.getRateToBase() * this.price;
            return Double.parseDouble(df.format(rate));
        } else {

            rate = this.account.getCurrency().getRateToBase() * this.price;
            return Double.parseDouble(df.format(rate));
        }
    }

    @NonNull
    public Currency getExpenseCurrency() {

        return this.expenseCurrency;
    }

    public void setExpenseCurrency(@NonNull Currency expenseCurrency) {

        this.expenseCurrency = expenseCurrency;
    }

    @NonNull
    public Calendar getDateTime() {

        return this.date;
    }

    @NonNull
    public String getDate() {

        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(getDateTime().getTime());
    }

    @NonNull
    public String getDisplayableDateTime() {

        return DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(getDateTime().getTimeInMillis()));
    }

    public void setDateTime(@NonNull Calendar date) {

        this.date = date;
    }

    @NonNull
    public String getName() {

        return title;
    }

    public void setName(@NonNull String title) {

        this.title = title;
    }

    public double getUnsignedPrice() {

        return price;
    }

    /**
     * Method um den tatsächlichen wert einer Buchung zu bekommen.
     * Falls die Buchung ein Parent ist wird die Summer der Kinder zurückgegeben
     *
     * @return tatsächlicher Wert der Buchung
     */
    public double getSignedPrice() {

        if (isParent()) {

            double calcPrice = 0;

            for (ExpenseObject child : this.children) {

                calcPrice += child.getSignedPrice();
            }

            return calcPrice;
        } else {

            return this.expenditure ? 0 - this.price : price;
        }
    }

    public void setPrice(double price) {

        if (price < 0) {

            this.price = Math.abs(price);
            this.expenditure = true;
        } else {

            this.price = price;
            this.expenditure = false;
        }
    }

    public long getIndex() {

        return index;
    }

    private void setIndex(long index) {

        this.index = index;
    }

    public boolean isExpenditure() {

        return expenditure;
    }

    /**
     * @param expenditure true (for outgoing money) or false (for incoming money)
     */
    public void setExpenditure(boolean expenditure) {

        this.expenditure = expenditure;
    }

    @NonNull
    public Category getCategory() {

        return category;
    }

    public void setCategory(@NonNull Category category) {

        this.category = category;
    }

    @NonNull
    public List<Tag> getTags() {

        return tags;
    }

    public void addTag(@NonNull Tag tag) {

        tags.add(tag);
    }

    public void setTags(@NonNull List<Tag> tags) {

        for (Tag tag : tags) {

            addTag(tag);
        }
    }

    @NonNull
    public String getNotice() {

        return notice;
    }

    public void setNotice(String notice) {

        this.notice = notice;
    }

    @NonNull
    public Account getAccount() {

        return account;
    }

    public void setAccount(@NonNull Account account) {

        this.account = account;
    }

    public void addChild(@NonNull ExpenseObject child) {

        children.add(child);
        setExpenseType(EXPENSE_TYPES.PARENT_EXPENSE);
    }

    public void addChildren(@NonNull List<ExpenseObject> children) {

        for (ExpenseObject childExpense : children) {

            addChild(childExpense);
        }
    }

    @NonNull
    public List<ExpenseObject> getChildren() {

        return this.children;
    }

    public int countChildren() {

        return this.children.size();
    }

    public boolean isParent() {

        return expenseType == EXPENSE_TYPES.PARENT_EXPENSE;
    }

    /**
     * Methode die die Felder der Buchung checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann die Buchung ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob die Buchung in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {

        return !this.title.isEmpty() && this.price != 0 && this.category.isSet() && this.account.isSet();
    }

    /**
     * Wenn der index der Buchung größer als null ist, dann gibt es die Buchung bereits in der Datenbank
     * und man kann sie sicher verwenden.
     *
     * @return boolean
     */
    public boolean isValid() {

        return getIndex() > -1 && account.isValid() && category.isValid() && getExpenseCurrency().isValid() && areTagsValid();
    }

    public boolean areTagsValid() {

        boolean result = true;
        for (Tag tag : getTags()) {
            result = result && tag.isValid();
        }

        return result;
    }

    @Override
    public String toString() {

        return getIndex() + " " + getName() + " " + getUnsignedPrice();
    }

    public boolean equals(ExpenseObject otherExpense) {

        boolean result = getName().equals(otherExpense.getName());
        result = result && (getUnsignedPrice() == otherExpense.getUnsignedPrice());
        result = result && getExpenseCurrency().equals(otherExpense.getExpenseCurrency());
        result = result && getAccount().equals(otherExpense.getAccount());
        result = result && getExpenseType().equals(otherExpense.getExpenseType());
        result = result && getDateTime().equals(otherExpense.getDateTime());
        result = result && getNotice().equals(otherExpense.getNotice());
        result = result && getCategory().equals(otherExpense.getCategory());
        result = result && (getExchangeRate() == otherExpense.getExchangeRate());

        for (Tag tag : getTags()) {
            for (Tag otherTag : otherExpense.getTags()) {
                result = result && tag.equals(otherTag);
            }
        }

        for (ExpenseObject child : getChildren()) {
            for (ExpenseObject otherChildren : otherExpense.getChildren()) {
                result = result && child.equals(otherChildren);
            }
        }

        return result;
    }

    /**
     * Wenn die Buchung den Buchungstyp Normal oder Parent hat, dann ist die Buchung Valid.
     *
     * @return Ist die Buchung vom typ Normal oder Parent
     */
    public boolean isValidExpense() {

        return this.expenseType == EXPENSE_TYPES.NORMAL_EXPENSE || this.expenseType == EXPENSE_TYPES.PARENT_EXPENSE;
    }

    public void setExpenseType(@NonNull EXPENSE_TYPES expenseType) {

        this.expenseType = expenseType;
    }

    @NonNull
    public EXPENSE_TYPES getExpenseType() {

        return this.expenseType;
    }


    //Parcelable is super slow DO NOT USE IN PRODUCTION
    //TODO make faster!!

    /**
     * can be ignored mostly
     *
     * @return int
     */
    @Override
    public int describeContents() {

        return 0;
    }

    /**
     * converting the custom object into an parcelable object
     *
     * @param dest  destination Parcel
     * @param flags flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        Log.v(TAG, "Writing ExpenseObject to parcel" + flags);
        dest.writeLong(date.getTimeInMillis());
        dest.writeString(title);
        dest.writeDouble(price);
        dest.writeLong(index);
        dest.writeByte((byte) (expenditure ? 1 : 0));
        dest.writeParcelable(category, flags);
        dest.writeList(tags);
        dest.writeString(notice);
        dest.writeParcelable(account, flags);
        dest.writeList(children);
        dest.writeParcelable(expenseCurrency, flags);
    }

    /**
     * regenerating the parcelable object back into our Category object
     */
    public static final Parcelable.Creator<ExpenseObject> CREATOR = new Parcelable.Creator<ExpenseObject>() {

        @Override
        public ExpenseObject createFromParcel(Parcel in) {

            return new ExpenseObject(in);
        }

        @Override
        public ExpenseObject[] newArray(int size) {

            return new ExpenseObject[size];
        }
    };
}
