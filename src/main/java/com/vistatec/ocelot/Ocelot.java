/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vistatec.ocelot.OcelotApp.ErrorAlertException;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.OcelotJsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.config.json.OcelotAzureConfig;
import com.vistatec.ocelot.di.OcelotModule;
import com.vistatec.ocelot.events.ConfigTmRequestEvent;
import com.vistatec.ocelot.events.LQIConfigurationSelectionChangedEvent;
import com.vistatec.ocelot.events.LQIConfigurationsChangedEvent;
import com.vistatec.ocelot.events.NewPluginsInstalled;
import com.vistatec.ocelot.events.OcelotEditingEvent;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.PluginAddedEvent;
import com.vistatec.ocelot.events.ProfileChangedEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.findrep.FindAndReplaceController;
import com.vistatec.ocelot.its.view.ProvenanceProfileView;
import com.vistatec.ocelot.lgk.LingoTekManager;
import com.vistatec.ocelot.lqi.LQIGridController;
import com.vistatec.ocelot.lqi.LQIKeyEventHandler;
import com.vistatec.ocelot.lqi.LQIKeyEventManager;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.plugins.PluginManagerView;
import com.vistatec.ocelot.profile.ProfileManager;
import com.vistatec.ocelot.rules.FilterView;
import com.vistatec.ocelot.segment.view.SegmentAttributeView;
import com.vistatec.ocelot.segment.view.SegmentView;
import com.vistatec.ocelot.spellcheck.SpellcheckController;
import com.vistatec.ocelot.storage.model.PostUploadRequest;
import com.vistatec.ocelot.storage.service.AzureStorageService;
import com.vistatec.ocelot.storage.service.StorageService;
import com.vistatec.ocelot.storage.service.util.Util;
import com.vistatec.ocelot.tm.gui.TmGuiManager;
import com.vistatec.ocelot.ui.ODialogPanel;
import com.vistatec.ocelot.ui.OcelotToolBar;

/**
 * Main UI Thread class. Handles menu and file operations
 *
 */
public class Ocelot extends JPanel
		implements Runnable, ActionListener, KeyEventDispatcher, ItemListener, OcelotEventQueueListener {
	/** Default serial ID */
	private static final long serialVersionUID = 1L;
	private static String APPNAME = "Ocelot";
	private Image icon;
	private static Logger LOG = LoggerFactory.getLogger(Ocelot.class);

	private JMenuBar menuBar;
	private JMenu menuFile, menuView, menuExtensions, menuHelp, mnuEdit;
	private JMenuItem menuOpenXLIFF, menuDownloadLGK, menuExit, menuAbout, menuRules, menuProv,
            menuSave, menuSaveAs, menuFindReplace, menuSpellcheck, menuWorkspace;
	private JMenuItem menuPlugins;
	private JCheckBoxMenuItem menuTgtDiff;
	private JCheckBoxMenuItem menuShowNotTrans;
	private JMenuItem menuColumns;
	private JMenuItem menuConfigTm;
	private JMenuItem menuSaveAsTmx;
	private JMenuItem menuLqiGrid;
	private JMenuItem menuSaveToAzure;

	private OcelotToolBar toolBar;
	private JFrame mainframe;
	private JSplitPane mainSplitPane;
	private JSplitPane segAttrSplitPane;
	private JSplitPane tmConcordanceSplitPane;
	private SegmentAttributeView segmentAttrView;
	private DetailView itsDetailView;
	private SegmentView segmentView;
	private TmGuiManager tmGuiManager;
	private FindAndReplaceController frController;
    private SpellcheckController scController;
	private OcelotJsonConfigService configService;
	private ProfileManager profileManager;

	private boolean useNativeUI = false;
	private final Color optionPaneBackgroundColor;

	private final Injector ocelotScope;
	private final OcelotEventQueue eventQueue;
	private final OcelotApp ocelotApp;
	private final LQIGridController lqiGridController;
	private final LingoTekManager lgkManager;

	private PlatformSupport platformSupport;

	private StorageService storageService;

	private boolean enableStorage;

	public Ocelot(Injector ocelotScope) throws IOException, InstantiationException, IllegalAccessException {
		super(new BorderLayout());
		this.ocelotScope = ocelotScope;
		this.eventQueue = ocelotScope.getInstance(OcelotEventQueue.class);
		eventQueue.registerListener(this);
		this.ocelotApp = ocelotScope.getInstance(OcelotApp.class);
		this.tmGuiManager = ocelotScope.getInstance(TmGuiManager.class);
		this.eventQueue.registerListener(tmGuiManager);
		this.lqiGridController = ocelotScope.getInstance(LQIGridController.class);
		eventQueue.registerListener(ocelotApp);
		this.frController = ocelotScope.getInstance(FindAndReplaceController.class);
        this.scController = ocelotScope.getInstance(SpellcheckController.class);
		this.configService = (OcelotJsonConfigService) ocelotScope.getInstance(JsonConfigService.class);
		setEnableStorage(configService);
		this.profileManager = ocelotScope.getInstance(ProfileManager.class);
		lgkManager = ocelotScope.getInstance(LingoTekManager.class);
		platformSupport = ocelotScope.getInstance(PlatformSupport.class);
		platformSupport.init(this);

		useNativeUI = Boolean.valueOf(System.getProperty("ocelot.nativeUI", "false"));
		optionPaneBackgroundColor = (Color) UIManager.get("OptionPane.background");

		SegmentView segView = ocelotScope.getInstance(SegmentView.class);
		eventQueue.registerListener(segView);

		SegmentAttributeView segAttrView = ocelotScope.getInstance(SegmentAttributeView.class);
		eventQueue.registerListener(segAttrView);

		DetailView detailView = ocelotScope.getInstance(DetailView.class);
		eventQueue.registerListener(detailView);

		add(setupMainPane(segView, segAttrView, detailView));
	}

	private Component setupMainPane(SegmentView segView, SegmentAttributeView segAttrView, DetailView detailView)
			throws IOException, InstantiationException, IllegalAccessException {

		segmentView = segView;
		segmentView.toggleNotTranslatableSegments(configService.isShowNotTranslatableRows());

		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, setupSegAttrDetailPanes(segAttrView, detailView),
				setupSegmentTmPanes());
		mainSplitPane.setOneTouchExpandable(true);

		return mainSplitPane;
	}

	private Component setupSegAttrDetailPanes(SegmentAttributeView segAttrView, DetailView detailView) {
		Dimension segAttrSize = new Dimension(385, 280);
		itsDetailView = detailView;
		itsDetailView.setPreferredSize(segAttrSize);

		segmentAttrView = segAttrView;
		segmentAttrView.setMinimumSize(new Dimension(305, 280));
		segmentAttrView.setPreferredSize(segAttrSize);

		segAttrSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, segmentAttrView, itsDetailView);
		segAttrSplitPane.setOneTouchExpandable(true);

		return segAttrSplitPane;
	}

	private Component setupSegmentTmPanes() {

		tmConcordanceSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tmGuiManager.getTmPanel(), segmentView);
		tmConcordanceSplitPane.setOneTouchExpandable(true);

		return tmConcordanceSplitPane;
	}

	public void setMainTitle(String sourceTitle) {
		mainframe.setTitle(APPNAME + " - " + sourceTitle);
	}

	public void setMainTitle(String sourceTitle, String targetTitle) {
		mainframe.setTitle(APPNAME + " - " + sourceTitle + ", " + targetTitle);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.menuAbout) {
			showAbout();

		} else if (e.getSource() == this.menuOpenXLIFF) {
			promptOpenXLIFFFile();
		} else if (e.getSource().equals(menuDownloadLGK)) {
			downloadFromLgk();
		} else if (e.getSource() == this.menuRules) {
			showModelessDialog(ocelotScope.getInstance(FilterView.class), "Filters");
		} else if (e.getSource() == this.menuPlugins) {
			showModelessDialog(ocelotScope.getInstance(PluginManagerView.class), "Plugin Manager");

		} else if (e.getSource() == this.menuProv) {
			ProvenanceProfileView userProfileView = ocelotScope.getInstance(ProvenanceProfileView.class);
			this.eventQueue.registerListener(userProfileView);
			showModelessDialog(userProfileView, "Provenance");

		} else if (e.getSource().equals(menuWorkspace)) {
			profileManager.displayProfileDialog(mainframe);
		} else if (e.getSource() == this.menuExit) {
			handleApplicationExit();
		} else if (e.getSource() == this.menuSaveAs) {
			saveAs();
		} else if (e.getSource() == this.menuSaveToAzure) {
			if (ocelotApp.hasOpenFile()) {
				handleStoring(configService);
			}
		} else if (e.getSource().equals(menuSaveAsTmx)) {
			tmGuiManager.saveAsTmx(mainframe);
		} else if (e.getSource() == this.menuSave) {
			saveFile();
		} else if (e.getSource() == this.menuTgtDiff) {
			this.segmentView.setEnabledTargetDiff(this.menuTgtDiff.isSelected());
		} else if (e.getSource() == this.menuColumns) {
			showModelessDialog(new ColumnSelector(segmentView.getTableModel()), "Configure Columns");
		} else if (e.getSource() == this.menuConfigTm) {
			eventQueue.post(new ConfigTmRequestEvent(mainframe));
		} else if (e.getSource() == this.menuLqiGrid) {
			lqiGridController.displayLQIGrid(mainframe);
		} else if (e.getSource() == this.menuFindReplace) {
			frController.displayDialog(mainframe);
		} else if (e.getSource() == this.menuSpellcheck) {
            scController.displayDialog(mainframe);
		} else if (e.getSource().equals(menuShowNotTrans)) {
			try {
				segmentView.toggleNotTranslatableSegments(menuShowNotTrans.isSelected());
				configService.saveNotTransRowConfig(menuShowNotTrans.isSelected());
				frController.setShowNotTransSegments(menuShowNotTrans.isSelected());
			} catch (TransferException e1) {
				LOG.warn("Impossible to save the \"Show not traslatable segments\" configuration", e1);
			}
		}
	}

	private void downloadFromLgk() {
		boolean canDownload = true;
		if (ocelotApp.isFileDirty() && menuSaveToAzure.isEnabled()) {
			canDownload = checkSaveToAzure();
		}
		if (canDownload) {
			File file = lgkManager.downloadFile(mainframe, configService.getUserProvenance().getLangCode());
			openFile(file, true);
		}
	}

	private void promptOpenXLIFFFile() {
		boolean canOpenFile = true;
		if(ocelotApp.isFileDirty() && menuSaveToAzure.isEnabled()){
			canOpenFile = checkSaveToAzure();
		}
		if(canOpenFile){
			FileDialog fd = new FileDialog(mainframe, "Open", FileDialog.LOAD);
			fd.setFilenameFilter(new XliffFileFilter());
			fd.setVisible(true);
			File sourceFile = getSelectedFile(fd);
			fd.dispose();
			openFile(sourceFile, false);
		}
	}

	private void openFile(File file, boolean temporary) {
		if (file != null) {
			try {
				ocelotApp.openFile(file, temporary);
			} catch (FileNotFoundException ex) {
				LOG.error("Failed to parse file '" + file.getName() + "'", ex);
			} catch (Exception e) {
				String errorMsg = "Could not open " + file.getName();
				LOG.error(errorMsg, e);
				alertUser("XLIFF Parsing Error", errorMsg + ": " + e.getMessage());
			}
		}
	}

	@Subscribe
	public void handleFileOpenedEvent(final OpenFileEvent event) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setMainTitle(event.getFilename());
				segmentView.reloadTable();

				menuSave.setEnabled(true);
				menuSaveAs.setEnabled(true);
				menuSaveAsTmx.setEnabled(true);
				menuSaveToAzure.setEnabled(enableStorage);
                menuFindReplace.setEnabled(true);
                menuSpellcheck.setEnabled(true);
				toolBar.loadFontsAndSizes(ocelotApp.getFileSourceLang(), ocelotApp.getFileTargetLang());
				toolBar.setSourceFont(segmentView.getSourceFont());
				toolBar.setTargetFont(segmentView.getTargetFont());

			}
		});
	}

	private File promptSaveAs(String defFileName) {
		FileDialog fd = new FileDialog(mainframe, "Save As...", FileDialog.SAVE);
		fd.setFile(defFileName);
		fd.setVisible(true);
		File f = getSelectedFile(fd);
		fd.dispose();
		return f;
	}

	private File getSelectedFile(FileDialog fd) {
		return (fd.getFile() == null) ? null : new File(fd.getDirectory(), fd.getFile());
	}

	private void saveAs() {
		if (ocelotApp.hasOpenFile()) {
			saveAs(ocelotApp.getDefaultFileName());
		}
	}

	private void saveAs(String defFileName) {

		File saveFile = promptSaveAs(defFileName);
		if (saveFile != null && save(saveFile)) {
			setMainTitle(saveFile.getName());
		}
	}

	private void saveFile() {
		if (ocelotApp.hasOpenFile()) {
			if (ocelotApp.isTemporaryFile()) {
				saveAs();
			} else {
				save(ocelotApp.getOpenFile());
			}
		}
	}

	private boolean save(File saveFile) {
		try {
			ocelotApp.saveFile(saveFile);

		} catch (OcelotApp.ErrorAlertException ex) {
			alertUser(ex.title, ex.body);
			return false;

		} catch (Exception e) {
			LOG.error("Failed to save file: '" + saveFile.getName() + "'", e);
			return false;
		}
		return true;
	}

	private void alertUser(String windowTitle, String message) {
		JTextArea textArea = new JTextArea(message);
		textArea.setColumns(30);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		// This is a hack for the mac, where textarea defaults to a white
		// background
		// but JOptionPane defaults to a non-white background.
		textArea.setBackground(optionPaneBackgroundColor);
		textArea.setSize(textArea.getPreferredSize().width, 1);
		JOptionPane.showMessageDialog(mainframe, textArea, windowTitle, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Show the about dialog.
	 */
	public void showAbout() {
		showModelessDialog(new AboutDialog(icon), "About Ocelot");
	}

	private boolean checkSaveToAzure() {
		LOG.debug("Checking if the currently open file has been saved to Azure");
		boolean retValue = true;
		if (!ocelotApp.getSavedToAzure()) {
			int option = JOptionPane.showConfirmDialog(mainframe,
					"The currently open file has not been saved to Azure. Do you want to continue?", "Save To Azure",
					JOptionPane.YES_NO_OPTION);
			retValue = option == JOptionPane.YES_OPTION;
		}
		return retValue;
	}

	/**
	 * Exit handler. This should prompt to save unsaved data.
	 */
	public void handleApplicationExit() {
		boolean canQuit = true;
		if (ocelotApp.isFileDirty()) {
			if (menuSaveToAzure.isEnabled()) {
				canQuit = checkSaveToAzure();
			} else {
				int rv = JOptionPane.showConfirmDialog(this,
						"You have unsaved changes. Would you like to save before exiting?", "Save Unsaved Changes",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (rv == JOptionPane.YES_OPTION) {
					canQuit = save(ocelotApp.getOpenFile());

				} else if (rv != JOptionPane.NO_OPTION) {
					canQuit = false;
				}
			}

		}
		if (canQuit) {
			quitOcelot();
		}
	}

	private void initializeMenuBar() {
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuBar.add(menuFile);

		menuOpenXLIFF = new JMenuItem("Open XLIFF");
		menuOpenXLIFF.addActionListener(this);
		menuOpenXLIFF.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, getPlatformKeyMask()));
		menuFile.add(menuOpenXLIFF);

		menuDownloadLGK = new JMenuItem("Download from LGK");
		menuDownloadLGK.addActionListener(this);
		menuDownloadLGK.setEnabled(lgkManager.isEnabled());
		menuFile.add(menuDownloadLGK);

		menuSave = new JMenuItem("Save");
		menuSave.setEnabled(false);
		menuSave.addActionListener(this);
		menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, getPlatformKeyMask()));
		menuFile.add(menuSave);

		menuSaveAs = new JMenuItem("Save As...");
		menuSaveAs.setEnabled(false);
		menuSaveAs.addActionListener(this);
		menuSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.SHIFT_MASK | getPlatformKeyMask()));
		menuFile.add(menuSaveAs);

		menuSaveToAzure = new JMenuItem("Save to Azure");
		menuSaveToAzure.setEnabled(false);
		menuSaveToAzure.addActionListener(this);
		// TODO add accelerator
		menuFile.add(menuSaveToAzure);

		menuSaveAsTmx = new JMenuItem("Save As tmx");
		menuSaveAsTmx.setEnabled(false);
		menuSaveAsTmx.addActionListener(this);
		// TODO add accelerator
		menuFile.add(menuSaveAsTmx);

		menuProv = new JMenuItem("Provenance");
		menuProv.addActionListener(this);
		menuProv.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, getPlatformKeyMask()));
		menuFile.add(menuProv);

		menuWorkspace = new JMenuItem("Workspace");
		menuWorkspace.addActionListener(this);
		menuWorkspace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, getPlatformKeyMask()));
		menuFile.add(menuWorkspace);

		menuExit = new JMenuItem("Exit");
		menuExit.addActionListener(this);
		menuFile.add(menuExit);

		mnuEdit = new JMenu("Edit");
		menuFindReplace = new JMenuItem("Find and Replace");
        menuFindReplace.setEnabled(false);
		menuFindReplace.addActionListener(this);
		mnuEdit.add(menuFindReplace);
        menuSpellcheck = new JMenuItem("Spellcheck");
        menuSpellcheck.setEnabled(false);
        menuSpellcheck.addActionListener(this);
        mnuEdit.add(menuSpellcheck);
		menuBar.add(mnuEdit);

		menuView = new JMenu("View");
		menuBar.add(menuView);

		menuTgtDiff = new JCheckBoxMenuItem("Show Target Differences");
		menuTgtDiff.addActionListener(this);
		menuTgtDiff.setSelected(segmentView.getEnabledTargetDiff());
		menuView.add(menuTgtDiff);

		menuShowNotTrans = new JCheckBoxMenuItem("Show Not Translatable Segments");
		menuShowNotTrans.addActionListener(this);
		menuShowNotTrans.setSelected(configService.isShowNotTranslatableRows());
		menuView.add(menuShowNotTrans);

		menuColumns = new JMenuItem("Configure Columns");
		menuColumns.addActionListener(this);
		menuView.add(menuColumns);

		menuConfigTm = new JMenuItem("Configure TM");
		menuConfigTm.addActionListener(this);
		menuView.add(menuConfigTm);

		menuLqiGrid = new JMenuItem("LQI Grid");
		menuLqiGrid.addActionListener(this);
		menuView.add(menuLqiGrid);

		menuRules = new JMenuItem("Filters");
		menuRules.addActionListener(this);
		menuRules.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, getPlatformKeyMask()));
		menuView.add(menuRules);

		SegmentMenu segmentMenu = new SegmentMenu(eventQueue, getPlatformKeyMask(),
				lqiGridController.getConfigService());
		menuBar.add(segmentMenu.getMenu());
		this.eventQueue.registerListener(segmentMenu);

		menuExtensions = new JMenu("Extensions");
		buildExtensionsMenu();
		menuBar.add(menuExtensions);

		menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		menuAbout = new JMenuItem("About");
		menuAbout.addActionListener(this);
		menuHelp.add(menuAbout);

		platformSupport.setMenuMnemonics(menuFile, menuView, menuExtensions, menuHelp);
		mainframe.setJMenuBar(menuBar);
	}

	private void buildExtensionsMenu() {

		menuPlugins = new JMenuItem("Plugins");
		menuPlugins.addActionListener(this);
		menuExtensions.add(menuPlugins);
		List<JMenu> pluginMenuList = ocelotApp.getPluginMenuList(mainframe);
		for (JMenu menu : pluginMenuList) {
			menuExtensions.add(menu);
		}
	}

	@Subscribe
	public void notifyPluginAdded(PluginAddedEvent event) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				menuExtensions.removeAll();
				buildExtensionsMenu();
				repaint();
			}
		});
	}

	public static int getPlatformKeyMask() {
		return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	}

	private boolean isPlatformKeyDown(KeyEvent ke) {
		// For reasons that are mysterious to me, the value of
		// platformKeyMask isn't the same as the modifiers to a KeyEvent.
		return platformSupport.isPlatformKeyDown(ke);
	}

	@Override
	public void run() {
		mainframe = new JFrame(APPNAME);
		mainframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// TODO: cleanup
				handleApplicationExit();
			}
		});

		Toolkit kit = Toolkit.getDefaultToolkit();
		icon = kit.createImage(Ocelot.class.getResource("logo64.png"));
		mainframe.setIconImage(icon);

		initializeMenuBar();
		try {
			toolBar = new OcelotToolBar(this, lqiGridController.getConfigService().readLQIConfig(), eventQueue);
			toolBar.addPluginWidgets(ocelotApp.getPluginToolBarWidgets());
		} catch (TransferException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mainframe.getContentPane().add(toolBar, BorderLayout.NORTH);
		mainframe.getContentPane().add(this, BorderLayout.CENTER);

		// adding LQI Key listener
		LQIKeyEventHandler ocelotKeyEventHandler = new LQIKeyEventHandler(lqiGridController, mainframe.getRootPane());
		LQIKeyEventManager.getInstance().addKeyEventHandler(ocelotKeyEventHandler);
		LQIGridConfigurations lqiGrid = lqiGridController.readLQIGridConfiguration(mainframe);
		loadLQIKeyListener(null, lqiGrid.getActiveConfiguration());

		// Display the window
		Dimension userWindowSize = getUserDefinedWindowSize();
		if (userWindowSize != null) {
			mainframe.setMinimumSize(userWindowSize);
		}
		mainframe.pack();
		mainframe.setVisible(true);
		if (configService.isTmPanelVisible()) {
			tmConcordanceSplitPane.setDividerLocation(0.4);
		} else {
			tmConcordanceSplitPane.getLeftComponent().setMinimumSize(new Dimension());
			tmConcordanceSplitPane.setDividerLocation(0.0);
		}
		if (!configService.isAttributesViewVisible() && !configService.isDetailsViewVisible()) {
			mainSplitPane.getLeftComponent().setMinimumSize(new Dimension());
			mainSplitPane.setDividerLocation(0.0);
		} else if (!configService.isAttributesViewVisible() && configService.isDetailsViewVisible()) {
			segAttrSplitPane.getLeftComponent().setMinimumSize(new Dimension());
			segAttrSplitPane.setDividerLocation(0.0);
		} else if (configService.isAttributesViewVisible() && !configService.isDetailsViewVisible()) {
			segAttrSplitPane.getRightComponent().setMinimumSize(new Dimension());
			segAttrSplitPane.setDividerLocation(1.0);
		}
		addEditingListenerToTxtFields();
		profileManager.checkProfileAndPromptMessage(mainframe);
	}

	private Dimension getUserDefinedWindowSize() {
		String val = System.getProperty("ocelot.windowSize");
		if (val == null) {
			return null;
		}
		Matcher m = Pattern.compile("(\\d+)x(\\d+)").matcher(val);
		if (m.matches()) {
			LOG.info("Using user-defined window size {}", val);
			return new Dimension(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
		}
		LOG.warn("Ignoring unparsable ocelot.windowSize value '{}'", val);
		return null;
	}

	@Subscribe
	public void onProfileChanged(ProfileChangedEvent event) {
		restart();
	}

	@Subscribe
	public void handleLqiConfigSavedEvent(LQIConfigurationsChangedEvent event) {
		try {
			toolBar.setLQIConfigurations(event.getLqiGridSavedConfigurations());
			if (event.isActiveConfChanged()) {
				loadLQIKeyListener(event.getOldActiveConfiguration(),
						event.getLqiGridSavedConfigurations().getActiveConfiguration());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Subscribe
	public void handleNewLqiConfigSelected(LQIConfigurationSelectionChangedEvent event) {
		// TODO
		try {
			loadLQIKeyListener(event.getOldSelectedConfiguration(), event.getNewSelectedConfiguration());
			ocelotApp.saveLqiConfiguration(event.getNewSelectedConfiguration().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void loadLQIKeyListener(LQIGridConfiguration oldGridConfiguration,
			LQIGridConfiguration newLqiGridConfiguration) {
		if (oldGridConfiguration != null) {
			LQIKeyEventManager.getInstance().removeActions(oldGridConfiguration);
		}
		if (newLqiGridConfiguration != null) {
			LQIKeyEventManager.getInstance().load(newLqiGridConfiguration);
		}
	}

	private void restart() {

		try {
			close();
			startOcelot();
		} catch (Exception e) {
			LOG.error("Error while starting Ocelot.", e);
			e.printStackTrace();
		}
	}

	public static void startOcelot() throws IOException, InstantiationException, IllegalAccessException {
		Injector ocelotScope = Guice.createInjector(new OcelotModule());

		Ocelot ocelot = new Ocelot(ocelotScope);
		DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ocelot);

		try {
			if (ocelot.useNativeUI) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		SwingUtilities.invokeLater(ocelot);
	}

	public void close() {
		LQIKeyEventManager.destroy();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				mainframe.dispose();
				mainframe.setVisible(false);
			}
		});
	}

	private void quitOcelot() {
		close();
		System.exit(0);
	}

	private void addEditingListenerToTxtFields() {

		final FocusListener focusListener = new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				eventQueue.post(new OcelotEditingEvent(OcelotEditingEvent.Type.STOP_EDITING));
			}

			@Override
			public void focusGained(FocusEvent e) {
				eventQueue.post(new OcelotEditingEvent(OcelotEditingEvent.Type.START_EDITING));
			}
		};

		final ContainerListener containerListener = new ContainerListener() {

			@Override
			public void componentRemoved(ContainerEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentAdded(ContainerEvent e) {
				addListenersToComponents(e.getChild(), focusListener, this);
			}
		};

		addListenersToComponents(mainframe.getContentPane(), focusListener, containerListener);

	}

	private void addListenersToComponents(Component component, FocusListener focusListener,
			ContainerListener containerListener) {
		if (component instanceof JTextComponent) {
			component.addFocusListener(focusListener);
		} else if (component instanceof Container) {
			Container container = (Container) component;
			container.addContainerListener(containerListener);
			if (container.getComponentCount() > 0) {
				for (int i = 0; i < container.getComponentCount(); i++) {
					addListenersToComponents(container.getComponent(i), focusListener, containerListener);
				}
			}
		}

	}

	public boolean isEditing() {

		return segmentView.getTable().getEditorComponent() != null;
	}

	void showModelessDialog(ODialogPanel panel, String title) {
		JDialog dialog = new JDialog(mainframe, title);
		panel.setDialog(dialog);
		JButton defaultButton = panel.getDefaultButton();
		if (defaultButton != null) {
			dialog.getRootPane().setDefaultButton(defaultButton);
		}
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setIconImage(icon);
		panel.postInit();
		dialog.setVisible(true);
	}

	public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException {
		if (System.getProperty("log4j.configuration") == null) {
			PropertyConfigurator.configure(Ocelot.class.getResourceAsStream("/log4j.properties"));
		} else {
			PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
		}

		startOcelot();
	}

	// TODO
	@Override
	public boolean dispatchKeyEvent(KeyEvent ke) {
		if (ke.getID() == KeyEvent.KEY_PRESSED) {
			if (isPlatformKeyDown(ke) && ke.isShiftDown() && ke.getKeyCode() == KeyEvent.VK_TAB) {
				segmentAttrView.focusNextTab();

			} else if (isPlatformKeyDown(ke) && !ke.isShiftDown() && ke.getKeyCode() == KeyEvent.VK_TAB) {
				segmentView.requestFocusTable();

			}
		}
		return false;
	}

	public static class MyOwnFocusTraversalPolicy extends FocusTraversalPolicy {
		Vector<Component> order;

		public MyOwnFocusTraversalPolicy(Vector<Component> order) {
			this.order = new Vector<Component>(order.size());
			this.order.addAll(order);
		}

		public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
			int idx = (order.indexOf(aComponent) + 1) % order.size();
			return order.get(idx);
		}

		public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
			int idx = order.indexOf(aComponent) - 1;
			if (idx < 0) {
				idx = order.size() - 1;
			}
			return order.get(idx);
		}

		public Component getDefaultComponent(Container focusCycleRoot) {
			return order.get(0);
		}

		public Component getLastComponent(Container focusCycleRoot) {
			return order.lastElement();
		}

		public Component getFirstComponent(Container focusCycleRoot) {
			return order.get(0);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JComboBox<?> combo = (JComboBox<?>) e.getSource();
		if (combo != null) {
			if (combo.getName().equals(OcelotToolBar.SOURCE_FONT_TOOL_NAME)) {
				handleSourceFontChangedEvent();
			} else if (combo.getName().equals(OcelotToolBar.TARGET_FONT_TOOL_NAME)) {
				handleTargetFontChangedEvent();
			}
		}
	}

	/**
	 * Handles the event the selected target font changed. It applies the new
	 * selected font to both target columns.
	 */
	private void handleTargetFontChangedEvent() {

		final Font targetFont = toolBar.getSelectedTargetFont();
		if (targetFont != null) {
			segmentView.setTargetFont(targetFont);
		}
	}

	/**
	 * Handles the event the selected target font changed. It applies the new
	 * selected font to the source column.
	 */
	private void handleSourceFontChangedEvent() {

		final Font sourceFont = toolBar.getSelectedSourceFont();
		if (sourceFont != null) {
			segmentView.setSourceFont(sourceFont);
		}

	}

	@Subscribe
	public void handlePluginInstalled(NewPluginsInstalled event) {

		toolBar.addPluginWidgets(ocelotApp.getPluginToolBarWidgets());
	}

	private void setEnableStorage(OcelotJsonConfigService configService) {

		OcelotAzureConfig ocelotAzureConfiguration = configService.getOcelotAzureConfiguration();
		if (ocelotAzureConfiguration != null) {
			enableStorage = ocelotAzureConfiguration.isComplete();
			ocelotApp.enableSegmentErrorChecker(enableStorage);
		}
	}

	/**
	 * Handles the event save to Azure
	 */
	private void handleStoring(OcelotJsonConfigService configService) {

		OcelotAzureConfig ocelotAzureConfiguration = configService.getOcelotAzureConfiguration();

		if (ocelotAzureConfiguration != null) {

			File tempFile = null;
			try {
				if (ocelotAzureConfiguration.isComplete()) {
					LOG.debug("Checking if this file has already been saved to Azure...");
					boolean saveToAzure = true;
					if (ocelotApp.getSavedToAzure()) {
						int option = JOptionPane.showConfirmDialog(mainframe,
								"This file has already been uploaded to Azure. Do you want to upload it again?",
								"Save To Azure Confirmation", JOptionPane.YES_NO_OPTION);
						saveToAzure = option == JOptionPane.YES_OPTION;
					}

					if (saveToAzure && ocelotApp.checkEditedSegments(mainframe)) {
						tempFile = File.createTempFile("ocelot", "azure");
						ocelotApp.saveFile(tempFile);

						storageService = new AzureStorageService(ocelotAzureConfiguration.getSas(),
								ocelotAzureConfiguration.getBlobEndpoint(),
								ocelotAzureConfiguration.getQueueEndpoint());

						String fileId = UUID.randomUUID().toString();
						boolean uploadedFileToBlobStorage = storageService.uploadFileToBlobStorage(
								tempFile.getAbsolutePath(), "unprocessed", fileId, ocelotApp.getDefaultFileName());
						if (uploadedFileToBlobStorage) {
							LOG.debug("File with id " + fileId + " was uploaded to blob storage");

							PostUploadRequest postUploadRequest = Util.getPostUploadRequest(fileId);
							String json = Util.serializeToJson(postUploadRequest);
							LOG.debug("Post Upload Request for Storage Queue in json format is " + json);
							boolean messageSent = storageService.sendMessageToPostUploadQueue(json);
							if (!messageSent) {
								LOG.error("No message sent to Storage queue.");
							} else {
								LOG.info("Sent message to Storage queue.");
							}
							ocelotApp.savedToAzure();
							JOptionPane.showMessageDialog(mainframe, "File successfully saved to Azure.",
									"Save to Azure", JOptionPane.INFORMATION_MESSAGE);
						} else {
							LOG.error("File with id " + fileId + " was not uploaded to blob storage");
							JOptionPane.showMessageDialog(mainframe,
									"An error has occurred while saving the document to Azure. Please, try again.");
						}
					}
				}
			} catch (IOException e) {
				LOG.error("Error while saving the document.", e);
				JOptionPane.showMessageDialog(mainframe, "An error has occurred while saving the document.");
			} catch (ErrorAlertException e) {
				alertUser(e.title, e.body);
			} finally {
				if (tempFile != null) {
					tempFile.delete();
				}
			}
		}
	}

}
