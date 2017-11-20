package com.example.lucas.haushaltsmanager;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

class ExpenseObject implements Parcelable {

    private Calendar date;
    private String title = "";
    private double price = 0;
    private long index;
    private boolean expenditure;
    private Category category;
    private List<String> tag = new LinkedList<>();
    private String notice = "";
    private Account account;
    private List<ExpenseObject> children = new ArrayList<>();

    private String TAG = "ExpenseObject: ";


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

    String getOldDate() {//deprecated use getDate instead

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

    Calendar getDate() {

        return this.date;
    }

    String getDBDate() {

        String dbDate = this.date.get(Calendar.YEAR) + "-";
        if (this.date.get(Calendar.MONTH) <= 9) {

            dbDate += "0";
        }
        dbDate += this.date.get(Calendar.MONTH);
        dbDate += "-" + this.date.get(Calendar.DAY_OF_MONTH) + " " + this.date.get(Calendar.HOUR_OF_DAY) + ":" + this.date.get(Calendar.MINUTE) + ":" + this.date.get(Calendar.SECOND);

        return dbDate;
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

    /**
     * @param expenditure true (for outgoing money) or false (for incoming money)
     */
    void setExpenditure(boolean expenditure) {
        this.expenditure = expenditure;
    }

    void setExpenditure(int expenditure) {

        this.expenditure = expenditure != 0;
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

        return !this.children.isEmpty();
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


    //make class Parcelable

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
