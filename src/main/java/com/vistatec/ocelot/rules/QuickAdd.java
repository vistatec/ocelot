package com.vistatec.ocelot.rules;

import net.sf.okapi.common.HashCodeUtil;

import com.vistatec.ocelot.its.LanguageQualityIssue;

import static com.vistatec.ocelot.ObjectUtils.safeEquals;

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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof QuickAdd)) return false;
        QuickAdd qa = (QuickAdd)o;
        return hotkey == qa.hotkey &&
               safeEquals(name, qa.name) &&
               safeEquals(lqiData, qa.lqiData);
    }

    @Override
    public int hashCode() {
        int h = HashCodeUtil.hash(HashCodeUtil.SEED, hotkey);
        h = HashCodeUtil.hash(h, name);
        h = HashCodeUtil.hash(h, lqiData);
        return h;
    }
}
