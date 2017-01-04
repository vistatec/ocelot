package com.vistatec.ocelot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGrid;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

public class TestLQIConfig {

	private static final String RES_FOLDER = "/com/vistatec/ocelot/config/";

	@Test
	public void testWriteConfiguration() throws IOException, TransferException,
	        JAXBException, URISyntaxException, SAXException {

		File configFile = File.createTempFile("lqi_config", "json");
		LqiJsonConfigService confService = createConfigService(configFile);
		confService.saveLQIConfig(getTestLqiGrid());
		File expectedConfFile = new File(getClass().getResource(
		        RES_FOLDER + "lqi_config.json").toURI());
		Model expected = ModelFactory.createDefaultModel();
		expected.read(new FileInputStream(expectedConfFile), null, "JSON-LD");
		Model actual = ModelFactory.createDefaultModel();
		actual.read(new FileInputStream(configFile), null, "JSON-LD");
		Assert.assertTrue(expected.isIsomorphicWith(actual));
	}

	@Test
	public void testReadConfiguration() throws URISyntaxException,
	        TransferException, JAXBException {

		File configFile = new File(getClass().getResource(
		        RES_FOLDER + "lqi_config.json").toURI());
		LqiJsonConfigService confService = createConfigService(configFile);
		LQIGrid lqiGrid = confService.readLQIConfig();
		LQIGrid expectedLqiGrid = getTestLqiGrid();
		assertLQIGrid(expectedLqiGrid, lqiGrid);
	}

	private LqiJsonConfigService createConfigService(final File configFile)
	        throws TransferException, JAXBException {
//		ByteSource configSource = !configFile.exists() ? ByteSource.empty()
//		        : Files.asByteSource(configFile);
//
//		CharSink configSink = Files.asCharSink(configFile,
//		        Charset.forName("UTF-8"));
		return new LqiJsonConfigService(new LqiJsonConfigTransferService(
				configFile));

	}

	private LQIGrid getTestLqiGrid() {
		LQIGrid grid = new LQIGrid();
		grid.setActiveConfName("Configuration A");

		LQIGridConfiguration gridConf = new LQIGridConfiguration();
		gridConf.setName("Configuration A");
		gridConf.setThreshold(80.0);
		gridConf.setSupplier("Google");
		// severities
		List<LQISeverity> severities = new ArrayList<LQISeverity>();
		severities.add(new LQISeverity("Minor", 1.0));
		severities.add(new LQISeverity("Major", 2.0));
		severities.add(new LQISeverity("Critical", 4.0));
		gridConf.setSeverities(severities);

		// categories
		List<LQIErrorCategory> categories = new ArrayList<LQIErrorCategory>();
		LQIErrorCategory errCat = new LQIErrorCategory("terminology");
		errCat.setWeight(20.0);
		List<LQIShortCut> shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(severities.get(0), 127, ""));
		errCat.setShortcuts(shortcuts);
		categories.add(errCat);

		errCat = new LQIErrorCategory("whitespace");
		errCat.setWeight(1.2);
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


		gridConf.setErrorCategories(categories);
		grid.addConfiguration(gridConf);
		
		gridConf = new LQIGridConfiguration();
		gridConf.setName("Configuration B");
		gridConf.setSupplier("Nike");
		gridConf.setThreshold(85.0);
		
		grid.addConfiguration(gridConf);
		
		gridConf.addSeverity(new LQISeverity("Severity 1", 1.0));
		gridConf.addSeverity(new LQISeverity("Severity 2", 2.0));
		gridConf.addSeverity(new LQISeverity("Severity 3", 3.0));
		
		errCat = new LQIErrorCategory("Category 1");
		errCat.setWeight(2.0);
		shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(gridConf.getSeverity("Severity 1"), 60, "Alt"));
		shortcuts.add(new LQIShortCut(gridConf.getSeverity("Severity 3"), 63, "Shift+Ctrl"));
		errCat.setShortcuts(shortcuts);
		gridConf.addErrorCategory(errCat, 0);
		
		errCat = new LQIErrorCategory("Category 2");
		errCat.setWeight(0.0);
		gridConf.addErrorCategory(errCat, 1);
		
		errCat = new LQIErrorCategory("Category 3");
		errCat.setWeight(1.0);
		shortcuts = new ArrayList<LQIShortCut>();
		shortcuts.add(new LQIShortCut(gridConf.getSeverity("Severity 2"), 65, "Ctrl+Alt"));
		errCat.setShortcuts(shortcuts);
		gridConf.addErrorCategory(errCat, 2);

		return grid;
	}

	private void assertLQIGrid(LQIGrid expected, LQIGrid actual) {

		if (expected != null && actual != null) {
			
			Assert.assertEquals(expected.getActiveConfName(), actual.getActiveConfName());
			assertLQIGridConfigurations(expected.getConfigurations(), actual.getConfigurations());
		} else {
			Assert.assertTrue(expected == null && actual == null);
		}
	}
	
	
	private void assertLQIGridConfigurations(List<LQIGridConfiguration> expected, List<LQIGridConfiguration> actual){
		
		if(expected != null && actual != null){
			Assert.assertEquals(expected.size(), actual.size());
			Comparator<LQIGridConfiguration> confComparator = new Comparator<LQIGridConfiguration>() {
				
				@Override
				public int compare(LQIGridConfiguration o1, LQIGridConfiguration o2) {
					return o1.getName().compareTo(o2.getName());
				}
			};
			Collections.sort(expected, confComparator);
			Collections.sort(actual, confComparator);
			for(int i = 0; i<expected.size(); i++){
				Assert.assertEquals(expected.get(i).getName(), actual.get(i).getName());
				Assert.assertEquals(expected.get(i).getSupplier(), actual.get(i).getSupplier());
				Assert.assertEquals(expected.get(i).getThreshold(), actual.get(i).getThreshold(), 0.0);
				assertLQICategories(expected.get(i).getErrorCategories(), actual.get(i).getErrorCategories());
				assertLQISeverities(expected.get(i).getSeverities(), actual.get(i).getSeverities());
			}
		} else {
			Assert.assertNull(expected);
			Assert.assertNull(actual);
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
