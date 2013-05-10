package com.spartansoftwareinc;

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
    private static Logger LOG = Logger.getLogger(ReviewerWorkbench.class);
    JFrame mainframe;

    JMenuBar menuBar;
    JMenu menuFile, menuFilter, menuHelp;
    JMenuItem menuOpenHTML, menuOpenXLIFF, menuSplit, menuExit, menuAbout,
            menuRules, menuProv, menuSave, menuSaveAs;

    JSplitPane mainSplitPane;
    JSplitPane segAttrSplitPane;
    SegmentAttributeView segmentAttrView;
    ITSDetailView itsDetailView;
    SegmentView segmentView;
    OpenXLIFFView openXLIFFView;
    OpenHTMLView openHTMLView;

    private JFileChooser fc;
    protected File openSrcFile, openTgtFile, saveSrcFile, saveTgtFile;

    public ReviewerWorkbench() throws IOException, InstantiationException, IllegalAccessException {
        super(new BorderLayout());
        itsDetailView = new ITSDetailView();
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

    public void setMainTitle(String title) {
        mainframe.setTitle(title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.menuAbout) {
            JOptionPane.showMessageDialog(this, "Reviewer's Workbench, version " + Version.get(), "About", JOptionPane.INFORMATION_MESSAGE);

        } else if (e.getSource() == this.menuOpenHTML) {
            SwingUtilities.invokeLater(openHTMLView);

        } else if (e.getSource() == this.menuOpenXLIFF) {
            SwingUtilities.invokeLater(openXLIFFView);

        } else if (e.getSource() == this.menuRules) {
            FilterView rules = new FilterView(segmentView);
            SwingUtilities.invokeLater(rules);

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
                        }
                    }
                }
            }
        }
    }

    private File promptSaveAs() {
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
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

        menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        menuAbout = new JMenuItem("About");
        menuAbout.addActionListener(this);
        menuHelp.add(menuAbout);

        mainframe.setJMenuBar(menuBar);
    }

    public void run() {
        mainframe = new JFrame("Reviewer's Workbench");
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // TODO: cleanup
            }
        });

        fc = new JFileChooser();
        initializeMenuBar();
        mainframe.getContentPane().add(this);

        // Display the window
        mainframe.pack();
        mainframe.setVisible(true);
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, InstantiationException, IllegalAccessException {
        if (System.getProperty("log4j.configuration") == null) {
            PropertyConfigurator.configure(ReviewerWorkbench.class.getResourceAsStream("log4j.properties"));
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
                LanguageQualityIssue lqi = segmentView.ruleConfig.getQuickAddLQI(ke.getKeyCode() - KeyEvent.VK_0);
                if (seg != null && lqi != null) {
                    seg.addNewLQI(lqi);
                }

            } else if (ke.isControlDown() && ke.isShiftDown()
                    && ke.getKeyCode() == KeyEvent.VK_TAB) {
                int selectedTab = segmentAttrView.getSelectedIndex();
                selectedTab = selectedTab+1 == segmentAttrView.getTabCount() ? 0 : selectedTab+1;
                segmentAttrView.setSelectedIndex(selectedTab);
                segmentAttrView.getComponentAt(selectedTab).requestFocus();

            } else if (ke.isControlDown() && !ke.isShiftDown()
                    && ke.getKeyCode() == KeyEvent.VK_TAB) {
                segmentView.sourceTargetTable.requestFocus();

            } else if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_EQUALS) {
                segmentAttrView.setSelectedIndex(1);
                segmentAttrView.addLQIView.typeList.requestFocus();
            }
        }
        return false;
    }
}