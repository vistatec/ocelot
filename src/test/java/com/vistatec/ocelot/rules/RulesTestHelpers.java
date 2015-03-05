package com.vistatec.ocelot.rules;

import com.vistatec.ocelot.its.model.LanguageQualityIssue;

public class RulesTestHelpers {

    public static LanguageQualityIssue lqi(String type, int severity) {
        LanguageQualityIssue lqi = new LanguageQualityIssue();
        lqi.setType(type);
        lqi.setSeverity(severity);
        return lqi;
    }
    public static LanguageQualityIssue lqi(String type, int severity, String comment) {
        LanguageQualityIssue lqi = new LanguageQualityIssue();
        lqi.setType(type);
        lqi.setSeverity(severity);
        lqi.setComment(comment);
        return lqi;
    }
}
