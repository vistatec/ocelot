package com.spartansoftwareinc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
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
	JMenu menuFile;
	JMenuItem menuOpen, menuAbout;
	JScrollPane mainScroll;
	JTable sourceTargetTable;
	JTextArea textArea;
	JFileChooser fc;
	File openFile;
	
	public ReviewerWorkbench() {
		super(new BorderLayout());
		textArea = new JTextArea(24,80);
		mainScroll = new JScrollPane(textArea);
		add(mainScroll);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.menuAbout) {
			JOptionPane.showMessageDialog(this, "Reviewer's Workbench, version "+Version.get(), "About", JOptionPane.INFORMATION_MESSAGE);

		} else if (e.getSource() == this.menuOpen) {
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.openFile = fc.getSelectedFile();
				Thread t = new Thread(new OpenThread());
				t.start();
			}
		}
	}
	
	private void initializeMenuBar() {
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		menuOpen = new JMenuItem("Open");
		menuOpen.addActionListener(this);
		menuFile.add(menuOpen);
		
		menuAbout = new JMenuItem("About");
		menuAbout.addActionListener(this);
		menuBar.add(menuAbout);
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
		ReviewerWorkbench r = new ReviewerWorkbench();
		SwingUtilities.invokeLater(r);
	}
	
	class OpenThread implements Runnable {
		// TODO: Retrieve segment data and metadata from file
		public void run() {
			String[] columnLabels = {"Source", "Target"};
			Object[][] data = {
					{"heat sink", "dissipateur de chaleur"},
					{"heat sink", "dissipation thermique"}
			};
			
			sourceTargetTable = new JTable(data, columnLabels);
			sourceTargetTable.addMouseListener(new TableClickHandler());
			mainScroll.setViewportView(sourceTargetTable);
		}
	}
	
	class TableClickHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
//			System.out.println(sourceTargetTable.rowAtPoint(e.getPoint()));
			ContextMenu menu = new ContextMenu();
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
