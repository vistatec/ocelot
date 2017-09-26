package com.vistatec.ocelot.di;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.vistatec.ocelot.DefaultPlatformSupport;
import com.vistatec.ocelot.OSXPlatformSupport;
import com.vistatec.ocelot.OcelotApp;
import com.vistatec.ocelot.PlatformSupport;
import com.vistatec.ocelot.config.Configs;
import com.vistatec.ocelot.config.ConfigurationException;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.LqiJsonConfigService;
import com.vistatec.ocelot.config.ProfileConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.findrep.FindAndReplaceController;
import com.vistatec.ocelot.its.stats.model.ITSDocStats;
import com.vistatec.ocelot.lgk.LingoTekManager;
import com.vistatec.ocelot.lqi.LQIGridController;
import com.vistatec.ocelot.plugins.PluginManager;
import com.vistatec.ocelot.profile.ProfileManager;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RulesParser;
import com.vistatec.ocelot.services.ITSDocStatsService;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.ProvenanceService;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.services.SegmentServiceImpl;
import com.vistatec.ocelot.services.XliffService;
import com.vistatec.ocelot.spellcheck.SpellcheckController;
import com.vistatec.ocelot.tm.TmManager;
import com.vistatec.ocelot.tm.TmPenalizer;
import com.vistatec.ocelot.tm.TmService;
import com.vistatec.ocelot.tm.gui.TmGuiManager;
import com.vistatec.ocelot.tm.okapi.OkapiTmManager;
import com.vistatec.ocelot.tm.okapi.OkapiTmService;
import com.vistatec.ocelot.tm.okapi.OkapiTmxWriter;
import com.vistatec.ocelot.tm.penalty.SimpleTmPenalizer;

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

        PlatformSupport platformSupport = getPlatformSupport();

        bind(PlatformSupport.class).toInstance(platformSupport);

        JsonConfigService ocelotCfgService = null;
		LqiJsonConfigService lqiCfgService = null;
		ProfileConfigService profileCfgService = null;
        RuleConfiguration ruleConfig = null;
        PluginManager pluginManager = null;
        TmManager tmManager = null;
        TmService tmService = null;
        TmPenalizer penalizer = null;
        TmGuiManager tmGuiManager = null;
        LQIGridController lqiGridController = null;
        FindAndReplaceController frController = null;
        SpellcheckController scController = null;
        ProfileManager profileManager = null;
        try {
            File ocelotDir = new File(System.getProperty("user.home"), ".ocelot");
            ocelotDir.mkdirs();
            ConfigurationManager confManager = new ConfigurationManager();
            confManager.readAndCheckConfiguration(ocelotDir);
            profileCfgService = confManager.getProfileConnfigService();
            File confFolder = confManager.getOcelotMainConfigurationFolder();
            profileManager = new ProfileManager(confFolder, profileCfgService, eventQueue);
            ocelotCfgService = confManager.getOcelotConfigService();
            lqiCfgService = confManager.getLqiConfigService();
            Configs configs = confManager.getRulesConfigs();
            ruleConfig = new RulesParser().loadConfig(configs.getRulesReader());

            pluginManager = new PluginManager(ocelotCfgService, confManager.getPluginsFolder(), eventQueue);
            pluginManager.discover();
            eventQueue.registerListener(pluginManager);

            SegmentService segmentService = new SegmentServiceImpl(eventQueue);
            bind(SegmentService.class).toInstance(segmentService);
            eventQueue.registerListener(segmentService);

            File tm = confManager.getTmConfigDir();
            OkapiTmxWriter tmxWriter = new OkapiTmxWriter(segmentService);
            eventQueue.registerListener(tmxWriter);
            tmManager = new OkapiTmManager(tm, ocelotCfgService, tmxWriter);
            
            bind(OkapiTmManager.class).toInstance((OkapiTmManager) tmManager);
            penalizer = new SimpleTmPenalizer(tmManager);
            tmService = new OkapiTmService((OkapiTmManager)tmManager, penalizer, ocelotCfgService);
            tmGuiManager = new TmGuiManager(tmManager, tmService, eventQueue, ocelotCfgService);
            
            lqiGridController = new LQIGridController(lqiCfgService, eventQueue,
                                                      platformSupport, confManager.getOcelotConfigService().canShowManageConfsButton());
            eventQueue.registerListener(lqiGridController);
            bind(LQIGridController.class).toInstance(lqiGridController);
            
            frController = new FindAndReplaceController(eventQueue, ocelotCfgService.isShowNotTranslatableRows());
            eventQueue.registerListener(frController);
            scController = new SpellcheckController(eventQueue, ocelotCfgService);
            eventQueue.registerListener(scController);
            LingoTekManager lgkManager = new LingoTekManager(ocelotCfgService.getLingoTekConfigurationParams());
            bind(LingoTekManager.class).toInstance(lgkManager);
        } catch (IOException | TransferException | ConfigurationException ex) {
            LOG.error("Failed to initialize configuration", ex);
            System.exit(1);
        }

        bind(RuleConfiguration.class).toInstance(ruleConfig);
        bind(PluginManager.class).toInstance(pluginManager);
        bind(TmManager.class).toInstance(tmManager);
        bind(TmPenalizer.class).toInstance(penalizer);
        bind(TmService.class).toInstance(tmService);
        bind(TmGuiManager.class).toInstance(tmGuiManager);
        bind(FindAndReplaceController.class).toInstance(frController);
        bind(SpellcheckController.class).toInstance(scController);
        bind(ProfileManager.class).toInstance(profileManager);

		bindServices(eventQueue, profileCfgService, ocelotCfgService, lqiCfgService, docStats);
    }
    

	public static PlatformSupport getPlatformSupport() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Mac")) {
            return new OSXPlatformSupport();
        }
        return new DefaultPlatformSupport();
    }


	private void bindServices(OcelotEventQueue eventQueue,
	         ProfileConfigService profileCfgService, JsonConfigService jsonCfgService, LqiJsonConfigService lqiCfgService,
            ITSDocStats docStats) {
		
		bind(ProfileConfigService.class).toInstance(profileCfgService);
        bind(JsonConfigService.class).toInstance(jsonCfgService);
		bind(LqiJsonConfigService.class).toInstance(lqiCfgService);

        ProvenanceService provService = new ProvenanceService(eventQueue, jsonCfgService);
        bind(ProvenanceService.class).toInstance(provService);
        eventQueue.registerListener(provService);

        ITSDocStatsService docStatsService = new ITSDocStatsService(docStats, eventQueue);
        bind(ITSDocStatsService.class).toInstance(docStatsService);
        eventQueue.registerListener(docStatsService);

        XliffService xliffService = new OkapiXliffService(jsonCfgService, eventQueue);
        bind(XliffService.class).toInstance(xliffService);
        eventQueue.registerListener(xliffService);

    }

}