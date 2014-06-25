package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Interface to abstract out the storage of the various
 * Ocelot config files.
 */
public interface Configs {

    Reader getProvenanceReader() throws IOException;

    Reader getOcelotReader() throws IOException;

    Reader getRulesReader() throws IOException;

    Writer getProvenanceWriter() throws IOException;

    Writer getOcelotWriter() throws IOException;

    Writer getRulesWriter() throws IOException;

}
