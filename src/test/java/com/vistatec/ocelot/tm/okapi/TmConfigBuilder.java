package com.vistatec.ocelot.tm.okapi;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.vistatec.ocelot.config.json.OcelotRootConfig;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;

/**
 * Test helper for creating test TMs.
 */
public class TmConfigBuilder {
    private final File tmRootDir;
    private String tmName;
    private File testTmFileResource;
    private int threshold, maxResults;

    public TmConfigBuilder(File tmRootDir) {
        this.tmRootDir = tmRootDir;
    }

    public TmConfigBuilder tmName(String tmName) {
        this.tmName = tmName;
        return this;
    }

    public TmConfigBuilder testTmFileResource(File testTmx) {
        this.testTmFileResource = testTmx;
        return this;
    }

    public TmConfigBuilder fuzzyThreshold(int threshold) {
        this.threshold = threshold;
        return this;
    }

    public TmConfigBuilder maxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    public OcelotRootConfig build() throws URISyntaxException, IOException {
        final OcelotRootConfig config = new OcelotRootConfig();
        config.getTmManagement().setFuzzyThreshold(threshold);
        config.getTmManagement().setMaxResults(maxResults);

        TmData tmpTestData = new TmData(tmRootDir, tmName, testTmFileResource);

        TmConfig tmCfg = new TmConfig();
        tmCfg.setTmName(tmpTestData.tmName);
        tmCfg.setTmDataDir(tmpTestData.tmDataDir.getAbsolutePath());
        tmCfg.setEnabled(true);

        List<TmConfig> tmCfgs = new ArrayList<>();
        tmCfgs.add(tmCfg);
        config.getTmManagement().setTms(tmCfgs);

        return config;
    }

    private class TmData {

        public final String tmName;
        public final File tmDir, tmDataDir;

        private TmData(File tmRootDir, String testTmName, File testResource) throws IOException, URISyntaxException {
            this.tmName = testTmName;

            this.tmDir = new File(tmRootDir, testTmName);
            this.tmDir.mkdirs();

            this.tmDataDir = new File(this.tmDir, "data");
            this.tmDataDir.mkdirs();

            copyTestTmxToDataDir(testResource);
        }

        private void copyTestTmxToDataDir(File testResource) throws IOException {
            FileUtils.copyFile(testResource, new File(this.tmDataDir, "test.tmx"));
        }
    }
}
