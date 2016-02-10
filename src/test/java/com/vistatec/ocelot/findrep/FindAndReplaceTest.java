package com.vistatec.ocelot.findrep;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
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
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.config.OcelotConfigService;
import com.vistatec.ocelot.config.XmlConfigTransferService;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.XliffService;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class FindAndReplaceTest {

	private static final String FOLDER_PATH = "/com/vistatec/ocelot/findrep/";

	private static XliffService xliffService;

	private static FindAndReplaceManager frManager;

	@BeforeClass
	public static void setup() throws TransferException, JAXBException,
			IOException, XMLStreamException {
		OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
		File ocelotDir = new File(System.getProperty("user.home"), ".ocelot");
		File configFile = new File(ocelotDir, "ocelot_cfg.xml");
		ByteSource configSource = !configFile.exists() ? ByteSource.empty()
				: Files.asByteSource(configFile);

		CharSink configSink = Files.asCharSink(configFile,
				Charset.forName("UTF-8"));
		OcelotConfigService confService = new OcelotConfigService(
				new XmlConfigTransferService(configSource, configSink));
		xliffService = new OkapiXliffService(confService, eventQueue);
		frManager = new FindAndReplaceManager();
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
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, true);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				true);
		String word = "to";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(0, 0, 46, 48,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 50, 52,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 83, 85,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordMultipleWordsCSSourceEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, true);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				true);
		String word = "in the metrics";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(0, 0, 85, 99,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 118, 132,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordMultipleWordsCSUpDirSourceEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, true);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				true);
		frManager.setDirection(FindAndReplaceManager.DIRECTION_UP);
		String word = "in the metrics";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(1, 0, 118, 132,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 85, 99,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordCSSourceUpDirEnglish() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, true);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				true);
		frManager.setDirection(FindAndReplaceManager.DIRECTION_UP);
		String word = "to";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(1, 0, 83, 85,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 50, 52,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 46, 48,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordCSSourceEnglish() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, false);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				true);
		String word = "to";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(0, 0, 46, 48,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 115, 117,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 50, 52,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 83, 85,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordCSSourceUpDirEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, false);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				true);
		frManager.setDirection(FindAndReplaceManager.DIRECTION_UP);
		String word = "to";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(1, 0, 83, 85,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 50, 52,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 115, 117,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 46, 48,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordNoCSSourceEnglish() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, false);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				false);
		String word = "to";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(0, 0, 46, 48,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 74, 76,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 111, 113,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 115, 117,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 50, 52,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 83, 85,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordNoCSSourceDirUpEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, false);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				false);
		frManager.setDirection(FindAndReplaceManager.DIRECTION_UP);
		String word = "to";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(1, 0, 83, 85,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 50, 52,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 115, 117,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 111, 113,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 74, 76,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 46, 48,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordNoCSSourceEnglish() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, true);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				false);
		String word = "to";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(0, 0, 46, 48,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 74, 76,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 50, 52,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 83, 85,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordNoCSSourceUpDirEnglish()
			throws URISyntaxException, IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "Tiny.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_SOURCE, Locale.ENGLISH);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, true);
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				false);
		frManager.setDirection(FindAndReplaceManager.DIRECTION_UP);
		String word = "to";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(1, 0, 83, 85,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(1, 0, 50, 52,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 74, 76,
				FindAndReplaceManager.SCOPE_SOURCE));
		expResults.add(new FindReplaceResult(0, 0, 46, 48,
				FindAndReplaceManager.SCOPE_SOURCE));
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordTargetJapanese() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "jp_MT.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_TARGET, Locale.JAPANESE);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, true);
		// String word = "イント";
		String word = "ノード";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(0, 0, 0, 3,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(1, 0, 0, 3,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(1, 0, 21, 24,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(2, 0, 8, 11,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(2, 0, 23, 26,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(4, 0, 48, 51,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(5, 0, 64, 67,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(6, 0, 59, 62,
				FindAndReplaceManager.SCOPE_TARGET));
		assertResults(expResults, results);
		word = "ノー";
		results = findResults(xliffDoc.getSegments(), word);
		expResults = new ArrayList<FindReplaceResult>();
		assertResults(expResults, results);
	}

	@Test
	public void testWholeWordTargetUpDirJapanese() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "jp_MT.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_TARGET, Locale.JAPANESE);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, true);
		frManager.setDirection(FindAndReplaceManager.DIRECTION_UP);
		// String word = "イント";
		String word = "ノード";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(6, 0, 59, 62,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(5, 0, 64, 67,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(4, 0, 48, 51,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(2, 0, 23, 26,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(2, 0, 8, 11,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(1, 0, 21, 24,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(1, 0, 0, 3,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(0, 0, 0, 3,
				FindAndReplaceManager.SCOPE_TARGET));
		assertResults(expResults, results);
		word = "ノー";
		results = findResults(xliffDoc.getSegments(), word);
		expResults = new ArrayList<FindReplaceResult>();
		assertResults(expResults, results);
	}

	@Test
	public void testNoWholeWordTargetJapanese() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "jp_MT.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_TARGET, Locale.JAPANESE);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, false);
		String word = "ノー";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(0, 0, 0, 2,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(1, 0, 0, 2,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(1, 0, 21, 23,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(2, 0, 8, 10,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(2, 0, 23, 25,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(4, 0, 48, 50,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(5, 0, 64, 66,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(6, 0, 59, 61,
				FindAndReplaceManager.SCOPE_TARGET));
		assertResults(expResults, results);

	}

	@Test
	public void testNoWholeWordTargetUpDirJapanese() throws URISyntaxException,
			IOException, XMLStreamException {

		File file = new File(getClass().getResource(FOLDER_PATH + "jp_MT.xlf")
				.toURI());
		XLIFFDocument xliffDoc = xliffService.parse(file);
		frManager.setScope(FindAndReplaceManager.SCOPE_TARGET, Locale.JAPANESE);
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION, false);
		frManager.setDirection(FindAndReplaceManager.DIRECTION_UP);
		String word = "ノー";
		List<FindReplaceResult> results = findResults(xliffDoc.getSegments(),
				word);
		List<FindReplaceResult> expResults = new ArrayList<FindReplaceResult>();
		expResults.add(new FindReplaceResult(6, 0, 59, 61,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(5, 0, 64, 66,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(4, 0, 48, 50,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(2, 0, 23, 25,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(2, 0, 8, 10,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(1, 0, 21, 23,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(1, 0, 0, 2,
				FindAndReplaceManager.SCOPE_TARGET));
		expResults.add(new FindReplaceResult(0, 0, 0, 2,
				FindAndReplaceManager.SCOPE_TARGET));
		assertResults(expResults, results);

	}

	private List<FindReplaceResult> findResults(List<OcelotSegment> segments,
			String text) {
		List<FindReplaceResult> results = new ArrayList<FindReplaceResult>();
		FindReplaceResult result = null;
		while (result == null
				|| (result.getStringStartIndex() != -1 && result
						.getStringEndIndex() != -1)) {
			frManager.findNextWord(text, segments);
			result = new FindReplaceResult(frManager.getSegmentIndex(),
					frManager.getCurrAtomIndex(),
					frManager.getWordFirstIndex(),
					frManager.getWordLastIndex(), frManager.getScope());
			if (frManager.getWordFirstIndex() != -1
					&& frManager.getWordLastIndex() != -1) {
				results.add(result);
			}
		}
		return results;
	}

	private void assertResults(List<FindReplaceResult> expected,
			List<FindReplaceResult> actual) {
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