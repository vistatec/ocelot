package com.vistatec.ocelot.freme.manager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.segment.model.Enrichment;
import com.vistatec.ocelot.segment.model.EntityEnrichment;
import com.vistatec.ocelot.segment.model.LinkEnrichment;
import com.vistatec.ocelot.segment.model.TerminologyEnrichment;

public class EnrichmentDetailsPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 2996737747478539335L;

    private static final String ENTITY_ICON_PATH = "";

    private static final String TERMINOLOGY_ICON_PATH = "";

    private static final String LINK_ICON_PATH = "";

    private int width;

    private int height;

    private JTable entityTable;

    private JPanel entityPanel;

    private EnrichmentTableModel entityModel;

    private Component terminologyPanel;

    private Component linkPanel;

    private List<Enrichment> enrichments;

    private Color background;

    public EnrichmentDetailsPanel(final List<Enrichment> enrichments,
            final Color background) {
        this.enrichments = enrichments;
        this.background = background;
        buildPanel();
    }

    private void buildPanel() {

        if (enrichments != null) {
            for (Enrichment e : enrichments) {
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
         final Dimension size = new Dimension(width + 60, height + 20);
//        final Dimension size = new Dimension(300, 200);
        if (entityPanel != null) {
            add(entityPanel);
            int tableHeight = entityTable.getFontMetrics(entityTable.getFont())
                    .getHeight();
            // entityTable.setSize(new Dimension(width + 10,
            // tableHeight*entityModel.getRowCount() + 10));
            // entityPanel.setSize(entityTable.getSize());
            entityTable.setSize(width + 50, entityTable.getRowHeight()*entityModel.getRowCount());
            entityTable.setPreferredSize(new Dimension( width + 50, entityTable.getRowHeight()*entityModel.getRowCount()));
            entityPanel.setSize(entityTable.getSize());
            entityPanel.setPreferredSize(entityTable.getSize());
        }
        if (terminologyPanel != null) {
            add(terminologyPanel);
        }

        if (linkPanel != null) {
            add(linkPanel);
        }

        setSize(size);
        setPreferredSize(size);
        setBackground(background);
    }

    private void addToEntityPanel(EntityEnrichment enrich) {

        if (entityPanel == null) {
            entityModel = new EnrichmentTableModel(null);
            entityTable = new JTable(entityModel);
            entityPanel = buildDetailPanel(ENTITY_ICON_PATH, entityTable);
        }

        entityModel.addEnrichment(enrich);
        FontMetrics metrics = entityPanel.getFontMetrics(entityPanel.getFont());
        computeSize(metrics, enrich.toString());

    }

    private JPanel buildDetailPanel(final String iconPath, final JTable table) {

        JPanel panel = new JPanel();
        panel.setBackground(background);
        table.setBackground(background);
//        Toolkit kit = Toolkit.getDefaultToolkit();
//        ImageIcon icon = new ImageIcon(kit.createImage(Ocelot.class
//                .getResource(iconPath)));
//        JLabel iconLabel = new JLabel(icon);
//        iconLabel.setSize(48, 48);
//        panel.add(iconLabel);
        table.setShowGrid(false);
        table.setSelectionBackground(background);
        panel.add(table);
        TableColumn column = table.getColumnModel().getColumn(
                EnrichmentTableModel.BUTTON_COL);
        column.setCellEditor(new ToggleButtonEditor());
        column.setCellRenderer(new ToggleButtonRenderer());
        column.setMaxWidth(25);
        column.setWidth(25);
        table.setRowHeight(25);
        return panel;
    }

    private void addToTerminologyPanel(TerminologyEnrichment enrich) {

    }

    private void addToLinkPanel(LinkEnrichment enrich) {

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

        int enrichmentWidth = metrics.charsWidth(enrichString.toCharArray(), 0,
                enrichString.length());
        if (enrichmentWidth > width) {
            width = enrichmentWidth;
        }
        height = metrics.getHeight() + 40;
    }

}

class EnableDisableButton extends JToggleButton {

    /**
     * 
     */
    private static final long serialVersionUID = -4691075227280892484L;

    private static final String ENABLED_ICON_PATH = "enabled.png";

    private static final String DISABLED_ICON_PATH = "disabled.png";

    private ImageIcon selectedIcon;

    private ImageIcon unselectedIcon;

    // private Enrichment enrichment;

    public EnableDisableButton() {
        final Toolkit kit = Toolkit.getDefaultToolkit();
        selectedIcon = new ImageIcon(kit.createImage(Ocelot.class
                .getResource(ENABLED_ICON_PATH)));
        unselectedIcon = new ImageIcon(kit.createImage(Ocelot.class
                .getResource(DISABLED_ICON_PATH)));
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

    public ToggleButtonRenderer() {

        button = new EnableDisableButton();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        button.setSelected((boolean) value);
        button.setBackground(table.getBackground());
        return button;
    }
}

class ToggleButtonEditor extends DefaultCellEditor implements ActionListener{

    /**
     * 
     */
    private static final long serialVersionUID = 8057060671044058694L;

    private EnableDisableButton button;

    public ToggleButtonEditor() {
        super(new JCheckBox());
        button = new EnableDisableButton();
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
