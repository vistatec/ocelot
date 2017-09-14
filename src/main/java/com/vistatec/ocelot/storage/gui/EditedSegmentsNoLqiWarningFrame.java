package com.vistatec.ocelot.storage.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.vistatec.ocelot.events.GoToSegmentEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;

public class EditedSegmentsNoLqiWarningFrame extends JDialog implements ActionListener, Runnable, ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2640224804305092734L;

	private final static int WIDTH = 400;

	private final static int HEIGHT = 300;

	private OcelotEventQueue eventQueue;
	
	private List<Integer> warnSegmentNumbers;

	private JList<SegmentListItem> segmentsList;
	
	private JButton btnClose;

	public EditedSegmentsNoLqiWarningFrame(Window ownerWindow, List<Integer> warnSegmentNumbers, OcelotEventQueue eventQueue) {
		super(ownerWindow);
		this.eventQueue = eventQueue;
		this.warnSegmentNumbers = warnSegmentNumbers;
		buildDialog();
	}

	private void buildDialog() {

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Saving to Azure - Incomplete Edited Segments Warning");
		setSize(WIDTH, HEIGHT);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		add(getTitlePanel(), BorderLayout.NORTH);
		add(getMainPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
	}

	private Component getBottomPanel() {
		
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		btnClose = new JButton("Close");
		btnClose.addActionListener(this);
		bottomPanel.add(btnClose);
		return bottomPanel;
	}

	private Component getMainPanel() {
		
		segmentsList = new JList<SegmentListItem>(getListModel());
		segmentsList.addListSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(segmentsList);
		return scrollPane;
	}

	private Component getTitlePanel() {

		JPanel titlePanel = new JPanel(new GridBagLayout());
		JLabel lblWarnIcon = new JLabel();
		lblWarnIcon.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 10, 10, 10);
		titlePanel.add(lblWarnIcon, c);
		
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		
		
		JTextArea txtMessage = new JTextArea(
				"Segments listed below have been edited but no LQI errors have been created. Please, create a Language Quality Issue for each of them and try to save to Azure again.");
		txtMessage.setLineWrap(true);
		txtMessage.setWrapStyleWord(true);
		txtMessage.setBackground(SystemColor.control);
		txtMessage.setEditable(false);
		titlePanel.add(txtMessage, c);
		return titlePanel;
	}

	private SegmentListItem[] getListModel(){
		
		SegmentListItem[] model = new SegmentListItem[warnSegmentNumbers.size()];
		int i = 0;
		for(Integer segNum: warnSegmentNumbers){
			model[i++] = new SegmentListItem(segNum);
		}
		return model;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource().equals(btnClose)){
			close();
		}
	}

	public void close() {
		setVisible(false);
	}

	@Override
	public void run() {
		
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}
	
	public static void main(String[] args) {
		
		List<Integer> warnSegments = new ArrayList<Integer>();
		warnSegments.add(1);
		warnSegments.add(2);
		warnSegments.add(3);
		warnSegments.add(4);
		EditedSegmentsNoLqiWarningFrame dialog = new EditedSegmentsNoLqiWarningFrame(null, warnSegments, null);
		SwingUtilities.invokeLater(dialog);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		SegmentListItem selSegment = segmentsList.getSelectedValue();
		eventQueue.post(new GoToSegmentEvent(selSegment.getSegmentNumber()));
	}

}

class SegmentListItem {
	
	private int segmentNumber;
	
	public SegmentListItem(int segmentNumber) {
		this.segmentNumber = segmentNumber;
	}
	
	public int getSegmentNumber(){
		return segmentNumber;
	}
	
	@Override
	public String toString() {
	
		return "Segment #" + segmentNumber;
	}
}
