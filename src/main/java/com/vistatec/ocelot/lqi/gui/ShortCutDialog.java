package com.vistatec.ocelot.lqi.gui;

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
import com.vistatec.ocelot.lqi.model.LQIErrorCategory.LQIShortCut;

public class ShortCutDialog extends JDialog implements Runnable, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2783798014624857076L;

	// private final int[] symbolKeys = {
	// KeyEvent.getExtendedKeyCodeForChar('~'),
	// KeyEvent.getExtendedKeyCodeForChar('-'),
	// KeyEvent.getExtendedKeyCodeForChar('='),
	// KeyEvent.getExtendedKeyCodeForChar('['),
	// KeyEvent.getExtendedKeyCodeForChar(']'),
	// KeyEvent.getExtendedKeyCodeForChar(';'),
	// KeyEvent.getExtendedKeyCodeForChar('\''),
	// KeyEvent.getExtendedKeyCodeForChar('\\'),
	// KeyEvent.getExtendedKeyCodeForChar(','),
	// KeyEvent.getExtendedKeyCodeForChar('.'),
	// KeyEvent.getExtendedKeyCodeForChar('<'),
	// KeyEvent.getExtendedKeyCodeForChar('>') };

	private static final int WIDTH = 300;

	private static final int HEIGHT = 200;

	private JCheckBox ckbCtrl;

	private JCheckBox ckbAlt;

	private JCheckBox ckbShift;

	private JComboBox<KeyItem> cmbKeys;

	private JButton btnOk;

	private JButton btnCancel;

	private String errorCatTitle;

	private LQIShortCut lqiShortcut;

	// private LQIGridDialog gridDialog;

	public ShortCutDialog(LQIGridDialog gridDialog, String errorCatTitle,
	        LQIShortCut lqiShortCut) {

		super(gridDialog);
		setModal(true);
		// this.gridDialog = gridDialog;
		this.errorCatTitle = errorCatTitle;
		this.lqiShortcut = lqiShortCut;
	}

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

	private Component getTopComponent() {

		JPanel topPanel = new JPanel();
		JLabel catLabel = new JLabel(errorCatTitle);
		// catLabel.setFont(catLabel.getFont().deriveFont(Font.BOLD));
		topPanel.add(catLabel);
		return topPanel;
	}

	private Component getCenterComponent() {

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		ckbAlt = new JCheckBox("ALT");
		ckbCtrl = new JCheckBox("CTRL");
		ckbShift = new JCheckBox("SHIFT");
		cmbKeys = new JComboBox<KeyItem>(buildKeyComboModel());
		JLabel plus1 = new JLabel("<html> <font size=\"5\">+</font></html>");
		// plus1.setFont(plus1.getFont().deriveFont(20));
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

	private int fillKeyItemArray(KeyItem[] keyItemArray, int[] keyArray,
	        int startIndex) {

		for (int i = 0; i < keyArray.length; i++) {
			keyItemArray[i + startIndex] = new KeyItem(keyArray[i]);
		}
		return startIndex + keyArray.length;
	}

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

	@Override
	public void run() {

		init();
		setVisible(true);
	}

	public static void main(String[] args) {

		System.out.println(KeyEvent.getModifiersExText(KeyEvent.CTRL_DOWN_MASK
		        + KeyEvent.ALT_DOWN_MASK));
		String shortcut = KeyEvent.getModifiersExText(KeyEvent.CTRL_DOWN_MASK
		        + KeyEvent.ALT_DOWN_MASK)
		        + "+" + KeyEvent.getKeyText(KeyEvent.VK_2);
		System.out.println(shortcut);
		System.out.println(KeyEvent.getModifiersExText(0));
		System.out
		        .println(KeyEvent.getModifiersExText(KeyEvent.CTRL_DOWN_MASK));

		// KeyStroke key = KeyStroke.getKeyStroke('J', 0);
		// System.out.println(key.getKeyChar() + " - " +
		// KeyEvent.getKeyModifiersText(0));

		// System.out.println(KeyEvent.get);

		// JFrame frame = new JFrame();
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setLocationRelativeTo(null);
		// frame.setVisible(true);
		//
		// ShortCutDialog dialog = new ShortCutDialog(frame,
		// "Terminology - Minor");
		// SwingUtilities.invokeLater(dialog);

		// int[] funcKeys = {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_TAB,
		// KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE, KeyEvent.VK_SPACE,
		// KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_END,
		// KeyEvent.VK_HOME, KeyEvent.VK_LEFT, KeyEvent.VK_UP,
		// KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_INSERT,
		// KeyEvent.VK_DELETE};
		// int[] numberKeys = {KeyEvent.VK_0, KeyEvent.VK_1, KeyEvent.VK_2,
		// KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6,
		// KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9 };
		// int[] letterKeys = {KeyEvent.VK_A, KeyEvent.VK_B, KeyEvent.VK_C,
		// KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_F, KeyEvent.VK_G,
		// KeyEvent.VK_H, KeyEvent.VK_I, KeyEvent.VK_J, KeyEvent.VK_K,
		// KeyEvent.VK_L, KeyEvent.VK_M, KeyEvent.VK_N, KeyEvent.VK_O,
		// KeyEvent.VK_P, KeyEvent.VK_Q, KeyEvent.VK_R, KeyEvent.VK_S,
		// KeyEvent.VK_T, KeyEvent.VK_U, KeyEvent.VK_V, KeyEvent.VK_W,
		// KeyEvent.VK_X, KeyEvent.VK_Y, KeyEvent.VK_Z};
		// int[] numPadKeys = {KeyEvent.VK_NUMPAD0, KeyEvent.VK_NUMPAD1,
		// KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3, KeyEvent.VK_NUMPAD4,
		// KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD7,
		// KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9, KeyEvent.VK_ADD,
		// KeyEvent.VK_MULTIPLY, KeyEvent.VK_DIVIDE, KeyEvent.VK_SUBTRACT,
		// KeyEvent.VK_DECIMAL};
		// int[] fKeys = {KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3,
		// KeyEvent.VK_F4, KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7,
		// KeyEvent.VK_F8, KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11,
		// KeyEvent.VK_F12};
		// int[] symbolKeys = {KeyEvent.getExtendedKeyCodeForChar('~'),
		// KeyEvent.getExtendedKeyCodeForChar('-'),
		// KeyEvent.getExtendedKeyCodeForChar('='),
		// KeyEvent.getExtendedKeyCodeForChar('['),
		// KeyEvent.getExtendedKeyCodeForChar(']'),
		// KeyEvent.getExtendedKeyCodeForChar(';'),
		// KeyEvent.getExtendedKeyCodeForChar('\''),
		// KeyEvent.getExtendedKeyCodeForChar('\\'),
		// KeyEvent.getExtendedKeyCodeForChar(','),
		// KeyEvent.getExtendedKeyCodeForChar('.'),
		// KeyEvent.getExtendedKeyCodeForChar('<'),
		// KeyEvent.getExtendedKeyCodeForChar('>')};
		// for(int key: funcKeys ){
		// System.out.println(KeyEvent.getKeyText(key));
		// }
		// for(int numKey: numberKeys ){
		// System.out.println(KeyEvent.getKeyText(numKey));
		// }
		// for(int letterKey: letterKeys ){
		// System.out.println(KeyEvent.getKeyText(letterKey));
		// }
		// for(int numPadKey: numPadKeys ){
		// System.out.println(KeyEvent.getKeyText(numPadKey));
		// }
		// for(int fKey: fKeys ){
		// System.out.println(KeyEvent.getKeyText(fKey));
		// }
		// for(int symbolKey: symbolKeys ){
		// System.out.println(KeyEvent.getKeyText(symbolKey));
		// }

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnCancel)) {
			close();
		} else if (e.getSource().equals(btnOk)) {
			save();
		}
	}

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
		LQIGridDialog lqiDialog = (LQIGridDialog) getOwner();
		if (lqiDialog.isReservedShortcut(keyCode, modifiers)) {
			JOptionPane
			        .showMessageDialog(
			                this,
			                "This is a reserved shortcut. Please, choose a different one.",
			                "Reserved Short Cut", JOptionPane.WARNING_MESSAGE);
		} else {
			((LQIGridDialog) getOwner()).saveShortcut(
			        ((KeyItem) cmbKeys.getSelectedItem()).getKey(), modifiers);
			close();
		}

	}

	private void close() {
		setVisible(false);
		dispose();
	}
}

class KeyItem {

	private int key;

	public KeyItem(int key) {
		this.key = key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}

	@Override
	public String toString() {

		return KeyEvent.getKeyText(key);
	}

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

	@Override
	public int hashCode() {
		return key;
	}
}
