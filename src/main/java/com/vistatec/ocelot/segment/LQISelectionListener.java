package com.vistatec.ocelot.segment;

import com.vistatec.ocelot.its.LanguageQualityIssue;

/**
 * Listens for events related to the LQI pane.
 */
public interface LQISelectionListener {
    /**
     * Signals that an LQI has been selected in the LQI pane.
     */
    public void lqiSelected(LanguageQualityIssue lqi);

    /**
     * Signals that the LQI selection has been cleared in the LQI
     * pane, or that the LQI pane itself has been hidden.
     */
    public void lqiSelectionCleared();
}
