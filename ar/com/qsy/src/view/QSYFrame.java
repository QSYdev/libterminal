package ar.com.qsy.src.view;

import ar.com.qsy.src.app.terminal.Node;
import ar.com.qsy.src.app.terminal.Terminal;
import ar.com.qsy.src.patterns.observer.Event;
import ar.com.qsy.src.patterns.observer.EventListener;

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

public final class QSYFrame extends JFrame implements AutoCloseable, EventListener {

	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 550;
	private static final int HEIGHT = 600;

	private final SearchPanel searchPanel;
	private final CommandPanel commandPanel;
	private final RoutinePanel routinePanel;

	private final Terminal terminal;

	public QSYFrame(final Terminal terminal) {
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

		this.terminal = terminal;

		searchPanel = new SearchPanel(this);
		commandPanel = new CommandPanel(this);
		routinePanel = new RoutinePanel(this);

		final Container rightPane = new Container();
		rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
		rightPane.add(commandPanel);
		rightPane.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, Integer.MAX_VALUE), new Dimension(0, Integer.MAX_VALUE)));

		rightPane.add(routinePanel);
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

	private void newNodeCreated(final Node node) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				searchPanel.addNewNode(node);
			}
		});
	}

	private void removeDisconectedNode(final Node node) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				searchPanel.removeNode(node);
			}
		});
	}

	@Override
	public void close() throws Exception {
		searchPanel.close();
		commandPanel.close();
	}

	@Override
	public void receiveEvent(final Event event) {
		switch (event.getEventType()) {
		case newNode: {
			final Node node = (Node) event.getContent();
			newNodeCreated(node);
			break;
		}
		case disconnectedNode: {
			final Node node = (Node) event.getContent();
			removeDisconectedNode(node);
			break;
		}
		default: {
			break;
		}
		}
	}
}
