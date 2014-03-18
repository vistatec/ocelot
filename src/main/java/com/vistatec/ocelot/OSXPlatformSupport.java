package com.vistatec.ocelot;

import java.awt.event.ActionListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

/**
 * Additional platform integration for Mac OSX.  We load the relevant classes
 * via reflection to avoid explicit dependencies on the Mac-specific version
 * of the JDK.
 */
public class OSXPlatformSupport {
    private static Logger LOG = Logger.getLogger(OSXPlatformSupport.class);
    private static Application app;

    public static void init() {
        try {
            app = new Application();
            // Prevent sudden termination:
            Method disableTerm = app.getAppClass().getDeclaredMethod("disableSuddenTermination");
            disableTerm.invoke(app.getAppInstance());
        } catch (Exception e) {
            LOG.warn(e);
        }
    }

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
            LOG.warn(e);
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
            LOG.warn(e);;
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
