package ar.com.qsy.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ar.com.qsy.model.objects.QSYPacket;
import ar.com.qsy.model.objects.Terminal;

public final class QSYFrame extends JFrame implements AutoCloseable {

	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 550;
	private static final int HEIGHT = 600;

	private final SearchPanel searchPanel;
	private final CommandPanel commandPanel;

	private Terminal terminal;

	public QSYFrame() {
		super("QSY Packet Sender");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, WIDTH, HEIGHT);
		setLocationRelativeTo(null);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(final WindowEvent e) {
				// TODO notificar a la terminal.
			}
		});

		searchPanel = new SearchPanel(this);
		commandPanel = new CommandPanel(this);

		final Container rightPane = new Container();
		rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
		rightPane.add(commandPanel);
		rightPane.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, Integer.MAX_VALUE), new Dimension(0, Integer.MAX_VALUE)));

		final JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());

		contentPane.add(searchPanel, BorderLayout.CENTER);
		contentPane.add(rightPane, BorderLayout.EAST);
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		commandPanel.setEnabled(false);

		setVisible(true);
	}

	public SearchPanel getSearchPanel() {
		return searchPanel;
	}

	public CommandPanel getCommandPanel() {
		return commandPanel;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public void setTerminal(final Terminal terminal) {
		this.terminal = terminal;
	}

	public void addNewNode(final QSYPacket qsyPacket) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				searchPanel.addNewNode(qsyPacket);
			}
		});
	}

	@Override
	public void close() throws Exception {
		searchPanel.close();
		commandPanel.close();
	}
}
