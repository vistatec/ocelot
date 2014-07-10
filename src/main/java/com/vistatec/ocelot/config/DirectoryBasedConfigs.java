package com.vistatec.ocelot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class DirectoryBasedConfigs implements Configs {

    public static final String PROVENANCE_CONFIG = "provenance.properties";
    public static final String OCELOT_CONFIG = "ocelot_cfg.xml";
    public static final String RULES_CONFIG = "rules.properties";

    private File dir;

    public DirectoryBasedConfigs(File dir) {
        this.dir = dir;
    }

    protected Reader getReader(String filename) throws IOException {
        File f = new File(dir, filename);
        if (!f.exists()) {
            return null;
        }
        return new InputStreamReader(new FileInputStream(f), "UTF-8");
    }

    protected Writer getWriter(String filename) throws IOException {
        File f = new File(dir, filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        return new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
    }
    
    @Override
    public Reader getProvenanceReader() throws IOException {
        return getReader(PROVENANCE_CONFIG);
    }

    @Override
    public Reader getOcelotReader() throws IOException {
        return getReader(OCELOT_CONFIG);
    }

    @Override
    public Reader getRulesReader() throws IOException {
        return getReader(RULES_CONFIG);
    }

    @Override
    public Writer getProvenanceWriter() throws IOException {
        return getWriter(PROVENANCE_CONFIG);
    }

    @Override
    public Writer getOcelotWriter() throws IOException {
        return getWriter(OCELOT_CONFIG);
    }

    @Override
    public Writer getRulesWriter() throws IOException {
        return getWriter(RULES_CONFIG);
    }

}
