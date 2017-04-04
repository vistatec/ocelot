package com.vistatec.ocelot.tm.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.ui.IntegerDocument;

/**
 * Modal dialog letting users save fuzzy threshold and max results settings. 
 */
public class TmSettingsDialog extends JDialog implements Runnable,
        ActionListener {

    private static final Logger LOG = LoggerFactory.getLogger(TmSettingsDialog.class);

    /**serial version UID.  */ 
    private static final long serialVersionUID = -6912724603474734606L;

    /** Dialog width. */
    private static final int WIDTH = 230;

    /** Dialog height. */
    private static final int HEIGHT = 180;

    /** Text fields width. */
    private static final int TXT_WIDTH = 50;

    /** Text fields height. */
    private static final int TXT_HEIGHT = 20;

    /** Labels width. */
    private static final int LBL_WIDTH = 130;

    /** Label height. */
    private static final int LBL_HEIGHT = 20;

    /** The controller. */
    private TmGuiConfigController controller;

    /** The fuzzy threshold text field. */
    private JTextField txtFuzzyThreshold;

    /** The max results text field. */
    private JTextField txtMaxResultNum;

    /** The OK button. */
    private JButton btnOk;

    /** The Cancel button. */
    private JButton btnCancel;

    /** The old threshold value. */
    private String oldThreshold;

    /** The old max results value. */
    private String oldMaxResults;

    /** 
     * Constructor.
     * @param owner the owner dialog.
     * @param controller the controller.
     */
    public TmSettingsDialog(final JDialog owner,
            final TmGuiConfigController controller) {

        super(owner, true);
        this.controller = controller;

    }

    /**
     * Constructor.
     * @param owner the owner frame.
     * @param controller the controller. 
     */
    public TmSettingsDialog(final JFrame owner,
            final TmGuiConfigController controller) {

        super(owner, true);
        this.controller = controller;

    }

    /**
     * Builds the main panel, displaying text fields and labels. 
     * @return the main panel.
     */
    private Component getMainPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 15));
        JLabel lblThreshold = new JLabel("Fuzzy Threshold");
        configLabel(lblThreshold);
        JLabel lblMaxRes = new JLabel("Max. Results Number");
        configLabel(lblMaxRes);
        JLabel lblPerCent = new JLabel("%");
        txtFuzzyThreshold = new JTextField();
        configTextField(txtFuzzyThreshold);
        txtMaxResultNum = new JTextField();
        configTextField(txtMaxResultNum);

        panel.add(lblMaxRes);
        panel.add(txtMaxResultNum);
        panel.add(lblThreshold);
        panel.add(txtFuzzyThreshold);
        panel.add(lblPerCent);
        return panel;
    }

    /**
     * Builds the bottom panel displaying Ok and Cancel buttons.
     * @return the bottom panel.
     */
    private Component getBottomPanel() {

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnOk = new JButton("Ok");
        btnOk.addActionListener(this);
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);
        buttonPanel.setPreferredSize(new Dimension(WIDTH - 10, 40));

        JPanel bottomPanel = new JPanel();
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setPreferredSize(new Dimension(WIDTH, 1));
        bottomPanel.add(separator);
        bottomPanel.add(buttonPanel);
        bottomPanel.setPreferredSize(new Dimension(WIDTH - 10, 60));

        return bottomPanel;

    }

    /**
     * Configures the text field.
     * @param text the text field.
     */
    private void configTextField(final JTextField text) {
        text.setDocument(new IntegerDocument());
        text.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
        text.setHorizontalAlignment(JTextField.RIGHT);
    }

    /**
     * Configures the label.
     * @param label the label.
     */
    private void configLabel(final JLabel label) {

        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setPreferredSize(new Dimension(LBL_WIDTH, LBL_HEIGHT));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        setTitle("TM Settings");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        add(getMainPanel(), BorderLayout.CENTER);
        add(getBottomPanel(), BorderLayout.SOUTH);
        load();
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);

    }

    /**
     * Loads dialog fields.
     */
    private void load() {

        oldThreshold = String.valueOf(controller.getFuzzyThreshold());
        oldMaxResults = String.valueOf(controller.getMaxResults());
        txtFuzzyThreshold.setText(oldThreshold);
        txtMaxResultNum.setText(oldMaxResults);
    }

    /**
     * Saves the inserted values.
     */
    private void save() {
        String thresholdString = txtFuzzyThreshold.getText();
        String maxResultsString = txtMaxResultNum.getText();
        boolean showMessage = false;
        boolean canSave = true;
        if (thresholdString.isEmpty()) {
            txtFuzzyThreshold.setBorder(new LineBorder(Color.red));
            showMessage = true;
        }
        if (maxResultsString.isEmpty()) {
            txtMaxResultNum.setBorder(new LineBorder(Color.red));
            showMessage = true;
        }
        if (showMessage) {
            int option = JOptionPane
                    .showConfirmDialog(
                            this,
                            "Empty fields are not admitted. Do you want to use default values?",
                            "TM Settings Empty Fields",
                            JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                if (thresholdString.isEmpty()) {
                    thresholdString = String.valueOf(controller
                            .getDefaultFuzzyThreshold());
                }
                if (maxResultsString.isEmpty()) {
                    maxResultsString = String.valueOf(controller
                            .getDefaultMaxResults());
                }
                
            } else {
                canSave = false;
            }
        }
        if (canSave) {
            if (!oldThreshold.equals(thresholdString)
                    || !oldMaxResults.equals(maxResultsString)) {

                float threshold = Float.parseFloat(thresholdString) / 100;
                int maxResults = Integer.parseInt(maxResultsString);
                try {
                    controller.saveTmSettings(threshold, maxResults);
                    close();
                } catch (TransferException e) {
                    LOG.trace("Error while saving TM settings: threshold = "
                                    + threshold + " - max results = "
                                    + maxResults, e);
                    JOptionPane.showMessageDialog(this,
                            "An error has occurred while saving settings.",
                            "TM Settings Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                close();
            }
        }
    }

    /**
     * Discards changes.
     */
    private void cancel() {

        close();
    }

    /**
     * Closes the dialog.
     */
    private void close() {

        controller.closeDialog();
        setVisible(false);
        dispose();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(btnCancel)) {
            cancel();
        } else {
            save();
        }
    }

}
