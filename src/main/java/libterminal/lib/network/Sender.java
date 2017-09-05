package libterminal.lib.network;

import libterminal.lib.protocol.QSYPacket;
import libterminal.lib.node.Node;
import libterminal.patterns.observer.AsynchronousListener;
import libterminal.patterns.observer.Event;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Sender extends AsynchronousListener implements Runnable, AutoCloseable {

	private final TreeMap<Integer, Node> nodes;
	private final ByteBuffer byteBuffer;

	private final AtomicBoolean running;

	public Sender(final TreeMap<Integer, Node> nodes) {
		this.nodes = nodes;
		this.byteBuffer = ByteBuffer.allocate(QSYPacket.PACKET_SIZE);

		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				final Event event = getEvent();
				switch (event.getEventType()) {
				case commandPacketSent:
					final QSYPacket qsyPacket = (QSYPacket) event.getContent();
					final SocketChannel channel;
					synchronized (nodes) {
						channel = nodes.get(qsyPacket.getId()).getNodeSocketChannel();
					}
					byteBuffer.put(qsyPacket.getRawData());
					byteBuffer.flip();
					channel.write(byteBuffer);
					byteBuffer.clear();
					break;
				default:
					break;
				}
			} catch (InterruptedException e) {
				this.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void close() {
		running.set(false);
	}

}