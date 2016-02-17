package com.vistatec.ocelot.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.config.xml.LQIGridConfig;
import com.vistatec.ocelot.config.xml.LQIGridConfig.LQICategory;
import com.vistatec.ocelot.config.xml.LQIGridConfig.LQIConfigSeverity;
import com.vistatec.ocelot.config.xml.LQIGridConfig.Shortcut;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGrid;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

/**
 * LQI Grid configuration service.
 */
public class LqiConfigService {

	/** Configuration transfer service. */
	private final ConfigTransferService cfgXservice;

	/** The LQI grid root node. */
	private LQIGridConfig rootConfig;

	/**
	 * Constructor.
	 * 
	 * @param cfgXService
	 *            the configuration transfer service
	 * @throws ConfigTransferService.TransferException
	 *             the transfer exception
	 */
	public LqiConfigService(ConfigTransferService cfgXService)
	        throws ConfigTransferService.TransferException {
		this.cfgXservice = cfgXService;
		this.rootConfig = (LQIGridConfig) cfgXService.parse();

	}

	/**
	 * Saves the LQI grid configuration
	 * 
	 * @param lqiGrid
	 *            the LQI grid
	 * @throws TransferException
	 *             the transfer exception
	 */
	public void saveLQIConfig(LQIGrid lqiGrid) throws TransferException {

		if (lqiGrid != null) {
			rootConfig.clear();
			if (lqiGrid.getSeverities() != null) {
				List<LQIConfigSeverity> confSeverities = new ArrayList<LQIGridConfig.LQIConfigSeverity>();
				LQIConfigSeverity confSev = null;
				for (int i = 0; i < lqiGrid.getSeverities().size(); i++) {
					confSev = new LQIConfigSeverity();
					confSev.setName(lqiGrid.getSeverities().get(i).getName());
					confSev.setScore(lqiGrid.getSeverities().get(i).getScore());
					confSeverities.add(confSev);
				}
				rootConfig.setLqiSeverities(confSeverities);
			}
			if (lqiGrid.getErrorCategories() != null) {
				List<LQICategory> confCategories = new ArrayList<LQIGridConfig.LQICategory>();
				LQICategory confCat = null;
				LQIErrorCategory cat = null;
				for (int i = 0; i < lqiGrid.getErrorCategories().size(); i++) {
					cat = lqiGrid.getErrorCategories().get(i);
					confCat = new LQICategory();
					confCat.setName(cat.getName());
					confCat.setWeight(cat.getWeight());
					confCat.setPosition(i);
					if (cat.getShortcuts() != null) {
						List<Shortcut> confShortcuts = new ArrayList<LQIGridConfig.Shortcut>();
						Shortcut confSc = null;
						for (LQIShortCut sc : cat.getShortcuts()) {
							confSc = new Shortcut();
							confSc.setKeyCode(sc.getKeyCode());
							confSc.setModifiers(sc.getModifiersString());
							if (sc.getSeverity() != null) {
								confSc.setSeverityName(sc.getSeverity()
								        .getName());
							}
							confShortcuts.add(confSc);

						}
						confCat.setShortCuts(confShortcuts);
					}
					confCategories.add(confCat);

				}
				rootConfig.setLqiCategories(confCategories);

			}
		}
		cfgXservice.save(rootConfig);
	}

	/**
	 * Reads the LQI grid configuration.
	 * 
	 * @return the LQI grid object
	 * @throws TransferException
	 *             the transfer exception.
	 */
	public LQIGrid readLQIConfig() throws TransferException {

		LQIGrid grid = null;
		if (rootConfig != null) {
			grid = new LQIGrid();
			if (rootConfig.getLqiSeverities() != null) {
				Collections.sort(rootConfig.getLqiSeverities(),
				        new LQISeveritiesComparator());
				List<LQISeverity> severities = new ArrayList<LQISeverity>();
				for (LQIConfigSeverity confSev : rootConfig.getLqiSeverities()) {
					severities.add(new LQISeverity(confSev.getName(), confSev
					        .getScore()));
				}
				grid.setSeverities(severities);
			}
			if (rootConfig.getLqiCategories() != null) {
				Collections.sort(rootConfig.getLqiCategories(),
				        new LQICategoriesComparator());
				List<LQIErrorCategory> categories = new ArrayList<LQIErrorCategory>();
				LQIErrorCategory errCat = null;
				for (LQICategory confCat : rootConfig.getLqiCategories()) {
					errCat = new LQIErrorCategory(confCat.getName());
					errCat.setWeight(confCat.getWeight());
					if (confCat.getShortcuts() != null) {
						List<LQIShortCut> shortcuts = new ArrayList<LQIShortCut>();
						for (Shortcut sc : confCat.getShortcuts()) {
							shortcuts.add(new LQIShortCut(
							        findSeverityByName(sc.getSeverityName(),
							                grid.getSeverities()), sc
							                .getKeyCode(), sc.getModifiers()));
						}
						errCat.setShortcuts(shortcuts);
					}
					categories.add(errCat);
				}
				grid.setErrorCategories(categories);
			}
		}
		return grid;

	}

	/**
	 * Finds the severity having a specific name.
	 * 
	 * @param name
	 *            the severity name
	 * @param severities
	 *            the list of severities
	 * @return the severity having that name.
	 */
	private LQISeverity findSeverityByName(String name,
	        List<LQISeverity> severities) {

		LQISeverity severity = null;
		if (severities != null) {
			for (LQISeverity currSev : severities) {
				if (currSev.getName().equals(name)) {
					severity = currSev;
					break;
				}
			}
		}
		return severity;
	}
}

/**
 * LQI categories comparator.
 */
class LQICategoriesComparator implements Comparator<LQICategory> {

	@Override
	public int compare(LQICategory o1, LQICategory o2) {
		return Integer.valueOf(o1.getPosition()).compareTo(
		        Integer.valueOf(o2.getPosition()));
	}

}

/**
 * LQI severity comparator.
 */
class LQISeveritiesComparator implements Comparator<LQIConfigSeverity> {

	@Override
	public int compare(LQIConfigSeverity o1, LQIConfigSeverity o2) {
		return Double.compare(o1.getScore(), o2.getScore());
	}

}