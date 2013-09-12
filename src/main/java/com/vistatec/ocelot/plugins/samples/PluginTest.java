package com.vistatec.ocelot.plugins.samples;

import com.vistatec.ocelot.plugins.ITSPlugin;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.segment.Segment;
import java.util.List;

/**
 * Sample plugin for layout testing purposes.
 */
public class PluginTest implements ITSPlugin {

    @Override
    public String getPluginName() {
        return "Really Long Plugin Name That Stretches Screen";
    }

    @Override
    public String getPluginVersion() {
        return "1.0";
    }

    @Override
    public void sendProvData(String sourceLang, String targetLang,
            Segment seg, List<Provenance> prov) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendLQIData(String sourceLang, String targetLang, Segment seg, List<LanguageQualityIssue> lqi) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
