package ar.com.qsy;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Terminal implements Runnable, Cleanable {

	private final Buffer<QSYPacket> buffer;
	private final ReceiverSelector receiverSelector;
	private volatile AtomicBoolean searchNodes;

	private final HashSet<InetAddress> nodes;

	private volatile AtomicBoolean running;

	public Terminal(final Buffer<QSYPacket> buffer, final ReceiverSelector receiverSelector) {
		this.buffer = buffer;
		this.receiverSelector = receiverSelector;
		this.nodes = new HashSet<>();
		this.searchNodes = new AtomicBoolean(false);
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {
			final QSYPacket qsyPacket = buffer.remove();
			if (searchNodes.get() && QSYPacketTools.isHelloPacket(qsyPacket.getData())) {
				if (!nodes.contains(qsyPacket.getNodeAddress())) {
					printQSYPacket(qsyPacket);
					nodes.add(qsyPacket.getNodeAddress());
					receiverSelector.addSocketChannel(qsyPacket.getNodeAddress().getHostAddress(), QSYPacketTools.TCP_PORT, null);
				}
			} else if (QSYPacketTools.isKeepAlivePacket(qsyPacket.getData())) {
				printQSYPacket(qsyPacket);
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
	public void cleanUp() {
		nodes.clear();
		running.set(false);
	}

}
