package com.vistatec.ocelot.lqi.gui.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.vistatec.ocelot.lqi.constants.ShortCutConstants;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

/**
 * The shortcut dialog.
 */
public class ShortCutDialog extends JDialog implements Runnable, ActionListener {

	/** The serial version UID. */
	private static final long serialVersionUID = 2783798014624857076L;

	/** The dialog width. */
	private static final int WIDTH = 300;

	/** The dialog height. */
	private static final int HEIGHT = 200;

	/** The Ctrl check box */
	private JCheckBox ckbCtrl;

	/** The Alt check box */
	private JCheckBox ckbAlt;

	/** The Shift check box */
	private JCheckBox ckbShift;

	/** The combo listing the available keys. */
	private JComboBox<KeyItem> cmbKeys;

	/** Ok button */
	private JButton btnOk;

	/** Cancel button */
	private JButton btnCancel;

	/** The error category name. */
	private String errorCatTitle;

	/** The shortcut. */
	private LQIShortCut lqiShortcut;


	/**
	 * Constructor.
	 * @param gridDialog the LQI grid dialog.
	 * @param errorCatTitle the error category name
	 * @param lqiShortCut the shortcut.
	 */
	public ShortCutDialog(LQIConfigurationEditDialog owner, String errorCatTitle,
	        LQIShortCut lqiShortCut) {

		super(owner);
		setModal(true);
		this.errorCatTitle = errorCatTitle;
		this.lqiShortcut = lqiShortCut;
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {

		setTitle("Short Cut");
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setSize(new Dimension(WIDTH, HEIGHT));
		add(getTopComponent(), BorderLayout.NORTH);
		add(getCenterComponent(), BorderLayout.CENTER);
		add(getBottomComponent(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(getOwner());
		load();
	}

	/**
	 * Loads theshortcut and fills in the fields.
	 */
	private void load() {

		if (lqiShortcut != null) {
			cmbKeys.setSelectedItem(new KeyItem(lqiShortcut.getKeyCode()));
			if (lqiShortcut.getModifiers() != null) {
				for (int mod : lqiShortcut.getModifiers()) {
					switch (mod) {
					case KeyEvent.CTRL_DOWN_MASK:
						ckbCtrl.setSelected(true);
						break;
					case KeyEvent.ALT_DOWN_MASK:
						ckbAlt.setSelected(true);
						break;
					case KeyEvent.SHIFT_DOWN_MASK:
						ckbShift.setSelected(true);
						break;
					default:
						break;
					}
				}
			}
		}
	}

	/**
	 * Gets the component to be displayed at the top.
	 * @return the component to be displayed at the top.
	 */
	private Component getTopComponent() {

		JPanel topPanel = new JPanel();
		JLabel catLabel = new JLabel(errorCatTitle);
		topPanel.add(catLabel);
		return topPanel;
	}

	/**
	 * Gets the component to be displayed at the center.
	 * @return the component to be displayed at the center.
	 */
	private Component getCenterComponent() {

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		ckbAlt = new JCheckBox("ALT");
		ckbCtrl = new JCheckBox("CTRL");
		ckbShift = new JCheckBox("SHIFT");
		cmbKeys = new JComboBox<KeyItem>(buildKeyComboModel());
		JLabel plus1 = new JLabel("<html> <font size=\"5\">+</font></html>");
		JLabel plus2 = new JLabel("<html> <font size=\"5\">+</font></html>");

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		centerPanel.add(ckbCtrl, c);

		c.gridx = 0;
		c.gridy = 2;
		centerPanel.add(ckbAlt, c);

		c.gridx = 1;
		c.gridy = 1;
		centerPanel.add(plus1, c);
		c.gridx = 2;
		centerPanel.add(ckbShift, c);
		c.gridx = 3;
		centerPanel.add(plus2, c);
		c.gridx = 4;
		c.insets = new Insets(5, 5, 0, 10);
		centerPanel.add(cmbKeys, c);
		return centerPanel;
	}

	/**
	 * Builds the model for the combo listing available keys.
	 * @return the combo model. 
	 */
	private KeyItem[] buildKeyComboModel() {

		int keyItemsSize = ShortCutConstants.funcKeys.length
		        + ShortCutConstants.numberKeys.length
		        + ShortCutConstants.letterKeys.length
		        + ShortCutConstants.numPadKeys.length
		        + ShortCutConstants.fKeys.length;
		KeyItem[] keyItems = new KeyItem[keyItemsSize];
		int startIndex = 0;
		startIndex = fillKeyItemArray(keyItems, ShortCutConstants.funcKeys,
		        startIndex);
		startIndex = fillKeyItemArray(keyItems, ShortCutConstants.numberKeys,
		        startIndex);
		startIndex = fillKeyItemArray(keyItems, ShortCutConstants.letterKeys,
		        startIndex);
		startIndex = fillKeyItemArray(keyItems, ShortCutConstants.numPadKeys,
		        startIndex);
		startIndex = fillKeyItemArray(keyItems, ShortCutConstants.fKeys,
		        startIndex);
		return keyItems;
	}

	/**
	 * Fills the key items array with keys from a different array.
	 * @param keyItemArray the key items array
	 * @param keyArray the keys array
	 * @param startIndex the start index
	 * @return the new start index.
	 */
	private int fillKeyItemArray(KeyItem[] keyItemArray, int[] keyArray,
	        int startIndex) {

		for (int i = 0; i < keyArray.length; i++) {
			keyItemArray[i + startIndex] = new KeyItem(keyArray[i]);
		}
		return startIndex + keyArray.length;
	}

	/**
	 * Gets the component to be displayed at the bottom.
	 * @return the component to be displayed at the bottom.
	 */
	private Component getBottomComponent() {

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnOk = new JButton("Ok");
		btnOk.addActionListener(this);
		bottomPanel.add(btnOk);
		bottomPanel.add(btnCancel);
		return bottomPanel;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		init();
		setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnCancel)) {
			close();
		} else if (e.getSource().equals(btnOk)) {
			save();
		}
	}

	/**
	 * Saves the data.
	 */
	private void save() {

		int[] modifiers = new int[3];
		if (ckbCtrl.isSelected()) {
			modifiers[0] = KeyEvent.CTRL_DOWN_MASK;
		}
		if (ckbAlt.isSelected()) {
			modifiers[1] = KeyEvent.ALT_DOWN_MASK;
		}
		if (ckbShift.isSelected()) {
			modifiers[2] = KeyEvent.SHIFT_DOWN_MASK;
		}
		int keyCode = ((KeyItem) cmbKeys.getSelectedItem()).getKey();
		LQIConfigurationEditDialog lqiDialog = (LQIConfigurationEditDialog) getOwner();
		if (lqiDialog.isReservedShortcut(keyCode, modifiers)) {
			JOptionPane
			        .showMessageDialog(
			                this,
			                "This is a reserved shortcut. Please, choose a different one.",
			                "Reserved Short Cut", JOptionPane.WARNING_MESSAGE);
		} else {
			((LQIConfigurationEditDialog) getOwner()).saveShortcut(
			        ((KeyItem) cmbKeys.getSelectedItem()).getKey(), modifiers);
			close();
		}

	}

	/**
	 * Closes the dialog.
	 */
	private void close() {
		setVisible(false);
		dispose();
	}
}

/**
 * Key item class. Objects from this class are the items for the keys combo.
 */
class KeyItem {

	/** The key */
	private int key;

	/**
	 * Constructor.
	 * @param key the key. 
	 */
	public KeyItem(int key) {
		this.key = key;
	}

	/**
	 * Sets the key.
	 * @param key the key.
	 */
	public void setKey(int key) {
		this.key = key;
	}

	/**
	 * Gets the key.
	 * @return the key.
	 */
	public int getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return KeyEvent.getKeyText(key);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		boolean retValue = false;
		if (obj instanceof KeyItem) {
			retValue = key == ((KeyItem) obj).getKey();
		} else {
			retValue = super.equals(obj);
		}
		return retValue;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return key;
	}
}
