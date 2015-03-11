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

import com.vistatec.ocelot.config.ConfigService;
import com.vistatec.ocelot.config.ConfigTransferService;
import com.vistatec.ocelot.config.xml.RootConfig;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SimpleSegment;
import com.vistatec.ocelot.segment.model.SimpleSegmentVariant;
import com.vistatec.ocelot.tm.TmMatch;

public class TestOkapiTmService {
    private final Mockery mockery = new Mockery();
    private final ConfigTransferService cfgXService = mockery.mock(ConfigTransferService.class);
    private OkapiTmService tmService;
    private File testTm;

    @Before
    public void before() throws URISyntaxException, IOException, ConfigTransferService.TransferException {
        File testTmIndices = getTestOkapiTmDir();
        deleteDirectory(testTmIndices);
        testTmIndices.mkdirs();

        this.testTm = new File(TestOkapiTmService.class.getResource("simple_tm.tmx").toURI());
    }

    @Test
    public void testFuzzy() throws ConfigTransferService.TransferException, URISyntaxException, IOException {
        this.tmService = new OkapiTmServiceBuilder()
                .fuzzyThreshold(1)
                .maxResults(5)
                .build();
        this.tmService.importTmx("simple_tm", testTm);

        OcelotSegment appleOrange = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source(new SimpleSegmentVariant("apple orange"))
                .build();
        List<TmMatch> appleOrangeResults = tmService.getFuzzyTermMatches(appleOrange);
        assertEquals(2, appleOrangeResults.size());
        assertEquals("apple orange pear", appleOrangeResults.get(0).getSource().getDisplayText());
        assertEquals("simple_tm", appleOrangeResults.get(0).getTmOrigin());
        assertEquals("orange apple pear", appleOrangeResults.get(1).getSource().getDisplayText());
        assertEquals("simple_tm", appleOrangeResults.get(1).getTmOrigin());

        OcelotSegment orangeApple = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source(new SimpleSegmentVariant("orange apple"))
                .build();
        List<TmMatch> orangeAppleResults = tmService.getFuzzyTermMatches(orangeApple);
        assertEquals(2, appleOrangeResults.size());
        assertEquals("orange apple pear", orangeAppleResults.get(0).getSource().getDisplayText());
        assertEquals("simple_tm", orangeAppleResults.get(0).getTmOrigin());
        assertEquals("apple orange pear", orangeAppleResults.get(1).getSource().getDisplayText());
        assertEquals("simple_tm", orangeAppleResults.get(1).getTmOrigin());

        OcelotSegment watermelon = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source(new SimpleSegmentVariant("watermelon"))
                .build();
        List<TmMatch> watermelonResults = tmService.getFuzzyTermMatches(watermelon);
        assertEquals(1, watermelonResults.size());
        assertEquals("watermelon pineapple", watermelonResults.get(0).getSource().getDisplayText());
        assertEquals("simple_tm", watermelonResults.get(0).getTmOrigin());
    }

    @Test
    public void testConcordance() throws ConfigTransferService.TransferException, URISyntaxException, IOException {
        this.tmService = new OkapiTmServiceBuilder()
                .fuzzyThreshold(1)
                .maxResults(5)
                .build();
        this.tmService.importTmx("simple_tm", testTm);

        OcelotSegment seg = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source(new SimpleSegmentVariant("apple"))
                .build();
        List<TmMatch> results = tmService.getConcordanceMatches(seg);
        assertEquals(4, results.size());
    }

    @AfterClass
    public static void cleanup() throws URISyntaxException {
        deleteDirectory(getTestOkapiTmDir());
    }

    public static File getTestOkapiTmDir() throws URISyntaxException {
        File packageDir = new File(TestOkapiTmService.class.getResource("").toURI());
        return new File(packageDir, "test");
    }

    public static void deleteDirectory(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
            dir.delete();
        }
    }

    private class OkapiTmServiceBuilder {
        private int fuzzyThreshold, maxResults;

        public OkapiTmServiceBuilder fuzzyThreshold(int threshold) {
            this.fuzzyThreshold = threshold;
            return this;
        }

        public OkapiTmServiceBuilder maxResults(int max) {
            this.maxResults = max;
            return this;
        }

        public OkapiTmService build() throws ConfigTransferService.TransferException, URISyntaxException {
            final RootConfig config = new RootConfig();
            config.getTmManagement().setFuzzyThreshold(fuzzyThreshold);
            config.getTmManagement().setMaxResults(maxResults);
            mockery.checking(new Expectations() {
                {
                    allowing(cfgXService).parse();
                        will(returnValue(config));
                    allowing(cfgXService).save(with(any(RootConfig.class)));
                }
            });
            ConfigService cfgService = new ConfigService(cfgXService);
            return new OkapiTmService(new OkapiTmManager(getTestOkapiTmDir(), cfgService),
                    cfgService);
        }
    }
}
