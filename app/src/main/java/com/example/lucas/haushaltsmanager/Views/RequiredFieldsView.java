package com.example.lucas.haushaltsmanager.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.List;

public class RequiredFieldsView extends View {
    private List<IRequiredField> requiredFields;
    private MappingList mappingList;

    private HeaderView header;

    private OnMappingCreated listener;

    public RequiredFieldsView(Context context) {
        super(context);
    }

    public RequiredFieldsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RequiredFieldsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void configure(
            final ButtonContainer buttonContainer,
            HeaderView headerView,
            String[] availableHeader,
            List<IRequiredField> requiredFields
    ) {
        this.header = headerView;
        mappingList = new MappingList();

        this.requiredFields = requiredFields;

        buttonContainer.createButtons(availableHeader);

        buttonContainer.setOnButtonClickListener(new ButtonContainer.OnButtonContainerClick() {
            @Override
            public void onClick(String buttonText, int buttonIndex) {
                mappingList.addMapping(header.getBoundField(), buttonIndex);

                if (isLast()) {
                    listener.onMappingCreated(mappingList);

                    buttonContainer.disableButtons();
                } else {
                    showNext();
                }
            }
        });

        showNext();
    }

    public void setListener(OnMappingCreated listener) {
        this.listener = listener;
    }

    public void showNext() {
        IRequiredField nextField = getNext();

        header.bind(nextField);
    }

    private boolean isLast() {
        IRequiredField currentField = header.getBoundField();

        return requiredFields.indexOf(currentField) + 1 == requiredFields.size();
    }

    private IRequiredField getNext() {
        IRequiredField currentField = header.getBoundField();

        int internalIndex = requiredFields.indexOf(currentField);

        // TODO: Kann ich eine ArrayList<T>.hasNext(T input) Methode implementieren, die nachguckt, ob der input 1. in der Liste zu finden ist und 2. ob nach dem input ein weiterer eintrag kommt

        return requiredFields.get(internalIndex + 1);
    }

    public interface OnMappingCreated {
        void onMappingCreated(MappingList mapping);
    }
}
