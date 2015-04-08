package com.vistatec.ocelot.tm.okapi;

import static org.jmock.Expectations.returnValue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SimpleSegment;
import com.vistatec.ocelot.services.SegmentService;

import net.sf.okapi.common.LocaleId;

public class TestOkapiTmxWriter {
    private final Mockery mockery = new Mockery();
    private final SegmentService segService = mockery.mock(SegmentService.class);
    private OkapiTmxWriter tmxWriter;
    private File testFile;

    @Before
    public void setup() throws IOException, URISyntaxException {
        File testDir = new File(TestOkapiTmxWriter.class.getResource("").toURI());
        testFile = new File(testDir, "okapi_tmx_writer_test.tmx");
        if (testFile.exists()) {
            throw new IOException("Unexpected file '"+testFile.getAbsolutePath()+"' found during test!");
        }
        testFile.createNewFile();
    }

    @After
    public void cleanup() {
        testFile.delete();
    }

    public void setupTmxWriter(final OcelotSegment testSeg) throws IOException {
        mockery.checking(new Expectations() {
            {
                allowing(segService).getNumSegments();
                    will(returnValue(1));
                allowing(segService).getSegment(0);
                    will(returnValue(testSeg));
            }
        });
        tmxWriter = new OkapiTmxWriter(segService);
    }

    @Test
    public void exportBasicTmx() throws IOException, URISyntaxException {
        final OcelotSegment testSeg = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source("plain text")
                .target("target plain text")
                .build();
        setupTmxWriter(testSeg);

        tmxWriter.setOpenFileLangs(new OpenFileEvent("export_tmx_test",
                LocaleId.ENGLISH.getLanguage(), LocaleId.FRENCH.getLanguage()));
        tmxWriter.exportTmx(testFile);
        assertExportedTmxFilesEqual(Files.asByteSource(testFile),
                TestOkapiTmxWriter.class.getResourceAsStream("export_tmx_test_goal.tmx"));
    }

    @Test
    public void exportTaggedTmx() throws IOException, URISyntaxException {
        SimpleSegment.Builder segBuilder = new SimpleSegment.Builder()
                .segmentNumber(1);
        segBuilder.source().code("1", "<mrk>", "<mrk id=\"1\" type=\"its:its\" translate=\"no\"")
                .text("test").code("2", "<mrk>", "<mrk id=\"2\" type=\"its:its\" translate=\"no\"");
        segBuilder.target().code("1", "<mrk>", "<mrk id=\"1\" type=\"its:its\" translate=\"no\"")
                .text("test").code("2", "<mrk>", "<mrk id=\"2\" type=\"its:its\" translate=\"no\"");
        setupTmxWriter(segBuilder.build());

        tmxWriter.setOpenFileLangs(new OpenFileEvent("export_tagged_tmx_test",
                LocaleId.ENGLISH.getLanguage(), LocaleId.FRENCH.getLanguage()));
        tmxWriter.exportTmx(testFile);
        assertExportedTmxFilesEqual(Files.asByteSource(testFile),
                TestOkapiTmxWriter.class.getResourceAsStream("export_tagged_tmx_test_goal.tmx"));
    }

    @Test
    public void exportMultipleSegmentsTmx() throws IOException, URISyntaxException {
        final OcelotSegment seg1 = new SimpleSegment.Builder()
                .segmentNumber(1)
                .source("source 1")
                .target("target 1")
                .build();

        SimpleSegment.Builder seg2Builder = new SimpleSegment.Builder()
                .segmentNumber(2);
        seg2Builder.source().code("1", "<mrk>", "<mrk id=\"1\" type=\"its:its\" translate=\"no\"")
                .text("test").code("2", "<mrk>", "<mrk id=\"2\" type=\"its:its\" translate=\"no\"");
        seg2Builder.target().code("1", "<mrk>", "<mrk id=\"1\" type=\"its:its\" translate=\"no\"")
                .text("test").code("2", "<mrk>", "<mrk id=\"2\" type=\"its:its\" translate=\"no\"");
        final OcelotSegment seg2 = seg2Builder.build();

        mockery.checking(new Expectations() {
            {
                allowing(segService).getNumSegments();
                    will(returnValue(2));
                allowing(segService).getSegment(0);
                    will(returnValue(seg1));
                allowing(segService).getSegment(1);
                    will(returnValue(seg2));
            }
        });
        tmxWriter = new OkapiTmxWriter(segService);

        tmxWriter.setOpenFileLangs(new OpenFileEvent("export_multiple_segments_tmx_test",
                LocaleId.ENGLISH.getLanguage(), LocaleId.FRENCH.getLanguage()));
        tmxWriter.exportTmx(testFile);
        assertExportedTmxFilesEqual(Files.asByteSource(testFile),
                TestOkapiTmxWriter.class.getResourceAsStream("export_multiple_segments_tmx_test_goal.tmx"));
    }

    public static void assertExportedTmxFilesEqual(ByteSource testFile, InputStream goalStream) throws IOException {
        InputStream testStream = testFile.openStream();
        try (Scanner test = new Scanner(testStream, "UTF-8").useDelimiter("\\A");
                Scanner goal = new Scanner(goalStream, "UTF-8").useDelimiter("\\A")) {
            String goalStr = goal.next().replaceFirst("creationtoolversion=\".*\" ", "");
            String testStr = test.next().replaceFirst("creationtoolversion=\".*\" ", "");
            assertEquals(goalStr, testStr);
        }
    }
}
