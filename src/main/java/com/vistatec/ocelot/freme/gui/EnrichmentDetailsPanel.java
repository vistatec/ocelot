package com.vistatec.ocelot.freme.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;

/**
 * Panels displaying enrichments details.
 */
public class EnrichmentDetailsPanel extends JScrollPane {

	/** The serial version UID. */
	private static final long serialVersionUID = 2996737747478539335L;

	/** The Entity enrichment icon name. */
	private static final String ENTITY_ICON_NAME = "entity";

	/** The Terminology enrichment icon name. */
	private static final String TERMINOLOGY_ICON_NAME = "term";

	/** The Link enrichment icon name. */
	private static final String LINK_ICON_NAME = "link";

	/** The panel maximum height. */
	private static final int MAX_HEIGHT = 250;

	/** The panel width. */
	private int width;

	/** The panel height. */
	private int height;

	/** The table displaying entities details. */
	private JTable entityTable;

	/** The table displaying links details. */
	private JTable linkTable;

	/** The table displaying terms details. */
	private JTable termTable;

	/** The panel displaying entities details. */
	private JPanel entityPanel;

	/** The entity enrichments table model. */
	private EnrichmentTableModel entityModel;

	/** The link enrichments table model. */
	private EnrichmentTableModel linkModel;

	/** The terminology enrichments table model. */
	private EnrichmentTableModel termModel;

	/** The panel displaying terminology enrichments details. */
	private JPanel terminologyPanel;

	/** The panel displaying link enrichments details. */
	private JPanel linkPanel;

	/** The list of enrichments. */
	private List<Enrichment> enrichments;

	/** The background color. */
	private Color background;

	/** The main panel. */
	private JPanel mainPanel;

	/**
	 * Constructor.
	 * 
	 * @param enrichments
	 *            the list of enrichments
	 * @param background
	 *            the background color.
	 */
	public EnrichmentDetailsPanel(final List<Enrichment> enrichments,
	        final Color background) {
		this.enrichments = enrichments;
		this.background = background;
		buildPanel();
	}

	/**
	 * Builds the panel.
	 */
	private void buildPanel() {

		if (enrichments != null) {
			System.out.println("ENRICHMENTS");
			for (Enrichment e : enrichments) {
				System.out.println(e.toString());
				if (e.getType().equals(Enrichment.ENTITY_TYPE)) {
					addToEntityPanel((EntityEnrichment) e);
				} else if (e.getType().equals(Enrichment.TERMINOLOGY_TYPE)) {
					addToTerminologyPanel((TerminologyEnrichment) e);
				} else if (e.getType().equals(Enrichment.LINK_TYPE)) {
					addToLinkPanel((LinkEnrichment) e);
				}
			}
		}
		final Dimension size = new Dimension(width + 60, height + 50);
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(background);
		configEnrichmentPanel(entityPanel, entityTable, entityModel);
		configEnrichmentPanel(linkPanel, linkTable, linkModel);
		configEnrichmentPanel(terminologyPanel, termTable, termModel);
		setViewportView(mainPanel);
		setBorder(BorderFactory.createEmptyBorder());
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setSize(size);
		setPreferredSize(size);
		setBackground(background);
	}

	/**
	 * Configures an enrichment panel with its enrichments table and table
	 * model.
	 * 
	 * @param enrichPanel
	 *            the enrichment panel
	 * @param enrichTable
	 *            the enrichment table
	 * @param model
	 *            the enrichment table model.
	 */
	private void configEnrichmentPanel(final JPanel enrichPanel,
	        final JTable enrichTable, final EnrichmentTableModel model) {
		if (enrichPanel != null) {
			mainPanel.add(enrichPanel);
			enrichTable.setSize(width + 50,
			        enrichTable.getRowHeight() * model.getRowCount());
			enrichTable.setPreferredSize(new Dimension(width + 20, enrichTable
			        .getRowHeight() * model.getRowCount()));
			enrichPanel.setSize(enrichTable.getSize());
			enrichPanel.setPreferredSize(enrichTable.getSize());
		}
	}

	/**
	 * Adds an entity enrichment to the entities panel.
	 * 
	 * @param enrich
	 *            the enrichment to add.
	 */
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
							e1.printStackTrace();
						}
					}

				}
			};
			entityTable
			        .setDefaultRenderer(String.class, new LinkRenderer(true));
			TableColumn column = entityTable.getColumnModel().getColumn(1);
			column.setCellEditor(new LinkEditor(entityListener, true));
			entityPanel = buildDetailPanel(ENTITY_ICON_NAME, entityTable);
		}

		entityModel.addEnrichment(enrich);
		LinkRenderer renderer = (LinkRenderer) entityTable
		        .getCellRenderer(0, 1);
		JButton renderedComp = (JButton) renderer.getComponent(enrich
		        .toString());
		if (renderedComp.getWidth() > width) {
			width = renderedComp.getWidth();
		}

	}

	/**
	 * Adds a link enrichment to the links panel.
	 * 
	 * @param enrich
	 *            the link enrichment
	 */
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

			linkPanel = buildDetailPanel(LINK_ICON_NAME, linkTable);
		}
		linkModel.addEnrichment(enrich);
		LinkRenderer renderer = (LinkRenderer) linkTable.getCellRenderer(0, 1);
		JButton renderedComp = (JButton) renderer.getComponent(enrich
		        .toString());
		if (renderedComp.getWidth() > width) {
			width = renderedComp.getWidth();
		}
	}

	/**
	 * Builds a details panel.
	 * 
	 * @param iconPath
	 *            the path of the enrichment icon
	 * @param table
	 *            the table
	 * @return the built panel.
	 */
	private JPanel buildDetailPanel(final String iconPath, final JTable table) {

		JPanel panel = new JPanel();
		panel.setBackground(background);
		table.setBackground(background);
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

	/**
	 * Adds a terminology enrichment to the terminologies panel.
	 * 
	 * @param enrich
	 *            the enrichment.
	 */
	private void addToTerminologyPanel(final TerminologyEnrichment enrich) {

		if (terminologyPanel == null) {
			termModel = new EnrichmentTableModel(null, true);
			termTable = new JTable(termModel);
			ActionListener listener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selRow = termTable.getSelectedRow();
					if(selRow != -1){
						TerminologyEnrichment selEnrichment = (TerminologyEnrichment) termModel.getEnrichmentAtRow(selRow);
						TerminologyEnrichmentFrame termFrame = new TerminologyEnrichmentFrame( SwingUtilities
								.windowForComponent(EnrichmentDetailsPanel.this), selEnrichment);
						termFrame.open();
					}
				}
			};
			termTable.setDefaultRenderer(String.class, new LinkRenderer(true));
			TableColumn column = termTable.getColumnModel().getColumn(1);
			column.setCellEditor(new LinkEditor(listener, true));
			terminologyPanel = buildDetailPanel(TERMINOLOGY_ICON_NAME,
			        termTable);
		}

		termModel.addEnrichment(enrich);
		FontMetrics metrics = terminologyPanel.getFontMetrics(terminologyPanel
		        .getFont());
		computeSize(metrics, enrich.toString());
	}

	/**
	 * Gets all the enabled enrichments.
	 * 
	 * @return the list of enabled enrichments.
	 */
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

	/**
	 * Gets all the disabled enrichments.
	 * 
	 * @return the list of disabled enrichments.
	 */
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

	/**
	 * Computes the size of the panel.
	 * 
	 * @param metrics
	 *            the font metrics
	 * @param enrichString
	 *            the enrichment string
	 */
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

/**
 * Toggle button used for enabling/disabling the enrichments.
 */
class EnableDisableButton extends JToggleButton {

	/** The serial version UID. */
	private static final long serialVersionUID = -4691075227280892484L;

	/** The enabled icon suffix. */
	private static final String ENABLED_ICON_SUFFIX = "-enabled.png";

	/** The disabled icon suffix. */
	private static final String DISABLED_ICON_SUFFIX = "-disabled.png";

	/** The icon displayed when the button is selected. */
	private ImageIcon selectedIcon;

	/** The icon displayed when the button is not selected. */
	private ImageIcon unselectedIcon;

	/**
	 * Constructor.
	 * 
	 * @param iconPath
	 *            the prefix of the icon file name.
	 */
	public EnableDisableButton(final String iconPath) {
		final Toolkit kit = Toolkit.getDefaultToolkit();
		selectedIcon = new ImageIcon(kit.createImage(Ocelot.class
		        .getResource(iconPath + ENABLED_ICON_SUFFIX)));
		unselectedIcon = new ImageIcon(kit.createImage(Ocelot.class
		        .getResource(iconPath + DISABLED_ICON_SUFFIX)));
		setSelectedIcon(selectedIcon);
		setIcon(unselectedIcon);
		setPreferredSize(new Dimension(20, 20));
		setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
	}

}

/**
 * Renderer for the toggle button used for enabling/disabling enrichments.
 */
class ToggleButtonRenderer extends DefaultTableCellRenderer {

	/** The serial version UID. */
	private static final long serialVersionUID = -3961708012107549317L;

	/** The toggle button. */
	private EnableDisableButton button;

	/**
	 * Constructor.
	 * 
	 * @param iconPath
	 *            the icon name
	 */
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

/**
 * Editor for the toggle button used for enabling/disabling enrichments.
 */
class ToggleButtonEditor extends DefaultCellEditor implements ActionListener {

	/** The serial version UID. */
	private static final long serialVersionUID = 8057060671044058694L;

	/** The toggle button. */
	private EnableDisableButton button;

	/**
	 * Constructor.
	 * 
	 * @param iconPath
	 *            the icon name.
	 */
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

/**
 * Renderer for the link enrichment.
 */
class LinkRenderer extends DefaultTableCellRenderer {

	/** The serial version UID. */
	private static final long serialVersionUID = 4339337048277285400L;

	/**
	 * States if the value a link enrichment. If it's false, then the value is
	 * the name of the entity the links are related to.
	 */
	private boolean link;

	/**
	 * Constructor.
	 * 
	 * @param link
	 *            a boolean stating if the value of this enrichment is an actual
	 *            link. If it's not a link, then it is the name of the related
	 *            entity.
	 */
	public LinkRenderer(final boolean link) {
		this.link = link;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
	        boolean isSelected, boolean hasFocus, int row, int column) {

		return getComponent((String) value);
	}

	/**
	 * Gets the component rendering this link enrichment.
	 * 
	 * @param value
	 *            the link value.
	 * @return the component that renders this link enrichment.
	 */
	public Component getComponent(String value) {
		JButton button = new JButton();
		if (link) {
			button.setText((String) value);
		} else {
			button.setText("View info about " + (String) value);
		}
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setToolTipText(button.getText());
		button.setOpaque(false);
		button.setBackground(Color.WHITE);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setForeground(Color.BLUE);
		button.setBorder(BorderFactory.createEmptyBorder());
		FontMetrics metrics = button.getFontMetrics(button.getFont());
		button.setPreferredSize(new Dimension(metrics.charsWidth(button
		        .getText().toCharArray(), 0, button.getText().length()) + 30,
		        button.getHeight()));
		button.setSize(new Dimension(metrics.charsWidth(button.getText()
		        .toCharArray(), 0, button.getText().length()) + 30, button
		        .getHeight()));
		return button;
	}
}

/**
 * Editor for link enrichments.
 */
class LinkEditor extends DefaultCellEditor {

	/** The serial version UID. */
	private static final long serialVersionUID = -1290282866909184711L;
	/** The action listener. */
	private ActionListener listener;

	/**
	 * States if the value a link enrichment. If it's false, then the value is
	 * the name of the entity the links are related to.
	 */
	private boolean link;

	/**
	 * Constructor.
	 * 
	 * @param listener
	 *            the action listener.
	 * @param link
	 *            a boolean stating if the value of this enrichment is an actual
	 *            link. If it's not a link, then it is the name of the related
	 *            entity.
	 */
	public LinkEditor(final ActionListener listener, boolean link) {
		super(new JCheckBox());
		this.listener = listener;
		this.link = link;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
	        boolean isSelected, int row, int column) {

		JButton button = new JButton();
		if (link) {
			button.setText((String) value);
		} else {
			button.setText("View info about " + (String) value);
		}
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setToolTipText(button.getText());
		button.setOpaque(false);
		button.setBackground(Color.WHITE);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setForeground(Color.BLUE);
		button.addActionListener(listener);
		button.setBorder(BorderFactory.createEmptyBorder());
		FontMetrics metrics = button.getFontMetrics(button.getFont());
		button.setPreferredSize(new Dimension(metrics.charsWidth(button
		        .getText().toCharArray(), 0, button.getText().length()) + 30,
		        button.getHeight()));
		button.setSize(new Dimension(metrics.charsWidth(button.getText()
		        .toCharArray(), 0, button.getText().length()) + 30, button
		        .getHeight()));
		return button;
	}

}
