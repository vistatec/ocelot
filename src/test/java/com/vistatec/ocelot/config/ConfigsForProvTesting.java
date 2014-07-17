package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class ConfigsForProvTesting implements Configs {
    private Reader r;
    private Writer w;
    public ConfigsForProvTesting(String r, StringWriter w) {
        this.r = r == null ? null : new StringReader(r);
        this.w = w;
    }
    @Override public Reader getProvenanceReader() throws IOException {
        return r;
    }
    @Override public Reader getOcelotReader() throws IOException {
        throw new UnsupportedOperationException();
    }
    @Override public Reader getRulesReader() throws IOException {
        throw new UnsupportedOperationException();
    }
    @Override public Writer getProvenanceWriter() throws IOException {
        return w;
    }
    @Override public Writer getOcelotWriter() throws IOException {
        throw new UnsupportedOperationException();
    }
    @Override public Writer getRulesWriter() throws IOException {
        throw new UnsupportedOperationException();
    }
}