package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

class ExpenseObject implements Parcelable {

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

    double getExchangeRate() {

        return this.expenseCurrency.getRateToBase();
    }

    @Nullable
    Double getCalcPrice() {

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

    Currency getExpenseCurrency() {

        return this.expenseCurrency;
    }

    void setExpenseCurrency(Currency expenseCurrency) {

        this.expenseCurrency = expenseCurrency;
    }

    @Override
    public String toString() {

        String toDisplay = "Book " + this.title + " " + this.price + " times as an " + this.expenditure;
        String toDisplay2 = "The booking belongs to " + this.category + ", happened at the " + date.get(Calendar.DAY_OF_MONTH) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.YEAR);
        String toDisplay3 = " and has to be inserted to " + this.account;


        return toDisplay + toDisplay2 + toDisplay3;
    }

    Calendar getDateTime() {

        return this.date;
    }

    String getDBDateTime() {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(this.date.getTime());
    }

    String getDate() {

        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(this.date.getTime());
    }

    String getDisplayableDateTime(Context context) {

        return DateUtils.formatDateTime(context, this.date.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }

    void setDateTime(Calendar date) {

        this.date = date;
    }

    void setDateTime(String date) {

        try {

            this.date.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date));
        } catch (ParseException e) {

            e.printStackTrace();
        }
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    double getUnsignedPrice() {
        return price;
    }

    double getSignedPrice() {

        return this.expenditure ? 0 - this.price : price;
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

    /**
     * @param expenditure true (for outgoing money) or false (for incoming money)
     */
    void setExpenditure(boolean expenditure) {
        this.expenditure = expenditure;
    }

    Category getCategory() {
        return category;
    }

    void setCategory(Category category) {
        this.category = category;
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

    void addChild(ExpenseObject child) {

        children.add(child);
    }

    void addChildren(List<ExpenseObject> children) {

        this.children.addAll(children);
    }

    List<ExpenseObject> getChildren() {

        return this.children;
    }

    int countChildren() {

        return this.children.size();
    }

    boolean hasChildren() {

        return !this.children.isEmpty() || this.account.getIndex() == 9999;
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
        Log.d("ExpenseObject date: ", "" + getDateTime());
        Log.d("ExpenseObject notice: ", "" + notice);
        Log.d("ExpenseObject account: ", "" + account.getAccountName());

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
