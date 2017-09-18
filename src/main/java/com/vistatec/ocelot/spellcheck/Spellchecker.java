package com.vistatec.ocelot.spellcheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.Categories;
import org.languagetool.rules.RuleMatch;

import com.vistatec.ocelot.config.json.SpellingConfig;
import com.vistatec.ocelot.config.json.SpellingConfig.SpellingDictionary;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.TextAtom;

public class Spellchecker {

    private Locale locale = null;
    private Language ltLanguage = null;
    private List<CheckResult> allResults = null;

    private int currResultIndex = -1;

    public void setLocale(Locale locale) {
        this.locale = Objects.requireNonNull(locale);
        reset();
    }

    public void reset() {
        allResults = null;
        ltLanguage = null;
        currResultIndex = -1;
    }

    private JLanguageTool getLanguageTool() throws LocaleNotSupportedException {
        Language lang = Languages.getLanguageForLocale(locale);
        if (!lang.getLocale().getLanguage().equals(locale.getLanguage())) {
            throw new LocaleNotSupportedException("Spellchecking is not supported for locale " + locale);
        }
        this.ltLanguage = lang;
        JLanguageTool lt = new JLanguageTool(lang);
        lt.getCategories().keySet().forEach(lt::disableCategory);
        lt.enableRuleCategory(Categories.TYPOS.getId());
        return lt;
    }

    public List<CheckResult> spellcheck(List<OcelotSegment> segments, Supplier<Boolean> isCancelled,
            BiConsumer<Integer, Integer> onProcessed, SpellingConfig cfg)
            throws LocaleNotSupportedException {
        JLanguageTool lt = getLanguageTool();
        SpellingDictionary dict = cfg.getDictionary(getLanguage());
        List<CheckResult> results = new ArrayList<>();
        for (int segIndex = 0; segIndex < segments.size(); segIndex++) {
            if (isCancelled.get()) {
                return null;
            }
            OcelotSegment seg = segments.get(segIndex);
            SegmentVariant var = seg.getTarget();
            List<SegmentAtom> atoms = var.getAtoms();
            for (int atomIndex = 0; atomIndex < atoms.size(); atomIndex++) {
                if (isCancelled.get()) {
                    return null;
                }
                SegmentAtom atom = atoms.get(atomIndex);
                if (atom instanceof TextAtom) {
                    String text = ((TextAtom) atom).getData();
                    try {
                        List<RuleMatch> matches = lt.check(text);
                        for (RuleMatch match : matches) {
                            String word = text.substring(match.getFromPos(), match.getToPos());
                            if (!dict.getLearnedWords().contains(word)) {
                                results.add(new CheckResult(segIndex, atomIndex, match.getFromPos(), match.getToPos(),
                                        word, match.getSuggestedReplacements()));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            onProcessed.accept(segIndex, segments.size());
        }
        this.allResults = results;
        this.currResultIndex = allResults.isEmpty() ? -1 : 0;
        return results;
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

    public List<CheckResult> ignoreAll() {
        String ignoredWord = allResults.get(currResultIndex).getWord();
        List<CheckResult> ignoredResults = new ArrayList<>();
        for (Iterator<CheckResult> it = allResults.iterator(); it.hasNext();) {
            CheckResult r = it.next();
            if (r.getWord().equals(ignoredWord)) {
                it.remove();
                ignoredResults.add(r);
            }
        }
        checkIndex();
        return ignoredResults;
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

    public void replaced(String newString) {
        replacedImpl(newString, currResultIndex);
    }

    private void replacedImpl(String newString, int idx) {
        if (allResults != null && !allResults.isEmpty() && idx >= 0 && idx < allResults.size()) {
            CheckResult currRes = allResults.get(idx);
            int resIdx = idx + 1;
            int delta = newString.length() - (currRes.getStringEndIndex() - currRes.getStringStartIndex());
            while (resIdx < allResults.size()) {
                CheckResult nextRes = allResults.get(resIdx++);
                if (nextRes.getSegmentIndex() == currRes.getSegmentIndex()
                        && nextRes.getAtomIndex() == currRes.getAtomIndex()) {

                    nextRes.setStringStartIndex(nextRes.getStringStartIndex() + delta);
                    nextRes.setStringEndIndex(nextRes.getStringEndIndex() + delta);
                }
            }
            allResults.remove(idx);
        }
    }

    public void replacedAll(String newString) {
        CheckResult currRes = getCurrentResult();
        List<Integer> affectedIndices = new ArrayList<>();
        for (int i = allResults.size() - 1; i >= 0; i--) {
            if (allResults.get(i).getWord().equals(currRes.getWord())) {
                affectedIndices.add(i);
            }
        }
        for (int i : affectedIndices) {
            replacedImpl(newString, i);
        }
    }

    public String getLanguage() {
        if (ltLanguage == null) {
            try {
                getLanguageTool();
            } catch (LocaleNotSupportedException e) {
                throw new IllegalStateException(
                        "Can't get effective spellchecking language because the specified locale was not supported", e);
            }
        }
        return ltLanguage.getLocaleWithCountryAndVariant().toLanguageTag();
    }
}
