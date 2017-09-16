package libterminal.lib.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import libterminal.lib.node.Node;
import libterminal.lib.protocol.QSYPacket;
import libterminal.patterns.observer.AsynchronousListener;
import libterminal.patterns.observer.Event.CommandPacketSentEvent;
import libterminal.patterns.observer.Event.InternalEvent;
import libterminal.patterns.visitor.InternalEventHandler;

public final class Sender extends AsynchronousListener implements Runnable, AutoCloseable {

	private final InternalEventHandler eventHandler;

	private final TreeMap<Integer, Node> nodes;
	private final ByteBuffer byteBuffer;

	private final AtomicBoolean running;

	public Sender(final TreeMap<Integer, Node> nodes) {
		this.nodes = nodes;
		this.byteBuffer = ByteBuffer.allocate(QSYPacket.PACKET_SIZE);
		this.eventHandler = new EventHandler();

		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				final InternalEvent event = (InternalEvent) getEvent();
				event.acceptHandler(eventHandler);
			} catch (InterruptedException e) {
				this.close();
			}
		}
	}

	@Override
	public void close() {
		running.set(false);
	}

	private final class EventHandler extends InternalEventHandler {

		@Override
		public void handle(final CommandPacketSentEvent event) {
			super.handle(event);
			try {
				final QSYPacket qsyPacket = event.getPacket();
				final SocketChannel channel;
				synchronized (nodes) {
					channel = nodes.get(qsyPacket.getId()).getNodeSocketChannel();
				}
				byteBuffer.put(qsyPacket.getRawData());
				byteBuffer.flip();
				channel.write(byteBuffer);
				byteBuffer.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
