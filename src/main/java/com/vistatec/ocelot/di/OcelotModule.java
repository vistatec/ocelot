package com.vistatec.ocelot.di;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.vistatec.ocelot.OcelotApp;
import com.vistatec.ocelot.config.ConfigService;
import com.vistatec.ocelot.config.OcelotConfigService;
import com.vistatec.ocelot.config.ConfigTransferService;
import com.vistatec.ocelot.config.Configs;
import com.vistatec.ocelot.config.DirectoryBasedConfigs;
import com.vistatec.ocelot.config.XmlConfigTransferService;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.stats.model.ITSDocStats;
import com.vistatec.ocelot.plugins.PluginManager;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RulesParser;
import com.vistatec.ocelot.services.ITSDocStatsService;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.ProvenanceService;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.services.SegmentServiceImpl;
import com.vistatec.ocelot.services.XliffService;
import com.vistatec.ocelot.tm.TmManager;
import com.vistatec.ocelot.tm.TmService;
import com.vistatec.ocelot.tm.okapi.OkapiTmManager;
import com.vistatec.ocelot.tm.okapi.OkapiTmService;
import com.vistatec.ocelot.tm.okapi.OkapiTmxWriter;

/**
 * Main Ocelot object dependency context module.
 */
public class OcelotModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(OcelotModule.class);

    @Override
    protected void configure() {
        OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
        bind(OcelotEventQueue.class).toInstance(eventQueue);

        ITSDocStats docStats = new ITSDocStats();
        bind(ITSDocStats.class).toInstance(docStats);

        bind(OcelotApp.class).in(Scopes.SINGLETON);

        ConfigService cfgService = null;
        RuleConfiguration ruleConfig = null;
        PluginManager pluginManager = null;
        TmManager tmManager = null;
        try {
            File ocelotDir = new File(System.getProperty("user.home"), ".ocelot");
            ocelotDir.mkdirs();

            Configs configs = new DirectoryBasedConfigs(ocelotDir);

            cfgService = setupConfigService(ocelotDir);
            ruleConfig = new RulesParser().loadConfig(configs.getRulesReader());

            pluginManager = new PluginManager(cfgService, new File(ocelotDir, "plugins"));
            pluginManager.discover();
            eventQueue.registerListener(pluginManager);

            SegmentService segmentService = new SegmentServiceImpl(eventQueue);
            bind(SegmentService.class).toInstance(segmentService);
            eventQueue.registerListener(segmentService);

            File tm = new File(ocelotDir, "tm");
            tm.mkdirs();
            OkapiTmxWriter tmxWriter = new OkapiTmxWriter(segmentService);
            eventQueue.registerListener(tmxWriter);
            tmManager = new OkapiTmManager(tm, cfgService, tmxWriter);
            bind(OkapiTmManager.class).toInstance((OkapiTmManager) tmManager);

        } catch (IOException | JAXBException | ConfigTransferService.TransferException ex) {
            LOG.error("Failed to initialize configuration", ex);
            System.exit(1);
        }

        bind(RuleConfiguration.class).toInstance(ruleConfig);
        bind(PluginManager.class).toInstance(pluginManager);
        bind(TmManager.class).toInstance(tmManager);

        bindServices(eventQueue, cfgService, docStats);
    }

    private void bindServices(OcelotEventQueue eventQueue, ConfigService cfgService,
            ITSDocStats docStats) {
        bind(ConfigService.class).toInstance(cfgService);

        ProvenanceService provService = new ProvenanceService(eventQueue, cfgService);
        bind(ProvenanceService.class).toInstance(provService);
        eventQueue.registerListener(provService);

        ITSDocStatsService docStatsService = new ITSDocStatsService(docStats, eventQueue);
        bind(ITSDocStatsService.class).toInstance(docStatsService);
        eventQueue.registerListener(docStatsService);

        XliffService xliffService = new OkapiXliffService(cfgService, eventQueue);
        bind(XliffService.class).toInstance(xliffService);
        eventQueue.registerListener(xliffService);

        bind(TmService.class).to(OkapiTmService.class);
    }

    private OcelotConfigService setupConfigService(File ocelotDir) throws ConfigTransferService.TransferException, JAXBException {
        File configFile = new File(ocelotDir, "ocelot_cfg.xml");
        ByteSource configSource = !configFile.exists() ?
                ByteSource.empty() :
                Files.asByteSource(configFile);

        CharSink configSink = Files.asCharSink(configFile,
                Charset.forName("UTF-8"));
        return new OcelotConfigService(new XmlConfigTransferService(
                configSource, configSink));
    }
}
