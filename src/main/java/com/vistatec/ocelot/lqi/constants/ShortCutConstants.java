package com.vistatec.ocelot.lqi.constants;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import com.vistatec.ocelot.PlatformSupport;

public class ShortCutConstants {

	/**
	 * <ul>
	 * <li>Ctrl + R - Opens the Filter Rules window.</li>
	 * <li>Ctrl + Shift + R - Replace the whole content of the target with the
	 * content of the selected target from the Translation/Concordance search
	 * panel</li>
	 * <li>Alt + Up - moves the selection to the upper row in the
	 * Translation/Concordance Search table.</li>
	 * <li>Alt + Down - moves the selection to the lower row in the
	 * Translation/Concordance Search table.</li>
	 * <li>Ctrl + O - Open XLIFF menu item shortcut</li>
	 * <li>Ctrl + S - Save menu item shortcut</li>
	 * <li>Ctrl + Shift + S - Save as menu item shortcut</li>
	 * <li>Ctrl + P - Provenance menu item shortcut</li>
	 * <li>Ctrl + W - Workspace menu item shortcut</li>
	 * <li>Ctrl + Equals - Add Issue menu item shortcut</li>
	 * <li>Alt + F - Opens the File menu</li>
	 * <li>Alt + V - Opens the View menu</li>
	 * <li>Alt + T - Opens the Filter menu</li>
	 * <li>Alt + E - Opens the Extensions menu</li>
	 * <li>Alt + H - Opens the Help menu</li>
	 * <li>Ctrl + Shift + F1 - ?</li>
	 * <li>Ctrl + Alt + Shift + F1 - ?</li>
	 * </ul>
	 * 
	 */
	public static List<KeyStroke> getOcelotReservedKeys(PlatformSupport platform) {
	    int platformMask = platform.getPlatformKeyMask();
	    List<KeyStroke> keys = new ArrayList<>();
	    Collections.addAll(keys,
	        KeyStroke.getKeyStroke(KeyEvent.VK_L, platformMask),
	        KeyStroke.getKeyStroke(KeyEvent.VK_R, platformMask),
	        KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_O, platformMask),
	        KeyStroke.getKeyStroke(KeyEvent.VK_S, platformMask),
	        KeyStroke.getKeyStroke(KeyEvent.VK_S, platformMask
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_P, platformMask),
	        KeyStroke.getKeyStroke(KeyEvent.VK_W, platformMask),
	        KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, platformMask),
	        KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_F1, platformMask
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_F1, platformMask
	                + KeyEvent.SHIFT_MASK + KeyEvent.SHIFT_MASK));
	    return keys;
    };

	/**
	 * <ul>
	 * <li>Space - add to selection</li>
	 * <li>Escape - Cancel</li>
	 * <li>Ctrl + Insert / Ctrl + C - Copy</li>
	 * <li>Shift + Del / Ctrl + X - Cut</li>
	 * <li>Shift + Space - Extend to</li>
	 * <li>F8 - Focus header</li>
	 * <li>Shift + Ctrl + Space - move selection to</li>
	 * <li>Shift + Insert / Ctrl + V - Paste</li>
	 * <li>Page Down - Scroll selection down</li>
	 * <li>Shift + Page Down - Scroll down extend selection</li>
	 * <li>Ctrl + Page Up - Scroll left change selection</li>
	 * <li>Ctrl + Shift + Page Up - Scroll left extend selection</li>
	 * <li>Ctrl + Page Down - Scroll right change selection</li>
	 * <li>Ctrl + Shift + Page Down - Scroll right extend selection</li>
	 * <li>Page Up - scroll up change selection</li>
	 * <li>Shift + Page Up - scroll up change selection</li>
	 * <li>Ctrl + A - Select All</li>
	 * <li>Home - Select first column</li>
	 * <li>Shift + Home - Select first column extend selection</li>
	 * <li>Ctrl + Home - Select first row</li>
	 * <li>Ctrl + Shift + Home - Select first row extend selection</li>
	 * <li>End - Select last column</li>
	 * <li>Shift + End - Select last column extend selection</li>
	 * <li>Ctrl + End - Select last row</li>
	 * <li>Ctrl + Shift + End - Select last row extend selection</li>
	 * <li>Right - Select next column</li>
	 * <li>Tab - Select next column cell</li>
	 * <li>Ctrl + Right - Select next column change lead</li>
	 * <li>Ctrl + Shift + Right / Shift + Right - Select next column extend
	 * selection</li>
	 * <li>Down - select next row</li>
	 * <li>Enter - select next row cell</li>
	 * <li>Ctrl + Down - select next row change lead</li>
	 * <li>Ctrl + Shift + Down / Shift + Down - select next row extend selection
	 * </li>
	 * <li>Left - Select previous column</li>
	 * <li>Shift + Tab - Select previous column extend selection</li>
	 * <li>Ctrl + Left - Select previous column change lead</li>
	 * <li>Ctrl + Shift + Left / Shift + Left - Select previous column extend
	 * selection</li>
	 * <li>Up - Select previous row</li>
	 * <li>Shift + Enter - Select previous row cell</li>
	 * <li>Crtl + Up - Select previous row change lead</li>
	 * <li>Crtl + Shift + Up / Shift + Up - Select previous row extend selection
	 * </li>
	 * <li>F2 - Start editing</li>
	 * <li>Ctrl + Space - toggle and anchor</li>
	 * <li>Ctrl + F1 - ?</li>
	 * <li>F6 - ?</li>
	 * <li>Ctrl + Shift + 0 - ?</li>
	 * <li>Ctrl + Shift + numPad-x - ?</li>
	 * <li>Ctrl + Alt + Shift + numPad-x - ?</li>
	 * <li>Alt + Shift + numPad-x - ?</li>
	 * <li>Shift + numPad-x - ?</li>
	 * <li>Ctrl + Alt + Shift + Decimal - ?</li>
	 * <li>Alt + Shift + Decimal - ?</li>
	 * <li>Shift + Decimal - ?</li>
	 * </ul>
	 */
	public static final KeyStroke[] SWING_RESERVED_KEYS = {

	        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.SHIFT_MASK
	                + KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0),
	        KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, KeyEvent.SHIFT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, KeyEvent.CTRL_MASK
	                + KeyEvent.SHIFT_MASK + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, KeyEvent.SHIFT_MASK
	                + KeyEvent.ALT_MASK),
	        KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, KeyEvent.SHIFT_MASK) };

	public static final int[] funcKeys = { KeyEvent.VK_BACK_SPACE,
	        KeyEvent.VK_TAB, KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE,
	        KeyEvent.VK_SPACE, KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN,
	        KeyEvent.VK_END, KeyEvent.VK_HOME, KeyEvent.VK_LEFT,
	        KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN,
	        KeyEvent.VK_INSERT, KeyEvent.VK_DELETE };

	public static final int[] numberKeys = { KeyEvent.VK_0, KeyEvent.VK_1,
	        KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5,
	        KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9 };

	public static final int[] letterKeys = { KeyEvent.VK_A, KeyEvent.VK_B,
	        KeyEvent.VK_C, KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_F,
	        KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_I, KeyEvent.VK_J,
	        KeyEvent.VK_K, KeyEvent.VK_L, KeyEvent.VK_M, KeyEvent.VK_N,
	        KeyEvent.VK_O, KeyEvent.VK_P, KeyEvent.VK_Q, KeyEvent.VK_R,
	        KeyEvent.VK_S, KeyEvent.VK_T, KeyEvent.VK_U, KeyEvent.VK_V,
	        KeyEvent.VK_W, KeyEvent.VK_X, KeyEvent.VK_Y, KeyEvent.VK_Z };

	public static final int[] numPadKeys = { KeyEvent.VK_NUMPAD0,
	        KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3,
	        KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6,
	        KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9,
	        KeyEvent.VK_ADD, KeyEvent.VK_MULTIPLY, KeyEvent.VK_DIVIDE,
	        KeyEvent.VK_SUBTRACT, KeyEvent.VK_DECIMAL };

	public static final int[] fKeys = { KeyEvent.VK_F1, KeyEvent.VK_F2,
	        KeyEvent.VK_F3, KeyEvent.VK_F4, KeyEvent.VK_F5, KeyEvent.VK_F6,
	        KeyEvent.VK_F7, KeyEvent.VK_F8, KeyEvent.VK_F9, KeyEvent.VK_F10,
	        KeyEvent.VK_F11, KeyEvent.VK_F12 };

	public static void main(String[] args) {

		JTable table = new JTable();
		if (table.getInputMap() != null && table.getInputMap().size() > 0) {
			for (KeyStroke key : table.getInputMap().allKeys()) {

				System.out.println(KeyEvent.getModifiersExText(key
				        .getModifiers())
				        + KeyEvent.getKeyText(key.getKeyCode())
				        + ": "
				        + table.getInputMap().get(key));
			}
		}
		if (table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) != null
		        && table.getInputMap(
		                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).size() > 0) {
			for (KeyStroke key : table.getInputMap(
			        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).allKeys()) {

				System.out.println(KeyEvent.getModifiersExText(key
				        .getModifiers())
				        + KeyEvent.getKeyText(key.getKeyCode())
				        + ": "
				        + table.getInputMap(
				                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				                .get(key));
			}
		}
	}

	public static List<KeyStroke> getReservedKeyList(PlatformSupport platform) {
		List<KeyStroke> keys = new ArrayList<KeyStroke>();
		keys.addAll(Arrays.asList(platform.getReservedKeys()));
		keys.addAll(getOcelotReservedKeys(platform));
		keys.addAll(Arrays.asList(SWING_RESERVED_KEYS));
		return keys;
	}

}
