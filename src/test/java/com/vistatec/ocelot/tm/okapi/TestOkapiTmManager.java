package com.vistatec.ocelot.tm.okapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.io.Files;
import com.vistatec.ocelot.config.ConfigService;
import com.vistatec.ocelot.config.ConfigTransferService;
import com.vistatec.ocelot.config.OcelotConfigService;
import com.vistatec.ocelot.config.xml.RootConfig;
import com.vistatec.ocelot.config.xml.TmManagement;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SimpleSegment;
import com.vistatec.ocelot.tm.TmPenalizer;
import com.vistatec.ocelot.tm.TmTmxWriter;

public class TestOkapiTmManager {
    private Mockery mockery;
    private ConfigService cfgService;

    private OkapiTmManager manager;
    private OkapiTmService tmService;
    private TmTmxWriter tmxWriter;
    private TmPenalizer penalizer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() throws URISyntaxException, IOException, ConfigTransferService.TransferException {
        File testTmIndices = OkapiTmTestHelpers.getTestOkapiTmDir();
        OkapiTmTestHelpers.deleteDirectory(testTmIndices);
        testTmIndices.mkdirs();

        mockery = new Mockery();
    }

    @Test
    public void testInitializeNewTm() throws IOException, ConfigTransferService.TransferException, URISyntaxException {
        final File newDataDir = setupNewForeignDataDir();
        tmxWriter = mockery.mock(TmTmxWriter.class);
        penalizer = mockery.mock(TmPenalizer.class);

        final ConfigTransferService cfgXService = mockery.mock(ConfigTransferService.class);
        final RootConfig config = new RootConfig();
        config.getTmManagement().setMaxResults(100);
        config.getTmManagement().setFuzzyThreshold(1.0);
        mockery.checking(new Expectations() {
                {
                    allowing(cfgXService).parse();
                        will(returnValue(config));
                    allowing(cfgXService).save(with(any(RootConfig.class)));
                    allowing(penalizer).applyPenalties(with(any(List.class)));
                        will(new OkapiTmTestHelpers.ReturnFirstArgument());
                }
        });
        cfgService = new OcelotConfigService(cfgXService);
        manager = new OkapiTmManager(OkapiTmTestHelpers.getTestOkapiTmDir(),
                cfgService, tmxWriter);
        tmService = new OkapiTmService(manager, penalizer, cfgService);

        manager.initializeNewTm("initTM", newDataDir);

        OcelotSegment appleOrange = new SimpleSegment.Builder()
                .source("apple orange")
                .build();
        assertEquals(2, tmService.getFuzzyTermMatches(appleOrange).size());
    }

    @Test
    public void testChangingTmDataDir() throws IOException, ConfigTransferService.TransferException, URISyntaxException {
        final File newDataDir = setupNewForeignDataDir();
        final File oldDataDir = setupOldForeignDataDir();
        tmxWriter = mockery.mock(TmTmxWriter.class);
        penalizer = mockery.mock(TmPenalizer.class);

        final String existingTmName = "exists";
        final TmManagement.TmConfig exist = setupExistingTm(existingTmName,
                oldDataDir.getAbsolutePath());

        final List<TmManagement.TmConfig> testTmConfigs = new ArrayList<>();
        testTmConfigs.add(exist);
        cfgService = mockery.mock(ConfigService.class);
        mockery.checking(new Expectations() {
                {
                    allowing(cfgService).getTms();
                        will(returnValue(testTmConfigs));

                    allowing(cfgService).getTmConfig(with("non-existent"));
                        will(returnValue(null));
                    allowing(cfgService).getTmConfig(existingTmName);
                        will(returnValue(exist));

                    oneOf(cfgService).saveTmDataDir(with(exist), with(newDataDir.getAbsolutePath()));

                    allowing(cfgService).getFuzzyThreshold();
                        will(returnValue(1.0));
                    allowing(cfgService).getMaxResults();
                        will(returnValue(100));

                    allowing(penalizer).applyPenalties(with(any(List.class)));
                        will(new OkapiTmTestHelpers.ReturnFirstArgument());
                }
        });
        manager = new OkapiTmManager(OkapiTmTestHelpers.getTestOkapiTmDir(),
                cfgService, tmxWriter);
        tmService = new OkapiTmService(manager, penalizer, cfgService);

        OcelotSegment appleOrange = new SimpleSegment.Builder()
                .source("apple orange")
                .build();

        assertEquals(0, tmService.getFuzzyTermMatches(appleOrange).size());
        manager.changeTmDataDir(existingTmName, newDataDir);
        assertEquals(2, tmService.getFuzzyTermMatches(appleOrange).size());

        assertTrue(oldDataDir.exists());
        assertTrue(newDataDir.exists());

        thrown.expect(IOException.class);
        manager.changeTmDataDir("non-existent", newDataDir);
    }

    private File setupOldForeignDataDir() throws IOException, URISyntaxException {
        File emptyTm = new File(TestOkapiTmManager.class.getResource("empty.tmx").toURI());

        File packageDir = new File(TestOkapiTmManager.class.getResource("").toURI());
        final File oldDataDir = new File(packageDir, "old");
        oldDataDir.mkdirs();

        Files.copy(emptyTm, new File(oldDataDir, "copied_empty.tmx"));
        return oldDataDir;
    }

    private File setupNewForeignDataDir() throws IOException, URISyntaxException {
        File testTm = new File(TestOkapiTmManager.class.getResource("simple_tm.tmx").toURI());

        File packageDir = new File(TestOkapiTmManager.class.getResource("").toURI());
        final File newDataDir = new File(packageDir, "new");
        newDataDir.mkdirs();

        Files.copy(testTm, new File(newDataDir, "copied_simple_tm.tmx"));
        return newDataDir;
    }

    private TmManagement.TmConfig setupExistingTm(String existingTmName, String dataDir) throws URISyntaxException{
        File existingTm = new File(OkapiTmTestHelpers.getTestOkapiTmDir(), existingTmName);
        existingTm.mkdirs();

        final TmManagement.TmConfig exist = new TmManagement.TmConfig();
        exist.setTmName(existingTmName);
        exist.setTmDataDir(dataDir);

        return exist;
    }

    @AfterClass
    public static void cleanup() throws URISyntaxException {
        OkapiTmTestHelpers.deleteDirectory(OkapiTmTestHelpers.getTestOkapiTmDir());
        File packageDir = new File(TestOkapiTmManager.class.getResource("").toURI());
        OkapiTmTestHelpers.deleteDirectory(new File(packageDir, "new"));
        OkapiTmTestHelpers.deleteDirectory(new File(packageDir, "old"));
    }
}
