package com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities;

public enum AccountEnum {
    // TODO: Kann ich das gebrauchen?
    BALANCE() {
        @Override
        void validate(String field) {
            // Der string muss überprüft werden, ob er alle kriterien für den titel eines kontos erfüllt
        }

        @Override
        Object getDataType() {
            return Double.class;
        }
    },

    TITLE() {
        @Override
        void validate(String field) {

        }

        @Override
        Object getDataType() {
            return String.class;
        }
    };

    abstract void validate(String field);

    abstract Object getDataType();
}
