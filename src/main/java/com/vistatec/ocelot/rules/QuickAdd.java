package com.vistatec.ocelot.rules;

import net.sf.okapi.common.HashCodeUtil;

import com.vistatec.ocelot.its.LanguageQualityIssue;

import static com.vistatec.ocelot.ObjectUtils.safeEquals;

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

    public LanguageQualityIssue getLQIData() {
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
        return safeEquals(name, qa.name) &&
               safeEquals(lqiData, qa.lqiData);
    }

    @Override
    public int hashCode() {
        int h = HashCodeUtil.hash(HashCodeUtil.SEED, name);
        h = HashCodeUtil.hash(h, lqiData);
        return h;
    }
}
