package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.UUID;

public class TemplateBooking implements Parcelable {
    public static final Parcelable.Creator<TemplateBooking> CREATOR = new Parcelable.Creator<TemplateBooking>() {

        @Override
        public TemplateBooking createFromParcel(Parcel source) {
            return new TemplateBooking(source);
        }

        @Override
        public TemplateBooking[] newArray(int size) {
            return new TemplateBooking[size];
        }
    };

    private final UUID id;
    private final ExpenseObject template;

    public TemplateBooking(
            @NonNull UUID id,
            @NonNull ExpenseObject template
    ) {
        this.id = id;
        this.template = template;
    }

    public TemplateBooking(@NonNull ExpenseObject template) {
        this(UUID.randomUUID(), template);
    }

    private TemplateBooking(Parcel source) {
        this.id = UUID.fromString(source.readString());
        this.template = (ExpenseObject) source.readParcelable(ExpenseObject.class.getClassLoader());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TemplateBooking)) {
            return false;
        }

        TemplateBooking otherTemplate = (TemplateBooking) obj;

        return id.equals(otherTemplate.getId())
                && template.equals(otherTemplate.getTemplate());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id.toString());
        dest.writeParcelable(this.template, flags);
    }

    public UUID getId() {
        return id;
    }

    public ExpenseObject getTemplate() {
        return template;
    }

    public String getTitle() {
        return template.getTitle();
    }

    public Category getCategory() {
        return template.getCategory();
    }

    public Price getPrice() {
        return template.getPrice();
    }
}
