package com.example.lucas.haushaltsmanager.Entities.Booking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BookingWithCategory implements IBooking, Parcelable {
    @Ignore
    public static final Parcelable.Creator<BookingWithCategory> CREATOR = new Parcelable.Creator<BookingWithCategory>() {

        @Override
        public BookingWithCategory createFromParcel(Parcel in) {

            return new BookingWithCategory(in);
        }

        @Override
        public BookingWithCategory[] newArray(int size) {

            return new BookingWithCategory[size];
        }
    };

    @Embedded
    private BookingWithoutCategory booking;
    @Relation(
            parentColumn = "id",
            entityColumn = "id"
    )
    private Category category;

    // Room Constructor
    public BookingWithCategory(
            BookingWithoutCategory booking,
            Category category
    ) {
        this.booking = booking;
        this.category = category;
    }

    public void setBooking(BookingWithoutCategory booking) {
        this.booking = booking;
    }

    @Ignore
    public BookingWithCategory(
            @NonNull UUID id,
            @NonNull String expenseName,
            @NonNull Price price,
            Calendar date,
            @NonNull Category category,
            String notice,
            @NonNull UUID accountId,
            @NonNull Booking.EXPENSE_TYPES expenseType
    ) {
        booking = new BookingWithoutCategory(
                id,
                expenseName,
                price,
                date,
                category.getId(),
                notice,
                accountId,
                expenseType
        );
        this.category = category;
    }

    @Ignore
    public BookingWithCategory(
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
                Booking.EXPENSE_TYPES.NORMAL_EXPENSE
        );
    }

    @Ignore
    private BookingWithCategory(Parcel source) {
        booking = (BookingWithoutCategory) source.readParcelable(BookingWithoutCategory.class.getClassLoader());
        category = (Category) source.readParcelable(Category.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(booking, flags);
        dest.writeParcelable(category, flags);
    }

    @NonNull
    public Calendar getDate() {
        return booking.getDate();
    }

    @Override
    public void setDate(@NonNull Calendar date) {
        booking.setDate(date);
    }

    @NonNull
    public String getTitle() {
        return booking.getTitle();
    }

    public void setTitle(@NonNull String title) {
        booking.setTitle(title);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BookingWithCategory)) {
            return false;
        }

        BookingWithCategory otherBooking = (BookingWithCategory) obj;
        return otherBooking.booking.equals(booking)
                && otherBooking.category.equals(category);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    public Price getPrice() {
        return booking.getPrice();
    }

    public void setPrice(@NonNull Price price) {
        booking.setPrice(price);
    }

    @NonNull
    public UUID getId() {
        return booking.getId();
    }

    public void setId(@NonNull UUID id) {
        booking.setId(id);
    }

    @NonNull
    public String getDateString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(getDate().getTime());
    }

    @NonNull
    public String getDisplayableDateTime() {
        return DateFormat.getDateInstance(DateFormat.SHORT)
                .format(new Date(getDate().getTimeInMillis()));
    }

    public double getUnsignedPrice() {
        return getPrice().getUnsignedValue();
    }

    public double getSignedPrice() {
        return getPrice().getSignedValue();
    }

    public boolean isExpenditure() {
        return getPrice().isNegative();
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
        return booking.getNotice();
    }

    public void setNotice(String notice) {
        booking.setNotice(notice);
    }

    @NonNull
    public UUID getAccountId() {
        return booking.getAccountId();
    }

    public void setAccountId(@NonNull UUID accountId) {
        booking.setAccountId(accountId);
    }

    public void setAccount(Account account) {
        setAccountId(account.getId());
    }

    @Deprecated
    public boolean isSet() {
        return !getTitle().isEmpty()
                && this.category.isSet();
    }

    @NonNull
    public Booking.EXPENSE_TYPES getExpenseType() {
        return booking.getExpenseType();
    }

    public void setExpenseType(@NonNull Booking.EXPENSE_TYPES expenseType) {
        booking.setExpenseType(expenseType);
    }

    public BookingWithoutCategory getBooking() {
        return booking;
    }
}
