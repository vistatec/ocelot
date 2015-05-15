package com.vistatec.ocelot.freme.manager;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.vistatec.ocelot.segment.model.Enrichment;

public class EnrichmentTableModel extends DefaultTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = -3616098048861377131L;

    public static final int DESCRIPTION_COL = 1;

    public static final int BUTTON_COL = 0;

    private List<Enrichment> model;

    public EnrichmentTableModel(List<Enrichment> model) {

        this.model = model;
    }

    @Override
    public int getColumnCount() {

        return 2;
    }

    @Override
    public String getColumnName(int column) {
        return "";
    }

    @Override
    public int getRowCount() {
        int count = 0;
        if (model != null) {
            count = model.size();
        }
        return count;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class clazz = null;
        switch (columnIndex) {
        case DESCRIPTION_COL:
            clazz = String.class;
            break;
        case BUTTON_COL:
            clazz = Boolean.class;
            break;
        default:
            break;
        }
        return clazz;
    }

    @Override
    public Object getValueAt(int row, int column) {

        Object retValue = null;
        if (model != null) {
            if (row < model.size()) {
                Enrichment enrich = model.get(row);
                switch (column) {
                case DESCRIPTION_COL:
                    retValue = enrich.toString();
                    break;
                case BUTTON_COL:
                    retValue = !enrich.isDisabled();
                    break;
                default:
                    break;
                }
            }
        }
        return retValue;
    }
    
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        
        if(model != null && row<model.size()){
            Enrichment enrich = model.get(row);
            switch (column) {
            case BUTTON_COL:
                enrich.setDisabled(!(Boolean)aValue);
                break;

            default:
                break;
            }
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return column == BUTTON_COL;
    }

    public void addEnrichment(final Enrichment enrichment) {
        if (enrichment != null) {
            if (model == null) {
                model = new ArrayList<Enrichment>();
            }
            model.add(enrichment);
        }
    }

}
