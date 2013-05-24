package com.spartansoftwareinc.plugins;

import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import java.util.List;

import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import com.spartansoftwareinc.vistatec.rwb.segment.Segment;

public interface Plugin {

	/**
	 * Get the name of this plugin, for display in the Workbench UI. 
	 * @return plugin name
	 */
	public String getPluginName();
	
	/**
	 * Get the version of this plugin, for display in the Workbench UI. 
	 * @return plugin version
	 */
	public String getPluginVersion();

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
