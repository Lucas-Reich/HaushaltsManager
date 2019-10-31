package com.example.lucas.haushaltsmanager.Views;

import android.content.Context;
import android.util.AttributeSet;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

public class HeaderView extends android.support.v7.widget.AppCompatTextView {
    private IRequiredField boundField;

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bind(IRequiredField field) {
        setText(field.getTranslationKey());

        boundField = field;
    }

    public IRequiredField getBoundField() {
        return boundField;
    }
}