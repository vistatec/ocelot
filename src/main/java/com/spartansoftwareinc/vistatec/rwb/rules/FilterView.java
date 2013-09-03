package com.spartansoftwareinc.vistatec.rwb.rules;

import com.spartansoftwareinc.vistatec.rwb.rules.RuleConfiguration.StateQualifier;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

/**
 * Window for selecting which filter rules to apply to the segment table.
 */
public class FilterView extends JPanel implements Runnable, ActionListener, ItemListener {

    private JFrame frame;
    RuleConfiguration filterRules;
    private String allString = "All Segments",
            metadataString = "All w/metadata",
            customString = "Custom Rules:";
    private JRadioButton all, allWithMetadata, custom;
    private HashMap<String, JCheckBox> rules = new HashMap<String, JCheckBox>();

    public FilterView(RuleConfiguration filterRules) {
        super(new GridBagLayout());
        this.filterRules = filterRules;
        setBorder(new EmptyBorder(10,10,10,10));

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.gridwidth = 1;
        int gridy = 0;

        JLabel title = new JLabel("Show segments matching rules:");
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        add(title, gridBag);

        all = new JRadioButton(allString);
        all.setSelected(filterRules.all);
        all.addActionListener(this);
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        add(all, gridBag);

        allWithMetadata = new JRadioButton(metadataString);
        allWithMetadata.setSelected(filterRules.allWithMetadata);
        allWithMetadata.addActionListener(this);
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        add(allWithMetadata, gridBag);

        custom = new JRadioButton(customString);
        custom.setSelected(!filterRules.all && !filterRules.allWithMetadata);
        custom.addActionListener(this);
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        add(custom, gridBag);

        ButtonGroup filterGroup = new ButtonGroup();
        filterGroup.add(all);
        filterGroup.add(allWithMetadata);
        filterGroup.add(custom);

        for (StateQualifier stateQualifier : RuleConfiguration.StateQualifier.values()) {
            addFilterCheckBox(stateQualifier.getName(),
                    filterRules.getStateQualifierEnabled(stateQualifier.getName()), gridBag, gridy++);
        }

        for (String rule : filterRules.getRuleLabels()) {
            addFilterCheckBox(rule, filterRules.getRuleEnabled(rule), gridBag, gridy++);
        }
    }

    public final void addFilterCheckBox(String checkboxName, boolean selected, GridBagConstraints gridBag, int gridy) {
        JCheckBox filterButton = new JCheckBox(checkboxName);
        filterButton.setEnabled(custom.isSelected());
        filterButton.setSelected(selected);
        filterButton.addItemListener(this);
        gridBag.gridwidth = 1;
        gridBag.gridx = 0;
        gridBag.gridy = gridy;
        gridBag.insets = new Insets(0, 20, 0, 0);
        add(filterButton, gridBag);
        rules.put(checkboxName, filterButton);
    }

    @Override
    public void run() {
        frame = new JFrame("Filters");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        if (source.getClass().equals(JCheckBox.class)) {
            JCheckBox check = (JCheckBox) source;
            StateQualifier stateQualifier = RuleConfiguration.StateQualifier.get(check.getText());
            if (stateQualifier != null) {
                filterRules.setStateQualifierEnabled(stateQualifier, e.getStateChange() == ItemEvent.SELECTED);
            } else {
                filterRules.enableRule(check.getText(), e.getStateChange() == ItemEvent.SELECTED);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == all) {
            enableRules(false);
            filterRules.setAllSegments(true);
            filterRules.setMetadataSegments(false);
        } else if (ae.getSource() == allWithMetadata) {
            enableRules(false);
            filterRules.setAllSegments(false);
            filterRules.setMetadataSegments(true);
        } else if (ae.getSource() == custom) {
            enableRules(true);
            filterRules.setAllSegments(false);
            filterRules.setMetadataSegments(false);
        }
    }

    private void enableRules(boolean flag) {
        for (JCheckBox checkbox : rules.values()) {
            checkbox.setEnabled(flag);
        }
    }
}
