package ar.com.qsy.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import ar.com.qsy.model.objects.QSYPacket;

public final class SearchPanel extends JPanel implements AutoCloseable {

	private static final long serialVersionUID = 1L;

	private final JTable table;
	private final JButton btnStartSearch;
	private final JButton btnStopSearch;

	private final QSYTableModel model;

	public SearchPanel(final QSYFrame parent) {
		this.setBorder(BorderFactory.createTitledBorder("Nodes"));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		table = new JTable(new QSYTableModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);

		final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

		final JScrollPane scrollPanel = new JScrollPane(table);
		this.add(scrollPanel);

		model = (QSYTableModel) table.getModel();

		btnStartSearch = new JButton("Search Nodes");
		btnStartSearch.setAlignmentX(CENTER_ALIGNMENT);
		btnStartSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// while (table.getModel().getRowCount() > 0) {
				// ((QSYTableModel) table.getModel()).removeRow(0);
				// }
				parent.getTerminal().searchNodes();
				btnStopSearch.setVisible(true);
				btnStartSearch.setVisible(false);
			}
		});

		this.add(btnStartSearch);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				final int selectedRow = table.getSelectedRow();
				final boolean enabled = selectedRow != -1;

				parent.getCommandPanel().setEnabled(enabled);
			}

		});

		btnStopSearch = new JButton("Finalize Nodes Search");
		btnStopSearch.setAlignmentX(CENTER_ALIGNMENT);
		btnStopSearch.setVisible(false);
		btnStopSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				parent.getTerminal().finalizeNodesSearch();
				btnStopSearch.setVisible(false);
				btnStartSearch.setVisible(true);
			}

		});
		this.add(btnStopSearch);
	}

	public JTable getTable() {
		return table;
	}

	public void addNewNode(final QSYPacket qsyPacket) {
		model.addNode(qsyPacket);
	}

	public void removeNode(final int nodeId) {
		model.removeNode(nodeId);
	}

	@Override
	public void close() throws Exception {
		return;
	}
}
