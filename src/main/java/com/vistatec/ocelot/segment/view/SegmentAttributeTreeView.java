/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.view;

import com.vistatec.ocelot.events.LQISelectionEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;

import java.util.LinkedList;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Tree View for displaying segment ITS metadata.
 */
public class SegmentAttributeTreeView extends JScrollPane implements TreeSelectionListener {
    private static final long serialVersionUID = 1L;

    private SegmentAttributeView segAttrView;

    protected JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode lqiRoot;
    private OcelotEventQueue eventQueue;

    public SegmentAttributeTreeView(OcelotEventQueue eventQueue, SegmentAttributeView sav) {
        this.eventQueue = eventQueue;
        segAttrView = sav;
        root = new DefaultMutableTreeNode("Data Categories");
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.addTreeSelectionListener(this);
        setViewportView(tree);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null && node.isLeaf() && node != root) {
            LanguageQualityIssue lqi = (LanguageQualityIssue) node.getUserObject();
            eventQueue.post(new LQISelectionEvent(lqi));
        }
    }

    public void loadLQI(LinkedList<LanguageQualityIssue> lqiList) {
        if (lqiRoot != null) {
            root.remove(lqiRoot);
        }
        lqiRoot = new DefaultMutableTreeNode("LQI");
        root.add(lqiRoot);

        for (LanguageQualityIssue lqi : lqiList) {
            addLQI(lqi);
        }
        treeModel.reload();
    }

    public void addLQI(LanguageQualityIssue lqi) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        node.setUserObject(lqi);
        lqiRoot.add(node);
    }

    public void expandTree() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    protected void clearTree() {
        root.removeAllChildren();
        lqiRoot = null;
        treeModel.reload();
    }
}
