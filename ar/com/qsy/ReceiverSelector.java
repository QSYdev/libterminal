package ar.com.qsy;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReceiverSelector implements Runnable, Cleanable {

	private final Selector selector;
	private final LinkedList<SimpleEntry<Object, SocketChannel>> newConnections;
	private final Buffer<QSYPacket> buffer;
	private final ByteBuffer byteBuffer;
	private final byte[] data;

	private AtomicBoolean running;

	public ReceiverSelector(final Buffer<QSYPacket> buffer) {
		Selector select = null;
		try {
			select = Selector.open();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.selector = select;
		this.newConnections = new LinkedList<SimpleEntry<Object, SocketChannel>>();
		this.buffer = buffer;
		this.running = new AtomicBoolean(true);
		this.byteBuffer = ByteBuffer.allocate(QSYPacketTools.PACKET_SIZE);
		this.data = new byte[QSYPacketTools.PACKET_SIZE];
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				addNewConnections();
				selector.select();
				for (SelectionKey key : selector.selectedKeys()) {
					if (key.isReadable()) {
						final SocketChannel channel = (SocketChannel) key.channel();
						channel.read(byteBuffer);
						byteBuffer.flip();
						byteBuffer.get(data);
						buffer.add(new QSYPacket(channel.socket().getInetAddress(), data));
						byteBuffer.clear();
					}
				}
				selector.selectedKeys().clear();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addSocketChannel(final String address, final int port, final Object object) {
		final InetSocketAddress hostAddress = new InetSocketAddress(address, port);
		try {
			SocketChannel client = SocketChannel.open(hostAddress);
			client.configureBlocking(false);
			synchronized (newConnections) {
				newConnections.add(new SimpleEntry<Object, SocketChannel>(object, client));
			}
			selector.wakeup();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanUp() {
		running.set(false);
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addNewConnections() throws ClosedChannelException {
		synchronized (newConnections) {
			for (SimpleEntry<Object, SocketChannel> item : newConnections) {
				SocketChannel s = item.getValue();
				Object o = item.getKey();
				s.register(selector, SelectionKey.OP_READ, o);
			}
			newConnections.clear();
		}
	}
}
