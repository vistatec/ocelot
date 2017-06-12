package com.vistatec.ocelot.config.json;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpellingConfig {

    private Map<String, SpellingDictionary> dictionaries;

    public SpellingConfig() {
        this.dictionaries = new HashMap<>();
    }

    public Map<String, SpellingDictionary> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(Map<String, SpellingDictionary> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public void setDictionary(String language, SpellingDictionary dictionary) {
        this.dictionaries.put(language, dictionary);
    }

    public SpellingDictionary getDictionary(String language) {
        SpellingDictionary dictionary = dictionaries.get(language);
        return dictionary == null ? new SpellingDictionary() : dictionary;
    }

    public static class SpellingDictionary {
        private Set<String> learnedWords;

        public SpellingDictionary() {
            this.learnedWords = new HashSet<>();
        }

        public void setLearnedWords(List<String> words) {
            this.learnedWords = new HashSet<>(words);
        }

        public Set<String> getLearnedWords() {
            return learnedWords;
        }
    }
}
