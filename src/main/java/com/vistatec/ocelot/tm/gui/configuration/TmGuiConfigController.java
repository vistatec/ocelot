package com.vistatec.ocelot.tm.gui.configuration;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.config.xml.TmManagement.TmConfig;
import com.vistatec.ocelot.tm.TmManager;
import com.vistatec.ocelot.tm.gui.TmGuiController;

public class TmGuiConfigController extends TmGuiController {

	private TmManager tmManager;
	
	private TmConfigDialog configDialog;
	
	private TmAddingDialog addDialog;
	
	private JDialog currDialog;

	private List<TmConfig> tmOrderedList;

	private List<TmConfig> deletedTmList;

	private List<TmConfig> changedDirList;
	
	public TmGuiConfigController(final TmManager tmManager) {
		
		this.tmManager = tmManager;
	}
	
	public void setTmOrderedList(final List<TmConfig> tmOrderedLIst)
			throws TransferException {
		tmManager.saveTmOrdering(tmOrderedLIst);
		this.tmOrderedList = tmOrderedLIst;
	}

	public void changeTmDirectory() throws IOException, TransferException {

		if (changedDirList != null) {
			for (TmConfig tm : changedDirList) {
				tmManager.changeTmDataDir(tm.getTmName(),
						new File(tm.getTmDataDir()));
			}
		}
	}

	public void createNewTm(final String tmName, final String tmDirPath)
			throws IOException, TransferException {
		tmManager.initializeNewTm(tmName, new File(tmDirPath));
		TmConfig newTm = new TmConfig();
		newTm.setTmName(tmName);
		newTm.setTmDataDir(tmDirPath);
		newTm.setEnabled(true);
		configDialog.addNewTm(newTm);
	}
	
	public void deleteTmList() throws IOException, TransferException {
		if (deletedTmList != null) {
			for (TmConfig currTm : deletedTmList) {
				tmManager.deleteTm(currTm.getTmName());
			}
		}
	}

	public List<TmConfig> getTmOrderedList() {
		return tmOrderedList;
	}

	public void openTmConfigDialog(final Window ownerFrame) {
		
		tmOrderedList = tmManager.fetchTms();
		//TODO delete this line and remove the comment from the previous line
//		tmOrderedList = createTmList();
		configDialog = new TmConfigDialog(this, ownerFrame);
		currDialog = configDialog;
		SwingUtilities.invokeLater(configDialog);
	}
	
	public void opentCreateTmDialog(){
		
		addDialog = new TmAddingDialog(this, configDialog);
		currDialog = addDialog;
		SwingUtilities.invokeLater(addDialog);
	}
	
	public void handleTmChangedDir(final TmConfig changedDirTm) {

		if (changedDirList == null) {
			changedDirList = new ArrayList<TmConfig>();
		}
		changedDirList.add(changedDirTm);
}


	public void handleTmDeleted(final TmConfig deletedTm) {

		if (deletedTmList == null) {
			deletedTmList = new ArrayList<TmConfig>();
		}
		deletedTmList.add(deletedTm);
		if (changedDirList != null && changedDirList.contains(deletedTm)) {
			changedDirList.remove(deletedTm);
		}
	}
public void closeDialog() {

	if (currDialog.equals(configDialog)) {
		currDialog = null;
		tmOrderedList = null;
		configDialog = null;
		changedDirList = null;
		deletedTmList = null;
	} else if(currDialog.equals(addDialog)){
		currDialog = configDialog;
		addDialog = null;
	}
}
}
