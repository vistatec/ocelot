package com.vistatec.ocelot.rules;

import com.vistatec.ocelot.its.LanguageQualityIssue;

/**
 * Data for a quickAdd rule.
 */
public class QuickAdd {
    public static final int INVALID_HOTKEY = -1;
    
    private String name;
    private LanguageQualityIssue lqiData = new LanguageQualityIssue();
    private int hotkey = INVALID_HOTKEY;

    QuickAdd(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public LanguageQualityIssue getLQIData() {
        return lqiData;
    }

    public int getHotkey() {
        return hotkey;
    }

    void setHotkey(int hotkey) {
        this.hotkey = hotkey;
    }

    /**
     * Check the validity of this rule.  A QuickAdd rule is valid if:
     * <ul>
     * <li>It has an assigned hotkey</li>
     * <li>Its LQI metadata has a type and severity set.</li> 
     * </ul>
     */
    boolean isValid() {
        if (hotkey == INVALID_HOTKEY) return false;
        if (lqiData.getType() != null &&
            lqiData.getSeverity() != 0) {
            return true;
        }
        return false;
    }

    // TODO: equals etc
}
