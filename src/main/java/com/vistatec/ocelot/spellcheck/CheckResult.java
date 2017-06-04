package com.vistatec.ocelot.spellcheck;

import java.util.List;

import com.vistatec.ocelot.findrep.FindResult;

public class CheckResult extends FindResult {

    private String word;
    private List<String> suggestions;

    public CheckResult(int segmentIndex, int atomIndex, int stringStartIndex, int stringEndIndex, String word,
            List<String> suggestions) {
        super(segmentIndex, atomIndex, stringStartIndex, stringEndIndex, true);
        this.word = word;
        this.suggestions = suggestions;
    }

    public String getWord() {
        return word;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }
}
