package com.example.lucas.haushaltsmanager.Entities.Booking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Booking implements Parcelable, IBooking {
    public static final Parcelable.Creator<Booking> CREATOR = new Parcelable.Creator<Booking>() {

        @Override
        public Booking createFromParcel(Parcel in) {

            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {

            return new Booking[size];
        }
    };

    private UUID id;
    private EXPENSE_TYPES expenseType;
    private String title;
    private Price price;
    private Calendar date;
    private Category category;
    private String notice;
    private UUID accountId;

    public Booking(
            @NonNull UUID id,
            @NonNull String expenseName,
            @NonNull Price price,
            Calendar date,
            @NonNull Category category,
            String notice,
            @NonNull UUID accountId,
            @NonNull EXPENSE_TYPES expenseType
    ) {
        this.id = id;
        setTitle(expenseName);
        setPrice(price);
        setDate(date != null ? date : Calendar.getInstance());
        setCategory(category);
        setNotice(notice != null ? notice : "");
        this.accountId = accountId;
        setExpenseType(expenseType);
    }

    public Booking(
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
                EXPENSE_TYPES.NORMAL_EXPENSE
        );
    }

    private Booking(Parcel source) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(source.readLong());
        setDate(cal);
        setTitle(source.readString());
        this.id = UUID.fromString(source.readString());
        setPrice((Price) source.readParcelable(Price.class.getClassLoader()));
        setCategory((Category) source.readParcelable(Category.class.getClassLoader()));
        setNotice(source.readString());
        accountId = UUID.fromString(source.readString());
        setExpenseType(EXPENSE_TYPES.valueOf(source.readString()));
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
        if (!(obj instanceof Booking)) {
            return false;
        }

        Booking otherExpense = (Booking) obj;

        boolean result = getTitle().equals(otherExpense.getTitle());
        result = result && (getUnsignedPrice() == otherExpense.getUnsignedPrice());
        result = result && getAccountId() == otherExpense.getAccountId();
        result = result && getExpenseType().equals(otherExpense.getExpenseType());
        result = result && getDate().getTimeInMillis() == otherExpense.getDate().getTimeInMillis();
        result = result && getNotice().equals(otherExpense.getNotice());
        result = result && getCategory().getId() == otherExpense.getCategory().getId();//ich kann die objekte nicht vergleichen da parent buchungen nur dummies bekommen

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
        dest.writeString(expenseType.name());
    }

    @NonNull
    public Price getPrice() {
        return price;
    }

    public void setPrice(@NonNull Price price) {
        this.price = price;
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
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
        return price.getSignedValue();
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

    @NonNull
    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(@NonNull UUID accountId) {
        this.accountId = accountId;
    }

    public void setAccount(Account account) {
        setAccountId(account.getId());
    }

    @Deprecated
    public boolean isSet() {
        return !this.title.isEmpty()
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
        PARENT_EXPENSE,
        NORMAL_EXPENSE,
        TRANSFER_EXPENSE, // TODO: Only used in TransferActivity
        CHILD_EXPENSE
    }
}
