package com.vistatec.ocelot.its.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class LanguageQualityIssuePropsPanel extends JPanel implements
        ActionListener, Runnable {
	private static final long serialVersionUID = 4489879495975477888L;

	private static final Logger LOG = LoggerFactory
	        .getLogger(LanguageQualityIssuePropsPanel.class);

	private OcelotEventQueue eventQueue;

	private LQIGridConfigurations lqiGrid;

	private boolean enabled = true;

	private OcelotSegment selectedSeg;

	private LanguageQualityIssue selectedLQI;

	private String prevType, prevComment;
	private LQISeverity prevSeverity;
	private URL prevProfile;
	private boolean prevEnabled;

	private JComboBox<String> typeList;

	private JComboBox<LQISeverity> severityList;

	private JTextArea commentContent;

	private JTextField profileRefLink;

	private JRadioButton enabledTrue;

	private JRadioButton enabledFalse;

	private JFrame frame;

	private WindowListener windowListener;

	private JButton btnSave;

	private JButton btnCancel;

	private JLabel segmentLabel;

	private JLabel segmentId;

	private JLabel typeLabel;

	private JLabel commentLabel;

	private JLabel severityLabel;

	private JLabel profileLabel;

	private JLabel enabledLabel;

	private JScrollPane commentScroll;

	public LanguageQualityIssuePropsPanel(OcelotEventQueue eventQueue,
	        LQIGridConfigurations lqiGrid) {

		this.eventQueue = eventQueue;
		this.lqiGrid = lqiGrid;
		makePanel();
	}

	private void makePanel() {
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		// Initialize default grid bag layout: left align, 1 grid each
		GridBagConstraints gridBag = new GridBagConstraints();
		gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
		gridBag.gridwidth = 1;

		// Add UI Components from top to bottom
		addTranslationSegment(gridBag);
		addType(gridBag);
		addComments(gridBag);
		addSeverity(gridBag);
		addProfileReference(gridBag);
		addEnabled(gridBag);
		addSave(gridBag);

	}

	private void addTranslationSegment(GridBagConstraints gridBag) {
		segmentLabel = new JLabel("Segment #: ");
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		add(segmentLabel, gridBag);

		segmentId = new JLabel();
		gridBag.gridx = 1;
		gridBag.gridy = 0;
		add(segmentId, gridBag);
	}

	private void addType(GridBagConstraints gridBag) {
		typeLabel = new JLabel("Type: ");
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		add(typeLabel, gridBag);

		typeList = new JComboBox<String>();
		if (lqiGrid != null ) {
			LQIGridConfiguration activeConf = lqiGrid.getActiveConfiguration();
			if(activeConf != null && activeConf.getErrorCategories() != null){
				
				String[] types = new String[activeConf.getErrorCategories().size()];
				int i = 0;
				for (LQIErrorCategory errCat : activeConf.getErrorCategories()) {
					types[i++] = errCat.getName();
				}
				typeList.setModel(new DefaultComboBoxModel<>(types));
			}
		}
		gridBag.gridx = 1;
		gridBag.gridy = 1;
		add(typeList, gridBag);
	}

	private void addComments(GridBagConstraints gridBag) {
		commentLabel = new JLabel("Comment: ");
		gridBag.gridx = 0;
		gridBag.gridy = 2;
		add(commentLabel, gridBag);

		commentContent = new JTextArea(5, 15);
		commentContent.setEditable(true);
		commentContent.setLineWrap(true);
		commentScroll = new JScrollPane(commentContent);
		gridBag.gridx = 1;
		gridBag.gridy = 2;
		add(commentScroll, gridBag);
	}

	private void addSeverity(GridBagConstraints gridBag) {
		severityLabel = new JLabel("Severity: ");
		gridBag.gridx = 0;
		gridBag.gridy = 3;
		add(severityLabel, gridBag);

		severityList = new JComboBox<>();
		if (lqiGrid != null ) {
			LQIGridConfiguration activeConf = lqiGrid.getActiveConfiguration();
			if(activeConf != null && activeConf.getSeverities() != null){
				
				severityList.setModel(new DefaultComboBoxModel<>(activeConf
						.getSeverities().toArray(
								new LQISeverity[activeConf.getSeverities().size()])));
			}
		}
		gridBag.gridx = 1;
		gridBag.gridy = 3;
		add(severityList, gridBag);
	}

	private void addProfileReference(GridBagConstraints gridBag) {
		profileLabel = new JLabel("Profile Reference: ");
		gridBag.gridx = 0;
		gridBag.gridy = 4;
		add(profileLabel, gridBag);

		// TODO: IRI validation?
		profileRefLink = new JTextField(15);
		gridBag.gridx = 1;
		gridBag.gridy = 4;
		add(profileRefLink, gridBag);
	}

	private void addEnabled(GridBagConstraints gridBag) {
		enabledLabel = new JLabel("Enabled: ");
		gridBag.gridx = 0;
		gridBag.gridy = 5;
		add(enabledLabel, gridBag);

		enabledTrue = new JRadioButton("Yes");
		enabledTrue.setSelected(true);
		enabledTrue.addActionListener(this);

		enabledFalse = new JRadioButton("No");
		enabledFalse.addActionListener(this);

		ButtonGroup group = new ButtonGroup();
		group.add(enabledTrue);
		group.add(enabledFalse);

		JPanel enabledPanel = new JPanel();
		enabledPanel.add(enabledTrue);
		enabledPanel.add(enabledFalse);
		gridBag.gridx = 1;
		gridBag.gridy = 5;
		add(enabledPanel, gridBag);
	}

	private void addSave(GridBagConstraints gridBag) {

		KeyListener enter = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent ke) {
			}

			@Override
			public void keyPressed(KeyEvent ke) {
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					if (ke.getSource().equals(btnSave)) {
						btnSave.doClick();
					} else {
						btnCancel.doClick();
					}
				}
			}
		};

		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		btnSave.addKeyListener(enter);
		btnSave.setEnabled(selectedSeg != null);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.addKeyListener(enter);

		JPanel actionPanel = new JPanel();
		actionPanel.add(btnSave);
		actionPanel.add(btnCancel);
		gridBag.gridx = 1;
		gridBag.gridy = 6;
		add(actionPanel, gridBag);
	}

	public void resetForm() {
		// if (!setType(prevType)) {
		// typeList.setSelectedIndex(0);
		// }
		setType(prevType);
		commentContent.setText(prevComment != null ? prevComment : "");
		// severityRating.setValue(prevSeverity);
		severityList.setSelectedItem(prevSeverity);
		profileRefLink.setText(prevProfile != null ? prevProfile.toString()
		        : "");
		if (prevEnabled) {
			enabledTrue.doClick();
		} else {
			enabledFalse.doClick();
		}
	}

	public void setType(String metadataType) {
		boolean found = false;
		for (int i = 0; i < typeList.getModel().getSize(); i++) {
			if (typeList.getModel().getElementAt(i).equals(metadataType)) {
				found = true;
				break;
			}
		}
		if (found) {
			typeList.setSelectedItem(metadataType);
		} else {
			typeList.setSelectedIndex(0);
		}
		// for (int i = 0; i < LQI_TYPE.length; i++) {
		// String value = LQI_TYPE[i];
		// if (value.equals(metadataType)) {
		// typeList.setSelectedIndex(i);
		// return true;
		// }
		// }
		// return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) {
			if (addingLQI()) {
				frame.dispose();
			} else {
				resetForm();
			}

		} else if (e.getSource() == btnSave) {
			LanguageQualityIssue lqi = new LanguageQualityIssue();
			lqi.setType(typeList.getSelectedItem().toString());
			lqi.setComment(commentContent.getText());
			lqi.setSeverity(((LQISeverity) severityList.getSelectedItem())
			        .getScore());
			lqi.setSeverityName(((LQISeverity) severityList.getSelectedItem())
			        .getName());
			if (!profileRefLink.getText().isEmpty()) {
				try {
					lqi.setProfileReference(new URL(profileRefLink.getText()));
				} catch (MalformedURLException ex) {
					LOG.warn("Profile reference '" + profileRefLink.getText()
					        + "' is not a valid URL", ex);
				}
			}
			lqi.setEnabled(enabled);

			if (!selectedSeg.getLQI().isEmpty()) {
				lqi.setIssuesRef(selectedSeg.getLQI().get(0).getIssuesRef());
			} else {
				// TODO: generate unique LQI issues ref
				lqi.setIssuesRef(Calendar.getInstance().getTime().toString());
			}

			if (addingLQI()) {
				eventQueue.post(new LQIAdditionEvent(lqi, selectedSeg));
				frame.dispose();
			} else {
				eventQueue.post(new LQIEditEvent(lqi, this.selectedLQI,
				        selectedSeg, this.selectedLQI));
			}

		} else if (e.getSource() == enabledTrue) {
			enabled = true;
		} else if (e.getSource() == enabledFalse) {
			enabled = false;
		}

	}

	public boolean addingLQI() {
		return frame != null && this.selectedLQI == null;
	}

	public void setWindowListener(WindowListener winodowListener) {
		this.windowListener = winodowListener;
	}

	public void setSegment(OcelotSegment segment) {
		this.selectedSeg = segment;
		btnSave.setEnabled(segment != null);
	}

	public void setMetadata(OcelotSegment selectedSegment,
	        LanguageQualityIssue lqi) {
		setSegment(selectedSegment);
		this.selectedLQI = lqi;

		prevType = lqi.getType();
		prevComment = lqi.getComment();
		prevSeverity = new LQISeverity(lqi.getSeverityName(), lqi.getSeverity());
		prevProfile = lqi.getProfileReference();
		prevEnabled = lqi.isEnabled();

		segmentLabel.setText("Segment #");
		if (selectedSegment != null) {
			segmentId.setText(selectedSegment.getSegmentNumber() + "");
		} else {
			segmentId.setText("");
		}

		typeLabel.setText("Type");
		setType(prevType);

		commentLabel.setText("Comment");
		commentContent.setText(prevComment);
		commentContent.setVisible(true);

		severityLabel.setText("Severity");
		severityList.setSelectedItem(prevSeverity);

		profileLabel.setText("Profile Reference");
		profileRefLink.setText(prevProfile != null ? prevProfile.toString()
		        : "");

		enabledLabel.setText("Enabled");
		enabledTrue.setSelected(prevEnabled);
		enabledFalse.setSelected(!prevEnabled);
	}

	public void clearDisplay() {
		segmentLabel.setText("");
		segmentId.setText("");
		typeLabel.setText("");
		typeList.setVisible(false);
		commentLabel.setText("");
		commentContent.setText("");
		commentScroll.setVisible(false);
		severityLabel.setText("");
		profileLabel.setText("");
		profileRefLink.setText("");
		profileRefLink.setVisible(false);
		enabledLabel.setText("");
		enabledTrue.setVisible(false);
		enabledFalse.setVisible(false);
		btnSave.setVisible(false);
		btnCancel.setVisible(false);
	}

	@Override
	public void run() {

		frame = new JFrame("Add Language Quality Issue");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.getContentPane().add(this);
		if (windowListener != null) {
			frame.addWindowListener(windowListener);
		}
		frame.pack();
		frame.setVisible(true);
	}

}
