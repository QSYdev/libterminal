package ar.com.qsy.model.objects;

import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.com.qsy.model.patterns.observer.AsynchronousListener;
import ar.com.qsy.model.patterns.observer.Event;
import ar.com.qsy.model.patterns.observer.Event.EventType;
import ar.com.qsy.model.patterns.observer.EventListener;
import ar.com.qsy.model.patterns.observer.EventSource;

public final class Terminal extends EventSource implements Runnable, AutoCloseable, EventListener {

	private final TreeMap<Integer, Node> nodes;

	private final AsynchronousListener internalListener;
	private final KeepAlive keepAlive;

	private final AtomicBoolean searchNodes;
	private final AtomicBoolean running;

	public Terminal() {
		this.nodes = new TreeMap<>();
		this.internalListener = new AsynchronousListener();
		this.keepAlive = new KeepAlive(nodes);
		addListener(keepAlive);
		keepAlive.addListener(this);
		this.searchNodes = new AtomicBoolean(false);
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {

			try {
				final Event event = internalListener.getEvent();
				switch (event.getEventType()) {
				case incomingQSYPacket: {
					final QSYPacket qsyPacket = (QSYPacket) event.getContent();

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
								sendEvent(new Event(EventType.newNode, node));
							}
						}
						break;
					}
					case Keepalive: {
						sendEvent(new Event(EventType.keepAliveReceived, qsyPacket));
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
					break;
				}
				case keepAliveError: {
					final Node node = (Node) event.getContent();
					synchronized (nodes) {
						nodes.remove(node.getNodeId());
					}
					node.close();
					sendEvent(new Event(EventType.disconnectedNode, node));
					System.err.println("Se ha desconectado el nodo id = " + node.getNodeId());
					break;
				}
				default: {
					break;
				}
				}
			} catch (final Exception e) {
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

	public void sendQSYPacket(final QSYPacket qsyPacket) throws Exception {
		sendEvent(new Event(EventType.commandPacketSent, qsyPacket));
	}

	@Override
	public void close() throws Exception {
		running.set(false);
		keepAlive.close();
	}

	@Override
	public void receiveEvent(final Event event) throws InterruptedException {
		internalListener.receiveEvent(event);
	}

}
