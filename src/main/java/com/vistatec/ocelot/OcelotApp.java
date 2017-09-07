/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vistatec.ocelot.events.NewPluginsInstalled;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.ProvenanceAddEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentNoteEditEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.plugins.PluginManager;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.services.EditDistanceReportService;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.services.XliffService;
import com.vistatec.ocelot.xliff.XLIFFDocument;
import com.vistatec.ocelot.xliff.freme.XliffFremeAnnotationWriter;

/**
 * Main Ocelot application context.
 */
public class OcelotApp implements OcelotEventQueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(OcelotApp.class);

    private final OcelotEventQueue eventQueue;

    private final PluginManager pluginManager;

    private final SegmentService segmentService;
    private final XliffService xliffService;
    private final EditDistanceReportService editDistService;
    private XLIFFDocument openXliffFile;
    
    private SegmentErrorChecker segErrorChecker;

    private File openFile;
    private boolean fileDirty = false, hasOpenFile = false;
    private boolean temporaryFile;
    private boolean savedToAzure;

    @Inject
    public OcelotApp(OcelotEventQueue eventQueue, PluginManager pluginManager,
            SegmentService segmentService, XliffService xliffService) {
        this.eventQueue = eventQueue;
        this.pluginManager = pluginManager;
        this.segmentService = segmentService;
        this.xliffService = xliffService;
		this.editDistService = new EditDistanceReportService(segmentService);
		this.segErrorChecker = new SegmentErrorChecker();
    }

    public File getOpenFile() {
        return openFile;
    }

    /**
     * Check if a file has been opened by the workbench.
     * @return
     */
    public boolean hasOpenFile() {
        return hasOpenFile;
    }

    /**
     * Returns whether there are unsaved changes in the segment data.
     * This includes segment edits and changes to LQI and Provenance data.
     * @return true if there are unsaved changes
     */
    public boolean isFileDirty() {
        return fileDirty;
    }

    public boolean isTemporaryFile(){
    	
    	return temporaryFile;
    }
    
    public String getDefaultFileName(){
    	String defFileName = null;
    	if(hasOpenFile){
    		if(temporaryFile){
    			defFileName = getFileNameFromOriginal(openXliffFile.getOriginal());
    		} else {
    			defFileName = openFile.getName();
    		}
    	}
    	
    	return defFileName; 
    }
    
    public void openFile(File openFile, boolean temporaryFile) throws IOException, FileNotFoundException, XMLStreamException {
        openXliffFile = xliffService.parse(openFile);
        segmentService.clearAllSegments();
        segmentService.setSegments(openXliffFile);

        this.pluginManager.notifyOpenFile(openFile.getName(), openXliffFile.getSegments());
        this.pluginManager.setSourceAndTargetLangs(openXliffFile.getSrcLocale().toString(), openXliffFile.getTgtLocale().toString());
        this.pluginManager.enrichSegments(openXliffFile.getSegments());
        this.openFile = openFile;
        hasOpenFile = true;
        fileDirty = false;
        this.temporaryFile = temporaryFile;
        String fileName = null;
        if(temporaryFile && openXliffFile.getOriginal() != null){
        	fileName = getFileNameFromOriginal(openXliffFile.getOriginal());
        } else {
        	fileName = openFile.getName();
        }
        segErrorChecker.clear();
        savedToAzure = false;
        eventQueue.post(new OpenFileEvent(fileName, openXliffFile));
    }
    
    private String getFileNameFromOriginal(String originalName){
    	return originalName + ".xlf";
    }

    public void saveFile(File saveFile) throws ErrorAlertException, IOException {
        if (saveFile == null) {
            throw new ErrorAlertException("No file to save!", "No file was specified to save to.");
        }

        String filename = saveFile.getName();
        if (saveFile.exists() && !saveFile.canWrite()) {
            throw new ErrorAlertException("Unable to save!",
                    "The file " + filename + " can not be saved, because the file is not writeable.");
        }
        pluginManager.notifyBeforeSaveFile();
        // Save to temp file, then move over actual target. This lets us ensure
        // the output is well-formed, as we save and then parse again to save
        // the annotations.
        Path tmpPath = Files.createTempFile("ocelot", "save");
        File tmpFile = tmpPath.toFile();
        xliffService.saveTime(pluginManager.getTimerSeconds());
        xliffService.save(openXliffFile, tmpFile);
        if(pluginManager.isFremePluginEnabled()){
	        try {
				XliffFremeAnnotationWriter annotationWriter = new XliffFremeAnnotationWriter(
				        openXliffFile.getSrcLocale().toString(), openXliffFile
				                .getTgtLocale().toString());
	            annotationWriter.saveAnnotations(tmpFile, segmentService);
	        } catch (Exception e) {
	            if (!tmpFile.delete()) {
	                LOG.info("Failed to delete temp file: " + tmpFile.getPath());
	            }
	            throw new ErrorAlertException("Unable to save!", "The file " + filename
	                    + " cannot be saved because the content is invalid. "
	                    + "If you edited tags, ensure they are correctly nested.");
	        }
        }
        this.fileDirty = false;
        editDistService.createEditDistanceReport(filename);
        pluginManager.notifySavedFile(filename);
        Files.move(tmpPath, saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        openFile = saveFile;
        segErrorChecker.clear();
    }

    public void saveLqiConfiguration(String lqiConfName) {
		xliffService.saveLqiConfiguration(lqiConfName);
	}
    
    public String getFileSourceLang() {
        return openXliffFile.getSrcLocale().toString();
    }

    public String getFileTargetLang() {
        return openXliffFile.getTgtLocale().toString();
    }

    @Subscribe
    public void segmentEdit(SegmentEditEvent e) {
        this.fileDirty = true;
    }

    @Subscribe
    public void provenanceAdded(ProvenanceAddEvent e) {
        this.fileDirty = true;
    }

    @Subscribe
    public void noteEdit(SegmentNoteEditEvent e) {
        this.fileDirty = true;
    }

    public class ErrorAlertException extends Exception {
        private static final long serialVersionUID = 1L;
        public final String title, body;

        public ErrorAlertException(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }

	public List<JMenu> getPluginMenuList(JFrame mainframe) {
		return pluginManager.getPluginMenuList(mainframe);
	}

	public List<JMenuItem> getSegmentContexPluginMenues(OcelotSegment segment,
			BaseSegmentVariant variant, boolean target) {

		return pluginManager.getSegmentContextMenuItems(segment, variant,
				target);
	}

	public List<Component> getPluginToolBarWidgets(){
		return pluginManager.getToolBarComponents();
	}


	public void handleNewPluginInstalled() {
		
		eventQueue.post(new NewPluginsInstalled());
	}
	public List<JMenuItem> getSegmentTextContexPluginMenues(
			final OcelotSegment segment, final String text, final int offset,
			final boolean target, Window ownerWindow) {
		return pluginManager.getSegmentTextContextMenuItems(segment, text,
				offset, target, ownerWindow);
	}

	public void initializeSegmentErrorChecker() {
		segErrorChecker = new SegmentErrorChecker();
		eventQueue.registerListener(segErrorChecker);
	}

	public void enableSegmentErrorChecker(boolean enabled) {
		if(enabled){
			eventQueue.registerListener(segErrorChecker);
		}
	}


	public boolean checkEditedSegments(JFrame mainframe ) {
		return segErrorChecker.checkIncompleteEditedSegments(mainframe, eventQueue);
	}

	public void savedToAzure() {
		
		savedToAzure = true;
	}
	
	public boolean getSavedToAzure() {
		return savedToAzure;
	}

}