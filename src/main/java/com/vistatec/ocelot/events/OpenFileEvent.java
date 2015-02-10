package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class OpenFileEvent implements OcelotEvent {
    private String filename;
    public OpenFileEvent(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
