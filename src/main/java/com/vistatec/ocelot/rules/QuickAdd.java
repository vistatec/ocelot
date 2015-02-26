package com.vistatec.ocelot.rules;

import java.util.Objects;

import com.vistatec.ocelot.its.LanguageQualityIssue;

/**
 * Data for a quickAdd rule.
 */
public class QuickAdd { 
    private String name;
    private LanguageQualityIssue lqiData = new LanguageQualityIssue();

    QuickAdd(String name) {
        this.name = name;
    }

    QuickAdd(String name, LanguageQualityIssue lqiData) {
        this.name = name;
        this.lqiData = lqiData;
    }

    public String getName() {
        return name;
    }

    /**
     * Create a new Language Quality Issue to add to a segment.
     * @return new LQI
     */
    public LanguageQualityIssue createLQI() {
        return new LanguageQualityIssue(lqiData);
    }

    LanguageQualityIssue getLQIData() {
        return lqiData;
    }

    /**
     * Check the validity of this rule.  A QuickAdd rule is valid if:
     * <ul>
     * <li>It has an assigned hotkey</li>
     * <li>Its LQI metadata has a type and severity set.</li> 
     * </ul>
     */
    boolean isValid() {
        if (lqiData.getType() != null &&
            lqiData.getSeverity() != 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof QuickAdd)) return false;
        QuickAdd qa = (QuickAdd)o;
        return Objects.equals(name, qa.name) &&
               Objects.equals(lqiData, qa.lqiData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lqiData);
    }
}
