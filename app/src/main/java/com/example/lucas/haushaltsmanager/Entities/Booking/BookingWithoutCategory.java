package com.example.lucas.haushaltsmanager.Entities.Booking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.Calendar;
import java.util.UUID;

@Entity(tableName = "bookings")
public class BookingWithoutCategory implements IBooking, Parcelable {
    @Ignore
    public static final Parcelable.Creator<BookingWithoutCategory> CREATOR = new Parcelable.Creator<BookingWithoutCategory>() {

        @Override
        public BookingWithoutCategory createFromParcel(Parcel in) {
            return new BookingWithoutCategory(in);
        }

        @Override
        public BookingWithoutCategory[] newArray(int size) {
            return new BookingWithoutCategory[size];
        }
    };

    @PrimaryKey
    @NonNull
    private UUID id;
    @ColumnInfo(name = "expense_type")
    private Booking.EXPENSE_TYPES expenseType;
    private String title;
    private Price price;
    private Calendar date;
    private String notice;
    @ColumnInfo(name = "account_id")
    private UUID accountId;
    @ColumnInfo(name = "category_id")
    private UUID categoryId;

    public BookingWithoutCategory(
            @NonNull UUID id,
            @NonNull String title,
            @NonNull Price price,
            Calendar date,
            UUID categoryId,
            String notice,
            @NonNull UUID accountId,
            @NonNull Booking.EXPENSE_TYPES expenseType
    ) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.date = date;
        this.categoryId = categoryId;
        this.notice = notice;
        this.accountId = accountId;
        this.expenseType = expenseType;
    }

    @Ignore
    private BookingWithoutCategory(Parcel source) {
        this.id = UUID.fromString(source.readString());
        this.expenseType = Booking.EXPENSE_TYPES.valueOf(source.readString());
        this.title = source.readString();
        this.price = (Price) source.readParcelable(Price.class.getClassLoader());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(source.readLong());
        this.date = cal;
        this.notice = source.readString();
        this.accountId = UUID.fromString(source.readString());
        this.categoryId = UUID.fromString(source.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id.toString());
        dest.writeString(expenseType.name());
        dest.writeString(title);
        dest.writeParcelable(price, flags);
        dest.writeLong(date.getTimeInMillis());
        dest.writeString(notice);
        dest.writeString(accountId.toString());
        dest.writeString(categoryId.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BookingWithoutCategory)) {
            return false;
        }

        BookingWithoutCategory otherBooking = (BookingWithoutCategory) obj;

        boolean result = expenseType.equals(otherBooking.expenseType);
        result = result && title.equals(otherBooking.title);
        result = result && price.equals(otherBooking.price);
        result = result && date.equals(otherBooking.date);
        result = result && notice.equals(otherBooking.notice);
        result = result && accountId.equals(otherBooking.accountId);
        result = result && categoryId.equals(otherBooking.categoryId);

        return result;
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public Price getPrice() {
        return price;
    }

    public void setPrice(@NonNull Price price) {
        this.price = price;
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
    public Booking.EXPENSE_TYPES getExpenseType() {
        return this.expenseType;
    }

    public void setExpenseType(@NonNull Booking.EXPENSE_TYPES expenseType) {
        this.expenseType = expenseType;
    }

    @NonNull
    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NonNull UUID categoryId) {
        this.categoryId = categoryId;
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

    public enum EXPENSE_TYPES {
        PARENT_EXPENSE,
        NORMAL_EXPENSE,
        TRANSFER_EXPENSE, // TODO: Only used in TransferActivity
        CHILD_EXPENSE
    }
}
