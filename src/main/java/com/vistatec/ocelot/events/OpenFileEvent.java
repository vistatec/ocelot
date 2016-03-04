package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class OpenFileEvent implements OcelotEvent {
    private final String filename;
    private XLIFFDocument xliff;

    public OpenFileEvent(String filename, XLIFFDocument xliff) {
        this.filename = filename;
        this.xliff = xliff;
    }

    public String getFilename() {
        return filename;
    }

    public XLIFFDocument getDocument() {
        return xliff;
    }
}
