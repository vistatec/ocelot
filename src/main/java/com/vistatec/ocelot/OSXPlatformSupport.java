package com.vistatec.ocelot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.JMenu;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Additional platform integration for Mac OSX.  We load the relevant classes
 * via reflection to avoid explicit dependencies on the Mac-specific version
 * of the JDK.
 */
public class OSXPlatformSupport implements PlatformSupport {
    private static Logger LOG = LoggerFactory.getLogger(OSXPlatformSupport.class);
    private static Application app;

    public synchronized void init(final Ocelot ocelot) {
        try {
            if (app == null) {
                app = new Application();
                // Prevent sudden termination:
                Method disableTerm = app.getAppClass().getDeclaredMethod("disableSuddenTermination");
                disableTerm.invoke(app.getAppInstance());
            }
            setQuitHandler(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ocelot.handleApplicationExit();
                }
            });
            setAboutHandler(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ocelot.showAbout();
                }
            });
        } catch (Exception e) {
            LOG.warn("Failed to initialize OS X UI features", e);
        }
    }

    @Override
    public int getPlatformKeyMask() {
        return KeyEvent.META_MASK;
    }

    @Override
    public boolean isPlatformKeyDown(KeyEvent ke) {
        return ke.isMetaDown();
    }

    @Override
    public void setMenuMnemonics(JMenu file, JMenu view, JMenu extensions, JMenu help) {
        // Mac doesn't use these
    }

    @Override
    public KeyStroke[] getReservedKeys() {
        return MACOSX_RESERVED_KEYS;
    }

    /**
     * <ul>
     * <li>Command + Tab - Switch between open applications</li>
     * <li>Command + Shift + Tab - Switch between open applications in the reverse
     * direction</li>
     * <li>Ctrl + Tab - Switches between program groups, tabs, or document
     * windows</li>
     * <li>Ctrl + Shift + Tab - Switches between program groups, tabs, or
     * document windows in the reverse direction</li>
     * <li>Command + Option + Esc - Open Force Quit window</li>
     * <li>Command + Space bar - Open Spotlight Search</li>
     * <li>Command + Q - Quit application</li>
     * <li>Command + W - Close current application window</li>
     * <li>Command+C - Copy the selected item</li>
     * <li>Command+X - Cut the selected item</li>
     * <li>Command+V - Paste the selected item</li>
     * <li>Command+A - Select all items in a document or window</li>
     * </ul>
     */
    private static final KeyStroke[] MACOSX_RESERVED_KEYS = {
        KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.META_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.META_MASK
                + KeyEvent.SHIFT_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK
                + KeyEvent.SHIFT_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.META_MASK
                + KeyEvent.ALT_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.META_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.META_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.META_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_MASK)
    };

    /**
     * Attach a handler to be called from the Mac OSX native 
     * "About" menu item (as opposed to the one in Ocelot's own 
     * menu bar.)
     * This is done by wrapping the ActionListener to respond to
     * com.apple.eawt.AboutHandler#handleAbout
     * 
     * @param al action listener
     */
    public static void setAboutHandler(final ActionListener al) {
        try {
            Class<?> aboutHandlerClass = Class.forName("com.apple.eawt.AboutHandler");
            InvocationHandler ih = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                    if (method.getName().equals("handleAbout")) {
                        // Respond to handleAbout(com.apple.eawt.AppEvent.AboutEvent)
                        al.actionPerformed(null);
                    }
                    return null;
                }
            };
            app.setAppMethod("setAboutHandler", aboutHandlerClass, ih);
        } catch (Exception e) {
            LOG.warn("Failed to initialize OS X UI features", e);
        }
    }

    /**
     * Attach a handler to be called from the Mac OSX native
     * "Quit" menu item (as opposed to the one in Ocelot's own
     * menu bar.)
     * This is done by wrapping the ActionListener to respond
     * to com.apple.eawt.QuitHandler#handleQuitResquestWith
     * @param al action listener
     */
    public static void setQuitHandler(final ActionListener al) {
        try {
            Class<?> quitHandlerClass = Class.forName("com.apple.eawt.QuitHandler");
            InvocationHandler ih = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                    if (method.getName().equals("handleQuitRequestWith")) {
                        // Invoke our handler, then allow the quit to proceed.
                        al.actionPerformed(null);
                        Class<?> quitResponseClass = Class.forName("com.apple.eawt.QuitResponse");
                        if (args != null && args.length > 1 && quitResponseClass.isInstance(args[1])) {
                            Method performQuit = quitResponseClass.getDeclaredMethod("performQuit");
                            performQuit.invoke(args[1]);
                        }
                    }
                    return null;
                }
            };
            app.setAppMethod("setQuitHandler", quitHandlerClass, ih);
        } catch (Exception e) {
            LOG.warn("Failed to initialize OS X UI features", e);
        }
    }

    private static class Application {
        private Class<?> appClass;
        private Object appInstance;
        
        Application() throws Exception {
            appClass = Class.forName("com.apple.eawt.Application");
            appInstance = appClass.getDeclaredMethod("getApplication").invoke(null);
        }

        Class<?> getAppClass() {
            return appClass;
        }

        Object getAppInstance() {
            return appInstance;
        }

        void setAppMethod(String method, Class<?> handlerClass, InvocationHandler handler) 
                                                throws Exception {
            Object wrapper = Proxy.newProxyInstance(OSXPlatformSupport.class.getClassLoader(),
                    new Class<?>[] { handlerClass}, handler);
            Method m = appClass.getDeclaredMethod(method, handlerClass);
            m.invoke(appInstance, wrapper);
        }
    }

}
