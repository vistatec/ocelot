package com.vistatec.ocelot.events;

import com.vistatec.ocelot.rules.QuickAdd;

public class QuickAddEvent {
    private QuickAdd qa;
    public QuickAddEvent(QuickAdd qa) {
        this.qa = qa;
    }

    public QuickAdd getQuickAdd() {
        return qa;
    }
}
