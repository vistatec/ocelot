package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Interface to abstract out the storage of the various
 * Ocelot config files.
 */
public interface Configs {

    Reader getRulesReader() throws IOException;

    Writer getRulesWriter() throws IOException;

}
