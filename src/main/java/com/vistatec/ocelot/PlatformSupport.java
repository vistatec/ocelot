package com.vistatec.ocelot;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.KeyStroke;

public interface PlatformSupport {
    /**
     * Perform platform-specific initialization.
     */
    void init(Ocelot ocelot);

    /**
     * Return the platform key mask for menu shortcuts.
     */
    int getPlatformKeyMask();

    /**
     * Return true if the platform key is down.
     */
    boolean isPlatformKeyDown(KeyEvent ke);

    /**
     * Set menu mnemonics.  Mnemonics violate the Apple HIG, so
     * this is a no-op for OSX.
     */
    void setMenuMnemonics(JMenu file, JMenu view, JMenu extensions, JMenu help);

    /**
     * Get the set of reserved keys for this platform.
     */
    KeyStroke[] getReservedKeys();
}
