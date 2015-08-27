package com.vistatec.ocelot.freme.manager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.segment.model.Enrichment;
import com.vistatec.ocelot.segment.model.EntityEnrichment;
import com.vistatec.ocelot.segment.model.LinkEnrichment;
import com.vistatec.ocelot.segment.model.TerminologyEnrichment;

public class EnrichmentDetailsPanel extends JScrollPane {

	/**
     * 
     */
	private static final long serialVersionUID = 2996737747478539335L;

	private static final String ENTITY_ICON_PATH = "entity";

	private static final String TERMINOLOGY_ICON_PATH = "term";

	private static final String LINK_ICON_PATH = "link";

	private static final int MAX_HEIGHT = 250;

	private int width;

	private int height;

	private JTable entityTable;

	private JTable linkTable;

	private JTable termTable;

	private JPanel entityPanel;

	private EnrichmentTableModel entityModel;

	private EnrichmentTableModel linkModel;

	private EnrichmentTableModel termModel;

	private JPanel terminologyPanel;

	private JPanel linkPanel;

	private List<Enrichment> enrichments;

	private Color background;

	private JPanel mainPanel;

	public EnrichmentDetailsPanel(final List<Enrichment> enrichments,
	        final Color background) {
		this.enrichments = enrichments;
		this.background = background;
		buildPanel();
	}

	private void buildPanel() {

		if (enrichments != null) {
			System.out.println("ENRICHMENTS");
			for (Enrichment e : enrichments) {
				System.out.println(e.toString());
				if (e.getType().equals(EntityEnrichment.ENRICHMENT_TYPE)) {
					addToEntityPanel((EntityEnrichment) e);
				} else if (e.getType().equals(
				        TerminologyEnrichment.ENRICHMENT_TYPE)) {
					addToTerminologyPanel((TerminologyEnrichment) e);
				} else if (e.getType().equals(LinkEnrichment.ENRICHMENT_TYPE)) {
					addToLinkPanel((LinkEnrichment) e);
				}
			}
		}
		final Dimension size = new Dimension(width + 60, height + 50);
		// final Dimension size = new Dimension(300, 200);
		// if (entityPanel != null) {
		// add(entityPanel);
		// int tableHeight = entityTable.getFontMetrics(entityTable.getFont())
		// .getHeight();
		// // entityTable.setSize(new Dimension(width + 10,
		// // tableHeight*entityModel.getRowCount() + 10));
		// // entityPanel.setSize(entityTable.getSize());
		// entityTable.setSize(width + 50,
		// entityTable.getRowHeight()*entityModel.getRowCount());
		// entityTable.setPreferredSize(new Dimension( width + 50,
		// entityTable.getRowHeight()*entityModel.getRowCount()));
		// entityPanel.setSize(entityTable.getSize());
		// entityPanel.setPreferredSize(entityTable.getSize());
		// }
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(background);
		configEnrichmentPanel(entityPanel, entityTable, entityModel);
		configEnrichmentPanel(linkPanel, linkTable, linkModel);
		configEnrichmentPanel(terminologyPanel, termTable, termModel);
		// if (terminologyPanel != null) {
		// add(terminologyPanel);
		// }

		// if (linkPanel != null) {
		// add(linkPanel);
		// }
		setViewportView(mainPanel);
		setBorder(BorderFactory.createEmptyBorder());
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setSize(size);
		setPreferredSize(size);
		setBackground(background);
	}

	private void configEnrichmentPanel(final JPanel enrichPanel,
	        final JTable enrichTable, final EnrichmentTableModel model) {
		if (enrichPanel != null) {
			mainPanel.add(enrichPanel);
			int tableHeight = enrichTable.getFontMetrics(enrichTable.getFont())
			        .getHeight();
			// entityTable.setSize(new Dimension(width + 10,
			// tableHeight*entityModel.getRowCount() + 10));
			// entityPanel.setSize(entityTable.getSize());
			enrichTable.setSize(width + 50,
			        enrichTable.getRowHeight() * model.getRowCount());
			enrichTable.setPreferredSize(new Dimension(width + 20, enrichTable
			        .getRowHeight() * model.getRowCount()));
			enrichPanel.setSize(enrichTable.getSize());
			enrichPanel.setPreferredSize(enrichTable.getSize());
		}
	}

	private void addToEntityPanel(EntityEnrichment enrich) {

		if (entityPanel == null) {
			entityModel = new EnrichmentTableModel(null, true);
			entityTable = new JTable(entityModel);
			ActionListener entityListener = new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton btn = (JButton) e.getSource();
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(new URI(btn.getText()));
						} catch (IOException ex) { /* TODO: error handling */
							ex.printStackTrace();
						} catch (URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}
			};
			entityTable.setDefaultRenderer(String.class, new LinkRenderer(true));
			TableColumn column = entityTable.getColumnModel().getColumn(1);
			column.setCellEditor(new LinkEditor(entityListener, true));
			entityPanel = buildDetailPanel(ENTITY_ICON_PATH, entityTable);
		}

		entityModel.addEnrichment(enrich);
		LinkRenderer renderer = (LinkRenderer) entityTable.getCellRenderer(0, 1);
		JButton renderedComp = (JButton) renderer.getComponent(enrich.toString());
		if(renderedComp.getWidth() > width){
			width = renderedComp.getWidth();
		}
//		FontMetrics metrics = entityPanel.getFontMetrics(entityPanel.getFont());
//		computeSize(metrics, enrich.toString());

	}

	private void addToLinkPanel(final LinkEnrichment enrich) {

		if (linkPanel == null) {
			linkModel = new EnrichmentTableModel(null, true);
			linkTable = new JTable(linkModel);
			ActionListener listener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					LinkEnrichmentFrame enrichFrame = new LinkEnrichmentFrame(
					        enrich,
					        SwingUtilities
					                .windowForComponent(EnrichmentDetailsPanel.this));
					enrichFrame.open();
				}
			};
			linkTable.setDefaultRenderer(String.class, new LinkRenderer(false));
			TableColumn column = linkTable.getColumnModel().getColumn(1);
			column.setCellEditor(new LinkEditor(listener, false));

			// TODO CHECK THIS
			linkPanel = buildDetailPanel(LINK_ICON_PATH, linkTable);
		}
		linkModel.addEnrichment(enrich);
		LinkRenderer renderer = (LinkRenderer) linkTable.getCellRenderer(0, 1);
		JButton renderedComp = (JButton) renderer.getComponent(enrich.toString());
		if(renderedComp.getWidth() > width){
			width = renderedComp.getWidth();
		}
		
//		FontMetrics metrics = renderedComp.getFontMetrics(renderedComp.getFont());
//		computeSize(metrics, renderedComp.getText());
	}

	private JPanel buildDetailPanel(final String iconPath, final JTable table) {

		JPanel panel = new JPanel();
		panel.setBackground(background);
		table.setBackground(background);
		// Toolkit kit = Toolkit.getDefaultToolkit();
		// ImageIcon icon = new ImageIcon(kit.createImage(Ocelot.class
		// .getResource(iconPath)));
		// JLabel iconLabel = new JLabel(icon);
		// iconLabel.setSize(48, 48);
		// panel.add(iconLabel);
		table.setShowGrid(false);
		table.setSelectionBackground(background);
		panel.add(table);
		TableColumn column = table.getColumnModel().getColumn(
		        EnrichmentTableModel.BUTTON_COL);
		column.setCellEditor(new ToggleButtonEditor(iconPath));
		column.setCellRenderer(new ToggleButtonRenderer(iconPath));
		column.setMaxWidth(25);
		column.setWidth(25);
		table.setRowHeight(25);
		return panel;
	}

	private void addToTerminologyPanel(TerminologyEnrichment enrich) {

		if (terminologyPanel == null) {
			termModel = new EnrichmentTableModel(null, false);
			termTable = new JTable(termModel);
			terminologyPanel = buildDetailPanel(TERMINOLOGY_ICON_PATH,
			        termTable);
		}

		termModel.addEnrichment(enrich);
		FontMetrics metrics = terminologyPanel.getFontMetrics(terminologyPanel
		        .getFont());
		computeSize(metrics, enrich.toString());
	}

	public List<Enrichment> getEnabledEnrichments() {

		List<Enrichment> enabledEnrichments = new ArrayList<Enrichment>();
		if (enrichments != null) {
			for (Enrichment e : enrichments) {
				if (!e.isDisabled()) {
					enabledEnrichments.add(e);
				}
			}
		}
		return enabledEnrichments;
	}

	public List<Enrichment> getDisabledEnrichments() {

		List<Enrichment> disabledEnrichments = new ArrayList<Enrichment>();
		if (enrichments != null) {
			for (Enrichment e : enrichments) {
				if (e.isDisabled()) {
					disabledEnrichments.add(e);
				}
			}
		}
		return disabledEnrichments;
	}

	private void computeSize(final FontMetrics metrics,
	        final String enrichString) {
		if (enrichString != null) {
			int enrichmentWidth = metrics.charsWidth(
			        enrichString.toCharArray(), 0, enrichString.length()) + 30;
			if (enrichmentWidth > width) {
				width = enrichmentWidth;
			}
			height += metrics.getHeight() + 10;
			if (height > MAX_HEIGHT) {
				height = MAX_HEIGHT;
			}

			System.out.println(" Enrichment: " + enrichString + "\n width = "
			        + width + " - height = " + height);
		}
	}

}

class EnableDisableButton extends JToggleButton {

	/**
     * 
     */
	private static final long serialVersionUID = -4691075227280892484L;

	private static final String ENABLED_ICON_SUFFIX = "-enabled.png";

	private static final String DISABLED_ICON_SUFFIX = "-disabled.png";

	private ImageIcon selectedIcon;

	private ImageIcon unselectedIcon;

	// private Enrichment enrichment;

	public EnableDisableButton(final String iconPath) {
		final Toolkit kit = Toolkit.getDefaultToolkit();
		selectedIcon = new ImageIcon(kit.createImage(Ocelot.class
		        .getResource(iconPath + ENABLED_ICON_SUFFIX)));
		unselectedIcon = new ImageIcon(kit.createImage(Ocelot.class
		        .getResource(iconPath + DISABLED_ICON_SUFFIX)));
		setSelectedIcon(selectedIcon);
		setIcon(unselectedIcon);
		setPreferredSize(new Dimension(20, 20));
		// setBorder(BorderFactory.createEmptyBorder());
		setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		// setBackground(SystemColor.info);
	}

	/*
	 * // @Override // public void setSelected(boolean selected) { //
	 * super.setSelected(selected); // enrichment.setDisabled(!selected); // }
	 */
}

class ToggleButtonRenderer extends DefaultTableCellRenderer {

	/**
     * 
     */
	private static final long serialVersionUID = -3961708012107549317L;

	private EnableDisableButton button;

	public ToggleButtonRenderer(final String iconPath) {

		button = new EnableDisableButton(iconPath);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
	        boolean isSelected, boolean hasFocus, int row, int column) {

		button.setSelected((boolean) value);
		button.setBackground(table.getBackground());
		return button;
	}
}

class ToggleButtonEditor extends DefaultCellEditor implements ActionListener {

	/**
     * 
     */
	private static final long serialVersionUID = 8057060671044058694L;

	private EnableDisableButton button;

	public ToggleButtonEditor(final String iconPath) {
		super(new JCheckBox());
		button = new EnableDisableButton(iconPath);
		button.addActionListener(this);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
	        boolean isSelected, int row, int column) {

		button.setSelected((boolean) value);
		return button;
	}

	@Override
	public Object getCellEditorValue() {
		return button.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		stopCellEditing();

	}
}

class LinkRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4339337048277285400L;

	// private ActionListener listener;

	// public LinkRenderer(final ActionListener listener) {
	//
	// this.listener = listener;
	// }
	
	private boolean link;
	
	public LinkRenderer(final boolean link) {
		this.link = link;
    }

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
	        boolean isSelected, boolean hasFocus, int row, int column) {

//		JButton button = new JButton("View info about " + (String) value);
//		button.setHorizontalAlignment(SwingConstants.LEFT);
//		button.setBorderPainted(false);
//		button.setOpaque(false);
//		button.setBackground(Color.WHITE);
//		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//		button.setForeground(Color.BLUE);
//		// button.addActionListener(listener);
		return getComponent( (String) value);
	}
	
	public Component getComponent(String value){
		JButton button = new JButton();
		if(link){
			button.setText((String) value);
		} else {
			button.setText("View info about " + (String) value);
		}
		button.setHorizontalAlignment(SwingConstants.LEFT);
//		button.setBorderPainted(false);
		button.setToolTipText(button.getText());
		button.setOpaque(false);
		button.setBackground(Color.WHITE);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setForeground(Color.BLUE);
//		button.getFont().deriveFont(Font.PLAIN);
		button.setBorder(BorderFactory.createEmptyBorder());
		FontMetrics metrics = button.getFontMetrics(button.getFont());
		button.setPreferredSize(new Dimension(metrics.charsWidth(button.getText().toCharArray(), 0, button.getText().length()) + 30, button.getHeight()));
		button.setSize(new Dimension(metrics.charsWidth(button.getText().toCharArray(), 0, button.getText().length()) + 30, button.getHeight()));
		// button.addActionListener(listener);
		return button;
	}
}

class LinkEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1290282866909184711L;
	private ActionListener listener;
	private boolean link;

	public LinkEditor(final ActionListener listener, boolean link) {
		super(new JCheckBox());
		this.listener = listener;
		this.link = link;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
	        boolean isSelected, int row, int column) {

		
//		JButton button = new JButton(link);
//		button.setHorizontalAlignment(SwingConstants.LEFT);
//		button.setBorderPainted(false);
//		button.setOpaque(false);
//		button.setBackground(Color.WHITE);
//		button.setToolTipText(link);
//		button.addActionListener(listener);
//		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//		button.setForeground(Color.BLUE);
		
		JButton button = new JButton();
		if(link){
			button.setText((String) value);
		} else {
			button.setText("View info about " + (String) value);
		}
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setToolTipText(button.getText());
//		button.setBorderPainted(false);
		button.setOpaque(false);
		button.setBackground(Color.WHITE);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setForeground(Color.BLUE);
//		button.getFont().deriveFont(Font.PLAIN);
		button.addActionListener(listener);
		button.setBorder(BorderFactory.createEmptyBorder());
		FontMetrics metrics = button.getFontMetrics(button.getFont());
		button.setPreferredSize(new Dimension(metrics.charsWidth(button.getText().toCharArray(), 0, button.getText().length()) + 30, button.getHeight()));
		button.setSize(new Dimension(metrics.charsWidth(button.getText().toCharArray(), 0, button.getText().length()) + 30, button.getHeight()));
		return button;
	}

	// @Override
	// public Object getCellEditorValue() {
	// return button.isSelected();
	// }

	// @Override
	// public void actionPerformed(ActionEvent e) {
	//
	// stopCellEditing();
	//
	// }
}
