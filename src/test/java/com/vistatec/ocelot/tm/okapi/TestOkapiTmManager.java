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

import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.config.json.OcelotRootConfig;
import com.vistatec.ocelot.config.json.TmManagement;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SimpleSegmentVariant;
import com.vistatec.ocelot.tm.TmPenalizer;
import com.vistatec.ocelot.tm.TmTmxWriter;

public class TestOkapiTmManager {
    private Mockery mockery;
    private JsonConfigService cfgService;

    private OkapiTmManager manager;
    private OkapiTmService tmService;
    private TmTmxWriter tmxWriter;
    private TmPenalizer penalizer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() throws URISyntaxException, IOException, TransferException {
        File testTmIndices = OkapiTmTestHelpers.getTestOkapiTmDir();
        OkapiTmTestHelpers.deleteDirectory(testTmIndices);
        testTmIndices.mkdirs();

        mockery = new Mockery();
    }

    @Test
    public void testInitializeNewTm() throws IOException, TransferException, URISyntaxException {
        tmxWriter = mockery.mock(TmTmxWriter.class);
        penalizer = mockery.mock(TmPenalizer.class);
        cfgService = mockery.mock(JsonConfigService.class);

        final OcelotRootConfig rootConfig = setupNewForeignDataDir();
        final TmManagement.TmConfig tmConfig = rootConfig.getTmManagement().getTms().get(0);
        final File tmDataDir = new File(tmConfig.getTmDataDir());
        assertTrue(tmDataDir.exists());

        final String INIT_TM_NAME = "initTM";
        final TmManagement.TmConfig initNewConfig = new TmManagement.TmConfig();
        initNewConfig.setTmName(INIT_TM_NAME);
        initNewConfig.setTmDataDir(tmConfig.getTmDataDir());
        initNewConfig.setEnabled(true);
        final List<TmManagement.TmConfig> configTms = new ArrayList();
        configTms.add(initNewConfig);

        mockery.checking(new Expectations() {
                {
                    allowing(cfgService).getTms();
                        will(onConsecutiveCalls(
                                returnValue(new ArrayList()),
                                returnValue(configTms)));
                    allowing(cfgService).saveConfig();

                    allowing(cfgService).getTmConfig(with(INIT_TM_NAME));
                        will(onConsecutiveCalls(
                                returnValue(null),
                                returnValue(initNewConfig),
                                returnValue(initNewConfig),
                                returnValue(initNewConfig)));
                    allowing(cfgService).getTmConfig(with(tmConfig.getTmName()));
                        will(returnValue(null));

                    allowing(cfgService).createNewTmConfig(with(INIT_TM_NAME),
                            with(true), with(tmConfig.getTmDataDir()));

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

        manager.initializeNewTm(INIT_TM_NAME, tmDataDir);

        List<SegmentAtom> appleOrange = new SimpleSegmentVariant("apple orange").getAtoms();
        assertEquals(2, tmService.getFuzzyTermMatches(appleOrange).size());
    }

    @Test
    public void testChangingTmDataDir() throws IOException, TransferException, URISyntaxException {
        tmxWriter = mockery.mock(TmTmxWriter.class);
        penalizer = mockery.mock(TmPenalizer.class);

        final OcelotRootConfig oldRootConfig = setupOldForeignDataDir();
        final TmConfig oldTmConfig = oldRootConfig.getTmManagement().getTms().get(0);
        final File oldDataDir = new File(oldTmConfig.getTmDataDir());
        assertTrue(oldDataDir.exists());

        final OcelotRootConfig newRootConfig = setupNewForeignDataDir();
        final TmConfig newTmConfig = newRootConfig.getTmManagement().getTms().get(0);
        final File newDataDir = new File(newTmConfig.getTmDataDir());
        assertTrue(newDataDir.exists());

        cfgService = mockery.mock(JsonConfigService.class);
        mockery.checking(new Expectations() {
                {
                    allowing(cfgService).getTms();
                        will(onConsecutiveCalls(
                                returnValue(new ArrayList()),
                                returnValue(oldRootConfig.getTmManagement().getTms()),
                                returnValue(oldRootConfig.getTmManagement().getTms())));
                    allowing(cfgService).saveConfig();

                    allowing(cfgService).getTmConfig(oldTmConfig.getTmName());
                        will(onConsecutiveCalls(
                                returnValue(null),
                                returnValue(oldTmConfig),
                                returnValue(oldTmConfig),
                                returnValue(oldTmConfig),
                                returnValue(oldTmConfig),
                                returnValue(oldTmConfig),
                                returnValue(oldTmConfig),
                                returnValue(oldTmConfig)));

                    allowing(cfgService).createNewTmConfig(with(oldTmConfig.getTmName()),
                            with(true), with(oldTmConfig.getTmDataDir()));

                    allowing(cfgService).getTmConfig(with("non-existent"));
                        will(returnValue(null));

                    oneOf(cfgService).saveTmDataDir(with(oldTmConfig), with(newDataDir.getAbsolutePath()));

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

        // Index existing TM
        manager.initializeNewTm(oldTmConfig.getTmName(), oldDataDir);

        List<SegmentAtom> appleOrange = new SimpleSegmentVariant("apple orange").getAtoms();
        assertEquals(0, tmService.getFuzzyTermMatches(appleOrange).size());

        // Change TM to new data directory
        manager.changeTmDataDir(oldTmConfig.getTmName(), newDataDir);
        assertEquals(2, tmService.getFuzzyTermMatches(appleOrange).size());

        assertTrue(oldDataDir.exists());
        assertTrue(newDataDir.exists());

        thrown.expect(IOException.class);
        manager.changeTmDataDir("non-existent", newDataDir);
    }

    static OcelotRootConfig setupOldForeignDataDir() throws IOException, URISyntaxException {
        File packageDir = new File(TestOkapiTmManager.class.getResource("").toURI());

        final String existingTmName = "exists";
        File emptyTmx = new File(TestOkapiTmManager.class.getResource("empty.tmx").toURI());

        return new TmConfigBuilder(packageDir)
                .tmName(existingTmName)
                .testTmFileResource(emptyTmx)
                .build();
    }

    static OcelotRootConfig setupNewForeignDataDir() throws IOException, URISyntaxException {
        File packageDir = new File(TestOkapiTmManager.class.getResource("/").toURI());

        final String newTmName = "new";
        File newTmx = new File(TestOkapiTmManager.class.getResource("simple_tm.tmx").toURI());

        return new TmConfigBuilder(packageDir)
                .tmName(newTmName)
                .testTmFileResource(newTmx)
                .fuzzyThreshold(1)
                .maxResults(100)
                .build();
    }

    @AfterClass
    public static void cleanup() throws URISyntaxException {
        OkapiTmTestHelpers.deleteDirectory(OkapiTmTestHelpers.getTestOkapiTmDir());
        File packageDir = new File(TestOkapiTmManager.class.getResource("").toURI());
        OkapiTmTestHelpers.deleteDirectory(new File(packageDir, "new"));
        OkapiTmTestHelpers.deleteDirectory(new File(packageDir, "old"));
    }
}
