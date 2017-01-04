package com.vistatec.ocelot.tm.okapi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.CharSink;
import com.google.common.io.CharStreams;
import com.vistatec.ocelot.config.JsonConfigTransferService;
import com.vistatec.ocelot.config.OcelotJsonConfigService;
import com.vistatec.ocelot.config.OcelotJsonConfigTransferService;
import com.vistatec.ocelot.config.TestOcelotJsonConfigTransferService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.config.json.OcelotRootConfig;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;

public class TestOcelotTmConfigService {
    private OcelotJsonConfigService cfgService;
    private StringWriter writer;

    @Before
    public void setup() throws TransferException, IOException {
        File testFile = File.createTempFile("OcelotTest", "TmConfig");
        FileWriter fileWriter = new FileWriter(testFile);
        fileWriter.write("{}");
        fileWriter.close();
        writer = new StringWriter();
        JsonConfigTransferService cfgXService = new TestOcelotJsonConfigTransferService(testFile, writer);
        this.cfgService = new OcelotJsonConfigService(cfgXService);
    }

    @Test
    public void testCreateTmConfig() throws URISyntaxException, IOException, TransferException, JAXBException {
        OcelotRootConfig config = TestOkapiTmManager.setupNewForeignDataDir();
        cfgService.createNewTmConfig("config_test", true,
                config.getTmManagement().getTms().get(0).getTmDataDir());
        System.out.println(writer.toString());
        File testFile = File.createTempFile("OcelotTest", "TmConfig");
        FileWriter fileWriter = new FileWriter(testFile);
        fileWriter.write(writer.toString());
        fileWriter.close();
        JsonConfigTransferService svc = new OcelotJsonConfigTransferService(testFile);
        List<TmConfig> tms = ((OcelotRootConfig)svc.read()).getTmManagement().getTms();
        assertEquals(1, tms.size());
        assertEquals("config_test", tms.get(0).getTmName());
        Path tmDir = Paths.get(tms.get(0).getTmDataDir());
        Path expectedDir = Paths.get(System.getProperty("user.dir"),
                                "target", "test-classes", "new", "data");
        assertEquals(expectedDir, tmDir);
    }

    @After
    public void teardown() throws URISyntaxException {
        TestOkapiTmManager.cleanup();
    }

    public class TestCharSink extends CharSink {
        private final StringBuilder output = new StringBuilder();

        @Override
        public Writer openStream() throws IOException {
            return CharStreams.asWriter(output);
        }

        public String getString() {
            return output.toString();
        }
    }
}
