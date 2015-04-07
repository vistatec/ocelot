package com.vistatec.ocelot.config.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * XML TM configuration object.
 */
public class TmManagement {
    private double fuzzyThreshold;
    private int maxResults;
    private List<TmConfig> tm;

    public TmManagement() {
        this.tm = new ArrayList<>();
    }

    @XmlElement
    public double getFuzzyThreshold() {
        return fuzzyThreshold;
    }

    public void setFuzzyThreshold(double fuzzyThreshold) {
        this.fuzzyThreshold = fuzzyThreshold;
    }

    @XmlElement
    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public List<TmConfig> getTms() {
        return tm;
    }

    public void setTm(List<TmConfig> tm) {
        this.tm = tm;
    }

    public static class TmConfig {
        private String tmName;
        private boolean enabled;
        private String tmDataDir;

        @XmlElement
        public String getTmName() {
            return tmName;
        }

        public void setTmName(String tmName) {
            this.tmName = tmName;
        }

        @XmlElement
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @XmlElement
        public String getTmDataDir() {
            return tmDataDir;
        }

        public void setTmDataDir(String tmDataDir) {
            this.tmDataDir = tmDataDir;
        }

    }
}
