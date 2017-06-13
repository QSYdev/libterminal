package ar.com.qsy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MulticastReceiver implements Runnable, Cleanable {

	private final InetAddress address;
	private final int port;
	private final MulticastSocket socket;
	private final DatagramPacket packet;

	private final AtomicBoolean running;

	private final Buffer<QSYPacket> buffer;

	public MulticastReceiver(final String serverAddress, final int serverPort, final Buffer<QSYPacket> buffer) {
		InetAddress mcIPAddress = null;
		MulticastSocket mcSocket = null;
		try {
			mcIPAddress = InetAddress.getByName(serverAddress);
			mcSocket = new MulticastSocket(serverPort);
			mcSocket.joinGroup(mcIPAddress);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.socket = mcSocket;
		this.address = mcIPAddress;
		this.port = serverPort;

		this.packet = new DatagramPacket(new byte[QSYPacketTools.PACKET_SIZE], QSYPacketTools.PACKET_SIZE);
		this.running = new AtomicBoolean(true);

		this.buffer = buffer;
	}

	@Override
	public void run() {
		while (running.get()) {
			if (receive(packet)) {
				buffer.add(new QSYPacket(packet.getAddress(), packet.getData()));
			} else {
				// TODO error en el multicast.
			}
		}
	}

	private boolean receive(final DatagramPacket packet) {
		boolean result = true;
		try {
			socket.receive(packet);
		} catch (final Exception e) {
			result = false;
		}
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
	public void cleanUp() {
		running.set(false);
		try {
			socket.leaveGroup(address);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}

}
