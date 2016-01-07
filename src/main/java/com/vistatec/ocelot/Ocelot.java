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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Vector;

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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vistatec.ocelot.di.OcelotModule;
import com.vistatec.ocelot.events.ConfigTmRequestEvent;
import com.vistatec.ocelot.events.OcelotEditingEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.view.ProvenanceProfileView;
import com.vistatec.ocelot.lqi.LQIGridController;
import com.vistatec.ocelot.lqi.gui.LQIKeyEventHandler;
import com.vistatec.ocelot.lqi.gui.LQIKeyEventManager;
import com.vistatec.ocelot.plugins.PluginManagerView;
import com.vistatec.ocelot.rules.FilterView;
import com.vistatec.ocelot.segment.view.SegmentAttributeView;
import com.vistatec.ocelot.segment.view.SegmentView;
import com.vistatec.ocelot.ui.ODialogPanel;
import com.vistatec.ocelot.ui.OcelotToolBar;
import com.vistatec.ocelot.tm.gui.TmGuiManager;

/**
 * Main UI Thread class. Handles menu and file operations
 * 
 */
public class Ocelot extends JPanel implements Runnable, ActionListener,
        KeyEventDispatcher, ItemListener {
	/** Default serial ID */
	private static final long serialVersionUID = 1L;
	private static String APPNAME = "Ocelot";
	private Image icon;
	private static Logger LOG = Logger.getLogger(Ocelot.class);

	private JMenuBar menuBar;
	private JMenu menuFile, menuView, menuExtensions, menuHelp;
	private JMenuItem menuOpenXLIFF, menuExit, menuAbout, menuRules, menuProv,
	        menuSave, menuSaveAs;
	private JMenuItem menuPlugins;
	private JCheckBoxMenuItem menuTgtDiff;
	private JMenuItem menuColumns;
	private JMenuItem menuConfigTm;
	private JMenuItem menuSaveAsTmx;
	private JMenuItem menuLqiGrid;

    private OcelotToolBar toolBar;
	private JFrame mainframe;
	private JSplitPane mainSplitPane;
	private JSplitPane segAttrSplitPane;
	private JSplitPane tmConcordanceSplitPane;
	private SegmentAttributeView segmentAttrView;
	private DetailView itsDetailView;
	private SegmentView segmentView;
	private TmGuiManager tmGuiManager;

	private boolean useNativeUI = false;
	private final Color optionPaneBackgroundColor;

	private final Injector ocelotScope;
	private final OcelotEventQueue eventQueue;
	private final OcelotApp ocelotApp;
	private final LQIGridController lqiGridController;

	private PlatformSupport platformSupport;

	public Ocelot(Injector ocelotScope) throws IOException,
	        InstantiationException, IllegalAccessException {
		super(new BorderLayout());
		this.ocelotScope = ocelotScope;
		this.eventQueue = ocelotScope.getInstance(OcelotEventQueue.class);
		this.ocelotApp = ocelotScope.getInstance(OcelotApp.class);
		this.tmGuiManager = ocelotScope.getInstance(TmGuiManager.class);
		this.eventQueue.registerListener(tmGuiManager);
		this.lqiGridController = ocelotScope
		        .getInstance(LQIGridController.class);
		eventQueue.registerListener(ocelotApp);

		platformSupport = ocelotScope.getInstance(PlatformSupport.class);
		platformSupport.init(this);

		useNativeUI = Boolean.valueOf(System.getProperty("ocelot.nativeUI",
		        "false"));
		optionPaneBackgroundColor = (Color) UIManager
		        .get("OptionPane.background");

		SegmentView segView = ocelotScope.getInstance(SegmentView.class);
		eventQueue.registerListener(segView);

		SegmentAttributeView segAttrView = ocelotScope
		        .getInstance(SegmentAttributeView.class);
		eventQueue.registerListener(segAttrView);

		DetailView detailView = ocelotScope.getInstance(DetailView.class);
		eventQueue.registerListener(detailView);

		add(setupMainPane(segView, segAttrView, detailView));
	}

	private Component setupMainPane(SegmentView segView,
	        SegmentAttributeView segAttrView, DetailView detailView)
	        throws IOException, InstantiationException, IllegalAccessException {

		segmentView = segView;

		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		        setupSegAttrDetailPanes(segAttrView, detailView),
		        setupSegmentTmPanes());
		mainSplitPane.setOneTouchExpandable(true);

		return mainSplitPane;
	}

	private Component setupSegAttrDetailPanes(SegmentAttributeView segAttrView,
	        DetailView detailView) {
		Dimension segAttrSize = new Dimension(385, 280);
		itsDetailView = detailView;
		itsDetailView.setPreferredSize(segAttrSize);

		segmentAttrView = segAttrView;
		segmentAttrView.setMinimumSize(new Dimension(305, 280));
		segmentAttrView.setPreferredSize(segAttrSize);

		segAttrSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
		        segmentAttrView, itsDetailView);
		segAttrSplitPane.setOneTouchExpandable(true);

		return segAttrSplitPane;
	}

	private Component setupSegmentTmPanes() {

		tmConcordanceSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
		        tmGuiManager.getTmPanel(), segmentView);
		tmConcordanceSplitPane.setOneTouchExpandable(true);
		tmConcordanceSplitPane.addContainerListener(new ContainerListener() {
			@Override
			public void componentRemoved(ContainerEvent e) {

			}

			@Override
			public void componentAdded(ContainerEvent e) {
				tmConcordanceSplitPane.setDividerLocation(0.3);
			}
		});
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
		} else if (e.getSource() == this.menuRules) {
			showModelessDialog(ocelotScope.getInstance(FilterView.class),
			        "Filters");
		} else if (e.getSource() == this.menuPlugins) {
			showModelessDialog(
			        ocelotScope.getInstance(PluginManagerView.class),
			        "Plugin Manager");

		} else if (e.getSource() == this.menuProv) {
			ProvenanceProfileView userProfileView = ocelotScope
			        .getInstance(ProvenanceProfileView.class);
			this.eventQueue.registerListener(userProfileView);
			showModelessDialog(userProfileView, "Credentials");

		} else if (e.getSource() == this.menuExit) {
			handleApplicationExit();
		} else if (e.getSource() == this.menuSaveAs) {
			if (ocelotApp.hasOpenFile()) {
				File saveFile = promptSaveAs();
				if (saveFile != null && save(saveFile)) {
					setMainTitle(saveFile.getName());
				}
			}
		} else if (e.getSource().equals(menuSaveAsTmx)) {
			tmGuiManager.saveAsTmx(mainframe);
		} else if (e.getSource() == this.menuSave) {
			save(ocelotApp.getOpenFile());
		} else if (e.getSource() == this.menuTgtDiff) {
			this.segmentView
			        .setEnabledTargetDiff(this.menuTgtDiff.isSelected());
		} else if (e.getSource() == this.menuColumns) {
			showModelessDialog(new ColumnSelector(segmentView.getTableModel()),
			        "Configure Columns");
		} else if (e.getSource() == this.menuConfigTm) {
			eventQueue.post(new ConfigTmRequestEvent(mainframe));
		} else if (e.getSource() == this.menuLqiGrid) {
			lqiGridController.displayLQIGrid();
		}
	}

	private void promptOpenXLIFFFile() {
		FileDialog fd = new FileDialog(mainframe, "Open", FileDialog.LOAD);
		fd.setFilenameFilter(new XliffFileFilter());
		fd.setVisible(true);
		File detectVersion = getSelectedFile(fd);
		File sourceFile = getSelectedFile(fd);
		fd.dispose();

		if (sourceFile != null) {
			try {
				ocelotApp.openFile(sourceFile, detectVersion);
				this.setMainTitle(sourceFile.getName());
				segmentView.reloadTable();

				this.menuSave.setEnabled(true);
				this.menuSaveAs.setEnabled(true);
				this.menuSaveAsTmx.setEnabled(true);
				this.toolBar.loadFontsAndSizes(ocelotApp.getFileSourceLang(), ocelotApp.getFileTargetLang());
                this.toolBar.setSourceFont(segmentView.getSourceFont());
                this.toolBar.setTargetFont(segmentView.getTargetFont());
			} catch (FileNotFoundException ex) {
				LOG.error(
				        "Failed to parse file '" + sourceFile.getName() + "'",
				        ex);
			} catch (Exception e) {
				String errorMsg = "Could not open " + sourceFile.getName();
				LOG.error(errorMsg, e);
				alertUser("XLIFF Parsing Error",
				        errorMsg + ": " + e.getMessage());
			}
		}
	}

	private File promptSaveAs() {
		FileDialog fd = new FileDialog(mainframe, "Save As...", FileDialog.SAVE);
		fd.setVisible(true);
		File f = getSelectedFile(fd);
		fd.dispose();
		return f;
	}

	private File getSelectedFile(FileDialog fd) {
		return (fd.getFile() == null) ? null : new File(fd.getDirectory(),
		        fd.getFile());
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
		JOptionPane.showMessageDialog(mainframe, textArea, windowTitle,
		        JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Show the about dialog.
	 */
	public void showAbout() {
		showModelessDialog(new AboutDialog(icon), "About Ocelot");
	}

	/**
	 * Exit handler. This should prompt to save unsaved data.
	 */
	public void handleApplicationExit() {
		boolean canQuit = true;
		if (ocelotApp.isFileDirty()) {
			int rv = JOptionPane
			        .showConfirmDialog(
			                this,
			                "You have unsaved changes. Would you like to save before exiting?",
			                "Save Unsaved Changes",
			                JOptionPane.YES_NO_CANCEL_OPTION);
			if (rv == JOptionPane.YES_OPTION) {
				save(ocelotApp.getOpenFile());
			} else if (rv != JOptionPane.NO_OPTION) {
				canQuit = false;
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
		menuOpenXLIFF.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
		        getPlatformKeyMask()));
		menuFile.add(menuOpenXLIFF);

		menuSave = new JMenuItem("Save");
		menuSave.setEnabled(false);
		menuSave.addActionListener(this);
		menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
		        getPlatformKeyMask()));
		menuFile.add(menuSave);

		menuSaveAs = new JMenuItem("Save As...");
		menuSaveAs.setEnabled(false);
		menuSaveAs.addActionListener(this);
		menuSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
		        Event.SHIFT_MASK | getPlatformKeyMask()));
		menuFile.add(menuSaveAs);

		menuSaveAsTmx = new JMenuItem("Save As tmx");
		menuSaveAsTmx.setEnabled(false);
		menuSaveAsTmx.addActionListener(this);
		// TODO add accelerator
		menuFile.add(menuSaveAsTmx);

		menuProv = new JMenuItem("Profile");
		menuProv.addActionListener(this);
		menuProv.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
		        getPlatformKeyMask()));
		menuFile.add(menuProv);

		menuExit = new JMenuItem("Exit");
		menuExit.addActionListener(this);
		menuFile.add(menuExit);

		menuView = new JMenu("View");
		menuBar.add(menuView);

		menuTgtDiff = new JCheckBoxMenuItem("Show Target Differences");
		menuTgtDiff.addActionListener(this);
		menuTgtDiff.setSelected(segmentView.getEnabledTargetDiff());
		menuView.add(menuTgtDiff);
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
		menuRules.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
		        getPlatformKeyMask()));
		menuView.add(menuRules);

		SegmentMenu segmentMenu = new SegmentMenu(eventQueue,
		        getPlatformKeyMask());
		menuBar.add(segmentMenu.getMenu());
		this.eventQueue.registerListener(segmentMenu);

		menuExtensions = new JMenu("Extensions");
		menuBar.add(menuExtensions);

		menuPlugins = new JMenuItem("Plugins");
		menuPlugins.addActionListener(this);
		menuExtensions.add(menuPlugins);

		List<JMenu> pluginMenuList = ocelotApp.getPluginMenuList();
		for (JMenu menu : pluginMenuList) {
			menuExtensions.add(menu);
		}

		menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		menuAbout = new JMenuItem("About");
		menuAbout.addActionListener(this);
		menuHelp.add(menuAbout);

		platformSupport.setMenuMnemonics(menuFile, menuView, menuExtensions, menuHelp);
		mainframe.setJMenuBar(menuBar);
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
		toolBar = new OcelotToolBar(this);
        mainframe.getContentPane().add(toolBar, BorderLayout.NORTH);
        mainframe.getContentPane().add(this, BorderLayout.CENTER);
		
		//adding LQI Key listener
		LQIKeyEventHandler ocelotKeyEventHandler = new LQIKeyEventHandler(lqiGridController, mainframe.getRootPane());
		LQIKeyEventManager.getInstance().addKeyEventHandler(ocelotKeyEventHandler);
		LQIKeyEventManager.getInstance().load(lqiGridController.readLQIGridConfiguration());
		// Display the window
		Dimension userWindowSize = getUserDefinedWindowSize();
		if (userWindowSize != null) {
		    mainframe.setMinimumSize(userWindowSize);
		}
		mainframe.pack();
		mainframe.setVisible(true);
		lqiGridController.setOcelotMainFrame(mainframe);
		tmConcordanceSplitPane.setDividerLocation(0.4);
		addEditingListenerToTxtFields();
	}

    private Dimension getUserDefinedWindowSize() {
        String val = System.getProperty("ocelot.windowSize");
        if (val == null) {
            return null;
        }
        Matcher m = Pattern.compile("(\\d+)x(\\d+)").matcher(val);
        if (m.matches()) {
            LOG.info("Using user-defined window size " + val);
            return new Dimension(Integer.valueOf(m.group(1)),
                    Integer.valueOf(m.group(2)));
        }
        LOG.warn("Ignoring unparsable ocelot.windowSize value '" + val + "'");
        return null;
    }

	private void quitOcelot() {
		LQIKeyEventManager.destroy();
		mainframe.dispose();
		mainframe.setVisible(false);
		System.exit(0);
	}

	private void addEditingListenerToTxtFields() {

		final FocusListener focusListener = new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				eventQueue.post(new OcelotEditingEvent(
				        OcelotEditingEvent.STOP_EDITING));
			}

			@Override
			public void focusGained(FocusEvent e) {
				eventQueue.post(new OcelotEditingEvent(
				        OcelotEditingEvent.START_EDITING));
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

	private void addListenersToComponents(Component component,
	        FocusListener focusListener, ContainerListener containerListener ) {
		if (component instanceof JTextComponent) {
			component.addFocusListener(focusListener);
		} else if (component instanceof Container) {
			Container container = (Container) component;
			container.addContainerListener(containerListener);
			if (container.getComponentCount() > 0) {
				for (int i = 0; i < container.getComponentCount(); i++) {
					addListenersToComponents(container.getComponent(i),
							focusListener, containerListener);
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

	public static void main(String[] args) throws IOException,
	        IllegalAccessException, InstantiationException {
		if (System.getProperty("log4j.configuration") == null) {
			PropertyConfigurator.configure(Ocelot.class
			        .getResourceAsStream("/log4j.properties"));
		} else {
			PropertyConfigurator.configure(System
			        .getProperty("log4j.configuration"));
		}

		Injector ocelotScope = Guice.createInjector(new OcelotModule());

		Ocelot ocelot = new Ocelot(ocelotScope);
		DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager()
		        .addKeyEventDispatcher(ocelot);

		try {
			if (ocelot.useNativeUI) {
				UIManager.setLookAndFeel(UIManager
				        .getSystemLookAndFeelClassName());
			} else {
				UIManager.setLookAndFeel(UIManager
				        .getCrossPlatformLookAndFeelClassName());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		SwingUtilities.invokeLater(ocelot);
	}

	// TODO
	@Override
	public boolean dispatchKeyEvent(KeyEvent ke) {
		if (ke.getID() == KeyEvent.KEY_PRESSED) {
			if (isPlatformKeyDown(ke) && ke.isShiftDown()
			        && ke.getKeyCode() == KeyEvent.VK_TAB) {
				segmentAttrView.focusNextTab();

			} else if (isPlatformKeyDown(ke) && !ke.isShiftDown()
			        && ke.getKeyCode() == KeyEvent.VK_TAB) {
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

		public Component getComponentAfter(Container focusCycleRoot,
		        Component aComponent) {
			int idx = (order.indexOf(aComponent) + 1) % order.size();
			return order.get(idx);
		}

		public Component getComponentBefore(Container focusCycleRoot,
		        Component aComponent) {
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
        JComboBox<?> combo = (JComboBox<?>)e.getSource();
        if(combo != null ){
            if(combo.getName().equals(OcelotToolBar.SOURCE_FONT_TOOL_NAME) ){
                handleSourceFontChangedEvent();
            } else if (combo.getName().equals(OcelotToolBar.TARGET_FONT_TOOL_NAME) ){
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
}
