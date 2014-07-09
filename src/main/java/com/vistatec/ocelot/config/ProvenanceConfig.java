package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that manages user-configured provenance information.
 */
public class ProvenanceConfig {
    private Logger LOG = LoggerFactory.getLogger(ProvenanceConfig.class);

    private Configs configs;
    private Properties p;

    public ProvenanceConfig(Configs configs) {
        this.configs = configs;
        reload();
    }

    public void reload() {
        p = new Properties();
        try {
            Reader r = configs.getProvenanceReader();
            if (r != null) {
                p.load(r);
                r.close();
            }
        }
        catch (IOException e) {
            LOG.warn("Failed to load user provenance information", e);
        }
    }

    public boolean isEmpty() {
        return !(p.getProperty("revPerson") != null ||
                 p.getProperty("revOrganization") != null||
                 p.getProperty("externalReference") != null);
    }

    public UserProvenance getUserProvenance() {
        return new UserProvenance(p.getProperty("revPerson"),
                                  p.getProperty("revOrganization"),
                                  p.getProperty("externalReference"));
    }

    public void save(UserProvenance prov) throws IOException {
        p.setProperty("revPerson", prov.getRevPerson());
        p.setProperty("revOrganization", prov.getRevOrg());
        p.setProperty("externalReference", prov.getProvRef());
        Writer w = configs.getProvenanceWriter();
        p.store(w, null);
        w.close();
    }
}
