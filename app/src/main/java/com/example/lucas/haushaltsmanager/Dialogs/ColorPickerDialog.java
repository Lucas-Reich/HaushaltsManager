// color picker from: https://github.com/chiralcode/Android-Color-Picker

package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Views.ColorPicker;

public class ColorPickerDialog extends AlertDialog {

    private ColorPicker colorPickerView;
    private OnColorSelectedListener mCallback;

    public ColorPickerDialog(Context context, int initialColor) {
        super(context);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        colorPickerView = new ColorPicker(context);
        colorPickerView.setColor(initialColor);

        relativeLayout.addView(colorPickerView, layoutParams);

        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), onClickListener);
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), onClickListener);

        setView(relativeLayout);
    }

    private OnClickListener onClickListener = new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int which) {

            switch (which) {
                case BUTTON_POSITIVE:

                    if (mCallback != null)
                        mCallback.onColorSelected(new Color(colorPickerView.getColor()));
                    break;
                case BUTTON_NEGATIVE:

                    dialog.dismiss();
                    break;
            }
        }
    };

    public void setOnColorSelectedListener(ColorPickerDialog.OnColorSelectedListener listener) {
        mCallback = listener;
    }

    public interface OnColorSelectedListener {
        void onColorSelected(Color color);
    }

}