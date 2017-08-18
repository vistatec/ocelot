package com.vistatec.ocelot.dqf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.OcelotApp;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.events.DQFFileClosedEvent;
import com.vistatec.ocelot.events.DQFFileOpenedEvent;
import com.vistatec.ocelot.events.DQFProjectOpenedEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

public class DQFOcelotGuiHelper implements OcelotEventQueueListener {

	private JTabbedPane projectEditorTabbedPane;
	private JTabbedPane taskEditorTabbedPane;
	private JSplitPane mainOcelotSplitPane;
	private JSplitPane ocelotEditorPane;
	private JInternalFrame projectFrame;

	private OcelotApp ocelotApp;

	public DQFOcelotGuiHelper(OcelotApp ocelotApp, JSplitPane mainOcelotSplitPane, JSplitPane ocelotEditorPane) {

		this.ocelotApp = ocelotApp;
		this.mainOcelotSplitPane = mainOcelotSplitPane;
		this.ocelotEditorPane = ocelotEditorPane;
	}

	private void displayProjectComponent(String projectName, Component projectComponent,
			LQIGridConfiguration lqiGridConfiguration) {

		// if (lqiGridConfiguration != null) {
		// LQIGridConfigurations tempLQIConfigurations = new
		// LQIGridConfigurations();
		// tempLQIConfigurations.addConfiguration(lqiGridConfiguration);
		// tempLQIConfigurations.setActiveConfiguration(lqiGridConfiguration);
		// loadLQIKeyListener(toolBar.getSelectedLQIConfiguration(),
		// lqiGridConfiguration);
		// lqiGridController.setDqfConfiguration(tempLQIConfigurations);
		// toolBar.setLQIConfigurations(tempLQIConfigurations);
		// toolBar.setLqiToolEnabled(false);
		// }

		Ocelot.getInstance().setDQFLQIGridConfiguration(lqiGridConfiguration);

		projectFrame = new JInternalFrame(projectName, false, true, false, false);
		projectFrame.setFrameIcon(ocelotApp.getDQFIcon());
		projectEditorTabbedPane = new JTabbedPane();
		projectEditorTabbedPane.addTab("Project", projectComponent);
		projectEditorTabbedPane.addTab("File Editor", ocelotEditorPane);
		projectFrame.add(projectEditorTabbedPane);
		mainOcelotSplitPane.setRightComponent(projectFrame);
		projectFrame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		projectFrame.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				System.out.println("Closing");
				startDQFProjectClosureProcedure(false);
				// boolean canClose = true;
				// if (ocelotApp.hasOpenFile() && ocelotApp.isFileDirty()) {
				// int option = JOptionPane.showConfirmDialog(projectFrame, "Do
				// you want to save the project file?",
				// "Save Project", JOptionPane.YES_NO_CANCEL_OPTION);
				// canClose = option == JOptionPane.YES_OPTION || option ==
				// JOptionPane.NO_OPTION;
				// if (option == JOptionPane.YES_OPTION) {
				// Ocelot.getInstance().saveFile();
				// }
				// }
				// if (canClose) {
				// projectFrame.setVisible(false);
				// closeDQFProject();
				// }
			}

			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				System.out.println("Closed");

			}
		});
		projectFrame.setVisible(true);
		// revalidate();
	}

	private void displayDQFTaskComponent(String taskName, Component taskComponent) {
		try {
			projectEditorTabbedPane.removeTabAt(1);
			taskEditorTabbedPane = new JTabbedPane();
			taskEditorTabbedPane.addTab("Task", taskComponent);
			taskEditorTabbedPane.addTab("File Editor", ocelotEditorPane);
			final JInternalFrame taskFrame = new JInternalFrame(taskName, false, true, false, false);
			taskFrame.setFrameIcon(ocelotApp.getDQFIcon());
			taskFrame.add(taskEditorTabbedPane);
			// JInternalFrame projectFrame =
			// ((JInternalFrame)mainOcelotSplitPane.getRightComponent());
			projectFrame.remove(projectEditorTabbedPane);
			projectFrame.add(taskFrame, BorderLayout.CENTER);
			taskFrame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
			taskFrame.addInternalFrameListener(new InternalFrameAdapter() {
				@Override
				public void internalFrameClosing(InternalFrameEvent e) {
					System.out.println("Closing");
					boolean canClose = true;
					if (ocelotApp.hasOpenFile() && ocelotApp.isFileDirty()) {
						int option = JOptionPane.showConfirmDialog(taskFrame, "Do you want to save the opened file?",
								"Save Task File", JOptionPane.YES_NO_CANCEL_OPTION);
						canClose = option == JOptionPane.YES_OPTION || option == JOptionPane.NO_OPTION;
						if (option == JOptionPane.YES_OPTION) {
							Ocelot.getInstance().saveFile();
						}
					}
					if (canClose) {
						taskFrame.setVisible(false);
						closeDQFTask();
					}
				}

				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
					System.out.println("Closed");

				}
			});
			taskFrame.setVisible(true);
			projectFrame.revalidate();
			projectFrame.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// repaint();

	}

	public boolean startDQFProjectClosureProcedure(boolean wait) {

		boolean canClose = true;
		if (ocelotApp.hasOpenFile() && ocelotApp.isFileDirty()) {
			Component activeComponent = taskEditorTabbedPane != null ? taskEditorTabbedPane : projectEditorTabbedPane;
			int option = JOptionPane.showConfirmDialog(activeComponent,
					"There is an open file in the editor pane. Do you want to save it?", "Save Project",
					JOptionPane.YES_NO_CANCEL_OPTION);
			canClose = option == JOptionPane.YES_OPTION || option == JOptionPane.NO_OPTION;
			if (option == JOptionPane.YES_OPTION) {
//				Runnable waitingDialogRunnable = null;
				JDialog waitingDialog = null;
				if (wait) {
//					final JDialog 
					waitingDialog = new JDialog(
							(Frame) SwingUtilities.getWindowAncestor(Ocelot.getInstance()), true);
					waitingDialog.setUndecorated(true);
					waitingDialog.setOpacity(0.0f);
					ocelotApp.setOcelotClosingWaitingDialog(waitingDialog);
//					waitingDialogRunnable = new Runnable() {
//
//						@Override
//						public void run() {
//							waitingDialog.setVisible(true);
//						}
//					};
				}
				Ocelot.getInstance().saveFile();
				if(waitingDialog != null){
					waitingDialog.setVisible(true);
				}
//				if (waitingDialogRunnable != null) {
//					try {
//						SwingUtilities.invokeAndWait(waitingDialogRunnable);
//					} catch (InvocationTargetException | InterruptedException e) {
//						LoggerFactory.getLogger(DQFOcelotGuiHelper.class).warn("Waiting dialog process interrupted", e);
//					}
//				}
			}
		}
		if (canClose) {
			closeDQFProject();
		}
		return canClose;
	}

	private void closeDQFProject() {

		projectFrame.setVisible(false);
		ocelotApp.closeDQFProject();
		Ocelot.getInstance().closeFile();
		try {
			Ocelot.getInstance().restoreLQIGridConfiguration();

		} catch (TransferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainOcelotSplitPane.setRightComponent(ocelotEditorPane);
		projectEditorTabbedPane = null;
	}

	protected void closeDQFTask() {

		Ocelot.getInstance().closeFile();
		projectEditorTabbedPane.add("File Editor", ocelotEditorPane);
		JInternalFrame projectFrame = ((JInternalFrame) mainOcelotSplitPane.getRightComponent());
		projectFrame.add(projectEditorTabbedPane);
		projectFrame.revalidate();
	}

	@Subscribe
	public void handleDQFProjectOpenedEvent(DQFProjectOpenedEvent event) {
		if (event.getIsTask()) {
			displayDQFTaskComponent(event.getProjectName(), event.getProjectGuiComponent());
		} else {
			displayProjectComponent(event.getProjectName(), event.getProjectGuiComponent(),
					event.getProjLqiGridConfiguration());
		}
	}

	@Subscribe
	public void handleDQFFileOpenedEvenet(DQFFileOpenedEvent event) {
		Ocelot.getInstance().openFile(event.getFile(), false);
		if (projectEditorTabbedPane.getTabCount() == 2) {
			projectEditorTabbedPane.setSelectedIndex(1);
		} else {
			taskEditorTabbedPane.setSelectedIndex(1);
		}
	}

	@Subscribe
	public void handleDQFFileClosedEvent(DQFFileClosedEvent event) {

		Ocelot.getInstance().closeFile();
	}

	public boolean isDQFProjectOpen() {
		return projectEditorTabbedPane != null;
	}
}
