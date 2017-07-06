package ar.com.qsy.model.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReceiverSelector implements Runnable, AutoCloseable {

	private final Selector selector;
	private final LinkedList<Node> pendingConnections;
	private final LinkedBlockingQueue<QSYPacket> inputBuffer;
	private final ByteBuffer byteBuffer;
	private final byte[] data;

	private final AtomicBoolean running;

	public ReceiverSelector(final LinkedBlockingQueue<QSYPacket> inputBuffer) throws IOException {
		this.selector = Selector.open();
		this.pendingConnections = new LinkedList<>();
		this.inputBuffer = inputBuffer;
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
						inputBuffer.put(new QSYPacket(channel.socket().getInetAddress(), data));
						byteBuffer.clear();
					}
				}
				selector.selectedKeys().clear();
			} catch (final IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void qsyHelloPacketReceived(final Node node) throws IOException {
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
}
