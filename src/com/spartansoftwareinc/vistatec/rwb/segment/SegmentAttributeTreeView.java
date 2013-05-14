package com.spartansoftwareinc.vistatec.rwb.segment;

import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
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
    private SegmentAttributeView segAttrView;

    protected JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode lqiRoot;

    public SegmentAttributeTreeView(SegmentAttributeView sav) {
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
            segAttrView.setSelectedMetadata(lqi);
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
        segAttrView.deselectMetadata();
    }
}
