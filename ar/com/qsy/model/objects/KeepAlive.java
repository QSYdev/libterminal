package ar.com.qsy.model.objects;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public final class KeepAlive implements AutoCloseable {

	public static final int MAX_KEEP_ALIVE_DELAY = (int) ((5 / 2f) * QSYPacket.KEEP_ALIVE_MS);

	private final TreeMap<Integer, Node> nodes;
	private final Timer timer;
	private final DeadNodesPurger deadNodesPurgerTask;

	public KeepAlive(final TreeMap<Integer, Node> nodes) {
		this.nodes = nodes;
		this.timer = new Timer("Dead Nodes Purger");
		this.timer.scheduleAtFixedRate(deadNodesPurgerTask = new DeadNodesPurger(), 0, MAX_KEEP_ALIVE_DELAY);
	}

	public void qsyHelloPacketReceived(final QSYPacket qsyPacket) {
		final long currentTime = System.currentTimeMillis();
		final int nodeId = qsyPacket.getId();
		final boolean nodeAlive;

		final Node node;
		synchronized (nodes) {
			node = nodes.get(nodeId);
		}
		nodeAlive = node.isAlive(currentTime);
		node.keepAlive(currentTime);

		if (!nodeAlive) {
			// TODO notificar a la terminal.
			System.err.println("Timer>> Se ha desconectado un nodo");
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
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	private final class DeadNodesPurger extends TimerTask implements AutoCloseable {

		public DeadNodesPurger() {
		}

		@Override
		public void run() {
			final long currentTime = System.currentTimeMillis();
			boolean nodeAlive = true;

			synchronized (nodes) {
				for (final Node node : nodes.values()) {
					nodeAlive = node.isAlive(currentTime);
				}
			}

			if (!nodeAlive) {
				// TODO notificar a la terminal.
				System.err.println("Timer>> Se ha desconectado un nodo");
			}
		}

		@Override
		public void close() throws Exception {
			return;
		}

	}

}
