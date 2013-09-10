package com.spartansoftwareinc.vistatec.rwb;

import com.spartansoftwareinc.vistatec.rwb.segment.SegmentController;
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
 * View for importing aligned files into the workbench.
 */
public class OpenHTMLView extends JPanel implements Runnable, ActionListener {
    private JFrame frame;
    private ReviewerWorkbench rw;
    private SegmentController segmentController;
    private JButton selectSource, selectTarget, importFiles, close;
    File sourceFile, targetFile;
    JFileChooser fileChooser;

    public OpenHTMLView(ReviewerWorkbench rw, SegmentController segmentController) {
        super(new GridLayout(0,2));
        this.rw = rw;
        this.segmentController = segmentController;
        fileChooser = new JFileChooser();
        setBorder(new EmptyBorder(10,10,10,10));

        add(new JLabel("Source:"));
        selectSource = new JButton("Source File");
        selectSource.addActionListener(this);
        add(selectSource);

        add(new JLabel("Target:"));
        selectTarget = new JButton("Target File");
        selectTarget.addActionListener(this);
        add(selectTarget);

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
        } else if (ae.getSource() == selectTarget) {
            int returnVal = fileChooser.showOpenDialog(this); 
            if (returnVal == JFileChooser.APPROVE_OPTION) { 
                targetFile = fileChooser.getSelectedFile();
                selectTarget.setText(targetFile.getName());
            }
        } else if (ae.getSource() == importFiles && sourceFile != null && targetFile != null) {
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
                segmentController.parseHTML5Files(sourceFile, targetFile);
                rw.openSrcFile = sourceFile;
                rw.openTgtFile = targetFile;
                rw.setMainTitle(sourceFile.getName(), targetFile.getName());
                rw.segmentView.getPluginManager().notifyOpenFile(
                        "Source File: "+sourceFile.getName()+
                        ", Target File: "+targetFile.getName());
                rw.menuSave.setEnabled(true);
                rw.menuSaveAs.setEnabled(true);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
