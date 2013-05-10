package com.spartansoftwareinc;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * View for importing an XLIFF file into the workbench.
 */
public class OpenXLIFFView extends JPanel implements Runnable, ActionListener {
    private JFrame frame;
    private ReviewerWorkbench rw;
    private SegmentView segmentView;
    private JButton selectSource, importFiles, close;
    File sourceFile;
    JFileChooser fileChooser;

    public OpenXLIFFView(ReviewerWorkbench rw, SegmentView segmentView) {
        super(new GridLayout(0,2));
        this.rw = rw;
        this.segmentView = segmentView;
        fileChooser = new JFileChooser();
        setBorder(new EmptyBorder(10,10,10,10));

        add(new JLabel("File:"));
        selectSource = new JButton("XLIFF File");
        selectSource.addActionListener(this);
        add(selectSource);

        importFiles = new JButton("Import");
        importFiles.addActionListener(this);
        add(importFiles);

        close = new JButton("Close");
        close.addActionListener(this);
        add(close);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == selectSource) {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                sourceFile = fileChooser.getSelectedFile();
                String s = sourceFile.getName();
                if (s.equals(sourceFile.getName())) {
                    selectSource.setText(sourceFile.getName());
                }
            }
        } else if (ae.getSource() == importFiles && sourceFile != null) {
            Thread t = new Thread(new OpenThread());
            t.start();
            frame.dispose();
        } else if (ae.getSource() == close) {
            frame.dispose();
        }
    }

    @Override
    public void run() {
        frame = new JFrame("Open Files...");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.getContentPane().add(this);

        frame.pack();
        frame.setVisible(true);
    }

    class OpenThread implements Runnable {

        @Override
        public void run() {
            try {
                segmentView.parseSegmentsFromXLIFFFile(sourceFile);
                rw.openSrcFile = sourceFile;
                rw.setMainTitle(sourceFile.getName());
                rw.menuSave.setEnabled(true);
                rw.menuSaveAs.setEnabled(true);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
