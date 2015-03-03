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

import com.vistatec.ocelot.config.AppConfig;
import com.vistatec.ocelot.plugins.PluginManager;
import com.vistatec.ocelot.rules.QuickAdd;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.services.XliffService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.ProvenanceAddEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

/**
 * Main Ocelot application context.
 */
public class OcelotApp implements OcelotEventQueueListener {
    private Logger LOG = LoggerFactory.getLogger(OcelotApp.class);

    protected AppConfig appConfig;
    private PluginManager pluginManager;
    private RuleConfiguration ruleConfig;

    private SegmentService segmentService;
    private XliffService xliffService;

    private File openFile;
    private boolean fileDirty = false, hasOpenFile = false;

    @Inject
    public OcelotApp(AppConfig config, PluginManager pluginManager,
            RuleConfiguration ruleConfig, SegmentService segmentService,
            XliffService xliffService) {
        this.appConfig = config;
        this.pluginManager = pluginManager;
        this.ruleConfig = ruleConfig;
        this.segmentService = segmentService;
        this.xliffService = xliffService;
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

    public void openFile(File openFile, File detectVersion) throws IOException, FileNotFoundException, XMLStreamException {
        List<Segment> segments = xliffService.parse(openFile, detectVersion);
        segmentService.clearAllSegments();
        segmentService.setSegments(segments);

        this.pluginManager.notifyOpenFile(openFile.getName());
        this.openFile = openFile;
        hasOpenFile = true;
        fileDirty = false;
    }

    public void saveFile(File saveFile) throws ErrorAlertException, IOException {
        if (saveFile == null) {
            throw new ErrorAlertException("No file to save!", "No file was specified to save to.");
        }

        String filename = saveFile.getName();
        if (saveFile.exists()) {
            if (!saveFile.canWrite()) {
                throw new ErrorAlertException("Unable to save!",
                        "The file " + filename + " can not be saved, because the file is not writeable.");
            }
        } else {
            if (!saveFile.createNewFile()) {
                throw new ErrorAlertException("Unable to save",
                        "The file " + filename + " can not be saved, because the directory is not writeable.");
            }
        }
        xliffService.save(saveFile);
        this.fileDirty = false;
        pluginManager.notifySaveFile(filename);
    }

    public void quickAddLQI(Segment seg, int hotkey) {
        QuickAdd qa = ruleConfig.getQuickAddLQI(hotkey);
        if (seg != null && qa != null && seg.isEditablePhase()) {
            segmentService.addLQI(new LQIAdditionEvent(qa.createLQI(), seg));
        }
    }

    public String getFileSourceLang() {
        return xliffService.getSourceLang();
    }

    public String getFileTargetLang() {
        return xliffService.getTargetLang();
    }

    @Subscribe
    public void segmentEdit(SegmentEditEvent e) {
        this.fileDirty = true;
    }

    @Subscribe
    public void provenanceAdded(ProvenanceAddEvent e) {
        this.fileDirty = true;
    }

    public class ErrorAlertException extends Exception {
        public final String title, body;

        public ErrorAlertException(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }
}
