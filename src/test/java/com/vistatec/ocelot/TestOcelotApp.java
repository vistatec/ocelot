package com.vistatec.ocelot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.stream.XMLStreamException;

public class TestOcelotApp {
    private OcelotApp ocelotApp;

    public void before() {
    }

    public void testDefaultFlags() {
        assertFalse(ocelotApp.isFileDirty());
        assertFalse(ocelotApp.hasOpenFile());
    }

    public void testOpenSimpleXliff() throws IOException, URISyntaxException, XMLStreamException {
        ocelotApp.openFile(loadResource("/test.xlf"), false);
        assertEquals("en", ocelotApp.getFileSourceLang());
        assertEquals("fr", ocelotApp.getFileTargetLang());
        assertTrue(ocelotApp.hasOpenFile());
        assertFalse(ocelotApp.isFileDirty());
    }

    private File loadResource(String resource) throws URISyntaxException {
        return new File(getClass().getResource(resource).toURI());
    }
}
