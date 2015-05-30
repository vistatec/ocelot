package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class OpenFileEvent implements OcelotEvent {
    private final String filename, srcLang, tgtLang;

    public OpenFileEvent(String filename, String srcLang, String tgtLang) {
        this.filename = filename;
        this.srcLang = srcLang;
        this.tgtLang = tgtLang;
    }

    public String getFilename() {
        return filename;
    }

    public String getSrcLang() {
        return srcLang;
    }

    public String getTgtLang() {
        return tgtLang;
    }

}
