package com.vistatec.ocelot.lqi.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;

import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGrid;

public class LQIKeyEventManager {

	private static LQIKeyEventManager instance;
	
	private List<LQIKeyEventHandler> keyEventHandlers;
	
	private LQIKeyEventManager() {
		
    }
	
	public static LQIKeyEventManager getInstance() {
		
		if(instance == null){
			instance = new LQIKeyEventManager();
		}
		return instance;
	}
	
	public static void destroy(){
		instance = null;
	}
	
	public void addKeyEventHandler(LQIKeyEventHandler keyEventHandler) {
		
		if(keyEventHandlers == null){
			keyEventHandlers = new ArrayList<LQIKeyEventHandler>();
		}
		keyEventHandlers.add(keyEventHandler);
	}
	
	public void removeKeyEventHandler(LQIKeyEventHandler keyEventHandler){
		
		if(keyEventHandlers != null){
			keyEventHandlers.remove(keyEventHandler);
		}
	}
	
	public void errorCategoryAdded(LQIGrid lqiGridObj, LQIErrorCategory errorCat){
		
		if(keyEventHandlers != null){
			for(LQIKeyEventHandler handler: keyEventHandlers){
				handler.errorCategoryAdded(lqiGridObj, errorCat);
			}
		}
	}
	
	public void errorCategoryDeleted(LQIGrid lqiGridObj, LQIErrorCategory errorCat){
		
		if(keyEventHandlers != null){
			for(LQIKeyEventHandler handler: keyEventHandlers){
				handler.errorCategoryDeleted(lqiGridObj, errorCat);
			}
		}
	}
	
	public void categoryNameChanged(LQIErrorCategory errorCat, String oldName){
		if(keyEventHandlers != null){
			for(LQIKeyEventHandler handler: keyEventHandlers){
				handler.categoryNameChanged(errorCat, oldName);
			}
		}
	}
	
	public void shortCutChanged(LQIErrorCategory errCat, KeyStroke oldShortCut, String severity){
		if(keyEventHandlers != null){
			for(LQIKeyEventHandler handler: keyEventHandlers){
				handler.shortCutChanged(errCat, oldShortCut, severity);
			}
		}
	}
	
	public void errorSeverityScoreChanged(int severityScore, String severityName){
		if(keyEventHandlers != null){
			for(LQIKeyEventHandler handler: keyEventHandlers){
				handler.errorSeverityScoreChanged(severityScore, severityName);
			}
		}
	}
	
	public void removeActions(LQIGrid lqiGridObject){
		if(keyEventHandlers != null){
			for(LQIKeyEventHandler handler: keyEventHandlers){
				handler.removeActions(lqiGridObject);
			}
		}
	}
	
	public void load(LQIGrid lqiGridObj){
		if(keyEventHandlers != null){
			for(LQIKeyEventHandler handler: keyEventHandlers){
				handler.load(lqiGridObj);
			}
		}
	}
}
