package ar.com.qsy.model.objects;

import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.com.qsy.view.QSYFrame;

public final class Terminal implements Runnable, AutoCloseable {

	private final QSYFrame view;

	private final BlockingQueue<QSYPacket> inputBuffer;
	private final ReceiverSelector receiverSelector;

	private final BlockingQueue<QSYPacket> outputBuffer;

	private final TreeMap<Integer, Node> nodes;

	private final KeepAlive keepAlive;

	private final AtomicBoolean searchNodes;
	private final AtomicBoolean running;

	public Terminal(final BlockingQueue<QSYPacket> inputBuffer, final ReceiverSelector receiverSelector, final BlockingQueue<QSYPacket> outputBuffer, final QSYFrame view) {
		this.view = view;

		this.inputBuffer = inputBuffer;
		this.receiverSelector = receiverSelector;
		this.outputBuffer = outputBuffer;
		this.nodes = new TreeMap<>();
		this.keepAlive = new KeepAlive(nodes);
		this.searchNodes = new AtomicBoolean(false);
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {

			try {
				final QSYPacket qsyPacket = inputBuffer.take();

				switch (qsyPacket.getType()) {
				case Hello: {
					if (searchNodes.get()) {
						final boolean contains;
						synchronized (nodes) {
							contains = nodes.containsKey(qsyPacket.getId());
						}
						if (!contains) {
							final Node node = new Node(qsyPacket);
							synchronized (nodes) {
								nodes.put(node.getNodeId(), node);
							}
							receiverSelector.qsyHelloPacketReceived(node);
							keepAlive.qsyHelloPacketReceived(qsyPacket);
							view.addNewNode(qsyPacket);
						}
					}
					break;
				}
				case Keepalive: {
					keepAlive.qsyKeepAlivePacketReceived(qsyPacket);
					break;
				}
				case Touche: {
					// TODO cuando se recibe un touche.
					System.out.println(qsyPacket);
					break;
				}
				default: {
					break;
				}
				}
			} catch (final InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}

	}

	public TreeMap<Integer, Node> getNodes() {
		return nodes;
	}

	public void searchNodes() {
		searchNodes.set(true);
	}

	public void finalizeNodesSearch() {
		searchNodes.set(false);
	}

	public void sendQSYPacket(final QSYPacket qsyPacket) throws InterruptedException {
		outputBuffer.put(qsyPacket);
	}

	@Override
	public void close() throws Exception {
		running.set(false);
		keepAlive.close();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

}
