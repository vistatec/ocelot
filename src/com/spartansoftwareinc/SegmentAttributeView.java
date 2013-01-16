package com.spartansoftwareinc;

import java.util.LinkedList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Displays ITS metadata attached to the selected segment in the SegmentView.
 */
public class SegmentAttributeView extends JTabbedPane implements TreeSelectionListener {
    private NewLanguageQualityIssueView addLQIView;
    private LanguageQualityIssueView lqiDetailView;

    private JScrollPane treeView;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode lqiRoot;

    private Segment selectedSegment;

    public SegmentAttributeView(LanguageQualityIssueView detailView) {
        this.lqiDetailView = detailView;

        root = new DefaultMutableTreeNode("Data Categories");
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.addTreeSelectionListener(this);
        treeView = new JScrollPane(tree);
        addTab("Main", treeView);

        addLQIView = new NewLanguageQualityIssueView(this);
        addTab("+", addLQIView);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        if (node != null && node.isLeaf() && node != root) {
            LanguageQualityIssue lqi = (LanguageQualityIssue)node.getUserObject();
            lqiDetailView.setLQI(selectedSegment, lqi);
        }
    }
    
    public Segment getSelectedSegment() {
        return this.selectedSegment;
    }
    
    public void setSelectedSegment(Segment seg) {
        this.selectedSegment = seg;
        if (seg.containsLQI()) { loadLQI(seg.getLQI()); }
        addLQIView.updateSegment();
    }

    public void loadLQI(LinkedList<LanguageQualityIssue> lqiList) {
        if (lqiRoot != null) { root.remove(lqiRoot); }
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
    
    private void clearTree() {
        root.removeAllChildren();
        treeModel.reload();
    }
}
