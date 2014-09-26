package com.vistatec.ocelot.ui;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Base class for panels that implement modeless dialogs.
 */
public abstract class ODialogPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JDialog parent;

    public ODialogPanel(LayoutManager mgr) {
        super(mgr);
    }

    public JButton getDefaultButton() {
        return null;
    }

    protected JDialog getDialog() {
        return parent;
    }

    public void setDialog(JDialog dialog) {
        this.parent = dialog;
        parent.add(this);
    }

    public void postInit() {
    }

    /**
     * Action listener that cleans up the containing dialog.
     */
    public class DisposeDialogListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            getDialog().dispose();
        }
    }
}
