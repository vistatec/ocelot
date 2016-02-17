package com.vistatec.ocelot.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameQualifier;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGrid;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

public class TestLQIConfig {

	private static final String RES_FOLDER = "/com/vistatec/ocelot/config/";

	@Test
	public void testWriteConfiguration() throws IOException, TransferException,
	        JAXBException, URISyntaxException, SAXException {

		File configFile = File.createTempFile("lqi_config", "xml");
		LqiConfigService confService = createConfigService(configFile);
		confService.saveLQIConfig(getTestLqiGrid());
		File expectedConfFile = new File(getClass().getResource(
		        RES_FOLDER + "lqi_config.xml").toURI());
		XMLUnit.setIgnoreWhitespace(Boolean.TRUE);
		// XMLUnit.setNormalizeWhitespace(Boolean.TRUE);
		XMLUnit.setIgnoreAttributeOrder(Boolean.TRUE);
		Diff result = new Diff(new FileReader(expectedConfFile),
		        new FileReader(configFile));
		result.overrideElementQualifier(new ElementNameQualifier());
		// XMLAssert.assertXMLEqual(result, true);
	}

	@Test
	public void testReadConfiguration() throws URISyntaxException,
	        TransferException, JAXBException {

		File configFile = new File(getClass().getResource(
		        RES_FOLDER + "lqi_config.xml").toURI());
		LqiConfigService confService = createConfigService(configFile);
		LQIGrid lqiGrid = confService.readLQIConfig();
		LQIGrid expectedLqiGrid = getTestLqiGrid();
		assertLQIGrid(expectedLqiGrid, lqiGrid);
	}

	private LqiConfigService createConfigService(final File configFile)
	        throws TransferException, JAXBException {
		ByteSource configSource = !configFile.exists() ? ByteSource.empty()
		        : Files.asByteSource(configFile);

		CharSink configSink = Files.asCharSink(configFile,
		        Charset.forName("UTF-8"));
		return new LqiConfigService(new LQIXmlConfigTransferService(
		        configSource, configSink));

	}

	private LQIGrid getTestLqiGrid() {
		LQIGrid grid = new LQIGrid();

		// severities
		List<LQISeverity> severities = new ArrayList<LQISeverity>();
		severities.add(new LQISeverity("Minor", 1.0));
		severities.add(new LQISeverity("Major", 2.0));
		severities.add(new LQISeverity("Critical", 4.0));
		grid.setSeverities(severities);

		// categories
		List<LQIErrorCategory> categories = new ArrayList<LQIErrorCategory>();
		LQIErrorCategory errCat = new LQIErrorCategory("terminology");
		errCat.setWeight(20.0f);
		List<LQIShortCut> shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(severities.get(0), 127, ""));
		errCat.setShortcuts(shortcuts);
		categories.add(errCat);

		errCat = new LQIErrorCategory("duplication");
		errCat.setWeight(25.0f);
		shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(severities.get(1), 54, "Ctrl+Alt"));
		errCat.setShortcuts(shortcuts);
		categories.add(errCat);

		errCat = new LQIErrorCategory("mistranslation");
		errCat.setWeight(30.0f);
		shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(severities.get(2), 68, "Ctrl+Shift"));
		errCat.setShortcuts(shortcuts);
		categories.add(errCat);

		errCat = new LQIErrorCategory("duplication");
		errCat.setWeight(30.0f);
		shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(severities.get(0), 66, "Ctrl+Alt+Shift"));
		shortcuts.add(new LQIShortCut(severities.get(1), 54, "Alt+Shift"));
		shortcuts.add(new LQIShortCut(severities.get(2), 68, ""));
		errCat.setShortcuts(shortcuts);
		categories.add(errCat);

		errCat = new LQIErrorCategory("omission");
		errCat.setWeight(15.0f);
		categories.add(errCat);

		errCat = new LQIErrorCategory("mistranslation");
		errCat.setWeight(35.0f);
		categories.add(errCat);

		grid.setErrorCategories(categories);

		return grid;
	}

	private void assertLQIGrid(LQIGrid expected, LQIGrid actual) {

		if (expected != null && actual != null) {

			assertLQISeverities(expected.getSeverities(),
			        actual.getSeverities());
			assertLQICategories(expected.getErrorCategories(),
			        actual.getErrorCategories());
		} else {
			Assert.assertTrue(expected == null && actual == null);
		}
	}

	private void assertLQISeverities(List<LQISeverity> expected,
	        List<LQISeverity> actual) {

		if (expected != null && actual != null) {
			Assert.assertEquals(expected.size(), actual.size());
			for (int i = 0; i < expected.size(); i++) {
				Assert.assertEquals(expected.get(i).getName(), actual.get(i)
				        .getName());
				Assert.assertEquals(expected.get(i).getScore(), actual.get(i)
				        .getScore(), 0.0);
			}
		} else {
			Assert.assertTrue(expected == null && actual == null);
		}
	}

	private void assertLQICategories(List<LQIErrorCategory> expected,
	        List<LQIErrorCategory> actual) {

		if (expected != null && actual != null) {
			Assert.assertEquals(expected.size(), actual.size());
			for (int i = 0; i < expected.size(); i++) {
				Assert.assertEquals(expected.get(i).getName(), actual.get(i)
				        .getName());
				Assert.assertEquals(expected.get(i).getWeight(), actual.get(i)
				        .getWeight(), 0.0);
				assertLQIShortcuts(expected.get(i).getShortcuts(), actual
				        .get(i).getShortcuts());
			}
		} else {
			Assert.assertTrue(expected == null && actual == null);
		}
	}

	private void assertLQIShortcuts(List<LQIShortCut> expected,
	        List<LQIShortCut> actual) {
		if (expected != null && actual != null) {
			Assert.assertEquals(expected.size(), actual.size());
			for (int i = 0; i < expected.size(); i++) {
				Assert.assertEquals(expected.get(i).getModifiersString(),
				        actual.get(i).getModifiersString());
				Assert.assertEquals(expected.get(i).getKeyCode(), actual.get(i)
				        .getKeyCode());
				Assert.assertEquals(expected.get(i).getSeverity().getName(),
				        actual.get(i).getSeverity().getName());

			}
		} else {
			Assert.assertTrue(expected == null && actual == null);
		}
	}
}
