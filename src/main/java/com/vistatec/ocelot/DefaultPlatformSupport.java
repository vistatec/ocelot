package com.vistatec.ocelot;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.KeyStroke;

public class DefaultPlatformSupport implements PlatformSupport {
    @Override
    public void init(Ocelot ocelot) {
    }

    @Override
    public int getPlatformKeyMask() {
        return KeyEvent.CTRL_MASK;
    }

    @Override
    public boolean isPlatformKeyDown(KeyEvent ke) {
        return ke.isControlDown();
    }

    @Override
    public void setMenuMnemonics(JMenu file, JMenu view, JMenu extensions, JMenu help) {
        file.setMnemonic(KeyEvent.VK_F);
        view.setMnemonic(KeyEvent.VK_V);
        extensions.setMnemonic(KeyEvent.VK_E);
        help.setMnemonic(KeyEvent.VK_H);
    }

    @Override
    public KeyStroke[] getReservedKeys() {
        return WINDOWS_RESERVED_KEYS;
    }

    /**
     * <ul>
     * <li>Alt + Tab - Switch between open applications</li>
     * <li>Alt + Shift + Tab - Switch between open applications in the reverse
     * direction</li>
     * <li>Ctrl + Tab - Switches between program groups, tabs, or document
     * windows</li>
     * <li>Ctrl + Shift + Tab - Switches between program groups, tabs, or
     * document windows in the reverse direction</li>
     * <li>Ctrl + Alt + Del - Open the Windows option screen for locking
     * computer, switching user, Task Manager, etc.</li>
     * <li>Ctrl + Alt + Decimal - Open the Windows option screen for locking
     * computer, switching user, Task Manager, etc.</li>
     * <li>Ctrl + Shift + Esc - Immediately bring up the Windows Task Manager</li>
     * <li>Ctrl + Esc - Open the Windows Start menu</li>
     * <li>Alt + Esc - Switch between open applications on Taskbar</li>
     * <li>Alt + Shift + Esc - Switch between open applications on Taskbar in
     * the reverse order</li>
     * <li>Alt + Space bar - Drops down the window control menu for the
     * currently open Windows program</li>
     * <li>Alt + Enter - Opens properties window of selected icon or program</li>
     * <li>F1 - Activates help for current open application.</li>
     * <li>Alt + F4 - closes the current open program window</li>
     * <li>Ctrl + F4 - closes the open window within the current active window</li>
     * <li>F10 - Activates the File menu bar in all versions of Windows.</li>
     * <li>Ctrl+C - Copy the selected item</li>
     * <li>Ctrl+X - Cut the selected item</li>
     * <li>Ctrl+V - Paste the selected item</li>
     * <li>Ctrl+A - Select all items in a document or window</li>
     * <li>Ctrl+Alt+Tab - Use the arrow keys to switch between open items</li>
     * <li>Ctrl+Alt+Shift+Tab - Use the arrow keys to switch between open items
     * <li>Alt + Shift + Space - Opens the contextual menu of the active window</li>
     * in the reverse order</li>
     * </ul>
     */
    private static final KeyStroke[] WINDOWS_RESERVED_KEYS = {

            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.ALT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.ALT_MASK
                    + KeyEvent.SHIFT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK
                    + KeyEvent.SHIFT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.ALT_MASK
                    + KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.CTRL_MASK
                    + KeyEvent.SHIFT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.ALT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.ALT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0),
            KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.ALT_MASK
                    + KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.ALT_MASK
                    + KeyEvent.SHIFT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, KeyEvent.ALT_MASK
                    + KeyEvent.CTRL_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.ALT_MASK
                    + KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK),
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.ALT_MASK
                    + KeyEvent.SHIFT_MASK) };
}
