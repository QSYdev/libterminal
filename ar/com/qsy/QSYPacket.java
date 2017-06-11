package ar.com.qsy;

import java.net.InetAddress;

public final class QSYPacket {

	private final InetAddress source;
	private final InetAddress destination;
	private final byte[] data;

	public QSYPacket(final InetAddress source, final InetAddress destination, final byte[] data) {
		this.source = source;
		this.destination = destination;
		this.data = data;
	}

	public InetAddress getSource() {
		return source;
	}

	public InetAddress getDestination() {
		return destination;
	}

	public byte[] getData() {
		return data;
	}

}
