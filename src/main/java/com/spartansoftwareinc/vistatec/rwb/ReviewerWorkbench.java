package com.spartansoftwareinc.vistatec.rwb;

import com.spartansoftwareinc.plugins.PluginManagerView;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.NewLanguageQualityIssueView;
import com.spartansoftwareinc.vistatec.rwb.its.ProvenanceProfileView;
import com.spartansoftwareinc.vistatec.rwb.rules.FilterView;
import com.spartansoftwareinc.vistatec.rwb.segment.Segment;
import com.spartansoftwareinc.vistatec.rwb.segment.SegmentAttributeView;
import com.spartansoftwareinc.vistatec.rwb.segment.SegmentView;
import java.awt.BorderLayout;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.swing.JFileChooser;
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
public class ReviewerWorkbench extends JPanel implements Runnable, ActionListener, KeyEventDispatcher {
    /** Default serial ID */
    private static final long serialVersionUID = 1L;
    private static String APPNAME = "Reviewer's Workbench";
    private static Logger LOG = Logger.getLogger(ReviewerWorkbench.class);
    JFrame mainframe;

    JMenuBar menuBar;
    JMenu menuFile, menuFilter, menuExtensions, menuHelp;
    JMenuItem menuOpenHTML, menuOpenXLIFF, menuSplit, menuExit, menuAbout,
            menuRules, menuProv, menuSave, menuSaveAs, menuPlugins;

    JSplitPane mainSplitPane;
    JSplitPane segAttrSplitPane;
    SegmentAttributeView segmentAttrView;
    DetailView itsDetailView;
    SegmentView segmentView;
    OpenXLIFFView openXLIFFView;
    OpenHTMLView openHTMLView;

    private JFileChooser saveFileChooser;
    protected File openSrcFile, openTgtFile, saveSrcFile, saveTgtFile;

    public ReviewerWorkbench() throws IOException, InstantiationException, IllegalAccessException {
        super(new BorderLayout());
        itsDetailView = new DetailView();
        Dimension segAttrSize = new Dimension(385, 380);
        segmentAttrView = new SegmentAttributeView(itsDetailView);
        segmentAttrView.setMinimumSize(segAttrSize);
        segmentAttrView.setPreferredSize(segAttrSize);
        segAttrSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                segmentAttrView, itsDetailView);
        segAttrSplitPane.setOneTouchExpandable(true);

        Dimension segSize = new Dimension(500, 500);
        segmentView = new SegmentView(segmentAttrView);
        segmentView.setMinimumSize(segSize);

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                segAttrSplitPane, segmentView);
        mainSplitPane.setOneTouchExpandable(true);

        add(mainSplitPane);

        openHTMLView = new OpenHTMLView(this, segmentView);
        openXLIFFView = new OpenXLIFFView(this, segmentView);

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
            JOptionPane.showMessageDialog(this, APPNAME+", version " + 
                    Version.PROJECT_VERSION + "-" + Version.SOURCE_VERSION, 
                    "About", JOptionPane.INFORMATION_MESSAGE);

        } else if (e.getSource() == this.menuOpenHTML) {
            SwingUtilities.invokeLater(openHTMLView);

        } else if (e.getSource() == this.menuOpenXLIFF) {
            SwingUtilities.invokeLater(openXLIFFView);

        } else if (e.getSource() == this.menuRules) {
            FilterView rules = new FilterView(segmentView.getRuleConfig());
            SwingUtilities.invokeLater(rules);

        } else if (e.getSource() == this.menuPlugins) {
            PluginManagerView plugins = new PluginManagerView(segmentView.getPluginManager(), segmentView);
            SwingUtilities.invokeLater(plugins);

        } else if (e.getSource() == this.menuProv) {
            ProvenanceProfileView prov = null;
            try {
                prov = new ProvenanceProfileView();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
            if (prov != null) {
                SwingUtilities.invokeLater(prov);
            } else {
                System.err.println("Failed to instantiate provenance view");
            }

        } else if (e.getSource() == this.menuExit) {
            mainframe.dispose();
        } else if (e.getSource() == this.menuSaveAs
                || e.getSource() == this.menuSave) {
            if (segmentView.isHTML()) {
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
        }
    }

    private File promptSaveAs() {
        int returnVal = saveFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return saveFileChooser.getSelectedFile();
        }
        return null;
    }
    
    private boolean save() {
        try {
            if (segmentView.isHTML()) {
                segmentView.save(saveSrcFile, saveTgtFile);
            } else {
                segmentView.save(saveSrcFile);
            }
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

    private void initializeMenuBar() {
        menuBar = new JMenuBar();
        menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menuFile);

        menuOpenHTML = new JMenuItem("Open HTML");
        menuOpenHTML.addActionListener(this);
        menuOpenHTML.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK));
        menuFile.add(menuOpenHTML);

        menuOpenXLIFF = new JMenuItem("Open XLIFF");
        menuOpenXLIFF.addActionListener(this);
        menuOpenXLIFF.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
        menuFile.add(menuOpenXLIFF);

        menuSave = new JMenuItem("Save");
        menuSave.setEnabled(false);
        menuSave.addActionListener(this);
        menuSave.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        menuFile.add(menuSave);

        menuSaveAs = new JMenuItem("Save As...");
        menuSaveAs.setEnabled(false);
        menuSaveAs.addActionListener(this);
        menuSaveAs.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Event.SHIFT_MASK | Event.CTRL_MASK));
        menuFile.add(menuSaveAs);

        menuProv = new JMenuItem("Profile");
        menuProv.addActionListener(this);
        menuProv.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
        menuFile.add(menuProv);

        menuExit = new JMenuItem("Exit");
        menuExit.addActionListener(this);
        menuFile.add(menuExit);

        menuFilter = new JMenu("Filter");
        menuFilter.setMnemonic(KeyEvent.VK_T);
        menuBar.add(menuFilter);

        menuRules = new JMenuItem("Rules");
        menuRules.addActionListener(this);
        menuRules.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
        menuFilter.add(menuRules);

        menuExtensions = new JMenu("Extensions");
        menuBar.add(menuExtensions);

        menuPlugins = new JMenuItem("Plugins");
        menuPlugins.addActionListener(this);
        menuExtensions.add(menuPlugins);

        menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        menuAbout = new JMenuItem("About");
        menuAbout.addActionListener(this);
        menuHelp.add(menuAbout);

        mainframe.setJMenuBar(menuBar);
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

        saveFileChooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                File saveFile = getSelectedFile();
                if (saveFile.exists() && getDialogType() == SAVE_DIALOG) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Overwrite existing file?", "File exists",
                            JOptionPane.YES_NO_OPTION);
                    switch (confirm) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            break;

                        case JOptionPane.NO_OPTION:
                            break;

                        case JOptionPane.CLOSED_OPTION:
                            break;

                        default:
                            break;
                    }
                } else if (!saveFile.exists() && getDialogType() == SAVE_DIALOG) {
                    super.approveSelection();
                }
            }
        };
        initializeMenuBar();
        mainframe.getContentPane().add(this);

        // Display the window
        mainframe.pack();
        mainframe.setVisible(true);
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, InstantiationException, IllegalAccessException {
        if (System.getProperty("log4j.configuration") == null) {
            PropertyConfigurator.configure(ReviewerWorkbench.class.getResourceAsStream("/log4j.properties"));
        } else {
            PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
        }
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (InstantiationException e) {
            System.err.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
        ReviewerWorkbench r = new ReviewerWorkbench();
        SwingUtilities.invokeLater(r);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ke) {
        if (ke.getID() == KeyEvent.KEY_PRESSED) {
            if (ke.isControlDown() && (ke.getKeyCode() >= KeyEvent.VK_0
                    && ke.getKeyCode() <= KeyEvent.VK_9)) {
                Segment seg = segmentView.getSelectedSegment();
                LanguageQualityIssue lqi = segmentView.getRuleConfig().getQuickAddLQI(ke.getKeyCode() - KeyEvent.VK_0);
                if (seg != null && lqi != null) {
                    seg.addNewLQI(lqi);
                }

            } else if (ke.isControlDown() && ke.isShiftDown()
                    && ke.getKeyCode() == KeyEvent.VK_TAB) {
                segmentAttrView.focusNextTab();

            } else if (ke.isControlDown() && !ke.isShiftDown()
                    && ke.getKeyCode() == KeyEvent.VK_TAB) {
                segmentView.requestFocusTable();

            } else if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_EQUALS) {
                if (segmentView.getSelectedSegment() != null) {
                    NewLanguageQualityIssueView addLQIView = new NewLanguageQualityIssueView();
                    addLQIView.setSegment(segmentAttrView.getSelectedSegment());
                    SwingUtilities.invokeLater(addLQIView);
                }
            }
        }
        return false;
    }
}
