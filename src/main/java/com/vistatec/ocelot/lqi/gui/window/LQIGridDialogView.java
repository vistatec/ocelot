package com.vistatec.ocelot.lqi.gui.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.vistatec.ocelot.lqi.LQIGridController;
import com.vistatec.ocelot.lqi.LQIKeyEventHandler;
import com.vistatec.ocelot.lqi.LQIKeyEventManager;
import com.vistatec.ocelot.lqi.gui.LQIGridButton;
import com.vistatec.ocelot.lqi.gui.LQIGridTableHelper;
import com.vistatec.ocelot.lqi.gui.LQIGridTableModel;
import com.vistatec.ocelot.lqi.gui.panel.LQIGridTableContainer;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

/**
 * This dialog displays the LQI Grid. It lets users create new Language Quality Issues for the selected segment.
 */
public class LQIGridDialogView extends JDialog implements Runnable, ActionListener {

	private static final long serialVersionUID = -8122056085897056836L;

	/** Dialog title. */
	private static final String TITLE = "LQI Grid";
	
	private JButton btnManageConf;
	
	private JButton btnClose;
	
	private JTextField txtConfigName;
	
	private LQIGridController controller;
	
	private LQIGridConfiguration activeConfiguration;
	
	private LQIKeyEventHandler lqiGridKeyEventHandler;
	
	private LQIGridTableHelper tableHelper;
	
	private LQIGridTableContainer tableContainer;
	
	private boolean showManageConfBtn;
	
	
	public LQIGridDialogView(Window owner, LQIGridController controller,
	        LQIGridConfigurations lqiGrid, boolean showManageConfBtn ) {
	
		super(owner);
		setModal(false);
		this.controller = controller;
		activeConfiguration = lqiGrid.getActiveConfiguration();
		tableHelper = new LQIGridTableHelper(controller.getPlatformSupport());
		this.showManageConfBtn = showManageConfBtn;
		
	}

	
	/**
	 * Initializes the dialog.
	 */
	private void init() {

		lqiGridKeyEventHandler = new LQIKeyEventHandler(controller,
		        getRootPane());
		LQIKeyEventManager.getInstance().addKeyEventHandler(
		        lqiGridKeyEventHandler);
		lqiGridKeyEventHandler.load(activeConfiguration);
		String title = TITLE;
		setTitle(title);
		setResizable(false);
		add(getTopPanel(), BorderLayout.NORTH);
		add(getCenterComponent(), BorderLayout.CENTER);
		add(getBottomComponent(), BorderLayout.SOUTH);
		tableContainer.setTableSize();
		setLocationRelativeTo(getOwner());
	}
	
	
	
	
	private Component getBottomComponent() {
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		btnClose = new JButton("Close");
		btnClose.addActionListener(this);
		bottomPanel.add(btnClose);
		btnManageConf = new JButton("Manage Configurations");
		btnManageConf.addActionListener(this);
		bottomPanel.add(Box.createRigidArea(new Dimension(1, 50)));
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(btnManageConf);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(btnClose);
		bottomPanel.add(Box.createHorizontalStrut(10));
		btnManageConf.setVisible(showManageConfBtn);
		
	    return bottomPanel;
    }


	private Component getTopPanel() {
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		txtConfigName = new JTextField();
		txtConfigName.setText(activeConfiguration.getName());
		txtConfigName.setEditable(false);
		txtConfigName.setBorder(null);
		topPanel.add(new JLabel("Configuration:"));
		topPanel.add(txtConfigName);
		return topPanel;
	}
	
	private Component getCenterComponent() {
		
		Action annotationAction = new AbstractAction() {
			
            private static final long serialVersionUID = 5854460573789976997L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LQIGridButton button = (LQIGridButton) e.getSource();
				int severityIndex = button.getSeverityColumn()
				        - tableHelper.getLqiTableModel()
				                .getSeverityColsStartIndex();
				double severity = activeConfiguration.getSeverities()
				        .get(severityIndex).getScore();
				String severityName = activeConfiguration.getSeverities()
				        .get(severityIndex).getName();
				String categoryName = tableHelper
				        .getLqiTable()
				        .getValueAt(
				                button.getCategoryRow(),
				                tableHelper.getLqiTableModel()
				                        .getErrorCategoryColumn())
				        .toString();
				controller.createNewLqi(categoryName, severity,
				        severityName);
			}
		};
		tableContainer = new LQIGridTableContainer(activeConfiguration, tableHelper, annotationAction, LQIGridTableModel.ISSUES_ANNOTS_MODE);
		
	    return tableContainer;
	    
    }
	
	public void replaceActiveConfiguration(LQIGridConfiguration lqiGridConf){
		
		System.out.println("Replacing configuration in LQI Grid dialog");
		//new active configuration selected
		if (!activeConfiguration.equals(lqiGridConf)) {
			activeConfiguration = lqiGridConf;
			txtConfigName.setText(lqiGridConf.getName());
			tableContainer.replaceConfiguration(lqiGridConf);
			repaint();
			revalidate();
			//name of the active configuration changed
		} else if (!txtConfigName.getText().equals(lqiGridConf.getName())) {
			txtConfigName.setText(lqiGridConf.getName());
			repaint();
		}
		
	}
	
	public void refresh(){
		
		txtConfigName.setText(activeConfiguration.getName());
		tableContainer.refresh();
	}


	
	@Override
	public void run() {
		init();
		setVisible(true);
	}
	

	public boolean canCreateIssue(){
		
		return !controller.isOcelotEditing() && !tableContainer.isEditingCommentColumn();
	}

	/**
	 * Gets the comment for a specific category.
	 * 
	 * @param errorCategory
	 *            the error category
	 * @return the comment
	 */
	public String getCommentForCategory(String errorCategory) {

		return tableContainer.getCommentByCategory(errorCategory);
	}
	
	public void clearCommentCellForCategory(String category){
		
		tableContainer.clearCommentCellForCategory(category);
	}

	public LQIGridConfiguration getLqiConfiguration(){
		
		return activeConfiguration;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
	    	
		if(e.getSource().equals(btnClose)){
			controller.closeActiveDialog();
		} else if (e.getSource().equals(btnManageConf)){
			controller.manageConfigurations();
		}
    }

}

