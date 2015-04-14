package com.vistatec.ocelot.tm.okapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharStreams;
import com.vistatec.ocelot.config.ConfigTransferService;
import com.vistatec.ocelot.config.OcelotConfigService;
import com.vistatec.ocelot.config.XmlConfigTransferService;
import com.vistatec.ocelot.config.xml.RootConfig;

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
    public void testCreateTmConfig() throws URISyntaxException, IOException, ConfigTransferService.TransferException {
        RootConfig config = TestOkapiTmManager.setupNewForeignDataDir();
        cfgService.createNewTmConfig("config_test", true,
                config.getTmManagement().getTms().get(0).getTmDataDir());
        String goal = readFile("create_config_goal.xml");
        Assert.assertEquals(goal, testOutput.getString());
    }

    @After
    public void teardown() throws URISyntaxException {
        TestOkapiTmManager.cleanup();
    }

    public String readFile(String file) {
        InputStream goalStream = TestOcelotTmConfigService.class.getResourceAsStream(file);
        Scanner goal = new Scanner(goalStream, "UTF-8").useDelimiter("\\A");
        return goal.next();
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
