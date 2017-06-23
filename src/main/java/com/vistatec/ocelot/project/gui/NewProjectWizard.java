package com.vistatec.ocelot.project.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class NewProjectWizard extends JDialog implements Runnable, ActionListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2189476058088806644L;

	private static final int WIDTH = 600;
	
	private static final int HEIGHT = 500;
	
	private enum WizardPage {

		WELCOME, PROJ_DETAILS, LANGUAGES, FILES, LQI_CONFIG, TM_CONFIG, END;
	}
	
	private JButton btnCancel;

	private JButton btnNext;

	private JButton btnBack;

	private JPanel mainPanel;
	
	private JLabel lblTitle;
	
	private JTextArea txtTitle;
//	private JButton btnEnd;
	private JPanel welcomePanel;

	private WizardPage currentPage;

	
	public NewProjectWizard(Window ownerFrame) {
		super(ownerFrame);
		setModal(true);
		currentPage = WizardPage.WELCOME;
		initDialog();
	}

	private void initDialog() {

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setTitle("Ocelot Project Wizard");
		setSize(new Dimension(WIDTH, HEIGHT));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setResizable(false);
		add(getTitlePanel(), BorderLayout.NORTH);
		mainPanel = new JPanel();
		add(mainPanel, BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
		
		setCurrentPanel();
	}
	
	private JPanel getTitlePanel(){
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		JPanel labelContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		lblTitle = new JLabel();
//		lblTitle.setAlignmentX(LEFT_ALIGNMENT);
		lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
//		lblTitle.setBorder(new LineBorder(Color.red));
		lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14));
		txtTitle = new JTextArea();
		txtTitle.setBorder(new EmptyBorder(5, 5, 5, 5));
		txtTitle.setBackground(SystemColor.control);
		txtTitle.setEditable(false);
		txtTitle.setWrapStyleWord(true);
		txtTitle.setLineWrap(true);
		labelContainer.add(lblTitle);
		titlePanel.add(labelContainer );
		titlePanel.add(txtTitle);
		return titlePanel;
	}

	
	private void setCurrentPanel() {
		mainPanel.removeAll();
		mainPanel.setLayout(new BorderLayout());
		JPanel currentPanel = null;
		switch (currentPage) {
		case WELCOME:
			currentPanel = getWelcomePanel();
//			lblTitle.setText("Welcome");
//			txtTitle.setText("This wizard will guide you to create a new Ocelot project.");
			lblTitle.setText("");
			txtTitle.setText("");
			btnBack.setEnabled(false);
			break;
		case END:
			btnNext.setText("End");
			break;
		default:
			break;
		}
		mainPanel.add(currentPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	private JPanel getWelcomePanel() {

		if(welcomePanel == null){
			welcomePanel = new JPanel(new BorderLayout());
			Toolkit kit = Toolkit.getDefaultToolkit();
			ImageIcon icon = new ImageIcon(kit.createImage(getClass().getResource("ocelot-logo.png")));
			JLabel logoLabel = new JLabel(icon);
			logoLabel.setBorder(new EmptyBorder(10, 50, 10, 10));
//			welcomePanel.add(logoLabel, BorderLayout.CENTER);
			welcomePanel.add(logoLabel, BorderLayout.WEST);
			JLabel welcomeLabel = new JLabel("Welcome");
			welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 30));
			JTextArea welcomeArea = new JTextArea();
			welcomeArea.setBackground(SystemColor.control);
			welcomeArea.setEditable(false);
			welcomeArea.setWrapStyleWord(true);
			welcomeArea.setLineWrap(true);
			welcomeArea.setFont(welcomeArea.getFont().deriveFont(Font.PLAIN, 16));
			welcomeArea.setText("This wizard will guide you to create an Ocelot project.");
			JPanel centerContainer = new JPanel();
			centerContainer.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(0, 50, 10, 50);
			centerContainer.add(welcomeLabel, c);
			c.gridy = 1;
			c.weightx = 0.5;
			c.insets = new Insets(0, 50, 10, 50);
			c.fill = GridBagConstraints.HORIZONTAL;
			centerContainer.add(welcomeArea, c);
			c.weighty = 0.1;
			c.gridy = 2;
			c.insets = new Insets(50, 50, 10, 50);
			centerContainer.add(logoLabel, c);
			
			welcomePanel.add(centerContainer, BorderLayout.CENTER);
			
			
		}
		return welcomePanel;
	}

	private JPanel getBottomPanel(){
		
		JPanel bottomPanel = new JPanel();
		btnBack = new JButton("< Back");
		btnBack.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
//		btnEnd = new JButton("End");
//		btnEnd.addActionListener(this);
		btnNext = new JButton("Next >");
		btnNext.addActionListener(this);
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		bottomPanel.add(btnCancel );
		bottomPanel.add(btnBack );
		bottomPanel.add(btnNext );
//		bottomPanel.add(btnEnd );
		return bottomPanel;
	}
	
	private void next() {

		if (!currentPage.equals(WizardPage.END)) {
			currentPage = WizardPage.values()[currentPage.ordinal() + 1];
		}
	}

	private void back() {
		if (!currentPage.equals(WizardPage.WELCOME)) {
			currentPage = WizardPage.values()[currentPage.ordinal() - 1];
		}
	}

	private void cancel() {

		int option = JOptionPane.showConfirmDialog(this, "Do you want to exit the wizard?", "Close Wizard Confirmation",
				JOptionPane.YES_NO_OPTION);
		if(option == JOptionPane.YES_OPTION){
			close();
		}
	}

	private void close() {

		setVisible(false);
		dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnNext)) {
			next();
		} else if (e.getSource().equals(btnBack)) {
			back();
		} else if (e.getSource().equals(btnCancel)) {
			cancel();
		}

	}

	@Override
	public void windowOpened(WindowEvent e) {
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		cancel();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}
	
	public static void main(String[] args) {
		
		NewProjectWizard wizard = new NewProjectWizard(null);
		SwingUtilities.invokeLater(wizard);
	}

	@Override
	public void run() {
		
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

}
