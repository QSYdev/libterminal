package ar.com.qsy.model.objects;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReceiverSelector implements Runnable, AutoCloseable {

	private final Selector selector;
	private final LinkedList<SimpleEntry<Object, SocketChannel>> newConnections;
	private final LinkedBlockingQueue<QSYPacket> buffer;
	private final ByteBuffer byteBuffer;
	private final byte[] data;

	private final AtomicBoolean running;

	public ReceiverSelector(final LinkedBlockingQueue<QSYPacket> buffer) throws IOException {
		this.selector = Selector.open();
		this.newConnections = new LinkedList<SimpleEntry<Object, SocketChannel>>();
		this.buffer = buffer;
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
						buffer.put(new QSYPacket(channel.socket().getInetAddress(), data));
						byteBuffer.clear();
					}
				}
				selector.selectedKeys().clear();
			} catch (final IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void registerNewSocketChannel(final String address, final int port, final Object object) throws IOException {
		final InetSocketAddress hostAddress = new InetSocketAddress(address, port);
		final SocketChannel client = SocketChannel.open(hostAddress);
		client.configureBlocking(false);
		synchronized (newConnections) {
			newConnections.add(new SimpleEntry<Object, SocketChannel>(object, client));
		}
		selector.wakeup();
	}

	private void addNewConnections() throws ClosedChannelException {
		synchronized (newConnections) {
			for (final SimpleEntry<Object, SocketChannel> item : newConnections) {
				final SocketChannel s = item.getValue();
				final Object o = item.getKey();
				s.register(selector, SelectionKey.OP_READ, o);
			}
			newConnections.clear();
		}
	}

	@Override
	public void close() {
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
}
