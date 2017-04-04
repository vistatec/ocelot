package com.vistatec.ocelot.lqi;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

/**
 * Handler class for managing LQI grid shortcuts.
 */
public class LQIKeyEventHandler {

	/** The LQI grid dialog. */
	private LQIGridController lqiGridController;

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
	public LQIKeyEventHandler(final LQIGridController lqiGridController,
	        JRootPane rootPane) {

		this.lqiGridController = lqiGridController;
		this.rootPane = rootPane;
	}

	/**
	 * Removes all the actions for a specific LQI grid object.
	 * 
	 * @param lqiGrid
	 *            the LQI grid object.
	 */
	public void removeActions(LQIGridConfiguration lqiGrid) {

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
	public void load(LQIGridConfiguration lqiGrid) {

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
		return rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
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
	public void errorCategoryAdded(LQIGridConfiguration lqiGrid, LQIErrorCategory errCategory) {

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
	public void errorCategoryDeleted(LQIGridConfiguration lqiGrid,
	        LQIErrorCategory errorCategory) {

		ActionMap actionMap = rootPane.getActionMap();
		InputMap inputMap = getInputMap();
		for (LQISeverity sev : lqiGrid.getSeverities()) {
			actionMap.remove(getActionName(errorCategory, sev.getName()));
		}

		if (errorCategory.getShortcuts() != null) {
			for (LQIShortCut shortcut : errorCategory.getShortcuts()) {
				inputMap.remove(shortcut.getKeyStroke());
			}
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
	 * @param severityName
	 *            the severity name
	 */
	public void shortCutChanged(LQIErrorCategory errorCategory,
	        KeyStroke oldShortcut, String severityName) {

		InputMap inputMap = getInputMap();
		String actionNameFromMap = (String) inputMap.get(oldShortcut);
		String actionName = getActionName(errorCategory, severityName);
		if (actionName.equals(actionNameFromMap)) {
			inputMap.remove(oldShortcut);
		}
		inputMap.put(errorCategory.getShortcut(severityName).getKeyStroke(),
		        actionName);
	}

	/**
	 * Handles the event the score value changes for a specific severity.
	 * 
	 * @param newScoreValue
	 *            the new score value
	 * @param errSeverity
	 *            the error severity.
	 */
	public void errorSeverityScoreChanged(double newScoreValue,
	        String errSeverity) {

		ActionMap actionMap = rootPane.getActionMap();
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

		if (errorCat != null && errorCat.getShortcuts() != null) {
			for (LQIShortCut shortcut : errorCat.getShortcuts()) {
				replaceActionNameErrCat(oldName, errorCat.getName(), shortcut
				        .getSeverity().getName(), shortcut.getKeyStroke());
			}
		}
	}

	/**
	 * Handles the event a severity name has changed.
	 * 
	 * @param errCategories
	 *            the list of error categories.
	 * @param newName
	 *            the severity new name.
	 * @param oldName
	 *            the severity old name.
	 */
	public void severityNameChanged(List<LQIErrorCategory> errCategories,
	        String newName, String oldName) {
		LQIShortCut shortcut = null;
		for (LQIErrorCategory errCat : errCategories) {
			shortcut = errCat.getShortcut(oldName);
			if (shortcut != null) {
				replaceActionNameSeverity(errCat.getName(), oldName, newName,
				        shortcut.getKeyStroke());
			}
		}

	}

	/**
	 * Replaces the action name when a severity name changes.
	 * 
	 * @param errCatName
	 *            the error category name
	 * @param oldSevName
	 *            the old severity name
	 * @param newSevName
	 *            the new severity name
	 * @param keyStroke
	 *            the key stroke
	 */
	private void replaceActionNameSeverity(String errCatName,
	        String oldSevName, String newSevName, KeyStroke keyStroke) {

		InputMap inputMap = getInputMap();
		ActionMap actionMap = rootPane.getActionMap();
		String currName = errCatName + oldSevName;
		String newName = errCatName + newSevName;
		Action action = actionMap.get(currName);
		if (action != null && action instanceof LQIAction) {
			actionMap.put(newName, action);
			((LQIAction) action).setSeverityName(newSevName);

		}
		if (keyStroke != null) {
			inputMap.put(keyStroke, newName);
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
	private void replaceActionNameErrCat(String catOldName, String catNewtName,
	        String severity, KeyStroke keyStroke) {

		InputMap inputMap = getInputMap();
		ActionMap actionMap = rootPane.getActionMap();
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
	private void putActionForErrCategory(LQIGridConfiguration lqiGrid,
	        LQIErrorCategory errorCategory) {

		if (lqiGrid.getSeverities() != null) {
			for (LQISeverity severity : lqiGrid.getSeverities()) {

				String actionName = getActionName(errorCategory,
				        severity.getName());
				rootPane.getActionMap().put(
				        actionName,
				        new LQIAction(errorCategory.getName(), severity
				                .getScore(), severity.getName()));
				LQIShortCut shortcut = errorCategory.getShortcut(severity
				        .getName());
				if (shortcut != null) {
					getInputMap().put(shortcut.getKeyStroke(), actionName);
				}
			}
		}
	}

	/**
	 * Gets the action name by concatenating the error category and the error
	 * severity names.
	 * 
	 * @param errCat
	 *            the error category
	 * @param errSeverity
	 *            the error severity
	 * @return the action name.
	 */
	private String getActionName(LQIErrorCategory errCat, String errSeverity) {

		return errCat.getName() + errSeverity;
	}

	/**
	 * Handles the event a severity has been deleted.
	 * 
	 * @param errCategories
	 *            the list of error categories.
	 * @param delSeverity
	 *            the deleted severity.
	 */
	public void severityDeleted(List<LQIErrorCategory> errCategories,
	        LQISeverity delSeverity) {

		if (errCategories != null) {
			for (LQIErrorCategory cat : errCategories) {

				rootPane.getActionMap().remove(
				        getActionName(cat, delSeverity.getName()));
				LQIShortCut shortcut = cat.getShortcut(delSeverity.getName());
				if (shortcut != null) {
					getInputMap().remove(shortcut.getKeyStroke());
				}
			}
		}

	}

	/**
	 * The LQI action. It stores the information needed for creating a Language
	 * Quality Issue.
	 */
	private class LQIAction extends AbstractAction {

		/** The serial version UID. */
		private static final long serialVersionUID = 1279195606709400222L;

		/** The category name. */
		private String categoryName;

		/** The severity score. */
		private double severityScore;

		/** The severity name. */
		private String severityName;

		/**
		 * Constructor.
		 * 
		 * @param categoryName
		 *            the category name.
		 * @param severitySCore
		 *            the severity score.
		 * @param severityName
		 *            the severity name.
		 */
		public LQIAction(String categoryName, double severitySCore,
		        String severityName) {
			this.categoryName = categoryName;
			this.severityScore = severitySCore;
			// this.severityType = severityType;
			this.severityName = severityName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			lqiGridController.createNewLqi(categoryName, severityScore,
			        severityName);
		}

		/**
		 * Sets the category name.
		 * 
		 * @param categoryName
		 *            the category name.
		 */
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		/**
		 * // * Sets the severity score.
		 * 
		 * @param severityScore
		 *            the severity score.
		 */
		public void setSeverityScore(double severityScore) {
			this.severityScore = severityScore;
		}

		/**
		 * Sets the severity name.
		 * 
		 * @param severityName
		 *            the severity name.
		 */
		public void setSeverityName(String severityName) {
			this.severityName = severityName;
		}

	}

}
