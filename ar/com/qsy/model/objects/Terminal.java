package ar.com.qsy.model.objects;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.com.qsy.model.utils.QSYPacketTools;
import ar.com.qsy.model.utils.QSYPacketTools.QSYPacket;

public final class Terminal implements Runnable, AutoCloseable {

	private final BlockingQueue<QSYPacket> buffer;
	private final ReceiverSelector receiverSelector;
	private final AtomicBoolean searchNodes;
	private final HashSet<InetAddress> nodes;

	private final AtomicBoolean running;

	public Terminal(final BlockingQueue<QSYPacket> buffer, final ReceiverSelector receiverSelector) {
		this.buffer = buffer;
		this.receiverSelector = receiverSelector;
		this.nodes = new HashSet<>();
		this.searchNodes = new AtomicBoolean(false);
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {

			try {
				QSYPacket qsyPacket = buffer.take();

				if (QSYPacketTools.isHelloPacket(qsyPacket.getData())) {
					if (searchNodes.get() && !nodes.contains(qsyPacket.getNodeAddress())) {
						printQSYPacket(qsyPacket);
						nodes.add(qsyPacket.getNodeAddress());
						receiverSelector.registerNewSocketChannel(qsyPacket.getNodeAddress().getHostAddress(), QSYPacketTools.TCP_PORT, null);
						// TODO notifyEvent(new
						// TerminalEventArgs(TerminalEventArgs.NEW_NODE_CONNECTION_EVENT,
						// new Object[] { this, qsyPacket }));
					}
				} else if (QSYPacketTools.isKeepAlivePacket(qsyPacket.getData())) {
					printQSYPacket(qsyPacket);
					// TODO Keepalive
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void printQSYPacket(final QSYPacket qsyPacket) {
		System.out.println("Data received from: " + qsyPacket.getNodeAddress());
		final byte[] data = qsyPacket.getData();
		for (int i = 0; i < data.length; i++) {
			System.out.print("[ " + data[i] + " ]\t");
		}
		System.out.println();
	}

	public void searchNodes() {
		searchNodes.set(true);
	}

	public void finalizeNodesSearch() {
		searchNodes.set(false);
	}

	@Override
	public void close() {
		nodes.clear();
		running.set(false);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

}
