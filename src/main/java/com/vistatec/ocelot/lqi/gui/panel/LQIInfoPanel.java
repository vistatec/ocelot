package com.vistatec.ocelot.lqi.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.vistatec.ocelot.lqi.gui.FloatDocument;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

/**
 * This panel displays info about an existing configuration for the LQI Grid.
 */
public class LQIInfoPanel extends JPanel {

	private static final long serialVersionUID = 7253091256238873534L;

	private static final int TXT_WIDTH = 150;

	private static final int TXT_HEIGHT = 25;

	private JTextField txtName;

	private JTextField txtSupplier;

	private JTextField txtThreshold;

	public LQIInfoPanel() {

		buildPanel();
	}

	private void buildPanel() {

		txtName = new JTextField();
		txtName.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
		txtSupplier = new JTextField();
		txtSupplier.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
		txtThreshold = new JTextField();
		txtThreshold.setPreferredSize(new Dimension(50, TXT_HEIGHT));
		txtThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
		txtThreshold.setDocument(new FloatDocument());
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 10, 0);
		c.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Name"), c);

		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 5, 10, 10);
		add(txtName, c);

		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("Threshold"), c);

		c.gridx = 3;
		c.gridy = 0;
		c.insets = new Insets(10, 5, 10, 0);
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(txtThreshold, c);

		c.gridx = 4;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(10, 2, 10, 10);
		add(new JLabel("%"), c);

		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(0, 10, 10, 0);
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("Supplier"), c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 5, 10, 10);
		add(txtSupplier, c);
	}

	public void load(LQIGridConfiguration lqiGridConfiguration) {

		if (lqiGridConfiguration != null) {

			txtName.setText(lqiGridConfiguration.getName());
			txtSupplier.setText(lqiGridConfiguration.getSupplier());
			if(lqiGridConfiguration.getThreshold() != null){
				txtThreshold.setText(Double.toString(lqiGridConfiguration
				        .getThreshold()));
			}
		}
	}
	
	public boolean checkMandatoryFields() {

		boolean fieldsChecked = true;
		if (txtName.getText().isEmpty()) {
			txtName.setBorder(getMandatoryBorder());
			fieldsChecked = false;
		} else {
			txtName.setBorder(getNormalBorder());
		}
		if (txtThreshold.getText().isEmpty()) {
			txtThreshold.setBorder(getMandatoryBorder());
			fieldsChecked = false;
		} else {
			txtThreshold.setBorder(getNormalBorder());
		}
		return fieldsChecked;
	}
	
	private Border getNormalBorder() {
		return new JTextField().getBorder();
	}

	private Border getMandatoryBorder() {

		return new LineBorder(Color.red);
	}

	public String getInsertedName() {
		
		return txtName.getText();
	}
	
	
	public String getInsertedSupplier(){
		
		return txtSupplier.getText();
	}
	
	public String getInsertedThreshold(){
		return txtThreshold.getText();
	}
	
	public void setEditable(boolean editable){
		
		txtName.setEditable(editable);
		txtSupplier.setEditable(editable);
		txtThreshold.setEditable(editable);
	}
}
