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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.ConfigService;
import com.vistatec.ocelot.config.ConfigTransferService;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.SegmentTargetEnterEvent;
import com.vistatec.ocelot.events.SegmentTargetExitEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.plugins.ReportPlugin.ReportException;
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
	private List<String> qualityPluginClassNames = new ArrayList<String>();
	private HashMap<ITSPlugin, Boolean> itsPlugins;
	private HashMap<SegmentPlugin, Boolean> segPlugins;
	private HashMap<ReportPlugin, Boolean> reportPlugins;
	private ClassLoader classLoader;
	private File pluginDir;
	private final ConfigService cfgService;
	private QualityPluginManager qualityPluginManager;

	public PluginManager(ConfigService cfgService, File pluginDir) {
		this.itsPlugins = new HashMap<ITSPlugin, Boolean>();
		this.segPlugins = new HashMap<SegmentPlugin, Boolean>();
		this.reportPlugins = new HashMap<ReportPlugin, Boolean>();

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
		Set<? extends Plugin> qualityPlugins = getQualityPlugins();
		plugins.addAll(itsPlugins);
		plugins.addAll(segmentPlugins);
		plugins.addAll(reportPlugins);
		plugins.addAll(qualityPlugins);
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

	public Set<QualityPlugin> getQualityPlugins() {
		return qualityPluginManager.getPlugins().keySet();
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
		} else if (plugin instanceof QualityPlugin) {
			QualityPlugin qualityPlugin = (QualityPlugin) plugin;
			enabled = qualityPluginManager.getPlugins().get(qualityPlugin);
		}
		return enabled;
	}

	public void setEnabled(Plugin plugin, boolean enabled)
	        throws ConfigTransferService.TransferException {
		if (plugin instanceof ITSPlugin) {
			ITSPlugin itsPlugin = (ITSPlugin) plugin;
			itsPlugins.put(itsPlugin, enabled);
		} else if (plugin instanceof SegmentPlugin) {
			SegmentPlugin segPlugin = (SegmentPlugin) plugin;
			segPlugins.put(segPlugin, enabled);
		} else if (plugin instanceof ReportPlugin) {
			ReportPlugin reportPlugin = (ReportPlugin) plugin;
			reportPlugins.put(reportPlugin, enabled);
		} else if (plugin instanceof QualityPlugin) {
			QualityPlugin qualityPlugin = (QualityPlugin) plugin;
			qualityPluginManager.enablePlugin(qualityPlugin, enabled);
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
		qualityPluginManager.initOpenedFileSettings(segments);
	}

	public void notifySaveFile(String filename) {
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
			Enumeration<JarEntry> e = new JarFile(file).entries();
			System.out.println("----------------------------------");
			System.out.println("File: " + file.getName());
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				String name = entry.getName();
				if (name.endsWith(".class")) {
					name = convertFileNameToClass(name);
					System.out.println(name);
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
						}
						if (SegmentPlugin.class.isAssignableFrom(clazz)) {
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
						}
						if (ReportPlugin.class.isAssignableFrom(clazz)) {
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
						}
						if (QualityPlugin.class.isAssignableFrom(clazz)) {
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
						}
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

	public List<JMenu> getPluginMenuList(final JFrame ocelotFrame) {

		List<JMenu> menuList = new ArrayList<JMenu>();
		if (isReportPluginEnabled()) {
			JMenu reportMenu = new JMenu("Reports");
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
			menuList.add(reportMenu);
		}
		if (qualityPluginManager.isQualityPluginLoaded()) {
			qualityPluginManager.setOcelotMainFrame(ocelotFrame);
			menuList.add(qualityPluginManager.getQualityPluginMenu());
		}
		return menuList;
	}

	// public JMenu getQualityPluginMenu(final JFrame ocelotFrame ){
	//
	// JMenu qualityMenu = new JMenu("Quality Score Evaluation");
	// JMenu auditProfMenu = new JMenu("Manage Audit Profile");
	// final JMenuItem mnuLoadAudit = new JMenuItem("Load Audit Profile");
	// auditProfMenu.add(mnuLoadAudit);
	// final JMenuItem mnuNewAudit = new JMenuItem("Create New Audit Profile");
	// auditProfMenu.add(mnuNewAudit);
	// final JMenuItem mnuCopyAudit = new JMenuItem("Copy Audit Profile");
	// auditProfMenu.add(mnuCopyAudit);
	// final JMenuItem mnuViewAudit = new JMenuItem("View Audit");
	// auditProfMenu.add(mnuViewAudit);
	// qualityMenu.add(auditProfMenu);
	//
	// ActionListener listener = new ActionListener() {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// if(e.getSource().equals(mnuViewAudit)){
	// qualityPlugins.keySet().iterator().next().viewAuditProfileProps();
	// } else if(e.getSource().equals(mnuCopyAudit)){
	// qualityPlugins.keySet().iterator().next().createAuditProfileFromExistingOne(file);;
	// }
	// }
	// };
	//
	// return qualityMenu;
	// }

	public boolean isReportPluginEnabled() {
		return reportPlugins != null && !reportPlugins.isEmpty()
		        && reportPlugins.entrySet().iterator().next().getValue();
	}

	public void newFiledOpened(List<OcelotSegment> segments) {

		

	}
	
	

	@Subscribe
	public void handleLqiAdded(LQIAdditionEvent event) {

		try{
		qualityPluginManager.addQualityIssue(event.getLQI());
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Subscribe
	public void handleLqiEdited(LQIEditEvent event) {
		qualityPluginManager.editedQualityIssue(event.getOldLQI(),
		        event.getLQI());
	}

	public static void main(String[] args) throws IOException, SAXException {

//		String regex = "[a-zA-Z0-9_]*?";
//		String str = "marta";
//		System.out.println(str.matches(regex));
//		String str2 = "php utilizza la sintassi di perl, e mentre ereg... beh, ereg";
//		String regex2 = "[^a-zA-Z0-9_].";
//		String[] splitString = str2.split(regex2);
//		System.out.println(Arrays.toString(splitString));
//		System.out.println(splitString.length);
		
		BreakIterator iterator = BreakIterator.getWordInstance(Locale.ITALIAN);
		String text = "php utilizza la sintassi di perl, e mentre ereg... beh, ereg";
		iterator.setText(text);
		int lastBoundary = 0; 
		int boundary = iterator.first();
		while(boundary != BreakIterator.DONE){
			for(int i = lastBoundary; i<boundary; i++){
				if(Character.isLetter(text.codePointAt(i))){
					System.out.println(text.substring(lastBoundary, boundary));
					break;
				}
			}
			lastBoundary = boundary;
			boundary = iterator.next();
		}
		if(lastBoundary < text.length()){
			for(int i = lastBoundary; i<text.length(); i++){
				if(Character.isLetter(text.codePointAt(i))){
					System.out.println(text.substring(lastBoundary));
					break;
				}
			}
		}

//		BodyContentHandler handler = new BodyContentHandler(10*1024*1024);
//		Metadata metadata = new Metadata();
//		AutoDetectParser parser = new AutoDetectParser();
//		InputStream in = new FileInputStream(new File(System.getProperty("user.home"), "words.txt"));
//		parser.parse(in, handler, metadata);
//		System.out.println(metadata.get(Metadata.WORD_COUNT));
		
	}
}
