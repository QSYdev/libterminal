package ar.com.qsy.model.objects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.com.qsy.model.patterns.observer.Event;
import ar.com.qsy.model.patterns.observer.Event.EventType;
import ar.com.qsy.model.patterns.observer.EventSource;

public final class MulticastReceiver extends EventSource implements Runnable {

	private final InetAddress address;
	private final MulticastSocket socket;
	private final DatagramPacket packet;

	private final AtomicBoolean running;

	public MulticastReceiver(final String serverAddress, final int serverPort) throws IOException {
		this.address = InetAddress.getByName(serverAddress);
		this.socket = new MulticastSocket(serverPort);
		this.socket.joinGroup(address);

		this.packet = new DatagramPacket(new byte[QSYPacket.PACKET_SIZE], QSYPacket.PACKET_SIZE);
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				if (receive(packet)) {
					sendEvent(new Event(EventType.incomingQSYPacket, new QSYPacket(packet.getAddress(), packet.getData())));
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
		super.close();
	}

}
