package com.vistatec.ocelot.findrep;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.eventbus.EventBus;
import com.vistatec.ocelot.config.ConfigurationException;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.XliffService;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class FindTest {

	private static final String FOLDER_PATH = "/com/vistatec/ocelot/findrep/";

	private static XliffService xliffService;

	private static WordFinder frManager;

	@BeforeClass
	public static void setup() throws JAXBException,
			IOException, XMLStreamException, ConfigurationException {
		OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
		File ocelotDir = new File(System.getProperty("user.home"), ".ocelot");
		ConfigurationManager confManager = new ConfigurationManager();
		confManager.readAndCheckConfiguration(ocelotDir);
		xliffService = new OkapiXliffService(confManager.getOcelotConfigService(), eventQueue);
		frManager = new WordFinder();
	}

	@Before
	public void resetFRManage() {
		System.out.println("Reset manager");
		frManager.reset();
	}

	@Test
	public void testWholeWordCSSourceEnglish() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, true);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				true);
		String word = "to";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(0, 0, 46, 48,
				false));
		expResults.add(new FindResult(1, 0, 50, 52,
				false));
		expResults.add(new FindResult(1, 0, 83, 85,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordMultipleWordsCSSourceEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, true);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				true);
		String word = "in the metrics";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(0, 0, 85, 99,
				false));
		expResults.add(new FindResult(1, 0, 118, 132,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordMultipleWordsCSUpDirSourceEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, true);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				true);
		frManager.setDirection(WordFinder.DIRECTION_UP);
		String word = "in the metrics";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(1, 0, 118, 132,
				false));
		expResults.add(new FindResult(0, 0, 85, 99,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordCSSourceUpDirEnglish() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, true);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				true);
		frManager.setDirection(WordFinder.DIRECTION_UP);
		String word = "to";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(1, 0, 83, 85,
				false));
		expResults.add(new FindResult(1, 0, 50, 52,
				false));
		expResults.add(new FindResult(0, 0, 46, 48,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordCSSourceEnglish() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, false);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				true);
		String word = "to";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(0, 0, 46, 48,
				false));
		expResults.add(new FindResult(0, 0, 115, 117,
				false));
		expResults.add(new FindResult(1, 0, 50, 52,
				false));
		expResults.add(new FindResult(1, 0, 83, 85,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordCSSourceUpDirEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, false);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				true);
		frManager.setDirection(WordFinder.DIRECTION_UP);
		String word = "to";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(1, 0, 83, 85,
				false));
		expResults.add(new FindResult(1, 0, 50, 52,
				false));
		expResults.add(new FindResult(0, 0, 115, 117,
				false));
		expResults.add(new FindResult(0, 0, 46, 48,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordNoCSSourceEnglish() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, false);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				false);
		String word = "to";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(0, 0, 46, 48,
				false));
		expResults.add(new FindResult(0, 0, 74, 76,
				false));
		expResults.add(new FindResult(0, 0, 111, 113,
				false));
		expResults.add(new FindResult(0, 0, 115, 117,
				false));
		expResults.add(new FindResult(1, 0, 50, 52,
				false));
		expResults.add(new FindResult(1, 0, 83, 85,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordNoCSSourceDirUpEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, false);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				false);
		frManager.setDirection(WordFinder.DIRECTION_UP);
		String word = "to";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(1, 0, 83, 85,
				false));
		expResults.add(new FindResult(1, 0, 50, 52,
				false));
		expResults.add(new FindResult(0, 0, 115, 117,
				false));
		expResults.add(new FindResult(0, 0, 111, 113,
				false));
		expResults.add(new FindResult(0, 0, 74, 76,
				false));
		expResults.add(new FindResult(0, 0, 46, 48,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordNoCSSourceEnglish() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, true);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				false);
		String word = "to";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(0, 0, 46, 48,
				false));
		expResults.add(new FindResult(0, 0, 74, 76,
				false));
		expResults.add(new FindResult(1, 0, 50, 52,
				false));
		expResults.add(new FindResult(1, 0, 83, 85,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordNoCSSourceUpDirEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, true);
		frManager.enableOption(WordFinder.CASE_SENSITIVE_OPTION,
				false);
		frManager.setDirection(WordFinder.DIRECTION_UP);
		String word = "to";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(1, 0, 83, 85,
				false));
		expResults.add(new FindResult(1, 0, 50, 52,
				false));
		expResults.add(new FindResult(0, 0, 74, 76,
				false));
		expResults.add(new FindResult(0, 0, 46, 48,
				false));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordTargetJapanese() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "jp_MT.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_TARGET, Locale.JAPANESE);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, true);
		// String word = "イント";
		String word = "ノード";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(0, 0, 0, 3,
				true));
		expResults.add(new FindResult(1, 0, 0, 3,
				true));
		expResults.add(new FindResult(1, 0, 21, 24,
				true));
		expResults.add(new FindResult(2, 0, 8, 11,
				true));
		expResults.add(new FindResult(2, 0, 23, 26,
				true));
		expResults.add(new FindResult(4, 0, 48, 51,
				true));
		expResults.add(new FindResult(5, 0, 64, 67,
				true));
		expResults.add(new FindResult(6, 0, 59, 62,
				true));
		assertResults(expResults, results);
		word = "ノー";
		results = findResults(xliffDoc.getSegments(), word);
		expResults = new ArrayList<FindResult>();
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordTargetUpDirJapanese() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "jp_MT.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_TARGET, Locale.JAPANESE);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, true);
		frManager.setDirection(WordFinder.DIRECTION_UP);
		// String word = "イント";
		String word = "ノード";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(6, 0, 59, 62,
				true));
		expResults.add(new FindResult(5, 0, 64, 67,
				true));
		expResults.add(new FindResult(4, 0, 48, 51,
				true));
		expResults.add(new FindResult(2, 0, 23, 26,
				true));
		expResults.add(new FindResult(2, 0, 8, 11,
				true));
		expResults.add(new FindResult(1, 0, 21, 24,
				true));
		expResults.add(new FindResult(1, 0, 0, 3,
				true));
		expResults.add(new FindResult(0, 0, 0, 3,
				true));
		assertResults(expResults, results);
		word = "ノー";
		results = findResults(xliffDoc.getSegments(), word);
		expResults = new ArrayList<FindResult>();
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordTargetJapanese() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "jp_MT.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_TARGET, Locale.JAPANESE);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, false);
		String word = "ノー";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(0, 0, 0, 2,
				true));
		expResults.add(new FindResult(1, 0, 0, 2,
				true));
		expResults.add(new FindResult(1, 0, 21, 23,
				true));
		expResults.add(new FindResult(2, 0, 8, 10,
				true));
		expResults.add(new FindResult(2, 0, 23, 25,
				true));
		expResults.add(new FindResult(4, 0, 48, 50,
				true));
		expResults.add(new FindResult(5, 0, 64, 66,
				true));
		expResults.add(new FindResult(6, 0, 59, 61,
				true));
		assertResults(expResults, results);

	}

	@Test
	public void testNoWholeWordTargetUpDirJapanese() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "jp_MT.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(WordFinder.SCOPE_TARGET, Locale.JAPANESE);
		frManager.enableOption(WordFinder.WHOLE_WORD_OPTION, false);
		frManager.setDirection(WordFinder.DIRECTION_UP);
		String word = "ノー";
		List<FindResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindResult> expResults = new ArrayList<FindResult>();
		expResults.add(new FindResult(6, 0, 59, 61,
				true));
		expResults.add(new FindResult(5, 0, 64, 66,
				true));
		expResults.add(new FindResult(4, 0, 48, 50,
				true));
		expResults.add(new FindResult(2, 0, 23, 25,
				true));
		expResults.add(new FindResult(2, 0, 8, 10,
				true));
		expResults.add(new FindResult(1, 0, 21, 23,
				true));
		expResults.add(new FindResult(1, 0, 0, 2,
				true));
		expResults.add(new FindResult(0, 0, 0, 2,
				true));
		assertResults(expResults, results);

	}

	private List<FindResult> findResults(List<OcelotSegment> segments,
			String text) {
		return frManager.findWord(text, segments);
	}

	private void assertResults(List<FindResult> expected,
			List<FindResult> actual) {
		Assert.assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			Assert.assertEquals(expected.get(i).getSegmentIndex(), actual.get(i)
					.getSegmentIndex());
			Assert.assertEquals(expected.get(i).getAtomIndex(), actual.get(i)
					.getAtomIndex());
			Assert.assertEquals(expected.get(i).getStringStartIndex(), actual
					.get(i).getStringStartIndex());
			Assert.assertEquals(expected.get(i).getStringEndIndex(),
					actual.get(i).getStringEndIndex());
		}
	}

}