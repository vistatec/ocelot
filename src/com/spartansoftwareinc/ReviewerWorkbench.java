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
import java.io.IOException;

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

import org.xml.sax.SAXException;

/**
 * Main UI Thread class. Handles menu and file operations
 *
 */
public class ReviewerWorkbench extends JPanel implements Runnable, ActionListener, KeyEventDispatcher {
    /** Default serial ID */
    private static final long serialVersionUID = 1L;
    JFrame mainframe;

    JMenuBar menuBar;
    JMenu menuFile, menuFilter, menuHelp;
    JMenuItem menuOpen, menuSplit, menuExit, menuAbout, menuRules;

    JSplitPane mainSplitPane;
    JSplitPane segAttrSplitPane;
    SegmentAttributeView segmentAttrView;
    LanguageQualityIssueView lqiView;
    SegmentView segmentView;

    JFileChooser fc;
    File openFile;

    public ReviewerWorkbench() throws IOException {
        super(new BorderLayout());
        lqiView = new LanguageQualityIssueView();
        Dimension segAttrSize = new Dimension(385, 380);
        segmentAttrView = new SegmentAttributeView(lqiView);
        segmentAttrView.setMinimumSize(segAttrSize);
        segmentAttrView.setPreferredSize(segAttrSize);
        segAttrSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                segmentAttrView, lqiView);
        segAttrSplitPane.setOneTouchExpandable(true);

        Dimension segSize = new Dimension(500, 500);
        segmentView = new SegmentView(segmentAttrView);
        segmentView.setMinimumSize(segSize);

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                segAttrSplitPane, segmentView);
        mainSplitPane.setOneTouchExpandable(true);

        add(mainSplitPane);

        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.menuAbout) {
            JOptionPane.showMessageDialog(this, "Reviewer's Workbench, version " + Version.get(), "About", JOptionPane.INFORMATION_MESSAGE);

        } else if (e.getSource() == this.menuOpen) {
            OpenView open = new OpenView(segmentView);
            SwingUtilities.invokeLater(open);

        } else if (e.getSource() == this.menuRules) {
            FilterView rules = new FilterView(segmentView);
            SwingUtilities.invokeLater(rules);

        } else if (e.getSource() == this.menuExit) {
            mainframe.dispose();
        }
    }

    private void initializeMenuBar() {
        menuBar = new JMenuBar();
        menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menuFile);

        menuOpen = new JMenuItem("Open");
        menuOpen.addActionListener(this);
        menuOpen.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
        menuFile.add(menuOpen);

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

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        try {
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
            if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_1) {
                segmentAttrView.setSelectedIndex(0);
                segmentAttrView.tree.requestFocus();
            } else if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_2) {
                segmentView.sourceTargetTable.requestFocus();
            } else if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_EQUALS) {
                segmentAttrView.setSelectedIndex(1);
                segmentAttrView.addLQIView.typeList.requestFocus();
            }
        }
        return false;
    }
}