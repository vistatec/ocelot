package com.spartansoftwareinc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class ReviewerWorkbench extends JPanel implements Runnable, ActionListener {
    /** Default serial ID */
    private static final long serialVersionUID = 1L;
    JFrame mainframe;

    JMenuBar menuBar;
    JMenu menuFile, menuHelp;
    JMenuItem menuOpen, menuSplit, menuExit, menuAbout;

    JSplitPane mainSplitPane;
    JSplitPane segAttrSplitPane;
    SegmentAttributeView segmentAttrView;
    LanguageQualityIssueView lqiView;
    SegmentView segmentView;

    JFileChooser fc;
    File openFile;

    public ReviewerWorkbench() {
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.menuAbout) {
            JOptionPane.showMessageDialog(this, "Reviewer's Workbench, version " + Version.get(), "About", JOptionPane.INFORMATION_MESSAGE);

        } else if (e.getSource() == this.menuOpen) {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.openFile = fc.getSelectedFile();
                Thread t = new Thread(new OpenThread());
                t.start();
            }
        } else if (e.getSource() == this.menuExit) {
            mainframe.dispose();
        }
    }

    private void initializeMenuBar() {
        menuBar = new JMenuBar();
        menuFile = new JMenu("File");
        menuBar.add(menuFile);

        menuOpen = new JMenuItem("Open");
        menuOpen.addActionListener(this);
        menuFile.add(menuOpen);

        menuExit = new JMenuItem("Exit");
        menuExit.addActionListener(this);
        menuFile.add(menuExit);

        menuHelp = new JMenu("Help");
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

    class OpenThread implements Runnable {
        // TODO: Retrieve segment data and metadata from file

        public void run() {
            segmentView.parseSegmentsFromFile();
        }
    }
}