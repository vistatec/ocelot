package com.spartansoftwareinc;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.RowFilter;
import org.apache.log4j.Logger;

/**
 * Collection of RuleFilters used to determine whether to filter out a segment
 * from the SegmentView.
 */
public class RuleConfiguration extends RowFilter<SegmentTableModel, Integer> {
    private static Logger LOG = Logger.getLogger("com.spartansoftwareinc.FilterRules");
    private File rwDir, rulesFile;
    private Pattern ruleFormat, flagFormat, quickAddFormat, quickAddHotkeyFormat;

    private ArrayList<String> ruleOrdering = new ArrayList<String>();
    private HashMap<String,RuleFilter> rules;
    private HashMap<String, DataCategoryFlag> flags;
    private HashMap<String, LanguageQualityIssue> quickAdd;
    private HashMap<Integer, String> quickAddHotkeys;
    protected boolean all = true, allWithMetadata;
    
    public RuleConfiguration() {
        rwDir = new File(System.getProperty("user.home"), ".reviewersWorkbench");
        rulesFile = new File(rwDir, "rules.properties");

        rules = new HashMap<String, RuleFilter>();
        flags = new HashMap<String, DataCategoryFlag>();
        quickAdd = new HashMap<String, LanguageQualityIssue>();
        quickAddHotkeys = new HashMap<Integer, String>();

        // ruleLabel.dataCategory = regex
        ruleFormat = Pattern.compile("([^.]+)\\.(\\w+)\\s*=(.*)");
        // ruleLabel.flag.flagType = display
        flagFormat = Pattern.compile("([^.]+)\\.flag\\.(\\w+)\\s*=(.*)");
        // ruleLabel.quickAdd.LQIType = value
        quickAddFormat = Pattern.compile("([^.]+)\\.quickAdd\\.(\\w+)\\s*=(.*)");
        quickAddHotkeyFormat = Pattern.compile("[0-9]");

        loadConfig();
    }
    
    public final void loadConfig() {
        if (rulesFile.exists()) {
            BufferedReader in;
            try {
                in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(rulesFile), "UTF-8"));
                parse(in);
            } catch (UnsupportedEncodingException ex) {
                LOG.error("Encoding not supported",ex);
            } catch (FileNotFoundException ex) {
                LOG.error("Rules file not found", ex);
            } catch (IOException ex) {
                LOG.error("IO error", ex);
            } catch (InstantiationException ex) {
                LOG.error("Failed to instantiate Matcher class", ex);
            } catch (IllegalAccessException ex) {
                LOG.error("Matcher class not accessible", ex);
            }
        } else {
            LOG.error("Failed to find rules.properties file",
                    new FileNotFoundException(rulesFile.getAbsolutePath()));
        }
    }

    public void parse(BufferedReader configFile) throws IOException, InstantiationException, IllegalAccessException {
        String line;
        while ((line = configFile.readLine()) != null) {
            Matcher rulePattern = ruleFormat.matcher(line);
            Matcher flagPattern = flagFormat.matcher(line);
            Matcher quickAddPattern = quickAddFormat.matcher(line);
            Matcher whitespace = Pattern.compile("\\s*").matcher(line);
            if (rulePattern.matches()) {
                String ruleLabel = rulePattern.group(1);
                String dataCategory = rulePattern.group(2);
                String regex = rulePattern.group(3).trim();

                DataCategoryField dataCategoryField =
                        DataCategoryField.byName(dataCategory);
                if (dataCategoryField == null) {
                    LOG.error("Unrecognized datacategory: "+dataCategory
                            +", line: "+line);
                } else {
                    DataCategoryField.Matcher dcfMatcher = 
                            dataCategoryField.getMatcherClass().newInstance();
                    dcfMatcher.setPattern(regex);

                    RuleMatcher ruleMatcher =
                            new RuleMatcher(dataCategoryField, dcfMatcher);
                    addRuleConstaint(ruleLabel, ruleMatcher);
                }
            } else if (flagPattern.matches()) {
                String ruleLabel = flagPattern.group(1);
                String flagType = flagPattern.group(2);
                String value = flagPattern.group(3).trim();

                if (flagType.equals("fill")) {
                    addFill(ruleLabel, new Color(Integer.decode(value)));
                } else if (flagType.equals("border")) {
                    addBorder(ruleLabel, new Color(Integer.decode(value)));
                } else if (flagType.equals("text")) {
                    addText(ruleLabel, value);
                } else {
                    LOG.error("Unrecognized flag: "+line);
                }
            } else if (quickAddPattern.matches()) {
                String ruleLabel = quickAddPattern.group(1);
                String LQIType = quickAddPattern.group(2);
                String value = quickAddPattern.group(3).trim();

                if (LQIType.equals(DataCategoryField.LQI_TYPE.getName())) {
                    setLQIType(ruleLabel, value);

                } else if (LQIType.equals(DataCategoryField.LQI_SEVERITY.getName())) {
                    setLQISeverity(ruleLabel, Double.parseDouble(value));

                } else if (LQIType.equals(DataCategoryField.LQI_COMMENT.getName())) {
                    setLQIComment(ruleLabel, value);

                } else if (LQIType.equals("hotkey")) {
                    if (quickAddHotkeyFormat.matcher(value).matches()) {
                        quickAddHotkeys.put(Integer.parseInt(value), ruleLabel);
                    } else {
                        LOG.error("Illegal quickAdd hotkey: "+value);
                    }
                } else {
                    LOG.error("Illegal quickAdd type: "+LQIType);
                }
            } else if (!whitespace.matches()) {
                LOG.error("Unrecognized rule: "+line);
            }
        }
    }
    
    public ArrayList<String> getRuleLabels() {
        return ruleOrdering;
    }
    
    public RuleFilter getRule(String ruleLabel) {
        return rules.get(ruleLabel);
    }
    
    public void addRuleConstaint(String ruleLabel, RuleMatcher ruleMatcher) {
        if (rules.get(ruleLabel) == null) {
            RuleFilter ruleFilter = new RuleFilter();
            ruleFilter.addRuleMatcher(ruleMatcher);
            rules.put(ruleLabel, ruleFilter);
            ruleOrdering.add(ruleLabel);
        } else {
            rules.get(ruleLabel).addRuleMatcher(ruleMatcher);
        }
    }

    public DataCategoryFlag getDataCategoryFlag(String ruleLabel) {
        DataCategoryFlag ret = flags.get(ruleLabel);
        if (ret == null) {
            ret = new DataCategoryFlag();
        }
        return ret;
    }

    public void addFill(String ruleLabel, Color fill) {
        DataCategoryFlag flag = getDataCategoryFlag(ruleLabel);
        flag.setFill(fill);
        flags.put(ruleLabel, flag);
    }

    public void addBorder(String ruleLabel, Color border) {
        DataCategoryFlag flag = getDataCategoryFlag(ruleLabel);
        flag.setBorder(BorderFactory.createLineBorder(border));
        flags.put(ruleLabel, flag);
    }

    public void addText(String ruleLabel, String text) {
        DataCategoryFlag flag = getDataCategoryFlag(ruleLabel);
        flag.setText(text);
        flags.put(ruleLabel, flag);
    }

    public LanguageQualityIssue getQuickAddLQI(int hotkey) {
        return quickAdd.get(quickAddHotkeys.get(hotkey));
    }

    public LanguageQualityIssue getQuickAddLQI(String ruleLabel) {
        LanguageQualityIssue lqi = quickAdd.get(ruleLabel);
        if (lqi == null) {
            lqi = new LanguageQualityIssue();
        }
        return lqi;
    }

    public void setLQIType(String ruleLabel, String type) {
        LanguageQualityIssue lqi = getQuickAddLQI(ruleLabel);
        lqi.setType(type);
        quickAdd.put(ruleLabel, lqi);
    }

    public void setLQISeverity(String ruleLabel, double severity) {
        LanguageQualityIssue lqi = getQuickAddLQI(ruleLabel);
        lqi.setSeverity(severity);
        quickAdd.put(ruleLabel, lqi);
    }

    public void setLQIComment(String ruleLabel, String comment) {
        LanguageQualityIssue lqi = getQuickAddLQI(ruleLabel);
        lqi.setComment(comment);
        quickAdd.put(ruleLabel, lqi);
    }

    public ITSMetadata getTopDataCategory(Segment seg, int flagCol) {
        LinkedList<ITSMetadata> displayFlags = new LinkedList<ITSMetadata>();
        for (int pos = ruleOrdering.size()-1; pos >= 0; pos--) {
            RuleFilter r = rules.get(ruleOrdering.get(pos));
            LinkedList<ITSMetadata> itsMatches = r.displayMatches(seg);
            for (ITSMetadata its : itsMatches) {
                DataCategoryFlag dcf = flags.get(ruleOrdering.get(pos));
                if (dcf != null) {
                    its.setFill(dcf.getFill());
                    its.setBorder(dcf.getBorder());
                    its.setText(dcf.getText());
                }
                if (!displayFlags.contains(its)) {
                    displayFlags.add(its);
                }
            }

            if (displayFlags.size() > flagCol) {
                return displayFlags.get(flagCol);
            }
        }

        for (ITSMetadata its : seg.getAllITSMetadata()) {
            if (!displayFlags.contains(its)) {
                displayFlags.add(its);
                if (displayFlags.size() > flagCol) {
                    return displayFlags.get(flagCol);
                }
            }
        }

        return null;
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
