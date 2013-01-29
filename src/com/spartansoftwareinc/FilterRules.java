package com.spartansoftwareinc;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
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

    public void parseRules() throws IOException {
        Properties p = new Properties();
        p.load(RuleFilter.class.getResourceAsStream("rules.properties"));
        HashMap<String,LinkedList<RuleMatcher>> matchers =
                new HashMap<String,LinkedList<RuleMatcher>>();

        Enumeration keys = p.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String[] components = key.split("\\.");
            String ruleNumber = components[0];
            String datacategory = components[1];
            String regex = p.getProperty(key);

            DataCategoryField dataCategoryField = DataCategoryField.byName(datacategory);
            DataCategoryField.Matcher dcfMatcher =
                    dataCategoryField.equals(DataCategoryField.LQI_SEVERITY) ?
                        new Matchers.NumericMatcher() :
                        new Matchers.RegexMatcher();
            dcfMatcher.setPattern(regex);
            RuleMatcher ruleMatcher = new RuleMatcher(dataCategoryField,dcfMatcher);

            if (matchers.get(ruleNumber) == null) {
                LinkedList<RuleMatcher> rmList = new LinkedList<RuleMatcher>();
                rmList.add(ruleMatcher);
                matchers.put(ruleNumber, rmList);
            } else {
                matchers.get(ruleNumber).add(ruleMatcher);
            }
        }

        rules = new HashMap<String,RuleFilter>();
        for (String ruleNum : matchers.keySet()) {
            rules.put(ruleNum, new RuleFilter(matchers.get(ruleNum)));
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
