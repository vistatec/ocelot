package com.vistatec.ocelot.tm.okapi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.vistatec.ocelot.config.ConfigTransferService;
import com.vistatec.ocelot.config.OcelotJsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.config.json.OcelotRootConfig;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SimpleSegmentVariant;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.TmPenalizer;
import com.vistatec.ocelot.tm.TmTmxWriter;

public class TestOkapiTmService {
    private final Mockery mockery = new Mockery();
    private final ConfigTransferService cfgXService = mockery.mock(ConfigTransferService.class);

    private OkapiTmService tmService;
    private File testTm;

    @Before
    public void before() throws URISyntaxException, IOException, TransferException {
        File testTmIndices = OkapiTmTestHelpers.getTestOkapiTmDir();
        OkapiTmTestHelpers.deleteDirectory(testTmIndices);
        testTmIndices.mkdirs();

        this.testTm = new File(TestOkapiTmService.class.getResource("simple_tm.tmx").toURI());
    }

    @Test
    public void testFuzzy() throws TransferException, URISyntaxException, IOException {
        final OcelotRootConfig config = new TmConfigBuilder(OkapiTmTestHelpers.getTestOkapiTmDir())
                    .tmName("simple_tm")
                    .testTmFileResource(testTm)
                    .fuzzyThreshold(1)
                    .maxResults(5)
                    .build();
        this.tmService = new OkapiTmServiceBuilder(config).build();

        List<SegmentAtom> appleOrange = new SimpleSegmentVariant("apple orange").getAtoms();
        List<TmMatch> appleOrangeResults = tmService.getFuzzyTermMatches(appleOrange);
        assertEquals(2, appleOrangeResults.size());
        assertEquals("apple orange pear", appleOrangeResults.get(0).getSource().getDisplayText());
        assertEquals("simple_tm", appleOrangeResults.get(0).getTmOrigin());
        assertEquals("orange apple pear", appleOrangeResults.get(1).getSource().getDisplayText());
        assertEquals("simple_tm", appleOrangeResults.get(1).getTmOrigin());

        List<SegmentAtom> orangeApple = new SimpleSegmentVariant("orange apple").getAtoms();
        List<TmMatch> orangeAppleResults = tmService.getFuzzyTermMatches(orangeApple);
        assertEquals(2, appleOrangeResults.size());
        assertEquals("orange apple pear", orangeAppleResults.get(0).getSource().getDisplayText());
        assertEquals("simple_tm", orangeAppleResults.get(0).getTmOrigin());
        assertEquals("apple orange pear", orangeAppleResults.get(1).getSource().getDisplayText());
        assertEquals("simple_tm", orangeAppleResults.get(1).getTmOrigin());

        List<SegmentAtom> watermelon = new SimpleSegmentVariant("watermelon").getAtoms();
        List<TmMatch> watermelonResults = tmService.getFuzzyTermMatches(watermelon);
        assertEquals(1, watermelonResults.size());
        assertEquals("watermelon pineapple", watermelonResults.get(0).getSource().getDisplayText());
        assertEquals("simple_tm", watermelonResults.get(0).getTmOrigin());
    }

    @Test
    public void testConcordance() throws TransferException, URISyntaxException, IOException {
        final OcelotRootConfig config = new TmConfigBuilder(OkapiTmTestHelpers.getTestOkapiTmDir())
                    .tmName("simple_tm")
                    .testTmFileResource(testTm)
                    .fuzzyThreshold(1)
                    .maxResults(5)
                    .build();
        this.tmService = new OkapiTmServiceBuilder(config).build();

        List<SegmentAtom> apple = new SimpleSegmentVariant("apple").getAtoms();
        List<TmMatch> results = tmService.getConcordanceMatches(apple);
        assertEquals(4, results.size());
    }

    @Test
    public void testSearchOnlyEnabled() throws TransferException, URISyntaxException, IOException {
        final OcelotRootConfig config = new TmConfigBuilder(OkapiTmTestHelpers.getTestOkapiTmDir())
                    .tmName("simple_tm")
                    .testTmFileResource(testTm)
                    .fuzzyThreshold(1)
                    .maxResults(5)
                    .build();
        this.tmService = new OkapiTmServiceBuilder(config).build();

        List<SegmentAtom> apple = new SimpleSegmentVariant("apple").getAtoms();
        List<TmMatch> results = tmService.getConcordanceMatches(apple);
        assertEquals(4, results.size());

        config.getTmManagement().getTms().get(0).setEnabled(false);
        results = tmService.getConcordanceMatches(apple);
        assertEquals(0, results.size());
    }

    @AfterClass
    public static void cleanup() throws URISyntaxException {
        OkapiTmTestHelpers.deleteDirectory(OkapiTmTestHelpers.getTestOkapiTmDir());
    }

    private class OkapiTmServiceBuilder {
        private final OcelotRootConfig config;

        public OkapiTmServiceBuilder(OcelotRootConfig config) {
            this.config = config;
        }

        public OkapiTmService build() throws TransferException, URISyntaxException, IOException {
            TmTmxWriter tmxWriter = mockery.mock(TmTmxWriter.class);
            final TmPenalizer penalizer = mockery.mock(TmPenalizer.class);

            mockery.checking(new Expectations() {
                {
                    allowing(cfgXService).read();
                        will(returnValue(config));
                    allowing(cfgXService).save(with(any(OcelotRootConfig.class)));
                    allowing(penalizer).applyPenalties(with(any(List.class)));
                        will(new OkapiTmTestHelpers.ReturnFirstArgument());
                }
            });

            OcelotJsonConfigService cfgService = new OcelotJsonConfigService(cfgXService);
            OkapiTmManager tmManager = new OkapiTmManager(OkapiTmTestHelpers.getTestOkapiTmDir(), cfgService, tmxWriter);
            return new OkapiTmService(tmManager, penalizer, cfgService);
        }
    }
}
