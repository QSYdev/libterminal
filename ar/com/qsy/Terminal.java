package ar.com.qsy;

import java.net.InetAddress;
import java.util.HashSet;

public final class Terminal implements Runnable, Cleanable {

	private final Buffer<QSYPacket> buffer;
	private final ReceiverSelector receiverSelector;
	private volatile boolean searchNodes;

	private final HashSet<InetAddress> nodes;

	private volatile boolean running;

	public Terminal(final Buffer<QSYPacket> buffer, final ReceiverSelector receiverSelector) {
		this.buffer = buffer;
		this.receiverSelector = receiverSelector;
		this.nodes = new HashSet<>();
		this.searchNodes = false;
		this.running = true;
	}

	@Override
	public void run() {
		while (running) {
			final QSYPacket qsyPacket = buffer.remove();
			if (searchNodes && QSYPacketTools.isHelloPacket(qsyPacket.getData())) {
				if (!nodes.contains(qsyPacket.getSource())) {
					printQSYPacket(qsyPacket);
					nodes.add(qsyPacket.getSource());
					receiverSelector.addSocketChannel(qsyPacket.getSource().getHostAddress(), QSYPacketTools.TCP_PORT, null);
				}
			} else if (QSYPacketTools.isKeepAlivePacket(qsyPacket.getData())) {
				printQSYPacket(qsyPacket);
			}
		}
	}

	private void printQSYPacket(final QSYPacket qsyPacket) {
		System.out.println("Data received from: " + qsyPacket.getSource());
		final byte[] data = qsyPacket.getData();
		for (int i = 0; i < data.length; i++) {
			System.out.print("[ " + data[i] + " ]\t");
		}
		System.out.println();
	}

	public void searchNodes() {
		searchNodes = true;
	}

	public void finalizeNodesSearch() {
		searchNodes = false;
	}

	@Override
	public void cleanUp() {
		nodes.clear();
		running = false;
	}

}
