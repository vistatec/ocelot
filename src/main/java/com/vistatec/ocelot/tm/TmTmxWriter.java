package com.vistatec.ocelot.tm;

import java.io.File;
import java.io.IOException;

/**
 * Export currently open file as a TMX file for usage as a TM.
 */
public interface TmTmxWriter {
    public void exportTmx(File tmx) throws IOException;
}
