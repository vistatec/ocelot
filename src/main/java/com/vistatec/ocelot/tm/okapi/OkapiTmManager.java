package com.vistatec.ocelot.tm.okapi;

import com.vistatec.ocelot.tm.TmManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.ConfigService;
import com.vistatec.ocelot.config.ConfigTransferService;
import com.vistatec.ocelot.config.xml.TmConfig;

import net.sf.okapi.tm.pensieve.seeker.PensieveSeeker;
import net.sf.okapi.tm.pensieve.writer.PensieveWriter;

/**
 * Use Okapi Pensieve to perform functionality expected of a {@link TmManager}.
 */
public class OkapiTmManager implements TmManager {
    private static final Logger LOG = LoggerFactory.getLogger(OkapiTmManager.class);
    private final File tmDir;
    private final ConfigService cfgService;
    private boolean newTm;

    public OkapiTmManager(File tmDir, ConfigService cfgService) throws ConfigTransferService.TransferException {
        this.tmDir = tmDir;
        this.cfgService = cfgService;
        discover();
    }

    private void discover() throws ConfigTransferService.TransferException {
        Set<String> configuredTms = new HashSet<>();
        List<TmConfig.TmEnabled> tms = this.cfgService.getTms();
        for (TmConfig.TmEnabled tm : tms) {
            configuredTms.add(tm.getTmName());
        }
        for (String tmName : this.tmDir.list()) {
            if (!configuredTms.contains(tmName)) {
                cfgService.enableTm(tmName, false);
            }
        }
    }

    @Override
    public void importTmx(String tmName, File tmx) throws IOException {
        Directory luceneDir = loadTm(tmName);

        PensieveWriter writer = new PensieveWriter(luceneDir, newTm);
        OkapiTmTmxImporter parser = new OkapiTmTmxImporter();
        parser.parse(tmx, writer);
        writer.close();
        if (newTm) {
            try {
                cfgService.enableTm(tmName, true);
            } catch (ConfigTransferService.TransferException e) {
                LOG.error("Failed to import tmx file '"+tmx.getName()+"' into key "+tmName);
                throw new IOException(e);
            }
        }
        this.newTm = false;
    }

    public Directory loadTm(String tmName) throws IOException {
        File fileTm = new File(tmDir, tmName);
        this.newTm = !fileTm.exists();
        return FSDirectory.open(fileTm);
    }

    public Iterator<TmPair> getSeekers() throws IOException {
        List<TmPair> seekers = new ArrayList<>();
        for (TmConfig.TmEnabled tm : this.cfgService.getTms()) {
            seekers.add(new TmPair(tm.getTmName(),
                    new PensieveSeeker(loadTm(tm.getTmName()))));
        }
        return seekers.iterator();
    }

    public static class TmPair {
        private final String tmOrigin;
        private final PensieveSeeker seeker;

        public TmPair(String tmOrigin, PensieveSeeker seeker) {
            this.tmOrigin = tmOrigin;
            this.seeker = seeker;
        }

        public String getTmOrigin() {
            return tmOrigin;
        }

        public PensieveSeeker getSeeker() {
            return seeker;
        }

    }
}
