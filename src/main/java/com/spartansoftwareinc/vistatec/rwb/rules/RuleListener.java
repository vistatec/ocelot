package com.spartansoftwareinc.vistatec.rwb.rules;

/**
 * Listens for changes in Rule configuration.
 */
public interface RuleListener {

    /**
     * Notify if a Rule has been enabled or disabled.
     */
    public void enabledRule(String ruleLabel, boolean enabled);

    /**
     * Notify if all segments filter option is enabled.
     */
    public void allSegments(boolean enabled);

    /**
     * Notify if all segments with metadata filter option is enabled.
     */
    public void allMetadataSegments(boolean enabled);
}
