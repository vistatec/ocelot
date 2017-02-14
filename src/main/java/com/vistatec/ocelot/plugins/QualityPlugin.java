package com.vistatec.ocelot.plugins;

import java.awt.Window;
import java.io.File;
import java.util.List;

import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.plugins.exception.AuditProfileException;
import com.vistatec.ocelot.plugins.exception.NoAuditProfileLoadedException;
import com.vistatec.ocelot.plugins.exception.QualityEvaluationException;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public interface QualityPlugin extends Plugin {

	public void loadAuditProfile(File file) throws AuditProfileException,
	        QualityEvaluationException;

	public void loadLQIGridConfiguration(LQIGridConfiguration configuration)
	        throws QualityEvaluationException;

	public File createNewAuditProfile(Window ownerWindow)
	        throws AuditProfileException;;

	public File createAuditProfileFromExistingOne(File file, Window ownerWindow)
	        throws AuditProfileException;;

	public void viewAuditProfileProps(Window ownerWindow)
	        throws AuditProfileException;

	public boolean evaluateQualityScore(Window owner)
	        throws NoAuditProfileLoadedException, QualityEvaluationException;

	public void documentOpened(int sampleSize,
	        List<LanguageQualityIssue> lqiList, List<OcelotSegment> segments, String fileName)
	        throws QualityEvaluationException;

	public void enableEvaluationOnTheFly(boolean enable)
	        throws NoAuditProfileLoadedException, QualityEvaluationException;

	public void lqiCreated(LanguageQualityIssue lqi)
	        throws QualityEvaluationException;

	public void lqiEdited(LanguageQualityIssue oldLqi,
	        LanguageQualityIssue newLqi) throws QualityEvaluationException;

	public void lqiRemoved(LanguageQualityIssue lqi)
	        throws QualityEvaluationException;

	// public boolean enableEvaluationOnTheFly(int sampleSize,
	// List<LanguageQualityIssue> lqiList) throws NoAuditProfileLoadedException;
	//
	// public void disableEvaluationOnTheFly();

	// public void setEvalOnTheFlyEnabled(boolean enabled) throws
	// NoAuditProfileLoadedException;
	//
	// public boolean initEvaluationOnTheFly(int sampleSize,
	// List<LanguageQualityIssue> lqiList);
	//
	// public boolean lqiAdded(LanguageQualityIssue lqi);
	//
	// public boolean lqiEdited(LanguageQualityIssue oldLqi,
	// LanguageQualityIssue newLqi);
	//
	public void displayOnTheFlyResult(Window owner);

}
