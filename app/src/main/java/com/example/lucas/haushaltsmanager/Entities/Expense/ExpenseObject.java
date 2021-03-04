package com.example.lucas.haushaltsmanager.Entities.Expense;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
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
    private long index;
    private String title;
    private Price price;
    private Calendar date;
    private Category category;
    private String notice;
    private long mAccountId;
    private List<ExpenseObject> children = new ArrayList<>();
    private Currency mCurrency;

    public ExpenseObject(
            long index,
            @NonNull String expenseName,
            Price price,
            Calendar date,
            @NonNull Category category,
            String notice,
            long accountId,
            @NonNull EXPENSE_TYPES expenseType,
            @NonNull List<ExpenseObject> children,
            @NonNull Currency currency
    ) {
        setIndex(index);
        setTitle(expenseName);
        setPrice(price);
        setDateTime(date != null ? date : Calendar.getInstance());
        setCategory(category);
        setNotice(notice != null ? notice : "");
        setAccountId(accountId);
        setExpenseType(expenseType);
        addChildren(children);
        setCurrency(currency);
    }

    public ExpenseObject(@NonNull String title, Price price, @NonNull Category category, long accountId, Currency currency) {

        this(
                ExpensesDbHelper.INVALID_INDEX,
                title,
                price,
                Calendar.getInstance(),
                category,
                null,
                accountId,
                EXPENSE_TYPES.NORMAL_EXPENSE,
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
                new ArrayList<ExpenseObject>(),
                mainCurrency
        );
    }

    public static ExpenseObject copy(ExpenseObject other) {
        return copyWithNewIndex(other, other.getIndex());
    }

    public static ExpenseObject copyWithNewIndex(ExpenseObject other, long newIndex) {
        return new ExpenseObject(
                newIndex,
                other.getTitle(),
                other.getPrice(),
                other.getDate(),
                other.getCategory(),
                other.getNotice(),
                other.getAccountId(),
                other.getExpenseType(),
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

        for (ExpenseObject child : getChildren()) {
            result = result && otherExpense.getChildren().contains(child);
        }

        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(
                "%s %s %s",
                getIndex(),
                getTitle(),
                getUnsignedPrice()
        );
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date.getTimeInMillis());
        dest.writeString(title);
        dest.writeLong(index);
        dest.writeParcelable(price, flags);
        dest.writeParcelable(category, flags);
        dest.writeString(notice);
        dest.writeLong(mAccountId);
        dest.writeList(children);
        dest.writeString(expenseType.name());
        dest.writeParcelable(mCurrency, flags);
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Currency getCurrency() {

        return mCurrency;
    }

    public void setCurrency(Currency currency) {
        mCurrency = currency;
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

    @NonNull
    public Category getCategory() {

        return category;
    }

    public void setCategory(@NonNull Category category) {

        this.category = category;
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
