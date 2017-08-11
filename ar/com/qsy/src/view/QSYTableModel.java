package ar.com.qsy.src.view;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import ar.com.qsy.src.app.node.Node;

public final class QSYTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] columnsName = { "ID", "IP Node", "Status" };
	private final List<Integer> nodes;

	public QSYTableModel() {
		this(new Object[][] {});
	}

	public QSYTableModel(final Object[][] rowData) {
		super(rowData, columnsName);
		this.nodes = new LinkedList<>();
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	public void addNode(final Node node) {
		addRow(new Object[] { node.getNodeId(), node.getNodeAddress(), "enabled" });
		nodes.add(node.getNodeId());
	}

	public void removeNode(final Node node) {
		final int rowToDelete = nodes.indexOf(node.getNodeId());
		if (rowToDelete != -1) {
			removeRow(rowToDelete);
			nodes.remove(rowToDelete);
		}

	}

}
