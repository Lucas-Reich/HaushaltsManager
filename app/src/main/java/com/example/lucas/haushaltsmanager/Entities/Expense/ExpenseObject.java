package com.example.lucas.haushaltsmanager.Entities.Expense;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ExpenseObject implements Parcelable, Booking {
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
    private final UUID id;
    private String title;
    private Price price;
    private Calendar date;
    private Category category;
    private String notice;
    private UUID accountId;
    private List<ExpenseObject> children = new ArrayList<>();

    public ExpenseObject(
            @NonNull UUID id,
            @NonNull String expenseName,
            @NonNull Price price,
            Calendar date,
            @NonNull Category category,
            String notice,
            @NonNull UUID accountId,
            @NonNull EXPENSE_TYPES expenseType,
            @NonNull List<ExpenseObject> children
    ) {
        this.id = id;
        setTitle(expenseName);
        setPrice(price);
        setDate(date != null ? date : Calendar.getInstance());
        setCategory(category);
        setNotice(notice != null ? notice : "");
        this.accountId = accountId;
        setExpenseType(expenseType);
        addChildren(children);
    }

    public ExpenseObject(
            @NonNull String title,
            @NonNull Price price,
            @NonNull Category category,
            @NonNull UUID accountId
    ) {
        this(
                UUID.randomUUID(),
                title,
                price,
                Calendar.getInstance(),
                category,
                "",
                accountId,
                EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<ExpenseObject>()
        );
    }

    private ExpenseObject(Parcel source) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(source.readLong());
        setDate(cal);
        setTitle(source.readString());
        this.id = UUID.fromString(source.readString());
        setPrice((Price) source.readParcelable(Price.class.getClassLoader()));
        setCategory((Category) source.readParcelable(Category.class.getClassLoader()));
        setNotice(source.readString());
        accountId = UUID.fromString(source.readString());
        source.readList(this.children, ExpenseObject.class.getClassLoader());
        setExpenseType(EXPENSE_TYPES.valueOf(source.readString()));
    }

    public static ExpenseObject createDummyExpense() {
        Category category = new Category(
                app.getContext().getString(R.string.no_name),
                Color.black(),
                ExpenseType.expense()
        );

        return new ExpenseObject(
                UUID.randomUUID(),
                app.getContext().getString(R.string.no_name),
                new Price(0, false),
                Calendar.getInstance(),
                category,
                null,
                UUID.randomUUID(),
                EXPENSE_TYPES.DUMMY_EXPENSE,
                new ArrayList<ExpenseObject>()
        );
    }

    @NonNull
    public Calendar getDate() {

        return this.date;
    }

    @Override
    public void setDate(@NonNull Calendar date) {

        this.date = date;
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
        if (!(obj instanceof ExpenseObject)) {
            return false;
        }

        ExpenseObject otherExpense = (ExpenseObject) obj;

        boolean result = getTitle().equals(otherExpense.getTitle());
        result = result && (getUnsignedPrice() == otherExpense.getUnsignedPrice());
        result = result && getAccountId() == otherExpense.getAccountId();
        result = result && getExpenseType().equals(otherExpense.getExpenseType());
        result = result && getDate().getTimeInMillis() == otherExpense.getDate().getTimeInMillis();
        result = result && getNotice().equals(otherExpense.getNotice());
        result = result && getCategory().getId() == otherExpense.getCategory().getId();//ich kann die objekte nicht vergleichen da parent buchungen nur dummies bekommen

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
                getId().toString(),
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
        dest.writeString(id.toString());
        dest.writeParcelable(price, flags);
        dest.writeParcelable(category, flags);
        dest.writeString(notice);
        dest.writeString(accountId.toString());
        dest.writeList(children);
        dest.writeString(expenseType.name());
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public UUID getId() {
        return id;
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

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccount(Account account) {
        accountId = account.getId();
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

    @Deprecated
    public boolean isSet() {
        return !this.title.equals(app.getContext().getString(R.string.no_name))
                && price != null
                && this.category.isSet();
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
