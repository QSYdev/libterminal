package libterminal.lib.network;

import libterminal.lib.protocol.QSYPacket;
import libterminal.patterns.observer.Event;
import libterminal.patterns.observer.EventSource;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MulticastReceiver extends EventSource implements Runnable {

    private final MulticastSocket socket;
	private final DatagramPacket packet;
	private final AtomicBoolean running;

	public MulticastReceiver(Inet4Address interfaceAddress, Inet4Address multicastAddress, int port) throws IOException {
		this.socket = new MulticastSocket(port);
		this.socket.joinGroup(new InetSocketAddress(multicastAddress, port), NetworkInterface.getByInetAddress(interfaceAddress));

		this.packet = new DatagramPacket(new byte[QSYPacket.PACKET_SIZE],QSYPacket.PACKET_SIZE);
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {
			InetAddress sender;
			try {
				socket.receive(packet);
				sender = packet.getAddress();
				sendEvent(new Event(Event.EventType.incomingQSYPacket, new QSYPacket(sender, packet.getData())));
			} catch (IOException e) {
				if (!socket.isClosed())
					e.printStackTrace();
				running.set(false);
			}
		}
	}

	@Override
	public void close() throws Exception {
		running.set(false);
		socket.close();
		super.close();
	}

}
