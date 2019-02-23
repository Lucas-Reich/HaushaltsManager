package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

public class Template implements Parcelable {
    private static final String TAG = Template.class.getSimpleName();

    private long mIndex;
    private ExpenseObject mTemplate;

    public Template(long index, ExpenseObject template) {
        setIndex(index);
        setTemplate(template);
    }

    public Template(ExpenseObject template) {

        this(-1, template);
    }

    private Template(Parcel source) {
        Log.v(TAG, "Recreating Template from parcel data");

        setIndex(source.readLong());
        setTemplate((ExpenseObject) source.readParcelable(ExpenseObject.class.getClassLoader()));
    }

    private void setIndex(long index) {
        mIndex = index;
    }

    public long getIndex() {
        return mIndex;
    }

    public void setTemplate(ExpenseObject template) {
        mTemplate = template;
    }

    public ExpenseObject getTemplate() {
        return mTemplate;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Template) {
            Template template = (Template) obj;

            boolean result;
            result = getIndex() == template.getIndex();
            result = result && getTemplate().equals(template.getTemplate());

            return result;
        } else {

            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.v(TAG, "Writing Template to parcel " + flags);

        dest.writeLong(getIndex());
        dest.writeParcelable(getTemplate(), flags);
    }

    public static final Parcelable.Creator<Template> CREATOR = new Parcelable.Creator<Template>() {

        @Override
        public Template createFromParcel(Parcel source) {
            return new Template(source);
        }

        @Override
        public Template[] newArray(int size) {
            return new Template[size];
        }
    };
}
