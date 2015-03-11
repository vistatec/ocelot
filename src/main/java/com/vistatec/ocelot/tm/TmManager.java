package com.vistatec.ocelot.tm;

import java.io.File;
import java.io.IOException;

/**
 * Manages importing TMs in Ocelot.
 */
public interface TmManager {
    /**
     * Parse the TMX file and associate the segments with the given {@code tmName}.
     * @param tmName
     * @param tmx
     * @throws IOException
     */
    public void importTmx(String tmName, File tmx) throws IOException;
}
