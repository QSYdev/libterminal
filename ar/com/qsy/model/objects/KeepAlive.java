package ar.com.qsy.model.objects;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import ar.com.qsy.model.patterns.observer.Event;
import ar.com.qsy.model.patterns.observer.Event.EventType;
import ar.com.qsy.model.patterns.observer.EventSource;
import ar.com.qsy.model.patterns.observer.SynchronousListener;

public final class KeepAlive extends EventSource implements AutoCloseable, SynchronousListener {

	public static final int MAX_KEEP_ALIVE_DELAY = (int) ((5 / 2f) * QSYPacket.KEEP_ALIVE_MS);

	private final TreeMap<Integer, Node> nodes;
	private final Timer timer;
	private final DeadNodesPurger deadNodesPurgerTask;

	public KeepAlive(final TreeMap<Integer, Node> nodes) {
		this.nodes = nodes;
		this.timer = new Timer("Dead Nodes Purger");
		this.timer.scheduleAtFixedRate(deadNodesPurgerTask = new DeadNodesPurger(), 0, MAX_KEEP_ALIVE_DELAY);
	}

	private void newNodeCreated(final Node node) throws Exception {
		final long currentTime = System.currentTimeMillis();
		final boolean nodeAlive;

		nodeAlive = node.isAlive(currentTime);
		node.keepAlive(currentTime);

		if (!nodeAlive) {
			sendEvent(new Event(EventType.keepAliveError, node));
		}
	}

	private void qsyKeepAlivePacketReceived(final QSYPacket qsyPacket) {
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
	}

	@Override
	public void receiveEvent(final Event event) throws Exception {
		switch (event.getEventType()) {
		case newNode: {
			final Node node = (Node) event.getContent();
			newNodeCreated(node);
			break;
		}
		case keepAliveReceived: {
			final QSYPacket qsyPacket = (QSYPacket) event.getContent();
			qsyKeepAlivePacketReceived(qsyPacket);
			break;
		}
		default: {
			break;
		}
		}
	}

	private final class DeadNodesPurger extends TimerTask implements AutoCloseable {

		public DeadNodesPurger() {
		}

		@Override
		public void run() {
			final long currentTime = System.currentTimeMillis();
			boolean nodeAlive = true;
			Node disconectedNode = null;

			synchronized (nodes) {
				for (final Node node : nodes.values()) {
					if (!node.isAlive(currentTime)) {
						nodeAlive = false;
						disconectedNode = node;
						break;
					}
				}
			}

			if (!nodeAlive) {
				try {
					sendEvent(new Event(EventType.keepAliveError, disconectedNode));
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
