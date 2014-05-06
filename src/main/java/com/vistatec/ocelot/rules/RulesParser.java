package com.vistatec.ocelot.rules;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class RulesParser {
    private static Logger LOG = Logger.getLogger(RulesParser.class);

    // ruleLabel.dataCategory = regex
    private Pattern ruleFormat = Pattern.compile("([^.]+)\\.(\\w+)\\s*=(.*)");
    // ruleLabel.flag.flagType = display
    private Pattern flagFormat = Pattern.compile("([^.]+)\\.flag\\.(\\w+)\\s*=(.*)");
    // ruleLabel.quickAdd.LQIType = value
    private Pattern quickAddFormat = Pattern.compile("([^.]+)\\.quickAdd\\.(\\w+)\\s*=(.*)");
    private Pattern quickAddHotkeyFormat = Pattern.compile("[0-9]");
    // [id-match|exact-match|fuzzy-match|mt-suggestion] = hex
    private Pattern stateQualifierFormat = Pattern.compile("(id-match|exact-match|fuzzy-match|mt-suggestion)\\s*=\\s*(.*)");

    public RuleConfiguration loadConfig(File rulesFile) {
        if (rulesFile.exists()) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(rulesFile), "UTF-8"));
                return parse(in);
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
            LOG.warn("No rules.properties file found at location:"+rulesFile.getAbsolutePath());
        }
        return new RuleConfiguration();
    }

    public RuleConfiguration parse(BufferedReader configFile) throws IOException, InstantiationException, IllegalAccessException {
        RuleConfiguration config = new RuleConfiguration();
        String line;
        while ((line = configFile.readLine()) != null) {
            Matcher rulePattern = ruleFormat.matcher(line);
            Matcher flagPattern = flagFormat.matcher(line);
            Matcher quickAddPattern = quickAddFormat.matcher(line);
            Matcher stateQualifierPattern = stateQualifierFormat.matcher(line);
            Matcher whitespace = Pattern.compile("\\s*").matcher(line);
            if (stateQualifierPattern.matches()) {
                String state = stateQualifierPattern.group(1);
                StateQualifier stateQualifier = StateQualifier.get(state);
                String hexColor = stateQualifierPattern.group(2).trim();
                if (stateQualifier != null) {
                    config.setStateQualifierColor(stateQualifier, new Color(Integer.decode(hexColor)));
                } else {
                    LOG.debug("Ignoring state-qualifier: "+state);
                }

            } else if (rulePattern.matches()) {
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
                    config.addRuleConstaint(ruleLabel, ruleMatcher);
                }
            } else if (flagPattern.matches()) {
                String ruleLabel = flagPattern.group(1);
                String flagType = flagPattern.group(2);
                String value = flagPattern.group(3).trim();

                if (flagType.equals("fill")) {
                    config.addFill(ruleLabel, new Color(Integer.decode(value)));
                } else if (flagType.equals("border")) {
                    config.addBorder(ruleLabel, new Color(Integer.decode(value)));
                } else if (flagType.equals("text")) {
                    config.addText(ruleLabel, value);
                } else {
                    LOG.error("Unrecognized flag: "+line);
                }
            } else if (quickAddPattern.matches()) {
                String ruleLabel = quickAddPattern.group(1);
                String LQIType = quickAddPattern.group(2);
                String value = quickAddPattern.group(3).trim();

                QuickAdd quickAdd = config.getQuickAddByLabel(ruleLabel);

                if (LQIType.equals(DataCategoryField.LQI_TYPE.getName())) {
                    quickAdd.getLQIData().setType(value);

                } else if (LQIType.equals(DataCategoryField.LQI_SEVERITY.getName())) {
                    quickAdd.getLQIData().setSeverity(Double.parseDouble(value));

                } else if (LQIType.equals(DataCategoryField.LQI_COMMENT.getName())) {
                    quickAdd.getLQIData().setComment(value);

                } else if (LQIType.equals("hotkey")) {
                    if (quickAddHotkeyFormat.matcher(value).matches()) {
                        config.setQuickAddHotkey(Integer.parseInt(value), quickAdd);
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
        // Validate rules and remove any that were malformed (no matchers):
        for (Rule r : new ArrayList<Rule>(config.getRules())) {
            if (r.matchers.size() == 0) {
                LOG.warn("Ignoring rule '" + r.getLabel() + 
                          "' that has no matchers.");
                config.removeRule(r);
            }
        }
        // Validate quick add rules
        for (QuickAdd qa : new ArrayList<QuickAdd>(config.getQuickAdds())) {
            if (!qa.isValid()) {
                LOG.warn("Ignoring invalid quickAdd rule '" + qa.getName());
                config.getQuickAdds().remove(qa.getName());
            }
        }
        return config;
    }
}
