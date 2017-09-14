package com.vistatec.ocelot.spellcheck;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.eventbus.EventBus;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.config.json.SpellingConfig;
import com.vistatec.ocelot.config.json.SpellingConfig.SpellingDictionary;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.XliffService;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class SpellcheckTest {

    private static final String FOLDER_PATH = "/com/vistatec/ocelot/spellcheck/";

    private static XliffService xliffService;

    private static Spellchecker spellchecker;

    @BeforeClass
    public static void setup() throws Exception {
        OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
        File ocelotDir = new File(System.getProperty("user.home"), ".ocelot");
        ConfigurationManager confManager = new ConfigurationManager();
        confManager.readAndCheckConfiguration(ocelotDir);
        xliffService = new OkapiXliffService(confManager.getOcelotConfigService(), eventQueue);
        spellchecker = new Spellchecker();
    }

    @Before
    public void reset() {
        spellchecker.reset();
    }

    @Test
    public void testEnglish() throws Exception {
        File file = new File(getClass().getResource(FOLDER_PATH + "Spelling_en.xlf").toURI());
        XLIFFDocument xliffDoc = xliffService.parse(file);
        spellchecker.setLocale(Locale.ENGLISH);
        List<CheckResult> results = spellchecker.spellcheck(xliffDoc.getSegments(), () -> false, (n, total) -> {
        }, new SpellingConfig());
        assertEquals(3, results.size());
        assertEquals("fud", results.get(0).getWord());
        assertEquals("hoge", results.get(1).getWord());
        assertEquals("fuga", results.get(2).getWord());
    }

    @Test
    public void testEnglishUserDictionary() throws Exception {
        File file = new File(getClass().getResource(FOLDER_PATH + "Spelling_en.xlf").toURI());
        XLIFFDocument xliffDoc = xliffService.parse(file);
        spellchecker.setLocale(Locale.ENGLISH);
        SpellingConfig config = new SpellingConfig();
        SpellingDictionary dict = new SpellingDictionary();
        dict.setLearnedWords(Arrays.asList("fud", "hoge"));
        config.setDictionary(spellchecker.getLanguage(), dict);
        List<CheckResult> results = spellchecker.spellcheck(xliffDoc.getSegments(), () -> false, (n, total) -> {
        }, config);
        assertEquals(1, results.size());
        assertEquals("fuga", results.get(0).getWord());
    }

    @Test
    public void testRussian() throws Exception {
        File file = new File(getClass().getResource(FOLDER_PATH + "Spelling_ru.xlf").toURI());
        XLIFFDocument xliffDoc = xliffService.parse(file);
        spellchecker.setLocale(Locale.forLanguageTag("ru-ru"));
        List<CheckResult> results = spellchecker.spellcheck(xliffDoc.getSegments(), () -> false, (n, total) -> {
        }, new SpellingConfig());
        assertEquals(1, results.size());
        assertEquals(" \u0434\u0435\u044F\u0442\u0435\u043B\u043B\u043B\u043B\u044C\u043D\u043E\u0441\u0442",
                results.get(0).getWord());
    }
}
