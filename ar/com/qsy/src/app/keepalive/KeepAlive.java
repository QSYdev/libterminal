package ar.com.qsy.src.app.keepalive;

import ar.com.qsy.src.app.protocol.QSYPacket;
import ar.com.qsy.src.app.node.Node;
import ar.com.qsy.src.patterns.observer.Event;
import ar.com.qsy.src.patterns.observer.Event.EventType;
import ar.com.qsy.src.patterns.observer.EventSource;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public final class KeepAlive extends EventSource {

	public static final int MAX_KEEP_ALIVE_DELAY = (int) ((5 / 2f) * QSYPacket.KEEP_ALIVE_MS);

	private final TreeMap<Integer, Node> nodes;
	private final Timer timer;
	private final DeadNodesPurger deadNodesPurgerTask;

	public KeepAlive(final TreeMap<Integer, Node> nodes) {
		this.nodes = nodes;
		this.timer = new Timer("Dead Nodes Purger", false);
		this.timer.scheduleAtFixedRate(deadNodesPurgerTask = new DeadNodesPurger(), 0, MAX_KEEP_ALIVE_DELAY);
	}

	public void newNodeCreated(final Node node) throws Exception {
		final long currentTime = System.currentTimeMillis();
		final boolean nodeAlive;

		nodeAlive = node.isAlive(currentTime);
		node.keepAlive(currentTime);

		if (!nodeAlive) {
			sendEvent(new Event(EventType.keepAliveError, node));
		}
	}

	public void qsyKeepAlivePacketReceived(final QSYPacket qsyPacket) {
		final long currentTime = System.currentTimeMillis();

		final Node node;
		synchronized (nodes) {
			node = nodes.get(qsyPacket.getId());
		}
		node.keepAlive(currentTime);
	}

	@Override
	public void close() throws Exception {
		timer.cancel();
		deadNodesPurgerTask.close();
		super.close();
	}

	private final class DeadNodesPurger extends TimerTask implements AutoCloseable {

		public DeadNodesPurger() {
		}

		@Override
		public void run() {
			final long currentTime = System.currentTimeMillis();
			boolean nodeAlive = true;
			Node disconnectedNode = null;

			synchronized (nodes) {
				for (final Node node : nodes.values()) {
					if (!node.isAlive(currentTime)) {
						nodeAlive = false;
						disconnectedNode = node;
						break;
					}
				}
			}

			if (!nodeAlive) {
				try {
					sendEvent(new Event(EventType.keepAliveError, disconnectedNode));
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void close() throws Exception {
			return;
		}

	}

}
