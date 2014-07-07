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

import com.vistatec.ocelot.segment.SegmentController;
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
 * @deprecated This class is not maintained.
 */
public class OpenHTMLView extends JPanel implements Runnable, ActionListener {
    private static final long serialVersionUID = 1L;

    private JFrame frame;
    private Ocelot rw;
    private SegmentController segmentController;
    private JButton selectSource, selectTarget, importFiles, close;
    File sourceFile, targetFile;
    JFileChooser fileChooser;

    public OpenHTMLView(Ocelot rw, SegmentController segmentController) {
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
            /*
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
        */
        }
    }
}
