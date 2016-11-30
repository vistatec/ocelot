package com.vistatec.ocelot.xliff.freme.helper;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DocumentTreeHelper {
	
	public static List<NodeWrapper> getFlatTree(Node node) {

		List<NodeWrapper> nodes = new ArrayList<NodeWrapper>();
		// nodes.add(node);
		if (node.getChildNodes() != null) {
			Node child = null;
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				child = node.getChildNodes().item(i);
				nodes.add(new NodeWrapper(child, node));
				nodes.addAll(getFlatTree(child));
			}
		}
		return nodes;
	}
	
	public static void rebuildTree(Element sourceElement,
	        List<NodeWrapper> newFlatNodes) {

		removeChildrenNode(sourceElement);
		for (NodeWrapper currNode : newFlatNodes) {
			removeChildrenNode(currNode.getNode());
			if (currNode.getParent() != null) {
				currNode.getParent().appendChild(currNode.getNode());
			}
		}

	}
	
	private static void removeChildrenNode(Node node) {

		NodeList children = node.getChildNodes();
		List<Node> childrenToRemove = new ArrayList<Node>();
		for (int i = 0; i < children.getLength(); i++) {
//			node.removeChild(children.item(i));
			
			childrenToRemove.add(children.item(i));
		}
		for( Node child: childrenToRemove){
			node.removeChild(child);
		}
	}

	public static class NodeWrapper {

		private Node node;

		private Node parent;

		public NodeWrapper(Node node, Node parent) {
			this.node = node;
			this.parent = parent;
		}

		public Node getNode() {
			return node;
		}

		public void setNode(Node node) {
			this.node = node;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}
		
		
	}
}
