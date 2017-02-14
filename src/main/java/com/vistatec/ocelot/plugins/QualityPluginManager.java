package com.vistatec.ocelot.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.plugins.exception.AuditProfileException;
import com.vistatec.ocelot.plugins.exception.EvaluationOnTheFlyFailedException;
import com.vistatec.ocelot.plugins.exception.NoAuditProfileLoadedException;
import com.vistatec.ocelot.plugins.exception.QualityEvaluationException;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class QualityPluginManager implements ActionListener {

	protected final static String AUDIT_PROFILE_FILE_EXT = ".aud";

	private final Logger logger = LoggerFactory
	        .getLogger(QualityPluginManager.class);

	private JMenu qualityPluginMenu;

	private JCheckBoxMenuItem mnuOnTheFly;

	private HashMap<QualityPlugin, Boolean> qualityPlugins;

	private JFrame ocelotMainFrame;

	public QualityPluginManager() {

		this.qualityPlugins = new HashMap<QualityPlugin, Boolean>();
	}

	public JMenu getQualityPluginMenu() {

		qualityPluginMenu = new JMenu("Quality Score Evaluation");
		JMenu auditProfMenu = new JMenu("Manage Audit Profile");
		QualityMenuItem mnuItem = new QualityMenuItem("Load Audit Profile",
		        QualityMenuItem.LOAD_AUDIT_PROF);
		mnuItem.addActionListener(this);
		auditProfMenu.add(mnuItem);
		mnuItem = new QualityMenuItem("Create New Audit Profile",
		        QualityMenuItem.CREATE_AUDIT_PROF);
		mnuItem.addActionListener(this);
		auditProfMenu.add(mnuItem);
		mnuItem = new QualityMenuItem("Copy Audit Profile",
		        QualityMenuItem.COPY_AUDIT_PROF);
		mnuItem.addActionListener(this);
		auditProfMenu.add(mnuItem);
		mnuItem = new QualityMenuItem("View Audit Profile",
		        QualityMenuItem.VIEW_AUDIT_PROF);
		mnuItem.addActionListener(this);
		auditProfMenu.add(mnuItem);
		//TODO check if this feature should be still available
//		qualityPluginMenu.add(auditProfMenu);
		mnuItem = new QualityMenuItem("Evaluate Score",
		        QualityMenuItem.EVALUATE_SCORE);
		mnuItem.addActionListener(this);
		qualityPluginMenu.add(mnuItem);

		mnuOnTheFly = new JCheckBoxMenuItem("Evaluation on the fly");
		mnuOnTheFly.setSelected(false);
		mnuOnTheFly.addActionListener(this);
		qualityPluginMenu.add(mnuOnTheFly);
		qualityPluginMenu.setEnabled(false);
		for (QualityPlugin plugin : qualityPlugins.keySet()) {
			if (qualityPlugins.get(plugin)) {
				qualityPluginMenu.setEnabled(true);
				break;
			}
		}
		return qualityPluginMenu;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() instanceof QualityMenuItem) {
			QualityMenuItem menuItem = (QualityMenuItem) e.getSource();
			switch (menuItem.getMenuAction()) {
			case QualityMenuItem.LOAD_AUDIT_PROF:
				loadAuditProfile();
				break;
			case QualityMenuItem.CREATE_AUDIT_PROF:
				createAuditProfile();
				break;
			case QualityMenuItem.COPY_AUDIT_PROF:
				copyAuditProfile();
				break;
			case QualityMenuItem.VIEW_AUDIT_PROF:
				viewAuditProfProps();
				break;
			case QualityMenuItem.EVALUATE_SCORE:
				evaluateScore();
				break;
			default:
				break;
			}
		} else if (e.getSource() instanceof JCheckBoxMenuItem) {
			enableEvaluationOnTheFly(((JCheckBoxMenuItem) e.getSource())
			        .isSelected());
		}
	}

	private void enableEvaluationOnTheFly(boolean enable) {

		try {
			qualityPlugins.keySet().iterator().next()
			        .enableEvaluationOnTheFly(enable);
		} catch (NoAuditProfileLoadedException e) {
			mnuOnTheFly.setSelected(false);
			logger.warn("Request of enabling evaluation on the fly and no audit profile is loaded.");
			JOptionPane
			        .showMessageDialog(
			                ocelotMainFrame,
			                "No audit profile loaded. Please, load an audit profile and try again.",
			                "Evluation on the fly", JOptionPane.WARNING_MESSAGE);
		} catch (EvaluationOnTheFlyFailedException e) {
			logger.debug("Evaluation on the fly - Fail result!");
			promptFailMessage();
		} catch (QualityEvaluationException e) {
			logger.error(
			        "Error while initialing evaluation on the fly functionality",
			        e);
		}
	}

	private void promptFailMessage() {

		int option = JOptionPane
		        .showConfirmDialog(
		                ocelotMainFrame,
		                "The result of the score evaluation is a FAILURE.\nDo you want to view the result?",
		                "Quality score evaluation failure",
		                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (option == JOptionPane.YES_OPTION) {
			qualityPlugins.keySet().iterator().next()
			        .displayOnTheFlyResult(ocelotMainFrame);
		}
	}

	private void evaluateScore() {

		try {
			qualityPlugins.keySet().iterator().next()
			        .evaluateQualityScore(ocelotMainFrame);
		} catch (NoAuditProfileLoadedException e) {
			logger.warn(
			        "Request to eavluate score and no loaded audit profile.", e);
			JOptionPane
			        .showMessageDialog(
			                ocelotMainFrame,
			                "No audit profile loaded. Please, load an audit profile and try again.",
			                "Evaluate Score", JOptionPane.WARNING_MESSAGE);
		} catch (QualityEvaluationException e) {
			logger.error("Error while evaluating score.", e);
			JOptionPane
			        .showMessageDialog(
			                ocelotMainFrame,
			                "An error has occurred while performing the score evaluation.",
			                "Score Evaluation Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void viewAuditProfProps() {

		try {
			qualityPlugins.keySet().iterator().next()
			        .viewAuditProfileProps(ocelotMainFrame);
		} catch (NoAuditProfileLoadedException e) {
			logger.warn(
			        "Request to view loaded profile properties and no loaded audit profile.",
			        e);
			JOptionPane
			        .showMessageDialog(
			                ocelotMainFrame,
			                "No audit profile loaded. Please, load an audit profile and try again.",
			                "View Audit Profile Properties",
			                JOptionPane.WARNING_MESSAGE);
		} catch (AuditProfileException e) {
			logger.error("Error while displaying audit profile properties.", e);
			JOptionPane
			        .showMessageDialog(
			                ocelotMainFrame,
			                "An error occurred while displaying audit profile properties.",
			                "View Audit Profile Properties",
			                JOptionPane.ERROR_MESSAGE);
		}

	}

	private void auditProfileCreated(File createdAuditProfFile) {
		int option = JOptionPane.showConfirmDialog(ocelotMainFrame,
		        "Audit profile successfully created.\nDo you want to load it?",
		        "New Audit Profile", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			loadAuditProfile(createdAuditProfFile);
		}
	}

	private void copyAuditProfile() {

		JFileChooser fileChooser = getAuditProfFileChooser();
		int option = fileChooser.showOpenDialog(ocelotMainFrame);
		if (option == JFileChooser.APPROVE_OPTION) {
			File auditToCopy = fileChooser.getSelectedFile();
			if (auditToCopy != null) {
				try {
					File createdAuditFile = qualityPlugins
					        .keySet()
					        .iterator()
					        .next()
					        .createAuditProfileFromExistingOne(auditToCopy,
					                ocelotMainFrame);
					auditProfileCreated(createdAuditFile);
				} catch (AuditProfileException e) {
					JOptionPane
					        .showMessageDialog(
					                ocelotMainFrame,
					                "An error occurred while creating the audit profile.",
					                "Copy Audit Profile",
					                JOptionPane.ERROR_MESSAGE);
					logger.error("Error while creating the audit profile", e);
				}
			}
		}
	}

	private void createAuditProfile() {

		try {
			File createdAuditFile = qualityPlugins.keySet().iterator().next()
			        .createNewAuditProfile(ocelotMainFrame);
			auditProfileCreated(createdAuditFile);
		} catch (AuditProfileException e) {

			logger.error("Error while creating the audit profile.", e);
			JOptionPane.showMessageDialog(ocelotMainFrame,
			        "An error occurred while creating the audit profile.",
			        "New Audit Profile", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadAuditProfile(File file) {
		try {
			qualityPlugins.keySet().iterator().next().loadAuditProfile(file);
			JOptionPane.showMessageDialog(ocelotMainFrame,
			        "Audit Profile successfully loaded.", "Load Audit Profile",
			        JOptionPane.INFORMATION_MESSAGE);
		} catch (AuditProfileException e) {
			logger.error("Error while loading the audit profile.", e);
			JOptionPane.showMessageDialog(ocelotMainFrame,
			        "An error occurred while loading the audit profile.",
			        "Load Audit Profile", JOptionPane.ERROR_MESSAGE);
		} catch (EvaluationOnTheFlyFailedException e) {
			promptFailMessage();
		} catch (QualityEvaluationException e) {
			logger.error("Error while initializing the evaluation on the fly",
			        e);
			promptEvaluationOnTheFlyErrorMessage();
		}

	}

	private void loadAuditProfile() {

		JFileChooser fileChooser = getAuditProfFileChooser();
		int option = fileChooser.showOpenDialog(ocelotMainFrame);
		if (option == JFileChooser.APPROVE_OPTION) {
			File selFile = fileChooser.getSelectedFile();
			if (selFile != null) {
				loadAuditProfile(selFile);

			}
		}
	}

	private JFileChooser getAuditProfFileChooser() {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new AuditProfileFileFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		return fileChooser;
	}

	public HashMap<QualityPlugin, Boolean> getPlugins() {
		return qualityPlugins;
	}

	public void setOcelotMainFrame(JFrame ocelotMainFrame) {
		this.ocelotMainFrame = ocelotMainFrame;
	}

	public boolean isQualityPluginLoaded() {
		return !qualityPlugins.isEmpty();
	}

	public void enablePlugin(QualityPlugin qualityPlugin, boolean enabled) {
		qualityPlugins.put(qualityPlugin, enabled);
		qualityPluginMenu.setEnabled(enabled);
		if(!enabled){
			mnuOnTheFly.setSelected(false);
		}
	}

	public void addQualityIssue(LanguageQualityIssue lqi) {

		QualityPlugin plugin = getPlugin();
		if (plugin != null) {
			try {
				plugin.lqiCreated(lqi);
			} catch (EvaluationOnTheFlyFailedException e) {
				logger.warn("Quality issue added - Fail Result");
				promptFailMessage();
			} catch (QualityEvaluationException e) {
				logger.error(
				        "Error while evaluating on the fly the created lqi", e);
			}
		}
	}

	public void editedQualityIssue(LanguageQualityIssue oldLqi,
	        LanguageQualityIssue newLqi) {

		QualityPlugin plugin = getPlugin();
		if (plugin != null) {
			try {
				plugin.lqiEdited(oldLqi, newLqi);
			} catch (EvaluationOnTheFlyFailedException e) {
				logger.warn("Fail result on lqi edited.");
				promptFailMessage();
			} catch (QualityEvaluationException e) {
				logger.error(
				        "Error while evaluating the quality score on lqi edited.",
				        e);
				promptEvaluationOnTheFlyErrorMessage();
			}
		}

	}
	
	public void removedQualityIssue(LanguageQualityIssue removedLqi){
		
		QualityPlugin plugin = getPlugin();
		if (plugin != null) {
			try {
				plugin.lqiRemoved(removedLqi);
			} catch (EvaluationOnTheFlyFailedException e) {
				logger.warn("Fail result on lqi removed.");
				promptFailMessage();
			} catch (QualityEvaluationException e) {
				logger.error(
				        "Error while evaluating the quality score on lqi removed.",
				        e);
				promptEvaluationOnTheFlyErrorMessage();
			}
		}

	}

	private QualityPlugin getPlugin() {

		QualityPlugin plugin = null;
		if (!qualityPlugins.isEmpty()) {
			plugin = qualityPlugins.keySet().iterator().next();
		}
		return plugin;
	}

	public void initOpenedFileSettings(List<OcelotSegment> segments, String fileName) {

		if (!qualityPlugins.isEmpty()) {
			QualityPlugin plugin = qualityPlugins.keySet().iterator().next();
			if (plugin != null && segments != null) {
				StringBuilder sourceText = new StringBuilder();
				List<LanguageQualityIssue> lqiList = new ArrayList<LanguageQualityIssue>();
				for (OcelotSegment seg : segments) {
					sourceText.append(seg.getSource().getDisplayText());
					sourceText.append(" ");
					if (seg.getLQI() != null) {
						lqiList.addAll(seg.getLQI());
					}
				}
				int sampleSize = countWords(sourceText.toString());
				try {
					plugin.documentOpened(sampleSize, lqiList, segments, fileName);
				} catch (QualityEvaluationException e) {

					promptEvaluationOnTheFlyErrorMessage();
				}
			}
		}
	}

	private void promptEvaluationOnTheFlyErrorMessage() {
		mnuOnTheFly.setSelected(false);
		JOptionPane
		        .showMessageDialog(
		                ocelotMainFrame,
		                "An error occurred while evaluating the score.\nThe \"Evaluation on the fly\" has been disabled.",
		                "Evaluation on the fly error",
		                JOptionPane.ERROR_MESSAGE);
	}

	private int countWords(String text) {
		int wordCount = 0;
		BreakIterator iterator = BreakIterator.getWordInstance(Locale.ENGLISH);
		iterator.setText(text);
		int lastBoundary = 0;
		int boundary = iterator.first();
		while (boundary != BreakIterator.DONE) {
			for (int i = lastBoundary; i < boundary; i++) {
				if (Character.isLetter(text.codePointAt(i))) {
					wordCount++;
					break;
				}
			}
			lastBoundary = boundary;
			boundary = iterator.next();
		}
		if (lastBoundary < text.length()) {
			for (int i = lastBoundary; i < text.length(); i++) {
				if (Character.isLetter(text.codePointAt(i))) {
					wordCount++;
					break;
				}
			}
		}
		return wordCount;
	}

	public void loadConfiguration(LQIGridConfiguration newSelectedConfiguration) {
		
		if(!qualityPlugins.isEmpty() ) {
			QualityPlugin plugin = qualityPlugins.keySet().iterator().next();
			if(isPluginEnabled(plugin)){
				try {
	                plugin.loadLQIGridConfiguration(newSelectedConfiguration);
                } catch (QualityEvaluationException e) {
	                logger.error("Error while loading the LQI grid configuration.", e);
                }
			}
		}
	    
    }
	
	private boolean isPluginEnabled(QualityPlugin plugin){
		return qualityPlugins.get(plugin);
	}

}

class QualityMenuItem extends JMenuItem {

	private static final long serialVersionUID = 7784958170502035140L;

	public static final int LOAD_AUDIT_PROF = 0;

	public static final int CREATE_AUDIT_PROF = 1;

	public static final int COPY_AUDIT_PROF = 2;

	public static final int VIEW_AUDIT_PROF = 3;

	public static final int EVALUATE_SCORE = 4;

	private int mnuAction;

	public QualityMenuItem(String text, int mnuAction) {
		super(text);
		this.mnuAction = mnuAction;
	}

	public int getMenuAction() {
		return mnuAction;
	}

}

class AuditProfileFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return f != null
		        && (f.isDirectory() || f.getName().toLowerCase()
		                .endsWith(QualityPluginManager.AUDIT_PROFILE_FILE_EXT));
	}

	@Override
	public String getDescription() {

		return "Audit Profile files";
	}

}
