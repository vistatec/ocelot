package com.vistatec.ocelot.lqi;

import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;

import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQISeverity;

/**
 * This singleton class manages the LQI Key events by invoking specific event
 * handlers.
 */
public class LQIKeyEventManager {

	/** the instance. */
	private static LQIKeyEventManager instance;

	/** The list of handlers. */
	private List<LQIKeyEventHandler> keyEventHandlers;

	/**
	 * Constructor.
	 */
	private LQIKeyEventManager() {

	}

	/**
	 * Gets the instance.
	 * 
	 * @return the instance.
	 */
	public static LQIKeyEventManager getInstance() {

		if (instance == null) {
			instance = new LQIKeyEventManager();
		}
		return instance;
	}

	/**
	 * Destroys the instance.
	 */
	public static void destroy() {
		instance = null;
	}

	/**
	 * Adds a new Key event handler.
	 * 
	 * @param keyEventHandler
	 *            the Key event handler.
	 */
	public void addKeyEventHandler(LQIKeyEventHandler keyEventHandler) {

		if (keyEventHandlers == null) {
			keyEventHandlers = new ArrayList<LQIKeyEventHandler>();
		}
		keyEventHandlers.add(keyEventHandler);
	}

	/**
	 * Removes and existing Key event handler.
	 * 
	 * @param keyEventHandler
	 *            the Key event handler to be removed.
	 */
	public void removeKeyEventHandler(LQIKeyEventHandler keyEventHandler) {

		if (keyEventHandlers != null) {
			keyEventHandlers.remove(keyEventHandler);
		}
	}

	/**
	 * Handles the event a new error category has been added.
	 * 
	 * @param lqiGridObj
	 *            the LQI grid
	 * @param errorCat
	 *            the new error category
	 */
	public void errorCategoryAdded(LQIGridConfiguration lqiGridObj, LQIErrorCategory errorCat) {

		if (keyEventHandlers != null) {
			for (LQIKeyEventHandler handler : keyEventHandlers) {
				handler.errorCategoryAdded(lqiGridObj, errorCat);
			}
		}
	}

	/**
	 * Handles the event an error category has been deleted
	 * 
	 * @param lqiGridObj
	 *            the LQI grid
	 * @param errorCat
	 *            the error category deleted.
	 */
	public void errorCategoryDeleted(LQIGridConfiguration lqiGridObj,
	        LQIErrorCategory errorCat) {

		if (keyEventHandlers != null) {
			for (LQIKeyEventHandler handler : keyEventHandlers) {
				handler.errorCategoryDeleted(lqiGridObj, errorCat);
			}
		}
	}

	/**
	 * Handles the event a category name has changed.
	 * 
	 * @param errorCat
	 *            the error category
	 * @param oldName
	 *            the old name
	 */
	public void categoryNameChanged(LQIErrorCategory errorCat, String oldName) {
		if (keyEventHandlers != null) {
			for (LQIKeyEventHandler handler : keyEventHandlers) {
				handler.categoryNameChanged(errorCat, oldName);
			}
		}
	}

	/**
	 * Handles the event a shortcut has changed
	 * 
	 * @param errCat
	 *            the error category
	 * @param oldShortCut
	 *            the old shortcut
	 * @param severityName
	 *            the related severity name.
	 */
	public void shortCutChanged(LQIErrorCategory errCat, KeyStroke oldShortCut,
	        String severityName) {
		if (keyEventHandlers != null) {
			for (LQIKeyEventHandler handler : keyEventHandlers) {
				handler.shortCutChanged(errCat, oldShortCut, severityName);
			}
		}
	}

	/**
	 * Handles the event a severity score has changed.
	 * 
	 * @param severityScore
	 *            the severity score
	 * @param severityName
	 *            the severity name
	 */
	public void errorSeverityScoreChanged(double severityScore,
	        String severityName) {
		if (keyEventHandlers != null) {
			for (LQIKeyEventHandler handler : keyEventHandlers) {
				handler.errorSeverityScoreChanged(severityScore, severityName);
			}
		}
	}

	/**
	 * Removes all the actions for the LQI grid.
	 * 
	 * @param lqiGridObject
	 *            the LQI grid.
	 */
	public void removeActions(LQIGridConfiguration lqiGridObject) {
		if (keyEventHandlers != null) {
			for (LQIKeyEventHandler handler : keyEventHandlers) {
				handler.removeActions(lqiGridObject);
			}
		}
	}

	/**
	 * Loads all the actions for the LQI grid.
	 * 
	 * @param lqiGridObj
	 */
	public void load(LQIGridConfiguration lqiGridObj) {
		if (keyEventHandlers != null) {
			for (LQIKeyEventHandler handler : keyEventHandlers) {
				handler.load(lqiGridObj);
			}
		}
	}

	/**
	 * Handles the event the severity name has changed.
	 * 
	 * @param errCategories
	 *            the error category list
	 * @param newName
	 *            the severity new name
	 * @param oldName
	 *            the severity old name
	 */
	public void errorSeverityNameChanged(List<LQIErrorCategory> errCategories,
	        String newName, String oldName) {

		if (keyEventHandlers != null) {
			for (LQIKeyEventHandler handler : keyEventHandlers) {
				handler.severityNameChanged(errCategories, newName, oldName);
			}
		}
	}

	/**
	 * Handles the event a severity has been deleted.
	 * 
	 * @param errCategories
	 *            the list of error categories.
	 * @param delSeverity
	 *            the severity deleted.
	 */
	public void errorSeverityDeleted(List<LQIErrorCategory> errCategories,
	        LQISeverity delSeverity) {

		if (keyEventHandlers != null) {
			for (LQIKeyEventHandler handler : keyEventHandlers) {
				handler.severityDeleted(errCategories, delSeverity);
			}
		}
	}
}
