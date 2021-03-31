package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.R;

import java.util.Objects;
import java.util.UUID;

public class Category implements Parcelable {
    public static final Creator<Category> CREATOR = new Creator<Category>() {

        @Override
        public Category createFromParcel(Parcel in) {

            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {

            return new Category[size];
        }
    };

    private final UUID id;
    private String name;
    private Color color;
    private ExpenseType defaultExpenseType;

    public Category(
            @NonNull UUID id,
            @NonNull String name,
            @NonNull Color color,
            @NonNull ExpenseType defaultExpenseType
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.defaultExpenseType = defaultExpenseType;
    }

    public Category(
            @NonNull String name,
            @NonNull Color color,
            @NonNull ExpenseType expenseType
    ) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.color = color;
        this.defaultExpenseType = expenseType;
    }

    private Category(Parcel source) {
        this.id = UUID.fromString(source.readString());
        this.name = source.readString();
        this.color = (Color) source.readParcelable(Color.class.getClassLoader());
        this.defaultExpenseType = ExpenseType.load(source.readInt() == 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Category)) {
            return false;
        }

        Category otherCategory = (Category) obj;

        return id.equals(otherCategory.getId())
                && name.equals(otherCategory.getTitle())
                && color.equals(otherCategory.getColor())
                && defaultExpenseType.equals(otherCategory.getDefaultExpenseType());
    }

    @Override
    @NonNull
    public String toString() {
        return getTitle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getTitle(),
                getColor(),
                getDefaultExpenseType()
        );
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id.toString());
        dest.writeString(name);
        dest.writeParcelable(color, flags);
        dest.writeInt(defaultExpenseType.value() ? 1 : 0);
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(@NonNull Color color) {
        this.color = color;
    }

    public ExpenseType getDefaultExpenseType() {
        return defaultExpenseType;
    }

    public void setDefaultExpenseType(@NonNull ExpenseType expenseType) {
        this.defaultExpenseType = expenseType;
    }

    /**
     * Methode die die Felder der Kategorie checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann die Kategorie ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob die Kategorie in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {
        return !name.equals(app.getContext().getString(R.string.no_name));
    }
}
