package com.vistatec.ocelot.events;

public class OpenFileEvent {
    private String filename;
    public OpenFileEvent(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
