package com.vistatec.ocelot.project;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.vistatec.ocelot.events.OpenProjectFileEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;

public class OcelotProjectPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = -4150958748577903247L;

	private JTable filesTable;

	private ProjectFilesTableModel filesTableModel;

	private OcelotEventQueue eventQueue;

	public OcelotProjectPanel(OcelotEventQueue eventQueue) {
		this.eventQueue = eventQueue;
		makePanel();
	}

	private void makePanel() {

		setLayout(new BorderLayout());
		filesTableModel = new ProjectFilesTableModel();
		filesTable = new JTable(filesTableModel);
		filesTable.addMouseListener(this);
		JScrollPane scroll = new JScrollPane(filesTable);
		add(scroll, BorderLayout.CENTER);

	}

	public void loadFiles(List<ProjectFile> files) {
		filesTableModel.setModel(files);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

			int selRow = filesTable.getSelectedRow();
			if (selRow > -1) {
				ProjectFile selFile = filesTableModel.getFileAtRow(selRow);
				eventQueue.post(new OpenProjectFileEvent(selFile));
				// try {

				// ocelotApp.openFile(selFile.getFile(), false);
				// ((JTabbedPane)getParent()).setSelectedIndex(1);
				// } catch (IOException | XMLStreamException ex) {
				// JOptionPane.showMessageDialog(this, "An error occurred while
				// opening the file",
				// "Ocelot Open File Error", JOptionPane.ERROR_MESSAGE);
				// }
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// do nothing

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// do nothing

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing

	}

}

class ProjectFilesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -4917450068869627349L;

	private static final int FILE_NAME_COL = 0;

	private static final int TARGET_LANG_COL = 1;

	private static final int TYPE_COL = 2;
	
	private static final int STATUS_COL = 3;

	private final String[] columns = { "File", "Target Language", "Type", "Status" };

	private List<ProjectFile> model;

	@Override
	public int getRowCount() {
		return model != null ? model.size() : 0;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int column) {
		return column < columns.length ? columns[column] : "";
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Object retValue = null;

		if (rowIndex < getRowCount()) {
			ProjectFile file = model.get(rowIndex);
			switch (columnIndex) {
			case FILE_NAME_COL:
				retValue = file.getFile().getName();
				break;
			case TARGET_LANG_COL:
				retValue = file.getTargetLanguage();
				break;
			case TYPE_COL:
				retValue = file.getType().toString();
				break;
			case STATUS_COL:
				break;
			default:
				break;
			}
		}
		return retValue;
	}

	public ProjectFile getFileAtRow(int row) {

		if (row < getRowCount()) {
			return model.get(row);
		} else {
			return null;
		}
	}

	public void setModel(List<ProjectFile> model) {
		this.model = model;
		fireTableDataChanged();
	}

}
