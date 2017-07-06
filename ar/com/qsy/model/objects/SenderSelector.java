package ar.com.qsy.model.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SenderSelector implements Runnable, AutoCloseable {

	private final TreeMap<Integer, Node> nodes;
	private final LinkedBlockingQueue<QSYPacket> outputBuffer;
	private final ByteBuffer byteBuffer;

	private final AtomicBoolean running;

	public SenderSelector(final TreeMap<Integer, Node> nodes, final LinkedBlockingQueue<QSYPacket> outputBuffer) {
		this.nodes = nodes;
		this.outputBuffer = outputBuffer;
		this.byteBuffer = ByteBuffer.allocate(QSYPacket.PACKET_SIZE);

		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				final QSYPacket qsyPacket = outputBuffer.take();
				switch (qsyPacket.getType()) {
				case Command: {
					final SocketChannel channel;
					synchronized (nodes) {
						channel = nodes.get(qsyPacket.getId()).getNodeSocketChannel();
					}
					byteBuffer.put(qsyPacket.getRawData());
					byteBuffer.flip();
					channel.write(byteBuffer);
					byteBuffer.clear();
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

	@Override
	public void close() throws Exception {
		running.set(false);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

}
