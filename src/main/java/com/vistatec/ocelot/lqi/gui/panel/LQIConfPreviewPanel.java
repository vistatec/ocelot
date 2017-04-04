package com.vistatec.ocelot.lqi.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import com.vistatec.ocelot.lqi.gui.LQIGridTableHelper;
import com.vistatec.ocelot.lqi.gui.LQIGridTableModel;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

/**
 * This panel displays a preview of an existing configuration of the LQI Grid.
 */
public class LQIConfPreviewPanel extends JPanel {

	private static final long serialVersionUID = -5354525240763635796L;

	private LQIInfoPanel infoPanel;

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
		tableContainer
		        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableContainer.setPreferredSize(new Dimension(700, 200));
		setLayout(new BorderLayout());
		add(infoPanel, BorderLayout.NORTH);
		add(tableContainer, BorderLayout.CENTER);

	}

	public void load(LQIGridConfiguration lqiGridConf) {

		if (lqiGridConf != null) {
			infoPanel.load(lqiGridConf);
			JTable lqiTable = tableHelper.createLQIGridTable(lqiGridConf,
			        LQIGridTableModel.ISSUES_ANNOTS_MODE, null);
			lqiTable.setEnabled(false);
			lqiTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableHelper.initColorForColumns();
			tableHelper.configureTable(null);
			tableContainer.setViewportView(lqiTable);
			add(infoPanel, BorderLayout.NORTH);
			add(tableContainer, BorderLayout.CENTER);
		} else {
			remove(infoPanel);
			remove(tableContainer);
		}
		revalidate();

	}

	public void clear() {
		System.out.println("Clear preview");
		load(null);
	}

}
