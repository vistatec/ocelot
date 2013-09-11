package com.spartansoftwareinc.plugins;

import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import com.spartansoftwareinc.vistatec.rwb.segment.Segment;
import com.spartansoftwareinc.vistatec.rwb.segment.SegmentTableModel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detect, install, and instantiate any available plugin classes.
 * 
 * This is meant to be used by calling discover(), and then kept 
 * around to provide access to instances of all the discovered plugins.
 * The plugins themselves are instantiated immediately and are treated
 * like stateful singletons.
 */
public class PluginManager {
        private static Logger LOG = LoggerFactory.getLogger(PluginManager.class);
        private List<String> itsPluginClassNames = new ArrayList<String>();
        private List<String> segPluginClassNames = new ArrayList<String>();
        private HashMap<ITSPlugin, Boolean> itsPlugins;
        private HashMap<SegmentPlugin, Boolean> segPlugins;
	private ClassLoader classLoader;
        private File pluginDir;
	
	public PluginManager() {
            this.itsPlugins = new HashMap<ITSPlugin, Boolean>();
            this.segPlugins = new HashMap<SegmentPlugin, Boolean>();
            pluginDir = new File(System.getProperty("user.home"), ".reviewersWorkbench/plugins");
	}

        public File getPluginDir() {
            return this.pluginDir;
        }

        public void setPluginDir(File pluginDir) {
            this.pluginDir = pluginDir;
        }

        public Set<Plugin> getPlugins() {
            Set<Plugin> plugins = new HashSet<Plugin>();
            Set<? extends Plugin> itsPlugins = getITSPlugins();
            Set<? extends Plugin> segmentPlugins = getSegmentPlugins();
            plugins.addAll(itsPlugins);
            plugins.addAll(segmentPlugins);
            return plugins;
        }

	/**
	 * Get a list of available ITS plugin instances.
	 */
	public Set<ITSPlugin> getITSPlugins() {
            return this.itsPlugins.keySet();
	}

        public Set<SegmentPlugin> getSegmentPlugins() {
            return this.segPlugins.keySet();
        }

        /**
         * Return if the plugin should receive data from the workbench.
         */
        public boolean isEnabled(Plugin plugin) {
            boolean enabled = false;
            if (plugin instanceof ITSPlugin) {
                ITSPlugin itsPlugin = (ITSPlugin) plugin;
                enabled = itsPlugins.get(itsPlugin);
            } else if (plugin instanceof SegmentPlugin) {
                SegmentPlugin segPlugin = (SegmentPlugin) plugin;
                enabled = segPlugins.get(segPlugin);
            }
            return enabled;
        }

        public void setEnabled(Plugin plugin, boolean enabled) {
            if (plugin instanceof ITSPlugin) {
                ITSPlugin itsPlugin = (ITSPlugin) plugin;
                itsPlugins.put(itsPlugin, enabled);
            } else if (plugin instanceof SegmentPlugin) {
                SegmentPlugin segPlugin = (SegmentPlugin) plugin;
                segPlugins.put(segPlugin, enabled);
            }
        }

        /**
         * ITSPlugin handler for exporting LQI/Provenance metadata of segments.
         * @param sourceLang
         * @param targetLang
         * @param segments
         */
        public void exportData(String sourceLang, String targetLang,
                SegmentTableModel segments) {
            for (int row = 0; row < segments.getRowCount(); row++) {
                Segment seg = segments.getSegment(row);
                List<LanguageQualityIssue> lqi = seg.getLQI();
                List<Provenance> prov = seg.getProv();
                for (ITSPlugin plugin : getITSPlugins()) {
                    if (isEnabled(plugin)) {
                        try {
                            plugin.sendLQIData(sourceLang, targetLang,
                                    seg, lqi);
                            plugin.sendProvData(sourceLang, targetLang,
                                    seg, prov);
                        } catch (Exception e) {
                            LOG.error("ITS Plugin '"+plugin.getPluginName()
                                    +"' threw an exception on ITS metadata export", e);
                        }
                    }
                }
            }
        }

        /**
         * SegmentPlugin handler for beginning a target segment edit.
         * @param seg
         */
        public void notifySegmentTargetEnter(Segment seg) {
            for (SegmentPlugin segPlugin : segPlugins.keySet()) {
                if (isEnabled(segPlugin)) {
                    try {
                        segPlugin.onSegmentTargetEnter(seg);
                    } catch (Exception e) {
                        LOG.error("Segment plugin '"+segPlugin.getPluginName()
                                +"' threw an exception on segment target enter", e);
                    }
                }
            }
        }

        /**
         * SegmentPlugin handler for finishing a target segment edit.
         * @param seg
         */
        public void notifySegmentTargetExit(Segment seg) {
            for (SegmentPlugin segPlugin : segPlugins.keySet()) {
                if (isEnabled(segPlugin)) {
                    try {
                        segPlugin.onSegmentTargetExit(seg);
                    } catch (Exception e) {
                        LOG.error("Segment plugin '"+segPlugin.getPluginName()
                                +"' threw an exception on segment target exit", e);
                    }
                }
            }
        }

        public void notifyOpenFile(String filename) {
            for (SegmentPlugin segPlugin : segPlugins.keySet()) {
                if (isEnabled(segPlugin)) {
                    try {
                        segPlugin.onFileOpen(filename);
                    } catch (Exception e) {
                        LOG.error("Segment plugin '"+segPlugin.getPluginName()
                                +"' threw an exception on file open", e);
                    }
                }
            }
        }

        public void notifySaveFile(String filename) {
            for (SegmentPlugin segPlugin : segPlugins.keySet()) {
                if (isEnabled(segPlugin)) {
                    try {
                        segPlugin.onFileSave(filename);
                    } catch (Exception e) {
                        LOG.error("Segment plugin '"+segPlugin.getPluginName()
                                +"' threw an exception on file save", e);
                    }
                }
            }
        }

	/**
	 * Search the provided directory for any JAR files containing valid 
	 * plugin classes.  Instantiate and configure any such classes.
	 * @param pluginDirectory
	 * @throws IOException if something goes wrong reading the directory
	 */
	public void discover(File pluginDirectory) throws IOException {
		if (!pluginDirectory.isDirectory()) {
			return;
		}
		
		File[] jarFiles = pluginDirectory.listFiles(new JarFilenameFilter());
		
		installClassLoader(jarFiles);
		
		for (File f : jarFiles) {
			scanJar(f);
		}
		
		for (String s : itsPluginClassNames) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends ITSPlugin> c = (Class<ITSPlugin>)Class.forName(s, false, classLoader);
				ITSPlugin plugin = c.newInstance();
				itsPlugins.put(plugin, false);
			}
			catch (ClassNotFoundException e) {
				// XXX Shouldn't happen?
				System.out.println("Warning: " + e.getMessage());
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
                for (String s : segPluginClassNames) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends SegmentPlugin> c = (Class<SegmentPlugin>)Class.forName(s, false, classLoader);
				SegmentPlugin plugin = c.newInstance();
				segPlugins.put(plugin, false);
			}
			catch (ClassNotFoundException e) {
				// XXX Shouldn't happen?
				System.out.println("Warning: " + e.getMessage());
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void installClassLoader(File[] jarFiles) throws IOException {
		final List<URL> pluginJarURLs = new ArrayList<URL>();
		for (File file : jarFiles) {
			// Make sure that this is actually a real jar
			if (!isValidJar(file)) {
				continue;
			}
			// TODO - this may break when the path contains whitespace
			URL url = file.toURI().toURL();
			pluginJarURLs.add(url);
		}

		classLoader = AccessController.doPrivileged(
				new PrivilegedAction<URLClassLoader>() {
					public URLClassLoader run() {
						return new URLClassLoader(
								pluginJarURLs.toArray(new URL[pluginJarURLs.size()]),
								Thread.currentThread().getContextClassLoader());
					}
				});		
	}
	
	void scanJar(final File file) {
		try {
			Enumeration<JarEntry> e = new JarFile(file).entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				String name = entry.getName();
				if (name.endsWith(".class")) {
					name = convertFileNameToClass(name);
					try {
						Class<?> clazz = Class.forName(name, false, classLoader);
						// Skip non-instantiable classes
						if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
							continue;
						}
						if (ITSPlugin.class.isAssignableFrom(clazz)) {
							// It's a plugin!  Just store the name for now
							// since we will need to reinstantiate it later with the 
							// real classloader (I think)
							if (itsPluginClassNames.contains(name)) {
								// TODO: log this
								System.out.println("Warning: found multiple implementations of plugin class " +
												   name);
							}
							else {
								itsPluginClassNames.add(name);
							}
						} else if (SegmentPlugin.class.isAssignableFrom(clazz)) {
							// It's a plugin!  Just store the name for now
							// since we will need to reinstantiate it later with the 
							// real classloader (I think)
							if (segPluginClassNames.contains(name)) {
								// TODO: log this
								System.out.println("Warning: found multiple implementations of plugin class " +
												   name);
							}
							else {
								segPluginClassNames.add(name);
							}
						}
					}
					catch (ClassNotFoundException ex) {
						// XXX shouldn't happen?
						System.out.println("Warning: " + ex.getMessage());
					}
				}
			}
		}
		catch (IOException e) {
			// XXX Log this and continue
			e.printStackTrace();
		}
	}

	private boolean isValidJar(File f) throws IOException {
		JarInputStream is = new JarInputStream(new FileInputStream(f));
		boolean rv = (is.getNextEntry() != null);
		is.close();
		return rv;
	}
	
	// Convert file name to a java class name
	private String convertFileNameToClass(String filename) {
		String s = filename.substring(0, filename.length() - 6);
		return s.replace('/', '.');
	}
	
	class JarFilenameFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String filename) {
			if (filename == null || filename.equals("")) {
				return false;
			}
			int i = filename.lastIndexOf('.');
			if (i == -1) {
				return false;
			}
			String s = filename.substring(i);
			return s.equalsIgnoreCase(".jar");
		}
	}
}
