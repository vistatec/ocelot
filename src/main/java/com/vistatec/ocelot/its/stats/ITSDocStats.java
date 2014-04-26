package com.vistatec.ocelot.its.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;

/**
 * Collect and merge ITS metadata statistics for the document.
 */
public class ITSDocStats {
    private List<ITSStats> stats = new ArrayList<ITSStats>();
    private Map<String, ITSStats> statsMap = new HashMap<String, ITSStats>();
    
    public List<ITSStats> getStats() {
        return stats;
    }

    public void clear() {
        stats.clear();
        statsMap.clear();
    }

    private void add(ITSStats stat) {
        stats.add(stat);
        statsMap.put(stat.getKey(), stat);
    }

    public void addLQIStats(LanguageQualityIssue lqi) {
        updateStats(new LanguageQualityIssueStats(lqi));
    }

    public void addProvenanceStats(Provenance prov) {
        calcProvenanceStats("person", prov.getPerson());
        calcProvenanceStats("org", prov.getOrg());
        calcProvenanceStats("tool", prov.getTool());
        calcProvenanceStats("revPerson", prov.getRevPerson());
        calcProvenanceStats("revOrg", prov.getRevOrg());
        calcProvenanceStats("revTool", prov.getRevTool());
    }

    private void calcProvenanceStats(String type, String value) {
        if (value != null) {
            updateStats(new ProvenanceStats(type, value));
        }
    }

    private void updateStats(ITSStats stats) {
        ITSStats oldStats = statsMap.get(stats.getKey());
        if (oldStats != null) {
            oldStats.combine(stats);
        }
        else {
            add(stats);
        }
    }
}
