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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.vistatec.ocelot.di.OcelotModule;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.view.ProvenanceProfileView;
import com.vistatec.ocelot.plugins.PluginManagerView;
import com.vistatec.ocelot.rules.FilterView;
import com.vistatec.ocelot.rules.QuickAddView;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.view.SegmentAttributeView;
import com.vistatec.ocelot.segment.view.SegmentView;
import com.vistatec.ocelot.ui.ODialogPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Main UI Thread class. Handles menu and file operations
 *
 */
public class Ocelot extends JPanel implements Runnable, ActionListener, KeyEventDispatcher {
    /** Default serial ID */
    private static final long serialVersionUID = 1L;
    private static String APPNAME = "Ocelot";
    private Image icon;
    private static Logger LOG = Logger.getLogger(Ocelot.class);

    private JMenuBar menuBar;
    private JMenu menuFile, menuView, menuFilter, menuExtensions, menuHelp;
    private JMenuItem menuOpenXLIFF, menuExit, menuAbout,
            menuRules, menuProv, menuSave, menuSaveAs, menuQuickAdd;
    private JMenuItem menuPlugins;
    private JCheckBoxMenuItem menuTgtDiff;
    private JMenuItem menuColumns;

    private JFrame mainframe;
    private JSplitPane mainSplitPane;
    private JSplitPane segAttrSplitPane;
    private SegmentAttributeView segmentAttrView;
    private DetailView itsDetailView;
    private SegmentView segmentView;

    private final String platformOS;
    private boolean useNativeUI = false;
    private final Color optionPaneBackgroundColor;

    private final Injector ocelotScope;
    private final OcelotEventQueue eventQueue;
    private final OcelotApp ocelotApp;

    public Ocelot(Injector ocelotScope) throws IOException, InstantiationException, IllegalAccessException {
        super(new BorderLayout());
        this.ocelotScope = ocelotScope;
        this.eventQueue = ocelotScope.getInstance(OcelotEventQueue.class);
        this.ocelotApp = ocelotScope.getInstance(OcelotApp.class);
        eventQueue.registerListener(ocelotApp);

        platformOS = System.getProperty("os.name");
        useNativeUI = Boolean.valueOf(System.getProperty("ocelot.nativeUI", "false"));
        optionPaneBackgroundColor = (Color)UIManager.get("OptionPane.background");

        SegmentView segView = ocelotScope.getInstance(SegmentView.class);
        eventQueue.registerListener(segView);

        SegmentAttributeView segAttrView = ocelotScope.getInstance(SegmentAttributeView.class);
        eventQueue.registerListener(segAttrView);

        DetailView detailView = ocelotScope.getInstance(DetailView.class);
        eventQueue.registerListener(detailView);

        add(setupMainPane(segView, segAttrView, detailView));
    }

    private Component setupMainPane(SegmentView segView,
            SegmentAttributeView segAttrView, DetailView detailView) throws IOException, InstantiationException, IllegalAccessException {
        Dimension segSize = new Dimension(500, 500);

        segmentView = segView;
        segmentView.setMinimumSize(segSize);

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                setupSegAttrDetailPanes(segAttrView, detailView), segmentView);
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

    public void setMainTitle(String sourceTitle) {
        mainframe.setTitle(APPNAME+" - "+sourceTitle);
    }

    public void setMainTitle(String sourceTitle, String targetTitle) {
        mainframe.setTitle(APPNAME+" - "+sourceTitle+", "+targetTitle);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.menuAbout) {
            showAbout();

        } else if (e.getSource() == this.menuOpenXLIFF) {
            promptOpenXLIFFFile();
        } else if (e.getSource() == this.menuRules) {
            showModelessDialog(ocelotScope.getInstance(FilterView.class), "Filters");
        } else if (e.getSource() == this.menuQuickAdd) {
            showModelessDialog(ocelotScope.getInstance(QuickAddView.class), "QuickAdd Rules");
        } else if (e.getSource() == this.menuPlugins) {
            showModelessDialog(ocelotScope.getInstance(PluginManagerView.class), "Plugin Manager");

        } else if (e.getSource() == this.menuProv) {
            ProvenanceProfileView userProfileView = ocelotScope.getInstance(ProvenanceProfileView.class);
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
        } else if (e.getSource() == this.menuSave) {
            save(ocelotApp.getOpenFile());
        } else if (e.getSource() == this.menuTgtDiff) {
            this.segmentView.setEnabledTargetDiff(this.menuTgtDiff.isSelected());
        }
        else if (e.getSource() == this.menuColumns) {
            showModelessDialog(new ColumnSelector(segmentView.getTableModel()), "Configure Columns");
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
            } catch (FileNotFoundException ex) {
                LOG.error("Failed to parse file '" + sourceFile.getName() + "'", ex);
            } catch (Exception e) {
                String errorMsg = "Could not open " + sourceFile.getName();
                LOG.error(errorMsg, e);
                alertUser("XLIFF Parsing Error", errorMsg + ": " + e.getMessage());
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
        return (fd.getFile() == null) ? null :
                new File(fd.getDirectory(), fd.getFile());
    }

    private boolean save(File saveFile) {
        try {
            ocelotApp.saveFile(saveFile);

        } catch (OcelotApp.ErrorAlertException ex) {
            alertUser(ex.title, ex.body);
            return false;

        } catch (Exception e) {
            LOG.error("Failed to save file: '"+saveFile.getName()+"'", e);
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
        // This is a hack for the mac, where textarea defaults to a white background
        // but JOptionPane defaults to a non-white background.
        textArea.setBackground(optionPaneBackgroundColor);
        textArea.setSize(textArea.getPreferredSize().width, 1);
        JOptionPane.showMessageDialog(mainframe, textArea,
                windowTitle, JOptionPane.ERROR_MESSAGE);
    }

    private void showAbout() {
        showModelessDialog(new AboutDialog(icon), "About Ocelot");
    }

    /**
     * Exit handler.  This should prompt to save unsaved data.
     */
    private void handleApplicationExit() {
        if (ocelotApp.isFileDirty()) {
            int rv = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Would you like to save before exiting?",
                    "Save Unsaved Changes",
                    JOptionPane.YES_NO_OPTION);
            if (rv == JOptionPane.YES_OPTION) {
                save(ocelotApp.getOpenFile());
            }
        }
        mainframe.dispose();
    }

    /**
     * Set menu mnemonics for non-Mac platforms.  (Mnemonics
     * violate the Mac interface guidelines.)
     */
    private void setMenuMnemonics() {
        if (!isMac()) {
            menuFile.setMnemonic(KeyEvent.VK_F);
            menuView.setMnemonic(KeyEvent.VK_V);
            menuFilter.setMnemonic(KeyEvent.VK_T);
            menuHelp.setMnemonic(KeyEvent.VK_H);
        }
    }

    private void initializeMenuBar() {
        menuBar = new JMenuBar();
        menuFile = new JMenu("File");
        menuBar.add(menuFile);

        menuOpenXLIFF = new JMenuItem("Open XLIFF");
        menuOpenXLIFF.addActionListener(this);
        menuOpenXLIFF.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, getPlatformKeyMask()));
        menuFile.add(menuOpenXLIFF);

        menuSave = new JMenuItem("Save");
        menuSave.setEnabled(false);
        menuSave.addActionListener(this);
        menuSave.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, getPlatformKeyMask()));
        menuFile.add(menuSave);

        menuSaveAs = new JMenuItem("Save As...");
        menuSaveAs.setEnabled(false);
        menuSaveAs.addActionListener(this);
        menuSaveAs.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Event.SHIFT_MASK | getPlatformKeyMask()));
        menuFile.add(menuSaveAs);

        menuProv = new JMenuItem("Profile");
        menuProv.addActionListener(this);
        menuProv.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_P, getPlatformKeyMask()));
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

        menuFilter = new JMenu("Filter");
        menuBar.add(menuFilter);

        menuRules = new JMenuItem("Rules");
        menuRules.addActionListener(this);
        menuRules.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_R, getPlatformKeyMask()));
        menuFilter.add(menuRules);

        menuQuickAdd = new JMenuItem("QuickAdd");
        menuQuickAdd.addActionListener(this);
        menuFilter.add(menuQuickAdd);

        SegmentMenu segmentMenu = new SegmentMenu(eventQueue, getPlatformKeyMask());
        menuBar.add(segmentMenu.getMenu());
        this.eventQueue.registerListener(segmentMenu);
        
        menuExtensions = new JMenu("Extensions");
        menuBar.add(menuExtensions);

        menuPlugins = new JMenuItem("Plugins");
        menuPlugins.addActionListener(this);
        menuExtensions.add(menuPlugins);

        menuHelp = new JMenu("Help");
        menuBar.add(menuHelp);

        menuAbout = new JMenuItem("About");
        menuAbout.addActionListener(this);
        menuHelp.add(menuAbout);

        setMenuMnemonics();
        setMacSpecific();

        mainframe.setJMenuBar(menuBar);
    }

    private int getPlatformKeyMask() {
        return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    }

    private boolean isPlatformKeyDown(KeyEvent ke) {
        // For reasons that are mysterious to me, the value of
        // platformKeyMask isn't the same as the modifiers to a KeyEvent.
        return isMac() ? ke.isMetaDown() : ke.isControlDown(); 
    }

    boolean isMac() {
        return (platformOS.startsWith("Mac"));
    }
    
    @Override
    public void run() {
        mainframe = new JFrame(APPNAME);
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // TODO: cleanup
            }
        });

        Toolkit kit = Toolkit.getDefaultToolkit();
        icon = kit.createImage(Ocelot.class.getResource("logo64.png"));
        mainframe.setIconImage(icon);

        initializeMenuBar();
        mainframe.getContentPane().add(this);

        // Display the window
        mainframe.pack();
        mainframe.setVisible(true);
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

        Injector ocelotScope = Guice.createInjector(new OcelotModule());

        Ocelot ocelot = new Ocelot(ocelotScope);
        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ocelot);

        try {
            if (ocelot.useNativeUI) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        SwingUtilities.invokeLater(ocelot);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ke) {
        if (ke.getID() == KeyEvent.KEY_PRESSED) {
            if (isPlatformKeyDown(ke) && (ke.getKeyCode() >= KeyEvent.VK_0
                    && ke.getKeyCode() <= KeyEvent.VK_9)) {
                OcelotSegment seg = segmentView.getSelectedSegment();
                int hotkey = ke.getKeyCode() - KeyEvent.VK_0;
                ocelotApp.quickAddLQI(seg, hotkey);

            } else if (isPlatformKeyDown(ke) && ke.isShiftDown()
                    && ke.getKeyCode() == KeyEvent.VK_TAB) {
                segmentAttrView.focusNextTab();

            } else if (isPlatformKeyDown(ke) && !ke.isShiftDown()
                    && ke.getKeyCode() == KeyEvent.VK_TAB) {
                segmentView.requestFocusTable();

            }
        }
        return false;
    }

    /**
     * Perform Mac OSX-specific platform initialization.
     */
    private void setMacSpecific() {
        if (isMac()) {
            OSXPlatformSupport.init();
            OSXPlatformSupport.setQuitHandler(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleApplicationExit();
                }
            });
            OSXPlatformSupport.setAboutHandler(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAbout();
                }
            });
        }
    }
}
