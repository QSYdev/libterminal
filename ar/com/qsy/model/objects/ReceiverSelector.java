package ar.com.qsy.model.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.com.qsy.model.patterns.observer.Event;
import ar.com.qsy.model.patterns.observer.Event.EventType;
import ar.com.qsy.model.patterns.observer.EventListener;
import ar.com.qsy.model.patterns.observer.EventSource;

public final class ReceiverSelector extends EventSource implements Runnable, AutoCloseable, EventListener {

	private final Selector selector;
	private final LinkedList<Node> pendingConnections;
	private final ByteBuffer byteBuffer;
	private final byte[] data;

	private final AtomicBoolean running;

	public ReceiverSelector() throws IOException {
		this.selector = Selector.open();
		this.pendingConnections = new LinkedList<>();
		this.running = new AtomicBoolean(true);
		this.byteBuffer = ByteBuffer.allocate(QSYPacket.PACKET_SIZE);
		this.data = new byte[QSYPacket.PACKET_SIZE];
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				addNewConnections();
				selector.select();
				for (final SelectionKey key : selector.selectedKeys()) {
					if (key.isReadable()) {
						final SocketChannel channel = (SocketChannel) key.channel();
						channel.read(byteBuffer);
						byteBuffer.flip();
						byteBuffer.get(data);
						sendEvent(new Event(EventType.IncomingQSYPacket, new QSYPacket(channel.socket().getInetAddress(), data)));
						byteBuffer.clear();
					}
				}
				selector.selectedKeys().clear();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void newNodeCreated(final Node node) throws IOException {
		synchronized (pendingConnections) {
			pendingConnections.add(node);
		}
		selector.wakeup();
	}

	private void addNewConnections() throws ClosedChannelException {
		synchronized (pendingConnections) {
			for (final Node node : pendingConnections) {
				final SocketChannel s = node.getNodeSocketChannel();
				s.register(selector, SelectionKey.OP_READ, null);
			}
			pendingConnections.clear();
		}
	}

	@Override
	public void close() throws Exception {
		running.set(false);
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	@Override
	public void receiveEvent(final Event event) throws InterruptedException, IOException {
		switch (event.getEventType()) {
		case newNode: {
			final Node node = (Node) event.getContent();
			newNodeCreated(node);
			break;
		}
		default: {
			break;
		}
		}
	}
}
