package com.vistatec.ocelot.tm.okapi;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharStreams;
import com.vistatec.ocelot.config.ConfigTransferService;
import com.vistatec.ocelot.config.OcelotConfigService;
import com.vistatec.ocelot.config.XmlConfigTransferService;
import com.vistatec.ocelot.config.xml.RootConfig;
import com.vistatec.ocelot.config.xml.TmManagement.TmConfig;

public class TestOcelotTmConfigService {
    private OcelotConfigService cfgService;
    private TestCharSink testOutput;

    @Before
    public void setup() throws JAXBException, ConfigTransferService.TransferException {
        testOutput = new TestCharSink();
        ConfigTransferService cfgXService = new XmlConfigTransferService(ByteSource.empty(),
                testOutput);
        this.cfgService = new OcelotConfigService(cfgXService);
    }

    @Test
    public void testCreateTmConfig() throws URISyntaxException, IOException, ConfigTransferService.TransferException, JAXBException {
        RootConfig config = TestOkapiTmManager.setupNewForeignDataDir();
        cfgService.createNewTmConfig("config_test", true,
                config.getTmManagement().getTms().get(0).getTmDataDir());
        ConfigTransferService svc = new XmlConfigTransferService(
                ByteSource.wrap(testOutput.getString().getBytes(StandardCharsets.UTF_8)),
                new TestCharSink());
        List<TmConfig> tms = svc.parse().getTmManagement().getTms();
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
