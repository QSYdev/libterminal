package ar.com.qsy.view;

import java.util.TreeMap;

import javax.swing.table.DefaultTableModel;

import ar.com.qsy.model.objects.externalObjects.Node;

public final class QSYTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] columnsName = { "ID", "IP Node", "Status" };
	private final TreeMap<Integer, Integer> nodes;

	public QSYTableModel() {
		this(new Object[][] {});
	}

	public QSYTableModel(final Object[][] rowData) {
		super(rowData, columnsName);
		this.nodes = new TreeMap<>();
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	public void addNode(final Node node) {
		addRow(new Object[] { node.getNodeId(), node.getNodeAddress(), "enabled" });
		nodes.put(node.getNodeId(), getRowCount() - 1);
	}

	public void removeNode(final Node node) {
		final Integer rowToDelete = nodes.get(node.getNodeId());
		if (rowToDelete != null) {
			removeRow(rowToDelete);
			nodes.remove(node.getNodeId());
		}

	}

}
