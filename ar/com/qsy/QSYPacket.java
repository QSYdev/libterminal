package ar.com.qsy;

import java.net.InetAddress;

public final class QSYPacket {

	private final InetAddress nodeAddress;
	private final byte[] data;

	public QSYPacket(final InetAddress nodeAddress, final byte[] data) {
		this.nodeAddress = nodeAddress;
		this.data = new byte[QSYPacketTools.PACKET_SIZE];
		System.arraycopy(data, 0, this.data, 0, QSYPacketTools.PACKET_SIZE);
	}

	public InetAddress getNodeAddress() {
		return nodeAddress;
	}

	public byte[] getData() {
		return data;
	}

}
