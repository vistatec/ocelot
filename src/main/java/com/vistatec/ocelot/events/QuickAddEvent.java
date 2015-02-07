package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.rules.QuickAdd;

public class QuickAddEvent implements OcelotEvent {
    private QuickAdd qa;
    public QuickAddEvent(QuickAdd qa) {
        this.qa = qa;
    }

    public QuickAdd getQuickAdd() {
        return qa;
    }
}
