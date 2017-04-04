package com.vistatec.ocelot.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.vistatec.ocelot.events.LQIConfigurationSelectionChangedEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.lqi.gui.ConfigurationItem;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

/**
 * This tool lets users to choose the active configuration for the LQI grid.
 */
public class OcelotLQIConfTool extends JPanel implements ItemListener {

	/** The serial version UID. */
	private static final long serialVersionUID = -9177859185289414702L;

	/** The tool height. */
	private static final int COMBO_HEIGHT = 20;

	/** The Ocelot event queue. */
	private OcelotEventQueue eventQueue;

	/** The combo box listing the available configurations. */
	private JComboBox<ConfigurationItem> cmbConfigs;
	
	private LQIGridConfiguration prevSelectedConf;

	/**
	 * Constructor.
	 * 
	 * @param lqiGridConfigurations
	 *            the object describing the LQI grid configurations
	 * @param eventQueue
	 *            the Ocelot event queue.
	 */
	public OcelotLQIConfTool(LQIGridConfigurations lqiGridConfigurations,
	        OcelotEventQueue eventQueue) {

		this.eventQueue = eventQueue;
		cmbConfigs = new JComboBox<ConfigurationItem>();
		buildTool();
		setLQIConfigurations(lqiGridConfigurations);
		cmbConfigs.addItemListener(this);
	}

	/**
	 * Builds the tool.
	 */
	private void buildTool() {

		setOpaque(false);
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 5, 5, 5);
		add(new JLabel("LQI Grid Configuration"), c);

		c.gridx = 1;
		c.insets = new Insets(5, 0, 5, 5);
		add(cmbConfigs, c);

	}
	
	private void setComboSize(){
		final int comboWidth = getComboWidth();
		cmbConfigs.setPreferredSize(new Dimension(comboWidth, COMBO_HEIGHT));
		cmbConfigs.setMinimumSize(new Dimension(comboWidth, COMBO_HEIGHT));
		cmbConfigs.setMaximumSize(new Dimension(comboWidth, COMBO_HEIGHT));
	}
	
	/**
	 * Gets the width for the combo depending on the width of the largest item
	 * in the combo.
	 * 
	 * @return the width of the combo.
	 */
	private int getComboWidth() {

		String longestItem = getLargestComboItem();
		int maxStringWidth = cmbConfigs.getFontMetrics(cmbConfigs.getFont())
		        .stringWidth(longestItem);
		return maxStringWidth + 40;
	}

	/**
	 * Gets the largest item in the combo.
	 * 
	 * @return the largest item in the comnbo.
	 */
	private String getLargestComboItem() {

		String maxString = "";
		for (int i = 0; i < cmbConfigs.getItemCount(); i++) {
			if (cmbConfigs.getItemAt(i).toString().length() > maxString
			        .length()) {
				maxString = cmbConfigs.getItemAt(i).toString();
			}
		}
		return maxString;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		if(e.getStateChange() == ItemEvent.DESELECTED){
			prevSelectedConf = ((ConfigurationItem)e.getItem()).getConfiguration();
		} else if (e.getStateChange() == ItemEvent.SELECTED) {
			System.out.println("Selection changed: " + cmbConfigs.getSelectedItem());
			eventQueue.post(new LQIConfigurationSelectionChangedEvent(
			        ((ConfigurationItem) cmbConfigs.getSelectedItem())
			                .getConfiguration(), prevSelectedConf));
			prevSelectedConf = null;
		}
	}

	public void setLQIConfigurations(LQIGridConfigurations configurations) {

		LQIGridConfiguration currSelConf = null;
		if(cmbConfigs.getSelectedItem() != null){
			currSelConf = ((ConfigurationItem)cmbConfigs.getSelectedItem()).getConfiguration();
		}
		DefaultComboBoxModel<ConfigurationItem> comboModel = buildComboModel(configurations);
		cmbConfigs.setModel(comboModel);
		cmbConfigs.setSelectedItem(new ConfigurationItem(configurations.getActiveConfiguration()));
		if(currSelConf == null || !((ConfigurationItem)cmbConfigs.getSelectedItem()).getConfiguration().equals(currSelConf)){
			eventQueue.post(new LQIConfigurationSelectionChangedEvent(
			        ((ConfigurationItem) cmbConfigs.getSelectedItem())
			                .getConfiguration(), currSelConf));
		}
		setComboSize();
		cmbConfigs.repaint();
	}

	private DefaultComboBoxModel<ConfigurationItem> buildComboModel(LQIGridConfigurations lqiGridConfigurations) {

		ConfigurationItem[] configsArray = new ConfigurationItem[lqiGridConfigurations
		        .getConfigurations().size()];
		for (int i = 0; i < configsArray.length; i++) {
			configsArray[i] = new ConfigurationItem(lqiGridConfigurations
			        .getConfigurations().get(i));
		}
		return new DefaultComboBoxModel<ConfigurationItem>( configsArray);
	}

	
	public LQIGridConfiguration getSelectedConfiguration(){
		return ((ConfigurationItem)cmbConfigs.getSelectedItem()).getConfiguration();
	}
	
}
