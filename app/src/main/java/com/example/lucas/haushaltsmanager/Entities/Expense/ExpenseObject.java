package com.example.lucas.haushaltsmanager.Entities.Expense;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseObject implements Parcelable, Booking {
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

    private EXPENSE_TYPES expenseType;

    /**
     * Database index of expense
     */
    private long index;

    /**
     * Title of expense
     */
    private String title;

    private Price price;

    /**
     * Date of expense
     */
    private Calendar date;

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
     * Id des Zugehörigen Accounts
     */
    private long mAccountId;

    /**
     * Children of expense
     */
    private List<ExpenseObject> children = new ArrayList<>();

    /**
     * Die Währung der Buchung
     */
    private Currency mCurrency;

    public ExpenseObject(long index, @NonNull String expenseName, Price price, Calendar date, @NonNull Category category, String notice, long accountId, @NonNull EXPENSE_TYPES expenseType, @NonNull List<Tag> tags, @NonNull List<ExpenseObject> children, @NonNull Currency currency) {

        setIndex(index);
        setTitle(expenseName);
        setPrice(price);
        setDateTime(date != null ? date : Calendar.getInstance());
        setCategory(category);
        setNotice(notice != null ? notice : "");
        setAccountId(accountId);
        setExpenseType(expenseType);
        setTags(tags);
        addChildren(children);
        setCurrency(currency);
    }

    public ExpenseObject(@NonNull String title, Price price, @NonNull Category category, long accountId, Currency currency) {

        this(
                -1,
                title,
                price,
                Calendar.getInstance(),
                category,
                null,
                accountId,
                EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                currency
        );
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
    private ExpenseObject(Parcel source) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(source.readLong());
        setDateTime(cal);
        setTitle(source.readString());
        setIndex(source.readLong());
        setPrice((Price) source.readParcelable(Price.class.getClassLoader()));
        setCategory((Category) source.readParcelable(Category.class.getClassLoader()));
        source.readList(this.tags, Tag.class.getClassLoader());
        setNotice(source.readString());
        setAccountId(source.readLong());
        source.readList(this.children, ExpenseObject.class.getClassLoader());
        setExpenseType(EXPENSE_TYPES.valueOf(source.readString()));
        setCurrency((Currency) source.readParcelable(Currency.class.getClassLoader()));
    }

    /**
     * Methode um eine dummy Booking zu erstellen
     *
     * @return dummy Booking
     */
    public static ExpenseObject createDummyExpense() {
        Currency mainCurrency = new UserSettingsPreferences(app.getContext()).getMainCurrency();

        return new ExpenseObject(
                ExpensesDbHelper.INVALID_INDEX,
                app.getContext().getString(R.string.no_name),
                new Price(0, false, mainCurrency),
                Calendar.getInstance(),
                Category.createDummyCategory(),
                null,
                ExpensesDbHelper.INVALID_INDEX,
                EXPENSE_TYPES.DUMMY_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                mainCurrency
        );
    }

    public static ExpenseObject copy(ExpenseObject other) {
        return new ExpenseObject(
                other.getIndex(),
                other.getTitle(),
                other.getPrice(),
                other.getDate(),
                other.getCategory(),
                other.getNotice(),
                other.getAccountId(),
                other.getExpenseType(),
                other.getTags(),
                other.getChildren(),
                other.getCurrency()
        );
    }

    @NonNull
    public Calendar getDate() {

        return this.date;
    }

    @NonNull
    public String getTitle() {

        return title;
    }

    public void setTitle(@NonNull String title) {

        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExpenseObject))
            return false;

        ExpenseObject otherExpense = (ExpenseObject) obj;

        boolean result = getTitle().equals(otherExpense.getTitle());
        result = result && (getUnsignedPrice() == otherExpense.getUnsignedPrice());
        result = result && getAccountId() == otherExpense.getAccountId();
        result = result && getExpenseType().equals(otherExpense.getExpenseType());
        result = result && getDate().getTimeInMillis() == otherExpense.getDate().getTimeInMillis();
        result = result && getNotice().equals(otherExpense.getNotice());
        result = result && getCategory().getIndex() == otherExpense.getCategory().getIndex();//ich kann die objekte nicht vergleichen da parent buchungen nur dummies bekommen
        result = result && getCurrency().getIndex() == otherExpense.getCurrency().getIndex();//ich kann die objekte nicht vergleichen da parent buchungen nur dummies bekommen

        for (Tag tag : getTags()) {
            for (Tag otherTag : otherExpense.getTags()) {
                result = result && tag.equals(otherTag);
            }
        }

        for (ExpenseObject child : getChildren()) {
            result = result && otherExpense.getChildren().contains(child);
        }

        return result;
    }

    @Override
    public String toString() {

        return getIndex() + " " + getTitle() + " " + getUnsignedPrice();
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
        dest.writeLong(date.getTimeInMillis());
        dest.writeString(title);
        dest.writeLong(index);
        dest.writeParcelable(price, flags);
        dest.writeParcelable(category, flags);
        dest.writeList(tags);
        dest.writeString(notice);
        dest.writeLong(mAccountId);
        dest.writeList(children);
        dest.writeString(expenseType.name());
        dest.writeParcelable(mCurrency, flags);
    }

    public void setDateTime(@NonNull Calendar date) {

        this.date = date;
    }

    @NonNull
    public String getDateString() {

        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(getDate().getTime());
    }

    @NonNull
    public String getDisplayableDateTime() {

        return DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(getDate().getTimeInMillis()));
    }

    public double getUnsignedPrice() {

        return price.getUnsignedValue();
    }

    /**
     * Method um den tatsächlichen wert einer Buchung zu bekommen.
     * Falls die Buchung ein Parent ist wird die Summer der Kinder zurückgegeben
     *
     * @return tatsächlicher Wert der Buchung
     */
    public double getSignedPrice() {
        if (!isParent()) {
            return price.getSignedValue();
        }


        double calcPrice = 0;
        for (ExpenseObject child : children) {
            calcPrice += child.getSignedPrice();
        }

        return calcPrice;
    }

    public long getIndex() {

        return index;
    }

    private void setIndex(long index) {

        this.index = index;
    }

    public boolean isExpenditure() {

        return price.isNegative();
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
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

    /**
     * Methode um ein Liste von Tags zu einer Buchung hinzuzufügen
     *
     * @param tags Tags die der Buchung hinzugefügt werden sollen
     */
    public void setTags(@NonNull List<Tag> tags) {
        for (Tag tag : tags)
            addTag(tag);
    }

    public void addTag(@NonNull Tag tag) {

        tags.add(tag);
    }

    @NonNull
    public String getNotice() {

        return notice;
    }

    public void setNotice(String notice) {

        this.notice = notice;
    }

    public long getAccountId() {
        return mAccountId;
    }

    public void setAccountId(long accountId) {
        mAccountId = accountId;
    }

    public void setAccount(Account account) {
        mAccountId = account.getIndex();
    }

    public Currency getCurrency() {

        return mCurrency;
    }

    public void setCurrency(Currency currency) {
        mCurrency = currency;
    }

    public void removeChild(ExpenseObject child) {

        this.children.remove(child);
        if (children.size() == 0)
            setExpenseType(EXPENSE_TYPES.NORMAL_EXPENSE);
    }

    public void removeChildren() {
        children.clear();
    }

    public ExpenseObject addChild(@NonNull ExpenseObject child) {

        child.setExpenseType(EXPENSE_TYPES.CHILD_EXPENSE);
        children.add(child);
        setExpenseType(EXPENSE_TYPES.PARENT_EXPENSE);

        return this;
    }

    /**
     * Methode um einer Buchung mehrere Buchungen hinzuzufügen
     *
     * @param children Buchungen die als Kinder hinzugefügt werden sollen
     */
    public void addChildren(@NonNull List<ExpenseObject> children) {
        for (ExpenseObject childExpense : children)
            addChild(childExpense);
    }

    @NonNull
    public List<ExpenseObject> getChildren() {

        return this.children;
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

        return !this.title.equals(app.getContext().getString(R.string.no_name))
                && price != null
                && this.category.isSet()
                && this.mAccountId != -1;
    }

    @NonNull
    public EXPENSE_TYPES getExpenseType() {

        return this.expenseType;
    }

    public void setExpenseType(@NonNull EXPENSE_TYPES expenseType) {

        this.expenseType = expenseType;
    }

    public enum EXPENSE_TYPES {
        DUMMY_EXPENSE,
        DATE_PLACEHOLDER,
        PARENT_EXPENSE,
        NORMAL_EXPENSE,
        TRANSFER_EXPENSE,
        CHILD_EXPENSE
    }
}
