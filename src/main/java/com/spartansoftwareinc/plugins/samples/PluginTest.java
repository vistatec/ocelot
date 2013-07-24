package com.spartansoftwareinc.plugins.samples;

import com.spartansoftwareinc.plugins.Plugin;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import com.spartansoftwareinc.vistatec.rwb.segment.Segment;
import java.util.List;

/**
 * Sample plugin for layout testing purposes.
 */
public class PluginTest implements Plugin {

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
