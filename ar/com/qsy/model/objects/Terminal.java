package ar.com.qsy.model.objects;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Terminal implements Runnable, AutoCloseable {

	private final BlockingQueue<QSYPacket> buffer;
	private final ReceiverSelector receiverSelector;
	private final AtomicBoolean searchNodes;
	private final HashSet<InetAddress> nodes;
	private final AtomicBoolean running;

	private final KeepAliveChecker keepAliveChecker;
	private Thread kacThread;

	public Terminal(final BlockingQueue<QSYPacket> buffer, final ReceiverSelector receiverSelector) {
		this.buffer = buffer;
		this.receiverSelector = receiverSelector;
		this.nodes = new HashSet<>();
		this.searchNodes = new AtomicBoolean(false);
		this.running = new AtomicBoolean(true);
		keepAliveChecker = new KeepAliveChecker(this.nodes);
	}

	@Override
	public void run() {
		while (running.get()) {

			try {
				final QSYPacket qsyPacket = buffer.take();
				switch (qsyPacket.getType()) {
				case Hello: {
					if (searchNodes.get() && !nodes.contains(qsyPacket.getNodeAddress())) {
						System.out.println(qsyPacket);
						if(nodes.isEmpty()){
							kacThread = new Thread(keepAliveChecker, "KeepAliveChecker");
							kacThread.start();
						}
						nodes.add(qsyPacket.getNodeAddress());
						receiverSelector.registerNewSocketChannel(qsyPacket.getNodeAddress().getHostAddress(), QSYPacket.TCP_PORT, null);
						keepAliveChecker.update(qsyPacket.getNodeAddress());
					}
					break;
				}
				case Keepalive: {
					//System.out.println(qsyPacket);
					//TODO chequear concurrencia
					keepAliveChecker.update(qsyPacket.getNodeAddress());
					break;
				}
				case Command: {
					// TODO cuando se recibe un command.
					break;
				}
				case Touche: {
					// TODO cuando se recibe un touche.
					break;
				}
				}
			} catch (final InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}

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
