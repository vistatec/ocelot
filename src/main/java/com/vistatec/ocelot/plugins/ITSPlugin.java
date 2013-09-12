package com.vistatec.ocelot.plugins;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.segment.Segment;
import java.util.List;

/**
 * ITS Plugins are sent all of the ITS metadata attached to segments in an open
 * file on export.
 */
public interface ITSPlugin extends Plugin {
    /**
     * Send this plugin the ITS Language Quality Issue data for a segment from
     * the workbench.
     */
    public void sendLQIData(String sourceLang, String targetLang,
            Segment seg, List<LanguageQualityIssue> lqi);

    /**
     * Send this plugin the ITS Provenance data for a segment from the
     * workbench.
     */
    public void sendProvData(String sourceLang, String targetLang,
            Segment seg, List<Provenance> prov);
}
