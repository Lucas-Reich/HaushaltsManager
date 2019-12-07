package com.example.lucas.haushaltsmanager.Views;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;

import java.util.Locale;

import javax.annotation.Nullable;

public class MoneyEditText extends androidx.appcompat.widget.AppCompatEditText {
    public MoneyEditText(Context context) {
        super(context);

        setHint(MoneyUtils.formatHumanReadable(null, Locale.getDefault()));
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public MoneyEditText(Context context, @Nullable Price initialValue) {
        super(context);

        setHint(MoneyUtils.formatHumanReadable(initialValue, Locale.getDefault()));
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public MoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        setHint(MoneyUtils.formatHumanReadable(null, Locale.getDefault()));
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    public MoneyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setHint(MoneyUtils.formatHumanReadable(null, Locale.getDefault()));
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
    }
}
