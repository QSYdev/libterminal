package ar.com.qsy.model.objects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MulticastReceiver implements Runnable, AutoCloseable {

	private final InetAddress address;
	private final int port;
	private final MulticastSocket socket;
	private final DatagramPacket packet;

	private final AtomicBoolean running;

	private final BlockingQueue<QSYPacket> inputBuffer;

	public MulticastReceiver(final String serverAddress, final int serverPort, final BlockingQueue<QSYPacket> inputBuffer) throws IOException {
		this.address = InetAddress.getByName(serverAddress);
		this.socket = new MulticastSocket(serverPort);
		this.socket.joinGroup(address);
		this.port = serverPort;

		this.packet = new DatagramPacket(new byte[QSYPacket.PACKET_SIZE], QSYPacket.PACKET_SIZE);
		this.running = new AtomicBoolean(true);

		this.inputBuffer = inputBuffer;
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				if (receive(packet)) {
					inputBuffer.put(new QSYPacket(packet.getAddress(), packet.getData()));
				} else {
					throw new Exception("<< QSY_MULTICAST_ERROR >> Ha ocurrido un error en la conexion con el multicast");
				}
			} catch (final IOException | IllegalArgumentException | InterruptedException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

	}

	private boolean receive(final DatagramPacket packet) throws IOException {
		boolean result = true;
		socket.receive(packet);
		return result;
	}

	public void setRunning(final boolean running) {
		this.running.set(running);
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void close() throws Exception {
		running.set(false);
		try {
			socket.leaveGroup(address);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

}
