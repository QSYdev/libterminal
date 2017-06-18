package ar.com.qsy.model.objects;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Terminal implements Runnable, AutoCloseable {

	private final BlockingQueue<QSYPacket> buffer;
	private final ReceiverSelector receiverSelector;
	private final AtomicBoolean searchNodes;
	private final Hashtable<InetAddress,Long> nodesRegistry;
	private final AtomicBoolean running;

	private final KeepAliveChecker keepAliveChecker;
	private Thread kacThread;

	public Terminal(final BlockingQueue<QSYPacket> buffer, final ReceiverSelector receiverSelector) {
		this.buffer = buffer;
		this.receiverSelector = receiverSelector;
		this.nodesRegistry = new Hashtable<InetAddress,Long>();
		this.searchNodes = new AtomicBoolean(false);
		this.running = new AtomicBoolean(true);
		keepAliveChecker = new KeepAliveChecker(this.nodesRegistry);
	}

	@Override
	public void run() {
		while (running.get()) {

			try {
				final QSYPacket qsyPacket = buffer.take();
				switch (qsyPacket.getType()) {
				case Hello: {
					if (searchNodes.get() && !nodesRegistry.contains(qsyPacket.getNodeAddress())) {
						System.out.println(qsyPacket);
						if(nodesRegistry.isEmpty()){
							kacThread = new Thread(keepAliveChecker, "KeepAliveChecker");
							kacThread.start();
						}
						System.out.println(System.currentTimeMillis()%10000+"\tAgregando nodo y keepAlive");
						nodesRegistry.put(qsyPacket.getNodeAddress(), System.currentTimeMillis());
						receiverSelector.registerNewSocketChannel(qsyPacket.getNodeAddress().getHostAddress(), QSYPacket.TCP_PORT, null);
					}
					break;
				}
				case Keepalive: {
					long ahora = System.currentTimeMillis();
					System.out.printf("Actualizando keepAlive nodo %s. prev: %d - ahora: %d\n",qsyPacket.getNodeAddress(), (nodesRegistry.get(qsyPacket.getNodeAddress())%10000), ahora%10000);
					nodesRegistry.put(qsyPacket.getNodeAddress(),System.currentTimeMillis());
					break;
				}
				case Command: {
					// TODO cuando se recibe un command.
					break;
				}
				case Touche: {
					// TODO cuando se recibe un touche.
					break;
				}
				}
			} catch (final InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void searchNodes() {
		searchNodes.set(true);
	}

	public void finalizeNodesSearch() {
		searchNodes.set(false);
	}

	@Override
	public void close() {
		nodesRegistry.clear();
		running.set(false);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

}
