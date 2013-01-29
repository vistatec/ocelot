package com.spartansoftwareinc;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.swing.RowFilter;

/**
 * Collection of RuleFilters used to determine whether to filter out a segment
 * from the SegmentView.
 */
public class FilterRules extends RowFilter<SegmentTableModel, Integer> {
    protected HashMap<String,RuleFilter> rules = new HashMap<String,RuleFilter>();
    protected boolean all = true, allWithMetadata;

    public FilterRules() throws IOException {
        parseRules();
    }

    private static DataCategoryField.Matcher regexMatcher(String regex) {
        DataCategoryField.Matcher m = new Matchers.RegexMatcher();
        m.setPattern(regex);
        return m;
    }

    public void parseRules() throws IOException {
        Properties p = new Properties();
        p.load(RuleFilter.class.getResourceAsStream("rules.properties"));
        rules = new HashMap<String,RuleFilter>();

        Enumeration keys = p.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String[] components = key.split("\\.");
            String ruleNumber = components[0];
            String datacategory = components[1];
            String regex = p.getProperty(key);
            Pattern matcher = Pattern.compile(regex);

            if (rules.get(ruleNumber) == null) {
                RuleFilter rule = new RuleFilter(datacategory, matcher);
                rules.put(ruleNumber, rule);
            } else {
                RuleFilter rule = rules.get(ruleNumber);
                rule.addRuleCond(datacategory,matcher);
            }
        }
    }

    @Override
    public boolean include(Entry<? extends SegmentTableModel, ? extends Integer> entry) {
        if (all) { return true; }

        SegmentTableModel model = entry.getModel();
        Segment s = model.getSegment(entry.getIdentifier());
        if (allWithMetadata) {
            return s.getAllITSMetadata().size() > 0;
        } else {
            for (RuleFilter r : rules.values()) {
                if (r.getEnabled() && r.matches(s)) {
                    return true;
                }
            }
            return false;
        }
    }
}
