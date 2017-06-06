package com.vistatec.ocelot.spellcheck;

import java.io.IOException;
import java.util.ArrayList;
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

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.TextAtom;

public class Spellchecker {

    private Locale locale = null;
    private List<CheckResult> allResults = null;

    private int currResultIndex = -1;

    public void setLocale(Locale locale) {
        this.locale = Objects.requireNonNull(locale);
        reset();
    }

    public void reset() {
        allResults = null;
        currResultIndex = -1;
    }

    private JLanguageTool getLanguageTool() throws LocaleNotSupportedException {
        Language lang = Languages.getLanguageForLocale(locale);
        if (!lang.getLocale().getLanguage().equals(locale.getLanguage())) {
            throw new LocaleNotSupportedException("Spellchecking is not supported for locale " + locale);
        }
        JLanguageTool lt = new JLanguageTool(lang);
        lt.getCategories().keySet().forEach(lt::disableCategory);
        lt.enableRuleCategory(Categories.TYPOS.getId());
        return lt;
    }

    public List<CheckResult> spellcheck(List<OcelotSegment> segments, Supplier<Boolean> isCancelled,
            BiConsumer<Integer, Integer> onProcessed) throws LocaleNotSupportedException {
        JLanguageTool lt = getLanguageTool();
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
                            results.add(new CheckResult(segIndex, atomIndex, match.getFromPos(), match.getToPos(), word,
                                    match.getSuggestedReplacements()));
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