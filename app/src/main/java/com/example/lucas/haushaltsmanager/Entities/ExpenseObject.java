package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.lucas.haushaltsmanager.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ExpenseObject implements Parcelable {

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
    private List<String> tag = new LinkedList<>();

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

    public ExpenseObject(long index,@NonNull String title, double price, Calendar date, boolean expenditure,@NonNull Category category, String notice,@NonNull Account account, Currency expenseCurrency) {

        this.index = index;
        this.title = title;
        this.price = price;
        this.date = date != null ? date : Calendar.getInstance();
        this.expenditure = expenditure;
        this.category = category;
        this.notice = notice != null ? notice : "";
        this.account = account;
        this.expenseCurrency = expenseCurrency != null ? expenseCurrency : account.getCurrency();
    }

    public ExpenseObject(long index,@NonNull String title, double price, boolean expenditure,@NonNull String date,@NonNull Category category, String notice,@NonNull Account account, Currency expenseCurrency) {

        this.index = index;
        this.title = title;
        this.price = price;
        this.date = Calendar.getInstance();
        setDateTime(date);
        this.expenditure = expenditure;
        this.category = category;
        this.notice = notice != null ? notice : "";
        this.account = account;
        this.expenseCurrency = expenseCurrency != null ? expenseCurrency : account.getCurrency();
    }

    public ExpenseObject(@NonNull String title, double price, boolean expenditure,@NonNull Category category, Currency expenseCurrency,@NonNull Account account) {

        this(-1, title, price, null, expenditure, category, null, account, expenseCurrency);
    }

    /**
     * Methode um eine dummy Expense zu erstellen
     *
     * @param context Context
     * @return dummy Expense
     */
    public static ExpenseObject createDummyExpense(Context context) {

        return new ExpenseObject(-1, context.getResources().getString(R.string.no_name), 0, null, false, Category.createDummyCategory(context), null, Account.createDummyAccount(context, null), Currency.createDummyCurrency(context));
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

    public Currency getExpenseCurrency() {

        return this.expenseCurrency;
    }

    public void setExpenseCurrency(Currency expenseCurrency) {

        this.expenseCurrency = expenseCurrency;
    }

    @Override
    public String toString() {

        return "" + this.index + " " + this.title;
    }

    public Calendar getDateTime() {

        return this.date;
    }

    public String getDBDateTime() {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(this.date.getTime());
    }

    public String getDate() {

        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(this.date.getTime());
    }

    public String getDisplayableDateTime(Context context) {

        return DateUtils.formatDateTime(context, this.date.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }

    public void setDateTime(Calendar date) {

        this.date = date;
    }

    public void setDateTime(String date) {

        try {

            this.date.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date));
        } catch (ParseException e) {

            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getUnsignedPrice() {
        return price;
    }
/*
    double getSignedPrice() {

        return this.expenditure ? 0 - this.price : price;
    }
*/

    /**
     * Method um den tatsächlichen wert einer Buchung zu bekommen.
     * Falls die Buchung ein Parent ist wird die Summer der Kinder zurückgegeben
     *
     * @return tatsächlicher Wert der Buchung
     */
    public double getSignedPrice() {

        if (hasChildren()) {

            double calcPrice = 0;

            for(ExpenseObject child : this.children) {

                calcPrice += child.getSignedPrice();
            }

            return calcPrice;
        } else {

            return this.expenditure ? 0 - this.price : price;
        }
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public boolean getExpenditure() {
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

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tag;
    }

    public void setTag(String tag) {

        this.tag.add(tag);
    }

    public void setTags(List<String> tags) {

        this.tag = tags;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {

        this.notice = notice;
    }

    public Account getAccount() {

        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void addChild(ExpenseObject child) {

        children.add(child);
    }

    public void addChildren(List<ExpenseObject> children) {

        this.children.addAll(children);
    }

    public List<ExpenseObject> getChildren() {

        return this.children;
    }

    public int countChildren() {

        return this.children.size();
    }

    public boolean hasChildren() {

        return !this.children.isEmpty() || this.account.getIndex() == 9999;
    }

    public boolean isSet() {

        return !this.title.isEmpty() && this.price != 0.0 && !this.category.getCategoryName().isEmpty();
    }


    //make class Parcelable || Parcelable is super slow DO NOT USE IN PRODUCTION
    //TODO make faster!!

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

        Log.v(TAG, "ParcelData (Parcel source): time to put back parcel data");
        cal.setTimeInMillis(source.readLong());
        date = cal;
        title = source.readString();
        price = source.readDouble();
        index = source.readLong();
        expenditure = source.readByte() != 0;
        category = source.readParcelable(Category.class.getClassLoader());
        tag = source.createStringArrayList();
        notice = source.readString();
        account = source.readParcelable(Account.class.getClassLoader());
        children = source.createTypedArrayList(ExpenseObject.CREATOR);
        expenseCurrency = source.readParcelable(Currency.class.getClassLoader());
    }

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

        Log.v(TAG, "write to parcel..." + flags);
        dest.writeLong(date.getTimeInMillis());
        dest.writeString(title);
        dest.writeDouble(price);
        dest.writeLong(index);
        dest.writeByte((byte) (expenditure ? 1 : 0));
        dest.writeParcelable(category, flags);
        dest.writeList(tag);
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
