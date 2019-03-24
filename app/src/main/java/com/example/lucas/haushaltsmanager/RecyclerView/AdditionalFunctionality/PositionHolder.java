package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import java.util.Calendar;

class PositionHolder {
    int relPos, absPos;
    Calendar datePos;
    boolean isInvalid = false;

    PositionHolder(Calendar datePos, int relPos, int absPos) {
        this.datePos = datePos;
        this.relPos = relPos;
        this.absPos = absPos;
    }

    static PositionHolder createInvalidPosition() {
        PositionHolder invalidPosition = new PositionHolder(null, -1, -1);
        invalidPosition.isInvalid = true;

        return invalidPosition;
    }
}
