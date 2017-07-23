package ar.com.qsy.src.app.network;

import ar.com.qsy.src.app.protocol.QSYPacket;
import ar.com.qsy.src.app.terminal.Node;
import ar.com.qsy.src.patterns.command.Command;
import ar.com.qsy.src.patterns.observer.EventListener;
import ar.com.qsy.src.patterns.observer.EventSource;
import ar.com.qsy.src.patterns.observer.Event;
import ar.com.qsy.src.patterns.observer.Event.EventType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReceiverSelector extends EventSource implements Runnable, EventListener {

	private final Selector selector;
	private final LinkedList<Command> pendingActions;
	private final ByteBuffer byteBuffer;
	private final byte[] data;

	private final AtomicBoolean running;

	public ReceiverSelector() throws IOException {
		this.selector = Selector.open();
		this.pendingActions = new LinkedList<>();
		this.byteBuffer = ByteBuffer.allocate(QSYPacket.PACKET_SIZE);
		this.data = new byte[QSYPacket.PACKET_SIZE];

		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {
			try {

				synchronized (pendingActions) {
					for (final Command action : pendingActions) {
						action.execute();
					}
					pendingActions.clear();
				}

				selector.select();

				for (final SelectionKey key : selector.selectedKeys()) {
					if (key.isReadable()) {
						final SocketChannel channel = (SocketChannel) key.channel();
						channel.read(byteBuffer);
						byteBuffer.flip();
						byteBuffer.get(data);
						sendEvent(new Event(EventType.incomingQSYPacket, new QSYPacket(channel.socket().getInetAddress(), data)));
						byteBuffer.clear();
					}
				}
				selector.selectedKeys().clear();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void newNodeCreated(final Node node) {
		synchronized (pendingActions) {
			pendingActions.add(() -> {
				final SocketChannel s = node.getNodeSocketChannel();
				try {
					s.register(selector, SelectionKey.OP_READ, null);
				} catch (ClosedChannelException e) {
					e.printStackTrace();
				}
			});
		}
		selector.wakeup();
	}

	@Override
	public void close() throws Exception {
		running.set(false);
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.close();
	}

	@Override
	public void receiveEvent(final Event event) throws InterruptedException, IOException {
		switch (event.getEventType()) {
		case newNode: {
			final Node node = (Node) event.getContent();
			newNodeCreated(node);
			break;
		}
		case disconnectedNode: {
			final Node node = (Node) event.getContent();
			node.getNodeSocketChannel().keyFor(selector).cancel();
			break;
		}
		default: {
			break;
		}
		}
	}

}
