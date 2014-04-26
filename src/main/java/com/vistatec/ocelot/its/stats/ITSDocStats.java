package com.vistatec.ocelot.its.stats;

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;

/**
 * Collect and merge ITS metadata statistics for the document.
 */
public class ITSDocStats {
    private List<ITSStats> stats = new ArrayList<ITSStats>();

    
    public List<ITSStats> getStats() {
        return stats;
    }

    public void clear() {
        stats.clear();
    }

    void add(ITSStats stat) {
        stats.add(stat);
    }

    public void updateProvStats(Provenance prov) {
        calcProvenanceStats("person", prov.getPerson());
        calcProvenanceStats("org", prov.getOrg());
        calcProvenanceStats("tool", prov.getTool());
        calcProvenanceStats("revPerson", prov.getRevPerson());
        calcProvenanceStats("revOrg", prov.getRevOrg());
        calcProvenanceStats("revTool", prov.getRevTool());
    }

    private void calcProvenanceStats(String type, String value) {
        if (value != null) {
            boolean foundExistingStat = false;
            for (ITSStats stat : getStats()) {
                if (stat instanceof ProvenanceStats) {
                    ProvenanceStats provStat = (ProvenanceStats)stat;
                    if (provStat.getProvType().equals(type+":"+value)) {
                        provStat.setCount(provStat.getCount() + 1);
                        foundExistingStat = true;
                    }
                }
            }
            if (!foundExistingStat) {
                ProvenanceStats provStat = new ProvenanceStats();
                provStat.setProvType(type+":"+value);
                provStat.setType(type);
                provStat.setValue(value);
                add(provStat);
            }
        }
    }

    public void updateLQIStats(LanguageQualityIssue lqi) {
        boolean foundExistingStat = false;
        for (ITSStats stat : getStats()) {
            if (stat instanceof LanguageQualityIssueStats) {
                LanguageQualityIssueStats lqiStat = (LanguageQualityIssueStats) stat;
                if (lqiStat.getType().equals(lqi.getType())) {
                    lqiStat.setRange(lqi.getSeverity());
                    foundExistingStat = true;
                }
            }
        }
        if (!foundExistingStat) {
            LanguageQualityIssueStats lqiStat = new LanguageQualityIssueStats();
            lqiStat.setType(lqi.getType());
            lqiStat.setRange(lqi.getSeverity());
            add(lqiStat);
        }
    }
}
