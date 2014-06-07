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

import com.vistatec.ocelot.config.AppConfig;
import com.vistatec.ocelot.config.Configs;
import com.vistatec.ocelot.config.DirectoryBasedConfigs;
import com.vistatec.ocelot.plugins.PluginManager;
import com.vistatec.ocelot.plugins.PluginManagerView;
import com.vistatec.ocelot.its.NewLanguageQualityIssueView;
import com.vistatec.ocelot.its.ProvenanceProfileView;
import com.vistatec.ocelot.rules.FilterView;
import com.vistatec.ocelot.rules.QuickAdd;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RulesParser;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentAttributeView;
import com.vistatec.ocelot.segment.SegmentController;
import com.vistatec.ocelot.segment.SegmentView;

import java.awt.BorderLayout;
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.SAXException;

/**
 * Main UI Thread class. Handles menu and file operations
 *
 */
public class Ocelot extends JPanel implements Runnable, ActionListener, KeyEventDispatcher {
    /** Default serial ID */
    private static final long serialVersionUID = 1L;
    private static String APPNAME = "Ocelot";
    private static Image icon;
    private static Logger LOG = Logger.getLogger(Ocelot.class);
    JFrame mainframe;

    JMenuBar menuBar;
    JMenu menuFile, menuView, menuFilter, menuExtensions, menuHelp;
    JMenuItem menuOpenHTML, menuOpenXLIFF, menuSplit, menuExit, menuAbout,
            menuRules, menuProv, menuSave, menuSaveAs;
    JMenuItem menuPlugins;
    JCheckBoxMenuItem menuTgtDiff;

    JSplitPane mainSplitPane;
    JSplitPane segAttrSplitPane;
    SegmentAttributeView segmentAttrView;
    DetailView itsDetailView;
    SegmentView segmentView;
    SegmentController segmentController;
    OpenHTMLView openHTMLView;

    protected File openSrcFile, openTgtFile, saveSrcFile, saveTgtFile;
    protected AppConfig config;
    private String platformOS;
    private boolean useNativeUI = false;
    private Configs configs;

    public Ocelot(Configs configs, AppConfig config, PluginManager pluginManager, 
                  RuleConfiguration ruleConfig)
            throws IOException, InstantiationException, IllegalAccessException {
        super(new BorderLayout());
        this.configs = configs;
        this.config = config;

        platformOS = System.getProperty("os.name");
        useNativeUI = Boolean.valueOf(System.getProperty("ocelot.nativeUI", "false"));
        
        Dimension segAttrSize = new Dimension(385, 280);
        itsDetailView = new DetailView();
        itsDetailView.setPreferredSize(segAttrSize);
        segmentAttrView = new SegmentAttributeView(itsDetailView);
        segmentAttrView.setMinimumSize(new Dimension(305, 280));
        segmentAttrView.setPreferredSize(segAttrSize);
        segAttrSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                segmentAttrView, itsDetailView);
        segAttrSplitPane.setOneTouchExpandable(true);
        
        Dimension segSize = new Dimension(500, 500);
        segmentController = new SegmentController(configs);

        segmentView = new SegmentView(segmentAttrView, segmentController, config,
                                      ruleConfig, pluginManager);
        segmentView.setMinimumSize(segSize);
        segmentController.setSegmentView(segmentView);

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                segAttrSplitPane, segmentView);
        mainSplitPane.setOneTouchExpandable(true);

        add(mainSplitPane);

        openHTMLView = new OpenHTMLView(this, segmentController);

        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
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

        } else if (e.getSource() == this.menuOpenHTML) {
            SwingUtilities.invokeLater(openHTMLView);

        } else if (e.getSource() == this.menuOpenXLIFF) {
            promptOpenXLIFFFile();

        } else if (e.getSource() == this.menuRules) {
            FilterView rules = new FilterView(segmentView.getRuleConfig(), icon);
            SwingUtilities.invokeLater(rules);

        } else if (e.getSource() == this.menuPlugins) {
            PluginManagerView plugins = new PluginManagerView(segmentView.getPluginManager(), segmentController, icon);
            SwingUtilities.invokeLater(plugins);

        } else if (e.getSource() == this.menuProv) {
            ProvenanceProfileView prov = null;
            try {
                prov = new ProvenanceProfileView(configs, icon);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
            if (prov != null) {
                SwingUtilities.invokeLater(prov);
            } else {
                System.err.println("Failed to instantiate provenance view");
            }

        } else if (e.getSource() == this.menuExit) {
            handleApplicationExit();
        } else if (e.getSource() == this.menuSaveAs
                || e.getSource() == this.menuSave) {
            if (segmentController.isHTML()) {
                if (openSrcFile != null
                        && openTgtFile != null) {
                    saveSrcFile = e.getSource() == this.menuSaveAs ?
                            promptSaveAs() : openSrcFile;
                    if (saveSrcFile != null) {
                        saveTgtFile = e.getSource() == this.menuSaveAs ?
                                promptSaveAs() : openTgtFile;
                        if (saveTgtFile != null) {
                            if (save() && e.getSource() == this.menuSaveAs) {
                                openSrcFile = saveSrcFile;
                                openTgtFile = saveTgtFile;
                                setMainTitle(saveSrcFile.getName(),
                                        saveTgtFile.getName());
                            }
                        }
                    }
                }
            } else {
                if (openSrcFile != null) {
                    saveSrcFile = e.getSource() == this.menuSaveAs ?
                            promptSaveAs() : openSrcFile;
                    if (saveSrcFile != null) {
                        if (save() && e.getSource() == this.menuSaveAs) {
                            openSrcFile = saveSrcFile;
                            setMainTitle(saveSrcFile.getName());
                        }
                    }
                }
            }
        } else if (e.getSource() == this.menuTgtDiff) {
            this.segmentController.setEnabledTargetDiff(this.menuTgtDiff.isSelected());
        }
    }

    private void promptOpenXLIFFFile() {
        FileDialog fd = new FileDialog(mainframe, "Open", FileDialog.LOAD);
        fd.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xlf");
            }
        });
        fd.setVisible(true);
        File sourceFile = getSelectedFile(fd);
        fd.dispose();
        if (sourceFile != null) {
            try {
                segmentController.parseXLIFFFile(sourceFile);
                this.openSrcFile = sourceFile;
                this.setMainTitle(sourceFile.getName());

                this.segmentView.getPluginManager().notifyOpenFile(sourceFile.getName());

                this.menuSave.setEnabled(true);
                this.menuSaveAs.setEnabled(true);
            } catch (FileNotFoundException ex) {
                LOG.error("Failed to parse file '" + sourceFile.getName() + "'", ex);
            } catch (Exception e) {
                String errorMsg = "Failed opening XLIFF File '"+sourceFile.getName()+"'";
                LOG.error(errorMsg, e);
                alertUser("XLIFF Parsing Error", errorMsg+" - "+e.getMessage());
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
    
    private boolean save() {
        try {
            String filename;
            if (segmentController.isHTML()) {
                filename = "Source file: '"+saveSrcFile.getName()
                        +", Target file: '"+saveTgtFile+"'";
                segmentController.save(saveSrcFile, saveTgtFile);
            } else {
                filename = saveSrcFile.getName();
                segmentController.save(saveSrcFile);
            }
            segmentView.getPluginManager().notifySaveFile(filename);
            return true;
        } catch (UnsupportedEncodingException ex) {
            LOG.error(ex);
        } catch (FileNotFoundException ex) {
            LOG.error(ex);
        } catch (IOException ex) {
            LOG.error(ex);
        }
        return false;
    }

    private void alertUser(String windowTitle, String message) {
        JOptionPane.showMessageDialog(mainframe, message, windowTitle, JOptionPane.ERROR_MESSAGE);
    }

    private void showAbout() {
        String javaVersion = System.getProperty("java.version");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        JOptionPane.showMessageDialog(this, 
                APPNAME + ", version " + Version.PROJECT_VERSION + 
                    " (Build " + Version.SOURCE_VERSION + ")" +
                    "\n" + platformOS + " " + osVersion + " (" + osArch + ")" +
                    "\nJava " + javaVersion,
                "About", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(icon));
    }

    /**
     * Exit handler.  This should prompt to save unsaved data.
     */
    private void handleApplicationExit() {
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

//        menuOpenHTML = new JMenuItem("Open HTML");
//        menuOpenHTML.addActionListener(this);
//        menuOpenHTML.setAccelerator(
//                KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK));
//        menuFile.add(menuOpenHTML);

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
        menuTgtDiff.setSelected(this.segmentController.enabledTargetDiff());
        menuView.add(menuTgtDiff);

        menuFilter = new JMenu("Filter");
        menuBar.add(menuFilter);

        menuRules = new JMenuItem("Rules");
        menuRules.addActionListener(this);
        menuRules.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_R, getPlatformKeyMask()));
        menuFilter.add(menuRules);

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

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, InstantiationException, IllegalAccessException {
        if (System.getProperty("log4j.configuration") == null) {
            PropertyConfigurator.configure(Ocelot.class.getResourceAsStream("/log4j.properties"));
        } else {
            PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
        }

        // XXX I don't like the fact that the values of these properties
        // aren't known by the running application itself, only main.
        File ocelotDir = new File(System.getProperty("user.home"), ".ocelot");
        ocelotDir.mkdirs();
        Configs configs = new DirectoryBasedConfigs(ocelotDir);
        AppConfig appConfig = new AppConfig(configs);

        RuleConfiguration ruleConfig = new RulesParser().loadConfig(configs.getRulesReader());
        PluginManager pluginManager = new PluginManager(appConfig, new File(ocelotDir, "plugins"));
        pluginManager.discover();

        Ocelot ocelot = new Ocelot(configs, appConfig, pluginManager, ruleConfig);

        try {
            if (ocelot.useNativeUI) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (InstantiationException e) {
            System.err.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
        SwingUtilities.invokeLater(ocelot);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ke) {
        if (ke.getID() == KeyEvent.KEY_PRESSED) {
            if (isPlatformKeyDown(ke) && (ke.getKeyCode() >= KeyEvent.VK_0
                    && ke.getKeyCode() <= KeyEvent.VK_9)) {
                Segment seg = segmentView.getSelectedSegment();
                QuickAdd qa = segmentView.getRuleConfig().getQuickAddLQI(ke.getKeyCode() - KeyEvent.VK_0);
                if (seg != null && qa != null && seg.isEditablePhase()) {
                    seg.addNewLQI(qa.getLQIData());
                }

            } else if (isPlatformKeyDown(ke) && ke.isShiftDown()
                    && ke.getKeyCode() == KeyEvent.VK_TAB) {
                segmentAttrView.focusNextTab();

            } else if (isPlatformKeyDown(ke) && !ke.isShiftDown()
                    && ke.getKeyCode() == KeyEvent.VK_TAB) {
                segmentView.requestFocusTable();

            } else if (isPlatformKeyDown(ke) && ke.getKeyCode() == KeyEvent.VK_EQUALS) {
                if (segmentView.getSelectedSegment() != null) {
                    NewLanguageQualityIssueView addLQIView = new NewLanguageQualityIssueView();
                    addLQIView.setSegment(segmentAttrView.getSelectedSegment());
                    SwingUtilities.invokeLater(addLQIView);
                }
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
