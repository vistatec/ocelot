package com.vistatec.ocelot.tm.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.config.xml.TmManagement.TmConfig;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.tm.TmManager;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.TmService;
import com.vistatec.ocelot.tm.gui.configuration.CreateNewTmDialog;
import com.vistatec.ocelot.tm.gui.configuration.TmConfigDialog;

public class TmController {

	private TmManager tmManager;

	private TmService tmService;

	private List<TmConfig> tmOrderedList;

	private List<TmConfig> deletedTmList;

	private List<TmConfig> changedDirList;

	private JDialog currDialog;

	private TmConfigDialog configDialog;
	
	private CreateNewTmDialog createDialog;

	public TmController(final TmManager tmManager, final TmService tmService) {

		this.tmManager = tmManager;
		this.tmService = tmService;
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

	public List<TmMatch> getFuzzyMatches(List<SegmentAtom> currentSelection) {
		List<TmMatch> matches = null;
		try {
			matches = tmService.getFuzzyTermMatches(currentSelection);
		} catch (IOException e) {
			// TODO prompt error message to the user.
		}
		return matches;
	}

	public List<TmMatch> getConcordanceMatches(
			List<SegmentAtom> currentSelection) {

		List<TmMatch> matches = null;
		try {
			matches = tmService.getConcordanceMatches(currentSelection);
		} catch (IOException e) {
			// TODO prompt error message to the user.
		}
		return matches;
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

	public void openTmConfigDialog(final JFrame ownerFrame) {
		
		tmOrderedList = tmManager.fetchTms();
		//TODO delete this line and remove the comment from the previous line
//		tmOrderedList = createTmList();
		configDialog = new TmConfigDialog(this, ownerFrame);
		currDialog = configDialog;
		SwingUtilities.invokeLater(configDialog);
	}
	
	public void opentCreateTmDialog(){
		
		createDialog = new CreateNewTmDialog(this, configDialog);
		currDialog = createDialog;
		SwingUtilities.invokeLater(createDialog);
	}
	
	//TODO delete, only for test purpose
		private List<TmConfig> createTmList(){
			
			List<TmConfig> list = new ArrayList<TmConfig>();
			
			TmConfig tm = new TmConfig();
			tm.setEnabled(true);
			tm.setPenalty(1.5f);
			tm.setTmDataDir("C:\\Users\\Martab\\Projects\\Ocelot Project\\Ocelot\\src\\main\\resources\\com\\vistatec\\ocelot");
			tm.setTmName("TM 1");
			list.add(tm);
			tm = new TmConfig();
			tm.setEnabled(false);
			tm.setPenalty(3.5f);
			tm.setTmDataDir("C:\\Users\\Martab");
			tm.setTmName("TM 2");
			list.add(tm);
			return list;
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

	public void handleTmChangedDir(final TmConfig changedDirTm) {

			if (changedDirList == null) {
				changedDirList = new ArrayList<TmConfig>();
			}
			changedDirList.add(changedDirTm);
	}

	

	public void closeDialog() {

		if (currDialog.equals(configDialog)) {
			currDialog = null;
			tmOrderedList = null;
			configDialog = null;
			changedDirList = null;
			deletedTmList = null;
		} else if(currDialog.equals(createDialog)){
			currDialog = configDialog;
			createDialog = null;
		}
	}
}
