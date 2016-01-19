package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

import net.sf.okapi.common.LocaleId;

public class OpenFileEvent implements OcelotEvent {
    private final LocaleId srcLang, tgtLang;
    private final String filename;

    public OpenFileEvent(String filename, LocaleId srcLang, LocaleId tgtLang) {
        this.filename = filename;
        this.srcLang = srcLang;
        this.tgtLang = tgtLang;
    }

    public String getFilename() {
        return filename;
    }

    public LocaleId getSrcLang() {
        return srcLang;
    }

    public LocaleId getTgtLang() {
        return tgtLang;
    }
}
