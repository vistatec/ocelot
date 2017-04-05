/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.plugins;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.events.EnrichingStartedStoppedEvent;
import com.vistatec.ocelot.events.EnrichmentViewEvent;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIConfigurationSelectionChangedEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.PluginAddedEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentTargetEnterEvent;
import com.vistatec.ocelot.events.SegmentTargetExitEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.freme.gui.EnrichmentFrame;
import com.vistatec.ocelot.freme.gui.LDGraphFrame;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.plugins.ReportPlugin.ReportException;
import com.vistatec.ocelot.plugins.freme.FremeMenu;
import com.vistatec.ocelot.plugins.freme.FremePlugin;
import com.vistatec.ocelot.plugins.freme.FremePluginManager;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.services.SegmentService;

/**
 * Detect, install, and instantiate any available plugin classes.
 * 
 * This is meant to be used by calling discover(), and then kept around to
 * provide access to instances of all the discovered plugins. The plugins
 * themselves are instantiated immediately and are treated like stateful
 * singletons.
 */
public class PluginManager implements OcelotEventQueueListener {
	private static Logger LOG = LoggerFactory.getLogger(PluginManager.class);
	private List<String> itsPluginClassNames = new ArrayList<String>();
	private List<String> segPluginClassNames = new ArrayList<String>();
	private List<String> reportPluginClassNames = new ArrayList<String>();
	private List<String> fremePluginClassNames = new ArrayList<String>();
	private List<String> qualityPluginClassNames = new ArrayList<String>();
	private List<String> timerPluginClassNames = new ArrayList<String>();
	private HashMap<ITSPlugin, Boolean> itsPlugins;
	private HashMap<SegmentPlugin, Boolean> segPlugins;
	private HashMap<ReportPlugin, Boolean> reportPlugins;
	private HashMap<FremePlugin, Boolean> fremePlugins;
	private HashMap<TimerPlugin, Boolean> timerPlugins;
	private FremePluginManager fremeManager;
	private OcelotEventQueue eventQueue;
	private ClassLoader classLoader;
	private File pluginDir;
	private final JsonConfigService cfgService;
	private QualityPluginManager qualityPluginManager;
	private JMenu reportMenu;

	public PluginManager(JsonConfigService cfgService, File pluginDir,
			OcelotEventQueue eventQueue) {
		this.itsPlugins = new HashMap<ITSPlugin, Boolean>();
		this.segPlugins = new HashMap<SegmentPlugin, Boolean>();
		this.reportPlugins = new HashMap<ReportPlugin, Boolean>();
		this.timerPlugins = new HashMap<TimerPlugin, Boolean>();
        this.fremePlugins = new HashMap<FremePlugin, Boolean>();
		this.fremeManager = new FremePluginManager(eventQueue);
		this.eventQueue = eventQueue;
		this.cfgService = cfgService;
		this.pluginDir = pluginDir;
		qualityPluginManager = new QualityPluginManager();
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
		Set<? extends Plugin> reportPlugins = getReportPlugins();
        Set<? extends Plugin> fremePlugins = getFremePlugins();
        Set<? extends Plugin> qualityPlugins = getQualityPlugins();
        Set<? extends Plugin> timerPlugins = getTimerPlugins();
		plugins.addAll(itsPlugins);
		plugins.addAll(segmentPlugins);
		plugins.addAll(reportPlugins);
        plugins.addAll(fremePlugins);
        plugins.addAll(qualityPlugins);
        plugins.addAll(timerPlugins);
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

	public Set<ReportPlugin> getReportPlugins() {
		return this.reportPlugins.keySet();
	}

	public Set<FremePlugin> getFremePlugins() {
		return this.fremePlugins.keySet();
	}

	public Set<QualityPlugin> getQualityPlugins() {
		return qualityPluginManager.getPlugins().keySet();
	}
        
    public Set<TimerPlugin> getTimerPlugins() {
    	return this.timerPlugins.keySet();
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
		} else if (plugin instanceof ReportPlugin) {
			ReportPlugin reportPlugin = (ReportPlugin) plugin;
			enabled = reportPlugins.get(reportPlugin);
		} else if (plugin instanceof FremePlugin) {
			FremePlugin fremePlugin = (FremePlugin) plugin;
			enabled = fremePlugins.get(fremePlugin);
		} else if (plugin instanceof QualityPlugin) {
			QualityPlugin qualityPlugin = (QualityPlugin) plugin;
			enabled = qualityPluginManager.getPlugins().get(qualityPlugin);
		} else if (plugin instanceof TimerPlugin) {
			TimerPlugin timerPlugin = (TimerPlugin) plugin;
			enabled = timerPlugins.get(timerPlugin);
		}
		return enabled;
	}

	public void setEnabled(Plugin plugin, boolean enabled)
	        throws TransferException {
		if (plugin instanceof ITSPlugin) {
			ITSPlugin itsPlugin = (ITSPlugin) plugin;
			itsPlugins.put(itsPlugin, enabled);
		} else if (plugin instanceof SegmentPlugin) {
			SegmentPlugin segPlugin = (SegmentPlugin) plugin;
			segPlugins.put(segPlugin, enabled);
		} else if (plugin instanceof ReportPlugin) {
			ReportPlugin reportPlugin = (ReportPlugin) plugin;
			reportPlugins.put(reportPlugin, enabled);
			reportMenu.setEnabled(enabled);
		} else if (plugin instanceof FremePlugin) {
			FremePlugin fremePlugin = (FremePlugin) plugin;
			fremePlugins.put(fremePlugin, enabled);
			if (fremeManager.getFremeMenu(fremePlugin) != null) {
				fremeManager.setFremeMenuEnabled(enabled);
			}
		} else if (plugin instanceof QualityPlugin) {
			QualityPlugin qualityPlugin = (QualityPlugin) plugin;
			qualityPluginManager.enablePlugin(qualityPlugin, enabled);
		} else if (plugin instanceof TimerPlugin) {
			TimerPlugin timerPlugin = (TimerPlugin) plugin;
			timerPlugins.put(timerPlugin, enabled);
			timerPlugin.getTimerWidget().setEnabled(enabled);
		}
		cfgService.savePluginEnabled(plugin, enabled);
	}

	/**
	 * Return the set of all {@link ITSPlugin} that are currently enabled.
	 * 
	 * @return set of enabled plugins
	 */
	public Set<ITSPlugin> getEnabledITSPlugins() {
		Set<ITSPlugin> enabled = new HashSet<ITSPlugin>();
		for (ITSPlugin plugin : getITSPlugins()) {
			if (isEnabled(plugin)) {
				enabled.add(plugin);
			}
		}
		return enabled;
	}

	/**
	 * ITSPlugin handler for exporting LQI/Provenance metadata of segments.
	 * 
	 * @param sourceLang
	 * @param targetLang
	 * @param segmentService
	 */
	public void exportData(String sourceLang, String targetLang,
	        SegmentService segmentService) {
		for (int row = 0; row < segmentService.getNumSegments(); row++) {
			OcelotSegment seg = segmentService.getSegment(row);
			List<LanguageQualityIssue> lqi = seg.getLQI();
			List<Provenance> prov = seg.getProvenance();
			for (ITSPlugin plugin : getEnabledITSPlugins()) {
				try {
					plugin.sendLQIData(sourceLang, targetLang, seg, lqi);
					plugin.sendProvData(sourceLang, targetLang, seg, prov);
				} catch (Exception e) {
					LOG.error("ITS Plugin '" + plugin.getPluginName()
					        + "' threw an exception on ITS metadata export", e);
				}
			}
		}
	}

	/**
	 * SegmentPlugin handler for beginning a target segment edit.
	 * 
	 * @param event
	 */
	@Subscribe
	public void notifySegmentTargetEnter(SegmentTargetEnterEvent event) {
		OcelotSegment seg = event.getSegment();
		for (SegmentPlugin segPlugin : segPlugins.keySet()) {
			if (isEnabled(segPlugin)) {
				try {
					segPlugin.onSegmentTargetEnter(seg);
				} catch (Exception e) {
					LOG.error("Segment plugin '" + segPlugin.getPluginName()
					        + "' threw an exception on segment target enter", e);
				}
			}
		}
		handleTimerOnUserAction();
	}

	/**
	 * SegmentPlugin handler for finishing a target segment edit.
	 * 
	 * @param event
	 */
	@Subscribe
	public void notifySegmentTargetExit(SegmentTargetExitEvent event) {
		OcelotSegment seg = event.getSegment();
		for (SegmentPlugin segPlugin : segPlugins.keySet()) {
			if (isEnabled(segPlugin)) {
				try {
					segPlugin.onSegmentTargetExit(seg);
				} catch (Exception e) {
					LOG.error("Segment plugin '" + segPlugin.getPluginName()
					        + "' threw an exception on segment target exit", e);
				}
			}
		}
	}

	@Subscribe
	public void enrichmentViewRequest(EnrichmentViewEvent e) {
		try {
			if (e.getViewType() == EnrichmentViewEvent.STD_VIEW) {
				EnrichmentFrame enrichFrame = new EnrichmentFrame(
				        e.getVariant(), null);
				SwingUtilities.invokeLater(enrichFrame);
			} else if(isFremePluginEnabled()){
				if (e.getVariant().getTripleModel() == null) {
					JOptionPane.showMessageDialog(null,
					        "Impossible to create the graph. Some information is missing. Please, try to enrich this segment again.",
					        "Enrichment Graph View",
					        JOptionPane.WARNING_MESSAGE);
				} else {
					LDGraphFrame graphFrame = new LDGraphFrame(null,
					        fremePlugins.keySet().iterator().next()
					                .getGraphComponent(
					                        e.getVariant().getTripleModel(),
					                        e.getSegNum()),
					        e.getSegNum(), e.isTarget());
					graphFrame.open();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void notifyOpenFile(String filename, List<OcelotSegment> segments) {
		for (SegmentPlugin segPlugin : segPlugins.keySet()) {
			if (isEnabled(segPlugin)) {
				try {
					segPlugin.onFileOpen(filename);
				} catch (Exception e) {
					LOG.error("Segment plugin '" + segPlugin.getPluginName()
					        + "' threw an exception on file open", e);
				}
			}
		}
		if (isReportPluginEnabled()) {
			ReportPlugin reportPlugin = reportPlugins.keySet().iterator()
			        .next();
			reportPlugin.onOpenFile(filename, segments);
		}
		qualityPluginManager.initOpenedFileSettings(segments, filename);
		for(TimerPlugin timerPlugin: timerPlugins.keySet()){
			if(isEnabled(timerPlugin)){
				timerPlugin.resetTimer();
				timerPlugin.startTimer();
			}
		}
	}

	public void notifySavedFile(String filename) {
		for (SegmentPlugin segPlugin : segPlugins.keySet()) {
			if (isEnabled(segPlugin)) {
				try {
					segPlugin.onFileSave(filename);
				} catch (Exception e) {
					LOG.error("Segment plugin '" + segPlugin.getPluginName()
					        + "' threw an exception on file save", e);
				}
			}
		}
		
	}
	
	public void notifyBeforeSaveFile(){
		for(TimerPlugin timerPlugin: timerPlugins.keySet() ){
			if(isEnabled(timerPlugin)){
				timerPlugin.stopTimer();
			}
		}
	}

	/**
	 * Search the default directory for plugins. Equivalent to
	 * <code>discover(getPluginDir())</code>.
	 * 
	 * @throws IOException
	 */
	public void discover() throws IOException {
		discover(getPluginDir());
	}

	/**
	 * Search the provided directory for any JAR files containing valid plugin
	 * classes. Instantiate and configure any such classes.
	 * 
	 * @param pluginDirectory
	 * @throws IOException
	 *             if something goes wrong reading the directory
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
				Class<? extends ITSPlugin> c = (Class<ITSPlugin>) Class
				        .forName(s, false, classLoader);
				ITSPlugin plugin = c.newInstance();
				itsPlugins.put(plugin, cfgService.wasPluginEnabled(plugin));
			} catch (ClassNotFoundException e) {
				// XXX Shouldn't happen?
				System.out.println("Warning: " + e.getMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (String s : segPluginClassNames) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends SegmentPlugin> c = (Class<SegmentPlugin>) Class
				        .forName(s, false, classLoader);
				SegmentPlugin plugin = c.newInstance();
				segPlugins.put(plugin, cfgService.wasPluginEnabled(plugin));
			} catch (ClassNotFoundException e) {
				// XXX Shouldn't happen?
				System.out.println("Warning: " + e.getMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (String s : reportPluginClassNames) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends ReportPlugin> c = (Class<ReportPlugin>) Class
				        .forName(s, false, classLoader);
				ReportPlugin plugin = c.newInstance();
				reportPlugins.put(plugin, cfgService.wasPluginEnabled(plugin));
			} catch (ClassNotFoundException e) {
				// XXX Shouldn't happen?
				System.out.println("Warning: " + e.getMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (String s : fremePluginClassNames) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends FremePlugin> c = (Class<FremePlugin>) Class
				        .forName(s, false, classLoader);
				Constructor<? extends FremePlugin> constructor = c
				        .getDeclaredConstructor(String.class);
				FremePlugin plugin = constructor.newInstance(pluginDir
				        .getAbsolutePath());
				fremePlugins.put(plugin, false);
				setEnabled(plugin, cfgService.wasPluginEnabled(plugin));
			} catch (ClassNotFoundException e) {
				// XXX Shouldn't happen?
				System.out.println("Warning: " + e.getMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (String s : qualityPluginClassNames) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends QualityPlugin> c = (Class<QualityPlugin>) Class
				        .forName(s, false, classLoader);
				QualityPlugin plugin = c.newInstance();
				qualityPluginManager.getPlugins().put(plugin,
				        cfgService.wasPluginEnabled(plugin));
			} catch (ClassNotFoundException e) {
				// XXX Shouldn't happen?
				System.out.println("Warning: " + e.getMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
                for (String s : timerPluginClassNames) {
        			try {
        				@SuppressWarnings("unchecked")
        				Class<? extends TimerPlugin> c = (Class<TimerPlugin>) Class
        				        .forName(s, false, classLoader);
        				TimerPlugin plugin = c.newInstance();
        				timerPlugins.put(plugin, cfgService.wasPluginEnabled(plugin));
        			} catch (ClassNotFoundException e) {
        				// XXX Shouldn't happen?
        				System.out.println("Warning: " + e.getMessage());
        			} catch (Exception e) {
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

		classLoader = AccessController
		        .doPrivileged(new PrivilegedAction<URLClassLoader>() {
			        public URLClassLoader run() {
				        return new URLClassLoader(pluginJarURLs
				                .toArray(new URL[pluginJarURLs.size()]), Thread
				                .currentThread().getContextClassLoader());
			        }
		        });
	}

	void scanJar(final File file) {
		try {
			System.out.println("******************************");
			System.out.println("jar name = " + file.getName());
			Enumeration<JarEntry> e = new JarFile(file).entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				String name = entry.getName();
				if (name.endsWith(".class")) {
					name = convertFileNameToClass(name);
					System.out.println("-----" + name);
					try {
						Class<?> clazz = Class
						        .forName(name, false, classLoader);
						// Skip non-instantiable classes
						if (clazz.isInterface()
						        || Modifier.isAbstract(clazz.getModifiers())) {
							continue;
						}
						if (ITSPlugin.class.isAssignableFrom(clazz)) {
							// It's a plugin! Just store the name for now
							// since we will need to reinstantiate it later with
							// the
							// real classloader (I think)
							if (itsPluginClassNames.contains(name)) {
								// TODO: log this
								System.out
								        .println("Warning: found multiple implementations of plugin class "
								                + name);
							} else {
								itsPluginClassNames.add(name);
							}
						} else if (SegmentPlugin.class.isAssignableFrom(clazz)) {
							// It's a plugin! Just store the name for now
							// since we will need to reinstantiate it later with
							// the
							// real classloader (I think)
							if (segPluginClassNames.contains(name)) {
								// TODO: log this
								System.out
								        .println("Warning: found multiple implementations of plugin class "
								                + name);
							} else {
								segPluginClassNames.add(name);
							}
						} else if (ReportPlugin.class.isAssignableFrom(clazz)) {
							// It's a plugin! Just store the name for now
							// since we will need to reinstantiate it later with
							// the
							// real classloader (I think)
							if (reportPluginClassNames.contains(name)) {
								// TODO: log this
								System.out
								        .println("Warning: found multiple implementations of plugin class "
								                + name);
							} else {
								reportPluginClassNames.add(name);
							}
						} else if (FremePlugin.class.isAssignableFrom(clazz)) {
							// It's a plugin! Just store the name for now
							// since we will need to reinstantiate it later with
							// the
							// real classloader (I think)
							if (fremePluginClassNames.contains(name)) {
								// TODO: log this
								System.out
								        .println("Warning: found multiple implementations of plugin class "
								                + name);
							} else {
								fremePluginClassNames.add(name);
							}
						} else if (QualityPlugin.class.isAssignableFrom(clazz)) {
							// It's a plugin! Just store the name for now
							// since we will need to reinstantiate it later with
							// the
							// real classloader (I think)
							if (qualityPluginClassNames.contains(name)) {
								// TODO: log this
								System.out
								        .println("Warning: found multiple implementations of plugin class "
								                + name);
							} else {
								qualityPluginClassNames.add(name);
							}
						} else if (TimerPlugin.class.isAssignableFrom(clazz)) {
							// It's a plugin! Just store the name for now
							// since we will need to reinstantiate it later with
							// the
							// real classloader (I think)
							if (timerPluginClassNames.contains(name)) {
								// TODO: log this
								System.out
								        .println("Warning: found multiple implementations of plugin class "
								                + name);
							} else {
								timerPluginClassNames.add(name);
							} }
					} catch (ClassNotFoundException ex) {
						// XXX shouldn't happen?
						System.out.println("Warning: " + ex.getMessage());
					}
				}
			}

		} catch (IOException e) {
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

	static class JarFilenameFilter implements FilenameFilter {
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

	@Subscribe
	public void handleEnrichingStartedStoppedEvent(
	        EnrichingStartedStoppedEvent event) {

		fremeManager
		        .setEnriching(event.getAction() == EnrichingStartedStoppedEvent.STARTED);
		FremeMenu fremeMenu = (FremeMenu) fremeManager
		        .getFremeMenu(fremePlugins.keySet().iterator().next());
		if (fremeMenu != null) {
			((FremeMenu) fremeMenu)
			        .setEnrichMenuEnabled(event.getAction() == EnrichingStartedStoppedEvent.STOPPED);
		}
		fremeManager
		        .setContextMenuItemEnabled(event.getAction() == EnrichingStartedStoppedEvent.STOPPED);
	}

	public void enrichSegments(List<OcelotSegment> segments) {

		fremeManager.setSegments(segments);
		enrichSegments(FremePluginManager.OVERRIDE_ENRICHMENTS);
	}

	public void setSourceAndTargetLangs(String sourceLang, String targetLang) {

		if (fremePlugins != null && !fremePlugins.isEmpty()) {
			int dashIdx = sourceLang.indexOf("-");
			if (dashIdx != -1) {
				sourceLang = sourceLang.substring(0, dashIdx);
			}
			dashIdx = targetLang.indexOf("-");
			if (dashIdx != -1) {
				targetLang = targetLang.substring(0, dashIdx);
			}
			fremePlugins.keySet().iterator().next()
			        .setSourceAndTargetLanguages(sourceLang, targetLang);
		}
	}

	private void enrichSegments(int action) {

		if (fremePlugins != null && !fremePlugins.isEmpty()) {
			Entry<FremePlugin, Boolean> fremeEntry = fremePlugins.entrySet()
			        .iterator().next();
			if (fremeEntry.getValue()) {
				fremeManager.enrich(fremeEntry.getKey(), action);
			}
		}
	}

	@Subscribe
	public void segmentEdit(SegmentEditEvent e) {
		if (e.getSegment().getTarget() instanceof BaseSegmentVariant) {
			enrichVariant((BaseSegmentVariant) e.getSegment().getTarget(), e
			        .getSegment().getSegmentNumber(), true,
			        FremePluginManager.OVERRIDE_ENRICHMENTS);
		}
	}

	public void enrichVariant(BaseSegmentVariant variant, int segmentNumber,
	        boolean target, int action) {

		if (fremePlugins != null && !fremePlugins.isEmpty()) {
			Entry<FremePlugin, Boolean> fremeEntry = fremePlugins.entrySet()
			        .iterator().next();
			if (fremeEntry.getValue()) {
				fremeManager.enrich(fremeEntry.getKey(), variant,
				        segmentNumber, target, action);
			}
		}
	}

	private JMenu getFremeMenu() {

		JMenu fremeMenu = null;
		if (fremePlugins != null && !fremePlugins.isEmpty()) {
			FremePlugin fremePlugin = fremePlugins.keySet().iterator().next();
			fremeMenu = fremeManager.getFremeMenu(fremePlugin);
			fremeMenu.setEnabled(isFremePluginEnabled());
		}
		return fremeMenu;
	}

	public List<JMenu> getPluginMenuList(final JFrame ocelotFrame) {

		List<JMenu> menuList = new ArrayList<JMenu>();
		if (!reportPlugins.isEmpty()) {
			reportMenu = new JMenu("Reports");
			JMenuItem generateMenuItem = new JMenuItem("Generate Reports");
			generateMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						reportPlugins.keySet().iterator().next()
						        .generateReport(ocelotFrame);
					} catch (ReportException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			reportMenu.add(generateMenuItem);
			reportMenu.setEnabled(isReportPluginEnabled());
			menuList.add(reportMenu);
		}
		if (fremePlugins != null && !fremePlugins.isEmpty()) {
			menuList.add(getFremeMenu());
		}
		if (qualityPluginManager.isQualityPluginLoaded()) {
			qualityPluginManager.setOcelotMainFrame(ocelotFrame);
			menuList.add(qualityPluginManager.getQualityPluginMenu());
		}
		return menuList;
	}

	public boolean isReportPluginEnabled() {
		return reportPlugins != null && !reportPlugins.isEmpty()
		        && reportPlugins.entrySet().iterator().next().getValue();
	}

	public boolean isFremePluginEnabled() {
		return fremePlugins != null && !fremePlugins.isEmpty()
		        && fremePlugins.entrySet().iterator().next().getValue();
	}


    private void handleTimerOnUserAction(){
    	for(TimerPlugin timerPlugin: timerPlugins.keySet()){
			if(isEnabled(timerPlugin)){
				timerPlugin.recordUserActivity();
			}
		}
    }
	
	@Subscribe
	public void handleLqiDeleted(LQIRemoveEvent event) {

		qualityPluginManager.removedQualityIssue(event.getLQI());
		handleTimerOnUserAction();
	}

	@Subscribe
	public void handleLqiAdded(LQIAdditionEvent event) {

		qualityPluginManager.addQualityIssue(event.getLQI());
		handleTimerOnUserAction();
	}

	@Subscribe
	public void handleLqiEdited(LQIEditEvent event) {
		qualityPluginManager.editedQualityIssue(event.getOldLQI(),
		        event.getLQI());
		handleTimerOnUserAction();
	}
	
	@Subscribe
	public void handleLQIConfigSelected(LQIConfigurationSelectionChangedEvent e){
		
		qualityPluginManager.loadConfiguration(e.getNewSelectedConfiguration());
	}

	public List<JMenuItem> getSegmentContextMenuItems(
	        final OcelotSegment segment, final BaseSegmentVariant variant,
	        final boolean target) {

		List<JMenuItem> items = new ArrayList<JMenuItem>();
		if (fremePlugins != null && !fremePlugins.isEmpty()) {
			FremePlugin fremePlugin = fremePlugins.keySet().iterator().next();
			if (fremePlugins.get(fremePlugin)) {
				items = fremeManager.getSegmentContextMenuItems(fremePlugin,
				        segment, variant, target);
			}
		}
		return items;
	}
	
	public List<Component> getToolBarComponents(){
		
		List<Component> components = new ArrayList<Component>();
		if(timerPlugins != null && !timerPlugins.isEmpty()){
			TimerPlugin timerPlugin = timerPlugins.keySet().iterator().next();
			Component timerWidget = timerPlugin.getTimerWidget();
			if(isEnabled(timerPlugin) ){
				timerWidget.setEnabled(true);
			} else {
				timerWidget.setEnabled(false );
			}
			components.add(timerWidget);
		}
		
		return components;
	}

	public Double getTimerSeconds() {
		Double time = null;
		if(timerPlugins != null && !timerPlugins.isEmpty()){
			TimerPlugin timerPlugin = timerPlugins.keySet().iterator().next();
			if(isEnabled(timerPlugin)){
				time = timerPlugin.getSeconds();
			}
		}
		return time;
	}
	public List<JMenuItem> getSegmentTextContextMenuItems(final OcelotSegment segment, final String text, final int offset, final boolean target, final Window ownerWindow){		
		
		List<JMenuItem> items = new ArrayList<JMenuItem>();		
		if(fremePlugins != null && !fremePlugins.isEmpty()){		
			FremePlugin fremePlugin = fremePlugins.keySet().iterator().next();		
			if (fremePlugins.get(fremePlugin)) {		
				items = fremeManager.getTextContextMenuItems(segment, text, offset, target, ownerWindow);		
			}		
		}		
		return items;		
	}		
	public void pluginsAdded() {		
		eventQueue.post(new PluginAddedEvent());		
    }

}