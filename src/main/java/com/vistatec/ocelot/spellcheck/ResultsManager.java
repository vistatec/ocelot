package com.vistatec.ocelot.spellcheck;

import java.util.List;

public class ResultsManager {

    private final List<CheckResult> allResults;

    private int currResultIndex;

    public ResultsManager(List<CheckResult> results) {
        this.currResultIndex = 0;
        this.allResults = results;
    }

    public boolean hasResults() {
        return allResults != null && !allResults.isEmpty() && currResultIndex >= 0
                && currResultIndex < allResults.size();
    }

    public CheckResult getCurrentResult() {
        return hasResults() ? allResults.get(currResultIndex) : null;
    }

    public void ignoreOne() {
        allResults.remove(currResultIndex);
        checkIndex();
    }

    private void checkIndex() {
        currResultIndex = Math.max(0, Math.min(currResultIndex, allResults.size() - 1));
    }

    public void ignoreAll() {
        String ignored = allResults.get(currResultIndex).getWord();
        allResults.removeIf(r -> r.getWord().equals(ignored));
        checkIndex();
    }

    public List<CheckResult> getAllResults() {
        return allResults;
    }

    public int getCurrentResIndex() {
        return currResultIndex;
    }

    public int getRemainingResults() {
        return allResults.size() - currResultIndex;
    }
}
