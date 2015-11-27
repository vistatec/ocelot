package com.vistatec.ocelot.lqi.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.vistatec.ocelot.lqi.constants.LQIConstants;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory.LQIShortCut;
import com.vistatec.ocelot.lqi.model.LQIGrid;

/**
 * Handler class for managing LQI grid shortcuts.
 */
public class LQIKeyEventHandler {

	/** The LQI grid dialog. */
	private LQIGridDialog lqiGridDialog;

	/** The root pane that should listen to key events. */
	private JRootPane rootPane;

	/**
	 * Constructor.
	 * 
	 * @param lqiGridDialog
	 *            the LQI grid dialog.
	 * @param rootPane
	 *            the root pane listening to key events.
	 */
	public LQIKeyEventHandler(final LQIGridDialog lqiGridDialog, JRootPane rootPane) {

		this.lqiGridDialog = lqiGridDialog;
		this.rootPane = rootPane;
		getInputMap().put(KeyStroke.getKeyStroke('A', KeyEvent.CTRL_DOWN_MASK), "testAction");
		Action testAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(lqiGridDialog, "Test!");
			}
		};
		rootPane.getActionMap().put("testAction", testAction);

	}

	/**
	 * Removes all the actions for a specific LQI grid object.
	 * 
	 * @param lqiGrid
	 *            the LQI grid object.
	 */
	public void removeActions(LQIGrid lqiGrid) {

		if (lqiGrid != null && lqiGrid.getErrorCategories() != null) {
			for (LQIErrorCategory errCat : lqiGrid.getErrorCategories()) {
				errorCategoryDeleted(lqiGrid, errCat);
			}
		}
	}

	/**
	 * Loads all the actions for a specific LQI grid object.
	 * 
	 * @param lqiGrid
	 *            the LQI grid object.
	 */
	public void load(LQIGrid lqiGrid) {

		if (lqiGrid != null && lqiGrid.getErrorCategories() != null) {
			for (LQIErrorCategory errorCategory : lqiGrid.getErrorCategories()) {
				putActionForErrCategory(lqiGrid, errorCategory);
			}
		}
	}

	/**
	 * Gets the input map.
	 * 
	 * @return the input map.
	 */
	private InputMap getInputMap() {
//		return rootPane.getInputMap(
//		        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		return rootPane.getInputMap(
		        JComponent.WHEN_IN_FOCUSED_WINDOW);
//		 return rootPane.getInputMap();
	}

	/**
	 * Manages the event a new error category has been added. It adds all the
	 * actions for the new error category.
	 * 
	 * @param lqiGrid
	 *            the LQI grid object
	 * @param errCategory
	 *            the error category
	 */
	public void errorCategoryAdded(LQIGrid lqiGrid, LQIErrorCategory errCategory) {

		putActionForErrCategory(lqiGrid, errCategory);
	}

	/**
	 * Handles the event an error category gets deleted. It removes all the
	 * actions related to that error category.
	 * 
	 * @param lqiGrid
	 *            the LQI grid
	 * @param errorCategory
	 *            the deleted error category
	 */
	public void errorCategoryDeleted(LQIGrid lqiGrid,
	        LQIErrorCategory errorCategory) {

		ActionMap actionMap = rootPane.getActionMap();
		// ActionMap actionMap = lqiTable.getActionMap();
		InputMap inputMap = getInputMap();
		actionMap.remove(getActionName(errorCategory,
		        LQIConstants.MINOR_SEVERITY_NAME));
		actionMap.remove(getActionName(errorCategory,
		        LQIConstants.SERIOUS_SEVERITY_NAME));
		actionMap.remove(getActionName(errorCategory,
		        LQIConstants.CRITICAL_SEVERITY_NAME));
		if (errorCategory.getMinorShortcut() != null) {
			inputMap.remove(errorCategory.getMinorShortcut().getKeyStroke());
		}
		if (errorCategory.getSeriousShortcut() != null) {
			inputMap.remove(errorCategory.getSeriousShortcut().getKeyStroke());
		}
		if (errorCategory.getCriticalShortcut() != null) {
			inputMap.remove(errorCategory.getCriticalShortcut().getKeyStroke());
		}
	}

	/**
	 * Handles the event a shortcut has been changed for specific error category
	 * and severity.
	 * 
	 * @param errorCategory
	 *            the error category
	 * @param oldShortcut
	 *            the old shortcut
	 * @param errSeverity
	 *            the error severity
	 */
	public void shortCutChanged(LQIErrorCategory errorCategory,
	        KeyStroke oldShortcut, String errSeverity) {

		InputMap inputMap = getInputMap();
		String actionNameFromMap = (String) inputMap.get(oldShortcut);
		String actionName = getActionName(errorCategory, errSeverity);
		if (actionName.equals(actionNameFromMap)) {
			inputMap.remove(oldShortcut);
		}
		if (errSeverity.equals(LQIConstants.MINOR_SEVERITY_NAME)) {
			inputMap.put(errorCategory.getMinorShortcut().getKeyStroke(),
			        actionName);
		} else if (errSeverity.equals(LQIConstants.SERIOUS_SEVERITY_NAME)) {
			inputMap.put(errorCategory.getSeriousShortcut().getKeyStroke(),
			        actionName);
		} else if (errSeverity.equals(LQIConstants.CRITICAL_SEVERITY_NAME)) {
			inputMap.put(errorCategory.getCriticalShortcut().getKeyStroke(),
			        actionName);
		}
	}

	/**
	 * Handles the event the score value changes for a specific severity.
	 * 
	 * @param newScoreValue
	 *            the new score value
	 * @param errSeverity
	 *            the error severity.
	 */
	public void errorSeverityScoreChanged(int newScoreValue, String errSeverity) {

		ActionMap actionMap = rootPane.getActionMap();
		// ActionMap actionMap = lqiTable.getActionMap();
		Action action = null;
		for (Object actionName : actionMap.allKeys()) {
			action = actionMap.get(actionName);
			if (action instanceof LQIAction
			        && ((String) actionName).endsWith(errSeverity)) {
				((LQIAction) action).setSeverityScore(newScoreValue);
			}
		}
	}

	/**
	 * Handles the event an error category name has changed.
	 * 
	 * @param errorCat
	 *            the error category
	 * @param oldName
	 *            the category old name
	 */
	public void categoryNameChanged(LQIErrorCategory errorCat, String oldName) {

		replaceActionName(oldName, errorCat.getName(),
		        LQIConstants.MINOR_SEVERITY_NAME,
		        errorCat.getMinorShortcut() != null ? errorCat
		                .getMinorShortcut().getKeyStroke() : null);
		replaceActionName(oldName, errorCat.getName(),
		        LQIConstants.SERIOUS_SEVERITY_NAME,
		        errorCat.getSeriousShortcut() != null ? errorCat
		                .getSeriousShortcut().getKeyStroke() : null);
		replaceActionName(oldName, errorCat.getName(),
		        LQIConstants.CRITICAL_SEVERITY_NAME,
		        errorCat.getCriticalShortcut() != null ? errorCat
		                .getCriticalShortcut().getKeyStroke() : null);

	}

	public static void main(String[] args) {

		String comma = ",";
		try {
			System.out.println(URLEncoder.encode(comma, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Replaces the action name for a specific severity.
	 * 
	 * @param catOldName
	 *            the category old name
	 * @param catNewtName
	 *            the category new name
	 * @param severity
	 *            the severity
	 * @param keyStroke
	 *            the shortcut
	 */
	private void replaceActionName(String catOldName, String catNewtName,
	        String severity, KeyStroke keyStroke) {

		InputMap inputMap = getInputMap();
		ActionMap actionMap = rootPane.getActionMap();
		// ActionMap actionMap = lqiTable.getActionMap();
		String currName = catOldName + severity;
		String newName = catNewtName + severity;
		Action action = actionMap.get(currName);
		if (action != null && action instanceof LQIAction) {
			actionMap.put(newName, action);
			((LQIAction) action).setCategoryName(catNewtName);

		}
		if (keyStroke != null) {
			inputMap.put(keyStroke, newName);
		}

	}

	/**
	 * Puts the action in the map for a specific error category.
	 * 
	 * @param lqiGrid
	 *            the LQI grid object
	 * @param errorCategory
	 *            the error category
	 */
	private void putActionForErrCategory(LQIGrid lqiGrid,
	        LQIErrorCategory errorCategory) {
		putActionInMap(errorCategory, LQIConstants.MINOR_SEVERITY_NAME,
		        lqiGrid.getMinorScore(), errorCategory.getMinorShortcut());
		putActionInMap(errorCategory, LQIConstants.SERIOUS_SEVERITY_NAME,
		        lqiGrid.getSeriousScore(), errorCategory.getSeriousShortcut());
		putActionInMap(errorCategory, LQIConstants.CRITICAL_SEVERITY_NAME,
		        lqiGrid.getCriticalScore(), errorCategory.getCriticalShortcut());
	}

	/**
	 * 
	 * @param errorCategory
	 * @param severity
	 * @param severityScore
	 * @param shortCut
	 */
	private void putActionInMap(LQIErrorCategory errorCategory,
	        String severity, int severityScore, LQIShortCut shortCut) {

		String actionName = getActionName(errorCategory, severity);
		rootPane
		        .getActionMap()
		        .put(actionName,
		                new LQIAction(errorCategory.getName(), severityScore));
		// lqiTable
		// .getActionMap()
		// .put(actionName,
		// new LQIAction(errorCategory.getName(), severityScore));

		if (shortCut != null) {
			getInputMap().put(shortCut.getKeyStroke(), actionName);
		}
	}

	private String getActionName(LQIErrorCategory errCat, String errSeverity) {

		return errCat.getName() + errSeverity;
	}

	private class LQIAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1279195606709400222L;

		private String categoryName;

		private double severityScore;

		public LQIAction(String categoryName, double severitySCore) {
			this.categoryName = categoryName;
			this.severityScore = severitySCore;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			lqiGridDialog.createNewLqi(categoryName, severityScore,
			        lqiGridDialog.getCommentForCategory(categoryName));
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public void setSeverityScore(double severityScore) {
			this.severityScore = severityScore;
		}

	}
}
