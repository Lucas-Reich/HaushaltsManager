package com.example.lucas.haushaltsmanager.Views;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ButtonContainer {
    private LinearLayout container;
    private OnButtonContainerClick listener;

    public ButtonContainer(LinearLayout rootLayout) {
        container = rootLayout;
    }

    public void setOnButtonClickListener(OnButtonContainerClick listener) {
        this.listener = listener;
    }

    public void createButtons(String[] text) {
        Button button;

        for (String field : text) {
            button = generateButton(field);

            container.addView(button);
        }
    }

    public void disableButtons() {
        int childCount = container.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childView = container.getChildAt(i);

            disableView(childView);
        }
    }

    private void disableView(View view) {
        view.setClickable(false);

        view.setEnabled(false);
    }

    private Button generateButton(String buttonText) {
        Button button = new Button(container.getContext());

        button.setText(buttonText);

        button.setOnClickListener(createOnButtonClickListener());

        return button;
    }

    private View.OnClickListener createOnButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View button) {
//                disableView(button);

                if (null != listener) {
                    String buttonText = ((Button) button).getText().toString();
                    listener.onClick(buttonText);
                }
            }
        };
    }

    public interface OnButtonContainerClick {
        void onClick(String buttonText);
    }
}
