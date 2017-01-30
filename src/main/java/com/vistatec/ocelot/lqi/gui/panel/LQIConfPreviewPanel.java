package com.vistatec.ocelot.lqi.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import com.vistatec.ocelot.config.ConfigurationException;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.config.LqiJsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.lqi.gui.LQIGridTableHelper;
import com.vistatec.ocelot.lqi.gui.LQIGridTableModel;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

public class LQIConfPreviewPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5354525240763635796L;

	private LQIInfoPanel infoPanel;
	
//	private LQIGridTableContainer gridTable;
	
	private LQIGridTableHelper tableHelper;
	
	private JScrollPane tableContainer;
	
	public LQIConfPreviewPanel() {
		tableHelper = new LQIGridTableHelper(null);
		makePanel();
    }
	
	private void makePanel() {
		
		setSize(new Dimension(700, 250));
		setPreferredSize(new Dimension(700, 250));
		infoPanel = new LQIInfoPanel();
		infoPanel.setEditable(false);
		tableContainer = new JScrollPane();
		tableContainer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableContainer.setPreferredSize(new Dimension(700, 200));
		setLayout(new BorderLayout());
		add(infoPanel, BorderLayout.NORTH);
		add(tableContainer, BorderLayout.CENTER);
//		load(null);
//		tableContainer = new JScrollPane();
//		tableContainer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//		tableContainer.setPreferredSize(new Dimension(400, 250));
//		gridTable = new LQIGridTableContainer(new LQIGridConfiguration(),
//		        new LQIGridTableHelper(null), null,
//		        LQIGridTableContainer.PREVIEW_MODE);
		
	}
	
	public void load(LQIGridConfiguration lqiGridConf){
		
		if(lqiGridConf != null){
			infoPanel.load(lqiGridConf);
			JTable lqiTable = tableHelper.createLQIGridTable(lqiGridConf, LQIGridTableModel.ISSUES_ANNOTS_MODE, null);
			lqiTable.setEnabled(false);
			lqiTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableHelper.initColorForColumns();
			tableHelper.configureTable(null);
			tableContainer.setViewportView(lqiTable);
			add(infoPanel, BorderLayout.NORTH);
			add(tableContainer, BorderLayout.CENTER);
			
//			tableContainer.setViewportView(gridTable);
		} else {
//			tableContainer.setViewportView(null);
			remove(infoPanel);
			remove(tableContainer);
		}
		revalidate();
		
	}
	
	public static void main(String[] args) throws ConfigurationException, TransferException {
		ConfigurationManager confManager = new ConfigurationManager();
	        confManager.readAndCheckConfiguration(new File(System.getProperty("user.home"), ".ocelot"));
	        LqiJsonConfigService lqiConfService = confManager.getLqiConfigService();
	        LQIGridConfigurations lqiGrid = lqiConfService.readLQIConfig();
	        JFrame frame = new JFrame();
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        
	        LQIConfPreviewPanel previewPanel = new LQIConfPreviewPanel();
	        previewPanel.load(lqiGrid.getActiveConfiguration());
//	        JScrollPane scrollPane = new JScrollPane(previewPanel);
//	        scrollPane.setPreferredSize(new Dimension(400, 200));
	        frame.add(previewPanel);
	        frame.pack();
	        
//	        previewPanel.load(lqiGridConf);
	        
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
    }

	public void clear() {
	    System.out.println("Clear preview");
		load(null);
    }

}
