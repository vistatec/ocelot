package com.vistatec.ocelot.rules;

import java.awt.Color;

public class StateQualifierRule {
    private StateQualifier stateQualifier;
    private Color color;
    private boolean enabled = false;

    /**
     * Create a new disabled StateQualifierRule for the given
     * StateQualfier.
     * @param sq state qualifier
     */
    public StateQualifierRule(StateQualifier sq) {
        this.stateQualifier = sq;
    }

    public StateQualifier getStateQualifier() {
        return stateQualifier;
    }
    public void setStateQualifier(StateQualifier stateQualifier) {
        this.stateQualifier = stateQualifier;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
