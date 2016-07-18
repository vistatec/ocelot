package com.vistatec.ocelot.rules;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesParser {
    private static Logger LOG = LoggerFactory.getLogger(RulesParser.class);

    // ruleLabel.dataCategory = regex
    private Pattern ruleFormat = Pattern.compile("([^.]+)\\.(\\w+)\\s*=(.*)");
    // ruleLabel.flag.flagType = display
    private Pattern flagFormat = Pattern.compile("([^.]+)\\.flag\\.(\\w+)\\s*=(.*)");
    // [id-match|exact-match|fuzzy-match|mt-suggestion] = hex
    private Pattern stateQualifierFormat = Pattern.compile("(id-match|exact-match|fuzzy-match|mt-suggestion)\\s*=\\s*(.*)");
    // NO LONGER SUPPORTED:
    // ruleLabel.quickAdd.LQIType = value
    private Pattern quickAddFormat = Pattern.compile("([^.]+)\\.quickAdd\\.(\\w+)\\s*=(.*)");

    public RuleConfiguration loadConfig(Reader reader) {
        try {
            if (reader != null) {
                return parse(new BufferedReader(reader));
            }
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
        return new RuleConfiguration();
    }

    public RuleConfiguration parse(BufferedReader configFile) throws IOException, InstantiationException, IllegalAccessException {
        RuleConfiguration config = new RuleConfiguration();
        String line;
        boolean hasDeprecatedQuickAddPatterns = false;
        while ((line = configFile.readLine()) != null) {
            Matcher rulePattern = ruleFormat.matcher(line);
            Matcher flagPattern = flagFormat.matcher(line);
            Matcher deprecatedQuickAddPattern = quickAddFormat.matcher(line);
            Matcher stateQualifierPattern = stateQualifierFormat.matcher(line);
            Matcher whitespace = Pattern.compile("\\s*").matcher(line);
            if (stateQualifierPattern.matches()) {
                String state = stateQualifierPattern.group(1);
                StateQualifier stateQualifier = StateQualifier.get(state);
                String hexColor = stateQualifierPattern.group(2).trim();
                if (stateQualifier != null) {
                    config.setStateQualifierColor(stateQualifier, new Color(Integer.decode(hexColor)));
                } else {
                    LOG.debug("Ignoring state-qualifier: {}", state);
                }

            } else if (rulePattern.matches()) {
                String ruleLabel = rulePattern.group(1);
                String dataCategory = rulePattern.group(2);
                String regex = rulePattern.group(3).trim();

                DataCategoryField dataCategoryField =
                        DataCategoryField.byName(dataCategory);
                if (dataCategoryField == null) {
                    LOG.error("Unrecognized data category: {}, line: {}", dataCategory, line);
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
                    LOG.error("Unrecognized flag: {}", line);
                }
            } else if (deprecatedQuickAddPattern.matches()) {
                hasDeprecatedQuickAddPatterns = true;
            } else if (!whitespace.matches()) {
                LOG.error("Unrecognized rule: "+line);
            }
        }
        // Validate rules and remove any that were malformed (no matchers):
        for (Rule r : new ArrayList<Rule>(config.getRules())) {
            if (r.matchers.size() == 0) {
                LOG.warn("Ignoring rule '{}' that has no matches.", r.getLabel());
                config.removeRule(r);
            }
        }
        if (hasDeprecatedQuickAddPatterns) {
            LOG.warn("Skipping deprecated quickadd rules.");
        }
        return config;
    }
}
