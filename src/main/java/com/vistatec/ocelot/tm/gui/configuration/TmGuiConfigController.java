package com.vistatec.ocelot.tm.gui.configuration;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.config.json.TmManagement;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig.TmxFile;
import com.vistatec.ocelot.tm.TmManager;

/**
 * This class stands over all graphic processes pertaining the TM configuration.
 * It manages two dialogs: the <code>TmConfigDialog</code> letting the user set
 * the TM configuration and the
 * <code>TmAddingDialog/code> letting user add a new TM to the configuration.
 * It also manages the "Save as tmx" functionality.
 */
public class TmGuiConfigController {

	/** The logger for this class. */
	private static final Logger logger = LoggerFactory.getLogger(TmGuiConfigController.class);

	/** Default fuzzy threshold value. */
	private static final float DEF_FUZZY_THRESHOLD = 0.75f;

	/** Default max results value. */
	private static final int DEF_MAX_RESULTS = 5;

	/** The object managing the TM Configuration in Ocelot. */
	private TmManager tmManager;

	/** The Ocelot configuration service. */
	private JsonConfigService cfgService;

	/** The TM configuration dialog. */
	private TmConfigDialog configDialog;

	/** The Adding TM dialog. */
	private TmAddingDialog addDialog;

	/** The TM Settings dialog. */
	private TmSettingsDialog settingDialog;

	/** Current opened dialog. */
	private JDialog currDialog;

	/** The TM ordered list. */
	private List<TmConfig> tmOrderedList;

	/**
	 * A list storing the previous TM configuration. It is used to restore
	 * previous configuration in case user discards current changes.
	 */
	private List<TmConfig> tmRollbackList;

	/**
	 * Constructor.
	 * 
	 * @param tmManager
	 *            the TM Manager.
	 * @param cfgService
	 *            the configuration service
	 */
	public TmGuiConfigController(final TmManager tmManager,
	        final JsonConfigService cfgService) {

		this.tmManager = tmManager;
		this.cfgService = cfgService;
		try {
			this.cfgService.saveFuzzyThreshold(DEF_FUZZY_THRESHOLD);
			this.cfgService.saveMaxResults(DEF_MAX_RESULTS);
		} catch (TransferException e) {
			logger.trace(
			        "Error while setting TM default values to the Configuration Service.",
			        e);
		}
	}

	/**
	 * Changes the root directory for an existing TM.
	 * 
	 * @param tm
	 *            the TM
	 * @param newRootDir
	 *            the new root directory.
	 * @return <code>true</code> if the directory has been successfully changed;
	 *         <code>false</code> otherwise.
	 */
	public boolean changeTmDirectory(final TmConfig tm, final File newRootDir) {

		boolean changed = true;
		try {
			tmManager.changeTmDataDir(tm.getTmName(), newRootDir);
		} catch (Exception e) {
			changed = false;
			logger.trace(
			        "Error while changing the root directory [tm = "
			                + tm.getTmName() + " - Old dir = "
			                + tm.getTmDataDir() + " - New dir = " + newRootDir
			                + "]", e);
			JOptionPane
			        .showMessageDialog(
			                configDialog,
			                "An error occurred while changing the selected TM directory.",
			                "TM Change Directory Error",
			                JOptionPane.ERROR_MESSAGE);
		}
		return changed;
	}

	//
	// /**
	// * Creates a new TM.
	// *
	// * @param tmName
	// * the TM name.
	// * @param tmDirPath
	// * the TM root directory.
	// * @throws IOException
	// * The IO exception
	// * @throws TransferException
	// * the transfer exception
	// */
	// public void createNewTm(final String tmName, final String tmDirPath)
	// throws IOException, TransferException {
	// tmManager.initializeNewTm(tmName, new File(tmDirPath));
	// TmConfig newTm = new TmConfig();
	// newTm.setTmName(tmName);
	// newTm.setTmDataDir(tmDirPath);
	// newTm.setEnabled(true);
	// configDialog.addNewTm(newTm);
	// }

	/**
	 * Adds a new TM to the configuration table.
	 * 
	 * @param tmxFiles
	 *            the list of Translation Memory eXchange files.
	 * @throws TransferException 
	 * @throws IOException 
	 */
	public void createNewTm(final File[] tmxFiles) throws IOException, TransferException {

		if (tmxFiles != null && tmxFiles.length > 0) {
			String tmDataDir = tmxFiles[0].getParentFile().getAbsolutePath();
			String ext = ".tmx";
			int extIndex = -1;
			StringBuilder tmName = new StringBuilder();
//			List<TmManagement.TmConfig.TmxFile> tmxFilesList = new ArrayList<TmManagement.TmConfig.TmxFile>();
			List<String> tmxFileNames = new ArrayList<String>();
			for (File currFile : tmxFiles) {
				extIndex = currFile.getName().toLowerCase().indexOf(ext);
				if (tmName.length() > 0) {
					tmName.append(" - ");
				}
				tmName.append(currFile.getName().substring(0, extIndex));
//				TmManagement.TmConfig.TmxFile configFile = new TmManagement.TmConfig.TmxFile();
				tmxFileNames.add(currFile.getName());
//				configFile.setFileName(currFile.getName());
//				tmxFilesList.add(configFile);
			}
			TmConfig newTm = new TmConfig();
			newTm.setEnabled(true);
			newTm.setTmDataDir(tmDataDir);
			newTm.setTmName(tmName.toString());
			List<TmxFile> tmxFilesConf = new ArrayList<TmManagement.TmConfig.TmxFile>();
			if(tmxFileNames != null){
				for(String tmxFileName: tmxFileNames){
					tmxFilesConf.add(new TmxFile(tmxFileName));
				}
			}
			newTm.setTmxFiles(tmxFilesConf);
			tmManager.initializeNewTm(tmName.toString(), tmxFiles);
			configDialog.addNewTm(newTm);
		}
	}

	/**
	 * Deletes the TM identified by the name passed as parameter.
	 * 
	 * @param tmName
	 *            the TM name.
	 * @return <code>true</code> if the TM has been successfully deleted;
	 *         <code>false </code> otherwise.
	 */
	public boolean deleteTm(String tmName) {

		boolean deleted = true;

		try {
			tmManager.deleteTm(tmName);
		} catch (Exception e) {
			deleted = false;
			logger.trace("Error while deleting the TM " + tmName, e);
			JOptionPane
			        .showMessageDialog(configDialog,
			                "An error occurred while deleting the TM '"
			                        + tmName + "'.", "Delete TM Error",
			                JOptionPane.ERROR_MESSAGE);
		}
		return deleted;
	}

	/**
	 * Saves current segments in the Ocelot main grid in a tmx file.
	 * 
	 * @param currentWindow
	 *            the current opened window.
	 */
	public void saveAsTmx(final Window currentWindow) {

		try {
			// Create a filter for the file chooser. Set "tmx" as the accepted
			// extension
			FileNameExtensionFilter filter = new FileNameExtensionFilter("tmx",
			        "tmx");
			// Create and configure the file chooser
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(filter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setDialogTitle("Save as tmx");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			// open the file chooser in "Save" mode.
			int option = fileChooser.showSaveDialog(currentWindow);
			// if the user presses the "Save" button
			if (option == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				// check if the selected file can be accepted by the filter
				// (i.e. it has tmx extension)
				if (!filter.accept(selectedFile)) {
					// if the selected file has not the correct format, add .tmx
					// at the end of the file name
					selectedFile = new File(selectedFile.getAbsolutePath()
					        + "." + filter.getExtensions()[0]);
				}
				// save current segments into the selected file.
				tmManager.saveOpenFileAsTmx(selectedFile);
			}
		} catch (Exception e) {
			logger.trace("Error while saving the opened file as a tmx.", e);
			JOptionPane.showMessageDialog(currentWindow,
			        "An error occurred while saving the tmx file.",
			        "Save as tmx error", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Gets the TM ordered list (current configuration).
	 * 
	 * @return the TM ordered list.
	 */
	public List<TmConfig> getTmOrderedList() {
		return tmOrderedList;
	}

	/**
	 * Opens the TM Configuration dialog.
	 * 
	 * @param ownerFrame
	 *            the owener frame.
	 */
	public void openTmConfigDialog(final Window ownerFrame) {

		List<TmConfig> configuredTmList = tmManager.fetchTms();
		if (configuredTmList != null) {
			tmOrderedList = new ArrayList<>(configuredTmList);
			saveDataForRollback();
		}
		configDialog = new TmConfigDialog(this, ownerFrame);
		currDialog = configDialog;
		SwingUtilities.invokeLater(configDialog);
	}

	/**
	 * Opens the Add TM dialog.
	 */
	public void opentAddTmDialog() {

		addDialog = new TmAddingDialog(this, configDialog);
		currDialog = addDialog;
		SwingUtilities.invokeLater(addDialog);
	}

	/**
	 * Closes the current opened dialog.
	 */
	public void closeDialog() {

		if (currDialog.equals(configDialog)) {
			currDialog = null;
			tmOrderedList = null;
			tmRollbackList = null;
			configDialog = null;
		} else if (currDialog.equals(addDialog)) {
			currDialog = configDialog;
			addDialog = null;
		} else if (currDialog.equals(settingDialog)) {
			currDialog = configDialog;
			settingDialog = null;
		}
	}

	/**
	 * Stores current configuration in the rollback list in order to restore the
	 * previous configuration in case of discard.
	 */
	private void saveDataForRollback() {

		if (tmOrderedList != null && !tmOrderedList.isEmpty()) {

			tmRollbackList = new ArrayList<>();
			TmConfig tm = null;
			for (TmConfig currTm : tmOrderedList) {
				tm = new TmConfig();
				tm.setEnabled(currTm.isEnabled());
				tm.setPenalty(currTm.getPenalty());
				tm.setTmDataDir(currTm.getTmDataDir());
				tm.setTmName(currTm.getTmName());
				tmRollbackList.add(tm);
			}
		}
	}

	/**
	 * Saves the new configuration.
	 * 
	 * @param tmList
	 *            the list being the new TM configuration.
	 * @return <code>true</code> if the saving operation has been successfully
	 *         performed; <code>false</code> otherwise.
	 */
	public boolean save(List<TmConfig> tmList) {
		boolean saved = false;
		try {
			tmManager.saveTmOrdering(tmList);
			tmOrderedList = tmList;
			tmRollbackList = null;
			saved = true;
		} catch (Exception e) {
			logger.trace("An error occurred while saving the TM ordering.", e);
			JOptionPane.showMessageDialog(configDialog,
			        "An error occurred while saving the TM ordering.",
			        "Save TM Ordering Error", JOptionPane.ERROR_MESSAGE);
		}
		return saved;
	}

	/**
	 * Discards current changes to the TM configuration and restores the
	 * previous one.
	 */
	public void cancel() {

		if (tmRollbackList != null) {
			try {
				for (TmConfig tm : tmRollbackList) {
					TmConfig currConfig = tmManager.fetchTm(tm.getTmName());
					if (currConfig != null) {
						tmOrderedList.remove(currConfig);
						if (!currConfig.getTmDataDir()
						        .equals(tm.getTmDataDir())) {
							changeTmDirectory(currConfig,
							        new File(tm.getTmDataDir()));
						}
					} else {
						tmManager.initializeNewTm(tm.getTmName(),
						        new File(tm.getTmDataDir()));
					}

				}
				for (TmConfig toDelTm : tmOrderedList) {
					tmManager.deleteTm(toDelTm.getTmName());
				}
				tmOrderedList = tmRollbackList;
				tmManager.saveTmOrdering(tmRollbackList);
				tmRollbackList = null;
			} catch (Exception e) {

				logger.trace(
				        "An error occurred while restoring previous TM Configuration.",
				        e);
				JOptionPane
				        .showMessageDialog(
				                configDialog,
				                "An error occurred while restoring the TM Configuration.",
				                "Cancel TM Changes Error",
				                JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Gets currently configured fuzzy threshold.
	 * 
	 * @return currently configured fuzzy threshold.
	 */
	public int getFuzzyThreshold() {

		return (int) (cfgService.getFuzzyThreshold() * 100);
	}

	/**
	 * Gets currently configured max results.
	 * 
	 * @return currently configured max results
	 */
	public int getMaxResults() {
		return cfgService.getMaxResults();
	}

	/**
	 * Gets the default fuzzy threshold value.
	 * 
	 * @return the default fuzzy threshold value.
	 */
	public int getDefaultFuzzyThreshold() {

		return (int) (DEF_FUZZY_THRESHOLD * 100);
	}

	/**
	 * Gets the default max results value.
	 * 
	 * @return the default max results value.
	 */
	public int getDefaultMaxResults() {

		return DEF_MAX_RESULTS;
	}

	/**
	 * Saves the TM settings.
	 * 
	 * @param fuzzyThreshold
	 *            the fuzzy threshold value.
	 * @param maxResults
	 *            the max results value.
	 * @throws TransferException
	 *             the Transfer exception.
	 */
	public void saveTmSettings(float fuzzyThreshold, int maxResults)
	        throws TransferException {

		cfgService.saveFuzzyThreshold(fuzzyThreshold);
		cfgService.saveMaxResults(maxResults);
	}

	/**
	 * Opens the TM Settings dialog.
	 */
	public void openSettingsDialog() {

		settingDialog = new TmSettingsDialog(configDialog, this);
		currDialog = settingDialog;
		SwingUtilities.invokeLater(settingDialog);
	}
}
