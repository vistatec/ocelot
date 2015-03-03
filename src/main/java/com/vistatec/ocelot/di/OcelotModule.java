package com.vistatec.ocelot.di;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.vistatec.ocelot.OcelotApp;
import com.vistatec.ocelot.config.AppConfig;
import com.vistatec.ocelot.config.Configs;
import com.vistatec.ocelot.config.DirectoryBasedConfigs;
import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.stats.ITSDocStats;
import com.vistatec.ocelot.plugins.PluginManager;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RulesParser;
import com.vistatec.ocelot.services.ITSDocStatsService;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.ProvenanceService;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.services.SegmentServiceImpl;
import com.vistatec.ocelot.services.XliffService;

/**
 * Main Ocelot object dependency context module.
 */
public class OcelotModule extends AbstractModule {
    private static Logger LOG = LoggerFactory.getLogger(OcelotModule.class);

    @Override
    protected void configure() {
        OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
        bind(OcelotEventQueue.class).toInstance(eventQueue);

        ITSDocStats docStats = new ITSDocStats();
        bind(ITSDocStats.class).toInstance(docStats);

        bind(OcelotApp.class).in(Scopes.SINGLETON);

        AppConfig appConfig = null;
        ProvenanceConfig provConfig = null;
        RuleConfiguration ruleConfig = null;
        PluginManager pluginManager = null;
        try {
            File ocelotDir = new File(System.getProperty("user.home"), ".ocelot");
            ocelotDir.mkdirs();

            Configs configs = new DirectoryBasedConfigs(ocelotDir);

            appConfig = new AppConfig(configs);
            provConfig = new ProvenanceConfig(configs);
            ruleConfig = new RulesParser().loadConfig(configs.getRulesReader());

            pluginManager = new PluginManager(appConfig, new File(ocelotDir, "plugins"));
            pluginManager.discover();
            eventQueue.registerListener(pluginManager);
        } catch (IOException ex) {
            LOG.error("Failed to initialize configuration", ex);
            System.exit(1);
        }

        bind(AppConfig.class).toInstance(appConfig);
        bind(ProvenanceConfig.class).toInstance(provConfig);
        bind(RuleConfiguration.class).toInstance(ruleConfig);
        bind(PluginManager.class).toInstance(pluginManager);

        bindServices(eventQueue, provConfig, docStats);
    }

    private void bindServices(OcelotEventQueue eventQueue, ProvenanceConfig provConfig,
            ITSDocStats docStats) {
        ProvenanceService provService = new ProvenanceService(eventQueue, provConfig);
        bind(ProvenanceService.class).toInstance(provService);
        eventQueue.registerListener(provService);

        SegmentService segmentService = new SegmentServiceImpl(eventQueue);
        bind(SegmentService.class).toInstance(segmentService);
        eventQueue.registerListener(segmentService);

        ITSDocStatsService docStatsService = new ITSDocStatsService(docStats, eventQueue);
        bind(ITSDocStatsService.class).toInstance(docStatsService);
        eventQueue.registerListener(docStatsService);

        XliffService xliffService = new OkapiXliffService(provConfig, eventQueue);
        bind(XliffService.class).toInstance(xliffService);
        eventQueue.registerListener(xliffService);
    }
}
