package ar.com.qsy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public final class ReceiverSelector implements Runnable, Cleanable {

	private final Selector selector;
	private final Buffer<QSYPacket> buffer;
	private final ByteBuffer bb;
	private final byte[] data;

	private volatile boolean running;

	public ReceiverSelector(final Buffer<QSYPacket> buffer) {
		Selector select = null;
		try {
			select = Selector.open();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.selector = select;
		this.buffer = buffer;
		this.running = true;

		this.bb = ByteBuffer.allocate(QSYPacketTools.PACKET_SIZE);
		this.data = new byte[QSYPacketTools.PACKET_SIZE];
	}

	@Override
	public void run() {
		while (running) {
			try {
				selector.select();
			} catch (IOException e) {
				e.printStackTrace();
			}
			final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

			while (iterator.hasNext()) {
				final SelectionKey key = iterator.next();

				if (key.isReadable()) {

					final SocketChannel channel = (SocketChannel) key.channel();
					try {
						channel.read(bb);
					} catch (final IOException e) {
						e.printStackTrace();
					}
					bb.flip();
					bb.get(data);
					final Socket socket = channel.socket();
					buffer.add(new QSYPacket(socket.getInetAddress(), socket.getLocalAddress(), data));
					bb.clear();

				}
				iterator.remove();
			}
		}
	}

	public void addSocketChannel(final String address, final int port, final Object object) {
		final InetSocketAddress hostAddress = new InetSocketAddress(address, port);
		SocketChannel client;
		try {
			client = SocketChannel.open(hostAddress);
			client.configureBlocking(false);
			selector.wakeup();
			client.register(selector, SelectionKey.OP_READ, object);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanUp() {
		running = false;
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}